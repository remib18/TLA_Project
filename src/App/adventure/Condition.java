package App.adventure;

import java.util.List;
import java.util.Objects;

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

    private boolean execute(State state) {
        switch (kind) {
            case ITEM -> {
                var itemQuantity = state.getInventory().get(name);
                if (Objects.isNull(itemQuantity)) {
                    return false;
                }
                return itemQuantity >= value;
            }
            case CHARACTER -> {
                var character = state.getFollowingCharacters().get(name);
                if (Objects.isNull(character)) {
                    return false;
                }
                return character;
            }
            default -> throw new IllegalStateException(STR."Unexpected value: \{kind}");
        }
    }

    public static boolean execute(List<Condition> conditions, State state) {
        for (var condition : conditions) {
            if (!condition.execute(state)) {
                return false;
            }
        }
        return true;
    }
}
