package tutorial.toolbar.core;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JScrollPane;

import tutorial.support.JTutorialFrame;
import tutorial.support.Tutorial;
import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DefaultDockable;
import bibliothek.gui.dock.station.split.SplitDockGrid;
import bibliothek.gui.dock.wizard.WizardSplitDockStation;
import bibliothek.gui.dock.wizard.WizardSplitDockStation.Side;

@Tutorial( id="WizardSplitDockStation", title="Wizard" )
public class WizardSplitDockStationTutorial {
	public static void main( String[] args ){
		/* Sometimes allowing a Dockable to have its preferred size is more important than having
		 * no empty space between them. The WizardSplitDockStation allows clients to show several
		 * columns of Dockables, and the user can set the exact size of the Dockables without influencing
		 * other columns. 
		 * 
		 * The WizardSplitDockStation is intended to be positioned at the four sides of a container like a frame. */
		
		/* As in any example we need a frame and a controller */
		JTutorialFrame frame = new JTutorialFrame( WizardSplitDockStationTutorial.class );
		
		DockController controller = new DockController();
		controller.setRootWindow( frame );
		frame.destroyOnClose( controller );
		
		/* We create one station for each side of the frame. The "Side" enumeration tells
		 * the station how it should align its children. */
		WizardSplitDockStation left = new WizardSplitDockStation( Side.LEFT );
		WizardSplitDockStation right = new WizardSplitDockStation( Side.RIGHT );
		WizardSplitDockStation top = new WizardSplitDockStation( Side.TOP );
		WizardSplitDockStation bottom = new WizardSplitDockStation( Side.BOTTOM );
		
		controller.add( left );
		controller.add( right );
		controller.add( top );
		controller.add( bottom );
		
		/* Since the WizardSplitDockStation tries to keep the size of the Dockables, it needs to
		 * be placed inside a JScrollPane. That way it can still show its children even if there
		 * is not enough space */
		frame.add( new JScrollPane( left ), BorderLayout.WEST );
		frame.add( new JScrollPane( right ), BorderLayout.EAST );
		frame.add( new JScrollPane( top ), BorderLayout.NORTH );
		frame.add( new JScrollPane( bottom ), BorderLayout.SOUTH );
		
		/* In order to set up the layout we use a SplitDockGrid. Now SplitDockGrid does not know
		 * anything about columns, hence it is our responsibility to create a valid layout. We do
		 * this by inserting "vertical dividiers" between the Dockables that make up a column. */
		SplitDockGrid grid = new SplitDockGrid();
		grid.addDockable( 0, 0, 1, 1, create( "A" ) );
		grid.addDockable( 0, 1, 1, 1, create( "B" ) );
		grid.addDockable( 0, 2, 1, 1, create( "C" ) );
		grid.addVerticalDivider( 1, 0, 3 );
		grid.addDockable( 1, 0, 1, 1, create( "D" ) );
		grid.addDockable( 1, 1, 1, 1, create( "E" ) );
		grid.addVerticalDivider( 2, 0, 3 );
		grid.addDockable( 2, 0, 1, 1, create( "F" ) );
		grid.addDockable( 2, 1, 1, 1, create( "G" ) );
		
		/* We apply the layout described in "grid" by converting it into a tree-layout and dropping the tree */
		left.dropTree( grid.toTree() );
		
		frame.setVisible( true );

		/* Finally we tell the WizardSplitDockStation to reset all sizes using the preferred sizes of
		 * the involved Dockables. Since the station has a size of 0/0 at the time we add the Dockables,
		 * their original sizes were too small. */
		left.resetToPreferredSizes();
	}
	
	/* This method just creates a new Dockable with a JButton as content */
	private static Dockable create( String title ){
		DefaultDockable dockable = new DefaultDockable( title );
		dockable.setLayout( new BorderLayout() );
		JButton button = new JButton( title );
		dockable.add( button );
		return dockable;
	}
}
