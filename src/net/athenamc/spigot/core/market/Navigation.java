package net.athenamc.spigot.core.market;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.google.common.collect.Lists;

import net.athenamc.core.currencies.CurrencyAPI.CurrencyType;
import net.athenamc.spigot.core.SpigotCore;

public class Navigation extends Market {
	ItemStack war, factions, parkour, skyblock, lbw, prison, kitpvp, pets, hubGear, particle, gadgets;

	public Navigation(SpigotCore plugin) {
		super(plugin);
		war = createItem(Material.STONE_SWORD, "&c&lWar", null, (short) 0);
		factions = createItem(Material.DIAMOND_SWORD, "&3&lFactions", null, (short) 0);
		parkour = createItem(Material.GOLD_BOOTS, "&6&lParkour", null, (short) 0);
		skyblock = createItem(Material.WOOD_AXE, "&a&lSkyblock", null, (short) 0);
		lbw = createItem(Material.SPONGE, "&e&lLuckyBlockWars", null, (short) 0);
		prison = createItem(Material.IRON_FENCE, "&7&lPrison", null, (short) 0);
		kitpvp = createItem(Material.DEAD_BUSH, "&5&lKitPvP", null, (short) 0);
		pets = createItem(Material.BONE, "&9&lPets", null, (short) 0);
		hubGear = createItem(Material.DIAMOND_CHESTPLATE, "&b&lHub Gear", null, (short) 0);
		particle = createItem(Material.NOTE_BLOCK, "&d&lParticles", null, (short) 0);
		gadgets = createItem(Material.WATCH, "&4&lGadgets", null, (short) 0);
	}

	@Override
	public void openInventory(Player player) {
		Inventory inv = Bukkit.createInventory(null, 54, ChatColor.GREEN + "Market place");

		inv.setItem(12, war);
		inv.setItem(13, factions);
		inv.setItem(14, parkour);
		inv.setItem(21, skyblock);
		inv.setItem(22, lbw);
		inv.setItem(23, prison);
		inv.setItem(31, kitpvp);
		inv.setItem(47, pets);

		inv.setItem(45,
				createItem(Material.RABBIT_FOOT, "&a&lCurrencies", Lists.newArrayList(
						"&f&oGems: " + plugin.getCurrencyApi().getCurrency(player.getUniqueId(), CurrencyType.GEMS),
						"&f&oTickets: "
								+ plugin.getCurrencyApi().getCurrency(player.getUniqueId(), CurrencyType.TICKETS)),
						(short) 0));
		inv.setItem(49, hubGear);
		inv.setItem(51, particle);
		inv.setItem(53, gadgets);

		player.openInventory(inv);
	}
}
