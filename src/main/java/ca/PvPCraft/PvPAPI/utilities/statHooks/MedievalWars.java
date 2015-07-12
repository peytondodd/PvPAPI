package ca.PvPCraft.PvPAPI.utilities.statHooks;

import java.sql.ResultSet;
import java.sql.SQLException;

import ca.PvPCraft.PvPAPI.utilities.Mysql;
import ca.PvPCraft.PvPAPI.utilities.Stats;

import org.bukkit.ChatColor;

public class MedievalWars {
	
	
	
	
	public static int getTotalCaptures(String pname){
		int captures = 0;
		ResultSet rs = null;
		try {
			rs = Mysql.PS.getSecureQuery("SELECT * FROM UserInfo WHERE User = ?", ""+Mysql.getUserID(pname));
			if (rs.next())
				captures =  rs.getInt("FlagCaptures");
		} catch (SQLException e) {
			e.printStackTrace();
			} finally {
				try {
					if (rs != null)
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		
		return captures;
	}
	
	
	
		

	public static String getChatRank(String pname, boolean Brackets) throws SQLException {
		int Kills = Stats.getTotalKills(pname);
		
		String P = "";
		if (Kills <= 100)
			P = "Peasant";
		else if (Kills <= 250)
			P = "Lionheart";
		else if (Kills <= 650)
			P = "Squire";
		else if (Kills <= 875)
			P = "Crusader";
		else if (Kills <= 1200)
			P = "Baron";
		else if (Kills <= 1600)
			P = "Count";
		else if (Kills <= 2200)
			P = "Desperado";
		else if (Kills <= 3200)
			P = "War-Chief";
		else if (Kills <= 4000)
			P = "Overlord";
		else if (Kills <= 6000)
			P = "Bandito";
		else if (Kills <= 8000)
			P = "Justiciar";
		else if (Kills <= 10000)
			P = "Archon";
		else if (Kills <= 13000)
			P = "Emperor";
		else if (Kills <= 17000)
			P = "Wunderkind";
		else if (Kills <= 20000)
			P = "The Amazing";
		
		
		String Name = "";
		
		if (Brackets)
		Name = ChatColor.GRAY + "(" + ChatColor.GOLD  + P + ChatColor.GRAY + ") ";
		else
		Name =  ChatColor.GOLD  + P + ChatColor.GRAY;
		
		return Name;
	}
	
}
