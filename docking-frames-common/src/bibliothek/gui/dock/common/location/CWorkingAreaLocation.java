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

import bibliothek.gui.dock.common.CLocation;
import bibliothek.gui.dock.common.CWorkingArea;
import bibliothek.gui.dock.common.mode.ExtendedMode;
import bibliothek.gui.dock.layout.DockableProperty;
import bibliothek.gui.dock.station.split.SplitDockProperty;

/**
 * A location representing a {@link CWorkingArea}.
 * @author Benjamin Sigg
 */
public class CWorkingAreaLocation extends CSplitLocation{
    /** the area to which this location relates */
    private CWorkingArea area;
    
    /**
     * Creates a new location.
     * @param area the area which is represented by this location
     */
    public CWorkingAreaLocation( CWorkingArea area ){
        if( area == null )
            throw new NullPointerException( "area must not be null" );
        this.area = area;
    }
    
    /**
     * Gets the workingarea to which this location relates.
     * @return the area or <code>null</code> if the default center is meant.
     */
    public CWorkingArea getWorkingArea(){
        return area;
    }
    
    @Override
    public CLocation aside() {
        return this;
    }
    
    @Override
    public ExtendedMode findMode() {
        return ExtendedMode.NORMALIZED;
    }

    @Override
    public DockableProperty findProperty( DockableProperty successor ) {
    	if( successor == null ){
    		return new SplitDockProperty( 0, 0, 1, 1 );
    	}
        return successor;
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
