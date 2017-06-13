package net.athenamc.spigot.core.permissions;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.athenamc.core.Core;
import net.athenamc.core.exceptions.RankDoesNotExistException;
import net.athenamc.core.permissions.PermissionAPI;
import net.athenamc.core.ranks.Rank;

public class SpigotPermissionAPI extends PermissionAPI {
	private HashMap<UUID, HashMap<String, Boolean>> permissions;

	public SpigotPermissionAPI(Core plugin) {
		super(plugin);
		permissions = new HashMap<UUID, HashMap<String, Boolean>>();

		reloadPlayerPermissions();
	}

	public void reloadPlayerPermissions() {
		try {
			ResultSet rs = plugin.getSqlManager().executeQuery("SELECT * from player_permissions");
			while (rs.next()) {
				HashMap<String, Boolean> map;
				if (permissions.containsKey(UUID.fromString(rs.getString("uuid"))))
					map = permissions.get(UUID.fromString(rs.getString("uuid")));
				else
					map = new HashMap<String, Boolean>();
				map.put(rs.getString("permission"), rs.getBoolean("value"));
				permissions.put(UUID.fromString(rs.getString("uuid")), map);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void reloadPlayerPermissions(UUID uuid) {
		try {
			ResultSet rs = plugin.getSqlManager()
					.executeQuery("SELECT * from player_permissions WHERE uuid='" + uuid.toString() + "'");
			while (rs.next()) {
				HashMap<String, Boolean> map;
				if (permissions.containsKey(uuid))
					map = permissions.get(uuid);
				else
					map = new HashMap<String, Boolean>();
				map.put(rs.getString("permission"), rs.getBoolean("value"));
				permissions.put(uuid, map);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void reloadRankPermissions() {
		for (Rank rank : plugin.getRankApi().getRanks())
			rank.reloadPerms();
	}

	public boolean isPermissionSet(UUID uuid, String permissionName) {
		return permissions.containsKey(uuid) && permissions.get(uuid).containsKey(permissionName);
	}

	public void setRankPermission(String rankName, String permission, boolean value) throws RankDoesNotExistException {
		setRankPermission(plugin.getRankApi().getRank(rankName), permission, value);
	}

	public void setRankPermission(Rank rank, String permission, boolean value) {
		rank.setPermission(permission, value);

		plugin.getSqlManager().execute("REPLACE INTO rank_permissions VALUES ('" + rank.getName() + "', '" + permission
				+ "', " + (value ? "1" : "0") + ")");
	}

	public void setPlayerPermission(UUID uuid, String permission, boolean value) {
		HashMap<String, Boolean> perms = permissions.get(uuid);
		if (perms == null)
			perms = new HashMap<String, Boolean>();
		perms.put(permission, value);

		permissions.put(uuid, perms);
		plugin.getSqlManager().execute("REPLACE INTO player_permissions VALUES ('" + uuid.toString() + "', '"
				+ permission + "', " + (value ? "1" : "0") + ")");
	}

	public void unsetRankPermission(String rankName, String permission) throws RankDoesNotExistException {
		unsetRankPermission(plugin.getRankApi().getRank(rankName), permission);
	}

	public void unsetRankPermission(Rank rank, String permission) {
		rank.unsetPermission(permission);
		plugin.getSqlManager().execute("DELETE FROM rank_permissions WHERE rank ='" + rank.getName()
				+ "' AND permission='" + permission + "'");
	}

	public void unsetPlayerPermission(UUID uuid, String permission) {
		HashMap<String, Boolean> perms = permissions.get(uuid);
		if (perms == null)
			perms = new HashMap<String, Boolean>();
		perms.remove(permission);

		permissions.put(uuid, perms);
		plugin.getSqlManager().execute("DELETE FROM player_permissions WHERE uuid='" + uuid.toString()
				+ "' AND permission='" + permission + "'");
	}
	
	@Override
	public Map<String, Boolean> getPlayerPermissions(UUID uuid) {
		HashMap<String, Boolean> perms = permissions.get(uuid);
		if (perms == null)
			perms = new HashMap<String, Boolean>();
		return perms;
	}
}
