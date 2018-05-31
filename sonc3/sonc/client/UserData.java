package sonc.client;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Information about the current logged in user.
 */
public class UserData implements IsSerializable {
	String username;
	String password;

	public void setUsername(String username) {
		this.username = username;
	}

	public String getUsername() {
		return this.username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPassword() {
		return this.password;
	}

}