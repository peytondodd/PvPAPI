package ca.PvPCraft.PvPAPI.enums;

import ca.PvPCraft.PvPAPI.utilities.IconMenu;

import org.bukkit.inventory.InventoryView;

	public class IconInfo{
		String iconMenuTitle = null;
		IconMenu iconMenu = null;
		InventoryView invView = null;
		
		public IconInfo(IconMenu menu, String title, InventoryView view){
			iconMenuTitle = title;
			iconMenu = menu;
			invView = view;
		}
		
		public String getTitle(){return iconMenuTitle;}
		public IconMenu getMenu(){return iconMenu;}
		public InventoryView getView(){return invView;}
		
	}
	