package entityClasses;

/***
 * <p><b>Exception:</b> AnalyzerException </p>
 * 
 * <p><b>Purpose:</b></p>
 * <p>
 * Custom exception used to indicate invalid conditions/situations 
 * during reply analysis operations.
 * 
 * This exception is thrown when an invalid input such as null or
 * empty usernames provided.
 * </p>
 */
public class AnalyzerException extends Exception {
	/**
	 * Default constructor of the class
	 */
	public AnalyzerException() {}
	
	/***
	 * This constructor is used to initialize the exception 
	 * with a specific message.
	 * 
	 * @param message error message
	 */
	public AnalyzerException(String message) {
		super(message);
	}
}
