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
package bibliothek.gui.dock.common.perspective;

import bibliothek.gui.dock.perspective.PerspectiveDockable;
import bibliothek.gui.dock.perspective.PerspectiveStation;
import bibliothek.gui.dock.station.stack.StackDockPerspective;

/**
 * This perspective represents a group of dockables.
 * @author Benjamin Sigg
 */
public class CStackPerspective extends StackDockPerspective implements ShrinkablePerspectiveStation{
	/**
	 * Creates a new station.
	 */
	public CStackPerspective(){
		// ignore
	}

	/**
	 * Creates a new station.
	 * @param children the children of this perspective
	 * @param selection the selected child, can be <code>null</code>
	 */
	public CStackPerspective( PerspectiveDockable[] children, PerspectiveDockable selection ){
		super( children, selection );
	}
	
	/**
	 * Creates a new station.
	 * @param children the children of this station
	 * @param selection the selected child, can be <code>null</code>
	 */
	public CStackPerspective( CDockablePerspective[] children, CDockablePerspective selection ){
		this( toArray( children ), selection == null ? null : selection.intern().asDockable() );
	}
	
	private static PerspectiveDockable[] toArray( CDockablePerspective[] children ){
		PerspectiveDockable[] result = new PerspectiveDockable[ children.length ];
		for( int i = 0; i < result.length; i++ ){
			result[i] = children[i].intern().asDockable();
		}
		return result;
	}

	public PerspectiveDockable shrink(){
		int count = getDockableCount();
		if( count > 1 ){
			return this;
		}
		
		PerspectiveStation parent = getParent();
		
		if( count == 1 ){
			PerspectiveDockable result = getDockable( 0 );
			if( parent != null ){
				parent.replace( this, result );
				return result;
			}
			else{
				remove( 0 );
				return result;
			}
		}
		else{
			if( parent != null ){
				parent.remove( this );
				return null;
			}
			else{
				return null;
			}
		}
	}
	
	/**
	 * Inserts <code>dockable</code> at location <code>index</code>.
	 * @param index the location where to insert <code>dockable</code>
	 * @param dockable the element to insert, not <code>null</code>
	 */
	public void insert( int index, CDockablePerspective dockable ){
		insert( index, dockable.intern().asDockable() );
	}
	
	/**
	 * Adds <code>dockable</code> at the end of the list of dockables.
	 * @param dockable the element to add at the end
	 */
	public void add( CDockablePerspective dockable ){
		add( dockable.intern().asDockable() );
	}
	
	/**
	 * Removes the element <code>dockable</code> from this station.
	 * @param dockable the element to remove
	 */
	public void remove( CDockablePerspective dockable ){
		remove( dockable.intern().asDockable() );
	}
}
