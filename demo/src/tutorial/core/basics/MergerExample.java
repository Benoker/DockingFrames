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
import bibliothek.gui.dock.control.relocator.Merger;
import bibliothek.gui.dock.control.relocator.MultiMerger;
import bibliothek.gui.dock.control.relocator.StackMerger;
import bibliothek.gui.dock.station.StationDropOperation;
import bibliothek.gui.dock.station.split.PutInfo;
import bibliothek.gui.dock.station.split.SplitDockGrid;

@Tutorial( id="Merger", title="Merge DockStations" )
public class MergerExample {
	/* The framework knows classes that are DockStation and Dockable at the same time, for example the 
	 * StackDockStation. When these objects are moved around, they are usually treated like a Dockable, 
	 * and the framework completely ignores that they are in reality a group of Dockables.
	 * 
	 * Sometimes this leads to layouts that do not look nice, for example a StackDockStation within 
	 * another StackDockStation. With help of the interface "Merger" clients can implement custom rules of
	 * what is going to happen when a DockStation is dropped onto another DockStation.
	 * 
	 * This example shows how a client can tell a SplitDockStation that dropping a StackDockStation onto
	 * a Dockable should not result in creating a new StackDockStation.
	 */
	public static void main( String[] args ){
		/* Setting up a JFrame and a controller */
		JTutorialFrame frame = new JTutorialFrame( MergerExample.class );
		DockController controller = new DockController();
		controller.setRootWindow( frame );
		frame.destroyOnClose( controller );
		
		/* Setting up our custom rules. The MultiMerger is just a collection containing other Mergers */
		MultiMerger merger = new MultiMerger();
		/* The StackMerger is the default Merger that is normally installed. It handles dropping
		 * StackDockStations onto StackDockStations */
		merger.add( new StackMerger() );
		/* Now we create our custom Merger */
		merger.add( new CustomMerger() );
		/* And by accessing the DockRelocator we can apply our new Merger */
		controller.getRelocator().setMerger( merger );
		
		/* Now we just set up some Dockable to play around with */
		SplitDockStation station = new SplitDockStation();
		controller.add( station );
		frame.add( station );
		
		SplitDockGrid grid = new SplitDockGrid();
		grid.addDockable( 0, 0, 1, 1, new ColorDockable( "Red", Color.RED ) );
		grid.addDockable( 0, 1, 1, 1, new ColorDockable( "Dark Red", Color.RED.darker() ) );
		grid.addDockable( 1, 0, 1, 1, new ColorDockable( "Light Red", Color.RED.brighter() ) );
		grid.addDockable( 1, 1, 1, 1, new ColorDockable( "Dark Green", Color.GREEN.darker() ) );
		grid.addDockable( 1, 1, 1, 1, new ColorDockable( "Light Green", Color.GREEN.brighter() ) );
		grid.addHorizontalDivider( 0, 2, 1 );
		station.dropTree( grid.toTree() );
		
		frame.setVisible( true );
	}
	
	/* This is our custom implementation of a Merger */
	public static class CustomMerger implements Merger{
		/* This method tells whether this Merger can do something meaningful with the
		 * given parent and child of a dropping operation */
		public boolean canMerge( StationDropOperation operation, DockStation parent, DockStation child ){
			if( parent instanceof SplitDockStation && child instanceof StackDockStation ){
				SplitDockStation station = (SplitDockStation)parent;
				PutInfo put = station.getDropInfo();
				if( put != null && put.getCombinerTarget() != null ){
					/* This merger only reacts if there is an operation in progress that would
					 * lead to a combination */
					return true;
				}
			}
			return false;
		}

		/* And this is our custom merging algorithm */
		public void merge( StationDropOperation operation, DockStation parent, DockStation child ){
			merge( (SplitDockStation)parent, (StackDockStation)child );
		}
		
		public void merge( SplitDockStation parent, StackDockStation child ){
			/* Access the Dockable over which child is dropped */
			PutInfo put = parent.getDropInfo();
			Dockable old = put.getCombinerSource().getOld();
			/* Remove child from its parent... */
			if( child.getDockParent() != null ){
				child.getDockParent().drag( child );
			}
			/* ... replace the old Dockable with the child... */
			parent.replace( old, child );
			/* ... and then add the old Dockable to the child */
			child.add( old, 0 );
		}
	}
}
