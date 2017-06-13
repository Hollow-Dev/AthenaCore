package net.athenamc.proxy.core.permissions;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.athenamc.core.permissions.PermissionAPI;
import net.athenamc.proxy.core.BungeeCore;

public class BungeePermissionAPI extends PermissionAPI {
	public BungeePermissionAPI(BungeeCore plugin) {
		super(plugin);
	}

	@Override
	public boolean hasPermission(UUID uuid, String permissionName) {
		plugin.getLogger().info("Checking if the player has the permission: " + permissionName);
		HashMap<String, Boolean> permissions = new HashMap<String, Boolean>();
		ResultSet rs = plugin.getSqlManager()
				.executeQuery("SELECT * FROM player_permissions WHERE uuid='" + uuid.toString() + "' AND permission='"
						+ permissionName + "' OR uuid='" + uuid.toString() + "' AND permission='*'");
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
		return plugin.getRankApi().getRank(uuid).hasPermission(permissionName);
	}

	@Override
	public Map<String, Boolean> getPlayerPermissions(UUID uuid) {
		HashMap<String, Boolean> permissions = new HashMap<String, Boolean>();
		ResultSet rs = plugin.getSqlManager()
				.executeQuery("SELECT * FROM player_permissions WHERE uuid='" + uuid.toString() + "'");
		try {
			while (rs.next()) {
				permissions.put(rs.getString("permission"), rs.getBoolean("value"));
			}
		} catch (Exception e) {
		}
		return permissions;
	}
}
