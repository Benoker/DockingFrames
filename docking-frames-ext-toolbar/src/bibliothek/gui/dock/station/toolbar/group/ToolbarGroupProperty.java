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

package bibliothek.gui.dock.station.toolbar.group;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.ToolbarGroupDockStation;
import bibliothek.gui.dock.layout.AbstractDockableProperty;
import bibliothek.gui.dock.layout.DockableProperty;
import bibliothek.util.Path;
import bibliothek.util.Version;
import bibliothek.util.xml.XElement;

/**
 * Describes the position of one child of a {@link ToolbarGroupDockStation}.
 * 
 * @author Benjamin Sigg
 */
public class ToolbarGroupProperty extends AbstractDockableProperty{
	/** the index of the column */
	private int column;
	/** the index of the line within the column */
	private int line;
	/** the name of the {@link Dockable} */
	private Path placeholder;

	/**
	 * Creates a new, empty property
	 */
	public ToolbarGroupProperty(){
		// nothing
	}

	/**
	 * Creates a new property.
	 * 
	 * @param column
	 *            the index of the column
	 * @param line
	 *            the index of the line within the column
	 * @param placeholder
	 *            the name of the dockable, can be <code>null</code>
	 */
	public ToolbarGroupProperty( int column, int line, Path placeholder ){
		this.column = column;
		this.line = line;
		this.placeholder = placeholder;
	}

	/**
	 * Gets the column in which the dockable was.
	 * 
	 * @return the column
	 */
	public int getColumn(){
		return column;
	}

	/**
	 * Gets the line in the column in which the dockable was.
	 * 
	 * @return the line in the column
	 */
	public int getLine(){
		return line;
	}

	/**
	 * Gets the name of the dockable.
	 * 
	 * @return the name, can be <code>null</code>
	 */
	public Path getPlaceholder(){
		return placeholder;
	}
	
	@Override
	public String toString(){
		return getClass().getSimpleName() + "[column=" + getColumn() + ", line=" + getLine() + ", placeholder=" + getPlaceholder() + ", successor=" + getSuccessor() + "]";
	}

	@Override
	public DockableProperty copy(){
		final ToolbarGroupProperty result = new ToolbarGroupProperty(column,
				line, placeholder);
		copy(result);
		return result;
	}

	@Override
	public String getFactoryID(){
		return ToolbarGroupPropertyFactory.ID;
	}

	@Override
	public void store( DataOutputStream out ) throws IOException{
		Version.write(out, Version.VERSION_1_1_1a);
		out.writeInt(column);
		out.writeInt(line);
		if (placeholder == null){
			out.writeBoolean(false);
		} else{
			out.writeBoolean(true);
			out.writeUTF(placeholder.toString());
		}
	}

	@Override
	public void store( XElement element ){
		element.addElement("column").setInt(column);
		element.addElement("line").setInt(line);
		if (placeholder != null){
			element.addElement("placeholder").setString(placeholder.toString());
		}
	}

	@Override
	public void load( DataInputStream in ) throws IOException{
		final Version version = Version.read(in);
		if (!version.equals(Version.VERSION_1_1_1a)){
			throw new IOException("data from an unknown version: " + version);
		}
		column = in.readInt();
		line = in.readInt();
		if (in.readBoolean()){
			placeholder = new Path(in.readUTF());
		} else{
			placeholder = null;
		}
	}

	@Override
	public void load( XElement element ){
		final XElement xcolumn = element.getElement("column");
		final XElement xline = element.getElement("xline");
		final XElement xplaceholder = element.getElement("placeholder");

		if (xcolumn != null){
			column = xcolumn.getInt();
		}
		if (xline != null){
			line = xline.getInt();
		}
		if (xplaceholder != null){
			placeholder = new Path(xplaceholder.getString());
		} else{
			placeholder = null;
		}
	}
}
