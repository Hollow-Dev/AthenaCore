package net.athenamc.spigot.core.permissions;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.permissions.PermissibleBase;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.ServerOperator;

import net.athenamc.spigot.core.SpigotCore;

public class PermissibleOverride extends PermissibleBase {
	private UUID uuid;

	public PermissibleOverride(ServerOperator opable) {
		super(opable);
		if (opable instanceof OfflinePlayer)
			uuid = ((OfflinePlayer) opable).getUniqueId();
	}

	@Override
	public boolean hasPermission(Permission perm) {
		return hasPermission(perm.getName());
	}

	@Override
	public boolean hasPermission(String permissionName) {
		if (isOp())
			return true;
		return ((SpigotCore) Bukkit.getPluginManager().getPlugin("Core")).getPermissionApi().hasPermission(uuid,
				permissionName);
	}

	@Override
	public boolean isPermissionSet(String permissionName) {
		return ((SpigotCore) Bukkit.getPluginManager().getPlugin("Core")).getPermissionApi().isPermissionSet(uuid,
				permissionName);
	}

	@Override
	public boolean isPermissionSet(Permission perm) {
		return isPermissionSet(perm.getName());
	}
}
