package eu.octanne.xelephia.lootzone;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import eu.octanne.xelephia.XelephiaPlugin;
import eu.octanne.xelephia.util.AnvilGUI;
import eu.octanne.xelephia.util.AnvilGUI.AnvilCloseEvent;
import eu.octanne.xelephia.util.AnvilGUI.AnvilSlot;
import eu.octanne.xelephia.util.Utils;

public class LootZoneManager implements Listener {

	protected ArrayList<LootZone> lootZones = new ArrayList<LootZone>();

	private HashMap<String,LootZoneEdit> lootZoneEdit = new HashMap<>();
	private ArrayList<String> lootZoneInEdit = new ArrayList<>();

	private class LootZoneEdit{
		public LootZone zone;
		public Inventory inv;
		public int scroll;
		public boolean willClose = false;
		public boolean inAnvil = false;

		public LootZoneEdit(LootZone zone, Inventory inv) {
			this.zone = zone;
			this.inv = inv;
			scroll = 0;
		}	

		public int getScrollMax() {
			int scrollMax = 0;
			scrollMax = (zone.getLoots().size()-9);
			if (scrollMax <= 0) scrollMax = 0;
			return scrollMax;
		}
	}

	protected File zoneFolder = new File("plugins/Xelephia/zone/");

	public LootZoneManager() {
		//Serialization
		ConfigurationSerialization.registerClass(Loot.class, "Loot");

		// LootZone load
		load();

		// Register Listener
		Bukkit.getPluginManager().registerEvents(this, XelephiaPlugin.getInstance());
	}

	protected void load() {
		if(zoneFolder.isDirectory() && zoneFolder.listFiles() != null)
			for(File file : zoneFolder.listFiles()) {
				if(file.getName().contains(".yml")) {
					LootZone zone = new LootZone(file.getName().split(".yml")[0]);
					if(zone.getName() != null)lootZones.add(zone);
				}
			}
	}

	protected void save() {
		for(LootZone zone : lootZones) {
			zone.save();
		}
	}

	public List<LootZone> getLootZones() {
		return lootZones;
	}

	public LootZone getZone(String name) {
		for (LootZone zone : lootZones) {
			if (zone.getName().equalsIgnoreCase(name))
				return zone;
		}
		return null;
	}

	public boolean hasZone(String name) {
		if (getZone(name) != null)
			return true;
		else
			return false;
	}

	public boolean createZone(String name, int timeZone, Location loc) {
		LootZone zone = new LootZone(name, loc, timeZone);
		lootZones.add(zone);
		return true;
	}

	public void editLootZone(Player p, String name) {
		if(lootZoneInEdit.contains(name)) {
			if(lootZoneEdit.containsKey(p.getName())) p.openInventory(lootZoneEdit.get(p.getName()).inv);
			else p.sendMessage("§eLoot §8| §cErreur: La zone "+name+" est déjà en édition.");
		}else {
			LootZone zone = getZone(name);
			Inventory inv = createOrUpdateEditMenu(zone, 0, null);
			lootZoneInEdit.add(name);
			lootZoneEdit.put(p.getName(), new LootZoneEdit(zone, inv));
			p.openInventory(inv);
		}
	}

	public void reOpenEditor(Player p) {
		if(lootZoneEdit.containsKey(p.getName())) {
			LootZoneEdit zoneEdit = lootZoneEdit.get(p.getName());
			createOrUpdateEditMenu(zoneEdit.zone, zoneEdit.scroll, zoneEdit.inv);
			p.openInventory(zoneEdit.inv);
		}
	}

	public boolean removeZone(String zoneName) {
		for (LootZone zone : lootZones) {
			if (zone.getName().equalsIgnoreCase(zoneName)) {
				zone.remove();
				lootZones.remove(zone);
				return true;
			} else
				continue;
		}
		return false;
	}

	// Event Move
	@EventHandler
	public void onPlayerInZone(PlayerMoveEvent e) {

	}

	@EventHandler
	public void onInMenu(InventoryClickEvent e) {
		if(e.getWhoClicked() instanceof Player) {
			Player p = (Player) e.getWhoClicked();
			if(e.getInventory() != null && lootZoneEdit.isEmpty() ? false : lootZoneEdit.containsKey(p.getName())) {
				LootZoneEdit zoneEdit = lootZoneEdit.get(p.getName());
				if(zoneEdit.inv.equals(e.getView().getTopInventory())) {
					if(e.getRawSlot() <= 26) {
						if(!(e.getRawSlot() < 9 || e.getRawSlot() > 17)) {
							// Delete Loot, Edit % or Edit max
							if(e.getCurrentItem() != null && !e.getCurrentItem().getType().equals(Material.AIR)) {
								e.setCancelled(true);
								int lootNb = e.getSlot() - 9 + zoneEdit.scroll;
								Loot eLoot = zoneEdit.zone.getLoots().get(lootNb);
								// Delete
								if(e.isShiftClick()) {
									zoneEdit.zone.getLoots().remove(eLoot);
									zoneEdit.zone.save();

									if(zoneEdit.scroll > zoneEdit.getScrollMax()) 
										zoneEdit.scroll = zoneEdit.getScrollMax();

									//zoneEdit.scroll-=1;
									createOrUpdateEditMenu(zoneEdit.zone, zoneEdit.scroll, zoneEdit.inv);
									//p.updateInventory();
								}
								// Edit QTE Max
								else if(e.getClick().equals(ClickType.DOUBLE_CLICK)){
									AnvilGUI menu = new AnvilGUI(p, new AnvilGUI.AnvilEventHandler() {

										Loot loot = eLoot;
										
										@Override
										public void onAnvilClick(AnvilGUI.AnvilClickEvent event) {
											event.setWillClose(true);
											event.setWillDestroy(true);
											if(event.getSlot() == AnvilGUI.AnvilSlot.OUTPUT) {
												int quantity;
												try {
													quantity = Integer.parseInt(event.getName());
												}catch(NumberFormatException exp) {
													event.getPlayer().sendMessage("§eLoot §8| §cErreur : La valeur §9" + event.getName() + " §cest invalide.");
													return;
												}
												loot.max = quantity <= 64 ? quantity : 64;
												loot.max = quantity < loot.item.getAmount() ? loot.item.getAmount() : quantity;
												p.sendMessage("§eLoot §8| §aLa quantité max est défini à §9" + loot.max + "§.");
											}
										}

										@Override
										public void onAnvilClose(AnvilCloseEvent event) {
											reOpenEditor(event.getPlayer());
											zoneEdit.inAnvil = false;
										}
									});
									ArrayList<String> lore = new ArrayList<String>();
									lore.add("§aModifier quantité max :");
									lore.add("§7Entrer la valeur voulue,");
									lore.add("§e> §7Renommer l'item pour valider.");
									ItemStack anvilItem = Utils.createItemStack("Entrée valeur...", Material.PAPER, 1, lore, 0, true);
									menu.setSlot(AnvilSlot.INPUT_LEFT, anvilItem);
									try {
										zoneEdit.inAnvil = true;
										menu.open();
									} catch (IllegalAccessException | InvocationTargetException | InstantiationException exp) {
										exp.printStackTrace();
									}
								}
								else if(e.getClick().equals(ClickType.MIDDLE)) {
									AnvilGUI menu = new AnvilGUI(p, new AnvilGUI.AnvilEventHandler() {

										Loot loot = eLoot;
										
										@Override
										public void onAnvilClick(AnvilGUI.AnvilClickEvent event) {
											event.setWillClose(true);
											event.setWillDestroy(true);
											if(event.getSlot() == AnvilGUI.AnvilSlot.OUTPUT) {
												double prct;
												try {
													prct = Double.parseDouble(event.getName());
												}catch(NumberFormatException exp) {
													event.getPlayer().sendMessage("§eLoot §8| §cErreur : La valeur §9" + event.getName() + " §cest invalide.");
													return;
												}
												loot.luckPrct = prct <= 100 ? prct : 100;
												loot.luckPrct = prct < 0 ? 0 : prct;
												p.sendMessage("§eLoot §8| §aLe pourcentage est défini sur §9" + loot.luckPrct + "§a.");
											}
										}

										@Override
										public void onAnvilClose(AnvilCloseEvent event) {
											reOpenEditor(event.getPlayer());
											zoneEdit.inAnvil = false;
										}
									});
									ArrayList<String> lore = new ArrayList<String>();
									lore.add("§aModifier pourcentage :");
									lore.add("§7Entrer la valeur voulue,");
									lore.add("§e> §7Renommer l'item pour valider.");
									ItemStack anvilItem = Utils.createItemStack("Entrée valeur...", Material.PAPER, 1, lore, 0, true);
									menu.setSlot(AnvilSlot.INPUT_LEFT, anvilItem);
									try {
										zoneEdit.inAnvil = true;
										menu.open();
									} catch (IllegalAccessException | InvocationTargetException | InstantiationException exp) {
										exp.printStackTrace();
									}
								}
							}else e.setCancelled(true);
						}else{
							e.setCancelled(true);
							// Scroll
							if(e.getSlot() == 24 && zoneEdit.scroll < zoneEdit.getScrollMax()) {
								createOrUpdateEditMenu(zoneEdit.zone, zoneEdit.scroll+1, zoneEdit.inv);
								zoneEdit.scroll+=1;
								//p.updateInventory();
							}else if(e.getSlot() == 20 && zoneEdit.scroll > 0) {
								createOrUpdateEditMenu(zoneEdit.zone, zoneEdit.scroll-1, zoneEdit.inv);
								zoneEdit.scroll-=1;
								//p.updateInventory();
							}else if(e.getSlot() == 26) {
								zoneEdit.willClose = true;
								p.closeInventory();
							}
						}
					}else if(e.getAction().equals(InventoryAction.MOVE_TO_OTHER_INVENTORY) && e.getView().getItem(17) != null){
						ItemStack newItem = e.getCurrentItem().clone();
						e.getClickedInventory().clear(e.getSlot());
						//p.updateInventory();
						zoneEdit.zone.addLoot(new Loot(newItem, 1, newItem.getAmount()));
						zoneEdit.zone.save();
						createOrUpdateEditMenu(zoneEdit.zone, zoneEdit.scroll, zoneEdit.inv);
						//p.updateInventory();
					}
				}
			}
		}
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		if(lootZoneEdit.isEmpty() ? false : lootZoneEdit.containsKey(p.getName())) {
			LootZoneEdit zoneEdit = lootZoneEdit.get(p.getName());
			zoneEdit.zone.save();
			lootZoneInEdit.remove(zoneEdit.zone.getName());
			lootZoneEdit.remove(p.getName());
		}
	}
	
	@EventHandler
	public void onCloseMenu(InventoryCloseEvent e) {
		if(e.getPlayer() instanceof Player) {
			Player p = (Player) e.getPlayer();
			if(e.getInventory() != null && lootZoneEdit.isEmpty() ? false : lootZoneEdit.containsKey(p.getName())) {
				LootZoneEdit zoneEdit = lootZoneEdit.get(p.getName());
				zoneEdit.zone.save();
				if(zoneEdit.willClose) {
					lootZoneInEdit.remove(zoneEdit.zone.getName());
					lootZoneEdit.remove(p.getName());
				}else if(!zoneEdit.inAnvil){
					Bukkit.getScheduler().scheduleSyncDelayedTask(XelephiaPlugin.getInstance(), new Runnable() {
						@Override
						public void run() {
							reOpenEditor(p);
						}
					}, 8);
				}
			}
		}
	}

	private Inventory createOrUpdateEditMenu(LootZone zone, int scroll, @Nullable Inventory INV) {

		Inventory inv;
		boolean isSet;

		if(INV == null) {
			inv = Bukkit.createInventory(null, 27, "§cLootZone §8| §9"+zone.getName());
			isSet = false;
		}
		else {
			inv = INV;
			isSet = true;
		}

		if(!isSet) {
			for(int i = 0; i < 9; i++) inv.setItem(i, Utils.createItemStack(" ", Material.STAINED_GLASS_PANE, 1, new ArrayList<String>(), 7, false));
			for(int i = 18; i < 27; i++) inv.setItem(i, Utils.createItemStack(" ", Material.STAINED_GLASS_PANE, 1, new ArrayList<String>(), 7, false));

			ItemStack rollRightItem = Utils.createItemSkull("§9Défiler (droite)", new ArrayList<String>(), SkullType.PLAYER, "MHF_ArrowRight", false);
			ItemStack rollLeftItem = Utils.createItemSkull("§9Défiler (gauche)", new ArrayList<String>(), SkullType.PLAYER, "MHF_ArrowLeft", false); 
			ItemStack closeItem = Utils.createItemStack("§cFermer & Sauver", Material.BARRIER, 1, new ArrayList<String>(), 0, false);
			ArrayList<String> tutoLore = new ArrayList<>();
			tutoLore.add("§aActions disponibles :");
			tutoLore.add("§cShift-Click §7pour supprimer le loot");
			tutoLore.add("§cMidle-Click §7pour editer le pourcentage");
			tutoLore.add("§cDouble-Click §7pour editer la quantité max");
			ItemStack tutorialItem = Utils.createItemSkull("§7Tutoriel", tutoLore, SkullType.PLAYER, "MHF_Question", false);

			inv.setItem(20, rollLeftItem);
			inv.setItem(22, tutorialItem);
			inv.setItem(24, rollRightItem);
			inv.setItem(26, closeItem);
		}

		// Info
		ArrayList<String> infoLore = new ArrayList<>();
		infoLore.add("§7Nombre de loot : §c"+zone.getLoots().size());
		infoLore.add("§7Temps de contrôle : §c"+zone.getControlTime()+" §7secs");
		infoLore.add("§7Location : (§c"+zone.pos.getBlockX()+"§7, §c"+zone.pos.getBlockY()+"§7,"
				+ " §c"+zone.pos.getBlockZ()+"§7)");
		ItemStack infoItem = Utils.createItemStack("§aInformations", Material.BOOK, 1, infoLore, 0, false);
		inv.setItem(4, infoItem);

		// LootItems
		//Bukkit.broadcastMessage("=====INSERT ITEMS=====");
		for(int i = 0; i < 9; i++) {
			Loot loot;
			if(!zone.getLoots().isEmpty() && i+scroll < zone.getLoots().size()) {
				loot = zone.getLoots().get(i+scroll);
				//Bukkit.broadcastMessage("§7Shown item => slot : " + (int)(i+9) + " index : " + (int)(i+scroll) + " scroll : " + scroll);
				ItemStack item = loot.getItem().clone();
				ItemMeta meta = item.getItemMeta();
				ArrayList<String> lore = new ArrayList<String>();
				lore.add("§7Pourcentage : §c"+loot.getLuckPrct());
				lore.add("§7Quantité max : §c"+loot.getMax());
				meta.setLore(lore);
				item.setItemMeta(meta);
				inv.setItem(i+9, item);
			}
			else {
				inv.clear(i+9);
				//Bukkit.broadcastMessage("§7Clear item => slot : " + (int)(i+9) + " index : " + (int)(i+scroll) + " scroll : " + scroll);
			}
		}

		return inv;
	}
}
