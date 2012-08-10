package tutorial.core.guide;

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
import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.layout.DockableProperty;
import bibliothek.gui.dock.station.split.SplitDockGrid;
import bibliothek.gui.dock.station.support.PlaceholderStrategy;
import bibliothek.gui.dock.station.support.PlaceholderStrategyListener;
import bibliothek.gui.dock.util.DockUtilities;
import bibliothek.util.Path;

@Tutorial( id="Placeholders", title="Persistent Layout: Placeholders" )
public class PlaceholderExample {
	/* Placeholders allow the framework to mark the location of a Dockable even if the Dockable is not
	 * known to a DockController.
	 * 
	 * This example shows how a primitive mechanism to close and reopen Dockables can be implemented using
	 * a PlaceholderStrategy and using local layout data (in form of a DockableProperty).
	 */
	public static void main( String[] args ){
		/* Setting up a frame, a station and a controller */
		JTutorialFrame frame = new JTutorialFrame( PlaceholderExample.class );
		DockController controller = new DockController();
		controller.setRootWindow( frame );
		frame.destroyOnClose( controller );
		
		SplitDockStation station = new SplitDockStation();
		controller.add( station );
		frame.add( station );
		
		/* At this point we register our custom PlaceholderStrategy. PlaceholderStrategies should always
		 * be installed early on, that way no placeholders are lost. */
		controller.getProperties().set( PlaceholderStrategy.PLACEHOLDER_STRATEGY, new CustomPlaceholderStrategy() );
		
		/* Creating some custom Dockables and adding them to the tree */
		CustomDockable red = new CustomDockable( new Path( "red" ), "Red", Color.RED );
		CustomDockable green = new CustomDockable( new Path( "green" ), "Green", Color.GREEN );
		CustomDockable blue = new CustomDockable( new Path( "blue" ), "Blue", Color.BLUE );
		CustomDockable yellow = new CustomDockable( new Path( "yelow" ), "Yellow", Color.YELLOW );
		
		SplitDockGrid grid = new SplitDockGrid();
		grid.addDockable( 0, 0, 1, 1, red );
		grid.addDockable( 0, 1, 1, 1, green );
		grid.addDockable( 1, 0, 1, 1, blue );
		grid.addDockable( 1, 1, 1, 1, yellow );
		station.dropTree( grid.toTree() );
		
		/* And here we create a menu containing checkboxes to open and close our custom Dockables */
		JMenu menu = new JMenu( "Dockables" );
		menu.add( red.createMenuItem() );
		menu.add( green.createMenuItem() );
		menu.add( blue.createMenuItem() );
		menu.add( yellow.createMenuItem() );
		JMenuBar bar = new JMenuBar();
		bar.add( menu );
		frame.setJMenuBar( bar );
		
		frame.setVisible( true );
	}
	
	/* This dockable remembers its location if it is closed and can open itself at the former location */
	private static class CustomDockable extends ColorDockable{
		private Path placeholder;
		private DockableProperty location;
		private DockStation root;
		
		public CustomDockable( Path placeholder, String title, Color color ){
			super( title, color );
			this.placeholder = placeholder;
		}
		
		public Path getPlaceholder(){
			return placeholder;
		}
		
		/* creates a checkbox for opening/closing this dockable */
		public JMenuItem createMenuItem(){
			final JCheckBoxMenuItem item = new JCheckBoxMenuItem( getTitleText(), getTitleIcon() );
			item.setSelected( getDockParent() != null );
			item.addActionListener( new ActionListener(){
				public void actionPerformed( ActionEvent e ){
					if( item.isSelected() ){
						doShow();
					}
					else{
						doClose();
					}
				}
			});
			return item;
		}
		
		public void doClose(){
			DockStation parent = getDockParent();
			if( parent != null ){
				/* remember the old location... */
				root = DockUtilities.getRoot( this );
				location = DockUtilities.getPropertyChain( this );
				/* ... then close */
				parent.drag( this );
			}
		}
		
		public void doShow(){
			if( getDockParent() == null ){
				/* drop this at the former location */
				if( !root.drop( this, location ) ){
					root.drop( this );
				}
				location = null;
			}
		}
	}
	
	/* This is our very simple PlaceholderStrategy. It only recognizes our custom Dockable and
	 * returns its placeholder. */
	private static class CustomPlaceholderStrategy implements PlaceholderStrategy{
		public void addListener( PlaceholderStrategyListener listener ){
			// ignore
		}

		public Path getPlaceholderFor( Dockable dockable ){
			if( dockable instanceof CustomDockable ){
				return ((CustomDockable) dockable).getPlaceholder();
			}
			return null;
		}

		public void install( DockStation station ){
			// ignore
		}

		public boolean isValidPlaceholder( Path placeholder ){
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
