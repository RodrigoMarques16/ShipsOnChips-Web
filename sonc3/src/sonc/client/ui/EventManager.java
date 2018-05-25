package sonc.client.ui;

import java.util.HashMap;

public class EventManager {

	private static EventManager instance = null;
	private static HashMap<Class<?>, InvokeClickHandler<?>> clickMap = new HashMap<>();

	private EventManager() {
	}

	public static EventManager getInstance() {
		if (instance == null) {
			instance = new EventManager();
		}
		return instance;
	}

	public static <T> InvokeClickHandler<?> getClickHandler(Class<T> type) {
		if (!clickMap.containsKey(type)) {
			clickMap.put(type, new InvokeClickHandler<T>());
		}
		return clickMap.get(type);
	}

}
