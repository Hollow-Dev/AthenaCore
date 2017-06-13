package net.athenamc.core.currencies;

import java.sql.ResultSet;
import java.util.UUID;

import net.athenamc.core.Core;

public class CurrencyAPI {
	private Core core;

	public CurrencyAPI(Core core) {
		this.core = core;
	}

	public enum CurrencyType {
		GEMS, TICKETS;
	}

	public boolean canAfford(UUID uuid, int cost, CurrencyType type) {
		return getCurrency(uuid, type) >= cost;
	}

	public boolean buyIfCanAfford(UUID uuid, int cost, CurrencyType type) {
		if (canAfford(uuid, cost, type)) {
			setCurrency(uuid, getCurrency(uuid, type) - cost, type);
			return true;
		}
		return false;
	}

	public int getCurrency(UUID uuid, CurrencyType type) {
		try {
			String currency = type.name().toLowerCase();
			ResultSet rs = core.getSqlManager().executeQuery(
					"SELECT " + currency.toLowerCase() + " FROM currencies WHERE uuid='" + uuid.toString() + "'");
			while (rs.next())
				return rs.getInt(currency.toLowerCase());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	public void setCurrency(UUID uuid, int amount, CurrencyType type) {
		String currency = type.name().toLowerCase();
		if (amount < 0)
			amount = 0;
		core.getSqlManager().execute(
				"REPLACE INTO currencies (uuid, " + currency + ") VALUES ('" + uuid.toString() + "', " + amount + ")");

		core.callCurrencyChangeEvent(type, uuid, amount);
	}
}
