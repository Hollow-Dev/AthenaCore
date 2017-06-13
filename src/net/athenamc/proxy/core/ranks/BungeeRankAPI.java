package net.athenamc.proxy.core.ranks;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import net.athenamc.core.Core;
import net.athenamc.core.exceptions.RankDoesNotExistException;
import net.athenamc.core.ranks.Rank;
import net.athenamc.core.ranks.RankAPI;

public class BungeeRankAPI extends RankAPI {
	public BungeeRankAPI(Core plugin) {
		super(plugin);
	}

	@Override
	public Rank getRank(UUID uuid) {
		ResultSet rs = plugin.getSqlManager()
				.executeQuery("SELECT rank FROM player_ranks WHERE uuid='" + uuid.toString() + "'");
		try {
			if (rs.next())
				return getRank(rs.getString("rank"));
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (RankDoesNotExistException e) {
			e.printStackTrace();
		}
		try	{
			Rank defaultRank = getRank("member");
			return defaultRank;
		} catch (RankDoesNotExistException e) {
			return null;
		}
	}
}
