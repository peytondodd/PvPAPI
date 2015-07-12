package ca.PvPCraft.PvPAPI.utilities.statHooks;

import java.sql.ResultSet;
import java.sql.SQLException;

import ca.PvPCraft.PvPAPI.Main;
import ca.PvPCraft.PvPAPI.utilities.Message;
import ca.PvPCraft.PvPAPI.utilities.Mysql;
import ca.PvPCraft.PvPAPI.utilities.Stats;

import org.bukkit.entity.Player;

public class HungerGames {
	
	
	
	
	public static String getRankHG(Player p, boolean b) {
		int Score = Stats.getFame(p.getName());
		String Rank = "";
		String T1 = "";
		String T2 = "";
		String T3 = "";
		String T4 = "";
		String T5 = "";
		String T6 = "";
		String T7 = "";
		String T8 = "";
		String T9 = "";
		String T10 = "";
		String T11 = "";
		String T12 = "";
		String T13 = "";
		String T14 = "";
		String T15 = "";
		String T16 = "";
		String T17 = "";
		
		T1 = "Newbie";
		T2 = "Hero";
		T3 = "Fierce Hero";
		T4 = "Mighty Hero";
		T5 = "Deadly Hero";
		T6 = "Terrifying Hero";
		T7 = "Conquering Hero";
		T8 = "Subjugating Hero";
		T9 = "Vanquishing Hero";
		T10 = "Renowned Hero";
		T11 = "Illustrious Hero";
		T12 = "Eminent Hero";
		T13 = "King's Hero";
		T14 = "Emperor's Hero";
		T15 = "Balthazar's Hero";
		T16 = "Legendary Hero";
		T17 = "PvP Overlord";
		
		
		if (Score >= 140000)
			Rank = T17;
		else if (Score >= 100000)
			Rank = T16;
		else if (Score >= 60000)
			Rank = T15;
		else if (Score >= 36000)
			Rank = T14;
		else if (Score >= 21600)
			Rank = T13;
		else if (Score >= 12960)
			Rank = T12;
		else if (Score >= 7750)
			Rank = T11;
		else if (Score >= 4665)
			Rank = T10;
		else if (Score >= 2800)
			Rank = T9;
		else if (Score >= 1680)
			Rank = T8;
		else if (Score >= 1000)
			Rank = T7;
		else if (Score >= 600)
			Rank = T6;
		else if (Score >= 360)
			Rank = T5;
		else if (Score >= 180)
			Rank = T4;
		else if (Score >= 75)
			Rank = T3;
		else if (Score >= 25)
			Rank = T2;
		else if (Score >= 0)
			Rank = T1;
		
		
		if (Rank != null && b == true)
			Rank = "&8[&e" + Rank + "&8]&7 ";
		else if (Rank != null)
			Rank = "&e" + Rank + "&7";
		
		return Message.Colorize(Rank);
	}
	
	
	public static int getTotalWinsHG(String p){
		int Wins = 0;
		if (Main.UseMySQL){
			ResultSet rs = null;
			try {
				rs = Mysql.PS.getSecureQuery("SELECT *, COUNT(*) AS Wins FROM HungerGames_Rounds WHERE Winner = ?", ""+Mysql.getUserID(p));
				if (rs.next()){
					Wins = rs.getInt("Wins");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return Wins;
	}
	
}
