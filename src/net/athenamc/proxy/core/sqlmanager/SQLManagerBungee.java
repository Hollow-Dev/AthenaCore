package net.athenamc.proxy.core.sqlmanager;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.TimeUnit;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetProvider;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import net.athenamc.core.sqlmanager.SQLManager;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.YamlConfiguration;

public class SQLManagerBungee implements SQLManager {
	private HikariDataSource source;
	private Plugin plugin;

	private String database;

	public SQLManagerBungee(Plugin plugin, String database) {
		this.database = database;
		this.plugin = plugin;
		try {
			HikariConfig config = new HikariConfig();
			config.setJdbcUrl("jdbc:mysql://localhost:3306/" + database);
			config.setUsername("root");
			config.setPassword("lv4bMuGwTpQ3KXFmSOlvzS1tDwdud5H6O");
			config.addDataSourceProperty("connectionTimeout", "15000");
			config.addDataSourceProperty("cachePrepStmts", "true");
			config.addDataSourceProperty("prepStmtCacheSize", "250");
			config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
			config.addDataSourceProperty("useServerPrepStmts", "true");
			source = new HikariDataSource(config);
		} catch (Exception e) {
			plugin.getLogger().severe("The database could not connect, therefore the plugin will disable itself");
		}
	}

	public void stop() {
		if (source != null) {
			try {
				source.close();
				source = null;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void execute(final String sql) {
		plugin.getProxy().getScheduler().schedule(plugin, new Runnable() {
			public void run() {
				String s = sql;
				Statement statement = null;
				Connection conn = null;
				if (source == null) {
					System.out.println("Failed attempt to execute " + sql);
					return;
				}
				try {
					conn = source.getConnection();
					try {
						statement = conn.createStatement();
						statement.execute(s);
					} catch (Exception e) {
						System.out.println("Error: " + sql);
						e.printStackTrace();
					} finally {
						if (statement != null)
							statement.close();
					}
				} catch (SQLException e) {
					System.out.println("Error: " + sql);
					e.printStackTrace();
				} finally {
					try {
						if (conn != null)
							conn.close();
					} catch (Exception e) {
						System.out.println("Error: " + sql);
						e.printStackTrace();
					}
				}
				statement = null;
				conn = null;
			}
		}, 1, TimeUnit.MILLISECONDS);
	}

	public CachedRowSet executeQuery(String sql) {
		Statement statement = null;
		ResultSet rs = null;
		CachedRowSet crs = null;
		Connection conn = null;
		if (source == null) {
			System.out.println("Failed attempt to execute " + sql);
			return null;
		}
		try {
			conn = source.getConnection();
			try {
				statement = conn.createStatement();
				try {
					rs = statement.executeQuery(sql);
					crs = RowSetProvider.newFactory().createCachedRowSet();
					crs.populate(rs);
				} catch (Exception e) {
					System.out.println("Error: " + sql);
					e.printStackTrace();
				} finally {
					if (rs != null)
						rs.close();
				}
			} catch (Exception e) {
				System.out.println("Error: " + sql);
				e.printStackTrace();
			} finally {
				if (statement != null)
					statement.close();
			}
		} catch (SQLException e) {
			System.out.println("Error: " + sql);
			e.printStackTrace();
		} finally {
			try {
				if (conn != null)
					conn.close();
			} catch (Exception e) {
				System.out.println("Error: " + sql);
				e.printStackTrace();
			}
		}
		statement = null;
		rs = null;
		conn = null;
		return crs;
	}

	public void executePrepared(final String sql, final boolean forced, final String... values) {
		plugin.getProxy().getScheduler().schedule(plugin, new Runnable() {
			public void run() {
				String s = sql;
				PreparedStatement ps = null;
				Connection conn = null;
				if (source == null) {
					System.out.println("Failed attempt to execute " + sql);
					return;
				}
				try {
					conn = source.getConnection();
					try {
						ps = conn.prepareStatement(s);
						for (int k = 0; k < values.length; k++)
							ps.setString(k + 1, values[k]);
						ps.executeUpdate();
					} catch (Exception e) {
						System.out.println("Error: " + sql);
						e.printStackTrace();
					} finally {
						if (ps != null)
							ps.close();
					}
				} catch (SQLException e) {
					System.out.println("Error: " + sql);
					e.printStackTrace();
				} finally {
					try {
						if (conn != null)
							conn.close();
					} catch (Exception e) {
						System.out.println("Error: " + sql);
						e.printStackTrace();
					}
				}
				ps = null;
				conn = null;
			}
		}, 1, TimeUnit.MILLISECONDS);
	}
}
