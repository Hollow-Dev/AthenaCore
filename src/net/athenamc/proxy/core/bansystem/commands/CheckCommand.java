package net.athenamc.proxy.core.bansystem.commands;

import java.util.UUID;

import net.athenamc.proxy.core.BungeeCore;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class CheckCommand extends Command {
	private BungeeCore plugin;

	public CheckCommand(BungeeCore plugin) {
		super("check", "core.punishment.check");
		this.plugin = plugin;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if (args.length > 0) {
			ProxiedPlayer player = plugin.getProxy().getPlayer(args[0]);
			UUID check = null;
			if (player != null) {
				check = player.getUniqueId();
			} else {
				check = plugin.getUUID(args[0]);
			}
			if (check == null) {
				sender.sendMessage(new ComponentBuilder("Error: That player has never joined the server").create());
				return;
			}

			if (plugin.getPunishmentApi().isBanned(check)) {
				sender.sendMessage(new ComponentBuilder("Banned: ").color(ChatColor.GREEN).append("Yes")
						.color(ChatColor.RED).create());
				sender.sendMessage(
						new ComponentBuilder("Banner: ")
								.color(ChatColor.GREEN).append(plugin.getProxy()
										.getPlayer(plugin.getPunishmentApi().getBanner(check)).getName())
								.color(ChatColor.RED).create());
				sender.sendMessage(
						new ComponentBuilder("Time remaining: ").color(ChatColor.GREEN)
								.append(plugin
										.getTimeString(plugin.getPunishmentApi().getBanEnd(check)))
								.color(ChatColor.RED).create());
				sender.sendMessage(new ComponentBuilder("Reason: ").color(ChatColor.GREEN)
						.append(plugin.getPunishmentApi().getBanReason(check)).color(ChatColor.RED).create());
			} else {
				sender.sendMessage(new ComponentBuilder("Banned: ").color(ChatColor.GREEN).append("No")
						.color(ChatColor.RED).create());
			}

			if (plugin.getPunishmentApi().isMuted(check)) {
				sender.sendMessage(new ComponentBuilder("Muted: ").color(ChatColor.GREEN).append("Yes")
						.color(ChatColor.RED).create());
				sender.sendMessage(
						new ComponentBuilder("Muter: ")
								.color(ChatColor.GREEN).append(plugin.getProxy()
										.getPlayer(plugin.getPunishmentApi().getMuter(check)).getName())
								.color(ChatColor.RED).create());
				sender.sendMessage(
						new ComponentBuilder("Time remaining: ").color(ChatColor.GREEN)
								.append(plugin
										.getTimeString(plugin.getPunishmentApi().getMuteEnd(check)))
								.color(ChatColor.RED).create());
			} else {
				sender.sendMessage(new ComponentBuilder("Muted: ").color(ChatColor.GREEN).append("No")
						.color(ChatColor.RED).create());
			}
			sender.sendMessage(new ComponentBuilder("Kicks: ").color(ChatColor.GREEN)
					.append(plugin.getPunishmentApi().getKicks(check) + "").color(ChatColor.RED).create());
		} else {
			sender.sendMessage(new ComponentBuilder("Error, not enough arguments, please use syntax: /check [player]")
					.color(ChatColor.RED).create());
		}
	}
}
