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
     * S -> `setTitle Str;A`
     * @return The root node of the tree built ready to be interpreted
     */
    private Node S() throws UnexpectedTokenException {
        Node nodeStmt = new Node(NodeType.STATEMENT);

        // Checking the title
        checkTokenAndReturn(Tokens.setTitle);

        // Adding the title to the node
        Token<?> t = checkTokenAndReturn(Tokens.strValue);
        String title = (String) t.value();

        nodeStmt.addChild(new Node(NodeType.SET_TITLE, title));
        nodeStmt.addChild(A());

        return nodeStmt;
    }

    /**
     * A -> `BA|ε`
     * @return The node built
     */
    private List<Node> A() throws UnexpectedTokenException {
        if (isEndOfInput()) {
            return null;
        }
        List<Node> res = new ArrayList<>();
        res.add(B());
        List<Node> prev = A();
        if (!Objects.isNull(prev)) {
            res.addAll(prev);
        }
        return res;
    }

    /**
     * B -> `addLocation Int Str C`
     * @return The node built
     */
    private Node B() throws UnexpectedTokenException {
        // Check the token
        checkTokenAndReturn(Tokens.addLocation);

        // Get the int identifier
        Token<?> t = checkTokenAndReturn(Tokens.intValue);
        Integer id = (Integer) t.value();

        // Get the string description
        t = checkTokenAndReturn(Tokens.strValue);
        String description = (String) t.value();

        Node node = new Node(NodeType.ADD_LOCATION, id, description);
        node.addChild(C());

        return node;
    }

    /**
     * C -> `-> Int Str C|ε`
     * @return The node built
     */
    private List<Node> C() throws UnexpectedTokenException {
        if (isEndOfInput()) {
            return null;
        }
        List<Node> res = new ArrayList<>();
        Token<?> t = readToken();
        if (Objects.isNull(t)) {
            throw new UnexpectedTokenException("Expected a token, but found null");
        }
        if (t.type() != Tokens.arrow) {
            throw new UnexpectedTokenException(new Tokens[]{Tokens.arrow}, t.type());
        }

        // Get the int identifier
        t = readToken();
        if (Objects.isNull(t)) {
            throw new UnexpectedTokenException("Expected a token, but found null");
        }
        if (t.type() != Tokens.intValue) {
            throw new UnexpectedTokenException(new Tokens[]{Tokens.intValue}, t.type());
        }
        Integer id = (Integer) t.value();

        // Get the string description
        t = readToken();
        if (Objects.isNull(t)) {
            throw new UnexpectedTokenException("Expected a token, but found null");
        }
        if (t.type() != Tokens.strValue) {
            throw new UnexpectedTokenException(new Tokens[]{Tokens.strValue}, t.type());
        }
        String description = (String) t.value();

        res.add(new Node(NodeType.OPTION_DEFINITION, id, description));
        List<Node> prev = C();
        if (!Objects.isNull(prev)) {
            res.addAll(prev);
        }

        return res;
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
        return readToken();
    }
}
