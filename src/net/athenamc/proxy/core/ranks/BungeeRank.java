package net.athenamc.proxy.core.ranks;

import java.sql.ResultSet;
import java.util.HashMap;

import net.athenamc.core.Core;
import net.athenamc.core.ranks.Rank;
import net.md_5.bungee.api.ProxyServer;

public class BungeeRank extends Rank {
	public BungeeRank(String chatFormat, String prefix, int power, String rank) {
		super(chatFormat, prefix, power, rank);
	}

	public BungeeRank(String rank) {
		super(rank);
	}

	@Override
	public boolean hasPermission(String permissionName) {
		plugin.getLogger().info("Checking if " + getName() + " has the permission: " + permissionName);
		HashMap<String, Boolean> permissions = new HashMap<String, Boolean>();
		ResultSet rs = plugin.getSqlManager().executeQuery("SELECT * FROM rank_permissions WHERE rank='" + getName()
				+ "' AND permission='" + permissionName + "' OR rank='" + getName() + "' AND permission='*'");
		try {
			while (rs.next()) {
				permissions.put(rs.getString("permission"), rs.getBoolean("value"));
			}
		} catch (Exception e) {
		}

		if (permissions.containsKey("*"))
			return permissions.get("*");
		else if (permissions.containsKey(permissionName))
			return permissions.get(permissionName);
		Rank r = plugin.getRankApi().getRankLower(this);
		if (r != null)
			r.hasPermission(permissionName);
		return false;
	}

	@Override
	public Core loadPlugin() {
		return (Core) ProxyServer.getInstance().getPluginManager().getPlugin("Core");
	}
}
