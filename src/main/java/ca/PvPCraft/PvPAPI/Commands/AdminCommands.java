package ca.PvPCraft.PvPAPI.Commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.Map.Entry;

import ca.PvPCraft.PvPAPI.Main;
import ca.PvPCraft.PvPAPI.methods.*;
import ca.PvPCraft.PvPAPI.methods.serverPinging.ServerSignManagement;
import ca.PvPCraft.PvPAPI.repeatingTasks.Announcements;
import ca.PvPCraft.PvPAPI.utilities.Files;
import ca.PvPCraft.PvPAPI.utilities.Message;
import ca.PvPCraft.PvPAPI.utilities.Mysql;
import ca.PvPCraft.PvPAPI.utilities.UUIDFetcher;
import ca.PvPCraft.PvPAPI.utilities.Fanciful.FancyMessage;

public class AdminCommands implements CommandExecutor{
	public Main plugin;

	public AdminCommands(Main main) {
		plugin = main;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, final String[] args) {
		if (cmd.getName().equalsIgnoreCase("socialspy")) {
			if (sender instanceof Player) {
				Player p = (Player) sender;
				if (p.hasPermission("pvp.socialspy") || p.isOp()) {
					if (PlayersInfo.isSocialSpy(p)) {
						PlayersInfo.SocialSpies.remove(p.getName());
						Mysql.PS.getSecureQuery("UPDATE UserInfo SET SocialSpy = 0 WHERE User = ?", ""+Mysql.getUserID(p.getName()));
						Message.P(p, Message.Replacer(Message.SocialSpyToggled, "&cdisabled", "%toggle"), true);
					} else {
						PlayersInfo.SocialSpies.add(p.getUniqueId());
						Mysql.PS.getSecureQuery("UPDATE UserInfo SET SocialSpy = 1 WHERE User = ?", ""+Mysql.getUserID(p.getName()));
						Message.P(p, Message.Replacer(Message.SocialSpyToggled, "&aenabled", "%toggle"), true);
					}
				} else {
					Message.P(p, Message.NoPermission, true);
				}
			} else {
				Message.C(Message.NotHumanlyPossible);
			}

			return true;
		} else if (cmd.getName().equalsIgnoreCase("convertids")) {
			if (sender instanceof Player) {
				final Player p = (Player) sender;
				if (p.hasPermission("pvp.*") || (p.hasPermission("pvp.admin") || p.isOp())) {

					if (args[0].equalsIgnoreCase("files")){
						new BukkitRunnable() {
							@Override
							public void run() {

								Path path = Paths.get(plugin.getDataFolder() + "/files");   
								if (java.nio.file.Files.exists(path, LinkOption.NOFOLLOW_LINKS)){

									Message.P(p, Message.StartedUUIDConvert, true);


									HashMap<String, File> list = new HashMap<String, File>();
									for (File fileName : Files.listf(plugin.getDataFolder() + "/files")){
										String playerName = fileName.getName().replace(".dat", "");
										list.put(playerName, fileName);
									}

									for (Entry<String, File> playerInfo : list.entrySet()){
										String val = Files.getUUID(playerInfo.getKey());
										if (val != null)
											playerInfo.getValue().renameTo(new File(plugin.getDataFolder() + "/files/" + val + ".dat"));

									}



								}

							}
						}.runTaskAsynchronously(plugin);
					}
					else{
						new BukkitRunnable() {
							@Override
							public void run() {
								Message.P(p, Message.StartedUUIDConvert, true);
								ResultSet rs = Mysql.PS.getSecureQuery("SELECT * FROM Users WHERE UUID = 'null' OR UUID IS NULL ORDER BY ID ASC");
								try {
									Map<String, Integer> rows = new HashMap<String, Integer>();
									List<String> usernames = new ArrayList<String>(rows.keySet());

									while (rs.next()) {
										rows.put(rs.getString("Username"), rs.getInt("ID"));
									}

									UUIDFetcher fetcher = new UUIDFetcher(usernames);
									Map<String, UUID> response = null;
									try {
										response = fetcher.call();
									} catch (Exception e) {
										e.printStackTrace();
									}

									for (String name : response.keySet()) {
										Mysql.PS.getSecureQuery("UPDATE Users SET UUID = ? WHERE ID = ?", response.get(name).toString(), rows.get(name).toString());
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
						}.runTaskAsynchronously(plugin);
					}
				}
			} else {
				Message.C(Message.NotHumanlyPossible);
			}
			return true;
		} else if (cmd.getName().equalsIgnoreCase("signedit")) {
			if (sender instanceof Player) {
				Player p = (Player) sender;
				if (p.hasPermission("pvp.*") || p.hasPermission("pvp.signedit") || (p.hasPermission("pvp.admin") || p.isOp())) {
					if (args.length >= 2) {

						StringBuilder sb = new StringBuilder();
						for (int x = 1; x <= args.length - 1; x++){
							sb.append(args[x]);
							sb.append(" ");
						}

						if (ServerSignManagement.blockIsSign(p.getTargetBlock((Set<Material>) null, 50))) {
							if (Message.Colorize(sb.toString()).length() >= 18) {
								Message.P(p, Message.MessageTooLong, true);
							} else {
								if (ServerSignManagement.blockIsSign(p.getTargetBlock((Set<Material>) null, 50))) {
									Sign sign = (Sign) p.getTargetBlock((Set<Material>) null, 50).getState();
									int choiceLine = Integer.parseInt(args[0]) - 1;
									if (choiceLine >= 0 && choiceLine <= 3){
										sign.setLine(choiceLine, Message.Colorize(sb.toString()));
										sign.update();
									}
									else
										Message.P(p, Message.ChooseAValidLineNumber, false);
								}
							}
						} else {
							Message.P(p, Message.LookAtsign, true);
						}
					} else if (args.length == 1) {
						Message.P(p, Message.SignEditCommand, true);
					} else {
						Message.P(p, Message.SignEditCommand, true);
					}
				}
			} else {
				Message.C(Message.NotHumanlyPossible);
			}
			return true;
		} else if (cmd.getName().equalsIgnoreCase("ghostmode")) {
			if (sender instanceof Player) {
				Player p = (Player) sender;
				if (p.hasPermission("pvp.*") || (p.hasPermission("pvp.admin") || p.isOp())) {
					Player target = null;
					if (args.length >= 1) {
						target = PlayersInfo.getPlayer(args[0]);
					}
					if (target != null)
						p.teleport(target);
					PlayersInfo.toggleGhostMode(p);
				}
			} else {
				Message.C(Message.NotHumanlyPossible);
			}
			return true;
		} else if (cmd.getName().equalsIgnoreCase("invsee")) {
			if (sender instanceof Player) {
				Player p = (Player) sender;
				if (p.hasPermission("pvp.*") || (p.hasPermission("pvp.admin") || (p.hasPermission("pvp.invsee")) || p.isOp())) {
					if (args.length >= 1) {
						Player target = PlayersInfo.getPlayer(args[0]);
						if (target != null) {
							p.openInventory(target.getInventory());
						} else {
							Message.P(p, Message.PlayerNotOnline, true);
						}
					} else {
						Message.P(p, Message.CommandInvSeeHelp, true);
					}
				}
			} else {
				Message.C(Message.NotHumanlyPossible);
			}
			return true;
		} else if (cmd.getName().equalsIgnoreCase("say")) {
			if (sender instanceof Player) {
				Player p = (Player) sender;
				if (p.hasPermission("pvp.*") || (p.hasPermission("pvp.admin") || p.isOp())) {
					if (args.length == 0) {
						p.sendMessage(ChatColor.RED + "/say <Message>");
					} else {
						StringBuilder msg = new StringBuilder();
						for (String word : args) {
							msg.append(word);
							msg.append(" ");
						}

						Message.G(ChatColor.translateAlternateColorCodes('&', msg.toString()), true);
					}
				}
			} else {
				if (args.length == 0) {
					System.out.println(ChatColor.RED + "/say <Message>");
				} else {
					StringBuilder msg = new StringBuilder();
					for (String word : args) {
						msg.append(word);
						msg.append(" ");
					}

					Message.G(ChatColor.translateAlternateColorCodes('&', msg.toString()), true);
					plugin.getServer().broadcastMessage(ChatColor.GRAY + "[" + ChatColor.RED + "PKz" + ChatColor.GRAY + "] " + ChatColor.GOLD + msg.toString());
				}
			}
			return true;
		}  else if (cmd.getName().equalsIgnoreCase("am")) {
			if (sender instanceof Player) {
				Player p = (Player) sender;
				if (p.hasPermission("pvp.automessages") || p.isOp()) {
					if (args.length == 0) {
						Message.PrintHelpMenu(p, "am");
					} else if (args.length >= 1) {
						if (args[0].equalsIgnoreCase("add")) {
							Announcements.MessagesModify.put(p.getName(), "add");
							Message.P(p, Message.TypeOutAnnouncement, true);
						} else if (args[0].equalsIgnoreCase("remove")) {
							if (args.length == 1) {
								// Display announcements..
								Message.P(p, Message.HeaderMenu, false);
								for (int x = 0; x < Main.Announcements.size(); x++) {
									Message.P(p, Message.Replacer(Message.Replacer(Message.AnnouncementOutput, "" + (x + 1), "%num"), Main.Announcements.get(x), "%msg"), false);
								}
								Message.P(p, Message.MenuAM2, false);
							} else {
								// Delete announcement
								if (Main.Announcements.size() + 1 >= Integer.parseInt(args[1]) && 1 >= Integer.parseInt(args[1])) {
									Message.P(p, Message.Replacer(Message.AnnouncementDeleted, Main.Announcements.get(Integer.parseInt(args[1]) - 1), "%msg"), false);
									Main.Announcements.remove(Integer.parseInt(args[1]) - 1);
									Files.config.getCustomConfig().set("Announcements.Messages", Main.Announcements);
									Files.config.saveCustomConfig();
								} else {
									Message.P(p, Message.CountIsTooHigh, true);
								}
							}
						} else if (args[0].equalsIgnoreCase("setdelay")) {
							if (args.length == 1) {
								Message.P(p, Message.MenuAM3, true);
							} else {
								if (ConvertTimings.isInteger(args[1])) {
									Main.DefaultDelay = Integer.parseInt(args[1]);
									Files.config.getCustomConfig().set("Announcements.DefaultDelay", Main.DefaultDelay);
									Files.config.saveCustomConfig();
									Main.refreshConfigEntries(plugin);
								} else {
									Message.P(p, Message.MehNeedNumba, true);
								}
							}
						} else if (args[0].equalsIgnoreCase("reload")) {
							Locations.RefreshConfig(Files.config);
							Main.refreshConfigEntries(plugin);
						}
					}
				} else {
					Message.P(p, Message.NoPermission, true);
				}
			} else {
				Message.C(Message.NotHumanlyPossible);
			}
			return true;
		} else if (cmd.getName().equalsIgnoreCase("tm")) {
			if (sender instanceof Player) {
				Player p = (Player) sender;
				if (p.hasPermission("pvp.tutorial.setup") || p.isOp()) {
					if (args.length >= 1) {
						if (args[0].equalsIgnoreCase("set")) {
							if (args.length >= 2) {
								if (args[1].equalsIgnoreCase("start")) {
									Locations.SaveLocation(Files.teleports, p.getLocation(), "Tutorial.Start");
									Message.P(p, Message.SavedTutorialStart, true);
								} else if (args[1].equalsIgnoreCase("end")) {
									PlayersInfo.editingAdmin.put(p.getUniqueId(), "tutorialEnd");
									Message.P(p, Message.TutorialEndSetup, true);
								} else {
									Message.P(p, Message.MenuTM1, true);
								}
							} else {
								Message.P(p, Message.MenuTM1, true);
							}
						} else if (args[0].equalsIgnoreCase("forceComplete")) {
							if (args.length >= 2) {
								Mysql.completedTutorial(args[1]);
							} else {
								Message.P(p, Message.MenuTM2, true);
							}
						} else if (args[0].equalsIgnoreCase("forceReset")) {
							if (args.length >= 2) {
								Mysql.resetTutorial(args[1]);
							} else {
								Message.P(p, Message.MenuTM3, true);
							}
						}
					} else {
						Message.PrintHelpMenu(p, "tm");
					}
				} else {
					Message.P(p, Message.NoPermission, true);
				}
			} else {
				Message.C(Message.NotHumanlyPossible);
			}
			return true;
		} else if (cmd.getName().equalsIgnoreCase("gm")) {
			if (sender instanceof Player) {
				Player p = (Player) sender;
				if (p.hasPermission("pvp.gamemode") || p.isOp()) {
					Player target = p;
					if (args.length >= 1) {
						if (args.length >= 2) {
							target = PlayersInfo.getPlayer(args[1]);
						}

						if (target != null) {
							if (ConvertTimings.isInteger(args[0])) {
								int num = Integer.parseInt(args[0]);
								GameMode gm = GameMode.getByValue(num);

								if (target != p) {
									Message.P(p, Message.Replacer(Message.Replacer(Message.ChangedGamemode, target.getName(), "%p"), Message.CleanCapitalize(gm.toString()), "%gm"), true);
								}
								target.setGameMode(gm);
								Message.P(target, Message.Replacer(Message.Replacer(Message.ChangedGamemode, target.getName(), "%p"), Message.CleanCapitalize(gm.toString()), "%gm"), true);
							} else {
								Message.P(p, Message.MehNeedNumba, true);
							}
						} else {
							Message.P(p, Message.PlayerNotOnline, true);
						}
					} else {
						Message.P(p, Message.GameModeHelp, true);
					}
				} else {
					Message.P(p, Message.NoPermission, true);
				}
			} else {
				if (args.length >= 2) {
					Player target = PlayersInfo.getPlayer(args[1]);
					if (target != null) {
						if (ConvertTimings.isInteger(args[0])) {
							int num = Integer.parseInt(args[0]);
							GameMode gm = GameMode.getByValue(num);
							target.setGameMode(gm);
							Message.P(target, Message.Replacer(Message.Replacer(Message.ChangedGamemode, target.getName(), "%p"), Message.CleanCapitalize(gm.toString()), "%gm"), true);
						} else {
							Message.C(Message.MehNeedNumba);
						}
					} else {
						Message.C(Message.PlayerNotOnline);
					}
				} else {
					Message.C(Message.GameModeHelp);
				}
			}
			return true;
		} else if (cmd.getName().equalsIgnoreCase("setworldspawn")) {
			if (sender instanceof Player) {
				Player p = (Player) sender;
				if (p.hasPermission("pvp.setworldspawn") || p.isOp()) {
					p.getWorld().setSpawnLocation(p.getLocation().getBlockX(), p.getLocation().getBlockY(), p.getLocation().getBlockZ());
					Message.P(p, Message.WorldSpawnSet, true);
				} else {
					Message.P(p, Message.NoPermission, true);
				}
			} else {
				Message.C(Message.NotHumanlyPossible);
			}
			return true;
		}  else if (cmd.getName().equalsIgnoreCase("feedbacks")) {
			if (sender instanceof Player) {
				Player p = (Player) sender;
				if (p.hasPermission("pvp.feedback.read") || p.isOp()) {
					/*
					 * Feedback listing and replying.
					 */
					if (args.length >= 1){
						if (args[0].equalsIgnoreCase("list")){
							String limit = "10";
							if (args.length == 2){
								// Player wants a specific page?
								if (ConvertTimings.isInteger(args[1])){
									int numLimit = Integer.parseInt(args[1]);
									limit = (10 * numLimit) + "," + (10 * (numLimit + 1));
								}
								else{
									Message.P(p, Message.MehNeedNumba, true);
									return true;
								}
							}

							ResultSet rs = Mysql.PS.getSecureQuery("SELECT * FROM Feedback WHERE Reply IS NULL ORDER BY ID DESC LIMIT " + limit);
							try {
								while (rs.next()){
									new FancyMessage("&7" + rs.getString("ID") + " - " + rs.getString("Feedback")).tooltip("&7Posted in &b" + rs.getString("Server")  + "&7, " + ConvertTimings.getTime((int)(System.currentTimeMillis()/1000) - rs.getInt("Time")) + " ago by &a" + Mysql.getUsername(rs.getInt("User"))).suggest("/feedbacks reply " + rs.getString("ID")).send(p);
								}
							} catch (SQLException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						else if (args[0].equalsIgnoreCase("replied")){
							String limit = "10";
							if (args.length == 2){
								// Player wants a specific page?
								if (ConvertTimings.isInteger(args[1])){
									int numLimit = Integer.parseInt(args[1]);
									limit = (10 * numLimit) + "," + (10 * (numLimit + 1));
								}
								else{
									Message.P(p, Message.MehNeedNumba, true);
									return true;
								}
							}

							ResultSet rs = Mysql.PS.getSecureQuery("SELECT * FROM Feedback WHERE Reply IS NOT NULL ORDER BY ID DESC LIMIT " + limit);
							try {
								while (rs.next()){
									String admin = "Console";
									if (rs.getString("RepliedBy") != null){
										admin = Mysql.getUsername(rs.getInt("RepliedBy"));
									} 


									String reply = rs.getString("Reply");



									int replyTime = 100000000;
									if (rs.getString("ReplyTime") != null)
										replyTime = (int)(System.currentTimeMillis()/1000) - rs.getInt("ReplyTime");
									new FancyMessage("&7" + rs.getString("ID") + " - " + rs.getString("Feedback")).tooltip("&7Posted in &b" + rs.getString("Server")  + "&7, " + ConvertTimings.getTime((int)(System.currentTimeMillis()/1000) - rs.getInt("Time")) + " ago by &a" + Mysql.getUsername(rs.getInt("User"))).then(" &7|| &bReply Info &7||").tooltip("&7Replied by " + admin + "'" + reply + "'" + " " + ConvertTimings.convertTime(replyTime, true) + " ago.").send(p);
								}
							} catch (SQLException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						else if (args[0].equalsIgnoreCase("reply")){
							if (args.length >= 2 && ConvertTimings.isInteger(args[1])){
								ResultSet rs = Mysql.PS.getSecureQuery("SELECT * FROM Feedback WHERE ID = ? AND Reply IS NULL LIMIT 1", args[1]);
								try {
									if (rs.next()){
										String message = "";
										for (int x = 2; x <= args.length - 1; x++){
											message += args[x] + " ";
										}
										if (!message.isEmpty()){
											Mysql.PS.getSecureQuery("UPDATE Feedback SET Reply = ?, RepliedBy = ?, ReplyTime = ? WHERE ID = ?", message, ""+Mysql.getUserID(p.getName()), ""+(System.currentTimeMillis()/1000), args[1]);
											Message.P(p, "Replied to feedback. Thanks for helping out!", true);
										}
									}
									else
										Message.P(p, "Message does not exist or was already replied to.", true);
								} catch (SQLException e) {
									e.printStackTrace();
								}
							}
							else
								Message.P(p, "/Feedbacks reply <MessageID>", true);
						}
					}
					else{
						Message.PrintHelpMenu(p, "feedbacks");
					}
				} else {
					Message.P(p, Message.NoPermission, true);
				}
			} else 
				Message.C(Message.NotHumanlyPossible);
			return true;
		} else if (cmd.getName().equalsIgnoreCase("tpall")) {
			if (sender instanceof Player) {
				Player p = (Player) sender;
				if (p.hasPermission("pvp.tpall") || p.isOp()) {
					for (Player pl : Bukkit.getOnlinePlayers()) {
						if (pl != p) {
							pl.teleport(p);
							Message.P(pl, Message.Replacer(Message.Replacer(Message.TeleportedToTargetBy, p.getName(), "%teleportedby"), p.getName(), "%target"), true);
						}
					}
					Message.P(p, ChatColor.GOLD + "All players have been teleported to you.", true);
				} else {
					Message.P(p, Message.NoPermission, true);
				}
			} else {
				Message.C(Message.NotHumanlyPossible);
			}
			return true;
		} else if (cmd.getName().equalsIgnoreCase("teleports")) {
			if (sender instanceof Player) {
				Player p = (Player) sender;
				if (p.hasPermission("pvp.teleportwarps")) {
					if (args.length == 0) {
						Message.PrintHelpMenu(p, "Teleports");
					} else if (args.length >= 1) {
						if (args[0].equalsIgnoreCase("setwarp")) {
							if (args.length == 1) {
								Message.PrintHelpMenu(p, "Teleports-setwarp");
							} else {
								Message.P(p, Message.Replacer(Message.SavedWarp, args[1], "%name"), true);
								Locations.SaveLocation(Files.teleports, p.getLocation(), "Warps." + args[1].toLowerCase());
							}
						} else if (args[0].equalsIgnoreCase("setportal")) {
							if (args.length == 1) {
								Message.PrintHelpMenu(p, "Teleports-setportal");
							} else {
								if (Main.WorldEditEnable) {
									if (WorldEditHook.getSelectionPoint(p, 1) != null && WorldEditHook.getSelectionPoint(p, 2) != null){
										Message.P(p, Message.Replacer(Message.SavedPortal, args[1], "%name"), true);
										Locations.SaveLocation(Files.teleports, WorldEditHook.getSelectionPoint(p, 1), "Portals." + args[1].toLowerCase() + ".1");
										Locations.SaveLocation(Files.teleports, WorldEditHook.getSelectionPoint(p, 2), "Portals." + args[1].toLowerCase() + ".2");
										if (args.length >= 3) {
											String server = args[2];
											Files.teleports.getCustomConfig().set("Portals." + args[1].toLowerCase() + ".ServerTP", server.toLowerCase());
											Files.teleports.saveCustomConfig();
											Locations.RefreshConfig(Files.teleports);
											Message.P(p, Message.Replacer(Message.LinkedPortalToServer, server, "%server"), true);
										}
									} else {
										if (WorldEditHook.getSelectionPoint(p, 1) == null) {
											Message.P(p, Message.Replacer(Message.PointNotChosen, ""+1, "%num"), true);
										}

										if (WorldEditHook.getSelectionPoint(p, 2) == null){
											Message.P(p, Message.Replacer(Message.PointNotChosen, ""+2, "%num"), true);
										}
									}
								}
							}
						}
					}
				} else {
					Message.P(p, Message.NoPermission, false);
				}

			} else {
				sender.sendMessage(Message.NoPermission);
			}
			return true;
		}/*
		else if (cmd.getName().equalsIgnoreCase("ban")) {
			if (sender instanceof Player) {
				Player p = (Player) sender;
			}
			else{

			}
			return true;
		}
		else if (cmd.getName().equalsIgnoreCase("tempban")) {
			if (sender instanceof Player) {
				Player p = (Player) sender;
			}
			else{

			}
			return true;
		}
		else if (cmd.getName().equalsIgnoreCase("unban")) {
			if (sender instanceof Player) {
				Player p = (Player) sender;
			}
			else{

			}
			return true;
		}
		else if (cmd.getName().equalsIgnoreCase("warn")) {
			if (sender instanceof Player) {
				Player p = (Player) sender;
			}
			else{

			}
			return true;
		}
		else if (cmd.getName().equalsIgnoreCase("mute")) {
			if (sender instanceof Player) {
				Player p = (Player) sender;
			}
			else{

			}
			return true;
		}
		else if (cmd.getName().equalsIgnoreCase("tempmute")) {
			if (sender instanceof Player) {
				Player p = (Player) sender;
			}
			else{

			}
			return true;
		}
		else if (cmd.getName().equalsIgnoreCase("unmute")) {
			if (sender instanceof Player) {
				Player p = (Player) sender;
			}
			else{

			}
			return true;
		}
		else if (cmd.getName().equalsIgnoreCase("kick")) {
			if (sender instanceof Player) {
				Player p = (Player) sender;
			}
			else{

			}
			return true;
		}
		else if (cmd.getName().equalsIgnoreCase("banip")) {
			if (sender instanceof Player) {
				Player p = (Player) sender;
			}
			else{

			}
			return true;
		}
		else if (cmd.getName().equalsIgnoreCase("tempbanip")) {
			if (sender instanceof Player) {
				Player p = (Player) sender;
			}
			else{

			}
			return true;
		}
		else if (cmd.getName().equalsIgnoreCase("unbanip")) {
			if (sender instanceof Player) {
				Player p = (Player) sender;
			}
			else{

			}
			return true;
		}*/
		return false;
	}
}
