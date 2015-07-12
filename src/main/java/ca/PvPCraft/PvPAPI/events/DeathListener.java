package ca.PvPCraft.PvPAPI.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;
import java.util.ArrayList;

import ca.PvPCraft.PvPAPI.Main;
import ca.PvPCraft.PvPAPI.methods.ConvertTimings;
import ca.PvPCraft.PvPAPI.methods.EconSystem;
import ca.PvPCraft.PvPAPI.methods.PlayersInfo;
import ca.PvPCraft.PvPAPI.utilities.Files;
import ca.PvPCraft.PvPAPI.utilities.Message;


public class DeathListener implements Listener{
	public static Main plugin;
	
	public DeathListener(Main mainclass) {
		plugin = mainclass;
		
		for (String msgs : Files.deathconf.getCustomConfig().getStringList("PvP"))
			PvPDeathMessages.add(msgs);
		for (String msgs : Files.deathconf.getCustomConfig().getStringList("PvP-Bow"))
			BowDeathMessages.add(msgs);
		for (String msgs : Files.deathconf.getCustomConfig().getStringList("Fall"))
			FallDeathMessages.add(msgs);
		for (String msgs : Files.deathconf.getCustomConfig().getStringList("Explode"))
			ExplodeDeathMessages.add(msgs);
		for (String msgs : Files.deathconf.getCustomConfig().getStringList("Drown"))
			DrownDeathMessages.add(msgs);
		for (String msgs : Files.deathconf.getCustomConfig().getStringList("Poison"))
			PoisonDeathMessages.add(msgs);
		for (String msgs : Files.deathconf.getCustomConfig().getStringList("Fire"))
			FireDeathMessages.add(msgs);
		for (String msgs : Files.deathconf.getCustomConfig().getStringList("Suicide"))
			SuicideDeathMessages.add(msgs);
		for (String msgs : Files.deathconf.getCustomConfig().getStringList("FallBlock"))
			FallblockDeathMessages.add(msgs);
		for (String msgs : Files.deathconf.getCustomConfig().getStringList("Lightning"))
			LightningDeathMessages.add(msgs);
		
		
		mainclass.getServer().getPluginManager().registerEvents(this, mainclass);
		}
	
	public static ArrayList<String> PvPDeathMessages = new ArrayList<String>();
	public static ArrayList<String> BowDeathMessages = new ArrayList<String>();
	public static ArrayList<String> FallDeathMessages = new ArrayList<String>();
	public static ArrayList<String> ExplodeDeathMessages = new ArrayList<String>();
	public static ArrayList<String> DrownDeathMessages = new ArrayList<String>();
	public static ArrayList<String> PoisonDeathMessages = new ArrayList<String>();
	public static ArrayList<String> FireDeathMessages = new ArrayList<String>();
	public static ArrayList<String> SuicideDeathMessages = new ArrayList<String>();
	public static ArrayList<String> FallblockDeathMessages = new ArrayList<String>();
	public static ArrayList<String> LightningDeathMessages = new ArrayList<String>();

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        Player p = e.getEntity();
        e.setDeathMessage(RandomlyGenerateFunnyQuote(p));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onKill(PlayerDeathEvent e) throws SQLException{
        Player defender = e.getEntity();
        Player damager = e.getEntity().getKiller();

        if (Main.FactionsMoneyPercentLoss > 0){
            if (Main.EconomyEnable){
                int percentLoss = Main.FactionsMoneyPercentLoss;
                double amtChange = EconSystem.getMoney(defender.getName()) * (percentLoss/100);
                amtChange = Math.round(amtChange);
                if (amtChange > 0){
                    EconSystem.modifyMoney(damager.getName(), amtChange, true);
                    EconSystem.modifyMoney(defender.getName(), -amtChange, true);
                }
            }
        }

        for (ItemStack item : e.getDrops()){
            if (Main.DisableDrops){
                if (!Main.filterDrops.contains(item.getTypeId()))
                    item.setAmount(0);
            } else if (Main.filterDrops.contains(item.getTypeId())) {
                item.setAmount(0);
            }
        }
        PlayersInfo.EliminatedUser(damager, defender);
    }
	
	private String RandomlyGenerateFunnyQuote(Player p) {
		String phrase = "";
		String defwep = Message.CleanCapitalize(p.getItemInHand().getType().toString());
		Player dam = p.getKiller();
		if (p.getLastDamageCause() != null){
			if (dam != null){
				String damwep = Message.CleanCapitalize(dam.getItemInHand().getType().toString());
				if (damwep == "bow")
				phrase = BowDeathMessages.get(ConvertTimings.randomInt(0, BowDeathMessages.size() - 1));
				else
				phrase = PvPDeathMessages.get(ConvertTimings.randomInt(0, PvPDeathMessages.size() - 1));
				
		
				phrase = Message.Replacer(phrase, damwep, "%damwep");
				phrase = Message.Replacer(phrase, dam.getName(), "%dam");
			}
			else if (p.getLastDamageCause().getCause() == DamageCause.FALL)
				phrase = FallDeathMessages.get(ConvertTimings.randomInt(0, FallDeathMessages.size() - 1));
			else if (p.getLastDamageCause().getCause() == DamageCause.BLOCK_EXPLOSION)
				phrase = ExplodeDeathMessages.get(ConvertTimings.randomInt(0, ExplodeDeathMessages.size() - 1));
			else if (p.getLastDamageCause().getCause() == DamageCause.DROWNING)
				phrase = DrownDeathMessages.get(ConvertTimings.randomInt(0, DrownDeathMessages.size() - 1));
			else if (p.getLastDamageCause().getCause() == DamageCause.FIRE || p.getLastDamageCause().getCause() == DamageCause.LAVA || p.getLastDamageCause().getCause() == DamageCause.FIRE_TICK)
				phrase = FireDeathMessages.get(ConvertTimings.randomInt(0, FireDeathMessages.size() - 1));
			else if (p.getLastDamageCause().getCause() == DamageCause.LIGHTNING)
				phrase = LightningDeathMessages.get(ConvertTimings.randomInt(0, LightningDeathMessages.size() - 1));
			else if (p.getLastDamageCause().getCause() == DamageCause.SUICIDE)
				phrase = SuicideDeathMessages.get(ConvertTimings.randomInt(0, SuicideDeathMessages.size() - 1));
			else if (p.getLastDamageCause().getCause() == DamageCause.POISON)
				phrase = PoisonDeathMessages.get(ConvertTimings.randomInt(0, PoisonDeathMessages.size() - 1));
			else if (p.getLastDamageCause().getCause() == DamageCause.FALLING_BLOCK)
				phrase = FallblockDeathMessages.get(ConvertTimings.randomInt(0, FallblockDeathMessages.size() - 1));
		}
		else
			phrase = SuicideDeathMessages.get(ConvertTimings.randomInt(0, SuicideDeathMessages.size() - 1));
		
		phrase = Message.Replacer(phrase, defwep, "%defwep");
		phrase = Message.Replacer(phrase, p.getName(), "%def");
			return Message.Colorize("&7" + phrase);
	}
}
