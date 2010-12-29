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
import bibliothek.gui.dock.facile.mode.Location;
import bibliothek.gui.dock.facile.mode.LocationModeManager;
import bibliothek.gui.dock.layout.DockableProperty;
import bibliothek.gui.dock.station.Combiner;
import bibliothek.gui.dock.station.stack.StackDockProperty;

/**
 * This {@link CGroupBehavior} assumes that all {@link Dockable}s which share a common {@link StackDockStation} as
 * parent belong to the same group. This behavior also assumes that all {@link Combiner}s will create new 
 * {@link StackDockStation}s and that {@link StackDockStation}s cannot be put into each other. All these assumptions
 * hold true with the default settings of a {@link CControl}.<br>
 * This behavior does not move around entire {@link StackDockStation}s, it moves around each {@link Dockable} indiviually.
 * This has the benefit that {@link StackDockStation}s cannot accidentally be put into each other.
 * @author Benjamin Sigg
 */
public class StackGroupBehavior implements CGroupBehavior {
	public void forward( Dockable dockable, ExtendedMode target, CGroupBehaviorCallback callback ){
		if( isGrouped( dockable, target, callback ) ) {
			moveGroup( (StackDockStation)dockable.getDockParent(), dockable, target, callback );
		}
		else {
			callback.setMode( dockable, target );
		}
	}

	private boolean isGrouped( Dockable dockable, ExtendedMode target, CGroupBehaviorCallback callback ){
		if( target == ExtendedMode.MAXIMIZED ){
			return false;
		}
		
		DockStation parent = dockable.getDockParent();
		if( parent instanceof StackDockStation ) {
			LocationModeManager<?> manager = callback.getManager();

			for( int i = 0, n = parent.getDockableCount(); i < n; i++ ) {
				if( !manager.isModeAvailable( parent.getDockable( i ), target ) ) {
					return false;
				}
			}
			return true;
		}
		return false;
	}
	
	private void moveGroup( StackDockStation dockParent, Dockable dockable, ExtendedMode target, CGroupBehaviorCallback callback ){
		// find location of dockable in respect to the other items
		int baseIndex = dockParent.indexOf( dockable );
		Dockable[] children = new Dockable[ dockParent.getDockableCount() ];
		for( int i = 0; i < children.length; i++ ){
			children[i] = dockParent.getDockable( i );
		}
		
		// move the first item and find out where it lands
		callback.setMode( dockable, target );
		
		// move all the items that were before dockable
		for( int i = baseIndex - 1; i >= 0; i-- ){
			Location base = callback.getLocation( dockable );
			Dockable moving = children[i];
			Location movingLocation = new Location( base.getMode(), base.getRoot(), copyAndSetStackLocation( base.getLocation(), i - baseIndex + 1 ) );
			callback.setLocation( moving, movingLocation );
		}
		
		// move all the items that were after dockable
		for( int i = baseIndex + 1; i < children.length; i++ ){
			Location base = callback.getLocation( dockable );
			Dockable moving = children[i];
			Location movingLocation = new Location( base.getMode(), base.getRoot(), copyAndSetStackLocation( base.getLocation(), i - baseIndex ) );
			callback.setLocation( moving, movingLocation );
		}
		
		DockStation newParent = dockable.getDockParent();
		if( newParent instanceof StackDockStation ){
			newParent.setFrontDockable( dockable );
		}
	}
	
	private DockableProperty copyAndSetStackLocation( DockableProperty property, int delta ){
		property = property.copy();
		DockableProperty last = property;
		while( last.getSuccessor() != null ){
			last = last.getSuccessor();
		}
		if( last instanceof StackDockProperty ){
			((StackDockProperty)last).setIndex( ((StackDockProperty)last).getIndex() + delta );
		}
		else{
			StackDockProperty stack = new StackDockProperty( delta );
			last.setSuccessor( stack );
		}
		return property;
	}
}
