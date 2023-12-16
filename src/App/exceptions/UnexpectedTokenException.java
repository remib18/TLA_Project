package App.exceptions;

import App.lexicalAnalysis.Tokens;

import java.util.Arrays;

public class UnexpectedTokenException extends Exception {
	public UnexpectedTokenException(String message) {
		super(message);
	}

	public UnexpectedTokenException(Tokens[] expected, Tokens found) {
		super(STR."Expected one of \{Arrays.toString(expected)}, but found \{found}");
	}
}
