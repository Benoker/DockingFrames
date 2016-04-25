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
package bibliothek.gui.dock.support.util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import bibliothek.util.xml.XElement;

/**
 * A resource that is created by the application and is stored persistent
 * by the {@link ApplicationResourceManager}.
 * @author Benjamin Sigg
 */
public interface ApplicationResource {
    /**
     * Transforms this resource in a stream of bytes.
     * @param out the stream to write into
     * @throws IOException if the operation can't be completed
     */
    public void write( DataOutputStream out ) throws IOException;
    
    /**
     * Reads the content of this resource from a stream of bytes.
     * @param in the stream to read from
     * @throws IOException if the operation can't be finished
     */
    public void read( DataInputStream in ) throws IOException;
    
    /**
     * Writes the contents of this resource in xml format.
     * @param element the element to write into, the attributes of
     * <code>element</code> should not be changed.
     */
    public void writeXML( XElement element );
    
    /**
     * Reads the contents of this resource from a xml element.
     * @param element the element to read from.
     */
    public void readXML( XElement element );
}
