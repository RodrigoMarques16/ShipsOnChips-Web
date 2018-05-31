package sonc.game;

import java.io.IOException;
import java.io.Serializable;

import javax.naming.NameNotFoundException;

import com.google.gwt.user.client.rpc.IsSerializable;

import sonc.battle.Ship;
import sonc.shared.SoncException;
import sonc.utils.AgentBuilder;

/**
 * A player of the SonC game. An instance of this class records the player's
 * authentication and the last code submitted.
 * 
 * @author Rodrigo Marques
 */
public class Player implements Serializable, IsSerializable {

	private static final long serialVersionUID = 1L;

	private String		   code;
	private String		   nick;
	private String		   password;
	transient private Ship ship	= null;

	Player(String nick, String password) {
		this.nick = nick;
		this.password = password;
	}

	String getNick() {
		return nick;
	}

	void setNick(String nick) {
		this.nick = nick;
	}

	String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * Get the latest version of this player's code
	 * 
	 * @return code of this player
	 */
	String getCode() {
		return code;
	}

	/**
	 * Set the latest version of this player's code
	 * 
	 * @param code of this player
	 */
	void setCode(String code) {
		this.code = code;
	}

	/**
	 * Try to compile and instance the submitted code and report errors. It uses
	 * the AgentBuilder class.
	 * 
	 * @throws SoncException
	 */
	void checkCode() throws SoncException {
		AgentBuilder builder;
		if (code == null)
			throw new SoncException("No code");
		try {
			builder = new AgentBuilder();
		} catch (IOException e) {
			throw new SoncException(e);
		}
		try {
			@SuppressWarnings("unused")
			Ship playerShip = builder.getInstance(Ship.class, code, nick);
		} catch (InstantiationException | IllegalAccessException | NameNotFoundException
				| IOException e) {
			throw new SoncException(e);
		}
	}

	/**
	 * Make an instance of Ship after compiling and instancing the submitted
	 * code. This instance is stored in this class
	 * 
	 * @return instance ship or null if exceptions occurred when compiling the
	 *         code or instancing the class
	 */
	Ship instanceShip() {
		AgentBuilder builder;
		if (code == null)
			return null;
		try {
			builder = new AgentBuilder();
		} catch (IOException e) {
			return null;
		}
		try {
			ship = builder.getInstance(Ship.class, code, nick);
			return ship;
		} catch (InstantiationException | IllegalAccessException | NameNotFoundException
				| IOException e) {
			return null;
		}
	}

	/**
	 * Get the latest instance of the player's ship
	 * 
	 * @return instance ship or null if it's not yet instantiated
	 */
	Ship getShip() {
		return ship;
	}

	/**
	 * Check the authentication of this player
	 * 
	 * @param password for checking
	 * @return true password is the expected, false otherwise
	 */
	boolean authenticate(String password) {
		if (password == this.password)
			return true;
		return false;
	}

}
