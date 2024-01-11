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
        checkTokenAndReturn(Tokens.setTitle);
        Node node = new Node(NodeType.SET_TITLE);

        // Adding the title to the node
        Token<?> t = checkTokenAndReturn(Tokens.strValue);
        Node str = new Node(NodeType.STR, t.value());
        node.addChild(str);

        // Checking the end of the statement
        checkTokenAndReturn(Tokens.instructionEnd);

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
        checkTokenAndReturn(Tokens.setHealth);

        // Get the int health
        Token <?> t = checkTokenAndReturn(Tokens.intValue);
        Node hp = new Node(NodeType.INT, t.value());

        Node node = new Node(NodeType.SET_HEALTH);
        node.addChild(hp);

        // Check the end of the statement
        checkTokenAndReturn(Tokens.instructionEnd);

        return node;
    }

    /**
     * B -> `FB | EB | ε
     * @return The node built
     */
    private List<Node> B() throws UnexpectedTokenException {
        Tokens type = readTokenType();
        cursor--;
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
        Tokens type = readTokenType();
        if(type != Tokens.addItem){
            cursor--;
            return null;
        }

        // Get the var name
        Token <?> t = checkTokenAndReturn(Tokens.varValue);
        Node variable = new Node(NodeType.VAR, t.value());

        Node node = new Node(NodeType.ADD_ITEM);
        node.addChild(variable);

        // Check the end of the statement
        checkTokenAndReturn(Tokens.instructionEnd);

        return node;
    }

    /**
     * F -> `addCharacter Var Int Int;`
     * @return The node built
     */
    private Node F() throws UnexpectedTokenException {
        // Check the token
        Tokens type = readTokenType();
        if(type != Tokens.addCharacter){
            cursor--;
            return null;
        }

        // Get the Var name
        Token<?> t = checkTokenAndReturn(Tokens.varValue);
        Node variable = new Node(NodeType.VAR, t.value());
        // Get the int initial place
        t = checkTokenAndReturn(Tokens.intValue);
        Node init = new Node(NodeType.INT, t.value());
        // Get the int health
        t = checkTokenAndReturn(Tokens.intValue);
        Node hp = new Node(NodeType.INT, t.value());

        Node node = new Node(NodeType.ADD_CHARACTER);
        node.addChild(variable);
        node.addChild(init);
        node.addChild(hp);

        // Check the end of the statement
        checkTokenAndReturn(Tokens.instructionEnd);

        return node;
    }

    /**
     * C -> `setInventory Cp;`
     * @return The node built
     */
    private Node C() throws UnexpectedTokenException {
        // Check the token
        checkTokenAndReturn(Tokens.setInventory);

        Node res = new Node(NodeType.SET_INVENTORY);
        Token <?> t = checkTokenAndReturn(Tokens.instructionEnd, Tokens.varValue);
        while (t.type() == Tokens.varValue){
            cursor--;
            res.addChild(Cp());
            t = checkTokenAndReturn(Tokens.instructionEnd, Tokens.varValue);
        }

        return res;

    }

    /**
     * Cp -> `Var:Int Cp | ε`
     * @return The node built
     */
    private Node Cp() throws UnexpectedTokenException {
        // Get the var name
        Token <?> t = checkTokenAndReturn(Tokens.varValue);
        Node variable = new Node(NodeType.VAR, t.value());
        // Check the colon
        checkTokenAndReturn(Tokens.colon);
        // Get the int quantity
        t = checkTokenAndReturn(Tokens.intValue);
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
        Token<?> t = checkTokenAndReturn(Tokens.addLocation, Tokens.endOfInput);
        if (t.type() == Tokens.endOfInput) {
            return null;
        }

        // Get the int identifier
        t = checkTokenAndReturn(Tokens.intValue);
        Node id = new Node(NodeType.INT, t.value());

        // Get the string description
        t = checkTokenAndReturn(Tokens.strValue);
        Node description = new Node(NodeType.STR, t.value());

        Node node = new Node(NodeType.ADD_LOCATION);
        node.addChild(id);
        node.addChild(description);
        node.addChild(I());
        node.addChild(H());

        // Check the end of the statement
        checkTokenAndReturn(Tokens.instructionEnd);

        return node;
    }

    /**
     * H -> `-> M Int Str IH | ε`
     * @return The node built
     */
    private List<Node> H() throws UnexpectedTokenException {
        List<Node> res = new ArrayList<>();
        Token<?> t = checkTokenAndReturn(Tokens.arrow, Tokens.instructionEnd);
        if (t.type() == Tokens.instructionEnd) {
            cursor--;
            return res;
        }

        Node node = new Node(NodeType.OPTION_DEFINITION);

        // Get the condition(s)
        Node next = M();
        if (next != null) {
            node.addChild(next);
        }

        // Get the int identifier
        t = checkTokenAndReturn(Tokens.intValue);
        Node id = new Node(NodeType.INT, t.value());

        // Get the string description
        t = checkTokenAndReturn(Tokens.strValue);
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
        Token<?> t = checkTokenAndReturn(Tokens.arrow, Tokens.openParenthesis, Tokens.instructionEnd);
        if (t.type() == Tokens.arrow || t.type() == Tokens.instructionEnd) {
            cursor--;
            return null;
        }
        Node actions = new Node(NodeType.SET_ACTIONS);
        // Get the action nodes
        actions.addChild(J());

        // Check the end of the action
        checkTokenAndReturn(Tokens.closeParenthesis);

        return actions;
    }

    /**
     * J -> `Jp J | ε`
     *
     * @return The node built
     */
    private List<Node> J() throws UnexpectedTokenException {
        var type = this.readTokenType();
        cursor--;
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
        var type = this.readTokenType();
        cursor--;
        if (type == Tokens.closeParenthesis) {
            return null;
        }

        Node action = new Node(NodeType.SET_ACTION);
        // Get the type of action
        Node K = K();
        action.addChild(K);

        // Check the colon
        checkTokenAndReturn(Tokens.colon);

        // Get the + or -
        Node L = L();
        action.addChild(L);

        // Get the var value
        Token <?> t = switch ((String) K.getValue()) {
            case "health" -> checkTokenAndReturn(Tokens.intValue);
            case "team", "inventory" -> checkTokenAndReturn(Tokens.varValue);
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
        Token<?> t = checkTokenAndReturn(Tokens.health, Tokens.inventory, Tokens.team);
        return new Node(NodeType.SET_ACTION_KIND, t.value());
    }

    /**
     * L -> `+ | -`
     * @return The node built
     */
    private Node L() throws UnexpectedTokenException {
        Token<?> t = checkTokenAndReturn(Tokens.minus, Tokens.plus);
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
        Token<?> t = checkTokenAndReturn(Tokens.intValue, Tokens.openParenthesis);
        if (t.type() == Tokens.intValue) {
            cursor--;
            return null;
        }

        Node node = new Node(NodeType.SET_CONDITIONS);

        // Get the action node
        List<Node> res = Mp();
        if(!Objects.isNull(res)){
            node.addChild(res);
        }

        // Todo: error caused somewhere here
        // Node.displayNode(node);

        // Check the end of the action
        checkTokenAndReturn(Tokens.closeParenthesis);

        return node;
    }

    /**
     * Mp -> `NMp | ε`
     *
     * @return The node built
     */
    private List<Node> Mp() throws UnexpectedTokenException {
        List<Node> res = new ArrayList<>();
        Tokens t = this.readTokenType();
        if (t == Tokens.closeParenthesis) {
            cursor--;
            return null;
        }
        List<Node> prev = Mp();
        cursor--;

        Node next = N();
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
        Tokens type = readTokenType();
        if(type != Tokens.item) {
            cursor--;
            return null;
        }
        // Check the colon
        checkTokenAndReturn(Tokens.colon);
        // Get the var name
        Token <?> t = checkTokenAndReturn(Tokens.varValue);
        Node variable = new Node(NodeType.VAR, t.value());
        // Check the colon
        checkTokenAndReturn(Tokens.colon);
        // Get the int quantity
        t = checkTokenAndReturn(Tokens.intValue);
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
        Tokens type = readTokenType();
        if(type != Tokens.character) {
            cursor--;
            return null;
        }
        // Check the colon
        checkTokenAndReturn(Tokens.colon);
        // Get the var name
        Token <?> t = checkTokenAndReturn(Tokens.varValue);
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
        Tokens type = this.readTokenType();
        Tokens nextType = this.readTokenType();
        cursor--;
        if (nextType != Tokens.item && nextType != Tokens.character) {
            return null;
        }
        if(type != Tokens.exclamationPoint){
            cursor--;
            return true;
        }
        return false;

    }

    /**
     * Read the next token and consume it
     * @return The next token or null if the end of the input is reached
     */
    private Token<?> readToken() {
        if (cursor >= tokens.size()) {
            return null;
        }
        Token<?> t = tokens.get(cursor);
        cursor++;
        return t;
    }

    /**
     * Read the type of the next token without consuming it
     * @return The type of the next token or null if the end of the input is reached
     */
    private Tokens readTokenType() {
        Token<?> t = readToken();
        if (t == null) {
            return null;
        }
        return t.type();
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
    private Token<?> checkTokenAndReturn(Tokens... expected) throws UnexpectedTokenException {
        Token<?> t = readToken();
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
