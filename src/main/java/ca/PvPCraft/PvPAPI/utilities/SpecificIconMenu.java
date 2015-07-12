package ca.PvPCraft.PvPAPI.utilities;

import java.sql.SQLException;
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
 
public class SpecificIconMenu implements Listener {
 
    private String name;
    private String owner;
    private int size;
    private OptionClickEventHandler handler;
    private Plugin plugin;
   
    private String[] optionNames;
    private ItemStack[] optionIcons;
    
    public SpecificIconMenu(String name, String owner, int size, OptionClickEventHandler handler, Plugin plugin) {
        this.name = name;
        this.size = size;
        this.owner = owner;
        this.handler = handler;
        this.plugin = plugin;
        this.optionNames = new String[size];
        this.optionIcons = new ItemStack[size];
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
   
    public SpecificIconMenu setOption(int position, ItemStack icon, String name, String... info) {
        optionNames[position] = name;
        optionIcons[position] = setItemNameAndLore(icon, name, info);
        return this;
    }
   
    public void open(Player player) {
        Inventory inventory = Bukkit.createInventory(player, size, name);
        for (int i = 0; i < optionIcons.length; i++) {
            if (optionIcons[i] != null) {
                inventory.setItem(i, optionIcons[i]);
            }
        }
        player.openInventory(inventory);
    }
   
    public void destroy() {
        HandlerList.unregisterAll(this);
        handler = null;
        plugin = null;
        optionNames = null;
        optionIcons = null;
    }
   
    @EventHandler(priority=EventPriority.MONITOR)
    void onInventoryClick(InventoryClickEvent event) throws SQLException {
        if (event.getInventory().getTitle().equals(name) && event.getWhoClicked().getName().equals(owner)) {
            event.setCancelled(true);
            int slot = event.getRawSlot();
            if (slot >= 0 && slot < size && optionNames[slot] != null) {
                Plugin plugin = this.plugin;
                final OptionClickEvent e = new OptionClickEvent((Player)event.getWhoClicked(), slot, optionNames[slot], event.getClick());
                handler.onOptionClick(e);
                if (e.willClose()) {
                    final Player p = (Player)event.getWhoClicked();
                    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                        public void run() {
                            if (e.willDestroy()) {
                                destroy();
                            }
                            p.closeInventory();
                        }
                    }, 1);
                }
            }
        }
    }
    
    public interface OptionClickEventHandler {
        public void onOptionClick(OptionClickEvent event) throws SQLException;       
    }
    
    public class OptionClickEvent {
        private Player player;
        private int position;
        private String name;
        private boolean close;
        private boolean destroy;
		private ClickType clickType;
       
        public OptionClickEvent(Player player, int position, String name, ClickType ClickType) {
            this.player = player;
            this.position = position;
            this.name = name;
            this.close = true;
            this.destroy = false;
            this.clickType = ClickType;
        }
       
        public Player getPlayer() {
            return player;
        }
       
        public int getPosition() {
            return position;
        }
       
        public String getName() {
            return name;
        }
        public ClickType getClick() {
            return clickType;
        }
        public boolean willClose() {
            return close;
        }
       
        public boolean willDestroy() {
            return destroy;
        }
       
        public void setWillClose(boolean close) {
            this.close = close;
        }
       
        public void setWillDestroy(boolean destroy) {
            this.destroy = destroy;
        }
    }
   
    private ItemStack setItemNameAndLore(ItemStack item, String name, String[] lore) {
    	ItemMeta im = item.getItemMeta();
    		im.setDisplayName(name);
    		im.setLore(Arrays.asList(lore));
    	item.setItemMeta(im);
        return item;
    }
   
}