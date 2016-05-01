package bibliothek.help.javadoc.inspection;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import bibliothek.extension.gui.dock.theme.FlatTheme;
import bibliothek.gui.DockFrontend;
import bibliothek.gui.dock.DefaultDockable;
import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.station.split.SplitDockGrid;
import bibliothek.gui.dock.themes.NoStackTheme;
import bibliothek.help.javadoc.Entryable;
import bibliothek.help.model.Entry;

/**
 * A class that shows the structure of a tree of {@link Entryable}s. This
 * object also creates an {@link InspectionPanel} which will be updated whenever
 * the user selects another {@link Node node} of the tree.
 * @author Benjamin Sigg
 *
 */
public class InspectionTree extends JTree{
    /** the panel showing the contents of the currently selected {@link Entryable} */
    private InspectionPanel panel;
    /** shows which {@link Node} to select for a given link */
    private Map<String, Node> links = new HashMap<String, Node>();
    
    /**
     * Opens a frame showing the tree that has <code>entryable</code> as root,
     * and additionally an {@link InspectionPanel}. This method blocks until
     * the frame has been closed.
     * @param entryable the root of the tree
     */
    public static void inspect( Entryable entryable ){
        DockFrontend frontend = new DockFrontend();
        frontend.getController().setTheme( new NoStackTheme( new FlatTheme() ) );
        SplitDockStation station = new SplitDockStation();
        frontend.addRoot( "station", station );
        
        final JFrame frame = new JFrame( "Inspect" );
        
        InspectionTree tree = new InspectionTree( entryable );
        SplitDockGrid grid = new SplitDockGrid();

        grid.addDockable( 0, 0, 1, 1, 
                new DefaultDockable( new JScrollPane( tree ), "Tree" ));
        grid.addDockable( 1, 0, 1, 1, 
                new DefaultDockable( new JScrollPane( tree.getPanel() ), "Entry" ));
        station.dropTree( grid.toTree() );
        
        frame.add( station, BorderLayout.CENTER );
        frame.setBounds( 20, 20, 500, 300 );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        
        final Object LOCK = new Object();
        frame.setVisible( true );
        
        frame.addWindowListener( new WindowAdapter(){
            @Override
            public void windowClosing( WindowEvent e ) {
                frame.setVisible( false );
                synchronized( LOCK ){
                    LOCK.notify();
                }
            }
        });
        
        while( frame.isVisible() ){
            synchronized( LOCK ){
                try {
                    LOCK.wait( 1000 );
                }
                catch( InterruptedException e ) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    /**
     * Creates a new tree, using <code>entryable</code> as root.
     * @param entryable the root
     */
    public InspectionTree( Entryable entryable ){
        panel = new InspectionPanel();
        panel.inspect( null );
        
        DefaultTreeModel model = new DefaultTreeModel( new Node( entryable ));
        setModel( model );
        
        addTreeSelectionListener( new TreeSelectionListener(){
            public void valueChanged( TreeSelectionEvent e ) {
                Node node = (Node)getLastSelectedPathComponent();
                if( node != null )
                    panel.inspect( node.getEntry() );
                else
                    panel.inspect( null );
            }
        });
    }
    
    /**
     * Gets the panel that shows the contents of the currently selected node
     * of this tree.
     * @return the panel
     */
    public InspectionPanel getPanel() {
        return panel;
    }
    
    /**
     * Selects the {@link Entry} which has the name <code>link</code>.
     * @param link the name of the <code>Entry</code>
     */
    public void select( String link ){
        Node node = links.get( link );
        if( node != null ){
             setSelectionPath( new TreePath( node.getPath() ) );
        }
    }

    /**
     * A wrapper of an {@link Entryable} used to easily  combine 
     * <code>Entryable</code> and {@link JTree}.
     * @author Benjamin Sigg
     */
    private class Node extends DefaultMutableTreeNode{
        /** the data of this node */
        private Entry entry;
        
        /**
         * Creates a new node
         * @param entryable the content of this node
         */
        public Node( Entryable entryable ){
            this.entry = entryable.toEntry();
            String link = entry.getType() + ":" + entry.getId();
            setUserObject( link );
            links.put( link, this );
            
            for( Entryable child : entryable.children() )
                add( new Node( child ) );
        }
        
        /**
         * Gets the content of this node.
         * @return the content
         */
        public Entry getEntry() {
            return entry;
        }
    }
}
