package ca.PvPCraft.PvPAPI.Commands;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.TimeZone;
import java.util.UUID;

import ca.PvPCraft.PvPAPI.Main;
import ca.PvPCraft.PvPAPI.enums.Challenge;
import ca.PvPCraft.PvPAPI.enums.IconInfo;
import ca.PvPCraft.PvPAPI.enums.Reward;
import ca.PvPCraft.PvPAPI.enums.Tasks;
import ca.PvPCraft.PvPAPI.events.TeleportListener;
import ca.PvPCraft.PvPAPI.methods.ConvertTimings;
import ca.PvPCraft.PvPAPI.methods.Locations;
import ca.PvPCraft.PvPAPI.methods.Packages;
import ca.PvPCraft.PvPAPI.methods.PlayersInfo;
import ca.PvPCraft.PvPAPI.methods.ProtocolLib;
import ca.PvPCraft.PvPAPI.methods.itemModifications;
import ca.PvPCraft.PvPAPI.methods.serverPinging.hubMethods;
import ca.PvPCraft.PvPAPI.repeatingTasks.Announcements;
import ca.PvPCraft.PvPAPI.utilities.IconMenu;
import ca.PvPCraft.PvPAPI.utilities.Message;
import ca.PvPCraft.PvPAPI.utilities.Mysql;
import ca.PvPCraft.PvPAPI.utilities.Stats;
import ca.PvPCraft.PvPAPI.utilities.Fanciful.FancyMessage;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;




public class PlayerCommands implements CommandExecutor{

	public static Main plugin;


	public PlayerCommands(Main main) {
		plugin = main;
	}
	public static HashMap<String, IconInfo> MENUS = new HashMap<String, IconInfo>();

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, final String[] args)
	{
		if (cmd.getName().equalsIgnoreCase("feedback")){
			if (sender instanceof Player){
				Player p = (Player) sender;
				if (Main.UseMySQL){
					if (args.length == 0){
						Message.P(p, "Thank you for your intrest in helping us.", true);
						Message.P(p, "In this feedback section, you may suggest ideas or report any bugs you experience, during your gameplay.", false);
						Message.P(p, "/Feedback <Message>", true);
					}
					else{
						String msg = "";
						for (int a = 0; a <= args.length - 1; a++){
							msg = msg + " " + args[a];
						}
						Message.P(p, ChatColor.GOLD + "Thank you for your feedback!", true);

						Mysql.PS.getSecureQuery("INSERT INTO Feedback (User, Feedback, Time, Server) VALUES (?, ?, ?, ?)", "" + Mysql.getUserID(p.getName()), msg, "" + (System.currentTimeMillis() / 1000L), Main.Server);
					}
				}
			}
			else
				Message.C(Message.NotHumanlyPossible);
			return true;
		}
		else if (cmd.getName().equalsIgnoreCase("changeskin")){
			if (sender instanceof Player){
				Player p = (Player) sender;
				if (p.isOp()){
					if (args.length == 2){
						Player player = PlayersInfo.getPlayer(args[0]);
						String targetSkin = args[1];
						Main.factory.changeDisplay(player, targetSkin, player.getName());
					}
					else
						Message.P(p, "/Changeskin <Player> <TargetSkin>", true);
				}
			}
			else
				Message.C(Message.NotHumanlyPossible);
			return true;
		}
		else if (cmd.getName().equalsIgnoreCase("ignore")){
			if (sender instanceof Player){
				Player p = (Player) sender;
				if (Main.UseMySQL){
					if (args.length >= 1){
						String ignorance = args[0];
						if (Mysql.getClosestPlayer(ignorance) != null){
							if (Mysql.getPlayerIgnores(Mysql.getUserID(p.getName())).contains(Mysql.getUserID(ignorance))){
								Mysql.removePlayerIgnore(Mysql.getUserID(p.getName()), Mysql.getUserID(ignorance));
								Message.P(p, Message.Replacer(Message.RemovedIgnorePlayer, ignorance, "%player"), true);
							}
							else{
								Mysql.addPlayerIgnore(Mysql.getUserID(p.getName()), Mysql.getUserID(ignorance));
								Message.P(p, Message.Replacer(Message.AddedIgnorePlayer, ignorance, "%player"), true);
							}
						}
						else
							Message.P(p, Message.Replacer(Message.PlayerDoesNotExist, ignorance, "%player"), true);
					}
					else
						Message.P(p, Message.CommandIgnoreHelp, true);
				}
			}
			else
				Message.C(Message.NotHumanlyPossible);
			return true;
		}
		else if (cmd.getName().equalsIgnoreCase("buy")){
			if (sender instanceof Player){
				final Player p = (Player) sender;
				if (MENUS.containsKey(p.getName())){
					MENUS.get(p.getName()).getMenu().destroy();
					MENUS.remove(p.getName());
				}
				ResultSet count;
				try {
					count = Mysql.PS.getSecureQuery("SELECT *, COUNT(*) AS TotalPackages FROM DonatePackages ORDER BY ID ASC");
					if (count.next()){
						int invsize = 0;
						int pos = 0;
						for(int i=0; i<=10; i++) {
							if((i*9) >= count.getInt("TotalPackages")) {
								invsize = invsize + i*9;
								break;
							}
						}
						IconMenu menu = new IconMenu(ChatColor.RED + "PvPKillz In-game Shop", p.getName(), invsize, new IconMenu.OptionClickEventHandler() {
							public void onOptionClick(IconMenu.OptionClickEvent event) throws SQLException {
								Packages.printPackageBuy(ChatColor.stripColor(event.getName()), p);
								event.setWillClose(true);
								event.setWillDestroy(false);
							}
						}, plugin);
						try {
							if (count.getInt("TotalPackages") >= 1){
								ResultSet count2 = Mysql.PS.getSecureQuery("SELECT * FROM DonatePackages ORDER BY ID ASC");
								while (count2.next()){

									String Item = count2.getString("ItemID");
									int ItemID = 0;
									short ItemData = 0;
									ItemStack IS = new ItemStack(Material.getMaterial(ItemID));

									if (Item.contains(":")){
										ItemID = Integer.parseInt(Item.split(":")[0]);
										ItemData = Short.parseShort(Item.split(":")[1]);
										IS = new ItemStack(Material.getMaterial(ItemID));
										IS.setDurability(ItemData);
									}
									else{
										ItemID = Integer.parseInt(Item);
										IS = new ItemStack(Material.getMaterial(ItemID));
									}

									String PackageName = count2.getString("Name");
									String details = "";
									ArrayList<String> list = new ArrayList<String>();
									if (count2.getString("MonthlyPackage") != null)
										list.add(Message.Colorize("&aMonthly Price: &b$" + count2.getString("MonthlyPackage")));
									if (count2.getString("PermenantPackage") != null)
										list.add(Message.Colorize("&aPermenant Price: &b$" + count2.getString("PermenantPackage")));

									if (count2.getString("Perks") != null){
										details = count2.getString("Perks");
										if (details.contains("//")){
											for (String entries : details.split("//")){
												list.add(Message.Colorize(ChatColor.GRAY + entries));
											}
										}
										else
											list.add(Message.Colorize(ChatColor.GRAY + details));
									}
									else
										list.add(Message.Colorize("&7&m----&r&a No Perks for this gamemode &7&m----"));

									String[] info = new String[list.size()];
									info = list.toArray(info);
									menu.setOption(pos, IS, ChatColor.GREEN + PackageName, info);
									pos++;
								}
							}
							else
								Message.P(p, Message.NoPackagesConfigured, true);
						} catch (SQLException e) {e.printStackTrace();}
						InventoryView iv = menu.open(p);
						MENUS.put(p.getName(), new IconInfo(menu, "shop", iv));  
					}
					else
						Message.P(p, Message.NoPackagesConfigured, true);
				} catch (SQLException e1) {e1.printStackTrace();}
			}
			else
				Message.C(Message.NotHumanlyPossible);
			return true;
		}
		else if (cmd.getName().equalsIgnoreCase("contests")){
			if (sender instanceof Player){
				Player p = (Player) sender;
				if (Main.UseMySQL){
					if (args.length == 0){
						Message.PrintHelpMenu(p, "Contests");
					}
					else{
						if (args[0].equalsIgnoreCase("list") && args.length == 1){
							try {
								ResultSet rs = Mysql.PS.getSecureQuery("SELECT * FROM Contests WHERE Server = ? AND Start <= ? AND End >= ?", Main.Server, ""+System.currentTimeMillis()/1000, ""+System.currentTimeMillis()/1000);
								if (rs.next()){
									do {
										Date date = new Date(rs.getLong("End")*1000L); // *1000 is to convert seconds to milliseconds
										SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy"); // the format of your date
										sdf.setTimeZone(TimeZone.getTimeZone("GMT+5"));
										String Date = sdf.format(date);

										Message.P(p, Message.Replacer(Message.Replacer(Message.ListOutput, "&a"+Date+"&7", "%endDate"), rs.getString("ContestName"), "%contestName"), false);
										Message.P(p, Message.Replacer(Message.DetailsContest, rs.getString("ContestDetails"), "%contestDetails"), false);

									} while (rs.next());
								}
								else
									Message.P(p, Message.NoContestsAvailable, true);
							} catch (SQLException e) {e.printStackTrace();}
						}
						else if (args.length >= 1){
							if (args[0].equalsIgnoreCase("enter")){
								if (args.length >= 2){
									try {
										ResultSet rs = Mysql.PS.getSecureQuery("SELECT * FROM Contests WHERE Server = ? AND LOWER(ContestName) = LOWER(?) AND Start <= ? AND End >= ?", Main.Server, args[1].toLowerCase(), ""+System.currentTimeMillis()/1000, ""+System.currentTimeMillis()/1000);
										if (rs.next()){
											ResultSet rs2 = Mysql.PS.getSecureQuery("SELECT * FROM ContestEntries WHERE User = ? AND Server = ? AND LOWER(ContestName) = LOWER(?)", ""+Mysql.getUserID(p.getName()), Main.Server, args[1].toLowerCase());
											if (!rs2.next()){
												boolean metRequirements = false;
												String PlotID = "";
												if (rs.getBoolean("PlotsNeeded")){
													ResultSet rsPlots = Mysql.PS.getSecureQuery("SELECT *, COUNT(*) AS PlotCount FROM plotmePlots WHERE LOWER(owner) = LOWER(?)", ""+p.getName());
													if (rsPlots.next()){
														if (rsPlots.getInt("PlotCount") > 1){
															metRequirements = false;
															ResultSet rsPlots2 = Mysql.PS.getSecureQuery("SELECT * FROM plotmePlots WHERE LOWER(owner) = LOWER(?)", ""+p.getName());
															int x = 0;
															while (rsPlots2.next()){
																x++;
																Message.P(p, Message.Replacer(Message.Replacer(Message.PlotListOutput, ""+x, "%id"), rsPlots2.getString("idX") + ";" + rsPlots2.getString("idZ"), "%plotID"), false);
															}
															Announcements.MessagesModify.put(p.getName(), "addPlot");
															PlayersInfo.contestRequired.put(p.getName(), rs.getString("ContestName"));
															Message.P(p, Message.ContestNeedPlotID, true);
														}
														else{
															PlotID = rsPlots.getString("idX") + ";" + rsPlots.getString("idZ");
															metRequirements = true;
														}
													}
													else{
														metRequirements = false;
														Message.P(p, Message.NeedPlots, true);
													}

												}
												else
													metRequirements = true;
												if (metRequirements == true){
													Mysql.PS.getSecureQuery("INSERT INTO ContestEntries (User, Server, ContestName, EntryTime, PlotEntry) VALUES (?,?,?,?,?)", ""+Mysql.getUserID(p.getName()), Main.Server, rs.getString("ContestName"), ""+System.currentTimeMillis()/1000, PlotID);
													Message.P(p, Message.Replacer(Message.JoinedContest, rs.getString("ContestName"), "%name"), true);
												}
											}
											else
												Message.P(p, Message.AlreadyInContest, true);
										}
										else
											Message.P(p, Message.NoContestWithName, true);
									} catch (SQLException e) {e.printStackTrace();}
								}
								else
									Message.PrintHelpMenu(p, "Contests");
							}
							else if (args[0].equalsIgnoreCase("quit")){
								if (args.length >= 2){
									try {
										ResultSet rs = Mysql.PS.getSecureQuery("SELECT * FROM ContestEntries WHERE User = ? AND Server = ? AND LOWER(ContestName) = LOWER(?)", ""+Mysql.getUserID(p.getName()), Main.Server, args[1].toLowerCase());
										if (rs.next()){
											Mysql.PS.getSecureQuery("DELETE FROM ContestEntries WHERE User = ? AND Server = ? AND LOWER(ContestName) = LOWER(?)", ""+Mysql.getUserID(p.getName()), Main.Server, rs.getString("ContestName"));
											Message.P(p, Message.Replacer(Message.LeftContest, rs.getString("ContestName"), "%name"), true);
										}
										else
											Message.P(p, Message.NoContestEntries, true);
									} catch (SQLException e) {e.printStackTrace();}
								}
								else
									Message.PrintHelpMenu(p, "Contests");
							}
							else if (args[0].equalsIgnoreCase("list")){
								if (args.length >= 2){
									try {
										ResultSet rs = Mysql.PS.getSecureQuery("SELECT *, COUNT(*) AS TotalEntries FROM ContestEntries Server = ? AND LOWER(ContestName) = LOWER(?)", Main.Server, args[1].toLowerCase());
										if (rs.next()){
											Message.P(p, Message.Replacer(Message.EntriesHeader, rs.getString("ContestName"), "%title"), false);

											if (rs.getInt("TotalEntries") >= 1){
												do {
													Date date = new Date(rs.getLong("EntryTime")*1000L); // *1000 is to convert seconds to milliseconds
													SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy"); // the format of your date
													sdf.setTimeZone(TimeZone.getTimeZone("GMT+5"));
													String Date = sdf.format(date);
													String plotInfo = "";
													if (rs.getString("PlotEntry") != "")
														plotInfo = "&7 - &b"+rs.getString("PlotEntry");

													Message.P(p, Message.Replacer(Message.Replacer(Message.Replacer(Message.EntriesList, plotInfo, "%plotInfo"), Mysql.getUsername(rs.getInt("User")), "%submitter"), Date, "%submitDate"), false);
												} while (rs.next());
											}
											else{
												Message.P(p, Message.NoContestEntriesFound, true);
											}
										}
										else
											Message.P(p, Message.NoContestEntries, true);
									} catch (SQLException e) {e.printStackTrace();}
								}
								else
									Message.PrintHelpMenu(p, "Contests");
							}
							else
								Message.PrintHelpMenu(p, "Contests");
						}
						else
							Message.PrintHelpMenu(p, "Contests");
					}
				}
			}
			else
				Message.C(Message.NotHumanlyPossible);
			return true;
		}
		else if (cmd.getName().equalsIgnoreCase("challenge")){
			if (sender instanceof Player){
				Player p = (Player) sender;
				PlayersInfo.checkChallenges(p.getName());
				if (args.length == 1 && args[0].equalsIgnoreCase("list")){
					// Output a list of challenges both complete and non-complete.
					Message.P(p, " ", false);
					Message.P(p, Message.Replacer(Message.HeaderMenu, "Challenges", "PvPAPI"), false);
					for (Entry<Integer, Challenge> challenge : Main.challenges.entrySet()){
						if (!PlayersInfo.challengesCompleted.get(p.getName()).contains(challenge.getKey()))
						new FancyMessage("&0 - ").then("&7" + challenge.getValue().getName() + " &8 / &7" + challenge.getValue().getShortDesc()).tooltip("&7Click to display more info.").command("/challenge " + challenge.getKey()).send(p);
					}
				}
				else if (args.length == 1 && args[0].equalsIgnoreCase("completed")){
					// Output a list of challenges both complete and non-complete.
					Message.P(p, " ", false);
					Message.P(p, Message.Replacer(Message.HeaderMenu, "Challenges", "PvPAPI"), false);
					for (Integer challengeID : PlayersInfo.challengesCompleted.get(p.getName())){
						Challenge challenge = Main.challenges.get(challengeID);
						new FancyMessage("&0 - ").then("&7" + challenge.getName() + " &8 / &7" + challenge.getShortDesc()).tooltip("&7Click to display more info.").command("/challenge " + challengeID).send(p);
					}
				}
				else if (args.length == 1 && ConvertTimings.isInteger(args[0])){
					if (Main.challenges.containsKey(Integer.parseInt(args[0]))){
						// Challenge Exists.
						Challenge c = Main.challenges.get(Integer.parseInt(args[0]));
						Message.P(p, " ", false);
						Message.P(p, "&6&m----------&a " + c.getName() + " &6&m----------", false);
						Message.P(p, "&6Description: &7" + (c.getLongDesc().isEmpty() ? c.getShortDesc() : c.getLongDesc()), false);
						Message.P(p, "&6Requirements:", false);
						for (Tasks task : c.getChallenges()){
							Message.P(p, " &7&l► " + (task.hasCount() ? "&a&l" + task.getCount() + "" : "") + "&7 " + task.getTaskName(), false);
						}
						Message.P(p, "&6Rewards:", false);
						for (Reward reward : c.getRewards()){
							Message.P(p, " &7&l► " + (reward.hasCount() ? "&a&l" + reward.getCount() + "" : "") + "&7 " + reward.getRewardName(), false);
						}
						Message.P(p, " ", false);
					}
				}
				else{
					Message.PrintHelpMenu(p, "challengesCmds");
				}
			}
			else
				Message.C(Message.NotHumanlyPossible);
			return true;
		}
		else if (cmd.getName().equalsIgnoreCase("refer")){
			/*
			if (sender instanceof Player){
				Player p = (Player) sender;
				if (Main.UseMySQL){
					if (args.length == 0){
						Message.PrintHelpMenu(p, "Referral");
					}
					else{
						if (args[0].equalsIgnoreCase("list")){
							// Find all people this user already referred...
							ResultSet rs = Mysql.PS.getSecureQuery("SELECT * FROM Referral WHERE LOWER(Referrer) = LOWER(?)", ""+Mysql.getUserID(p.getName()));
							try {
								int x = 0;
								while (rs.next()){
									x++;
									String Joined = "";
									if (rs.getBoolean("Joined"))
										Joined = "Joined";
									String PlayerName = rs.getString("Referred");  
									Message.P(p, Message.Replacer(Message.Replacer(Message.ListOutput, Joined, "%endDate"), PlayerName, "%contestName"), false);
								}
								if (x == 0){
									Message.P(p, Message.DidnotInvite, true);
								}
							} catch (SQLException e) {e.printStackTrace();}
						}
						else{
							ResultSet rs = Mysql.PS.getSecureQuery("SELECT *, COUNT(*) AS totalRefers FROM Referral WHERE LOWER(Referrer) = LOWER(?) AND Joined = 0", ""+Mysql.getUserID(p.getName()));
							try {
								if (rs.next()){
									if (rs.getInt("totalRefers") < Main.MaxRefers){
										ResultSet rs1 = Mysql.PS.getSecureQuery("SELECT * FROM Users WHERE LOWER(Username) = LOWER(?)", args[0]);
										if (rs1.next()){
											Message.P(p, Message.PlayersKnowsServer, true);
										}
										else{
											ResultSet rs2 = Mysql.PS.getSecureQuery("SELECT * FROM Referral WHERE LOWER(Referred) = LOWER(?)", args[0]);
											if (rs2.next()){
												if (rs2.getInt("Referrer") == Mysql.getUserID(p.getName())){
													Mysql.PS.getSecureQuery("DELETE FROM Referral WHERE Referrer = ? and LOWER(Referred) = LOWER(?)", ""+Mysql.getUserID(p.getName()), args[0]);
													Message.P(p,Message.Replacer(Message.UnreferredUser, rs2.getString("Referred"), "%user"), true);
												}
												else
													Message.P(p, Message.AlreadyReferred, true);
											}
											else{
												Mysql.PS.getSecureQuery("INSERT INTO Referral (Referrer, Referred, Time) VALUES (?,?,?)", ""+Mysql.getUserID(p.getName()), args[0], ""+System.currentTimeMillis()/1000);
												Message.P(p, Message.Replacer(Message.InviteYourFriend, args[0].toLowerCase(), "%friend"), true);
											}
										}
									}
									else{
										Message.P(p, Message.ReachedMaxUnjoinedRefers, true);
									}

								}
							} catch (SQLException e) {e.printStackTrace();}
						}
					}
				}
			}
			else
				Message.C(Message.PlayerNotOnline);

			 */
			return true;
		}
		else if (cmd.getName().equalsIgnoreCase("tell")){
			if (sender instanceof Player){
				Player p = (Player) sender;
				if (args.length <= 1){
					Message.P(p, Message.PrivateMessageHelp, true);
					Message.P(p, Message.PrivateMessageCmd, true);
				}
				else if (args.length >= 2){
					String recp = args[0];
					String msg = "";

					for (int a = 1; a <= args.length - 1; a++)
						msg = msg + " " + args[a];

					Player pl = PlayersInfo.getPlayer(recp);
					PlayersInfo.sendMessage(p, pl, msg);

				}
			}
			else{
				if (args.length <= 1){
					Message.C( Message.PrivateMessageHelp);
					Message.C(Message.PrivateMessageCmd);
				}
				else if (args.length >= 2){
					String recp = args[0];
					String msg = "";

					for (int a = 1; a <= args.length - 1; a++)
						msg = msg + " " + args[a];

					Player pl = PlayersInfo.getPlayer(recp);
					if (pl != null){
						Message.C(Message.Replacer(Message.Replacer(Message.SentTo, pl.getName(), "%target"), msg, "%msg"));
						Message.P(pl,Message.Replacer(Message.Replacer(Message.SentFrom, sender.getName(), "%sender"), msg, "%msg"), false);
						PlayersInfo.LastMessaged.put(sender.getName(), pl.getName());
						PlayersInfo.LastMessaged.put(pl.getName(), sender.getName());
						for (UUID socialspies : PlayersInfo.SocialSpies){
							if (pl.getUniqueId() != socialspies && ((Player) sender).getUniqueId() != socialspies){
								Player socialspy = Bukkit.getPlayer(socialspies);
								Message.P(socialspy, Message.Replacer(Message.Replacer(Message.Replacer(Message.SocialSpyMessage, pl.getName(), "%target"), sender.getName(), "%sender"), msg, "%msg"), true);
							}
						}
					}
					else
						Message.C(Message.PlayerNotOnline);
				}
			}

			return true;
		}
		else if (cmd.getName().equalsIgnoreCase("tutorial")){
			if (sender instanceof Player){
				Player p = (Player) sender;
				if (Main.TutorialEnabled){
					if (PlayersInfo.notCompletedTutorial(p.getName())){
						p.teleport(Locations.tutorialLoc);
						Message.P(p, Message.Replacer(Message.TeleportedToTarget, Message.TutorialRoom, "%target"), true);
					}
					else
						Message.P(p, Message.TutorialCompleted, true);
				}
				else
					Message.P(p, Message.TutorialNotEnabled, true);
			}
			else
				Message.C(Message.NotHumanlyPossible);
			return true;
		}
		else if (cmd.getName().equalsIgnoreCase("descitem")){
			if (sender instanceof Player){
				Player p = (Player) sender;
				ItemStack is = p.getItemInHand();
				if (p.hasPermission("pvp.items.desc") || p.isOp()){
					if (is != null){
						if (args.length == 0){
							String newDesc = "";
							for (String arg : args){
								newDesc += arg + " ";
							}
							if (is.hasItemMeta()){
								if (is.getItemMeta().hasLore()){
									if (p.hasPermission("pvp.items.redesc") || p.isOp()){
										if (p.hasPermission("pvp.items.desc_color") || p.isOp())
											newDesc = Message.Colorize(newDesc);
										ItemMeta im = is.getItemMeta();
										im.setLore(itemModifications.multilineFromString(newDesc));
										is.setItemMeta(im);
										Message.P(p, Message.ItemReDesc, true);
									}
									else
										Message.P(p, Message.ReDescingIsPvP, true);
								}
								else{
									if (p.hasPermission("pvp.items.desc_color") || p.isOp())
										newDesc = Message.Colorize(newDesc);
									ItemMeta im = is.getItemMeta();
									im.setLore(itemModifications.multilineFromString(newDesc));
									is.setItemMeta(im);
									Message.P(p, Message.ItemReDesc, true);
								}
							}
							else{
								if (p.hasPermission("pvp.items.desc_color") || p.isOp())
									newDesc = Message.Colorize(newDesc);
								ItemMeta im = is.getItemMeta();
								im.setLore(itemModifications.multilineFromString(newDesc));
								is.setItemMeta(im);
								Message.P(p, Message.ItemReDesc, true);
							}
						}
						else
							Message.P(p, Message.ChooseADesc, true);
					}
					else
						Message.P(p, Message.HoldAnItemToModify, true);
				}
			}
			else
				Message.C(Message.NotHumanlyPossible);
			return true;
		}
		else if (cmd.getName().equalsIgnoreCase("enchantfake")){
			if (sender instanceof Player){
				Player p = (Player) sender;
				ItemStack is = p.getItemInHand();
				if (p.hasPermission("pvp.items.enchant") || p.isOp()){
					if (is != null){
						if (!ProtocolLib.isGlowing(is)){
							is = ProtocolLib.setGlowing(is);
							p.setItemInHand(is);
						}
						else
							is.removeEnchantment(Enchantment.SILK_TOUCH);
					}
					else
						Message.P(p, Message.HoldAnItemToModify, true);
				}
			}
			else
				Message.C(Message.NotHumanlyPossible);
			return true;
		}
		else if (cmd.getName().equalsIgnoreCase("nameitem")){
			if (sender instanceof Player){
				Player p = (Player) sender;
				ItemStack is = p.getItemInHand();
				if (p.hasPermission("pvp.items.name") || p.isOp()){
					if (is != null){
						if (args.length == 1){
							String newName = args[0];
							if (is.hasItemMeta()){
								if (is.getItemMeta().hasDisplayName()){
									if (p.hasPermission("pvp.items.rename") || p.isOp()){
										if (p.hasPermission("pvp.items.name_color") || p.isOp())
											newName = Message.Colorize(newName);
										ItemMeta im = is.getItemMeta();
										im.setDisplayName(newName);
										is.setItemMeta(im);
										Message.P(p, Message.ItemRenamed, true);
									}
									else
										Message.P(p, Message.RenamingIsEmerald, true);
								}
								else{
									if (p.hasPermission("pvp.items.name_color") || p.isOp())
										newName = Message.Colorize(newName);
									ItemMeta im = is.getItemMeta();
									im.setDisplayName(newName);
									is.setItemMeta(im);
									Message.P(p, Message.ItemRenamed, true);
								}
							}
							else{
								if (p.hasPermission("pvp.items.name_color") || p.isOp())
									newName = Message.Colorize(newName);
								ItemMeta im = is.getItemMeta();
								im.setDisplayName(newName);
								is.setItemMeta(im);
								Message.P(p, Message.ItemRenamed, true);
							}
						}
						else
							Message.P(p, Message.ChooseAName, true);
					}
					else
						Message.P(p, Message.HoldAnItemToModify, true);
				}
			}
			else
				Message.C(Message.NotHumanlyPossible);
			return true;
		}
		else if (cmd.getName().equalsIgnoreCase("reply")){
			if (sender instanceof Player){
				Player p = (Player) sender;
				if (args.length == 0){
					Message.P(p, Message.ReplyHelp, true);
					Message.P(p, Message.ReplyHelpCmd, false);
				}
				else if (args.length >= 1){
					String msg = "";
					for (int a = 0; a <= args.length - 1; a++)
						msg =  msg + " " + args[a];

					if (PlayersInfo.LastMessaged.containsKey(p.getName())){
						Player last = PlayersInfo.getPlayer(PlayersInfo.LastMessaged.get(p.getName()));
						PlayersInfo.sendMessage(p, last, msg);
					}
					else
						Message.P(p, Message.NoOneContactedYou, true);
				}
			}
			else{
				if (args.length == 0){
					Message.C( Message.ReplyHelp);
					Message.C( Message.ReplyHelpCmd);
				}
				else if (args.length >= 1){
					String msg = "";
					for (int a = 0; a <= args.length - 1; a++)
						msg =  msg + " " + args[a];

					if (PlayersInfo.LastMessaged.containsKey(sender.getName())){
						Player last = PlayersInfo.getPlayer(PlayersInfo.LastMessaged.get(sender.getName()));
						if (last != null){
							PlayersInfo.LastMessaged.put("Console", last.getName());
							PlayersInfo.LastMessaged.put(last.getName(), "Console");

							Message.C(Message.Replacer(Message.Replacer(Message.SentTo, last.getName(), "%target"), msg, "%msg"));
							Message.P(last,Message.Replacer(Message.Replacer(Message.SentFrom, sender.getName(), "%sender"), msg, "%msg"), false);
							for (UUID socialspies : PlayersInfo.SocialSpies){
								if (last.getUniqueId() != socialspies && ((Player) sender).getUniqueId() != socialspies){
									Player socialspy = Bukkit.getPlayer(socialspies);
									Message.P(socialspy, Message.Replacer(Message.Replacer(Message.Replacer(Message.SocialSpyMessage, last.getName(), "%target"), sender.getName(), "%sender"), msg, "%msg"), true);
								}
							}
						}
					}
					else
						Message.C(Message.NoOneContactedYou);
				}
			}

			return true;
		}
		else if (cmd.getName().equalsIgnoreCase("tp")){
			if (sender instanceof Player){
				Player p = (Player) sender;
				if (p.hasPermission("pvp.tp") || p.isOp()){
					if (args.length >= 1){
						Player target = null;
						Player teleporter = null;
						if (args.length >= 3){
							if (args.length == 3)
								teleporter = p;
							else if (args.length >= 4)
								teleporter = PlayersInfo.getPlayer(args[0]);
							int x = 0;
							int y = 0;
							int z = 0;

							if (teleporter == p){
								if (ConvertTimings.isInteger(args[0]) && ConvertTimings.isInteger(args[1]) && ConvertTimings.isInteger(args[2])){
									x = Integer.parseInt(args[0]);
									y = Integer.parseInt(args[1]);
									z = Integer.parseInt(args[2]);

								}
								else
									Message.P(p, Message.MehNeedNumba, true);
							}
							else{
								if (teleporter != null){
									if (ConvertTimings.isInteger(args[1]) && ConvertTimings.isInteger(args[2]) && ConvertTimings.isInteger(args[3])){
										x = Integer.parseInt(args[1]);
										y = Integer.parseInt(args[2]);
										z = Integer.parseInt(args[3]);
									}
									else
										Message.P(p, Message.MehNeedNumba, true);
								}
								else
									Message.P(p, Message.PlayerNotOnline, true);
							}
							teleporter.teleport(new Location(teleporter.getWorld(), x, y, z));

							Message.P(teleporter, Message.Replacer(Message.TeleportedToTarget, "your target.", "%target"), true);
						}
						else{
							if (args.length >= 2){
								target = PlayersInfo.getPlayer(args[1]);
								teleporter = PlayersInfo.getPlayer(args[0]);
							}
							else{
								target = PlayersInfo.getPlayer(args[0]);
								teleporter = p;
							}
							if (target != null && teleporter != null){
								if (target != teleporter){
									if (p != teleporter)
										Message.P(teleporter, Message.Replacer(Message.Replacer(Message.TeleportedToTargetBy, p.getName(), "%teleportedby"), target.getName(), "%target"), true);
									else
										Message.P(teleporter, Message.Replacer(Message.TeleportedToTarget, target.getName(), "%target"), true);
									teleporter.teleport(target);
								}
								else
									Message.P(p, Message.NotHumanlyPossible, true);
							}
							else
								Message.P(p, Message.PlayerNotOnline, true);
						}
					}
					else
						Message.P(p, Message.CommandTPHelp, true);
				}
				else if (p.hasPermission("pvp.tpa"))
					Message.P(p, Message.TryTpa, true);
				else
					Message.P(p, Message.NoPermission, true);
			}
			else {
				Player target = null;
				Player teleporter = null;
				if (args.length >= 4){

					teleporter = PlayersInfo.getPlayer(args[0]);
					int x = 0;
					int y = 0;
					int z = 0;

					if (teleporter != null){
						if (ConvertTimings.isInteger(args[1]) && ConvertTimings.isInteger(args[2]) && ConvertTimings.isInteger(args[3])){
							x = Integer.parseInt(args[1]);
							y = Integer.parseInt(args[2]);
							z = Integer.parseInt(args[3]);
						}
						else
							Message.C(Message.MehNeedNumba);
					}
					else
						Message.C(Message.PlayerNotOnline);
					teleporter.teleport(new Location(teleporter.getWorld(), x, y, z));  
					Message.P(teleporter, Message.Replacer(Message.TeleportedToTarget, "your target.", "%target"), true);

				}
				else{
					if (args.length >= 2){
						target = PlayersInfo.getPlayer(args[1]);
						teleporter = PlayersInfo.getPlayer(args[0]);
						if (target != null && teleporter != null){
							if (target != teleporter){
								Message.P(teleporter, Message.Replacer(Message.Replacer(Message.TeleportedToTargetBy, sender.getName(), "%teleportedby"), target.getName(), "%target"), true);
								teleporter.teleport(target);
							}
							else
								Message.C(Message.NotHumanlyPossible);
						}
						else
							Message.C(Message.PlayerNotOnline);
					}
					else 
						Message.C(Message.CommandTPHelp);
				}

			}
			return true;
		}
		else if (cmd.getName().equalsIgnoreCase("tpa")){
			if (sender instanceof Player){
				Player p = (Player) sender;
				if (p.hasPermission("pvp.tpa")){
					//WORK ON TPA
					if (args.length >= 1){
						if (PlayersInfo.getPlayer(args[0]) != null){
							Player target = PlayersInfo.getPlayer(args[0]);
							Message.P(p, Message.Replacer(Message.SentRequestToTarget, target.getName(), "%target"), true);
							Message.P(target, Message.Replacer(Message.SentRequestTPA, p.getName(), "%p"), true);
							Message.P(target, Message.SentRequestTPAOpts, true);

							PlayersInfo.tpaRequestTo.put(target.getName(), p.getName());
						}
						else
							Message.P(p, Message.PlayerNotOnline, true);


					}
					else
						Message.P(p, Message.TpaHelp, true);
				}
				else
					Message.C(Message.NotHumanlyPossible);
			}
			else

				return true;
		}
		else if (cmd.getName().equalsIgnoreCase("nickname")){
			if (sender instanceof Player){
				Player p = (Player) sender;
				if (p.hasPermission("pvp.nickname")  || p.isOp()){
					if (args.length >= 1){
						String nick = null;
						if (!args[0].equalsIgnoreCase("none"))
							nick = args[0];
						PlayersInfo.changeNickname(p, nick);
					}
					else
						Message.P(p, Message.NickNameHelp, true);
				}
				else if (p.hasPermission("pvp.nickname.others")  || p.isOp()){
					if (args.length >= 2){
						String nick = null;
						if (!args[1].equalsIgnoreCase("none"))
							nick = args[1];
						PlayersInfo.changeNickname(PlayersInfo.getPlayer(args[0]), nick);
					}
					else
						Message.P(p, Message.NickNameOthersHelp, true);
				}
				else
					Message.P(p, Message.NoPermission, true);
			}
			else
				Message.C(Message.NotHumanlyPossible);
			return true;
		}
		else if (cmd.getName().equalsIgnoreCase("tpaccept")){
			if (sender instanceof Player){
				Player p = (Player) sender;
				if (p.hasPermission("pvp.tpa")){
					//WORK ON TPA
					if (PlayersInfo.tpaRequestTo.containsKey(p.getName())){
						if (PlayersInfo.getPlayer(PlayersInfo.tpaRequestTo.get(p.getName())) != null){
							Player teleporter = PlayersInfo.getPlayer(PlayersInfo.tpaRequestTo.get(p.getName()));
							Message.P(teleporter, Message.Replacer(Message.TeleportedToTarget, p.getName(), "%target"), true);
							Message.P(p, Message.Replacer(Message.Tpaccepted, teleporter.getName(), "%player"), true);
							PlayersInfo.tpaRequestTo.remove(p.getName());

							PlayersInfo.delayedTeleports.remove(p.getName());
							TeleportListener.teleport(teleporter, p);
						}
						else
							Message.P(p, Message.PlayerNotOnline, true);
					}
					else
						Message.P(p, Message.NoTpaRequests, true);
				}
				else
					Message.P(p, Message.NoPermission, true);
			}
			else
				Message.C(Message.NotHumanlyPossible);
			return true;
		}
		else if (cmd.getName().equalsIgnoreCase("tpdeny")){
			if (sender instanceof Player){
				Player p = (Player) sender;
				if (p.hasPermission("pvp.tpa")){
					//WORK ON TPA
					if (PlayersInfo.tpaRequestTo.containsKey(p.getName())){
						if (PlayersInfo.getPlayer(PlayersInfo.tpaRequestTo.get(p.getName())) != null){
							Player teleporter = PlayersInfo.getPlayer(PlayersInfo.tpaRequestTo.get(p.getName()));
							Message.P(teleporter, Message.Replacer(Message.TeleportIsCancelled, p.getName(), "%player"), true);
							PlayersInfo.tpaRequestTo.remove(p.getName());

							PlayersInfo.delayedTeleports.remove(p.getName());
						}
						else
							Message.P(p, Message.PlayerNotOnline, true);
					}
					else
						Message.P(p, Message.NoTpaRequests, true);
				}
				else
					Message.P(p, Message.NoPermission, true);
			}
			else
				Message.C(Message.NotHumanlyPossible);
			return true;
		}
		else if (cmd.getName().equalsIgnoreCase("topstats")){

			if (sender instanceof Player){
				Player p = (Player) sender;
				if (args.length == 0){
					Message.P(p, " ", false);
					Message.P(p, "&6&lChoose a catagory;", true);
					new FancyMessage("&a#1 ► &6Most Kills").tooltip("&7Show players with the most kills.").command("/topstats kills").send(p);
					new FancyMessage("&a#2 ► &6Most Deaths").tooltip("&7Show players with the most deaths.").command("/topstats deaths").send(p);
					new FancyMessage("&a#3 ► &6Most Tokens").tooltip("&7Show players with the most tokens.").command("/topstats tokens").send(p);
					new FancyMessage("&a#4 ► &6Most Playtime").tooltip("&7Show players with the most playtime on " + Main.Server + ".").command("/topstats playtime").send(p);
					new FancyMessage("&a#5 ► &6Most used weapons").tooltip("&7Show players with the most used weapons.").command("/topstats weapons").send(p);
					new FancyMessage("&a/topstats #").tooltip("&7Display top stats for different statistics.").send(p);
					Message.P(p, Message.Replacer(Message.HeaderMenu, "&m----------", "&a PvPAPI &6&m"), false);
				}
				else{
					if (args[0].equalsIgnoreCase("kills") || args[0].equalsIgnoreCase("1")){
						Message.P(p, Message.Replacer(Message.HeaderMenu, "&aTop kills&6&m", "&a PvPAPI &6&m"), false);
						ResultSet rs = Mysql.PS.getSecureQuery("SELECT *, COUNT(*) AS Kills FROM `" + Main.Server + "_Kills` WHERE Killer != 0 GROUP BY Killer ORDER BY Kills DESC LIMIT 10");
						try {
							int count = 1;
							while (rs.next()){
								new FancyMessage("&6&l" + count + "&7 - &a" + rs.getInt("Kills") + "&7 by ")
								.then(Mysql.getUsername(rs.getInt("Killer")))
								.tooltip("&7 Show " + Mysql.getUsername(rs.getInt("Killer")) + "'s records...")
								.command("/stats " + Mysql.getUsername(rs.getInt("Killer"))).send(p);
								count++;
							}
							new FancyMessage(Message.viewMoreInfoWebsite).link("http://www.PvP.Kz").send(p);
						} catch (SQLException e) {
							Message.P(p, Message.failedToRetrieveInfo, true);
						}
						Message.P(p, Message.Replacer(Message.HeaderMenu, "&m--------------", "&a PvPAPI &6&m"), false);
					}
					else if (args[0].equalsIgnoreCase("deaths") || args[0].equalsIgnoreCase("2")){
						Message.P(p, Message.Replacer(Message.HeaderMenu, "&aTop deaths&6&m", "&a PvPAPI &6&m"), false);
						ResultSet rs = Mysql.PS.getSecureQuery("SELECT *, COUNT(*) AS Deaths FROM `" + Main.Server + "_Kills` WHERE Victim != 0 GROUP BY Victim ORDER BY Deaths DESC LIMIT 10");
						try {
							int count = 1;
							while (rs.next()){
								new FancyMessage("&6&l" + count + "&7 - &a" + rs.getInt("Deaths") + "&7 by ")
								.then("&7"+Mysql.getUsername(rs.getInt("Victim")))
								.tooltip("&7 Show " + Mysql.getUsername(rs.getInt("Victim")) + "'s records...")
								.command("/stats " + Mysql.getUsername(rs.getInt("Victim"))).send(p);
								count++;
							}
							new FancyMessage(Message.viewMoreInfoWebsite).link("http://www.PvP.Kz").send(p);
						} catch (SQLException e) {
							Message.P(p, Message.failedToRetrieveInfo, true);
						}
						Message.P(p, Message.Replacer(Message.HeaderMenu, "&m----------", "&a PvPAPI &6&m"), false);
					}
					else if (args[0].equalsIgnoreCase("tokens") || args[0].equalsIgnoreCase("3")){
						Message.P(p, Message.Replacer(Message.HeaderMenu, "&aMost tokens on PvPKillz&6&m", "&a PvPAPI &6&m"), false);
						ResultSet rs = Mysql.PS.getSecureQuery("SELECT * from Points WHERE ID != 0 ORDER BY Points DESC LIMIT 10");
						try {
							int count = 1;
							while (rs.next()){
								new FancyMessage("&6&l" + count + "&7 - &a" + rs.getInt("Points") + "&7 by ")
								.then("&7"+Mysql.getUsername(rs.getInt("ID")))
								.tooltip("&7 Show " + Mysql.getUsername(rs.getInt("ID")) + "'s records...")
								.command("/stats " + Mysql.getUsername(rs.getInt("ID"))).send(p);
								count++;
							}
							new FancyMessage(Message.viewMoreInfoWebsite).link("http://www.PvP.Kz").send(p);
						} catch (SQLException e) {
							Message.P(p, Message.failedToRetrieveInfo, true);
						}
						Message.P(p, Message.Replacer(Message.HeaderMenu, "&m-----------------------", "&a PvPAPI &6&m"), false);
					}
					else if (args[0].equalsIgnoreCase("playtime") || args[0].equalsIgnoreCase("4")){
						Message.P(p, Message.Replacer(Message.HeaderMenu, "&aMost playtime&6&m", "&a PvPAPI &6&m"), false);
						ResultSet rs = Mysql.PS.getSecureQuery("SELECT *, SUM(End - Start) AS playTime from gameSessions WHERE User != 0 AND User != 101 AND Server = '" + Main.Server + "' GROUP BY User ORDER BY playTime DESC LIMIT 10");
						try {
							int count = 1;
							while (rs.next()){
								new FancyMessage("&6&l" + count + "&7 - &a" + ConvertTimings.convertTime(rs.getInt("playTime"), true) + "&7 by ")
								.then("&7"+Mysql.getUsername(rs.getInt("User")))
								.tooltip("&7 Show " + Mysql.getUsername(rs.getInt("User")) + "'s records...")
								.command("/stats " + Mysql.getUsername(rs.getInt("User"))).send(p);
								count++;
							}
							new FancyMessage(Message.viewMoreInfoWebsite).link("http://www.PvP.Kz").send(p);
						} catch (SQLException e) {
							Message.P(p, Message.failedToRetrieveInfo, true);
						}
						Message.P(p, Message.Replacer(Message.HeaderMenu, "&m-------------", "&a PvPAPI &6&m"), false);

					} else if (args[0].equalsIgnoreCase("weapons") || args[0].equalsIgnoreCase("5")){
						Message.P(p, Message.Replacer(Message.HeaderMenu, "&aMost weapons used&6&m", "&a PvPAPI &6&m"), false);
						ResultSet rs = Mysql.PS.getSecureQuery("SELECT *, COUNT(*) AS WeaponUses FROM `" + Main.Server + "_Kills` WHERE Killer != 0 GROUP BY Killer, KillerWep ORDER BY WeaponUses DESC LIMIT 10");
						try {
							int count = 1;
							while (rs.next()){
								new FancyMessage("&6&l" + count + "&7 - &b" + rs.getInt("WeaponUses") + "&7 with &a" + rs.getString("KillerWep") + "&7 by ")
								.then("&7"+Mysql.getUsername(rs.getInt("Killer")))
								.tooltip("&7 Show " + Mysql.getUsername(rs.getInt("Killer")) + "'s records...")
								.command("/stats " + Mysql.getUsername(rs.getInt("Killer"))).send(p);
								count++;
							}
							new FancyMessage(Message.viewMoreInfoWebsite).link("http://www.PvP.Kz").send(p);
						} catch (SQLException e) {
							Message.P(p, Message.failedToRetrieveInfo, true);
						}
						Message.P(p, Message.Replacer(Message.HeaderMenu, "&m-----------------", "&a PvPAPI &6&m"), false);
					}
				}
			}
			else
				Message.C(Message.NotHumanlyPossible);
			return true;
		}
		else if (cmd.getName().equalsIgnoreCase("stats")) {
			if (sender instanceof Player){
				Player p = (Player) sender;
				String pname = "I";
				String player = p.getName();

				if (args.length >= 1){
					pname = args[0];
					player = Mysql.getClosestPlayer(pname);
				}


				if (player != null) {
					int Kills = 0;
					int Deaths = 0;
					int Tokens = Stats.getTokens(player);
					String favWeapon = "N/A";
					String lastKillTime = "Never";
					String lastKillPerson = "N/A";

					if (Main.KillStatsEnable){
						Kills = Stats.getTotalKills(player);
						Deaths = Stats.getTotalDeaths(player);
						favWeapon = Stats.getFavWeapon(player);
						lastKillTime = Stats.getLastKillTime(player);
						lastKillPerson = Stats.getLastKill(player);
					}

					Message.P(p, Message.Replacer(Message.HeaderMenu, player +"'s Stats for " + Main.Server, "PvPAPI"), false);
					new FancyMessage("&7Kills &c" + Kills)
					.tooltip("&7Amount of &bkills&7 on &a" + Main.Server)
					.then("  &7Deaths &8" + Deaths)
					.tooltip("&7Amount of &8deaths&7 on &a" + Main.Server)
					.then("  &7Tokens &b" + Tokens)
					.tooltip("&7Total tokens on &aPvP.Kz")
					.send(p);
					new FancyMessage("&7Most used weapon &b" + favWeapon)
					.tooltip("&7Most used weapon on &a" + Main.Server)
					.send(p);

					new FancyMessage("&8Last Kill &7" + lastKillTime + " ago &7(")
					.then("&5"+lastKillPerson)
					.command("/stats " + lastKillPerson)
					.then("&7)")
					.tooltip("&7 Show " + lastKillPerson + "'s records...")
					.send(p);

				} else
					Message.P(p, Message.Replacer(Message.PlayerDoesNotExist, pname, "%player"), false);
			}
			else
				Message.C(Message.NotHumanlyPossible);
			return true;
		} 
		else if (cmd.getName().equalsIgnoreCase("servers")){
			if (sender instanceof Player){
				Player p = (Player) sender;
				hubMethods.OpenServersChoice(p);
			}
			else
				Message.C(Message.NotHumanlyPossible);
			return true;
		}
		else if (cmd.getName().equalsIgnoreCase("roll")){

			if (sender instanceof Player){
				Player p = (Player) sender;
				Mysql.checkDailyRewards(p);
			}
			else{
				Message.C(Message.NotHumanlyPossible);
			}
			return true;
		}
		return false;
	}

	private Integer getID(Challenge challenge) {
		for (Entry<Integer, Challenge> entry : Main.challenges.entrySet()){
			if (entry.getValue().equals(challenge))
				return entry.getKey();
		}
		return null;
	}

}
