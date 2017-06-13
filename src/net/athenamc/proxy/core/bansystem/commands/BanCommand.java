
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

public class BanCommand extends Command {
	private BungeeCore plugin;

	public BanCommand(BungeeCore plugin) {
		super("ban", "core.punishment.ban");
		this.plugin = plugin;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if (args.length > 1) {
			ProxiedPlayer player = ProxyServer.getInstance().getPlayer(args[0]);
			if (player == null) {
				if (!sender.hasPermission("core.punishment.ban.offline")) {
					sender.sendMessage(new ComponentBuilder("You do not have permission to ban offline players")
							.color(ChatColor.RED).create());
					return;
				}
			} else {
				if (!sender.hasPermission("core.punishment.ban.online")) {
					sender.sendMessage(new ComponentBuilder("You do not have permission to ban online players")
							.color(ChatColor.RED).create());
					return;
				}
			}
			String msg = "";
			for (int i = 1; i < args.length; i++)
				msg += args[i] + " ";
			msg = msg.trim();

			BaseComponent[] staffMsg = new ComponentBuilder("[").color(ChatColor.GRAY).append("STAFF")
					.color(ChatColor.GREEN).bold(true).append("] ").bold(false).color(ChatColor.GRAY)
					.append(args[0]).color(ChatColor.RED)
					.append(" has been banned permanently from the network by ").color(ChatColor.GRAY)
					.append(sender.getName()).color(ChatColor.DARK_PURPLE).color(ChatColor.RED).append(" with reason: ")
					.color(ChatColor.GRAY).append(msg).color(ChatColor.RED).create();
			UUID uuid;
			if (sender instanceof ProxiedPlayer)
				uuid = ((ProxiedPlayer) sender).getUniqueId();
			else
				uuid = UUID.fromString("f78a4d8d-d51b-4b39-98a3-230f2de0c670");
			UUID bannee = null;
			if (player != null) {
				bannee = player.getUniqueId();
				TextComponent comp = new TextComponent("You have been permanently banned from the network\nReason: " + msg);
				player.disconnect(comp);
			} else {
				bannee = plugin.getUUID(args[0]);
			}
			if (bannee == null) {
				sender.sendMessage(new ComponentBuilder("Error: That player has never joined the server").create());
				return;
			}
			
			plugin.getPunishmentApi().ban(bannee, msg, -1L, uuid);

			for (ProxiedPlayer p : ProxyServer.getInstance().getPlayers())
				if (p.hasPermission("core.punishment.ban.alert"))
					p.sendMessage(staffMsg);
			ProxyServer.getInstance().getConsole().sendMessage(staffMsg);
			return;
		} else
			sender.sendMessage(
					new ComponentBuilder("Error, not enough arguments, please use syntax: /ban [player] [reason]")
							.color(ChatColor.RED).create());
	}
}
