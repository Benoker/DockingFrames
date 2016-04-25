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
 * A location which represents the index on a {@link FlapDockStation}. 
 * @author Benjamin Sigg
 */
public class CFlapIndexLocation extends AbstractStackholdingLocation{
    private int index;
    private CFlapLocation parent;
    
    /**
     * Creates a new location
     * @param parent the {@link FlapDockStation} to which this location
     * belongs
     * @param index the exact position of this location
     */
    public CFlapIndexLocation( CFlapLocation parent, int index ){
        if( parent == null )
            throw new NullPointerException( "parent must not be null" );
        
        this.parent = parent;
        this.index = index;
    }
    
    @Override
    public CFlapLocation getParent(){
    	return parent;
    }
    
    /**
     * Gets the exact location of this location on its parent.
     * @return the exact location
     */
    public int getIndex() {
        return index;
    }
    
	/**
	 * @deprecated see {@link CLocation#aside()} for an explanation.
	 */
	@Deprecated
    @Override
    public CLocation aside() {
    	return stack( 1 );
    }

    @Override
    public ExtendedMode findMode() {
        return ExtendedMode.MINIMIZED;
    }

    @Override
    public DockableProperty findProperty( DockableProperty successor ) {
        FlapDockProperty property = new FlapDockProperty( index );
        property.setSuccessor( successor );
        
        if( parent != null ){
        	return parent.findProperty( property );
        }
        
        return property;
    }

    @Override
    public String findRoot() {
        return parent.findRoot();
    }
    
    @Override
    public String toString() {
        return String.valueOf( parent ) + " [index " + index + "]"; 
    }
}
