package net.athenamc.spigot.core.market;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import com.google.common.collect.Lists;

import net.athenamc.spigot.core.SpigotCore;

public class Factions extends Market {
	private Inventory inv;

	public Factions(SpigotCore plugin) {
		super(plugin);
		inv = Bukkit.createInventory(null, 54, ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "Factions");
		inv.setItem(53, createItem(Material.IRON_FENCE, ChatColor.RED + ChatColor.BOLD.toString() + "Back to menu",
				Lists.newArrayList(ChatColor.RED + "Click this to go back to the navigation menu"), (short) 0));
	}

	@Override
	public void openInventory(Player player) {
		player.openInventory(inv);
	}
}
