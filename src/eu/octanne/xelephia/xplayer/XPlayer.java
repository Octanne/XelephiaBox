package eu.octanne.xelephia.xplayer;

import java.lang.reflect.Field;
import java.math.RoundingMode;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
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
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.google.gson.reflect.TypeToken;

import eu.octanne.xelephia.XelephiaPlugin;
import eu.octanne.xelephia.grade.Grade;
import eu.octanne.xelephia.lootzone.LootZoneManager;
import eu.octanne.xelephia.util.Utils;
import net.minecraft.server.v1_8_R3.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerListHeaderFooter;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle.EnumTitleAction;

public class XPlayer {

	// Until Time CONST
	static private final int untilAppleTimeSec = XelephiaPlugin.getMainConfig().get().getInt("untilAppleTimeSec",6);
	static private final int untilLootTimeHour = XelephiaPlugin.getMainConfig().get().getInt("untilLootTimeHour",1);

	// Decimal Format
	DecimalFormat df = new DecimalFormat("#.##");
	
	public enum MessageType {
		ACTIONBAR,
		SUBTITLE
	}

	public PermissionAttachment perms = null;
	
	protected PScoreboard scoreboard = null;
	
	protected BukkitTask combatTask;
	protected boolean inCombat = false, decoInCombat = false;
	private boolean combatRelaunch = false;
	protected String lastDamagerName;

	protected Grade grade;

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

	protected boolean kitEquiped;

	protected int totalLoot;
	protected int hourLoot;

	// Last Loot date is in reality the date when hourLoot will be reset
	protected Calendar untilLootDate;

	protected ArrayList<String> unlockKits;

	// Damage System
	protected HashMap<String, Double> damageTaken = new HashMap<String, Double>();
	protected double totalDamage = 0;
	
	protected Calendar untilAppleDate;

	protected XPlayer lastMessenger;

	public void finalize() throws Throwable {
		saveIntoDB();
		Bukkit.getLogger().info("Libération mémoire XPlayer : "+lastPlayerName);
		untilAppleDate = null;
		damageTaken = null;
		menuStats = null;
		combatTask = null;
		scoreboard = null;
	}

	public XPlayer(UUID pUUID) {

		// Decimal Format
		df.setRoundingMode(RoundingMode.HALF_EVEN);
		DecimalFormatSymbols dfs = df.getDecimalFormatSymbols();
		dfs.setDecimalSeparator('.');
		df.setDecimalFormatSymbols(dfs);

		this.playerUUID = pUUID;

		/*
		 * DATABASE QUERRY OR CREATE
		 */
		try {
			PreparedStatement q = XelephiaPlugin.getPlayersDB().getConnection()
					.prepareStatement("SELECT playerName, uuid, coins, killCount, deathCount, actualKillStreak, "
							+ "highKillStreak, lastLootDate, totalLoot, hourLoot, unlockKits, kitEquiped, grade FROM players WHERE uuid=?");
			q.setString(1, pUUID.toString());
			ResultSet rs = q.executeQuery();
			boolean isExist = rs.next();
			if (isExist) {
				this.lastPlayerName = Bukkit.getPlayer(pUUID) != null ? Bukkit.getPlayer(pUUID).getName() : rs.getString("playerName");
				this.playerUUID = UUID.fromString(rs.getString("uuid"));
				this.coins = rs.getInt("coins");
				this.killCount = rs.getInt("killCount");
				this.deathCount = rs.getInt("deathCount");
				this.actualKillStreak = rs.getInt("actualKillStreak");
				this.highKillStreak = rs.getInt("highKillStreak");
				this.hourLoot = rs.getInt("hourLoot");
				this.totalLoot = rs.getInt("totalLoot");
				this.untilLootDate = Utils.getGson().fromJson(rs.getString("untilLootDate"),
						new TypeToken<Calendar>() {
				}.getType());
				this.unlockKits = Utils.getGson().fromJson(rs.getString("unlockKits"),
						new TypeToken<ArrayList<String>>() {
				}.getType());
				if(rs.getString("kitEquiped").equalsIgnoreCase("true")) this.kitEquiped = true;
				else this.kitEquiped = false;
				this.grade = XelephiaPlugin.getGradeManager().getGrade(rs.getString("grade"));

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
				this.lastPlayerName = Bukkit.getPlayer(pUUID).getName();
				this.grade = XelephiaPlugin.getGradeManager().getDefault();

				PreparedStatement qCreate = XelephiaPlugin.getPlayersDB().getConnection()
						.prepareStatement("INSERT INTO players (playerName, uuid, coins, killCount, deathCount, "
								+ "actualKillStreak, highKillStreak, untilLootDate, totalLoot, hourLoot, unlockKits, kitEquiped, grade) "
								+ "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)");
				qCreate.setString(1, this.lastPlayerName);
				qCreate.setString(2, this.playerUUID.toString());
				qCreate.setDouble(3, this.coins);
				qCreate.setInt(4, this.killCount);
				qCreate.setInt(5, this.deathCount);
				qCreate.setInt(6, this.actualKillStreak);
				qCreate.setInt(7, this.highKillStreak);
				qCreate.setString(8, Utils.getGson().toJson(this.untilLootDate));
				qCreate.setInt(9, this.totalLoot);
				qCreate.setInt(10, this.hourLoot);
				qCreate.setString(11, Utils.getGson().toJson(unlockKits));
				qCreate.setString(12, ""+this.kitEquiped);
				qCreate.setString(13, grade.getName());


				qCreate.execute();
				qCreate.close();
				Bukkit.getLogger().log(Level.INFO, "[Xelephia] Création du joueur : " + this.lastPlayerName + " !");
			}
			q.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		this.menuStats = Bukkit.createInventory(null, 27, "§8Statistiques de §b" + this.lastPlayerName);
	}
	
	/*
	 * saveIntoDB
	 */
	public boolean saveIntoDB() {
		try {
			PreparedStatement qCreate = XelephiaPlugin.getPlayersDB().getConnection()
					.prepareStatement("UPDATE players SET playerName = ?, coins = ?, killCount = ?, "
							+ "deathCount = ?, actualKillStreak = ?, highKillStreak = ?, untilLootDate = ?, "
							+ "totalLoot = ?, hourLoot = ?, unlockKits = ?, kitEquiped = ?, grade = ? WHERE uuid = ?");
			qCreate.setString(1, this.lastPlayerName);
			qCreate.setDouble(2, this.coins);
			qCreate.setInt(3, this.killCount);
			qCreate.setInt(4, this.deathCount);
			qCreate.setInt(5, this.actualKillStreak);
			qCreate.setInt(6, this.highKillStreak);
			qCreate.setString(7, Utils.getGson().toJson(this.untilLootDate));
			qCreate.setInt(8, this.totalLoot);
			qCreate.setInt(9, this.hourLoot);
			qCreate.setString(10, Utils.getGson().toJson(unlockKits));
			qCreate.setString(11, ""+this.kitEquiped);
			qCreate.setString(12, this.grade.getName());

			qCreate.setString(13, this.playerUUID.toString());
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

	/**
	 * Load or Update
	 * the player's scoreboard
	 */
	public void loadScoreboard() {
		if(isOnline() && scoreboard != null) {
			scoreboard.update();
		}else if(isOnline()){
			scoreboard = new PScoreboard(this);
		}
	}
	
	public void setGrade(Grade grade) {
		this.grade = grade;
		if(isOnline())this.grade.applyPermissions(this);
		if(isOnline())this.grade.applyTag(this);
	}

	/*
	 * updateLoots & return true if update
	 */
	public boolean updateLoots() {
		if(Calendar.getInstance().after(untilLootDate)) {
			hourLoot = 0;
			untilLootDate = null;
			return true;
		}else return false;
	}

	public void updateUntilAppleDate() {
		this.untilAppleDate = Calendar.getInstance();
		this.untilAppleDate.add(Calendar.SECOND, untilAppleTimeSec);
	}

	public void updateUntilLootDate() {
		this.untilLootDate = Calendar.getInstance();
		this.untilLootDate.add(Calendar.HOUR_OF_DAY, untilLootTimeHour);
	}

	/*
	 * getTimeBeforeResetLoot
	 */
	public String getTimeBeforeResetLoot() {
		Calendar now = Calendar.getInstance();
		if(untilLootDate != null && now.before(untilLootDate)) {
			long millis = untilLootDate.getTimeInMillis()-now.getTimeInMillis();
			int sec = (int) (millis/1000);
			int min = sec/60;
			sec %= 60;
			return "§9Rechargé dans §c"+min+" §9min.§c "+sec+" §9sec.";
		}else {
			return "§9Entièrement chargé";
		}

	}
	/*
	 * getTimeUntilApple
	 */
	public int getTimeUntilApple() {
		Calendar now = Calendar.getInstance();
		if(untilAppleDate != null && now.before(untilAppleDate)) {
			long millis = untilAppleDate.getTimeInMillis()-now.getTimeInMillis();
			int sec = (int) (millis/1000);
			return sec;
		}else {
			return 0;
		}
	}

	/*
	 * getBPlayer
	 */
	public Player getBPlayer() {
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
	
	/*
	 * getGrade 
	 */
	public Grade getGrade() {
		return grade;
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
			}.runTaskTimer(XelephiaPlugin.getInstance(), 0, 20);
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
	 * incrementHourLoot
	 */
	public void incrementHourLoot() {
		hourLoot+=1;
	}
	/*
	 * incrementTotalLoot
	 */
	public void incrementTotalLoot() {
		totalLoot+=1;
	}

	/*
	 * getUntilLootDate
	 */
	public Calendar getUntilLootDate() {
		return untilLootDate;
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
	
	/*
	 * NMS Methods
	 */
	public void sendMessage(MessageType type, String message) {
		// NMS
		if(type.equals(MessageType.SUBTITLE)) {
			PacketPlayOutTitle subTitle = new PacketPlayOutTitle(EnumTitleAction.SUBTITLE, ChatSerializer.a("{\"text\":\"" + message.replace("&", "§") + "\"}"),10,15,10);
			PacketPlayOutTitle title = new PacketPlayOutTitle(EnumTitleAction.TITLE, ChatSerializer.a("{\"text\":\" \"}"),10,15,10);
			((CraftPlayer) getBPlayer()).getHandle().playerConnection.sendPacket(title);
			((CraftPlayer) getBPlayer()).getHandle().playerConnection.sendPacket(subTitle);
		}else if(type.equals(MessageType.ACTIONBAR)) {
			PacketPlayOutChat packet = new PacketPlayOutChat(ChatSerializer.a("{\"text\":\"" + message.replace("&", "§") + "\"}"), (byte) 2);
			((CraftPlayer) getBPlayer()).getHandle().playerConnection.sendPacket(packet);
		}

	}
	public void setFooterAndHeader(String header, String footer) {
		header = header.replace("{MAX}", Bukkit.getMaxPlayers()+"");
		header = header.replace("{ONLINE}", Bukkit.getOnlinePlayers().size()+"");
		header = header.replace("{PLAYERNAME}", getName());
		footer = footer.replace("{MAX}", Bukkit.getMaxPlayers()+"");
		footer = footer.replace("{ONLINE}", Bukkit.getOnlinePlayers().size()+"");
		footer = footer.replace("{PLAYERNAME}", getName());
		// NMS
		PacketPlayOutPlayerListHeaderFooter headerAndFooter = new PacketPlayOutPlayerListHeaderFooter(ChatSerializer.a("{\"text\":\"" + header.replace("&", "§") + "\"}"));
		try {
			Field footerField = headerAndFooter.getClass().getDeclaredField("b");
			footerField.setAccessible(true);
			footerField.set(headerAndFooter, ChatSerializer.a("{\"text\":\"" + footer.replace("&", "§") + "\"}"));
		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
			e.printStackTrace();
		}
		((CraftPlayer) getBPlayer()).getHandle().playerConnection.sendPacket(headerAndFooter);
	}

	/*
	 * Reset the Player
	 */
	public void resetPlayer() {
		this.actualKillStreak = 0;
		this.coins = 0;
		this.deathCount = 0;
		this.highKillStreak = 0;
		this.hourLoot = 0;
		this.killCount = 0;
		this.untilLootDate = null;
		this.totalLoot = 0;
		this.unlockKits = new ArrayList<>();
		saveIntoDB();
	}

	/*
	 * Stats Menu
	 */
	public void openStats(Player p) {
		this.updateLoots();
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
		loreHead.add("§9Grade : "+grade.getDisplayName());
		loreHead.add("§9Coins : §e" + getCoins() + " §l¢");
		menuStats.setItem(4, Utils.createItemSkull("§b" + this.lastPlayerName, loreHead, SkullType.PLAYER,
				this.lastPlayerName, false));

		// Remaining loot or recharge time
		ArrayList<String> loreGold = new ArrayList<String>();
		loreGold.add(getTimeBeforeResetLoot());
		menuStats.setItem(10, Utils.createItemStack("§bLoot restant : §a" + (LootZoneManager.maxLootPerHour - this.hourLoot), Material.GOLD_INGOT, 1,
				loreGold, 0, false));

		// All loot
		menuStats.setItem(16, Utils.createItemStack("§bLoot total : §a" + this.totalLoot, Material.DIAMOND, 1,
				new ArrayList<String>(), 0, false));

		// KillStreak
		ArrayList<String> loreGSword = new ArrayList<String>();
		loreGSword.add(
				"§9Record battu dans §a" + (highKillStreak - actualKillStreak) + " §9(§c" + highKillStreak + "§9)");
		menuStats.setItem(20, Utils.createItemStack("§bKillStreak : §a" + this.actualKillStreak, Material.GOLD_SWORD, 1,
				loreGSword, 0, false));

		// Death and Ratio
		ArrayList<String> loreSkeleton = new ArrayList<String>();
		loreSkeleton.add("§9Ratio (§ckills§9/§cmorts§9) : §a" + this.getRatio());
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

	public String getCombatStatut() {
		return inCombat == true ? "§cActif" : "§cInactif";
	}

}
