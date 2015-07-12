package ca.PvPCraft.PvPAPI.utilities.MySQL.delegates;

/**
 * @author Evan Lindsay
 * @date 8/23/13
 * @time 2:18 AM
 */
public interface HostnameDatabase {
	String getHostname();

	void setHostname(String hostname);

	int getPort();

	void setPort(int port);

	String getUsername();

	void setUsername(String username);

	String getPassword();

	void setPassword(String password);

	String getDatabase();

	void setDatabase(String database);
}
