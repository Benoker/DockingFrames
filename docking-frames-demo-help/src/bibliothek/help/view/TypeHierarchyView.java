package bibliothek.help.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.List;

import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.*;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DefaultDockable;
import bibliothek.help.control.LinkManager;
import bibliothek.help.control.Linking;
import bibliothek.help.control.Undoable;
import bibliothek.help.model.Entry;
import bibliothek.help.model.HierarchyNode;
import bibliothek.help.util.ResourceSet;

/**
 * A {@link Dockable} that shows the type-hierarchy of a class or interface. The
 * hierarchy is encoded in an {@link Entry} and the method {@link Entry#toSubHierarchy()}
 * is used to decode it.<br>
 * This class implements {@link Linking} and can update its content automatically
 * using a {@link LinkManager}.
 * @author Benjamin Sigg
 *
 */
public class TypeHierarchyView extends DefaultDockable implements Linking, Undoable{
    /** the visual representation of the hierarchy-tree */
    private JTree tree;
    /** the tree */
    private Entry entry;
    
    /**
     * Creates a new view.
     * @param manager A manager to which this view will add a listener. This
     * view will update its content whenever that listener is called.
     */
    public TypeHierarchyView( LinkManager manager ){
        setTitleText( "Hierarchy" );
        setTitleIcon( ResourceSet.ICONS.get( "hierarchy" ) );
        
        manager.add( this );
        manager.getUR().register( this );
        
        tree = new JTree(){
        	@Override
        	public void updateUI(){
        		super.updateUI();
        		setCellRenderer( new Renderer() );
        	}
        };
        setLayout( new BorderLayout() );
        add( new JScrollPane( tree ), BorderLayout.CENTER );
        
        tree.setModel( new DefaultTreeModel( new DefaultMutableTreeNode()) );
    }
    
    public Entry getCurrent(){
    	return entry;
    }
    
    public void setCurrent( Entry entry ){
    	if( this.entry != entry ){
	    	this.entry = entry;
	    	HierarchyNode node = entry.toSubHierarchy();
	        if( node == null ){
	        	tree.setModel( new DefaultTreeModel( new DefaultMutableTreeNode( "< empty >" )) );        	
	        }
	        else{
	        	MutableTreeNode model = toModel( node );
	        	tree.setModel( new DefaultTreeModel( model ) );
	        	expandAll( model, new TreePath( model ) );
	        }
    	}
    }
    
    public void selected( List<Entry> list ) {
        for( Entry entry : list ){
            if( entry.getType().equals( "hierarchy-class" ) || entry.getType().equals( "empty" )){
            	setCurrent( entry );
                break;
            }
        }
    }
    
    /**
     * Wraps <code>node</code> into a {@link TreeNode} such that it can
     * be shown in a {@link JTree}.
     * @param node a node in a tree
     * @return a wrapper around <code>node</code> and all its children
     */
    private MutableTreeNode toModel( HierarchyNode node ){
        DefaultMutableTreeNode model = new DefaultMutableTreeNode( node );
        for( int i = 0, n = node.getChildrenCount(); i<n; i++ )
            model.add( toModel( node.getChild( i )) );
        
        return model;
    }
    
    /**
     * Ensures that <code>node</code> and all children of <code>node</code>
     * are expanded.
     * @param node a node
     * @param path the path to <code>node</code>
     */
    private void expandAll( TreeNode node, TreePath path ){
        tree.expandPath( path );
        for( int i = 0, n = node.getChildCount(); i<n; i++ ){
            TreeNode child = node.getChildAt( i );
            TreePath next = path.pathByAddingChild( child );
            expandAll( child, next );
        }
    }
    
    /**
     * A {@link TreeCellRenderer} that shows an icon for nodes which
     * represent classes or interfaces.
     * @author Benjamin Sigg
     *
     */
    private class Renderer extends DefaultTreeCellRenderer{
        @Override
        public Component getTreeCellRendererComponent( JTree tree,
                Object value, boolean sel, boolean expanded, boolean leaf,
                int row, boolean hasFocus ) {
            
            Object user = ((DefaultMutableTreeNode)value).getUserObject();
            if( user instanceof HierarchyNode ){
                HierarchyNode node = (HierarchyNode)user;
                super.getTreeCellRendererComponent( tree, node.getName(), sel, expanded, leaf, row, hasFocus );
                if( node.getType().equals( "c" ))
                    setIcon( ResourceSet.ICONS.get( "class" ) );
                else if( node.getType().equals( "i" ))
                    setIcon( ResourceSet.ICONS.get( "interface" ) );
                else
                    setIcon( null );
            }
            else
                super.getTreeCellRendererComponent( tree, value, sel, expanded, leaf, row, hasFocus );
            
            return this;
        }
    }
}
