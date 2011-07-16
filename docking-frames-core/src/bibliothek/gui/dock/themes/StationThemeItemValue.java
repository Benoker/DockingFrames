/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2010 Benjamin Sigg
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
package bibliothek.gui.dock.themes;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.dock.util.UIValue;
import bibliothek.gui.dock.util.TypedUIProperties.Type;
import bibliothek.util.Path;

/**
 * A default implementation of an {@link UIValue} that reads a item from
 * a {@link ThemeManager}.
 * @author Benjamin Sigg
 * @param <V> the kind of value this {@link UIValue} handles
 */
public class StationThemeItemValue<V> implements UIValue<V>{
    /** overriding delegate */
	private V delegate;
	
	/** currently assigned value */
	private V value;
	
	/** the station that uses this factory */
	private DockStation station;
	
	/** the unique identifier of this {@link UIValue} */
	private String id;
	
	/** what kind of {@link UIValue} this is */
	private Path kind;
	
	/** what type of value this handles */
	private Type<V> type;
	
	/** the current controller */
	private DockController controller;
    
	/**
	 * Creates a new object.
	 * @param id the identifier used for retrieving a resource of {@link ThemeManager}
	 * @param kind what kind of {@link UIValue} this is
	 * @param type what kind of value this {@link UIValue} handles
	 * @param station the owner of this object, not <code>null</code>
	 */
	public StationThemeItemValue( String id, Path kind, Type<V> type, DockStation station ){
		if( id == null ){
			throw new IllegalArgumentException( "id must not be null" );
		}
		if( station == null ){
			throw new IllegalArgumentException( "station must not be null" );
		}
		if( type == null ){
			throw new IllegalArgumentException( "type must not be null" );
		}
		if( kind == null ){
			throw new IllegalArgumentException( "kind must not be null" );
		}
		
		this.id = id;
		this.kind = kind;
		this.type = type;
		this.station = station;
	}
	
	/**
	 * Gets the station that owns this {@link UIValue}.
	 * @return the owner
	 */
	public DockStation getStation(){
		return station;
	}
	
    /**
     * Gets the delegate of this wrapper.
     * @return the delegate, may be <code>null</code>
     */
    public V getDelegate() {
        return delegate;
    }
    
    /**
     * Allows this {@link UIValue} to register itself on <code>controller</code> to
     * read the current value.
     * @param controller the controller to observer, can be <code>null</code>
     */
    public void setController( DockController controller ){
    	if( this.controller != null ){
    		this.controller.getThemeManager().remove( this );
    	}
    	
		this.controller = controller;
		
		if( this.controller != null ){
			this.controller.getThemeManager().add( id, kind, type, this );
		}
	}
    
    /**
     * Sets the delegate of this wrapper.
     * @param delegate the delegate or <code>null</code>
     */
    public void setDelegate( V delegate ) {
        if( delegate == this )
            throw new IllegalArgumentException( "Infinite recursion is not allowed" );
        
        this.delegate = delegate;
    }
    
    public void set( V value ){
    	this.value = value;
    }
    
    /**
     * Gets the resource that is currently used.
     * @return the current factory
     */
    public V get(){
    	if( delegate != null ){
    		return delegate;
    	}
    	if( value != null ){
    		return value;
    	}
    	return null;
    }
}
