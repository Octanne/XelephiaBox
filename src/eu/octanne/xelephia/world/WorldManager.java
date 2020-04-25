package eu.octanne.xelephia.world;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.event.Listener;
import org.bukkit.WorldCreator;

import eu.octanne.xelephia.util.ConfigYaml;

public class WorldManager implements Listener {

	private ArrayList<XWorld> worldList = new ArrayList<>();
	private ArrayList<XWorld> defaultWorlds = new ArrayList<>();


	protected ConfigYaml worldConfig;

	public WorldManager() {
		worldConfig = new ConfigYaml("worlds.yml");
		if(!worldConfig.getFile().exists())
			try {
				worldConfig.getFile().createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}

		loadConfig(worldConfig.get().getKeys(false));
		
		// LOAD DEFAULT WORLDS
		for(World world : Bukkit.getWorlds()) {
			if(getWorld(world.getName()) == null)defaultWorlds.add(new XWorld(world, false, this));
			Bukkit.getLogger().info("[Xelephia] add DefaultWorld : " + world.getName());
		}
	}

	private void loadConfig(Set<String> set) {
		for(String path : set) {
			worldList.add(new XWorld(path, this));
		}
	}

	public boolean importWorld(String name) {
		File file = new File(Bukkit.getWorldContainer().getName()+"/"+name+"/level.dat");
		if(file.exists() && getWorld(name) == null) {
			World w = Bukkit.createWorld(new WorldCreator(name));
			XWorld world = new XWorld(w, true, this);
			worldList.add(world);
			return true;
		}else return false;
	}

	public boolean createWorld(String name, Environment env, XWorldType type, boolean structure) {
		File file = new File(name);
		if(file.exists()) return false;
		XWorld world = new XWorld(name, env, type, structure, true, this);
		world.load();
		if(world.isLoad()) {
			worldList.add(world);
			return true;
		}else return false;
	}

	public ArrayList<XWorld> getWorlds() {
		return worldList;
	}

	public ArrayList<XWorld> getDefaultWorlds() {
		return defaultWorlds;
	}

	public XWorld getWorld(String name) {
		for(XWorld world : worldList) {
			if(world.getName().equalsIgnoreCase(name)) return world;
		}
		for(XWorld world : defaultWorlds) {
			if(world.getName().equalsIgnoreCase(name)) return world;
		}
		return null;
	}
}
