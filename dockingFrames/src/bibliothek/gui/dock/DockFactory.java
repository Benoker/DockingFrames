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


package bibliothek.gui.dock;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;

/**
 * A DockFactory has the ability to store and load instances of one
 * subtype of {@link DockElement}.
 * @author Benjamin Sigg
 * @param <D> the type of element which can be written and read by this factory
 */
public interface DockFactory<D extends DockElement> {

    /**
     * Gets the unique name of this factory.
     * @return the id
     */
    public String getID();
    
    /**
     * Saves the properties of a DockElement. If the element is a 
     * {@link DockStation}, then the factory has to store the location
     * of the children. The factory can use the unique ids of the children
     * which are stored in the map <code>children</code>. The factory
     * don't have to store any information about the children itself.<br>
     * If <code>element</code> is a Dockable, no information about the
     * parent has to be stored.
     * @param element the element to save
     * @param children a list of unique names for each child of <code>element</code>,
     * may be <code>null</code> if <code>element</code> is not a DockStation.
     * @param out a stream to write information
     * @throws IOException if the element can't be saved
     */
    public void write( 
            D element, 
            Map<Dockable, Integer> children,
            DataOutputStream out ) throws IOException;

    /**
     * Reads a {@link DockElement} which was earlier stored by a DockFactoy
     * of the same type.
     * @param children the known children of the element that is read. It's
     * possible that not all children that were stored last time could be
     * read again. In this case the map will contain no or a <code>null</code>
     * entry.
     * @param ignoreChildren <code>true</code> if the layout of the current
     * children should not be changed. The map <code>children</code> is empty
     * if <code>ignoreChildren</code> is <code>true</code>.
     * @param in the stream to read from. The number of bytes read don't have
     * to be the same number as the bytes that were written.
     * @return the element that was read, <code>null</code> is a valid
     * result and indicates that an element is no longer available.
     * @throws IOException if the element can't be read from the stream
     */
    public D read(
            Map<Integer, Dockable> children,
            boolean ignoreChildren,
            DataInputStream in ) throws IOException;
    
    /**
     * Reads a {@link DockElement} which was earlier stored by a DockFactory
     * of the same type. The contents have to be written into an already
     * existing element.
     * @param children the known children of the element that is read. It's
     * possible that not all children that were stored last time could be
     * read again. In this case the map will contain no or a <code>null</code>
     * entry.
     * @param ignoreChildren <code>true</code> if the layout of the current
     * children should not be changed. The map <code>children</code> is empty
     * if <code>ignoreChildren</code> is <code>true</code>.
     * @param preloaded an element which was created elsewhere and now
     * has to be set up correctly by this factory
     * @param in the stream to read from. The number of bytes read don't have
     * to be the same number as the bytes that were written.
     * @throws IOException if the element can't be read from the stream
     */
    public void read(
            Map<Integer, Dockable> children,
            boolean ignoreChildren,
            D preloaded,
            DataInputStream in ) throws IOException;
}
