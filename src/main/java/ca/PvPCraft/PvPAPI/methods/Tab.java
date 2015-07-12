package ca.PvPCraft.PvPAPI.methods;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.logging.Logger;

import ca.PvPCraft.PvPAPI.Main;
import ca.PvPCraft.PvPAPI.utilities.Message;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.mcsg.double0negative.tabapi.TabAPI;

public class Tab implements Listener{
	public static Main plugin;
	static Logger log = Bukkit.getLogger();

	public Tab(Main mainclass) {
		plugin = mainclass;
		mainclass.getServer().getPluginManager().registerEvents(this, mainclass);
	}
	
	
	
	  public static void clearAll(Player p) {
		    if (Main.TabAPIEnable)
		      for (int i = 0; i < TabAPI.getVertSize(); i++)
		        for (int j = 0; j < TabAPI.getHorizSize(); j++)
		          TabAPI.setTabString(plugin, p, i, j, "");
		  }
		  public static void updateTabAll(){
			  if (Main.TabAPIEnable) {
				Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable(){
					public void run(){
						for (Player pl : plugin.getServer().getOnlinePlayers())
							updateTab(pl);
					}
				}, 6);
			  }
		  }
		  
		  
		  
		  public static void updateTab(Player p) {
		    if (Main.TabAPIEnable) {
		    	TabAPI.updatePlayer(p);
		    }
		  }
		  
		  public static void setTab(Player p, HashMap<Integer, LinkedList<String>> list){
			  ArrayList<String> usedStuffz = new ArrayList<String>();
			  for (Entry<Integer, LinkedList<String>> columnsEntry : list.entrySet()){
				  int rowNum = 0;
				  for (String rowString : columnsEntry.getValue()){
					  rowString = Message.Colorize(rowString);
					  while (usedStuffz.contains(rowString))
						  rowString = rowString + " ";
					  usedStuffz.add(rowString);
					  TabAPI.setTabString(plugin, p, rowNum, columnsEntry.getKey(), rowString);
					  rowNum++;
				  }
			  }
			  updateTab(p);
		  }
		  
		  public static void TabsetPriority(Player pl, int i) {
			    if (Main.TabAPIEnable)
			      TabAPI.setPriority(plugin, pl, i);
			  }

			  public static void TabRefresh(Player p) {
			    if (Main.TabAPIEnable)
			      TabAPI.updatePlayer(p);
			  }
}
