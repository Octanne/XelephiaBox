package eu.octanne.xelephia.xplayer;

import java.text.DecimalFormat;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
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
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

import com.sk89q.worldguard.bukkit.RegionQuery;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.DefaultFlag;

import java.util.Random;
import eu.octanne.xelephia.XelephiaPlugin;
import eu.octanne.xelephia.kit.KitSystem;
import eu.octanne.xelephia.xplayer.XPlayer.MessageType;

public class XPlayerListener implements Listener {

	DecimalFormat df = new DecimalFormat("#.##");
	
	// Player Join ADD XPlayer of Online
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		XPlayer xp = XelephiaPlugin.getXPlayer(e.getPlayer().getUniqueId());
		XelephiaPlugin.xplayersOnline.add(xp);
		e.setJoinMessage(XelephiaPlugin.getMessageConfig().getConfig().getString("joinPlayer").replace("{PLAYER}",
				xp.getName()));
		if(xp.kitEquiped == false && !e.getPlayer().getInventory().contains(KitSystem.selectorItem) && !e.getPlayer().isDead()) {
			e.getPlayer().getInventory().addItem(KitSystem.selectorItem);
		}
	}

	// Player Quit REMOVE XPlayer of Online
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		XPlayer xp = XelephiaPlugin.getXPlayer(e.getPlayer().getUniqueId());
		if (XelephiaPlugin.getXPlayersOnline().contains(xp)) {
			if(xp.inCombat()) {
				Entity killer = null;
				if(xp.lastDamagerName != null) killer = Bukkit.getPlayer(xp.lastDamagerName);
				e.getPlayer().setHealth(1);
				if(killer != null) xp.getBukkitPlayer().damage(10, killer);
				else xp.getBukkitPlayer().damage(10);
			}
			xp.saveIntoDB();
			XelephiaPlugin.xplayersOnline.remove(xp);
		}
		e.setQuitMessage(XelephiaPlugin.getMessageConfig().getConfig().getString("quitPlayer").replace("{PLAYER}",
				e.getPlayer().getName()));
	}
	
	// New
	
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
	 * RespawnEvent
	 */
	@EventHandler
	public void onRespawn(PlayerRespawnEvent e) {
		XPlayer xP = XelephiaPlugin.getXPlayer(e.getPlayer().getUniqueId());
		((CraftPlayer) e.getPlayer()).getHandle().getDataWatcher().watch(9, (byte) 0);
		//Give kit Item
		if(xP.kitEquiped == false && !e.getPlayer().getInventory().contains(KitSystem.selectorItem)) {
			e.getPlayer().getInventory().addItem(KitSystem.selectorItem);
		}
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
	 * InteractItem
	 */
	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		if(e.getItem() != null && e.getItem().equals(KitSystem.selectorItem)) XelephiaPlugin.getKitSystem().openMenu(e.getPlayer());
	}
	
	
	private boolean isPvPActive(Player p) {
		RegionQuery query = WorldGuardPlugin.inst().getRegionContainer().createQuery();
		if (query.testState(p.getLocation(), WorldGuardPlugin.inst().wrapPlayer(p), DefaultFlag.PVP)) {
		    return true;
		}else return false;
	}
	
	/*
	 * DamageTakenEvent
	 */
	@EventHandler
	public void onTakeDamage(EntityDamageByEntityEvent e) {
		if(e.getEntity().getType().equals(EntityType.PLAYER) && !e.isCancelled() && 
				(Bukkit.getPluginManager().isPluginEnabled(WorldGuardPlugin.inst()) ? isPvPActive((Player) e.getEntity()) : true)) {
			XPlayer xPVictim = XelephiaPlugin.getXPlayer(e.getEntity().getUniqueId());
			XPlayer xPDamager = null;
			
			// Damager
			Entity enD = e.getDamager();
			if(enD instanceof Projectile){
				if(((Projectile)enD).getShooter() instanceof Player) {
					xPDamager = XelephiaPlugin.getXPlayer(((Player) ((Projectile)enD).getShooter()).getUniqueId());
				}
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
		
		if(xP.combatTask != null && Bukkit.getScheduler().isCurrentlyRunning(xP.combatTask.getTaskId())) {
			Bukkit.getScheduler().cancelTask(xP.combatTask.getTaskId());
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
			xPKiller.actualKillStreak++;
			xPKiller.killCount++;
			if (xPKiller.highKillStreak < xPKiller.actualKillStreak)
				xPKiller.highKillStreak = xPKiller.actualKillStreak;
		}
		// Trie items
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
	}
}
