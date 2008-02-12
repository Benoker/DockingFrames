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

package bibliothek.gui.dock;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.layout.DockLayout;
import bibliothek.util.xml.XElement;

/**
 * A {@link DockFactory} can convert the contents of a {@link DockElement} in
 * a persistent form.<br>
 * Some kind of {@link DockElement} will be converted into a {@link DockLayout}, 
 * this layout can then be written into a stream.
 * @author Benjamin Sigg
 * @param <D> the type of element which can be written and read by this factory
 * @param <L> the type of object that stores the contents of a <code>D</code>
 */
public interface DockFactory<D extends DockElement, L extends DockLayout> {

    /**
     * Gets the unique name of this factory.
     * @return the id
     */
    public String getID();
    
    /**
     * Gets the layout of <code>element</code>.
     * @param element the element for which a new layout should be created
     * @param children a map containing unique identifiers for the children
     * of the element. Children which are not in this map should not be
     * stored in the layout.
     * @return the new layout
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
     * Creates a new {@link DockElement} and changes the layout of the new 
     * element such that is matches <code>layout</code>.
     * @param layout the new layout
     * @param children some children, note that the map may not contain all elements
     * which were present when the layout was created. 
     * @return a new element or <code>null</code> if layout can't be used
     */
    public D layout( L layout, Map<Integer, Dockable> children );
    
    /**
     * Creates a new {@link DockElement} and changes the layout of the new 
     * element such that is matches <code>layout</code>. This method should
     * not add any children to the element.
     * @param layout the new layout
     * @return a new element or <code>null</code> if layout can't be used
     */
    public D layout( L layout );
    
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
     * @return the new layout
     * @throws IOException if an I/O-error occurs
     */
    public L read( DataInputStream in ) throws IOException;
    
    /**
     * Reads a layout from an xml-element.
     * @param element the element to read, should not be changed by this 
     * method.
     * @return the new layout
     */
    public L read( XElement element );
}
