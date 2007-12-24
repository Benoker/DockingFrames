package demo;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import bibliothek.gui.DockController;
import bibliothek.gui.dock.DefaultDockable;
import bibliothek.gui.dock.FlapDockStation;
import bibliothek.gui.dock.ScreenDockStation;
import bibliothek.gui.dock.SplitDockStation;

/*
 * The same as "Start" but with more stations and additional settings
 */

public class Demo02_Base {
	public static void main(String[] args) {
		// create a frame
		JFrame frame = new JFrame( "Demo" );
		frame.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
		frame.setSize( 600, 500 );
		
		// the controller manages all operations
		DockController controller = new DockController();
		
		// let the controller handle sub-stations with only one child
		controller.setSingleParentRemove( true );
		
		// some stations
		SplitDockStation station = new SplitDockStation();
		ScreenDockStation screen = new ScreenDockStation( frame );
		FlapDockStation east = new FlapDockStation();
		FlapDockStation west = new FlapDockStation();
		FlapDockStation south = new FlapDockStation();
		FlapDockStation north = new FlapDockStation();
		
		// the stations have to be registered
		frame.add( station, BorderLayout.CENTER );
		frame.add( east.getComponent(), BorderLayout.EAST );
		frame.add( west.getComponent(), BorderLayout.WEST );
		frame.add( north.getComponent(), BorderLayout.NORTH );
		frame.add( south.getComponent(), BorderLayout.SOUTH );

		// the order matters: it tells something about the importance of the
		// stations. A important station has to be added first
		controller.add( east );
		controller.add( west );
		controller.add( north );
		controller.add( south );
		controller.add( station );
		controller.add( screen );
		
		// create two panels
		JPanel black = new JPanel();
		black.setBackground( Color.BLACK );
		black.setOpaque( true );
		
		JPanel green = new JPanel();
		green.setBackground( Color.GREEN );
		green.setOpaque( true );
		
		// add the two panels
		station.drop( new DefaultDockable( black, "Black" ));
		station.drop( new DefaultDockable( green, "Green" ));
		
		// make the whole thing visible
		frame.setVisible( true );
		screen.setShowing( true );
	}
}
