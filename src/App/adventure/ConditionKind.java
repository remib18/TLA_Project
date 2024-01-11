package App.adventure;


/**
 * Represents the kind of condition.
 */
public enum ConditionKind {
    ITEM,
    CHARACTER;

    /**
     * Get the condition kind from a string
     * @param s The string to parse
     * @return The condition kind
     */
    public static ConditionKind fromString(String s) {
        return switch (s) {
            case "item" -> ITEM;
            case "character" -> CHARACTER;
            default -> throw new IllegalArgumentException(STR."Invalid condition kind: \{s}");
        };
    }
}
