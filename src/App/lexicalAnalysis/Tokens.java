package App.lexicalAnalysis;

public enum Tokens {
    setTitle,
    addLocation,
    arrow,
    instructionEnd,
    intValue,
    strValue,
    endOfInput;

    @Override
    public String toString() {
        return switch (this) {
            case setTitle -> "setTitle";
            case addLocation -> "addLocation";
            case arrow -> "arrow";
            case intValue -> "intValue";
            case strValue -> "strValue";
            case instructionEnd -> "statementEnd";
            case endOfInput -> "endOfInput";
        };
    }
}
