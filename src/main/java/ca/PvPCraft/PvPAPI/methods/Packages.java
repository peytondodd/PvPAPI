package ca.PvPCraft.PvPAPI.methods;

import ca.PvPCraft.PvPAPI.Main;
import ca.PvPCraft.PvPAPI.utilities.Message;
import ca.PvPCraft.PvPAPI.utilities.Fanciful.FancyMessage;

import org.bukkit.entity.Player;

import static com.rosaloves.bitlyj.Bitly.shorten;

import com.rosaloves.bitlyj.Bitly;
import com.rosaloves.bitlyj.Url;


public class Packages {

	public static void printPackageBuy(String packageName, Player p) {

		String webLink = "http://" + Main.PackageWebsite + "?package=" + packageName + "&userName=" + p.getName();
		Url url = Bitly.as("o_62e1o0l1eq", "R_25e8926b13dbdbab17d3aa7fed8d3210").call(shorten(webLink));
		String link = url.getShortUrl();
		Message.P(p, Message.HeaderMenu, false);
		Message.P(p, Message.Replacer(Message.ChosenPackage, packageName, "%name"), false);
		Message.P(p, Message.PackageLink, false);
		new FancyMessage("&7Buy link: ")
		.then("&a"+link)
		.link(link)
		.tooltip("&7Click to open shop link.").send(p);
		Message.P(p, Message.HeaderMenu, false);
	}
}
