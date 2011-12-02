package tutorial.core.basics;

import java.awt.Color;

import javax.swing.JPanel;

import tutorial.support.JTutorialFrame;
import tutorial.support.Tutorial;
import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DefaultDockable;
import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.station.split.SplitDockProperty;

/*
 * Hello World - with the core library
 */
@Tutorial(title="Hello World", id="HelloWorld")
public class HelloWorldExample {
	public static void main( String[] args ){
		/* One always needs some frame, dialog or applet to show things.
		 * JTutorialFrame is a normal JFrame which sets up title, size and
		 * closing-listeners */
		JTutorialFrame frame = new JTutorialFrame( HelloWorldExample.class );
		
		/* The DockController is the heart of DF. Through this object any
		 * module can communicate with any other module. Most applications
		 * require exactly one controller. */
		DockController controller = new DockController();
		
		/* Sometimes the framework needs to show a dialog or a window. The
		 * root-window is used as parent of all these internal windows.
		 * If you have a more complex setup, or no JFrame available, you might 
		 * also want to have a look at "setRootWindowProvider". */ 
		controller.setRootWindow( frame );
		
		/* Since many tutorials may run in the same JVM we need todo some 
		 * cleaning up when closing a tutorial. Calling DockController.kill()
		 * will free some resources, and the JTutorialFrame does call this
		 * method automatically. */
		frame.destroyOnClose( controller );
		
		
		/* There are Dockables - the panels the user sees and can drag & drop - and
		 * there are DockStations. DockStations are the parent Components of Dockables,
		 * Before an application can show any Dockable it first needs at least
		 * one station (like you need a window before you can show a button).
		 * There are different stations, SplitDockStation is the most common on. It
		 * shots its children in a grid of varying size. */
		SplitDockStation station = new SplitDockStation();
		
		/* The station needs to be connected to the DockController */
		controller.add( station );
		/* SplitDockStation is a Component and needs to be added to some window. */
		frame.add( station );
		
		/* the basic application is now finished and we can start filling it up with content */
		
		/* Creating a Dockable is very easy. A DefaultDockable basically provides a Container
		 * and some methods to set title and icon. */
		Dockable north = createDockable( "Red", Color.RED );
		
		/* We just drop the first Dockable onto the station. Since there are no other Dockables
		 * on "station" yet, "north" gets all the available space */
		station.drop( north );
		
		/* Let's create a second Dockable */
		Dockable south = createDockable( "Green", Color.GREEN );
		
		/* We drop "south" at the bottom of "station". The location "SplitDockProperty.SOUTH" is
		 * just needed for dropping "south". As the user is able to move around a Dockable as soon
		 * as it is dropped, any initial location cannot be used or depended on later. */
		station.drop( south, SplitDockProperty.SOUTH );
		
		frame.setVisible( true );
	}
	
	private static Dockable createDockable( String title, Color color ){
		DefaultDockable dockable = new DefaultDockable();
		dockable.setTitleText( title );
		
		JPanel panel = new JPanel();
		panel.setOpaque( true );
		panel.setBackground( color );
		dockable.add( panel );
		
		return dockable;
	}
}
