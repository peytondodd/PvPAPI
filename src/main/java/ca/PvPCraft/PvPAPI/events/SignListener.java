package ca.PvPCraft.PvPAPI.events;

import java.io.IOException;
import java.util.logging.Logger;

import ca.PvPCraft.PvPAPI.Main;
import ca.PvPCraft.PvPAPI.enums.ServerInfo;
import ca.PvPCraft.PvPAPI.enums.SignInfo;
import ca.PvPCraft.PvPAPI.methods.Locations;
import ca.PvPCraft.PvPAPI.methods.serverPinging.ServerSignManagement;
import ca.PvPCraft.PvPAPI.utilities.Files;
import ca.PvPCraft.PvPAPI.utilities.Message;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;


public class SignListener implements Listener{
	public static Main plugin;
	static Logger log = Bukkit.getLogger();
	
	public SignListener (Main mainclass){
		plugin = mainclass;
		mainclass.getServer().getPluginManager().registerEvents(this, mainclass);
		
	}
	
	
	@EventHandler
	public void onSignPlace (SignChangeEvent e) throws IOException{
		Player p = e.getPlayer();
		if (p.hasPermission("pvp.serversigns")){
			String[] lines = e.getLines();
			FileConfiguration file = Files.servers.getCustomConfig();
			if (lines[0].toLowerCase().contains("[pvpsigns]")){
				ServerInfo subServer = subServersofContain(lines[1]);
				if (Main.ServerList.containsKey(lines[1].toLowerCase()) || subServer != null){
					if (Main.signLayouts.containsKey(lines[2])){
						// The sign has passed all checks...						
						
						
						if (subServer != null){
							int ID = 1;
							String masterServer = masterofContain(lines[1]).getListedName();
							String configLine = masterServer + ".Subservers." + subServer.getListedName() + ".Signs";
							
							if (file.contains(configLine)){
								ID = file.getConfigurationSection(configLine).getKeys(false).size() + 1;
							}
							
							Main.ServerList.get(masterServer).getSubserver(lines[1]).getSigns().add(new SignInfo(e.getBlock().getLocation(), lines[2], lines[1], ID));
							Files.servers.getCustomConfig().set(configLine + "." + ID +".Layout", lines[2]);
							Locations.SaveLocation(Files.servers, e.getBlock().getLocation(), configLine + "." + ID);
						}
						else{
							int ID = 1;
							
							String configLine = lines[1] + ".Signs";
							
							if (file.contains(configLine)){
								ID = file.getConfigurationSection(configLine).getKeys(false).size() + 1;
							}
							
							Main.ServerList.get(lines[1].toLowerCase()).getSigns().add(new SignInfo(e.getBlock().getLocation(), lines[2], lines[1], ID));
							Files.servers.getCustomConfig().set(configLine + "." + ID +".Layout", lines[2]);
							Locations.SaveLocation(Files.servers, e.getBlock().getLocation(), configLine + "." + ID);
						}
						
						
						Message.P(p, Message.Replacer(Message.ServerSignCreated, lines[1], "<server>"), true);						
					}
					else{
						e.setCancelled(true);
						Message.P(p, Message.InvalidLayout, true);
					}
				}
				else{
					e.setCancelled(true);
					Message.P(p, Message.InvalidServer, true);
					String servers = "";
					for (String server : Main.ServerList.keySet())
						servers = servers + "," + server;
					Message.P(p, Message.Replacer(Message.ValidServers, servers.replaceFirst(",", ""), "%valid"), true);
				}
			}
		}
	}
	
	private ServerInfo subServersofContain(String subServer) {
		for (ServerInfo server : Main.ServerList.values()){
			if (!server.getSubservers().isEmpty()){
				if (server.getSubserver(subServer) != null)
					return server.getSubserver(subServer);
			}
		}
		return null;
	}
	private ServerInfo masterofContain(String subServer) {
		for (ServerInfo server : Main.ServerList.values()){
			if (!server.getSubservers().isEmpty()){
				if (server.getSubserver(subServer) != null)
					return server;
			}
		}
		return null;
	}

	@EventHandler
	public void OnSignDestroy(BlockBreakEvent e) throws IOException{
		Player p = e.getPlayer();
		Block b = e.getBlock();
		
		if (ServerSignManagement.blockIsSign(b) && p.hasPermission("pvp.serversigns")){
			//Check if it is a server sign...
			
			String serverName = getServerSign(b);
			if (serverName != null){
				
				
				if (serverName.contains("----Sub")){
					// We are removing a sub server...
					String[] splitInfo = serverName.split("----");
					String[] splitMaster = splitInfo[0].split(":");
					String[] splitSub = splitInfo[1].split(":");
					String masterName = splitMaster[1];
					String subName = splitSub[1];
					boolean removed = false;
					
					// We removed the sign from our list. Next we need to remove from config.
					for (ServerInfo subServer : Main.ServerList.get(masterName).getSubservers()){
						if (subServer.getListedName().equalsIgnoreCase(subName)){
							for (SignInfo signData : subServer.getSigns()){
								if (Locations.locationSameAs(signData.getSignLoc(),b.getLocation())){
									Files.servers.getCustomConfig().set(masterName + ".Subservers." + subServer.getListedName() + ".Signs." + signData.getSignID(), null);
									Files.servers.saveCustomConfig();
									Main.ServerList.get(masterName).getSubserver(subServer.getListedName()).getSigns().remove(signData);
									Message.P(p, Message.Replacer(Message.ServerSignBroken, subServer.getDisplayName(), "<server>"), true);						
									removed = true;
									break;
								}
							}
							if (removed == true)
								break;
						}
					}
					
					
					
					
					
					
				}
				else{
					String[] splitMaster = serverName.split(":");
					String masterName = splitMaster[1];

					// We removed the sign from our list. Next we need to remove from config.
					for (SignInfo signData : Main.ServerList.get(masterName).getSigns()){
						if (Locations.locationSameAs(signData.getSignLoc(),b.getLocation())){
							Files.servers.getCustomConfig().set(masterName + ".Signs." + signData.getSignID(), null);
							Message.P(p, Message.Replacer(Message.ServerSignBroken, signData.getServer().getDisplayName(), "<server>"), true);						
							Files.servers.saveCustomConfig();
							Main.ServerList.get(masterName).getSigns().remove(Main.ServerList.get(masterName).getSigns().indexOf(signData));
							break;
						}
					}
				}
			}
		}
	}


	public static String getServerSign(Block b) {
		// We loop through normal server info set...
		for (ServerInfo infoSet : Main.ServerList.values()){
			for (SignInfo signInfo : infoSet.getSigns()){
				if (Locations.locationSameAs(signInfo.getSignLoc(),b.getLocation())){
					return "Master:"+infoSet.getListedName();
				}
			}
			// We check if there is a filled subserver list, so we scan it...
			if (!infoSet.getSubservers().isEmpty())
			for (ServerInfo subServerInfoSet : infoSet.getSubservers()){
				for (SignInfo signInfo : subServerInfoSet.getSigns()){
					if (Locations.locationSameAs(signInfo.getSignLoc(),b.getLocation())){
						return "Master:"+infoSet.getListedName() + "----Sub:"+subServerInfoSet.getListedName();
					}
				}
			}
		}
		
		return null;
	}
	
	
	
	
	
}
