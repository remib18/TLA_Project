package App.adventure;

public enum EventKind {
    INVENTORY,
    HEALTH,
    FOLLOWING_CHARACTERS,
    ;

    public static EventKind fromString(String actionKindStr) {
        return switch (actionKindStr) {
            case "inventory" -> INVENTORY;
            case "health" -> HEALTH;
            case "following_characters", "team" -> FOLLOWING_CHARACTERS;
            default -> throw new IllegalArgumentException(STR."Invalid action kind: \{actionKindStr}");
        };
    }
}
