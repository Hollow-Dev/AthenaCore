package net.athenamc.spigot.core;

import java.io.File;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import io.netty.util.internal.ThreadLocalRandom;
import lombok.Getter;
import net.athenamc.core.Core;
import net.athenamc.core.bansystem.PunishmentAPI;
import net.athenamc.core.currencies.CurrencyAPI;
import net.athenamc.core.currencies.CurrencyAPI.CurrencyType;
import net.athenamc.core.exceptions.PermissionRequiredException;
import net.athenamc.core.ranks.Rank;
import net.athenamc.spigot.core.bansystem.listeners.MuteListener;
import net.athenamc.spigot.core.currencies.CurrencyChangeEvent;
import net.athenamc.spigot.core.currencies.CurrencyModule;
import net.athenamc.spigot.core.market.MarketModule;
import net.athenamc.spigot.core.permissions.PermissionModule;
import net.athenamc.spigot.core.permissions.SpigotPermissionAPI;
import net.athenamc.spigot.core.ranks.RankModule;
import net.athenamc.spigot.core.ranks.SpigotRankAPI;
import net.athenamc.spigot.core.sqlmanager.SQLManagerBukkit;
import net.athenamc.spigot.core.staff.StaffModule;

public class SpigotCore extends JavaPlugin implements Core, Listener {
	@Getter
	private CurrencyAPI currencyApi;
	@Getter
	private SpigotPermissionAPI permissionApi;
	@Getter
	private SpigotRankAPI rankApi;
	@Getter
	private PunishmentAPI punishmentApi;

	public void callCurrencyChangeEvent(CurrencyType type, UUID uuid, int amount) {
		getServer().getPluginManager().callEvent(new CurrencyChangeEvent(type, uuid, amount));
	}

	public boolean checkPerm(CommandSender sender, String permission) throws PermissionRequiredException {
		if (sender instanceof Player)
			return checkPerm(permission, ((Player) sender).getUniqueId());
		else
			return true;
	}

	@Getter
	private String serverName;
	@Getter
	private String bungeeName;

	private HashMap<UUID, Long> joinTime;
	private HashMap<UUID, Long> serverTime;
	private HashMap<UUID, Long> globalTime;

	@Getter
	private SQLManagerBukkit sqlManager;

	@Getter
	private SQLManagerBukkit serverSqlManager;

	private Location spawn;

	@Override
	public void onEnable() {
		if (getConfig().contains("Spawn"))
			spawn = new Location(Bukkit.getWorld(getConfig().getString("Spawn.World")),
					getConfig().getDouble("Spawn.X"), getConfig().getDouble("Spawn.Y"),
					getConfig().getDouble("Spawn.Z"), (float) getConfig().getDouble("Spawn.Yaw"),
					(float) getConfig().getDouble("Spawn.Pitch"));

		for (File file : new File("./").listFiles()) {
			if (file.getName().toLowerCase().startsWith("server_")) {
				serverName = (file.getName().substring(file.getName().indexOf('_') + 1,
						file.getName().lastIndexOf('_'))).toLowerCase();
				bungeeName = file.getName().substring(file.getName().lastIndexOf('_') + 1);
				break;
			}
		}
		if (serverName == null || bungeeName == null || serverName.equalsIgnoreCase("")
				|| bungeeName.equalsIgnoreCase("")) {
			getLogger()
					.severe("You must create a file with the server name and the server's name in the bungee config");
			getServer().shutdown();
			return;
		} else {
			getLogger().info("Found name: " + serverName + " and Bungee name: " + bungeeName);
		}
		try {
			sqlManager = new SQLManagerBukkit(this, "general");
		} catch (Exception e) {
			e.printStackTrace();
			getLogger().severe("The general database could not connect, therefore the plugin will disable itself");
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		try {
			serverSqlManager = new SQLManagerBukkit(this, serverName);
		} catch (Exception e) {
			e.printStackTrace();
			getLogger().severe(
					"The " + serverName + " database could not connect, therefore the plugin will disable itself");
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		rankApi = new SpigotRankAPI(this);
		permissionApi = new SpigotPermissionAPI(this);
		new RankModule(this);
		new PermissionModule(this);

		for (Player p : Bukkit.getOnlinePlayers())
			getRankApi().loadRank(p.getUniqueId());
		getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
		new StaffModule(this);
		currencyApi = new CurrencyAPI(this);
		new CurrencyModule(this);
		new MarketModule(this);

		getServer().getPluginManager().registerEvents(new MuteListener(this), this);

		joinTime = new HashMap<UUID, Long>();
		serverTime = new HashMap<UUID, Long>();
		globalTime = new HashMap<UUID, Long>();

		getServer().getPluginManager().registerEvents(this, this);
		for (Player player : Bukkit.getOnlinePlayers())
			loadPlaytime(player);
		new BukkitRunnable() {
			public void run() {
				for (Player player : Bukkit.getOnlinePlayers())
					savePlayTime(player);
			}
		}.runTaskTimer(this, 0, 20 * 10);
		punishmentApi = new PunishmentAPI(this);
	}

	@Override
	public void onDisable() {
		if (spawn != null) {
			getConfig().set("Spawn.World", spawn.getWorld().getName());
			getConfig().set("Spawn.X", spawn.getX());
			getConfig().set("Spawn.Y", spawn.getY());
			getConfig().set("Spawn.Z", spawn.getZ());
			getConfig().set("Spawn.Yaw", spawn.getYaw());
			getConfig().set("Spawn.Pitch", spawn.getPitch());
			saveConfig();
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		try {
			if (sender instanceof Player) {
				Player player = (Player) sender;
				if (cmd.getName().equalsIgnoreCase("hub")) {
					checkPerm(sender, "core.hub");
					if (serverName.equalsIgnoreCase("Hub")) {
						sender.sendMessage(ChatColor.RED + "You're already on the hub!");
					} else {
						sender.sendMessage(ChatColor.GREEN + "Sending you to the hub!");
						ByteArrayDataOutput out = ByteStreams.newDataOutput();
						out.writeUTF("Connect");
						out.writeUTF("Hub");

						player.sendPluginMessage(this, "BungeeCord", out.toByteArray());
					}
				} else if (cmd.getName().equalsIgnoreCase("playtime")) {
					checkPerm(sender, "core.playtime");
					long time = (System.currentTimeMillis() - joinTime.get(player.getUniqueId()));
					sender.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "You have played on server GLOBAL for: "
							+ ChatColor.YELLOW + "" + ChatColor.BOLD
							+ getStringFromTime((time + globalTime.get(player.getUniqueId()))));
					sender.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "You have played on server "
							+ serverName.toUpperCase() + " for: " + ChatColor.YELLOW + "" + ChatColor.BOLD
							+ getStringFromTime((time + serverTime.get(player.getUniqueId()))));
				} else if (cmd.getName().equalsIgnoreCase("spawnpoint")) {
					checkPerm(sender, "core.spawnpoint");
					spawn = player.getLocation();
					sender.sendMessage(
							ChatColor.GREEN + "You have set the world's spawn point to your current location");
				}
			} else
				sender.sendMessage(ChatColor.RED + "That command can only be performed by a player");
		} catch (PermissionRequiredException e) {
			sender.sendMessage(
					ChatColor.RED + "You must have the permission " + e.getPermission() + " to perform that command");
		}
		return true;
	}

	private String getStringFromTime(long time) {
		int days, hours, minutes, seconds;
		days = (int) (time / 1000 / 60 / 60 / 24);
		time -= (days * 1000 * 60 * 60 * 24);
		hours = (int) (time / 1000 / 60 / 60);
		time -= (hours * 1000 * 60 * 60);
		minutes = (int) (time / 1000 / 60);
		time -= (minutes * 1000 * 60);
		seconds = (int) (time / 1000);
		return (days > 9 ? days : "0" + days) + "D " + (hours > 9 ? hours : "0" + hours) + "H "
				+ (minutes > 9 ? minutes : "0" + minutes) + "M " + (seconds > 9 ? seconds : "0" + seconds) + "S";
	}

	@EventHandler
	private void onJoin(PlayerJoinEvent e) {
		loadPlaytime(e.getPlayer());
	}

	@EventHandler
	private void onRespawn(PlayerRespawnEvent e) {
		if (spawn != null)
			e.setRespawnLocation(spawn);
	}

	@EventHandler
	private void onQuit(PlayerQuitEvent e) {
		savePlayTime(e.getPlayer());
		joinTime.remove(e.getPlayer().getUniqueId());
		globalTime.remove(e.getPlayer().getUniqueId());
		serverTime.remove(e.getPlayer().getUniqueId());
	}

	private void loadPlaytime(Player player) {
		joinTime.put(player.getUniqueId(), System.currentTimeMillis());
		try {
			ResultSet rs = getSqlManager()
					.executeQuery("SELECT time FROM playtime WHERE uuid='" + player.getUniqueId().toString() + "'");
			while (rs.next()) {
				globalTime.put(player.getUniqueId(), rs.getLong("time"));
			}
		} catch (Exception e) {
		}
		try {
			ResultSet rs = getServerSqlManager()
					.executeQuery("SELECT time FROM playtime WHERE uuid='" + player.getUniqueId().toString() + "'");
			while (rs.next()) {
				serverTime.put(player.getUniqueId(), rs.getLong("time"));
			}
		} catch (Exception e) {
		}

		if (!globalTime.containsKey(player.getUniqueId()))
			globalTime.put(player.getUniqueId(), 0L);
		if (!serverTime.containsKey(player.getUniqueId()))
			serverTime.put(player.getUniqueId(), 0L);
	}

	private void savePlayTime(Player player) {
		long time = System.currentTimeMillis() - joinTime.get(player.getUniqueId());
		getSqlManager().execute("INSERT INTO playtime (uuid, time) VALUES ('" + player.getUniqueId() + "', " + time
				+ ") ON DUPLICATE KEY UPDATE time = " + (time + globalTime.get(player.getUniqueId())));
		getServerSqlManager().execute("INSERT INTO playtime (uuid, time) VALUES ('" + player.getUniqueId() + "', "
				+ time + ") ON DUPLICATE KEY UPDATE time = " + (time + serverTime.get(player.getUniqueId())));
	}

	@EventHandler
	private void chatEvent(AsyncPlayerChatEvent e) {
		for (Player player : Bukkit.getOnlinePlayers()) {
			StringBuilder msg = new StringBuilder();
			boolean send = false;
			for (String s : e.getMessage().split(" ")) {
				if (s.equalsIgnoreCase(player.getName())) {
					e.getRecipients().remove(player);
					send = true;
					int colorCode = ThreadLocalRandom.current().nextInt(13);
					if (colorCode == 1 || colorCode == 4 || colorCode == 12)
						colorCode++;
					msg.append(ChatColor.getByChar(Integer.toHexString(colorCode))).append(ChatColor.BOLD);
				}
				msg.append(s);
				Rank rank = getRankApi().getRank(e.getPlayer().getUniqueId());
				if (!ChatColor.getLastColors(msg.toString()).equalsIgnoreCase(rank.getChatFormat()))
					msg.append(rank.getChatFormat());
				msg.append(" ");
			}
			if (send) {
				player.sendMessage(String.format(e.getFormat(), e.getPlayer().getName(), msg.toString()));
				player.playSound(player.getLocation(), Sound.NOTE_PLING, 10, 1);
			}
		}
	}

	@EventHandler
	private void commandPreprocess(PlayerCommandPreprocessEvent e) {
		if (e.getPlayer().hasPermission("core.bypass")) {
			if (e.getMessage().contains(":") || e.getMessage().toLowerCase().startsWith("/plugins")
					|| e.getMessage().split(" ")[0].equalsIgnoreCase("/pl")
					|| e.getMessage().split(" ")[0].equalsIgnoreCase("/?")) {
				e.setCancelled(true);
				e.getPlayer().sendMessage(ChatColor.RED + ChatColor.BOLD.toString() + "Athena " + ChatColor.DARK_GRAY
						+ ">> " + ChatColor.RED + "You are blocked from using that command!");
			}
		}
	}

	public boolean checkPerm(String permission, UUID uuid) throws PermissionRequiredException {
		if (!getPermissionApi().hasPermission(uuid, permission))
			throw new PermissionRequiredException(permission);
		return true;
	}
}
