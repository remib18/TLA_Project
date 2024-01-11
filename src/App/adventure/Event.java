package App.adventure;

import java.util.List;

public record Event(EventKind kind, EventOperationType opType, Object value) {

    /**
     * Creates a new event, either of kind inventory or following characters.
     * @param kind The kind of event. Must be INVENTORY or FOLLOWING_CHARACTERS.
     * @param opType The operation type of the event.
     * @param value The value of the event.
     */
    public Event(EventKind kind, EventOperationType opType, int value) {
        this(kind, opType, (Object) value);
        if (kind != EventKind.HEALTH) {
            throw new IllegalArgumentException("EventKind must be HEALTH when value is an int");
        }
    }

    /**
     * Creates a new health event.
     * @param kind The kind of event. Must be HEALTH.
     * @param opType The operation type of the event.
     * @param value The value of the event.
     */
    public Event(EventKind kind, EventOperationType opType, String value) {
        this(kind, opType, (Object) value);
        if (kind == EventKind.HEALTH) {
            throw new IllegalArgumentException("EventKind must be INVENTORY or FOLLOWING_CHARACTERS when value is a String");
        }
    }

    /**
     * Executes a list of events on a state.
     * @param events The list of events to execute.
     * @param state The state to execute the events on.
     * @return The new state after the events have been executed.
     */
    public static State execute(List<Event> events, State state) {
        if (events == null || events.isEmpty()) {
            return state;
        }

        var health = state.getCurrentHealth();
        var inventory = state.getInventory();
        var followingCharacters = state.getFollowingCharacters();

        for (var event : events) {
            switch (event.kind) {
                case HEALTH -> {
                    switch (event.opType) {
                        case POSITIVE -> health += (int) event.value;
                        case NEGATIVE -> health -= (int) event.value;
                    }
                }
                case INVENTORY -> {
                    switch (event.opType) {
                        case POSITIVE -> inventory.put((Item) event.value, inventory.getOrDefault(event.value, 0) + 1);
                        case NEGATIVE -> inventory.put((Item) event.value, inventory.getOrDefault(event.value, 0) - 1);
                    }
                }
                case FOLLOWING_CHARACTERS -> {
                    switch (event.opType) {
                        case POSITIVE -> followingCharacters.put((Character) event.value, true);
                        case NEGATIVE -> followingCharacters.put((Character) event.value, false);
                    }
                }
            }
        }

        // inventory cleaning
        inventory.entrySet().removeIf(entry -> entry.getValue() <= 0);

        // make sure health is always positive
        health = Math.max(0, health);

        return new State(
                health == 0,
                state.isGameWon(),
                health,
                inventory,
                followingCharacters,
                state.getCurrentLocationId(),
                State.formatCharactersLocations(state.getCharactersLocations())
        );

    }

    public static State execute(List<Event> events, State state, int moveToLocation) {
        var newState = execute(events, state);
        return new State(
                newState.isGameOver(),
                newState.isGameWon(),
                newState.getCurrentHealth(),
                newState.getInventory(),
                newState.getFollowingCharacters(),
                moveToLocation,
                State.formatCharactersLocations(newState.getCharactersLocations())
        );
    }
}
