package demo;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import bibliothek.gui.DockController;
import bibliothek.gui.dock.DefaultDockable;
import bibliothek.gui.dock.common.action.ReplaceActionGuard;
import bibliothek.gui.dock.station.SplitDockStation;

/*
 * Demo 1:
 * Shows how to setup a very simple client with two panels 
 */

public class Demo01_Start {
	public static void main(String[] args) {
		// create a frame
		JFrame frame = new JFrame( "Demo" );
		frame.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
		frame.setSize( 600, 500 );
		
		// the controller manages all operations
		DockController controller = new DockController();
		
		// Add an action "replace station by child" to the controller.
		// This action allows to remove unnecessary stations by the user.
		controller.addActionGuard( new ReplaceActionGuard( controller ) );
		
		// a station that shows some panels
		SplitDockStation station = new SplitDockStation();
		
		// the station has to be registered
		frame.add( station, BorderLayout.CENTER );
		controller.add( station );
		
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
	}
}
