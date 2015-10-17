package tutorial.toolbar.core;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JPanel;

import tutorial.support.ColorIcon;
import tutorial.support.JTutorialFrame;
import tutorial.support.Tutorial;
import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.Orientation;
import bibliothek.gui.dock.ScreenDockStation;
import bibliothek.gui.dock.ToolbarContainerDockStation;
import bibliothek.gui.dock.ToolbarDockStation;
import bibliothek.gui.dock.ToolbarGroupDockStation;
import bibliothek.gui.dock.ToolbarItemDockable;
import bibliothek.gui.dock.action.actions.SimpleButtonAction;
import bibliothek.gui.dock.station.toolbar.group.ToolbarGroupProperty;

@Tutorial( id="ToolbarHelloWorld", title="Basic Toolbars")
public class ToolbarHelloWorld {
	public static void main( String[] args ){
		/* A toolbar is nothing more than a specialized DockStation, with some special Dockables
		 * as children. Each Dockable represents one "item" of the toolbar, e.g. a button. The user
		 * can drag and drop the buttons, and thus customize his application.
		 * 
		 * The Toolbar extension adds several new DockStations to the framework, and these stations
		 * must be used in the correct order:
		 * 
		 * 1. The ToolbarContainerDockStation is the root of the toolbar tree. It only accepts
		 *    ToolbarGroupDockStations as children. 
		 * 2. The ToolbarGroupDockStation represents a group of toolbars, its children are ordered
		 *    in columns. It only accepts ToolbarDockStations as children. 
		 * 3. A toolbar itself is represented by a ToolbarDockStation.
		 * 
		 * The exact relation between these stations is defined by the interface ToolbarStrategy. */
		
		/* As in any example we need a frame and a controller */
		JTutorialFrame frame = new JTutorialFrame( ToolbarHelloWorld.class );
		
		DockController controller = new DockController();
		controller.setRootWindow( frame );
		frame.destroyOnClose( controller );

		/* Toolbars can float if a ScreenDockStation is available, so we add one */
		ScreenDockStation screen = new ScreenDockStation(frame);
		controller.add(screen);

		/* We now create the root stations. Notice how each station requires an "Orientation", the
		 * orientation will automatically forwarded to the children of the station */
		ToolbarContainerDockStation west = new ToolbarContainerDockStation( Orientation.VERTICAL, 5);
		ToolbarContainerDockStation east = new ToolbarContainerDockStation( Orientation.VERTICAL, 5);
		ToolbarContainerDockStation north = new ToolbarContainerDockStation( Orientation.HORIZONTAL, 5);
		ToolbarContainerDockStation south = new ToolbarContainerDockStation( Orientation.HORIZONTAL, 5);
		
		/* Our root stations need to be registered at the DockController */
		controller.add( west );
		controller.add( east );
		controller.add( north );
		controller.add( south );

		/* We place a single JPanel in the center of the frame and add a border to it. This allows us to 
		 * see where the ToolbarContainerDockStations end. */
		JPanel center = new JPanel();
		center.setBorder( BorderFactory.createLineBorder( Color.BLACK, 1 ) );
		frame.add( center, BorderLayout.CENTER );

		/* Now the root stations are added directly to all sides of the frame */
		frame.add( west.getComponent(), BorderLayout.WEST );
		frame.add( east.getComponent(), BorderLayout.EAST );
		frame.add( north.getComponent(), BorderLayout.NORTH );
		frame.add( south.getComponent(), BorderLayout.SOUTH );

		/* We want to show more than one toolbar, so we create station that represents a group
		 * of toolbars. */
		ToolbarGroupDockStation group = new ToolbarGroupDockStation();

		/* The icons are required for the buttons of the toolbar. */
		Icon redIcon = new ColorIcon( Color.RED );
		Icon greenIcon = new ColorIcon( Color.GREEN );
		Icon blueIcon = new ColorIcon( Color.BLUE );

		/* We create 4 toolbars and add them directly to the group. The numbers at the end
		 * of the call are the column and the line where to insert the toolbar (in respect
		 * to the toolbars that are already shown). */
		group.drop( createToolbar( redIcon, greenIcon, blueIcon ), 0, 0 );
		group.drop( createToolbar( redIcon, greenIcon, blueIcon ), 0, 1 );
		group.drop( createToolbar( redIcon, greenIcon ), 1, 0 );
		group.drop( createToolbar( redIcon, greenIcon ), 1, 1 );

		/* Another 3 toolbars are added. The location of a toolbar is described by a
		 * ToolbarGroupProperty, which just consists of a column and a line field. */
		group.drop( createToolbar( redIcon, greenIcon ), new ToolbarGroupProperty( 1, 0, null ));
		group.drop( createToolbar( redIcon, greenIcon, blueIcon ), new ToolbarGroupProperty( 3, 2, null ));
		group.drop( createToolbar( redIcon, greenIcon, blueIcon ), new ToolbarGroupProperty( -1, 5, null ));
		
		/* When we are finished building the group, we make it visible by adding it to the container
		 * at the left side of the frame */
		west.drop( group );

		frame.setVisible( true );
		screen.setShowing( true );
	}
	
	/* This method creates one toolbar, adding one button for each icon */
	private static ToolbarDockStation createToolbar( Icon ... icons ){
		/* Creating a toolbar is really easy. Create a ToolbarDockStation... */
		ToolbarDockStation toolbar = new ToolbarDockStation();
		for( Icon icon : icons ){
			/* ... and add some Dockables */
			toolbar.drop( createDockable( icon ));
		}
		return toolbar;
	}
	
	/* This methods creates one button of the toolbar */
	private static Dockable createDockable( Icon icon ){
		/* There are two kind of Dockables that can be used as children of a toolbar:
		 * 1. ComponentDockable allows to show any kind of Component on the toolbar
		 * 2. ToolbarActionDockable allows to show DockActions on the toolbar */
		
		/* We set up a button using a DockAction for this task */
		SimpleButtonAction action = new SimpleButtonAction();
		action.setIcon( icon );
		
		/* To convert the DockAction into a Dockable we create a new ToolbarActionDockable */
		final ToolbarItemDockable dockable = new ToolbarItemDockable( action );
		dockable.setTitleIcon(icon);
		return dockable;
	}
}
