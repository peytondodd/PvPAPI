package ca.PvPCraft.PvPAPI.utilities;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

import ca.PvPCraft.PvPAPI.Main;
import ca.PvPCraft.PvPAPI.methods.ConvertTimings;
import ca.PvPCraft.PvPAPI.methods.PlayersInfo;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class Stats implements Listener{
	
	public static Main plugin;
	static Logger log = Bukkit.getLogger();
	public static HashMap<String, Integer> TotalScore = new HashMap<String, Integer>();// Total score of a player - Polled from DB
	public static HashMap<String, Integer> TotalKills = new HashMap<String, Integer>();// Total Kills of a player - Polled from DB
	public static HashMap<String, Integer> TotalFame = new HashMap<String, Integer>();// Fame count of a player - During round
	public static HashMap<String, Integer> RoundScore = new HashMap<String, Integer>();// Round Score of a player - During round
	public static HashMap<String, Integer> RoundKills = new HashMap<String, Integer>();// Total Kills of a player - During round
	public static HashMap<String, Integer> RoundDeaths = new HashMap<String, Integer>();// Total Deaths of a player - During round
	public static HashMap<String, Integer> TotalDeaths = new HashMap<String, Integer>();// Total Deaths of a player - Polled from DB
	public static HashMap<String, ArrayList<String>> Assists = new HashMap<String, ArrayList<String>>();// Total Assists during game
	public static HashMap<String, Integer> killStreaks = new HashMap<String, Integer>();// Total Deaths of a player - Polled from DB
	
	
	public static HashMap<String, Integer> Tokens = new HashMap<String, Integer>();// Total Deaths of a player - Polled from DB
	public static HashMap<String, Integer> PlotsOwned = new HashMap<String, Integer>();// Total Deaths of a player - Polled from DB
	
	public Stats (Main mainclass){
		plugin = mainclass;
	}
	
	/*  All add Methods */

	public static void earnedAKill(Player p){
		if (!RoundKills.containsKey(p.getName()))
			RoundKills.put(p.getName(), 0);
		
		if (!killStreaks.containsKey(p.getName()))
			killStreaks.put(p.getName(), 0);
		
		killStreaks.put(p.getName(), killStreaks.get(p.getName()) + 1);
		
		RoundKills.put(p.getName(), getKills(p.getName()) + 1);
	}
	
	public static void earnedADeath(Player p) {
		if (!RoundDeaths.containsKey(p.getName()))
			RoundDeaths.put(p.getName(), 0);
		
		if (killStreaks.containsKey(p.getName()))
			killStreaks.remove(p.getName());		
		
		if (TotalFame.containsKey(p)){
			ResultSet rs = Mysql.PS.getSecureQuery("UPDATE HungerGames_Fame SET Fame = ? WHERE User = ?", ""+getFame(p.getName()), ""+Mysql.getUserID(p.getName()));
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		RoundDeaths.put(p.getName(), getDeaths(p.getName()) + 1);
	}
	
	public static void addToAssist(Player dam, Player def){
		if (!Assists.containsKey(dam.getName()))
			Assists.put(dam.getName(), new ArrayList<String>());
			
		
		if (!Assists.get(dam.getName()).contains(def.getName()))
			Assists.get(dam.getName()).add(def.getName());
	}
	
	/*  All get Methods */
	
	
	
	
	public static ArrayList<String> getAssists(Player p){
		if (!Assists.containsKey(p.getName()))
			Assists.put(p.getName(), new ArrayList<String>());
			
			ArrayList<String> Assist = Assists.get(p.getName());
		
		return Assist;
	}

	public static int getKills(String p){
		if (!RoundKills.containsKey(p))
			RoundKills.put(p, 0);
			return RoundKills.get(p);
	}
	
	
	public static int getDeaths(String p){
		if (!RoundDeaths.containsKey(p))
			RoundDeaths.put(p, 0);
			return RoundDeaths.get(p);
	}
	
	public static int getKillstreak(String name){
		int number = 0;
		
		if (killStreaks.containsKey(name))
			number = killStreaks.get(name);
		return number;
	}
	
	
	/*
	 * MySQL Functions below... For agthering data from DB... :D
	 */
	
	
	
	
	public static int getTotalKills(String p){
		int Kills = 0;
		
		
		if (!TotalKills.containsKey(p)){
			if (Main.UseMySQL){
				ResultSet rs = Mysql.PS.getSecureQuery("SELECT *, COUNT(*) AS Count FROM `" + Main.Server + "_Kills` WHERE Killer = ?", ""+Mysql.getUserID(p));
				try {
					if (rs.next())
						Kills = rs.getInt("Count");
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
			TotalKills.put(p, Kills);
		}
		Kills = TotalKills.get(p) + getKills(p);
		return Kills;
	}
		
	public static int getTotalDeaths(String p){
		int Deaths = 0;
		if (!TotalDeaths.containsKey(p)){
			if (Main.UseMySQL){
				ResultSet rs = Mysql.PS.getSecureQuery("SELECT *, COUNT(*) AS Count FROM `" + Main.Server + "_Kills` WHERE Victim = ?", ""+Mysql.getUserID(p));
				try {
					if (rs.next())
						Deaths = rs.getInt("Count");
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
			TotalDeaths.put(p, Deaths);
		}
		Deaths = TotalDeaths.get(p) + getDeaths(p);
		return Deaths;
	}
	
	public static int getRoundScore (String p){
		if (!RoundScore.containsKey(p))
			RoundScore.put(p, 0);
		
		return RoundScore.get(p);
	}

	public static void resetAllStats(String name) {
		if (Stats.Assists.containsKey(name))
		Stats.Assists.remove(name);
		if (Stats.killStreaks.containsKey(name))
		Stats.killStreaks.remove(name);
		if (Stats.RoundDeaths.containsKey(name))
		Stats.RoundDeaths.remove(name);
		if (Stats.RoundKills.containsKey(name))
		Stats.RoundKills.remove(name);
		if (Stats.TotalFame.containsKey(name))
		Stats.TotalFame.remove(name);
		if (Stats.RoundScore.containsKey(name))
		Stats.RoundScore.remove(name);
		if (Stats.TotalDeaths.containsKey(name))
		Stats.TotalDeaths.remove(name);
		if (Stats.TotalKills.containsKey(name))
		Stats.TotalKills.remove(name);
		if (Stats.TotalScore.containsKey(name))
		Stats.TotalScore.remove(name);
		if (Stats.Tokens.containsKey(name))
		Stats.Tokens.remove(name);
		if (Stats.PlotsOwned.containsKey(name))
		Stats.PlotsOwned.remove(name);
	}
	

	public static int getTokens(String name) {
		if (!Tokens.containsKey(name)){
			Tokens.put(name, Mysql.retrieveTokens(name));
		}
		return Tokens.get(name);
	}

	public static int getFame(String name) {
		if (!TotalFame.containsKey(name)) {
			TotalFame.put(name, Mysql.getHungerGamesFame(name));
		}
			
		return TotalFame.get(name);
	}

	public static int getPlotsOwned(String name) {
		if (!PlotsOwned.containsKey(name))
			PlotsOwned.put(name, Mysql.getPlotsOwned(name));
			
		return PlotsOwned.get(name);
	}

	public static String getFavWeapon(String pname) {
		ResultSet rs = Mysql.PS.getSecureQuery("SELECT *, COUNT(*) AS Weapon FROM `" + Main.Server + "_Kills` WHERE Killer = ? GROUP BY KillerWep ORDER BY Weapon DESC",""+ Mysql.getUserID(pname));
		try {
			if (rs.next())
				return rs.getString("KillerWep");
		} catch (SQLException e) {
			e.printStackTrace();
			} finally {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		return "None";
	}

	public static String getLastKillTime(String pname) {
		ResultSet rs = Mysql.PS.getSecureQuery("SELECT * FROM `" + Main.Server + "_Kills` WHERE Killer = ? ORDER BY ID DESC", ""+Mysql.getUserID(pname));
		try {
			if (rs.next())
				return ConvertTimings.convertTime((int)(System.currentTimeMillis()/1000) - rs.getInt("Time"), true);
		} catch (SQLException e) {
			e.printStackTrace();
			} finally {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		return "N/A";
	}

	public static String getLastKill(String pname) {
		ResultSet rs = Mysql.PS.getSecureQuery("SELECT * FROM `" + Main.Server + "_Kills` WHERE Killer = ? ORDER BY ID DESC", ""+Mysql.getUserID(pname));
		try {
			if (rs.next())
				return Mysql.getUsername(rs.getInt("Victim"));
		} catch (SQLException e) {e.printStackTrace();}
		return "N/A";
	}

	public static void addScore(String p) {
		
		int amount = getKillstreak(p);
		if (getKillstreak(p) <= 4)
			amount = getKillstreak(p);
		else if (getKillstreak(p) == 5)
			amount = 6;
		else if (getKillstreak(p) >= 6)
			amount = (getKillstreak(p) * 4) - 16;
		
		if (PlayersInfo.getPlayer(p) != null) {
			Player pl = PlayersInfo.getPlayer(p);
			if (pl.hasPermission(Message.Perm1X5Fame)) {
				amount = (int) Math.round(amount * 1.5);
			} else if (pl.hasPermission(Message.PermDoubleFame)) {
				amount = amount * 2;
			}
		}
		
		Message.P(PlayersInfo.getPlayer(p), Message.Replacer(Message.EarnedFame, "" + amount, "%fame"), false);
		TotalFame.put(p, getFame(p) + amount);
	}
	
	
	
	
}
