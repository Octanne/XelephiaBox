package eu.octanne.xelephia.lootzone;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import eu.octanne.xelephia.XelephiaPlugin;
import eu.octanne.xelephia.util.ConfigYaml;
import eu.octanne.xelephia.xplayer.XPlayer;
import eu.octanne.xelephia.xplayer.XPlayer.MessageType;

public class LootZone {

	protected Location pos;
	protected String name;

	protected int controlTime;

	protected List<Loot> loots;

	private HashMap<String,BukkitTask> playerInZone = new HashMap<String,BukkitTask>();

	private ConfigYaml config;

	private LootZoneListener listener;

	public LootZone(String name, Location pos, int controlTime) {
		config = new ConfigYaml("zone/"+name+".yml");
		listener = new LootZoneListener();

		this.pos = pos;
		this.name = name;
		this.controlTime = controlTime;

		loots = new ArrayList<Loot>();
		save();
		Bukkit.getPluginManager().registerEvents(listener, XelephiaPlugin.getInstance());
	}

	protected LootZone(String name) {
		config = new ConfigYaml("zone/"+name+".yml");
		listener = new LootZoneListener();
		this.name = name;
		load();
		Bukkit.getPluginManager().registerEvents(listener, XelephiaPlugin.getInstance());
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
		unregister();
	}

	private void unregister() {
		PlayerQuitEvent.getHandlerList().unregister(listener);
		PlayerMoveEvent.getHandlerList().unregister(listener);
	}

	protected void captureZone(Player p) {
		Bukkit.getScheduler().cancelTask(playerInZone.get(p.getName()).getTaskId());
		playerInZone.remove(p.getName());
		Bukkit.broadcastMessage("§eLoot §8| §aCapture de la zone §9" + name + "§a par §9" + p.getName() + "§a.");
		XPlayer xP = (XelephiaPlugin.getXPlayer(p.getName()));
		xP.sendMessage(MessageType.SUBTITLE, "§eLoot §8| §aCapture de la zone §9" + name + "§a.");
		p.playSound(xP.getBukkitPlayer().getLocation(),
				Sound.LEVEL_UP, 4.0F, xP.getBukkitPlayer().getLocation().getPitch());
		// Need to be finish
		// TODO
	}

	private class LootZoneListener implements Listener{

		@EventHandler
		public void onPlayerInZone(PlayerMoveEvent e) {
			Player p = e.getPlayer();
			XPlayer xP = (XelephiaPlugin.getXPlayer(p.getName()));
			// Enter Zone
			if(inZone(e.getTo()) && !playerInZone.containsKey(p.getName())) {
				xP.sendMessage(MessageType.SUBTITLE, "§eLoot §8| §bEntrée dans la zone §9" + name + "§b.");
				BukkitTask task = new BukkitRunnable() {

					int sec = 0;

					@Override
					public void run() {
						if(sec <= controlTime) {
							sec++;
						}else {
							captureZone(p);
							this.cancel();
						}
					}
				}.runTaskTimer(XelephiaPlugin.getInstance(), 0, 20);
				playerInZone.put(p.getName(),task);
			}
			// Leave Zone
			if(playerInZone.containsKey(p.getName()) && !inZone(e.getTo())) {
				Bukkit.getScheduler().cancelTask(playerInZone.get(p.getName()).getTaskId());
				playerInZone.remove(p.getName());
				xP.sendMessage(MessageType.SUBTITLE, "§eLoot §8| §cSorti de la zone §9" + name + "§c.");
			}
		}

		@EventHandler
		public void onPlayerQuit(PlayerQuitEvent e) {
			// Cancel if leave
			Player p = e.getPlayer();
			if(playerInZone.containsKey(p.getName())) {
				Bukkit.getScheduler().cancelTask(playerInZone.get(p.getName()).getTaskId());
				playerInZone.remove(p.getName());
			}
		}
	}
}
