package sonc.client.rpc;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;

/**
 * Abstract class for buttons that invoke a method from the server
 *
 * @param <T> The type of the callback expected from the server
 */
public abstract class InvokeButton<T> extends Button implements Invoker<T> {
	public InvokeButton(String html, ClickHandler handler) {
		super(html, handler);
	}
}
