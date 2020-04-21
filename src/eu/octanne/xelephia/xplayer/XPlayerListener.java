package eu.octanne.xelephia.xplayer;

import java.text.DecimalFormat;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.WeatherType;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Random;
import eu.octanne.xelephia.XelephiaPlugin;
import eu.octanne.xelephia.kit.KitSystem;
import eu.octanne.xelephia.xplayer.XPlayer.MessageType;

public class XPlayerListener implements Listener {

	DecimalFormat df = new DecimalFormat("#.##");

	private int coucheDelSelector = 150;
	
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
		p.teleport((Location) XelephiaPlugin.getMainConfig().getConfig().get("spawn", p.getWorld()
				.getSpawnLocation()));
		// SELECTOR KIT
		if(xp.kitEquiped == false && !e.getPlayer().getInventory().contains(KitSystem.selectorItem) && !e.getPlayer().isDead()) {
			e.getPlayer().getInventory().addItem(KitSystem.selectorItem);
		}
		// CUSTOM MESSAGE
		e.setJoinMessage(XelephiaPlugin.getMessageConfig().getConfig().getString("joinPlayer").replace("{PLAYER}", xp.getName()));
	}

	/*
	 * PlayerQuitEvent
	 */
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		XPlayer xP = XelephiaPlugin.getXPlayer(e.getPlayer().getUniqueId());
		if (XelephiaPlugin.getXPlayersOnline().contains(xP)) {
			// DECO COMBAT
			if(xP.inCombat() && !xP.getBukkitPlayer().isDead()) {
				xP.combatTask.cancel();
				xP.inCombat = false;
				xP.decoInCombat = true;
				Player killer = null;
				if(xP.lastDamagerName != null) killer = Bukkit.getPlayer(xP.lastDamagerName);
				if(killer != null) {
					e.getPlayer().setHealth(0.0);
				}else {
					xP.getBukkitPlayer().setHealth(0.0);
				}
			}
			// SAVE XPLAYER
			xP.saveIntoDB();
			XelephiaPlugin.xplayersOnline.remove(xP);
		}
		// CUSTOM MESSAGE
		e.setQuitMessage(XelephiaPlugin.getMessageConfig().getConfig().getString("quitPlayer").replace("{PLAYER}", e.getPlayer().getName()));
	}

	/*
	 * PlayerMoveEvent
	 */
	@EventHandler
	public void onPlayerMoveEvent(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		if(e.getTo().getY() < coucheDelSelector && p.getInventory().first(KitSystem.selectorItem) != -1) {
			p.getInventory().clear(p.getInventory().first(KitSystem.selectorItem));
		}
	}
	
	/*
	 * InventoryClickEvent
	 */
	@EventHandler
	public void onInStatsMenu(InventoryClickEvent e) {
		if(e.getWhoClicked() instanceof Player) {
			if (e.getClickedInventory() != null
					&& e.getClickedInventory().getName().contains("§8Statistiques de §b")) {
				e.setCancelled(true);
			}
		}
	}

	/*
	 * InteractItemEvent
	 */
	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		if(e.getItem() != null && e.getItem().equals(KitSystem.selectorItem)) XelephiaPlugin.getKitSystem().openMenu(e.getPlayer());
		
	}
	
	/*
	 * PlayerDropEvent
	 */
	@EventHandler
	public void onDropItem(PlayerDropItemEvent e) {
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
				}else return;
			}else if(enD instanceof Player) {
				xPDamager = XelephiaPlugin.getXPlayer(e.getDamager().getUniqueId());
			}else return;

			if(xPVictim.equals(xPDamager)) return;

			xPVictim.combat();
			xPDamager.combat();
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
			XPlayer xPKiller = XelephiaPlugin.getXPlayer(e.getEntity().getKiller().getUniqueId());
			xPKiller.getBukkitPlayer().setHealth(xPKiller.getBukkitPlayer().getHealth()+6);
			xPKiller.actualKillStreak++;
			xPKiller.killCount++;
			if(xPKiller.getBukkitPlayer().getInventory().firstEmpty() == -1) xPKiller.getBukkitPlayer().getWorld()
			.dropItem(e.getEntity().getKiller().getLocation(), new ItemStack(Material.EXP_BOTTLE, 1));
			else xPKiller.getBukkitPlayer().getInventory().addItem(new ItemStack(Material.EXP_BOTTLE, 1));
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
			e.setDeathMessage("§cMort §8|§b §9"+xP.getName()+" §ba déconnecté en plein combat.");
		}
	}
}