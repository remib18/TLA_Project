package App;

import App.adventure.Adventure;
import App.adventure.AdventureContent;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

public class Interpreter {

    /**
     * Interprets a file and returns an adventure ready to be played
     * @param fileName - The name of the file to interpret
     * @return The adventure
     */
    public static Adventure interpret(String fileName) {
        // String content = getFileContent(fileName);
        // Todo: implement this method
        return new Adventure(AdventureContent.title, AdventureContent.init());
    }

    /**
     * Reads the content of a file
     * @param fileName - The name of the file to read
     * @return The content of the file
     */
    private static String getFileContent(String fileName) {
        if (Objects.isNull(fileName)) {
            throw new IllegalArgumentException("File name is null");
        }
        try {
            return new String(Files.readAllBytes(Paths.get(fileName)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
