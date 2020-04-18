package eu.octanne.xelephia.lootzone;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.inventory.ItemStack;

@SerializableAs("Loot")
public class Loot implements ConfigurationSerializable{

	protected ItemStack item;
	protected double luckPrct;
	protected int max;

	public Loot(ItemStack item, double luckPrct, int max) {
		this.item = item;
		this.luckPrct = luckPrct;
		this.max = max;
	}

	public double getLuckPrct() {
		return luckPrct;
	}

	public int getMax() {
		return max;
	}
	
	public ItemStack getItem() {
		return item;
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<>();
		map.put("item", item);
		map.put("percent", luckPrct);
		map.put("max", max);
		
		return map;
	}
	
	public static Loot deserialize(Map<String, Object> map) {
		return new Loot((ItemStack)map.get("item"),(Double)map.get("percent"), (Integer)map.get("max"));
	}
}
