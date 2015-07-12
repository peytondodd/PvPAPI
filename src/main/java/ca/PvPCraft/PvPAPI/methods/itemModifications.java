package ca.PvPCraft.PvPAPI.methods;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import ca.PvPCraft.PvPAPI.Main;
import ca.PvPCraft.PvPAPI.utilities.Message;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public class itemModifications {

	public static Main plugin;
	static Logger log = Bukkit.getLogger();

	public itemModifications (Main mainclass){
		plugin = mainclass;
	}
	
	public static ArrayList<String> multilineFromString(String line){
		int totalLineLength = 0;
		String[] splitupDesc = line.split(" ");

		ArrayList<String> newInfoLine = new ArrayList<String>();
		StringBuilder lineCur = new StringBuilder();
		ChatColor lastColor = ChatColor.GRAY;
		
		
		for (int x = 0; x <= splitupDesc.length - 1; x++){
			String word = splitupDesc[x];
			if (word.contains("&"))
				if (ChatColor.getByChar(word.charAt(word.indexOf("&") + 1)) != null)
					lastColor = ChatColor.getByChar(word.charAt(word.indexOf("&") + 1));
			
			word = Message.Colorize(word);
			if ((totalLineLength + word.length()) >= 40 || x >= splitupDesc.length - 1){
				if ((totalLineLength + word.length()) >= 40)
					x--;
				else if (x >= splitupDesc.length - 1){
					if (lineCur.toString().isEmpty())
						lineCur.append(word);
					else
						lineCur.append(" " + word);
				}
				totalLineLength = 0;					

				newInfoLine.add(lastColor + lineCur.toString());
				lineCur =  new StringBuilder();
			}
			else{
				if (lineCur.toString().isEmpty())
					lineCur.append(word);
				else
					lineCur.append(" " + word);

				totalLineLength += word.length();
			}
		}
		return newInfoLine;
	}


	private static void giveIntroBook(Player p) {
		ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
		BookMeta meta = (BookMeta) book.getItemMeta();
		meta.setTitle(ChatColor.GRAY + "" + ChatColor.BOLD + "Welcome to PvPKillz");
		meta.setAuthor(ChatColor.AQUA + "PvPKillz");
		List<String> pages = new ArrayList<String>();
		pages.add(ChatColor.GRAY + "" + ChatColor.STRIKETHROUGH + "-----------\n" + 
				ChatColor.AQUA + "" + ChatColor.BOLD + "      Welcome\n" + ChatColor.RESET +
				ChatColor.GRAY + "" + ChatColor.STRIKETHROUGH + "-----------\n" + ChatColor.RESET +
				ChatColor.DARK_GRAY + 
				"What is PvPKillz?\n" + ChatColor.RESET + ChatColor.GRAY + 
				"PvPKillz is a gaming community which helps you play your most favorite minigames & gamemodes from one place, and also have the best experience.\n" +
				"Rules ►\n" + 
				"Need help? ►►");

		pages.add(
				ChatColor.GRAY + "" + ChatColor.STRIKETHROUGH + "-----------\n" + 
						ChatColor.DARK_RED + "" + ChatColor.BOLD + "        Rules\n" + ChatColor.RESET +
						ChatColor.GRAY + "" + ChatColor.STRIKETHROUGH + "-----------\n" + ChatColor.RESET +
						ChatColor.GOLD + "�? No Hacks/Mods\n"+
						ChatColor.GOLD + "�? Only English in public\n"+
						ChatColor.GOLD + "�? No swearing\n"+
						ChatColor.GOLD + "�? Respect everyone\n"+
						ChatColor.GOLD + "�? Follow gamemode rules\n" +
						ChatColor.GOLD + "For all rules:\n" +
						ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "http://PvP.Kz\n");
		pages.add(
				ChatColor.GRAY + "" + ChatColor.STRIKETHROUGH + "-----------\n" + 
						ChatColor.DARK_RED + "" + ChatColor.BOLD + "      Need Help?\n" + ChatColor.RESET +
						ChatColor.GRAY + "" + ChatColor.STRIKETHROUGH + "-----------\n" + ChatColor.RESET + 
						"For more help, please visit our forums at \n" +
						ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "http://PvPKillz.com\n" + ChatColor.RESET + " or email us at \n" + ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Support@PvPKillz.com"
				);
		meta.setPages(pages);
		book.setItemMeta(meta);
		p.getInventory().addItem(book);
	}


	public static void giveItem(Player p, String itemName, Material itemMaterial, int slot, String loreText, boolean doFakeEnchant) {
		ItemStack selector = new ItemStack(itemMaterial);


		itemName = Message.Colorize(itemName);
		loreText = Message.Colorize(loreText);


		ItemMeta im = selector.getItemMeta();
		im.setDisplayName(ChatColor.AQUA + itemName);
		List<String> lore = new ArrayList<String>();
		if (loreText.contains("\n")){
			String[] loreitems = loreText.split("\n");
			for (String loretxt: loreitems){
				lore.add(loretxt);
			}
		}
		else
			lore.add(loreText);

		im.setLore(lore);
		selector.setItemMeta(im);

		if (doFakeEnchant){
			selector = ProtocolLib.setGlowing(selector);
		}
		
		if (slot != 1000)
			p.getInventory().setItem(slot, selector);
		else
			p.getInventory().addItem(selector);
	}


	@SuppressWarnings("deprecation")
	public static ItemStack getItemInfo(String item, Player p){
		int Red = 0;
		int Green = 0;
		int Blue = 0;
		if (item.contains(">")){
			String[] splitString = item.split(">");
			item = splitString[0];
			if (splitString[1].equalsIgnoreCase("white")){
				Red = 255;
				Green = 255;
				Blue = 255;
			}
			else if (splitString[1].equalsIgnoreCase("red")){
				Red = 255;
			}
			else if (splitString[1].equalsIgnoreCase("blue")){
				Blue = 255;
			}
			else if (splitString[1].equalsIgnoreCase("green")){
				Green = 255;
			}
			else if (splitString[1].equalsIgnoreCase("yellow")){
				Red = 255;
				Green = 255;
			}
			else if (splitString[1].equalsIgnoreCase("black")){

			}
			else{
				String[] colorPicks = splitString[1].split(",");
				Red = Integer.parseInt(colorPicks[0]);
				Green = Integer.parseInt(colorPicks[1]);
				Blue = Integer.parseInt(colorPicks[2]);
			}
		}



		String[] oneitem = item.split(",");
		ItemStack i = null;
		Integer id = null;
		Integer amount = null;
		Short durability = null;
		String enchantment = null;
		String enchantment1 = null;
		String enchantment2 = null;
		String ench_numb = null;
		String ench_numb1 = null;
		String ench_numb2 = null;
		String itemstring = null;

		if (oneitem[0].contains(":")) {
			String[] ITEM_ID = oneitem[0].split(":");
			id = Integer.valueOf(Integer.parseInt(ITEM_ID[0]));
			durability = Short.valueOf(Short.parseShort(ITEM_ID[1]));
			amount = 1;
			if (oneitem.length>=2)
				amount = Integer.valueOf(Integer.parseInt(oneitem[1]));
			i = new ItemStack(id.intValue(), amount.intValue(),
					durability.shortValue());

		} else {
			if (oneitem.length == 1){
				id = Integer.valueOf(Integer.parseInt(oneitem[0]));
				amount = 1;
			}
			else{
				id = Integer.valueOf(Integer.parseInt(oneitem[0]));
				amount = Integer.valueOf(Integer.parseInt(oneitem[1]));
			}
			i = new ItemStack(id.intValue(), amount.intValue());
		}


		itemstring = i.getType().toString();
		if (oneitem.length >= 8) {
			enchantment = Enchantment
					.getById(Integer.parseInt(oneitem[2])).getName()
					.toLowerCase();
			ench_numb = oneitem[3];

			enchantment1 = Enchantment
					.getById(Integer.parseInt(oneitem[4])).getName()
					.toLowerCase();
			ench_numb1 = oneitem[5];

			enchantment2 = Enchantment
					.getById(Integer.parseInt(oneitem[6])).getName()
					.toLowerCase();
			ench_numb2 = oneitem[7];

			i.addUnsafeEnchantment(Enchantment.getById(Integer.parseInt(oneitem[2])), Integer.parseInt(oneitem[3]));
			i.addUnsafeEnchantment(Enchantment.getById(Integer.parseInt(oneitem[4])), Integer.parseInt(oneitem[5]));
			i.addUnsafeEnchantment(Enchantment.getById(Integer.parseInt(oneitem[6])), Integer.parseInt(oneitem[7]));
			if (p == null)
				itemstring = itemstring + " with " + enchantment + " "
						+ ench_numb + ",\n " + enchantment1 + " " + ench_numb1 + " and " + enchantment2 + " " + ench_numb2;
		}
		else if (oneitem.length >= 6) {
			enchantment = Enchantment
					.getById(Integer.parseInt(oneitem[2])).getName()
					.toLowerCase();
			ench_numb = oneitem[3];

			enchantment1 = Enchantment
					.getById(Integer.parseInt(oneitem[4])).getName()
					.toLowerCase();
			ench_numb1 = oneitem[5];

			i.addUnsafeEnchantment(Enchantment.getById(Integer.parseInt(oneitem[2])), Integer.parseInt(oneitem[3]));
			i.addUnsafeEnchantment(Enchantment.getById(Integer.parseInt(oneitem[4])), Integer.parseInt(oneitem[5]));
			if (p == null)
				itemstring = itemstring + " with " + enchantment + " "
						+ ench_numb + " and " + enchantment1 + " " + ench_numb1;
		}

		else if (oneitem.length >= 4) {
			enchantment = Enchantment
					.getById(Integer.parseInt(oneitem[2])).getName()
					.toLowerCase();
			ench_numb = oneitem[3];

			i.addUnsafeEnchantment(Enchantment.getById(Integer.parseInt(oneitem[2])), Integer.parseInt(oneitem[3]));
			if (p == null)
				itemstring = itemstring + " with " + enchantment + " "
						+ ench_numb;
		}

		if (itemstring != null)
			itemstring = Message.CleanCapitalize(itemstring);


		if (!itemstring.equalsIgnoreCase(i.getType().name())){
			ItemMeta im = i.getItemMeta();
			im.setDisplayName(itemstring);
			i.setItemMeta(im);
		}


		if ((id < 298) || (317 < id)) {
			if (p != null) {
				p.getInventory().addItem(i);
			}
		} else if ((id == 298) || (id == 302) || (id == 306) || (id == 310) || (id == 314)) {
			if (id == 298) {
				LeatherArmorMeta c = (LeatherArmorMeta)i.getItemMeta();
				c.setColor(Color.fromBGR(Blue, Green, Red));
				i.setItemMeta(c);
			}
			i.setAmount(1);
			if (p != null) {
				p.getInventory().setHelmet(i);
			}
		} else if ((id == 299) || (id == 303) || (id == 307) || (id == 311) || (id == 315)) {
			if (id == 299) {
				LeatherArmorMeta c = (LeatherArmorMeta)i.getItemMeta();
				c.setColor(Color.fromBGR(Blue, Green, Red));
				i.setItemMeta(c);
			}
			i.setAmount(1);
			if (p != null) {
				p.getInventory().setChestplate(i);
			}
		} else if ((id == 300) || (id == 304) || (id == 308) || (id == 312) || (id == 316)) {
			if (id == 300) {
				LeatherArmorMeta l = (LeatherArmorMeta)i.getItemMeta();
				l.setColor(Color.fromBGR(Blue, Green, Red));
				i.setItemMeta(l);
			}

			i.setAmount(1);
			if (p != null) {
				p.getInventory().setLeggings(i);
			}
		} else if ((id == 301) || (id == 305) || (id == 309) || (id == 313) || (id == 317)) {
			if (id == 301) {
				LeatherArmorMeta b = (LeatherArmorMeta)i.getItemMeta();
				b.setColor(Color.fromBGR(Blue, Green, Red));
				i.setItemMeta(b);
			}
			i.setAmount(1);
			if (p != null) {
				p.getInventory().setBoots(i);
			}
		}



		return i;
	}


	public static boolean compareItems(ItemStack itemInHand, Material item, String name) {
		if (itemInHand.hasItemMeta()){
			if (itemInHand.getItemMeta().hasDisplayName()){
				if (ChatColor.stripColor(itemInHand.getItemMeta().getDisplayName()).equalsIgnoreCase(ChatColor.stripColor(Message.Colorize(name))))
					return true;
			}
		}
		return false;
	}


}
