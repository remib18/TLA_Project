package App.exceptions;

public class IncompleteParsingException extends Exception {

    public IncompleteParsingException() {
        super("Syntactic analysis ended before all tokens were examined.");
    }

}
