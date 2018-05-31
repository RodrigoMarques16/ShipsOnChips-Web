package sonc.client.rpc;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Interface for widgets which need to call the server
 * 
 * @author Rodrigo
 *
 * @param <T> The type of the callback expected from the server
 */
public interface Invoker<T> {
	/**
	 * Method to run when calling the server, don't forget to call the server here
	 * 
	 * @param callback returned from the server
	 */
	public void onCall(AsyncCallback<T> callback);

	/**
	 * Method called when the server is called successfully
	 * 
	 * @param result returned from the server
	 */
	public void onSucess(T result);
}
