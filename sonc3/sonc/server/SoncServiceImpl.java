package sonc.server;

import sonc.client.SoncService;
import sonc.game.Manager;
import sonc.shared.FieldVerifier;
import sonc.shared.Movie;
import sonc.shared.SoncException;
import sonc.utils.AgentBuilder;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server-side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class SoncServiceImpl extends RemoteServiceServlet implements SoncService {

	private static Manager instance = null;
	
	static {
		try {
			Manager.setPlayersFile(new File("sonc3/players.ser"));
			instance = Manager.getInstance();
		} catch (SoncException e) {
			System.err.print(e.toString());
		}
		AgentBuilder.addToClassPath(Paths.get(System.getProperty("user.dir"), "WEB-INF", "classes").toString());
	}
	
	@Override
	public boolean register(String userId, String password) throws SoncException {
		if (!FieldVerifier.isValidName(userId))
			return false;
		userId = FieldVerifier.escapeHtml(userId);
		password = FieldVerifier.escapeHtml(password);
		return instance.register(userId, password);
	}

	@Override
	public boolean updatePassword(String nick, String oldPassword, String newPassword)
			throws SoncException {
		nick = FieldVerifier.escapeHtml(nick);
		oldPassword = FieldVerifier.escapeHtml(oldPassword);
		newPassword = FieldVerifier.escapeHtml(newPassword);
		return instance.updatePassword(nick, oldPassword, newPassword);
	}

	@Override
	public boolean authenticate(String nick, String password) {
		nick = FieldVerifier.escapeHtml(nick);
		password = FieldVerifier.escapeHtml(password);
		return instance.authenticate(nick, password);
	}

	@Override
	public String getCurrentCode(String nick, String password) throws SoncException {
		nick = FieldVerifier.escapeHtml(nick);
		password = FieldVerifier.escapeHtml(password);
		return instance.getCurrentCode(nick, password);
	}

	@Override
	public void buildShip(String nick, String password, String code) throws SoncException {
		nick = FieldVerifier.escapeHtml(nick);
		password = FieldVerifier.escapeHtml(password);
		instance.buildShip(nick, password, code);
	}

	@Override
	public List<String> getPlayersNamesWithShips() {
		return instance.getPlayersNamesWithShips();
	}

	@Override
	public Movie battle(List<String> nicks) throws SoncException {
		return instance.battle(nicks);
	}

}
