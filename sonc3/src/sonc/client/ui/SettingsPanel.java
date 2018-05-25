package sonc.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;

import sonc.client.SoncService;
import sonc.client.SoncServiceAsync;
import sonc.client.UserData;
import sonc.shared.SoncException;

public class SettingsPanel extends VerticalPanel {

	private final SoncServiceAsync soncSvc    = (SoncServiceAsync) GWT.create(SoncService.class);
	private final InvokeClickHandler<?> handler  = EventManager.getClickHandler(Boolean.class);
	
    private final String textBoxSize          = "150px";
    
    private final PasswordTextBox oldPassword = new PasswordTextBox();
    private final PasswordTextBox newPassword = new PasswordTextBox();
    private final Button updatePasswordButton = new UpdatePasswordButton("Update password");
    
    private FlexTable settingsLayout = new FlexTable();
    private UserData userData;
    
    private class UpdatePasswordButton extends InvokeButton<Boolean> {
		public UpdatePasswordButton(String html) {
			super(html, handler);
		}
		@Override
		public void onSucess(Boolean result) {
			if (result)
				Window.alert("Password updated.");
			else
				Window.alert("Password update failed.");
		}
		@Override
		public void onCall(AsyncCallback<Boolean> callback) {
			try {
				soncSvc.updatePassword(userData.getUsername(), oldPassword.getValue(), newPassword.getValue(), callback);
			} catch (SoncException e) {
				Window.alert(e.toString());
			}
		}
	}
    
	public SettingsPanel(UserData userData) {
		this.userData = userData;
		
		FlexCellFormatter cellFormatter = settingsLayout.getFlexCellFormatter();
		
		settingsLayout.setCellSpacing(6);
		
		// Add old password field
		oldPassword.setWidth(textBoxSize);
		settingsLayout.setHTML(0, 0, "Old password:");
		settingsLayout.setWidget(0, 1, oldPassword);

		// Add new password field
		newPassword.setWidth(textBoxSize);
		settingsLayout.setHTML(0, 2, "New password:");
		settingsLayout.setWidget(0, 3, newPassword);
		
		// Add update password button
		settingsLayout.setWidget(0, 4, updatePasswordButton);
		
		cellFormatter.setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_CENTER);
		
		int windowHeight = Window.getClientHeight();
		int windowWidth = Window.getClientWidth();
		
		this.setWidth(windowWidth * 0.5 + "px");
		this.setHeight(windowHeight * 0.5 + "px");
		this.add(settingsLayout);
		
	}
}
