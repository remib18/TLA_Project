package App;

import App.adventure.*;
import App.adventure.Character;
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
                    Location location = new Location(id, description, getPropositions(child), new ArrayList<>());
                    locations.put((Integer) child.getArg1(), location);
                }
                case STATEMENT -> throw new RuntimeException("Statement node should not be here");
                case OPTION_DEFINITION -> throw new RuntimeException("Option definition node should not be here");
            }
        }
        return new Adventure(title, new HashMap<>(), new HashMap<>(), locations, 5, new HashMap<>());
    }

    private static List<Proposition> getPropositions(Node node) {
        if (node.getType() != NodeType.ADD_LOCATION) {
            throw new IllegalArgumentException("Node is not a location");
        }
        List<Proposition> propositions = new ArrayList<>();
        for (int i = 0; i < node.getNumberOfChild(); i++) {
            Node child = node.getChildAt(i);
            if (child.getType() == NodeType.OPTION_DEFINITION) {
                Proposition proposition = new Proposition(new ArrayList<>(), (String) child.getArg2(), (Integer) child.getArg1(), new ArrayList<>());
                propositions.add(proposition);
            }
        }
        return propositions;
    }

    /**
     * Récupère les conditions d'un nœud de type OPTION_DEFINITION
     * @param node - Le nœud
     * @return La liste des conditions
     */
    private static List<Condition> getConditions(Node node) {
        checkNodeType(node, NodeType.OPTION_DEFINITION);
        // TODO: Implement @Benoit
        return new ArrayList<>();
    }

    /**
     * Récupère les événements d'un nœud de type OPTION_DEFINITION ou ADD_LOCATION
     * @param node - Le nœud
     * @return La liste des événements
     */
    private static List<Event> getEvents(Node node) {
        checkNodeType(node, NodeType.OPTION_DEFINITION, NodeType.ADD_LOCATION);
        // TODO: Implement @Pierre-Alexis
        return new ArrayList<>();
    }

    /**
     * Construit un emplacement à partir d'un nœud de type ADD_LOCATION
     * @param node - Le nœud
     * @return L'emplacement
     */
    private static Location buildLocation(Node node) {
        checkNodeType(node, NodeType.ADD_LOCATION);
        // TODO: Implement @Rémi
        return null;
    }

    /**
     * Construit un personnage à partir d'un nœud de type ADD_CHARACTER
     * @param node - Le nœud
     * @return Le personnage
     */
    private static Character buildCharacter(Node node) {
        checkNodeType(node, NodeType.ADD_CHARACTER);
        // TODO: Implement @Rémi
        return null;
    }

    /**
     * Construit un objet à partir d'un nœud de type ADD_ITEM
     * @param node - Le nœud
     * @return L'objet
     */
    private static Item buildItem(Node node) {
        checkNodeType(node, NodeType.ADD_ITEM);
        // TODO: Implement @Rémi
        return null;
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

    /**
     * Vérifie que le nœud est du bon type
     * @param node - Le nœud
     * @param type - Le type attendu
     * @param type2 - Le type attendu
     */
    private static void checkNodeType(Node node, NodeType type, NodeType type2) throws RuntimeException {
        boolean ok = true;
        if (Objects.nonNull(type) && node.getType() != type) {
            ok = false;
        }
        if (Objects.nonNull(type2) && node.getType() != type2) {
            ok = false;
        }
        if (!ok) {
            String typeStr = Objects.isNull(type) ? "" : type.toString();
            String type2Str = Objects.isNull(type2) ? "" : type2.toString();
            String types = typeStr + (typeStr.isEmpty() || type2Str.isEmpty() ? "" : " or ") + type2Str;
            throw new RuntimeException(STR."Node is not of type \{types}.");
        }
    }

    /**
     * Vérifie que le nœud est du bon type
     * @param node - Le nœud
     * @param type - Le type attendu
     */
    private static void checkNodeType(Node node, NodeType type) throws RuntimeException {
       checkNodeType(node, type, null);
    }
}
