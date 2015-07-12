package ca.PvPCraft.PvPAPI.delayedTasks;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;

import ca.PvPCraft.PvPAPI.Main;
import ca.PvPCraft.PvPAPI.enums.LengthType;
import ca.PvPCraft.PvPAPI.methods.Locations;
import ca.PvPCraft.PvPAPI.methods.PlayersInfo;
import ca.PvPCraft.PvPAPI.methods.Scoreboards;
import ca.PvPCraft.PvPAPI.utilities.Message;
import ca.PvPCraft.PvPAPI.utilities.Mysql;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import ru.tehkode.permissions.bukkit.PermissionsEx;




public class joinDelay extends BukkitRunnable {
	Player p;
	public joinDelay(Player player) {
		p = player;
	}

	@Override
	public void run() {

		if (Main.enableJoinPotionEffects){
			for (PotionEffect pe : Main.PotionEffects){
				p.addPotionEffect(pe);
			}
		}

		if (Main.UseMySQL){
			PlayersInfo.startTimes.put(p.getUniqueId(), System.currentTimeMillis()/1000);

			PermissionsEx.getPermissionManager().clearUserCache(p);
			if (!PermissionsEx.getUser(p).inGroup("Voter")){
				if (Mysql.getVotes(p.getName(), LengthType.days) >= 3){
					PermissionsEx.getUser(p).addGroup("Voter", null, 86400);
				}
			}


			ResultSet feedbacksReturn = Mysql.PS.getSecureQuery("SELECT * FROM Feedback WHERE User = ? AND Server = ? AND Reply IS NOT NULL AND ReplyRead = 0", ""+Mysql.getUserID(p.getName()), Main.Server);
			try {
				if (feedbacksReturn.next()){
					do {
						Message.P(p, Message.Replacer(Message.Replacer(Message.FeedBackReplied, feedbacksReturn.getString("Reply"), "%reply"), feedbacksReturn.getString("Feedback"), "%inquiry"), true);
						Mysql.PS.getSecureQuery("UPDATE Feedback SET ReplyRead = 1 WHERE ID = ?", ""+feedbacksReturn.getInt("ID"));
					} while (feedbacksReturn.next());
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				try {
					feedbacksReturn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			Integer lastWeek = (int) (System.currentTimeMillis()/1000) - 604800;
			ResultSet playTime = Mysql.PS.getSecureQuery("SELECT *, SUM(End - Start) AS totalTime FROM `gameSessions` WHERE Server != 'Hub' AND User = ? AND Start >= ? GROUP BY User", ""+Mysql.getUserID(p.getName()), ""+lastWeek);
			try {
				if (playTime.next()){
					if (playTime.getInt("totalTime") >= 50000){
						if (!PermissionsEx.getUser(p).inGroup("Addict")){
							PermissionsEx.getPermissionManager().clearUserCache(p);
							if (!PermissionsEx.getUser(p).inGroup("Addict")){
								PermissionsEx.getUser(p).addGroup("Addict", null, 86400);
							}
						}
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				try {
					playTime.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (!Main.motdList.isEmpty()){
				for (String line : Main.motdList){
					Message.filterMessage(p, line, false);
				}
			}

			if (Main.TutorialEnabled){
				if (!Mysql.isCompletedTutorial(p.getName())){
					Message.P(p, Message.MustCompleteTutorial, true);
					PlayersInfo.MustCompleteTutorial.add(p.getUniqueId());
					p.teleport(Locations.tutorialLoc);
				}
			}
			if (Main.DailyRoll){
				if (Mysql.checkCanRoll(p))
					Message.P(p, Message.YouCanRollDice, true);
			}

			PlayersInfo.updateNickname(p);
		}
		/*
		if (Main.scoreBoardEnabled)
		for (final Player p : Bukkit.getOnlinePlayers()){
			Scoreboards.updateScoreboardEntries(p, Main.scoreBoardTitles, null);
		}
		 */
	}
}
