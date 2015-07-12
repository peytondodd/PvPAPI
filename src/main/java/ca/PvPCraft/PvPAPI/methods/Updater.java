package ca.PvPCraft.PvPAPI.methods;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;

import ca.PvPCraft.PvPAPI.Main;
import ca.PvPCraft.PvPAPI.utilities.Message;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Updater {
	private static String dlLink = null;
	private static String versionLink = null;

	private static Main plugin;

	public Updater(Main mainClass) {
		plugin = mainClass;
		dlLink = "http://pvpcraft.ca:8080/job/" + plugin.getName() + "/lastStableBuild/" + plugin.getName() + "$" + plugin.getName() + "/artifact/" + "/" + plugin.getName() + "/" + plugin.getName() + "/" + ".jar";
		versionLink = "http://PvP.Kz/myPlugins/version.php?plugin=" + plugin.getName();
	}

	public static void update() {
		int oldVersion = getVersionFromString(plugin.getDescription().getVersion());
		String path = getFilePath();

		try {
			URL url = new URL(versionLink);
			URLConnection con = url.openConnection();
			
			InputStreamReader isr = new InputStreamReader(con.getInputStream());
			BufferedReader reader = new BufferedReader(isr);
			String pluginName = reader.readLine().split(": ")[1];
			String version = reader.readLine().split(": ")[1];
			String lastUpdate = reader.readLine().split(": ")[1];
			String importance = reader.readLine().split(": ")[1];
			int newVersion = getVersionFromString(version);
			reader.close();
			if(newVersion > oldVersion) {


				if (!importance.equalsIgnoreCase("URGENT") && Bukkit.getOnlinePlayers().size() == 0){
					Message.G("&8[&cAUTO&8] &7Restarting...", false);

					if (plugin.getServer().getScheduler().isCurrentlyRunning(Main.updaterTask.getTaskId()))
						plugin.getServer().getScheduler().cancelTask(Main.updaterTask.getTaskId());

					plugin.getServer().getScheduler().cancelTask(Main.updaterTask.getTaskId());

					plugin.getLogger().log(Level.INFO, "Succesfully updated plugin to v" + newVersion);
					plugin.getLogger().log(Level.INFO, "Reload/restart server to enable changes");
					url = new URL(dlLink);
					con = url.openConnection();
					InputStream in = con.getInputStream();
					FileOutputStream out = new FileOutputStream(path);
					byte[] buffer = new byte[1024];
					int size = 0;
					while((size = in.read(buffer)) != -1) {
						out.write(buffer, 0, size);
					}

					out.close();
					in.close();



					Bukkit.shutdown();
				}
				else if (importance.equalsIgnoreCase("URGENT")){

					if (plugin.getServer().getScheduler().isCurrentlyRunning(Main.updaterTask.getTaskId()))
						plugin.getServer().getScheduler().cancelTask(Main.updaterTask.getTaskId());

					plugin.getServer().getScheduler().cancelTask(Main.updaterTask.getTaskId());

					plugin.getLogger().log(Level.INFO, "Succesfully updated plugin to v" + newVersion);
					plugin.getLogger().log(Level.INFO, "Reload/restart server to enable changes");


					Message.G("&8[&cURGENT&8] &7Restarting server in 1 minute to apply new update.", false);

					url = new URL(dlLink);
					con = url.openConnection();
					InputStream in = con.getInputStream();
					FileOutputStream out = new FileOutputStream(path);
					byte[] buffer = new byte[1024];
					int size = 0;
					while((size = in.read(buffer)) != -1) {
						out.write(buffer, 0, size);
					}

					out.close();
					in.close();
					
					plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
						public void run() {
							Message.G("&8[&cURGENT&8] &7Restarting...", false);
							Bukkit.shutdown();
						}
					}, 20L * 60 * 1);

				}
			}
		} catch(IOException e) {
			Message.C("Failed to auto-update.");
		}
	}


	private static String getFilePath() {
		if(plugin instanceof JavaPlugin) {
			try {
				Method method = JavaPlugin.class.getDeclaredMethod("getFile");
				boolean wasAccessible = method.isAccessible();
				method.setAccessible(true);
				File file = (File) method.invoke(plugin);
				method.setAccessible(wasAccessible);

				return file.getPath();
			} catch(Exception e) {
				return "plugins" + File.separator + plugin.getName();
			}
		} else {
			return "plugins" + File.separator + plugin.getName();
		}
	}

	private static int getVersionFromString(String from) {
		String result = "";
		if (from.contains("."))
			result = from.replace(".", "");


		return result.isEmpty() ? 0 : Integer.parseInt(result);
	}
}