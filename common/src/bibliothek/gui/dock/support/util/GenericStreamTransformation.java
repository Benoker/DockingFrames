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

/**
 * A factory that can read or write elements from or to {@link DataInputStream}
 * or {@link DataOutputStream}s.
 * @author Benjamin Sigg
 *
 * @param <A> the type of elements written by this factory
 */
public interface GenericStreamTransformation<A> {
    /**
     * Writes an element.
     * @param out the stream to write into
     * @param element the element to write. Whether <code>null</code> can occur
     * depends on the environment this factory is used in.
     * @throws IOException if the element can't be written
     */
    public void write( DataOutputStream out, A element ) throws IOException;
    
    /**
     * Reads an element from a stream.
     * @param in the stream to read from
     * @return the new element, might be <code>null</code> in some environments.
     * @throws IOException if the element can't be read
     */
    public A read( DataInputStream in ) throws IOException;
}
