package App.exceptions;

import App.GrammarSettings;

public class LexicalErrorException extends Exception {

    public LexicalErrorException(Integer state, Integer symbolIndex) {
        super(STR."No defined transition with state(\{state}) and symbolIndex(\{symbolIndex}).");
    }

    public LexicalErrorException(Integer state, Character c) {
        super(STR."No defined transition with state(\{state}) and symbolIndex(\{GrammarSettings.getSymbolIndex(c)}) from character(\{c}).");
    }

}
