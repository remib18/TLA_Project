package App;

import App.adventure.Adventure;
import App.adventure.Location;
import App.adventure.Proposition;
import App.exceptions.IllegalCaracterException;
import App.exceptions.IncompleteParsingException;
import App.exceptions.LexicalErrorException;
import App.exceptions.UnexpectedTokenException;
import App.lexicalAnalysis.LexicalAnalysis;
import App.lexicalAnalysis.Token;
import App.syntacticAnalysis.Node;
import App.syntacticAnalysis.NodeType;
import App.syntacticAnalysis.TreeBuilder;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Interpreter {

    /**
     * Interprets a file and returns an adventure ready to be played
     * @param fileName - The name of the file to interpret
     * @return The adventure
     */
    public static Adventure interpret(String fileName) throws LexicalErrorException, UnexpectedTokenException, IncompleteParsingException, IllegalCaracterException {
        String content = getFileContent(fileName);
        HashMap<Integer, Location> locations = new HashMap<>();
        String title = null;
        List<Token<?>> tokens = LexicalAnalysis.run(content);
        Node root = TreeBuilder.build(tokens);

        for (int i = 0; i < root.getNumberOfChild(); i++) {
            Node child = root.getChildAt(i);
            if (child.getType() == NodeType.SET_TITLE) {
                title = (String) child.getArg1();
            }
            else if (child.getType() == NodeType.ADD_LOCATION) {
                List<Proposition> propositions = new ArrayList<>();
                int id = (int) child.getArg1();
                String description = (String) child.getArg2();
                int j=i+1;
                while (j < root.getNumberOfChild() && root.getChildAt(j).getType() == NodeType.OPTION_DEFINITION) {
                    Node optionChild = root.getChildAt(j);
                    Proposition proposition = new Proposition((String) optionChild.getArg2(), (Integer) optionChild.getArg1());
                    propositions.add(proposition);
                    j++;
                }
                Location location = new Location(id, description, propositions);
                locations.put((Integer) child.getArg1(), location);
            }
        }
            return new Adventure(title, locations);
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
