package eu.octanne.xelephia;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import eu.octanne.xelephia.commands.BroadcastCommand;
import eu.octanne.xelephia.commands.CoinsCommand;
import eu.octanne.xelephia.commands.DayCommand;
import eu.octanne.xelephia.commands.DiscordCommand;
import eu.octanne.xelephia.commands.FlyCommand;
import eu.octanne.xelephia.commands.GameModeCommand;
import eu.octanne.xelephia.commands.HealCommand;
import eu.octanne.xelephia.commands.MessageCommand;
import eu.octanne.xelephia.commands.NightCommand;
import eu.octanne.xelephia.commands.RenameCommand;
import eu.octanne.xelephia.commands.ResetPlayerCommand;
import eu.octanne.xelephia.commands.SetSpawnCommand;
import eu.octanne.xelephia.commands.SiteCommand;
import eu.octanne.xelephia.commands.SpawnCommand;
import eu.octanne.xelephia.commands.SpeedCommand;
import eu.octanne.xelephia.commands.StaffCommand;
import eu.octanne.xelephia.commands.StatsCommand;
import eu.octanne.xelephia.commands.SunCommand;
import eu.octanne.xelephia.commands.TPACommand;
import eu.octanne.xelephia.commands.TpAllCommand;
import eu.octanne.xelephia.grade.GradeCommand;
import eu.octanne.xelephia.grade.GradeManager;
import eu.octanne.xelephia.kit.KitCommand;
import eu.octanne.xelephia.kit.KitSystem;
import eu.octanne.xelephia.lootzone.LootCommand;
import eu.octanne.xelephia.lootzone.LootZoneManager;
import eu.octanne.xelephia.sql.DataBase;
import eu.octanne.xelephia.util.ConfigYaml;
import eu.octanne.xelephia.warp.SetWarpCommand;
import eu.octanne.xelephia.warp.WarpCommand;
import eu.octanne.xelephia.warp.WarpManager;
import eu.octanne.xelephia.world.VoidChunkGenerator;
import eu.octanne.xelephia.world.WorldCommand;
import eu.octanne.xelephia.world.WorldManager;
import eu.octanne.xelephia.xplayer.PScoreboard;
import eu.octanne.xelephia.xplayer.XPlayer;
import eu.octanne.xelephia.xplayer.XPlayerListener;

public class XelephiaPlugin extends JavaPlugin {
	
	// PLAYERS
	static private DataBase dbPlayers;
	static public Collection<XPlayer> xplayersOnline = new ArrayList<XPlayer>();

	// CONFIG FILE
	private static ConfigYaml messageConfig;
	private static ConfigYaml mainConfig;

	// TPA REQUEST
	static public HashMap<Player, Player> requestTPA = new HashMap<Player, Player>();

	// MANAGER
	private static WarpManager warpManager;
	private static WorldManager worldManager;
	private static KitSystem kitSystem;
	private static LootZoneManager lootZoneManager;
	private static GradeManager gradeManager;

	@Override
	public void onLoad() {

	}
	
	@Override
	public void onEnable() {
		// Players DB
		dbPlayers = new DataBase("players");
		dbPlayers.connect();
		createTable();
		
		// World
		worldManager = new WorldManager();
		
		/*
		 * Config
		 */
		mainConfig = new ConfigYaml("config.yml"); // Main
		loadMainConfig();
		messageConfig = new ConfigYaml("message.yml"); // Message
		loadMessage();
		
		// Manager
		warpManager = new WarpManager();
		kitSystem = new KitSystem();
		lootZoneManager = new LootZoneManager();
		gradeManager = new GradeManager();

		// Load Online Players
		for (Player p : Bukkit.getOnlinePlayers()) {
			XPlayer xp = XelephiaPlugin.getXPlayer(p.getUniqueId());
			xp.getGrade().applyPermissions(xp);
			xp.getGrade().applyTag(xp);
			XelephiaPlugin.xplayersOnline.add(xp);
			// TABLIST
			xp.setFooterAndHeader(XelephiaPlugin.getMainConfig().get().getString("tabList.header"), 
				XelephiaPlugin.getMainConfig().get().getString("tabList.footer"));
		}

		// ScoreBoard
		PScoreboard.startTask();
		
		// Register Command
		loadCommand();
		
		// Listener
		Bukkit.getPluginManager().registerEvents(new XPlayerListener(), this);
	}

	@Override
	public void onDisable() {
		// Save Player Online
		for (XPlayer xp : xplayersOnline) {
			xp.saveIntoDB();
		}
		
		// Players DB
		dbPlayers.disconnect();
		worldManager.save();
		
	}

	@Override
	public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
		if (id.equalsIgnoreCase("VOID")) {
			return new VoidChunkGenerator(true);
		}
		if(getWorldManager().getWorld(worldName).getType().needGenerator()) {
			if(getWorldManager().getWorld(worldName).getType().getName().equalsIgnoreCase("void")) {
				return new VoidChunkGenerator(true);
			}
		}
		return new VoidChunkGenerator(false);
	}

	// Plugin Instance
	static public Plugin getInstance() {
		return Bukkit.getPluginManager().getPlugin("Xelephia");
	}
	
	// Players DB
	static public DataBase getPlayersDB() {
		return dbPlayers;
	}

	// Online XPlayers 
	static public final Collection<XPlayer> getXPlayersOnline() {
		return xplayersOnline;
	}

	/*
	 * CONFIG
	 */
	static public ConfigYaml getMessageConfig() {
		return messageConfig;
	}

	static public ConfigYaml getMainConfig() {
		return mainConfig;
	}

	/*
	 * GET XPlayer
	 */
	static public XPlayer getXPlayer(UUID pUUID) {
		for (XPlayer xp : getXPlayersOnline()) {
			if (xp.getUUID().equals(pUUID))
				return xp;
		}
		if (Bukkit.getOfflinePlayer(pUUID) != null)
			return new XPlayer(pUUID);
		else
			return null;
	}
	static public XPlayer getXPlayer(String pName) {
		for (XPlayer xp : getXPlayersOnline()) {
			if (xp.getName().equals(pName))
				return xp;
		}
		try {
			PreparedStatement q = XelephiaPlugin.getPlayersDB().getConnection()
					.prepareStatement("SELECT uuid FROM players WHERE playerName=?");
			q.setString(1, pName);
			ResultSet rs = q.executeQuery();
			boolean isExist = rs.next();
			if (!isExist)
				return null;
			String strUUID = rs.getString("uuid");
			q.close();
			return getXPlayer(UUID.fromString(strUUID));
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	private void createTable() {
		dbPlayers.createTable("CREATE TABLE IF NOT EXISTS players (\n" + "    uuid txt NOT NULL,\n"
				+ "    playerName txt NOT NULL,\n" 
				+ "    grade txt NOT NULL,\n" 
				+ "    coins REAL NOT NULL,\n"
				+ "    killCount integer NOT NULL,\n" 
				+ "    deathCount integer NOT NULL,\n"
				+ "    actualKillStreak integer NOT NULL,\n" 
				+ "    highKillStreak integer NOT NULL\n,"
				+ "    untilLootDate txt,\n" 
				+ "    totalLoot integer NOT NULL,\n" 
				+ "    hourLoot integer NOT NULL,\n"
				+ "    unlockKits txt NOT NULL,\n" 
				+ "    kitEquiped txt NOT NULL\n" + 
				");");
	}

	/*
	 * Manager
	 */
	static public WarpManager getWarpManager() {
		return warpManager;
	}
	static public WorldManager getWorldManager() {
		return worldManager;
	}
	static public KitSystem getKitSystem() {
		return kitSystem;
	}
	static public LootZoneManager getLootZoneManager() {
		return lootZoneManager;
	}
	static public GradeManager getGradeManager() {
		return gradeManager;
	}
	
	public void loadCommand() {
		getCommand("fly").setExecutor(new FlyCommand());
		getCommand("speed").setExecutor(new SpeedCommand());
		getCommand("broadcast").setExecutor(new BroadcastCommand());
		getCommand("gamemode").setExecutor(new GameModeCommand());
		getCommand("discord").setExecutor(new DiscordCommand());
		getCommand("day").setExecutor(new DayCommand());
		getCommand("heal").setExecutor(new HealCommand());
		getCommand("night").setExecutor(new NightCommand());
		getCommand("sun").setExecutor(new SunCommand());
		getCommand("site").setExecutor(new SiteCommand());
		getCommand("tpall").setExecutor(new TpAllCommand());
		getCommand("rename").setExecutor(new RenameCommand());
		getCommand("setspawn").setExecutor(new SetSpawnCommand());
		getCommand("spawn").setExecutor(new SpawnCommand());
		getCommand("staff").setExecutor(new StaffCommand());
		getCommand("warp").setExecutor(new WarpCommand());
		getCommand("setwarp").setExecutor(new SetWarpCommand());
		getCommand("world").setExecutor(new WorldCommand());
		getCommand("tpa").setExecutor(new TPACommand());
		getCommand("tpyes").setExecutor(new TPACommand());
		getCommand("tpno").setExecutor(new TPACommand());
		getCommand("message").setExecutor(new MessageCommand());
		getCommand("respond").setExecutor(new MessageCommand());
		getCommand("stats").setExecutor(new StatsCommand());
		getCommand("kit").setExecutor(new KitCommand());
		getCommand("coins").setExecutor(new CoinsCommand());
		getCommand("loot").setExecutor(new LootCommand());
		getCommand("resetplayer").setExecutor(new ResetPlayerCommand());
		getCommand("setgrade").setExecutor(new GradeCommand());
	}

	/*
	 * Load Config
	 */
	public void loadMainConfig() {
		if(!mainConfig.get().isSet("maxLootPerHour"))mainConfig.set("maxLootPerHour", 5);
		if(!mainConfig.get().isSet("coucheDelSelector"))mainConfig.set("coucheDelSelector", 150);
		if(!mainConfig.get().isSet("untilAppleTimeSec"))mainConfig.set("untilAppleTimeSec", 6);
		if(!mainConfig.get().isSet("untilLootTimeHour"))mainConfig.set("untilLootTimeHour", 1);
		if(!mainConfig.get().isSet("maxLengthTag"))mainConfig.set("maxLengthTag", 30);
		if(!mainConfig.get().isSet("scoreboard.name"))mainConfig.set("scoreboard.name", "Xelephia | PVP/Box");
		if(!mainConfig.get().isSet("scoreboard.lines"))mainConfig.set("scoreboard.lines", new ArrayList<>());
		if(!mainConfig.get().isSet("chatFormat"))mainConfig.set("chatFormat", "{PREFIX} {PLAYERNAME} §7≫§r {MESSAGE}");
		if(!mainConfig.get().isSet("tabList.header"))mainConfig.set("tabList.header", "      §9▶ §6§lXelephia §bv§30.8 §a- §3PvP§8/§cBox §aRéinventé §9◀");
		if(!mainConfig.get().isSet("tabList.footer"))mainConfig.set("tabList.footer", "§bJoueurs : §7{ONLINE}§8/§7{MAX}\\n§3Bon jeu §b{PLAYERNAME} §3! §9| §3IP : §bplay.xelephia.fr");
		mainConfig.save();
	}
	public void loadMessage() {
		if (!messageConfig.getFile().exists()) {
			messageConfig.set("joinPlayer",
					ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + "+" + ChatColor.DARK_GRAY + "] {PLAYER}");
			messageConfig.set("quitPlayer",
					ChatColor.DARK_GRAY + "[" + ChatColor.DARK_RED + "-" + ChatColor.DARK_GRAY + "] {PLAYER}");
			messageConfig.set("FLY_DISABLE_PLAYER_ME", "§4Ton Fly a été désactivé !");
			messageConfig.set("FLY_ENABLE_PLAYER_ME", "§aTon Fly a été activé !");
			messageConfig.set("FLY_DISABLE_PLAYER_YOU", "§4Le Fly du Joueur {PLAYER} a été désactivé !");
			messageConfig.set("FLY_ENABLE_PLAYER_YOU", "§aLe Fly du Joueur {PLAYER} a été activé !");
			messageConfig.set("incorrectNumber", "§4Le nombre {NUMBER} est incorrect !");
			messageConfig.set("changeMeSpeed", "§aTa vitesse a été mise à jour ({SPEED}) !");
			messageConfig.set("changeYourSpeed", "§aLa vitesse du Joueur {PLAYER} a été mise à jour ({SPEED}) !");
			messageConfig.set("incorrectPlayer", "§4Le Joueur {PLAYER} est incorrect !");
			messageConfig.set("dayCommand", "§eLes dieux ont entendu ta demande, le soleil se lève...");
			messageConfig.set("discordCommand", "§aLe Discord : https://discord.gg/4EpE4ux !");
			messageConfig.set("healOtherCommand", "§eLa vie et la nourriture du joueur {PLAYER} ont été régénérées !");
			messageConfig.set("healMeCommand", "§eTa vie et ta nourriture ont été régénérées !");
			messageConfig.set("nightCommand", "§eLes dieux ont entendu ta demande, le soleil se couche...");
			messageConfig.set("siteCommand", "§aLe Site : https://www.google.com !");
			messageConfig.set("sunCommand", "§eLes dieux ont entendu ta demande, le soleil se lève...");
			messageConfig.set("tpallPlayerTeleport", "§aLe Joueur {PLAYER} t'a téléporté pour {REASON}");
			messageConfig.set("tpallPlayerExecutor", "§aTout les Joueurs ont été téléportés pour {REASON}");
			messageConfig.set("incorrectGameMode", "§4Le Gamemode précisé {GAMEMODE} est incorrect !");
			messageConfig.set("changeMeGameMode", "§aTon Gamemode a été mis a jour : {GAMEMODE}");
			messageConfig.set("changeOtherPlayerGameMode", "§aLe Gamemode de {PLAYER} a été mis à jour : {GAMEMODE}");
			messageConfig.set("spawnTeleport", "§aTéléportation au Spawn...");
			messageConfig.set("spawnPlayerTeleport", "§aLe Joueur {PLAYER} a été téléporté au spawn !");
			messageConfig.set("spawnPreTeleport", "§8Téléportation au spawn dans 5 secondes...");
			messageConfig.set("CancelTeleport", "§4Tu as bougé, téléportation annulée !");
			messageConfig.set("warpPreTeleport", "§8Téléportation au warp {WARP}§8 dans 5 secondes...");
			messageConfig.set("warpTeleport", "§aTu as été téléporté au warp {WARP} §a!");
			messageConfig.set("inexistantWarp", "§4Le warp {WARP} n'existe pas !");
			messageConfig.set("playerWarpTp", "§aLe Joueur {PLAYER} a été téléporté au warp {WARP} §a!");
			messageConfig.set("createWorld", "§aLe Monde {WORLD} du type {TYPE} et d'environnement {ENV} a été créé !");
			messageConfig.set("teleportPlayerInWorld", "§aLe Joueur {PLAYER} a été téléporté dans le Monde {WORLD} !");
			messageConfig.set("inexistantOrNoLoadWorld", "§4Le Monde {WORLD} n'existe pas ou n'a pas été chargé !");
			messageConfig.set("unloadWorld", "§aLe Monde {WORLD} a été déchargé !");
			messageConfig.set("loadWorld", "§aLe Monde {WORLD} a été chargé !");
			messageConfig.set("inexistantOrAlwaysLoadWorld", "§4Le monde {WORLD} n'existe pas ou est déjà chargé !");
			messageConfig.set("acceptTeleportRequest",
					"§aVous avez accepté la demande de téléportation de §c{PLAYER}§a.");
			messageConfig.set("tpaPreTeleport", "§7Téléportation dans 5 secondes...");
			messageConfig.set("tpaTeleport", "§7Téléportation à §c{PLAYER}§7...");
			messageConfig.set("noTeleportRequestFound", "§4Vous n'avez aucune demande en cours !");
			messageConfig.set("denyYouTeleportRequest", "§4{PLAYER} a refusé votre demande de téléportation.");
			messageConfig.set("denyTeleportRequest",
					"§4Vous venez de refuser la demande de téléportation de {PLAYER}.");
			messageConfig.set("Chat.FORMAT_STAFF", "{GRADE} §e|§r {RANK} {PLAYER} §c>> {MESSAGE}");
			messageConfig.set("Chat.FORMAT_PLAYER", "{GRADE} §e|§r {RANK} {PLAYER} §8>> {MESSAGE}");
			messageConfig.set("staffCriticErrorNotice",
					"§cUne erreur s'est produite, le staff a été prévenu. Merci de patienter.");
			messageConfig.set("noPermission", "§cErreur §8| §cCommande reservée à l'administration.");
			messageConfig.set("playerOnly", "Commande disponible uniquement en jeu.");
			messageConfig.save();
		}
	}

}
