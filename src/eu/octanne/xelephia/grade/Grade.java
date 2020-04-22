package eu.octanne.xelephia.grade;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.permissions.PermissionAttachment;

import eu.octanne.xelephia.XelephiaPlugin;
import eu.octanne.xelephia.xplayer.XPlayer;

public class Grade {
	
	public static int maxLengthTag = XelephiaPlugin.getMainConfig().get().getInt("maxLengthTag", 30);;
	
	private String sectionName;
	private String name;
	private String prefix;
	private String tabPrefix;
	
	private boolean isDefault;
	
	private List<String> permissions;
	private List<String> inheritence;
	
	private GradeManager parent;
	
	//private ConfigYaml config;
	
	@SuppressWarnings("unchecked")
	public Grade(String section, GradeManager manager){
		parent = manager;
		
		Bukkit.getLogger().info("Chargement du grade : "+section+"...");
		
		this.sectionName = section;
		this.name = parent.gradeConfig.get().getString(section+".name", "");
		this.prefix = parent.gradeConfig.get().getString(section+".prefix", "");
		this.tabPrefix = parent.gradeConfig.get().getString(section+".tabPrefix", "");
		this.permissions = (List<String>) parent.gradeConfig.get().getList(section+".permissions", new ArrayList<>());
		this.inheritence = (List<String>) parent.gradeConfig.get().getList(".inheritences", new ArrayList<>());
		this.isDefault = parent.gradeConfig.get().getBoolean(section+".default", true);
		if(isDefault) parent.defaultGrade = this;
		
		/*config = new ConfigYaml("grades/"+path);
		this.name = config.get().getString("name", "");
		this.prefix = config.get().getString("prefix", "");
		this.tabPrefix = config.get().getString("tabPrefix", "");
		this.permissions = (List<String>) config.get().getList("permissions", new ArrayList<>());
		this.inheritence = (List<String>) config.get().getList("inheritences", new ArrayList<>());
		this.isDefault = config.get().getBoolean("default", false);
		if(isDefault) parent.defaultGrade = this;*/
	}
	
	public String getName() {
		return sectionName;
	}
	
	public String getDisplayName() {
		return name;
	}
	
	public String getPrefix() {
		return prefix;
	}
	
	public String getTabPrefix() {
		return tabPrefix;
	}
	
	public void applyPermissions(XPlayer p) {
		if(p.perms != null) p.perms.remove();
		PermissionAttachment perm = p.getBukkitPlayer().addAttachment(XelephiaPlugin.getInstance());
		p.perms = perm;
		for(String gradeStr : inheritence) {
			Grade g = parent.getGrade(gradeStr);
			g.applyPermissions(p);
		}
		for(String permStr : permissions) {
			boolean status = true;
			if(permStr.startsWith("-"))status = false;
			perm.setPermission(permStr.startsWith("-") ? permStr.substring(1) : permStr, status);
		}
		p.getBukkitPlayer().recalculatePermissions();
	}
	
	public void applyTag(XPlayer p) {
		String tabName = tabPrefix;
		for(int i = 0; tabName.length() < maxLengthTag && i < p.getName().length(); i++) {
			tabName+= p.getName().charAt(i);
		}
		p.getBukkitPlayer().setPlayerListName(tabName);
	}
}
