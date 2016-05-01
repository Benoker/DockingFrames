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
import bibliothek.gui.dock.layout.DockableProperty;
import bibliothek.gui.dock.station.split.SplitDockProperty;

/**
 * This location points to the center of a {@link CContentArea}.
 * @author Benjamin Sigg
 */
public class CContentAreaCenterLocation extends CSplitLocation{
    /** location of the {@link CContentArea} itself */
    private CBaseLocation base;
    
    /**
     * Creates a new location
     * @param base the location describing a {@link CContentArea}, not <code>null</code>
     */
    public CContentAreaCenterLocation( CBaseLocation base ){
        if( base == null )
            throw new NullPointerException( "base is null" );
        this.base = base;
    }

    /**
     * Gets the location representing the {@link CContentArea} of which this
     * location represents the center.
     * @return the content-area
     */
    public CBaseLocation getBase(){
		return base;
	}
    
    @Override
    public DockableProperty findProperty( DockableProperty successor ){
    	if( successor == null ){
    		return new SplitDockProperty( 0, 0, 1, 1 );
    	}
    	return successor;
    }
    
    @Override
    public String findRoot() {
        CContentArea area = base.getContentArea();
        if( area == null )
            return CContentArea.getCenterIdentifier( CControl.CONTENT_AREA_STATIONS_ID );
        else
            return area.getCenterIdentifier();
    }
    
    @Override
    public String toString() {
        return "[" + findRoot() + "]";
    }
}
