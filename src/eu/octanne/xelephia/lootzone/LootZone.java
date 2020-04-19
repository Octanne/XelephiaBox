package eu.octanne.xelephia.lootzone;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import eu.octanne.xelephia.XelephiaPlugin;
import eu.octanne.xelephia.util.ConfigYaml;
import eu.octanne.xelephia.xplayer.XPlayer;
import eu.octanne.xelephia.xplayer.XPlayer.MessageType;

public class LootZone {

	protected Location pos;
	protected String name;

	protected int controlTime;

	protected List<Loot> loots;
	
	private HashMap<String,Integer> playerInZone = new HashMap<String,Integer>();

	private ConfigYaml config;
	
	public LootZone(String name, Location pos, int controlTime) {
		config = new ConfigYaml("zone/"+name+".yml");
		
		this.pos = pos;
		this.name = name;
		this.controlTime = controlTime;
		
		loots = new ArrayList<Loot>();
		save();
		Bukkit.getPluginManager().registerEvents(new LootZoneListener(), XelephiaPlugin.getInstance());
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

	public boolean inZone(Location loc) {
		if(loc.distance(pos) <= 5) {
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
	
	protected void captureZone(Player p) {
		Bukkit.getScheduler().cancelTask(playerInZone.get(p.getName()));
		playerInZone.remove(p.getName());
		Bukkit.broadcastMessage(p.getName() + " viens de capturé la zone " + name);
		// Need to be finish
		// TODO
	}
	
	private class LootZoneListener implements Listener{
		
		@EventHandler
		public void onPlayerInZone(PlayerMoveEvent e) {
			Player p = e.getPlayer();
			// Enter Zone
			if(inZone(e.getTo()) && !playerInZone.containsKey(p.getName())) {
				Integer task = 0;
				playerInZone.put(p.getName(),task);
				task = Bukkit.getScheduler().runTaskTimer(XelephiaPlugin.getInstance(), new Runnable() {

					int sec = 0;
					
					@Override
					public void run() {
						if(sec <= controlTime) {
							sec++;
						}else {
							captureZone(p);
						}
					}
				}, 0, 20).getTaskId();
			}
			// Leave Zone
			if(playerInZone.containsKey(p.getName()) && !inZone(e.getTo())) {
				Bukkit.getScheduler().cancelTask(playerInZone.get(p.getName()));
				XPlayer xP = (XelephiaPlugin.getXPlayer(p.getName()));
				xP.sendMessage(MessageType.SUBTITLE, "§eLoot §8| §cCapture de la zone §9" + name + " §cinterrompu.");
			}
		}
		
		@EventHandler
		public void onPlayerQuit(PlayerQuitEvent e) {
			// Cancel if leave
			Player p = e.getPlayer();
			if(playerInZone.containsKey(p.getName())) {
				Bukkit.getScheduler().cancelTask(playerInZone.get(p.getName()));
				playerInZone.remove(p.getName());
			}
		}
	}
}
