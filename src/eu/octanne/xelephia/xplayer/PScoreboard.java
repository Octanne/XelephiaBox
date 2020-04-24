package eu.octanne.xelephia.xplayer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import eu.octanne.xelephia.XelephiaPlugin;
import eu.octanne.xelephia.lootzone.LootZoneManager;

public class PScoreboard {
	
	static private BukkitTask scoreBoarkTask;
	
	private XPlayer parent;
	
	private Scoreboard scoreboard;
	private Objective objective;
	
	@SuppressWarnings("unchecked")
	private List<String> lines = (List<String>) XelephiaPlugin.getMainConfig().get().getList("scoreboard.lines", new ArrayList<>());
	
	private String scoreboardName = XelephiaPlugin.getMainConfig().get().getString("scoreboard.name", "Scoreboard");
	
	PScoreboard(XPlayer xp) {
		this.parent = xp;
		
		scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
		objective = scoreboard.registerNewObjective(UUID.randomUUID().toString(), "dummy");
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		objective.setDisplayName(scoreboardName);
		
		for(int i = 0; i < lines.size(); i++) {
			String line = replaceVar(lines.get(i));
			Score score = objective.getScore(line);
			score.setScore(lines.size()-i);
		}
		parent.getBPlayer().setScoreboard(scoreboard);
	}
	
	public void update(){
		Objective objective = scoreboard.registerNewObjective(UUID.randomUUID().toString(), "dummy");
		objective.setDisplayName(scoreboardName);
		
		for(int i = 0; i < lines.size(); i++) {
			String line = replaceVar(lines.get(i));
			Score score = objective.getScore(line);
			score.setScore(lines.size()-i);
		}
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		this.objective.unregister();
		this.objective = null;
		this.objective = objective;
	}
	
	private String replaceVar(String line) {
		line = line.replace("{ONLINE}", Bukkit.getOnlinePlayers().size()+"");
		line = line.replace("{GRADE}", parent.getGrade().getTabPrefix());
		line = line.replace("{MAX}", Bukkit.getMaxPlayers()+"");
		line = line.replace("{PLAYERNAME}", parent.getName());
		line = line.replace("{COINS}", parent.df.format(parent.getCoins()));
		line = line.replace("{KILL}", parent.getKillCount()+"");
		line = line.replace("{KILLSTREAK}", parent.getKillStreak()+"");
		line = line.replace("{HIGHKILLSTREAK}", parent.getHighKillStreak()+"");
		line = line.replace("{DEATH}", parent.getDeathCount()+"");
		line = line.replace("{RESETLOOT}", parent.getTimeBeforeResetLoot() == "§9Entièrement chargé" ? "§9Chargé" : parent.getTimeBeforeResetLoot());
		line = line.replace("{RATIO}", parent.df.format(parent.getRatio()));
		line = line.replace("{UNTILLOOT}", ""+(LootZoneManager.maxLootPerHour-parent.getHourLoot()));
		line = line.replace("{COMBAT}", parent.getCombatStatut());
		return line;
	}
	
	public static BukkitTask getTask() {
		return scoreBoarkTask;
	}
	
	public static void startTask() {
		scoreBoarkTask = new BukkitRunnable() {

			@Override
			public void run() {
				for(XPlayer xp : XelephiaPlugin.getXPlayersOnline()) {
					xp.loadScoreboard();
				}
			}
			
		}.runTaskTimer(XelephiaPlugin.getInstance(), 0, 20);
	}
}
