package eu.octanne.xelephia.lootzone;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
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
	protected int minPlayers;
	
	protected List<Loot> loots;
	
	BukkitTask broadcastTask;
	
	private enum TryCaptureResult {
		CAN_CAPTURE(),
		NEED_MORE_PLAYERS(),
		NO_MORE_CAPTURE();
	}

	private HashMap<String,BukkitTask> playerInCapture = new HashMap<String,BukkitTask>();

	private HashMap<String, TryCaptureResult> playerInBroadcast = new HashMap<String,TryCaptureResult>();
	
	private ConfigYaml config;

	private LootZoneListener listener;

	public LootZone(String name, Location pos, int controlTime) {
		config = new ConfigYaml("zone/"+name+".yml");
		listener = new LootZoneListener();

		this.pos = pos;
		this.name = name;
		this.controlTime = controlTime;
		this.minPlayers = 3;

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
		config.getConfig().set("minPlayers", this.minPlayers);
		config.getConfig().set("loots", this.loots);
		config.save();
	}

	@SuppressWarnings("unchecked")
	protected void load() {
		this.name = config.getConfig().getString("name", null);
		this.pos = (Location) config.getConfig().get("pos", null);
		this.controlTime = config.getConfig().getInt("time", 5);
		this.minPlayers = config.getConfig().getInt("minPlayers", 3);
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

	public int getMinPlayers() {
		return minPlayers;
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
		for(BukkitTask task : playerInCapture.values()) {
			task.cancel();
		}
		playerInCapture = null;
		playerInBroadcast = null;
		broadcastTask.cancel();
	}

	private void giveLoot(XPlayer p) {
		for(Loot loot : loots) {
			Random r = new Random();
			int prctInTenThousand = (int)loot.luckPrct*100;
			int drawLoot = r.nextInt((10000) + 1);
			if(drawLoot <= prctInTenThousand) {
				int minAmount = loot.getItem().getAmount();
				int rdmQuantity = r.nextInt((loot.max - minAmount) + 1) + minAmount;
				ItemStack item = loot.item.clone();
				item.setAmount(rdmQuantity);
				if (p.getBukkitPlayer().getInventory().firstEmpty() != -1) p.getBukkitPlayer().getInventory().addItem(item);
				else p.getBukkitPlayer().getWorld().dropItem(p.getBukkitPlayer().getLocation(), item);
			}
		}
	}
	
	private void captureZone(XPlayer p) {
		// BroadCast
		Bukkit.broadcastMessage("§eLoot §8| §aCapture de la zone §9" + name + "§a par §9" + p.getName() + "§a.");
		p.sendMessage(MessageType.SUBTITLE, "§eLoot §8| §aCapture de la zone §9" + name + "§a.");
		p.getBukkitPlayer().playSound(p.getBukkitPlayer().getLocation(),
				Sound.LEVEL_UP, 4.0F, p.getBukkitPlayer().getLocation().getPitch());
		// Scheduler
		Bukkit.getScheduler().cancelTask(playerInCapture.get(p.getName()).getTaskId());
		playerInCapture.remove(p.getName());
		// Limit Loot
		p.incrementHourLoot();
		if(p.getLastLootDate() == null) {
			p.updateLastLootDate();
		}
		// Give Loot
		giveLoot(p);
	}
	
	private void startBroadcastTask() {
		if(broadcastTask == null || !Bukkit.getScheduler().isCurrentlyRunning(broadcastTask.getTaskId()));
			broadcastTask = new BukkitRunnable() {

				@Override
				public void run() {
					if(!playerInBroadcast.isEmpty())
					for(String p : playerInBroadcast.keySet()) {
						TryCaptureResult result = playerInBroadcast.get(p);
						XPlayer xP = XelephiaPlugin.getXPlayer(p);
						if(result.equals(TryCaptureResult.NEED_MORE_PLAYERS)) {
							xP.sendMessage(MessageType.ACTIONBAR, "§cCapture impossible §e| §bIl manque §e"+(minPlayers-Bukkit.getOnlinePlayers().size())+"§b joueurs en ligne.");
						}else if(result.equals(TryCaptureResult.NO_MORE_CAPTURE)){
							xP.sendMessage(MessageType.ACTIONBAR, "§cCapture impossible §e| §bLimite de captures pour l'heure atteinte.");
						}else {
							Bukkit.getLogger().info("Erreur broadcastTask in LootZone : " + name);
						}
					}
					else this.cancel();
				}
			}.runTaskTimer(XelephiaPlugin.getInstance(), 0, 30);
	}

	private TryCaptureResult canCapture(XPlayer p) {
		p.updateLoots();
		if(Bukkit.getOnlinePlayers().size() < minPlayers) {
			return TryCaptureResult.NEED_MORE_PLAYERS;
		}else if(p.getHourLoot() > LootZoneManager.maxLootPerHour) {
			return TryCaptureResult.NO_MORE_CAPTURE;
		}else {
			return TryCaptureResult.CAN_CAPTURE;
		}
	}
	
	private class LootZoneListener implements Listener{

		@EventHandler
		public void onPlayerInZone(PlayerMoveEvent e) {
			Player p = e.getPlayer();
			XPlayer xP = (XelephiaPlugin.getXPlayer(p.getName()));
			// Enter Zone
			if(inZone(e.getTo()) && !inZone(e.getFrom())) {
				xP.sendMessage(MessageType.SUBTITLE, "§eLoot §8| §bEntrée dans la zone §9" + name + "§b.");
				TryCaptureResult result = canCapture(xP);
				
				// CAN CAPTURE
				if(result.equals(TryCaptureResult.CAN_CAPTURE)) {
					BukkitTask task = new BukkitRunnable() {

						int sec = 0;

						@Override
						public void run() {
							if(sec < controlTime) {
								xP.sendMessage(MessageType.ACTIONBAR, "§6Capture en cours §e| §bTemps restant :§e "+(controlTime-sec)+" §bsec(s) !");
								sec++;
							}else {
								captureZone(xP);
								this.cancel();
							}
						}
					}.runTaskTimer(XelephiaPlugin.getInstance(), 0, 20);
					playerInCapture.put(p.getName(), task);
				}else {
					startBroadcastTask();
					playerInBroadcast.put(p.getName(), result);
				}
			}
			// Leave Zone
			if(inZone(e.getFrom()) && !inZone(e.getTo())) {
				xP.sendMessage(MessageType.SUBTITLE, "§eLoot §8| §cSorti de la zone §9" + name + "§c.");
				if(playerInCapture.containsKey(p.getName())) {
					Bukkit.getScheduler().cancelTask(playerInCapture.get(p.getName()).getTaskId());
					playerInCapture.remove(p.getName());
				}else if(playerInBroadcast.containsKey(p.getName())){
					playerInBroadcast.remove(p.getName());
				}
			}
		}

		@EventHandler
		public void onPlayerQuit(PlayerQuitEvent e) {
			// Cancel if leave
			Player p = e.getPlayer();
			if(playerInCapture.containsKey(p.getName())) {
				Bukkit.getScheduler().cancelTask(playerInCapture.get(p.getName()).getTaskId());
				playerInCapture.remove(p.getName());
			}else if(playerInBroadcast.containsKey(p.getName())){
				playerInBroadcast.remove(p.getName());
			}
		}
	}
}
