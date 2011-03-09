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
package bibliothek.gui.dock.common.location;

import bibliothek.gui.dock.common.CContentArea;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.mode.ExtendedMode;

/**
 * A location aiming at minimized elements.
 * @author Benjamin Sigg
 */
public class CMinimizedLocation extends CFlapLocation{
	/** the location describing where the "minimized-area" is */
	private CBaseLocation parent;
	/** telling which side this location occupies */
	private Side side;
	
	/**
	 * Creates a new location.
	 * @param parent the location describing where the "minimized-areas" are
	 * @param side the side this location is aiming at.
	 */
	public CMinimizedLocation( CBaseLocation parent, Side side ){
		super( parent );
		
		if( side == null )
			throw new NullPointerException( "side is null" );
		
		this.parent = parent;
		this.side = side;
	}
	
	/**
	 * Gets the side of the {@link CContentArea} where this minimized-location
	 * points to.
	 * @return the side
	 */
	public Side getSide(){
		return side;
	}
	
	@Override
	public ExtendedMode findMode(){
		return ExtendedMode.MINIMIZED;
	}
	
	@Override
	public String findRoot(){
		CContentArea center = parent.getContentArea();
		String id;
		if( center == null )
			id = CControl.CONTENT_AREA_STATIONS_ID;
		else
			id = center.getUniqueId();
		
		switch( side ){
			case NORTH: return CContentArea.getNorthIdentifier( id );
			case SOUTH: return CContentArea.getSouthIdentifier( id );
			case EAST: return CContentArea.getEastIdentifier( id );
			case WEST: return CContentArea.getWestIdentifier( id );
			default: return null;
		}
	}
	
	@Override
	public String toString() {
	    return String.valueOf( parent ) + " [minimized " + side + "]";
	}
}
