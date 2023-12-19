package App;

import App.exceptions.IllegalCaracterException;
import App.exceptions.LexicalErrorException;

import java.util.Set;

public class GrammarSettings {

    /**
     * Initial state of the lexical analysis
     */
    public static final Integer INITIAL_STATE = 0;

    /**
     * States that once reached, the last character must be read again
     */
    public static final Set<Integer> STATES_WITH_ROLLBACK = Set.of(102, 103);

    /**
     * Transition table of the lexical analysis
     */
    private static final Integer[][] TRANSITIONS = {
            //       espace   "      \    -     >    ;    intVal     charVal
            /* 0 */ {   0,     1,   -1,   6,   -1,  105,    5,          4   },
            /* 1 */ {   1,   101,    2,   1,    1,    1,    1,          1   },
            /* 2 */ {   1,     1,    1,   1,    1,    1,    1,          1   },
            /* 3 */ { 102,   102,  102, 102,  102,  102,    3,          3   }, // État avec retour arrière
            /* 4 */ { 103,   103,  103, 103,  103,  103,    4,        103   }, // État avec retour arrière
            /* 5 */ {  -1,    -1,   -1,  -1,  104,   -1,   -1,         -1   },

            // -1  erreur
            // 101 acceptation d'un "
            // 102 acceptation d'un charVal (retourArriere)
            // 103 acceptation d'un intVal  (retourArriere)
            // 104 acceptation d'un >
            // 105 acceptation d'un ;
    };

    /**
     * Get the index of a character in the transition table
     * @param c - The character to get the index of
     * @return The index of the character in the transition table
     */
    public static Integer getSymbolIndex(Character c) throws IllegalCaracterException {
        return switch (c) {
            case null -> 0;
            case '\t', '\n' -> 0;
            case '"' -> 1;
            case '\\' -> 2;
            case '-' -> 3;
            case '>' -> 4;
            case ';' -> 5;
            default   -> {
                if (Character.isDigit(c)) {
                    yield 6;
                }
                if (Character.isLetter(c)) {
                    yield 7;
                }
                if (Character.isWhitespace(c)) {
                    yield 0;
                }
                throw new IllegalCaracterException(c);
            }
        };
    }

    /**
     * Get the next state from a current state and a character
     * @param state - The current state
     * @param symbolIndex - The index of the character in the transition table
     * @return The next state
     * @throws LexicalErrorException in case of an invalid transition
     */
    public static Integer getTransition(Integer state, Integer symbolIndex) throws LexicalErrorException {
        try {
            return GrammarSettings.TRANSITIONS[state][symbolIndex];
        } catch (IndexOutOfBoundsException _) {
            throw new LexicalErrorException(state, symbolIndex);
        }
    }

    /**
     * Get the next state from a current state and a character
     * @param state - The current state
     * @param c - The character
     * @return The next state
     * @throws LexicalErrorException in case of an invalid transition
     */
    public static Integer getTransition(Integer state, Character c) throws LexicalErrorException, IllegalCaracterException {
        try {
            return getTransition(state, getSymbolIndex(c));
        } catch (LexicalErrorException _) {
            throw new LexicalErrorException(state, c);
        }
    }
}
