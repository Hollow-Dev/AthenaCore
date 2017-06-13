
package net.athenamc.proxy.core.bansystem.commands;

import java.util.UUID;

import net.athenamc.proxy.core.BungeeCore;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class UnbanCommand extends Command {
	private BungeeCore plugin;

	public UnbanCommand(BungeeCore plugin) {
		super("unban", "core.punishment.unban");
		this.plugin = plugin;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if (args.length > 0) {
			UUID bannee = plugin.getUUID(args[0]);
			if (bannee == null) {
				sender.sendMessage(new ComponentBuilder("Error: That player has never joined the server").create());
				return;
			}
			if (plugin.getPunishmentApi().isBanned(bannee)) {
				BaseComponent[] staffMsg = new ComponentBuilder("[").color(ChatColor.GRAY).append("STAFF")
						.color(ChatColor.GREEN).bold(true).append("] ").bold(false).color(ChatColor.GRAY)
						.append(args[0]).color(ChatColor.RED).append(" has been unbanned from the network by ")
						.color(ChatColor.GRAY).append(sender.getName()).color(ChatColor.DARK_PURPLE).create();

				plugin.getPunishmentApi().unban(bannee);
				for (ProxiedPlayer p : ProxyServer.getInstance().getPlayers())
					if (p.hasPermission("core.punishment.unban.alert"))
						p.sendMessage(staffMsg);
				ProxyServer.getInstance().getConsole().sendMessage(staffMsg);
				return;
			} else {
				TextComponent comp = new TextComponent(args[0] + " is not banned. Therefore cannot be unbanned");
				comp.setColor(ChatColor.RED);
				sender.sendMessage(comp);
			}
		} else
			sender.sendMessage(new ComponentBuilder("Error, not enough arguments, please use syntax: /unban [player]")
					.color(ChatColor.RED).create());
	}
}
