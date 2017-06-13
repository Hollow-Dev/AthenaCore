package net.athenamc.core.sqlmanager;

import javax.sql.rowset.CachedRowSet;

public interface SQLManager {
	public void stop();
	public void execute(String sql);
	public CachedRowSet executeQuery(String sql);
	public void executePrepared(String sql, boolean forced, String... values);
}
