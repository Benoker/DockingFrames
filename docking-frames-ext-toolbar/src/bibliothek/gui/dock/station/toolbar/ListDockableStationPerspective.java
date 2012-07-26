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
package bibliothek.gui.dock.station.toolbar;

import bibliothek.gui.dock.layout.DockableProperty;
import bibliothek.gui.dock.perspective.PerspectiveDockable;
import bibliothek.gui.dock.perspective.PerspectiveStation;
import bibliothek.gui.dock.station.support.PerspectivePlaceholderList;
import bibliothek.gui.dock.station.support.PlaceholderMap;
import bibliothek.gui.dock.util.DockUtilities;
import bibliothek.util.FrameworkOnly;
import bibliothek.util.Path;

/**
 * An abstract implementation of a {@link PerspectiveStation} whose children are organized in a list.
 * @author Benjamin Sigg
 */
public abstract class ListDockableStationPerspective implements PerspectiveStation, PerspectiveDockable{
	/** the parent of this dockable */
	private PerspectiveStation parent;

	/** all the children of this station */
	private PerspectivePlaceholderList<PerspectiveDockable> dockables;
	
	/**
	 * Creates a new, empty perspective
	 */
	public ListDockableStationPerspective(){
		dockables = new PerspectivePlaceholderList<PerspectiveDockable>();
	}
	
	/**
	 * Creates a new perspective, adds <code>children</code> to this station.
	 * @param children the children to add
	 */
	public ListDockableStationPerspective( PerspectiveDockable[] children ){
		dockables = new PerspectivePlaceholderList<PerspectiveDockable>();
		for( PerspectiveDockable child : children ){
			DockUtilities.ensureTreeValidity( this, child );
			child.setParent( this );
			dockables.dockables().add( child );
		}
	}

	@Override
	public PerspectiveStation asStation(){
		return this;
	}

	@Override
	public PerspectiveDockable asDockable(){
		return this;
	}

	@Override
	public int getDockableCount(){
		return dockables.dockables().size();
	}

	@Override
	public PerspectiveDockable getDockable( int index ){
		return dockables.dockables().get( index );
	}

	/**
	 * Gets the location of <code>dockable</code> on this station.
	 * @param dockable the item to search
	 * @return the location or <code>-1</code> if not found
	 */
	public int indexOf( PerspectiveDockable dockable ){
		return dockables.dockables().indexOf( dockable );
	}
	
	/**
	 * Adds <code>dockable</code> at the end of the list of children.
	 * @param dockable the item to add, not <code>null</code>
	 */
	public void add( PerspectiveDockable dockable ){
		DockUtilities.ensureTreeValidity( this, dockable );
		dockables.dockables().add( dockable );
		dockable.setParent( this );
	}
	
	/**
	 * Adds <code>dockable</code> at location <code>index</code> in the list of children.
	 * @param index the location of the new child
	 * @param dockable the new child, not <code>null</code>
	 */
	public void add( int index, PerspectiveDockable dockable ){
		DockUtilities.ensureTreeValidity( this, dockable );
		dockables.dockables().add( index, dockable );
		dockable.setParent( this );
	}
	
	/**
	 * Gets direct access to the list of dockables, should be used with care.
	 * @return the list of dockables
	 */
	@FrameworkOnly
	public PerspectivePlaceholderList<PerspectiveDockable> getDockables(){
		return dockables;
	}
	
	/**
	 * Sets the list which contains all the children.
	 * @param dockables the list of the children
	 */
	protected void setDockables( PerspectivePlaceholderList<PerspectiveDockable> dockables ){
		this.dockables = dockables;
	}
	
	@Override
	public DockableProperty getDockableProperty( PerspectiveDockable child, PerspectiveDockable target ){
		int index = dockables.dockables().indexOf( child );
		Path placeholder = null;
		if( target != null ){
			placeholder = target.getPlaceholder();
		}
		else{
			placeholder = child.getPlaceholder();
		}
		return getDockableProperty( index, placeholder, child, target );
	}
	
	/**
	 * Called by {@link #getDockableProperty(PerspectiveDockable, PerspectiveDockable)}, returns a new 
	 * {@link DockableProperty} which matches the location described by <code>index</code> and <code>placeholder</code>.
	 * @param index the index of the item in the list of dockables, can be <code>-1</code>
	 * @param placeholder the name of the item, can be <code>null</code>
	 * @param child the child whose location is requested
	 * @param target the item whose location is requested, this may be <code>child</code>
	 * @return the location of <code>child</code>
	 */
	protected abstract DockableProperty getDockableProperty( int index, Path placeholder, PerspectiveDockable child, PerspectiveDockable target );

	@Override
	public PlaceholderMap getPlaceholders(){
		return dockables.toMap();
	}

	@Override
	public void setPlaceholders( PlaceholderMap placeholders ){
		if( getDockableCount() > 0 ){
			throw new IllegalStateException( "there are already children on this station" );
		}
		dockables = new PerspectivePlaceholderList<PerspectiveDockable>( placeholders );
	}

	/**
	 * Removes the {@link PerspectiveDockable} which is stored at location <code>index</code>.
	 * @param index the index of the item to remove
	 * @return the item that was removed
	 */
	public PerspectiveDockable remove( int index ){
		PerspectiveDockable dockable = getDockable( index );
		dockables.remove( index );
		dockable.setParent( null );
		return dockable;
	}
	
	@Override
	public boolean remove( PerspectiveDockable dockable ){
		int index = indexOf( dockable );
		if( index == -1 ){
			return false;
		}
		dockables.remove( index );
		dockable.setParent( null );
		return true;
	}

	@Override
	public void replace( PerspectiveDockable oldDockable, PerspectiveDockable newDockable ){
		int index = dockables.dockables().indexOf( oldDockable );
		if( index < 0 ){
			throw new IllegalArgumentException( "oldDockable is not child of this station" );
		}
		DockUtilities.ensureTreeValidity( this, newDockable );
		
		dockables.remove( index );
		dockables.dockables().add( index, newDockable );
		oldDockable.setParent( null );
		newDockable.setParent( this );
	}

	@Override
	public Path getPlaceholder(){
		return null;
	}

	@Override
	public PerspectiveStation getParent(){
		return parent;
	}

	@Override
	public void setParent( PerspectiveStation parent ){
		this.parent = parent;
	}
}
