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
}
