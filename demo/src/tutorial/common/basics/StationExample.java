package tutorial.common.basics;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;

import tutorial.support.ColorSingleCDockable;
import tutorial.support.JTutorialFrame;
import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.StackDockStation;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.CLocation;
import bibliothek.gui.dock.common.CMinimizeArea;
import bibliothek.gui.dock.common.event.CDockableLocationEvent;
import bibliothek.gui.dock.common.event.CDockableLocationListener;
import bibliothek.gui.dock.common.group.CGroupBehavior;
import bibliothek.gui.dock.common.intern.AbstractCStation;
import bibliothek.gui.dock.common.intern.CControlAccess;
import bibliothek.gui.dock.common.intern.DefaultCDockable;
import bibliothek.gui.dock.common.mode.CNormalModeArea;
import bibliothek.gui.dock.common.mode.ExtendedMode;
import bibliothek.gui.dock.common.perspective.CStationPerspective;
import bibliothek.gui.dock.facile.mode.Location;
import bibliothek.gui.dock.facile.mode.LocationMode;
import bibliothek.gui.dock.facile.mode.ModeAreaListener;
import bibliothek.gui.dock.layout.DockableProperty;
import bibliothek.gui.dock.support.mode.AffectedSet;
import bibliothek.gui.dock.util.DockUtilities;

public class StationExample {
	/* This example shows how a StackDockStation is converted into a CStation and how that
	 * CStation is registered as a station for dockable in the "normal" mode.
	 * 
	 * This is only a skeleton example, in a real application some additional tweaking will
	 * be necessary to get everything running smoothly. */
	public static void main( String[] args ){
		/* Setting up a frame */
		JTutorialFrame frame = new JTutorialFrame( StationExample.class );
		CControl control = new CControl( frame );
		frame.destroyOnClose( control );
		
		/* We do not want to move around the entire stack, so we change the group behavior */
		control.setGroupBehavior( CGroupBehavior.TOPMOST );
		
		/* We create our stack and some other CStations... */
		CStack stack = new CStack( "stack" );
		control.addStation( stack, true );
		
		CMinimizeArea east = control.createMinimizeArea( "east" );
		CMinimizeArea west = control.createMinimizeArea( "west" );
		CMinimizeArea south = control.createMinimizeArea( "south" );
		CMinimizeArea north = control.createMinimizeArea( "north" );
		
		/* ... and add them to our JFrame */
		frame.add( stack.getStation().getComponent() );
		frame.add( east, BorderLayout.EAST );
		frame.add( west, BorderLayout.WEST );
		frame.add( south, BorderLayout.SOUTH );
		frame.add( north, BorderLayout.NORTH );
		
		/* Now we set up some Dockable to play around with */
		final ColorSingleCDockable red = new ColorSingleCDockable( "Red", Color.RED );
		final ColorSingleCDockable green = new ColorSingleCDockable( "Green", Color.GREEN );
		final ColorSingleCDockable blue = new ColorSingleCDockable( "Blue", Color.BLUE );
		
		control.addDockable( red );
		control.addDockable( green );
		control.addDockable( blue );
		
		/* We fall back to the Core API to make the Dockables visible */
		stack.getStation().add( red.intern(), 0 );
		stack.getStation().add( green.intern(), 1 );
		stack.getStation().add( blue.intern(), 2 );
		
		frame.setVisible( true );
		
		EventQueue.invokeLater( new Runnable(){
			public void run(){
				/* We enabled or disable "maximized" mode depending on the current mode
				 * of the Dockables. */
				setup( red );
				setup( green );
				setup( blue );	
			}
		});
	}
	
	private static void setup( final DefaultCDockable dockable ){
		dockable.setMaximizable( false );
		dockable.addCDockableLocationListener( new CDockableLocationListener(){
			public void changed( CDockableLocationEvent event ){
				ExtendedMode mode = dockable.getExtendedMode();
				dockable.setMaximizable( mode == ExtendedMode.EXTERNALIZED || mode == ExtendedMode.MAXIMIZED );
			}
		});
	}
	
	/* This is our custom CStation that is build upon a StackDockStation. By implementing
	 * CNormalModeArea we can register this station as an area with children in the "normal" mode. */
	private static class CStack extends AbstractCStation<StackDockStation> implements CNormalModeArea{
		public CStack( String id ){
			StackDockStation station = new StackDockStation();
			
			/* Initialization, the CLocation points to 'station'. */
			init( station, id, new CLocation(){
				@Override
				public CLocation getParent(){
					return null;
				}
				
				@Override
				public String findRoot(){
					return getUniqueId();
				}
				
				@Override
				public DockableProperty findProperty( DockableProperty successor ){
					return successor;
				}
				
				@Override
				public ExtendedMode findMode(){
					return ExtendedMode.NORMALIZED;
				}
				
				@Override
				public CLocation aside(){
					return this;
				}
			});
		}

		/* This method is called by the CControl and allows access to some inner API that is
		 * hidden from normal clients. */
		protected void install( CControlAccess access ){
			access.getLocationManager().getNormalMode().add( this );
		}

		
		protected void uninstall( CControlAccess access ){
			access.getLocationManager().getNormalMode().remove( getUniqueId() );
		}

		public CStationPerspective createPerspective(){
			throw new IllegalStateException( "not implemented" );
		}

		public CLocation getBaseLocation(){
			return getStationLocation();
		}

		public boolean isNormalModeChild( Dockable dockable ){
			return isChild( dockable );
		}

		public DockableProperty getLocation( Dockable child ){
			return DockUtilities.getPropertyChain( getStation(), child );
		}

		public void setLocation( Dockable dockable, DockableProperty location, AffectedSet set ){
			set.add( dockable );
			
			if( isChild( dockable )){
				getStation().move( dockable, location );
			}
			else{
				if( !getStation().drop( dockable, location )){
					getStation().drop( dockable );
				}
			}
		}

		public void addModeAreaListener( ModeAreaListener listener ){
			// not required
		}

		public boolean autoDefaultArea(){
			return true;
		}

		public boolean isChild( Dockable dockable ){
			return dockable.getDockParent() == getStation();
		}

		public void removeModeAreaListener( ModeAreaListener listener ){
			// not required
		}

		public void setController( DockController controller ){
			// ignore
		}

		public void setMode( LocationMode mode ){
			// ignore
		}

		public CLocation getCLocation( Dockable dockable ){
			DockableProperty property = DockUtilities.getPropertyChain( getStation(), dockable );
			return getStationLocation().expandProperty( property );
		}

		public CLocation getCLocation( Dockable dockable, Location location ){
			DockableProperty property = location.getLocation();
			if( property == null ){
				return getStationLocation();
			}
			
			return getStationLocation().expandProperty( property );
		}

		public boolean respectWorkingAreas(){
			return true;
		}
	}
}
