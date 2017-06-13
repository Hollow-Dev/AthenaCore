
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

public class UnmuteCommand extends Command {
	private BungeeCore plugin;

	public UnmuteCommand(BungeeCore plugin) {
		super("unmute", "core.bansystem.unmute");
		this.plugin = plugin;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if (args.length > 0) {
			ProxiedPlayer player = ProxyServer.getInstance().getPlayer(args[0]);
			UUID muted = null;
			if (player.isConnected()) {
				muted = player.getUniqueId();
			} else {
				muted = plugin.getUUID(args[0]);
			}
			if (muted == null) {
				sender.sendMessage(new ComponentBuilder("Error: That player has never joined the server").create());
				return;
			}
			if (plugin.getPunishmentApi().isMuted(muted)) {
				BaseComponent[] staffMsg = new ComponentBuilder("[").color(ChatColor.GRAY).append("STAFF")
						.color(ChatColor.GREEN).bold(true).append("] ").bold(false).color(ChatColor.GRAY)
						.append(player.getName()).color(ChatColor.RED).append(" has been unmuted by ")
						.color(ChatColor.GRAY).append(sender.getName()).color(ChatColor.DARK_PURPLE).create();

				for (ProxiedPlayer p : ProxyServer.getInstance().getPlayers())
					if (p.hasPermission("core.punishment.unmute.alert"))
						p.sendMessage(staffMsg);
				ProxyServer.getInstance().getConsole().sendMessage(staffMsg);

				plugin.getPunishmentApi().unmute(muted);
				return;
			} else {
				TextComponent comp = new TextComponent(args[0] + " is not muted. Therefore cannot be unmuted");
				comp.setColor(ChatColor.RED);
				sender.sendMessage(comp);
			}
		} else
			sender.sendMessage(new ComponentBuilder("Error, not enough arguments, please use syntax: /unban [player]")
					.color(ChatColor.RED).create());
	}
}
