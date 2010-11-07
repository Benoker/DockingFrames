package tutorial.core.basics;

import java.awt.Color;

import tutorial.support.ColorDockable;
import tutorial.support.JTutorialFrame;
import tutorial.support.Tutorial;
import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.StackDockStation;
import bibliothek.gui.dock.control.SingleParentRemover;
import bibliothek.gui.dock.station.split.DockableSplitDockTree;
import bibliothek.gui.dock.station.split.SplitDockGrid;
import bibliothek.gui.dock.station.split.SplitDockProperty;
import bibliothek.gui.dock.station.stack.StackDockProperty;

@Tutorial(title="SplitDockStation", id="SplitDockStation")
public class SplitDockStationExample {
	public static void main( String[] args ){
		/* Understanding how to use the Stack, Flap and ScreenDockStation is easy. The first
		 * two are nothing else than lists, on the third the children do not influence each
		 * other in any way.
		 * 
		 * SplitDockStation on the other hand is rather complex and you may need some training
		 * to put Dockables at the location you want them. This example consists of three
		 * methods "create...", each of them sets up a SplitDockStation in another way, but
		 * all of them lead to the same result.
		 * 
		 * Since SplitDockStation is a Dockable itself, we can show them grouped together
		 * on a StackDockStation.
		 * */
		
		JTutorialFrame frame = new JTutorialFrame( SplitDockStationExample.class );
		DockController controller = new DockController();
		controller.setRootWindow( frame );
		frame.destroyOnClose( controller );
		
		/* In order to play around we disable the automatic replacement of stations which have
		 * only one child. You should use this feature with care in real applications. */
		controller.setSingleParentRemover( new SingleParentRemover(){
			protected boolean shouldTest( DockStation station ){
				if( station instanceof SplitDockStation ){
					return false;
				}
				return super.shouldTest( station );
			}
		});
		
		/* Set up some base station */
		SplitDockStation station = new SplitDockStation();
		controller.add( station );
		frame.add( station );
		
		StackDockStation stack = new StackDockStation();
		
		/* We now collect the different examples. Each station contains six children, three of
		 * them are grouped together. In the group we would like the middle on (green) to be selected.
		 * 
		 * We would also like to let the framework handle StackDockStations, we no longer
		 * create them explicitly.
		 * 
		 * The exact layout looks like this:
		 * 
		 *     40 %          60 % 
		 * -----------------------------
		 * |          |                | 
		 * |          |     Yellow     | 30 % 
		 * |  Red     |                |
		 * |  Green   |-----------------
		 * |  Blue    |      |         |
		 * |          |      |         |
		 * |          |      |         |
		 * |          | Cyan | Magenta | 70 %
		 * |          |      |         |
		 * |          |      |         |
		 * |          |      |         |
		 * -----------------------------
		 *    40 %     20 %    40 %
		 * 
		 *  
		 * Have a look at the "create..." methods to understand what is going on */
		stack.drop( createLayoutSequential() );
		stack.drop( createLayoutTree() );
		stack.drop( createLayoutGrid() );
		
		/* Conclusion:
		 * 
		 * ---------------------------------------------------------------------------------
		 * What          Pros                           Cons
		 * ---------------------------------------------------------------------------------
		 * Sequential     no need to add all             Very hard to use
		 *                Dockables at the same time
		 * ---------------------------------------------------------------------------------                     
		 * Tree           Absolute control of the        Not intuitive, you cannot just
		 *                layout                         set a location but need to consider
		 *                                               the current layout of the tree
		 *                                               
		 *                                               You need to build the entire layout
		 *                                               at once
		 * ---------------------------------------------------------------------------------
		 * Grid           Very easy, you can paint       You need to build the entire layout
		 *                your layout on a sheet of      at once
		 *                paper, measure and copy the
		 *                position of the Dockables 
		 * ---------------------------------------------------------------------------------
		 */
		
		/* Select the first tab */
		station.drop( stack );
		
		controller.setFocusedDockable( stack.getDockable( 0 ), false );
	
		frame.setVisible( true );
	}
	
	/* ** Adding one Dockable after another ** */
	private static SplitDockStation createLayoutSequential(){
		SplitDockStation station = new SplitDockStation();
		station.setTitleText("Sequential");
		
		/* Prepare the Dockables that we are going to drop on "station" */
		Dockable red = new ColorDockable( "Red", Color.RED );
		Dockable green =  new ColorDockable( "Green", Color.GREEN );
		Dockable blue = new ColorDockable( "Blue", Color.BLUE );		
		Dockable yellow = new ColorDockable( "Yellow", Color.YELLOW );
		Dockable cyan = new ColorDockable( "Cyan", Color.CYAN );
		Dockable magenta = new ColorDockable( "Magenta", Color.MAGENTA );
		
		/* We now drop one Dockable after the other on the station. Each time
		 * we call "drop" the layout of the children is changed. So we need to
		 * be very carefully in what we do and in the order we do it. */
		
		/* The first Dockable does not need a location, it gets all space because
		 * there are no other Dockables yet. */
		station.drop( red );
		
		/* In order to group "blue" with "red" we assign the same location to "blue"
		 * as "red" already has */
		station.drop( blue, new SplitDockProperty(0, 0, 1.0, 1.0 ));
		
		/* We want "green" to be between "red" and "blue" and we want "green" to be selected.
		 * For the selection we need to add "green" last (the newest Dockable gets selected).
		 * We can set the position by using a "successor" position. "greenLocation" tells
		 * "station" that "green" is grouped with "red" and "blue", where as the StackProperty
		 * tells the existing StackDockStation where to put "green" */
		SplitDockProperty greenLocation = new SplitDockProperty(0, 0, 1.0, 1.0 );
		greenLocation.setSuccessor( new StackDockProperty( 1 ));
		station.drop( green, greenLocation );
		
		/* The group "red/green/blue" has now position 0/0/1/1 and we put "yellow" at the 
		 * right side of this rectangle */
		station.drop( yellow, new SplitDockProperty( 0.4, 0.0, 0.6, 1.0 ));
		
		/* Now we cut of some parts of "yellow" and replace them by "cyan" */
		station.drop( cyan, new SplitDockProperty( 0.4, 0.3, 0.6, 0.7 ));
		
		/* Finally "magenta" cuts out some parts of "cyan" and replaces them */ 
		station.drop( magenta, new SplitDockProperty( 0.6, 0.3, 0.4, 0.7 ));
		
		return station;
	}
	
	/* ** Work with the interal structure of SplitDockStation ** */
	private static SplitDockStation createLayoutTree(){
		SplitDockStation station = new SplitDockStation();
		station.setTitleText("Tree");
		
		/* Prepare the Dockables we are going to put onto "station" */
		Dockable red = new ColorDockable( "Red", Color.RED, 0.5f );
		Dockable green =  new ColorDockable( "Green", Color.GREEN, 0.5f );
		Dockable blue = new ColorDockable( "Blue", Color.BLUE, 0.5f );		
		Dockable yellow = new ColorDockable( "Yellow", Color.YELLOW, 0.5f );
		Dockable cyan = new ColorDockable( "Cyan", Color.CYAN, 0.5f );
		Dockable magenta = new ColorDockable( "Magenta", Color.MAGENTA, 0.5f );
		
		/* Internally SplitDockStation is organized as binary tree. Each node represents
		 * a rectangle of the station, starting with the root at 0/0/1/1. Each node
		 * splits its rectangle either vertically or horizontally for its two children. And
		 * a leaf represnts a Dockable.
		 *
		 * With the help of a SplitDockTree we can build up a tree and tell "station"
		 * to copy its layout from it. */
		DockableSplitDockTree tree = new DockableSplitDockTree();
		
		/* A SplitDockTree is built from bottom to top. We start by creating the group
		 * of "red", "green" and "blue". As you see there is a method that does exactly
		 * what we need to have, it even sets the selected Dockable.
		 * The method returns a key, this key represents the leaf we just created. */
		DockableSplitDockTree.Key group = tree.put( new Dockable[]{ red, green, blue }, green );
		
		/* We now add "cyan" and "magenta", which are neighbors, at the same time. Calling
		 * "tree.horizontal( tree.put( cyan ), tree.put( magenta ), 1.0/3.0" would have the
		 * same effect, this method is just a shortcut.
		 * The third parameter "1.0/3.0" tells the tree that "cyan" has the size "1/3 w" and
		 * "magenta" has the size "2/3 w", where "w" is the width in pixel that will be
		 * assigned to the node "bottomRight".  */
		DockableSplitDockTree.Key bottomRight = tree.horizontal( cyan, magenta, 1.0/3.0 );
		
		/* We create a leaf for "yellow" */
		DockableSplitDockTree.Key keyYellow = tree.put( yellow );
		
		/* The "bottomRight" Dockables, cyan and magenta, are a neighbor to "yellow" which is
		 * at the top right. So we need to combine them vertically. */
		DockableSplitDockTree.Key right = tree.vertical( keyYellow, bottomRight, 0.3 );
		
		/* At last we have the left and the right side of our layout. We now bring them
		 * together. */
		DockableSplitDockTree.Key root = tree.horizontal( group, right, 0.4 );
		
		/* We built up the tree and are now telling "tree" which node is the root */
		tree.root( root );
		
		/* Finally we instruct "station" to change its layout such that it matches "tree" */
		station.dropTree( tree );
		
		return station;
	}
	
	/* ** Using helpful algorithms ** */
	private static SplitDockStation createLayoutGrid(){
		SplitDockStation station = new SplitDockStation();
		station.setTitleText("Grid");
		
		/* Prepare the Dockables we are going to put onto "station" */
		Dockable red = new ColorDockable( "Red", Color.RED, 2.5f );
		Dockable green =  new ColorDockable( "Green", Color.GREEN, 2.5f );
		Dockable blue = new ColorDockable( "Blue", Color.BLUE, 2.5f );		
		Dockable yellow = new ColorDockable( "Yellow", Color.YELLOW, 2.5f );
		Dockable cyan = new ColorDockable( "Cyan", Color.CYAN, 2.5f );
		Dockable magenta = new ColorDockable( "Magenta", Color.MAGENTA, 2.5f );
		
		/* You already have seen two ways to setup the layout of a SplitDockStation, chances
		 * are you liked none of them. The SplitDockGrid makes things much easier: you 
		 * only need to tell the grid where some Dockable should end up, the grid will
		 * calculate a layout of from this information. There is no need to add follow a 
		 * particular order when creating the grid.
		 *  */
		SplitDockGrid grid = new SplitDockGrid();
		
		/* We start by creating the group, which should be on the left side taking
		 * up 40% of the space. */
		grid.addDockable( 0, 0, 40, 100, red, green, blue );
		
		/* Then we ensure that in our new  group "green" is selected. You may notice
		 * that we need to type in the location of "green" again. That is because
		 * the location is used as key in a map, and "0/0/40/100" now points to the
		 * group "red/green/blue". */
		grid.setSelected( 0, 0, 40, 100, green );
		
		/* And then we add the other Dockables at the location they should appear later */
		grid.addDockable( 40, 0, 60, 30, yellow );
		grid.addDockable( 40, 30, 20, 70, cyan );
		grid.addDockable( 60, 30, 40, 70, magenta );
		
		/* With "toTree" we convert "grid" into a SplitDockTree, then we can replace the
		 * layout of "station" with this tree. */
		station.dropTree( grid.toTree() );
		
		return station;	
	}
}
