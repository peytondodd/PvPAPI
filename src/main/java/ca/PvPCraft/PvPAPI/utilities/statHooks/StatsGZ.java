package ca.PvPCraft.PvPAPI.utilities.statHooks;

import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import ca.PvPCraft.PvPAPI.Main;
import ca.PvPCraft.PvPAPI.utilities.Mysql;
import ca.PvPCraft.PvPAPI.utilities.Stats;

public class StatsGZ {
	public static Main plugin;
	public static HashMap<String, ArrayList<String>> Assists = new HashMap<String, ArrayList<String>>();// Total Assists during game


	public StatsGZ (Main mainclass){
		plugin = mainclass;
	}

	/*  All add Methods */

	public static void addToAssist(Player dam, Player def) {
		if (!Assists.containsKey(dam.getName())) {
			Assists.put(dam.getName(), new ArrayList<String>());
		}

		if (!Assists.get(dam.getName()).contains(def.getName())) {
			Assists.get(dam.getName()).add(def.getName());
		}
	}


	/*  All get Methods */

	public static ArrayList<String> getAssists(Player p) {
		if (!Assists.containsKey(p.getName())) {
			Assists.put(p.getName(), new ArrayList<String>());
		}

		ArrayList<String> Assist = Assists.get(p.getName());
		return Assist;
	}

	public static Integer getTotalAssists(String name) {
		// TODO Auto-generated method stub
		return 0;
	}

	public static Integer getWins(String name) {
		ResultSet rs = Mysql.PS.getSecureQuery("SELECT *, COUNT(*) AS wins FROM " + ca.PvPCraft.PvPAPI.Main.Server + "_Matches WHERE Red LIKE CONCAT('%', ?, '%') AND Winner = ? OR Blue LIKE CONCAT('%', ?, '%') AND Winner = ?", ""+Mysql.getUserID(name), "RED", ""+Mysql.getUserID(name), "BLUE");
		Integer wins = 0;
		try {
			if (rs.next()){
				wins = rs.getInt("wins");
			}
		} catch (SQLException e) {e.printStackTrace();}
		
		return wins;
	}

	public static String getRank(String p, boolean getLevel){
		Integer kills = Stats.getKills(p);
		Integer wins = getWins(p);
		Integer assists = getTotalAssists(p);

		Integer score = (kills * 3) + (wins * 20) + (assists * 1);
		String rankName = "";
		Integer level = 0;

		if (score >= 15000){
			level = 20;
			rankName = "General of the Army";
		}
		else if (score >= 12000){
			level = 19;
			rankName = "Colonel";
		}
		else if (score >= 10000){
			level = 18;
			rankName = "Lieutenant Colonel";
		}
		else if (score >= 8500){
			level = 17;
			rankName = "Major";
		}
		else if (score >= 7000){
			level = 16;
			rankName = "Captain";
		}
		else if (score >= 5500){
			level = 15;
			rankName = "First Lieutenant";
		}
		else if (score >= 3500){
			level = 14;
			rankName = "Second Lieutenant";
		}
		else if (score >= 2000){
			level = 13;
			rankName = "Warrant Officer";
		}
		else if (score >= 1500){
			level = 12;
			rankName = "Command Sergeant Major";
		}
		else if (score >= 1100){
			level = 11;
			rankName = "Sergeant Major";
		}
		else if (score >= 900){
			level = 10;
			rankName = "First Sergeant";
		}
		else if (score >= 650){
			level = 9;
			rankName = "Master Sergeant";
		}
		else if (score >= 475){
			level = 8;
			rankName = "Sergeant First Class";
		}
		else if (score >= 300){
			level = 7;
			rankName = "Staff Sergeant";
		}
		else if (score >= 175){
			level = 6;
			rankName = "Sergeant";
		}
		else if (score >= 100){
			level = 5;
			rankName = "Corporal";
		}
		else if (score >= 50){
			level = 4;
			rankName = "Specialist";
		}
		else if (score >= 25){
			level = 3;
			rankName = "Private First Class";
		}
		else if (score >= 10){
			level = 2;
			rankName = "Private-2";
		}
		else if (score >= 0){
			level = 1;
			rankName = "Private";
		}
		if (getLevel == false)
			return rankName;
		else
			return ""+level;
	}
	
	/*
	 * MySQL Functions below... For gathering data from DB... :D
	 */



}
