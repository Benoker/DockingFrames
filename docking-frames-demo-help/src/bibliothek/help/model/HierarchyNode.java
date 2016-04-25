package bibliothek.help.model;

/**
 * A node of a hierarchy-tree. The tree tells how classes and interfaces
 * are extended and implemented by each other. The root of the tree is
 * an interface or class, note that the root is not {@link Object}.
 * @author Benjamin Sigg
 *
 */
public class HierarchyNode {
    /** the name of the class or interface represented by this node */
    private String name;
    /** the kind of java-element that is represented by this node */
    private String type;
    /** children of this node */
    private HierarchyNode[] children;
    
    /**
     * Creates a new node.
     * @param name the name of the class or interface represented by this node
     * @param type the kind of java-element that this node represents
     * @param children interfaces and classes which the java-element, 
     * that is represented by this node, extends or implements.
     */
    public HierarchyNode( String name, String type, HierarchyNode[] children ){
        this.name = name;
        this.type = type;
        this.children = children;
    }
    
    /**
     * Gets the name of the class or interface that is represented by this node.
     * @return the name
     */
    public String getName() {
        return name;
    }
    
    /**
     * Gets the kind of java-element that is represented by this node.
     * @return the kind of element
     */
    public String getType() {
        return type;
    }
    
    /**
     * Gets the number of children this node has.
     * @return the number of children
     */
    public int getChildrenCount(){
        return children.length;
    }
    
    /**
     * Gets the <code>index</code>'th child of this node.
     * @param index the location of the child
     * @return the child
     * @throws ArrayIndexOutOfBoundsException if <code>index</code> is not
     * a valid position
     */
    public HierarchyNode getChild( int index ){
        return children[ index ];
    }
}
