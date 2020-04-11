package eu.octanne.xelephia.sql;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;

import org.bukkit.Bukkit;

public class DataBase {

	private Connection connection;
	private String url;

	public DataBase(String dbName) {
		File file = new File("plugins/Xelephia/db/");
		if (!file.exists())
			file.mkdirs();
		this.url = "jdbc:sqlite:plugins/Xelephia/db/" + dbName + ".db";
	}

	/*
	 * Connect
	 */
	public boolean connect() {
		try {
			try {
				Class.forName("org.sqlite.JDBC");
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			connection = DriverManager.getConnection(url);
			Bukkit.getLogger().log(Level.INFO, "[Xelephia] Chargement DB => SQLite réussi !");
		} catch (SQLException e) {
			e.printStackTrace();
			Bukkit.getLogger().log(Level.INFO, "[Xelephia] Arret du serveur !");
			Bukkit.getServer().shutdown();
			return false;
		}
		return true;
	}

	/*
	 * Disconnect
	 */
	public boolean disconnect() {
		if (isConnected()) {
			try {
				connection.close();
				Bukkit.getLogger().log(Level.INFO, "[Xelephia] Déchargement DB => SQLite réussi !");
			} catch (SQLException e) {
				e.printStackTrace();
				return false;
			}
			return true;
		} else
			return false;
	}

	/*
	 * isConnected
	 */
	public boolean isConnected() {
		return connection != null;
	}

	/*
	 * Connection
	 */
	public Connection getConnection() {
		return connection;
	}

	/*
	 * createTable
	 * 
	 * @return => false if TABLE exist
	 */
	public boolean createTable(String order) {
		try {
			Statement stmt = connection.createStatement();

			// create a new table
			stmt.execute(order);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			return false;
		}
		return true;
	}
}
