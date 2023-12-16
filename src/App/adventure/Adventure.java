package App.adventure;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Represents an adventure
 * @param title - The title of the adventure
 * @param locations - The locations of the adventure
 */
public record Adventure(String title, Map<Integer, Location> locations) {

    public Adventure {
        // check all locations ids in hash correspond to their key
        for (var location : locations.values()) {
            if (!Objects.equals(location.id(), locations.get(location.id()).id())) {
                throw new IllegalArgumentException("Location id does not correspond to its key in hash");
            }
        }
    }

    /**
     * Get the title of the adventure
     * @return The title of the adventure
     */
    @Override
    public String title() {
        return title;
    }

    /**
     * Get the locations of the adventure
     * @return The locations of the adventure
     */
    public List<Location> locationsToList() {
        return locations.values().stream().toList();
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
     * Add a location to the adventure
     * @param location - The location to add
     */
    public void addLocation(Location location) {
        locations.put(location.id(), location);
    }
}
