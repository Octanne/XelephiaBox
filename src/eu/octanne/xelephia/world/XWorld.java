package eu.octanne.xelephia.world;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.World.Environment;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

public class XWorld implements ConfigurationSerializable{

	private XWorldType type;
	private Environment env;

	private String worldName;

	private boolean defaultLoad = false;
	private boolean isLoad = false;

	private boolean hasStructure;

	XWorld(String name, Environment env, XWorldType type, boolean hasStructure, boolean defaultLoad){
		this.worldName = name;
		this.type = type;
		this.env = env;
		this.hasStructure = hasStructure;
		this.defaultLoad = defaultLoad;
	}

	XWorld(World world){
		this.worldName = world.getName();
		this.isLoad = true;
		this.type = XWorldType.getByWorldType(world.getWorldType());
		this.env = world.getEnvironment();
		this.hasStructure = world.canGenerateStructures();
		this.defaultLoad = true; 
	}

	/*
	 * Methods
	 */
	public boolean unload() {
		if(Bukkit.unloadWorld(getWorld(), true)) {
			isLoad = false;
			defaultLoad = false;
			return true;
		}else return false;
	}

	public boolean load() {
		if(Bukkit.getWorld(worldName) == null) {
			Bukkit.getLogger().info("Start load world : " + worldName + " type : " + type.getName() 
			+ " env : " + env);
			WorldCreator creator = new WorldCreator(worldName);
			creator.generateStructures(hasStructure);
			creator.environment(env);
			creator.type(type.getType());
			if(type.needGenerator())creator.generatorSettings(type.getName());
			Bukkit.createWorld(creator);
			isLoad = true;
			Bukkit.getLogger().info("End load world : " + worldName + " type : " + type.getName() 
			+ " env : " + env);
			return true;
		}else {
			isLoad = true;
			return false;
		}
	}

	public boolean isLoad() {
		return this.isLoad;
	}

	public boolean defaultLoad() {
		return defaultLoad;
	}

	public String getName() {
		return worldName;
	}

	public XWorldType getType() {
		return type;
	}

	public void setDefaultLoad(boolean load) {
		defaultLoad = load;
	}

	public World getWorld() {
		return Bukkit.getWorld(worldName);
	}

	/*
	 * Serialize
	 */
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<>();
		map.put("name", worldName);
		map.put("type", type.getName());
		map.put("environment", env.name());
		map.put("structure", hasStructure);
		map.put("load", defaultLoad);
		return map;
	}

	public static XWorld deserialize(Map<String, Object> map) {
		return new XWorld((String)map.get("name"), XWorldType.getEnvByName((String)map.get("environment")), XWorldType.getByName((String)map.get("type")), (boolean)map.get("structure"), (boolean)map.get("load"));
	}
}
