/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2009 Benjamin Sigg
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
package bibliothek.gui.dock.support.mode;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import bibliothek.util.Path;
import bibliothek.util.xml.XElement;

/**
 * A set of properties that belong to some {@link Mode} but are stored independent 
 * from that mode. Each of the read/write methods gets access to the {@link ModeSettingsConverter} that is
 * used by the {@link ModeSettings} to store data in memory. The methods are not required to use the converter,
 * but the implementation may be easier when using the converters read/write methods as well.
 * @author Benjamin Sigg
 * @param <A> format of data used by the {@link ModeManager} this {@link ModeSetting}
 * is associated with
 */
public interface ModeSetting<A> {
	/**
	 * Gets the unique identifier of the {@link Mode} this setting is
	 * associated with.
	 * @return the identifier
	 */
	public Path getModeId();
	
	/**
	 * Writes the contents of this setting into <code>out</code>.
	 * @param out the stream to write into
	 * @param converter converts data to and from persistent storage
	 * @throws IOException in case of an error
	 */
	public <B> void write( DataOutputStream out, ModeSettingsConverter<A, B> converter ) throws IOException;
	
	/**
	 * Reads the contents of this setting from <code>in</code>.
	 * @param in the stream to read from
	 * @param converter converts data to and from persistent storage
	 * @throws IOException in case of an error
	 */
	public <B> void read( DataInputStream in, ModeSettingsConverter<A, B> converter ) throws IOException;
	
	/**
	 * Writes the contents of this setting into <code>element</code>. This
	 * method should add children to <code>element</code>, but not change
	 * the attributes of <code>element</code>.
	 * @param element the item to write into
	 * @param converter converts data to and from persistent storage
	 */
	public <B> void write( XElement element, ModeSettingsConverter<A, B> converter );
	
	/**
	 * Reads the contents of this setting from <code>element</code>.
	 * @param element the item to read from
	 * @param converter converts data to and from persistent storage
	 */
	public <B> void read( XElement element, ModeSettingsConverter<A, B> converter );
}
