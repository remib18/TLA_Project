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
    public static final Set<Integer> ACCEPTATION_STATES = Set.of(101, 102, 103, 104, 105, 106, 107, 108, 109);

    /**
     * States that once reached, the last character must be read again
     */
    public static final Set<Integer> STATES_WITH_ROLLBACK = Set.of(1, 2, 5);

    /**
     * Transition table of the lexical analysis
     */
    private static final Integer[][] TRANSITIONS = {
            //       espace   setTitle    ;    addLocation    ->     "    \    chiffre     lettre
            /* 0 */ {  -1,     101,        -1,     -1,        -1,   -1,   -1,    -1,         -1   }, // État initial, n'accepte que 'setTitle'
            /* 1 */ {   0,     101,       102,     103,       104,    1,    2,     3,          4   }, // État avec retour arrière
            /* 2 */ {   1,       1,         1,       1,         1,  105,    2,   105,        105   }, // État avec retour arrière
            /* 3 */ {   108,   108,       108,     108,       108,  109,  109,     1,        109   },
            /* 4 */ {   106,   106,       106,     106,       106,  106,  106,     3,        106   },
            /* 5 */ {   4,     107,       107,     107,       107,  107,  107,     4,          4   }  // État avec retour arrière


            // 101 acceptation d'un setTitle
            // 102 acceptation d'un ;
            // 103 acceptation d'un addLocation
            // 104 acceptation d'un ->
            // 105 acceptation d'un "
            // 106 acceptation d'un intVal
            // 107 acceptation d'un stringVal
            // 108 acceptation d'un \
            // 109 acceptation d'un \char
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
