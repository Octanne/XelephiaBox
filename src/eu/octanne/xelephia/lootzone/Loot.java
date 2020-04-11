package eu.octanne.xelephia.lootzone;

import org.bukkit.inventory.ItemStack;

public class Loot {

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
}
