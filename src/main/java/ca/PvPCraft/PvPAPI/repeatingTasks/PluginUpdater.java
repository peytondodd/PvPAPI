package ca.PvPCraft.PvPAPI.repeatingTasks;

import ca.PvPCraft.PvPAPI.methods.Updater;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;




public class PluginUpdater extends BukkitRunnable {

	public PluginUpdater() {
		
	}

	@Override
	public void run() {
		if (Bukkit.getOnlinePlayers().size() == 0)
			Updater.update();
	}
}
