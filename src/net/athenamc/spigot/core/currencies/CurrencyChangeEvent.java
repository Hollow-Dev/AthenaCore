package net.athenamc.spigot.core.currencies;

import java.util.UUID;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import net.athenamc.core.currencies.CurrencyAPI.CurrencyType;

public class CurrencyChangeEvent extends Event {
	private CurrencyType type;
	private UUID uuid;
	private int newAmount;

	public CurrencyChangeEvent(CurrencyType type, UUID uuid, int newAmount) {
		this.type = type;
		this.uuid = uuid;
		this.newAmount = newAmount;
	}

	public CurrencyType getType() {
		return type;
	}

	public UUID getUuid() {
		return uuid;
	}

	public int getNewAmount() {
		return newAmount;
	}

	private static final HandlerList handlers = new HandlerList();

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
