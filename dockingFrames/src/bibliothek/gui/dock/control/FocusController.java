package bibliothek.gui.dock.control;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.event.DockRegisterListener;
import bibliothek.gui.dock.event.DockStationListener;

/**
 * A listener to the {@link DockRegister}, ensuring that always the newest or
 * a visible {@link Dockable} has the focus.
 * @author Benjamin Sigg
 */
public class FocusController implements DockRegisterListener{
	/** a listener added to every {@link DockStation} */
	private StationListener listener = new StationListener();
	/** the controller whose focused {@link Dockable} might be exchanged */
	private DockController controller;
	
	/**
	 * Creates a new focus-controller.
	 * @param controller the controller whose focused {@link Dockable} might be
	 * changed.
	 */
	public FocusController( DockController controller ){
		if( controller == null )
			throw new IllegalArgumentException( "controller must not be null" );
		
		this.controller = controller;
	}
	
	public void dockStationRegistered( DockController controller, DockStation station ){
		station.addDockStationListener( listener );
	}
	
	public void dockStationUnregistered( DockController controller, DockStation station ){
		station.removeDockStationListener( listener );
	}
	
	public void dockableUnregistered( DockController controller, Dockable dockable ){
		if( dockable == controller.getFocusedDockable() )
			controller.setFocusedDockable( null, false );
	}
	
	public void dockStationRegistering( DockController controller, DockStation station ){
		// do nothing
	}

	public void dockableRegistered( DockController controller, Dockable dockable ){
		// do nothing
	}

	public void dockableRegistering( DockController controller, Dockable dockable ){
		// do nothing
	}
	
    /**
     * A listener observing all stations and changing the focused {@link Dockable}
     * when necessary.
     * @author Benjamin Sigg
     */
    private class StationListener implements DockStationListener{
        public void dockableAdded( DockStation station, Dockable dockable ){
            if( !controller.getRelocator().isOnPut() ){
            	Dockable focusedDockable = controller.getFocusedDockable();
                if( dockable == focusedDockable || focusedDockable == null )
                    if( station.isVisible( dockable ))
                        controller.setFocusedDockable( dockable, true );
            }
        }
        
        public void dockableVisibiltySet( DockStation station, Dockable dockable, boolean visible ){
            if( !controller.isOnFocusing() && !visible && controller.isFocused( dockable ) ){
            	DockStation parent = dockable.getDockParent();
            	while( parent != null ){
            		dockable = parent.asDockable();
            		if( dockable != null ){
            			parent = dockable.getDockParent();
            			if( parent != null ){
            				if( parent.isVisible( dockable )){
            					controller.setFocusedDockable( dockable, false );
            					return;
            				}
            			}
            		}
            		else
            			break;
            	}
            	
                controller.setFocusedDockable( null, false );
            }
        }

		public void dockableAdding( DockStation station, Dockable dockable ){
			// do nothing
		}

		public void dockableRemoved( DockStation station, Dockable dockable ){
			// do nothing
		}

		public void dockableRemoving( DockStation station, Dockable dockable ){
			// do nothing
		}
    }
}
