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

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DockElement;
import bibliothek.util.Path;
import bibliothek.util.xml.XElement;

/**
 * A {@link DockLayoutInfo} is a wrapper around a {@link DockLayout}. The <code>DockLayout</code>
 * can either be stored as real object, as byte-stream or as part of an xml tree. This information 
 * is normally used by a {@link DockConverter} to create or store {@link DockElement}s. 
 * @author Benjamin Sigg
 */
public class DockLayoutInfo {
	/**
	 * The kind of information a {@link DockLayoutInfo} contains.
	 * @author Benjamin Sigg
	 */
	public static enum Data{
		/** no information at all is available */
		NULL,
		/** information is present as {@link XElement} */
		XML,
		/** information is present as byte stream */
		BYTE,
		/** information is present as {@link DockLayout} */
		DOCK_LAYOUT
	}
	
	/** the kind of information stored in this info */
	private Data kind = Data.NULL;
	
	/** the information of this info */
	private Object data;
	
	/** the location which the dockable has on its parent station */
	private DockableProperty location;
	
	/** the name of this element */
	private Path placeholder;
	
	/**
	 * Creates a new info.
	 */
	public DockLayoutInfo(){
		setData( null );
	}
	
	/**
	 * Creates a new info.
	 * @param data the data of this info
	 */
	public DockLayoutInfo( byte[] data ){
		setData( data );
	}
	
	/**
	 * Creates a new info.
	 * @param data the data of this info
	 */
	public DockLayoutInfo( XElement data ){
		setData( data );
	}
	
	/**
	 * Creates a new info
	 * @param data the data of this info
	 */
	public DockLayoutInfo( DockLayout<?> data ){
		setData( data );
	}
	
	/**
	 * Tells what kind of information can be found in this info.
	 * @return the kind of information, not <code>null</code>
	 */
	public Data getKind() {
		return kind;
	}
	
	/**
	 * Sets the location of the {@link Dockable} ,represented by this info,
	 * on its parent station.
	 * @param location the location, can be <code>null</code>
	 */
	public void setLocation(DockableProperty location) {
		this.location = location;
	}
	
	/**
	 * Gets the location of of the {@link Dockable} on its parent station.
	 * @return the location, may be <code>null</code>
	 */
	public DockableProperty getLocation() {
		return location;
	}

    /**
     * Sets a placeholder which represents this element.
     * @param placeholder the placeholder, can be <code>null</code>
     */
    public void setPlaceholder( Path placeholder ){
		this.placeholder = placeholder;
	}
    
    /**
     * Gets the representation of this element as placeholder.
     * @return the placeholder, can be <code>null</code>
     */
    public Path getPlaceholder(){
		return placeholder;
	}
	
	/**
	 * Sets the information of this info. The object <code>data</code>
	 * must either be <code>null</code>, or an instance of {@link XElement},
	 * <code>byte[]</code> or {@link DockLayout}
	 * @param data the new data
	 * @throws IllegalArgumentException if <code>data</code> has not one
	 * of the specified types
	 */
	public void setData( Object data ){
		if( data == null ){
			this.data = null;
			kind = Data.NULL;
		}
		else{
			if( data instanceof XElement ){
				this.data = data;
				kind = Data.XML;
			}
			else if( data instanceof byte[] ){
				this.data = data;
				kind = Data.BYTE;
			}
			else if( data instanceof DockLayout ){
				this.data = data;
				kind = Data.DOCK_LAYOUT;
			}
			else{
				throw new IllegalArgumentException( "data is of unknown format" );
			}
		}
	}
	
	/**
	 * Gets the data of this info formated as xml.
	 * @return the xml data or <code>null</code> if it cannot be converted
	 */
	public XElement getDataXML(){
		if( kind == Data.XML )
			return (XElement)data;
		return null;
	}
	
	/**
	 * Gets the data of this info as byte array.
	 * @return the byte data or <code>null</code> if it cannot be converted
	 */
	public byte[] getDataByte(){
		if( kind == Data.BYTE )
			return (byte[])data;
		return null;
	}
	
	/**
	 * Gets the data of this info as {@link DockLayout}.
	 * @return the {@link DockLayout} data or <code>null</code>
	 */
	public DockLayout<?> getDataLayout(){
		if( kind == Data.DOCK_LAYOUT )
			return (DockLayout<?>)data;
		return null;
	}
}
