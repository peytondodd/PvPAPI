package ca.PvPCraft.PvPAPI.methods;

import java.util.logging.Logger;

import ca.PvPCraft.PvPAPI.Main;
import ca.PvPCraft.PvPAPI.utilities.Message;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredServiceProvider;


public class EconSystem implements Listener{
	public static Main plugin;
	static Logger log = Bukkit.getLogger();
	public static Economy econ;
	public EconSystem(Main mainclass) {
		plugin = mainclass;
		mainclass.getServer().getPluginManager().registerEvents(this, mainclass);
		setupEconomy();
	}

	private boolean setupEconomy() {
		RegisteredServiceProvider<Economy> rsp = plugin.getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null) {
			return false;
		}
		econ = rsp.getProvider();
		return econ != null;
	}


	public static boolean modifyMoney(String player, double amt, boolean Notify){
		Player p = PlayersInfo.getPlayer(player);
		if (amt < 0){
			EconomyResponse take = econ.withdrawPlayer(p, amt);
			amt = Math.abs(amt);

			if(take.transactionSuccess()) {
				if (Notify)
					Message.P(p, Message.Replacer(Message.MoneyModify, Message.LostMoney, "%type"), true);
				return true;
			} else {
				Message.P(p, String.format("An error occured: %s", take.errorMessage), true);
				return false;
			}
		}
		else if (amt > 0){
			EconomyResponse give = econ.depositPlayer(p, amt);

			if(give.transactionSuccess()) {
				if (Notify)
					Message.P(p, Message.Replacer(Message.MoneyModify, Message.EarnedMoney, "%type"), true);
				return true;
			} else {
				Message.P(p, String.format("An error occured: %s", give.errorMessage), true);
				return false;
			}
		}

		return true;
	}

	public static double getMoney(String player){
		Player p = PlayersInfo.getPlayer(player);
		return econ.getBalance(p);
	}


}
