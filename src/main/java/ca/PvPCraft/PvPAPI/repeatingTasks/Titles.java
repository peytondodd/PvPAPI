package ca.PvPCraft.PvPAPI.repeatingTasks;

import java.util.HashMap;

import ca.PvPCraft.PvPAPI.Main;
import ca.PvPCraft.PvPAPI.utilities.Message;
import ca.PvPCraft.PvPAPI.utilities.Bar.BarAPI;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;




public class Titles extends BukkitRunnable {

	int MessageID = 0;
	public static HashMap<String, String> MessagesModify = new HashMap<String, String>();
	public static HashMap<String, Integer> colorSetup = new HashMap<String, Integer>();
	public static char[] colorsWanted = "C6EAD9BF".toCharArray();
	public Titles() {

	}

	@Override
	public void run() {
		if (MessageID >= Main.Titles.size())
			MessageID = 0;

		StringBuilder coloredVer = new StringBuilder();
		String message = Main.Titles.get(MessageID);
		if (message.toLowerCase().contains("<rb>")){
			// We need to increase the shift, and reset when it gets to the chars size.
			String[] coloredSets = message.split("<rb>");
			for (int x = 0; x < coloredSets.length; x++){
				String coloredLine = coloredSets[x];
				
				if (x % 2 == 0 || x == 0){
					// Even number thus normal text not to be colored.
					coloredVer.append(coloredLine);
				}
				else{
					// Odd number thus rainbow test --> To be colored
					if (!colorSetup.containsKey(coloredLine))
						colorSetup.put(coloredLine, colorsWanted.length - 1);
					char[] coloredLineChars = coloredLine.toCharArray();
					int colorIndex = colorSetup.get(coloredLine);
					
					for (int charNum = 0; charNum < coloredLineChars.length; charNum++){
						if (coloredLineChars[charNum] != ' '){
							
							if (colorIndex >= 0){
								coloredVer.append("&" + colorsWanted[colorIndex] + coloredLineChars[charNum]);
								colorIndex--;
							}
							else{
								colorIndex = colorsWanted.length - 1;
								coloredVer.append("&" + colorsWanted[colorIndex] + coloredLineChars[charNum]);
							}
						}
						else
							coloredVer.append(coloredLineChars[charNum]);
					}


					if (colorSetup.get(coloredLine) > 0)
						colorSetup.put(coloredLine, colorSetup.get(coloredLine) - 1);
					else
						colorSetup.put(coloredLine, colorsWanted.length - 1);
				}
			}
		}
		else
			coloredVer.append(message);
		
		//float percentage = (float)(MessageID / Main.Titles.size() + 1) * 100f;
		String fullLine = coloredVer.toString();
		BarAPI.setMessage(Message.Colorize(fullLine));

		MessageID++;
	}
}
