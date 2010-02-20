/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2008 Benjamin Sigg
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
import java.util.Map;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DockElement;
import bibliothek.util.xml.XElement;

/**
 * A {@link DockConverter} can store or load content which is related
 * to a certain kind of {@link DockElement}.<br>
 * The content of an element is first converted in a special intermediate form
 * represented by some object of type <code>L</code>. This intermediate object
 * can then be written as byte-stream or in xml.
 * @author Benjamin Sigg
 * @param <D> the kind of {@link DockElement} this converter handles
 * @param <L> the kind of data this converter uses as intermediate format
 */
public interface DockConverter <D extends DockElement, L>{
    /**
     * Gets the unique name of this converter. Please note that unique identifiers
     * starting with "dock." should not be used by clients.
     * @return the id
     */
    public String getID();
    
    /**
     * Gets the layout of <code>element</code>. This method should create
     * a new instance of the layout object, that new object should not be
     * tied to <code>element</code> in any way. A layout can be living for
     * a long period of time and might be used on another <code>dockable</code>
     * object.
     * @param element the element for which a new layout should be created
     * @param children a map containing unique identifiers for the children
     * of the element. Children which are not in this map should not be
     * stored in the layout.
     * @return the newly created, independent layout object.
     */
    public L getLayout( D element, Map<Dockable, Integer> children );
    
    /**
     * Reads the contents of <code>layout</code> and changes the layout of
     * <code>element</code> accordingly. This method should remove all
     * children from <code>element</code> and add new children.
     * @param element the element whose content and children will be rearranged.
     * @param layout the new layout of <code>element</code>
     * @param children some children, note that the map may not contain all elements
     * which were present when the layout was created.
     */
    public void setLayout( D element, L layout, Map<Integer, Dockable> children );
    
    /**
     * Reads the contents of <code>layout</code> and changes the layout of
     * <code>element</code> accordingly. This method should not add or remove
     * children to or from <code>element</code>.
     * @param element the element whose properties will be changed
     * @param layout the new set of properties
     */
    public void setLayout( D element, L layout );
    
    /**
     * Writes the contents of <code>layout</code> into <code>out</code>.
     * @param layout the layout to store
     * @param out the stream to write into
     * @throws IOException if an I/O-error occurs
     */
    public void write( L layout, DataOutputStream out ) throws IOException;
    
    /**
     * Writes the contents of <code>layout</code> into <code>element</code>.
     * @param layout the layout to store
     * @param element an xml-element into which this method should write, the
     * attributes of <code>element</code> should not be changed.
     */
    public void write( L layout, XElement element );
    
    /**
     * Reads a layout from a stream.
     * @param in the stream to read from
     * @return the new layout, can be <code>null</code> if the layout
     * should be discarded
     * @throws IOException if an I/O-error occurs
     */
    public L read( DataInputStream in ) throws IOException;
    
    /**
     * Reads a layout from an xml-element.
     * @param element the element to read, should not be changed by this 
     * method.
     * @return the new layout, can be <code>null</code> if the layout
     * should be discarded
     */
    public L read( XElement element );
}
