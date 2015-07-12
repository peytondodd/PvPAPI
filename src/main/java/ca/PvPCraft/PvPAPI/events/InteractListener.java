package ca.PvPCraft.PvPAPI.events;

import java.sql.SQLException;

import ca.PvPCraft.PvPAPI.Main;
import ca.PvPCraft.PvPAPI.methods.Locations;
import ca.PvPCraft.PvPAPI.methods.PlayersInfo;
import ca.PvPCraft.PvPAPI.utilities.Files;
import ca.PvPCraft.PvPAPI.utilities.Message;
import ca.PvPCraft.PvPAPI.utilities.Mysql;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class InteractListener implements Listener{
	public static Main plugin;
	
	public InteractListener(Main mainclass) {
		plugin = mainclass;
		mainclass.getServer().getPluginManager().registerEvents(this, mainclass);
		}
	
	@EventHandler
	public void OnInteractEvent (PlayerInteractEvent e) throws SQLException{
		Player p = e.getPlayer();
		if (PlayersInfo.editingAdmin.containsKey(p.getName())){
			if (PlayersInfo.editingAdmin.get(p.getName()).equalsIgnoreCase("tutorialEnd")){
				Locations.SaveLocation(Files.teleports, e.getClickedBlock().getLocation(), "Tutorial.End");
				PlayersInfo.editingAdmin.remove(p.getName());
				Message.P(p, Message.Replacer(Message.TutorialEndSetupDone, Message.CleanCapitalize(e.getClickedBlock().getType().name()), "%block"), true);
			}
			e.setCancelled(true);
		}
		if (PlayersInfo.notCompletedTutorial(p.getName()) && Main.TutorialEnabled && e.getClickedBlock() != null && Locations.tutorialEndLoc != null){
			if (Locations.locationSameAs(e.getClickedBlock().getLocation(), Locations.tutorialEndLoc)){
				Mysql.completedTutorial(p.getName());
				Message.P(p, Message.YouFinishedTutorial, true);
			}
		}
	}
	
	
	
	@EventHandler
	public void OnDropEvent (PlayerDropItemEvent e){
		if (Main.DisableDrops){
			if (!Main.filterDrops.contains(e.getItemDrop().getItemStack().getTypeId()))
				e.setCancelled(true);
		}
		else
			if (Main.filterDrops.contains(e.getItemDrop().getItemStack().getTypeId()))
				e.setCancelled(true);
	}
	
	/*
	@EventHandler
	public void InventoryClick (InventoryClickEvent e){
		if (e.getWhoClicked() instanceof Player){
			Player p = (Player) e.getWhoClicked();
			ItemStack is = e.getCursor();
			if (is.hasItemMeta()){
				if (is.getType() == Material.NETHER_STAR){
					if (is.getItemMeta().hasDisplayName()){
						if (is.getItemMeta().getDisplayName().toLowerCase().contains("player bandaid")){
							if (e.getSlot() >= 0 && p.getInventory().getItem(e.getSlot()) != null){
								ItemStack newIS = p.getInventory().getItem(e.getSlot());
								if (newIS.hasItemMeta()){
									if (newIS.getType() == Material.NETHER_STAR){
										if (newIS.getItemMeta().hasDisplayName()){
											if (newIS.getItemMeta().getDisplayName().toLowerCase().contains("player bandaid")){
												e.setCancelled(true);
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}*/
}
