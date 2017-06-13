package net.athenamc.core.ranks;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

import net.athenamc.core.Core;
import net.athenamc.core.exceptions.RankAlreadyExistsException;
import net.athenamc.core.exceptions.RankDoesNotExistException;
import net.athenamc.proxy.core.ranks.BungeeRank;
import net.md_5.bungee.api.ChatColor;

public class RankAPI {
	protected HashSet<Rank> ranks;
	private HashMap<UUID, Rank> playerRank;

	protected Core plugin;

	public RankAPI(Core plugin) {
		this.plugin = plugin;

		playerRank = new HashMap<UUID, Rank>();

		reloadRanks();
	}

	public void reloadRanks() {
		ranks = new HashSet<Rank>();
		ResultSet rs = plugin.getSqlManager().executeQuery("SELECT * FROM ranks");
		try {
			Rank rank;
			while (rs.next()) {
				rank = new BungeeRank(ChatColor.translateAlternateColorCodes('&', rs.getString("chatformat")),
						ChatColor.translateAlternateColorCodes('&', rs.getString("prefix")), rs.getInt("weight"),
						rs.getString("name"));
				ranks.add(rank);
			}
		} catch (

		Exception e) {
			e.printStackTrace();
		}
	}

	public Rank getRank(UUID uuid) {
		if (playerRank.containsKey(uuid)) {
			Rank rank = playerRank.get(uuid);
			if (ranks.contains(rank))
				return rank;
		} else {
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
		}
		try {
			Rank defaultRank = getRank("member");
			playerRank.put(uuid, defaultRank);
			return defaultRank;
		} catch (RankDoesNotExistException e) {
			return null;
		}
	}

	public Rank getRank(String rankName) throws RankDoesNotExistException {
		for (Rank rank : ranks)
			if (rank.getName().equalsIgnoreCase(rankName))
				return rank;
		throw new RankDoesNotExistException(rankName);
	}

	public void deleteRank(String rankName) throws RankDoesNotExistException {
		if (!rankName.equalsIgnoreCase("member"))
			ranks.remove(getRank(rankName));
	}

	public void createRank(String name) throws RankAlreadyExistsException {
		name = ChatColor.stripColor(name);
		try {
			Rank rank = getRank(name);
			throw new RankAlreadyExistsException(rank);
		} catch (RankDoesNotExistException e) {
			ranks.add(new BungeeRank(name));
			plugin.getSqlManager()
					.execute("INSERT INTO ranks (name) VALUES ('" + name + "') ON DUPLICATE KEY UPDATE name=name");
		}
	}

	public void setRank(UUID uuid, String rankName) throws RankDoesNotExistException {
		if (playerRank.containsKey(uuid))
			playerRank.put(uuid, getRank(rankName));
		plugin.getSqlManager().execute("INSERT INTO player_ranks(uuid, rank) VALUES ('" + uuid.toString() + "', '"
				+ rankName + "') ON DUPLICATE KEY UPDATE rank='" + rankName + "'");
	}

	public void loadRank(UUID uuid) {
		try {
			ResultSet rs = plugin.getSqlManager()
					.executeQuery("SELECT rank FROM player_ranks WHERE uuid='" + uuid + "'");
			while (rs.next())
				playerRank.put(uuid, getRank(rs.getString("rank")));
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (RankDoesNotExistException e) {
		}
		Rank rank = plugin.getRankApi().getRank(uuid);
	}

	public void unload(UUID uuid) {
		playerRank.remove(uuid);
	}

	public HashSet<Rank> getRanks() {
		return ranks;
	}

	public Rank getRankLower(Rank rank) {
		ArrayList<Rank> sortedRanks = new ArrayList<Rank>();
		sortedRanks.addAll(ranks);
		Collections.sort(sortedRanks);
		Collections.reverse(sortedRanks);

		for (Rank lower : sortedRanks)
			if (lower.getPower() < rank.getPower())
				return lower;
		return null;
	}
}