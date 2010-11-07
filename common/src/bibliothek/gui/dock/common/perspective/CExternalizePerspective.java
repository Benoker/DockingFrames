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

import bibliothek.gui.dock.common.CExternalizeArea;
import bibliothek.gui.dock.perspective.PerspectiveDockable;
import bibliothek.gui.dock.perspective.PerspectiveStation;
import bibliothek.gui.dock.station.screen.ScreenDockPerspective;
import bibliothek.gui.dock.station.support.PlaceholderMap;
import bibliothek.util.Path;

/**
 * A representation of a {@link CExternalizeArea}.
 * @author Benjamin Sigg
 */
public class CExternalizePerspective implements CStationPerspective{
	/** the intern representation of this perspective */
	private ScreenDockPerspective delegate;
	
	/** the unique identifer of this perspective */
	private String id;
	
	/**
	 * Creates a new, empty perspective.
	 * @param id the unique identifier of this perspective
	 */
	public CExternalizePerspective( String id ){
		if( id == null ){
			throw new IllegalArgumentException( "id is null" );
		}
		if( !Path.isValidPath( id ) ){
			throw new IllegalArgumentException( "id is not a valid id" );
		}
		this.id = id;
		delegate = new ScreenDockPerspective();
	}
	
	public String getUniqueId(){
		return id;
	}

	public ScreenDockPerspective intern(){
		return delegate;
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
	
	public PlaceholderMap getPlaceholders(){
		return delegate.getPlaceholders();
	}
	
	public void setPlaceholders( PlaceholderMap placeholders ){
		delegate.setPlaceholders( placeholders );	
	}
}