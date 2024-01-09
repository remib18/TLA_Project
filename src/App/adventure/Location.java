package App.adventure;

import java.util.List;
import java.util.Objects;

/*
 * Lieu dans l'aventure.
 * 
 * Composée d'une description et d'aucune, une ou plusieurs propositions.
 */
public record Location(Integer id, String description, List<Proposition> propositions, List<Event> events) {
    private static Integer nextId = 0;
    private static Boolean createdWithIds = null;

    /**
     * Create a location with an id
     * @param id - The id of the location
     * @param description - The description of the location
     * @param propositions - The propositions of the location
     */
    public Location {
        checkCreatedWithIds();
        if (Objects.isNull(id)) {
            id = nextId;
            nextId++;
        }
    }

    /**
     * Create a location with an auto-generated id
     * @param description - The description of the location
     * @param propositions - The propositions of the location
     */
    public Location(String description, List<Proposition> propositions, List<Event> events) {
        this(null, description, propositions, events);
    }

    /**
     * Check if the location was created with ids or not
     */
    private void checkCreatedWithIds() {
        if (!Objects.isNull(createdWithIds) && !createdWithIds && !Objects.isNull(id)) {
            throw new RuntimeException("You can't create a location with an id, there are already locations created without ids");
        }
        if (Objects.isNull(createdWithIds) && !Objects.isNull(id)) {
            createdWithIds = true;
        }

        if (!Objects.isNull(createdWithIds) && createdWithIds && Objects.isNull(id)) {
            throw new RuntimeException("You can't create a location without an id, there are already locations created with ids");
        }
        if (Objects.isNull(createdWithIds) && Objects.isNull(id)) {
            createdWithIds = false;
        }
    }

    /**
     * Get the string representation of the location
     * @return The string representation of the location
     */
    @Override
    public String toString() {
        return STR."\{id}(\{description}, \{propositions})";
    }

    /**
     * Get the id of the location
     * @return The id of the location
     */
    @Override
    public Integer id() {
        return id;
    }

    /**
     * Get the description of the location
     * @return The description of the location
     */
    @Override
    public String description() {
        return description;
    }

    /**
     * Get the propositions of the location
     * @return The propositions of the location
     */
    @Override
    public List<Proposition> propositions() {
        return propositions;
    }

    /**
     * Get the events of the location
     * @return The events of the location
     */
    @Override
    public List<Event> events() {
        return events;
    }
}
