package ca.PvPCraft.PvPAPI.events;

import java.util.logging.Logger;

import ca.PvPCraft.PvPAPI.Main;
import ca.PvPCraft.PvPAPI.methods.ConvertTimings;
import ca.PvPCraft.PvPAPI.methods.PlayersInfo;
import ca.PvPCraft.PvPAPI.utilities.Message;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;


public class TeleportListener implements Listener{
	public static Main plugin;
	static Logger log = Bukkit.getLogger();
	
	public TeleportListener (Main mainclass){
		plugin = mainclass;
		mainclass.getServer().getPluginManager().registerEvents(this, mainclass);
	}
	
	
	
	public static void teleport(final Player p, final Location loc){
		if (Main.DelayedTeleport >= 1){
			int delay = 0;
			if (!p.hasPermission("pvp.instantTeleport")){
				delay = Main.DelayedTeleport;
				Message.P(p, Message.Replacer(Message.delayedTeleport, ConvertTimings.convertTime(delay, true), "%time"), true);
				
				int Task = Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
					public void run() {
						p.teleport(loc);
						PlayersInfo.delayedTeleports.remove(p.getName());
						}
					}, 20L * delay);
				PlayersInfo.delayedTeleports.put(p.getName(), Task);
			}
			else
				p.teleport(loc);
		}
	}
	
	public static void teleport(final Player p, final Player pl){
		if (Main.DelayedTeleport >= 1){
			int delay = 0;
			if (!p.hasPermission("pvp.instantTeleport")){
				delay = Main.DelayedTeleport;
				Message.P(p, Message.Replacer(Message.delayedTeleport, ConvertTimings.convertTime(delay, true), "%time"), true);
				
				int Task = Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
					public void run() {
						p.teleport(pl);
						PlayersInfo.delayedTeleports.remove(p.getName());
						}
					}, 20L * delay);
				PlayersInfo.delayedTeleports.put(p.getName(), Task);
			}
			else
				p.teleport(pl);
		}
	}
	
}
