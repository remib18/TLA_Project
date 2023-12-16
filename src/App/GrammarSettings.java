package App;

import App.exceptions.LexicalErrorException;

import java.util.Set;

public class GrammarSettings {

    /**
     * Initial state of the lexical analysis
     */
    public static final Integer INITIAL_STATE = 0;

    /**
     * States that are considered as final states
     */
    public static final Set<Integer> ACCEPTATION_STATES = Set.of();

    /**
     * States that once reached, the last character must be read again
     */
    public static final Set<Integer> STATES_WITH_ROLLBACK = Set.of();

    /**
     * Transition table of the lexical analysis
     */
    private static final Integer[][] TRANSITIONS = {  // Todo: set transition table values
            //       espace  caractère     ;  addLocation    ->     "  chiffre  lettre
            /* 1 */ {    0,       null,  null,         null,  null,  null,     null,    null }
    };

    /**
     * Get the index of a character in the transition table
     * @param c - The character to get the index of
     * @return The index of the character in the transition table
     */
    public static Integer getSymbolIndex(Character c) {
        return switch (c) {
            // Todo: implement
            default -> null;
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
    public static Integer getTransition(Integer state, Character c) throws LexicalErrorException {
        try {
            return getTransition(state, getSymbolIndex(c));
        } catch (LexicalErrorException _) {
            throw new LexicalErrorException(state, c);
        }
    }
}
