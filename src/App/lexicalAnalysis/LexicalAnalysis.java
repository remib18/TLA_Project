package App.lexicalAnalysis;

import App.GrammarSettings;
import App.exceptions.IllegalCaracterException;
import App.exceptions.LexicalErrorException;
import App.exceptions.UnexpectedTokenException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LexicalAnalysis {

    private final String input;
    private int cursor;
    private Integer state;
    private final List<Token<?>> res;
    private StringBuilder buffer;
    private boolean insideString = false;

    /**
     * Run the analysis
     * @param input - The input string to analyze
     * @return The list of tokens
     * @throws LexicalErrorException in case of an invalid input
     */
    public static List<Token<?>> run(String input) throws LexicalErrorException, IllegalCaracterException, UnexpectedTokenException {
        LexicalAnalysis la = new LexicalAnalysis(input);
        la.execute();
        return la.toTokenList();
    }

    private LexicalAnalysis(String input) {
        this.input = input;
        this.cursor = 0;
        this.state = GrammarSettings.INITIAL_STATE;
        this.res = new ArrayList<>();
        this.buffer = new StringBuilder();
    }

    /**
     * Read a single character from the input string
     * @return The character read or null if the end of the input string is reached
     *
     * @mutates this.cursor - The cursor is incremented by one
     */
    private Character readChar() {
        try {
            Character c = input.charAt(cursor);
            cursor++;
            return c;
        } catch (StringIndexOutOfBoundsException e) {
            return null;
        }
    }

    /**
     * Rollback the cursor by one
     *
     * @mutates this.cursor - The cursor is decremented by one
     */
    private void back() {
        if (cursor == 0) return;
        cursor--;
    }

    /**
     * Register a token in the result list
     *
     * @mutates this.res - The token is added to the result list
     */
    private void execute() throws LexicalErrorException, IllegalCaracterException, UnexpectedTokenException {

        Character c;
        Integer symbolIndex;
        do {
            c = readChar();
            symbolIndex = GrammarSettings.getTransition(state, c);
            if (symbolIndex >= 100) {
                registerToken(symbolIndex);
                state = 0;
                buffer = new StringBuilder();
                if (GrammarSettings.STATES_WITH_ROLLBACK.contains(symbolIndex)) {
                    back();
                }
                continue;
            }
            state = symbolIndex;
            if (state > 0) {
                buffer.append(c);
            }
        } while (!Objects.isNull(c));

    }

    /**
     * Register a token in the result list
     *
     * @mutates this.res - The token is added to the result list
     */
    private void registerToken(Integer symbolIndex) throws UnexpectedTokenException {
        Tokens tokenType = null;
        Object tokenValue = null;

        switch (symbolIndex) {
            case 101:
                // On commence à enregistrer une nouvelle chaîne de caractères mais sans enregistrer le token "
                // Lorsque l'on rencontre un nouveau ", on enregistre le token
                if (insideString) {
                    // Si nous sommes à l'intérieur d'une chaîne de caractères, nous enregistrons le token strValue
                    tokenType = Tokens.strValue;
                    tokenValue = buffer.toString();
                    insideString = false;
                } else {
                    buffer = new StringBuilder();
                    insideString = true;
                }
                break;
            case 102:
                String bufferContent = buffer.toString();
                if (bufferContent.equals("setTitle")) {
                    tokenType = Tokens.setTitle;
                } else if (bufferContent.equals("addLocation")) {
                    tokenType = Tokens.addLocation;
                }
                tokenValue = bufferContent;
                break;
            case 103:
                tokenType = Tokens.intValue;
                tokenValue = Integer.parseInt(buffer.toString());
                break;
            case 104:
                tokenType = Tokens.arrow;
                break;
            case 105:
                tokenType = Tokens.statementEnd;
                break;
            default:
                throw new UnexpectedTokenException(buffer.toString());
        }

        if (tokenType != null) {
            Token<?> token = new Token<>(tokenType, tokenValue);
            res.add(token);
        }
    }

    /**
     * @return The token list
     */
    private List<Token<?>> toTokenList() {
        return this.res;
    }
}
