package eu.octanne.xelephia.xplayer;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import eu.octanne.xelephia.XelephiaPlugin;
import eu.octanne.xelephia.lootzone.LootZoneManager;

public class PScoreboard {
	
	private XPlayer parent;
	
	private Scoreboard scoreboard;
	private Objective objective;
	
	@SuppressWarnings("unchecked")
	private List<String> lines = (List<String>) XelephiaPlugin.getMainConfig().get().getList("scoreboard.line", new ArrayList<>());
	
	private String scoreboardName = XelephiaPlugin.getMainConfig().get().getString("scoreboard.name", "Scoreboard");
	
	PScoreboard(XPlayer xp) {
		this.parent = xp;
		
		scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
		objective = scoreboard.registerNewObjective("Scoreboard", "dummy");
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		objective.setDisplayName(scoreboardName);
		
		int i = 0;
		for(String line : lines) {
			line = replaceVar(line);
			Score score = objective.getScore(line);
			score.setScore(i);
			i++;
		}
		xp.getBPlayer().setScoreboard(scoreboard);
	}
	
	public void update(){
		scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
		objective = scoreboard.registerNewObjective("Scoreboard", "dummy");
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		objective.setDisplayName(scoreboardName);
		
		int i = 0;
		for(String line : lines) {
			line = replaceVar(line);
			Score score = objective.getScore(line);
			score.setScore(i);
			i++;
		}
	}
	
	private String replaceVar(String line) {
		line = line.replace("{ONLINE}", Bukkit.getOnlinePlayers().size()+"");
		line = line.replace("{MAX}", Bukkit.getMaxPlayers()+"");
		line = line.replace("{PLAYERNAME}", parent.getName());
		line = line.replace("{COINS}", parent.df.format(parent.getCoins()));
		line = line.replace("{KILL}", parent.getKillCount()+"");
		line = line.replace("{KILLSTREAK}", parent.getKillStreak()+"");
		line = line.replace("{HIGHKILLSTREAK}", parent.getHighKillStreak()+"");
		line = line.replace("{DEATH}", parent.getDeathCount()+"");
		line = line.replace("{RESETLOOT}", parent.getTimeBeforeResetLoot());
		line = line.replace("{RATIO}", parent.df.format(parent.getRatio()));
		line = line.replace("{UNTILLOOT}", ""+(LootZoneManager.maxLootPerHour-parent.getHourLoot()));
		line = line.replace("{COMBAT}", parent.getCombatStatut());
		return line;
	}
}
