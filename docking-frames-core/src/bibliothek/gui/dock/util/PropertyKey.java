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
import bibliothek.gui.dock.util.property.PropertyFactory;

/**
 * The key for an entry in a map of {@link DockProperties properties}.
 * @author Benjamin Sigg
 *
 * @param <A> the type of entry
 */
public class PropertyKey<A> {
	/**
	 * The Icon used for a {@link Dockable} if it has no icon.<br>
	 * This key should only be used for writing the icon, reading the icon should be done through the {@link IconManager}
	 * using the key "dockable.default".
	 */
	public static final PropertyKey<Icon> DOCKABLE_ICON = new PropertyKey<Icon>( "javax.swing.Icon_dockable_icon" );
	/**
	 * The Icon used for a {@link DockStation} if it has no icon.<br>
	 * This key should only be used for writing the icon, reading the icon should be done through the {@link IconManager}
	 * using the key "dockStation.default".
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
	
	/**
	 * The tooltip used for a {@link Dockable} that has no tooltip set
	 */
	public static final PropertyKey<String> DOCKABLE_TOOLTIP = new PropertyKey<String>( "java.lang.String_dockable_tooltip" );
	/**
	 * The tooltip used for a {@link DockStation} that has no tooltip set
	 */
	public static final PropertyKey<String> DOCK_STATION_TOOLTIP = new PropertyKey<String>( "java.lang.String_dock_station_tooltip" );
    
	/** a unique identifier */
	private String id;
	
	/** default value */
	private PropertyFactory<A> value;

	/** if set, then the <code>null</code> value gets replaced by the default specified in this key */
	private boolean nullValueReplacedByDefault = false;
	
	/**
	 * Creates a new key.
	 * @param id a unique identifier, should contain the name of the
	 * type of property, represented by this key.
	 */
	public PropertyKey( String id ){
	    this( id, null, false );
	}

    /**
     * Creates a new key.
     * @param id a unique identifier, should contain the name of the
     * type of property, represented by this key.
     * @param value the value that will be used when no value is set
     * in the properties
     * @deprecated replaced by {@link #PropertyKey(String, PropertyFactory, boolean)}
     */
	@Deprecated
	public PropertyKey( String id, PropertyFactory<A> value ){
	    this( id, value, false );
	}

	/**
	 * Creates a new key.
     * @param id a unique identifier, should contain the name of the
     * type of property, represented by this key.
     * @param value the value that will be used when no value is set
     * in the properties
	 * @param nullValueReplacedByDefault if set, then the <code>null</code> value
	 * in {@link DockProperties} gets replaced by the default value of this key even if
	 * the <code>null</code> value was set explicitly.
	 */
	public PropertyKey( String id, PropertyFactory<A> value, boolean nullValueReplacedByDefault ){
		if( id == null )
			throw new IllegalArgumentException( "id must not be null" );
		
		this.value = value;
		this.id = id;
		this.nullValueReplacedByDefault = nullValueReplacedByDefault;
	}
	
	/**
	 * Gets a default-value that should be used when no value is set
	 * in the {@link DockProperties}.<br>
	 * Note: this method should not be called by clients.
	 * @param properties the properties for which the default value will be used
	 * @return the default-value
	 */
	public final A getDefault( DockProperties properties ){
	    if( value == null )
	    	return null;
	    
	    if( properties == null )
	    	return value.getDefault( this  );
	    else
	    	return value.getDefault( this, properties );
	}
	
	/**
	 * If set, then the <code>null</code> value should be replaced by the
	 * default value specified by this key.
	 * @return <code>true</code> if <code>null</code> means default
	 */
	public boolean isNullValueReplacedByDefault() {
        return nullValueReplacedByDefault;
    }
	
	@Override
	public final int hashCode(){
		return id.hashCode();
	}

	@Override
	public final boolean equals( Object obj ){
		return this == obj;
	}
	
	@Override
	public String toString(){
		return id;
	}
}
