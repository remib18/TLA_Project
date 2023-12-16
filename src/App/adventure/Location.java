package App.adventure;

import java.util.List;
import java.util.Objects;

/*
 * Lieu dans l'aventure.
 * 
 * Composée d'une description et d'aucune, une ou plusieurs propositions.
 */
public record Location(Integer id, String description, List<Proposition> propositions) {
    private static Integer nextId = 0;
    private static Boolean createdWithIds = null;

    public Location {
        checkCreatedWithIds();
        if (Objects.isNull(id)) {
            id = nextId;
            nextId++;
        }
    }

    public Location(String description, List<Proposition> propositions) {
        this(null, description, propositions);
    }

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

    @Override
    public String toString() {
        return STR."\{id}(\{description}, \{propositions})";
    }

    @Override
    public Integer id() {
        return id;
    }

    @Override
    public String description() {
        return description;
    }

    @Override
    public List<Proposition> propositions() {
        return propositions;
    }
}
