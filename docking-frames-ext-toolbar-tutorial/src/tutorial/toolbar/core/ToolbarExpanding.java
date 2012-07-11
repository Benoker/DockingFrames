package tutorial.toolbar.core;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import tutorial.support.ColorIcon;
import tutorial.support.JTutorialFrame;
import tutorial.support.Tutorial;
import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.Orientation;
import bibliothek.gui.dock.ExpandableToolbarItemStrategy;
import bibliothek.gui.dock.ToolbarContainerDockStation;
import bibliothek.gui.dock.ToolbarDockStation;
import bibliothek.gui.dock.ToolbarItemDockable;
import bibliothek.gui.dock.action.actions.SimpleButtonAction;
import bibliothek.gui.dock.toolbar.expand.DefaultExpandableToolbarItemStrategy;
import bibliothek.gui.dock.toolbar.expand.ExpandedState;

@Tutorial( id="ToolbarExpanding", title="Expandable Items" )
public class ToolbarExpanding {
	public static void main( String[] args ){
		/* Having only some small buttons on a toolbar may not be enough. How about allowing the buttons to 
		 * expand in order to show some text? Or get even bigger in order to show some kind of editor component?
		 * 
		 * The ExpandableToolbarItemStrategy allows exactly that. It controls two buttons on top of the toolbars,
		 * allowing the user to switch between three different ExpandedStates. Usually this feature is disabled,
		 * but there is a default implementation of the interface searching for ExpandableToolbarItems, 
		 * an interface that can be implemented by Dockables. 
		 * 
		 * The class ToolbarItemDockable implements ExpandableToolbarItems, we need to initialize it with different
		 * Components for the different states. */
		
		/* As in any example we need a frame and a controller */
		JTutorialFrame frame = new JTutorialFrame( ToolbarExpanding.class );
		
		DockController controller = new DockController();
		controller.setRootWindow( frame );
		frame.destroyOnClose( controller );
		
		/* In order to enable expandable items we need to set a ExpandableToolbarItemStrategy. The default
		 * implementation will do fine for most clients. */
		controller.getProperties().set( ExpandableToolbarItemStrategy.STRATEGY, new DefaultExpandableToolbarItemStrategy() );
		
		/* We need some stations to show the toolbars */
		ToolbarContainerDockStation west = new ToolbarContainerDockStation( Orientation.VERTICAL, 5);
		ToolbarContainerDockStation north = new ToolbarContainerDockStation( Orientation.HORIZONTAL, 5);
		
		controller.add( west );
		controller.add( north );
		
		frame.add( west.getComponent(), BorderLayout.WEST );
		frame.add( north.getComponent(), BorderLayout.NORTH );
		
		/* The icons are required for the buttons of the toolbar. */
		Icon redIcon = new ColorIcon( Color.RED );
		Icon greenIcon = new ColorIcon( Color.GREEN );
		Icon blueIcon = new ColorIcon( Color.BLUE );

		/* Notice that we can drop the toolbars directly onto the root stations, the extension will automatically
		 * add the missing ToolbarGroupDockStations */
		west.drop( createToolbar( redIcon, greenIcon, blueIcon ) );
		north.drop( createToolbar( redIcon, greenIcon, blueIcon ) );
		
		frame.setVisible( true );
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
		ToolbarItemDockable dockable = new ToolbarItemDockable();
		dockable.setTitleIcon( icon );
		dockable.setTitleText( "Name" );
		
		/* For each of the possible states we add one Component to the dockable */
		SimpleButtonAction action = new SimpleButtonAction();
		action.setIcon( icon );
		dockable.setAction( action, ExpandedState.SHRUNK );
		dockable.setComponent( new JButton( "This is a description" ), ExpandedState.STRETCHED );
		dockable.setComponent( new JScrollPane( new JTextArea( "Some detailed information about this feature." ) ), ExpandedState.EXPANDED );
		
		return dockable;
	}
}
