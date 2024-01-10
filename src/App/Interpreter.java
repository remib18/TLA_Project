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
                case SET_TITLE -> title = getTitle(child);
                case ADD_LOCATION -> {
                    Location location = buildLocation(child);
                    locations.put(location.id(), location);
                }
                case STATEMENT -> throw new RuntimeException("Statement node should not be here");
                case OPTION_DEFINITION -> throw new RuntimeException("Option definition node should not be here");
            }
        }
        return new Adventure(title, new HashMap<>(), new HashMap<>(), locations, 5, new HashMap<>());
    }

    /**
     * Retourne le titre d'un nœud de type SET_TITLE
     * @param node - Le nœud
     * @return Le titre
     */
    private static String getTitle(Node node) {
        checkNodeType(node, NodeType.SET_TITLE);
        var child = node.getChildAt(0);
        checkNodeType(child, NodeType.STR);
        return (String) child.getValue();
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
     * Construit un emplacement à partir d'un nœud de type ADD_LOCATION
     * @param node - Le nœud
     * @return L'emplacement
     */
    private static Location buildLocation(Node node) {
        checkNodeType(node, NodeType.ADD_LOCATION);
        Integer id = null;
        String description = null;
        List<Event> events = new ArrayList<>();
        List<Proposition> propositions = new ArrayList<>();

        List<Node> propositionNodes = new ArrayList<>();

        for (Node child : node.getChildren()) {
            switch (child.getType()) {
                case INT -> id = (Integer) child.getValue();
                case STR -> description = (String) child.getValue();
                case OPTION_DEFINITION -> propositionNodes.add(child);
                case SET_ACTIONS -> events.addAll(getEvents(child));
                default -> throw new RuntimeException(STR."Unexpected node type: \"\{child.getType()}\" in an ADD_LOCATION node.");
            }
        }

        propositions = getPropositions(propositionNodes);

        return new Location(id, description, propositions, events);
    }

    /**
     * Construit les propositions à partir d'une liste de nœuds de type OPTION_DEFINITION
     * @param nodes - La liste de nœuds
     * @return La liste des propositions
     */
    private static List<Proposition> getPropositions(List<Node> nodes) {
        List<Proposition> propositions = new ArrayList<>();
        for (Node node : nodes) {
            checkNodeType(node, NodeType.OPTION_DEFINITION);
            String description = (String) node.getChildAt(0).getValue();
            Integer targetLocationId = (Integer) node.getChildAt(1).getValue();
            List<Condition> conditions = getConditions(node.getChildAt(2));
            List<Event> events = getEvents(node.getChildAt(3));

            propositions.add(new Proposition(conditions, description, targetLocationId, events));
        }
        return propositions;
    }

    /**
     * Construit les conditions à partir d'un nœud de type SET_CONDITIONS
     * @param node - Le nœud
     * @return La liste des conditions
     */
    private static List<Condition> getConditions(Node node) {
        checkNodeType(node, NodeType.SET_CONDITIONS);
        // TODO: Implement @Benoit
        return new ArrayList<>();
    }

    /**
     * Construit les événements à partir d'un nœud de type SET_ACTIONS
     * @param node - Le nœud
     * @return La liste des événements
     */
    private static List<Event> getEvents(Node node) {
        checkNodeType(node, NodeType.SET_ACTIONS);
        // TODO: Implement @Pierre-Alexis
        return new ArrayList<>();
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
