package ca.PvPCraft.PvPAPI.utilities;

import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import ca.PvPCraft.PvPAPI.Main;
import ca.PvPCraft.PvPAPI.enums.LengthType;
import ca.PvPCraft.PvPAPI.methods.EconSystem;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import ru.tehkode.permissions.bukkit.PermissionsEx;



public class Message{
	public static Main plugin;
	public static final String Quit = "&c- &7%player";
	public static final String Joined = "&a+ &7%player";
	public static String PREFIX = ChatColor.RED + "[" + ChatColor.GOLD + "PvP" + ChatColor.RED + "] " + ChatColor.GREEN;
	/*
	 *  Translation file (Will be extra, since this plugin will be used in english, but I need a challenge)
	 */
	public static final String Day = "day";
	public static final String Days = "days";
	public static final String HaveTimeLeft = "&7You have &5%time&7 to enter the command.";
	public static final String Hours = "hours";
	public static final String Hour = "hour";
	public static final String Minutes = "minutes";
	public static final String Minute = "minute";	
	public static final String Second = "second";	
	public static final String Seconds = "seconds";
	public static final String MoneyModify = "You have %type money.";
	public static final String EarnedMoney = "&aearned&7";
	public static final String LostMoney = "&clost&7";
	public static final String NoPermission = "Much command, such permission, very no.";
	public static final String GameModeHelp = "&c/Gamemode &7<&a0&7/&a1&7/&a2&7> &7[&btarget&7]";
	public static final String ChangedGamemode = "&7%p's gamemode has been &bset&7 to &a%gm&7.";
	public static final String SentTo = "&6Me &l➔ &b %target&7: %msg";
	public static final String SentFrom = "&b%sender &l➔ &6 Me: %msg";
	public static final String PlayerNotOnline = "&cPlayer is not online.";
	public static final String NoOneContactedYou = "&cYou have no one to reply to.";
	public static final String ReplyHelp = "Quickly reply to the last message sent.";
	public static final String ReplyHelpCmd = "&6/r &7<Message>";
	public static final String PrivateMessageCmd = "&7/tell <&atarget&7> &7<&aMessage&7>";
	public static final String PrivateMessageHelp = "Private message players across the server - It is magic.";
	public static final String CommandTPHelp = "&c/tp &7[&8player&7] &7<&atarget&7>";
	public static final String TeleportedToTargetBy = "&7You have been teleported to &b%target&7 by &a%teleportedby&7.";
	public static final String TeleportedToTarget = "&7You have teleported to &b%target.";
	public static final String SocialSpyMessage = "&cSS &7%sender &l➔ &7 %target:&8 %msg";
	public static final String SocialSpyToggled = "&7SocialSpy is now %toggle&7.";
	public static final String PleaseDoNotSpam = "&cPlease do not repeat the same message.";
	public static final String MustCompleteTutorial = "&cYou must complete the tutorial. To begin it type /Tutorial";
	public static final String TutorialCompleted = "&7You have already &acompleted&7 the tutorial.";
	public static final String TutorialNotEnabled = "&7Tutorial mode is &cnot enabled&7 here.";
	public static final String TutorialRoom = "Tutorial Room";
	public static final String SetWarpMenu = "&7 ¯¯¯ Set &6Warp Menu&7 ¯¯¯";
	public static final String SetWarpHelp = "&a/SetWarp <WarpName> &7| Set the warp.";
	public static final String SavedTeleport = "Saved the teleport warp named %name.";
	public static final String SavedPortal = "The portal %name is now saved.";
	public static final String PointNotChosen = "&cERROR: You have not chosen point %num.";
	public static final String SavedWarp = "&aYou have saved the %name warp.";
	public static final String HelpWarp = "&7/teleports setwarp &a<WarpName> ";
	public static final String PermHighLighting = "pvp.hubhighlight";
	public static final String LinkedPortalToServer = "Portal is now linked with %server.";
	public static final String TypeOutAnnouncement = "Please type out your announcement, like a normal chat message. To cancel type 'cancel'";
	public static final String AnnouncementAdded = "The announcement %msg has been added.";
	public static final String HeaderMenu = "&6&m----------&a PvPAPI &6&m----------";
	private static final String MenuAM1 = "&7/am &aadd &7 | Add a message to announcements";
	public static final String MenuAM2 = "&7/am &aremove &7<&b#&7> &7 | Remove a message from announcements";
	public static final String MenuAM3 = "&7/am &asetDelay &7<&bSeconds&7> &7 | Change delay between messages";
	private static final String MenuAM4 = "&7/am &areload &7 | Reload message from config";
	public static final String AnnouncementCancelled = "Announcement mode has been cancelled.";
	public static final String AnnouncementOutput = "&b%num &7- &7%msg";
	public static final String NotHumanlyPossible = "Sorry, but this is not humanly possible.";
	public static final String AnnouncementDeleted = "&aAnnouncement: '%msg'&c has been deleted.";
	public static final String CountIsTooHigh = "What are you counting? Sheep? Too much!";
	public static final String MenuTM1 = "&7/tm &aset &7<&aStart&7/&aEnd&7> | Set tutorial points.";
	public static final String MenuTM2 = "&7/tm forceComplete &7<&aplayer&7> | Force finish tutorial for player";
	public static final String MenuTM3 = "&7/tm forceReset &7<&aplayer&7> | Force player to redo tutorial";
	public static final String SavedTutorialStart = "Tutorial start spawn point has been set.";
	public static final String TutorialEndSetup = "Interact with the object the user must interact with to complete the tutorial.";
	public static final String TutorialEndSetupDone = "Tutorials will now end with user interacting with %block.";
	public static final String YouFinishedTutorial = "&f&lCONGRATZ! &aYou have finished the tutorial.";
	public static final String MehNeedNumba = "Me very mad! Me want numba! (Translated: I require a number.)";
	public static final String MenuCont1 = "&7/Contests &7<&aEnter&7/&cQuit&7> &7<&aContest Name&7>";
	public static final String MenuCont2 = "&7/Contests &bList &7[&bContestName&7] | List related contests to gamemode or view entries for contest";
	public static final String NoContestEntries = "You do not have any entries for that contest.";
	public static final String NoContestWithName = "There are no contests with that name.";
	public static final String MenuRefer1 = "&7/Refer <&bPlayerName&7> | Add/Remove invite (Not case sensitive)";
	public static final String MenuRefer2 = "&7/Refer List | List who you have invited.";
	public static final String ListOutput = "&7- &b%contestName &7  | &8 %endDate";
	public static final String NoContestsAvailable = "There are no contests available at this time.";
	public static final String JoinedContest = "You have now joined contest &a%name&7.";
	public static final String LeftContest = "You have now left contest &c%name&7.";
	public static final String AlreadyInContest = "You are already in a contest.";
	public static final String DetailsContest = "&bDetails: &7%contestDetails";
	public static final String NeedPlots = "You must own a plot to submit it.";
	public static final String ContestNeedPlotID = "Please specify a plot ID for the contest, type out the number from the above list of the plot you want to submit. To check what plot you are standing in, type /p info";
	public static final String PlotListOutput = "&b%id &7- &b%plotID";
	public static final String Cancelled = "You have cancelled your choice.";
	public static final String NoContestEntriesFound = "No contest entries were found, how about you submit the first!";
	public static final String EntriesHeader = "&6&m----------&a %title &6&m----------";
	public static final String EntriesList = "&7%submitter &b- &7 %submitDate%plotInfo.";
	public static final String MenuTeleports1 = "&7/teleports setWarp &7<&aWarpName&7> | Link warp with portal (Same name as portal)";
	public static final String MenuTeleports2 = "&7/teleports setPortal &7<&aPortalName&7> | Link portal with warp (Same name as warp)";
	public static final String MenuTeleports3 = "&7/teleports setPortal &7<&aPortalName&7> &7<&bServerName&7> | Link portal to another server";
	public static final String TpaHelp = "&7/tpa &7<&aTarget&7> &7| Send teleport request to target.";
	public static final String SentRequestTPA = "%p has requested to teleport to you.";
	public static final String SentRequestTPAOpts = "&7/&atpaccept &7- &aAccept &7| &7/&ctpadeny &7- &cDeny";
	public static final String SentRequestToTarget = "&7Teleport request sent to &b%target&7.";
	public static final String TpadenyHelp = "&7/&ctpdeny &7 | Deny teleport request";
	public static final String Tpaccepted = "&7You have &aaccepted&7 the teleport request of &b%player&7.";
	public static final String TpacceptHelp = "&7/&atpaccept &7 | Accept teleport request";
	public static final String NoTpaRequests = "&7No teleport requests found.";
	public static final String TeleportIsCancelled = "&7Teleport has been cancelled by %player.";
	public static final String delayedTeleport = "&7Your teleport has been &ainitiated&7. &bStand still&7 for &a%time.";
	public static final String TryTpa = "&7You should maybe try &a/tpa";
	public static final String NickNameHelp = "&7/Nick <&aNewName&7/&bNone&7>";
	public static final String NameChanged = "&7Your nickname is now &a%name&7.";
	public static final String RollTheDice = "&6%player&7 has rolled the daily dice, and earned &6$%amount&7. &7Type /&aroll&7 to roll the daily dice.";
	public static final String RolledTheDailyDice = "&cYou have already rolled the magic dice for today.";
	public static final String YouCanRollDice = "&7You can roll the magic dice, for a daily reward. &7Type &6/roll&7 to roll.";
	public static final String PvPDisabled = "&cPlayer vs Player&7 is &cdisabled.&7 To re-enable it, type /PvP";
	public static final String PvEDisabled = "&cPlayer vs Entity&7 is &cdisabled.&7 To re-enable it, type /PvE";
	public static final String CannotToggleDueToAge = "You are unable to do this, since you are not new.";
	public static final String CurrentlyHasPvPDisabled = "&7%player currently has PvP disabled.";
	public static final String CurrentMode = "You currently have %mode %status. Type /%mode to toggle.";
	public static final String NoPackagesConfigured = "No packages have been configured yet.";
	public static final String ChosenPackage = "&7You have chosen the &a%name package&7.";
	public static final String PackageLink = "&7You may purchase the package here:";
	public static final String Link = "&7Link: &a%link";
	public static final String NickNameOthersHelp = "&7/Nick &7[&5target&7] <&aNewName&7/&bNone&7>";
	public static final String YouAreChallanged = "&7%challanger would like to challenge you to a duel.";
	public static final String ChallangedTarget = "&7You have challenged &6%target.";
	public static final String WelcomeToHub = ChatColor.RED + "Welcome to PvPKillz Servers!";
	public static final String HappyHalloween = "&6Happy Halloween,&7 <p>!";
	public static final String MerryChristmas = "&aM&ce&ar&cr&ay&c C&ah&cr&ai&cs&at&cm&aa&cs,&a <p>!";
	public static final String HappyNewYears = "Happy New Years, <p>!";
	public static final String EnglishWelcome = "&5Welcome to PvPKillz Hub!";
	public static final String ServerSignBroken = "The teleport sign has been destroyed.";
	public static final String ServerSignCreated = "Teleport sign to <server> has been created.";
	public static final String ServerIsFull = "Sorry, but this server is full.";
	public static final String ServerisOffline = "Sorry, but this server is restarting.";
	public static final String FullHelath = "&7You are at full health.";
	public static final String WorldSpawnSet = "This worlds spawn has been set to your location.";
	public static final String PlayerDoesNotExist = "Player '%player' does not exist.";
	public static final String AddedIgnorePlayer = "&cNow you are ignoring %player.";
	public static final String RemovedIgnorePlayer = "&aNow your are not ignoring %player.";
	public static final String BlockedFromFriends = "&7The player &c%recp has blocked you&7. Therefore no messages can be sent.";
	public static final String CommandIgnoreHelp = "/ignore &7<&cplayer&7>";
	public static final String AlreadyReferred = "This user is already referred...";
	public static final String ReachedMaxUnjoinedRefers = "You have reached the max unjoined referrals. Clear older ones to add new ones.";
	public static final String InviteYourFriend = "Now make sure that %friend joins the server, and plays.";
	public static final String PlayersKnowsServer = "This player already joined PvPKillz previously. Invite someone new.";
	public static final String DidnotInvite = "You have not invited anyone yet.";
	public static final String UnreferredUser = "You have unreferred %user.";
	public static final String ServersMenuTitle = "PvPKillz servers...";
	public static final String ServersExpandTitle = "%type servers...";
	public static final String InvalidLayout = "You have chosen an invalid layout.";
	public static final String InvalidServer = "You have chosen an invalid server.";
	public static final String ValidServers = "Here are some valid servers: %valid";
	public static final String ReturnToMainMenu = "Go to main menu.";
	public static final String Border = "&7&m--";
	public static final String SuccessPurchase = "You have successfully purchased this upgrade!";
	public static final String CannotAfford = "You need %diff Tokens to purchase this.";
	public static final String PurchaseSuccess = "&cYou have used $&7 Tokens.";
	public static final String EarnedPoints = "&7Earned &a$&7 Tokens";
	public static final String GhostModeToggled = "&7Ghostmode has been &6%mode.";
	public static final String GhostModePrecautions = "When your in Ghostmode the following apply to you:\n- Cannot interact with anything\n- Cannot use chat (Even pm's)\n - You do not appear in playerlist\n- Your name is shown as nothing.";
	public static final String CommandInvSeeHelp = "&7/&6invsee&7 <&5target&7>";
	public static final String FeedBackReplied = "&7You have recently sent us this feedback:\n%inquiry\n&aReply from staff:\n&6%reply.\n";
	public static final String StartedUUIDConvert = "Initiated the UUID convert system. Do not run again, without checking DB.";
	public static final String MessageTooLong = "&cError:&7 Message must be at max of 15 characters.";
	public static final String LookAtsign = "&cError:&7 You must be looking at a sign.";
	public static final String SignEditCommand = "&8/&7SignEdit &7<&bLine&7> <&aText&7>";
	public static final String SpawnEntityCommand = "&8/&7fakePlayer create <&aName&7> <&aType&7>";
	public static final String SpawnedFakeEntity = "&7A fake &b%type &7entity has been spawned under you.";
	public static final String failedTransaction = "&cError: &7Failed to do transaction. Try again at a different time.";
	public static final String ChooseAValidLineNumber = "&cError&7: Please choose a valid number; 1 - 4";
	public static final String HoldAnItemToModify = "Please hold an item to modify.";
	public static final String RenamingIsEmerald = "&cRenaming more than once is a &aEmerald feature&7. &7Type &a/Buy&7 to upgrade.";
	public static final String ChooseAName = "&6Choose a name for the item.";
	public static final String ItemRenamed = "Item name has been set.";
	public static final String ReDescingIsPvP = "&cChanging description more than once is a &aPvP feature&7. &7Type &a/Buy&7 to upgrade.";
	public static final String ItemReDesc = "Item description has been set.";
	public static final String ChooseADesc = "&6Choose a description for the item.";
	public static final String Perm1X5Fame = null;
	public static final String PermDoubleFame = null;
	public static final String EarnedFame = "You have earned %fame fame.";
	public static final String CompassName = "&aServer Selector &7[Right-Click]";
	public static final String CompassDesc = "&7A way to navigate the realm of PvPKillz.";
	public static final String ClockerName = "&aPlayer Toggler &7[Right-Click]";
	public static final String WardrobeName = "&aWardrobe &7[Right-Click]";
	public static final String WardrobeDesc = "&7A way to change your looks.";
	public static final String ClockerDesc = "&7A method of not seeing anyone.";
	public static String viewMoreInfoWebsite = "&7View more info on our website; &bhttp://PvP.kz";
	public static String failedToRetrieveInfo = "&cFailed to retrieve information...";
	public static String canSeeEveryone = "&aYou can now see all players.";
	public static String everyoneHidden = "&aAll players have been hidden.";
	public static String featurePvP = "&7This is a &aPvP feature&7, type &a/buy &7to become PvP rank!";
	public static String featureEmerald =  "&7This is a &aEmerald feature&7, type &a/buy &7to become Emerald rank!";;
	public Message (Main mainclass){
		plugin = mainclass;
	}

	static Logger log = Bukkit.getLogger();


	public static void G (String Message, boolean Prefix){
		Message = ChatColor.translateAlternateColorCodes('&', Message);

		if (Prefix == true)
			plugin.getServer().broadcastMessage(PREFIX + ChatColor.GREEN + Message);
		else
			plugin.getServer().broadcastMessage(ChatColor.GREEN + Message);
	}
	public static void P (Player p, String Message, boolean Prefix){
		Message = ChatColor.translateAlternateColorCodes('&', Message);

		if (!Prefix)
			p.sendMessage(ChatColor.GREEN  + Message);
		else
			p.sendMessage(PREFIX + Message);

		log.info(ChatColor.stripColor(PREFIX) + ChatColor.stripColor(Message) + " | sent to " + p.getName());
	}





	public static void NP (Player p, String Message, boolean Prefix){
		Message = ChatColor.translateAlternateColorCodes('&', Message);
		P(p, ChatColor.GREEN + Message, Prefix);
		log.info(ChatColor.stripColor(Message) + " | sent to " + p.getName());
	}


	public static void GPAll (Player p, String Message, boolean Prefix){
		Message = ChatColor.translateAlternateColorCodes('&', Message);

		for (Player pl : plugin.getServer().getOnlinePlayers()){
			if (pl != p)
				P(pl,ChatColor.GRAY + Message, Prefix);
		}
		log.info(ChatColor.stripColor(PREFIX) + ChatColor.stripColor(Message) + " | sent to all players.");
	}
	
	
	public static String Replacer(String fullstr, String replace, String find) {
		StringBuilder sb = new StringBuilder(fullstr);
		find = find.toLowerCase();
		if (StringUtils.contains(sb.toString().toLowerCase(), find))
			replace = StringUtils.replace(fullstr, find, replace);
		else
			replace = fullstr;
		replace = Colorize(replace);
		
		return replace;
	}
	
	public static String Replacer(String fullstr, String replace, String find, boolean color) {
		StringBuilder sb = new StringBuilder(fullstr);
		if (StringUtils.contains(sb.toString(), find))
			replace = StringUtils.replace(fullstr, find, replace);
		else
			replace = fullstr;
		if (color)
		replace = Colorize(replace);

		return replace;
	}
	
	
	public static String CleanCapitalize(String msg) {
		if (msg.contains("_"))
			msg = msg.replace('_', ' ');

		if (msg.contains("["))
			msg = msg.replace('[', ' ');

		if (msg.contains("]"))
			msg = msg.replace(']', ' ');


		msg = msg.toLowerCase().substring(0, 1).toUpperCase() + msg.toLowerCase().substring(1);

		return msg;
	}
	public static String Colorize(String string) {
		return ChatColor.translateAlternateColorCodes('&', string);
	}
	public static void PrintHelpMenu(Player p, String menuType) {
		Message.P(p, Message.HeaderMenu, false);
		if (menuType.equalsIgnoreCase("am")){
			Message.P(p, Message.MenuAM1, false);
			Message.P(p, Message.MenuAM2, false);
			Message.P(p, Message.MenuAM3, false);
			Message.P(p, Message.MenuAM4, false);
		}
		else if (menuType.equalsIgnoreCase("tm")){
			Message.P(p, Message.MenuTM1, false);
			Message.P(p, Message.MenuTM2, false);
			Message.P(p, Message.MenuTM3, false);
		}
		else if (menuType.equalsIgnoreCase("Contests")){
			Message.P(p, Message.MenuCont1, false);
			Message.P(p, Message.MenuCont2, false);
		}
		else if (menuType.equalsIgnoreCase("Referral")){
			Message.P(p, Message.MenuRefer1, false);
			Message.P(p, Message.MenuRefer2, false);
		}
		else if (menuType.equalsIgnoreCase("Teleports")){
			Message.P(p, Message.MenuTeleports1, false);
			Message.P(p, Message.MenuTeleports2, false);
			Message.P(p, Message.MenuTeleports3, false);
		}
		else if (menuType.equalsIgnoreCase("feedbacks")){
			Message.P(p, "&7/Feedbacks &areply &8<&7ID&8> &8<&7Message&8>", false);
			Message.P(p, "&7/Feedbacks &alist &8[&7Page&8]", false);
			Message.P(p, "&7/Feedbacks &areplies &8[&7Page&8]", false);
		}
		else if (menuType.equalsIgnoreCase("challengesCmds")){
			Message.P(p, "&b/challenge list &7 - View Challenges", false);
			Message.P(p, "&b/challenge completed &7 - View Completed Challenges", false);
			Message.P(p, "&b/challenge &7 - View Menu", false);
		}

	}
	public static void C(String msg) {
		log.log(Level.INFO, ChatColor.stripColor(Colorize(PREFIX + msg)));
	}
	public static void filterMessage(Player p, String var, Boolean prefix){
		String pName = p.getName();
		var = Message.Replacer(var, pName+"", "%name", true);
		var = Message.Replacer(var, pName+"", "%player", true);
		var = Message.Replacer(var, p.getDisplayName()+"", "%displayname", true);
		if (Main.UseMySQL){
			if (Main.KillStatsEnable){
				var = Message.Replacer(var, Stats.getTotalKills(pName)+"", "%totalkills", true);
				var = Message.Replacer(var, Stats.getTotalDeaths(pName)+"", "%totaldeaths", true);
				var = Message.Replacer(var, Stats.getKills(pName)+"", "%kills", true);
				var = Message.Replacer(var, Stats.getDeaths(pName)+"", "%deaths", true);
			}
			var = Message.Replacer(var, Stats.getTokens(pName)+"", "%tokens", true);
			var = Message.Replacer(var, Mysql.getUserID(pName)+"", "%playerid", true);
			var = Message.Replacer(var, Mysql.getVotes(pName, LengthType.days)+"", "%votes", true);
			var = Message.Replacer(var, Mysql.getVotes(pName, LengthType.months)+"", "%monthlyvotes", true);
			var = Message.Replacer(var, Mysql.getVotes(pName, LengthType.years)+"", "%totalvotes", true);
			var = Message.Replacer(var, Mysql.getTopVoter()+"", "%topvoter", true);
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
		
		P(p,var,prefix);
	}




}
