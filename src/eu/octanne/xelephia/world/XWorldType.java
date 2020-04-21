package eu.octanne.xelephia.world;

import org.bukkit.WorldType;
import org.bukkit.World.Environment;

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
		}else if(name.equals("VOID")) {
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
			return XWorldType.NORMAL;
		}
	}
	
	static public Environment getEnvByName(String name) {
		if(name.equalsIgnoreCase("NETHER")) {
			return Environment.NETHER;
		}else if(name.equalsIgnoreCase("END") || name.equalsIgnoreCase("THE_END") ) {
			return Environment.THE_END;
		}else if(name.equalsIgnoreCase("NORMAL")) {
			return Environment.NORMAL;
		}else return Environment.NORMAL;
	}
}