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
import bibliothek.gui.dock.station.split.SplitDockPathProperty;

/**
 * The root of a path of turns, leading to the location of an element, beginning
 * at the "normalized-area".
 * @author Benjamin Sigg
 */
public class FCenterTreeLocationRoot extends AbstractFCenterTreeLocation{
	/** the element describing where the "normalized-area" is */
	private FBaseLocation parent;
	
	/**
	 * Creates a new location.
	 * @param parent the location describing where the "normalized-area" is.
	 * @param size the relative size of this location
	 * @param side the side which is occupied by this location
	 * @see AbstractFCenterTreeLocation#AbstractFCenterTreeLocation(double, Side)
	 */
	public FCenterTreeLocationRoot( FBaseLocation parent, double size, Side side ){
		super( size, side );
		if( parent == null )
			throw new NullPointerException( "Parent must not be null" );
		
		this.parent = parent;
	}
	
	@Override
	public String findRoot(){
		FCenter center = parent.getCenter();
		if( center == null )
			return FCenter.getCenterIdentifier( FControl.CENTER_STATIONS_ID );
		else
			return center.getCenterIdentifier();
	}
	
	@Override
	protected SplitDockPathProperty findPathProperty(){
		return new SplitDockPathProperty();
	}
}
