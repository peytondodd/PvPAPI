package ca.PvPCraft.PvPAPI.methods;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import ca.PvPCraft.PvPAPI.Main;
import ca.PvPCraft.PvPAPI.enums.Config;
import ca.PvPCraft.PvPAPI.utilities.Files;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;


public class Locations {
	
	
	
	public static Main plugin;
	
	
	public static Location tutorialLoc = null;
	public static Location tutorialEndLoc = null;

	
	
	
	
    public static ConcurrentHashMap<String, Location> serverTeleports = new ConcurrentHashMap<String, Location>();
    public static ConcurrentHashMap<String, ArrayList<Location>> portalLocation = new ConcurrentHashMap<String, ArrayList<Location>>();

    
    
	public Locations(Main mainclass) {
		plugin = mainclass;
		RefreshConfig(Files.teleports);
	}
	
	


	public static boolean locationSameAs(Location pointShould, Location pointGot){
		if (pointShould.getBlockX() == pointGot.getBlockX() &&
				pointShould.getBlockY() == pointGot.getBlockY() &&
					pointShould.getBlockZ() == pointGot.getBlockZ())
		return true;
		else
		return false;
	}
	
	
	
	public static boolean locationBetweenPoints(Location loc, Location point1, Location point2){
		if (((loc.getBlockX() >= point1.getBlockX() && loc.getBlockX() <= point2.getBlockX()) || 
			(loc.getBlockX() <= point1.getBlockX() && loc.getBlockX() >= point2.getBlockX())) &&
			((loc.getBlockY() >= point1.getBlockY() && loc.getBlockY() <= point2.getBlockY()) || 
			(loc.getBlockY() <= point1.getBlockY() && loc.getBlockY() >= point2.getBlockY())) &&
			((loc.getBlockZ() >= point1.getBlockZ() && loc.getBlockZ() <= point2.getBlockZ()) || 
			(loc.getBlockZ() <= point1.getBlockZ() && loc.getBlockZ() >= point2.getBlockZ())))
			return true;
		else
			return false;
	}
	
    public static List<Block> blocksFromTwoPoints(Location loc1, Location loc2, boolean includeAir)
    {
        List<Block> blocks = new ArrayList<Block>();
 
        int topBlockX = (loc1.getBlockX() < loc2.getBlockX() ? loc2.getBlockX() : loc1.getBlockX());
        int bottomBlockX = (loc1.getBlockX() > loc2.getBlockX() ? loc2.getBlockX() : loc1.getBlockX());
 
        int topBlockY = (loc1.getBlockY() < loc2.getBlockY() ? loc2.getBlockY() : loc1.getBlockY());
        int bottomBlockY = (loc1.getBlockY() > loc2.getBlockY() ? loc2.getBlockY() : loc1.getBlockY());
 
        int topBlockZ = (loc1.getBlockZ() < loc2.getBlockZ() ? loc2.getBlockZ() : loc1.getBlockZ());
        int bottomBlockZ = (loc1.getBlockZ() > loc2.getBlockZ() ? loc2.getBlockZ() : loc1.getBlockZ());
 
        for(int x = bottomBlockX; x <= topBlockX; x++)
        {
            for(int z = bottomBlockZ; z <= topBlockZ; z++)
            {
                for(int y = bottomBlockY; y <= topBlockY; y++)
                {
                    Block block = loc1.getWorld().getBlockAt(x, y, z);
                    // Don't include air and block is not air
                    if (block.getType() != Material.AIR){
                    	blocks.add(block);
                    }
                    else if (block.getType() == Material.AIR && includeAir){
                    blocks.add(block);
                    }
                }
            }
        }
       
        return blocks;
    }
	
	
	public static Location getLocOfString(String string, Config files) {
		Location loc = null;
		FileConfiguration file = files.getCustomConfig();
		if (file.contains(string)){
			int x = file.getInt(string + ".X");
			int y = file.getInt(string + ".Y");
			int z = file.getInt(string + ".Z");
			String world = file.getString(string + ".World");
			
			
			loc = new Location(plugin.getServer().getWorld(world),x,y,z);
			
			if (file.contains(string + ".Yaw")){
				loc.setYaw((float)file.getDouble(string + ".Yaw"));
			}
			if (file.contains(string + ".Pitch")){
				loc.setPitch((float)file.getDouble(string + ".Pitch"));
			}
		}
		else
			loc = plugin.getServer().getWorlds().get(0).getSpawnLocation();
		return loc;
	}
	
	public static String locationIsPortal(Location to) {
		for (Entry<String, ArrayList<Location>> x : portalLocation.entrySet()){
			Location point1 = x.getValue().get(0);
			Location point2 = x.getValue().get(1);
			if (locationBetweenPoints(to, point1, point2))
				return x.getKey();
		}
		return null;
	}
	
	
	public static void RefreshConfig(Config conf) {
		try {
			conf.reloadCustomConfig();
		} catch (Exception e) {e.printStackTrace();}
		
		if (Files.teleports.getCustomConfig().contains("Warps"))
		for (String locName : Files.teleports.getCustomConfig().getConfigurationSection("Warps").getKeys(false))
			serverTeleports.put(locName, getLocOfString("Warps." + locName, Files.teleports));
		
		if (Files.teleports.getCustomConfig().contains("Portals"))
		for (String portalName : Files.teleports.getCustomConfig().getConfigurationSection("Portals").getKeys(false)){
			portalLocation.put(portalName, new ArrayList<Location>());
			portalLocation.get(portalName).add(getLocOfString("Portals." + portalName + ".1", Files.teleports));
			portalLocation.get(portalName).add(getLocOfString("Portals." + portalName + ".2", Files.teleports));
		}
		
		
		if (Main.TutorialEnabled){
			if (Files.teleports.getCustomConfig().contains("Tutorial.Start"))
			tutorialLoc = getLocOfString("Tutorial.Start", Files.teleports);
			
			if (Files.teleports.getCustomConfig().contains("Tutorial.End"))
			tutorialEndLoc = getLocOfString("Tutorial.End", Files.teleports);
		}
	}

	public static void SaveLocation(Config cf, Location loc, String locName) {
		cf.getCustomConfig().set(locName + ".X", loc.getX());
		cf.getCustomConfig().set(locName + ".Y", loc.getY());
		cf.getCustomConfig().set(locName + ".Z", loc.getZ());
		cf.getCustomConfig().set(locName + ".World", loc.getWorld().getName());
		cf.getCustomConfig().set(locName + ".Yaw", loc.getYaw());
		cf.getCustomConfig().set(locName + ".Pitch", loc.getPitch());
		cf.saveCustomConfig();
		RefreshConfig(cf);
	}
	
}
