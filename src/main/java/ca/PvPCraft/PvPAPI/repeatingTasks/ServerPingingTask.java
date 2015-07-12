package ca.PvPCraft.PvPAPI.repeatingTasks;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import ca.PvPCraft.PvPAPI.Main;
import ca.PvPCraft.PvPAPI.Commands.PlayerCommands;
import ca.PvPCraft.PvPAPI.enums.IconInfo;
import ca.PvPCraft.PvPAPI.enums.ServerInfo;
import ca.PvPCraft.PvPAPI.methods.PlayersInfo;
import ca.PvPCraft.PvPAPI.methods.Scoreboards;
import ca.PvPCraft.PvPAPI.methods.serverPinging.MinecraftPing;
import ca.PvPCraft.PvPAPI.methods.serverPinging.ServerSignManagement;
import ca.PvPCraft.PvPAPI.utilities.IconMenu;
import ca.PvPCraft.PvPAPI.utilities.Message;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;




public class ServerPingingTask extends BukkitRunnable {

	static Main plugin;

	public ServerPingingTask() {

	}

	@Override
	public void run() {

		for (Entry<String, ServerInfo> entry : Main.ServerList.entrySet()){
			if (entry.getValue().getSubservers().isEmpty()){
				if (entry.getValue().getAddress() != null){
					try {
						Thread t;
						t = new Thread(new MinecraftPing(entry.getKey(), entry.getValue().getAddress()));
						t.start();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			else{
				for (ServerInfo subServer : entry.getValue().getSubservers()){
					try {
						Thread t;
						t = new Thread(new MinecraftPing(entry.getKey() + ":" + subServer.getListedName(), subServer.getAddress()));
						t.start();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}


				for (Entry<String, IconInfo> menu : PlayerCommands.MENUS.entrySet()){
					
					int pos = 0;
					int MaxPos = 0;
					for (ServerInfo serverInfo: Main.ServerList.values()){
						if (MaxPos < serverInfo.getPos())
							MaxPos = serverInfo.getPos();
					}
					if (MaxPos <= Main.ServerList.size())
						MaxPos = Main.ServerList.size();


					int invsize = 9;

					for(int i=0; i<=10; i++) {
						if((i*9) >= MaxPos) {
							invsize = invsize + i*9;
							break;
						}
					}
					ArrayList<Integer> bannedPos = new ArrayList<Integer>();
					
					if (menu.getValue().getTitle().equalsIgnoreCase("masterList"))
						for (ServerInfo serverInfo: Main.ServerList.values()){
							String serverName = Message.Colorize(serverInfo.getDisplayName());
							ItemStack serverIcon = serverInfo.getIcon();


							if (serverInfo.getStatus() != null){
								serverIcon.setAmount(serverInfo.getStatus().getPlayers().getOnline());
							}


							List<String> container = serverInfo.getDescription();
							if (serverInfo.getStatus() != null)
								container = ServerSignManagement.getFilteredDesc(container, serverInfo);



							ArrayList<String> coloredcontainer = new ArrayList<String>();
							for (String continfo : container){
								if (continfo != null)
									coloredcontainer.add(Message.Colorize(continfo));
							}

							if (bannedPos.contains(pos)){
								do {
									pos++;
								} while (bannedPos.contains(pos));
							}

							int tempPos = pos;
							if (serverInfo.getPos() >= pos){
								pos = serverInfo.getPos();
								bannedPos.add(pos);
							}



							ItemStack is = serverIcon;
							ItemMeta im = is.getItemMeta();
							im.setDisplayName(ChatColor.GOLD + serverName);
							im.setLore(coloredcontainer);
							is.setItemMeta(im);
							menu.getValue().getView().setItem(pos, is);


							coloredcontainer.clear();
							pos = tempPos;
							if (serverInfo.getPos() < pos){
								pos++;
							}
						}
				}
			}
		}
		/*
		if (Main.scoreBoardEnabled)
		for (final Player p : Bukkit.getOnlinePlayers()){
			Scoreboards.updateScoreboardEntries(p, Main.scoreBoardTitles, null);
		}
		 */
	}
}
