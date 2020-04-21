package eu.octanne.xelephia.warp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import eu.octanne.xelephia.XelephiaPlugin;
import eu.octanne.xelephia.util.Utils;

public class Warp implements ConfigurationSerializable {

	protected String name;
	protected Location location;
	protected ItemStack itemIcon;

	private int task;

	public Warp(String name, Location loc, ItemStack itemIcon) {
		this.name = name;
		location = loc;
		this.itemIcon = Utils.createItemStack(name, itemIcon.getType(), 1, new ArrayList<>(), itemIcon.getDurability(), false);
	}
	
	/*
	 * SERIALIZE
	 */
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<>();
		map.put("name", name);
		map.put("loc", location);
		map.put("itemIcon", itemIcon);
		
		return map;
	}
	
	public static Warp deserialize(Map<String, Object> map) {
		return new Warp((String)map.get("name"),(Location)map.get("loc"), (ItemStack)map.get("itemIcon"));
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

	/*
	 * TELEPORT
	 */
	public void teleport(Player p) {
		if (p.hasPermission("xelephia.warp." + name)) {
			int x = p.getLocation().getBlockX(), y = p.getLocation().getBlockY(), z = p.getLocation().getBlockZ();
			p.sendMessage(XelephiaPlugin.getMessageConfig().getConfig().getString("warpPreTeleport").replace("{WARP}",
					name));
			task = Bukkit.getScheduler().scheduleSyncRepeatingTask(XelephiaPlugin.getInstance(), new Runnable() {

				int sec = 5;

				@Override
				public void run() {
					if (sec == 0) {
						p.sendMessage(XelephiaPlugin.getMessageConfig().getConfig().getString("warpTeleport")
								.replace("{WARP}", name));
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
				name));
		p.teleport(location);
	}

}
