package ca.PvPCraft.PvPAPI.events;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.logging.Logger;

import ca.PvPCraft.PvPAPI.Main;
import ca.PvPCraft.PvPAPI.methods.Locations;
import ca.PvPCraft.PvPAPI.methods.PlayersInfo;
import ca.PvPCraft.PvPAPI.utilities.Files;
import ca.PvPCraft.PvPAPI.utilities.Message;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;


public class MoveListener implements Listener{
	public static Main plugin;
	static Logger log = Bukkit.getLogger();
	
	public MoveListener (Main mainclass){
		plugin = mainclass;
		mainclass.getServer().getPluginManager().registerEvents(this, mainclass);
	}
	
	
	
	
	
	
	
	@EventHandler
	public void OnPlayerMove(PlayerMoveEvent e){
		Player p = e.getPlayer();
		if (!Locations.locationSameAs(e.getTo(), e.getFrom())){
			String Portal = Locations.locationIsPortal(e.getTo());
			if (Portal != null){
				if (Files.teleports.getCustomConfig().getString("Portals." + Portal + ".ServerTP") != null){
					joinServer(p, Files.teleports.getCustomConfig().getString("Portals." + Portal + ".ServerTP"));
				}
				else{
					Location locPort = Locations.serverTeleports.get(Portal);
					if (locPort != null)
					p.teleport(locPort);
				}
			}
		}
		if (PlayersInfo.delayedTeleports.containsKey(p.getName())){
			if (plugin.getServer().getScheduler().isCurrentlyRunning(PlayersInfo.delayedTeleports.get(p.getName()))){
				plugin.getServer().getScheduler().cancelTask(PlayersInfo.delayedTeleports.get(p.getName()));
				Message.P(p, Message.Replacer(Message.TeleportIsCancelled, "your movement", "%player"), true);
				PlayersInfo.delayedTeleports.remove(p.getName());
			}
		}
		
	}
	
	
	public static boolean joinServer(Player p, String serverName){
	    boolean successConnect = false;
			ByteArrayOutputStream b = new ByteArrayOutputStream();
			DataOutputStream out = new DataOutputStream(b);
			try {
				out.writeUTF("Connect");
				out.writeUTF(serverName);
				p.sendPluginMessage(plugin, "BungeeCord", b.toByteArray());
				successConnect = true;
			}
			catch (IOException localIOException) {System.out.println("Failed connect to server...");}
	    return successConnect;
	}
	
	
}
