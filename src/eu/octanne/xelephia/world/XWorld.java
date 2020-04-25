package eu.octanne.xelephia.world;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.World.Environment;

public class XWorld {

	private XWorldType type;
	private Environment env;

	private String worldName;

	private boolean defaultLoad = false;
	private boolean isLoad = false;

	private boolean hasStructure;
	
	private WorldManager parent;

	protected XWorld(String name, Environment env, XWorldType type, boolean hasStructure, boolean defaultLoad, WorldManager parent){
		this.parent = parent;
		
		this.worldName = name;
		this.type = type;
		this.env = env;
		this.hasStructure = hasStructure;
		this.defaultLoad = defaultLoad;
		
		save(name);
	}
	
	protected XWorld(String path, WorldManager parent) {
		this.parent = parent;
		
		// LOAD
		this.worldName = parent.worldConfig.get().getString(path+".name"); 
		this.env = XWorldType.getEnvByName(parent.worldConfig.get().getString(path+".environment"));
		this.type = XWorldType.getByName(parent.worldConfig.get().getString(path+".type"));
		this.hasStructure = parent.worldConfig.get().getBoolean(path+".structure");
		this.defaultLoad = parent.worldConfig.get().getBoolean(path+".load");
		
		load();
	}

	protected XWorld(World world){
		this.isLoad = true;
		
		this.worldName = world.getName();
		this.type = XWorldType.getByWorldType(world.getWorldType());
		this.env = world.getEnvironment();
		this.hasStructure = world.canGenerateStructures();
		this.defaultLoad = true; 
	}

	private void save(String path) {
		parent.worldConfig.set(path+".name", worldName);
		parent.worldConfig.set(path+".type", type.getName());
		parent.worldConfig.set(path+".environment", env.name());
		parent.worldConfig.set(path+".structure", hasStructure);
		parent.worldConfig.set(path+".load", defaultLoad);
		parent.worldConfig.save();
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
}
