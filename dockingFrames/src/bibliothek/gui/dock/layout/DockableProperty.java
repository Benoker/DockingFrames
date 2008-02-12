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

package bibliothek.gui.dock.layout;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.util.xml.XElement;

/**
 * Describes the location of a {@link Dockable} on a {@link DockStation}. 
 * The properties can be nested, that describes the fact that DockStations 
 * can be Dockables itself.<br>
 * A DockableProperty should not have any reference to its Dockable or its
 * DockStation. 
 * @author Benjamin Sigg
 */
public interface DockableProperty {
    /**
     * Gets the property which should be used for the child of this
     * dockable DockStation.
     * @return the property for the child, or <code>null</code>
     * @see #setSuccessor(DockableProperty)
     */
    public DockableProperty getSuccessor();
    
    /**
     * Sets the property which will be used if this property was used and
     * there is not yet a leaf in the tree of DockStations and Dockables reached.<br>
     * For example: there is a DockStation <code>root</code>, a 
     * dockable DockStation <code>node</code> which is a child of <code>root</code>
     * and a Dockable <code>leaf</code> which is a child of <code>node</code>.<br>
     * A DockableProperty called <code>one</code> would describe the relationship
     * between <code>root</code> and <code>node</code>. Another DockableProperty
     * called <code>two</code> would describe the relationship between
     * <code>node</code> and <code>leaf</code>. In this case, the successor
     * of <code>one</code> would be <code>two</code>.
     * @param properties the location of a child
     */
    public void setSuccessor( DockableProperty properties );
    
    /**
     * Gest the unique name of the {@link DockablePropertyFactory} which
     * can create this type of DockableProperty.
     * @return the id
     * @see DockablePropertyFactory
     */
    public String getFactoryID();
    
    /**
     * Stores the contents of this DockableProperty in a stream. The
     * {@link #getSuccessor() successor} (if there is one) must
     * not be saved.
     * @param out the stream to write in
     * @throws IOException if anything unexpected happens
     */
    public void store( DataOutputStream out ) throws IOException;
    
    /**
     * Stores the contents of this property as xml element.
     * @param element the element into which to write, the attributes of
     * this element should not be changed
     */
    public void store( XElement element );
    
    /**
     * Reads the contents of this DockableProperty from a stream. The
     * property can assume that a property with the same type has written
     * into the stream.
     * @param in the stream to read
     * @throws IOException if anything unexpected happens
     */
    public void load( DataInputStream in ) throws IOException;
    
    /**
     * Reads the contents of this {@link DockableProperty} from an
     * xml element.
     * @param element the element that was written earlier by this property
     */
    public void load( XElement element );
}
