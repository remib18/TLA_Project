package App.syntacticAnalysis;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Node {
    private final List<Node> child = new ArrayList<>();
    private final NodeType type;
    private Object arg1;
    private Object arg2;

    /**
     * Create a node with a value
     * @param nodeType The type of the node
     * @param arg1 The value of the first argument of the node
     */
    public Node(NodeType nodeType, Object arg1) {
        this.type = nodeType;
        this.arg1 = arg1;
    }

    /**
     * Create a node with a value
     * @param nodeType The type of the node
     * @param arg1 The value of the first argument of the node
     * @param arg2 The value of the second argument of the node
     */
    public Node(NodeType nodeType, Object arg1, Object arg2) {
        this.type = nodeType;
        this.arg1 = arg1;
        this.arg2 = arg2;
    }

    /**
     * Create a node with no value
     * @param cl The type of the node
     */
    public Node(NodeType cl) {
        this.type = cl;
    }

    /**
     * Add a child to the node
     * @param n The child to add
     */
    public void addChild(Node n) {
        child.add(n);
    }

    /**
     * Add a list of child to the node
     * @param n The child to add
     */
    public void addChild(List<Node> n) {
        child.addAll(n);
    }

    /**
     * Get the child at the given index
     * @param i The index of the child
     * @return The child at the given index
     */
    public Node getChildAt(int i) {
        return this.child.get(i);
    }

    /**
     * Get the number of child of the node
     * @return The number of child of the node
     */
    public int getNumberOfChild() {
        return this.child.size();
    }

    /**
     * Get the type of the node
     * @return The type of the node
     */
    public NodeType getType() {
        return type;
    }

    /**
     * Get the first argument of the node
     * @return The value of the node
     */
    public Object getArg1() {
        return arg1;
    }

    /**
     * Get the second argument of the node
     * @return The value of the node
     */
    public Object getArg2() {
        return arg2;
    }

    /**
     * Display a node in a string
     * @return The string representation of the node
     */
    public String toString() {
        String type = Objects.isNull(this.type) ? "" : this.type.toString();
        String arg1Str = Objects.isNull(arg1) ? "" : STR.", \{arg1}";
        String arg2Str = Objects.isNull(arg2) ? "" : STR.", \{arg2}";
        return STR."<\{type}\{arg1Str}\{arg2Str}>";
    }

    /**
     * Display a node in the console
     * @param node node to display
     * @param depth indentation level
     */
    static void displayNode(Node node, int depth) {
        String ident = "  ".repeat(depth);
        int nbNoeudsEnfants =  node.getNumberOfChild();
        String s = STR."\{ident}\{node} (\{nbNoeudsEnfants} child nodes";
        System.out.println(s);
        for(int i = 0; i < nbNoeudsEnfants; i++) {
            displayNode(node.getChildAt(i), depth + 1);
        }
    }

}
