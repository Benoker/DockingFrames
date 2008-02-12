/**
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

package bibliothek.gui.dock.station.flap;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.FlapDockStation;
import bibliothek.gui.dock.layout.AbstractDockableProperty;
import bibliothek.util.xml.XElement;

/**
 * FlapDockProperties are used on the {@link FlapDockStation} to determine
 * the location of a {@link Dockable}.
 * @author Benjamin Sigg
 *
 */
public class FlapDockProperty extends AbstractDockableProperty {
	/**
	 * The location of the first {@link Dockable}.
	 */
    public static final FlapDockProperty FIRST = new FlapDockProperty( 0 );
    
    /**
     * The location of the last {@link Dockable}.
     */
    public static final FlapDockProperty LAST = new FlapDockProperty( Integer.MAX_VALUE );
    
    private int index;
    
    /**
     * Constructs a FlapDockProperty
     */
    public FlapDockProperty(){
    	// do nothing
    }
    
    /**
     * Constructs a FlapDockProperty
     * @param index the location of the {@link Dockable}
     * @see #setIndex(int)
     */
    public FlapDockProperty( int index ){
        setIndex( index );
    }
    
    /**
     * Sets the location of the {@link Dockable} on its {@link FlapDockStation}.
     * @param index the location
     */
    public void setIndex( int index ){
        if( index < 0 )
            throw new IllegalArgumentException( "Index must be >= 0: " + index );
        
        this.index = index;
    }
    
    /**
     * Gets the location of the {@link Dockable} on its {@link FlapDockStation}.
     * @return the location
     * @see #setIndex(int)
     */
    public int getIndex() {
        return index;
    }
    
    public String getFactoryID() {
        return FlapDockPropertyFactory.ID;
    }

    public void store( DataOutputStream out ) throws IOException {
        out.writeInt( index );
    }
    
    public void store( XElement element ) {
        element.setInt( index );
    }

    public void load( DataInputStream in ) throws IOException {
        setIndex( in.readInt() );
    }
    
    public void load( XElement element ) {
        index = element.getInt();
    }
}
