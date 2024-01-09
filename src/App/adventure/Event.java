package App.adventure;

public record Event(EventKind kind, EventOperationType opType, Object value) {
    public Event(EventKind kind, EventOperationType opType, int value) {
        this(kind, opType, (Object) value);
        if (kind != EventKind.HEALTH) {
            throw new IllegalArgumentException("EventKind must be HEALTH when value is an int");
        }
    }

    public Event(EventKind kind, EventOperationType opType, String value) {
        this(kind, opType, (Object) value);
        if (kind == EventKind.HEALTH) {
            throw new IllegalArgumentException("EventKind must be INVENTORY or FOLLOWING_CHARACTERS when value is a String");
        }
    }


}
