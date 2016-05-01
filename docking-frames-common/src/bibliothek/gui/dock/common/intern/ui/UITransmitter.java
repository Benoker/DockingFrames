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
package bibliothek.gui.dock.common.intern.ui;

import java.util.*;

import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.event.CControlListener;
import bibliothek.gui.dock.common.intern.CDockable;
import bibliothek.gui.dock.common.intern.color.ColorTransmitter;
import bibliothek.gui.dock.util.UIBridge;
import bibliothek.gui.dock.util.UIValue;

/**
 * An {@link UITransmitter} listens to some kind of map
 * and updates some {@link UIValue}s when the contents of that map change.
 * @author Benjamin Sigg
 * @param <V> the kind of value that is described by the {@link UIValue}
 * @param <U> the kind of {@link UIValue} used in this transmitter
 */
public abstract class UITransmitter<V, U extends UIValue<V>> implements UIBridge<V, U> {
    private CControl control;
    private Set<String> keys = new HashSet<String>();
    private Map<String, List<U>> values = new HashMap<String, List<U>>();
    private Listener listener = new Listener();
    
    /**
     * Creates a new {@link ColorTransmitter}.
     * @param keys the keys which should be monitored by this transmitter
     */
    public UITransmitter( String... keys ){
        for( String key : keys )
            this.keys.add( key );
    }
    
    public void add( String id, U value ) {
        if( keys.contains( id )){
            boolean empty = values.isEmpty();
            
            List<U> list = values.get( id );
            if( list == null ){
                list = new LinkedList<U>();
                values.put( id, list );
            }
            list.add( value );            
            if( empty )
                setListening( true );
        }
    }
    
    public void remove( String id, U value ) {
        if( keys.contains( id )){
            boolean empty = values.isEmpty();
            
            List<U> list = values.get( id );
            list.remove( value );
            if( list.isEmpty() ){
                values.remove( id );
            }            
            if( !empty && values.isEmpty() )
                setListening( false );
        }
    }
    
    /**
     * Sets the {@link CControl} which should be observed for new {@link CDockable}s
     * by this transmitter.
     * @param control the observed control, can be <code>null</code>
     */
    public void setControl( CControl control ) {
        if( !values.isEmpty() )
            setListening( false );
        
        this.control = control;
        
        if( !values.isEmpty() )
            setListening( true );
    }
    
    /**
     * Adds or removes all listeners from the {@link CControl}.
     * @param listening <code>true</code> if the listeners are to be
     * added, <code>false</code> if they have to be removed
     */
    private void setListening( boolean listening ){
        if( this.control != null ){
            if( listening ){
                control.addControlListener( listener );
                for( int i = 0, n = control.getCDockableCount(); i<n; i++ )
                    connect( control.getCDockable( i ) );
            }
            else{
                this.control.removeControlListener( listener );
                for( int i = 0, n = this.control.getCDockableCount(); i<n; i++ )
                    disconnect( control.getCDockable( i ) );
            }
        }
    }
    
    public void set( String id, V value, U observer ) {
        if( keys.contains( id )){
            value = get( value, id, observer );
        }
        observer.set( value );
    }
    
    /**
     * Called when a value needs to be set whose key has been registered at
     * this {@link UITransmitter}.
     * @param value the original value
     * @param id the key of the value
     * @param observer the destination for the value
     * @return the value that should be set to <code>observer</code>
     */
    protected abstract V get( V value, String id, U observer );
    
    /**
     * Called when a value in an observed map has changed.
     * @param dockable the owner of the map
     * @param key the name of the changed value
     * @param value the new value in the map, can be <code>null</code>
     */
    protected abstract void update( CDockable dockable, String key, V value );
    
    /**
     * Gets the {@link CDockable} which is associated with <code>observer</code>.
     * @param observer some observer
     * @return the associated dockable or <code>null</code>
     */
    protected abstract CDockable getDockable( U observer );
    
    /**
     * Transmits <code>value</code> to all {@link UIValue}s which
     * listen to the given id and which are associated with <code>dockable</code>.
     * @param id the id of the changed value
     * @param value the new value, might be <code>null</code>
     * @param dockable the associated dockable, might be <code>null</code>
     */
    protected void set( String id, V value, CDockable dockable ){
        List<U> list = values.get( id );
        if( list != null ){
            for( U observer : list ){
                if( dockable == getDockable( observer )){
                    observer.set( value );
                }
            }
        }
    }
    
    /**
     * Adds a listener to <code>dockable</code> and calls
     * {@link #update(CDockable, String, Object)} whenever some value
     * needs an update.
     * @param dockable the element to observe
     */
    protected abstract void connect( CDockable dockable );
    
    /**
     * Removes a listener from <code>dockable</code>.
     * @param dockable the element from which a listener should be removed
     */
    protected abstract void disconnect( CDockable dockable );
    
    /**
     * A listener that gets informed when new maps join or some color in a map
     * changes.
     * @author Benjamin Sigg
     */
    private class Listener implements CControlListener{
        public void added( CControl control, CDockable dockable ) {
            // ignore
        }

        public void removed( CControl control, CDockable dockable ) {
            // ignore
        }

        public void closed( CControl control, CDockable dockable ) {
            disconnect( dockable );
        }

        public void opened( CControl control, CDockable dockable ) {
            connect( dockable );
        }
    }
}
