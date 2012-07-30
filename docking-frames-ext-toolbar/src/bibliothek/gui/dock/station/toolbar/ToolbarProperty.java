/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2012 Herve Guillaume, Benjamin Sigg
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
 * Herve Guillaume
 * rvguillaume@hotmail.com
 * FR - France
 *
 * Benjamin Sigg
 * benjamin_sigg@gmx.ch
 * CH - Switzerland
 */

package bibliothek.gui.dock.station.toolbar;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import bibliothek.gui.DockStation;
import bibliothek.gui.dock.ToolbarDockStation;
import bibliothek.gui.dock.ToolbarGroupDockStation;
import bibliothek.gui.dock.layout.AbstractDockableProperty;
import bibliothek.gui.dock.layout.DockableProperty;
import bibliothek.util.Path;
import bibliothek.util.Version;
import bibliothek.util.xml.XElement;

/**
 * Described the location of a child of a {@link ToolbarGroupDockStation} or a
 * {@link ToolbarDockStation}.
 * 
 * @author Benjamin Sigg
 */
public class ToolbarProperty extends AbstractDockableProperty{
	/** the index of the child */
	private int index;

	/** the placeholder for the child, can be <code>null</code> */
	private Path placeholder;

	/**
	 * Creates a new, empty {@link ToolbarProperty}.
	 */
	public ToolbarProperty(){
		// nothing
	}

	/**
	 * Creates a new property.
	 * 
	 * @param index
	 *            the index of a child of a {@link DockStation}
	 * @param placeholder
	 *            the name of the child, can be <code>null</code>
	 */
	public ToolbarProperty( int index, Path placeholder ){
		this.index = index;
		this.placeholder = placeholder;
	}
	
	@Override
	public String toString(){
		return getClass().getSimpleName() + "[index=" + index + ", placeholder=" + placeholder + ", successor=" + getSuccessor() + "]";
	}

	@Override
	public DockableProperty copy(){
		final ToolbarProperty copy = new ToolbarProperty(index, placeholder);
		final DockableProperty successor = getSuccessor();
		if (successor != null){
			copy.setSuccessor(successor.copy());
		}
		return copy;
	}

	/**
	 * Gets the index of the child on its parent.
	 * 
	 * @return the index
	 */
	public int getIndex(){
		return index;
	}

	/**
	 * Gets the name of the child.
	 * 
	 * @return the name of the child, can be <code>null</code>
	 */
	public Path getPlaceholder(){
		return placeholder;
	}

	@Override
	public String getFactoryID(){
		return ToolbarPropertyFactory.ID;
	}

	@Override
	public void store( DataOutputStream out ) throws IOException{
		Version.write(out, Version.VERSION_1_1_1);
		out.writeInt(index);
		if (placeholder == null){
			out.writeBoolean(false);
		} else{
			out.writeBoolean(true);
			out.writeUTF(placeholder.toString());
		}
	}

	@Override
	public void load( DataInputStream in ) throws IOException{
		final Version version = Version.read(in);
		if (!version.equals(Version.VERSION_1_1_1)){
			throw new IOException("data from an unknown version: " + version);
		}
		index = in.readInt();
		if (in.readBoolean()){
			placeholder = new Path(in.readUTF());
		} else{
			placeholder = null;
		}
	}

	@Override
	public void store( XElement element ){
		element.addElement("index").setInt(index);
		if (placeholder != null){
			element.addElement("placeholder").setString(placeholder.toString());
		}
	}

	@Override
	public void load( XElement element ){
		final XElement xindex = element.getElement("index");
		final XElement xplaceholder = element.getElement("placeholder");

		if (xindex != null){
			index = xindex.getInt();
		}
		if (xplaceholder != null){
			placeholder = new Path(xplaceholder.getString());
		} else{
			placeholder = null;
		}
	}
}
