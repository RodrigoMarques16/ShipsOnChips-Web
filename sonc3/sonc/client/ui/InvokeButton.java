package sonc.client.ui;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;

/**
 * Abstract class for buttons that invoke a method from the server
 *
 * @param <T> The type of the callback expected from the server
 */
public abstract class InvokeButton<T> extends Button {
	public InvokeButton(String html, ClickHandler handler) {
		super(html, handler);
	}

	public abstract void onCall(AsyncCallback<T> callback);

	public abstract void onSucess(T result);
}
