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
import bibliothek.gui.dock.station.SplitDockStation;
import bibliothek.gui.dock.station.split.SplitDockGrid;
import bibliothek.gui.dock.themes.NoStackTheme;
import bibliothek.help.javadoc.Entryable;
import bibliothek.help.model.Entry;

public class InspectionTree extends JTree{
    private InspectionPanel panel;
    private Map<String, Node> links = new HashMap<String, Node>();
    
    public static void inspect( Entryable entryable ){
        DockFrontend frontend = new DockFrontend();
        frontend.getController().setTheme( new NoStackTheme( new FlatTheme() ) );
        SplitDockStation station = new SplitDockStation();
        frontend.addRoot( station, "station" );
        
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
    
    public InspectionTree( Entryable entryable ){
        panel = new InspectionPanel( this );
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
    
    public InspectionPanel getPanel() {
        return panel;
    }
    
    public void select( String link ){
        Node node = links.get( link );
        if( node != null ){
             setSelectionPath( new TreePath( node.getPath() ) );
        }
    }

    private class Node extends DefaultMutableTreeNode{
        private Entry entry;
        
        public Node( Entryable entryable ){
            this.entry = entryable.toEntry();
            String link = entry.getType() + ":" + entry.getId();
            setUserObject( link );
            links.put( link, this );
            
            for( Entryable child : entryable.children() )
                add( new Node( child ) );
        }
        
        public Entry getEntry() {
            return entry;
        }
    }
}
