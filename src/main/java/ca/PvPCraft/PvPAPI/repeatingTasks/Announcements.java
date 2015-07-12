package ca.PvPCraft.PvPAPI.repeatingTasks;

import java.util.HashMap;

import ca.PvPCraft.PvPAPI.Main;
import ca.PvPCraft.PvPAPI.utilities.Message;

import org.bukkit.scheduler.BukkitRunnable;




public class Announcements extends BukkitRunnable {

	static Main plugin;
	int MessageID = 0;
	public static HashMap<String, String> MessagesModify = new HashMap<String, String>();

    public Announcements() {

    }

	@Override
	public void run() {
		if (MessageID >= Main.Announcements.size())
			MessageID = 0;
		
		String message = Main.Announcements.get(MessageID);
		Message.G(message, true);
		
		MessageID++;
	}
}
