package tutorial.toolbar.core;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.Icon;

import tutorial.support.ColorIcon;
import tutorial.support.JTutorialFrame;
import tutorial.support.Tutorial;
import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.Orientation;
import bibliothek.gui.dock.ToolbarContainerDockStation;
import bibliothek.gui.dock.ToolbarDockStation;
import bibliothek.gui.dock.ToolbarGroupDockStation;
import bibliothek.gui.dock.ToolbarItemDockable;
import bibliothek.gui.dock.action.actions.SimpleButtonAction;
import bibliothek.gui.dock.station.toolbar.menu.CustomizationButton;
import bibliothek.gui.dock.station.toolbar.menu.DefaultCustomizationMenu;
import bibliothek.gui.dock.station.toolbar.menu.EagerCustomizationToolbarButton;
import bibliothek.gui.dock.station.toolbar.menu.GroupedCustomizationMenuContent;

@Tutorial(id = "ToolbarCustomization", title = "Customizing")
public class ToolbarCustomization {
	public static void main( String[] args ){
		/* For some applications it can be interesting to offer an easy way for adding and hiding buttons
		 * of a toolbar. The extension offers two features to help with this task:
		 * 1. Clients can register a "ToolbarGroupHeaderFactory", this factory allows to show some Component
		 *    on the top/left side of each group of toolbars.
		 * 2. A default implementation of "ToolbarGroupHeaderFactory" is offered, this implementation adds
		 *    a button which opens a customizeable menu.
		 */
		
		/* We need a frame and a controller */
		JTutorialFrame frame = new JTutorialFrame( ToolbarCustomization.class );

		DockController controller = new DockController();
		controller.setRootWindow( frame );
		frame.destroyOnClose( controller );
		
		/* We start by creating a ToolbarGroupHeaderFactory, in this case we use the implementation
		 * offered by CustomizationButton. This factory is registered in the properties. */
		CustomizationButton customization = new CustomizationButton( controller );
		controller.getProperties().set( ToolbarGroupDockStation.HEADER_FACTORY, customization );

		/* We need to tell the button how to show a menu. The DefaultCustomizationMenu is in fact a JDialog
		 * which behaves like a popup menu. */
		customization.setMenu( new DefaultCustomizationMenu() );
		
		/* Of course the menu needs some content. The extensions offers different implementations for
		 * CustomizationMenuContent. GroupedCustomizationMenuContent is really a builder for creating
		 * a Component consisting of groups, where each group is a panel with a title and some more
		 * CustomizationMenuContents. */
		GroupedCustomizationMenuContent customizationContent = new GroupedCustomizationMenuContent();
		
		/* Never forget to tell the menu what its content is */
		customization.setContent( customizationContent );

		/* We create the first group of CustomizationMenuContents... */
		GroupedCustomizationMenuContent.Group groupA = customizationContent.addGroup( "Top group" );
		/* ... and add some buttons to it. Each button wrapps around a Dockable, which can be added
		 * or removed from the toolbar  */
		groupA.add( new EagerCustomizationToolbarButton( createDockable( new ColorIcon( Color.BLUE ) ) ) );
		groupA.add( new EagerCustomizationToolbarButton( createDockable( new ColorIcon( Color.YELLOW ) ) ) );
		groupA.add( new EagerCustomizationToolbarButton( createDockable( new ColorIcon( Color.GREEN ) ) ) );
		groupA.add( new EagerCustomizationToolbarButton( createDockable( new ColorIcon( Color.WHITE ) ) ) );
		groupA.add( new EagerCustomizationToolbarButton( createDockable( new ColorIcon( Color.BLACK ) ) ) );
		groupA.add( new EagerCustomizationToolbarButton( createDockable( new ColorIcon( Color.CYAN ) ) ) );
		groupA.add( new EagerCustomizationToolbarButton( createDockable( new ColorIcon( Color.LIGHT_GRAY ) ) ) );
		groupA.add( new EagerCustomizationToolbarButton( createDockable( new ColorIcon( Color.MAGENTA ) ) ) );

		/* And a second group just to make the menu look a bit nicer */
		GroupedCustomizationMenuContent.Group groupB = customizationContent.addGroup( "Bottom group" );
		groupB.add( new EagerCustomizationToolbarButton( createDockable( new ColorIcon( Color.BLUE ) ) ) );
		groupB.add( new EagerCustomizationToolbarButton( createDockable( new ColorIcon( Color.YELLOW ) ) ) );
		groupB.add( new EagerCustomizationToolbarButton( createDockable( new ColorIcon( Color.GREEN ) ) ) );
		groupB.add( new EagerCustomizationToolbarButton( createDockable( new ColorIcon( Color.WHITE ) ) ) );

		/* We are done setting up the menu, now we create the actual toolbars */
		
		/* We now create the root stations. */
		ToolbarContainerDockStation west = new ToolbarContainerDockStation( Orientation.VERTICAL, 5 );
		ToolbarContainerDockStation east = new ToolbarContainerDockStation( Orientation.VERTICAL, 5 );
		ToolbarContainerDockStation north = new ToolbarContainerDockStation( Orientation.HORIZONTAL, 5 );
		ToolbarContainerDockStation south = new ToolbarContainerDockStation( Orientation.HORIZONTAL, 5 );

		/* Our root stations need to be registered at the DockController */
		controller.add( west );
		controller.add( east );
		controller.add( north );
		controller.add( south );

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

		group.drop( createToolbar( redIcon, greenIcon, blueIcon ), 0, 0 );
		group.drop( createToolbar( redIcon, greenIcon ), 1, 0 );

		/* When we are finished building the group, we make it visible by adding it to the container
		 * at the left side of the frame */
		west.drop( group );

		frame.setVisible( true );
	}

	/* This method creates one toolbar, adding one button for each icon */
	private static ToolbarDockStation createToolbar( Icon... icons ){
		/* Creating a toolbar is really easy. Create a ToolbarDockStation... */
		ToolbarDockStation toolbar = new ToolbarDockStation();
		for( Icon icon : icons ) {
			/* ... and add some Dockables */
			toolbar.drop( createDockable( icon ) );
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

		/* To convert the DockAction into a Dockable we create a new ToolbarItemDockable */
		ToolbarItemDockable dockable = new ToolbarItemDockable( action );
		dockable.setTitleIcon( icon );
		return dockable;
	}
}
