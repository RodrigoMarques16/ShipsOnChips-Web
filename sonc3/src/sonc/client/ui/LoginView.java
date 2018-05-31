package sonc.client.ui;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DeckLayoutPanel;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import sonc.client.SoncServiceAsync;
import sonc.client.UserData;
import sonc.client.events.EventManager;
import sonc.client.rpc.InvokeButton;
import sonc.client.rpc.InvokeClickHandler;
import sonc.shared.SoncException;

/**
 * The first page displayed to the user, for registration and authentication.
 */
public class LoginView extends VerticalPanel {
	
	private final InvokeClickHandler<?> handler = EventManager.getClickHandler(Boolean.class);
	
	private final String headline               = "<h1>Ships on Chips Web Client</h1>";
	private final String usernameLabel          = "Username: ";
    private final String passwordLabel          = "Password: ";
    private final String textBoxSize            = "250px";
 
	private final TextBox username              = new TextBox();
	private final Button loginButton            = new LoginButton("Login");
	private final Button registerButton         = new RegisterButton("Register");
	private final PasswordTextBox password      = new PasswordTextBox();
	
	private DecoratorPanel decPanel = new DecoratorPanel();
	private FlexTable loginLayout   = new FlexTable();
	private DeckLayoutPanel	deck;
	private UserData userData;
	private SoncServiceAsync soncSvc;
	
	
	/**
	 * A button to invoke the server for user registration.
	 */
	private class RegisterButton extends InvokeButton<Boolean> {
		public RegisterButton(String html) {
			super(html, handler);
		}

		@Override
		public void onSucess(Boolean result) {
			if (result)
				Window.alert("Registration sucessful. You may now login.");
			else
				Window.alert("Registration failed.");
		}

		@Override
		public void onCall(AsyncCallback<Boolean> callback) {
			try {
				soncSvc.register(username.getValue(), password.getValue(), callback);
			} catch (SoncException e) {
				Window.alert(e.toString());
			}
		}
	}
	
	/**
	 * A button to invoke the server for user login.
	 */
	private class LoginButton extends InvokeButton<Boolean> {
		public LoginButton(String html) {
			super(html, handler);
		}

		@Override
		public void onSucess(Boolean result) {
			if (result) {
				userData.setUsername(username.getValue());
				userData.setPassword(password.getValue());
				deck.showWidget(1);
			} else
				Window.alert("Invalid credentials.");
		}

		@Override
		public void onCall(AsyncCallback<Boolean> callback) {
			soncSvc.authenticate(username.getValue(), password.getValue(), callback);
		}
	}

	public LoginView(DeckLayoutPanel deck, UserData userData, SoncServiceAsync soncSvc) {

		this.deck = deck;
		this.userData = userData;
		this.soncSvc = soncSvc;

		FlexCellFormatter cellFormatter = loginLayout.getFlexCellFormatter();

		loginLayout.setCellSpacing(6);

		// Add header
		loginLayout.setHTML(0, 0, headline);
		cellFormatter.setColSpan(0, 0, 2);
		cellFormatter.setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_CENTER);

		// Add user name field
		username.setWidth(textBoxSize);
		loginLayout.setHTML(1, 0, usernameLabel);
		loginLayout.setWidget(1, 1, username);

		// Add password field
		password.setWidth(textBoxSize);
		loginLayout.setHTML(2, 0, passwordLabel);
		loginLayout.setWidget(2, 1, password);

		// Add registration button
		loginLayout.setWidget(3, 0, registerButton);
		cellFormatter.setColSpan(3, 0, 1);
		cellFormatter.setHorizontalAlignment(3, 0, HasHorizontalAlignment.ALIGN_LEFT);

		// Add login button
		loginLayout.setWidget(3, 1, loginButton);
		cellFormatter.setColSpan(3, 1, 1);
		cellFormatter.setHorizontalAlignment(3, 1, HasHorizontalAlignment.ALIGN_RIGHT);

		// Wrap everything in a DecoratorPanel
		decPanel.setWidget(loginLayout);

		int windowHeight = Window.getClientHeight();
		int windowWidth = Window.getClientWidth();

		this.setWidth(windowWidth * 0.5 + "px");
		this.setHeight(windowHeight * 0.5 + "px");
		this.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		this.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		this.add(decPanel);

		setStylePrimaryName("sonc-Login");
	}

}
