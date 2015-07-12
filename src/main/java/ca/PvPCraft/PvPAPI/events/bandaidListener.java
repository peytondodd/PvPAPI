package ca.PvPCraft.PvPAPI.events;

import java.util.HashMap;

import ca.PvPCraft.PvPAPI.Main;
import ca.PvPCraft.PvPAPI.utilities.Message;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;


public class bandaidListener implements Listener{
	
	public static Main plugin;
	public static HashMap<String, Boolean> Taken = new HashMap<String, Boolean>();

	public bandaidListener(Main mainclass) {
		plugin = mainclass;
		mainclass.getServer().getPluginManager().registerEvents(this, mainclass);
		}

	
	
	@EventHandler
	public void OnBlockChoose (PlayerInteractEvent e){
		final Player p = e.getPlayer();
		
		if ((e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)
				&& p.getItemInHand().hasItemMeta() == true && p.getItemInHand().getItemMeta().hasDisplayName() == true){
			
			if (p.getItemInHand().getItemMeta().getDisplayName().toLowerCase().contains("player bandaid")){
				if (p.getHealth() == p.getMaxHealth())
    				Message.P(p, Message.FullHelath, true);
				else if (p.getHealth() < p.getMaxHealth()){
					
					
    				if (10.0 <= p.getHealth())
    					p.setHealth(20.0);
    				else
    					p.setHealth(p.getHealth() + 10.0);
					
					
					
					if (p.getItemInHand().getAmount() > 1)
						p.getItemInHand().setAmount(p.getItemInHand().getAmount() - 1);
					else
						p.setItemInHand(new ItemStack(Material.AIR));
					p.updateInventory();
					
					p.setFireTicks(0);
				    for (PotionEffect effect : p.getActivePotionEffects()){
				        
				    	if (effect.getDuration() <= 20 * 60 * 5)
				    	p.removePotionEffect(effect.getType());
				    }
				}
				
			}
		}
	}
	
	@EventHandler
	public void InstantHeal (final PlayerToggleSneakEvent e){
		if (!Taken.containsKey(e.getPlayer().getName()))
			Taken.put(e.getPlayer().getName(), false);
		final Player p = e.getPlayer();
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			@SuppressWarnings("deprecation")
			public void run() {
		        if (e.isSneaking() != p.isSneaking()){
		        	for (ItemStack is : p.getInventory().getContents()){
		        		if (is != null && is.getTypeId() == 399){
		        			if (Taken.get(p.getName()) == false){
		        				if (is.hasItemMeta() && is.getItemMeta().hasDisplayName() == true && is.getItemMeta().getDisplayName().toLowerCase().contains("player bandaid"))
		        			
			        			if (p.hasPermission("pvp.bandaid.instantheal")){
				        			if (p.getHealth() == p.getMaxHealth())
				        				Message.P(p, Message.FullHelath, true);
				        			else if (p.getHealth() < p.getMaxHealth()){
				        				
				        				if (10.0 <= p.getHealth())
				        					p.setHealth(20.0);
				        				else
				        					p.setHealth(p.getHealth() + 10.0);
				        				
				        				p.setHealth(p.getMaxHealth());
				        				p.setFireTicks(0);
				        				
				        				
				        				
				        				for (PotionEffect effect : p.getActivePotionEffects()){
				        				    if (effect.getDuration() <= 20 * 60 * 5)
				        				    	p.removePotionEffect(effect.getType());
				        				    }
						        			if (is.getAmount() > 1)
						        				is.setAmount(is.getAmount() - 1);	
						        			else{
							        			p.getInventory().remove(Material.NETHER_STAR);
							        			is.setTypeId(0);
							        			p.getInventory().remove(is);
						        			}
						        	}
				        			p.updateInventory();
				        			
				        			Taken.put(e.getPlayer().getName(), true);
			        			}
		        			}
		        		}
		        	}
        			Taken.put(e.getPlayer().getName(), false);
		        }
			}
		}, 3L);
	}
}
