package net.athenamc.spigot.core.currencies;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.athenamc.core.Core;
import net.athenamc.core.currencies.CurrencyAPI;
import net.athenamc.core.currencies.CurrencyAPI.CurrencyType;
import net.athenamc.core.exceptions.PermissionRequiredException;
import net.athenamc.spigot.core.SpigotCore;

public class CurrencyModule implements CommandExecutor {
	private SpigotCore plugin;

	public CurrencyModule(SpigotCore plugin) {
		this.plugin = plugin;

		plugin.getCommand("deltickets").setExecutor(this);
		plugin.getCommand("delgems").setExecutor(this);
		plugin.getCommand("givetickets").setExecutor(this);
		plugin.getCommand("givegems").setExecutor(this);
		plugin.getCommand("tickets").setExecutor(this);
		plugin.getCommand("gems").setExecutor(this);
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		try {
			if (cmd.getName().equalsIgnoreCase("gems")) {
				if (sender instanceof Player) {
					Player player = (Player) sender;
					player.sendMessage(ChatColor.GRAY + "You currently have " + ChatColor.GREEN
							+ plugin.getCurrencyApi().getCurrency(player.getUniqueId(), CurrencyType.GEMS)
							+ ChatColor.DARK_PURPLE + " GEMS!");
				} else {
					sender.sendMessage("This command is for players only");
				}
			} else if (cmd.getName().equalsIgnoreCase("tickets")) {
				if (sender instanceof Player) {
					Player player = (Player) sender;
					player.sendMessage(ChatColor.GRAY + "You currently have " + ChatColor.GREEN
							+ plugin.getCurrencyApi().getCurrency(player.getUniqueId(), CurrencyType.TICKETS)
							+ ChatColor.DARK_PURPLE + " CAPS!");
				} else {
					sender.sendMessage("This command is for players only");
				}
			} else if (args.length > 1) {
				int amount = Integer.parseInt(args[1]);
				OfflinePlayer player = Bukkit.getOfflinePlayer(args[0]);

				CurrencyAPI api = plugin.getCurrencyApi();
				CurrencyType type = CurrencyType
						.valueOf(cmd.getName().replace("give", "").replace("del", "").toUpperCase());

				int currentValue = api.getCurrency(player.getUniqueId(), type);

				if (cmd.getName().startsWith("give")) {
					plugin.checkPerm(sender, "core.currencies." + type.name().toLowerCase() + ".give");
					api.setCurrency(player.getUniqueId(), currentValue + amount, type);
					sender.sendMessage(ChatColor.GREEN + "Successfully gave " + ChatColor.DARK_PURPLE + player.getName()
							+ ChatColor.RED + " " + amount + ChatColor.GREEN + " " + type.name() + "!");
				} else if (cmd.getName().toLowerCase().startsWith("del")) {
					plugin.checkPerm(sender, "core.currencies." + type.name().toLowerCase() + ".delete");
					api.setCurrency(player.getUniqueId(), currentValue - amount, type);
					if (amount < currentValue)
						sender.sendMessage(ChatColor.RED + "Successfully removed " + amount + " " + type.name() + "!");
					else
						sender.sendMessage(
								ChatColor.RED + "Successfully removed " + currentValue + " " + type.name() + "!");
				}
			} else
				return false;
		} catch (PermissionRequiredException e) {
			sender.sendMessage(
					ChatColor.RED + "You must have the permission " + e.getPermission() + " to perform that command");
		} catch (NumberFormatException e) {
			sender.sendMessage(ChatColor.RED + "You entered an invalid number");
		}
		return true;
	}
}
