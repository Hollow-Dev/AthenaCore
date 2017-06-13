package net.athenamc.spigot.core.market;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import com.google.common.collect.Lists;

import net.athenamc.spigot.core.SpigotCore;
import net.md_5.bungee.api.ChatColor;

public class KitPvP extends Market {
	private Inventory inv;

	public KitPvP(SpigotCore plugin) {
		super(plugin);
		inv = Bukkit.createInventory(null, 54, ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "KitPvP");
		inv.setItem(53, createItem(Material.IRON_FENCE, ChatColor.RED + ChatColor.BOLD.toString() + "Back to menu",
				Lists.newArrayList(ChatColor.RED + "Click this to go back to the navigation menu"), (short) 0));

		inv.setItem(10,
				createItem(Material.DIAMOND_AXE, ChatColor.RED + ChatColor.BOLD.toString() + "CLERIC",
						Lists.newArrayList(ChatColor.RED + "Left click to purchase permanently",
								ChatColor.RED + "Right click to rent for a month", "",
								ChatColor.RED + "Purchase: 100 TICKETS", ChatColor.RED + "Rent: 1000 GEMS"),
						(short) 0));
		inv.setItem(12,
				createItem(Material.DIAMOND_AXE, ChatColor.RED + ChatColor.BOLD.toString() + "ARCHER",
						Lists.newArrayList(ChatColor.RED + "Left click to purchase permanently",
								ChatColor.RED + "Right click to rent for a month", "",
								ChatColor.RED + "Purchase: 150 TICKETS", ChatColor.RED + "Rent: 1500 GEMS"),
						(short) 0));
		inv.setItem(14,
				createItem(Material.DIAMOND_AXE, ChatColor.RED + ChatColor.BOLD.toString() + "WIZARD",
						Lists.newArrayList(ChatColor.RED + "Left click to purchase permanently",
								ChatColor.RED + "Right click to rent for a month", "",
								ChatColor.RED + "Purchase: 200 TICKETS", ChatColor.RED + "Rent: 2000 GEMS"),
						(short) 0));
		inv.setItem(16,
				createItem(Material.DIAMOND_AXE, ChatColor.RED + ChatColor.BOLD.toString() + "BOMBER",
						Lists.newArrayList(ChatColor.RED + "Left click to purchase permanently",
								ChatColor.RED + "Right click to rent for a month", "",
								ChatColor.RED + "Purchase: 250 TICKETS", ChatColor.RED + "Rent: 2500 GEMS"),
						(short) 0));
		inv.setItem(28,
				createItem(Material.DIAMOND_AXE, ChatColor.RED + ChatColor.BOLD.toString() + "RUNNER",
						Lists.newArrayList(ChatColor.RED + "Left click to purchase permanently",
								ChatColor.RED + "Right click to rent for a month", "",
								ChatColor.RED + "Purchase: 300 TICKETS", ChatColor.RED + "Rent: 3000 GEMS"),
						(short) 0));
		inv.setItem(30,
				createItem(Material.DIAMOND_AXE, ChatColor.RED + ChatColor.BOLD.toString() + "FREEZE",
						Lists.newArrayList(ChatColor.RED + "Left click to purchase permanently",
								ChatColor.RED + "Right click to rent for a month", "",
								ChatColor.RED + "Purchase: 350 TICKETS", ChatColor.RED + "Rent: 3500 GEMS"),
						(short) 0));
		inv.setItem(32,
				createItem(Material.DIAMOND_AXE, ChatColor.RED + ChatColor.BOLD.toString() + "KNIGHT",
						Lists.newArrayList(ChatColor.RED + "Left click to purchase permanently",
								ChatColor.RED + "Right click to rent for a month", "",
								ChatColor.RED + "Purchase: 400 TICKETS", ChatColor.RED + "Rent: 4000 GEMS"),
						(short) 0));
		inv.setItem(34,
				createItem(Material.DIAMOND_AXE, ChatColor.RED + ChatColor.BOLD.toString() + "NINJA",
						Lists.newArrayList(ChatColor.RED + "Left click to purchase permanently",
								ChatColor.RED + "Right click to rent for a month", "",
								ChatColor.RED + "Purchase: 450 TICKETS", ChatColor.RED + "Rent: 4500 GEMS"),
						(short) 0));
	}

	@Override
	public void openInventory(Player player) {
		player.openInventory(inv);
	}
}
