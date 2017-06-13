package net.athenamc.proxy.core.currencies;

import java.util.UUID;

import net.athenamc.core.currencies.CurrencyAPI.CurrencyType;
import net.md_5.bungee.api.plugin.Event;

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
}
