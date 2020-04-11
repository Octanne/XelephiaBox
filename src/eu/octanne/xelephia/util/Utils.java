package eu.octanne.xelephia.util;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

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
}
