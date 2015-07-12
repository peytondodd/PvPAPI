package ca.PvPCraft.PvPAPI.events;

import java.sql.SQLException;
import java.util.logging.Logger;

import ca.PvPCraft.PvPAPI.Main;
import ca.PvPCraft.PvPAPI.delayedTasks.joinDelay;
import ca.PvPCraft.PvPAPI.methods.PlayersInfo;
import ca.PvPCraft.PvPAPI.utilities.Message;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;


public class JoinListener implements Listener{
	public static Main plugin;
	static Logger log = Bukkit.getLogger();

	public JoinListener (Main mainclass){
		plugin = mainclass;
		mainclass.getServer().getPluginManager().registerEvents(this, mainclass);
	}



	@EventHandler
	public void JoinEvent (PlayerJoinEvent e) throws SQLException{
		final Player p = e.getPlayer();
		if (!p.hasPermission("pvp.hideLogin") && Main.JoinMessage)
			e.setJoinMessage(Message.Replacer(Message.Joined, e.getPlayer().getName(), "%player"));
		else
			e.setJoinMessage(null);
		new joinDelay(p).runTaskLaterAsynchronously(plugin, 1 * 10L);
	}
	
	
	@EventHandler
	public void QuitEvent (PlayerQuitEvent e){
		if (!e.getPlayer().hasPermission("pvp.hideLogin") && Main.QuitMessage)
			e.setQuitMessage(Message.Replacer(Message.Quit, e.getPlayer().getName(), "%player"));
		else
			e.setQuitMessage(null);
		PlayersInfo.leftServer(e.getPlayer());
	}
	@EventHandler
	public void KickEvent (PlayerKickEvent e){
		if (!e.getPlayer().hasPermission("pvp.hideLogin") && Main.QuitMessage)
			e.setLeaveMessage(Message.Replacer(Message.Quit, e.getPlayer().getName(), "%player"));
		else
			e.setLeaveMessage(null);
		PlayersInfo.leftServer(e.getPlayer());
	}
}
