/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2011 Benjamin Sigg
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
import bibliothek.gui.dock.common.mode.ExtendedMode;
import bibliothek.gui.dock.facile.mode.Location;
import bibliothek.gui.dock.layout.DockableProperty;
import bibliothek.gui.dock.station.stack.StackDockProperty;

/**
 * This movement moves an entire {@link StackDockStation} to the place indicated by one of its children.
 * @author Benjamin Sigg
 */
public class StackGroupMovement implements CGroupMovement{
	private StackDockStation dockParent;
	private Dockable dockable;
	private ExtendedMode target;
	
	private DockStation currentStation;
	private Dockable currentDockable;
	
	/**
	 * Creates a new movement object.
	 * @param dockParent the station that should be moved
	 * @param dockable the dockable that indicates the target location
	 * @param target the mode that <code>dockable</code> should have after the movement
	 */
	public StackGroupMovement( StackDockStation dockParent, Dockable dockable, ExtendedMode target ){
		this.dockParent = dockParent;
		this.dockable = dockable;
		this.target = target;
	}
	
	public void apply( CGroupBehaviorCallback callback ){
		// find location of dockable in respect to the other items
		int baseIndex = dockParent.indexOf( dockable );
		Dockable[] children = new Dockable[ dockParent.getDockableCount() ];
		for( int i = 0; i < children.length; i++ ){
			children[i] = dockParent.getDockable( i );
		}
		
		// move the first item and find out where it lands
		callback.setMode( dockable, target );
		
		// check premature end because of multiple Dockables movement.
		boolean oneMissing = false;
		for( Dockable child : children ){
			if( child != dockable ){
				if( callback.getManager().getMode( child ) != target ){
					oneMissing = true;
					break;
				}
				else if( child.getDockParent() != dockable.getDockParent() ){
					oneMissing = true;
					break;
				}
			}
		}
		
		// premature end
		if( !oneMissing ){
			return;
		}
		
		int missing = 0;
		
		// move all the items that were before dockable
		for( int i = baseIndex - 1; i >= 0; i-- ){
			currentDockable = children[i];
			currentStation = dockable.getDockParent();
			Location base = callback.getLocation( dockable );
			Location movingLocation = new Location( base.getMode(), base.getRoot(), copyAndSetStackLocation( base.getLocation(), i - baseIndex + 1 - missing ), true );
			callback.setLocation( currentDockable, movingLocation );
			if( currentDockable.getDockParent() != dockable.getDockParent() ){
				missing++;
			}
		}
		
		// move all the items that were after dockable
		for( int i = baseIndex + 1; i < children.length; i++ ){
			currentDockable = children[i];
			currentStation = dockable.getDockParent();
			Location base = callback.getLocation( dockable );
			Location movingLocation = new Location( base.getMode(), base.getRoot(), copyAndSetStackLocation( base.getLocation(), i - baseIndex - missing ), true );
			callback.setLocation( currentDockable, movingLocation );
			if( currentDockable.getDockParent() != dockable.getDockParent() ){
				missing++;
			}
		}
		
		DockStation newParent = dockable.getDockParent();
		if( newParent instanceof StackDockStation ){
			newParent.setFrontDockable( dockable );
		}
		
		currentStation = null;
		currentDockable = null;
	}
	
	public boolean forceAccept( DockStation parent, Dockable child ){
		return parent != currentStation || child != currentDockable;
	}
	
	private DockableProperty copyAndSetStackLocation( DockableProperty property, int delta ){
		if( property == null ){
			return null;
		}
		
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
