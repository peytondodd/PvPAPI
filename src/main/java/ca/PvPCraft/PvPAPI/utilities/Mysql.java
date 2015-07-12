package ca.PvPCraft.PvPAPI.utilities;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.TimeZone;
import java.util.UUID;
import java.util.logging.Logger;

import ca.PvPCraft.PvPAPI.Main;
import ca.PvPCraft.PvPAPI.enums.LengthType;
import ca.PvPCraft.PvPAPI.methods.ConvertTimings;
import ca.PvPCraft.PvPAPI.methods.EconSystem;
import ca.PvPCraft.PvPAPI.methods.PlayersInfo;
import ca.PvPCraft.PvPAPI.utilities.MySQL.MySQL;

public class Mysql {

	static Main plugin;
	static Mysql instance;
	//public static BoneCP ServerCon;
	static MySQL ServerCon = null;
	public static Integer task = 0;
	public Mysql(Main mainclass) {
		plugin = mainclass;
		instance = this;
	}

	public static int getUserID(String p) {
		int userid = 0;
		try {
			UUID UUID = null;


			if (Bukkit.getPlayer(p) != null){
				UUID = Bukkit.getPlayer(p).getUniqueId();
			}
			else
				UUID = UUIDFetcher.getUUIDUser(p);


			if (!PlayersInfo.PlayerIDs.containsKey(p)){
				ResultSet res;
				if (UUID != null || Bukkit.getPlayer(p).getUniqueId() != null){// If we found a record of the user in the database we set userid to the id we found also if we get the only entry, by checking if we have last and final entry as the same entry.

					if (Bukkit.getPlayer(p).getUniqueId() != null){
						UUID = Bukkit.getPlayer(p).getUniqueId();
					}


					res = PS.getSecureQuery("SELECT * FROM Users WHERE LOWER(UUID) = LOWER(?) LIMIT 1", ""+UUID);
					if (res.next()){
						userid = Integer.parseInt(res.getString("ID"));// We parse the string
						PlayersInfo.PlayerIDs.put(p, userid);
					}
					else{
						Mysql.PS.getSecureQuery("INSERT INTO Users (Username, UUID) VALUES (?, ?)", p, ""+UUID);
						ResultSet rs = Mysql.PS.getSecureQuery("SELECT * FROM Users WHERE UUID = ?", ""+UUID);

						if (rs.next())
							userid = res.getInt("ID");
					}
					PlayersInfo.PlayerUUIDs.put(p, UUID);
				}
				else {
					res = PS.getSecureQuery("SELECT * FROM Users WHERE LOWER(Username) = LOWER(?) LIMIT 1", ""+p);

					if (res.next()){// If we found a record of the user in the database we set userid to the id we found.
						userid = Integer.parseInt(res.getString("ID"));// We parse the string
						if (java.util.UUID.fromString(res.getString("UUID")) == null){
							UUID = java.util.UUID.randomUUID();
							PS.getSecureQuery("UPDATE Users SET UUID = ? WHERE Username = ? AND ID = ?", UUID.toString(), p, ""+userid);
						}
						else
							UUID = java.util.UUID.fromString(res.getString("UUID"));

						PlayersInfo.PlayerIDs.put(p, userid);
						PlayersInfo.PlayerUUIDs.put(p, UUID);
					}
					else{
						userid = 0;// We set the userid to 0 since they don't have an ID assigned.
						UUID = Bukkit.getPlayer(p).getUniqueId();

						if (UUID == null){
							UUID = java.util.UUID.randomUUID();
						}
						Mysql.PS.getSecureQuery("INSERT INTO Users (Username, UUID) VALUES (?, ?)", p, ""+UUID);
						ResultSet rs = Mysql.PS.getSecureQuery("SELECT * FROM Users WHERE UUID = ?", ""+UUID);
						if (rs.next())
							userid = res.getInt("ID");
					}
				}
				if (userid != 0) {
					ResultSet rss = Mysql.PS.getSecureQuery("SELECT * FROM UserInfo WHERE User = ?", ""+userid);

					if (!rss.next()) {
						Player pl = plugin.getServer().getPlayerExact(p);
						PS.getSecureQuery("INSERT INTO UserInfo (User, JoinDate, LastLogin) VALUES (?,?,?)", ""+userid, ""+System.currentTimeMillis() / 1000, ""+pl.getAddress().getAddress().getHostAddress());
					}
				}
			}
			else
				userid = PlayersInfo.PlayerIDs.get(p);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return userid;// We return the ID.
	}

	// User's Name, The change in points, RewardType -- rewardtype is an Enum, If you don't know what that is, it is basically a new variable type, with pre-defined variable values.
	public static boolean modifyTokens (String p, int Change, boolean PrintMessage) throws SQLException{
		// We will be editing the user's points...

		int Player = Mysql.getUserID (p);// We grab the ID | We use ID's instead of names.. Why? It saves a lot of space to save Integers than Strings.

		int curPts = Stats.getTokens(p);// We grab their current points from the method above.
		Player user = Bukkit.getPlayerExact(p);// We get the actual player, so we can send them messages later

		if (Change < 0){// We want to take away coins
			if(curPts < Math.abs(Change)){// Not enough coins
				if (PrintMessage)
					Message.P(user, Message.Replacer(Message.CannotAfford, "" + Math.abs(Stats.getTokens(p) - Math.abs(Change)), "%diff"), true);
				return false;// User does not have enough money, so we don't continue and return false.
			}
			else if (curPts >= Math.abs(Change)){// Enough coins met
				int newPts = curPts - Math.abs(Change);
				Stats.Tokens.put(p, newPts);
				if (PrintMessage)
					Message.P(user, Message.Replacer(Message.PurchaseSuccess, "" + Math.abs(Change), "$"), true);
				// We message them   >> I created my own Message Class I use to process all Messages. Why? It allows me to format them easily and makes it easier for me to localize my plugin into other languages
				Mysql.PS.getSecureQuery("UPDATE Points SET Points = ? WHERE ID = ?", ""+newPts, ""+Player);// We update the points

				return true;// We return that the user was able to complete a transaction without issues.
			}

		}
		else if (Change >= 0){// We want to give coins
			int newPts = curPts + Math.abs(Change);// We get the current amount of points and add the new value. ABS is useless, but ehh keep it.

			// Same as Above...
			Mysql.PS.getSecureQuery("UPDATE Points SET Points = ? WHERE ID = ?", ""+newPts, ""+Player);

			Stats.Tokens.put(p, newPts);

			// We message the user of them earning points.
			if (PrintMessage)
				Message.P(user, Message.Replacer(Message.EarnedPoints, "" + Math.abs(Change), "$"), true);
			return true;
		}
		return false;
	}


	public static void addGameSession(String p, long start){
		ResultSet rs = PS.getSecureQuery("INSERT INTO gameSessions (User, Start, End, Server) VALUES (?,?,?,?)", Mysql.getUserID(p)+"", start+"", ""+System.currentTimeMillis()/1000, Main.Server);
		try {
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


	public static long gamingLength(String p){
		ResultSet rs = PS.getSecureQuery("SELECT * FROM gameSessions WHERE User = ? AND Server = ?", Mysql.getUserID(p)+"", Main.Server);
		long length = 0;
		try {
			while (rs.next()){
				length += (rs.getLong("End") - rs.getLong("Start"));

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
		return length;
	}




	public static int joinedDateAgo(String p){
		final long DAY_IN_MILLIS = 60 * 60 * 24;
		ResultSet rs = PS.getSecureQuery("SELECT * FROM UserInfo WHERE User = ?",  ""+getUserID(p));

		ResultSet columnLookup = Mysql.PS.getSecureQuery("SELECT * FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = '" + Mysql.getDatabaseName() + "' AND TABLE_NAME = 'UserInfo' AND COLUMN_NAME = '" + Main.Server + "_JoinDate'");
		try {
			if (columnLookup.next() == false)
				Mysql.PS.getSecureQuery("ALTER TABLE UserInfo ADD '" + Main.Server + "_JoinDate' VARCHAR(60)");
		} catch (SQLException e) {e.printStackTrace();}
		int diffInDays = 0;
		try {
			if (rs.next())
				diffInDays = (int) (((System.currentTimeMillis()/1000)/DAY_IN_MILLIS - rs.getInt(Main.Server + "_JoinDate"))/ DAY_IN_MILLIS);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return diffInDays;
	}


	public static int retrieveTokens(String name) {
		int userpts = 0;
		ResultSet rs = null;
		try {
			rs = Mysql.PS.getSecureQuery("SELECT * FROM Points WHERE ID = ? LIMIT 1", ""+getUserID(name));
			if (rs.next())
				userpts = Integer.parseInt(rs.getString("Points"));
			else 
				PS.getSecureQuery("INSERT INTO Points (ID) VALUES (?)", ""+getUserID(name));
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return userpts;
	}

	public static String getClosestPlayer(String partName) {
		ResultSet rs = Mysql.PS.getSecureQuery("SELECT * FROM Users WHERE LOWER(Username) LIKE LOWER(?)", partName);
		String closestPlayer = null;
		try {
			if (rs.next())
				closestPlayer = rs.getString("Username");
			else{
				ResultSet rs2 = Mysql.PS.getSecureQuery("SELECT * FROM Users WHERE LOWER(Username) LIKE CONCAT(?, '%') OR LOWER(Username) LIKE CONCAT('%', ?, '%') OR LOWER(Username) LIKE CONCAT('%',?)", partName, partName, partName);
				if (rs2.next())
					closestPlayer = rs2.getString("Username");
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

		return closestPlayer;
	}
	public static ArrayList<Integer> getPlayerIgnores(Integer ID) {
		ArrayList<Integer> playerList = new ArrayList<Integer>();
		if (!PlayersInfo.ignoredPlayers.containsKey(Mysql.getUsername(ID))){
			ResultSet rs = Mysql.PS.getSecureQuery("SELECT * FROM UserInfo WHERE User = ?", ""+ ID);
			try {
				if (rs.next()){
					String ignoredPlayers = rs.getString("PlayerIgnores");
					if (ignoredPlayers != null && ignoredPlayers != ""){
						String allIgnores = rs.getString("PlayerIgnores");
						String[] usersString = allIgnores.split(":");
						ArrayList<Integer> ignores = new ArrayList<Integer>();

						for (String userID : usersString)
							ignores.add(Integer.parseInt(userID));
						return ignores;
					}
					else
						return new ArrayList<Integer>();
				}
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			PlayersInfo.ignoredPlayers.put(PlayersInfo.getPlayer(Mysql.getUsername(ID)).getUniqueId(), playerList);
		}
		else
			playerList = PlayersInfo.ignoredPlayers.get(Mysql.getUsername(ID));

		return playerList;
	}

	public static void addPlayerIgnore(Integer ID, Integer ignoredPlayer) {
		ArrayList<Integer> playerList = getPlayerIgnores(ID);
		playerList.add(ignoredPlayer);
		String stringedEntry = "";
		for (Integer entry : playerList){
			stringedEntry = entry + ":" + stringedEntry;
		}
		ResultSet rs = Mysql.PS.getSecureQuery("UPDATE UserInfo SET PlayerIgnores = ? WHERE User = ?", stringedEntry, playerList.toString());
		try {
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public static void removePlayerIgnore(Integer ID, Integer ignoredPlayer) {
		ArrayList<Integer> playerList = getPlayerIgnores(ID);
		if (playerList.contains("" + ignoredPlayer)){
			playerList.remove(""+ignoredPlayer);
			String stringedEntry = "";
			Mysql.PS.getSecureQuery("UPDATE UserInfo SET PlayerIgnores = ? WHERE User = ?", stringedEntry, playerList.toString());
		}
	}
	// We want to use their ID (Incase we want to display Stats in-game, we will
	// be getting that the player with for example ID 1 has 200 kills we need to
	// know who has ID 1)
	public static String getUsername(int id) {
		ResultSet res;
		String username = "None";
		try {
			res = Mysql.PS.getSecureQuery("SELECT * FROM Users WHERE ID = ? LIMIT 1", ""+id);
			if (res.next())
				username = res.getString("Username");
			else
				username = "None";
		} catch (SQLException e) {e.printStackTrace();}

		return username;// Return the String name
	}

	public static void ConnectToDB(){
		ServerCon = new MySQL(Logger.getLogger("Minecraft"),
				"[PvP]",
				Main.DBHost,
				3306,
				Main.DBName,
				Main.DBUser,
				Main.DBPass);
		ServerCon.open();
		/*
		String myDriver = "com.mysql.jdbc.Driver";
		Main.UseMySQL = true;
		try {
			Class.forName(myDriver);
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}

		BoneCP connectionPool = null;
		try {
			// setup the connection pool
			BoneCPConfig config = new BoneCPConfig();
			config.setJdbcUrl("jdbc:mysql://" + Main.DBHost + "/" + Main.DBName +"?autoReconnect=true"); // jdbc url specific to your database, eg jdbc:mysql://127.0.0.1/yourdb
			config.setUsername(Main.DBUser); 
			config.setPassword(Main.DBPass);
			config.setMinConnectionsPerPartition(1);
			config.setMaxConnectionsPerPartition(10000);
			config.setPartitionCount(1);
			config.setDefaultAutoCommit(true);
			config.setStatementsCacheSize(0);
			connectionPool = new BoneCP(config); // setup the connection pool
			ServerCon = connectionPool; // fetch a connection
		} catch (Exception e){
			Message.C("Failed...");
		}
		 */
	}
	public static boolean checkCanRoll(Player p) {
		boolean Reward = false;
		ResultSet rs = PS.getSecureQuery("SELECT * FROM Factions_DailyRewards WHERE User = ? LIMIT 1", ""+Mysql.getUserID(p.getName()));
		try {
			if (rs.next()){
				int last = rs.getInt("LastRewarded");
				Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("Canada/Eastern"));
				int Day = localCalendar.get(Calendar.DAY_OF_YEAR);
				int diffInDays = Day - last;
				if (diffInDays >= 1){
					Reward = true;
				}
			}
			else{
				Reward = true;
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

		return Reward;
	}

	// Just for creative. Will be managed for other servers later
	public static boolean isCompletedTutorial(String player){
		boolean result = true;
		if (Main.TutorialEnabled && Main.UseMySQL){
			result = false;
			ResultSet rs = PS.getSecureQuery("SELECT * FROM UserInfo WHERE User = ?", "" + Mysql.getUserID(player));
			try {
				if (rs.next()){
					int tutorial = rs.getInt(Main.TutorialColumnName);
					if (tutorial == 1)
						result = true;
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
		return result;
	}

	// Just for creative. Will be managed for other servers later
	public static void completedTutorial(String player){
		if (Main.TutorialEnabled && Main.UseMySQL && PlayersInfo.MustCompleteTutorial.contains(player)){
			PlayersInfo.MustCompleteTutorial.remove(player);
			ResultSet rs = PS.getSecureQuery("UPDATE UserInfo SET " + Main.TutorialColumnName + " = 1 WHERE User = ?", "" + Mysql.getUserID(player));
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	// Just for creative. Will be managed for other servers later
	public static void resetTutorial(String player){
		if (Main.TutorialEnabled && Main.UseMySQL){
			if (PlayersInfo.notCompletedTutorial(player))
				PlayersInfo.MustCompleteTutorial.add(PlayersInfo.getPlayer(player).getUniqueId());
			ResultSet rs = PS.getSecureQuery("UPDATE UserInfo SET " + Main.TutorialColumnName + " = 0 WHERE User = ?", "" + Mysql.getUserID(player));
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public static void checkDailyRewards(Player p) {
		boolean reward = false;
		if (Main.EconomyEnable && Main.DailyRoll && Main.UseMySQL){
			ResultSet rs = Mysql.PS.getSecureQuery("SELECT * FROM Factions_DailyRewards WHERE User = ? LIMIT 1", ""+Mysql.getUserID(p.getName()));
			Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("Canada/Eastern"));
			int Day = localCalendar.get(Calendar.DAY_OF_YEAR);

			try {
				if (rs.next()){
					int last = rs.getInt("LastRewarded");

					int diffInDays = (int) Day - last;
					if (diffInDays >= 1){
						Mysql.PS.getSecureQuery("UPDATE Factions_DailyRewards SET LastRewarded = ? WHERE User = ?", ""+Day, ""+getUserID(p.getName()));
						reward = true;
					}
				}
				else{
					Mysql.PS.getSecureQuery("INSERT INTO Factions_DailyRewards (User, LastRewarded) VALUES (?, ?)", ""+getUserID(p.getName()), ""+Day);
					reward = true;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}



			if (reward){
				int ran = ConvertTimings.randomInt(0, 300);
				int amt = 0;
				if (ran == 0)
					amt = ConvertTimings.randomInt(1200, 1500);
				else if (ran <= 2)
					amt = ConvertTimings.randomInt(700, 1100);
				else if (ran <= 7)
					amt = ConvertTimings.randomInt(500, 700);
				else if (ran <= 20)
					amt = ConvertTimings.randomInt(425, 500);
				else if (ran <= 40)
					amt = ConvertTimings.randomInt(300, 425);
				else if (ran <= 70)
					amt = ConvertTimings.randomInt(200, 250);
				else if (ran <= 110)
					amt = ConvertTimings.randomInt(150, 200);
				else if (ran <= 160)
					amt = ConvertTimings.randomInt(100, 150);
				else if (ran <= 220)
					amt = ConvertTimings.randomInt(80, 100);
				else if (ran <= 300)
					amt = ConvertTimings.randomInt(50, 80);
				else
					amt = ConvertTimings.randomInt(0, 50);
				if (plugin.getServer().getOnlinePlayers().size() <= 20 || amt >= 400)
					Message.G(Message.Replacer (Message.Replacer(Message.RollTheDice, p.getName(), "%player"), "" + amt, "%amount"), true);
				EconSystem.modifyMoney(p.getName(), amt, false);
			}
			else{
				Message.P(p, Message.RolledTheDailyDice, true);
			}
		}
	}



	public static class PS {
		public static ResultSet getSecureQuery(String query, String... entries){
			if (ServerCon != null){
				ResultSet rs = null;
				rs = ServerCon.query(query, entries);
				return rs;
			}
			/*
				PreparedStatement ps;
				try {
					Connection con = getConnection();

					if (query.toLowerCase().contains("select"))
						ps = con.prepareStatement(query);
					else
						ps = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

					for (int i = 0; i < entries.length; i++) {
						ps.setString(i + 1, entries[i]);
					}

					ExecutorService executor = Executors.newSingleThreadExecutor();
					Task task = instance.new Task();
					task.ps = ps;
					task.query = query;
					Future<ResultSet> future = executor.submit(task);
					ResultSet rs = null;
					try {
						rs = future.get();
					} catch (InterruptedException | ExecutionException e) {
						e.printStackTrace();
					} finally {
						if (con != null){
							con.close();
							return rs;
						}
					}
				}
				catch (SQLException e){


				}
			 */
			return null;
		}

		/*
		public static ResultSet getSecureQuery(String query){
			if (ServerCon != null){
				ResultSet rs = null;
				if (ServerCon.open()){
					try {
						rs = ServerCon.query(query);
					} catch (SQLException e) {
						e.printStackTrace();
					}
					return rs;
				}
			}
		 */
		/*
				PreparedStatement ps;
				try {
					Connection con = getConnection();
					if (query.toLowerCase().contains("select"))
						ps = con.prepareStatement(query);
					else
						ps = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
					ExecutorService executor = Executors.newSingleThreadExecutor();
					Task task = instance.new Task();
					task.ps = ps;
					task.query = query;
					Future<ResultSet> future = executor.submit(task);
					executor.shutdown(); // Important!
					ResultSet rs = null;
					try {
						rs = future.get();
					} catch (InterruptedException | ExecutionException e) {
						e.printStackTrace();
					} finally {
						if (con != null){
							con.close();
							return rs;
						}
					}
				}
				catch (SQLException e){


				}
		 */
		/*
		 * 			}
			else{
				Message.C("Failed to complete query. Did not have connection to Database. Reconnecting... But this query will not re-run.");
				Mysql.ConnectToDB();
			}

		 */

		//	return null;
		//}
	}
	/*
	class Task implements Callable<ResultSet> {
		String query = "";
		PreparedStatement ps = null;
		public ResultSet call() {
			if (ps != null){
				if (query.toLowerCase().contains("select")) {
					try {
						return ps.executeQuery();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					try {
						ps.executeUpdate();
						ResultSet rs = ps.getGeneratedKeys();
						ps.close();
						return rs;
					} catch (SQLException e) {
						e.printStackTrace();
					}

				}
			}
			else{
				Message.C("Failed to complete query. Did not have connection to Database. Reconnecting... But this query will not re-run.");
				Mysql.ConnectToDB();
			}
			return null;
		}
	}
	 */
	
	
	public static int DeathCode(DamageCause cause) {
		if (cause == DamageCause.ENTITY_ATTACK)
			return 1;
		else if (cause == DamageCause.PROJECTILE)
			return 2;
		else if (cause == DamageCause.BLOCK_EXPLOSION)
			return 3;
		else if (cause == DamageCause.CONTACT)
			return 4;
		else if (cause == DamageCause.CUSTOM)
			return 5;
		else if (cause == DamageCause.DROWNING)
			return 6;
		else if (cause == DamageCause.ENTITY_EXPLOSION)
			return 7;
		else if (cause == DamageCause.FALL)
			return 8;
		else if (cause == DamageCause.FALLING_BLOCK)
			return 9;
		else if (cause == DamageCause.FIRE || cause == DamageCause.FIRE_TICK)
			return 10;
		else if (cause == DamageCause.LAVA)
			return 11;
		else if (cause == DamageCause.LIGHTNING)
			return 12;
		else if (cause == DamageCause.MAGIC)
			return 13;
		else if (cause == DamageCause.MELTING)
			return 14;
		else if (cause == DamageCause.POISON)
			return 15;
		else if (cause == DamageCause.STARVATION)
			return 16;
		else if (cause == DamageCause.SUFFOCATION)
			return 17;
		else if (cause == DamageCause.SUICIDE)
			return 18;
		else if (cause == DamageCause.THORNS)
			return 19;
		else if (cause == DamageCause.VOID)
			return 20;
		else if (cause == DamageCause.WITHER)
			return 21;
		else
			return 0;
	}

	public static String getUsername(){
		return Main.DBUser;
	}
	public static String getDatabaseName(){
		return Main.DBName;
	}
	public static String getHost(){
		return Main.DBHost;
	}

	public static Integer getPlotsOwned(String name) {
		ResultSet rs = Mysql.PS.getSecureQuery("SELECT *, COUNT(*) AS Count FROM plotmePlots WHERE owner = ?", name);
		int count = 0;
		try {
			if (rs.next()){
				count = rs.getInt("Count");
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
		return count;
	}

	public static Integer getHungerGamesFame(String name) {
		ResultSet rs = Mysql.PS.getSecureQuery("SELECT * FROM HungerGames_Fame WHERE User = ?",""+Mysql.getUserID(name));
		int count = 0;
		try {
			if (rs.next()){
				count = rs.getInt("Fame");
			}
			else{
				Mysql.PS.getSecureQuery("INSERT INTO HungerGames_Fame (User, Fame) VALUES (?,0)", ""+Mysql.getUserID(name));
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
		return count;
	}

	public static Integer getVotes(String pName, LengthType type) {
		Integer votes = 0;
		if (!PlayersInfo.votesCompleted.containsKey(pName) || !PlayersInfo.votesCompleted.get(pName).containsKey(type)){
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(System.currentTimeMillis());
			cal.set(Calendar.HOUR_OF_DAY,0);
			cal.set(Calendar.MINUTE,0);
			cal.set(Calendar.SECOND,0);
			cal.set(Calendar.MILLISECOND,0);
			if (type == LengthType.months)
				cal.set(Calendar.DAY_OF_MONTH, 1);
			if (type == LengthType.years)
				cal.set(Calendar.DAY_OF_YEAR, 1);

			ResultSet rs = Mysql.PS.getSecureQuery("SELECT *,COUNT(*) AS votes FROM VotingRecords WHERE UserID = ? AND Time >= ?", getUserID(pName)+"", ""+(cal.getTimeInMillis()/1000));
			try {
				if (rs.next()){
					votes = rs.getInt("votes");
					if (!PlayersInfo.votesCompleted.containsKey(pName))
						PlayersInfo.votesCompleted.put(pName, new HashMap<LengthType, Integer>());
					PlayersInfo.votesCompleted.get(pName).put(type, votes);
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
		else
			votes = PlayersInfo.votesCompleted.get(pName).get(type);
		return votes;
	}

	public static String getTopVoter() {
		//Mysql.PS.getSecureQuery("SELECT * From VotingRecords WHERE ")
		return "NaN";
	}

}
