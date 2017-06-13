package net.athenamc.spigot.core.market;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import com.google.common.collect.Lists;

import net.athenamc.spigot.core.SpigotCore;
import net.md_5.bungee.api.ChatColor;

public class Skyblock extends Market {
	private Inventory inv;

	public Skyblock(SpigotCore plugin) {
		super(plugin);
		inv = Bukkit.createInventory(null, 54, ChatColor.GREEN + "" + ChatColor.BOLD + "Skyblock");
		inv.setItem(53, createItem(Material.IRON_FENCE, ChatColor.RED + ChatColor.BOLD.toString() + "Back to menu",
				Lists.newArrayList(ChatColor.RED + "Click this to go back to the navigation menu"), (short) 0));
	}

	@Override
	public void openInventory(Player player) {
		player.openInventory(inv);
	}
}
