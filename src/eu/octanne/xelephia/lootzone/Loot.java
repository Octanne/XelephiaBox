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

	public Loot(ItemStack item, double luckPrct) {
		this.item = item;
		this.luckPrct = luckPrct;
	}

	public double getLuckPrct() {
		return luckPrct;
	}

	public ItemStack getItem() {
		return item;
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<>();
		map.put("item", item);
		map.put("percent", luckPrct);
		
		return map;
	}
	
	public static Loot deserialize(Map<String, Object> map) {
		return new Loot((ItemStack)map.get("item"),(Integer)map.get("percent"));
	}
}
