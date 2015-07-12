package ca.PvPCraft.PvPAPI.utilities.MySQL;

import com.jolbox.bonecp.BoneCPConfig;

import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import ca.PvPCraft.PvPAPI.utilities.MySQL.delegates.HostnameDatabase;
import ca.PvPCraft.PvPAPI.utilities.MySQL.delegates.HostnameDatabaseImpl;

/**
 * @author Evan Lindsay
 * @date 8/23/13
 * @time 2:09 AM
 */
public class MySQL extends Database {
	
	private HostnameDatabase delegate = new HostnameDatabaseImpl();

	public MySQL(Logger log, String prefix, String database,
	             String username, String password) {
		super(log, prefix, "[MySQL] ");
		setHostname("localhost");
		setPort(3306);
		setDatabase(database);
		setUsername(username);
		setPassword(password);
	}

	public MySQL(Logger log, String prefix, String hostname,
	             int port, String database, String username,
	             String password) {
		super(log, prefix, "[MySQL] ");
		setHostname(hostname);
		setPort(port);
		setDatabase(database);
		setUsername(username);
		setPassword(password);
	}

	public String getHostname() {
		return delegate.getHostname();
	}

	public void setHostname(String hostname) {
		delegate.setHostname(hostname);
	}

	public int getPort() {
		return delegate.getPort();
	}

	public void setPort(int port) {
		delegate.setPort(port);
	}

	public String getUsername() {
		return delegate.getUsername();
	}

	public void setUsername(String username) {
		delegate.setUsername(username);
	}

	public String getPassword() {
		return delegate.getPassword();
	}

	public void setPassword(String password) {
		delegate.setPassword(password);
	}

	public String getDatabase() {
		return delegate.getDatabase();
	}

	public void setDatabase(String database) {
		delegate.setDatabase(database);
	}

	@Override
	protected boolean initialize() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			return true;
		} catch (ClassNotFoundException e) {
			this.writeError("MySQL driver class missing: " + e.getMessage() + ".", true);
			return false;
		}
	}

	@Override
	public boolean open() {
		if (initialize()) {
			initConfig();
			BoneCPConfig config = getConfig();
			/*
			Properties properties = new Properties();
			properties.setProperty("zeroDateTimeBehavior", "convertToNull");
			properties.setProperty("username", getUsername());
			properties.setProperty("password", getPassword());
			properties.setProperty("jdbcUrl", url);
			properties.setProperty("idleConnectionTestPeriod", "60");
			properties.setProperty("idleMaxAge", "240");
			properties.setProperty("maxConnectionsPerPartition", "10");
			properties.setProperty("minConnectionsPerPartition", "1");
			properties.setProperty("partitionCount", "4");
			properties.setProperty("acquireIncremen", "5");
			properties.setProperty("statementsCacheSize", "100");
			properties.setProperty("releaseHelperThreads", "3");
			*/
			String url = "jdbc:mysql://" + getHostname() + ":" + getPort() + "/" + getDatabase();
			config.setJdbcUrl(url);
			config.setUsername(getUsername());
			config.setPassword(getPassword());
			config.setMinConnectionsPerPartition(1);
			config.setMaxConnectionsPerPartition(5);
			config.setPartitionCount(3);
			config.setStatementsCacheSize(0);
			config.setQueryExecuteTimeLimit(3, TimeUnit.SECONDS);
			config.setDefaultAutoCommit(true);
			config.setDefaultReadOnly(false);
			try {
				initCP(config);
				return true;
			} catch (SQLException e) {
				this.writeError("Not connected to the database: " + e.getMessage() + ".", true);
				return false;
			}
		} else {
			return false;
		}
	}
	
	
}
