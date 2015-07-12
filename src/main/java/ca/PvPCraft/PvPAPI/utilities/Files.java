package ca.PvPCraft.PvPAPI.utilities;


import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import ca.PvPCraft.PvPAPI.Main;
import ca.PvPCraft.PvPAPI.enums.Config;

import org.bukkit.Bukkit;



public class Files{
	public static Main plugin;
	static Logger log = Bukkit.getLogger();

	public static Config config;
	public static Config deathconf;
	public static Config teleports;

	public static Config servers;
	public static Config layouts;
	public static Config banConf;
	//public static Config challengeConf;

	public static Config npcs;
	public Files (Main mainclass){
		plugin = mainclass;

		npcs = new Config("npcs", plugin);
		config = new Config("config", plugin);
		deathconf = new Config("deathmsgs", plugin);
		teleports = new Config("teleports", plugin);
		banConf = new Config("banConfig", plugin);
		//if (Main.Server.equalsIgnoreCase("Faction"))
		//challengeConf = new Config("challengeConfig", plugin);
	}
	public static void loadDelayedSignFiles() {
		// TODO Auto-generated method stub
		if (Main.SignTeleportsEnable){
			servers = new Config("Servers", plugin);
			layouts = new Config("Layouts", plugin);
		}
	}

	public static List<File> listf(String directoryName) {
		File directory = new File(directoryName);

		List<File> resultList = new ArrayList<File>();

		// get all the files from a directory
		File[] fList = directory.listFiles();
		resultList.addAll(Arrays.asList(fList));
		for (File file : fList) {
			if (file.isFile()) {
				System.out.println(file.getAbsolutePath());
			} else if (file.isDirectory()) {
				resultList.addAll(listf(file.getAbsolutePath()));
			}
		}
		//System.out.println(fList);
		return resultList;
	}
	
	public static String getUUID(String player) {
		ResultSet rs = Mysql.PS.getSecureQuery("SELECT * FROM Users WHERE Username = ?", player);
		try {
			if (rs.next())
				return rs.getString("UUID");
			else
				return player;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return player;
		
    }

}