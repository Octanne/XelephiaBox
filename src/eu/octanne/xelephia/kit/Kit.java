package eu.octanne.xelephia.kit;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import eu.octanne.xelephia.util.ConfigYaml;

public class Kit {

	private String nameKit;
	private ConfigYaml config;
	private String unName;

	private int cost;
	private Material logo;
	private ItemStack contents[];

	public Kit(String pathFile) {
		config = new ConfigYaml("kits/" + pathFile);
		unName = pathFile.replace(".yml", "");
		load();
	}

	public Kit(String pathFile, String name, Material logo, int cost, ItemStack[] contents) {
		config = new ConfigYaml("kits/" + pathFile);
		this.unName = pathFile.replace(".yml", "");
		this.nameKit = name;
		this.logo = logo;
		this.cost = cost;
		this.contents = contents;
		save();
	}

	private boolean load() {
		nameKit = config.getConfig().getString("name", "null");
		if (nameKit == "null")
			return false;
		cost = config.getConfig().getInt("cost", -1);
		@SuppressWarnings("unchecked")
		ArrayList<ItemStack> contentsTemp = (ArrayList<ItemStack>) config.getConfig().get("contents");
		contents = contentsTemp.toArray(new ItemStack[contentsTemp.size()]);
		logo = Material.getMaterial(config.getConfig().getString("logo", "IRON_SWORD"));
		return true;
	}

	public boolean save() {
		config.set("name", nameKit);
		config.set("cost", cost);
		config.set("logo", logo.toString());
		config.set("contents", contents);
		config.save();
		return true;
	}

	public void setCost(int newCost) {
		cost = newCost;
	}

	public void setName(String newName) {
		nameKit = newName;
	}

	public void setContents(ItemStack[] newContents) {
		contents = newContents;
	}

	public void setLogo(Material newLogo) {
		logo = newLogo;
	}

	public String getName() {
		return nameKit;
	}

	public String getUnName() {
		return unName;
	}

	public ItemStack[] getContents() {
		return contents;
	}

	public int getCost() {
		return cost;
	}

	public Material getLogo() {
		return logo;
	}

	public void remove() {
		config.getFile().delete();
	}

	public boolean give(Player p) {
		ItemStack[] contentsG = contents.clone();
		for (ItemStack item : contentsG) {
			if (item == null)
				continue;
			if (p.getInventory().firstEmpty() != -1) p.getInventory().addItem(item);
			else p.getWorld().dropItem(p.getLocation(), item);
		}
		return true;
	}
}
