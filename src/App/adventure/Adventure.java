package App.adventure;

import java.util.*;

/**
 * Represents an adventure
 */
public class Adventure {
    private final String title;
    private final Map<String, Character> characters;
    private final Map<String, Item> items;
    private final Map<Integer, Location> locations;

    private int currentHealth;

    private Map<Item, Integer> inventory = new HashMap<>();

    private List<Character> followingCharacters = new ArrayList<>();

    public Adventure(String title, Map<String, Character> characters, Map<String, Item> items, Map<Integer, Location> locations, int initialHealth, Map<String, Integer> initialItems) {
        this.title = title;
        this.characters = characters;
        this.items = items;
        this.locations = locations;

        this.currentHealth = initialHealth;

        for (var entry : initialItems.entrySet()) {
            inventory.put(items.get(entry.getKey()), entry.getValue());
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
     * Get a location by its id
     * @param id - The id of the location
     * @return The location
     */
    public Location getLocation(int id) {
        return locations.get(id);
    }

    /**
     * Get the available propositions for a location. A proposition is available if all its conditions are met.
     * @param locationId - The id of the location
     * @return The available propositions for the location
     */
    public List<Proposition> getAvailablePropositions(int locationId) {
        return getLocation(locationId).propositions();
    }
}