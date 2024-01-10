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
    public static final Set<Integer> STATES_WITH_ROLLBACK = Set.of(102, 103, 104, 106);

    /**
     * Transition table of the lexical analysis
     */
    private static final Integer[][] TRANSITIONS = {
            //       espace   "      \    -     >    ;      :       (       )       +       !       $    intVal     charVal
            /* 0 */ {   0,     1,   -1,   5,   -1,  107,  108,    109,    110,    111,    112,     4,      5,          3   },
            /* 1 */ {   1,   101,    2,   1,    1,    1,    1,      1,       1,     1,      1,      1,     1,          1   },
            /* 2 */ {   1,     1,    1,   1,    1,    1,    1,      1,       1,     1,      1,      1,     1,          1   },
            /* 3 */ { 102,   102,  102, 102,  102,  102,  102,    102,     102,   102,    102,    102,     3,          3   },
            /* 4 */ { 103,   103,  103, 103,  103,  103,  103,    103,     103,   103,    103,    103,     4,          4   },
            /* 5 */ { 104,   104,  104, 104,  104,  104,  104,    104,     104,   104,    104,    104,     5,        104   },
            /* 6 */ { 106,   106,  106, 106,  105,  106,  106,    106,     106,   106,    106,    106,   106,        106   }

            // -1  erreur
            // 101 acceptation d'un "
            // 102 acceptation d'un charVal (retourArriere)
            // 103 acceptation d'une variable $ (retourArriere)
            // 104 acceptation d'un intVal (retourArriere)
            // 105 acceptation d'un -> 
            // 106 acceptation d'un - (retourArriere)
            // 107 acceptation d'un ;
            // 108 acceptation d'un :
            // 109 acceptation d'un (
            // 110 acceptation d'un )
            // 111 acceptation d'un +
            // 112 acceptation d'un !

    };

    /**
     * Get the index of a character in the transition table
     * @param c - The character to get the index of
     * @return The index of the character in the transition table
     */
    public static Integer getSymbolIndex(Character c) throws IllegalCaracterException {
        return switch (c) {
            case null -> 0;
            case ' ', '\t', '\n' -> 0;
            case '"' -> 1;
            case '\\' -> 2;
            case '-' -> 3;
            case '>' -> 4;
            case ';' -> 5;
            case ':' -> 6;
            case '(' -> 7;
            case ')' -> 8;
            case '+' -> 9;
            case '!' -> 10;
            case '$' -> 11;
            default   -> {
                if (Character.isDigit(c)) {
                    yield 12;
                }
                if (Character.isWhitespace(c)) {
                    yield 0;
                }
                else {
                    yield 13;
                }
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
