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
package bibliothek.extension.gui.dock.preference;

import java.util.ArrayList;
import java.util.List;

/**
 * An abstract implementation of {@link Preference} that offers support
 * for {@link PreferenceListener}s.
 * 
 * @author Benjamin Sigg
 *
 * @param <V> the kind of value this preference uses
 */
public abstract class AbstractPreference<V> implements Preference<V>{
    /** the list of known listeners */
    private List<PreferenceListener<V>> listeners = new ArrayList<PreferenceListener<V>>();
    
    public void addPreferenceListener( PreferenceListener<V> listener ) {
        if( listener == null )
            throw new IllegalArgumentException( "listener must not be null" );
        listeners.add( listener );
    }
    
    public void removePreferenceListener( PreferenceListener<V> listener ) {
        listeners.remove( listener );
    }
    
    /**
     * Tells whether this preference currently has listeners.
     * @return <code>true</code> if there are any listeners
     */
    protected boolean hasListeners(){
    	return listeners.size() > 0;
    }
    
    /**
     * Gets all the listeners of this preference.
     * @return the list of listeners
     */
    @SuppressWarnings("unchecked")
    protected PreferenceListener<V>[] listeners(){
        return listeners.toArray( new PreferenceListener[ listeners.size() ] );
    }
    
    /**
     * Informs all listeners that the value of this preference has changed.
     */
    protected void fireChanged(){
        for( PreferenceListener<V> listener : listeners() )
            listener.changed( this );
    }
    
    public boolean isEnabled( PreferenceOperation operation ) {
        return false;
    }
    
    public PreferenceOperation[] getOperations() {
        return null;
    }
    
    public void doOperation( PreferenceOperation operation ) {
        // do nothing
    }
}
