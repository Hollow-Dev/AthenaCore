package net.athenamc.core.permissions;

import java.util.Map;
import java.util.UUID;

import net.athenamc.core.Core;

public abstract class PermissionAPI {
	protected Core plugin;
	
	public PermissionAPI(Core plugin) {
		this.plugin = plugin;
	}
	
	public boolean hasPermission(UUID uuid, String permission) {
		if (isPermissionSet(uuid, "*"))
			return getPlayerPermissions(uuid).get("*");
		else if (isPermissionSet(uuid, permission))
			return getPlayerPermissions(uuid).get(permission);
		return plugin.getRankApi().getRank(uuid).hasPermission(permission);
	}
	
	public boolean isPermissionSet(UUID uuid, String permission) {
		return getPlayerPermissions(uuid).containsKey(permission);
	}
	
	public abstract Map<String, Boolean> getPlayerPermissions(UUID uuid);
}
