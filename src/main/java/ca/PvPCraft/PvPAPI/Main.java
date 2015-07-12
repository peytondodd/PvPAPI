package ca.PvPCraft.PvPAPI;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import ru.tehkode.permissions.bukkit.PermissionsEx;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ca.PvPCraft.PvPAPI.Commands.AdminCommands;
import ca.PvPCraft.PvPAPI.Commands.PlayerCommands;
import ca.PvPCraft.PvPAPI.enums.Challenge;
import ca.PvPCraft.PvPAPI.enums.Scoreboard_v1_7_R4;
import ca.PvPCraft.PvPAPI.enums.ServerInfo;
import ca.PvPCraft.PvPAPI.enums.SignInfo;
import ca.PvPCraft.PvPAPI.events.*;
import ca.PvPCraft.PvPAPI.methods.*;
//import ca.PvPCraft.PvPAPI.methods.scoreboards.protocol.Lang;
//import ca.PvPCraft.PvPAPI.methods.scoreboards.protocol.ReloadFixLoader;
//import ca.PvPCraft.PvPAPI.methods.scoreboards.protocol.SbManager;
//import ca.PvPCraft.PvPAPI.methods.scoreboards.protocol.Version;
import ca.PvPCraft.PvPAPI.methods.serverPinging.ServerSignManagement;
import ca.PvPCraft.PvPAPI.methods.serverPinging.hubMethods;
import ca.PvPCraft.PvPAPI.repeatingTasks.ScoreboardUpdater;
import ca.PvPCraft.PvPAPI.utilities.CustomSkin;
import ca.PvPCraft.PvPAPI.utilities.Files;
import ca.PvPCraft.PvPAPI.utilities.Message;
import ca.PvPCraft.PvPAPI.utilities.Mysql;
import ca.PvPCraft.PvPAPI.utilities.Stats;
import ca.PvPCraft.PvPAPI.utilities.Bar.BarAPI;



public class Main extends JavaPlugin implements PluginMessageListener {
	public static final int MaxRefers = 5;
	//private ReloadFixLoader classLoader;
	//private SbManager scoreboardManager;
	public static boolean SignTeleportsEnable = false;// Are sign teleports enabled?
	public static boolean TitlesEnabled = false;
	public static boolean ServersTeleportsEnable = false;// Teleports are enabled
	public static boolean scoreBoardEnabled = false;
	public static boolean permissionsEnabled = false;
	public static List<String> scoreBoardTitles = new ArrayList<String>();
	public static String NickPrefix = null;
	public static String PackageWebsite = null;
	public static int DelayedTeleport = 0;// Delay on teleport...
	public static boolean ProtocolLibEnable = false;// Enable ProtocolLib
	public static boolean TagAPIEnable = false;// Enable TagAPI
	public static boolean TabAPIEnable = false;// Enable TabAPI
	public static String Server = null;// Server this is on...
	public static boolean EconomyEnable = false;// Enable Economy
	public static boolean FactionsEnabled = false;// Is Factions plugin enabled?
	public static boolean TutorialEnabled = false;// Is tutorial mode enabled?
	public static boolean WorldEditEnable = false;// Is worldedit enabled?
	public static String TutorialColumnName = null;//FinishedTutorialCreative
	public static boolean KillStatsEnable = false;//Kills Statistics?
	public static boolean DisableDrops = false;//Disable Players Drops and any dropping abilities.
	public static boolean DailyRoll = false;
	public static String DBUser = null;
	public static String DBPass = null;
	public static String DBName = null;
	public static String DBHost = null;
	public static boolean useWeaponName = false;
	public static boolean UseMySQL;
	public static boolean enableNPC = false;
	public static boolean JoinMessage = true;
	public static boolean QuitMessage = true;
	public static List<String> Announcements = new ArrayList<String>();
	public static List<String> Titles = new ArrayList<String>();
	public static List<String> BannedCommands = new ArrayList<String>();
	public static List<String> preexists = new ArrayList<String>();
	public static HashMap<String, String> aliasCommands = new HashMap<String, String>();// List of commands which rework to different commands...
	public static HashMap<Integer, ArrayList<String>> TabList = new HashMap<Integer, ArrayList<String>>();// List of commands which rework to different commands...
	public static HashMap<String, ServerInfo> ServerList = new HashMap<String, ServerInfo>();// List of commands which rework to different commands...
	public static boolean enableJoinPotionEffects = false;
	public static ArrayList<PotionEffect> PotionEffects = new ArrayList<PotionEffect>();
	public static HashMap<String, ArrayList<String>> signLayouts = new HashMap<String, ArrayList<String>>();// List of commands which rework to different commands...
	public static boolean BanManageEnable = false;
	public static List<Integer> filterDrops = new ArrayList<Integer>();
	public static List<String> motdList = new ArrayList<String>();
	public static boolean enableOverhead = false;
	public static String scoreboardTitle = "";
	public static int DefaultDelay = 0;
	public static double DefaultTitleDelay = 0;
	// Faction Settings
	public static int FactionsMoneyPercentLoss = 0;
	public static BukkitTask AnnouncementTask;
	public static BukkitTask TitlesTask;
	public static BukkitTask ScoreboardTask;
	public static BukkitTask SignsUpdatingTask;
	public static BukkitTask updaterTask;
	public static Main plugin;
	public static CustomSkin factory;
	public static String serverVer = "";	
	public static HashMap<String, Integer> list = new HashMap<String, Integer>();
	public static HashMap<Integer, Challenge> challenges = null;

	public void onEnable() {
		new Files(this);
		new Message(this);
		new PlayerCommands(this);
		new AdminCommands(this);
		new BlockingCommands(this);
		new ChatListener(this);
		new DamageListener(this);
		new DeathListener(this);
		new JoinListener(this);
		new MoveListener(this);
		new InteractListener(this);
		new PlayersInfo(this);
		new Mysql(this);
		refreshConfigEntries(this);
		new Locations(this);
		new TeleportListener(this);
		new bandaidListener(this);
		new Stats(this);
		new Updater(this);
		new Scoreboards(this);
		new BarAPI(this);
		new Scoreboard_v1_7_R4(this);
		serverVer = this.getServer().getBukkitVersion();
		factory = new CustomSkin(this);

		if (SignTeleportsEnable){
			Files.loadDelayedSignFiles();
			new SignListener(this);

			new ServerSignManagement(this);
			new hubMethods(this);


			FileConfiguration file = Files.servers.getCustomConfig();
			for (String servers : file.getKeys(false)){
				ServerInfo server = getServerInfo(servers, servers);

				ServerList.put(servers, server);

				if (file.contains(servers + ".Subservers")){
					for (String subServer : file.getConfigurationSection(servers + ".Subservers").getKeys(false)){
						ServerList.get(servers).addSubserver(subServer, getServerInfo(servers + ".Subservers." + subServer, subServer));
					}
				}
			}

			file = Files.layouts.getCustomConfig();
			for (String layout : file.getKeys(false)){
				ArrayList<String> LayoutInfo = (ArrayList<String>) file.getStringList(layout);

				signLayouts.put(layout, LayoutInfo);
			}



			if (SignsUpdatingTask != null){
				if (this.getServer().getScheduler().isCurrentlyRunning(SignsUpdatingTask.getTaskId()))
					this.getServer().getScheduler().cancelTask(SignsUpdatingTask.getTaskId());
			}
			SignsUpdatingTask = new ca.PvPCraft.PvPAPI.repeatingTasks.ServerPingingTask().runTaskTimerAsynchronously(this, 1 * 20L, 1 * 20L);
		}


		plugin = this;
		getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
		Plugin protocolLib = this.getServer().getPluginManager().getPlugin("ProtocolLib");
		Plugin factions = this.getServer().getPluginManager().getPlugin("Factions");
		Plugin tab = this.getServer().getPluginManager().getPlugin("TabAPI");
		Plugin tag = this.getServer().getPluginManager().getPlugin("TagAPI");
		Plugin vault = this.getServer().getPluginManager().getPlugin("Vault");
		Plugin worldEdit = this.getServer().getPluginManager().getPlugin("WorldEdit");
		Plugin essentials = this.getServer().getPluginManager().getPlugin("Essentials");

		Plugin banManage = this.getServer().getPluginManager().getPlugin("BanManager");
		Plugin pexPerms = this.getServer().getPluginManager().getPlugin("PermissionsEx");

		if (pexPerms == null)
			permissionsEnabled = false;
		else
			permissionsEnabled = true;

		if (banManage == null)
			BanManageEnable = false;
		else{
			BanManageEnable = true;
			new BanManage(this);
		}

		if (protocolLib == null){
			Message.C("ProtocolLib was not found...");
			ProtocolLibEnable = false;
		}
		else{
			ProtocolLibEnable = true;
			new ProtocolLib(this);
		}


		if (factions == null)
			FactionsEnabled = false;
		else{
			FactionsEnabled = true;
			new FactionsHook(this);
		}


		if (protocolLib == null || tab == null)
			TabAPIEnable = false;
		else{
			TabAPIEnable = true;
			new Tab(this);
		}

		if (protocolLib == null || tag == null)
			TagAPIEnable = false;
		else{
			TagAPIEnable = true;
			new Tag(this);
		}


		if (vault == null || essentials == null)
			EconomyEnable = false;
		else{
			EconomyEnable = true;
			new EconSystem(this);
		}



		if (worldEdit == null)
			WorldEditEnable = false;
		else{
			WorldEditEnable = true;
			new WorldEditHook(this);
		}

		RegisterCommands();

		for (Player p : Bukkit.getOnlinePlayers())
			preexists.add(p.getName());
		this.getServer().getMessenger().registerIncomingPluginChannel(this, "PvPAPI", this);


		if (Server.equalsIgnoreCase("Faction")){
			ResultSet tableLookup = Mysql.PS.getSecureQuery("SELECT * FROM information_schema.TABLES WHERE TABLE_SCHEMA = '" + Mysql.getDatabaseName() + "' AND TABLE_NAME = '" + Main.Server + "_Challenges'");
			challenges = new HashMap<Integer, Challenge>();
			Challenge c = null;
			c = new Challenge("Faction Rookie", "Have %valF players from faction on."); c.addRequirement("Faction Players Online;5"); c.addReward("Money;5000");
			challenges.put(100, c);
			c = new Challenge("Faction Smarts", "Have %valF players from faction on."); c.addRequirement("Faction Players Online;10"); c.addReward("Money;1000");
			challenges.put(101,c);
			c = new Challenge("Faction Expert", "Have %valF players from faction on."); c.addRequirement("Faction Players Online;15"); c.addReward("Money;15000");
			challenges.put(102, c);
			c = new Challenge("Contributor", "Regain personal power to full power."); c.addRequirement("Personal Power"); c.addReward("Money;7500"); c.addReward("Power Boost;2");
			challenges.put(105, c);
			c = new Challenge("Elite Faction", "Regain full faction's power"); c.addRequirement("Faction Power"); c.addReward("Money;7500"); c.addReward("Power Boost;4");
			challenges.put(106, c);
			c = new Challenge("Legendary kingdom", "Claim %valF faction land."); c.addRequirement("Faction Land;40"); c.addReward("Money;10000");
			challenges.put(120, c);
			c = new Challenge("Nearly a kingdom", "Claim %valF faction land."); c.addRequirement("Faction Land;30"); c.addReward("Money;7500");
			challenges.put(121, c);
			c = new Challenge("Getting there...", "Claim %valF faction land."); c.addRequirement("Faction Land;20"); c.addReward("Money;5000");
			challenges.put(122, c);
			c = new Challenge("Starting the kingdom", "Claim %valF faction land."); c.addRequirement("Faction Land;10"); c.addReward("Money;2500");
			challenges.put(123, c);
			c = new Challenge("Enemy Aggressor", "Enter enemy faction's land."); c.addRequirement("Enter Enemy Land"); c.addReward("Money;9000");
			challenges.put(130, c);
			c = new Challenge("Name in Disguise", "Claim a personal title in faction."); c.addRequirement("Custom Title"); c.addReward("Money;4000");
			challenges.put(131, c);
			c = new Challenge("Starting Killer", "Get %valT total kills."); c.addRequirement("Total Kills;1"); c.addReward("Money;1000");
			challenges.put(140, c);
			c = new Challenge("Intermediate Killer", "Get %valT total kills."); c.addRequirement("Total Kills;5"); c.addReward("Money;3000");
			challenges.put(141, c);
			c = new Challenge("Aware Killer", "Get %valT total kills."); c.addRequirement("Total Kills;25"); c.addReward("Money;7500");
			challenges.put(142, c);
			c = new Challenge("Secret Killer", "Get %valT total kills."); c.addRequirement("Total Kills;50"); c.addReward("Money;12500");
			challenges.put(143, c);
			c = new Challenge("Assassin", "Get %valT total kills."); c.addRequirement("Total Kills;150"); c.addReward("Money;20000");
			challenges.put(144, c);
			try {
				if (tableLookup.next() == false)
					Mysql.PS.getSecureQuery("CREATE TABLE `" + Main.Server + "_Challenges` ( " +
							"ID INT AUTO_INCREMENT, " +
							"UserID INT NULL, " + 
							"Challenge_ID INT NULL, " +
							"TimeComplete INT NULL, " +
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
			this.getServer().getScheduler().runTaskTimerAsynchronously(this, new Runnable(){
				@Override
				public void run() {
					for (Player p : getServer().getOnlinePlayers()){
						PlayersInfo.checkChallenges(p.getName());
					}
				}
			}, 60L, 60L);
		}
	}

	@SuppressWarnings("deprecation")
	private ServerInfo getServerInfo(String path, String name) {
		FileConfiguration file = Files.servers.getCustomConfig();

		String dispName = null;
		String bungName = null;
		int starCount = 0;
		int position = 0;
		String iconMenu = null;
		String hostString = null;
		List<String> description = null;

		if (file.contains(path + ".displayName"))
			dispName = file.getString(path + ".displayName");
		if (file.contains(path + ".bungeeName"))
			bungName = file.getString(path + ".bungeeName");
		if (file.contains(path + ".starCount"))	
			starCount = file.getInt(path + ".starCount");
		if (file.contains(path + ".position"))	
			position = file.getInt(path + ".position");

		if (file.contains(path + ".IconMenu"))
			iconMenu = file.getString(path + ".IconMenu");
		if (file.contains(path + ".Host"))
			hostString = file.getString(path + ".Host");
		if (file.contains(path + ".Description"))
			description = file.getStringList(path + ".Description");

		InetSocketAddress IP = null;

		if (hostString != null){
			if (hostString.contains(":")){
				String[] hostedinfo = hostString.split(":");
				int Port = Integer.parseInt(hostedinfo[1]);
				String Ip = hostedinfo[0];

				IP = new InetSocketAddress(Ip, Port);

			}
			else
				IP = new InetSocketAddress(hostString, 25565);
		}

		int ItemID = 0;
		short ItemData = 0;

		if (iconMenu.contains(":")){
			String[] split = iconMenu.split(":");
			ItemID = Integer.parseInt(split[0]);
			ItemData = Short.parseShort(split[1]);
		}
		else
			ItemID = Integer.parseInt(iconMenu);
		ItemStack IconItem = new ItemStack(Material.getMaterial(ItemID));
		IconItem.setDurability(ItemData);


		ServerInfo info = new ServerInfo(name, dispName, bungName, description, null, IconItem, starCount);
		info.setHost(IP);
		info.setPosition(position);
		if (file.contains(path + ".Signs")){
			for (String signID : file.getConfigurationSection(path + ".Signs").getKeys(false)){
				Location signLoc = Locations.getLocOfString(path + ".Signs." + signID, Files.servers);
				String layout = file.getConfigurationSection(path + ".Signs." + signID).getString("Layout");

				info.getSigns().add(new SignInfo(signLoc, layout, name, Integer.parseInt(signID)));
			}
		}


		return info;
	}

	private void RegisterCommands() {
		getCommand("feedback").setExecutor(new PlayerCommands(this));
		getCommand("tell").setExecutor(new PlayerCommands(this));
		getCommand("reply").setExecutor(new PlayerCommands(this));
		if (Main.TutorialEnabled)
			getCommand("tutorial").setExecutor(new PlayerCommands(this));
		getCommand("tp").setExecutor(new PlayerCommands(this));
		getCommand("tpall").setExecutor(new PlayerCommands(this));
		getCommand("contests").setExecutor(new PlayerCommands(this));
		getCommand("socialspy").setExecutor(new AdminCommands(this));
		getCommand("gm").setExecutor(new AdminCommands(this));
		getCommand("teleports").setExecutor(new AdminCommands(this));
		getCommand("am").setExecutor(new AdminCommands(this));
		getCommand("tm").setExecutor(new AdminCommands(this));
		getCommand("tpa").setExecutor(new PlayerCommands(this));
		getCommand("tpaccept").setExecutor(new PlayerCommands(this));
		getCommand("tpdeny").setExecutor(new PlayerCommands(this));
		getCommand("nickname").setExecutor(new PlayerCommands(this));
		getCommand("pve").setExecutor(new PlayerCommands(this));
		getCommand("pvp").setExecutor(new PlayerCommands(this));
		if (Main.DailyRoll)
			getCommand("roll").setExecutor(new PlayerCommands(this));
		getCommand("say").setExecutor(new AdminCommands(this));
		getCommand("fakeplayer").setExecutor(new AdminCommands(this));
		getCommand("setworldspawn").setExecutor(new AdminCommands(this));
		getCommand("buy").setExecutor(new PlayerCommands(this));
		if (Main.ServersTeleportsEnable)
			getCommand("servers").setExecutor(new PlayerCommands(this));
		getCommand("stats").setExecutor(new PlayerCommands(this));
		getCommand("topstats").setExecutor(new PlayerCommands(this));

		getCommand("nameitem").setExecutor(new PlayerCommands(this));
		getCommand("descitem").setExecutor(new PlayerCommands(this));
		getCommand("enchantfake").setExecutor(new PlayerCommands(this));

		getCommand("refer").setExecutor(new PlayerCommands(this));
		getCommand("changeskin").setExecutor(new PlayerCommands(this));
		getCommand("ignore").setExecutor(new PlayerCommands(this));
		getCommand("ghostmode").setExecutor(new AdminCommands(this));
		getCommand("invsee").setExecutor(new AdminCommands(this));
		getCommand("convertids").setExecutor(new AdminCommands(this));
		getCommand("signedit").setExecutor(new AdminCommands(this));
		getCommand("feedbacks").setExecutor(new AdminCommands(this));
		getCommand("challenge").setExecutor(new PlayerCommands(this));

	}
	public void onDisable(){

		for (Player player : plugin.getServer().getOnlinePlayers()) {
			BarAPI.quit(player);
		}
		BarAPI.players.clear();

		for (int timerID : BarAPI.timers.values()) {
			Bukkit.getScheduler().cancelTask(timerID);
		}

		BarAPI.timers.clear();

		/*
		if (Mysql.ServerCon != null){
			try {
				if (!Mysql.ServerCon.isClosed())
					Mysql.ServerCon.close();
			} catch (SQLException e) {System.out.println("No database connection established.");}
		}
		 */
	}

	public static void refreshConfigEntries(Main plugin) {
		// TODO Auto-generated method stub
		FileConfiguration conf = Files.config.getCustomConfig();
		String ConfigSetting = "";

		ConfigSetting = "Factions.moneyLossPercent";
		if (conf.contains(ConfigSetting))
			FactionsMoneyPercentLoss = conf.getInt(ConfigSetting);


		ConfigSetting = "Global.JoinMsg";
		if (conf.contains(ConfigSetting))
			JoinMessage = conf.getBoolean(ConfigSetting);

		ConfigSetting = "Global.QuitMsg";
		if (conf.contains(ConfigSetting))
			QuitMessage = conf.getBoolean(ConfigSetting);

		ConfigSetting = "Global.delayedTeleport";
		if (conf.contains(ConfigSetting))
			DelayedTeleport = conf.getInt(ConfigSetting);


		ConfigSetting = "Global.ServerName";
		if (conf.contains(ConfigSetting))
			Server = conf.getString(ConfigSetting);




		ConfigSetting = "Global.ShopWebsite";
		if (conf.contains(ConfigSetting))
			PackageWebsite = conf.getString(ConfigSetting);


		ConfigSetting = "Announcements.Messages";
		if (conf.contains(ConfigSetting))
			Announcements = conf.getStringList(ConfigSetting);


		ConfigSetting = "Titles.Enable";
		if (conf.contains(ConfigSetting))
			TitlesEnabled = conf.getBoolean(ConfigSetting);

		ConfigSetting = "Titles.Messages";
		if (conf.contains(ConfigSetting))
			Titles = conf.getStringList(ConfigSetting);

		ConfigSetting = "Titles.DefaultDelay";
		if (conf.contains(ConfigSetting))
			DefaultTitleDelay = conf.getDouble(ConfigSetting);


		ConfigSetting = "Announcements.DefaultDelay";
		if (conf.contains(ConfigSetting))
			DefaultDelay = conf.getInt(ConfigSetting);

		ConfigSetting = "Rewards.DailyRoll";
		if (conf.contains(ConfigSetting))
			DailyRoll = conf.getBoolean(ConfigSetting);


		ConfigSetting = "Tutorials.Enabled";
		if (conf.contains(ConfigSetting))
			TutorialEnabled = conf.getBoolean(ConfigSetting);

		ConfigSetting = "Tutorials.ColumnName";
		if (conf.contains(ConfigSetting))
			TutorialColumnName = conf.getString(ConfigSetting);

		ConfigSetting = "Global.NickNamePrefix";
		if (conf.contains(ConfigSetting))
			NickPrefix = conf.getString(ConfigSetting);


		ConfigSetting = "Drops.Disabled";
		if (conf.contains(ConfigSetting))
			DisableDrops = conf.getBoolean(ConfigSetting);
		else
			conf.set(ConfigSetting, false);


		ConfigSetting = "PotionEffects.Enable";
		if (conf.contains(ConfigSetting))
			enableJoinPotionEffects = conf.getBoolean(ConfigSetting);
		else
			conf.set(ConfigSetting, false);

		if (enableJoinPotionEffects){
			ConfigSetting = "PotionEffects.PotionsOnJoin";
			if (conf.contains(ConfigSetting)){
				List<String> peEffects = conf.getStringList(ConfigSetting);
				for (String peEffect : peEffects){
					String[] splitUpPotion = peEffect.split(",");

					int ID = 0;
					int Duration = 0;
					int Amplifier = 0;
					ID = Integer.parseInt(splitUpPotion[0]);
					Duration = Integer.parseInt(splitUpPotion[1]);
					Amplifier = Integer.parseInt(splitUpPotion[2]);

					PotionEffects.add(new PotionEffect(PotionEffectType.getById(ID), Duration, Amplifier));
				}
			}
			else
				conf.set(ConfigSetting, new ArrayList<String>());
		}



		ConfigSetting = "Drops.Filter";
		if (conf.contains(ConfigSetting))
			filterDrops = conf.getIntegerList(ConfigSetting);
		else
			conf.set(ConfigSetting, new ArrayList<Integer>());


		ConfigSetting = "Global.EnableServerTeleports";
		if (conf.contains(ConfigSetting))
			ServersTeleportsEnable = conf.getBoolean(ConfigSetting);
		else
			conf.set(ConfigSetting, false);


		ConfigSetting = "Scoreboard.Title";
		if (conf.contains(ConfigSetting))
			scoreboardTitle = conf.getString(ConfigSetting);
		else
			conf.set(ConfigSetting, "PvPKillz");

		ConfigSetting = "Scoreboard.enableOverhead";
		if (conf.contains(ConfigSetting))
			enableOverhead = conf.getBoolean(ConfigSetting);
		else
			conf.set(ConfigSetting, false);

		ConfigSetting = "Scoreboard.Enable";
		if (conf.contains(ConfigSetting))
			scoreBoardEnabled = conf.getBoolean(ConfigSetting);
		else
			conf.set(ConfigSetting, false);


		ConfigSetting = "Scoreboard.Titles";
		if (conf.contains(ConfigSetting))
			scoreBoardTitles = conf.getStringList(ConfigSetting);
		else
			conf.set(ConfigSetting, new ArrayList<String>());

		ConfigSetting = "Global.BlockedCommands";
		if (conf.contains(ConfigSetting))
			BannedCommands = conf.getStringList(ConfigSetting);
		else
			conf.set(ConfigSetting, new ArrayList<String>());

		ConfigSetting = "Global.AliasCommands";
		if (conf.contains(ConfigSetting)){
			List<String> AliasCommandsList = conf.getStringList(ConfigSetting);
			for (String commandSetup : AliasCommandsList){
				if (commandSetup.contains(":")){
					String oldCommand = commandSetup.split(":")[0];
					String newCommand = commandSetup.split(":")[1];
					aliasCommands.put(oldCommand, newCommand);
				}
			}
		}
		else
			conf.set(ConfigSetting, new ArrayList<String>());

		ConfigSetting = "MOTD";
		if (conf.contains(ConfigSetting))
			motdList = conf.getStringList(ConfigSetting);


		ConfigSetting = "Global.EnableSignTeleports";
		if (conf.contains(ConfigSetting))
			SignTeleportsEnable = conf.getBoolean(ConfigSetting);
		else
			conf.set(ConfigSetting, false);


		ConfigSetting = "Global.EnableKillsStats";
		if (conf.contains(ConfigSetting))
			KillStatsEnable = conf.getBoolean(ConfigSetting);
		else
			conf.set(ConfigSetting, false);


		ConfigSetting = "Global.NickNamePrefix";
		if (conf.contains(ConfigSetting))
			NickPrefix = conf.getString(ConfigSetting);
		else
			conf.set(ConfigSetting, "≈");
		if (!Announcements.isEmpty()){
			if (AnnouncementTask != null){
				if (plugin.getServer().getScheduler().isCurrentlyRunning(AnnouncementTask.getTaskId()))
					plugin.getServer().getScheduler().cancelTask(AnnouncementTask.getTaskId());
			}
			AnnouncementTask = new ca.PvPCraft.PvPAPI.repeatingTasks.Announcements().runTaskTimerAsynchronously(plugin, Main.DefaultDelay * 20L, Main.DefaultDelay * 20L);
		}
		if (Main.scoreBoardEnabled){
			if (ScoreboardTask != null){
				if (plugin.getServer().getScheduler().isCurrentlyRunning(ScoreboardTask.getTaskId()))
					plugin.getServer().getScheduler().cancelTask(ScoreboardTask.getTaskId());
			}
			ScoreboardTask = new ScoreboardUpdater().runTaskTimerAsynchronously(plugin, 20L, 20L);
		}


		if (updaterTask != null){
			if (plugin.getServer().getScheduler().isCurrentlyRunning(updaterTask.getTaskId()))
				plugin.getServer().getScheduler().cancelTask(updaterTask.getTaskId());
		}
		updaterTask = new ca.PvPCraft.PvPAPI.repeatingTasks.PluginUpdater().runTaskTimerAsynchronously(plugin, 20L * 30 * 1, 20L * 30 * 1);



		if (Main.TitlesEnabled){
			if (TitlesTask != null){
				if (plugin.getServer().getScheduler().isCurrentlyRunning(TitlesTask.getTaskId()))
					plugin.getServer().getScheduler().cancelTask(TitlesTask.getTaskId());
			}
			TitlesTask = new ca.PvPCraft.PvPAPI.repeatingTasks.Titles().runTaskTimerAsynchronously(plugin, (int)(Main.DefaultTitleDelay * 20L), (int)(Main.DefaultTitleDelay * 20L));
		}

		if (UseMySQL == false){
			UseMySQL = conf.getBoolean("Mysql.Enable");

			if (UseMySQL == true){
				DBHost = conf.getString("Mysql.Host");
				DBName = conf.getString("Mysql.TableName");
				DBUser = conf.getString("Mysql.User");
				DBPass = conf.getString("Mysql.Password");
				Mysql.ConnectToDB();
			}
		}
		Files.config.saveCustomConfig();
	}
	/*
	public ReloadFixLoader getClassLoaderBypass() {
        return classLoader;
    }
    private void checkScoreboardCompatibility() {
        final int compare = Version.compare("1.5", Version.getMinecraftVersionString());
        if (compare >= 0) {
            //The minecraft version is higher or equal the minimum scoreboard version
            return;
        }

        getLogger().warning(Lang.get("noCompatibleVersion"));
        //This plugin isn't compatible with the server version so we disabling it
        getPluginLoader().disablePlugin(this);
    }

    private void checkCompatibility() {
        //Inform the user that he should use compatibility modus to be compatible with some plugins
        getLogger().info("The plugin will now use raw packets to be compatible with other scoreboard plugins");
    }
	 */

	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] bytes) {
		if (!channel.equals("PvPAPI")) {
			return;
		}

		ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
		DataInputStream in = new DataInputStream(stream);
		String data;
		try {
			data = in.readUTF();
			if (data.contains("Voted3:")){
				String playerName = data.split(":")[1];
				Player p = PlayersInfo.getPlayer(playerName);
				PermissionsEx.getPermissionManager().clearUserCache(p);
				if (!PermissionsEx.getUser(p).inGroup("Voter")){
					PermissionsEx.getUser(p).addGroup("Voter", null, 86400);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}