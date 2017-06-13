package net.athenamc.spigot.core.ranks;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.Team;

import lombok.Getter;
import net.athenamc.core.Core;
import net.athenamc.core.ranks.Rank;
import net.athenamc.spigot.core.SpigotCore;

public class SpigotRank extends Rank {
	@Getter
	private Team team;

	public SpigotRank(String chatFormat, String prefix, int power, String rank) {
		super(chatFormat, prefix, power, rank);
		createTeam();
	}

	public SpigotRank(String name) {
		super(name);
		createTeam();
	}

	private void createTeam() {
		team = Bukkit.getScoreboardManager().getMainScoreboard().getTeam(getName());
		if (team == null)
			team = Bukkit.getScoreboardManager().getMainScoreboard().registerNewTeam(getName());
		if (getName().equalsIgnoreCase("Lord")) {
			team.setPrefix(ChatColor.GOLD + ChatColor.BOLD.toString() + "Lord " + ChatColor.WHITE);
		} else if (getName().equalsIgnoreCase("SystemAdmin")) {
			team.setPrefix(ChatColor.DARK_GREEN + ChatColor.BOLD.toString() + "S-Admin " + ChatColor.WHITE);
		} else {
			String prefix = getPrefix();
			if (prefix.length() > 13)
				prefix = prefix.substring(0, 13);
			team.setPrefix(prefix + " " + ChatColor.WHITE);
		}
		team.setSuffix("");
	}
	
	@Override
	public void setName(String name) {
		super.setName(name);
		team.setPrefix(ChatColor.YELLOW + name + " " + ChatColor.WHITE);
	}

	public void addPlayerToTeam(UUID uuid) {
		team.addEntry(Bukkit.getOfflinePlayer(uuid).getName());
	}

	@Override
	public Core loadPlugin() {
		return (SpigotCore) Bukkit.getPluginManager().getPlugin("Core");
	}
}
