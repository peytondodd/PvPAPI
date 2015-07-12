package ca.PvPCraft.PvPAPI.delayedTasks;

import java.sql.ResultSet;
import java.sql.SQLException;

import ca.PvPCraft.PvPAPI.Main;
import ca.PvPCraft.PvPAPI.methods.PlayersInfo;
import ca.PvPCraft.PvPAPI.utilities.Message;
import ca.PvPCraft.PvPAPI.utilities.Mysql;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;




public class assignNickname extends BukkitRunnable {
	Player p;
	public assignNickname(Player player) {
		p = player;
	}

	@Override
	public void run() {
		ResultSet rs = null;
		try {
			rs = Mysql.PS.getSecureQuery("SELECT * FROM UserInfo WHERE User = ?", ""+Mysql.getUserID(p.getName()));
			if (rs.next()){
				if ((rs.getString("Nickname") != null && rs.getString("Nickname") != "") && rs.getString("Nickname") != p.getName()){
					p.setDisplayName(Main.NickPrefix + Message.Colorize(rs.getString("Nickname")));
					String newName = rs.getString("Nickname");
					String newNameList = newName;
					if (newName.length() >= 16)
						newNameList = newName.substring(0, 15);
					p.setCustomName(newName);
					p.setPlayerListName(Main.NickPrefix + Message.Colorize(newNameList));
					PlayersInfo.nickNames.put(p.getUniqueId(), Main.NickPrefix + newName);

				}
				else{
					p.setDisplayName(p.getName());
					String newName = p.getName();
					String newNameList = newName;
					if (newName.length() >= 16)
						newNameList = newName.substring(0, 15);
					p.setCustomName(newName);
					p.setPlayerListName(Message.Colorize(newNameList));
					PlayersInfo.nickNames.put(p.getUniqueId(), newName);
				}

				int SocialSpy = rs.getInt("SocialSpy");
				if (!PlayersInfo.isSocialSpy(p) && SocialSpy == 1)
					PlayersInfo.SocialSpies.add(p.getUniqueId());
			}
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
	}
}
