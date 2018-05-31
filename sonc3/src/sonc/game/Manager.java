package sonc.game;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import sonc.battle.Ship;
import sonc.battle.World;
import sonc.battle.bots.SittingDuckBot;
import sonc.battle.bots.StalkerBot;
import sonc.client.SoncService;
import sonc.shared.Movie;
import sonc.shared.SoncException;

/**
 * An instance of this class is responsible for managing a community of players
 * with several games taking place simultaneously. The methods of this class are
 * those needed by web client thus it follows the <b>Facade</b> design pattern.
 * It also follows the <b>singleton</b> design pattern to provide a single
 * instance of this class to the application.
 * 
 * @author Rodrigo Marques
 */
public class Manager implements Serializable, SoncService {

	private static final long serialVersionUID = 1L;

	private static File	   playersFile = null;
	private static Manager instance	   = null;

	private Players allPlayers = new Players();

	private Manager() {
	} // protects class from being instantiated

	/**
	 * Restores Manager instance from file through serialization
	 * 
	 * @throws SoncException if there's an error during deserialization
	 */
	private static void restore() throws SoncException {
		if (playersFile.canRead() && playersFile.length() != 0) {
			try (FileInputStream stream = new FileInputStream(playersFile);
					ObjectInputStream deserializer = new ObjectInputStream(stream);) {
				instance = (Manager) deserializer.readObject();
				deserializer.close();
				stream.close();
			} catch (IOException | ClassNotFoundException e) {
				throw new SoncException("error in file restore", e);
			}
		} else {
			instance = new Manager();
		}
	}

	/**
	 * Backs up Manager instance to file through serialization
	 * 
	 * @throws SoncException if there's an error during serialization
	 */
	private static void backup() throws SoncException {
		try (FileOutputStream stream = new FileOutputStream(playersFile);
				ObjectOutputStream serializer = new ObjectOutputStream(stream);) {
			serializer.writeObject(instance);
			serializer.close();
			stream.close();
		} catch (IOException e) {
			throw new SoncException("error in file backup", e);
		}
	}

	/**
	 * Returns the single instance of this class as proposed in the
	 * <b>singleton</b> design pattern. If a backup of this class is available
	 * then the manager is recreated from that data
	 * 
	 * @return instance of this class
	 * @throws SoncException if I/O error occurs reading serialisation
	 */
	public static Manager getInstance() throws SoncException {
		if (instance == null) {
			if (playersFile.exists()) {
				restore();
			} else {
				instance = new Manager();
				try {
					playersFile.createNewFile();
				} catch (IOException e) {
					throw new SoncException("Error creating players file", e);
				}
			}
		}
		return instance;
	}

	/**
	 * Name of file containing managers's data
	 * 
	 * @return the playersFile
	 */
	public static File getPlayersFile() {
		return playersFile;
	}

	/**
	 * Change pathname of file containing manager's data
	 * 
	 * @param playersFile the playersFile to set
	 */
	public static void setPlayersFile(File managerFile) {
		Manager.playersFile = managerFile;
	}

	/* (non-Javadoc)
	 * @see sonc.game.PlayerManager#register(java.lang.String, java.lang.String)
	 */
	@Override
	public boolean register(String userId, String password) throws SoncException {
		if (instance.allPlayers.register(userId, password)) {
			backup();
			return true;
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see sonc.game.PlayerManager#updatePassword(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public boolean updatePassword(java.lang.String nick, String oldPassword, String newPassword)
			throws SoncException {
		if (instance.allPlayers.updatePassword(nick, oldPassword, newPassword)) {
			backup();
			return true;
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see sonc.game.PlayerManager#authenticate(java.lang.String, java.lang.String)
	 */
	@Override
	public boolean authenticate(String nick, String password) {
		return instance.allPlayers.authenticate(nick, password);
	}

	/* (non-Javadoc)
	 * @see sonc.game.PlayerManager#getCurrentCode(java.lang.String, java.lang.String)
	 */
	@Override
	public String getCurrentCode(String nick, String password) throws SoncException {
		if (instance.allPlayers.getPlayer(nick) == null)
			throw new SoncException("User isn't registered");
		if (authenticate(nick, password) == false)
			throw new SoncException("Failed to authenticate");
		else
			return instance.allPlayers.getPlayer(nick).getCode();
	}

	/* (non-Javadoc)
	 * @see sonc.game.PlayerManager#buildShip(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void buildShip(java.lang.String nick, java.lang.String password, java.lang.String code)
			throws SoncException {
		Player player = instance.allPlayers.getPlayer(nick);

		if (player == null)
			throw new SoncException("User isn't registered");

		if (authenticate(nick, password) == false)
			throw new SoncException("Failed to authenticate");

		player.setCode(code);
		player.checkCode();
		player.instanceShip();
		backup();
	}

	/* (non-Javadoc)
	 * @see sonc.game.PlayerManager#getPlayersNamesWithShips()
	 */
	@Override
	public List<String> getPlayersNamesWithShips() {
		List<String> l = instance.allPlayers.getPlayersNamesWithShips();
		Collections.sort(l);
		return l;
	}

	/* (non-Javadoc)
	 * @see sonc.game.PlayerManager#battle(java.util.List)
	 */
	@Override
	public Movie battle(java.util.List<java.lang.String> nicks) throws SoncException {
		World world = new World();
		List<Ship> ships = new ArrayList<>();

		for (String name : nicks) {
			Player player = allPlayers.getPlayer(name);
			Ship ship = player.getShip();

			// Let's assume ships are compiled before battle is called
			if (player.getShip() != null)
				ships.add(ship);
			// else
			// ships.add(player.instanceShip());
		}

		Collections.shuffle(ships);
		return world.battle(ships);
	}

	/**
	 * Resets the singleton instance
	 * For use with JUnit tests
	 * 
	 * @param deleteFile whether to delete the file or not
	 */
	void reset(Boolean deleteFile) {
		instance = null;
		if (deleteFile)
			playersFile.delete();
	}

	@Override
	public Movie testBattle() throws SoncException {
		World world = new World();
		List<Ship> ships = new ArrayList<>();
		StalkerBot bot1 = new StalkerBot();
		SittingDuckBot bot2 = new SittingDuckBot();
		ships.add(bot1);
		ships.add(bot2);
		return world.battle(ships);
	}

}
