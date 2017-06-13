package net.athenamc.core.ranks;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.athenamc.core.Core;
import net.md_5.bungee.api.ChatColor;

@RequiredArgsConstructor
public abstract class Rank implements Comparable<Rank> {
	@Getter
	@Setter
	private String chatFormat = ChatColor.GRAY.toString();
	@Getter
	private String prefix = "";
	@Getter
	@Setter
	private int power = 0;
	@NonNull
	@Getter
	private String name;
	@Getter
	private Map<String, Boolean> permissions;

	protected Core plugin;

	public Rank(String chatFormat, String prefix, int power, String rank) {
		plugin = loadPlugin();
		this.chatFormat = chatFormat;
		this.prefix = prefix;
		this.power = power;
		this.name = rank;

		permissions = new HashMap<String, Boolean>();

		reloadPerms();
	}

	public abstract Core loadPlugin();

	public void setPrefix(String prefix) {
		this.prefix = ChatColor.translateAlternateColorCodes('&', prefix);
	}

	public void setName(String name) {
		this.name = ChatColor.stripColor(name);
		if (name.length() > 10)
			name = name.substring(0, 10);
	}

	public void unsetPermission(String permission) {
		permissions.remove(permission);
	}

	public void setPermission(String permission, Boolean value) {
		permissions.put(permission, value);
	}

	public void reloadPerms() {
		try {
			ResultSet rs = plugin.getSqlManager()
					.executeQuery("SELECT permission, value FROM rank_permissions WHERE rank='" + name + "'");
			while (rs.next())
				permissions.put(rs.getString("permission"), rs.getBoolean("value"));
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public boolean hasPermission(String permissionName) {
		plugin.getLogger().info("Checking if rank: " + getName() + " has permission : " + permissionName);
		if (permissions.containsKey("*"))
			return permissions.get("*");
		else if (permissions.containsKey(permissionName))
			return permissions.get(permissionName);
		else
			try {
				return plugin.getRankApi().getRankLower(this).hasPermission(permissionName);
			} catch (NullPointerException e) {
				return false;
			}
	}

	public int compareTo(Rank rank) {
		if (rank.getPower() == getPower())
			return getName().compareTo(rank.getName());
		else if (getPower() > rank.getPower())
			return 1;
		else
			return -1;
	}
}