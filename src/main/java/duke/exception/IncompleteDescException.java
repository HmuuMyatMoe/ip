package duke.exception;

/**
 * Encapsulates the related fields and behavior of an IncompleteDescException.
 * Represents the exception to throw when description given is missing or incomplete.
 */
public class IncompleteDescException extends DukeException {
    /**
     * Instantiates IncompleteDescException
     *
     * @param message The error message.
     */
    public IncompleteDescException(String message) {
        super(message);
    }
}
