package sonc.game;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import sonc.shared.SoncException;

import java.util.HashMap;

/**
 * A collection of players, persisted on file. Contains methods for
 * registration, authentication and retrieving players and their names.
 */
public class Players implements Serializable {

	private static final long serialVersionUID = 1L;
	Map<String, Player>		  players		   = new HashMap<>();

	/**
	 * Register a player with given nick and password
	 * 
	 * @param nick of user
	 * @param password of user
	 * @return true if registered and false otherwise
	 */
	boolean register(String nick, String password) {
		if (isNickValid(nick)) {
			players.put(nick, new Player(nick, password));
			return true;
		}
		return false;
	}

	boolean isNickValid(String nick) {
		if (nick.contains(" ") || (players.get(nick) != null))
			return false;
		return true;
	}

	/**
	 * Change password if old password matches current one
	 * 
	 * @param nick of user
	 * @param oldPassword for authentication before update
	 * @param newPassword after update
	 * @return true if password changed and false otherwise
	 */
	boolean updatePassword(String nick, String oldPassword, String newPassword) {
		Player player = players.get(nick);
		if (player != null && oldPassword.equals(player.getPassword())) {
			player.setPassword(newPassword);
			players.put(nick, player);
			return true;
		}
		return false;
	}

	/**
	 * Authenticate user given id and password
	 * 
	 * @param nick of user to authenticate
	 * @param password of user to authenticate
	 * @return true if authenticated and false otherwise
	 */
	boolean authenticate(String nick, String password) {
		Player player = players.get(nick);
		if (player == null)
			return false;
		return password.equals(player.getPassword());
	}

	/**
	 * Get's a player given id
	 * 
	 * @param name of user
	 * @return instance of player
	 * @throws SoncException if user doesn't exist
	 */
	Player getPlayer(String name) {
		return players.get(name);
	}

	List<String> getPlayersNamesWithShips() {
		List<String> plrs = new ArrayList<String>();
		players.forEach((nick, player) -> {
			if (player.getCode() != null)
				plrs.add(nick);
		});
		return plrs;
	}

}
