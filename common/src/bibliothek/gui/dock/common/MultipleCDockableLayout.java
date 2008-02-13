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
package bibliothek.gui.dock.common;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import bibliothek.util.xml.XElement;

/**
 * An intermediate representation of the layout of a {@link MultipleCDockable}.
 * This layout should not have any references to the {@link MultipleCDockable}
 * it describes.
 * @author Benjamin Sigg
 */
public interface MultipleCDockableLayout {
    /**
     * Writes the content of this layout into <code>out</code>.
     * @param out the stream to write into
     * @throws IOException if an I/O-error occurs
     */
    public void writeStream( DataOutputStream out ) throws IOException;
    
    /**
     * Reads the content of this layout from <code>out</code>. All
     * properties should be set to their default value or to the value read
     * from the stream.
     * @param in the stream to read
     * @throws IOException if an I/O-error occurs
     */
    public void readStream( DataInputStream in ) throws IOException;
    
    /**
     * Writes the content of this layout into <code>element</code>.
     * @param element the xml element into which this method can write,
     * the attributes of <code>element</code> should not be changed
     */
    public void writeXML( XElement element );
    
    /**
     * Reads the content of this layout from <code>element</code>. All
     * properties should be set to their default value or to the value
     * read from <code>element</code>. This method can assume that the xml-element
     * was written by another layout of the same type, and that no attributes or
     * elements have been deleted or altered by DockingFrames itself.
     * @param element the element to read
     */
    public void readXML( XElement element );
}
