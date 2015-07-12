package ca.PvPCraft.PvPAPI.enums;

import ca.PvPCraft.PvPAPI.Main;

import org.bukkit.Location;

	public class SignInfo{
		Location loc = null;
		String layoutName = "";
		String server = "";
		int SignID;
		public SignInfo(Location signLoc, String LayoutName, String serverUsed, int signID){
			loc = signLoc;
			server = serverUsed;
			layoutName = LayoutName;
			SignID = signID;
		}
		
		public String getLayoutName(){
			return layoutName;
		}
		public ServerInfo getServer(){
			if (Main.ServerList.containsKey(server)){
				return Main.ServerList.get(server);
			}
			else{
			for (ServerInfo servers : Main.ServerList.values()){
				if (servers.getSubserver(server) != null)
					return servers.getSubserver(server);
				}
			}
			return null;
		}
		public Location getSignLoc(){
			return loc;
		}
		public int getSignID(){
			return SignID;
		}
	}
	