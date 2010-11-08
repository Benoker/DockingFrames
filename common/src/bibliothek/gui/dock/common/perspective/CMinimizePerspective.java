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

import bibliothek.gui.dock.common.CMinimizeArea;
import bibliothek.gui.dock.perspective.PerspectiveDockable;
import bibliothek.gui.dock.perspective.PerspectiveStation;
import bibliothek.gui.dock.station.flap.FlapDockPerspective;
import bibliothek.gui.dock.station.support.PlaceholderMap;

/**
 * This {@link PerspectiveStation} represents a {@link CMinimizeArea}.
 * @author Benjamin Sigg
 */
public class CMinimizePerspective implements CStationPerspective{
	/** the intern representation of this perspective */
	private FlapDockPerspective delegate;
	
	/** a unique identifier of this station */
	private String id;
	
	/**
	 * Creates a new, empty perspective.
	 * @param id the unique identifier of this perspective
	 */
	public CMinimizePerspective( String id ){
		if( id == null ){
			throw new IllegalArgumentException( "id is null" );
		}
		this.id = id;
		delegate = new FlapDockPerspective();
	}

	/**
	 * Adds <code>dockable</code> at the end of the list of children of this area.
	 * @param dockable the element to add
	 */
	public void add( PerspectiveDockable dockable ){
		delegate.add( dockable );
	}
	
	/**
	 * Inserts <code>dockable</code> at location <code>index</code> to the list of children of this area.
	 * @param index the location of <code>dockable</code>
	 * @param dockable the element to insert
	 */
	public void insert( int index, PerspectiveDockable dockable ){
		delegate.insert( index, dockable );
	}
	
	/**
	 * Adds a placeholder for <code>dockable</code> at the end of the list of children of this area.
	 * @param dockable the element for which a placeholder will be registered
	 */
	public void addPlaceholder( PerspectiveDockable dockable ){
		delegate.addPlaceholder( dockable );
	}
	
	/**
	 * Inserts a placeholder for <code>dockable</code> at location <code>index</code> in the list of
	 * children of this area.
	 * @param index the location of the placeholder
	 * @param dockable the element for which a placeholder will be registered
	 */
	public void insertPlaceholder( int index, PerspectiveDockable dockable ){
		delegate.insertPlaceholder( index, dockable );
	}
	
	/**
	 * Gets the location of <code>dockable</code> on this area.
	 * @param dockable some child of this area
	 * @return the location or -1 if not found
	 */
	public int indexOf( PerspectiveDockable dockable ){
		return delegate.indexOf( dockable );
	}
	
	/**
	 * Removes <code>dockable</code> from this area.
	 * @param dockable the element to remove
	 * @return <code>true</code> if <code>dockable</code> was found and removed, 
	 * <code>false</code> otherwise
	 */
	public boolean remove( PerspectiveDockable dockable ){
		return delegate.remove( dockable );
	}
	
	/**
	 * Removes the <code>index</code>'th child of this area.
	 * @param index the index of the child to remove
	 * @return the child that was removed
	 */
	public PerspectiveDockable remove( int index ){
		return delegate.remove( index );
	}
	
	public PerspectiveDockable getDockable( int index ){
		return delegate.getDockable( index );
	}

	public int getDockableCount(){
		return delegate.getDockableCount();
	}

	public PerspectiveDockable asDockable(){
		return null;
	}

	public PerspectiveStation asStation(){
		return this;
	}

	public String getFactoryID(){
		return delegate.getFactoryID();
	}
	
	public String getUniqueId(){
		return id;
	}
	
	public FlapDockPerspective intern(){
		return delegate;
	}

	public PlaceholderMap getPlaceholders(){
		return delegate.getPlaceholders();
	}
	
	public void setPlaceholders( PlaceholderMap placeholders ){
		delegate.setPlaceholders( placeholders );	
	}
}
