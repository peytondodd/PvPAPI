package ca.PvPCraft.PvPAPI.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

























import ca.PvPCraft.PvPAPI.Main;
import ca.PvPCraft.PvPAPI.utilities.Stats;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTargetEvent;

public class DamageListener implements Listener{
	public static Main plugin;
	public static HashMap<String, ArrayList<UUID>> HitPreviously = new HashMap<String, ArrayList<UUID>>();// FullTeam Kills	

	public DamageListener(Main mainclass) {
		plugin = mainclass;
		mainclass.getServer().getPluginManager().registerEvents(this, mainclass);
		}
	
	

	@EventHandler
	public void OnEntityTarget (EntityTargetEvent e){
		/*
		Entity ent = e.getEntity();
		if (ent instanceof Monster){
			Monster monster = (Monster) ent;
			
			if (e.getTarget() instanceof Player){
				Player p = (Player)e.getTarget();
				if (PlayersInfo.PvEDisable.contains(p.getName()) && e.getReason() != TargetReason.TARGET_ATTACKED_ENTITY){
					monster.setTarget(null);
				}
				else if (PlayersInfo.PvEDisable.contains(p.getName()) && e.getReason() == TargetReason.TARGET_ATTACKED_ENTITY){
					if (!HitPreviously.containsKey(p.getName()))
						HitPreviously.put(p.getName(), new ArrayList<UUID>());
				}
			}
		}
		*/
	}
	
	@EventHandler
	public void BlockAttackingEntDmg (EntityDamageByEntityEvent e){
		if (e.getEntity() instanceof Player){
			Player def = (Player) e.getEntity();
			if (e.getDamager() instanceof Player){
				Player dam  = (Player) e.getDamager();
				
				
				Stats.addToAssist(dam, def);
				
				
				
				/*
				if (PlayersInfo.PvPDisable.contains(def.getName()) || PlayersInfo.PvPDisable.contains(dam.getName())){
					
						if (PlayersInfo.PvPDisable.contains(dam.getName()))
							Message.P(def, Message.Replacer(Message.Replacer(Message.CurrentMode, "disabled", "%status"), "PvE", "%mode"), true);
						else if (PlayersInfo.PvPDisable.contains(def.getName())){
							Message.P(def, Message.Replacer(Message.Replacer(Message.CurrentMode, "disabled", "%status"), "PvE", "%mode"), true);
							Message.P(dam, Message.Replacer(Message.Replacer(Message.CurrentMode, "disabled", "%status"), "PvE", "%mode"), true);
						
					}
				}
				*/
			}
			/*
			else if (e.getDamager() instanceof Monster){
				if (PlayersInfo.PvEDisable.contains(def.getName())){
					e.setCancelled(true);
					Message.P(def, Message.Replacer(Message.Replacer(Message.CurrentMode, "disabled", "%status"), "PvE", "%mode"), true);
				}
			}
			*/
		
		
		}
		else if (e.getEntity() instanceof Player){
			/*
			Player p = (Player) e.getDamager();
			
			if (e.getDamager() instanceof Monster){
				if (HitPreviously.containsKey(p.getName()) && PlayersInfo.PvEDisable.contains(p.getName())){
					e.setCancelled(true);
					Message.P(p, Message.Replacer(Message.Replacer(Message.CurrentMode, "disabled", "%status"), "PvE", "%mode"), true);
				}
			}
			*/
	   }
	}
	
	
	
	
	
}
