package App.lexicalAnalysis;

public enum Tokens {
    setTitle,
    setHealth,
    setInventory,
    addCharacter,
    addItem,
    addLocation,
    arrow,
    instructionEnd,
    plus,
    minus,
    intValue,
    strValue,
    varValue,
    endOfInput,
    colon,
    openParenthesis,
    closeParenthesis,
    exclamationPoint,
    health,
    inventory,
    team,
    item,
    character;


    @Override
    public String toString() {
        return switch (this) {
            case setTitle -> "setTitle";
            case setHealth -> "setHealth";
            case setInventory -> "setInventory";
            case addCharacter -> "addCharacter";
            case addItem -> "addItem";
            case addLocation -> "addLocation";
            case arrow -> "arrow";
            case instructionEnd -> "instructionEnd";
            case plus -> "plus";
            case minus -> "minus";
            case intValue -> "intValue";
            case strValue -> "strValue";
            case varValue -> "varValue";
            case endOfInput -> "endOfInput";
            case colon -> "colon";
            case openParenthesis -> "openParenthesis";
            case closeParenthesis -> "closeParenthesis";
            case exclamationPoint -> "exclamationPoint";
            case health -> "health";
            case inventory -> "inventory";
            case team -> "team";
            case item -> "item";
            case character -> "character";
        };
    }
}
