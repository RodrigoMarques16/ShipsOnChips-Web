package sonc.server;

import sonc.client.SoncService;
import sonc.game.Manager;
import sonc.shared.FieldVerifier;
import sonc.shared.Movie;
import sonc.shared.SoncException;

import java.io.File;
import java.util.List;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server-side implementation of the RPC service.
 * 
 * -///////::::::::::::::///////:::::////////+++++++/
://////////////+ossyyhhhhyyyyyysso+//+++++ooooooo+
:///////////osyyyyyyyyyyyyyyyyyyyyyyso++++ooooooo+
:////////+syyyyyyyyyyyyyyyyyyyyyyyyyyyysooooooooo+
:++++++oyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyysoooooo+
:+++++syyyyyyyyyyyyyyyyyyyyyyyyso+////+oyyyyssssso
/+++oyyyyyyyyyyyyyyssosyyy:.`           `:syysssso
/oooyyyyso++/:--.`    /yyy:               `oyyssso
/ssyyyo              `yyyyo     -+oss+.    `syysso
/ssyyyy`             +yyyyy`    -yyyys+     ssssso
+syyyyy+:/+oosy.    -yyyyss:     sso+/`    .ssssso
+syyyyyyyyyyyy/     osssssso              .ossssso
+sssssssssssso     :ssssssss.           -/ssssssso
+ssssssssssss-    `sssssssss/     -//+osssssssssso
+sssssssssss+     /ssssssssso     :sssssssssssssyo
+sssssssssss`    .sssssssssss.    .sssssssssssssys
+ssssssssss-     +++/::-.osss/     osssssssssssyys
+yysssssss+              :ssso     :oooooooooosyys
+yyyysssss`             `-oooo//++ooo++++++++syyys
+yyyyysss/   `..-:://++ooo+++++++++++++++++oyyyyys
+yyyyyyyoo+ooo+++++++++++++++++++++++++//+syyyyyys
+yyyyyyyyso++++++++++++++++////////////+syyyyyyyys
+yyyyyyyyyyyso++////////////////////+oyyyyyyyyyyys
+yyyyyyyyyyyyyysso+////////////++osyyyyyyyyyyyyyys
+sssssssssyyyyyyyyyyyssssssssyyyyyyyyyyyyyyyyyyyyo
 * 
 */
@SuppressWarnings("serial")
public class SoncServiceImpl extends RemoteServiceServlet implements SoncService {

	private static Manager instance = null;
	
	static {
		try {
			Manager.setPlayersFile(new File("sonc3/players.ser"));
			instance = Manager.getInstance();
		} catch (SoncException e) {
		}
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
