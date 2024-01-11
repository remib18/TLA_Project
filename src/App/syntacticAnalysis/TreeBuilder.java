package App.syntacticAnalysis;

import App.exceptions.IncompleteParsingException;
import App.exceptions.UnexpectedTokenException;
import App.lexicalAnalysis.Token;
import App.lexicalAnalysis.Tokens;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class TreeBuilder {

    private final boolean debug = false;

    private int cursor;
    private final List<Token<?>> tokens;

    /**
     * Build a tree from a list of tokens
     * @param tokens The list of tokens
     * @return The root node of the tree built ready to be interpreted
     * @throws IncompleteParsingException If the end of the input is reached before the end of the parsing
     */
    public static Node build(List<Token<?>> tokens) throws IncompleteParsingException, UnexpectedTokenException {
        TreeBuilder tb = new TreeBuilder(tokens);
        return tb.build();
    }

    private TreeBuilder(List<Token<?>> tokens) {
        this.tokens = tokens;
        this.cursor = 0;
    }

    /**
     * Build the tree
     * @return The root node of the tree built ready to be interpreted
     * @throws IncompleteParsingException If the end of the input is reached before the end of the parsing
     */
    private Node build() throws IncompleteParsingException, UnexpectedTokenException {
        Node expr = S();
        if (cursor != tokens.size()) {
            throw new IncompleteParsingException();
        }
        return expr;
    }

    /**
     * S -> `setTitle Str;ABCD`
     * @return The root node of the tree built ready to be interpreted
     */
    private Node S() throws UnexpectedTokenException {
        Node nodeStmt = new Node(NodeType.STATEMENT);

        // Checking the title
        checkTokenAndReturn("S", Tokens.setTitle);
        Node node = new Node(NodeType.SET_TITLE);

        // Adding the title to the node
        Token<?> t = checkTokenAndReturn("S", Tokens.strValue);
        Node str = new Node(NodeType.STR, t.value());
        node.addChild(str);

        // Checking the end of the statement
        checkTokenAndReturn("S", Tokens.instructionEnd);

        nodeStmt.addChild(node);
        nodeStmt.addChild(A());
        nodeStmt.addChild(B());
        nodeStmt.addChild(C());
        nodeStmt.addChild(D());

        return nodeStmt;
    }

    /**
     * A -> `setHealth Int;`
     * @return The node built
     */
    private Node A() throws UnexpectedTokenException {
        checkTokenAndReturn("A", Tokens.setHealth);

        // Get the int health
        Token <?> t = checkTokenAndReturn("A", Tokens.intValue);
        Node hp = new Node(NodeType.INT, t.value());

        Node node = new Node(NodeType.SET_HEALTH);
        node.addChild(hp);

        // Check the end of the statement
        checkTokenAndReturn("A", Tokens.instructionEnd);

        return node;
    }

    /**
     * B -> `FB | EB | ε
     * @return The node built
     */
    private List<Node> B() throws UnexpectedTokenException {
        Tokens type = readTokenType("B");
        back("A");
        if (isEndOfInput() || (type != Tokens.addItem && type != Tokens.addCharacter)) {
            return new ArrayList<>();
        }
        List<Node> res = new ArrayList<>();

        Node next = F();
        if (!Objects.isNull(next)) {
            res.add(next);
        }
        next = E();
        if (!Objects.isNull(next)) {
            res.add(next);

        }

        List<Node> prev = B();
        if (!Objects.isNull(prev)) {
            res.addAll(prev);
        }
        return res;
    }

    /**
     * E -> `addItem Var;`
     * @return The node built
     */
    private Node E() throws UnexpectedTokenException {
        // Check the token
        Tokens type = readTokenType("E");
        if(type != Tokens.addItem){
            back("E");
            return null;
        }

        // Get the var name
        Token <?> t = checkTokenAndReturn("E", Tokens.varValue);
        Node variable = new Node(NodeType.VAR, t.value());

        Node node = new Node(NodeType.ADD_ITEM);
        node.addChild(variable);

        // Check the end of the statement
        checkTokenAndReturn("E", Tokens.instructionEnd);

        return node;
    }

    /**
     * F -> `addCharacter Var Int Int;`
     * @return The node built
     */
    private Node F() throws UnexpectedTokenException {
        // Check the token
        Tokens type = readTokenType("F");
        if(type != Tokens.addCharacter){
            back("F");
            return null;
        }

        // Get the Var name
        Token<?> t = checkTokenAndReturn("F", Tokens.varValue);
        Node variable = new Node(NodeType.VAR, t.value());
        // Get the int initial place
        t = checkTokenAndReturn("F", Tokens.intValue);
        Node init = new Node(NodeType.INT, t.value());
        // Get the int health
        t = checkTokenAndReturn("F", Tokens.intValue);
        Node hp = new Node(NodeType.INT, t.value());

        Node node = new Node(NodeType.ADD_CHARACTER);
        node.addChild(variable);
        node.addChild(init);
        node.addChild(hp);

        // Check the end of the statement
        checkTokenAndReturn("F", Tokens.instructionEnd);

        return node;
    }

    /**
     * C -> `setInventory Cp;`
     * @return The node built
     */
    private Node C() throws UnexpectedTokenException {
        // Check the token
        checkTokenAndReturn("C", Tokens.setInventory);

        Node res = new Node(NodeType.SET_INVENTORY);
        Token <?> t = checkTokenAndReturn("C", Tokens.instructionEnd, Tokens.varValue);
        while (t.type() == Tokens.varValue){
            back("C");
            res.addChild(Cp());
            t = checkTokenAndReturn("C", Tokens.instructionEnd, Tokens.varValue);
        }

        return res;

    }

    /**
     * Cp -> `Var:Int Cp | ε`
     * @return The node built
     */
    private Node Cp() throws UnexpectedTokenException {
        // Get the var name
        Token <?> t = checkTokenAndReturn("Cp", Tokens.varValue);
        Node variable = new Node(NodeType.VAR, t.value());
        // Check the colon
        checkTokenAndReturn("Cp", Tokens.colon);
        // Get the int quantity
        t = checkTokenAndReturn("Cp", Tokens.intValue);
        Node qt = new Node(NodeType.INT, t.value());

        Node node = new Node(NodeType.SET_INVENTORY_SLOT);
        node.addChild(variable);
        node.addChild(qt);
        return node;
    }

    /**
     * D -> `GD | ε`
     * @return The node built
     */
    private List<Node> D() throws UnexpectedTokenException {
        if (isEndOfInput()) {
            return new ArrayList<>();
        }
        List<Node> res = new ArrayList<>();
        Node next = G();
        if (!Objects.isNull(next)) {
            res.add(next);
        }
        List<Node> prev = D();
        if (!Objects.isNull(prev)) {
            res.addAll(prev);
        }
        return res;
    }

    /**
     * G -> `addLocation Int Str IH;`
     * @return The node built
     */
    private Node G() throws UnexpectedTokenException {
        // Check the token
        Token<?> t = checkTokenAndReturn("G", Tokens.addLocation, Tokens.endOfInput);
        if (t.type() == Tokens.endOfInput) {
            return null;
        }

        // Get the int identifier
        t = checkTokenAndReturn("G", Tokens.intValue);
        Node id = new Node(NodeType.INT, t.value());

        // Get the string description
        t = checkTokenAndReturn("G", Tokens.strValue);
        Node description = new Node(NodeType.STR, t.value());

        Node node = new Node(NodeType.ADD_LOCATION);
        node.addChild(id);
        node.addChild(description);
        node.addChild(I());
        node.addChild(H());

        // Check the end of the statement
        checkTokenAndReturn("G", Tokens.instructionEnd);

        return node;
    }

    /**
     * H -> `-> M Int Str IH | ε`
     * @return The node built
     */
    private List<Node> H() throws UnexpectedTokenException {
        List<Node> res = new ArrayList<>();
        Token<?> t = checkTokenAndReturn("H", Tokens.arrow, Tokens.instructionEnd);
        if (t.type() == Tokens.instructionEnd) {
            back("H");
            return res;
        }

        Node node = new Node(NodeType.OPTION_DEFINITION);

        // Get the condition(s)
        Node next = M();
        if (next != null) {
            node.addChild(next);
        }

        // Get the int identifier
        t = checkTokenAndReturn("H", Tokens.intValue);
        Node id = new Node(NodeType.INT, t.value());

        // Get the string description
        t = checkTokenAndReturn("H", Tokens.strValue);
        Node description = new Node(NodeType.STR, t.value());


        node.addChild(id);
        node.addChild(description);

        //Get the action(s)
        Node i = I();
        if (i != null) {
            node.addChild(i);
        }
        res.add(node);

        List<Node> prev = H();
        if (!Objects.isNull(prev)) {
            res.addAll(prev);
        }

        return res;
    }

    /**
     * I -> `(J) | ε`
     * @return The node built
     */
    private Node I() throws UnexpectedTokenException {
        // Check the token
        Token<?> t = checkTokenAndReturn("I", Tokens.arrow, Tokens.openParenthesis, Tokens.instructionEnd);
        if (t.type() == Tokens.arrow || t.type() == Tokens.instructionEnd) {
            back("I");
            return null;
        }
        Node actions = new Node(NodeType.SET_ACTIONS);
        // Get the action nodes
        actions.addChild(J());

        // Check the end of the action
        checkTokenAndReturn("I", Tokens.closeParenthesis);

        return actions;
    }

    /**
     * J -> `Jp J | ε`
     *
     * @return The node built
     */
    private List<Node> J() throws UnexpectedTokenException {
        var type = this.readTokenType("J");
        back("J");
        if (type == Tokens.closeParenthesis) {
            return null;
        }
        List<Node> res = new ArrayList<>();
        Node node = Jp();
        if (!Objects.isNull(node)) {
            res.add(node);
        }
        List<Node> prev = J();
        if (!Objects.isNull(prev)) {
            res.addAll(prev);
        }
        return res;
    }

    /**
     * Jp -> `K:LVar`
     * @return The node built
     */
    private Node Jp() throws UnexpectedTokenException {
        var type = this.readTokenType("Jp");
        back("Jp");
        if (type == Tokens.closeParenthesis) {
            return null;
        }

        Node action = new Node(NodeType.SET_ACTION);
        // Get the type of action
        Node K = K();
        action.addChild(K);

        // Check the colon
        checkTokenAndReturn("Jp", Tokens.colon);

        // Get the + or -
        Node L = L();
        action.addChild(L);

        // Get the var value
        Token <?> t = switch ((String) K.getValue()) {
            case "health" -> checkTokenAndReturn("Jp", Tokens.intValue);
            case "team", "inventory" -> checkTokenAndReturn("Jp", Tokens.varValue);
            default -> throw new UnexpectedTokenException("Expected a token, but found null");
        };
        NodeType nameType = t.type() == Tokens.intValue ? NodeType.INT : NodeType.VAR;
        Node name = new Node(nameType, t.value());
        action.addChild(name);

        return action;
    }

    /**
     * K -> `health | inventory | team`
     * @return The node built
     */
    private Node K() throws UnexpectedTokenException {
        Token<?> t = checkTokenAndReturn("K", Tokens.health, Tokens.inventory, Tokens.team);
        return new Node(NodeType.SET_ACTION_KIND, t.value());
    }

    /**
     * L -> `+ | -`
     * @return The node built
     */
    private Node L() throws UnexpectedTokenException {
        Token<?> t = checkTokenAndReturn("K", Tokens.minus, Tokens.plus);
        String value = switch (t.type()) {
            case plus -> "+";
            case minus -> "-";
            default -> throw new UnexpectedTokenException("Expected a token, but found null");
        };
        return new Node(NodeType.SET_ACTION_OP, value);
    }

    /**
     * M -> `(Mp) | ε`
     *
     * @return The node built
     */
    private Node M() throws UnexpectedTokenException {
        // Check the token
        Token<?> t = checkTokenAndReturn("M", Tokens.intValue, Tokens.openParenthesis);
        if (t.type() == Tokens.intValue) {
            back("M");
            return null;
        }

        Node node = new Node(NodeType.SET_CONDITIONS);

        // Get the action node
        List<Node> res = Mp();
        node.addChild(res);

        // Todo: error caused somewhere here
        // Node.displayNode(node);

        // Check the end of the action
        checkTokenAndReturn("M", Tokens.closeParenthesis);

        return node;
    }

    /**
     * Mp -> `NMp | ε`
     *
     * @return The node built
     */
    private List<Node> Mp() throws UnexpectedTokenException {
        var type = this.readTokenType("Mp");
        back("Mp");
        if (type == Tokens.closeParenthesis) {
            return null;
        }
        Node next = N();
        List<Node> prev = Mp();

        List<Node> res = new ArrayList<>();

        if(!Objects.isNull(next)) {
            res.add(next);
        }
        if (!Objects.isNull(prev)) {
            res.addAll(prev);
        }
        return res;
    }

    /**
     * N -> `Neg Np`
     * @return The node built
     */
    private Node N() throws UnexpectedTokenException {
        final Boolean neg = Neg();
        if (Objects.isNull(neg)) {
            return null;
        }
        Node node = new Node(NodeType.SET_CONDITION, neg);

        List<Node> next = Np();
        if (!Objects.isNull(next)){
            node.addChild(next);
        }

        if (!Objects.isNull(next) && next.isEmpty()) {
            return null;
        }

        return node;
    }

    /**
     * Np -> `O | P`
     * @return The node built
     */
    private List<Node> Np() throws UnexpectedTokenException {
        List<Node> node = O();
        if (!Objects.isNull(node)) {
            return node;
        }
        node = P();
        if (!Objects.isNull(node)) {
            return node;
        }
        return null;
    }

    /**
     * O -> `item:Var:Int`
     * @return The node built
     */
    private List<Node> O() throws UnexpectedTokenException {
        // Get the "item"
        Tokens type = readTokenType("O");
        if(type != Tokens.item) {
            back("O");
            return null;
        }
        // Check the colon
        checkTokenAndReturn("O", Tokens.colon);
        // Get the var name
        Token <?> t = checkTokenAndReturn("O", Tokens.varValue);
        Node variable = new Node(NodeType.VAR, t.value());
        // Check the colon
        checkTokenAndReturn("O", Tokens.colon);
        // Get the int quantity
        t = checkTokenAndReturn("O", Tokens.intValue);
        Node qt = new Node(NodeType.INT, t.value());

        List<Node> res = new ArrayList<>();
        res.add(new Node(NodeType.SET_CONDITION_KIND, "item"));
        res.add(variable);
        res.add(qt);

        return res;
    }

    /**
     * P -> `character:Var`
     * @return The node built
     */
    private List<Node> P() throws UnexpectedTokenException {
        // Get the "character"
        Tokens type = readTokenType("P");
        if(type != Tokens.character) {
            back("P");
            return null;
        }
        // Check the colon
        checkTokenAndReturn("P", Tokens.colon);
        // Get the var name
        Token <?> t = checkTokenAndReturn("P", Tokens.varValue);
        Node variable = new Node(NodeType.VAR, t.value());
        Node qt = new Node(NodeType.INT, 1);

        List<Node> res = new ArrayList<>();
        res.add(new Node(NodeType.SET_CONDITION_KIND, "character"));
        res.add(variable);
        res.add(qt);

        return res;
    }

    /**
     * Neg -> `! | ε`
     *
     * @return The node built
     */
    private Boolean Neg() {
        Tokens type = this.readTokenType("Neg");
        Tokens nextType = this.readTokenType("Neg");
        back("Neg");
        if(type != Tokens.exclamationPoint){
            back("Neg");
            return true;
        }
        if (nextType != Tokens.item && nextType != Tokens.character) {
            return null;
        }
        return false;

    }

    /**
     * Read the next token and consume it
     * @return The next token or null if the end of the input is reached
     */
    private Token<?> readToken(String method) {
        if (cursor >= tokens.size()) {
            if (debug) {
                System.out.println(STR."Reading Token \"null\" at method \{method}.");
            }
            return null;
        }
        Token<?> t = tokens.get(cursor);
        if (debug) {
            System.out.println(STR."Reading Token \"\{t}\" at method \{method}.");
        }
        cursor++;
        return t;
    }

    /**
     * Read the type of the next token without consuming it
     * @return The type of the next token or null if the end of the input is reached
     */
    private Tokens readTokenType(String method) {
        Token<?> t = readToken(method);
        if (t == null) {
            return null;
        }
        return t.type();
    }

    /**
     *
     */
    private void back(String method) {
        if (debug) {
            System.out.println(STR."Going back \{method}.");
        }
        cursor--;
    }

    /**
     * Check if the end of the input is reached
     * @return True if the end of the input is reached, false otherwise
     */
    private boolean isEndOfInput() {
        return cursor >= tokens.size();
    }

    /**
     * Utility method that check if the next token is of the given type
     * @param expected The expected type of the next token
     * @return The next token
     * @throws UnexpectedTokenException If the next token is not of the expected type
     */
    private Token<?> checkTokenAndReturn(String method, Tokens... expected) throws UnexpectedTokenException {
        Token<?> t = readToken(STR."\{method}:checkTokenAndReturn");
        if (Objects.isNull(t)) {
            throw new UnexpectedTokenException("Expected a token, but found null");
        }
        if (!Arrays.asList(expected).contains(t.type())) {
            displayErrorLocation();
            throw new UnexpectedTokenException(expected, t.type());
        }
        return t;
    }

    /**
     * Utility method that display the location of the error in the input to get some context
     */
    private void displayErrorLocation() {
        final int actualCursor = cursor - 1;
        final int margin = 5;
        int start = Math.max(0, actualCursor - margin);
        int end = Math.min(tokens.size(), actualCursor + margin);
        System.out.println(STR."Error at token \{actualCursor}:");
        for (int i = start; i < end; i++) {
            try {
                if (i == actualCursor) {
                    final String ANSI_RED = "\u001B[31m";
                    final String ANSI_RESET = "\u001B[0m";
                    System.out.println(STR."\{ANSI_RED}-> \{tokens.get(i)}\{ANSI_RESET}");
                } else {
                    System.out.println(STR."      \{tokens.get(i)}");
                }
            } catch (IndexOutOfBoundsException _) {}
        }
    }
}
