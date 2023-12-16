package App.lexicalAnalysis;

public enum Tokens {
    setTitle,
    addLocation,
    arrow,
    intValue,
    strValue;

    @Override
    public String toString() {
        return switch (this) {
            case setTitle -> "setTitle";
            case addLocation -> "addLocation";
            case arrow -> "arrow";
            case intValue -> "intValue";
            case strValue -> "strVal";
        };
    }
}
