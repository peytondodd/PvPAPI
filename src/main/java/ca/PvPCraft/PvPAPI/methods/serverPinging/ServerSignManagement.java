package ca.PvPCraft.PvPAPI.methods.serverPinging;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import ca.PvPCraft.PvPAPI.Main;
import ca.PvPCraft.PvPAPI.enums.ServerInfo;
import ca.PvPCraft.PvPAPI.enums.SignInfo;
import ca.PvPCraft.PvPAPI.methods.serverPinging.MinecraftPing.StatusResponse;
import ca.PvPCraft.PvPAPI.utilities.Message;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.event.Listener;


public class ServerSignManagement implements Listener{
	public static Main plugin;
	static Logger log = Bukkit.getLogger();




	public ServerSignManagement (Main mainclass){
		plugin = mainclass;
		mainclass.getServer().getPluginManager().registerEvents(this, mainclass);
	}



	/*

	public static boolean joinServerSign(Player p, String serverName){
	    boolean successConnect = false;

	    if (ServerInfo.containsKey(serverName)){
	    	Players players = ServerInfo.get(serverName).getPlayers();


			if (ServerInfo.get(serverName).getDescription() != null){// Server is online?
				if (players.getOnline() < players.getMax()){
					ByteArrayOutputStream b = new ByteArrayOutputStream();
				    DataOutputStream out = new DataOutputStream(b);
				    try {
				      out.writeUTF("Connect");
				      out.writeUTF(serverName);
				      p.sendPluginMessage(plugin, "BungeeCord", b.toByteArray());
				      successConnect = true;
				      System.out.println("Success!!");
				    }
				    catch (IOException localIOException) {
				    	System.out.println("I failed?");
				    }
				}
				else
					Message.P(p, Message.ServerIsFull, true);
			}
			else// Server is offline.
				Message.P(p, Message.ServerisOffline, true);
	    }
	    else
		      System.out.println("Weird Fail...");

	    return successConnect;
	}
	 */












	public static boolean blockIsSign(Block b) {
		if (b != null && (b.getType() == Material.SIGN || b.getType() == Material.SIGN_POST || b.getType() == Material.WALL_SIGN))
			return true;
		else
			return false;
	}



	public static void setUpSigns(ServerInfo SignData) {
		for (SignInfo signInfo : SignData.getSigns()){
			Block b = signInfo.getSignLoc().getBlock();
			if (ServerSignManagement.blockIsSign(b)){

				Sign sign = (Sign) b.getState();
				String[] signLines = new String[4];


				if (SignData.getStatus() != null){
					// Server is online...

					if (signInfo.getServer().getStatus().getDescription().contains("GSign")){
						// We have a minigame/game sign.
						// Let us format it this way :D
						String currentStatusString = signInfo.getServer().getStatus().getDescription();
						String[] splitUpMotd = currentStatusString.split("=");
						String currentGamestate = splitUpMotd[0];
						String currentMap = splitUpMotd[1];
						String currentPlayers = splitUpMotd[2];
						String currentStatus = splitUpMotd[3];
						signLines[0] = "&7["+currentStatus + "&7]";
						signLines[1] = currentGamestate;
						signLines[2] = currentMap;
						signLines[3] = currentPlayers + " online";
					}
					else{
						for (int x = 0; x <= 3; x++)
							signLines[x] = getFilteredLayout(signInfo).get(x);
					}
				}
				else{
					signLines[0] = "&4▀▀▀▀▀▀▀▀▀";
					signLines[1] = "&4[Restarting]";
					signLines[2] = "&4" + signInfo.getServer().getBungeeName().toUpperCase();
					signLines[3] = "&4▀▀▀▀▀▀▀▀▀";
				}

				for (int x = 0; x <= 3; x++)
					sign.setLine(x, Message.Colorize(signLines[x]));

				sign.update();
			}
		}
	}

	public static List<String> getFilteredLayout(SignInfo info){
		ServerInfo dataSet = info.getServer();
		StatusResponse sp = dataSet.getStatus();
		List<String> newList = new ArrayList<String>();
		for (String line : Main.signLayouts.get(info.getLayoutName())){
			line = Message.Replacer(line, sp.getDescription(), "%motd");
			line = Message.Replacer(line, ChatColor.stripColor(sp.getDescription()), "%motdnc");
			line = Message.Replacer(line, "" + sp.getPlayers().getOnline(), "%curplayers");
			line = Message.Replacer(line, "" + sp.getPlayers().getMax(), "%maxplayers");
			line = Message.Replacer(line, Message.CleanCapitalize(dataSet.getDisplayName()), "%server");
			line = Message.Replacer(line, Message.CleanCapitalize(dataSet.getDisplayName()).toUpperCase(), "%serverc");
			String Stars = "";
			for (int a = 0; a <= (dataSet.getStars() - 1); a++)
				Stars = Stars + "★";
			line = Message.Replacer(line, Stars, "%stars");
			line = Message.Colorize(line);
			newList.add(line);
		}
		return newList;
	}

	public static List<String> getFilteredDesc(List<String> container, ServerInfo dataSet){
		StatusResponse sp = dataSet.getStatus();
		List<String> newList = new ArrayList<String>();
		for (String line : container){
			line = Message.Replacer(line, sp.getDescription(), "%motd");
			line = Message.Replacer(line, ChatColor.stripColor(sp.getDescription()), "%motdnc");
			line = Message.Replacer(line, "" + sp.getPlayers().getOnline(), "%curplayers");
			line = Message.Replacer(line, "" + sp.getPlayers().getMax(), "%maxplayers");
			line = Message.Replacer(line, Message.CleanCapitalize(dataSet.getDisplayName()), "%server");
			line = Message.Replacer(line, Message.CleanCapitalize(dataSet.getDisplayName()).toUpperCase(), "%serverc");
			String Stars = "";
			for (int a = 0; a <= (dataSet.getStars() - 1); a++)
				Stars = Stars + "★";
			line = Message.Replacer(line, Stars, "%stars");
			line = Message.Colorize(line);





			String Motd = ChatColor.stripColor(dataSet.getStatus().getDescription()).replace("()", "�?");
			if (Motd.contains("=")){
				String[] motdSplit = Motd.split("=");
				String playersOn = motdSplit[2];
				String Status = "";

				if (!motdSplit[2].contains("/"))
					playersOn = dataSet.getStatus().getPlayers().getOnline() + "/" + dataSet.getStatus().getPlayers().getMax();

				if (!dataSet.getStatus().getDescription().toLowerCase().contains("voting"))
					Status = ChatColor.DARK_GRAY + "" +  ChatColor.BOLD + "[Spectate]";
				else if (dataSet.getStatus().getPlayers().getOnline() >= dataSet.getStatus().getPlayers().getMax())
					Status = ChatColor.RED + "" +  ChatColor.BOLD + "[Full]";
				else
					Status = ChatColor.GREEN + "" +  ChatColor.BOLD + "[Join]";


				line = Message.Replacer(line, Status, "%join");

				line = Message.Replacer(line, ChatColor.GREEN + "" + ChatColor.BOLD + motdSplit[1],"%map");

				line =  Message.Replacer(line, ChatColor.DARK_GRAY + motdSplit[0], "%status");

				line = Message.Replacer(line, playersOn, "%players");
			}
			
			newList.add(line);
		}
		return newList;
	}



}
