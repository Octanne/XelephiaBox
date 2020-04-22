package eu.octanne.xelephia.grade;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import eu.octanne.xelephia.util.ConfigYaml;
import eu.octanne.xelephia.util.Utils;

public class GradeManager {
	
	private ArrayList<Grade> grade = new ArrayList<>();
	
	//private File gradeFolder = new File("plugins/Xelephia/grades");
	
	protected ConfigYaml gradeConfig;
	
	protected Grade defaultGrade;
	
	public GradeManager(){
		
		gradeConfig = new ConfigYaml("grades.yml");
		if(!gradeConfig.getFile().exists()){
			gradeConfig.getFile().mkdirs();
			try {
				gradeConfig.getFile().createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
			InputStream stream = getClass().getResourceAsStream("/config/grades.yml");
			Utils.copyAStream(stream, gradeConfig.getFile());
			gradeConfig.reload();
		}
		
		/*if(!gradeFolder.exists()) {
			gradeFolder.mkdirs();
			File defaultFile = new File("plugins/Xelephia/grades/default.yml");
			try {
				defaultFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
			InputStream stream = getClass().getResourceAsStream("/config/default.yml");
			Utils.copyAStream(stream, defaultFile);
		}*/
		load();
	}
	
	private void load() {
		Set<String> gradeSet = ((HashMap<String, Object>) gradeConfig.get().getValues(false)).keySet();
		for(String gradeStr : gradeSet) {
			grade.add(new Grade(gradeStr, this));
		}
		/*FilenameFilter filter = (dir, name) -> name.endsWith(".yml");
		for(File path : gradeFolder.listFiles(filter)) {
			grade.add(new Grade(path.getName(), this));
		}*/
	}
	
	public Grade getDefault() {
		return defaultGrade;
	}
	
	public Grade getGrade(String name) {
		for(Grade grade : grade) {
			if(grade.getName().equalsIgnoreCase(name))return grade;
		}
		return defaultGrade;
	}
	
	public Grade getGradeWithNull(String name) {
		for(Grade grade : grade) {
			if(grade.getName().equalsIgnoreCase(name))return grade;
		}
		return null;
	}
}
