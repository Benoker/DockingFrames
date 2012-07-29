/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2012 Herve Guillaume, Benjamin Sigg
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
 * Herve Guillaume
 * rvguillaume@hotmail.com
 * FR - France
 *
 * Benjamin Sigg
 * benjamin_sigg@gmx.ch
 * CH - Switzerland
 */
package bibliothek.gui.dock.toolbar.perspective;

import bibliothek.gui.dock.common.perspective.CDockablePerspective;
import bibliothek.gui.dock.common.perspective.CommonElementPerspective;
import bibliothek.gui.dock.common.perspective.SingleCDockablePerspective;
import bibliothek.gui.dock.perspective.PerspectiveDockable;
import bibliothek.gui.dock.station.toolbar.ToolbarDockPerspective;

/**
 * A wrapper around a {@link ToolbarDockPerspective}, represents a single toolbar.
 * @author Benjamin Sigg
 */
public class CToolbarPerspective {
	/** the internal representation of a toolbar */
	private ToolbarDockPerspective delegate;
	
	/**
	 * Creates a new perspective wrapping around <code>delegate</code>.
	 * @param delegate the internal representation, not <code>null</code>
	 */
	public CToolbarPerspective( ToolbarDockPerspective delegate ){
		if( delegate == null ){
			throw new IllegalArgumentException( "delegate must not be null" );
		}
		this.delegate = delegate;
	}
	
	/**
	 * Allows access to the internal representation of this perspective.
	 * @return the internal representation, not <code>null</code>
	 */
	public ToolbarDockPerspective getDelegate(){
		return delegate;
	}
	
	/**
	 * Gets the number of children of this toolbar.
	 * @return the total number of children
	 */
	public int getItemCount(){
		return delegate.getDockableCount();
	}
	
	/**
	 * Gets the item at location <code>index</code>.
	 * @param index the location of the item
	 * @return the item or <code>null</code> if the item has the wrong type, the item
	 * usually is of type {@link SingleCDockablePerspective}
	 */
	public CDockablePerspective getItem( int index ){
		PerspectiveDockable item = delegate.getDockable( index );
		if( item instanceof CommonElementPerspective ){
			return ((CommonElementPerspective)item).getElement().asDockable();
		}
		else{
			return null;
		}
	}
	
	/**
	 * Creates and adds a new {@link SingleCDockablePerspective} at the end of this toolbar.
	 * @param id the unique identifier of the item
	 */
	public void add( String id ){
		add( new SingleCDockablePerspective( id ));
	}
	
	/**
	 * Creates and adds a new {@link SingleCDockablePerspective} at location <code>index</code>
	 * in this toolbar.
	 * @param index the location of the new item
	 * @param id the unique identifier of the item
	 */
	public void add( int index, String id ){
		add( index, new SingleCDockablePerspective( id ));
	}
	
	/**
	 * Adds <code>item</code> at the end of this toolbar.
	 * @param item the item to add, not <code>null</code>
	 */
	public void add( CDockablePerspective item ){
		delegate.add( item.intern().asDockable() );
	}
	
	/**
	 * Inserts <code>item</code> at location <code>index</code> in this toolbar. 
	 * @param index the location of the new item
	 * @param item the new item
	 */
	public void add( int index, CDockablePerspective item ){
		delegate.add( index, item.intern().asDockable() );
	}
	
	/**
	 * Removes the <code>index</code>'th child of this toolbar.
	 * @param index the location of the item to remove
	 */
	public void remove( int index ){
		delegate.remove( index );
	}
	
	/**
	 * Removes <code>item</code> from this toolbar
	 * @param item the item to remove
	 */
	public void remove( CDockablePerspective item ){
		delegate.remove( item.intern().asDockable() );
	}
}
