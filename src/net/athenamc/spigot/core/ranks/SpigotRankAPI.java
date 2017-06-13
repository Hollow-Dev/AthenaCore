package net.athenamc.spigot.core.ranks;

import java.sql.ResultSet;
import java.util.HashSet;
import java.util.UUID;

import net.athenamc.core.exceptions.RankAlreadyExistsException;
import net.athenamc.core.exceptions.RankDoesNotExistException;
import net.athenamc.core.ranks.Rank;
import net.athenamc.core.ranks.RankAPI;
import net.athenamc.spigot.core.SpigotCore;
import net.md_5.bungee.api.ChatColor;

public class SpigotRankAPI extends RankAPI {
	public SpigotRankAPI(SpigotCore plugin) {
		super(plugin);
	}

	@Override
	public void reloadRanks() {
		ranks = new HashSet<Rank>();
		ResultSet rs = plugin.getSqlManager().executeQuery("SELECT * FROM ranks");
		try {
			Rank rank;
			while (rs.next()) {

				rank = new SpigotRank(ChatColor.translateAlternateColorCodes('&', rs.getString("chatformat")),
						ChatColor.translateAlternateColorCodes('&', rs.getString("prefix")), rs.getInt("weight"),
						rs.getString("name"));
				ranks.add(rank);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void createRank(String name) throws RankAlreadyExistsException {
		name = ChatColor.stripColor(name);
		try {
			Rank rank = getRank(name);
			throw new RankAlreadyExistsException(rank);
		} catch (RankDoesNotExistException e) {
			ranks.add(new SpigotRank(name));
			plugin.getSqlManager().execute("REPLACE INTO ranks (name) VALUES ('" + name + "')");
		}
	}

	@Override
	public void loadRank(UUID uuid) {
		super.loadRank(uuid);
		SpigotRank rank = (SpigotRank) plugin.getRankApi().getRank(uuid);
		rank.addPlayerToTeam(uuid);
	}
}
