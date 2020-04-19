package eu.octanne.xelephia.lootzone;

import java.io.File;
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
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import eu.octanne.xelephia.XelephiaPlugin;
import eu.octanne.xelephia.util.Utils;

public class LootZoneManager implements Listener {

	protected ArrayList<LootZone> lootZones = new ArrayList<LootZone>();

	private HashMap<String,LootZoneEdit> lootZoneEdit = new HashMap<>();

	private class LootZoneEdit{
		public LootZone zone;
		public Inventory inv;
		public int scroll;

		public LootZoneEdit(LootZone zone, Inventory inv) {
			this.zone = zone;
			this.inv = inv;
			scroll = 0;
		}	

		public int getScrollMax() {
			int scrollMax = 0;
			if(inv.getItem(17) != null) scrollMax = (zone.getLoots().size()-9+1);
			else scrollMax = (zone.getLoots().size()-9);
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
		LootZone zone = getZone(name);
		Inventory inv = openOrUpdateEditMenu(zone, 0, null);
		lootZoneEdit.put(p.getName(), new LootZoneEdit(zone, inv));
		p.openInventory(inv);
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
								Bukkit.broadcastMessage("Delete and Edit");
								e.setCancelled(true);
								int lootNb = e.getSlot() - 9 + zoneEdit.scroll;
								Loot eLoot = zoneEdit.zone.getLoots().get(lootNb);
								// Delete
								if(e.isShiftClick()) {
									zoneEdit.zone.getLoots().remove(eLoot);
									zoneEdit.zone.save();
									//zoneEdit.scroll-=1;
									openOrUpdateEditMenu(zoneEdit.zone, zoneEdit.scroll, zoneEdit.inv);
									//p.updateInventory();
								}
								// Edit QTE Max
								else if(e.getClick().equals(ClickType.DOUBLE_CLICK)){

								}
								else if(e.getClick().equals(ClickType.MIDDLE)) {

								}
							}else e.setCancelled(true);
						}else{
							e.setCancelled(true);
							// Scroll
							if(e.getSlot() == 24 && zoneEdit.scroll < zoneEdit.getScrollMax()) {
								openOrUpdateEditMenu(zoneEdit.zone, zoneEdit.scroll+1, zoneEdit.inv);
								zoneEdit.scroll+=1;
								//p.updateInventory();
							}else if(e.getSlot() == 20 && zoneEdit.scroll > 0) {
								openOrUpdateEditMenu(zoneEdit.zone, zoneEdit.scroll-1, zoneEdit.inv);
								zoneEdit.scroll-=1;
								//p.updateInventory();
							}else if(e.getSlot() == 26) {
								p.closeInventory();
								zoneEdit.zone.save();
								lootZoneEdit.remove(p.getName());
							}
						}
					}else if(e.getAction().equals(InventoryAction.MOVE_TO_OTHER_INVENTORY) && e.getView().getItem(17) != null){
						ItemStack newItem = e.getCurrentItem().clone();
						e.getClickedInventory().clear(e.getSlot());
						//p.updateInventory();
						zoneEdit.zone.addLoot(new Loot(newItem, 1, newItem.getAmount()));
						zoneEdit.zone.save();
						openOrUpdateEditMenu(zoneEdit.zone, zoneEdit.scroll, zoneEdit.inv);
						//p.updateInventory();
					}
				}
			}
		}
	}

	@EventHandler
	public void onCloseMenu(InventoryCloseEvent e) {
		if(e.getPlayer() instanceof Player) {
			Player p = (Player) e.getPlayer();
			if(e.getInventory() != null && lootZoneEdit.isEmpty() ? false : lootZoneEdit.containsKey(p.getName())) {
				LootZoneEdit zoneEdit = lootZoneEdit.get(p.getName());
				zoneEdit.zone.save();
				lootZoneEdit.remove(p.getName());
			}
		}
	}

	private Inventory openOrUpdateEditMenu(LootZone zone, int scroll, @Nullable Inventory INV) {

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

		int scrollMax = (inv.getItem(17) != null) ? (zone.getLoots().size()-9+1) : (zone.getLoots().size()-9);
		if(scrollMax <= 0) scrollMax = 0;
		if(scroll > scrollMax) scroll = scrollMax;

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
