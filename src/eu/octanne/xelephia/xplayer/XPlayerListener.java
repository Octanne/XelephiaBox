package eu.octanne.xelephia.xplayer;

import java.text.DecimalFormat;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.WeatherType;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

import com.sk89q.worldguard.bukkit.RegionContainer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;

import java.util.Random;
import eu.octanne.xelephia.XelephiaPlugin;
import eu.octanne.xelephia.kit.KitSystem;
import eu.octanne.xelephia.util.Utils;
import eu.octanne.xelephia.xplayer.XPlayer.MessageType;

public class XPlayerListener implements Listener {

	DecimalFormat df = new DecimalFormat("#.##");

	static private int coucheDelSelector = XelephiaPlugin.getMainConfig().get().getInt("coucheDelSelector",150);

	/*
	 * PlayerJoinEvent
	 */
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		// LOAD XPLAYER
		XPlayer xp = XelephiaPlugin.getXPlayer(p.getUniqueId());
		XelephiaPlugin.xplayersOnline.add(xp);
		// SET WEATHER
		p.setPlayerWeather(WeatherType.CLEAR);
		// WORLD TP
		p.teleport((Location) XelephiaPlugin.getMainConfig().get().get("spawn", p.getWorld()
				.getSpawnLocation()));
		// SELECTOR KIT
		if(xp.kitEquiped == false && !e.getPlayer().getInventory().contains(KitSystem.selectorItem) && !e.getPlayer().isDead()) {
			e.getPlayer().getInventory().addItem(KitSystem.selectorItem);
		}
		// LOAD PERMISSION
		xp.getGrade().applyPermissions(xp);
		xp.getGrade().applyTag(xp);
		// JOIN MESSAGE
		e.setJoinMessage(XelephiaPlugin.getMessageConfig().get().getString("joinPlayer").replace("{PLAYER}", xp.getName()));
		// TABLIST
		for(XPlayer xpF : XelephiaPlugin.xplayersOnline) {
			xpF.setFooterAndHeader(XelephiaPlugin.getMainConfig().get().getString("tabList.header"), 
					XelephiaPlugin.getMainConfig().get().getString("tabList.footer"));
		}
		// SCOREBOARD
		xp.loadScoreboard();
	}

	/*
	 * PlayerKickEvent
	 */
	@EventHandler
	public void onPlayerKick(PlayerKickEvent e) {
		XPlayer xP = XelephiaPlugin.getXPlayer(e.getPlayer().getUniqueId());
		if (XelephiaPlugin.getXPlayersOnline().contains(xP)) {
			// DECO COMBAT
			if(xP.inCombat() && !xP.getBPlayer().isDead()) {
				xP.combatTask.cancel();
				xP.inCombat = false;
				xP.decoInCombat = true;
				Player killer = null;
				if(xP.lastDamagerName != null) killer = Bukkit.getPlayer(xP.lastDamagerName);
				if(killer != null) {
					e.getPlayer().setHealth(0.0);
				}else {
					xP.getBPlayer().setHealth(0.0);
				}
			}
			// SAVE XPLAYER
			xP.saveIntoDB();
			XelephiaPlugin.xplayersOnline.remove(xP);
		}
		// TABLIST
		for(XPlayer xpF : XelephiaPlugin.xplayersOnline) {
			String header = XelephiaPlugin.getMainConfig().get().getString("tabList.header");
			header = header.replace("{MAX}", Bukkit.getMaxPlayers()+"");
			header = header.replace("{ONLINE}", (Bukkit.getOnlinePlayers().size()-1)+"");
			header = header.replace("{PLAYERNAME}", xpF.getName());
			String footer = XelephiaPlugin.getMainConfig().get().getString("tabList.footer");
			footer = footer.replace("{MAX}", Bukkit.getMaxPlayers()+"");
			footer = footer.replace("{ONLINE}", (Bukkit.getOnlinePlayers().size()-1)+"");
			footer = footer.replace("{PLAYERNAME}", xpF.getName());
			xpF.setFooterAndHeader(header, footer);
		}
	}
	
	/*
	 * PlayerQuitEvent
	 */
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		XPlayer xP = XelephiaPlugin.getXPlayer(e.getPlayer().getUniqueId());
		if (XelephiaPlugin.getXPlayersOnline().contains(xP)) {
			// DECO COMBAT
			if(xP.inCombat() && !xP.getBPlayer().isDead()) {
				xP.combatTask.cancel();
				xP.inCombat = false;
				xP.decoInCombat = true;
				Player killer = null;
				if(xP.lastDamagerName != null) killer = Bukkit.getPlayer(xP.lastDamagerName);
				if(killer != null) {
					e.getPlayer().setHealth(0.0);
				}else {
					xP.getBPlayer().setHealth(0.0);
				}
			}
			// SAVE XPLAYER
			xP.saveIntoDB();
			XelephiaPlugin.xplayersOnline.remove(xP);
		}
		// TABLIST
		for(XPlayer xpF : XelephiaPlugin.xplayersOnline) {
			String header = XelephiaPlugin.getMainConfig().get().getString("tabList.header");
			header = header.replace("{MAX}", Bukkit.getMaxPlayers()+"");
			header = header.replace("{ONLINE}", (Bukkit.getOnlinePlayers().size()-1)+"");
			header = header.replace("{PLAYERNAME}", xpF.getName());
			String footer = XelephiaPlugin.getMainConfig().get().getString("tabList.footer");
			footer = footer.replace("{MAX}", Bukkit.getMaxPlayers()+"");
			footer = footer.replace("{ONLINE}", (Bukkit.getOnlinePlayers().size()-1)+"");
			footer = footer.replace("{PLAYERNAME}", xpF.getName());
			xpF.setFooterAndHeader(header, footer);
		}
		// QUIT MESSAGE
		e.setQuitMessage(XelephiaPlugin.getMessageConfig().get().getString("quitPlayer").replace("{PLAYER}", e.getPlayer().getName()));
	}

	/*
	 * Custom Chat
	 */
	@EventHandler
	public void onSendMessage(AsyncPlayerChatEvent e) {
		XPlayer xp = XelephiaPlugin.getXPlayer(e.getPlayer().getUniqueId());
		String format = XelephiaPlugin.getMainConfig().get().getString("chatFormat");
		String message = e.getMessage();
		if(e.getPlayer().hasPermission("xelephia.chatcolor"))message = message.replace("&", "§");
		format = format.replace("{PREFIX}", xp.getGrade().getPrefix());
		format = format.replace("{PLAYERNAME}", "%1$s");
		format = format.replace("{MESSAGE}", "%2$s");
		e.setMessage(message);
		e.setFormat(format);
	}

	/*
	 * WorldGuard Fix Food
	 */
	@EventHandler
	public void onPlayerLooseFood(FoodLevelChangeEvent e) {
		Player p = (Player)e.getEntity();
		if(Bukkit.getPluginManager().isPluginEnabled("WorldGuard")) {
			try {
				Location loc = p.getLocation();
				RegionContainer container = WorldGuardPlugin.inst().getRegionContainer();
				RegionManager regions = container.get(loc.getWorld());
				ApplicableRegionSet set = regions.getApplicableRegions(loc);
				if(set != null) {
					Integer min_food = set.queryValue(null, DefaultFlag.MIN_FOOD);
					if(min_food != null && e.getFoodLevel() < min_food) {
						p.setFoodLevel(min_food);
						e.setCancelled(true);
					}
					Integer max_food = set.queryValue(null, DefaultFlag.MAX_FOOD);
					if(max_food != null && e.getFoodLevel() > max_food) {
						p.setFoodLevel(max_food);
						e.setCancelled(true);
					}
					Integer feed_amount = set.queryValue(null, DefaultFlag.FEED_AMOUNT);
					if(feed_amount != null) {
						p.setFoodLevel(feed_amount);
						e.setCancelled(true);
					}
				}
			} catch (IllegalArgumentException | SecurityException e1) {
				e1.printStackTrace();
			}
		}
	}

	/*
	 * PlayerMoveEvent
	 */
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		// KitSelector Remover
		if(e.getTo().getY() < coucheDelSelector && p.getInventory().first(KitSystem.selectorItem) != -1) {
			p.getInventory().clear(p.getInventory().first(KitSystem.selectorItem));
		}
	}


	/*
	 * Arrow Remover
	 */
	@EventHandler
	public void onArrowHit(ProjectileHitEvent e) {
		if(e.getEntity() instanceof Arrow) {
			Arrow arrow = (Arrow) e.getEntity();
			arrow.remove();
		}
	}

	/*
	 * InventoryClickEvent
	 */
	@EventHandler
	public void onInvClick(InventoryClickEvent e) {
		if(e.getWhoClicked() instanceof Player) {
			// Fix Items in stats menu
			if (e.getClickedInventory() != null && e.getClickedInventory().getName().contains("§8Statistiques de §b")) e.setCancelled(true);
			if(e.getView().getTopInventory() != null && e.getView().getTopInventory().getType().equals(InventoryType.ENDER_CHEST)) {
				if(e.getCurrentItem().hasItemMeta() && e.getCurrentItem().getItemMeta().getLore().contains("§cItem de kit") && e.getRawSlot() > 26) {
					e.setCancelled(true);
				}else if(e.getClick().equals(ClickType.NUMBER_KEY)) {
					ItemStack item = e.getView().getBottomInventory().getItem(e.getHotbarButton());
					if(item.hasItemMeta() && item.getItemMeta().getLore().contains("§cItem de kit")) {
						e.setCancelled(true);
					}
				}
			}
		}
	}

	/*
	 * InteractItemEvent
	 */
	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		// Open Kit Menu
		if(e.getItem() != null && e.getItem().equals(KitSystem.selectorItem)) XelephiaPlugin.getKitSystem().openMenu(e.getPlayer());
	}

	/*
	 * ItemConsumeEvent
	 */
	@EventHandler
	public void onConsume(PlayerItemConsumeEvent e) {
		// G-Apple Cooldown
		if(e.getItem().getType().equals(Material.GOLDEN_APPLE)) {
			XPlayer xP = XelephiaPlugin.getXPlayer(e.getPlayer().getUniqueId());
			int sec = xP.getTimeUntilApple();
			if(sec <= 0) {
				xP.updateUntilAppleDate();
				return;
			}else {
				xP.sendMessage(MessageType.SUBTITLE, "§9"+sec+" §csec avant la prochaine §6G-Apple §c!");
				e.setCancelled(true);
			}
		}else return;
	}

	/*
	 * DropEvent
	 */
	@EventHandler
	public void onDropItem(PlayerDropItemEvent e) {
		// Anti Drop KitSelector
		if(e.getItemDrop().getItemStack().isSimilar(KitSystem.selectorItem)) {
			e.setCancelled(true);
		}
	}

	/*
	 * RespawnEvent
	 */
	@EventHandler
	public void onRespawn(PlayerRespawnEvent e) {
		XPlayer xP = XelephiaPlugin.getXPlayer(e.getPlayer().getUniqueId());
		// NMS
		((CraftPlayer) e.getPlayer()).getHandle().getDataWatcher().watch(9, (byte) 0);

		// REMOVE COMBAT MODE
		if(xP.inCombat) {
			xP.combatTask.cancel();
			xP.inCombat = false;
		}
		// Give kit Item
		if(xP.kitEquiped == false && !e.getPlayer().getInventory().contains(KitSystem.selectorItem)) {
			e.getPlayer().getInventory().addItem(KitSystem.selectorItem);
		}
		// TP Spawn
		e.setRespawnLocation((Location) XelephiaPlugin.getMainConfig().get().get("spawn", e.getPlayer().getWorld()
				.getSpawnLocation()));
	}

	/*
	 * DamageTakenEvent
	 */
	@EventHandler
	public void onTakeDamage(EntityDamageByEntityEvent e) {
		if(e.getEntity() instanceof Player && !e.isCancelled() && !e.getEntity().isDead()) {
			XPlayer xPVictim = XelephiaPlugin.getXPlayer(e.getEntity().getUniqueId());
			XPlayer xPDamager = null;

			// Damager
			Entity enD = e.getDamager();
			if(enD instanceof Projectile){
				Projectile pro = (Projectile) enD;
				if(pro.getShooter() instanceof Player) {
					xPDamager = XelephiaPlugin.getXPlayer(((Player)pro.getShooter()).getUniqueId());
					if(pro.getType().equals(EntityType.ARROW)) {
						int arrowSlot = xPDamager.getBPlayer().getInventory().first(Material.ARROW);
						if(arrowSlot != -1) {
							xPDamager.getBPlayer().getInventory().getItem(arrowSlot).setAmount(
									xPDamager.getBPlayer().getInventory().getItem(arrowSlot).getAmount()+1);
						}else {
							ArrayList<String> lore = new ArrayList<>();
							lore.add(" ");
							lore.add("§cItem de kit");
							ItemStack arrowItem = Utils.createItemStack("", Material.ARROW, 1, lore, 0, false);
							xPDamager.getBPlayer().getInventory().addItem(arrowItem);
						}
					}
				}else return;
			}else if(enD instanceof Player) {
				xPDamager = XelephiaPlugin.getXPlayer(e.getDamager().getUniqueId());
			}else return;

			// Anti Auto Attack
			if(xPVictim.equals(xPDamager)) return;

			// Combat MOD
			xPVictim.combat();
			xPDamager.combat();

			// Damage System
			xPVictim.lastDamagerName = xPDamager.getName();
			xPVictim.totalDamage+=e.getDamage();
			if(xPVictim.damageTaken.containsKey(xPDamager.getName())) {
				xPVictim.damageTaken.replace(xPDamager.getName(), 
						xPVictim.damageTaken.get(xPDamager.getName())+e.getDamage());
			}else {
				xPVictim.damageTaken.put(xPDamager.getName(), e.getDamage());
			}
		}
	}

	/*
	 * DeathEvent
	 */
	@EventHandler
	public void onDeath(PlayerDeathEvent e) {

		ArrayList<ItemStack> removeItems = new ArrayList<ItemStack>();
		XPlayer xP = XelephiaPlugin.getXPlayer(e.getEntity().getUniqueId());

		if(xP.inCombat) {
			xP.combatTask.cancel();
			xP.inCombat = false;
		}

		xP.setKitEquiped(false);
		xP.actualKillStreak = 0;
		xP.deathCount++;

		// Calculate Coins Reward
		Random rand = new Random();
		int rewardAmount = rand.nextInt((5 - 2) + 1) + 2;

		if(!xP.damageTaken.isEmpty()) {
			for(String pName : xP.damageTaken.keySet()) {
				XPlayer xPDamager = XelephiaPlugin.getXPlayer(pName);
				double prct = (xP.damageTaken.get(pName)/xP.totalDamage);
				double reward = (prct*rewardAmount);
				xPDamager.giveCoins(reward);

				if(xPDamager.isOnline())xPDamager.sendMessage(MessageType.ACTIONBAR, "§6+ §c"+df.format(reward)+" §6Coins");
			}
		}

		xP.damageTaken.clear(); // Reset Damage Map
		xP.totalDamage = 0; // Reset Total Damage

		// Killer
		if(e.getEntity().getKiller() != null) {
			XPlayer xPKiller = XelephiaPlugin.getXPlayer(xP.lastDamagerName);
			xPKiller.getBPlayer().setHealth(xPKiller.getBPlayer().getHealth()+6 < 20 ? xPKiller.getBPlayer().getHealth()+6 : 20);
			xPKiller.actualKillStreak++;
			xPKiller.killCount++;
			// Give Bottle
			if(xPKiller.getBPlayer().getInventory().firstEmpty() == -1) xPKiller.getBPlayer().getWorld()
			.dropItem(e.getEntity().getKiller().getLocation(), new ItemStack(Material.EXP_BOTTLE, 1));
			else xPKiller.getBPlayer().getInventory().addItem(new ItemStack(Material.EXP_BOTTLE, 1));
			// Update KillStreak
			if (xPKiller.highKillStreak < xPKiller.actualKillStreak)
				xPKiller.highKillStreak = xPKiller.actualKillStreak;
		}
		// Triage des items
		for(ItemStack item : e.getDrops()) {
			if(item.hasItemMeta() && item.getItemMeta().hasLore()) {
				if(item.isSimilar(KitSystem.selectorItem)) {
					removeItems.add(item);
					continue;
				}else {
					//Check TAGS
					for(String str : item.getItemMeta().getLore()) {
						if(str.equals("§cItem de kit")) {
							removeItems.add(item);
							break;
						}else if(str.equals("§cItem permanent")) {
							removeItems.add(item);
							e.getEntity().getInventory().addItem(item);
							break;
						}else if(str.equals("§cItem lootable")) {
							break;
						}
					}
				}
			}
		}
		// Clean
		for(ItemStack item : removeItems) {
			e.getDrops().remove(item);
		}
		// XP
		e.setNewExp((int) (e.getEntity().getTotalExperience()*0.85));
		e.setDroppedExp((int) (e.getEntity().getTotalExperience()*0.15));

		/*
		 * Custom Death Message
		 */
		
		if(xP.decoInCombat == true) {
			e.setDeathMessage("§cMort §8| §9"+xP.getBPlayer().getDisplayName()+" §ba déconnecté en plein combat.");
			return;
		}else if(xP.lastDamagerName != null && XelephiaPlugin.getXPlayer(xP.lastDamagerName) != null){
			DamageCause cause = e.getEntity().getLastDamageCause().getCause();
			XPlayer xPKiller = XelephiaPlugin.getXPlayer(xP.lastDamagerName);
			if(cause.equals(DamageCause.ENTITY_ATTACK)) {
				e.setDeathMessage("§cMort §8|§3 "+xP.getBPlayer().getDisplayName()+" §eest mort tué(e) par §c"+xPKiller.getBPlayer().getDisplayName()+" §e!");
			}else if((cause.equals(DamageCause.FIRE) || cause.equals(DamageCause.FIRE_TICK))) {
				e.setDeathMessage("§cMort §8|§3 "+xP.getBPlayer().getDisplayName()+" §cest mort dans les flammes de §4"+xPKiller.getBPlayer().getDisplayName()+" §c!");
			}else if(cause.equals(DamageCause.MAGIC)) {
				e.setDeathMessage("§cMort §8|§3 "+xP.getBPlayer().getDisplayName()+" §dc'est fais tué par le §5sorcier §c"+xPKiller.getBPlayer().getDisplayName()+" §d!");
			}else {
				e.setDeathMessage("§cMort §8|§3 "+xP.getBPlayer().getDisplayName()+" §eest mort tué(e) par §c"+xPKiller.getBPlayer().getDisplayName()+" §e!");
			}
		}else {
			DamageCause cause = e.getEntity().getLastDamageCause().getCause();
			if(cause.equals(DamageCause.FIRE)) {
				e.setDeathMessage("§cMort §8|§3 "+xP.getBPlayer().getDisplayName()+" §cest mort dans les flammes de l'§4enfer §c!");
			}else if(cause.equals(DamageCause.MAGIC)) {
				e.setDeathMessage("§cMort §8|§3 "+xP.getBPlayer().getDisplayName()+" §cest mort d'un sortilège §4maléfique §c!");
			}else {
				e.setDeathMessage("§cMort §8|§3 "+xP.getBPlayer().getDisplayName()+" §cest mort de façon incongrue...");
			}
		}
	}
}