package ca.PvPCraft.PvPAPI.methods;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.logging.Logger;

import ca.PvPCraft.PvPAPI.Main;
import ca.PvPCraft.PvPAPI.Commands.PlayerCommands;
import ca.PvPCraft.PvPAPI.delayedTasks.assignNickname;
import ca.PvPCraft.PvPAPI.enums.Challenge;
import ca.PvPCraft.PvPAPI.enums.LengthType;
import ca.PvPCraft.PvPAPI.enums.Reward;
import ca.PvPCraft.PvPAPI.enums.ScoreboardUtility;
import ca.PvPCraft.PvPAPI.enums.Tasks;
import ca.PvPCraft.PvPAPI.methods.serverPinging.hubMethods;
import ca.PvPCraft.PvPAPI.utilities.Message;
import ca.PvPCraft.PvPAPI.utilities.Mysql;
import ca.PvPCraft.PvPAPI.utilities.Stats;
import ca.PvPCraft.PvPAPI.utilities.Fanciful.FancyMessage;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;



public class PlayersInfo {



	public static Main plugin;// We need to refer to the main class, this is just a method class, and inorder to hook into
	// The variables which the plugin has access to, it must grab the value of the plugin from main. We just initialize the variable here. It is equal to null here...
	static Logger log = Bukkit.getLogger();// This line is not needed in most cases, but it is used to print info into the console

	// Here we identify the different lists we will be using...
	// An array list will be a 1 column type of list, and you can't add the same thing twice, only once.
	// A hash map has 2 columns known as    KEY | VALUE
	// For String and String it can be "Undeadkillz" | "Owner" so the key is undeadkillz and value is owner. This can be used to also link to people together.
	/*
	 * I personally prefer using String instead of Player because we want to save memory and if we save the player object we will
	 * get the health, max health, permissions and a whole ton of stuff not wanted for the purpose.
	 * Instead we just save the players Name into the list and if we ever want to get the player's object then we
	 * will use a method here, which will get the users info from online players. If you are certain you will be using an object
	 * after the user leaves, you may save the object, but be sure to always clear it, so that it does not cause memory overflow
	 * If you ahve any questions just ask me...
	 * 
	 */
	public static ArrayList<UUID> SocialSpies = new ArrayList<UUID>();// List of names of players who have social spy enabled
	public static ArrayList<UUID> MustCompleteTutorial = new ArrayList<UUID>();// List of players who must complete tutorial, also used to avoid Database queries
	public static HashMap<UUID, String> editingAdmin = new HashMap<UUID, String>();// List of players editing, just ignore...
	public static HashMap<String, String> contestRequired = new HashMap<String, String>();// Ignore this...
	public static HashMap<String, Integer> delayedTeleports = new HashMap<String, Integer>();// The time a person has left to tp list. We can't make 1 global variable
	// Because we have to account for people who want to tpa at the same time, we want to assign them different timers.
	public static HashMap<String, UUID> PlayerUUIDs = new HashMap<String, UUID>();// We save players ID's instead of query, and we only query if the person we want is not there.
	public static HashMap<String, Integer> PlayerIDs = new HashMap<String, Integer>();// We save players ID's instead of query, and we only query if the person we want is not there.
	public static HashMap<UUID, String> nickNames = new HashMap<UUID, String>();// We save nicknames locally instead of doing a database query which may cause lag.
	public static HashMap<String, String> tpaRequestTo = new HashMap<String, String>();// Key = Person being requested and Value is the requester
	public static HashMap<UUID, ArrayList<Integer>> ignoredPlayers = new HashMap<UUID, ArrayList<Integer>>();// Key = Person being requested and Value is the requester
	public static HashMap<String, String> LastMessaged = new HashMap<String, String>();
	public static ArrayList<String> Ghosts = new ArrayList<String>();// Ghost modes list
	public static HashMap<UUID, Long> startTimes = new HashMap<UUID, Long>();// We save the player start session times, to have them recorded later.
	public static HashMap<UUID, HashMap<String, String>> recordedExtraStat = new HashMap<UUID, HashMap<String, String>>();// We save the player start session times, to have them recorded later.
	public static HashMap<String, HashMap<LengthType, Integer>> votesCompleted = new HashMap<String, HashMap<LengthType, Integer>>();// Ignore this...
	public static HashMap<String, ArrayList<Integer>> challengesCompleted = new HashMap<String, ArrayList<Integer>>();// Ignore this...

	public PlayersInfo (Main mainclass){
		plugin = mainclass;// We get the call of (this) from main into here, and set plugin to the main plugin class, which has the variables we need to use server api.
	}


	public static void ClearFully(Player p) {// We want a method which will take in a player, and do the following...
		p.getInventory().clear();// We clear the player's inventory here
		p.getInventory().setHelmet(null);// Remove item | it is not part of the inventory so we have to do a seperate call.
		p.getInventory().setChestplate(null);// Remove item | it is not part of the inventory so we have to do a seperate call.
		p.getInventory().setLeggings(null);// Remove item | it is not part of the inventory so we have to do a seperate call.
		p.getInventory().setBoots(null);// Remove item | it is not part of the inventory so we have to do a seperate call.
	}



	// Ignore this | It is too complicated.

	// We just run a check on the array to see if a player must complete the tutorial or not.
	// The majority of the players will most likely not be in the list, so we choose the smaller pupolation to fill the list
	public static boolean notCompletedTutorial(String p){
		return MustCompleteTutorial.contains(p);// Also used to avoid using queries, more than once.
	}

	public static Player getPlayer(String name) {
		Player target = null;

		// We do a plugin call and check to see if anyone is found with the specific name player object we want.
		if (plugin.getServer().matchPlayer(name).size() >= 1)
			target = plugin.getServer().matchPlayer(name).get(0);
		return target;
	}

	// We check if the player is a social spy.
	public static boolean isSocialSpy(Player p){
		return SocialSpies.contains(p.getName());
	}


	// If the player left the server, we want to make sure to get thier info deleted else, we will just keep stacking huge lists of players who are not online.
	public static void leftServer(final Player p) {
		plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable(){
			@Override
			public void run(){
				if (startTimes.containsKey(p.getUniqueId())){
					Mysql.addGameSession(p.getName(), startTimes.get(p.getUniqueId()));
					startTimes.remove(p.getUniqueId());
				}

				// The check is done on every list we have above.
				if (SocialSpies.contains(p.getUniqueId()))// We must do a check before removing else the method will fail.
					SocialSpies.remove(p.getUniqueId());
				if (MustCompleteTutorial.contains(p.getUniqueId()))
					MustCompleteTutorial.remove(p.getUniqueId());
				// A neat feature I like in Java is that if you have one statement to run when an if statement is met, you don't need to put the {} brackets.
				// Example without brackets 
				if (PlayersInfo.contestRequired.containsKey(p.getName()))
					PlayersInfo.contestRequired.remove(p.getName());
				// Example with brackets
				if (PlayersInfo.ignoredPlayers.containsKey(p.getUniqueId()))
					PlayersInfo.ignoredPlayers.remove(p.getUniqueId());
				if (PlayersInfo.editingAdmin.containsKey(p.getUniqueId()))
					PlayersInfo.editingAdmin.remove(p.getUniqueId());
				if (PlayersInfo.PlayerIDs.containsKey(p.getName()))
					PlayersInfo.PlayerIDs.remove(p.getName());
				if (PlayersInfo.PlayerUUIDs.containsKey(p.getName()))
					PlayersInfo.PlayerUUIDs.remove(p.getName());
				if (PlayersInfo.nickNames.containsKey(p.getUniqueId()))
					PlayersInfo.nickNames.remove(p.getUniqueId());
				if (PlayersInfo.tpaRequestTo.containsKey(p.getName()))
					PlayersInfo.tpaRequestTo.remove(p.getName());
				if (PlayersInfo.LastMessaged.containsKey(p.getName()))
					PlayersInfo.LastMessaged.remove(p.getName());
				if (PlayersInfo.delayedTeleports.containsKey(p.getName()))
					PlayersInfo.delayedTeleports.remove(p.getName());

				if (hubMethods.doNotShow.contains(p.getName()))
					hubMethods.doNotShow.remove(p.getName());
				if (Main.preexists.contains(p.getName()))
					Main.preexists.remove(p.getName());
				if (Ghosts.contains(p.getName()))
					Ghosts.remove(p.getName());
				if (PlayerCommands.MENUS.containsKey(p.getName()))
					PlayerCommands.MENUS.remove(p.getName());

				ScoreboardUtility.remove(p);
				if (Scoreboards.locationScoreboard.containsKey(p.getName()))
					Scoreboards.locationScoreboard.remove(p.getName());
				if (Scoreboards.updateDeny.containsKey(p.getName()))
					Scoreboards.updateDeny.remove(p.getName());
				if (Scoreboards.updateDeny2.containsKey(p.getName()))
					Scoreboards.updateDeny2.remove(p.getName());
				if (votesCompleted.containsKey(p.getName()))
					votesCompleted.remove(p.getName());
				if (challengesCompleted.containsKey(p.getName()))
					challengesCompleted.remove(p.getName());
				Stats.resetAllStats(p.getName());
			}
		});
	}

	// Ignore this for now, I will teach you this later...
	public static void changeNickname(Player p, String newName) {
		String moddedName = Main.NickPrefix + newName;
		if (newName != null){
			moddedName = Main.NickPrefix + newName;
			Message.P(p, Message.Replacer(Message.NameChanged, Message.Colorize(moddedName), "%name"), true);
			PlayersInfo.nickNames.put(p.getUniqueId(), moddedName);
		}
		else{
			moddedName = p.getName();
			newName = p.getName();
			Message.P(p, Message.Replacer(Message.NameChanged, Message.Colorize(p.getName()), "%name"), true);
			if (PlayersInfo.nickNames.containsKey(p.getUniqueId()))
				PlayersInfo.nickNames.remove(p.getUniqueId());
		}

		p.setDisplayName(Message.Colorize(moddedName));
		String newNameList = Message.Colorize(moddedName);

		if (moddedName.length() >= 16)
			newNameList = newName.substring(0, 15);

		p.setCustomName(moddedName);
		p.setPlayerListName(Message.Colorize(newNameList));

		ResultSet rs = Mysql.PS.getSecureQuery("UPDATE UserInfo SET Nickname = ? WHERE User = ?", newName, ""+Mysql.getUserID(p.getName()));
		try {
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	// Also ignore this...



	public static String getNickname(Player p){
		if (nickNames.containsKey(p.getUniqueId())){
			return nickNames.get(p.getUniqueId());
		}
		else{
			ResultSet rs = Mysql.PS.getSecureQuery("SELECT * FROM UserInfo WHERE User = ?", ""+Mysql.getUserID(p.getName()));
			try {
				if (rs.next()){
					if ((rs.getString("Nickname") != null && rs.getString("Nickname") != "") && rs.getString("Nickname") != p.getName()){
						return Main.NickPrefix + Message.Colorize(rs.getString("Nickname"));
					}
					else
						return p.getName();
				}
				else
					return p.getName();
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


		return p.getName();
	}
	public static void updateNickname(final Player p) {
		new assignNickname(p).runTaskLaterAsynchronously(plugin, 1 * 10L);
	}


	public static void EliminatedUser(Player dam, Player def) throws SQLException {
		if (Main.UseMySQL && Main.KillStatsEnable){
			ResultSet tableLookup = Mysql.PS.getSecureQuery("SELECT * FROM information_schema.TABLES WHERE TABLE_SCHEMA = '" + Mysql.getDatabaseName() + "' AND TABLE_NAME = '" + Main.Server + "_Kills'");
			try {
				if (tableLookup.next() == false)
					Mysql.PS.getSecureQuery("CREATE TABLE `" + Main.Server + "_Kills` ( " +
							"ID INT AUTO_INCREMENT, " +    
							"Killer INT NULL, " + 
							"Victim INT NULL, " +
							"Reason INT NULL, " +
							"KillerWep VARCHAR (50) NULL, " +
							"DefenderWep VARCHAR (50) NULL, " +
							"Time INT NOT NULL, " +
							"PRIMARY KEY ( ID ))");
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				try {
					tableLookup.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			if (def != null){
				if (recordedExtraStat.containsKey(def.getUniqueId())){
					if (recordedExtraStat.get(def.getUniqueId()).size() >= 1){
						for (Entry<String, String> entry : recordedExtraStat.get(def.getUniqueId()).entrySet()){
							String valType = entry.getValue().split("/")[0];
							ResultSet columnLookup = Mysql.PS.getSecureQuery("SELECT * FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = '" + Mysql.getDatabaseName() + "' AND TABLE_NAME = '" + Main.Server + "_Kills' AND COLUMN_NAME = '" + entry.getKey() + "'");
							if (columnLookup.next() == false){
								Mysql.PS.getSecureQuery("ALTER TABLE `" + Main.Server + "_Kills` ADD `" + entry.getKey() + "` " + valType + " NULL");
							}
						}
					}
				}
			}
			else
				Message.C("Defender is not found...");

			Long Time = System.currentTimeMillis()/1000;
			int DeathCode = 0;
			String DamWep = null;
			String DefWep = null;

			if (dam == null && def.getLastDamageCause() != null)
				DeathCode = Mysql.DeathCode(def.getLastDamageCause().getCause());
			else if (dam != null && dam.isOnline()){
				DamWep = Message.CleanCapitalize(dam.getItemInHand().getType().name());
				if (Main.useWeaponName){
					if (dam.getItemInHand().hasItemMeta()){
						if (dam.getItemInHand().getItemMeta().hasDisplayName()){
							DamWep = Message.CleanCapitalize(dam.getItemInHand().getItemMeta().getDisplayName());
						}
					}
				}
			}
			if (def != null && def.isOnline()){
				DefWep = Message.CleanCapitalize(def.getItemInHand().getType().name());
				if (Main.useWeaponName){
					if (def.getItemInHand().hasItemMeta()){
						if (def.getItemInHand().getItemMeta().hasDisplayName()){
							DefWep = Message.CleanCapitalize(def.getItemInHand().getItemMeta().getDisplayName());
						}
					}
				}
			}
			int defID = 0;
			int damID = 0;
			if (def != null){
				defID = Mysql.getUserID(def.getName());
				Stats.earnedADeath(def);
			}
			if (dam != null){
				damID = Mysql.getUserID(dam.getName());
				Stats.earnedAKill(dam);
			}

			Mysql.PS.getSecureQuery("INSERT INTO `" + Main.Server + "_Kills` (Killer, Victim, Reason, KillerWep, DefenderWep, Time) VALUES (?, ?, ?, ?, ?, ?)", ""+damID, ""+defID,
					""+DeathCode, ""+DamWep, ""+DefWep, ""+Time);
			if (def != null){
				if (recordedExtraStat.containsKey(def.getUniqueId())){
					if (recordedExtraStat.get(def.getUniqueId()).size() >= 1){
						for (Entry<String, String> entry : recordedExtraStat.get(def.getUniqueId()).entrySet()){
							String val = entry.getValue().split("/")[1];
							Mysql.PS.getSecureQuery("UPDATE `" + Main.Server + "_Kills` SET " + entry.getKey() + " = ? WHERE Killer = ? AND Victim = ? AND Time = ?", ""+val, ""+damID, ""+defID, ""+Time);
						}
						recordedExtraStat.get(def.getUniqueId()).clear();
					}
				}
			}
			/*
			if (Main.scoreBoardEnabled)
			for (final Player p : Bukkit.getOnlinePlayers()){
				Scoreboards.updateScoreboardEntries(p, Main.scoreBoardTitles, null);
			}
			 */
		}
	}


	public static void sendMessage(Player sender, Player recp, String message) {
		if (recp != null && recp != sender){
			String attached = "";
			if (!Mysql.getPlayerIgnores(Mysql.getUserID(recp.getName())).contains(Mysql.getUserID(sender.getName()))){
				Message.P(sender,Message.Replacer(Message.Replacer(Message.SentTo, recp.getName(), "%target"), message, "%msg"), false);
				Message.P(recp,Message.Replacer(Message.Replacer(Message.SentFrom, sender.getName(), "%sender"), message, "%msg"), false);
				LastMessaged.put(sender.getName(), recp.getName());
				LastMessaged.put(recp.getName(), sender.getName());
			}
			else{
				attached = "&cBLOCKED&7 ";
				Message.P(sender, Message.Replacer(Message.BlockedFromFriends, recp.getName(), "%recp"), true);
			}

			for (UUID socialspies : PlayersInfo.SocialSpies){
				if (recp.getUniqueId() != socialspies && sender.getUniqueId() != socialspies){
					Player socialspy = Bukkit.getPlayer(socialspies);
					if (socialspy != null)
						Message.P(socialspy, attached + Message.Replacer(Message.Replacer(Message.Replacer(Message.SocialSpyMessage, recp.getName(), "%target"), sender.getName(), "%sender"), message, "%msg"), true);
				}
			}


		}
		else
			Message.P(sender, Message.PlayerNotOnline, true);
	}


	public static void toggleGhostMode(Player p) {
		if (Ghosts.contains(p.getName())){
			// Disable Ghostmode
			Ghosts.remove(p.getName());
			for (Player pl : plugin.getServer().getOnlinePlayers())
				pl.showPlayer(p);
			p.removePotionEffect(PotionEffectType.INVISIBILITY);
			p.removePotionEffect(PotionEffectType.NIGHT_VISION);
			p.setAllowFlight(false);
			p.setFlying(false);
			updateNickname(p);
			p.setCustomNameVisible(true);
			Message.P(p, Message.Replacer(Message.GhostModeToggled, "disabled", "%mode"), true);
		}
		else{
			//Enable Ghostmode
			Ghosts.add(p.getName());
			for (Player pl : plugin.getServer().getOnlinePlayers())
				pl.hidePlayer(p);
			p.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 1000000, 2));
			p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 1000000, 2));
			p.setAllowFlight(true);
			p.setDisplayName("");
			p.setCustomNameVisible(false);
			p.setPlayerListName("");
			Message.P(p, Message.Replacer(Message.GhostModeToggled, "enabled", "%mode"), true);
			Message.P(p, Message.GhostModePrecautions, true);

		}
	}

	public static boolean checkChallenges(String name) {
		if (!PlayersInfo.challengesCompleted.containsKey(name)){
			ResultSet rs = Mysql.PS.getSecureQuery("SELECT * FROM `" + Main.Server + "_Challenges` WHERE UserID = ?", ""+Mysql.getUserID(name));
			try {
				if (!PlayersInfo.challengesCompleted.containsKey(name))
					PlayersInfo.challengesCompleted.put(name, new ArrayList<Integer>());
				while (rs.next()){
					PlayersInfo.challengesCompleted.get(name).add(rs.getInt("Challenge_ID"));
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				if (rs != null)
					try {
						rs.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
			}
		}

		int rewardID = 0;
		Player p = PlayersInfo.getPlayer(name);

		for (Entry<Integer, Challenge> challenge : Main.challenges.entrySet()){
			Challenge c = challenge.getValue();
			rewardID = challenge.getKey();
			if (!PlayersInfo.challengesCompleted.get(name).contains(challenge.getKey())){
				Boolean incomplete = false;
				for (Tasks task : c.getChallenges()){
					if (task.getTaskName().equalsIgnoreCase("Total Kills")){
						if (Stats.getTotalKills(name) < task.getCount()){
							incomplete = true;
							break;
						}
					}
					if (Main.Server.equalsIgnoreCase("Faction")){
						if (task.getTaskName().equalsIgnoreCase("Faction Players Online")){
							if (FactionsHook.getFactionPlayer(p).getFaction().getOnlinePlayers().size() < task.getCount()){
								incomplete = true;
								break;
							}
							else if (ChatColor.stripColor(FactionsHook.getFactionPlayer(p).getFaction().getName()).toLowerCase().contains("wilderness") == true){
								incomplete = true;
								break;
							}
						}
						else if (task.getTaskName().equalsIgnoreCase("Personal Power")){
							if (FactionsHook.getFactionPlayer(p).getPower() != FactionsHook.getFactionPlayer(p).getPowerMax()){
								incomplete = true;
								break;
							}
							else if (ChatColor.stripColor(FactionsHook.getFactionPlayer(p).getFaction().getName()).toLowerCase().contains("wilderness") == true){
								incomplete = true;
								break;
							}
						}
						else if (task.getTaskName().equalsIgnoreCase("Faction Power")){
							if (FactionsHook.getFactionPlayer(p).getFaction().getPower() != FactionsHook.getFactionPlayer(p).getFaction().getPowerMax()){
								incomplete = true;
								break;
							}
							else if (ChatColor.stripColor(FactionsHook.getFactionPlayer(p).getFaction().getName()).toLowerCase().contains("wilderness") == true){
								incomplete = true;
								break;
							}
						}
						else if (task.getTaskName().equalsIgnoreCase("Faction Land")){
							if (FactionsHook.getFactionPlayer(p).getFaction().getLeader() != null && !name.equalsIgnoreCase(FactionsHook.getFactionPlayer(p).getFaction().getLeader().getName())){
								incomplete = true;
								break;
							}
							else if (ChatColor.stripColor(FactionsHook.getFactionPlayer(p).getFaction().getName()).toLowerCase().contains("wilderness") == true){
								incomplete = true;
								break;
							}
							else if (FactionsHook.getFactionPlayer(p).getFaction().getLandCount() < task.getCount()){
								incomplete = true;
								break;
							}
						}
						else if (task.getTaskName().equalsIgnoreCase("Enter Enemy Land")){
							if (!FactionsHook.getFactionPlayer(p).isInEnemyTerritory()){
								incomplete = true;
								break;
							}
							else if (ChatColor.stripColor(FactionsHook.getFactionPlayer(p).getFaction().getName()).toLowerCase().contains("wilderness") == true){
								incomplete = true;
								break;
							}
						}
						else if (task.getTaskName().equalsIgnoreCase("Custom Title")){
							if (!FactionsHook.getFactionPlayer(p).hasTitle()){
								incomplete = true;
								break;
							}
						}
					}
				}
				if (incomplete == false){
					StringBuilder rewards = new StringBuilder();
					for (Reward reward : c.getRewards()){
						if (reward.getRewardName().equalsIgnoreCase("Money")){
							if (Main.EconomyEnable){
								EconSystem.modifyMoney(name, reward.getCount(), false);
								rewards.append("$" + reward.getCount());
							}
						}
						else if (reward.getRewardName().equalsIgnoreCase("Power Boost")){
							FactionsHook.getFactionPlayer(p).setPowerBoost(FactionsHook.getFactionPlayer(p).getPowerBoost() + reward.getCount());
							if (rewards.toString().isEmpty())
								rewards.append("Power Boost of " + reward.getCount());
							else
								rewards.append(", Power Boost of " + reward.getCount());
						}
						else if (reward.getRewardName().equalsIgnoreCase("Faction Power Boost")){
							FactionsHook.getFactionPlayer(p).getFaction().setPowerBoost(FactionsHook.getFactionPlayer(p).getFaction().getPowerBoost() + reward.getCount());
							if (rewards.toString().isEmpty())
								rewards.append("Faction Power Boost of " + reward.getCount());
							else
								rewards.append(", Faction Power Boost of " + reward.getCount());
						}
					}
					Message.P(PlayersInfo.getPlayer(name), "&6&lYou have completed a challenge!", true);
					new FancyMessage("&6&l" + c.getName() + " challenge completed.").tooltip("&7Click to display more info.").command("/challenge " + challenge.getKey()).send(p);
					Message.P(PlayersInfo.getPlayer(name), "&aRewards:&7 " + rewards.toString() + ".", true);
					PlayersInfo.challengesCompleted.get(name).add(rewardID);
					Mysql.PS.getSecureQuery("INSERT INTO `" + Main.Server + "_Challenges` (`UserID`, `Challenge_ID`, `TimeComplete`) VALUES (?, ?, ?)", ""+Mysql.getUserID(name), ""+rewardID, ""+(System.currentTimeMillis()/1000));
				}
			}
		}
		return true;
	}


}
