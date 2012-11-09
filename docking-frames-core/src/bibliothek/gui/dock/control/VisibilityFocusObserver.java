/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2007 Benjamin Sigg
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 * Benjamin Sigg
 * benjamin_sigg@gmx.ch
 * CH - Switzerland
 */
package bibliothek.gui.dock.control;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.control.focus.DefaultFocusRequest;
import bibliothek.gui.dock.event.DockRegisterAdapter;
import bibliothek.gui.dock.event.DockStationAdapter;

/**
 * A listener to the {@link DockRegister}, ensuring that always the newest or
 * a visible {@link Dockable} has the focus.
 * @author Benjamin Sigg
 */
public class VisibilityFocusObserver extends DockRegisterAdapter {
	/** a listener added to every {@link DockStation} */
	private StationListener listener = new StationListener();
	/** the controller whose focused {@link Dockable} might be exchanged */
	private DockController controller;
	
	/**
	 * Creates a new focus-controller.
	 * @param controller the controller whose focused {@link Dockable} might be
	 * changed.
	 */
	public VisibilityFocusObserver( DockController controller ){
		if( controller == null )
			throw new IllegalArgumentException( "controller must not be null" );
		
		this.controller = controller;
	}
	
	@Override
	public void dockStationRegistered( DockController controller, DockStation station ){
		station.addDockStationListener( listener );
	}
	
	@Override
	public void dockStationUnregistered( DockController controller, DockStation station ){
		station.removeDockStationListener( listener );
	}
	
	@Override
	public void dockableUnregistered( DockController controller, Dockable dockable ){
		if( dockable == controller.getFocusedDockable() )
			controller.setFocusedDockable( new DefaultFocusRequest( null, null, false ));
	}
		
    /**
     * A listener observing all stations and changing the focused {@link Dockable}
     * when necessary.
     * @author Benjamin Sigg
     */
    private class StationListener extends DockStationAdapter {
        @Override
        public void dockableAdded( DockStation station, Dockable dockable ){
            if( !controller.getRelocator().isOnPut() ){
            	Dockable focusedDockable = controller.getFocusedDockable();
                if( dockable == focusedDockable || focusedDockable == null ){
                    if( dockable.isDockableShowing() ){
                        controller.setFocusedDockable( new DefaultFocusRequest( dockable, null, true ));
                    }
                }
            }
        }
        
        @Override
        public void dockableShowingChanged( DockStation station, Dockable dockable, boolean visible ){
            if( !controller.isOnFocusing() && !visible && controller.isFocused( dockable ) ){
            	DockStation parent = dockable.getDockParent();
            	while( parent != null ){
            		dockable = parent.asDockable();
            		if( dockable != null ){
            			parent = dockable.getDockParent();
            			if( parent != null ){
            				if( parent.isChildShowing( dockable )){
            					controller.setFocusedDockable( new DefaultFocusRequest( dockable, null, false ));
            					return;
            				}
            			}
            		}
            		else
            			break;
            	}
            	
                controller.setFocusedDockable( new DefaultFocusRequest( null, null, false ));
            }
        }
    }
}
