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
			return (zone.getLoots().size()-10) <= 0 ? 1 : zone.getLoots().size()-10;
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
		Inventory inv = openEditMenu(zone, 0, null);
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
				if(zoneEdit.inv.equals(e.getClickedInventory())) {
					// Delete Loot, Edit % or Edit max
					if((e.getSlot() < 9 || e.getSlot() > 17)) {
						
					}else {
						e.setCancelled(true);
						// Scroll
						if(e.getSlot() == 24 && zoneEdit.scroll < zoneEdit.getScrollMax()) openEditMenu(zoneEdit.zone, 
								zoneEdit.scroll+1, zoneEdit.inv);
						else if(e.getSlot() == 20 && zoneEdit.scroll > 0) openEditMenu(zoneEdit.zone, 
								zoneEdit.scroll-1, zoneEdit.inv);
					}
				}else return;
			}else return;
		}
	}
	
	@EventHandler
	public void onCloseMenu(InventoryCloseEvent e) {
		
	}
	
	private Inventory openEditMenu(LootZone zone, int scroll, @Nullable Inventory INV) {
		
		int scrollMax = (zone.getLoots().size()-10) <= 0 ? 1 : zone.getLoots().size()-10;
		if(scroll > scrollMax) scroll = scrollMax;
		
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
			ArrayList<String> tutoLore = new ArrayList<>();
			tutoLore.add("§aActions disponible:");
			tutoLore.add("§cClick §7diter pourcentage");
			tutoLore.add("§cShift-Click §7pour supprimer");
			tutoLore.add("§cDouble-Click §7editer quantité max");
			ItemStack tutorialItem = Utils.createItemSkull("§7Tutoriel", tutoLore, SkullType.PLAYER, "MHF_Question", false);;
			
			inv.setItem(20, rollLeftItem);
			inv.setItem(22, tutorialItem);
			inv.setItem(24, rollRightItem);
		}
		
		// Info
		ArrayList<String> infoLore = new ArrayList<>();
		infoLore.add("§7Nombres de loot : §c"+zone.getLoots().size());
		infoLore.add("§7Temps de contrôle : §c"+zone.getControlTime()+" §7secs");
		infoLore.add("§7Location : (§c"+zone.pos.getBlockX()+"§7, §c"+zone.pos.getBlockY()+"§7,"
				+ " §c"+zone.pos.getBlockZ()+"§7)");
		ItemStack infoItem = Utils.createItemStack("§aInformations", Material.BOOK, 1, infoLore, 0, false);
		inv.setItem(4, infoItem);

		// LootItems
		for(int i = 9; i > 18; i++) {
			Loot loot;
			if(i+scroll < zone.getLoots().size()) loot = zone.getLoots().get(i+scroll);
			else {
				inv.clear(i);
				continue;
			}
			ItemStack item = loot.getItem().clone();
			ItemMeta meta = item.getItemMeta();
			ArrayList<String> lore = new ArrayList<>();
			lore.add(" ");
			lore.add("§7Pourcentage : §c"+loot.getLuckPrct());
			lore.add("§7Quantité max : §c"+loot.getMax());
			meta.setLore(lore);
			item.setItemMeta(meta);
			inv.setItem(i, item);
		}
		
		return inv;
	}
}
