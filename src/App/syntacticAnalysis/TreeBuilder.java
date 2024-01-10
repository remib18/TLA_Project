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

        // Adding the title to the node
        Token<?> t = checkTokenAndReturn(Tokens.strValue);
        String title = (String) t.value();

        // Checking the end of the statement
        checkTokenAndReturn(Tokens.instructionEnd);

        nodeStmt.addChild(new Node(NodeType.SET_TITLE, title));
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
        // Check the token
        Token<?> t = checkTokenAndReturn(Tokens.setHealth, Tokens.endOfInput);
        if (isEndOfInput()) {
            return null;
        }
        // Get the int health
        t = checkTokenAndReturn(Tokens.intValue);
        Integer hp = (Integer) t.value();

        Node node = new Node(NodeType.SET_HEALTH, hp);
        node.addChild(C());

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
        Token<?> t = checkTokenAndReturn(Tokens.addItem, Tokens.endOfInput);
        if (isEndOfInput()) {
            return null;
        }
        // Get the var name
        t = checkTokenAndReturn(Tokens.varValue);
        String str = (String) t.value();

        Node node = new Node(NodeType.ADD_ITEM, str);
        node.addChild(C());

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
        Token<?> t = checkTokenAndReturn(Tokens.addCharacter, Tokens.endOfInput);
        if (isEndOfInput()) {
            return null;
        }
        // Get the Var name
        t = checkTokenAndReturn(Tokens.intValue);
        Integer name = (Integer) t.value();
        // Get the int intial place
        t = checkTokenAndReturn(Tokens.intValue);
        Integer init = (Integer) t.value();
        // Get the int health
        t = checkTokenAndReturn(Tokens.intValue);
        Integer hp = (Integer) t.value();

        Node node = new Node(NodeType.ADD_CHARACTER, name, init, hp);
        node.addChild(C());

        // Check the end of the statement
        checkTokenAndReturn(Tokens.instructionEnd);

        return node;
    }

    //Confirmation pour C et Cp ? (il est tard, je me suis embrouillé)

    /**
     * C -> `setInventory Cp;`
     * @return The node built
     */
    private List<Node> C() throws UnexpectedTokenException {
        // Check the token
        Token<?> t = checkTokenAndReturn(Tokens.setInventory, Tokens.endOfInput);
        if (t.type() == Tokens.endOfInput) {
            return null;
        }

        return Cp();

    }

    /**
     * Cp -> `Var:Int Cp | ε`
     * @return The node built
     */
    private List<Node> Cp() throws UnexpectedTokenException {
        if (isEndOfInput()) {
            return null;
        }
        List<Node> res = new ArrayList<>();
        Token<?> t = checkTokenAndReturn(Tokens.addLocation, Tokens.instructionEnd);
        if (t.type() == Tokens.instructionEnd || t.type() == Tokens.addLocation) {
            cursor--;
            return res;
        }
        // Get the var name
        t = checkTokenAndReturn(Tokens.varValue);
        String name = (String) t.value();
        // Check the colon
        checkTokenAndReturn(Tokens.colon);
        // Get the int quantity
        t = checkTokenAndReturn(Tokens.intValue);
        Integer qt = (Integer) t.value();

        res.add(new Node(NodeType.SET_INVENTORY_SLOT, name, qt));
        List<Node> prev = Cp();
        if (!Objects.isNull(prev)) {
            res.addAll(prev);
        }

        return res;
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
        Integer id = (Integer) t.value();

        // Get the string description
        t = checkTokenAndReturn(Tokens.strValue);
        String description = (String) t.value();

        Node node = new Node(NodeType.ADD_LOCATION, id, description);
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
        if (isEndOfInput()) {
            return null;
        }
        List<Node> res = new ArrayList<>();
        Token<?> t = checkTokenAndReturn(Tokens.arrow, Tokens.instructionEnd);
        if (t.type() == Tokens.instructionEnd) {
            cursor--;
            return res;
        }

        // Get the condition(s)
        List<Node> next = M();
        if (next != null) {
            res.addAll(next);
        }

        // Get the int identifier
        t = checkTokenAndReturn(Tokens.intValue);
        Integer id = (Integer) t.value();

        // Get the string description
        t = checkTokenAndReturn(Tokens.strValue);
        String description = (String) t.value();

        res.add(new Node(NodeType.OPTION_DEFINITION, id, description));
        List<Node> next = I();
        if (next != null) {
            res.addAll(next);
        }
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
    private List<Node> I() throws UnexpectedTokenException {
        // Check the token
        Token<?> t = checkTokenAndReturn(Tokens.arrow, Tokens.endOfInput);
        if (t.type() == Tokens.endOfInput) {
            return null;
        }
        // Check the start of the action
        checkTokenAndReturn(Tokens.openParenthesis);

        // Get the action node
        List<Node> res = J();

        // Check the end of the action
        checkTokenAndReturn(Tokens.closeParenthesis);

        return res;
    }

    /**
     * J -> `Jp J | ε`
     *
     * @return The node built
     */
    private List<Node> J() throws UnexpectedTokenException {
        if (isEndOfInput()) {
            return null;
        }
        List<Node> res = new ArrayList<>();
        Token<?> t = checkTokenAndReturn(Tokens.closeParenthesis, Tokens.instructionEnd);
        if (t.type() == Tokens.instructionEnd || t.type() == Tokens.closeParenthesis) {
            cursor--;
            return res;
        }
        res.add(Jp());
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
        Token<?> t = checkTokenAndReturn(Tokens.endOfInput);
        if (t.type() == Tokens.endOfInput) {
            return null;
        }

        // Get the type of action
        t = checkTokenAndReturn(Tokens.varValue);
        String type = K();
        // Check the colon
        checkTokenAndReturn(Tokens.colon);
        // Get the + or -
        t = checkTokenAndReturn(Tokens.plus, Tokens.minus);
        Character op = L();
        // Get the var value
        t = checkTokenAndReturn(Tokens.varValue);
        String name = (String) t.value();

        return new Node(NodeType.SET_ACTION, type, op, name);
    }

    //C'est trop tard mtn mais K est inutile ? parce que je n'ai pas de token health, inventory ou team ?

    /**
     * K -> `health | inventory | team`
     * @return The node built
     */
    private String K() throws UnexpectedTokenException {
        Token<?> t = checkTokenAndReturn(Tokens.endOfInput);
        if (t.type() == Tokens.endOfInput) {
            return null;
        }
        t = checkTokenAndReturn(Tokens.strValue);
        String description = (String) t.value();
        if (description.equals("health") || description.equals("inventory") || description.equals("team")){
            return description;
        }
        throw new UnexpectedTokenException("Should find health, inventory or team. Found else");
    }

    /**
     * L -> `+ | -`
     * @return The node built
     */
    private Character L() throws UnexpectedTokenException {
        Token<?> t = checkTokenAndReturn(Tokens.endOfInput);
        if (t.type() == Tokens.endOfInput) {
            return null;
        }
        t = checkTokenAndReturn(Tokens.minus, Tokens.plus);
        return (Character) t.value();
    }

    /**
     * M -> `(Mp) | ε`
     *
     * @return The node built
     */
    private List<Node> M() throws UnexpectedTokenException {
// Check the token
        Token<?> t = checkTokenAndReturn(Tokens.intValue, Tokens.endOfInput);
        if (t.type() == Tokens.endOfInput || t.type() == Tokens.intValue) {
            return null;
        }
        // Check the start of the action
        checkTokenAndReturn(Tokens.openParenthesis);

        // Get the action node
        List<Node> res = Mp();

        // Check the end of the action
        checkTokenAndReturn(Tokens.closeParenthesis);

        return res;
    }

    /**
     * Mp -> `NMp | ε`
     *
     * @return The node built
     */
    private List<Node> Mp() throws UnexpectedTokenException {
        if (isEndOfInput()) {
            return null;
        }
        List<Node> res = new ArrayList<>();
        Token<?> t = checkTokenAndReturn(Tokens.closeParenthesis, Tokens.instructionEnd);
        if (t.type() == Tokens.instructionEnd || t.type() == Tokens.closeParenthesis) {
            cursor--;
            return res;
        }
        res.add(N());
        List<Node> prev = J();
        if (!Objects.isNull(prev)) {
            res.addAll(prev);
        }
        return res;
        return null;
    }

    /**
     * N -> `Neg Np`
     * @return The node built
     */
    private Node N() throws UnexpectedTokenException {
        Token<?> t = checkTokenAndReturn(Tokens.endOfInput);
        if (t.type() == Tokens.endOfInput) {
            return null;
        }
        Character neg = Neg();
        Node node = Np();
        if (node == null){
            throw new UnexpectedTokenException("This should not happen");
        }
        return new Node(NodeType.SET_CONDITION, neg, node.getArg1(), node.getArg2(), node.getArg3());
    }

    /**
     * Np -> `O | P`
     * @return The node built
     */
    private Node Np() throws UnexpectedTokenException {
        if (isEndOfInput()) {
            return null;
        }
        Node node = O();
        if (!Objects.isNull(node)) {
            return node;
        } else {
            node = P();
            if (!Objects.isNull(node)) {
                return node;
            }
        } throw new UnexpectedTokenException("Should have found item or character. Found else");
    }

    /**
     * O -> `item:Var:Int`
     * @return The node built
     */
    private Node O() throws UnexpectedTokenException {
        Token<?> t = checkTokenAndReturn(Tokens.endOfInput);
        if (t.type() == Tokens.endOfInput) {
            return null;
        }

        // Get the "item"
        t = checkTokenAndReturn(Tokens.strValue);
        String item = (String) t.value();
        if (!item.equals("item")){
            return null;
        }
        // Check the colon
        checkTokenAndReturn(Tokens.colon);
        // Get the var name
        t = checkTokenAndReturn(Tokens.varValue);
        String name = (String) t.value();
        // Check the colon
        checkTokenAndReturn(Tokens.colon);
        // Get the int quantity
        t = checkTokenAndReturn(Tokens.intValue);
        String qt = (String) t.value();

        return new Node(NodeType.SET_CONDITION, item, name, qt);
    }

    /**
     * P -> `character:Var`
     * @return The node built
     */
    private Node P() throws UnexpectedTokenException {
        Token<?> t = checkTokenAndReturn(Tokens.endOfInput);
        if (t.type() == Tokens.endOfInput) {
            return null;
        }

        // Get the "item"
        t = checkTokenAndReturn(Tokens.strValue);
        String character = (String) t.value();
        if (!character.equals("character")){
            return null;
        }
        // Check the colon
        checkTokenAndReturn(Tokens.colon);
        // Get the var name
        t = checkTokenAndReturn(Tokens.varValue);
        String name = (String) t.value();

        return new Node(NodeType.SET_CONDITION, character, name, 1);
    }

    /**
     * Neg -> `! | ε`
     * @return The node built
     */
    private Character Neg() throws UnexpectedTokenException {
        Token<?> t = checkTokenAndReturn(Tokens.endOfInput);
        if (t.type() == Tokens.endOfInput) {
            return ' ';
        }
        t = checkTokenAndReturn(Tokens.exclamationPoint);
        return '!';
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
