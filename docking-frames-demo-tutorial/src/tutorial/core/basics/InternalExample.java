package tutorial.core.basics;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;

import tutorial.support.ColorDockable;
import tutorial.support.JTutorialFrame;
import tutorial.support.Tutorial;
import bibliothek.gui.DockController;
import bibliothek.gui.dock.FlapDockStation;
import bibliothek.gui.dock.ScreenDockStation;
import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.station.screen.InternalBoundaryRestriction;
import bibliothek.gui.dock.station.screen.InternalFullscreenStrategy;
import bibliothek.gui.dock.station.screen.ScreenDockProperty;
import bibliothek.gui.dock.station.screen.window.InternalScreenDockWindowFactory;
import bibliothek.gui.dock.util.DockProperties;

@Tutorial(title="JDesktopPane", id="Internal")
public class InternalExample {
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
		DockController controller = new DockController();
		controller.setRootWindow( frame );
		frame.destroyOnClose( controller );
		
		/* The ScreenDockStation needs some special factories and strategies to handle 
		 * the JDesktopPane.
		 *  - The boundary restriction ensures that a window cannot be moved out of the desktop
		 *  - The fullscreen strategy tells when a window is in fullscreen mode and when not
		 *  - The window factory creates the windows on which Dockables are shown */
		DockProperties properties = controller.getProperties();
		properties.set( ScreenDockStation.BOUNDARY_RESTRICTION, new InternalBoundaryRestriction( desktop ) );
		properties.set( ScreenDockStation.FULL_SCREEN_STRATEGY, new InternalFullscreenStrategy( desktop ) );
		properties.set( ScreenDockStation.WINDOW_FACTORY, new InternalScreenDockWindowFactory( desktop ) );
		
		/* Nothing special about the rest of the application, just setting up some stations
		 * and Dockables */
		SplitDockStation center = new SplitDockStation();
		controller.add( center );
		internalFrame.add( center, BorderLayout.CENTER );
		
		/* The FlapDockStation will recognize and handle the JDesktopPane automatically */
		FlapDockStation north = new FlapDockStation();
		controller.add( north );
		internalFrame.add( north.getComponent(), BorderLayout.NORTH );
		
		ScreenDockStation screen = new ScreenDockStation( controller.getRootWindowProvider() );
		controller.add( screen );
		
		center.drop( new ColorDockable( "Green", Color.GREEN ));
		north.drop( new ColorDockable( "Red", Color.RED ));
		screen.drop( new ColorDockable( "Blue", Color.BLUE ), new ScreenDockProperty( 300, 200, 100, 100 ) );
		
		/* Now we make all frames and windows visible. */
		frame.setVisible( true );
		screen.setShowing( true );
	}
}
