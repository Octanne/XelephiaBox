package eu.octanne.xelephia.kit;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import eu.octanne.xelephia.XelephiaPlugin;
import eu.octanne.xelephia.util.Utils;
import eu.octanne.xelephia.xplayer.XPlayer;

public class KitSystem implements Listener {

	public static ItemStack selectorItem;

	protected class preKit {
		public Inventory inv;
		public int cost;
		public String name;
		public Material logo;

		public preKit(Inventory inv, int cost, String name, Material logo) {
			this.inv = inv;
			this.cost = cost;
			this.name = name;
			this.logo = logo;
		}
	}

	protected File kitsFolder = new File("plugins/Xelephia/kits/");

	private HashMap<Player, preKit> preKitList = new HashMap<Player, preKit>();

	protected ArrayList<Kit> kitsList = new ArrayList<Kit>();

	public KitSystem() {
		ArrayList<String> loreSelector = new ArrayList<String>();
		loreSelector.add(" ");
		loreSelector.add("§7Clique pour ouvrir");
		selectorItem = Utils.createItemStack("§eChoix du kit", Material.NETHER_STAR, 1, loreSelector, 0, true);
		Bukkit.getPluginManager().registerEvents(this, XelephiaPlugin.getInstance());
		if (!kitsFolder.exists())
			kitsFolder.mkdirs();
		loadKits();
	}

	private void loadKits() {
		FilenameFilter filter = (dir, name) -> name.endsWith(".yml");
		for (File kitFile : kitsFolder.listFiles(filter)) {
			Kit kit = new Kit(kitFile.getName());
			if (kit.getName() != null)
				kitsList.add(kit);
		}
	}

	public boolean createKit(String nameKit, int cost, Player p, Material logo) {

		Inventory inv = Bukkit.createInventory(null, 27, "§aDéposer les items ici");
		preKitList.put(p, new preKit(inv, cost, nameKit, logo));
		p.openInventory(inv);
		return true;
	}

	public boolean editKit(String nameKit, Player p) {
		for (Kit kit : kitsList) {
			if (kit.getUnName().equalsIgnoreCase(nameKit)) {
				Inventory inv = Bukkit.createInventory(null, 27, "§aEditer le kit");
				inv.setContents(kit.getContents());
				preKitList.put(p, new preKit(inv, 0, nameKit, Material.AIR));
				p.openInventory(inv);
				return true;
			} else
				continue;
		}
		return false;
	}

	public boolean removeKit(String nameKit) {
		for (Kit kit : kitsList) {
			if (kit.getUnName().equalsIgnoreCase(nameKit)) {
				kit.remove();
				kitsList.remove(kit);
				return true;
			} else
				continue;
		}
		return false;
	}

	public File getKitFolder() {
		return kitsFolder;
	}

	public ArrayList<Kit> getKits() {
		return kitsList;
	}
	
	private Kit getKit(int slot) {
		return kitsList.get(slot);
	}

	private Kit getKit(String name) {
		for (Kit kit : kitsList) {
			if (kit.getUnName().equalsIgnoreCase(name))
				return kit;
		}
		return null;
	}

	public void openMenu(Player p) {
		Inventory menuKit = Bukkit.createInventory(null, 27, "§6Kit | §eChoix des kits");
		XPlayer xP = XelephiaPlugin.getXPlayer(p.getUniqueId());

		for (Kit kit : kitsList) {
			boolean hasKit = (kit.getCost() == 0 ) ? true : (xP.getUnlockKit().contains(kit.getUnName()));
			/*
			 * Lore
			 */
			ArrayList<String> lore = new ArrayList<String>();
			lore.add("§7Prix : §c" + kit.getCost() + " §6coins");
			lore.add(" ");
			if (hasKit)
				lore.add("§aVous possédez ce kit.");
			else
				lore.add("§cVous ne possédez pas ce kit.");
			if (hasKit)
				lore.add("§cDouble-Click §7pour équiper.");
			else
				lore.add("§cDouble-Click §7pour acheter.");
			lore.add("§cMiddle-Click §7pour voir le kit.");
			menuKit.addItem(Utils.createItemStack(kit.getName(), kit.getLogo(), 1, lore, 0, hasKit));
		}
		p.openInventory(menuKit);
	}

	/*
	 * Listener
	 */
	@EventHandler
	public void onInKitsMenu(InventoryClickEvent e) {
		if (e.getWhoClicked() instanceof Player && e.getClickedInventory() != null) {
			if (e.getView().getTopInventory().getName().equals("§6Kit | §eChoix des kits")) {
				if(e.getRawSlot() <= 26 || e.getAction().equals(InventoryAction.MOVE_TO_OTHER_INVENTORY)) {
					e.setCancelled(true);
					if (e.getCurrentItem() != null && !e.getCurrentItem().getType().equals(Material.AIR)) {
						Kit kit = getKit(e.getSlot());
						XPlayer xP = XelephiaPlugin.getXPlayer(e.getWhoClicked().getUniqueId());

						// Middle Click => View Kit
						if (e.getClick().equals(ClickType.MIDDLE)) {
							Inventory kitV = Bukkit.createInventory(null, 27, "§9Visualisation du kit");
							kitV.setContents(kit.getContents());
							e.getWhoClicked().openInventory(kitV);
						}
						// Other Click => Select or Buy
						else if (e.getClick().equals(ClickType.DOUBLE_CLICK)) {
							// Buy
							if (e.getCurrentItem().getEnchantments().isEmpty()) {
								if (xP.getCoins() >= kit.getCost()) {
									xP.getBPlayer().playSound(xP.getBPlayer().getLocation(), Sound.ORB_PICKUP,
											4.0F, xP.getBPlayer().getLocation().getPitch());
									xP.getUnlockKit().add(kit.getUnName());
									xP.takeCoins(kit.getCost());
									openMenu(xP.getBPlayer());
									xP.getBPlayer().sendMessage("§cKit §7| §aAchat du kit " + kit.getName()
									+ "§a pour §6" + kit.getCost() + " §acoins.");
									return;
								} else {
									xP.getBPlayer().playSound(xP.getBPlayer().getLocation(),
											Sound.ENDERDRAGON_HIT, 4.0F, xP.getBPlayer().getLocation().getPitch());
									xP.getBPlayer().sendMessage("§cKit §7| §cFond insuffisant vous n'avez que §6"
											+ xP.getCoins() + " §ccoins.");
									return;
								}
							}
							// Equip
							else if(!xP.kitEquiped()){
								kit.give(xP.getBPlayer());
								xP.getBPlayer().closeInventory();
								xP.getBPlayer().playSound(xP.getBPlayer().getLocation(), Sound.ORB_PICKUP, 3.0F,
										xP.getBPlayer().getLocation().getPitch());
								xP.setKitEquiped(true);
								return;
							}
							// Already Equiped
							else {
								xP.getBPlayer().closeInventory();
								xP.getBPlayer().playSound(xP.getBPlayer().getLocation(),
										Sound.ENDERDRAGON_HIT, 4.0F, xP.getBPlayer().getLocation().getPitch());
								xP.getBPlayer().sendMessage("§cKit §7| §cVous avez déjà choisit votre kit.");
								return;
							}
						}
					}
				}
			} else if (e.getView().getTopInventory().getName().equals("§9Visualisation du kit")) {
				if(e.getRawSlot() <= 26 || e.getAction().equals(InventoryAction.MOVE_TO_OTHER_INVENTORY)) {
					e.setCancelled(true);
				}
			}
		}
	}
	@EventHandler
	public void onCloseMenu(InventoryCloseEvent e) {
		if (e.getInventory().getName().equals("§9Visualisation du kit")) {
			Bukkit.getScheduler().scheduleSyncDelayedTask(XelephiaPlugin.getInstance(), new Runnable() {
				@Override
				public void run() {
					openMenu((Player) e.getPlayer());
				}
			}, 8);
		}
		if (e.getInventory().getName().equals("§aDéposer les items ici")) {
			if (preKitList.containsKey(e.getPlayer())) {
				preKit preKit = preKitList.get(e.getPlayer());
				kitsList.add(
						new Kit(preKit.name + ".yml", preKit.name, preKit.logo, preKit.cost, preKit.inv.getContents()));
				preKitList.remove(e.getPlayer());
				e.getPlayer().sendMessage(
						"§cKit §8| §aCréation du kit §9" + preKit.name + "§a au prix de §6" + preKit.cost + " §acoins.");
				((Player) e.getPlayer()).playSound(e.getPlayer().getLocation(), Sound.LEVEL_UP, 3.0F,
						e.getPlayer().getLocation().getPitch());
			}
		}
		if (e.getInventory().getName().equals("§aEditer le kit")) {
			if (preKitList.containsKey(e.getPlayer())) {
				preKit preKit = preKitList.get(e.getPlayer());
				Kit kit = getKit(preKit.name);
				kit.setContents(preKit.inv.getContents());
				kit.save();
				e.getPlayer().sendMessage(
						"§cKit §8| §aModification du kit §9" + preKit.name + "§a sauvegarder.");
				((Player) e.getPlayer()).playSound(e.getPlayer().getLocation(), Sound.ORB_PICKUP, 3.0F,
						e.getPlayer().getLocation().getPitch());
			}
		}
	}
}
