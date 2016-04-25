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

import bibliothek.gui.dock.common.CGridArea;

/**
 * A location that represents a {@link CGridArea}.
 * @author Benjamin Sigg
 */
public class CGridAreaLocation extends CSplitLocation{
    private CGridArea area;
    
    /**
     * Creates the new location
     * @param area the grid area which is represented by this location
     */
    public CGridAreaLocation( CGridArea area ){
        if( area == null )
            throw new NullPointerException( "area must not be null" );
        
        this.area = area;
    }
    
    /**
     * Gets the area which is represented by this location.
     * @return the area, not <code>null</code>
     */
    public CGridArea getArea(){
		return area;
	}

    @Override
    public String findRoot() {
        return area.getUniqueId();
    }
    
    @Override
    public String toString() {
        return "[" + findRoot() + "]";
    }
}
