package eu.octanne.xelephia.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import com.google.common.io.Files;
import com.google.gson.Gson;

public class Utils {

	static Gson gson = new Gson();

	// CREATE ITEM WITH DATA
	@SuppressWarnings("deprecation")
	static public ItemStack createItemStack(String DisplayName, Material id, int QteItem, ArrayList<String> Lore,
			int data, boolean Glowing) {

		ItemStack item = new ItemStack(id, QteItem, (short) 0, (byte) data);
		ItemMeta itemmeta = item.getItemMeta();
		itemmeta.setDisplayName(DisplayName);
		itemmeta.setLore(Lore);
		itemmeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		itemmeta.addItemFlags(ItemFlag.HIDE_DESTROYS);
		itemmeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		itemmeta.addItemFlags(ItemFlag.HIDE_PLACED_ON);
		itemmeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
		itemmeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		if (Glowing) {
			itemmeta.addEnchant(Enchantment.ARROW_FIRE, 10, true);
		}
		item.setItemMeta(itemmeta);
		return item;
	}

	@SuppressWarnings("deprecation")
	static public ItemStack createItemStack(String DisplayName, Material id, int QteItem, ArrayList<String> Lore,
			int data, ItemMeta meta) {

		ItemStack item = new ItemStack(id, QteItem, (short) 0, (byte) data);
		ItemMeta itemmeta = meta;
		itemmeta.setDisplayName(DisplayName);
		itemmeta.setLore(Lore);
		item.setItemMeta(itemmeta);
		return item;
	}

	// CREATE ITEM SKULL WITHOUT DATA
	static public ItemStack createItemSkull(String DisplayName, ArrayList<String> Lore, SkullType Type, String Owner,
			boolean Glowing) {

		ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (short) Type.ordinal());
		SkullMeta itemmeta = (SkullMeta) item.getItemMeta();
		itemmeta.setLore(Lore);
		itemmeta.setDisplayName(DisplayName);
		itemmeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		itemmeta.addItemFlags(ItemFlag.HIDE_DESTROYS);
		itemmeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		itemmeta.addItemFlags(ItemFlag.HIDE_PLACED_ON);
		itemmeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
		itemmeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		itemmeta.setOwner(Owner);
		if (Glowing) {
			itemmeta.addEnchant(Enchantment.DURABILITY, 10, true);
		}
		item.setItemMeta(itemmeta);
		return item;
	}

	static public Gson getGson() {
		return gson;
	}
	
	static public void copyAStream(InputStream stream, File target) {
		try {
			byte[] buffer = new byte[stream.available()];
			stream.read(buffer);
    	 
    	    Files.write(buffer, target);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/*private boolean isPvPActive(Entity e){
	if(Bukkit.getPluginManager().isPluginEnabled("WorldGuard")) {
		Player p;
		if(e instanceof Projectile && (((Projectile)e).getShooter()) instanceof Player) {
			p = (Player)((Projectile)e).getShooter();
		}else if(!(e instanceof Player)) return false;
		p = (Player)e;
		try {
			Class<?> classWorldGuardPlugin = Class.forName("com.sk89q.worldguard.bukkit.WorldGuardPlugin");
			Class<?> classRegionContainer = Class.forName("com.sk89q.worldguard.bukkit.RegionContainer");
			Class<?> classRegionQuery = Class.forName("com.sk89q.worldguard.bukkit.RegionQuery");
			Class<?> classStateFlag = Class.forName("com.sk89q.worldguard.protection.flags.StateFlag");
			Class<?> classDefaultFlag = Class.forName("com.sk89q.worldguard.protection.flags.DefaultFlag");
			Class<?> classRegionAssociable = Class.forName("com.sk89q.worldguard.protection.association.RegionAssociable");

			Object worldGuardPlugin = classWorldGuardPlugin.getMethod("inst").invoke(null);
			Object regionContainer = classWorldGuardPlugin.getMethod("getRegionContainer").invoke(worldGuardPlugin);
			Object query = classRegionContainer.getMethod("createQuery").invoke(regionContainer);

			Class[] wrapPara = {Player.class};
			Object wrapPlayer = classWorldGuardPlugin.getMethod("wrapPlayer", wrapPara).invoke(worldGuardPlugin, p);

			Object pvpFlag = classDefaultFlag.getField("PVP");

			Class[] queryPara = {Location.class, classRegionAssociable, classStateFlag};
			if((boolean)classRegionQuery.getMethod("testState", queryPara).invoke(query, p.getLocation(), wrapPlayer, pvpFlag)) {
				Bukkit.getLogger().info("[Xelephia] Work Function PVP ALLOW");
				return true;
			}else {
				Bukkit.getLogger().info("[Xelephia] Work Function PvP Not Allow");
				return false;
			}
		} catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException | NoSuchFieldException e1) {
			Bukkit.getLogger().info("[Xelephia] Error in WorldGuard Support System");
			e1.printStackTrace();
			return true;
		}
	}else return true;
	}*/
}
