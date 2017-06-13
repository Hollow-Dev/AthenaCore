package net.athenamc.core;

import java.util.UUID;
import java.util.logging.Logger;

import net.athenamc.core.bansystem.PunishmentAPI;
import net.athenamc.core.currencies.CurrencyAPI;
import net.athenamc.core.currencies.CurrencyAPI.CurrencyType;
import net.athenamc.core.exceptions.PermissionRequiredException;
import net.athenamc.core.permissions.PermissionAPI;
import net.athenamc.core.ranks.RankAPI;
import net.athenamc.core.sqlmanager.SQLManager;

public interface Core {
	public abstract SQLManager getSqlManager();
	public abstract SQLManager getServerSqlManager();
	public abstract void callCurrencyChangeEvent(CurrencyType type, UUID uuid, int amount);
	
	public abstract CurrencyAPI getCurrencyApi();
	public abstract RankAPI getRankApi();
	public abstract PermissionAPI getPermissionApi();
	public abstract PunishmentAPI getPunishmentApi();
	public abstract boolean checkPerm(String permission, UUID uuid) throws PermissionRequiredException;
	
	public abstract Logger getLogger();
}
