package bibliothek.extension.gui.dock;

import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import bibliothek.extension.gui.dock.preference.PreferenceEditorFactory;
import bibliothek.extension.gui.dock.preference.PreferenceModel;
import bibliothek.extension.gui.dock.preference.PreferenceOperation;
import bibliothek.extension.gui.dock.preference.PreferenceTreeModel;
import bibliothek.util.Path;

/**
 * A panel that shows a {@link JTree} and a {@link PreferenceTable}. The tree
 * is filled by a {@link PreferenceTreeModel}, and the selected node of the
 * tree is shown in the table.
 * @author Benjamin Sigg
 */
public class PreferenceTreePanel extends JPanel{
    private PreferenceTreeModel model;
    
    private JTree tree;
    private PreferenceTable table;
    
    /**
     * Creates a new panel.
     */
    public PreferenceTreePanel(){
        this( null );
    }
    
    /**
     * Creates a new panel.
     * @param model the contents of this panel, might be <code>null</code>
     */
    public PreferenceTreePanel( PreferenceTreeModel model ){
        this.model = model;
        setLayout( new GridLayout( 1, 1 ) );
        JSplitPane pane = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT );
        
        if( model == null ){
        	tree = new JTree( new DefaultTreeModel( new DefaultMutableTreeNode( "<null>" ) ) );
        }
        else{
        	tree = new JTree( model );
        }
        tree.setEditable( false );
        tree.setRootVisible( false );
        tree.setShowsRootHandles( true );
        tree.getSelectionModel().setSelectionMode( TreeSelectionModel.SINGLE_TREE_SELECTION );
        pane.setLeftComponent( new JScrollPane( tree ) );
        
        table = new PreferenceTable();
        pane.setRightComponent( new JScrollPane( table ) );
        
        tree.addTreeSelectionListener( new TreeSelectionListener(){
            public void valueChanged( TreeSelectionEvent e ) {
                checkSelection();
            }
        });
        
        add( pane );
        checkSelection();
    }
    
    /**
     * Adds an operation to this panel. Calling this method before setting a model
     * will allow the client to determine the order of the operations.
     * @param operation the new operation
     * @see PreferenceTable#addOperation(PreferenceOperation)
     */
    public void addOperation( PreferenceOperation operation ){
    	table.addOperation( operation );
    }
    
    /**
     * Sets an editor for some type.
     * @param type the type for which the editor will be used
     * @param factory the factory for new editors
     * @see PreferenceTable#setEditorFactory(Path, PreferenceEditorFactory)
     */
    public void setEditorFactory( Path type, PreferenceEditorFactory<?> factory ){
    	table.setEditorFactory( type, factory );
    }
    
    /**
     * Access to the {@link JTree} which shows the {@link PreferenceTreeModel}.
     * Clients should not change the {@link TreeModel} of the tree. But
     * they can customize the tree, for example by setting a new
     * {@link TreeCellRenderer}.
     * @return the tree used on this panel
     */
    public JTree getTree() {
		return tree;
	}
    
    /**
     * Access to the {@link PreferenceTable} which shows the currently
     * selected {@link PreferenceModel}. Clients should not change the
     * model of the table. But they can customize the table, for example
     * by adding a new {@link PreferenceEditorFactory}.
     * @return the table used on this panel
     */
    public PreferenceTable getTable() {
		return table;
	}
    
    @Override
    public Dimension getPreferredSize() {
    	Dimension size = super.getPreferredSize();
    	if( table.getModel() == null ){
    		size = new Dimension( size.width * 2, size.height );
    	}
    	return size;
    }
    
    private void checkSelection(){
        PreferenceModel preference = null;
        TreePath path = tree.getSelectionPath();
        if( path != null && model != null ){
            PreferenceTreeModel.Node node = (PreferenceTreeModel.Node)path.getLastPathComponent();
            preference = node.getModel();
        }
        table.setModel( preference );
    }
    
    /**
     * Sets the model of this panel.
     * @param model the new model, can be <code>null</code>
     */
    public void setModel( PreferenceTreeModel model ) {
        this.model = model;
        if( model == null ){
        	tree.setModel( new DefaultTreeModel( new DefaultMutableTreeNode( "<null>" ) ) );
        	table.setModel( null );
        }
        else{
        	tree.setModel( model );
        }
    }
    
    /**
     * Gets the model which is shown on this panel.
     * @return the model that is shown
     */
    public PreferenceTreeModel getModel() {
        return model;
    }
}
