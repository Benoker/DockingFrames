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

import bibliothek.gui.dock.FlapDockStation;
import bibliothek.gui.dock.common.CLocation;
import bibliothek.gui.dock.common.mode.ExtendedMode;
import bibliothek.gui.dock.layout.DockableProperty;
import bibliothek.gui.dock.station.flap.FlapDockProperty;

/**
 * A location which represents a {@link FlapDockStation}.
 * @author Benjamin Sigg
 */
public abstract class CFlapLocation extends CLocation{
    /**
     * Creates a location to append children at the end of the station.
     * @return the location marking the last position
     */
    public CFlapIndexLocation append(){
        return insert( Integer.MAX_VALUE );
    }
    
    /**
     * Creates a location to insert children into the station.
     * @param index the exact position
     * @return a location marking the position <code>index</code>
     */
    public CFlapIndexLocation insert( int index ){
        return new CFlapIndexLocation( this, index );
    }

    @Override
    public CLocation aside() {
        return this;
    }
    
    @Override
    public CLocation expandProperty( DockableProperty property ) {
        if( property instanceof FlapDockProperty ){
            FlapDockProperty flap = (FlapDockProperty)property;
            return insert( flap.getIndex() );
        }
        return null;
    }

    @Override
    public ExtendedMode findMode() {
        return ExtendedMode.MINIMIZED;
    }

    @Override
    public DockableProperty findProperty( DockableProperty successor ) {
        FlapDockProperty property = new FlapDockProperty( Integer.MAX_VALUE );
        property.setSuccessor( successor );
        return property;
    }
}
