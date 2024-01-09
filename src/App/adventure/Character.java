package App.adventure;


/**
 * Represents a character in the game.
 */
public class Character {

    private final String name;
    private final int initialLocationId;
    private final int health;

    /**
     * Creates a new character.
     * @param name The name of the character.
     * @param initialLocationId The id of the location the character starts in.
     * @param health The initial health of the character.
     */
    public Character(String name, int initialLocationId, int health) {
        this.name = name;
        this.initialLocationId = initialLocationId;
        this.health = health;
    }

    /**
     * Gets the name of the character.
     * @return The name of the character.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the id of the location the character starts in.
     * @return The id of the location the character starts in.
     */
    public int getInitialLocationId() {
        return initialLocationId;
    }

    /**
     * Gets the initial health of the character.
     * @return The initial health of the character.
     */
    public int getHealth() {
        return health;
    }

}
