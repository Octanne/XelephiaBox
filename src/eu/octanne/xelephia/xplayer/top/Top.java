package eu.octanne.xelephia.xplayer.top;

import java.lang.reflect.InvocationTargetException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;

import eu.octanne.xelephia.XelephiaPlugin;
import eu.octanne.xelephia.xplayer.XPlayer;

public class Top {
	
	private TopType type;
	private int nbPlayer;
	
	private Location topLocation;
	private List<ArmorStand> armorStandList = new ArrayList<>();
	
	
	public Top(TopType type, Location loc, int nbPlayer) {
		this.type = type;
		this.topLocation = loc;
		this.nbPlayer = nbPlayer;
		loadTop();
	}
	
	public void loadTop() {
		List<XPlayer> xPlayers = getTopPlayer(type, nbPlayer);
		ArmorStand standTitle = (ArmorStand) topLocation.getWorld().spawnEntity(topLocation, EntityType.ARMOR_STAND);
		standTitle.setGravity(false);
		standTitle.setVisible(false);
		standTitle.setCustomNameVisible(true);
		standTitle.setCustomName(type.getTitle());
		armorStandList.add(standTitle);
		for(XPlayer xP : xPlayers) {
			String data = "";
			try {
				data = ""+xP.getClass().getMethod(type.getMethod()).invoke(xP);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
					| NoSuchMethodException | SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			ArmorStand stand = (ArmorStand) topLocation.getWorld().spawnEntity(topLocation, EntityType.ARMOR_STAND);
			stand.setGravity(false);
			stand.setVisible(false);
			stand.setCustomNameVisible(true);
			stand.setCustomName(xP.getGrade().getDisplayName()+" "+xP.getName()+" : "+data+" "+type.getDisplayName());
			armorStandList.add(stand);
		}
	}
	
	public void updateTop() {
		List<XPlayer> xPlayers = getTopPlayer(type, nbPlayer);
		int i = 1;
		for(XPlayer xP : xPlayers) {
			String data = "";
			try {
				data = ""+xP.getClass().getMethod(type.getMethod()).invoke(xP);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
					| NoSuchMethodException | SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			ArmorStand stand = armorStandList.get(i);
			stand.setCustomName(xP.getGrade().getDisplayName()+" "+xP.getName()+" : "+data+" "+type.getDisplayName());
			i++;
		}
	}
	
	public void unloadTop() {
		for(ArmorStand stand : armorStandList) {
			stand.remove();
			stand.setHealth(0);
		}
	}
	
	static public List<XPlayer> getTopPlayer(TopType type, int nbPlayer) {
		List<XPlayer> top = new ArrayList<>();
		if(type.equals(TopType.COINS)) {
			try {
				PreparedStatement q = XelephiaPlugin.getPlayersDB().getConnection()
						.prepareStatement("SELECT uuid FROM players ORDER BY "+ type.getColumnName());
				ResultSet rs = q.executeQuery();
				boolean isExist = rs.next();
				if (!isExist) return top;
				
				for(int  i = 1; rs.next() || i <= nbPlayer; i++) {
					String strUUID = rs.getString("uuid");
					top.add(XelephiaPlugin.getXPlayer(UUID.fromString(strUUID)));
				}
				q.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return top;
		}else {
			return top;
		}
	}
	
	static private enum TopType {
		KILL("killCount", "Kill(s)", "§6TOP §7- §8(§cKill§8)", "getKillCount"),
		HIGHSTREAK("highKillStreak", "Kill(s)", "§6TOP §7- §8(§cKill Streak§8)", "getHighKillStreak"),
		COINS("coins", "coins", "§6TOP §7- §8(§eCoins§8)", "getCoins"),
		DEATH("deathCount", "mort(s)", "§6TOP §7- §8(§5Mort§8)", "getDeathCount");

		private String displayName;
		private String title;
		private String columnName;
		private String methodName;
		
		private TopType(String columnName, String displayName, String title, String methodName) {
			this.columnName = columnName;
			this.displayName = displayName;
			this.title = title;
			this.methodName = methodName;
		}
		
		public String getColumnName() {
			return columnName;
		}
		
		public String getDisplayName() {
			return displayName;
		}
		
		public String getTitle() {
			return title;
		}
		
		public String getMethod() {
			return methodName;
		}
	}
}
