/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2010 Benjamin Sigg
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
package bibliothek.gui.dock.common.group;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.StackDockStation;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.mode.ExtendedMode;
import bibliothek.gui.dock.facile.mode.LocationMode;
import bibliothek.gui.dock.facile.mode.LocationModeManager;
import bibliothek.gui.dock.station.Combiner;
import bibliothek.gui.dock.util.DockUtilities;

/**
 * This {@link CGroupBehavior} assumes that all {@link Dockable}s which share a common {@link StackDockStation} as
 * parent belong to the same group. This behavior also assumes that all {@link Combiner}s will create new 
 * {@link StackDockStation}s and that {@link StackDockStation}s cannot be put into each other. All these assumptions
 * hold true with the default settings of a {@link CControl}.<br>
 * This behavior does not move around entire {@link StackDockStation}s, it moves around each {@link Dockable} individually.
 * This has the benefit that {@link StackDockStation}s cannot accidentally be put into each other.
 * @author Benjamin Sigg
 */
public class StackGroupBehavior implements CGroupBehavior {
	public CGroupMovement prepare( LocationModeManager<? extends LocationMode> manager, Dockable dockable, ExtendedMode target ){
		if( isGrouped( dockable, target, manager ) ) {
			return new StackGroupMovement( (StackDockStation)dockable.getDockParent(), dockable, target );
		}
		else {
			return new SingleGroupMovement( dockable, target );
		}
	}

	private boolean isGrouped( Dockable dockable, ExtendedMode target, LocationModeManager<?> manager ){
		DockStation parent = dockable.getDockParent();
		if( parent instanceof StackDockStation ) {
			for( int i = 0, n = parent.getDockableCount(); i < n; i++ ) {
				if( !manager.isModeAvailable( parent.getDockable( i ), target ) ) {
					return false;
				}
			}
			return true;
		}
		return false;
	}
	
	public Dockable getGroupElement( LocationModeManager<? extends LocationMode> manager, Dockable dockable, ExtendedMode mode  ){
		DockStation parent = dockable.getDockParent();
		
		if( parent instanceof StackDockStation ){
			if( parent.asDockable().getDockParent() == null ){
				// cannot move around a dockable without location
				return dockable;
			}
			
			for( int i = 0, n = parent.getDockableCount(); i<n; i++ ){
				Dockable check = parent.getDockable( i );
				if( check != dockable ){
					if( !manager.isModeAvailable( check, mode ) ){
						return dockable;
					}
				}
			}
			return (StackDockStation)parent;
		}
		
		return dockable;
	}
	
	public Dockable getReplaceElement( LocationModeManager<? extends LocationMode> manager, Dockable old, Dockable dockable, ExtendedMode mode ){
		if( old == dockable )
			return null;
		
		if( !DockUtilities.isAncestor( old, dockable ) )
			return null;
		
		DockStation station = old.asDockStation();
		if( station == null )
			return old;
		
		if( station.getDockableCount() == 2 ){
			if( station.getDockable( 0 ) == dockable )
				return station.getDockable( 1 );
			if( station.getDockable( 1 ) == dockable )
				return station.getDockable( 0 );
		}
		
		return old;
	}
	
	public boolean shouldForwardActions( LocationModeManager<? extends LocationMode> manager, DockStation station, Dockable dockable, ExtendedMode mode ){
		Dockable dockStation = station.asDockable();
		if( dockStation != null && !manager.isModeAvailable( dockStation, mode )){
			return false;
		}
		
		for( int i = 0, n = station.getDockableCount(); i<n; i++ ){
			Dockable child = station.getDockable( i );
			if( !manager.isModeAvailable( child, mode ) ){
				return false;
			}
		}
		return true;
	}
}
