package ca.PvPCraft.PvPAPI.repeatingTasks;

import ca.PvPCraft.PvPAPI.Main;
import ca.PvPCraft.PvPAPI.enums.ScoreboardUtility;
import ca.PvPCraft.PvPAPI.methods.Scoreboards;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;




public class ScoreboardUpdater extends BukkitRunnable {

	static Main plugin;

	public ScoreboardUpdater() {

	}

	@Override
	public void run() {
		
		for (final Player p : Bukkit.getOnlinePlayers()){
			Scoreboards.updateScoreboardEntries(p, Main.scoreBoardTitles, null);
		}
	}
}
