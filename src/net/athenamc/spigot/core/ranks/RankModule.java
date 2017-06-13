package net.athenamc.spigot.core.ranks;

import java.sql.ResultSet;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import net.athenamc.core.exceptions.RankAlreadyExistsException;
import net.athenamc.core.exceptions.RankDoesNotExistException;
import net.athenamc.core.ranks.Rank;
import net.athenamc.spigot.core.SpigotCore;

public class RankModule implements Listener, CommandExecutor {
	private SpigotCore plugin;

	public RankModule(SpigotCore plugin) {
		this.plugin = plugin;
		plugin.getCommand("setrank").setExecutor(this);
		plugin.getCommand("delrank").setExecutor(this);
		plugin.getCommand("editrank").setExecutor(this);
		plugin.getCommand("checkrank").setExecutor(this);
		plugin.getCommand("addrank").setExecutor(this);
		plugin.getCommand("listranks").setExecutor(this);

		plugin.getServer().getPluginManager().registerEvents(this, plugin);

		for (Player player : Bukkit.getOnlinePlayers())
			plugin.getRankApi().loadRank(player.getUniqueId());
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		try {
			if (cmd.getName().equalsIgnoreCase("delrank")) {
				if (sender.hasPermission("core.ranks.delete")) {
					if (args.length > 0) {
						plugin.getRankApi().deleteRank(args[0]);
						sender.sendMessage(ChatColor.GREEN + "Deleted the rank " + args[0]);
						return true;
					}
					return false;
				}
			} else if (cmd.getName().equalsIgnoreCase("addrank")) {
				if (sender.hasPermission("core.ranks.add")) {
					if (args.length > 0) {
						plugin.getRankApi().createRank(args[0]);
						sender.sendMessage(ChatColor.GREEN + "Created a rank called " + args[0]);
						return true;
					}
					return false;
				}
			} else if (cmd.getName().equalsIgnoreCase("setrank")) {
				if (sender.hasPermission("core.ranks.set")) {
					if (args.length > 1) {
						OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[0]);
						plugin.getRankApi().setRank(offlinePlayer.getUniqueId(), args[1]);
						sender.sendMessage(ChatColor.GREEN + "Successfully set " + offlinePlayer.getName()
								+ "'s rank to " + args[1]);
						if (offlinePlayer.isOnline()) {
							Player player = (Player) offlinePlayer;
							((SpigotRank) plugin.getRankApi().getRank(player.getUniqueId()))
									.addPlayerToTeam(player.getUniqueId());
							player.sendMessage(ChatColor.GREEN + "Your rank has been set to " + args[1]);
						}
						return true;
					}
					return false;
				}
			} else if (cmd.getName().equalsIgnoreCase("checkrank")) {
				OfflinePlayer lookup = null;
				if (args.length > 0) {
					if (sender.hasPermission("core.ranks.check.others"))
						lookup = Bukkit.getOfflinePlayer(args[0]);
				} else if (sender instanceof Player) {
					if (sender.hasPermission("core.ranks.check.self"))
						lookup = (Player) sender;
				} else
					return false;
				if (lookup != null) {
					Rank rank = plugin.getRankApi().getRank(lookup.getUniqueId());
					sender.sendMessage(ChatColor.GREEN + lookup.getName() + "'s Rank: " + rank.getName());
					return true;
				}
			} else if (cmd.getName().equalsIgnoreCase("editrank")) {
				if (sender.hasPermission("core.ranks.edit"))
					if (args.length > 2) {
						Rank rank = plugin.getRankApi().getRank(args[0]);
						if (args[1].equalsIgnoreCase("chatformat")) {
							String color = ChatColor
									.getLastColors(ChatColor.translateAlternateColorCodes('&', args[2]));
							if (color.equalsIgnoreCase(""))
								sender.sendMessage(ChatColor.RED
										+ "Please enter a valid format, for example &e&l will make messages display like "
										+ ChatColor.YELLOW + ChatColor.BOLD + "this");
							else {
								rank.setChatFormat(color);
								sender.sendMessage(ChatColor.GREEN
										+ "Rank's chat color changed, their chat messages will now be formatted like "
										+ rank.getChatFormat() + "this");
								plugin.getSqlManager().execute("UPDATE ranks SET chatformat='" + args[2]
										+ "' WHERE name='" + rank.getName() + "'");
							}
						} else if (args[1].equalsIgnoreCase("name")) {
							sender.sendMessage(
									ChatColor.GREEN + "Rank " + rank.getName() + "'s name was changed to " + args[2]);
							plugin.getSqlManager().execute(
									"UPDATE ranks SET name='" + args[2] + "' WHERE name='" + rank.getName() + "'");
							plugin.getSqlManager().execute("UPDATE rank_permissions SET name='" + args[2]
									+ "' WHERE name='" + rank.getName() + "'");
							rank.setName(args[2]);
						} else if (args[1].equalsIgnoreCase("prefix")) {
							rank.setPrefix(ChatColor.translateAlternateColorCodes('&', args[2]));
							sender.sendMessage(ChatColor.GREEN + "Rank " + rank.getName() + "'s prefix was changed to "
									+ rank.getPrefix());
							plugin.getSqlManager().execute(
									"UPDATE ranks SET prefix='" + args[2] + "' WHERE name='" + rank.getName() + "'");
						} else if (args[1].equalsIgnoreCase("weight")) {
							rank.setPower(Integer.parseInt(args[2]));
							sender.sendMessage(ChatColor.GREEN + "Rank " + rank.getName() + "'s weight was set to "
									+ rank.getPower());
							plugin.getSqlManager().execute(
									"UPDATE ranks SET weight='" + args[2] + "' WHERE name='" + rank.getName() + "'");
						} else
							return false;
						return true;
					} else if (args.length > 1 && args[1].equalsIgnoreCase("prefix")) {
						Rank rank = plugin.getRankApi().getRank(args[0]);
						rank.setPrefix("");
						sender.sendMessage(ChatColor.GREEN + rank.getName() + "'s prefix was removed");
						plugin.getSqlManager().execute(
								"UPDATE ranks SET prefix='" + args[2] + "' WHERE name='" + rank.getName() + "'");
						return true;
					} else
						return false;
			} else if (cmd.getName().equalsIgnoreCase("listranks")) {
				if (sender.hasPermission("core.ranks.list"))
					try {
						ResultSet rs = plugin.getSqlManager()
								.executeQuery("SELECT name,prefix,weight FROM ranks ORDER BY weight ASC");
						while (rs.next()) {
							String name = ChatColor.translateAlternateColorCodes('&', rs.getString("prefix"));
							if (name == null || ChatColor.stripColor(name).equalsIgnoreCase(""))
								name = rs.getString("name");

							sender.sendMessage(ChatColor.GRAY + name + ChatColor.GRAY + ": " + ChatColor.GREEN
									+ rs.getInt("weight"));
						}
						return true;
					} catch (Exception e) {
						e.printStackTrace();
					}
			}
			sender.sendMessage(ChatColor.RED + "You do not have permission for that command");
			return true;
		} catch (RankDoesNotExistException e) {
			sender.sendMessage(ChatColor.RED + "There is no rank called " + e.getRankName());
		} catch (RankAlreadyExistsException e) {
			sender.sendMessage(ChatColor.RED + "There is already a rank called " + e.getRank().getName());
		} catch (NumberFormatException e) {
			sender.sendMessage(ChatColor.RED + args[2] + " is not a valid number");
		}
		return true;
	}

	@EventHandler
	private void chat(AsyncPlayerChatEvent e) {
		Rank rank = plugin.getRankApi().getRank(e.getPlayer().getUniqueId());
		if (!ChatColor.stripColor(rank.getPrefix()).equalsIgnoreCase(""))
			e.setFormat(" " + ChatColor.DARK_GRAY + "[" + rank.getPrefix() + ChatColor.DARK_GRAY + "]");
		e.setFormat(e.getFormat() + " " + ChatColor.WHITE + "%s:" + rank.getChatFormat() + " %s");
	}

	@EventHandler
	private void join(PlayerJoinEvent e) {
		e.getPlayer().setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
		plugin.getRankApi().loadRank(e.getPlayer().getUniqueId());
	}

	@EventHandler
	private void quit(PlayerQuitEvent e) {
		plugin.getRankApi().unload(e.getPlayer().getUniqueId());
	}
}
