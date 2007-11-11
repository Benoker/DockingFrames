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
package bibliothek.gui.dock.facile;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * A factory that can create and save {@link FDockable}s.
 * @author Benjamin Sigg
 *
 */
public interface FDockableFactory {
	/**
	 * Reads and creates the contents of a new {@link FDockable}.
	 * @param in the stream to read from
	 * @return the new dockable or <code>null</code>
	 * @throws IOException if the stream can't be read
	 */
	public FMultipleDockable read( DataInputStream in ) throws IOException;
	
	/**
	 * Writes the contents of <code>dockable</code> into <code>out</code>.
	 * @param dockable the element to store, the factory can expect that
	 * <code>dockable.getFactory() == this</code>.
	 * @param out the stream to write into
	 * @throws IOException if the stream is not writable
	 */
	public void write( FMultipleDockable dockable, DataOutputStream out ) throws IOException;
}
