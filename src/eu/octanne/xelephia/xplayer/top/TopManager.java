package eu.octanne.xelephia.xplayer.top;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;

import eu.octanne.xelephia.XelephiaPlugin;
import eu.octanne.xelephia.util.ConfigYaml;

public class TopManager {
	
	public List<Top> topList = new ArrayList<>();
	
	public TopManager() {
		enable();	
	}
	
	public void disable() {
		for(Top top : topList) {
			top.unloadTop();
		}
	}
	
	public void enable() {
		loadTops();
	}
	
	private void loadTops() {
		ConfigYaml config = new ConfigYaml("top.yml");
		for(String path : config.get().getKeys(false)) {
			topList.add(new Top(TopType.valueOf(config.get().getString(path+".type")), 
					(Location) config.get().get(path+".loc"), 
					config.get().getInt(path+".nbEntry")));
		}
	}
	
	static protected enum TopType {
		KILL("killCount", "getKillCount", "holoTop.kill.title", "holoTop.kill.unit"),
		HIGHSTREAK("highKillStreak", "getHighKillStreak", "holoTop.killstreak.title", "holoTop.killstreak.unit"),
		COINS("coins", "getCoins", "holoTop.coins.title", "holoTop.coins.unit"),
		DEATH("deathCount", "getDeathCount", "holoTop.death.title", "holoTop.death.unit");

		private String unitName;
		private String title;
		private String columnName;
		private String methodName;
		
		private TopType(String columnName, String methodName, String title, String unit) {
			this.columnName = columnName;
			this.unitName = unit;
			this.title = title;
			this.methodName = methodName;
		}
		
		public String getColumnName() {
			return columnName;
		}
		
		public String getUnit() {
			return XelephiaPlugin.getMainConfig().get().getString(unitName);
		}
		
		public String getTitle() {
			return XelephiaPlugin.getMainConfig().get().getString(title);
		}
		
		public String getMethod() {
			return methodName;
		}
	}
}
