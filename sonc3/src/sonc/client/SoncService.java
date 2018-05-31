package sonc.client;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import sonc.shared.Movie;
import sonc.shared.SoncException;

@RemoteServiceRelativePath("sonc3") 
public interface SoncService extends RemoteService {

	/**
	 * Register a player with given nick and password. Changes are stored in
	 * serialization file
	 * 
	 * @param userId of user
	 * @param password of user
	 * @return true if registered and false otherwise
	 * @throws SoncException if I/O error occurs when serializing data
	 */
	boolean register(String userId, String password) throws SoncException;

	/**
	 * Change password if old password matches the current one
	 * 
	 * @param nick of user
	 * @param oldPassword for authentication before update
	 * @param newPassword after update
	 * @return true if password changed and false otherwise
	 * @throws SoncException if I/O error occurs when serializing data
	 */
	boolean updatePassword(java.lang.String nick, String oldPassword, String newPassword)
			throws SoncException;

	/**
	 * Authenticate user given id and password
	 * 
	 * @param nick of user to authenticate
	 * @param password of user to authenticate
	 * @return true if authenticated and false otherwise
	 */
	boolean authenticate(String nick, String password);

	/**
	 * Return last submitted code by the authenticated used
	 * 
	 * @param nick of player
	 * @param password of player
	 * @return code of player's ship
	 * @throws SoncException if nick is unknown or password invalid
	 */
	String getCurrentCode(String nick, String password) throws SoncException;

	/**
	 * Set ship's code and try to instance it, for given user and from given
	 * code.
	 * 
	 * @param nick of player
	 * @param password of player
	 * @param code to compile and instantiate
	 * @throws SoncException if nick is unknown, password is invalid, code has
	 *             errors or an I/O error occurred
	 */
	void buildShip(java.lang.String nick, java.lang.String password, java.lang.String code)
			throws SoncException;

	/**
	 * Returns a sorted list of all registered players' nicks with ships.
	 * These nicks can be used in a simulation.
	 * 
	 * @return list of strings
	 */
	List<String> getPlayersNamesWithShips();

	/**
	 * Simulate a battle with ships of given players.
	 * Ships are shuffled in random order
	 * (using the java.util.Collections.shuffle() method)
	 * 
	 * @param nicks of players in this game
	 * @return movie with game
	 * @throws SoncException
	 */
	Movie battle(java.util.List<java.lang.String> nicks) throws SoncException;

	Movie testBattle() throws SoncException;

}