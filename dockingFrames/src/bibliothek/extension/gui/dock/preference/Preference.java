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

/**
 * A representation of a single entry in a {@link DefaultPreferenceModel}.
 * @author Benjamin Sigg
 *
 * @param <V> the kind of value this preference stores
 */
public interface Preference<V> {
    /**
     * Uses an unknown source to reload this preference.
     */
    public void read();
    
    /**
     * Stores the contents of this preference at the location where it will 
     * be used.
     */
    public void write();
    
    /**
     * Tells whether <code>operation</code> is enabled and thus {@link #doOperation(PreferenceOperation)}
     * can be called.
     * @param operation some operation of {@link #getOperations()}
     * @return <code>true</code> if the operation can be executed
     */
    public boolean isEnabled( PreferenceOperation operation );
    
    /**
     * Gets a list of operations which can be executed by this preference.
     * @return the list of operations (enabled and disabled operations)
     */
    public PreferenceOperation[] getOperations();
    
    /**
     * Executes the enabled operation <code>operation</code>
     * @param operation the key of the operation
     */
    public void doOperation( PreferenceOperation operation );
    
    /**
     * Adds a listener to this preference.
     * @param listener the new listener
     */
    public void addPreferenceListener( PreferenceListener<V> listener );
    
    /**
     * Removes a listener from this preference.
     * @param listener the listener to remove
     */
    public void removePreferenceListener( PreferenceListener<V> listener );
    
    /**
     * Gets a short human readable string that is used as name for this preference.
     * @return a short human readable string
     */
    public String getLabel();
    
    /**
     * Gets a long human readable description of this preference. 
     * @return the description, may be <code>null</code>, may be formated 
     * in HTML
     */
    public String getDescription();
    
    /**
     * Gets the value of this preference.
     * @return the value, might be <code>null</code> 
     */
    public V getValue();
    
    /**
     * Sets the value of this preference.
     * @param value the value, might be <code>null</code> (depends on the
     * editor used for this preference)
     */
    public void setValue( V value );
    
    /**
     * Gets the type of the value that this preferences uses.
     * @return the type of value
     */
    public Class<V> getPreferenceClass();
}
