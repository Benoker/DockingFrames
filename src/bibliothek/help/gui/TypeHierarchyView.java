package bibliothek.help.gui;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.*;

import bibliothek.gui.dock.DefaultDockable;
import bibliothek.help.control.LinkManager;
import bibliothek.help.control.Linking;
import bibliothek.help.model.Entry;
import bibliothek.help.model.HierarchyNode;

public class TypeHierarchyView extends DefaultDockable implements Linking{
    private JTree tree;
    
    public TypeHierarchyView( LinkManager manager ){
        setTitleText( "Hierarchy" );
        
        manager.add( this );
        
        tree = new JTree();
        setLayout( new BorderLayout() );
        add( new JScrollPane( tree ), BorderLayout.CENTER );
        
        tree.setModel( new DefaultTreeModel( new DefaultMutableTreeNode()) );
    }
    
    public void selected( List<Entry> list ) {
        for( Entry entry : list ){
            if( entry.getType().equals( "hierarchy-class" )){
                HierarchyNode node = entry.toSubHierarchy();
                MutableTreeNode model = toModel( node );
                tree.setModel( new DefaultTreeModel( model ) );
                expandAll( model, new TreePath( model ) );
                break;
            }
        }
    }
    
    private MutableTreeNode toModel( HierarchyNode node ){
        DefaultMutableTreeNode model = new DefaultMutableTreeNode( "(" + node.getType() + ") " + node.getName() );
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
}
