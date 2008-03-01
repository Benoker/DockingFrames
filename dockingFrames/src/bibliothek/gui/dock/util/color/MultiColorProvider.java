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
package bibliothek.gui.dock.util.color;

import java.awt.Color;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * A {@link ColorProvider} which uses other providers to handle some
 * colors.
 * @author Benjamin Sigg
 * @param <D> the kind of {@link DockColor}s this provider handles. 
 */
public class MultiColorProvider<D extends DockColor> implements ColorProvider<D>{
    /** the map of registered providers */
    private Map<String, ColorProvider<? super D>> providers =
        new HashMap<String, ColorProvider<? super D>>();
    
    /**
     * a map of {@link DockColor}s which are provided with {@link Color}s from
     * this {@link ColorProvider} 
     */
    private Map<String, List<D>> colors = new HashMap<String, List<D>>();
    
    /** the manager that delivers default colors when necessary */
    private ColorManager manager;
    
    /**
     * Creates a new {@link ColorProvider}.
     * @param manager the manager from whom this provider will get default
     * colors when necessary
     */
    public MultiColorProvider( ColorManager manager ){
        if( manager == null )
            throw new IllegalArgumentException( "Manager must not be null" );
        this.manager = manager;
    }
    
    /**
     * Specifies a provider that handles all calls regarding <code>id</code>.
     * @param id the key of the color <code>provider</code> should handle
     * @param provider the new provider or <code>null</code>
     */
    public void put( String id, ColorProvider<? super D> provider ){
        ColorProvider<? super D> old;
        if( provider == null )
            old = providers.remove( id );
        else
            old = providers.put( id, provider );
        
        if( old != null || provider != null ){
            List<D> list = colors.get( id );
            if( list != null ){
                if( old != null ){
                    for( D color : list )
                        old.remove( id, color );
                }
                
                Color original = manager.get( id );
                if( provider != null ){
                    for( D color : list ){
                        provider.add( id, color );
                        provider.set( id, original, color );
                    }
                }
                else{
                    for( D color : list )
                        color.set( original );
                }
            }
        }
    }
    
    /**
     * Searches the provider that handles colors with the key <code>id</code>.
     * @param id the key of the colors
     * @return the responsible provider or <code>null</code>
     */
    public ColorProvider<? super D> getProvider( String id ){
        return providers.get( id );
    }
    
    public void add( String id, D color ) {
        ColorProvider<? super D> provider = providers.get( id );
        if( provider != null )
            provider.add( id, color );
        
        List<D> list = colors.get( id );
        if( list == null ){
            list = new LinkedList<D>();
            colors.put( id, list );
        }
        list.add( color );
    }

    public void remove( String id, D color ) {
        ColorProvider<? super D> provider = providers.get( id );
        if( provider != null )
            provider.remove( id, color );
        
        List<D> list = colors.get( id );
        if( list != null ){
            list.remove( color );
            if( list.isEmpty() ){
                colors.remove( id );
            }
        }
    }

    public void set( String id, Color color, D observer ) {
        ColorProvider<? super D> provider = providers.get( id );
        if( provider != null )
            provider.set( id, color, observer );
        else
            observer.set( color );
    }
}
