package App.adventure;

public enum EventOperationType {
    POSITIVE,  // +
    NEGATIVE  // -
    ;

    public static EventOperationType fromString(String operationTypeStr) {
        return switch (operationTypeStr) {
            case "+" -> POSITIVE;
            case "-" -> NEGATIVE;
            default -> throw new IllegalArgumentException(STR."Invalid operation type: \{operationTypeStr}");
        };
    }
}
