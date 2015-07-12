package ca.PvPCraft.PvPAPI.methods;

import java.util.logging.Logger;

import ca.PvPCraft.PvPAPI.Main;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;

public class WorldEditHook implements Listener{
	public static Main plugin;
	static Logger log = Bukkit.getLogger();
	
    public static WorldEditPlugin worldEdit = null;

	public WorldEditHook(Main mainclass) {
		plugin = mainclass;
		mainclass.getServer().getPluginManager().registerEvents(this, mainclass);
		worldEdit = (WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
	}
	
	public static Location getSelectionPoint(Player p, int num){
		if (worldEdit.getSelection(p) != null){
			if (worldEdit.getSelection(p).getMaximumPoint() != null && num == 2)
				return worldEdit.getSelection(p).getMaximumPoint();
			else if (worldEdit.getSelection(p).getMinimumPoint() != null && num == 1)
				return worldEdit.getSelection(p).getMinimumPoint();
		}
		return null;
	}
}
