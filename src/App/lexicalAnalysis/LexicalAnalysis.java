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
            if (Objects.isNull(c)) {
                res.add(new Token<>(Tokens.endOfInput, null));
                continue;
            }
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
                tokenType = Tokens.strValue;
                tokenValue = buffer.substring(1, buffer.length());
                break;
            case 102:
                String bufferContent = buffer.toString();
                tokenType = switch (bufferContent) {
                    case "setTitle" -> Tokens.setTitle;
                    case "setHealth" -> Tokens.setHealth;
                    case "setInventory" -> Tokens.setInventory;
                    case "addCharacter" -> Tokens.addCharacter;
                    case "addItem" -> Tokens.addItem;
                    case "addLocation" -> Tokens.addLocation;
                    case "health" -> Tokens.health;
                    case "inventory" -> Tokens.inventory;
                    case "team" -> Tokens.team;
                    case "character" -> Tokens.character;
                    case "item" -> Tokens.item;
                    default -> throw new UnexpectedTokenException(STR."Unexpected token with symbol index : \{symbolIndex} and buffer : \{buffer.toString()}");
                };
                tokenValue = bufferContent;
                break;
            case 103:
                tokenType = Tokens.varValue;
                tokenValue = buffer.substring(1, buffer.length());
                break;
            case 104:
                tokenType = Tokens.intValue;
                tokenValue = Integer.parseInt(buffer.toString());
                break;
            case 105:
                tokenType = Tokens.arrow;
                break;
            case 106:
                tokenType = Tokens.minus;
                break;
            case 107:
                tokenType = Tokens.instructionEnd;
                break;
            case 108:
                tokenType = Tokens.colon;
                break;
            case 109:
                tokenType = Tokens.openParenthesis;
                break;
            case 110:
                tokenType = Tokens.closeParenthesis;
                break;
            case 111:
                tokenType = Tokens.plus;
                break;
            case 112:
                tokenType = Tokens.exclamationPoint;
                break;
            default:
                throw new UnexpectedTokenException(STR."Unexpected token with symbol index : \{symbolIndex} and buffer : \{buffer.toString()}");
        }

        Token<?> token = new Token<>(tokenType, tokenValue);
        res.add(token);
    }

    /**
     * @return The token list
     */
    private List<Token<?>> toTokenList() {
        return this.res;
    }
}
