package tutorial.toolbar.common;

import java.awt.Color;

import javax.swing.Icon;
import javax.swing.JButton;

import tutorial.support.ColorIcon;
import tutorial.support.JTutorialFrame;
import tutorial.support.Tutorial;
import bibliothek.gui.dock.ExpandableToolbarItemStrategy;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.CLocation;
import bibliothek.gui.dock.common.SingleCDockable;
import bibliothek.gui.dock.common.action.CButton;
import bibliothek.gui.dock.toolbar.CToolbarContentArea;
import bibliothek.gui.dock.toolbar.CToolbarItem;
import bibliothek.gui.dock.toolbar.expand.DefaultExpandableToolbarItemStrategy;
import bibliothek.gui.dock.toolbar.expand.ExpandedState;
import bibliothek.gui.dock.toolbar.location.CToolbarAreaLocation;

@Tutorial( id="ToolbarCommonHelloWorld", title="Simple Example" )
public class CommonHelloWorld {
	public static void main( String[] args ){
		/* The toolbar extension can be used together with the Common project. The extension adds some new 
		 * CStations and CDockables to the framework. This example will show how to use them. */
		
		/* But before we start we need a frame and a CControl */
		JTutorialFrame frame = new JTutorialFrame( CommonHelloWorld.class );
		CControl control = new CControl( frame );
		frame.destroyOnClose( control );
		
		/* We are going to allow our toolbars to expand to different sizes. This feature needsd to be enabled
		 * by replacing the default ExpandableToolbarItemStrategz  */
		control.putProperty( ExpandableToolbarItemStrategy.STRATEGY, new DefaultExpandableToolbarItemStrategy() );

		/* Instead of a CContentArea we create a CToolbarContentArea. This class adds four CToolbarAreas around
		 * the five CStations that are usually shown by the CContentArea. */
		CToolbarContentArea area = new CToolbarContentArea( control, "base" );
		
		/* The new area needs to be registered at the CControl... */
		control.addStationContainer( area );
		/* ... and added to the frame */
		frame.add( area );

		/* We are going to add several buttons to toolbar, we'll use these icons to make the more visible. */
		Icon red = new ColorIcon( Color.RED );
		Icon green = new ColorIcon( Color.GREEN );
		Icon blue = new ColorIcon( Color.BLUE );
		Icon yellow = new ColorIcon( Color.YELLOW );
		

		/* In this example we add all our toolbars to the eastern side of the frame. To build up the layout
		 * we are going to use the "CDockable.setLocation" method, which requires an object of type CLocation.
		 * The CToolbarAreaLocation offers methods to build such a CLocation, we can obtain such a builder by
		 * just asking the eastern toolbar for it. */
		CToolbarAreaLocation location = area.getEastToolbar().getStationLocation();
		
		/* The toolbars build a tree, in order to place an item we need to traverse the tree and point to a leaf.
		 * 1. In our case there are four trees, one on each side of the frame. "location" represents the
		 *    root of the tree to the east.
		 * 2. Below the root is the group. A group usually has a black title, and orders its children
		 *    in several columns.
		 * 3. The group is built of actual toolbars.
		 * 4. And finally each toolbar is a set of items. */
		
		/* The first location points to the first group -> the first toolbar of the first column -> the first item.
		 * The next two locations point to the same toolbar but to the next items. */
		add( control, "A", red,   location.group( 0 ).toolbar( 0, 0 ).item( 0 ) );
		add( control, "B", red,   location.group( 0 ).toolbar( 0, 0 ).item( 1 ) );
		add( control, "C", red,   location.group( 0 ).toolbar( 0, 0 ).item( 2 ) );
		
		/* The next three locations point to a new toolbar below the first one we added */
		add( control, "D", green, location.group( 0 ).toolbar( 0, 1 ).item( 0 ) );
		add( control, "E", green, location.group( 0 ).toolbar( 0, 1 ).item( 1 ) );
		add( control, "F", green, location.group( 0 ).toolbar( 0, 1 ).item( 2 ) );
		
		/* This location points to a toolbar that is above the first toolbar */
		add( control, "G", blue,  location.group( 0 ).toolbar( 0, -1 ).item( 0 ) );
		
		/* The next three locations all point to the same toolbar as well. The first location points to 
		 * a toolbar that is left of all the existing toolbars. Since this action adds a new toolbar
		 * at position "0", the next two locations need to point to the first toolbar of the group. */
		add( control, "H", red,   location.group( 0 ).toolbar( -1, 0 ).item( 0 ) );
		add( control, "I", red,   location.group( 0 ).toolbar( 0, 0 ).item( 0 ) );
		add( control, "J", red,   location.group( 0 ).toolbar( 0, 0 ).item( 0 ) );
		
		/* It is ok to create a location pointing to some strange places. While the framework cannot
		 * place the item at the desired location, it tries to place it as near as possible. */
		add( control, "K", green, location.group( 0 ).toolbar( 15, 16 ).item( 18 ) );
		
		/* Of course we can also point to another group, in this case we add a group before the
		 * one that contains all the other toolbars. */
		add( control, "L", blue,  location.group( -1 ).toolbar( 0, 0 ).item( 0 ) );

		/* If we want to place an item near another item, we can make use of the "aside" method
		 * offered by CLocation. This method creates a new location "near" itself. */
		add( control, "M", yellow, get( control, "L" ).aside() );
		add( control, "N", yellow, get( control, "M" ).aside() );
		
		frame.setBounds( 20, 20, 400, 400 );
		frame.setVisible( true );
	}

	/* This method creates and adds a dockable to the application. */
	private static void add( CControl control, String id, Icon icon, CLocation location ){
		/* We need some text for the buttons */
		String text = id + id.toLowerCase() + id.toLowerCase() + id.toLowerCase(); 
		
		/* Creating the Dockable, and accessing its internal representation to make it look nicer. */
		CToolbarItem item = new CToolbarItem( id );
		item.intern().setTitleText( text );
		
		/* The content of the Dockable can be any CAction, in this case the action is the default content. */
		item.setItem( new CButton( null, icon ) );
		/* We can also show a normal Component, in this case the Component is only shown if the 
		 * expanded-state of the dockable is changed to "STRECHED" */
		item.setItem( new JButton( text, icon ), ExpandedState.STRETCHED );
		
		/* These are standard functions not related to the extension. */
		item.setLocation( location );
		control.addDockable( item );
		item.setVisible( true );
	}
	
	/* This method gets the current location of the dockable with identifier "id" */
	private static CLocation get( CControl control, String id ){
		SingleCDockable dockable = control.getSingleDockable( id );
		return dockable.getBaseLocation();
	}
}
