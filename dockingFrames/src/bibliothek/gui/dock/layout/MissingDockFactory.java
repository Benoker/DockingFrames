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

import bibliothek.util.xml.XElement;
import java.io.IOException;

/**
 * A {@link MissingDockFactory} is used by a {@link DockSituation} to read
 * the contents of a file if the factory that was supposed to read that 
 * content is missing.
 * @author Benjamin Sigg
 */
public interface MissingDockFactory {
    /**
     * Reads up to <code>length</code> bytes from <code>in</code> and returns
     * some object that represents the content of <code>in</code>. Note that if
     * later a factory for <code>id</code> is registered, then that object will
     * be cast to the factories preferred way to look at it. A result of
     * <code>null</code> indicates that this factory does not know how to
     * handle the situation.
     * @param id the id of the factory which was supposed to read the stream
     * @param in the stream to read
     * @param length the maximal number of bytes this factory can read before
     * the end of the stream is reached
     * @return the content of <code>in</code> or <code>null</code>
     * @throws IOException forwarded from <code>in</code>
     */
    public Object read( String id, DataInputStream in, int length ) throws IOException;
    
    /**
     * Reads <code>element</code> and returns an object that represents the
     * content of <code>element</code>. Note that if later a factory for 
     * <code>id</code> is registered, then that object will be cast to the
     * factories preferred way to look at it.
     * @param id the id of the factory which was supposed to read <code>element</code>
     * @param element the contents
     * @return a representation of <code>element</code> or <code>null</code>
     */
    public Object readXML( String id, XElement element );
}
