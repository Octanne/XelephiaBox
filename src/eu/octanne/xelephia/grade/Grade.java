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
	
	private List<String> permissions;
	private List<String> inheritance;
	
	private GradeManager parent;
	
	private ConfigYaml config;
	
	@SuppressWarnings("unchecked")
	public Grade(String path, GradeManager manager){
		parent = manager;
		config = new ConfigYaml("grades/"+path);
		this.name = config.get().getString("name");
		this.prefix = config.get().getString("prefix");
		this.permissions = (List<String>) config.get().getList("permissions", new ArrayList<>());
		this.inheritance = (List<String>) config.get().getList("inheritance", new ArrayList<>());
	}
	
	public String getName() {
		return name;
	}
	
	public String getPrefix() {
		return prefix;
	}
	
	public void applyPermissions(XPlayer p) {
		PermissionAttachment perm = p.getBukkitPlayer().addAttachment(XelephiaPlugin.getInstance());
		for(String gradeStr : inheritance) {
			Grade g = parent.getGrade(gradeStr);
			g.applyPermissions(p);
		}
		for(String permStr : permissions) {
			boolean status = true;
			if(permStr.startsWith("-"))status = false;
			perm.setPermission(permStr.startsWith("-") ? permStr.substring(1) : permStr, status);
		}
	}
}
