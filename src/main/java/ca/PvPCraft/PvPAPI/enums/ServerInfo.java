package ca.PvPCraft.PvPAPI.enums;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import ca.PvPCraft.PvPAPI.methods.serverPinging.MinecraftPing.StatusResponse;

import org.bukkit.inventory.ItemStack;

	public class ServerInfo{
		String serverDisplay = "";
		String BackendName = "";
		String listedUnder = "";
		List<String> serverDescription = new ArrayList<String>();
		StatusResponse dataInfo = null;
		ItemStack iconMenu = null;
		InetSocketAddress Address = null;
		HashMap<String,ServerInfo> subServers = new HashMap<String, ServerInfo>();
		ArrayList<SignInfo> Serversigns = new ArrayList<SignInfo>();
		
		int starCount = 0;
		int IconPosition = 0;

		public ServerInfo(String listedName, String displayName, String bungeeCordName, List<String> description, StatusResponse info, ItemStack icon, int Stars){
			listedUnder = listedName;
			serverDisplay = displayName;
			BackendName = bungeeCordName;
			serverDescription = description;
			dataInfo = info;
			iconMenu = icon;
			starCount = Stars;
		}
		public String getListedName(){
			return listedUnder;
		}
		public String getDisplayName(){
			return serverDisplay;
		}
		public String getBungeeName(){
			return BackendName;
		}
		public List<String> getDescription(){
			return serverDescription;
		}
		public StatusResponse getStatus(){
			return dataInfo;
		}
		public void setStatus(StatusResponse response){
			dataInfo = response;
		}
		public void setHost(InetSocketAddress address){
			Address = address;
		}
		public ItemStack getIcon(){
			return iconMenu;
		}
		public Integer getStars(){
			return starCount;
		}
		public Integer getPos(){
			return IconPosition;
		}
		public InetSocketAddress getAddress(){
			return Address;
		}
		
		public  Collection<ServerInfo> getSubservers(){
			return subServers.values();
		}
		public  ServerInfo getSubserver(String name){
			if (subServers.containsKey(name))
				return subServers.get(name);
			else
				return null;
		}
		public ArrayList<SignInfo> getSigns(){
			return Serversigns;
		}
		public void addSubserver(String listedName, ServerInfo serverInfo) {
			subServers.put(listedName, serverInfo);
		}
		public void setPosition(int position) {
			IconPosition = position;
		}
		
	}
	