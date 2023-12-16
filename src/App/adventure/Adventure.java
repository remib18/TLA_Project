package App.adventure;

import App.Interpreter;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public record Adventure(String title, Map<Integer, Location> locations) {

    public Adventure {
        // check all locations ids in hash correspond to their key
        for (var location : locations.values()) {
            if (!Objects.equals(location.id(), locations.get(location.id()).id())) {
                throw new IllegalArgumentException("Location id does not correspond to its key in hash");
            }
        }
    }

    @Override
    public String title() {
        return title;
    }

    public List<Location> locationsToList() {
        return locations.values().stream().toList();
    }

    public Location getLocation(int id) {
        return locations.get(id);
    }

    public void addLocation(Location location) {
        locations.put(location.id(), location);
    }

    public static Adventure load(String fileName) {
        try {
            String content = new String(Files.readAllBytes(Paths.get(fileName)));
            return Interpreter.interpret(content);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
