package net.athenamc.spigot.core.permissions;

import java.lang.reflect.Field;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftHumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import net.athenamc.core.exceptions.PermissionRequiredException;
import net.athenamc.core.exceptions.RankDoesNotExistException;
import net.athenamc.core.ranks.Rank;
import net.athenamc.spigot.core.SpigotCore;

public class PermissionModule implements CommandExecutor, Listener {
	private SpigotCore plugin;
	private Field permissible;

	public PermissionModule(SpigotCore plugin) {
		this.plugin = plugin;

		plugin.getCommand("addperm").setExecutor(this);
		plugin.getCommand("delperm").setExecutor(this);
		plugin.getCommand("checkperm").setExecutor(this);

		plugin.getServer().getPluginManager().registerEvents(this, plugin);

		try {
			permissible = CraftHumanEntity.class.getDeclaredField("perm");
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		try {
			if (args.length > 2) {
				if (args[0].equalsIgnoreCase("rank") || args[0].equalsIgnoreCase("player")) {
					if (cmd.getName().equalsIgnoreCase("addperm")) {
						plugin.checkPerm(sender, "core.permissions.add." + args[0]);
						boolean value = true;
						String permission = args[2];
						if (args[2].startsWith("-")) {
							value = false;
							permission = args[2].substring(1);
						}
						if (args[0].equalsIgnoreCase("rank")) {
							plugin.getPermissionApi().setRankPermission(args[1], permission, value);
						} else {
							plugin.getPermissionApi().setPlayerPermission(
									Bukkit.getOfflinePlayer(args[1]).getUniqueId(), permission, value);
						}
						sender.sendMessage(ChatColor.GREEN + "Added the permission: " + (value ? "" : "-") + permission
								+ " to " + args[1]);
					} else if (cmd.getName().equalsIgnoreCase("delperm")) {
						plugin.checkPerm(sender, "core.permissions.delete." + args[0]);
						String permission = args[2];
						if (permission.startsWith("-"))
							permission = permission.substring(1);
						if (args[0].equalsIgnoreCase("rank")) {
							plugin.getPermissionApi().unsetRankPermission(args[1], permission);
						} else if (args[0].equalsIgnoreCase("player")) {
							plugin.getPermissionApi()
									.unsetPlayerPermission(Bukkit.getOfflinePlayer(args[1]).getUniqueId(), permission);
						} else
							return false;
						sender.sendMessage(ChatColor.GREEN + "Deleted the permission: " + args[2] + " from " + args[1]);
					}
				} else
					return false;
			} else if (args.length > 1) {
				if (cmd.getName().equalsIgnoreCase("checkperm")) {
					plugin.checkPerm(sender, "core.permissions.check." + args[0]);
					Map<String, Boolean> permissions;
					Rank lower = null;
					if (args[0].equalsIgnoreCase("rank")) {
						Rank rank = plugin.getRankApi().getRank(args[1]);
						permissions = rank.getPermissions();
						sender.sendMessage(ChatColor.GREEN + rank.getName() + "'s permissions:");
						lower = plugin.getRankApi().getRankLower(rank);
					} else if (args[0].equalsIgnoreCase("player")) {
						OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[1]);
						sender.sendMessage(ChatColor.GREEN + offlinePlayer.getName() + "'s permissions: ");
						permissions = plugin.getPermissionApi().getPlayerPermissions(offlinePlayer.getUniqueId());
						lower = plugin.getRankApi().getRank(offlinePlayer.getUniqueId());
					} else
						return false;
					for (Map.Entry<String, Boolean> entry : permissions.entrySet())
						sender.sendMessage(ChatColor.GREEN + "- " + (entry.getValue() ? "" : "-") + entry.getKey());
					if (lower != null)
						sender.sendMessage(ChatColor.GREEN + "- All permissions from rank: " + lower.getName());
				}
			} else
				return false;
			return true;
		} catch (

		PermissionRequiredException e) {
			sender.sendMessage(
					ChatColor.RED + "You must have the permission " + e.getPermission() + " to perform that command");
		} catch (RankDoesNotExistException e) {
			sender.sendMessage(ChatColor.RED + "There is no rank called " + args[1]);
		}
		return true;
	}

	@EventHandler
	private void onJoin(PlayerJoinEvent e) {
		try {
			permissible.setAccessible(true);
			permissible.set(e.getPlayer(), new PermissibleOverride(e.getPlayer()));
		} catch (IllegalArgumentException e1) {
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			e1.printStackTrace();
		}
	}
}
