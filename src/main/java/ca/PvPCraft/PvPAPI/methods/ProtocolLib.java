package ca.PvPCraft.PvPAPI.methods;

import java.util.logging.Logger;

import ca.PvPCraft.PvPAPI.Main;

import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import com.comphenix.protocol.Packets;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ConnectionSide;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.utility.MinecraftReflection;
import com.comphenix.protocol.wrappers.nbt.NbtCompound;
import com.comphenix.protocol.wrappers.nbt.NbtFactory;


@SuppressWarnings("deprecation")
public class ProtocolLib implements Listener{
	public static Main plugin;
	static Logger log = Bukkit.getLogger();

	public ProtocolLib(Main mainclass) {
		plugin = mainclass;
		mainclass.getServer().getPluginManager().registerEvents(this, mainclass);
		setupProtocolLib();
	}

	private void setupProtocolLib() {
		EnableProtocolLibFunctions();
	}
	
	// We enable ProtocolLib's API for use with fake enchanted items
    private void EnableProtocolLibFunctions() {
		ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(
				plugin, ConnectionSide.SERVER_SIDE, ListenerPriority.HIGH, 
				Packets.Server.SET_SLOT, Packets.Server.WINDOW_ITEMS) {
			@Override
			public void onPacketSending(PacketEvent event) {
				if (event.getPacketID() == Packets.Server.SET_SLOT) {
					addGlow(new ItemStack[] { event.getPacket().getItemModifier().read(0) });
				} else {
					addGlow(event.getPacket().getItemArrayModifier().read(0));
				}
			}
		});
	}
    
    
	private void addGlow(ItemStack[] stacks) {
		for (ItemStack stack : stacks) {
			if (stack != null) {
				// Only update those stacks that have our flag enchantment
				if (stack.getEnchantmentLevel(Enchantment.SILK_TOUCH) == 32) {
					NbtCompound compound = (NbtCompound) NbtFactory.fromItemTag(stack);
					compound.put(NbtFactory.ofList("ench"));
				}
			}
		}
	}

	public static ItemStack setGlowing(ItemStack is) {
		is = MinecraftReflection.getBukkitItemStack(is);
		is.addUnsafeEnchantment(Enchantment.SILK_TOUCH, 32);
		return is;
	}

	public static boolean isGlowing(ItemStack is) {
		return is.getEnchantmentLevel(Enchantment.SILK_TOUCH) == 32;
	}


}
