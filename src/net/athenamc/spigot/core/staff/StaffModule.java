package net.athenamc.spigot.core.staff;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

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
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.google.common.collect.Iterables;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import net.athenamc.core.exceptions.PermissionRequiredException;
import net.athenamc.core.ranks.RankAPI;
import net.athenamc.spigot.core.SpigotCore;

public class StaffModule implements CommandExecutor, Listener, PluginMessageListener {
	private SpigotCore plugin;
	private HashSet<UUID> staffChat, hidden;
	private boolean muted = false;
	private HashMap<UUID, Long> cooldown;

	public StaffModule(SpigotCore plugin) {
		this.plugin = plugin;
		Bukkit.getMessenger().registerIncomingPluginChannel(plugin, "BungeeCord", this);
		staffChat = new HashSet<UUID>();
		hidden = new HashSet<UUID>();
		cooldown = new HashMap<UUID, Long>();

		plugin.getCommand("staffchat").setExecutor(this);
		plugin.getCommand("chatmute").setExecutor(this);
		plugin.getCommand("chatunmute").setExecutor(this);
		plugin.getCommand("chatclear").setExecutor(this);
		plugin.getCommand("show").setExecutor(this);
		plugin.getCommand("hide").setExecutor(this);
		plugin.getCommand("fly").setExecutor(this);
		plugin.getCommand("flyspeed").setExecutor(this);

		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	public void onDisable() {
		for (UUID uuid : hidden) {
			Player hiddenplayer = Bukkit.getPlayer(uuid);
			for (Player player : Bukkit.getOnlinePlayers()) {
				player.showPlayer(hiddenplayer);
			}
		}
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		try {
			if (cmd.getName().equalsIgnoreCase("staffchat")) {
				plugin.checkPerm(sender, "core.staff.chat");
				if (args.length > 0) {
					StringBuilder sb = new StringBuilder();
					for (String s : args)
						sb.append(s).append(" ");
					sendMessage(sender, sb.toString().trim());
				} else if (sender instanceof Player) {
					toggleStaffChat((Player) sender);
				} else
					return false;
			} else if (cmd.getName().equalsIgnoreCase("chatclear")) {
				plugin.checkPerm(sender, "core.staff.chat.clear");
				String s = " ";
				for (int i = 0; i < 100; i++)
					Bukkit.broadcastMessage(s += " ");
				Bukkit.broadcastMessage(ChatColor.RED + "Chat was cleared by " + ChatColor.DARK_RED + sender.getName());
			} else if (cmd.getName().equalsIgnoreCase("chatmute")) {
				plugin.checkPerm(sender, "core.staff.chat.mute");
				if (!muted)
					Bukkit.broadcastMessage(
							ChatColor.RED + "Chat was muted by " + ChatColor.DARK_RED + sender.getName());
				muted = true;
			} else if (cmd.getName().equalsIgnoreCase("chatunmute")) {
				plugin.checkPerm(sender, "core.staff.chat.unmute");
				if (muted)
					Bukkit.broadcastMessage(
							ChatColor.RED + "Chat was unmuted by " + ChatColor.DARK_RED + sender.getName());
				muted = false;
			} else if (sender instanceof Player) {
				Player send = (Player) sender;
				if (cmd.getName().equalsIgnoreCase("hide")) {
					plugin.checkPerm(sender, "core.staff.hide");
					if (!hidden.contains(send.getUniqueId())) {
						hidden.add(send.getUniqueId());

						for (Player player : Bukkit.getOnlinePlayers()) {
							if (!player.hasPermission("core.staff.seehidden"))
								player.hidePlayer(send);
						}
						sender.sendMessage(ChatColor.GREEN + "You are now hidden from non staff members!");
					}
				} else if (cmd.getName().equalsIgnoreCase("show")) {
					plugin.checkPerm(sender, "core.staff.hide");
					if (hidden.contains(send.getUniqueId())) {
						hidden.remove(send.getUniqueId());
						for (Player player : Bukkit.getOnlinePlayers()) {
							player.showPlayer(send);
						}
						sender.sendMessage(ChatColor.RED + "You are now visible to all members!");
					}
				} else if (cmd.getName().equalsIgnoreCase("fly")) {
					plugin.checkPerm(sender, "core.staff.fly");
					if (send.getAllowFlight())
						send.setFlying(false);
					send.setAllowFlight(!send.getAllowFlight());
					sender.sendMessage(ChatColor.GOLD + "You have "
							+ (send.getAllowFlight() ? ChatColor.GREEN + "ENABLED" : ChatColor.RED + "DISABLED")
							+ ChatColor.GOLD + " Flight mode!");
				} else if (cmd.getName().equalsIgnoreCase("flyspeed")) {
					plugin.checkPerm(sender, "core.staff.flyspeed");
					if (args.length > 0) {
						Float speed = Float.parseFloat(args[0]);
						if (speed >= 0.01 && speed <= 1) {
							send.setFlySpeed(speed);
							sender.sendMessage(ChatColor.GOLD + "Flight speed set to " + ChatColor.GREEN + speed);
						} else
							sender.sendMessage(ChatColor.RED + "Fly speed must be between 0.01 and 1.0");
					} else
						return false;
				}
			} else
				return false;
		} catch (PermissionRequiredException e) {
			sender.sendMessage(
					ChatColor.RED + "You must have the permission " + e.getPermission() + " to perform that command");
		} catch (NumberFormatException nfe) {
			sender.sendMessage(ChatColor.RED + "You entered an invalid number");
		}
		return true;
	}

	private void sendMessage(CommandSender sender, String message) {
		Player player = Iterables.getFirst(Bukkit.getOnlinePlayers(), null);
		if (player != null) {
			String msg = ChatColor.GRAY.toString() + ChatColor.BOLD + "[" + ChatColor.GREEN + ChatColor.BOLD + "STAFF"
					+ ChatColor.GRAY + ChatColor.BOLD + "]: "
					+ (sender instanceof Player
							? plugin.getRankApi().getRank(((Player) sender).getUniqueId()).getPrefix() + " " : "")
					+ ChatColor.WHITE + sender.getName() + ChatColor.GRAY + ": " + ChatColor.GREEN + message;

			for (Player p : Bukkit.getOnlinePlayers())
				if (p.hasPermission("core.staff.chat"))
					p.sendMessage(msg);

			Bukkit.getLogger().info(msg);

			try {
				ByteArrayDataOutput out = ByteStreams.newDataOutput();
				out.writeUTF("Forward");
				out.writeUTF("ONLINE");
				out.writeUTF("BungeeCord");

				ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
				DataOutputStream msgout = new DataOutputStream(msgbytes);
				msgout.writeUTF("StaffChat");
				msgout.writeUTF(msg);
				out.writeShort(msgbytes.toByteArray().length);
				out.write(msgbytes.toByteArray());

				player.sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			sender.sendMessage(ChatColor.RED + "Chat message unable to send");
		}
	}

	public void sendReport(Player player, OfflinePlayer reportee) {
		String msg = ChatColor.GRAY + "" + ChatColor.BOLD + "[" + ChatColor.GREEN + ChatColor.BOLD + "STAFF"
				+ ChatColor.GRAY + ChatColor.BOLD + "]: " + ChatColor.GRAY + "Report on " + ChatColor.RED
				+ reportee.getName() + ChatColor.GRAY + " by " + ChatColor.GREEN + player.getName() + ChatColor.GRAY
				+ " on server " + ChatColor.DARK_PURPLE + plugin.getBungeeName();

		for (Player p : Bukkit.getOnlinePlayers())
			if (p.hasPermission("core.staff.report"))
				p.sendMessage(msg);

		Bukkit.getLogger().info(msg);

		try {
			ByteArrayDataOutput out = ByteStreams.newDataOutput();
			out.writeUTF("Forward");
			out.writeUTF("ONLINE");
			out.writeUTF("BungeeCord");

			ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
			DataOutputStream msgout = new DataOutputStream(msgbytes);
			msgout.writeUTF("Report");
			msgout.writeUTF(msg);
			out.writeShort(msgbytes.toByteArray().length);
			out.write(msgbytes.toByteArray());

			player.sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void toggleStaffChat(Player player) {
		if (staffChat.contains(player.getUniqueId())) {
			staffChat.remove(player.getUniqueId());
			player.sendMessage(ChatColor.RED + "You have left staffchat");
		} else {
			staffChat.add(player.getUniqueId());
			player.sendMessage(ChatColor.GREEN + "You have entered staffchat");
		}
	}

	public void onPluginMessageReceived(String channel, Player player, byte[] data) {
		try {
			if (!channel.equals("BungeeCord")) {
				return;
			}
			ByteArrayDataInput in = ByteStreams.newDataInput(data);
			String subchannel = in.readUTF();
			if (subchannel.equals("Forward")) {
				short len = in.readShort();
				byte[] msgbytes = new byte[len];
				in.readFully(msgbytes);

				DataInputStream msgin = new DataInputStream(new ByteArrayInputStream(msgbytes));
				String messageType = msgin.readUTF();
				String perm = "";
				if (messageType.equalsIgnoreCase("StaffChat"))
					perm = "core.staff.chat";
				else if (messageType.equalsIgnoreCase("Report"))
					perm = "core.staff.report";
				String msg = msgin.readUTF();
				Bukkit.getLogger().info(msg);
				for (Player p : Bukkit.getOnlinePlayers())
					if (p.hasPermission(perm))
						p.sendMessage(msg);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@EventHandler
	private void chatEvent(AsyncPlayerChatEvent e) {
		if (staffChat.contains(e.getPlayer().getUniqueId())) {
			e.setCancelled(true);
			sendMessage(e.getPlayer(), e.getMessage());
		}
		if (muted && !e.getPlayer().hasPermission("core.staff.chatmute.bypass"))
			e.setCancelled(true);
	}

	@EventHandler
	private void join(PlayerJoinEvent e) {
		if (!e.getPlayer().hasPermission("core.staff.seehidden"))
			for (UUID uuid : hidden)
				e.getPlayer().hidePlayer(Bukkit.getPlayer(uuid));
	}

	@EventHandler
	private void quit(PlayerQuitEvent e) {
		if (hidden.contains(e.getPlayer())) {
			hidden.remove(e.getPlayer().getUniqueId());
			for (Player player : Bukkit.getOnlinePlayers()) {
				player.showPlayer(e.getPlayer());
			}
		}
	}
}
