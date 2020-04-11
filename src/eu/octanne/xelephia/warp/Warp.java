package eu.octanne.xelephia.warp;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import eu.octanne.xelephia.XelephiaPlugin;
import eu.octanne.xelephia.util.Utils;

public class Warp {

	protected String name;
	protected Location location;
	protected ItemStack itemIcon;
	protected int slotInMenu;

	private int task;

	private Warp(String name) {
		if (XelephiaPlugin.getTeleportConfig().getConfig().isConfigurationSection("Warps." + name)) {
			this.name = name;
			@SuppressWarnings("unchecked")
			ArrayList<String> desc = (ArrayList<String>) XelephiaPlugin.getTeleportConfig().getConfig()
					.get("Warps." + name + ".desc");
			ItemStack item = Utils.createItemStack(
					XelephiaPlugin.getTeleportConfig().getConfig().getString("Warps." + name + ".name"),
					Material.getMaterial(
							XelephiaPlugin.getTeleportConfig().getConfig().getString("Warps." + name + ".itemIcon")),
					1, desc, XelephiaPlugin.getTeleportConfig().getConfig().getInt("Warps." + name + ".dataID"), false);
			itemIcon = item;
			location = (Location) XelephiaPlugin.getTeleportConfig().getConfig().get("Warps." + name + ".coordonate");
			slotInMenu = XelephiaPlugin.getTeleportConfig().getConfig().getInt("Warps." + name + ".slotNumber");
		}
	}

	public Warp(String name, Location loc, ItemStack itemIcon) {
		this.name = name;
		location = loc;
		this.itemIcon = itemIcon;
		save();
	}

	/*
	 * GETTERS
	 */
	public String getName() {
		return name;
	}

	public Location getLocation() {
		return location;
	}

	public ItemStack getItem() {
		return itemIcon;
	}

	public int getSlot() {
		return slotInMenu;
	}

	/*
	 * TELEPORT
	 */
	public void teleport(Player p) {
		if (p.hasPermission("xelephia.warp." + name)) {
			int x = p.getLocation().getBlockX(), y = p.getLocation().getBlockY(), z = p.getLocation().getBlockZ();
			p.sendMessage(XelephiaPlugin.getMessageConfig().getConfig().getString("warpPreTeleport").replace("{WARP}",
					getItem().getItemMeta().getDisplayName()));
			task = Bukkit.getScheduler().scheduleSyncRepeatingTask(XelephiaPlugin.getInstance(), new Runnable() {

				int sec = 5;

				@Override
				public void run() {
					if (sec == 0) {
						p.sendMessage(XelephiaPlugin.getMessageConfig().getConfig().getString("warpTeleport")
								.replace("{WARP}", getItem().getItemMeta().getDisplayName()));
						p.teleport(location);
						sec = 5;
						Bukkit.getScheduler().cancelTask(task);
					} else {
						if (x != p.getLocation().getBlockX() || y != p.getLocation().getBlockY()
								|| z != p.getLocation().getBlockZ()) {
							sec = 5;
							p.sendMessage(XelephiaPlugin.getMessageConfig().getConfig().getString("CancelTeleport"));
							Bukkit.getScheduler().cancelTask(task);
						} else {
							sec--;
						}
					}
				}
			}, 0, 20);
		} else {
			p.sendMessage(XelephiaPlugin.getMessageConfig().getConfig().getString("noPermission"));
		}
	}

	public void teleportByPass(Player p) {
		p.sendMessage(XelephiaPlugin.getMessageConfig().getConfig().getString("warpTeleport").replace("{WARP}",
				getItem().getItemMeta().getDisplayName()));
		p.teleport(location);
	}

	/*
	 * GET WARP
	 */
	static public Warp get(String name) {
		Warp warp = new Warp(name);
		if (warp.name != null) {
			return warp;
		} else
			return null;
	}

	/*
	 * SAVE WARP
	 */
	@SuppressWarnings({ "deprecation", "unchecked" })
	private void save() {
		if (!((ArrayList<String>) XelephiaPlugin.getTeleportConfig().getConfig().get("Warps.list",
				new ArrayList<String>())).contains(name)) {
			ArrayList<String> warpList = (ArrayList<String>) XelephiaPlugin.getTeleportConfig().getConfig()
					.get("Warps.list", new ArrayList<String>());
			warpList.add(name);
			XelephiaPlugin.getTeleportConfig().set("Warps.list", warpList);
			int size = warpList.size();
			slotInMenu = size - 1;
		}
		ArrayList<String> lore = new ArrayList<String>();
		if (itemIcon.getItemMeta().getLore() != null) {
			lore = (ArrayList<String>) itemIcon.getItemMeta().getLore();
		}
		XelephiaPlugin.getTeleportConfig().getConfig().set("Warps." + name + ".name", name);
		XelephiaPlugin.getTeleportConfig().getConfig().set("Warps." + name + ".desc", lore);
		XelephiaPlugin.getTeleportConfig().getConfig().set("Warps." + name + ".itemIcon",
				itemIcon.getType().toString());
		XelephiaPlugin.getTeleportConfig().getConfig().set("Warps." + name + ".dataID", itemIcon.getData().getData());
		XelephiaPlugin.getTeleportConfig().getConfig().set("Warps." + name + ".coordonate", location);
		XelephiaPlugin.getTeleportConfig().getConfig().set("Warps." + name + ".slotNumber", slotInMenu);
		XelephiaPlugin.getTeleportConfig().save();
	}
}
