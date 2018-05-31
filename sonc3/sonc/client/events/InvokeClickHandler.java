package sonc.client.events;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

import sonc.client.ui.InvokeButton;

/**
 * Class to handle click events that invoke the server
 */
public class InvokeClickHandler<T> implements ClickHandler {
	@SuppressWarnings("unchecked")
	public void onClick(ClickEvent event) {
		InvokeButton<T> source = (InvokeButton<T>) event.getSource();
		AsyncCallback<T> callback = new AsyncCallback<T>() {
			public void onFailure(Throwable caught) {
				Window.alert(caught.getMessage());
			}

			public void onSuccess(T result) {
				source.onSucess(result);
			}
		};
		source.onCall(callback);
	}
}
