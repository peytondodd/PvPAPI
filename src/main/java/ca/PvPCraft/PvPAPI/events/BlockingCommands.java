package ca.PvPCraft.PvPAPI.events;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.logging.Logger;

import ca.PvPCraft.PvPAPI.Main;
import ca.PvPCraft.PvPAPI.methods.PlayersInfo;
import ca.PvPCraft.PvPAPI.utilities.Message;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;


public class BlockingCommands implements Listener{
	public static Main plugin;
	static Logger log = Bukkit.getLogger();
	public static HashMap<String, String> LastMessage = new HashMap<String, String>();

	
	public BlockingCommands (Main mainclass){
		plugin = mainclass;
		mainclass.getServer().getPluginManager().registerEvents(this, mainclass);	
	}

	
	@EventHandler
	public void OnChatEvent (PlayerCommandPreprocessEvent e) throws SQLException{
		Player p = e.getPlayer();
		if (Main.TutorialEnabled && !e.getMessage().equalsIgnoreCase("/tutorial")){
			if (PlayersInfo.notCompletedTutorial(p.getName())){
				e.setCancelled(true);
				Message.P(p, Message.MustCompleteTutorial, true);
			}
		}
		else if (Main.BannedCommands.contains(e.getMessage().replaceFirst("/","")))
			e.setMessage("/totallyUnknownCommandHehehehe");
		else if (Main.aliasCommands.containsKey(e.getMessage().replaceFirst("/","")))
			e.setMessage("/" + Main.aliasCommands.get(e.getMessage().replaceFirst("/","")));
	}
}
