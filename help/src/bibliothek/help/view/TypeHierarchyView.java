package bibliothek.help.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.List;

import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.*;

import bibliothek.gui.dock.DefaultDockable;
import bibliothek.help.control.LinkManager;
import bibliothek.help.control.Linking;
import bibliothek.help.control.Undoable;
import bibliothek.help.model.Entry;
import bibliothek.help.model.HierarchyNode;
import bibliothek.help.util.ResourceSet;

public class TypeHierarchyView extends DefaultDockable implements Linking, Undoable{
    private JTree tree;
    private Entry entry;
    
    public TypeHierarchyView( LinkManager manager ){
        setTitleText( "Hierarchy" );
        setTitleIcon( ResourceSet.ICONS.get( "hierarchy" ) );
        
        manager.add( this );
        manager.getUR().register( this );
        
        tree = new JTree();
        setLayout( new BorderLayout() );
        add( new JScrollPane( tree ), BorderLayout.CENTER );
        
        tree.setModel( new DefaultTreeModel( new DefaultMutableTreeNode()) );
        tree.setCellRenderer( new Renderer() );
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
    
    private MutableTreeNode toModel( HierarchyNode node ){
        DefaultMutableTreeNode model = new DefaultMutableTreeNode( node );
        for( int i = 0, n = node.getChildrenCount(); i<n; i++ )
            model.add( toModel( node.getChild( i )) );
        
        return model;
    }
    
    private void expandAll( TreeNode node, TreePath path ){
        tree.expandPath( path );
        for( int i = 0, n = node.getChildCount(); i<n; i++ ){
            TreeNode child = node.getChildAt( i );
            TreePath next = path.pathByAddingChild( child );
            expandAll( child, next );
        }
    }
    
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
