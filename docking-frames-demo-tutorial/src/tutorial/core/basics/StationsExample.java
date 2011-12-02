package tutorial.core.basics;

import java.awt.BorderLayout;
import java.awt.Color;

import tutorial.support.ColorDockable;
import tutorial.support.JTutorialFrame;
import tutorial.support.Tutorial;
import bibliothek.gui.DockController;
import bibliothek.gui.dock.FlapDockStation;
import bibliothek.gui.dock.ScreenDockStation;
import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.StackDockStation;
import bibliothek.gui.dock.station.screen.ScreenDockProperty;
import bibliothek.gui.dock.station.split.SplitDockProperty;

@Tutorial(title="The Stations", id="TheStations")
public class StationsExample {
	public static void main( String[] args ){
		JTutorialFrame frame = new JTutorialFrame( StationsExample.class );
		
		DockController controller = new DockController();
		controller.setRootWindow( frame );
		frame.destroyOnClose( controller );
		
		/* As you already know, any Dockable needs a DockStation as parent. There are
		 * four implementations of DockStation, this example shows them all. */
		
		
		/* ** SplitDockStation ** */
		
		/* You have seen SplitDockStation in the first example. SplitDockStation actually 
		 * contains a tree: the leafs are Dockables and each node is a rectangle either split 
		 * horizontally or vertically. The user can grab the gap between the children of a node
		 * and assign different sizes to them. */
		SplitDockStation splitDockStation = new SplitDockStation();
		controller.add( splitDockStation );
		frame.add( splitDockStation );
		
		/* Let's drop some Dockables on the SplitDockStation, later we will add a StackDockStation at the right side */
		splitDockStation.drop( new ColorDockable( "Split NORTH-WEST", Color.RED ));
		splitDockStation.drop( new ColorDockable( "Split SOUTH_WEST", Color.GREEN ), new SplitDockProperty( 0, 0.5, 1, 0.5 ));
		
		
		/* ** StackDockStation ** */
		
		/* Basically a StackDockStation is a JTabbedPane showing only one of many Dockables. 
		 * 
		 * If you drag away a child of this station and the station remains with one child, then the framework
		 * replaces the station by its remaining child, effectively deleting the station. On the other hand if
		 * you drop a Dockable over another one the framework creates a new StackDockStation*/
		StackDockStation stackDockStation = new StackDockStation();
		
		/* Let's put something onto "stackDockStation" */
		stackDockStation.drop( new ColorDockable( "Stack 1", Color.BLUE ));
		stackDockStation.drop( new ColorDockable( "Stack 2", Color.YELLOW )); 
		
		/* StackDockStation itself is a Dockable, so we can drop "stackDockStation" on "splitDockStation". 
		 * Note that we do not need to add "stackDockStation" to "controller". Because "splitDockStation"
		 * is already known to "controller", all its children are known too. */
		splitDockStation.drop( stackDockStation, new SplitDockProperty( 0.5, 0, 0.5, 1.0 ));
		
		
		/* ** FlapDockStation ** */
		
		/* FlapDockStation is very similar to StackDockStation: it shows only one child at a time. This
		 * station will open a window that covers other Dockables to show its selected child. */
		FlapDockStation flapDockStation = new FlapDockStation();
		controller.add( flapDockStation );
		frame.add( flapDockStation.getComponent(), BorderLayout.NORTH );
		
		/* Let's add some Dockables to the station */
		flapDockStation.drop( new ColorDockable( "Flap 1", Color.WHITE ));
		flapDockStation.drop( new ColorDockable( "Flap 2", Color.BLACK ));
		
		/* ** ScreenDockStation ** */
		
		/* ScreenDockStation is responsible for free floaing Dockables. To be more exact, this station
		 * opens a new window for each child, the user can move and resize these windows.
		 * The station needs a root-window as parent for its own new windows. The best idea is to
		 * use the same window as root that "controller" already uses, forwarding the "rootWindowProvider"
		 * will accomplish that. */
		ScreenDockStation screenDockStation = new ScreenDockStation( controller.getRootWindowProvider() );
		controller.add( screenDockStation );
		
		/* Let's add a child, in this case we need also to tell the station where exactly to show the child on the screen */
		screenDockStation.drop( new ColorDockable( "Screen", Color.MAGENTA ), new ScreenDockProperty( 600, 200, 400, 300 ));
		
		/* Now we make all frames and windows visible. */
		frame.setVisible( true );
		screenDockStation.setShowing( true );
	}
}
