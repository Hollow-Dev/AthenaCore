package net.athenamc.proxy.core.listeners;

import net.athenamc.proxy.core.BungeeCore;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PermissionCheckEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PermissionListener implements Listener {
	private BungeeCore plugin;

	public PermissionListener(BungeeCore plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = Byte.MIN_VALUE)
	public void permissionCheck(PermissionCheckEvent e) {
		if (e.getSender() instanceof ProxiedPlayer) {
			boolean hasPermission = plugin.getPermissionApi()
					.hasPermission(((ProxiedPlayer) e.getSender()).getUniqueId(), e.getPermission());
			e.setHasPermission(hasPermission);
		} else
			e.setHasPermission(true);
	}
}
