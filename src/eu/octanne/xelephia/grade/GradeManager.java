package eu.octanne.xelephia.grade;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import eu.octanne.xelephia.util.Utils;

public class GradeManager {
	
	private ArrayList<Grade> grade = new ArrayList<>();
	
	private File gradeFolder = new File("plugins/Xelephia/grades");
	
	protected Grade defaultGrade;
	
	public GradeManager(){
		if(!gradeFolder.exists()) {
			gradeFolder.mkdirs();
			File defaultFile = new File("plugins/Xelephia/grades/default.yml");
			try {
				defaultFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
			InputStream stream = getClass().getResourceAsStream("/grades/default.yml");
			Utils.copyAStream(stream, defaultFile);
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
