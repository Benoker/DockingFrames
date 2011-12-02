package tutorial.core.basics;

import java.awt.Color;

import tutorial.support.ColorDockable;
import tutorial.support.JTutorialFrame;
import tutorial.support.Tutorial;
import bibliothek.extension.gui.dock.theme.EclipseTheme;
import bibliothek.gui.DockController;
import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.station.split.SplitDockProperty;
import bibliothek.gui.dock.station.stack.tab.DefaultMenuLineLayoutFactory;
import bibliothek.gui.dock.station.stack.tab.MenuLineLayout;
import bibliothek.gui.dock.station.stack.tab.MenuLineLayoutOrder;
import bibliothek.gui.dock.station.stack.tab.TabPane;
import bibliothek.gui.dock.station.stack.tab.MenuLineLayoutOrder.Item;

@Tutorial(title="Tab Layout", id="StackTabLayout")
public class StackTabLayoutExample {
	public static void main( String[] args ){
		/* The position of tabs, actions and overflow menu on a StackDockStation can be configured
		 * by a TabLayoutManager.
		 * 
		 * This example shows how the MenuLineLayout is configured and installed. Our goal is to
		 * put some tabs in the middle of the panel.
		 *  
		 * Note: The BasicTheme does not support this mechanism as it makes use of the JTabbedPane - 
		 * which is rather limited compared to the TabPane introduced by this framework.
		 */
		
		/* We start by setting up some stations and some Dockables */
		JTutorialFrame frame = new JTutorialFrame( StackTabLayoutExample.class );
		DockController controller = new DockController();
		controller.setRootWindow( frame );
		frame.destroyOnClose( controller );
		
		SplitDockStation station = new SplitDockStation();
		controller.add( station );
		frame.add( station );
		
		/* And we make sure we have a StackDockStation at hand */
		SplitDockProperty center = new SplitDockProperty( 0, 0, 1, 1 );
		station.drop( new ColorDockable( "Red", Color.RED ), center );
		station.drop( new ColorDockable( "Green", Color.GREEN ), center );
		station.drop( new ColorDockable( "Blue", Color.BLUE ), center);
		
		/* This LayoutManager is supported by the EclipseTheme, FlatTheme and BubbleTheme, hence we
		 * need to set one of them. */
		controller.setTheme( new EclipseTheme() );
		
		/* Creating the new layout manager... */
		MenuLineLayout layoutManager = new MenuLineLayout();
		/* ... and configuring it. This manager can be configured by setting the MenuLineLayoutFactory. */
		layoutManager.setFactory( new DefaultMenuLineLayoutFactory(){
			/* This method is responsible in choosing order and size of the various items that may show up */
			public MenuLineLayoutOrder createOrder( MenuLineLayout layout, TabPane pane ){
				MenuLineLayoutOrder order = new MenuLineLayoutOrder( Item.INFO, Item.TABS, Item.MENU );
				/* The parameters we set are, from left to right:
				 *  - The item to modify
				 *  - Weight: how much empty space belongs to the item
				 *  - Alignment: whether the item will show at the left or the right end of the empty space
				 *  - Fill: whether the item gets stretched to fill up the empty space */
				order.setConstraints( Item.INFO, 0.0f, 0.0f, 1.0f );
				order.setConstraints( Item.TABS, 1.0f, 0.5f, 0.0f );
				order.setConstraints( Item.MENU, 0.0f, 1.0f, 0.0f );
				return order;
			}
		});
		/* And we need to set the current layout manager using the DockProperties */
		controller.getProperties().set( TabPane.LAYOUT_MANAGER, layoutManager );
		
		frame.setVisible( true );
	}
}
