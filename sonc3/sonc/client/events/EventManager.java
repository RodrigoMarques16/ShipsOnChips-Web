package sonc.client.events;

import java.util.HashMap;

/**
 * Class for managing events
 * 
 * Creates singleton instances of event handlers as needed.
 */
public class EventManager {
	
	private static HashMap<Class<?>, InvokeClickHandler<?>> clickMap = new HashMap<>();

	public static <T> InvokeClickHandler<?> getClickHandler(Class<T> type) {
		if (!clickMap.containsKey(type)) {
			clickMap.put(type, new InvokeClickHandler<T>());
		}
		return clickMap.get(type);
	}

}
