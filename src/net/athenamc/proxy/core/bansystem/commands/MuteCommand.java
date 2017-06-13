
package net.athenamc.proxy.core.bansystem.commands;

import java.util.UUID;

import net.athenamc.proxy.core.BungeeCore;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class MuteCommand extends Command {
	private BungeeCore plugin;

	public MuteCommand(BungeeCore plugin) {
		super("mute", "core.punishment.mute");
		this.plugin = plugin;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if (args.length > 1) {
			ProxiedPlayer player = ProxyServer.getInstance().getPlayer(args[0]);
			if (player == null) {
				if (!sender.hasPermission("core.punishment.mute.offline")) {
					sender.sendMessage(new ComponentBuilder("You do not have permission to mute offline players")
							.color(ChatColor.RED).create());
					return;
				}
			} else {
				if (!sender.hasPermission("core.punishment.mute.online")) {
					sender.sendMessage(new ComponentBuilder("You do not have permission to mute online players")
							.color(ChatColor.RED).create());
					return;
				}
			}
			long time = plugin.getTimeFromString(args[1]);
			if (time >= 1) {
				time += System.currentTimeMillis();
				String timeString = plugin.getTimeString(time);

				BaseComponent[] staffMsg = new ComponentBuilder("[").color(ChatColor.GRAY).append("STAFF")
						.color(ChatColor.GREEN).bold(true).append("] ").bold(false).color(ChatColor.GRAY)
						.append(player.getName()).color(ChatColor.RED).append(" has been muted by ")
						.color(ChatColor.GRAY).append(sender.getName()).color(ChatColor.DARK_PURPLE).append(" for: ")
						.color(ChatColor.GRAY).append(timeString).color(ChatColor.RED).create();

				for (ProxiedPlayer p : ProxyServer.getInstance().getPlayers())
					if (p.hasPermission("core.punishment.mute.alert"))
						p.sendMessage(staffMsg);
				ProxyServer.getInstance().getConsole().sendMessage(staffMsg);

				UUID uuid;
				if (sender instanceof ProxiedPlayer)
					uuid = ((ProxiedPlayer) sender).getUniqueId();
				else
					uuid = UUID.fromString("f78a4d8d-d51b-4b39-98a3-230f2de0c670");
				UUID mutee = null;
				if (player != null) {
					mutee = player.getUniqueId();
				} else {
					mutee = plugin.getUUID(args[0]);
				}
				if (mutee == null) {
					sender.sendMessage(new ComponentBuilder("Error: That player has never joined the server").create());
					return;
				}

				plugin.getPunishmentApi().mute(mutee, time, uuid);
			} else {
				sender.sendMessage(new ComponentBuilder("You entered an invalid time").color(ChatColor.RED).create());
			}
			return;
		} else
			sender.sendMessage(new ComponentBuilder(
					"Error, not enough arguments, please use syntax: /temp [player] [time] [reason]")
							.color(ChatColor.RED).create());
	}
}
