package App.adventure;

import App.utils.Observable;

import java.util.*;

/**
 * Represents an adventure
 */
public class Adventure {
    private final String title;
    private final Map<String, Character> characters;
    private final Map<String, Item> items;
    private final Map<Integer, Location> locations;

    private Observable<State> state;

    public Adventure(String title, Map<String, Character> characters, Map<String, Item> items, Map<Integer, Location> locations, int initialHealth, Map<String, Integer> initialItems) {
        this.title = title;
        this.characters = characters;
        this.items = items;
        this.locations = locations;

        var inventory = new HashMap<String, Integer>();
        for (var key : initialItems.keySet()) {
            inventory.put(key, initialItems.get(key));
        }

        var charactersLocations = new HashMap<Character, Integer>();
        for (var character : characters.values()) {
            charactersLocations.put(character, character.getInitialLocationId());
        }

        State initialState = new State(
                false,
                false, initialHealth,
                inventory,
                new HashMap<>(),
                1,
                charactersLocations
        );

        state = new Observable<>(initialState);

        for (var entry : initialItems.entrySet()) {
            inventory.put(entry.getKey(), entry.getValue());
        }

        // check all locations ids in hash correspond to their key
        for (var location : locations.values()) {
            if (!Objects.equals(location.id(), locations.get(location.id()).id())) {
                throw new IllegalArgumentException("Location id does not correspond to its key in hash");
            }
        }

        // check all items names in hash correspond to their key
        for (var item : items.values()) {
            if (!Objects.equals(item.name(), items.get(item.name()).name())) {
                throw new IllegalArgumentException("Item name does not correspond to its key in hash");
            }
        }

        // check all characters names in hash correspond to their key
        for (var character : characters.values()) {
            if (!Objects.equals(character.getName(), characters.get(character.getName()).getName())) {
                throw new IllegalArgumentException("Character name does not correspond to its key in hash");
            }
        }
    }

    /**
     * Get the title of the adventure
     * @return The title of the adventure
     */
    public String getTitle() {
        return title;
    }

    /**
     * Get the current location of the player
     * @return The current location of the player
     */
    public Location getCurrentLocation() {
        Location location = getLocation(state.getValue().getCurrentLocationId());
        if (!Objects.isNull(location)) {
            state.update(Event.execute(location.events(), state.getValue()));
        }
        return location;
    }

    /**
     * Get the available propositions for a location. A proposition is available if all its conditions are met.
     * @param locationId - The id of the location
     * @return The available propositions for the location
     */
    public List<Proposition> getAvailablePropositions(int locationId) {
        if (state.getValue().isGameOver()) {
            return new ArrayList<>();
        }
        Location currentLocation = getLocation(locationId);
        List<Proposition> propositionList = currentLocation.propositions();
        List<Proposition> availablePropositions = new ArrayList<>();

        for (var proposition : propositionList) {
            if (Condition.execute(proposition.conditions(), state.getValue())) {
                availablePropositions.add(proposition);
            }
        }

        if (availablePropositions.isEmpty() && !state.getValue().isGameWon() && !state.getValue().isGameOver()) {
            state.update(new State(
                    state.getValue().isGameOver(),
                    true,
                    state.getValue().getCurrentHealth(),
                    state.getValue().getInventory(),
                    state.getValue().getFollowingCharacters(),
                    state.getValue().getCurrentLocationId(),
                    State.formatCharactersLocations(state.getValue().getCharactersLocations())
            ));
        }

        return availablePropositions;
    }

    /**
     * Perform a proposition by executing its events and updating the state
     * @param id - The id of the proposition
     */
    public Proposition performProposition(int id) {
        Location currentLocation = getLocation(state.getValue().getCurrentLocationId());
        if (Objects.isNull(currentLocation)) {
            return null;
        }
        Proposition proposition = currentLocation.propositions().get(id);
        state.update(Event.execute(proposition.events(), state.getValue(), proposition.locationNumber()));
        currentLocation = getLocation(state.getValue().getCurrentLocationId());
        if (Objects.isNull(currentLocation)) {
            updateStateOnNullLocation();
            return null;
        }
        return proposition;
    }

    private void updateStateOnNullLocation() {
        var currentState = state.getValue();
        state.update(new State(
                !currentState.isGameWon(),
                currentState.isGameWon(),
                currentState.getCurrentHealth(),
                currentState.getInventory(),
                currentState.getFollowingCharacters(),
                currentState.getCurrentLocationId(),
                State.formatCharactersLocations(currentState.getCharactersLocations())
        ));
    }

    /**
     * Get the observable state of the adventure
     * @return The observable state of the adventure
     */
    public Observable<State> getState() {
        return state;
    }

    /**
     * Get a location by its id
     * @param id - The id of the location
     * @return The location
     */
    public Location getLocation(int id) {
        return locations.get(id);
    }
}