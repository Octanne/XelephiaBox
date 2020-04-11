package eu.octanne.xelephia.lootzone;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.Inventory;

import eu.octanne.xelephia.XelephiaPlugin;

public class LootZoneManager implements Listener {

	protected ArrayList<LootZone> lootZones = new ArrayList<LootZone>();

	protected File zoneFolder = new File("plugins/Xelephia/zone/");

	public LootZoneManager() {
		// LootZone load

		// Register Listener
		Bukkit.getPluginManager().registerEvents(this, XelephiaPlugin.getInstance());
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
		LootZone zone = new LootZone(name, loc);
		zone.setControlTime(timeZone);
		lootZones.add(zone);
		return true;
	}

	@SuppressWarnings("unused")
	public void editLootZone(Player p, String name) {
		LootZone zone = getZone(name);
		Inventory inv = Bukkit.createInventory(null, 27, "§cLootZone §8| §9"+name);
		
	}
	
	public boolean removeZone(String name) {
		
		return false;
	}

	// Event Move
	@EventHandler
	public void onPlayerInZone(PlayerMoveEvent e) {
		
	}
}
