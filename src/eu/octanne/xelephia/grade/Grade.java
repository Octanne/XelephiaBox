package eu.octanne.xelephia.grade;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.permissions.PermissionAttachment;

import eu.octanne.xelephia.XelephiaPlugin;
import eu.octanne.xelephia.util.ConfigYaml;
import eu.octanne.xelephia.xplayer.XPlayer;

public class Grade {

	private String name;
	private String prefix;
	private String tabPrefix;
	
	private boolean isDefault;
	
	private List<String> permissions;
	private List<String> inheritence;
	
	private GradeManager parent;
	
	private ConfigYaml config;
	
	@SuppressWarnings("unchecked")
	public Grade(String path, GradeManager manager){
		parent = manager;
		config = new ConfigYaml("grades/"+path);
		this.name = config.get().getString("name", "");
		this.prefix = config.get().getString("prefix", "");
		this.tabPrefix = config.get().getString("tabPrefix", "");
		this.permissions = (List<String>) config.get().getList("permissions", new ArrayList<>());
		this.inheritence = (List<String>) config.get().getList("inheritences", new ArrayList<>());
		this.isDefault = config.get().getBoolean("default", false);
		if(isDefault) parent.defaultGrade = this;
	}
	
	public String getName() {
		return name;
	}
	
	public String getPrefix() {
		return prefix;
	}
	
	public String getTabPrefix() {
		return tabPrefix;
	}
	
	public void applyPermissions(XPlayer p) {
		PermissionAttachment perm = p.getBukkitPlayer().addAttachment(XelephiaPlugin.getInstance());
		for(String gradeStr : inheritence) {
			Grade g = parent.getGrade(gradeStr);
			g.applyPermissions(p);
		}
		for(String permStr : permissions) {
			boolean status = true;
			if(permStr.startsWith("-"))status = false;
			perm.setPermission(permStr.startsWith("-") ? permStr.substring(1) : permStr, status);
		}
	}
	
	public void applyTag(XPlayer p) {
		String tabName = tabPrefix;
		for(int i = 0; tabName.length() < 16; i++) {
			tabName+= p.getName().charAt(i);
		}
		p.getBukkitPlayer().setPlayerListName(tabName);
	}
}
