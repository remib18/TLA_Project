package App.lexicalAnalysis;

public record Token<T>(Tokens type, T value) {

    @Override
    public String toString() {
        if (value == null) {
            return type.toString();
        }
        return STR."\{type}(\{value})";
    }

    @Override
    public Tokens type() {
        return type;
    }

    @Override
    public T value() {
        return value;
    }
}
