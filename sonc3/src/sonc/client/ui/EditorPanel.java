package sonc.client.ui;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.ycp.cs.dh.acegwt.client.ace.AceEditor;
import edu.ycp.cs.dh.acegwt.client.ace.AceEditorMode;
import sonc.client.SoncServiceAsync;
import sonc.client.UserData;
import sonc.client.events.EventManager;
import sonc.client.rpc.InvokeButton;
import sonc.client.rpc.InvokeClickHandler;
import sonc.shared.SoncException;

/**
 * Code editing panel using Ace Editor
 * 
 * Users can retrieve submitted code or upload new code.
 */
public class EditorPanel extends VerticalPanel {
	
	private final InvokeClickHandler<?> handler = EventManager.getClickHandler(Void.class);
	
	private final Button uploadButton           = new UploadButton("Upload");
	private final UpdateButton updateButton     = new UpdateButton("Get current code");
	//private final FileUpload fileUpload       = new FileUpload();
	private final AceEditor editor              = new AceEditor();
	
	private FlowPanel buttons = new FlowPanel();
	private UserData  userData;
	private SoncServiceAsync soncSvc;

	/**
	 * Button to upload code to server
	 */
	private class UploadButton extends InvokeButton<Void> {
		public UploadButton(String html) {
			super(html, handler);
		}

		@Override
		public void onSucess(Void arg) {
			Window.alert("Ship submitted");
		}

		@Override
		public void onCall(AsyncCallback<Void> callback) {
			try {
				soncSvc.buildShip(userData.getUsername(), userData.getPassword(),
						editor.getValue(), callback);
			} catch (SoncException e) {
				Window.alert(e.toString());
			}
		}
	}

	/**
	 * Button to retrieve code from server
	 */
	private class UpdateButton extends InvokeButton<String> {
		public UpdateButton(String html) {
			super(html, handler);
		}

		@Override
		public void onSucess(String code) {
			editor.setValue(code);
		}

		@Override
		public void onCall(AsyncCallback<String> callback) {
			try {
				String name = userData.getUsername();
				String pwd = userData.getPassword();
				soncSvc.getCurrentCode(name, pwd, callback);
			} catch (SoncException e) {
				Window.alert(e.toString());
			}
		}
	}

	public EditorPanel(UserData userData, SoncServiceAsync soncSvc) {
		this.userData = userData;		
	    this.soncSvc = soncSvc;
	    
		/*
		 * this.add(new HTML("Select a file"));
		 * // Add a file upload widget
		 * fileUpload.addClickHandler( new ClickHandler() {
		 * @Override
		 * public void onClick(ClickEvent event) {
		 * codeArea.setText(fileUpload.getFilename());
		 * }
		 * });
		 * this.add(fileUpload);
		 */

	    // Add upload and update buttons
		buttons.add(updateButton);
		buttons.add(uploadButton);
		this.add(buttons);

		// Add Ace Editor
		this.add(new HTML("<br>Your code goes here:<br>"));
		editor.setWidth("600px");
	    editor.setHeight("400px");
		this.add(editor);
	}
	
	public void startEditor() {
		editor.startEditor();
		editor.setMode(AceEditorMode.JAVA);
	}
	
}
