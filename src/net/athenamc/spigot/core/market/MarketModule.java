package net.athenamc.spigot.core.market;

import java.sql.ResultSet;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

import net.athenamc.core.currencies.CurrencyAPI.CurrencyType;
import net.athenamc.core.exceptions.PermissionRequiredException;
import net.athenamc.spigot.core.SpigotCore;

public class MarketModule implements CommandExecutor, Listener {
	private SpigotCore plugin;
	private Navigation nav;
	private War war;
	private Factions factions;
	private Gadgets gadgets;
	private HubGear hubGear;
	private LBW lbw;
	private Parkour parkour;
	private Particles particles;
	private Pets pets;
	private Prison prison;
	private Skyblock skyblock;
	private KitPvP kitpvp;

	public MarketModule(SpigotCore plugin) {
		this.plugin = plugin;
		plugin.getCommand("market").setExecutor(this);
		plugin.getServer().getPluginManager().registerEvents(this, plugin);

		nav = new Navigation(plugin);
		war = new War(plugin);
		factions = new Factions(plugin);
		gadgets = new Gadgets(plugin);
		hubGear = new HubGear(plugin);
		lbw = new LBW(plugin);
		parkour = new Parkour(plugin);
		particles = new Particles(plugin);
		pets = new Pets(plugin);
		prison = new Prison(plugin);
		skyblock = new Skyblock(plugin);
		kitpvp = new KitPvP(plugin);
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		try {
			if (sender instanceof Player) {
				Player player = (Player) sender;
				plugin.checkPerm(sender, "core.market");
				sender.sendMessage(ChatColor.GREEN + "Opening Server Shop!");
				if (args.length > 0) {
					String market = args[0];
					if (market.equalsIgnoreCase("war")) {
						war.openInventory(player);
					} else if (market.equalsIgnoreCase("factions")) {
						factions.openInventory(player);
					} else if (market.equalsIgnoreCase("parkour")) {
						parkour.openInventory(player);
					} else if (market.equalsIgnoreCase("skyblock")) {
						skyblock.openInventory(player);
					} else if (market.equalsIgnoreCase("lbw")) {
						lbw.openInventory(player);
					} else if (market.equalsIgnoreCase("prison")) {
						prison.openInventory(player);
					} else if (market.equalsIgnoreCase("kitpvp")) {
						kitpvp.openInventory(player);
					} else if (market.equalsIgnoreCase("pets")) {
						pets.openInventory(player);
					} else if (market.equalsIgnoreCase("hubgear")) {
						hubGear.openInventory(player);
					} else if (market.equalsIgnoreCase("particle")) {
						particles.openInventory(player);
					} else if (market.equalsIgnoreCase("gadgets")) {
						gadgets.openInventory(player);
					} else {
						nav.openInventory(player);
					}
					return true;
				}
				nav.openInventory(player);
			} else
				return false;
		} catch (PermissionRequiredException e) {
			sender.sendMessage(
					ChatColor.RED + "You must have the permission " + e.getPermission() + " to perform that command");
		}
		return true;
	}

	@EventHandler
	private void inventoryClickEvent(InventoryClickEvent e) {
		if (e.getCurrentItem() != null && e.getCurrentItem().getType() == Material.IRON_FENCE) {
			if (e.getCurrentItem().hasItemMeta() && e.getCurrentItem().getItemMeta().hasDisplayName()) {
				if (e.getCurrentItem().getItemMeta().getDisplayName()
						.equalsIgnoreCase(ChatColor.RED + ChatColor.BOLD.toString() + "Back to menu")) {
					e.setCancelled(true);
					nav.openInventory((Player) e.getWhoClicked());
				}
			}
		} else if (e.getClickedInventory().getName().equalsIgnoreCase(ChatColor.GREEN + "Market place")) {
			e.setCancelled(true);
			if (e.getCurrentItem() != null && e.getCurrentItem().hasItemMeta()) {
				String itemName = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());
				if (itemName.equalsIgnoreCase("war")) {
					war.openInventory((Player) e.getWhoClicked());
				} else if (itemName.equalsIgnoreCase("factions")) {
					factions.openInventory((Player) e.getWhoClicked());
				} else if (itemName.equalsIgnoreCase("parkour")) {
					parkour.openInventory((Player) e.getWhoClicked());
				} else if (itemName.equalsIgnoreCase("skyblock")) {
					skyblock.openInventory((Player) e.getWhoClicked());
				} else if (itemName.equalsIgnoreCase("luckyblockwars")) {
					lbw.openInventory((Player) e.getWhoClicked());
				} else if (itemName.equalsIgnoreCase("prison")) {
					prison.openInventory((Player) e.getWhoClicked());
				} else if (itemName.equalsIgnoreCase("kitpvp")) {
					kitpvp.openInventory((Player) e.getWhoClicked());
				} else if (itemName.equalsIgnoreCase("pets")) {
					pets.openInventory((Player) e.getWhoClicked());
				} else if (itemName.equalsIgnoreCase("hub gear")) {
					hubGear.openInventory((Player) e.getWhoClicked());
				} else if (itemName.equalsIgnoreCase("particles")) {
					particles.openInventory((Player) e.getWhoClicked());
				} else if (itemName.equalsIgnoreCase("gadgets")) {
					gadgets.openInventory((Player) e.getWhoClicked());
				}
			}
		} else if (e.getClickedInventory().getName()
				.equalsIgnoreCase(ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "KitPvP")) {
			e.setCancelled(true);
			if (e.getCurrentItem() != null && e.getCurrentItem().hasItemMeta()) {
				String itemName = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());
				try {
					ResultSet rs = plugin.getSqlManager()
							.executeQuery("SELECT time FROM kitpvp.player_kits WHERE uuid='"
									+ e.getWhoClicked().getUniqueId() + "' AND kit='" + itemName + "'");
					if (rs.next()) {
						if (rs.getLong("time") == -1) {
							e.getWhoClicked().sendMessage(ChatColor.RED + "You already own that kit permanently");
							return;
						} else if (rs.getLong("time") >= System.currentTimeMillis() && !e.isLeftClick()) {
							e.getWhoClicked().sendMessage(ChatColor.RED
									+ "You're already renting this kit wait for the rent to expire or purchase it permanently");
							return;
						}
					}
				} catch (Exception ex) {
				}
				int price;
				if (e.isLeftClick()) {
					price = Integer.parseInt(e.getCurrentItem().getItemMeta().getLore().get(3)
							.replace(ChatColor.RED + "Purchase: ", "").replace(" TICKETS", ""));
					if (plugin.getCurrencyApi().canAfford(e.getWhoClicked().getUniqueId(), price, CurrencyType.TICKETS)) {
						plugin.getCurrencyApi()
								.setCurrency(e.getWhoClicked().getUniqueId(), plugin.getCurrencyApi()
										.getCurrency(e.getWhoClicked().getUniqueId(), CurrencyType.TICKETS) - price,
										CurrencyType.TICKETS);
						if (!plugin.getBungeeName().equalsIgnoreCase("KitPvP"))
							plugin.getSqlManager().execute("REPLACE INTO kitpvp.player_kits (uuid, kit, time) VALUES ('"
									+ e.getWhoClicked().getUniqueId() + "', '" + itemName + "', '-1')");
						else
							plugin.getServer().dispatchCommand(Bukkit.getConsoleSender(),
									"givekit " + e.getWhoClicked().getName() + " " + itemName + " -1");
						e.getWhoClicked().sendMessage(ChatColor.GREEN + "You have purchased the " + itemName + " kit");
					} else {
						e.getWhoClicked().sendMessage(ChatColor.RED + "You cannot afford this kit");
					}
				} else {
					price = Integer.parseInt(e.getCurrentItem().getItemMeta().getLore().get(4)
							.replace(ChatColor.RED + "Rent: ", "").replace(" GEMS", ""));
					if (plugin.getCurrencyApi().canAfford(e.getWhoClicked().getUniqueId(), price, CurrencyType.GEMS)) {
						plugin.getCurrencyApi()
								.setCurrency(e.getWhoClicked().getUniqueId(), plugin.getCurrencyApi()
										.getCurrency(e.getWhoClicked().getUniqueId(), CurrencyType.GEMS) - price,
										CurrencyType.GEMS);
						if (!plugin.getBungeeName().equalsIgnoreCase("KitPvP"))
							plugin.getSqlManager()
									.execute("REPLACE INTO kitpvp.player_kits (uuid, kit, time) VALUES ('"
											+ e.getWhoClicked().getUniqueId() + "', '" + itemName + "', '"
											+ (System.currentTimeMillis() + 604800000) + "')");
						else
							plugin.getServer().dispatchCommand(Bukkit.getConsoleSender(),
									"givekit " + e.getWhoClicked().getName() + " " + itemName + " 604800000");
						e.getWhoClicked().sendMessage(ChatColor.GREEN + "You have rented the " + itemName
								+ " kit, you can now use it for 30 days");
					} else {
						e.getWhoClicked().sendMessage(ChatColor.RED + "You cannot afford this kit");
					}
				}
			}
		}
	}
}
