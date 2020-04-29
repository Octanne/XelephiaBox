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
import eu.octanne.xelephia.xplayer.top.TopManager.TopType;

public class Top {

	private String name;

	private TopType type;
	private int nbPlayer;
	private Location topLocation;

	private List<ArmorStand> armorStandList = new ArrayList<>();


	protected Top(TopType type, Location loc, int nbPlayer, String name) {
		this.type = type;
		this.topLocation = loc;
		this.nbPlayer = nbPlayer;
		this.name = name;
		loadTop();
	}

	public void loadTop() {
		// Top Title
		ArmorStand standTitle = (ArmorStand) topLocation.getWorld().spawnEntity(topLocation, EntityType.ARMOR_STAND);
		standTitle.setGravity(false);
		standTitle.setVisible(false);
		standTitle.setCustomNameVisible(true);
		standTitle.setCustomName(type.getTitle());
		armorStandList.add(standTitle);

		for(int i = 1; armorStandList.size()-1 < nbPlayer; i++) {
			Location standLoc = topLocation.clone();
			standLoc.setY(topLocation.getY()-i*0.30-0.20);
			ArmorStand stand = (ArmorStand) standLoc.getWorld().spawnEntity(standLoc, EntityType.ARMOR_STAND);
			stand.setGravity(false);
			stand.setVisible(false);
			stand.setCustomNameVisible(true);
			stand.setCustomName("§7-----");
			armorStandList.add(stand);
		}
		updateTop();
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
			stand.setCustomName(xP.getGrade().getDisplayName()+" "+xP.getName()+" §8: §7"+data+" "+type.getUnit());
			i++;
		}
	}

	public void unloadTop() {
		for(ArmorStand stand : armorStandList) {
			stand.remove();
			stand.setHealth(0);
		}
	}

	public String getName() {
		return name;
	}

	protected List<XPlayer> getTopPlayer(TopType type, int nbPlayer) {
		List<XPlayer> top = new ArrayList<>();
		try {
			PreparedStatement q = XelephiaPlugin.getPlayersDB().getConnection()
					.prepareStatement("SELECT uuid FROM players ORDER BY "+type.getColumnName()+" DESC");
			ResultSet rs = q.executeQuery();
			boolean isExist = rs.next();
			if (!isExist) return top;

			for(int i = 1; rs.next() && i <= nbPlayer; i++) {
				String strUUID = rs.getString("uuid");
				top.add(XelephiaPlugin.getXPlayer(UUID.fromString(strUUID)));
			}
			q.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return top;
	}
}
