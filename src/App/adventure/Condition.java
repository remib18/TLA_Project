package App.adventure;

public record Condition(ConditionKind kind, String name, Integer value) {

    public Condition(ConditionKind kind, String name, int value) {
        this(kind, name, (Integer) value);
        if (kind != ConditionKind.ITEM) {
            throw new IllegalArgumentException("ConditionKind must be ITEM when value is an int");
        }
    }

    public Condition(ConditionKind kind, String name) {
        this(kind, name, (Integer) 1);
        if (kind != ConditionKind.CHARACTER) {
            throw new IllegalArgumentException("ConditionKind must be CHARACTER when no value is provided");
        }
    }
}
