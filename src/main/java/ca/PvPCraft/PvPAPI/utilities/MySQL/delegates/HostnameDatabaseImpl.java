package ca.PvPCraft.PvPAPI.utilities.MySQL.delegates;

/**
 * @author Evan Lindsay
 * @date 8/23/13
 * @time 2:20 AM
 */
public class HostnameDatabaseImpl implements HostnameDatabase {
	private String hostname = "localhost";
	private int port = 0;
	private String username  = "minecraft";
	private String password = "";
	private String database = "minecraft";

	@Override
	public String getHostname() {
		return hostname;
	}

	@Override
	public void setHostname(String hostname) {
		if (hostname == null || hostname.length() == 0) {
			// TODO DatabaseException
		}
		this.hostname = hostname;
	}

	@Override
	public int getPort() {
		return port;
	}

	@Override
	public void setPort(int port) {
		if (port < 0 || 65535 < port) {
			// TODO DatabaseException
		}
		this.port = port;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public void setUsername(String username) {
		if (username == null | username.length() == 0) {
			// TODO DatabaseException
		}
		this.username = username;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public void setPassword(String password) {
		if (password == null || password.length() == 0) {
			// TODO DatabaseException
		}
		this.password = password;
	}

	@Override
	public String getDatabase() {
		return database;
	}

	@Override
	public void setDatabase(String database) {
		if (database == null || database.length() == 0) {
			// TODO DatabaseException
		}
		this.database = database;
	}
}
