package ca.PvPCraft.PvPAPI.methods;

import java.util.logging.Logger;

import ca.PvPCraft.PvPAPI.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import com.massivecraft.factions.entity.MPlayer; 

public class FactionsHook implements Listener{
	public static Main plugin;
	static Logger log = Bukkit.getLogger();

	public FactionsHook (Main mainclass){
		plugin = mainclass;
		mainclass.getServer().getPluginManager().registerEvents(this, mainclass);
	}

	public static String getFactionName(String name) {
		MPlayer uplayer = MPlayer.get(PlayersInfo.getPlayer(name));
		String FactionName = uplayer.getFaction().getName();
		if (!ChatColor.stripColor(FactionName).toLowerCase().contains("wilderness"))
			FactionName = uplayer.getRole().getPrefix() + FactionName + " ";
		else
			FactionName = "";
		return FactionName;
	}
	
	public static MPlayer getFactionPlayer(Player p){
		MPlayer uplayer = MPlayer.get(p);
		return uplayer;
	}
	
}
