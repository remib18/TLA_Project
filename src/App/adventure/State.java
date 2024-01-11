package App.adventure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record State(
        boolean isGameOver,
        boolean isGameWon,
        int currentHealth,
        Map<String, Integer> inventory,
        Map<String, Boolean> followingCharacters,
        int currentLocationId,
        Map<Character, Integer> charactersLocations
) {
    /**
     * Check if the game is over
     * @return True if the game is over, false otherwise
     */
    public boolean isGameOver() {
        return isGameOver || currentHealth <= 0;
    }

    /**
     * Check if the game is won
     * @return True if the game is won, false otherwise
     */
    public boolean isGameWon() {
        return isGameWon && !isGameOver;
    }

    /**
     * Get the current health of the player
     * @return The current health of the player
     */
    public int getCurrentHealth() {
        return currentHealth;
    }

    /**
     * Get the inventory of the player
     * @return The inventory of the player
     */
    public Map<String, Integer> getInventory() {
        return inventory;
    }

    /**
     * Get the following characters of the player
     * @return The following characters of the player
     */
    public Map<String, Boolean> getFollowingCharacters() {
        return followingCharacters;
    }

    /**
     * Get the current location identifier of the player
     * @return An integer representing a location identifier
     */
    public int getCurrentLocationId() {
        return currentLocationId;
    }

    /**
     * Get the current repartition of characters in locations
     * @return A map of location identifiers to characters
     */
    public Map<Integer, List<Character>> getCharactersLocations() {
        // Swap keys and values
        var charactersLocations = new HashMap<Integer, List<Character>>();
        for (var entry : this.charactersLocations.entrySet()) {
            if (!charactersLocations.containsKey(entry.getValue())) {
                charactersLocations.put(entry.getValue(), new ArrayList<>());
            }
            charactersLocations.get(entry.getValue()).add(entry.getKey());
        }
        return charactersLocations;
    }

    /**
     * Get the characters at a given location
     * @param locationId - The location identifier
     * @return The characters at the given location
     */
    public List<Character> getCharactersAtLocation(int locationId) {
        var characters = new ArrayList<Character>();
        for (var entry : this.charactersLocations.entrySet()) {
            if (entry.getValue() == locationId) {
                characters.add(entry.getKey());
            }
        }
        return characters;
    }

    /**
     * Format the characters locations map to a map of characters to location identifiers
     * @param charactersLocations - The characters locations map
     * @return A map of characters to location identifiers
     */
    public static Map<Character, Integer> formatCharactersLocations(Map<Integer, List<Character>> charactersLocations) {
        var formattedCharactersLocations = new HashMap<Character, Integer>();
        for (var entry : charactersLocations.entrySet()) {
            for (var character : entry.getValue()) {
                formattedCharactersLocations.put(character, entry.getKey());
            }
        }
        return formattedCharactersLocations;
    }

    @Override
    public String toString() {
        return STR."State{isGameOver=\{isGameOver}, isGameWon=\{isGameWon}, currentHealth=\{currentHealth}, inventory=\{inventory}, followingCharacters=\{followingCharacters}, currentLocationId=\{currentLocationId}, charactersLocations=\{charactersLocations}\{'}'}";
    }
}
