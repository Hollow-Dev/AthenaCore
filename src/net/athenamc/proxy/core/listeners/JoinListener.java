package net.athenamc.proxy.core.listeners;

import net.athenamc.proxy.core.BungeeCore;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class JoinListener implements Listener {
	private BungeeCore core;

	public JoinListener(BungeeCore core) {
		this.core = core;
	}

	@EventHandler
	public void onJoin(LoginEvent e) {
		core.getSqlManager().execute("REPLACE INTO name_converter (name, uuid) VALUES ('" + e.getConnection().getName()
				+ "', '" + e.getConnection().getUniqueId() + "')");

		if (core.getPunishmentApi().isBanned(e.getConnection().getUniqueId())) {
			e.setCancelled(true);
			String reason = core.getPunishmentApi().getBanReason(e.getConnection().getUniqueId());
			if (core.getPunishmentApi().getBanEnd(e.getConnection().getUniqueId()) == -1L)
				e.setCancelReason("You have been banned permanently from the server\nReason: " + reason
						+ "\n\nAppeal at www.athenamc.net");
			else
				e.setCancelReason("You have been banned for "
						+ core.getTimeString(core.getPunishmentApi().getBanEnd(e.getConnection().getUniqueId()))
						+ " from the server\nReason: " + reason + "\n\nAppeal at www.athenamc.net");
		}
	}
}
