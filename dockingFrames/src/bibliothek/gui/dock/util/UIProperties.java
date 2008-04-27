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

import java.util.*;

public class UIProperties<V, U extends UIValue<V>> {
    /** the map of providers known to this manager */
    private java.util.Map<Class<? extends U>, PriorityValue<UIBridge<V, ? extends U>>> bridges =
        new HashMap<Class<? extends U>, PriorityValue<UIBridge<V, ? extends U>>>();
    
    /** the map of resources that have been set */
    private Map<String, PriorityValue<V>> resources = new HashMap<String, PriorityValue<V>>();
    
    /** a list of all observers */
    private List<Observer<?>> observers = new LinkedList<Observer<?>>();
    
    /** whether to stall updates or not */
    private int updateLock = 0;
    
    /**
     * Tells this manager to stall all updates. No {@link UIValue} will
     * be informed when a color or provider changes.
     */
    public void lockUpdate(){
        updateLock++;
    }
    
    /**
     * Tells this manager no longer to stall updates. This triggers a full
     * update on all {@link UIValue}s.
     */
    public void unlockUpdate(){
        updateLock--;
        if( updateLock == 0 ){
            for( Observer<?> observer : observers )
                observer.resetAll();
        }
    }
    
    /**
     * Adds a new bridge between this {@link UIProperties} and a set of
     * {@link UIValue}s that have a certain type.
     * @param <P> the kind of {@link UIValue}s the bridge can handle.
     * @param priority the importance of the new provider
     * @param kind the kind of values that the new bridge should handle
     * @param bridge the new bridge
     */
    public <P extends U>void publish( Priority priority, Class<? extends P> kind, UIBridge<V, P> bridge ){
        if( priority == null )
            throw new IllegalArgumentException( "priority must not be null" );
        if( kind == null )
            throw new IllegalArgumentException( "kind must not be null" );
        if( bridge == null )
            throw new IllegalArgumentException( "bridge must not be null" );
        
        PriorityValue<UIBridge<V, ? extends U>> value = bridges.get( kind );
        if( value == null ){
            value = new PriorityValue<UIBridge<V, ? extends U>>();
            bridges.put( kind, value );
        }
        
        if( value.set( priority, bridge )){
            if( updateLock == 0 ){
                for( Observer<?> check : observers ){
                    check.resetBridge();
                }
            }
        }
    }
    

    /**
     * Removes the bridge that handles the {@link UIValue}s of kind <code>kind</code>.
     * @param priority the importance of the bridge 
     * @param kind some kind of {@link UIValue}
     */
    public void unpublish( Priority priority, Class<? extends U> kind ){
        PriorityValue<UIBridge<V, ? extends U>> value = bridges.get( kind );
        if( value != null ){
            boolean change = value.set( priority, null );
            if( value.get() == null )
                bridges.remove( kind );
            
            if( change && updateLock == 0 ){
                for( Observer<?> check : observers ){
                    check.resetBridge();
                }
            }   
        }
    }
    
    /**
     * Searches for all occurrences of <code>bridge</code> and removes them.
     * All {@link UIValue}s that used <code>bridge</code> are redistributed.
     * @param priority the importance of the bridge 
     * @param bridge the bridge to remove
     */
    public void unpublish( Priority priority, UIBridge<V, ? extends U> bridge ){
        Iterator<PriorityValue<UIBridge<V, ? extends U>>> iterator = bridges.values().iterator();
        boolean change = false;
        
        while( iterator.hasNext() ){
            PriorityValue<UIBridge<V, ? extends U>> next = iterator.next();
            if( next.get( priority ) == bridge ){
                change = next.set( priority, null ) || change;
                if( next.get() == null ){
                    iterator.remove();
                }
            }
        }
        
        if( change && updateLock == 0 ){
            for( Observer<?> check : observers ){
                check.resetBridge();
            }
        }
    }

    /**
     * Installs a new {@link UIValue}. The value will be informed about
     * any change in the resource <code>id</code>.
     * @param <P> the type of the value
     * @param id the id of the resource that <code>value</code> will monitor
     * @param kind the type of the observer
     * @param value the new value
     */
    public <P extends U> void add( String id, Class<? super P> kind, P value ){
        if( kind == null )
            throw new IllegalArgumentException( "kind must not be null" );
        if( id == null )
            throw new IllegalArgumentException( "id must not be null" );
        if( value == null )
            throw new IllegalArgumentException( "value must not be null" );
        
        Observer<P> combination = new Observer<P>( id, kind, value );
        observers.add( combination );
    }
    
    /**
     * Uninstalls an observer of a resource
     * @param value the observer to remove
     */
    public void remove( U value ){
        ListIterator<Observer<?>> list = observers.listIterator();
        while( list.hasNext() ){
            Observer<?> next = list.next();
            if( next.getValue() == value ){
                list.remove();
                next.setBridge( null, false );
                return;
            }
        }
    }
    
    /**
     * Searches a bridge that can be used for <code>clazz</code>.
     * @param clazz the type whose bridge is searched
     * @return the bridge or <code>null</code>
     */
    protected UIBridge<V, ? extends U> getBridgeFor( Class<?> clazz ){
        return getBridgeFor( clazz, new HashSet<Class<?>>() );
    }
    
    
    /**
     * Searches a bridge that can be used for <code>clazz</code>.
     * @param clazz the type whose bridge is searched
     * @param checked a set of already checked types, might be expanded by this method
     * @return the bridge or <code>null</code>
     */
    private UIBridge<V, ? extends U> getBridgeFor( Class<?> clazz, Set<Class<?>> checked ){
        if( !checked.add( clazz ))
            return null;
        
        PriorityValue<UIBridge<V, ? extends U>> value = bridges.get( clazz );
        UIBridge<V, ? extends U> result = value == null ? null : value.get();
        if( result != null )
            return result;
        
        for( Class<?> next : clazz.getInterfaces() ){
            result = getBridgeFor( next, checked );
            if( result != null )
                return result;
        }
        
        Class<?> parent = clazz.getSuperclass();
        if( parent == null )
            return null;
        
        return getBridgeFor( parent, checked );
    }
    
    
    /**
     * Sets a new resource and informs all {@link UIValue} that are observing
     * <code>id</code> about the change.
     * @param priority the importance of this value
     * @param id the id of the color
     * @param resource the new resource
     */
    public void put( Priority priority, String id, V resource ){
        PriorityValue<V> value = resources.get( id );
        if( value == null ){
            value = new PriorityValue<V>();
            resources.put( id, value );
        }
        
        if( value.set( priority, resource ) ){
            if( updateLock == 0 ){
                for( Observer<?> observer : observers ){
                    if( observer.id.equals( id )){
                        observer.update( resource );
                    }
                }
            }
        }
        
        if( value.get() == null )
            resources.remove( id );
    }
    
    /**
     * Gets a resource.
     * @param id the id of the resource
     * @return the resource or <code>null</code>
     * @see #put(Priority, String, Object)
     */
    public V get( String id ){
        PriorityValue<V> value = resources.get( id );
        return value == null ? null : value.get();
    }
    
    /**
     * Removes all values that stored under the given priority.
     * @param priority the priority whose elements should be removed
     */
    public void clear( Priority priority ){
        Iterator<PriorityValue<V>> colorIterator = resources.values().iterator();
        while( colorIterator.hasNext() ){
            PriorityValue<V> value = colorIterator.next();
            value.set( priority, null );
            if( value.get() == null )
                colorIterator.remove();
        }
        
        Iterator<PriorityValue<UIBridge<V, ? extends U>>> providerIterator = bridges.values().iterator();
        while( providerIterator.hasNext() ){
            PriorityValue<UIBridge<V, ? extends U>> value = providerIterator.next();
            value.set( priority, null );
            if( value.get() == null )
                providerIterator.remove();
        }
        
        if( updateLock == 0 ){
            for( Observer<?> observer : observers ){
                observer.resetAll();
            }
        }
    }

    /**
     * Represents the combination of a resource, an {@link UIValue} and its 
     * {@link UIBridge}.
     * @author Benjamin Sigg
     * @param <P> the kind of {@link UIValue} that is wrapped by this <code>Observer</code>
     */
    private class Observer<P extends U>{
        /** the id of the observed resource */
        private String id;
        /** the kind of value this observers bridge handles */
        private Class<? super P> type;
        /** the observer which gets informed about changed resources */
        private P value;
        /** a bridge for modified resources */
        private UIBridge<V, ? super P> bridge;
        
        /**
         * Creates a new observer
         * @param id the id of the observed resource
         * @param type the type of observer that is wrapped by this <code>Observer</code>
         * @param value the listener for changed resources
         */
        public Observer( String id, Class<? super P> type, P value ){
            this.id = id;
            this.type = type;
            this.value = value;
            
            resetAll();
        }
        
        /**
         * Gets the listener for changed resources.
         * @return the listener
         */
        public P getValue() {
            return value;
        }
        
        /**
         * Updates resource and bridge of this <code>Observer</code>.
         */
        @SuppressWarnings("unchecked")
        public void resetAll(){
            UIBridge<V, ? super P> provider = (UIBridge<V, ? super P>)getBridgeFor( type );
            if( provider == null )
                update( get( id ) );
            else
                setBridge( provider, true );
        }
        
        /**
         * Ensures that the correct {@link UIBridge} is used.
         */
        @SuppressWarnings("unchecked")
        public void resetBridge(){
            setBridge( (UIBridge<V, ? super P>)getBridgeFor( type ), false );
        }
        
        /**
         * Sets the {@link UIBridge} of this <code>Observer</code>.
         * @param bridge the new bridge, can be <code>null</code>
         * @param force if <code>true</code>, than an update of the color will
         * be done anyway. Otherwise an update will only be done if a new
         * provider is set.
         */
        public void setBridge( UIBridge<V, ? super P> bridge, boolean force ) {
            if( this.bridge != bridge ){
                if( this.bridge != null )
                    this.bridge.remove( id, value );
                
                this.bridge = bridge;
                
                if( this.bridge != null ){
                    this.bridge.add( id, value );
                }
                
                update( get( id ));
            }
            else if( force ){
                update( get( id ));
            }
        }
        
        /**
         * Called when a resource has been exchanged and the {@link #getValue() listener}
         * of this <code>Observer</code> has to be informed.
         * @param value the new value of the resource, can be <code>null</code>
         */
        public void update( V value ){
            if( bridge == null )
                this.value.set( value );
            else
                bridge.set( id, value, this.value );
        }
    }

}
