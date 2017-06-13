
package net.athenamc.proxy.core.bansystem.commands;

import net.athenamc.proxy.core.BungeeCore;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class ReportCommand extends Command {
	private BungeeCore plugin;

	public ReportCommand(BungeeCore plugin) {
		super("report", "core.punishment.report");
		this.plugin = plugin;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if (args.length > 1) {
			ProxiedPlayer player = ProxyServer.getInstance().getPlayer(args[0]);
			if (player != null) {
				String msg = "";
				for (int i = 1; i < args.length; i++)
					msg += args[i] + " ";
				msg = msg.trim();

				BaseComponent[] staffMsg = new ComponentBuilder("[").color(ChatColor.GRAY).append("STAFF")
						.color(ChatColor.GREEN).bold(true).append("] ").bold(false).color(ChatColor.GRAY).append(player.getName())
						.color(ChatColor.RED).append(" has been reported by ").color(ChatColor.GRAY)
						.append(sender.getName()).color(ChatColor.DARK_PURPLE)
						.append(" on server ").color(ChatColor.GRAY)
						.append(((ProxiedPlayer) sender).getServer().getInfo().getName()).color(ChatColor.GREEN).append(" with reason: ").color(ChatColor.GRAY).append(msg).color(ChatColor.RED).create();

				for (ProxiedPlayer p : ProxyServer.getInstance().getPlayers())
					if (p.hasPermission("core.punishment.report.alert"))
						p.sendMessage(staffMsg);
				ProxyServer.getInstance().getConsole().sendMessage(staffMsg);
				sender.sendMessage(
						new ComponentBuilder("You have reported ").color(ChatColor.GRAY).append(player.getName())
								.color(ChatColor.RED).append(" to all online staff.").color(ChatColor.GRAY).create());
				return;
			} else {
				TextComponent comp = new TextComponent("There is no player online called " + args[0]);
				comp.setColor(ChatColor.RED);
				sender.sendMessage(comp);
			}
		} else
			sender.sendMessage(
					new ComponentBuilder("Error, not enough arguments, please use syntax: /report [player] [reason]")
							.color(ChatColor.RED).create());
	}
}
