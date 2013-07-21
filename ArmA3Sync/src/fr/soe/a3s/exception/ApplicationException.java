package fr.soe.a3s.exception;
/**
 * This abstract exception is the superclass of all application exception.
 * It is a checked exception because it extends the Exception class.
 */
public class ApplicationException extends Exception{
    
	protected ApplicationException() {
    }

    protected ApplicationException(final String message) {
        super(message);
    }
}
