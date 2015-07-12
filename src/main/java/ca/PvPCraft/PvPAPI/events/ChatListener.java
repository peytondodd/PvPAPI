package ca.PvPCraft.PvPAPI.events;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import ca.PvPCraft.PvPAPI.Main;
import ca.PvPCraft.PvPAPI.methods.BanManage;
import ca.PvPCraft.PvPAPI.methods.ConvertTimings;
import ca.PvPCraft.PvPAPI.methods.FactionsHook;
import ca.PvPCraft.PvPAPI.methods.PlayersInfo;
import ca.PvPCraft.PvPAPI.repeatingTasks.Announcements;
import ca.PvPCraft.PvPAPI.utilities.Files;
import ca.PvPCraft.PvPAPI.utilities.Message;
import ca.PvPCraft.PvPAPI.utilities.Mysql;
import ca.PvPCraft.PvPAPI.utilities.statHooks.HungerGames;
import ca.PvPCraft.PvPAPI.utilities.statHooks.MedievalWars;
import ca.PvPCraft.PvPAPI.utilities.statHooks.StatsGZ;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class ChatListener implements Listener{
	public static Main plugin;
	static Logger log = Bukkit.getLogger();

	public static ConcurrentHashMap<String, String> formats = new ConcurrentHashMap<String, String>();
	public static ConcurrentHashMap<String, ArrayList<String>> teams = new ConcurrentHashMap<String, ArrayList<String>>();
	public static ConcurrentHashMap<String, ArrayList<String>> targets = new ConcurrentHashMap<String, ArrayList<String>>();
	public static String defaultFormat = "%faction%prefix%name &8>%suffix %msg";

	public ChatListener (Main mainclass){
		plugin = mainclass;
		mainclass.getServer().getPluginManager().registerEvents(this, mainclass);	
	}


	@EventHandler
	public void OnChatEvent (AsyncPlayerChatEvent e) throws SQLException{
		if (!e.isCancelled() && !PlayersInfo.Ghosts.contains(e.getPlayer().getName())){

			Player p = e.getPlayer();

			if ((Main.BanManageEnable && BanManage.isMuted(e.getPlayer())))
				e.setCancelled(true);

			if (Announcements.MessagesModify.containsKey(p.getName())){
				if (Announcements.MessagesModify.get(p.getName()).equalsIgnoreCase("add")){
					if (e.getMessage().equalsIgnoreCase("cancel")){
						Message.P(p, Message.Cancelled, true);
					}
					else{
						Main.Announcements.add(e.getMessage());
						Files.config.getCustomConfig().set("Announcements.Messages", Main.Announcements);
						Files.config.saveCustomConfig();
						String msg = e.getMessage();
						Message.P(p, Message.Replacer(Message.AnnouncementAdded, Message.Colorize(msg), "%msg"), true);
					}
					Announcements.MessagesModify.remove(p.getName());
					e.setCancelled(true);
				}
				else if (Announcements.MessagesModify.get(p.getName()).equalsIgnoreCase("addPlot")){
					if (e.getMessage().equalsIgnoreCase("cancel")){
						Message.P(p, Message.Cancelled, true);
					}
					else{
						if (ConvertTimings.isInteger(e.getMessage())){
							ResultSet rsPlots = Mysql.PS.getSecureQuery("SELECT *, COUNT(*) AS PlotCount FROM plotmePlots WHERE LOWER(owner) LIKE LOWER(?)", ""+p.getName());

							if (rsPlots.next()){
								if (rsPlots.getInt("PlotCount")>= Integer.parseInt(e.getMessage()) && Integer.parseInt(e.getMessage()) >= 1){
									ResultSet rsPlots2 = Mysql.PS.getSecureQuery("SELECT * FROM plotmePlots WHERE LOWER(owner) LIKE LOWER(?)", ""+p.getName());
									if (rsPlots2.absolute(Integer.parseInt(e.getMessage()))){
										String ContestName = PlayersInfo.contestRequired.get(p.getName());
										Mysql.PS.getSecureQuery("INSERT INTO ContestEntries (User, Server, ContestName, EntryTime, PlotEntry) VALUES (?,?,?,?,?)", ""+Mysql.getUserID(p.getName()), Main.Server, ContestName, ""+System.currentTimeMillis()/1000, rsPlots2.getString("idX") + ";" + rsPlots2.getString("idZ"));
										Message.P(p, Message.Replacer(Message.JoinedContest, ContestName, "%name"), true);
									}
								}
								else
									Message.P(p, Message.Cancelled, true);
							}
							try {
								rsPlots.close();
							} catch (SQLException e2) {
								e2.printStackTrace();
							}
						}
						else
							Message.P(p, Message.Cancelled, true);
					}
					if (PlayersInfo.contestRequired.containsKey(p.getName()))
						PlayersInfo.contestRequired.remove(p.getName());
					Announcements.MessagesModify.remove(p.getName());
					e.setCancelled(true);
				}
			}
			else {
				String msg = e.getMessage();
				String format = "";
				String team = getTeam(p.getName());
				if (!formats.isEmpty()){
					if (team != null && formats.containsKey(team)){
						format = formats.get(team);
						format = Message.Replacer(format, Message.Colorize(team), "%team");
					}
					else
						format = defaultFormat;
				}
				else
					format = defaultFormat;


				if (Main.permissionsEnabled){
					PermissionUser user = PermissionsEx.getUser(p);
					format = Message.Replacer(format, Message.Colorize(user.getPrefix()), "%prefix");
					format = Message.Replacer(format, Message.Colorize(user.getSuffix()), "%suffix");
				}

				if (Main.FactionsEnabled)
					format = Message.Replacer(format, FactionsHook.getFactionName(p.getName()), "%faction");
				else
					format = Message.Replacer(format, "", "%faction");

				if (Main.Server.equalsIgnoreCase("HungerGames"))
					format = Message.Replacer(format, Message.Colorize(HungerGames.getRankHG(p, true)), "%rank");
				else if (Main.Server.equalsIgnoreCase("Gunz")){
					format = Message.Replacer(format, Message.Colorize(StatsGZ.getRank(p.getName(), false)), "%gunzrank");
					format = Message.Replacer(format, Message.Colorize(StatsGZ.getRank(p.getName(), true)), "%gunzlevel");
				}
				else if (Main.Server.equalsIgnoreCase("MedievalWar"))
					format = Message.Replacer(format, Message.Colorize(MedievalWars.getChatRank(p.getName(), false)), "%rank");
				
				format = Message.Replacer(format, e.getPlayer().getDisplayName(), "%name");


				/*
				 * Message Cleansing... xD
				 */
				if (e.getPlayer().hasPermission("pvp.chat.color"))
					msg = Message.Colorize(msg);
				else if (!Message.Colorize(msg).equalsIgnoreCase(msg))
					Message.P(p, Message.featureEmerald, true);
				msg = msg.replaceAll("%", "%%");
				
				
				format = Message.Replacer(Message.Colorize(format), msg, "%msg", false);

				e.setFormat(format);
				if (!teams.isEmpty()){
					e.getRecipients().clear();
					for (String targetTeams : targets.get(team)){
						for (String playerName : teams.get(targetTeams)){
							Player pl = PlayersInfo.getPlayer(playerName);
							if (pl != null){
								e.getRecipients().add(pl);
							}
							else
								teams.get(team).remove(playerName);
						}
					}
				}
			}
		}
		else
			e.setCancelled(true);
	}


	private String getTeam(String name) {
		for (Entry<String, ArrayList<String>> team : teams.entrySet()){
			if (team.getValue().contains(name))
				return team.getKey();
		}
		return null;
	}
}
