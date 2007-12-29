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
package bibliothek.gui.dock.facile.location;

import bibliothek.gui.dock.facile.FCenter;
import bibliothek.gui.dock.facile.FControl;
import bibliothek.gui.dock.facile.FDockable.ExtendedMode;
import bibliothek.gui.dock.layout.DockableProperty;
import bibliothek.gui.dock.station.flap.FlapDockProperty;

/**
 * A location aiming at minimized elements.
 * @author Benjamin Sigg
 */
public class FMinimizedLocation extends AbstractStackholdingLocation{
	/** the location describing where the "minimized-area" is */
	private FBaseLocation parent;
	/** telling which side this location occupies */
	private Side side;
	/** the location in the list of elements */
	private int index;
	
	/**
	 * Creates a new location.
	 * @param parent the location describing where the "minimized-areas" are
	 * @param side the side this location is aiming at.
	 * @param index the index of this location in the list of all minimized
	 * elements.
	 */
	public FMinimizedLocation( FBaseLocation parent, Side side, int index ){
		if( parent == null )
			throw new NullPointerException( "parent is null" );
		
		if( side == null )
			throw new NullPointerException( "side is null" );
		
		this.parent = parent;
		this.side = side;
		this.index = index;
	}
	
	@Override
	public String findRoot(){
		FCenter center = parent.getCenter();
		String id;
		if( center == null )
			id = FControl.CENTER_STATIONS_ID;
		else
			id = center.getUniqueId();
		
		switch( side ){
			case NORTH: return FCenter.getNorthIdentifier( id );
			case SOUTH: return FCenter.getSouthIdentifier( id );
			case EAST: return FCenter.getEastIdentifier( id );
			case WEST: return FCenter.getWestIdentifier( id );
			default: return null;
		}
	}
	
	@Override
	public ExtendedMode findMode(){
		return ExtendedMode.MINIMIZED;
	}
	
	@Override
	public DockableProperty findProperty( DockableProperty successor ){
		FlapDockProperty flap = new FlapDockProperty( index );
		flap.setSuccessor( successor );
		return flap;
	}
}
