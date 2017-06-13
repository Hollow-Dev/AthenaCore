package net.athenamc.spigot.core.bansystem.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import net.athenamc.spigot.core.SpigotCore;

public class MuteListener implements Listener {
	private SpigotCore core;

	public MuteListener(SpigotCore core) {
		this.core = core;
	}

	@EventHandler
	private void playerChatEvent(AsyncPlayerChatEvent e) {
		if (core.getPunishmentApi().isMuted(e.getPlayer().getUniqueId())) {
			e.setCancelled(true);
		}
	}
}
