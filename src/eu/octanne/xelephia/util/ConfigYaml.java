package eu.octanne.xelephia.util;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.YamlConfiguration;

public class ConfigYaml {

	private File file;
	private YamlConfiguration config;

	public ConfigYaml(String path) {
		file = new File("plugins/Xelephia/" + path);
		config = YamlConfiguration.loadConfiguration(file);
	}

	/*
	 * GET
	 */
	public YamlConfiguration getConfig() {
		return config;
	}

	public File getFile() {
		return file;
	}

	/*
	 * SET VALUE ON PATH
	 */
	public void set(String path, Object value) {
		config.set(path, value);
	}

	/*
	 * SAVE
	 */
	public void save() {
		try {
			config.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void save(YamlConfiguration config) {
		this.config = config;
		try {
			config.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
