package eu.octanne.xelephia.util;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import eu.octanne.xelephia.XelephiaPlugin;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

public class AnvilGUI {
    private Player player;
    @SuppressWarnings("unused")
	private AnvilEventHandler handler;
    private static Class<?> BlockPosition;
    private static Class<?> PacketPlayOutOpenWindow;
    private static Class<?> ContainerAnvil;
    private static Class<?> ChatMessage;
    private static Class<?> EntityHuman;
    private HashMap<AnvilSlot, ItemStack> items = new HashMap<AnvilSlot, ItemStack>();
    private Inventory inv;
    private Listener listener;

    private void loadClasses() {
        BlockPosition = NMSManager.get().getNMSClass("BlockPosition");
        PacketPlayOutOpenWindow = NMSManager.get().getNMSClass("PacketPlayOutOpenWindow");
        ContainerAnvil = NMSManager.get().getNMSClass("ContainerAnvil");
        EntityHuman = NMSManager.get().getNMSClass("EntityHuman");
        ChatMessage = NMSManager.get().getNMSClass("ChatMessage");
    }

    public AnvilGUI(final Player player, final AnvilEventHandler handler) {
        loadClasses();
        this.player = player;
        this.handler = handler;

        this.listener = new Listener() {
            @EventHandler
            public void onInventoryClick(InventoryClickEvent event) {
                if (event.getWhoClicked() instanceof Player) {
                    if (event.getInventory().equals(inv)) {
                        event.setCancelled(true);
                        if(event.getSlotType().equals(SlotType.RESULT)) {
                        	ItemStack item = event.getCurrentItem();
                            int slot = event.getRawSlot();
                            String name = "no_value";

                            if (item != null && !item.getType().equals(Material.AIR)) {
                                if (item.hasItemMeta()) {
                                    ItemMeta meta = item.getItemMeta();

                                    if (meta.hasDisplayName()) {
                                        name = meta.getDisplayName();
                                    }
                                }

                                AnvilClickEvent clickEvent = new AnvilClickEvent(AnvilSlot.bySlot(slot), name, (Player)event.getWhoClicked());

                                handler.onAnvilClick(clickEvent);

                                if (clickEvent.getWillClose()) {
                                    event.getWhoClicked().closeInventory();
                                }

                                if (clickEvent.getWillDestroy()) {
                                    destroy();
                                }
                            }
                        }        
                    }
                }
            }

            @EventHandler
            public void onInventoryClose(InventoryCloseEvent event) {
                if (event.getPlayer() instanceof Player) {
                    Inventory inv = event.getInventory();
                    player.setLevel(player.getLevel() - 1);
                    if (inv.equals(AnvilGUI.this.inv)) {
                        inv.clear();
                        AnvilCloseEvent closeEvent = new AnvilCloseEvent((Player)event.getPlayer());
                        handler.onAnvilClose(closeEvent);
                        destroy();
                    }
                }
            }

            @EventHandler
            public void onPlayerQuit(PlayerQuitEvent event) {
                if (event.getPlayer().equals(getPlayer())) {
                    player.setLevel(player.getLevel() - 1);
                    destroy();
                }
            }
        };

        Bukkit.getPluginManager().registerEvents(listener, XelephiaPlugin.getInstance()); //Replace with instance of main class
    }

    public Player getPlayer() {
        return player;
    }

    public void setSlot(AnvilSlot slot, ItemStack item) {
        items.put(slot, item);
    }

    public void open() throws IllegalAccessException, InvocationTargetException, InstantiationException {
        player.setLevel(player.getLevel() + 1);

        try {
            Object p = NMSManager.get().getHandle(player);


            Object container = ContainerAnvil.getConstructor(NMSManager.get().getNMSClass("PlayerInventory"), NMSManager.get().getNMSClass("World"), BlockPosition, EntityHuman).newInstance(NMSManager.get().getPlayerField(player, "inventory"), NMSManager.get().getPlayerField(player, "world"), BlockPosition.getConstructor(int.class, int.class, int.class).newInstance(0, 0, 0), p);
            NMSManager.get().getField(NMSManager.get().getNMSClass("Container"), "checkReachable").set(container, false);

            //Set the items to the items from the inventory given
            Object bukkitView = NMSManager.get().invokeMethod("getBukkitView", container);
            inv = (Inventory) NMSManager.get().invokeMethod("getTopInventory", bukkitView);

            for (AnvilSlot slot : items.keySet()) {
                inv.setItem(slot.getSlot(), items.get(slot));
            }

            //Counter stuff that the game uses to keep track of inventories
            int c = (int) NMSManager.get().invokeMethod("nextContainerCounter", p);

            //Send the packet
            Constructor<?> chatMessageConstructor = ChatMessage.getConstructor(String.class, Object[].class);
            Object playerConnection = NMSManager.get().getPlayerField(player, "playerConnection");
            Object packet = PacketPlayOutOpenWindow.getConstructor(int.class, String.class, NMSManager.get().getNMSClass("IChatBaseComponent"), int.class).newInstance(c, "minecraft:anvil", chatMessageConstructor.newInstance("Repairing", new Object[]{}), 0);

            Method sendPacket = NMSManager.get().getMethod("sendPacket", playerConnection.getClass(), PacketPlayOutOpenWindow);
            sendPacket.invoke(playerConnection, packet);

            //Set their active container to the container
            Field activeContainerField = NMSManager.get().getField(EntityHuman, "activeContainer");
            if (activeContainerField != null) {
                activeContainerField.set(p, container);

                //Set their active container window id to that counter stuff
                NMSManager.get().getField(NMSManager.get().getNMSClass("Container"), "windowId").set(activeContainerField.get(p), c);

                //Add the slot listener
                NMSManager.get().getMethod("addSlotListener", activeContainerField.get(p).getClass(), p.getClass()).invoke(activeContainerField.get(p), p);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void destroy() {
        player = null;
        handler = null;
        items = null;

        HandlerList.unregisterAll(listener);

        listener = null;
    }

    public enum AnvilSlot {
        INPUT_LEFT(0),
        INPUT_RIGHT(1),
        OUTPUT(2);

        private int slot;

        private AnvilSlot(int slot) {
            this.slot = slot;
        }

        public static AnvilSlot bySlot(int slot) {
            for (AnvilSlot anvilSlot : values()) {
                if (anvilSlot.getSlot() == slot) {
                    return anvilSlot;
                }
            }

            return null;
        }

        public int getSlot() {
            return slot;
        }
    }

    public interface AnvilEventHandler {
        void onAnvilClose(AnvilCloseEvent event);
        void onAnvilClick(AnvilClickEvent event);
    }

    public class AnvilCloseEvent {
    	
    	private Player player;
    	
        public AnvilCloseEvent(Player player) {
            this.player = player;
        }
        
        public Player getPlayer() {
        	return player;
        }
    }
    
    public class AnvilClickEvent {
        private AnvilSlot slot;

        private String name;
        private Player player;
        
        private boolean close = true;
        private boolean destroy = true;

        public AnvilClickEvent(AnvilSlot slot, String name, Player player) {
            this.slot = slot;
            this.name = name;
            this.player = player;
        }

        public AnvilSlot getSlot() {
            return slot;
        }

        public String getName() {
            return name;
        }
        
        public Player getPlayer() {
        	return player;
        }
        
        public boolean getWillClose() {
            return close;
        }

        public void setWillClose(boolean close) {
            this.close = close;
        }

        public boolean getWillDestroy() {
            return destroy;
        }

        public void setWillDestroy(boolean destroy) {
            this.destroy = destroy;
        }
    }
}
