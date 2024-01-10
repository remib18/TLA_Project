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
        if (isEndOfInput()) {
            return null;
        }
        List<Node> res = new ArrayList<>();

        Node next = F();
        if (!Objects.isNull(next)) {
            res.add(next);
        } else {
            next = E();
            if (!Objects.isNull(next)) {
                res.add(next);
            }
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
        checkTokenAndReturn(Tokens.addItem);

        // Get the var name
        Token <?> t = checkTokenAndReturn(Tokens.varValue);
        Node var = new Node(NodeType.VAR, t.value());

        Node node = new Node(NodeType.ADD_ITEM);
        node.addChild(var);

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
        checkTokenAndReturn(Tokens.addCharacter);

        // Get the Var name
        Token<?> t = checkTokenAndReturn(Tokens.varValue);
        Node var = new Node(NodeType.VAR, t.value());
        // Get the int initial place
        t = checkTokenAndReturn(Tokens.intValue);
        Node init = new Node(NodeType.INT, t.value());
        // Get the int health
        t = checkTokenAndReturn(Tokens.intValue);
        Node hp = new Node(NodeType.INT, t.value());

        Node node = new Node(NodeType.ADD_CHARACTER);
        node.addChild(var);
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
        checkTokenAndReturn(Tokens.varValue);

        // Get the var name
        Token <?> t = checkTokenAndReturn(Tokens.varValue);
        Node var = new Node(NodeType.VAR, t.value());
        // Check the colon
        checkTokenAndReturn(Tokens.colon);
        // Get the int quantity
        t = checkTokenAndReturn(Tokens.intValue);
        Node qt = new Node(NodeType.INT, t.value());

        Node node = new Node(NodeType.SET_INVENTORY_SLOT);
        node.addChild(var);
        node.addChild(qt);
        return node;
    }

    /**
     * D -> `GD | ε`
     * @return The node built
     */
    private List<Node> D() throws UnexpectedTokenException {
        if (isEndOfInput()) {
            return null;
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

        // Get the condition(s)
        Node next = M();
        Node conditions = new Node(NodeType.SET_CONDITIONS);
        if (next != null) {
            conditions.addChild(next);
        }

        // Get the int identifier
        t = checkTokenAndReturn(Tokens.intValue);
        Node id = new Node(NodeType.INT, t.value());

        // Get the string description
        t = checkTokenAndReturn(Tokens.strValue);
        Node description = new Node(NodeType.STR, t.value());

        Node node = new Node(NodeType.OPTION_DEFINITION);
        node.addChild(id);
        node.addChild(description);
        node.addChild(conditions);

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
        Token<?> t = checkTokenAndReturn(Tokens.arrow, Tokens.openParenthesis);
        if (t.type() == Tokens.arrow) {
            cursor--;
            return null;
        }
        Node actions = new Node(NodeType.SET_ACTIONS);
        // Get the action node
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
        if (this.readTokenType()==Tokens.closeParenthesis) {
            cursor--;
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
        if (this.readTokenType()==Tokens.closeParenthesis) {
            cursor--;
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
        Token <?> t = checkTokenAndReturn(Tokens.varValue);
        Node name = new Node(NodeType.VAR, t.value());
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
        return new Node(NodeType.SET_ACTION_OP, t.value());
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
        cursor--;
        if (t == Tokens.closeParenthesis) {
            return null;
        }
        Node next = N();
        if(!Objects.isNull(next)) {
            res.add(next);
        }
        List<Node> prev = J();
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
        Node node = new Node(NodeType.SET_CONDITION);
        Node neg = Neg();
        if (!Objects.isNull(neg)){
            node.addChild(neg);
        }
        Node next = Np();
        if (!Objects.isNull(next)){
            node.addChild(next);
        }
        return node;
    }

    /**
     * Np -> `O | P`
     * @return The node built
     */
    private Node Np() throws UnexpectedTokenException {
        Node node = O();
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
    private Node O() throws UnexpectedTokenException {
        // Get the "item"
        checkTokenAndReturn(Tokens.item);
        // Check the colon
        checkTokenAndReturn(Tokens.colon);
        // Get the var name
        Token <?> t = checkTokenAndReturn(Tokens.varValue);
        Node var = new Node(NodeType.VAR, t.value());
        // Check the colon
        checkTokenAndReturn(Tokens.colon);
        // Get the int quantity
        t = checkTokenAndReturn(Tokens.intValue);
        Node qt = new Node(NodeType.INT, t.value());

        Node node = new Node(NodeType.SET_CONDITION_BODY, "item");
        node.addChild(var);
        node.addChild(qt);

        return node;
    }

    /**
     * P -> `character:Var`
     * @return The node built
     */
    private Node P() throws UnexpectedTokenException {
        // Get the "item"
        checkTokenAndReturn(Tokens.character);
        // Check the colon
        checkTokenAndReturn(Tokens.colon);
        // Get the var name
        Token <?> t = checkTokenAndReturn(Tokens.varValue);
        Node var = new Node(NodeType.VAR, t.value());
        Node qt = new Node(NodeType.INT, 1);
        Node node = new Node(NodeType.SET_CONDITION, "character");
        node.addChild(var);
        node.addChild(qt);
        return node;
    }

    /**
     * Neg -> `! | ε`
     *
     * @return The node built
     */
    private Node Neg() throws UnexpectedTokenException {
        checkTokenAndReturn(Tokens.exclamationPoint);
        Tokens t = this.readTokenType();
        if(t != Tokens.exclamationPoint){
            cursor--;
            return null;
        }
        return new Node(NodeType.SET_CONDITION_NEG);

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
            throw new UnexpectedTokenException(expected, t.type());
        }
        return t;
    }
}
