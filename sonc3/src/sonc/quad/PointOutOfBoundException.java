package sonc.quad;

/**
 * 
 * Exception raised when the quad tree is used with a point outside its
 * boundaries. Programmers can easily avoid these exceptions by checking points
 * before attempting to insert them in a quad tree. Since it extends
 * RuntimeException, it is not mandatory to handle this kind of exception. *
 *
 */
public class PointOutOfBoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public PointOutOfBoundException() {
		super();
	}
}
