package ca.PvPCraft.PvPAPI.enums;

import java.util.HashMap;

import ca.PvPCraft.PvPAPI.Main;

import org.bukkit.entity.Player;


public class ScoreboardUtility {
	Main plugin;

	public ScoreboardUtility(Main mainclass) {
		plugin = mainclass;
	}


	public static void updateScoreboard(final Player p, HashMap<String, Integer> scores, String title) {
		if (Main.serverVer.equalsIgnoreCase("1.7.10-R0.1-SNAPSHOT")){
			Scoreboard_v1_7_R4.update(p, scores, title);
		}
	}
	
	
	public static void updateUndername(final Player p, String text, Integer value) {
		if (Main.serverVer.equalsIgnoreCase("1.7.9-R0.3-SNAPSHOT")){
			
		}
		else if (Main.serverVer.equalsIgnoreCase("1.7.10-R0.1-SNAPSHOT")){
			DisplayName_v1_7_R4.update(p, text, value);
		}
	}

	public static void remove(Player p) {
		if (Main.serverVer.equalsIgnoreCase("1.7.10-R0.1-SNAPSHOT")){
		      DisplayName_v1_7_R4.remove(p);
		      Scoreboard_v1_7_R4.scoreboards2.remove(p.getName());
		      DisplayName_v1_7_R4.scoreboards2.remove(p.getName());
		      DisplayName_v1_7_R4.scoreboards3.remove(p.getName());
		}
	}
}