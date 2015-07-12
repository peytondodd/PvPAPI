package ca.PvPCraft.PvPAPI.enums;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import ca.PvPCraft.PvPAPI.Main;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayOutScoreboardDisplayObjective;
import net.minecraft.server.v1_8_R3.PacketPlayOutScoreboardObjective;
import net.minecraft.server.v1_8_R3.PacketPlayOutScoreboardScore;
import net.minecraft.server.v1_8_R3.Scoreboard;
import net.minecraft.server.v1_8_R3.ScoreboardBaseCriteria;
import net.minecraft.server.v1_8_R3.ScoreboardScore;


public class Scoreboard_v1_7_R4 {
	Main plugin;
	//public static HashMap<String, Scoreboard> scoreboards2 = new HashMap<String, Scoreboard>();
	public static ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>> scoreboards2 = new ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>>();
	public static Scoreboard sb = new Scoreboard();
	//public static PacketSbManager sb = null;
	public Scoreboard_v1_7_R4(Main mainclass) {
		plugin = mainclass;
		//sb = new PacketSbManager(plugin);
	}


	public static void update(final Player p, HashMap<String, Integer> scores, String title) {
		//sb.sendUpdate(p, scores, title);
		String scoreboard = "sharedBoard";
		
		if (!Main.preexists.contains(p.getName())){

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
		        sb.registerObjective(scoreboard, new ScoreboardBaseCriteria("dummy"));
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
					((CraftPlayer) p).getHandle().getScoreboard().resetPlayerScores(score.getPlayerName(), null);
					PacketPlayOutScoreboardScore pScoreItem2 = new PacketPlayOutScoreboardScore(score);//Destroy score packet 2
					sendPacket(p, pScoreItem2);//Send score update packet
					scoreboards2.get(p.getName()).remove(score.getPlayerName());
				}
				if (scores.containsKey(score.getPlayerName())){
					score.setScore(scores.get(score.getPlayerName()));
					PacketPlayOutScoreboardScore pScoreItem2 = new PacketPlayOutScoreboardScore(score);//Create score packet 2
					sendPacket(p, pScoreItem2);//Send score update packet
					scoreboards2.get(p.getName()).put(score.getPlayerName(), score.getScore());
				}
			}

			for (Entry<String, Integer> entry : scores.entrySet()){
				// If score is in the new list but not the old list.
				if (!scoreboards2.get(p.getName()).containsKey(entry.getKey())){
					ScoreboardScore score = sb.getPlayerScoreForObjective(entry.getKey(), sb.getObjective(scoreboard));//Create a new item
					score.setScore(entry.getValue());//Set it's value to 12
					PacketPlayOutScoreboardScore pScoreItem2 = new PacketPlayOutScoreboardScore(score);//Create score packet 2
					sendPacket(p, pScoreItem2);//Send score update packet
					scoreboards2.get(p.getName()).put(entry.getKey(), entry.getValue());
				}
			}
		}
		/*
		freeMem = freeMem - Runtime.getRuntime().freeMemory();
		if (freeMem != 0)
			Message.C("Free Mem from Sector 2: " + freeMem + "\n");
			*/
	}

	public static void sendPacket(Player player, Packet packet) {
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
	}
}