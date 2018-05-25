package sonc.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.DeckLayoutPanel;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.RootLayoutPanel;

import sonc.client.ui.AuthView;
import sonc.client.ui.LoginView;

/**
 * Entry point for the SonC application
 * 
 * Wraps the application's pages with a DeckLayoutPanel.
 */
public class SoncUsers implements EntryPoint {

	private LayoutPanel		rootPanel;
	private DeckLayoutPanel	deck;
	private LoginView		loginView;
	private AuthView		authView;
	private UserData		userData;
	
	@Override
	public void onModuleLoad() {
		userData = new UserData();
		rootPanel = RootLayoutPanel.get();
		deck = new DeckLayoutPanel();
		
		loginView = new LoginView(deck, userData);
		authView = new AuthView(deck, userData);
		
		deck.add(loginView);
		deck.add(authView);
		deck.showWidget(0);
		
		rootPanel.add(deck);
	}

}
