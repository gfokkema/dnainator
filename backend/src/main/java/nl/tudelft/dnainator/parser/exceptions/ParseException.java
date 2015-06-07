package nl.tudelft.dnainator.parser.exceptions;

/**
 * Generic parse exception.
 * All exceptions related to parsing edges or nodes should extend from this.
 */
public abstract class ParseException extends Exception {
	/**
	 * Generated serialVersionUID.
	 */
	private static final long serialVersionUID = -4680051600548666152L;

	/**
	 * Constructs the exception with the provided message.
	 *
	 * @param msg The message to display.
	 */
	public ParseException(String msg) {
		super(msg);
	}
}
