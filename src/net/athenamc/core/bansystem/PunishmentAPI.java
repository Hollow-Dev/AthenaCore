package net.athenamc.core.bansystem;

import java.sql.ResultSet;
import java.util.UUID;

import net.athenamc.core.Core;

public class PunishmentAPI {
	private Core plugin;

	public PunishmentAPI(Core core) {
		this.plugin = core;
	}

	public boolean isBanned(UUID uuid) {
		ResultSet rs = plugin.getSqlManager().executeQuery("SELECT end FROM bans WHERE uuid='" + uuid.toString() + "'");
		try {
			if (rs.next())
				if (rs.getLong("end") > System.currentTimeMillis() || rs.getLong("end") == -1)
					return true;
		} catch (Exception e) {
		}
		return false;
	}

	public boolean isMuted(UUID uuid) {
		ResultSet rs = plugin.getSqlManager()
				.executeQuery("SELECT end FROM mutes WHERE uuid='" + uuid.toString() + "'");
		try {
			if (rs.next())
				if (rs.getLong("end") > System.currentTimeMillis() || rs.getLong("end") == -1)
					return true;
		} catch (Exception e) {
		}
		return false;
	}

	public int getKicks(UUID uuid) {
		ResultSet rs = plugin.getSqlManager()
				.executeQuery("SELECT amount FROM kicks WHERE uuid='" + uuid.toString() + "'");
		try {
			if (rs.next())
				return rs.getInt("amount");
		} catch (Exception e) {
		}
		return 0;
	}

	public void setKicks(UUID uuid, int amount) {
		plugin.getSqlManager()
				.execute("REPLACE INTO kicks (uuid, amount) VALUES ('" + uuid.toString() + "', " + amount + ")");
	}

	public String getBanReason(UUID uuid) {
		ResultSet rs = plugin.getSqlManager()
				.executeQuery("SELECT reason FROM bans WHERE uuid='" + uuid.toString() + "'");
		try {
			if (rs.next())
				return rs.getString("reason");
		} catch (Exception e) {
		}
		return null;
	}

	public UUID getBanner(UUID uuid) {
		ResultSet rs = plugin.getSqlManager()
				.executeQuery("SELECT banner FROM bans WHERE uuid='" + uuid.toString() + "'");
		try {
			if (rs.next())
				return UUID.fromString(rs.getString("banner"));
		} catch (Exception e) {
		}
		return null;
	}

	public UUID getMuter(UUID uuid) {
		ResultSet rs = plugin.getSqlManager()
				.executeQuery("SELECT muter FROM mutes WHERE uuid='" + uuid.toString() + "'");
		try {
			if (rs.next())
				return UUID.fromString(rs.getString("muter"));
		} catch (Exception e) {
		}
		return null;
	}

	public Long getBanEnd(UUID uuid) {
		ResultSet rs = plugin.getSqlManager().executeQuery("SELECT end FROM bans WHERE uuid='" + uuid.toString() + "'");
		try {
			if (rs.next())
				return rs.getLong("end");
		} catch (Exception e) {
		}
		return 0L;
	}

	public Long getMuteEnd(UUID uuid) {
		ResultSet rs = plugin.getSqlManager()
				.executeQuery("SELECT end FROM mutes WHERE uuid='" + uuid.toString() + "'");
		try {
			if (rs.next())
				return rs.getLong("end");
		} catch (Exception e) {
		}
		return 0L;
	}

	public void ban(UUID banned, String reason, Long time, UUID banner) {
		plugin.getSqlManager().execute("REPLACE INTO bans (uuid, reason, end, banner) VALUES ('" + banned.toString()
				+ "', '" + reason + "', " + time + ", '" + banner.toString() + "')");
	}

	public void unban(UUID banned) {
		plugin.getSqlManager().execute("DELETE FROM bans WHERE uuid='" + banned.toString() + "'");
	}

	public void mute(UUID muted, Long time, UUID muter) {
		plugin.getSqlManager().execute("REPLACE INTO mutes (uuid, end, muter) VALUES ('" + muted.toString() + "', "
				+ time + ", '" + muter.toString() + "')");
	}

	public void unmute(UUID muted) {
		plugin.getSqlManager().execute("DELETE FROM mutes WHERE uuid='" + muted.toString() + "'");
	}
}
