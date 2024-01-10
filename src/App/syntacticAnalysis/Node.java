package App.syntacticAnalysis;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Node {
    private final List<Node> child = new ArrayList<>();
    private final NodeType type;
    private Object value;

    /**
     * Create a node with a value
     * @param nodeType The type of the node
     * @param value The value of the node
     */
    public Node(NodeType nodeType, Object value) {
        this.type = nodeType;
        this.value = value;
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
     * Get the children of the node
     * @return The children of the node
     */
    public List<Node> getChildren() {
        return this.child;
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
     * Get the value of the node
     * @return The value of the node
     */
    public Object getValue() {
        return value;
    }

    /**
     * Display a node in a string
     * @return The string representation of the node
     */
    public String toString() {
        String type = Objects.isNull(this.type) ? "" : this.type.toString();
        String valueStr = Objects.isNull(value) ? "" : STR.", \{value}";
        return STR."<\{type}\{valueStr}>";
    }

    /**
     * Display a node in the console
     * @param node node to display
     * @param depth indentation level
     */
    private static void displayNode(Node node, int depth) {
        String ident = " \t\t".repeat(depth);
        int nbNoeudsEnfants =  node.getNumberOfChild();
        String s = STR."\{ident}\{node} (\{nbNoeudsEnfants} child nodes)";
        System.out.println(s);
        for(int i = 0; i < nbNoeudsEnfants; i++) {
            displayNode(node.getChildAt(i), depth + 1);
        }
    }

    /**
     * Display a node in the console
     * @param node node to display
     */
    public static void displayNode(Node node) {
        displayNode(node, 0);
    }

}
