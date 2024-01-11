package App.adventure;


/**
 * Represents an item in the game.
 * @param name The name of the item.
 */
public record Item(String name) {

    /**
     * Returns the name of the item.
     * @return The name of the item.
     */
    public String name() {
        return name;
    }
}
