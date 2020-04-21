package eu.octanne.xelephia.world;

import java.io.File;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.World.Environment;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.event.Listener;
import org.bukkit.WorldCreator;

import eu.octanne.xelephia.util.ConfigYaml;
import eu.octanne.xelephia.world.XWorld.XWorldType;

public class WorldManager implements Listener {

	private ArrayList<XWorld> worldList;
	
	private ConfigYaml worldConfig;

	@SuppressWarnings("unchecked")
	public WorldManager() {
		// Serialization
		ConfigurationSerialization.registerClass(XWorld.class, "XWorld");
		worldConfig = new ConfigYaml("worlds.yml");
		worldList = (ArrayList<XWorld>) worldConfig.getConfig().get("worlds", new ArrayList<>());
		
		startLoad();
	}
	
	private void startLoad() {
		for(XWorld world : worldList) {
			if(world.defaultLoad())world.load();
		}
		save();
	}
	
	public XWorld getWorld(String name) {
		for(XWorld world : worldList) {
			if(world.getName().equalsIgnoreCase(name)) return world;
		}
		return null;
	}
	
	public void save() {
		worldConfig.set("worlds", worldList);
		worldConfig.save();
	}
	
	public boolean importWorld(String name) {
		File file = new File(name+"/level.dat");
		if(file.exists()) {
			XWorld world = new XWorld(Bukkit.createWorld(new WorldCreator(name)));
			worldList.add(world);
			save();
			return true;
		}else return false;
	}
	
	public boolean createWorld(String name, Environment env, XWorldType type, boolean structure) {
		File file = new File(name);
		if(file.exists()) return false;
		XWorld world = new XWorld(name, env, type, structure, true);
		world.load();
		if(world.isLoad()) {
			worldList.add(world);
			save();
			return true;
		}else return false;
	}
	
	public ArrayList<XWorld> getWorlds() {
		return worldList;
	}
	
	static public Environment getEnvByName(String name) {
		if(name.equalsIgnoreCase("NETHER")) {
			return Environment.NETHER;
		}else if(name.equalsIgnoreCase("END") || name.equalsIgnoreCase("THE_END") ) {
			return Environment.THE_END;
		}else if(name.equalsIgnoreCase("NORMAL")) {
			return Environment.NORMAL;
		}else return null;
	}
}
