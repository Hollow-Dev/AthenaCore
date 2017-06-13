package net.athenamc.spigot.core.sqlmanager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetProvider;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import net.athenamc.core.sqlmanager.SQLManager;

public class SQLManagerBukkit implements SQLManager {
	private HikariDataSource source;
	private JavaPlugin plugin;

	public SQLManagerBukkit(JavaPlugin plugin, String database) throws Exception {
		this.plugin = plugin;
		
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
		new BukkitRunnable() {
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
		}.runTaskLaterAsynchronously(plugin, 1);
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
		new BukkitRunnable() {
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
		}.runTaskLaterAsynchronously(plugin, 1);
	}
}
