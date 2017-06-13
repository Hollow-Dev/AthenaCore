
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

public class KickCommand extends Command {
	private BungeeCore plugin;

	public KickCommand(BungeeCore plugin) {
		super("kick", "core.punishment.kick");
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
						.color(ChatColor.RED).append(" has been kicked from the network by ").color(ChatColor.GRAY)
						.append(sender.getName()).color(ChatColor.DARK_PURPLE).append(" with reason: ")
						.color(ChatColor.GRAY).append(msg).color(ChatColor.RED).create();
				TextComponent comp = new TextComponent("You have been kicked from the network\nReason: " + msg);
				player.disconnect(comp);
				
				for (ProxiedPlayer p : ProxyServer.getInstance().getPlayers())
					if (p.hasPermission("core.punishment.kick.alert"))
						p.sendMessage(staffMsg);
				ProxyServer.getInstance().getConsole().sendMessage(staffMsg);
				
				plugin.getPunishmentApi().setKicks(player.getUniqueId(),
						plugin.getPunishmentApi().getKicks(player.getUniqueId()) + 1);
				return;
			} else {
				TextComponent comp = new TextComponent("There is no player online called " + args[0]);
				comp.setColor(ChatColor.RED);
				sender.sendMessage(comp);
			}
		} else
			sender.sendMessage(
					new ComponentBuilder("Error, not enough arguments, please use syntax: /kick [player] [reason]")
							.color(ChatColor.RED).create());
	}
}
