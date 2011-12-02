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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * A {@link MultiUIBridge} is a collection of {@link UIBridge}s. Each bridge
 * in the collection is responsible to handle only one resource.
 * @author Benjamin Sigg
 * @param <V> the kind of resources that get handled by this bridge
 * @param <U> the kind of values that get the resources of kind <code>V</code>
 */
public class MultiUIBridge<V, U extends UIValue<V>> implements UIBridge<V, U> {
    /** the map of registered bridges */
    private Map<String, UIBridge<V, U>> bridges =
        new HashMap<String, UIBridge<V, U>>();
    
    /**
     * a map of {@link UIValue}s which are provided with resources by 
     * this bridge. 
     */
    private Map<String, List<U>> values = new HashMap<String, List<U>>();
    
    /** the manager that delivers default resources when necessary */
    private UIProperties<V, U, ?> manager;
    
    /**
     * Creates a new {@link MultiUIBridge}.
     * @param manager the manager from whom this provider will get default
     * colors when necessary
     */
    public MultiUIBridge( UIProperties<V, U, ?> manager ){
        if( manager == null )
            throw new IllegalArgumentException( "Manager must not be null" );
        this.manager = manager;
    }
    
    /**
     * Specifies a bridge that handles all calls regarding <code>id</code>.
     * @param id the key of the resource <code>bridge</code> should handle
     * @param bridge the new bridge or <code>null</code>
     */
    public void put( String id, UIBridge<V, U> bridge ){
        UIBridge<V,U> old;
        if( bridge == null )
            old = bridges.remove( id );
        else
            old = bridges.put( id, bridge );
        
        if( old != null || bridge != null ){
            List<U> list = values.get( id );
            if( list != null ){
                if( old != null ){
                    for( U color : list )
                        old.remove( id, color );
                }
                
                V original = manager.get( id );
                if( bridge != null ){
                    for( U color : list ){
                        bridge.add( id, color );
                        bridge.set( id, original, color );
                    }
                }
                else{
                    for( U color : list )
                        color.set( original );
                }
            }
        }
    }
    
    /**
     * Searches the bridge that handles resources with the key <code>id</code>.
     * @param id the key of the resources
     * @return the responsible bridge or <code>null</code>
     */
    public UIBridge<V,U> getBridge( String id ){
        return bridges.get( id );
    }
    
    public void add( String id, U uiValue ) {
        UIBridge<V, U> provider = bridges.get( id );
        if( provider != null )
            provider.add( id, uiValue );
        
        List<U> list = values.get( id );
        if( list == null ){
            list = new LinkedList<U>();
            values.put( id, list );
        }
        list.add( uiValue );
    }

    public void remove( String id, U uiValue ) {
        UIBridge<V, U> provider = bridges.get( id );
        if( provider != null )
            provider.remove( id, uiValue );
        
        List<U> list = values.get( id );
        if( list != null ){
            list.remove( uiValue );
            if( list.isEmpty() ){
                values.remove( id );
            }
        }
    }

    public void set( String id, V value, U uiValue ) {
        UIBridge<V, U> bridge = bridges.get( id );
        if( bridge != null )
            bridge.set( id, value, uiValue );
        else
            uiValue.set( value );
    }
}
