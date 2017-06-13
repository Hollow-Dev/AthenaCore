package net.athenamc.proxy.core;

import java.sql.ResultSet;
import java.util.UUID;

import lombok.Getter;
import net.athenamc.core.Core;
import net.athenamc.core.bansystem.PunishmentAPI;
import net.athenamc.core.currencies.CurrencyAPI;
import net.athenamc.core.currencies.CurrencyAPI.CurrencyType;
import net.athenamc.core.exceptions.PermissionRequiredException;
import net.athenamc.core.permissions.PermissionAPI;
import net.athenamc.core.ranks.RankAPI;
import net.athenamc.proxy.core.bansystem.BanModule;
import net.athenamc.proxy.core.currencies.CurrencyChangeEvent;
import net.athenamc.proxy.core.listeners.PermissionListener;
import net.athenamc.proxy.core.permissions.BungeePermissionAPI;
import net.athenamc.proxy.core.sqlmanager.SQLManagerBungee;
import net.athenamc.spigot.core.permissions.SpigotPermissionAPI;
import net.md_5.bungee.api.plugin.Plugin;

public class BungeeCore extends Plugin implements Core {
	@Getter
	private CurrencyAPI currencyApi;
	@Getter
	private RankAPI rankApi;
	@Getter
	private PermissionAPI permissionApi;
	@Getter
	private PunishmentAPI punishmentApi;

	public UUID getUUID(String playerName) {
		ResultSet rs = getSqlManager().executeQuery("SELECT uuid FROM name_converter WHERE name='" + playerName + "'");
		try {
			if (rs.next())
				return UUID.fromString(rs.getString("uuid"));
		} catch (Exception e) {
		}
		return null;
	}

	@Getter
	private SQLManagerBungee sqlManager;
	@Getter
	private SQLManagerBungee serverSqlManager;

	@Override
	public void onEnable() {
		this.sqlManager = new SQLManagerBungee(this, "general");
		this.serverSqlManager = new SQLManagerBungee(this, "bungeecord");

		currencyApi = new CurrencyAPI(this);
		rankApi = new RankAPI(this);
		punishmentApi = new PunishmentAPI(this);
		permissionApi = new BungeePermissionAPI(this);

		new BanModule(this);

		getProxy().getPluginManager().registerListener(this, new PermissionListener(this));
	}

	@Override
	public void onDisable() {
		getProxy().getScheduler().cancel(this);
	}

	public void callCurrencyChangeEvent(CurrencyType type, UUID uuid, int amount) {
		getProxy().getPluginManager().callEvent(new CurrencyChangeEvent(type, uuid, amount));
	}

	public boolean checkPerm(String permission, UUID uuid) throws PermissionRequiredException {
		if (!getPermissionApi().hasPermission(uuid, permission))
			throw new PermissionRequiredException(permission);
		return true;
	}

	public Long getTimeFromString(String timeString) {
		Long time = 0L;
		String temp = "";
		for (Character c : timeString.toLowerCase().toCharArray()) {
			if (Character.isDigit(c))
				temp += c + "";
			else if (temp.length() > 0) {
				Long tempLong = Long.parseLong(temp);
				switch (c) {
				case 'w':
					tempLong *= 7;
				case 'd':
					tempLong *= 24;
				case 'h':
					tempLong *= 60;
				case 'm':
					tempLong *= 60;
				default:
					tempLong *= 1000;
				}
				temp = "";
				time += tempLong;
			}
		}
		return time;
	}

	public String getTimeString(Long time) {
		time -= System.currentTimeMillis();
		String msg = "";
		int weeks, days, hours, minutes, seconds;
		weeks = (int) (time / 1000 / 60 / 60 / 24 / 7);
		time -= (weeks * 1000 * 60 * 60 * 24 * 7);
		days = (int) (time / 1000 / 60 / 60 / 24);
		time -= (days * 1000 * 60 * 60 * 24);
		hours = (int) (time / 1000 / 60 / 60);
		time -= (hours * 1000 * 60 * 60);
		minutes = (int) (time / 1000 / 60);
		time -= (minutes * 1000 * 60);
		seconds = (int) (time / 1000);
		if (weeks > 0)
			msg += weeks + " week" + (weeks == 1 ? "" : "s") + " ";
		if (days > 0)
			msg += days + " day" + (days == 1 ? "" : "s") + " ";
		if (hours > 0)
			msg += hours + " hour" + (hours == 1 ? "" : "s") + " ";
		if (minutes > 0)
			msg += minutes + " minute" + (minutes == 1 ? "" : "s") + " ";
		if (seconds > 0)
			msg += seconds + " second" + (seconds == 1 ? "" : "s") + " ";

		return msg.trim();
	}
}
