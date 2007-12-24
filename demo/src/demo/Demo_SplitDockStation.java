package demo;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DefaultDockable;
import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.station.split.SplitDockGrid;
import bibliothek.gui.dock.station.split.SplitDockTree;

/*
 * Shows how easy the contents of a SplitDockStation can be created, if a
 * SplitDockGrid is used. 
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
        Map<Character, Dockable[]> map = new HashMap<Character, Dockable[]>();
        map.put( 'a', new Dockable[]{ createDockable( "White", Color.WHITE )});
        map.put( 'b', new Dockable[]{ createDockable( "Green", Color.GREEN )});
        map.put( 'c', new Dockable[]{ createDockable( "Red", Color.RED )});
        map.put( 'd', new Dockable[]{ createDockable( "Black", Color.BLACK )});
        map.put( 'e', new Dockable[]{ createDockable( "Blue", Color.BLUE )});
        map.put( 'f', new Dockable[]{ createDockable( "Yellow", Color.YELLOW )});
        
        String layout =
            "aaabbcccc \n"+
            "aaabbcccc \n"+
            "aaabbcccc \n"+
            "ddeeeeeff \n"+
            "ddeeeeeff \n"+
            "ddeeeeeff ";
        
        SplitDockGrid grid = new SplitDockGrid( layout, map );
        
        return grid.toTree();
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
