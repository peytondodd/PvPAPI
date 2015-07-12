package ca.PvPCraft.PvPAPI.methods;

import java.util.HashMap;
import java.util.logging.Logger;

import ca.PvPCraft.PvPAPI.Main;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
//import org.kitteh.tag.PlayerReceiveNameTagEvent; //TODO: Replace with something newer

public class Tag implements Listener{
	public static Main plugin;
	static Logger log = Bukkit.getLogger();
	static HashMap<String, String> nameTags = new HashMap<String, String>();
	//TODO: restore functionality using scoreboard teams
	public Tag(Main mainclass) {
		plugin = mainclass;
		/*
		mainclass.getServer().getPluginManager().registerEvents(this, mainclass);
		*/
	}
	
	
	/*
	
	  @EventHandler
	  public void onNameTag(PlayerReceiveNameTagEvent e)
	  {
	    if (Main.TabAPIEnable) {
	      @SuppressWarnings("unused")
		Player p = e.getNamedPlayer();
	      if (nameTags.containsKey(e.getNamedPlayer().getName()))
	    	  e.setTag(nameTags.get(e.getNamedPlayer().getName()));
	    }
	  }
	
	*/
	public static void giveNameTag(Player p, String nametag){
		nameTags.put(p.getName(), nametag);
	}
	
	
}
