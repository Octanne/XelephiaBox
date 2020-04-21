package eu.octanne.xelephia.world;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
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
		this.type = XWorldType.getByWorldType(world.getWorldType());
		this.env = world.getEnvironment();
		this.hasStructure = world.canGenerateStructures();
		this.defaultLoad = false; 
	}

	/*
	 * Methods
	 */
	public boolean unload() {
		if(Bukkit.unloadWorld(getWorld(), true)) {
			isLoad = false;
			return true;
		}else return false;
	}

	public boolean load() {
		if(Bukkit.getWorld(worldName) == null) {
			WorldCreator creator = new WorldCreator(worldName);
			creator.generateStructures(hasStructure);
			creator.environment(env);
			creator.type(type.getType());
			if(type.needGenerator())creator.generatorSettings(type.getName());
			Bukkit.createWorld(new WorldCreator(worldName));
			isLoad = true;
			return true;
		}else {
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
		map.put("load", isLoad);
		return map;
	}

	public static XWorld deserialize(Map<String, Object> map) {
		return new XWorld((String)map.get("name"), WorldManager.getEnvByName((String)map.get("environment")), XWorldType.getByName((String)map.get("type")), (boolean)map.get("structure"), (boolean)map.get("load"));
	}

	/*
	 * Enum
	 */
	public enum XWorldType {
		AMPLIFIED("AMPLIFIED", WorldType.AMPLIFIED, false),
		CUSTOMIZED("CUSTOMIZED", WorldType.CUSTOMIZED, false),
		FLAT("FLAT", WorldType.FLAT, false),
		LARGE_BIOMES("LARGE_BIOMES", WorldType.LARGE_BIOMES, false),
		NORMAL("NORMAL", WorldType.NORMAL, false),
		VERSION_1_1("VERSION_1_1", WorldType.VERSION_1_1, false),
		VOID("VOID", WorldType.NORMAL, true);

		private String name;
		private WorldType type;
		private boolean needGenerator;

		XWorldType(String name, WorldType type, boolean needGen){
			this.name = name;
			this.needGenerator = needGen;
		}

		public String getName() {
			return name;
		}

		public WorldType getType() {
			return type;
		}

		public boolean needGenerator() {
			return needGenerator;
		}

		static public XWorldType getByName(String name) {
			if(name.equals("AMPLIFIED")) {
				return XWorldType.VOID;
			}else if(name.equals("CUSTOMIZED")) {
				return XWorldType.CUSTOMIZED;
			}else if(name.equals("FLAT")) {
				return XWorldType.FLAT;
			}else if(name.equals("LARGE_BIOMES")) {
				return XWorldType.VOID;
			}else if(name.equals("NORMAL")) {
				return XWorldType.NORMAL;
			}else if(name.equals("VERSION_1_1")) {
				return XWorldType.VOID;
			}else if(name.equals("Xelephia:void")) {
				return XWorldType.VOID;
			}else return null;
		}

		static public XWorldType getByWorldType(WorldType type) {
			if(type.equals(WorldType.AMPLIFIED)) {
				return XWorldType.AMPLIFIED;
			}else if(type.equals(WorldType.CUSTOMIZED)) {
				return XWorldType.CUSTOMIZED;
			}else if(type.equals(WorldType.FLAT)) {
				return XWorldType.FLAT;
			}else if(type.equals(WorldType.LARGE_BIOMES)) {
				return XWorldType.LARGE_BIOMES;
			}else if(type.equals(WorldType.NORMAL)) {
				return XWorldType.NORMAL;
			}else if(type.equals(WorldType.VERSION_1_1)) {
				return XWorldType.VERSION_1_1;
			}else {
				return null;
			}
		}
	}
}
