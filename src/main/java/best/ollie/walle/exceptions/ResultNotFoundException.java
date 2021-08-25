package best.ollie.walle.exceptions;

/**
 * Exception for when database returns no result
 */
public class ResultNotFoundException extends Exception {

	/**
	 * @param message Exact error message as to what happened for the database
	 */
	public ResultNotFoundException(String message) {
		super(message);
	}

}
