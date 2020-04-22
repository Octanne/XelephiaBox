package eu.octanne.xelephia.grade;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

public class GradeManager {
	
	private ArrayList<Grade> grade = new ArrayList<>();
	
	private File gradeFolder = new File("plugins/Xelephia/grades");
	
	private Grade defaultGrade;
	
	public GradeManager(){
		if(!gradeFolder.exists()) {
			gradeFolder.mkdirs();
		}
		load();
	}
	
	private void load() {
		FilenameFilter filter = (dir, name) -> name.endsWith(".yml");
		for(File path : gradeFolder.listFiles(filter)) {
			grade.add(new Grade(path.getName(), this));
		}
	}
	
	public Grade getDefault() {
		return defaultGrade;
	}
	
	public Grade getGrade(String name) {
		for(Grade grade : grade) {
			if(grade.getName().equalsIgnoreCase(name))return grade;
		}
		return null;
	}
}
