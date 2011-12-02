package tutorial.dockFrontend.basics;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import tutorial.support.ColorDockable;
import tutorial.support.JTutorialFrame;
import tutorial.support.Tutorial;
import bibliothek.extension.gui.dock.theme.SmoothTheme;
import bibliothek.gui.DockFrontend;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.event.DockFrontendAdapter;
import bibliothek.gui.dock.station.split.SplitDockGrid;
import bibliothek.gui.dock.station.support.PlaceholderStrategy;
import bibliothek.gui.dock.station.support.PlaceholderStrategyListener;
import bibliothek.gui.dock.themes.NoStackTheme;
import bibliothek.util.Path;

@Tutorial(title="Close-Button", id="DFCloseButton")
public class DockFrontendExample {
	public static void main( String[] args ){
		/* You can do a lot of things using the DockController. But some features are really
		 * missing, for example there is no button to just close a Dockable.
		 * 
		 * This is where DockFrontend enters the game. DockFrontend is a small wrapper around
		 * DockController, adding some often needed features.
		 * 
		 * This examples demonstrates how to setup a DockFrontend and how to use its 
		 * close-Dockable feature. */
		
		/* We start by creating a frame to display content */
		JTutorialFrame frame = new JTutorialFrame( DockFrontendExample.class );
		
		/* The framework needs to know what parent window to use for its dialogs and windows. 
		 * We can set this window directly during construction of DockFrontend */
		DockFrontend frontend = new DockFrontend( frame );
		frame.destroyOnClose( frontend );
		frontend.getController().setTheme( new NoStackTheme( new SmoothTheme() ));
		
		/* Let's create a DockStation for our Dockables */
		SplitDockStation station = new SplitDockStation();
		frame.add( station );
		
		/* We need to register "station" at "frontend". We also need to provide a
		 * unique identifier for "station", this identifier is used for persistently
		 * storing the layout. */ 
		frontend.addRoot( "split", station );
		
		/* Let's create some Dockables */
		ColorDockable red = new ColorDockable( "Red", Color.RED );
		ColorDockable green = new ColorDockable( "Green", Color.GREEN );
		ColorDockable blue = new ColorDockable( "Blue", Color.BLUE );
		
		/* We need to register our Dockables. Each Dockable gets associated with a 
		 * unique identifier. */
		frontend.addDockable( "red", red );
		frontend.addDockable( "green", green );
		frontend.addDockable( "blue", blue );
		
		/* We want to use the close-button, so we ensure it is actually enabled */
		frontend.setShowHideAction( true );
		
		/* Now we tell the framework that our Dockables can be closed */
		frontend.setHideable( red, true );
		frontend.setHideable( green, true );
		frontend.setHideable( blue, true );
		
		/* This step is optional. By telling the framework to use placeholders
		 * for our Dockables it can better track their location. In Core placeholders
		 * are normally disabled, only Common activates them.
		 * Our custom strategy cannot do much, it will only track ColorDockables. */
		frontend.getController().getProperties().set( 
				PlaceholderStrategy.PLACEHOLDER_STRATEGY, 
				new ExamplePlaceholderStrategy() );
		
		/* Now we prepare an initial layout, like we did in all the previous examples. */
		SplitDockGrid grid = new SplitDockGrid();
		grid.addDockable( 0, 0, 40, 100, red );
		grid.addDockable( 40, 0, 60, 50, green );
		grid.addDockable( 40, 50, 60, 50, blue );
		station.dropTree( grid.toTree() );
		
		/* A menu, with one entry per Dockable, will allow us to reopen closed Dockables */
		JMenu menu = new JMenu( "Panels" );
		menu.add( createMenuItem( red, frontend ));
		menu.add( createMenuItem( green, frontend ));
		menu.add( createMenuItem( blue, frontend ));
		JMenuBar menuBar = new JMenuBar();
		menuBar.add( menu );
		frame.setJMenuBar( menuBar );
		
		frame.setVisible( true );
	}
	
	private static JMenuItem createMenuItem( final ColorDockable observed, final DockFrontend frontend ){
		/* Here we create a JCheckBoxMenuItem that is selected only if "observed" is visible. */
		final JCheckBoxMenuItem item = new JCheckBoxMenuItem( observed.getTitleText() );
		
		/* We add a DockFrontendListener to "frontend" to be informed whenever a Dockable
		 * is opened or closed (shown and hidden in the terminology of DockFrontend) */
		frontend.addFrontendListener( new DockFrontendAdapter(){
			@Override
			public void shown( DockFrontend frontend, Dockable dockable ){
				if( dockable == observed ){
					item.setSelected( true );
				}
			}
			
			@Override
			public void hidden( DockFrontend fronend, Dockable dockable ){
				if( dockable == observed ){
					item.setSelected( false );
				}
			}
		});
		/* And an ActionListener added to "item" will tell us when the user clicks
		 * on the menu item. */
		item.addActionListener( new ActionListener(){
			public void actionPerformed( ActionEvent e ){
				if( item.isSelected() ){
					frontend.show( observed );
				}
				else{
					frontend.hide( observed );
				}
			}
		});
		
		/* Be sure the initial state of "item" is the correct one */
		item.setSelected( frontend.isShown( observed ));
		return item;
	}
	
	/* This is a PlaceholderStrategy that handles our ColorDockables. It is a
	 * very simple implementation and not intended for real applications. */
	private static class ExamplePlaceholderStrategy implements PlaceholderStrategy{
		public void addListener( PlaceholderStrategyListener listener ){
			// ignore
		}

		public Path getPlaceholderFor( Dockable dockable ){
			/* The placeholder for a ColorDockable is the unique identifier used
			 * in our DockFrontend */
			if( dockable instanceof ColorDockable ){
				return new Path( ((ColorDockable)dockable).getTitleText() );
			}
			else{
				return null;
			}
		}

		public void install( DockStation station ){
			// ignore
		}

		public boolean isValidPlaceholder( Path placeholder ){
			/* Any placeholder is valid, we do not care about old placeholders that 
			 * are no longer used. */
			return true;
		}

		public void removeListener( PlaceholderStrategyListener listener ){
			// ignore
		}

		public void uninstall( DockStation station ){
			// ignore
		}
	}
}
