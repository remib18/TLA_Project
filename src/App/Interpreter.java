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
        List<Token<?>> tokens = LexicalAnalysis.run(content);
        Node root = TreeBuilder.build(tokens);

        String title = null;
        HashMap<String, Character> characters = new HashMap<>();
        HashMap<String, Item> items = new HashMap<>();
        HashMap<Integer, Location> locations = new HashMap<>();
        Integer health = null;
        HashMap<String, Integer> inventory = new HashMap<>();

        for (int rootChildIndex = 0; rootChildIndex < root.getNumberOfChild(); rootChildIndex++) {
            Node child = root.getChildAt(rootChildIndex);
            switch (child.getType()) {
                case SET_TITLE -> title = getTitle(child);
                case SET_HEALTH -> health = getHealth(child);
                case ADD_CHARACTER -> {
                    Character character = buildCharacter(child);
                    if (characters.containsKey(character.getName())) {
                        throw new RuntimeException(STR."Character \"\{character.getName()}\" has already been defined.");
                    }
                    characters.put(character.getName(), character);
                }
                case ADD_ITEM -> {
                    Item item = buildItem(child);
                    items.put(item.name(), item);
                }
                case SET_INVENTORY -> inventory = getInventory(child, items);
                case ADD_LOCATION -> {
                    Location location = buildLocation(child, items, characters);
                    locations.put(location.id(), location);
                }
                default -> throw new RuntimeException(STR."Unexpected node type: \"\{child.getType()}\" in the root node.");
            }
        }

        if (Objects.isNull(title)) {
            throw new RuntimeException("Title is not defined.");
        }

        if (Objects.isNull(health)) {
            throw new RuntimeException("Health is not defined.");
        }

        return new Adventure(title, characters, items, locations, health, inventory);
    }

    /**
     * Return the title of the adventure
     * @param node - The node (of type SET_TITLE)
     * @return The title
     */
    private static String getTitle(Node node) {
        checkNodeType(node, NodeType.SET_TITLE);
        var child = node.getChildAt(0);
        checkNodeType(child, NodeType.STR);
        return (String) child.getValue();
    }

    /**
     * Return the initial health of the player
     * @param node - The node (of type SET_HEALTH)
     * @return The initial health
     */
    private static Integer getHealth(Node node) {
        checkNodeType(node, NodeType.SET_HEALTH);
        var child = node.getChildAt(0);
        checkNodeType(child, NodeType.INT);
        return (Integer) child.getValue();
    }

    /**
     * Build and return the initial inventory
     * @param node - The node (of type SET_INVENTORY)
     * @param items - The items of the adventure
     * @return The initial inventory
     */
    private static HashMap<String, Integer> getInventory(Node node, HashMap<String, Item> items) {
        checkNodeType(node, NodeType.SET_INVENTORY);
        HashMap<String, Integer> inventory = new HashMap<>();

        for (Node child : node.getChildren()) {
            checkNodeType(child, NodeType.SET_INVENTORY_SLOT);
            Node itemNameNode = child.getChildAt(0);
            Node quantityNode = child.getChildAt(1);

            checkNodeType(itemNameNode, NodeType.VAR);
            checkNodeType(quantityNode, NodeType.INT);

            String itemName = (String) itemNameNode.getValue();
            Integer quantity = (Integer) quantityNode.getValue();

            if (!items.containsKey(itemName)) {
                throw new RuntimeException(STR."Item \"\{itemName}\" does not exist. Please add it before adding it to the inventory.");
            }

            inventory.put(itemName, quantity);
        }

        return inventory;
    }

    /**
     * Build and return a character
     * @param node - The node (of type ADD_CHARACTER)
     * @return The character
     */
    private static Character buildCharacter(Node node) {
        checkNodeType(node, NodeType.ADD_CHARACTER);

        Node nameNode = node.getChildAt(0);
        Node initialLocationIdNode = node.getChildAt(1);
        Node healthNode = node.getChildAt(2);

        checkNodeType(nameNode, NodeType.VAR);
        checkNodeType(initialLocationIdNode, NodeType.INT);
        checkNodeType(healthNode, NodeType.INT);

        String name = (String) nameNode.getValue();
        Integer initialLocationId = (Integer) initialLocationIdNode.getValue();
        Integer health = (Integer) healthNode.getValue();

        return new Character(name, initialLocationId, health);
    }

    /**
     * Build and return an item
     * @param node - The node (of type ADD_ITEM)
     * @return The item
     */
    private static Item buildItem(Node node) {
        checkNodeType(node, NodeType.ADD_ITEM);

        Node itemNameNode = node.getChildAt(0);
        checkNodeType(itemNameNode, NodeType.VAR);
        String itemName = (String) itemNameNode.getValue();

        return new Item(itemName);
    }

    /**
     * Build and return a location
     * @param node - The node (of type ADD_LOCATION)
     * @param items - The items of the adventure
     * @param characters - The characters of the adventure
     * @return The location
     */
    private static Location buildLocation(Node node, HashMap<String, Item> items, HashMap<String, Character> characters) {
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

        propositions = getPropositions(propositionNodes, items, characters);

        return new Location(id, description, propositions, events);
    }

    /**
     * Build and return the propositions of a location
     * @param nodes - The nodes (of type OPTION_DEFINITION)
     * @param items - The items of the adventure
     * @param characters - The characters of the adventure
     * @return The propositions
     */
    private static List<Proposition> getPropositions(List<Node> nodes, HashMap<String, Item> items, HashMap<String, Character> characters) {
        List<Proposition> propositions = new ArrayList<>();

        for (Node node : nodes) {
            checkNodeType(node, NodeType.OPTION_DEFINITION);

            Node node1 = node.getChildAt(0);
            Node node2 = node.getChildAt(1);
            Node node3 = node.getChildAt(2);
            Node node4 = node.getChildAt(3);

            List<Condition> conditions = null;
            Integer targetLocationId = null;
            String description = null;
            List<Event> events = null;

            switch (node1.getType()) {
                case SET_CONDITIONS -> {
                    conditions = getConditions(node1, items, characters);

                    checkNodeType(node2, NodeType.INT);
                    targetLocationId = (Integer) node2.getValue();

                    checkNodeType(node3, NodeType.STR);
                    description = (String) node3.getValue();

                    checkNodeType(node4, NodeType.SET_ACTIONS, true);
                    events = getEvents(node4);
                }
                case INT -> {
                    conditions = new ArrayList<>();
                    targetLocationId = (Integer) node1.getValue();

                    checkNodeType(node2, NodeType.STR);
                    description = (String) node2.getValue();

                    checkNodeType(node3, NodeType.SET_ACTIONS, true);
                    events = getEvents(node3);
                }
                default -> throw new RuntimeException(STR."Unexpected node type: \"\{node1.getType()}\" in an OPTION_DEFINITION node.");
            }

            propositions.add(new Proposition(conditions, description, targetLocationId, events));
        }
        return propositions;
    }

    /**
     * Build and return the conditions of a proposition
     * @param node - The node (of type SET_CONDITIONS)
     * @param items - The items of the adventure
     * @param characters - The characters of the adventure
     * @return The conditions
     */
    private static List<Condition> getConditions(Node node, HashMap<String, Item> items, HashMap<String, Character> characters) {
        checkNodeType(node, NodeType.SET_CONDITIONS);
        ArrayList<Condition> conditions = new ArrayList<>();

        for (Node child : node.getChildren()) {
            checkNodeType(child, NodeType.SET_CONDITION);

            boolean isNotInverted = (Boolean) child.getValue();
            Node conditionKindNode = child.getChildAt(0);
            Node objectNameNode = child.getChildAt(1);
            Node valueNode = child.getChildAt(2);

            if (Objects.isNull(conditionKindNode)) {
                continue;
            }

            checkNodeType(conditionKindNode, NodeType.SET_CONDITION_KIND);
            String conditionKindStr = (String) conditionKindNode.getValue();
            ConditionKind conditionKind = ConditionKind.fromString(conditionKindStr);

            checkNodeType(objectNameNode, NodeType.VAR);
            String conditionName = (String) objectNameNode.getValue();

            Condition condition = switch (conditionKind) {
                case ITEM -> {
                    if (!items.containsKey(conditionName)) {
                        throw new RuntimeException(STR."Item \"\{conditionName}\" does not exist. Please add it before adding it to the inventory.");
                    }
                    checkNodeType(valueNode, NodeType.INT);
                    Integer quantity = (Integer) valueNode.getValue();
                    yield new Condition(conditionKind, conditionName, quantity, !isNotInverted);
                }
                case CHARACTER -> {
                    if (!characters.containsKey(conditionName)) {
                        throw new RuntimeException(STR."Character \"\{conditionName}\" does not exist. Please add it before adding it to the inventory.");
                    }
                    yield new Condition(conditionKind, conditionName, !isNotInverted);
                }
            };

            conditions.add(condition);
        }

        return conditions;
    }

    /**
     * Build and return the events of a proposition or a location
     * @param node - The node (of type SET_ACTIONS)
     * @return The events
     */
    private static List<Event> getEvents(Node node) {
        checkNodeType(node, NodeType.SET_ACTIONS, true);
        ArrayList<Event> events = new ArrayList<>();

        if (Objects.isNull(node)) {
            return events;
        }

        for (Node child : node.getChildren()) {
            checkNodeType(child, NodeType.SET_ACTION);

            Node actionKindNode = child.getChildAt(0);
            Node operationTypeNode = child.getChildAt(1);
            Node valueNode = child.getChildAt(2);

            checkNodeType(actionKindNode, NodeType.SET_ACTION_KIND);
            String actionKindStr = (String) actionKindNode.getValue();
            EventKind actionKind = EventKind.fromString(actionKindStr);

            checkNodeType(operationTypeNode, NodeType.SET_ACTION_OP);
            String operationTypeStr = (String) operationTypeNode.getValue();
            EventOperationType operationType = EventOperationType.fromString(operationTypeStr);

            Event event = switch (actionKind) {
                case HEALTH -> {
                    checkNodeType(valueNode, NodeType.INT);
                    Integer quantity = (Integer) valueNode.getValue();
                    yield new Event(actionKind, operationType, (int) quantity);
                }
                case INVENTORY, FOLLOWING_CHARACTERS -> {
                    checkNodeType(valueNode, NodeType.VAR);
                    String identifier = (String) valueNode.getValue();
                    yield new Event(actionKind, operationType, identifier);
                }
            };

            events.add(event);
        }

        return events;
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
     * Check that the node is of the given type and is not null
     * @param node - The node
     * @param type - The type
     * @param allowNull - Whether the node can be null or not
     * @throws RuntimeException if the node is null or is not of the given type
     */
    private static void checkNodeType(Node node, NodeType type, boolean allowNull) throws RuntimeException {
        if (!allowNull && Objects.isNull(node)) {
            throw new RuntimeException(STR."Node is null. Expected type: \"\{type.toString()}\".");
        }
        if (allowNull && Objects.isNull(node)) {
            return;
        }
        if (node.getType() != type) {
            throw new RuntimeException(STR."Node is not of type \"\{type.toString()}\". Got \"\{node.getType().toString()}\" instead.");
        }
    }

    /**
     * Check that the node is of the given type and is not null
     * @param node - The node
     * @param type - The type
     * @throws RuntimeException if the node is null or is not of the given type
     */
    private static void checkNodeType(Node node, NodeType type) throws RuntimeException {
        checkNodeType(node, type, false);
    }
}
