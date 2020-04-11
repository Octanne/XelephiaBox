package eu.octanne.xelephia.xplayer;

import java.math.RoundingMode;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.SkullType;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.google.gson.reflect.TypeToken;

import eu.octanne.xelephia.XelephiaPlugin;
import eu.octanne.xelephia.util.Utils;
import net.minecraft.server.v1_8_R3.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle.EnumTitleAction;

public class XPlayer {

	public enum MessageType {
		ACTIONBAR,
		SUBTITLE
	}

	// Decimal Format
	DecimalFormat df = new DecimalFormat("#.##");

	protected BukkitTask combatTask;
	protected boolean inCombat = false;
	private boolean combatRelaunch = false;
	protected String lastDamagerName;
	
	protected UUID playerUUID;
	protected String lastPlayerName;
	
	// Stats INV Menu
	protected Inventory menuStats;

	// STATS
	protected int killCount;
	protected int deathCount;

	protected int actualKillStreak;
	protected int highKillStreak;

	protected double coins;

	protected int totalLoot;

	protected boolean kitEquiped;
	
	protected int hourLoot;
	protected Date lastLootDate;

	protected ArrayList<String> unlockKits;
	
	protected HashMap<String, Double> damageTaken = new HashMap<String, Double>();
	protected double totalDamage = 0;
	
	protected XPlayer lastMessenger;

	public XPlayer(UUID pUUID) {
		// Decimal Format
		df.setRoundingMode(RoundingMode.HALF_EVEN);
		DecimalFormatSymbols dfs = df.getDecimalFormatSymbols();
		dfs.setDecimalSeparator('.');
		df.setDecimalFormatSymbols(dfs);

		this.playerUUID = pUUID;
		this.lastPlayerName = Bukkit.getOfflinePlayer(pUUID).getName();
		this.menuStats = Bukkit.createInventory(null, 27, "§8Statistiques de §b" + this.lastPlayerName);
		//Bukkit.getPluginManager().registerEvents(this, XelephiaPlugin.getInstance());

		/*
		 * DATABASE QUERRY OR CREATE
		 */
		try {
			PreparedStatement q = XelephiaPlugin.getPlayersDB().getConnection()
					.prepareStatement("SELECT playerName, uuid, coins, killCount, deathCount, actualKillStreak, "
							+ "highKillStreak, lastLootDate, totalLoot, hourLoot, unlockKits, kitEquiped FROM players WHERE uuid=?");
			q.setString(1, pUUID.toString());
			ResultSet rs = q.executeQuery();
			boolean isExist = rs.next();
			if (isExist) {
				this.playerUUID = UUID.fromString(rs.getString("uuid"));
				this.coins = rs.getInt("coins");
				this.killCount = rs.getInt("killCount");
				this.deathCount = rs.getInt("deathCount");
				this.actualKillStreak = rs.getInt("actualKillStreak");
				this.highKillStreak = rs.getInt("highKillStreak");
				this.hourLoot = rs.getInt("hourLoot");
				this.hourLoot = rs.getInt("totalLoot");
				this.lastLootDate = rs.getDate("lastLootDate");
				this.unlockKits = Utils.getGson().fromJson(rs.getString("unlockKits"),
						new TypeToken<ArrayList<String>>() {
						}.getType());
				if(rs.getString("kitEquiped").equalsIgnoreCase("true")) this.kitEquiped = true;
				else this.kitEquiped = false;
				
				Bukkit.getLogger().log(Level.INFO, "[Xelephia] Chargement du joueur : " + this.lastPlayerName + " !");
			} else {
				this.coins = 0;
				this.killCount = 0;
				this.deathCount = 0;
				this.actualKillStreak = 0;
				this.highKillStreak = 0;
				this.totalLoot = 0;
				this.hourLoot = 0;
				this.unlockKits = new ArrayList<String>();
				this.kitEquiped = false;
				
				PreparedStatement qCreate = XelephiaPlugin.getPlayersDB().getConnection()
						.prepareStatement("INSERT INTO players (playerName, uuid, coins, killCount, deathCount, "
								+ "actualKillStreak, highKillStreak, lastLootDate, totalLoot, hourLoot, unlockKits, kitEquiped) "
								+ "VALUES (?,?,?,?,?,?,?,?,?,?,?,?)");
				qCreate.setString(1, this.lastPlayerName);
				qCreate.setString(2, this.playerUUID.toString());
				qCreate.setDouble(3, this.coins);
				qCreate.setInt(4, this.killCount);
				qCreate.setInt(5, this.deathCount);
				qCreate.setInt(6, this.actualKillStreak);
				qCreate.setInt(7, this.highKillStreak);
				qCreate.setDate(8, this.lastLootDate);
				qCreate.setInt(9, this.totalLoot);
				qCreate.setInt(10, this.hourLoot);
				qCreate.setString(11, Utils.getGson().toJson(unlockKits));
				qCreate.setString(12, ""+this.kitEquiped);
				
				qCreate.execute();
				qCreate.close();
				Bukkit.getLogger().log(Level.INFO, "[Xelephia] Création du joueur : " + this.lastPlayerName + " !");
			}
			q.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public boolean saveIntoDB() {
		try {
			PreparedStatement qCreate = XelephiaPlugin.getPlayersDB().getConnection()
					.prepareStatement("UPDATE players SET playerName = ?, coins = ?, killCount = ?, "
							+ "deathCount = ?, actualKillStreak = ?, highKillStreak = ?, lastLootDate = ?, "
							+ "totalLoot = ?, hourLoot = ?, unlockKits = ?, kitEquiped = ? WHERE uuid = ?");
			qCreate.setString(1, this.lastPlayerName);
			qCreate.setDouble(2, this.coins);
			qCreate.setInt(3, this.killCount);
			qCreate.setInt(4, this.deathCount);
			qCreate.setInt(5, this.actualKillStreak);
			qCreate.setInt(6, this.highKillStreak);
			qCreate.setDate(7, this.lastLootDate);
			qCreate.setInt(8, this.totalLoot);
			qCreate.setInt(9, this.hourLoot);
			qCreate.setString(10, Utils.getGson().toJson(unlockKits));
			qCreate.setString(11, ""+this.kitEquiped);
			
			qCreate.setString(12, this.playerUUID.toString());
			// ADD Capture Zone Date
			qCreate.execute();
			qCreate.close();
			Bukkit.getLogger().log(Level.INFO, "[Xelephia] Sauvegarde du joueur : " + this.lastPlayerName + " !");
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/*
	 * isOnline
	 */
	public boolean isOnline() {
		if (Bukkit.getOfflinePlayer(playerUUID).isOnline())
			return true;
		else
			return false;
	}

	/*
	 * getPlayer
	 */
	public Player getBukkitPlayer() {
		if (this.isOnline())
			return Bukkit.getPlayer(playerUUID);
		else
			return null;
	}

	/*
	 * getOfflinePlayer
	 */
	public OfflinePlayer getOfflinePlayer() {
		return Bukkit.getOfflinePlayer(playerUUID);
	}

	/*
	 * getName
	 */
	public String getName() {
		return lastPlayerName;
	}

	/*
	 * getUUID
	 */
	public UUID getUUID() {
		return playerUUID;
	}

	/*
	 * getCoins
	 */
	public double getCoins() {
		String coinsStr = df.format(coins);
		
		return Double.parseDouble(coinsStr);
	}

	public boolean kitEquiped() {
		return kitEquiped;
	}
	
	public void setCoins(double coins) {
		if (coins < 0)
			coins = 0;
		this.coins = coins;
	}
	
	public void setKitEquiped(boolean status) {
		kitEquiped = status;
	}

	public void takeCoins(double amount) {
		this.coins -= amount;
		if (this.coins < 0)
			coins = 0;
	}

	public void giveCoins(double amount) {
		this.coins += amount;
	}

	public boolean inCombat() {
		return inCombat;
	}
	
	public void combat() {
		if(inCombat != true) {
			inCombat = true;
			
			sendMessage(MessageType.SUBTITLE,"§4「§c✗§4」§c Vous êtes en combat ! (20 secs)");
			combatTask = new BukkitRunnable() {
		        
				int sec = 20;
				
			    @Override
			    public void run() {
			       if (sec > 0 && combatRelaunch == false && inCombat == true) {
			           sec--;
			       }else if(sec > 0 && combatRelaunch == true && inCombat == true) {
			    	   sec = 20;
			    	   combatRelaunch = false;
			       }else {
			    	   this.cancel();
			    	   inCombat = false;
			    	   sendMessage(MessageType.SUBTITLE,"§7「§a✓§7」§a Vous n'êtes plus en combat.");
			       }
			    }
			}.runTaskTimerAsynchronously(XelephiaPlugin.getInstance(), 0, 20);
		}else {
			combatRelaunch = true;
		}
	}
	
	/*
	 * getKillCount
	 */
	public int getKillCount() {
		return killCount;
	}

	/*
	 * getDeathCount
	 */
	public int getDeathCount() {
		return deathCount;
	}

	/*
	 * getKillStreak
	 */
	public int getKillStreak() {
		return actualKillStreak;
	}

	/*
	 * getHighKillStreak
	 */
	public int getHighKillStreak() {
		return highKillStreak;
	}

	/*
	 * getUnlockKits
	 */
	public ArrayList<String> getUnlockKit() {
		return unlockKits;
	}

	/*
	 * getTotalLoot
	 */
	public int getTotalLoot() {
		return totalLoot;
	}

	/*
	 * getHourLoot
	 */
	public int getHourLoot() {
		return hourLoot;
	}

	/*
	 * getLastLootDate
	 */
	public Date getLastLootDate() {
		return lastLootDate;
	}

	/*
	 * getRatio
	 */
	public double getRatio() {
		if (deathCount == 0)
			return killCount;
		else {
			return Double.parseDouble(df.format((double) killCount / deathCount));
		}
	}
	
	public  void sendMessage(MessageType type, String message) {
		PacketPlayOutTitle times = new PacketPlayOutTitle(10,15,10);
		if(type.equals(MessageType.SUBTITLE)) {
			PacketPlayOutTitle subTitle = new PacketPlayOutTitle(EnumTitleAction.SUBTITLE, ChatSerializer.a("{\"text\":\"" + message.replace("&", "§") + "\"}"));
			PacketPlayOutTitle title = new PacketPlayOutTitle(EnumTitleAction.TITLE, ChatSerializer.a("{\"text\":\" \"}"));
			((CraftPlayer) getBukkitPlayer()).getHandle().playerConnection.sendPacket(title);
			((CraftPlayer) getBukkitPlayer()).getHandle().playerConnection.sendPacket(subTitle);
			((CraftPlayer) getBukkitPlayer()).getHandle().playerConnection.sendPacket(times);
		}else if(type.equals(MessageType.ACTIONBAR)) {
			PacketPlayOutChat packet = new PacketPlayOutChat(ChatSerializer.a("{\"text\":\"" + message.replace("&", "§") + "\"}"), (byte) 2);
			((CraftPlayer) getBukkitPlayer()).getHandle().playerConnection.sendPacket(packet);
			((CraftPlayer) getBukkitPlayer()).getHandle().playerConnection.sendPacket(times);
		}
		
	}

	/*
	 * open Stats Menu
	 */
	public void openStats(Player p) {
		for (int i = 0; i < 27; i++) {
			menuStats.setItem(i,
					Utils.createItemStack(" ", Material.STAINED_GLASS_PANE, 1, new ArrayList<String>(), 11, false));
		}
		for (int i = 11; i < 16; i++) {
			menuStats.setItem(i,
					Utils.createItemStack(" ", Material.STAINED_GLASS_PANE, 1, new ArrayList<String>(), 7, false));
		}

		// Head
		ArrayList<String> loreHead = new ArrayList<String>();
		loreHead.add("§8Grade : " + "§aindéfini");
		loreHead.add("§8Coins : §e" + this.coins + " §l¢");
		menuStats.setItem(4, Utils.createItemSkull("§b" + this.lastPlayerName, loreHead, SkullType.PLAYER,
				this.lastPlayerName, false));

		// Remaining loot or recharge time
		ArrayList<String> loreGold = new ArrayList<String>();
		loreGold.add("§8Rechargé dans §a" + "indéfini");
		menuStats.setItem(10, Utils.createItemStack("§bLoot restant : §a" + (5 - this.hourLoot), Material.GOLD_INGOT, 1,
				loreGold, 0, false));

		// All loot
		menuStats.setItem(16, Utils.createItemStack("§bLoot total : §a" + this.totalLoot, Material.DIAMOND, 1,
				new ArrayList<String>(), 0, false));

		// KillStreak
		ArrayList<String> loreGSword = new ArrayList<String>();
		loreGSword.add(
				"§8Record battu dans §a" + (highKillStreak - actualKillStreak) + " §8(§c" + highKillStreak + "§8)");
		menuStats.setItem(20, Utils.createItemStack("§bKillStreak : §a" + this.actualKillStreak, Material.GOLD_SWORD, 1,
				loreGSword, 0, false));

		// Death and Ratio
		ArrayList<String> loreSkeleton = new ArrayList<String>();
		loreSkeleton.add("§8Ratio (§ckills§8/§cmorts§8) : §a" + this.getRatio());
		menuStats.setItem(22, Utils.createItemSkull("§bMorts total : §a" + this.deathCount, loreSkeleton,
				SkullType.SKELETON, p.getName(), false));

		// Kill
		menuStats.setItem(24, Utils.createItemStack("§bKills total : §a" + this.killCount, Material.DIAMOND_SWORD, 1,
				new ArrayList<String>(), 0, false));

		p.openInventory(menuStats);
	}

	/*
	 * getLastMessenger
	 */
	public XPlayer getLastMessenger() {
		return lastMessenger;
	}

	/*
	 * setLastMessenger
	 */
	public void setLastMessenger(XPlayer lastMessenger) {
		this.lastMessenger = lastMessenger;
	}

}
