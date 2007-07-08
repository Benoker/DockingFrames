package bibliothek.help.model;

public class HierarchyNode {
    private String name;
    private String type;
    private HierarchyNode[] children;
    
    public HierarchyNode( String name, String type, HierarchyNode[] children ){
        this.name = name;
        this.type = type;
        this.children = children;
    }
    
    public String getName() {
        return name;
    }
    
    public String getType() {
        return type;
    }
    
    public int getChildrenCount(){
        return children.length;
    }
    
    public HierarchyNode getChild( int index ){
        return children[ index ];
    }
}
