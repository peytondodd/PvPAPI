package ca.PvPCraft.PvPAPI.methods.serverPinging;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TimeZone;
import java.util.logging.Logger;





























































import ca.PvPCraft.PvPAPI.Main;
import ca.PvPCraft.PvPAPI.Commands.PlayerCommands;
import ca.PvPCraft.PvPAPI.enums.IconInfo;
import ca.PvPCraft.PvPAPI.enums.ServerInfo;
import ca.PvPCraft.PvPAPI.enums.SignInfo;
import ca.PvPCraft.PvPAPI.events.SignListener;
import ca.PvPCraft.PvPAPI.methods.ConvertTimings;
import ca.PvPCraft.PvPAPI.methods.Locations;
import ca.PvPCraft.PvPAPI.methods.PlayersInfo;
import ca.PvPCraft.PvPAPI.methods.itemModifications;
import ca.PvPCraft.PvPAPI.methods.serverPinging.MinecraftPing.StatusResponse;
import ca.PvPCraft.PvPAPI.utilities.Files;
import ca.PvPCraft.PvPAPI.utilities.IconMenu;
import ca.PvPCraft.PvPAPI.utilities.Message;
import ca.PvPCraft.PvPAPI.utilities.Mysql;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.messaging.PluginMessageRecipient;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class hubMethods implements Listener{



	public static Main plugin;
	static Logger log = Bukkit.getLogger();
	public static ArrayList<String> doNotShow = new ArrayList<String>();

	public static ArrayList<String> delayAction = new ArrayList<String>();


	public hubMethods (Main mainclass){
		plugin = mainclass;
		mainclass.getServer().getPluginManager().registerEvents(this, mainclass);
	}


	public static void GiveServerTools(Player p) {
		p.getInventory().clear();
		itemModifications.giveItem(p, Message.Colorize(Message.ClockerName), Material.WATCH, 2, Message.ClockerDesc, true);		
		itemModifications.giveItem(p, Message.Colorize(Message.CompassName), Material.COMPASS, 4, Message.CompassDesc, true);
		itemModifications.giveItem(p, Message.Colorize(Message.WardrobeName), Material.CHEST, 6, Message.WardrobeDesc, true);
		p.getInventory().setHeldItemSlot(4);
	}



	@EventHandler
	public void OnDamage (EntityDamageEvent e){
		if (e.getEntity() instanceof Player){
			Player p = (Player) e.getEntity();
			if (e.getCause() == DamageCause.VOID){
				e.setCancelled(true);
				p.teleport(p.getWorld().getSpawnLocation().add(0,2,0));
			}
		}
	}

	@EventHandler
	public void OnPlayerJoin(PlayerJoinEvent e) throws SQLException{
		Player p = e.getPlayer();
		p.teleport(p.getWorld().getSpawnLocation());
		GivePlayerStarter(p);

		for (String pName : doNotShow){
			Player pl = PlayersInfo.getPlayer(pName);
			pl.hidePlayer(p);
			p.showPlayer(pl);
		}

	}

	public static void GivePlayerStarter(Player p) throws SQLException {
		for (PotionEffect pe : p.getActivePotionEffects())
			p.removePotionEffect(pe.getType());

		GiveServerTools(p);
	}
	
	@EventHandler
	public void onDragEvent (InventoryClickEvent e){
		if (!e.getWhoClicked().isOp()){
			e.setCancelled(true);
		}
	}


	@EventHandler
	public void OnSignUse (PlayerInteractEvent e){
		Action ac = e.getAction();
		Player p = e.getPlayer();
		if (ac == Action.RIGHT_CLICK_AIR || ac == Action.RIGHT_CLICK_BLOCK){
			if (itemModifications.compareItems(p.getItemInHand(), Material.COMPASS, Message.CompassName))
				OpenServersChoice(p);
			else if (itemModifications.compareItems(p.getItemInHand(), Material.CHEST, Message.WardrobeName))
				OpenWardrobe(p);
			else if (itemModifications.compareItems(p.getItemInHand(), Material.WATCH, Message.ClockerName))
				runClocker(p);
			else{
				// Sign selection herez....
				if(ServerSignManagement.blockIsSign(e.getClickedBlock())){
					String serverName = SignListener.getServerSign(e.getClickedBlock());
					if (serverName != null){
						if (serverName.contains("----Sub")){
							// We are removing a sub server...
							String[] splitInfo = serverName.split("----");
							String[] splitMaster = splitInfo[0].split(":");
							String[] splitSub = splitInfo[1].split(":");
							String masterName = splitMaster[1];
							String subName = splitSub[1];

							ServerConnect(Main.ServerList.get(masterName).getSubserver(subName).getBungeeName(), p);
						}
						else{
							String[] splitMaster = serverName.split(":");
							String masterName = splitMaster[1];
							ServerConnect(Main.ServerList.get(masterName).getBungeeName(), p);
						}
					}
				}
			}
		}
	}

	private void runClocker(final Player p) {
		if (!delayAction.contains(p.getName())){
			if (doNotShow.contains(p.getName())){
				doNotShow.remove(p.getName());
				Message.P(p, Message.canSeeEveryone, true);
				for (Player pl : Bukkit.getOnlinePlayers()){
					p.showPlayer(pl);
				}
			}
			else{
				doNotShow.add(p.getName());
				Message.P(p, Message.everyoneHidden, true);
				for (Player pl : Bukkit.getOnlinePlayers()){
					p.hidePlayer(pl);
				}
			}
			delayAction.add(p.getName());
			Bukkit.getScheduler().runTaskLater(plugin, new Runnable(){
				@Override
				public void run(){
					delayAction.remove(p.getName());
				}
			}, 20 * 2);

		}
		else
			Message.P(p, "&cPlease wait before doing this action again.", true);
	}





	private void OpenWardrobe(final Player p) {
		// TODO Auto-generated method stub
		if (PlayerCommands.MENUS.containsKey(p.getName())){
			PlayerCommands.MENUS.get(p.getName()).getMenu().destroy();
			PlayerCommands.MENUS.remove(p.getName());
		}

		String IconMenuTitle = Message.Colorize("&aThe PvPKillz Wardrobe");


		int invsize = 54;

		ArrayList<Integer> bannedPos = new ArrayList<Integer>();
		IconMenu menu = new IconMenu(IconMenuTitle, p.getName(), invsize, new IconMenu.OptionClickEventHandler() {
			public void onOptionClick(final IconMenu.OptionClickEvent event) {
				if (event.getItemStack() != null){
					if (event.getItemStack().getType().name().toLowerCase().contains("helmet"))
						p.getInventory().setHelmet(event.getItemStack());
					else if (event.getItemStack().getType().name().toLowerCase().contains("chestplate"))
						p.getInventory().setChestplate(event.getItemStack());
					else if (event.getItemStack().getType().name().toLowerCase().contains("leggings"))
						p.getInventory().setLeggings(event.getItemStack());
					else if (event.getItemStack().getType().name().toLowerCase().contains("boots"))
						p.getInventory().setBoots(event.getItemStack());
					else if (Bukkit.getPlayer(ChatColor.stripColor(event.getName())) != null){
						if (p.hasPermission("pvp.hub.changeskin"))
						Main.factory.changeDisplay(p, ChatColor.stripColor(event.getName()), p.getName());
						else
							Message.P(p, Message.featurePvP, true);
					}
				}
				event.setWillClose(true);
				event.setWillDestroy(true);
			}
		}, plugin);

		menu.setOption(0, itemModifications.getItemInfo("298,1>white", null), "White helmet", Message.Colorize("&7Click to wear"));
		menu.setOption(9, itemModifications.getItemInfo("299,1>white", null), "White chestplate", Message.Colorize("&7Click to wear"));
		menu.setOption(18, itemModifications.getItemInfo("300,1>white", null), "White leggings", Message.Colorize("&7Click to wear"));
		menu.setOption(27, itemModifications.getItemInfo("301,1>white", null), "White boots", Message.Colorize("&7Click to wear"));
		menu.setOption(1, itemModifications.getItemInfo("298,1>red", null), "Red helmet", Message.Colorize("&7Click to wear"));
		menu.setOption(10, itemModifications.getItemInfo("299,1>red", null), "Red chestplate", Message.Colorize("&7Click to wear"));
		menu.setOption(19, itemModifications.getItemInfo("300,1>red", null), "Red leggings", Message.Colorize("&7Click to wear"));
		menu.setOption(28, itemModifications.getItemInfo("301,1>red", null), "Red boots", Message.Colorize("&7Click to wear"));
		menu.setOption(2, itemModifications.getItemInfo("298,1>blue", null), "Blue helmet", Message.Colorize("&7Click to wear"));
		menu.setOption(11, itemModifications.getItemInfo("299,1>blue", null), "Blue chestplate", Message.Colorize("&7Click to wear"));
		menu.setOption(20, itemModifications.getItemInfo("300,1>blue", null), "Blue leggings", Message.Colorize("&7Click to wear"));
		menu.setOption(29, itemModifications.getItemInfo("301,1>blue", null), "Blue boots", Message.Colorize("&7Click to wear"));
		menu.setOption(3, itemModifications.getItemInfo("298,1>green", null), "Green helmet", Message.Colorize("&7Click to wear"));
		menu.setOption(12, itemModifications.getItemInfo("299,1>green", null), "Green chestplate", Message.Colorize("&7Click to wear"));
		menu.setOption(21, itemModifications.getItemInfo("300,1>green", null), "Green leggings", Message.Colorize("&7Click to wear"));
		menu.setOption(30, itemModifications.getItemInfo("301,1>green", null), "Green boots", Message.Colorize("&7Click to wear"));
		menu.setOption(4, itemModifications.getItemInfo("298,1>black", null), "Black helmet", Message.Colorize("&7Click to wear"));
		menu.setOption(13, itemModifications.getItemInfo("299,1>black", null), "Black chestplate", Message.Colorize("&7Click to wear"));
		menu.setOption(22, itemModifications.getItemInfo("300,1>black", null), "Black leggings", Message.Colorize("&7Click to wear"));
		menu.setOption(31, itemModifications.getItemInfo("301,1>black", null), "Black boots", Message.Colorize("&7Click to wear"));
		
		menu.setOption(6, itemModifications.getItemInfo("302,1", null), "Chain helmet", Message.Colorize("&7Click to wear"));
		menu.setOption(15, itemModifications.getItemInfo("303,1", null), "Chain chestplate", Message.Colorize("&7Click to wear"));
		menu.setOption(24, itemModifications.getItemInfo("304,1", null), "Chain leggings", Message.Colorize("&7Click to wear"));
		menu.setOption(33, itemModifications.getItemInfo("305,1", null), "Chain boots", Message.Colorize("&7Click to wear"));
		menu.setOption(7, itemModifications.getItemInfo("314,1", null), "Gold helmet", Message.Colorize("&7Click to wear"));
		menu.setOption(16, itemModifications.getItemInfo("315,1", null), "Gold chestplate", Message.Colorize("&7Click to wear"));
		menu.setOption(25, itemModifications.getItemInfo("316,1", null), "Gold leggings", Message.Colorize("&7Click to wear"));
		menu.setOption(34, itemModifications.getItemInfo("317,1", null), "Gold boots", Message.Colorize("&7Click to wear"));
		menu.setOption(8, itemModifications.getItemInfo("310,1", null), "Diamond helmet", Message.Colorize("&7Click to wear"));
		menu.setOption(17, itemModifications.getItemInfo("311,1", null), "Diamond chestplate", Message.Colorize("&7Click to wear"));
		menu.setOption(26, itemModifications.getItemInfo("312,1", null), "Diamond leggings", Message.Colorize("&7Click to wear"));
		menu.setOption(35, itemModifications.getItemInfo("313,1", null), "Diamond boots", Message.Colorize("&7Click to wear"));
		
		for (int slot = 45; slot <= 53; slot++){
			Player pl = (Player) Bukkit.getOnlinePlayers().toArray()[ConvertTimings.randomInt(0, Bukkit.getOnlinePlayers().size() - 1)];
			menu.setOption(slot, itemModifications.getItemInfo("397:3,1", null), Message.Colorize("&7"+pl.getName()), Message.Colorize("&7Click to disguise as " + pl.getName()));
		}
		
		
		bannedPos.clear();
		InventoryView invView = menu.open(p);
		PlayerCommands.MENUS.put(p.getName(), new IconInfo(menu, "Wardrobe", invView));
	}





	public static void OpenServersChoice (final Player p){

		if (PlayerCommands.MENUS.containsKey(p.getName())){
			PlayerCommands.MENUS.get(p.getName()).getMenu().destroy();
			PlayerCommands.MENUS.remove(p.getName());
		}

		String IconMenuTitle = Message.Colorize(Message.ServersMenuTitle);
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
		IconMenu menu = new IconMenu(IconMenuTitle, p.getName(), invsize, new IconMenu.OptionClickEventHandler() {
			public void onOptionClick(final IconMenu.OptionClickEvent event) {
				plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
					public void run() {
						MenuSubmission(getListingName(ChatColor.stripColor(event.getName())), event.getPlayer());
					}
				}, 2L);
				event.setWillClose(true);
				event.setWillDestroy(true);
			}
		}, plugin);


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


			String[] info = new String[coloredcontainer.size()];
			info = coloredcontainer.toArray(info);
			menu.setOption(pos, serverIcon, ChatColor.GOLD + serverName, info);
			coloredcontainer.clear();
			pos = tempPos;
			if (serverInfo.getPos() < pos){
				pos++;
			}
		}
		bannedPos.clear();
		InventoryView invView = menu.open(p);

		PlayerCommands.MENUS.put(p.getName(), new IconInfo(menu, "masterList", invView));
	}
	private static String getListingName(String displayName) {
		// TODO Auto-generated method stub
		for (Entry<String, ServerInfo> item : Main.ServerList.entrySet()){
			if (ChatColor.stripColor(Message.Colorize(item.getValue().getDisplayName())).equalsIgnoreCase(displayName))
				return item.getKey();
		}
		return null;
	}

	private static void MenuSubmission(final String serverClicked, Player p) {
		if (Main.ServerList.get(serverClicked).getSubservers().isEmpty()){
			// Send to server directly...
			ServerConnect(serverClicked, p);
		}
		else{
			String IconMenuTitle = Message.Colorize(Message.Replacer(Message.ServersExpandTitle, serverClicked, "%type"));
			int pos = 18;
			int invsize = 54;

			IconMenu menu = new IconMenu(IconMenuTitle, p.getName(), invsize, new IconMenu.OptionClickEventHandler() {
				public void onOptionClick(final IconMenu.OptionClickEvent event) {
					plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
						public void run() {
							if (event.getPosition() == 0){
								OpenServersChoice(event.getPlayer());
							}
							else
								SubserverJoin(serverClicked, ChatColor.stripColor(event.getName()), event.getPlayer());
						}
					}, 2L);
					event.setWillClose(true);
					event.setWillDestroy(true);
				}
			}, plugin);


			ItemStack backItem = new ItemStack(Material.STAINED_GLASS_PANE);
			backItem.setDurability((short) 4);
			menu.setOption(0, backItem, Message.Colorize(Message.ReturnToMainMenu), new String[0]);



			for (ServerInfo serverInfo: Main.ServerList.get(serverClicked).getSubservers()){
				String serverName = Message.Colorize(serverInfo.getDisplayName());
				ItemStack serverIcon = serverInfo.getIcon();


				if (serverInfo.getStatus() != null){
					serverIcon.setAmount(serverInfo.getStatus().getPlayers().getOnline());
				}

				List<String> container = serverInfo.getDescription();
				if (serverInfo.getStatus() != null)
					container = ServerSignManagement.getFilteredDesc(container, serverInfo);

				ArrayList<String> coloredcontainer = new ArrayList<String>();
				for (String continfo : container)
					coloredcontainer.add(Message.Colorize(continfo));

				String[] info = new String[coloredcontainer.size()];
				info = coloredcontainer.toArray(info);
				menu.setOption(pos, serverIcon, ChatColor.GOLD + serverName, info);
				coloredcontainer.clear();
				pos++;
			}
			InventoryView invView = menu.open(p);
			PlayerCommands.MENUS.put(p.getName(), new IconInfo(menu, "subList", invView));
		}
	}

	private static void SubserverJoin(String subserverCatagory, String subserver, Player p) {
		// Code the sub server to be joined and then work on the signs system and that is it.
		for (ServerInfo info : Main.ServerList.get(subserverCatagory).getSubservers()){
			if (ChatColor.stripColor(Message.Colorize(info.getDisplayName())).equalsIgnoreCase(subserver)){
				ServerConnect(info.getBungeeName(), p);
				break;
			}
		}
	}



	public static void ServerConnect(String server, Player p) {

		if (server != null){
			plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, "BungeeCord");
			ByteArrayOutputStream b = new ByteArrayOutputStream();
			DataOutputStream out = new DataOutputStream(b);
			try
			{
				out.writeUTF("Connect");
				out.writeUTF(server);
			}
			catch (IOException localIOException)
			{
			}

			((PluginMessageRecipient)p).sendPluginMessage(plugin, "BungeeCord", b.toByteArray());
		}	
	}


}
