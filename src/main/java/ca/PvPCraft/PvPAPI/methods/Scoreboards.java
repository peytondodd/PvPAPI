package ca.PvPCraft.PvPAPI.methods;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import ca.PvPCraft.PvPAPI.Main;
import ca.PvPCraft.PvPAPI.enums.LengthType;
import ca.PvPCraft.PvPAPI.enums.ScoreboardUtility;
import ca.PvPCraft.PvPAPI.utilities.Message;
import ca.PvPCraft.PvPAPI.utilities.Mysql;
import ca.PvPCraft.PvPAPI.utilities.Stats;
import ca.PvPCraft.PvPAPI.utilities.TextScroller;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import ru.tehkode.permissions.bukkit.PermissionsEx;


public class Scoreboards{

	public static HashMap<String, HashMap<Integer, TextScroller>> locationScoreboard = new HashMap<String, HashMap<Integer, TextScroller>>();
	public static HashMap<String, ArrayList<String>> updateDeny = new HashMap<String, ArrayList<String>>();
	public static HashMap<String, String> updateDeny2 = new HashMap<String, String>();
	public static Main plugin;

	public Scoreboards(Main mainclass) {
		plugin = mainclass;
	}

	public static void updateBelowName(Player p, String message, Integer value){
		String var = message;
		String pName = p.getName();

		var = Message.Replacer(var, pName+"", "name", true);
		if (Main.UseMySQL){
			if (Main.KillStatsEnable){
				var = Message.Replacer(var, Stats.getTotalKills(pName)+"", "%totalkills", true);
				var = Message.Replacer(var, Stats.getTotalDeaths(pName)+"", "%totaldeaths", true);
				var = Message.Replacer(var, Stats.getKills(pName)+"", "%kills", true);
				var = Message.Replacer(var, Stats.getDeaths(pName)+"", "%deaths", true);
			}
			var = Message.Replacer(var, Stats.getTokens(pName)+"", "%tokens", true);
			var = Message.Replacer(var, Mysql.getUserID(pName)+"", "%playerid", true);
			var = Message.Replacer(var, Mysql.getVotes(pName, LengthType.months)+"", "%monthlyvotes", true);
			var = Message.Replacer(var, Mysql.getVotes(pName, LengthType.years)+"", "%totalvotes", true);
			var = Message.Replacer(var, Mysql.getVotes(pName, LengthType.days)+"", "%votes", true);
			
			var = Message.Replacer(var, Bukkit.getOnlinePlayers().size()+"", "%players", true);
		}
		if (Main.EconomyEnable)
			var = Message.Replacer(var, (int)EconSystem.getMoney(pName)+"", "%money", true);
		if (Main.permissionsEnabled)
			var = Message.Replacer(var, Message.CleanCapitalize(PermissionsEx.getUser(pName).getPrefix().replace("*", ""))+"", "%rank");
		if (!Main.ServerList.isEmpty() && Main.SignsUpdatingTask != null){
			for (Entry<String, Integer> server : Main.list.entrySet()){
				var = Message.Replacer(var, ""+server.getValue(), "%"+server.getKey(), true);
			}
		}
		
		ScoreboardUtility.updateUndername(p, message, value);
	}

	public static void setDelayFix(final Player p, Integer length, ArrayList<String> scoreboardList, String title){
		if (!updateDeny.containsKey(p.getName())){
			updateDeny.put(p.getName(), scoreboardList);
			updateDeny2.put(p.getName(), title);
			plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable(){
				@Override
				public void run(){
					updateDeny.remove(p.getName());
					updateDeny2.remove(p.getName());
				}
			}, 20 * length);
		}
	}
	public static void updateScoreboardEntries(Player p, List<String> scoreBoardTitles, String scoreTitle){// Here we throw in the name of the player we want to update & the list of the scoreboard that should be shown to him
		String pName = p.getName();
		long freeMem = Runtime.getRuntime().freeMemory();
		if (updateDeny.containsKey(pName)){
			scoreBoardTitles = updateDeny.get(pName);
			scoreTitle = updateDeny2.get(p.getName());
		}

		if (scoreBoardTitles == null)
			scoreBoardTitles = Main.scoreBoardTitles;

		int countNum = 0;
		if (scoreTitle == null)
			scoreTitle = Main.scoreboardTitle;

		String titleScoreboard = scoreTitle;
		
		if (titleScoreboard.length() > 32){
			if (!locationScoreboard.containsKey(p.getName()))
				locationScoreboard.put(p.getName(), new HashMap<Integer, TextScroller>());
			if (!locationScoreboard.get(p.getName()).containsKey(200)){
				locationScoreboard.get(p.getName()).put(200, new TextScroller(titleScoreboard, 30, 3, '&'));
				if (titleScoreboard.length() > 32)
					titleScoreboard = titleScoreboard.substring(0,31);
			}
			else if (!locationScoreboard.get(p.getName()).get(200).getFull().equalsIgnoreCase(titleScoreboard)){
				locationScoreboard.get(p.getName()).put(200, new TextScroller(titleScoreboard, 30, 3, '&'));
				if (titleScoreboard.length() > 32)
					titleScoreboard = titleScoreboard.substring(0,31);
			}
			else
				titleScoreboard = locationScoreboard.get(p.getName()).get(200).next();
		}
		else
			titleScoreboard = Message.Colorize(titleScoreboard);
		
		/*
		freeMem = freeMem - Runtime.getRuntime().freeMemory();
		if (freeMem != 0)
			Message.C("Free Mem from Sector 3: " + freeMem + "\n");
		freeMem = Runtime.getRuntime().freeMemory();
		*/
		
		HashMap<String, Integer> newList = new HashMap<String, Integer>();
		int totalKills = 0;
		int totalDeaths = 0;
		int kills = 0;
		int deaths = 0;
		int tokens = 0;
		int id = 0;
		int online = 0;
		int money = 0;
		int killstreak = 0;
		int dailyVote = 0;
		int monthlyVote = 0;
		int yearlyVote = 0;
		
		String group = "";
		
		if (Main.UseMySQL){

			if (Main.KillStatsEnable){
				totalKills = Stats.getTotalKills(pName);
				totalDeaths = Stats.getTotalDeaths(pName);
				kills = Stats.getKills(pName);
				deaths = Stats.getDeaths(pName);
				killstreak = Stats.getKillstreak(pName);
			}
			tokens = Stats.getTokens(pName);
			id = Mysql.getUserID(pName);
			online = Bukkit.getOnlinePlayers().size();
			dailyVote = Mysql.getVotes(pName, LengthType.days);
			monthlyVote = Mysql.getVotes(pName, LengthType.months);
			yearlyVote = Mysql.getVotes(pName, LengthType.years);
		}
		
		if (Main.EconomyEnable)
			money = (int)EconSystem.getMoney(pName);
		if (Main.permissionsEnabled)
			group =  Message.CleanCapitalize(PermissionsEx.getUser(pName).getPrefix().replace("*", ""));
		
		
		for (String var : scoreBoardTitles){
			var = Message.Replacer(var, pName+"", "%name");
			if (Main.UseMySQL){
				if (Main.KillStatsEnable){
					var = Message.Replacer(var, totalKills+"", "%totalkills");
					var = Message.Replacer(var, totalDeaths+"", "%totaldeaths");
					var = Message.Replacer(var, killstreak+"", "%killstreak");
					var = Message.Replacer(var, kills+"", "%kills");
					var = Message.Replacer(var, deaths+"", "%deaths");
				}
				var = Message.Replacer(var, tokens+"", "%tokens");
				var = Message.Replacer(var, id+"", "%playerid");
				var = Message.Replacer(var, online+"", "%players");
				var = Message.Replacer(var, monthlyVote+"", "%monthlyvotes", true);
				var = Message.Replacer(var, yearlyVote+"", "%totalvotes", true);
				var = Message.Replacer(var, dailyVote+"", "%votes", true);
			}
			if (Main.EconomyEnable)
				var = Message.Replacer(var, money+"", "%money");
			if (Main.permissionsEnabled)
				var = Message.Replacer(var, group, "%rank");
			if (!Main.ServerList.isEmpty() && Main.SignsUpdatingTask != null){
				for (Entry<String, Integer> server : Main.list.entrySet()){
					var = Message.Replacer(var, ""+server.getValue(), "%"+server.getKey());
				}
			}
			String[] titleProps = var.split("//");
			int result = 0;
			String title = titleProps[0];
			String typeData = titleProps[1];
			if (ConvertTimings.isInteger(typeData))
				result = Integer.parseInt(typeData);
			else
				continue;

			if (title.length() > 16){
				
				if (!locationScoreboard.containsKey(p.getName()))
					locationScoreboard.put(p.getName(), new HashMap<Integer, TextScroller>());

				if (!locationScoreboard.get(p.getName()).containsKey(countNum)){
					locationScoreboard.get(p.getName()).put(countNum, new TextScroller(title, 16, 3, '&'));
					title = title.substring(0,15);
				}
				else{
					title = locationScoreboard.get(p.getName()).get(countNum).next();
				}
			}
			newList.put(Message.Colorize(title), result);
			var = null;
			title = null;
		}

		
		if (titleScoreboard != null)
			titleScoreboard = Message.Colorize(titleScoreboard);
		ScoreboardUtility.updateScoreboard(p, newList, titleScoreboard);
		newList.clear();
		/*
		freeMem = freeMem - Runtime.getRuntime().freeMemory();
		if (freeMem != 0)
			Message.C("Free Mem from Sector 1: " + freeMem + "\n");
		 */
	}

}
