/**
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2007 Benjamin Sigg
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * Benjamin Sigg
 * benjamin_sigg@gmx.ch
 * 
 * Wunderklingerstr. 59
 * 8215 Hallau
 * CH - Switzerland
 */


package bibliothek.gui.dock.station.stack;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import bibliothek.gui.dock.AbstractDockableProperty;
import bibliothek.gui.dock.Dockable;
import bibliothek.gui.dock.station.StackDockStation;

/**
 * A StackDockProperty stores the location of a {@link Dockable}
 * on a {@link StackDockStation}. The <code>Dockables</code> on
 * a <code>StackDockStation</code> are ordered in some way, and
 * the location is used to tell which <code>Dockable</code> is
 * in front of another <code>Dockable</code>.
 * @author Benjamin Sigg
 */
public class StackDockProperty extends AbstractDockableProperty {
    /** The first location on a {@link StackDockStation} */
    public static final StackDockProperty FRONT = new StackDockProperty( 0 );
    
    /** The last location on a {@link StackDockStation} */
    public static final StackDockProperty BACK = new StackDockProperty( Integer.MAX_VALUE );
    
    private int index;
    
    /**
     * Constructs a property.
     * @param index The location
     */
    public StackDockProperty( int index ){
        setIndex( index );
    }
    
    /**
     * Constructs a property with a location equal to 0.
     */
    public StackDockProperty(){
    	// do nothing
    }
    
    /**
     * Sets the location which is determined by this property. The smallest
     * location is 0.
     * @param index the location
     */
    public void setIndex( int index ) {
        this.index = index;
    }
    
    /**
     * Gets the location of this property.
     * @return the location
     * @see #setIndex(int)
     */
    public int getIndex() {
        return index;
    }
    
    public String getFactoryID() {
        return StackDockPropertyFactory.ID;
    }

    public void store( DataOutputStream out ) throws IOException {
        out.writeInt( index );
    }

    public void load( DataInputStream in ) throws IOException {
        index = in.readInt();
    }
}
