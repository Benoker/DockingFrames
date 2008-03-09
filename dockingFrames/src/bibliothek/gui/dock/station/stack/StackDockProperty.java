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

package bibliothek.gui.dock.station.stack;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.StackDockStation;
import bibliothek.gui.dock.layout.AbstractDockableProperty;
import bibliothek.util.Version;
import bibliothek.util.xml.XElement;

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
        Version.write( out, Version.VERSION_1_0_4 );
        out.writeInt( index );
    }
    
    public void store( XElement element ) {
        element.setInt( index );
    }

    public void load( DataInputStream in ) throws IOException {
        Version.read( in );
        index = in.readInt();
    }
    
    public void load( XElement element ) {
        index = element.getInt();
    }
}
