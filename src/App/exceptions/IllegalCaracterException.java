package App.exceptions;

public class IllegalCaracterException extends Exception {

    public IllegalCaracterException(Character c) {
        super("Symbole inconnu : " + c);
    }
}