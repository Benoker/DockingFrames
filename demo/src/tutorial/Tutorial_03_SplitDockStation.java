package tutorial;

import java.awt.Color;

import tutorial.support.ColorDockable;
import tutorial.support.JTutorialFrame;
import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.StackDockStation;
import bibliothek.gui.dock.control.SingleParentRemover;
import bibliothek.gui.dock.station.split.SplitDockGrid;
import bibliothek.gui.dock.station.split.SplitDockProperty;
import bibliothek.gui.dock.station.split.SplitDockTree;
import bibliothek.gui.dock.station.stack.StackDockProperty;

public class Tutorial_03_SplitDockStation {
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
		
		JTutorialFrame frame = new JTutorialFrame( Tutorial_03_SplitDockStation.class );
		DockController controller = new DockController();
		controller.setRootWindow( frame );
		
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
		
		/* We now drop one Dockable after the other on the station. */
		
		station.drop( red );
		station.drop( blue, new SplitDockProperty(0, 0, 1.0, 1.0 ));
		
		SplitDockProperty greenLocation = new SplitDockProperty(0, 0, 1.0, 1.0 );
		greenLocation.setSuccessor( new StackDockProperty( 1 ));
		station.drop( green, greenLocation );
		
		station.drop( yellow, new SplitDockProperty( 0.4, 0.0, 0.6, 1.0 ));
		station.drop( cyan, new SplitDockProperty( 0.4, 0.3, 0.6, 0.7 ));
		station.drop( magenta, new SplitDockProperty( 0.6, 0.3, 0.4, 0.7 ));
		
		return station;
	}
	
	private static SplitDockStation createLayoutTree(){
		SplitDockStation station = new SplitDockStation();
		station.setTitleText("Tree");
		
		Dockable red = new ColorDockable( "Red", Color.RED );
		Dockable green =  new ColorDockable( "Green", Color.GREEN );
		Dockable blue = new ColorDockable( "Blue", Color.BLUE );		
		Dockable yellow = new ColorDockable( "Yellow", Color.YELLOW );
		Dockable cyan = new ColorDockable( "Cyan", Color.CYAN );
		Dockable magenta = new ColorDockable( "Magenta", Color.MAGENTA );
		
		SplitDockTree tree = new SplitDockTree();
		SplitDockTree.Key group = tree.put( new Dockable[]{ red, green, blue }, green );
		SplitDockTree.Key bottomRight = tree.horizontal( cyan, magenta, 1.0/3.0 );
		SplitDockTree.Key keyYellow = tree.put( yellow );
		SplitDockTree.Key right = tree.vertical( keyYellow, bottomRight, 0.3 );
		SplitDockTree.Key root = tree.horizontal( group, right, 0.4 );
		tree.root( root );
		
		station.dropTree( tree );
		
		return station;
	}
	
	private static SplitDockStation createLayoutGrid(){
		SplitDockStation station = new SplitDockStation();
		station.setTitleText("Grid");
		
		Dockable red = new ColorDockable( "Red", Color.RED );
		Dockable green =  new ColorDockable( "Green", Color.GREEN );
		Dockable blue = new ColorDockable( "Blue", Color.BLUE );		
		Dockable yellow = new ColorDockable( "Yellow", Color.YELLOW );
		Dockable cyan = new ColorDockable( "Cyan", Color.CYAN );
		Dockable magenta = new ColorDockable( "Magenta", Color.MAGENTA );
		
		SplitDockGrid grid = new SplitDockGrid();
		
		grid.addDockable( 0, 0, 40, 100, red, green, blue );
		grid.setSelected( 0, 0, 40, 100, green );
		grid.addDockable( 40, 0, 60, 30, yellow );
		grid.addDockable( 40, 30, 20, 70, cyan );
		grid.addDockable( 60, 30, 40, 70, magenta );
		
		station.dropTree( grid.toTree() );
		
		return station;	
	}
}
