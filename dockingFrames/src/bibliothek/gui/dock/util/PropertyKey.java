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

package bibliothek.gui.dock.util;

import javax.swing.Icon;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;

/**
 * The key for an entry of {@link DockProperties}.
 * @author Benjamin Sigg
 *
 * @param <A> the type of entry
 */
public class PropertyKey<A> {
	/**
	 * The Icon used for a {@link Dockable} if it has no icon.
	 */
	public static final PropertyKey<Icon> DOCKABLE_ICON = new PropertyKey<Icon>( "javax.swing.Icon_dockable_icon" );
	/**
	 * The Icon used for a {@link DockStation} if it has no icon.
	 */
	public static final PropertyKey<Icon> DOCK_STATION_ICON = new PropertyKey<Icon>( "javax.swing.Icon_dock_station_icon" );
	
	/**
	 * The title of a {@link Dockable} if it has no title.
	 */
	public static final PropertyKey<String> DOCKABLE_TITLE = new PropertyKey<String>( "java.lang.String_dockable_title" );
	/**
	 * The title of a {@link DockStation} if it has no title.
	 */
	public static final PropertyKey<String> DOCK_STATION_TITLE = new PropertyKey<String>( "java.lang.String_dock_station_title" );
	
	/** a unique identifier */
	private String id;
	
	/** default value */
	private A value;

	/**
	 * Creates a new key.
	 * @param id a unique identifier, should contain the name of the
	 * type of property, represented by this key.
	 */
	public PropertyKey( String id ){
	    this( id, null );
	}

    /**
     * Creates a new key.
     * @param id a unique identifier, should contain the name of the
     * type of property, represented by this key.
     * @param value the value that will be used when no value is set
     * in the properties
     */
	public PropertyKey( String id, A value ){
		if( id == null )
			throw new IllegalArgumentException( "id must not be null" );
		
		this.value = value;
		this.id = id;
	}
	
	/**
	 * Gets a default-value that should be used when no value is set
	 * in the {@link DockProperties}.
	 * @return the default-value
	 */
	public final A getDefault(){
	    return value;
	}
	
	@Override
	public final int hashCode(){
		return id.hashCode();
	}

	@Override
	public final boolean equals( Object obj ){
		return this == obj;
	}
}
