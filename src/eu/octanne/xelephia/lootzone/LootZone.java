package eu.octanne.xelephia.lootzone;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import eu.octanne.xelephia.util.ConfigYaml;

public class LootZone {

	protected Location pos;
	protected String name;

	protected int controlTime;

	protected List<Loot> loots;

	private ConfigYaml config;
	
	public LootZone(String name, Location pos, int controlTime) {
		config = new ConfigYaml("zone/"+name+".yml");
		
		this.pos = pos;
		this.name = name;
		this.controlTime = controlTime;
		
		loots = new ArrayList<Loot>();
		save();
	}
	
	protected LootZone(String name) {
		config = new ConfigYaml("zone/"+name+".yml");
		this.name = name;
		load();
	}

	protected void save() {
		config.getConfig().set("name", this.name);
		config.getConfig().set("pos", this.pos);
		config.getConfig().set("time", this.controlTime);
		config.getConfig().set("loots", this.loots);
		config.save();
	}
	
	@SuppressWarnings("unchecked")
	protected void load() {
		this.name = config.getConfig().getString("name", null);
		this.pos = (Location) config.getConfig().get("pos", null);
		this.controlTime = config.getConfig().getInt("time", 0);
		loots = (List<Loot>) config.getConfig().get("loots", new ArrayList<Loot>());
	}
	
	public void addLoot(Loot loot) {
		loots.add(loot);
	}

	public boolean inZone(Player p) {
		if(p.getLocation().distance(pos) <= 5) {
			return true;
		}else return false;
	}

	protected void setControlTime(int sec) {
		controlTime = sec;
	}

	public int getControlTime() {
		return controlTime;
	}

	public String getName() {
		return name;
	}
	
	public List<Loot> getLoots(){
		return loots;
	}
	
	protected void remove() {
		config.getFile().delete();
	}
}
