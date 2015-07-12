package ca.PvPCraft.PvPAPI.enums;
import java.util.concurrent.ConcurrentHashMap;

import ca.PvPCraft.PvPAPI.Main;
import ca.PvPCraft.PvPAPI.utilities.Message;

import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.Scoreboard;


public class DisplayName_v1_7_R4 {
	Main plugin;
	static Scoreboard sb = new Scoreboard();
	//public static HashMap<String, Scoreboard> scoreboards2 = new HashMap<String, Scoreboard>();
	public static ConcurrentHashMap<String, Integer> scoreboards2 = new ConcurrentHashMap<String, Integer>();
	public static ConcurrentHashMap<String, String> scoreboards3 = new ConcurrentHashMap<String, String>();

	public DisplayName_v1_7_R4(Main mainclass) {
		plugin = mainclass;
	}

	public static void remove(Player p){
		if (sb != null){
			if (sb.getObjective("U-" + p.getName()) != null){
				sb.unregisterObjective(sb.getObjective("U-" + p.getName()));
			}
		}
	}

	public static void update(final Player p, String text, Integer value) {

		//if (((CraftPlayer) p).getHandle().getScoreboard() == null){
		//sb = new Scoreboard();
		//}
		//else{
		//	sb = ((CraftPlayer) p).getHandle().getScoreboard();
		//}
		/*


		if (Main.preexists.contains(p.getName())){
			//PacketPlayOutScoreboardObjective packet = new PacketPlayOutScoreboardObjective(sb.getObjective(scoreboard), 1);//Create Scoreboard create packet
			//sendPacket(p, packet);//Send Scoreboard create packet
			//Main.preexists.remove(p.getName());
		}

		if (sb.getObjective(scoreboard) == null && !scoreboards2.containsKey(p.getName())){
			//Message.C("2 - Register objective & send packet");
			sb.registerObjective(scoreboard, new ScoreboardBaseCriteria("dummy"));//Create new objective in the scoreboard
			scoreboards2.put(p.getName(), value);
			scoreboards3.put(p.getName(), text);

			PacketPlayOutScoreboardObjective packet = new PacketPlayOutScoreboardObjective(sb.getObjective(scoreboard), 0);//Create Scoreboard create packet
			PacketPlayOutScoreboardDisplayObjective display = new PacketPlayOutScoreboardDisplayObjective(2, sb.getObjective(scoreboard));//Create display packet set to sidebar mode
			sendPacket(p, packet);//Send Scoreboard create packet
			sendPacket(p, display);//Send the display packet
		}
		else if (sb.getObjective(scoreboard) != null && !scoreboards2.containsKey(p.getName())){
			//Message.C("2 - Unregister & then Register objective & send packet");
			//sb.unregisterObjective(sb.getObjective(scoreboard));
			//sb.registerObjective(scoreboard, new ScoreboardBaseCriteria("dummy"));//Create new objective in the scoreboard
			scoreboards2.put(p.getName(), value);
			scoreboards3.put(p.getName(), text);

			PacketPlayOutScoreboardObjective packet = new PacketPlayOutScoreboardObjective(sb.getObjective(scoreboard), 0);//Create Scoreboard create packet
			PacketPlayOutScoreboardDisplayObjective display = new PacketPlayOutScoreboardDisplayObjective(2, sb.getObjective(scoreboard));//Create display packet set to sidebar mode
			sendPacket(p, packet);//Send Scoreboard create packet
			sendPacket(p, display);//Send the display packet
		}
		scoreboards2.put(p.getName(), value);
		scoreboards3.put(p.getName(), text);


		for(Player p1 : Bukkit.getOnlinePlayers()){
			if (scoreboards3.containsKey(p1.getName())){
				for(Player p2 : Bukkit.getOnlinePlayers()){
					if (scoreboards2.containsKey(p1.getName()) && !p1.getName().equalsIgnoreCase(p2.getName())){
						sb.getObjective("U-" + p2.getName()).setDisplayName(scoreboards3.get(p1.getName()));
						ScoreboardScore health = sb.getPlayerScoreForObjective(p1.getName(), sb.getObjective("U-" + p2.getName()));//Create a new item with the players name
						health.setScore(scoreboards2.get(p1.getName()));//this will set the integer under to the player's name to their health.
						PacketPlayOutScoreboardScore pHealth = new PacketPlayOutScoreboardScore(health, 0);//Create score packet 1
						sendPacket(p2, pHealth);//Send score update packet
					}
				}
			}
		}


		/*
		if (!scoreboards2.get(p.getName()).containsKey(text) || scoreboards2.get(p.getName()).get(text) != value){
			if (!scoreboards2.get(p.getName()).containsKey(text)){
				/*
				for (String older : scoreboards2.get(p.getName()).keySet()){
					sb.resetPlayerScores(older);
					PacketPlayOutScoreboardScore pScoreItem2 = new PacketPlayOutScoreboardScore(sb.getPlayerScoreForObjective(older, sb.getObjective(scoreboard)), 1);//Destroy score packet 2
					sendPacket(p, pScoreItem2);//Send score update packet
					scoreboards2.get(p.getName()).remove(older);
				}

			}


			scoreboards2.get(p.getName()).put(text, value);
		}
		ScoreboardScore scoreItem1 = sb.getPlayerScoreForObjective(p.getName(), sb.getObjective(scoreboard));//Create a new item with the players name
		scoreItem1.setScore(value);//this will set the integer under to the player's name, who ran the command to 42
		PacketPlayOutScoreboardScore pScoreItem2 = new PacketPlayOutScoreboardScore(scoreItem1, 0);//Create score packet 2
		sendPacket(p, pScoreItem2);//Send score update packet
		 */
	}

	public static void sendPacket(Player player, Packet packet) {
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
	}
}

/*
package ca.PvPCraft.PvPAPI.enums;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;










import net.minecraft.server.v1_7_R4.Packet;
import net.minecraft.server.v1_7_R4.PacketPlayOutScoreboardDisplayObjective;
import net.minecraft.server.v1_7_R4.PacketPlayOutScoreboardObjective;
import net.minecraft.server.v1_7_R4.PacketPlayOutScoreboardScore;
import net.minecraft.server.v1_7_R4.Scoreboard;
import net.minecraft.server.v1_7_R4.ScoreboardBaseCriteria;
import net.minecraft.server.v1_7_R4.ScoreboardScore;
import ca.PvPCraft.PvPAPI.Main;
import ca.PvPCraft.PvPAPI.utilities.Message;


public class Scoreboard_v1_7_R4 {
	Main plugin;
	//public static HashMap<String, Scoreboard> scoreboards2 = new HashMap<String, Scoreboard>();
	public static ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>> scoreboards2 = new ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>>();
	public Scoreboard_v1_7_R4(Main mainclass) {
		plugin = mainclass;
	}

	public static void remove(String pName){
		Player p = Bukkit.getPlayer(pName);
		Scoreboard sb = null;


		if (sb == null){
			Message.C("Scoreboard is null");
			if (((CraftPlayer) p).getHandle().getScoreboard() == null){
				sb = new Scoreboard();
				Message.C("1 - We generated a scoreboard");
			}
			else{
				Message.C("1 - We used old scoreboard");
				sb = ((CraftPlayer) p).getHandle().getScoreboard();
			}
		}


		if (sb.getObjective(pName) != null){
			for (Object scoreItem : sb.getScoresForObjective(sb.getObjective(pName))){
				ScoreboardScore score = (ScoreboardScore) scoreItem;
				sb.resetPlayerScores(score.getPlayerName());
			}
			sb.unregisterObjective(sb.getObjective(pName));
		}
	}

	public static void update(final Player p, HashMap<String, Integer> scores, String title) {
		String scoreboard = p.getName();
		//if (!Main.preexists.contains(p.getName())){
		Scoreboard sb = null;
		//Message.C("Debug 1");
		if (sb == null){
			Message.C("Scoreboard is null");
			if (((CraftPlayer) p).getHandle().getScoreboard() == null){
				sb = new Scoreboard();
				Message.C("1 - We generated a scoreboard");
			}
			else{
				Message.C("1 - We used old scoreboard");
				sb = ((CraftPlayer) p).getHandle().getScoreboard();
			}
		}

		if (Main.preexists.contains(p.getName())){
			PacketPlayOutScoreboardObjective packet = new PacketPlayOutScoreboardObjective(sb.getObjective(scoreboard), 1);//Create Scoreboard create packet
			sendPacket(p, packet);//Send Scoreboard create packet
			Main.preexists.remove(p.getName());
		}
		else if (((CraftPlayer) p).getHandle().getScoreboard().getObjective(scoreboard) != null){
			PacketPlayOutScoreboardObjective packet = new PacketPlayOutScoreboardObjective(sb.getObjective(scoreboard), 1);//Create Scoreboard create packet
			sendPacket(p, packet);//Send Scoreboard create packet


			sb.registerObjective(scoreboard, new ScoreboardBaseCriteria("dummy"));//Create new objective in the scoreboard
			PacketPlayOutScoreboardObjective packet2 = new PacketPlayOutScoreboardObjective(sb.getObjective(scoreboard), 0);//Create Scoreboard create packet
			sendPacket(p, packet2);//Send Scoreboard create packet
		}


		if (sb.getObjective(scoreboard) == null && !scoreboards2.containsKey(p.getName())){
			//Message.C("2 - Register objective & send packet");
			sb.registerObjective(scoreboard, new ScoreboardBaseCriteria("dummy"));//Create new objective in the scoreboard
			sb.getObjective(scoreboard).setDisplayName(title);
			PacketPlayOutScoreboardObjective packet = new PacketPlayOutScoreboardObjective(sb.getObjective(scoreboard), 0);//Create Scoreboard create packet
			PacketPlayOutScoreboardDisplayObjective display = new PacketPlayOutScoreboardDisplayObjective(1, sb.getObjective(scoreboard));//Create display packet set to sidebar mode
			sendPacket(p, packet);//Send Scoreboard create packet
			sendPacket(p, display);//Send the display packet
			scoreboards2.put(p.getName(), new ConcurrentHashMap<String, Integer>());
		}
		else if (sb.getObjective(scoreboard) != null && !scoreboards2.containsKey(p.getName())){
			//Message.C("2 - Unregister & then Register objective & send packet");

			sb.unregisterObjective(sb.getObjective(scoreboard));

			sb.registerObjective(scoreboard, new ScoreboardBaseCriteria("dummy"));//Create new objective in the scoreboard
			sb.getObjective(scoreboard).setDisplayName(title);
			PacketPlayOutScoreboardObjective packet2 = new PacketPlayOutScoreboardObjective(sb.getObjective(scoreboard), 0);//Create Scoreboard create packet
			PacketPlayOutScoreboardDisplayObjective display2 = new PacketPlayOutScoreboardDisplayObjective(1, sb.getObjective(scoreboard));//Create display packet set to sidebar mode
			sendPacket(p, packet2);//Send Scoreboard create packet
			sendPacket(p, display2);//Send the display packet
			scoreboards2.put(p.getName(), new ConcurrentHashMap<String, Integer>());

		}
		else if (!scoreboards2.containsKey(p.getName())){
			//Message.C("2 - Just need to add to list??");
			scoreboards2.put(p.getName(), new ConcurrentHashMap<String, Integer>());
		}
		// Update scoreboard based on displayname...
		if (sb.getObjective(scoreboard).getDisplayName() != title){
			sb.getObjective(scoreboard).setDisplayName(title);
			PacketPlayOutScoreboardObjective packet = new PacketPlayOutScoreboardObjective(sb.getObjective(scoreboard), 2);//Update scoreboard packet
			sendPacket(p, packet);//Send update packet
		}

		for (Object scoreItem : sb.getScoresForObjective(sb.getObjective(scoreboard))){
			ScoreboardScore score = (ScoreboardScore) scoreItem;
			if (!scores.containsKey(score.getPlayerName())){
				((CraftPlayer) p).getHandle().getScoreboard().resetPlayerScores(score.getPlayerName());
				PacketPlayOutScoreboardScore pScoreItem2 = new PacketPlayOutScoreboardScore(score, 1);//Destroy score packet 2
				sendPacket(p, pScoreItem2);//Send score update packet
				scoreboards2.get(p.getName()).remove(score.getPlayerName());
			}
			if (scores.containsKey(score.getPlayerName())){
				if (score.getScore() != scores.get(score.getPlayerName())){
					score.setScore(scores.get(score.getPlayerName()));
					PacketPlayOutScoreboardScore pScoreItem2 = new PacketPlayOutScoreboardScore(score, 0);//Create score packet 2
					sendPacket(p, pScoreItem2);//Send score update packet
					scoreboards2.get(p.getName()).put(score.getPlayerName(), score.getScore());
				}
			}
		}

		for (Entry<String, Integer> entry : scores.entrySet()){
			// If score is in the new list but not the old list.
			if (!scoreboards2.get(p.getName()).containsKey(entry.getKey())){
				ScoreboardScore score = sb.getPlayerScoreForObjective(entry.getKey(), sb.getObjective(scoreboard));//Create a new item
				score.setScore(entry.getValue());//Set it's value to 12
				PacketPlayOutScoreboardScore pScoreItem2 = new PacketPlayOutScoreboardScore(score, 0);//Create score packet 2
				sendPacket(p, pScoreItem2);//Send score update packet
				scoreboards2.get(p.getName()).put(entry.getKey(), entry.getValue());
			}
		}
		//}
	}

	public static void sendPacket(Player player, Packet packet) {
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
	}
}
 */