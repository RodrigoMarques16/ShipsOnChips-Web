package sonc.client.ui;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.DeckLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;

import sonc.client.SoncServiceAsync;
import sonc.client.UserData;

/**
 * The page the user views after being successfully authenticated.
 */
public class AuthView extends TabLayoutPanel {

	private final static String[] titles = { "Editor", "Play", "Settings" };
	private EditorPanel			  editor;
	private PlayPanel			  play;
	private SettingsPanel		  settings;

	public AuthView(DeckLayoutPanel deck, UserData userData, SoncServiceAsync soncSvc) {
		super(3, Unit.EM);

		editor = new EditorPanel(userData, soncSvc);
		play = new PlayPanel();
		settings = new SettingsPanel(userData, soncSvc);

		this.add(editor, titles[0]);
		this.add(play, titles[1]);
		this.add(settings, titles[2]);

		this.selectTab(0);
	}
	
	public void startEditor() {
		editor.startEditor();
	}
	
}
