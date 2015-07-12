package ca.PvPCraft.PvPAPI.methods;

import java.util.logging.Logger;

import ca.PvPCraft.PvPAPI.Main;
import ca.PvPCraft.PvPAPI.enums.ReasonSet;
import me.confuser.banmanager.BmAPI;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class BanManage implements Listener{
	public static Main plugin;
	static Logger log = Bukkit.getLogger();

	public BanManage (Main mainclass){
		plugin = mainclass;
		mainclass.getServer().getPluginManager().registerEvents(this, mainclass);
	}



	public boolean isBanned(Player p){
		if (BmAPI.isBanned(p.getUniqueId()/*p.getName()*/))
			return true;
		else
			return false;//isBannedP(player.getName());
	}

	public static boolean isMuted(Player player) {
		if (Main.BanManageEnable)
			return BmAPI.isMuted(player.getUniqueId());
		else
			return false;//isMutedP(player.getName());
	}
	
	private static boolean isMutedP(String name) {
		// TODO Auto-generated method stub
		return false;
	}
	private static boolean isBannedP(String name) {
		// TODO Auto-generated method stub
		return false;
	}


	public boolean ban(String playerName, String reason){
		ReasonSet reasonSet = new ReasonSet(playerName, reason, 0);
		return true;
	}

	public boolean tempBan(String playerName, String length, String reason){
		ReasonSet reasonSet = new ReasonSet(playerName, reason, 0);
		
		return true;
	}

	public boolean mute(String playerName, String reason){
		ReasonSet reasonSet = new ReasonSet(playerName, reason, 0);
		
		return true;
	}

	public boolean tempMute(String playerName, String length, String reason){
		ReasonSet reasonSet = new ReasonSet(playerName, reason, 0);
		
		return true;
	}
	public boolean warn(String playerName, String reason){
		
		return true;
	}

}
