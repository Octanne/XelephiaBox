package eu.octanne.xelephia.world;

import java.io.File;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.World.Environment;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.WorldCreator;

import eu.octanne.xelephia.util.ConfigYaml;
import eu.octanne.xelephia.world.XWorld.XWorldType;

public class WorldManager {

	public ArrayList<XWorld> worldList;
	
	private ConfigYaml worldConfig = new ConfigYaml("worlds.yml");

	@SuppressWarnings("unchecked")
	public WorldManager() {
		// Serialization
		ConfigurationSerialization.registerClass(XWorld.class, "XWorld");
		worldList = (ArrayList<XWorld>) worldConfig.getConfig().get("worlds", new ArrayList<>());
		
		startLoad();
	}

	private void startLoad() {
		for(XWorld world : worldList) {
			if(world.defaultLoad())world.load();
		}
	}
	
	public XWorld getWorld(String name) {
		for(XWorld world : worldList) {
			if(world.getName().equalsIgnoreCase(name)) return world;
		}
		return null;
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

	public boolean importWorld(String name) {
		File file = new File(name+"/level.dat");
		if(file.exists()) {
			XWorld world = new XWorld(Bukkit.createWorld(new WorldCreator(name)));
			worldList.add(world);
			worldConfig.save();
			return true;
		}else return false;
		
		
	}
	
	public boolean createWorld(String name, Environment env, XWorldType type, boolean structure) {
		XWorld world = new XWorld(name, env, type, structure, true);
		world.load();
		if(world.isLoad()) {
			worldList.add(world);
			worldConfig.save();
			return true;
		}else return false;
	}
}
