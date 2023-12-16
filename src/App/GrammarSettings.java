package App;

import App.exceptions.LexicalErrorException;

import java.util.Set;

public class GrammarSettings {

    public static final Integer INITIAL_STATE = 0;
    public static final Set<Integer> ACCEPTATION_STATES = Set.of();
    public static final Set<Integer> STATES_WITH_ROLLBACK = Set.of();

    private static final Integer[][] TRANSITIONS = {  // Todo: set transition table values
            //       espace  caractère     ;  addLocation    ->     "  chiffre  lettre
            /* 1 */ {    0,       null,  null,         null,  null,  null,     null,    null }
    };

    public static Integer getSymbolIndex(Character c) {
        return switch (c) {
            // Todo: implement
            default -> null;
        };
    }

    public static Integer getTransition(Integer state, Integer symbolIndex) throws LexicalErrorException {
        try {
            return GrammarSettings.TRANSITIONS[state][symbolIndex];
        } catch (IndexOutOfBoundsException _) {
            throw new LexicalErrorException(state, symbolIndex);
        }
    }

    public static Integer getTransition(Integer state, Character c) throws LexicalErrorException {
        try {
            return getTransition(state, getSymbolIndex(c));
        } catch (LexicalErrorException _) {
            throw new LexicalErrorException(state, c);
        }
    }
}
