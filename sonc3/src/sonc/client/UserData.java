package sonc.client;

/**
 * Information about the current logged in user.
 */
public class UserData {
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