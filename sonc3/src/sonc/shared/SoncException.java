package sonc.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * @author Rodrigo Marques
 */
public class SoncException extends Exception implements IsSerializable {

	private static final long serialVersionUID = 1L;

	public SoncException() {
		super();
	}

	public SoncException(java.lang.String message) {
		super(message);
	}

	public SoncException(java.lang.String message, java.lang.Throwable cause) {
		super(message, cause);
	}

	public SoncException(java.lang.String message, java.lang.Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public SoncException(java.lang.Throwable cause) {
		super(cause);
	}

}
