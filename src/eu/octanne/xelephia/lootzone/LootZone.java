package eu.octanne.xelephia.lootzone;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class LootZone {

	protected Location pos;
	protected String name;

	protected int controlTime;

	protected List<Loot> loots;

	
	
	public LootZone(String name, Location pos) {
		this.pos = pos;
		this.name = name;

		loots = new ArrayList<Loot>();
	}
	
	protected LootZone(String name) {
		
	}

	protected void save() {
		
	}
	
	protected void load() {
		
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
}
