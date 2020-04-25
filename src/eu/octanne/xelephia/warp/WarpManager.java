package eu.octanne.xelephia.warp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import eu.octanne.xelephia.XelephiaPlugin;
import eu.octanne.xelephia.util.ConfigYaml;

public class WarpManager implements Listener {

	private ArrayList<Warp> warpList = new ArrayList<>();

	protected ConfigYaml warpConfig;

	public WarpManager() {
		warpConfig = new ConfigYaml("warps.yml");
		if(!warpConfig.getFile().exists())
			try {
				warpConfig.getFile().createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}

		loadWarps(warpConfig.get().getKeys(false));

		Bukkit.getPluginManager().registerEvents(this, XelephiaPlugin.getInstance());
	}

	public void loadWarps(Set<String> set) {
		for(String wName : set) {
			warpList.add(new Warp(wName, this));
		}
	}

	/*
	 * CREATE NEW WARP
	 */
	public void createWarp(String name, Location loc, ItemStack itemIcon) {
		warpList.add(new Warp(name, loc, itemIcon, this));
	}

	public Warp getWarp(String name) {
		for(Warp warp : warpList) {
			if(warp.getName().equals(""))return warp;
		}
		return null;
	}

	/*
	 * CONDITIONS
	 */
	public boolean isExist(String warpName) {
		for(Warp warp : warpList) {
			if(warp.getName().equalsIgnoreCase(warpName)) return true;
		}
		return false;
	}

	public ArrayList<Warp> getWarps(){
		return warpList;
	}

	/*
	 * BUKKIT EVENTS
	 */
	@EventHandler
	public void onClickInv(InventoryClickEvent e) {
		if (e.getClickedInventory() != null && e.getClickedInventory().getName().equals("§cWarps")) {
			e.setCancelled(true);
			for (Warp warp : getWarps()) {
				if (warp.getItem().equals(e.getCurrentItem())) {
					if (e.getWhoClicked().hasPermission("xelephia.bypass.tp")) {
						warp.teleportByPass((Player) e.getWhoClicked());
					} else
						warp.teleport((Player) e.getWhoClicked());
					e.getWhoClicked().closeInventory();
					break;
				}
			}
		}
	}
	@EventHandler
	public void onDrag(InventoryDragEvent e) {
		if (e.getInventory() != null && e.getInventory().getName().equals("§cWarps")) {
			e.setCancelled(true);
		}
	}

	/*
	 * WARP MENU
	 */
	public void openWarps(Player p) {
		Inventory inv = Bukkit.createInventory(null, 18, "§cWarps");
		for (Warp warp : getWarps()) {
			inv.addItem(warp.getItem());
		}
		p.openInventory(inv);
	}
}
