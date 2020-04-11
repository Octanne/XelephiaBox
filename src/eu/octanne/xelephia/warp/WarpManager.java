package eu.octanne.xelephia.warp;

import java.util.ArrayList;

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

public class WarpManager implements Listener {

	public WarpManager() {
		Bukkit.getPluginManager().registerEvents(this, XelephiaPlugin.getInstance());
	}

	/*
	 * CREATE NEW WARP
	 */
	public void createWarp(String name, Location loc, ItemStack itemIcon) {
		@SuppressWarnings("unused")
		Warp warp = new Warp(name, loc, itemIcon);
	}

	/*
	 * CONDITIONS
	 */
	public boolean isExist(String warpName) {
		if (Warp.get(warpName) != null) {
			return true;
		} else
			return false;
	}

	/*
	 * GET WARP
	 */
	public Warp getWarp(String warpName) {
		return Warp.get(warpName);
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
			inv.setItem(warp.getSlot(), warp.getItem());
		}
		p.openInventory(inv);
	}

	@SuppressWarnings("unchecked")
	public ArrayList<Warp> getWarps() {
		ArrayList<Warp> warps = new ArrayList<Warp>();
		for (String warpName : (ArrayList<String>) XelephiaPlugin.getTeleportConfig().getConfig().get("Warps.list",
				new ArrayList<String>())) {
			warps.add(Warp.get(warpName));
		}
		return warps;
	}
}
