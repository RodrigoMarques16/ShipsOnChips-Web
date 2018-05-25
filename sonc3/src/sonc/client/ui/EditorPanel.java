package sonc.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;

import sonc.client.SoncService;
import sonc.client.SoncServiceAsync;
import sonc.client.UserData;
import sonc.shared.SoncException;

public class EditorPanel extends VerticalPanel {

	private final SoncServiceAsync soncSvc   = (SoncServiceAsync) GWT.create(SoncService.class);
	private final InvokeClickHandler<?> handler = EventManager.getClickHandler(Boolean.class);
	
	private final Button uploadButton        = new UploadButton("Upload");
	private final FileUpload fileUpload      = new FileUpload();
	private final TextArea codeArea          = new TextArea();
	private UserData userData;

	
	private class UploadButton extends InvokeButton<Void> {
		public UploadButton(String html) {
			super(html, handler);
		}
		@Override
		public void onCall(AsyncCallback<Void> callback) {
			try {
				soncSvc.buildShip(userData.getUsername(), userData.getPassword(), codeArea.getText() , callback);
				Window.alert("Ship submitted.");
			} catch (SoncException e) {
				Window.alert(e.toString());
			}	
		}
		@Override
		public void onSucess(Void arg) {
		}
	}
	
	public EditorPanel() {
		this.add(new HTML("Select a file"));

		// Add a file upload widget
		fileUpload.addClickHandler( new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				codeArea.setText(fileUpload.getFilename());
			}
		});
		this.add(fileUpload);
		

		// Add a text area
		codeArea.setVisibleLines(30);
		codeArea.setSize("700px", "500px");
		this.add(new HTML("<br>Your code goes here:"));
		this.add(codeArea);
		
		
		this.add(new HTML("<br>"));
		this.add(uploadButton);

	}
	
}
