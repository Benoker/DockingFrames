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
import java.util.*;

import bibliothek.gui.dock.util.Priority;
import bibliothek.gui.dock.util.PriorityValue;

/**
 * A {@link ColorManager} contains {@link Color}s, {@link ColorProvider}s and
 * {@link DockColor}s. Some <code>DockColor</code>s are associated with a 
 * <code>ColorProvider</code>. If a <code>Color</code> in this manager is
 * {@link #put(Priority, String, Color) set}, then each <code>DockColor</code> that listens
 * for that color gets informed about the change either through its 
 * provider or directly from the manager.
 * @author Benjamin Sigg
 */
public class ColorManager {
    /** the map of providers known to this manager */
    private Map<Class<?>, PriorityValue<ColorProvider<?>>> providers =
        new HashMap<Class<?>, PriorityValue<ColorProvider<?>>>();
    
    /** the map of the known {@link Color}s */
    private Map<String, PriorityValue<Color>> colors = new HashMap<String, PriorityValue<Color>>();
    
    /** a list of all observers */
    private List<Observer<?>> observers = new LinkedList<Observer<?>>();
    
    /**
     * Adds a new provider of colors to this manager.
     * @param <D> the kind of observers this provider likes
     * @param priority the importance of the new provider
     * @param kind the kind of observers this provider likes
     * @param provider the new provider
     */
    public <D extends DockColor>void publish( Priority priority, Class<? extends D> kind, ColorProvider<D> provider ){
        if( priority == null )
            throw new IllegalArgumentException( "priority must not be null" );
        if( kind == null )
            throw new IllegalArgumentException( "kind must not be null" );
        if( provider == null )
            throw new IllegalArgumentException( "Provider must not be null" );
        
        PriorityValue<ColorProvider<?>> value = providers.get( kind );
        if( value == null ){
            value = new PriorityValue<ColorProvider<?>>();
            providers.put( kind, value );
        }
        
        if( value.set( priority, provider )){
            for( Observer<?> check : observers ){
                check.resetProvider();
            }
        }
    }
    
    /**
     * Searches for all occurrences of <code>provider</code> and removes them.
     * All {@link DockColor}s that used <code>provider</code> are redistributed.
     * @param priority the importance of the provider 
     * @param provider the provider to remove
     */
    public void unpublish( Priority priority, ColorProvider<?> provider ){
        Iterator<PriorityValue<ColorProvider<?>>> iterator = providers.values().iterator();
        boolean change = false;
        
        while( iterator.hasNext() ){
            PriorityValue<ColorProvider<?>> next = iterator.next();
            if( next.get( priority ) == provider ){
                change = next.set( priority, null ) || change;
                if( next.get() == null ){
                    iterator.remove();
                }
            }
        }
        
        if( change ){
            for( Observer<?> check : observers ){
                check.resetProvider();
            }
        }
    }

    /**
     * Installs a new observer.
     * @param <D> the type of the observer
     * @param colorId the id of the color that should be observed
     * @param kind the type of the observer
     * @param observer the new observer
     */
    public <D extends DockColor> void add( String colorId, Class<? super D> kind, D observer ){
        if( kind == null )
            throw new IllegalArgumentException( "kind must not be null" );
        if( colorId == null )
            throw new IllegalArgumentException( "colorId must not be null" );
        if( observer == null )
            throw new IllegalArgumentException( "observer must not be null" );
        
        Observer<D> combination = new Observer<D>( colorId, kind, observer );
        observers.add( combination );
    }
    
    /**
     * Uninstalls an observer
     * @param observer the observer to remove
     */
    public void remove( DockColor observer ){
        ListIterator<Observer<?>> list = observers.listIterator();
        while( list.hasNext() ){
            Observer<?> next = list.next();
            if( next.getObserver() == observer ){
                list.remove();
                next.setProvider( null );
                return;
            }
        }
    }
    
    /**
     * Searches a provider that can be used for <code>clazz</code>.
     * @param clazz the type whose provider is searched
     * @return the provider or <code>null</code>
     */
    protected ColorProvider<?> getProviderFor( Class<?> clazz ){
        return getProviderFor( clazz, new HashSet<Class<?>>() );
    }
    
    /**
     * Sets a color of this manager.
     * @param priority the importance of this value
     * @param id the id of the color
     * @param color the new color
     */
    public void put( Priority priority, String id, Color color ){
        PriorityValue<Color> value = colors.get( id );
        if( value == null ){
            value = new PriorityValue<Color>();
            colors.put( id, value );
        }
        
        if( value.set( priority, color ) ){
            for( Observer<?> observer : observers ){
                if( observer.id.equals( id )){
                    observer.update( color );
                }
            }
        }
        
        if( value.get() == null )
            colors.remove( id );
    }
    
    /**
     * Gets a color of this manager.
     * @param id the id of the color
     * @return the color or <code>null</code>
     * @see #put(Priority, String, Color)
     */
    public Color get( String id ){
        PriorityValue<Color> value = colors.get( id );
        return value == null ? null : value.get();
    }
    
    /**
     * Removes all values that stored under the given priority.
     * @param priority the priority whose elements should be removed
     */
    public void clear( Priority priority ){
        Iterator<PriorityValue<Color>> colorIterator = colors.values().iterator();
        while( colorIterator.hasNext() ){
            PriorityValue<Color> value = colorIterator.next();
            value.set( priority, null );
            if( value.get() == null )
                colorIterator.remove();
        }
        
        Iterator<PriorityValue<ColorProvider<?>>> providerIterator = providers.values().iterator();
        while( providerIterator.hasNext() ){
            PriorityValue<ColorProvider<?>> value = providerIterator.next();
            value.set( priority, null );
            if( value.get() == null )
                providerIterator.remove();
        }
        
        for( Observer<?> observer : observers ){
            observer.resetAll();
        }
    }
    
    /**
     * Searches a provider that can be used for <code>clazz</code>.
     * @param clazz the type whose provider is searched
     * @param checked a set of already checked types, might be expanded by this method
     * @return the provider or <code>null</code>
     */
    private ColorProvider<?> getProviderFor( Class<?> clazz, Set<Class<?>> checked ){
        if( !checked.add( clazz ))
            return null;
        
        PriorityValue<ColorProvider<?>> value = providers.get( clazz );
        ColorProvider<?> result = value == null ? null : value.get();
        if( result != null )
            return result;
        
        for( Class<?> next : clazz.getInterfaces() ){
            result = getProviderFor( next, checked );
            if( result != null )
                return result;
        }
        
        Class<?> parent = clazz.getSuperclass();
        if( parent == null )
            return null;
        
        return getProviderFor( parent, checked );
    }
    
    /**
     * Holds all the properties that are associated with a {@link DockColor}.
     * @author Benjamin Sigg
     * @param <D> the kind of <code>DockColor</code> that is wrapped by
     * this <code>Observer</code>.
     */
    private class Observer<D extends DockColor>{
        /** the id of the observed color */
        private String id;
        /** the kind of observer */
        private Class<? super D> type;
        /** the observer which gets informed about changed colors */
        private D observer;
        /** a provider for modified colors */
        private ColorProvider<D> provider;
        
        /**
         * Creates a new observer
         * @param id the id of the observed color
         * @param type the type of observer that is wrapped by this <code>Observer</code>
         * @param observer the listener for changed colors
         */
        public Observer( String id, Class<? super D> type, D observer ){
            this.id = id;
            this.type = type;
            this.observer = observer;
            
            resetAll();
        }
        
        /**
         * Gets the listener for changed colors.
         * @return the listener
         */
        public D getObserver() {
            return observer;
        }
        
        /**
         * Updates color and provider of this <code>Observer</code>.
         */
        @SuppressWarnings("unchecked")
        public void resetAll(){
            ColorProvider<D> provider = (ColorProvider<D>)getProviderFor( type );
            if( provider == null )
                update( get( id ) );
            else
                setProvider( provider );
        }
        
        /**
         * Ensures that the correct {@link ColorProvider} is used.
         */
        @SuppressWarnings("unchecked")
        public void resetProvider(){
            setProvider( (ColorProvider<D>)getProviderFor( type ) );
        }
        
        /**
         * Sets the {@link ColorProvider} of this <code>Observer</code>.
         * @param provider the new provider, can be <code>null</code>
         */
        public void setProvider( ColorProvider<D> provider ) {
            if( this.provider != provider ){
                if( this.provider != null )
                    this.provider.remove( observer );
                
                this.provider = provider;
                
                if( provider != null ){
                    provider.add( observer );
                }
                
                update( get( id ));
            }
        }
        
        /**
         * Called when a color has been exchanged and the {@link #getObserver() listener}
         * of this <code>Observer</code> has to be informed.
         * @param color the new color, can be <code>null</code>
         */
        public void update( Color color ){
            if( provider == null )
                observer.set( color );
            else
                provider.set( color, id, observer );
        }
    }
}
