package eu.octanne.xelephia.warp;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import eu.octanne.xelephia.XelephiaPlugin;
import eu.octanne.xelephia.util.Utils;

public class Warp {
	
	protected String name;
	protected Location location;
	protected ItemStack itemIcon;

	private WarpManager parent;

	protected Warp(String name, Location loc, ItemStack itemIcon, WarpManager parent) {
		this.parent = parent;
		this.name = name;
		location = loc;
		this.itemIcon = Utils.createItemStack(name, itemIcon.getType(), 1, new ArrayList<>(), itemIcon.getDurability(), false);
		save(name);
	}
	
	protected Warp(String path, WarpManager parent) {
		this.parent = parent;
		name = parent.warpConfig.get().getString(path+".name", "default");
		location = (Location) parent.warpConfig.get().get(path+".loc", Bukkit.getWorlds().get(0).getSpawnLocation());
		itemIcon = parent.warpConfig.get().getItemStack(path+".icon", new ItemStack(Material.DIRT));
	}
	
	/*
	 * SAVE
	 */
	private void save(String path) {
		parent.warpConfig.set(path+".name", name);
		parent.warpConfig.set(path+".loc", location);
		parent.warpConfig.set(path+".icon", itemIcon);
		parent.warpConfig.save();
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
			p.sendMessage(XelephiaPlugin.getMessageConfig().get().getString("warpPreTeleport").replace("{WARP}",name));
			new BukkitRunnable(){

				String name = p.getName();
				
				int x = p.getLocation().getBlockX(), 
					y = p.getLocation().getBlockY(), 
					z = p.getLocation().getBlockZ();
				
				int sec = 10;
				
				@Override
				public void run() {
					if (sec <= 0) {
						p.sendMessage(XelephiaPlugin.getMessageConfig().get().getString("warpTeleport").replace("{WARP}", name));
						sec = 10;
						this.cancel();
					} else {
						if (x != p.getLocation().getBlockX() || y != p.getLocation().getBlockY()
								|| z != p.getLocation().getBlockZ() || Bukkit.getPlayer(name) != null) {
							sec = 10;
							p.sendMessage(XelephiaPlugin.getMessageConfig().get().getString("CancelTeleport"));
							this.cancel();
						} else {
							sec--;
						}
					}
				}
				
			}.runTaskTimer(XelephiaPlugin.getInstance(), 0, 20);
		} else {
			p.sendMessage(XelephiaPlugin.getMessageConfig().get().getString("noPermission"));
		}
	}

	public void teleportByPass(Player p) {
		p.sendMessage(XelephiaPlugin.getMessageConfig().get().getString("warpTeleport").replace("{WARP}",
				name));
		p.teleport(location);
	}

}
