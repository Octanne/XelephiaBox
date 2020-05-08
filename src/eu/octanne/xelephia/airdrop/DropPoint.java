package eu.octanne.xelephia.airdrop;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import eu.octanne.xelephia.util.ConfigYaml;
import eu.octanne.xelephia.xplayer.XPlayer;

public class DropPoint {

	protected Location pos;
	protected String name;

	protected int respawnTime;
	protected int minPlayers;
	
	protected List<Loot> loots;

	private ConfigYaml config;

	public DropPoint(String name, Location pos, int respawnTime) {
		config = new ConfigYaml("airdrop/"+name+".yml");

		this.pos = pos;
		this.name = name;
		this.respawnTime = respawnTime;
		this.minPlayers = 3;

		loots = new ArrayList<Loot>();
		save();
	}

	protected DropPoint(String name) {
		config = new ConfigYaml("airdrop/"+name+".yml");
		this.name = name;
		load();
	}

	protected void save() {
		config.get().set("name", this.name);
		config.get().set("pos", this.pos);
		config.get().set("time", this.respawnTime);
		config.get().set("minPlayers", this.minPlayers);
		config.get().set("loots", this.loots);
		config.save();
	}

	@SuppressWarnings("unchecked")
	protected void load() {
		this.name = config.get().getString("name", null);
		this.pos = (Location) config.get().get("pos", null);
		this.respawnTime = config.get().getInt("time", 5);
		this.minPlayers = config.get().getInt("minPlayers", 3);
		loots = (List<Loot>) config.get().get("loots", new ArrayList<Loot>());
	}

	public void addLoot(Loot loot) {
		loots.add(loot);
	}

	public String getName() {
		return name;
	}

	public int getMinPlayers() {
		return minPlayers;
	}
	
	public List<Loot> getLoots(){
		return loots;
	}

	protected void remove() {
		config.getFile().delete();	
	}

	protected void giveLoot(XPlayer p) {
		for(Loot loot : loots) {
			Random r = new Random();
			int prctInTenThousand = (int)loot.luckPrct*100;
			int drawLoot = r.nextInt((10000) + 1);
			if(drawLoot <= prctInTenThousand) {
				int minAmount = loot.getItem().getAmount();
				int rdmQuantity = r.nextInt((loot.max - minAmount) + 1) + minAmount;
				ItemStack item = loot.item.clone();
				item.setAmount(rdmQuantity);
				if (p.getBPlayer().getInventory().firstEmpty() != -1) p.getBPlayer().getInventory().addItem(item);
				else p.getBPlayer().getWorld().dropItem(p.getBPlayer().getLocation(), item);
			}
		}
	}

	protected int getOnlinePlayer() {
		int size = 0;
		for(Player p : Bukkit.getOnlinePlayers()) {
			if(p.getGameMode().equals(GameMode.ADVENTURE) || p.getGameMode().equals(GameMode.SURVIVAL))size++;
		}
		return size;
	}
}
