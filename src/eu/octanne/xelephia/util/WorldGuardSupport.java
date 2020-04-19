package eu.octanne.xelephia.util;

import java.lang.reflect.InvocationTargetException;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;

import com.sk89q.worldguard.bukkit.RegionQuery;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.DefaultFlag;

public class WorldGuardSupport {
	@SuppressWarnings("rawtypes")
	static public boolean isPvPActive(Entity e) {
		Entity p = e;
		if(((Projectile)p).getShooter() instanceof Player) {
			p = (Player)((Projectile)e).getShooter();
		}else if(!(p instanceof Player)) return false;
		
		try {
			Class<?> classWorldGuardPlugin = Class.forName("com.sk89q.worldguard.bukkit.WorldGuardPlugin");
			Class<?> classRegionContainer = Class.forName("com.sk89q.worldguard.bukkit.RegionContainer");
			Class<?> classRegionQuery = Class.forName("com.sk89q.worldguard.bukkit.RegionQuery");
			Class<?> classLocalPlayer = Class.forName("com.sk89q.worldguard.LocalPlayer");
			Class<?> classDefaultFlag = Class.forName("com.sk89q.worldguard.protection.flags.DefaultFlag");
			
			Object worldGuardPlugin = classWorldGuardPlugin.getMethod("inst").invoke(null);
			Object regionContainer = classWorldGuardPlugin.getMethod("getRegionContainer").invoke(worldGuardPlugin);
			Object query = classRegionContainer.getMethod("createQuery").invoke(regionContainer);
			
			Class[] wrapPara = {Player.class};
			Object wrapPlayer = classWorldGuardPlugin.getMethod("wrapPlayer", wrapPara).invoke(worldGuardPlugin, e);
			
			Object pvpFlag = classDefaultFlag.getMethod("PVP").invoke(null);
			
			Class[] queryPara = {Location.class, classLocalPlayer, classDefaultFlag};
			return ((boolean)classRegionQuery.getMethod("testState", queryPara).invoke(query, e.getLocation(), wrapPlayer, pvpFlag));
		} catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e1) {
			e1.printStackTrace();
			//return false;
		}
		
		if(e instanceof Player) {
			RegionQuery query = WorldGuardPlugin.inst().getRegionContainer().createQuery();
			if (query.testState(e.getLocation(), WorldGuardPlugin.inst()
					.wrapPlayer((Player)e), DefaultFlag.PVP)) return true;
			else return false;
		}else if(((Projectile)e).getShooter() instanceof Player){
			RegionQuery query = WorldGuardPlugin.inst().getRegionContainer().createQuery();
			if (query.testState(e.getLocation(), WorldGuardPlugin.inst()
					.wrapPlayer((Player)((Projectile)e).getShooter()), DefaultFlag.PVP)) return true;
			else return false;
		}else return false;
	}
}
