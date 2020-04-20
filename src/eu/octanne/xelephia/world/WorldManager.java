package eu.octanne.xelephia.world;

import java.io.File;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.entity.Player;

import eu.octanne.xelephia.XelephiaPlugin;
import eu.octanne.xelephia.util.ConfigYaml;

public class WorldManager {

	public ConfigYaml worldConfig = new ConfigYaml("worlds.yml");

	public WorldManager() {
		@SuppressWarnings("unchecked")
		ArrayList<String> worldList = (ArrayList<String>) worldConfig.getConfig().get("worlds",
				new ArrayList<String>());
		for (String worldName : worldList) {
			Bukkit.createWorld(new WorldCreator(worldName));
		}
	}

	public ArrayList<String> getWorlds(String format) {
		if (format.equalsIgnoreCase("status")) {
			File file = Bukkit.getWorldContainer();
			String[] worlds = file.list();
			ArrayList<String> world = new ArrayList<String>();

			for (int nbr = 0; nbr < worlds.length; nbr++) {
				File fileO = new File(worlds[nbr] + "/level.dat");
				if (fileO.exists()) {
					if (Bukkit.getWorld(worlds[nbr]) != null) {
						world.add("§a" + worlds[nbr]);
					} else {
						world.add("§c" + worlds[nbr]);
					}
				}
			}
			return world;
		} else
			return null;
	}

	public void createWorld(String worldName, String typeWorld, String envWorld, boolean withStructure) {
		WorldCreator worldC = new WorldCreator(worldName);
		if (envWorld.equalsIgnoreCase("end")) {
			worldC.environment(Environment.THE_END);
		} else if (typeWorld.equalsIgnoreCase("nether")) {
			worldC.environment(Environment.NETHER);
		} else if (typeWorld.equalsIgnoreCase("normal")) {
			worldC.environment(Environment.NORMAL);
		}
		if (typeWorld.equalsIgnoreCase("normal")) {
			worldC.type(WorldType.NORMAL);
		} else if (typeWorld.equalsIgnoreCase("flat")) {
			worldC.type(WorldType.FLAT);
		} else if (typeWorld.equalsIgnoreCase("void")) {
			worldC.type(WorldType.NORMAL);
			worldC.generator("Xelephia:void");
		}
		if (withStructure) {
			worldC.generateStructures(true);
		} else
			worldC.generateStructures(false);
		Bukkit.createWorld(worldC);
		Bukkit.getWorld(worldName).save();
		Bukkit.getWorld(worldName).setSpawnLocation(4, 65, 4);
		@SuppressWarnings("unchecked")
		ArrayList<String> worldList = (ArrayList<String>) worldConfig.getConfig().get("worlds",
				new ArrayList<String>());
		worldList.add(worldName);
		worldConfig.set("worlds", worldList);
		worldConfig.save();
	}

	public void unloadWorld(String worldName) {
		for (Player p : Bukkit.getWorld(worldName).getPlayers()) {
			p.teleport((Location) XelephiaPlugin.getMainConfig().getConfig().get("spawn",
					Bukkit.getWorlds().get(0).getSpawnLocation()));
		}
		Bukkit.unloadWorld(worldName, true);
		Bukkit.getWorlds().remove(Bukkit.getWorld(worldName));
		@SuppressWarnings("unchecked")
		ArrayList<String> worldList = (ArrayList<String>) worldConfig.getConfig().get("worlds",
				new ArrayList<String>());
		worldList.remove(worldName);
		worldConfig.set("worlds", worldList);
		worldConfig.save();
	}

	public void loadWorld(String worldName) {
		Bukkit.createWorld(new WorldCreator(worldName));
		@SuppressWarnings("unchecked")
		ArrayList<String> worldList = (ArrayList<String>) worldConfig.getConfig().get("worlds",
				new ArrayList<String>());
		worldList.add(worldName);
		worldConfig.set("worlds", worldList);
		worldConfig.save();
	}
}
