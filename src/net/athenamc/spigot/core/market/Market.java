package net.athenamc.spigot.core.market;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.athenamc.core.Core;
import net.athenamc.spigot.core.SpigotCore;

public abstract class Market {
	protected Core plugin;

	public Market(SpigotCore plugin) {
		this.plugin = plugin;
	}

	public abstract void openInventory(Player player);

	public ItemStack createItem(Material material, String name, ArrayList<String> lore, short durability) {
		ItemStack item = new ItemStack(material);
		ItemMeta im = item.getItemMeta();
		im.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
		ArrayList<String> translatedLore = new ArrayList<String>();
		if (lore != null)
			for (String s : lore)
				translatedLore.add(ChatColor.translateAlternateColorCodes('&', s));
		im.setLore(translatedLore);

		item.setItemMeta(im);
		return item;
	}
}
