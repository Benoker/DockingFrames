package tutorial.common.basics;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;

import tutorial.core.basics.InternalExample;
import tutorial.support.ColorSingleCDockable;
import tutorial.support.JTutorialFrame;
import tutorial.support.Tutorial;
import bibliothek.gui.dock.ScreenDockStation;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.CLocation;
import bibliothek.gui.dock.station.screen.InternalBoundaryRestriction;
import bibliothek.gui.dock.station.screen.InternalFullscreenStrategy;
import bibliothek.gui.dock.station.screen.window.InternalScreenDockWindowFactory;

@Tutorial(title="JDesktopPane", id="CommonInternal")
public class JDesktopPaneExample {
	public static void main( String[] args ){
		/* DockingFrames has limited support for JDesktopPane and JInternalFrames. 
		 * This example sets up a frame containing a JInternalFrame and shows how to
		 * configure the framework to support this. */
		
		/* As usual we need some frame */
		JTutorialFrame frame = new JTutorialFrame( InternalExample.class );
		
		/* Setting up a new JDesktopPane and a new JInternalFrame, we will add our
		 * content the the JInternalFrame "internalFrame". */
		JDesktopPane desktop = new JDesktopPane();
		frame.add( desktop );
		JInternalFrame internalFrame = new JInternalFrame( "Internal" );
		internalFrame.setResizable( true );
		desktop.add( internalFrame );
		internalFrame.setBounds( 20, 20, 400, 300 );
		internalFrame.setVisible( true );
		internalFrame.setLayout( new BorderLayout() );
		
		/* Creating a controller */
		CControl control = new CControl( frame );
		frame.destroyOnClose( control );
		
		/* The ScreenDockStation needs some special factories and strategies to handle 
		 * the JDesktopPane.
		 *  - The boundary restriction ensures that a window cannot be moved out of the desktop
		 *  - The fullscreen strategy tells when a window is in fullscreen mode and when not
		 *  - The window factory creates the windows on which Dockables are shown */
		control.putProperty( ScreenDockStation.BOUNDARY_RESTRICTION, new InternalBoundaryRestriction( desktop ) );
		control.putProperty( ScreenDockStation.FULL_SCREEN_STRATEGY, new InternalFullscreenStrategy( desktop ) );
		control.putProperty( ScreenDockStation.WINDOW_FACTORY, new InternalScreenDockWindowFactory( desktop ) );
		
		/* Nothing special about the rest of the application, just setting up some stations
		 * and Dockables */
		internalFrame.add( control.getContentArea(), BorderLayout.CENTER );
		
		ColorSingleCDockable green = new ColorSingleCDockable( "Green", Color.GREEN );
		ColorSingleCDockable red = new ColorSingleCDockable( "Red", Color.RED );
		ColorSingleCDockable blue = new ColorSingleCDockable( "Blue", Color.BLUE );
		
		control.addDockable( green );
		control.addDockable( red );
		control.addDockable( blue );
		
		green.setLocation( CLocation.base().normal() );
		green.setVisible( true );
		
		red.setLocation( CLocation.base().minimalNorth() );
		red.setVisible( true );
		
		blue.setLocation( CLocation.external( 300, 200, 200, 100 ) );
		blue.setVisible( true );
		
		/* Now we make all frames and windows visible. */
		frame.setVisible( true );
	}
}
