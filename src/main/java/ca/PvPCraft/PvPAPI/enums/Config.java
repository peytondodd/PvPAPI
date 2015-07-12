package ca.PvPCraft.PvPAPI.enums;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;

import ca.PvPCraft.PvPAPI.Main;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

	public class Config{
		private String name;
		private File file;
		private FileConfiguration fileConfig;
		private Main plugin;
		public Config(String name, Main pl){
			this.name = name;
			this.plugin = pl;
		}
		
		public FileConfiguration getCustomConfig() {
			if (fileConfig == null){
			reloadCustomConfig();
			}
			return fileConfig;
		}
		@SuppressWarnings("deprecation")
		public void reloadCustomConfig() {
			if (fileConfig == null) {
			file = new File(plugin.getDataFolder(), name + ".yml");
				if (!file.exists()) {
					file.getParentFile().mkdirs();
					copy(plugin.getResource(name + ".yml"), file);
				}
			}
			file = new File(plugin.getDataFolder(), name + ".yml");
			fileConfig = YamlConfiguration.loadConfiguration(file);
			 
			InputStream defConfigStream = plugin.getResource(name + ".yml");
			if (defConfigStream != null) {
				YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
				fileConfig.setDefaults(defConfig);
			}
		}
		public void saveCustomConfig() {
			if (fileConfig == null || file == null) {
			return;
			}
			try {
				getCustomConfig().save(file);
			} catch (IOException ex) {
				plugin.getLogger().log(Level.SEVERE, "Could not save config to " + file, ex);
			}
		}
		public void saveDefaultConfig() {
			if (file == null) {
				file = new File(plugin.getDataFolder(), name + ".yml");
			}
			if (!file.exists()) {
				plugin.saveResource(name + ".yml", false);
			}
		}
		public static void copy(InputStream in, File file) {
			try {
				OutputStream out = new FileOutputStream(file);
				byte[] buf = new byte[1024];
				int len;
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
				out.close();
				in.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	