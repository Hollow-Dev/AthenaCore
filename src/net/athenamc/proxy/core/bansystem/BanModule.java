package net.athenamc.proxy.core.bansystem;

import net.athenamc.proxy.core.BungeeCore;
import net.athenamc.proxy.core.bansystem.commands.BanCommand;
import net.athenamc.proxy.core.bansystem.commands.CheckCommand;
import net.athenamc.proxy.core.bansystem.commands.KickCommand;
import net.athenamc.proxy.core.bansystem.commands.MuteCommand;
import net.athenamc.proxy.core.bansystem.commands.ReportCommand;
import net.athenamc.proxy.core.bansystem.commands.TempBanCommand;
import net.athenamc.proxy.core.bansystem.commands.UnbanCommand;
import net.athenamc.proxy.core.bansystem.commands.UnmuteCommand;
import net.athenamc.proxy.core.bansystem.commands.WarnCommand;
import net.athenamc.proxy.core.listeners.JoinListener;

public class BanModule {
	public BanModule(BungeeCore plugin) {
		plugin.getProxy().getPluginManager().registerCommand(plugin, new BanCommand(plugin));
		plugin.getProxy().getPluginManager().registerCommand(plugin, new CheckCommand(plugin));
		plugin.getProxy().getPluginManager().registerCommand(plugin, new KickCommand(plugin));
		plugin.getProxy().getPluginManager().registerCommand(plugin, new MuteCommand(plugin));
		plugin.getProxy().getPluginManager().registerCommand(plugin, new ReportCommand(plugin));
		plugin.getProxy().getPluginManager().registerCommand(plugin, new TempBanCommand(plugin));
		plugin.getProxy().getPluginManager().registerCommand(plugin, new UnbanCommand(plugin));
		plugin.getProxy().getPluginManager().registerCommand(plugin, new UnmuteCommand(plugin));
		plugin.getProxy().getPluginManager().registerCommand(plugin, new WarnCommand(plugin));

		plugin.getProxy().getPluginManager().registerListener(plugin, new JoinListener(plugin));
	}
}
