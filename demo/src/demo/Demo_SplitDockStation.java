package demo;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DefaultDockable;
import bibliothek.gui.dock.station.SplitDockStation;
import bibliothek.gui.dock.station.split.SplitDockTree;

/*
 * Shows how easy the contents of a SplitDockStation can be created, if a
 * SplitDockTree is used.
 */

public class Demo_SplitDockStation {
	public static void main(String[] args) {
		// create a frame
		JFrame frame = new JFrame( "Demo" );
		frame.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
		frame.setSize( 600, 500 );
		
		// the controller manages all operations
		DockController controller = new DockController();
		
		// let the controller handle sub-stations with only one child
		controller.setSingleParentRemove( true );
		
		// a station
		SplitDockStation station = new SplitDockStation();
		
		// the station has to be registered
		frame.add( station, BorderLayout.CENTER );
		
		// register the station
		controller.add( station );
		
		// add some children
		station.dropTree( createTree() );
		
		// make the whole thing visible
		frame.setVisible( true );
	}
	
	/*
	 * Creates the tree of the station
	 */
	public static SplitDockTree createTree(){
		SplitDockTree tree = new SplitDockTree();
		return tree.root( tree.horizontal( 
				tree.vertical( 
						tree.put( createDockable( "White", Color.WHITE ) ), 
						tree.vertical( 
								createDockable( "Green", Color.GREEN ), 
								createDockable( "Red", Color.RED ) ), 
							0.333 ),
				tree.vertical( 
						tree.put( createDockable( "Black", Color.BLACK ) ), 
						tree.vertical( 
								createDockable( "Blue", Color.BLUE ), 
								createDockable( "Yellow", Color.YELLOW )), 
							0.333 )) );
	}
	
	/*
	 * Creates a new child
	 */
	public static Dockable createDockable( String name, Color color ){
		JPanel panel = new JPanel();
		panel.setBackground( color );
		panel.setOpaque( true );
		return new DefaultDockable( panel, name );
	}
}
