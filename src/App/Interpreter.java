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

        for (int rootChildIndex = 0; rootChildIndex < root.getNumberOfChild(); rootChildIndex++) {
            Node child = root.getChildAt(rootChildIndex);
            switch (child.getType()) {
                case SET_TITLE -> title = (String) child.getArg1();
                case ADD_LOCATION -> {
                    int id = (int) child.getArg1();
                    String description = (String) child.getArg2();
                    Location location = new Location(id, description, getPropositions(child));
                    locations.put((Integer) child.getArg1(), location);
                }
                case STATEMENT -> throw new RuntimeException("Statement node should not be here");
                case OPTION_DEFINITION -> throw new RuntimeException("Option definition node should not be here");
            }
        }
        return new Adventure(title, locations);
    }

    private static List<Proposition> getPropositions(Node node) {
        if (node.getType() != NodeType.ADD_LOCATION) {
            throw new IllegalArgumentException("Node is not a location");
        }
        List<Proposition> propositions = new ArrayList<>();
        for (int i = 0; i < node.getNumberOfChild(); i++) {
            Node child = node.getChildAt(i);
            if (child.getType() == NodeType.OPTION_DEFINITION) {
                Proposition proposition = new Proposition((String) child.getArg2(), (Integer) child.getArg1());
                propositions.add(proposition);
            }
        }
        return propositions;
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
