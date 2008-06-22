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
 * A generic {@link Preference} that can hold any value.
 * @author Benjamin Sigg
 *
 * @param <V> the kind of value this preference holds
 */
public class DefaultPreference<V> extends AbstractPreference<V>{
    private V value;
    private Class<V> type;
    
    private String label;
    private String description;
    
    /**
     * Creates a new preference.
     * @param type the type of value this preference uses
     */
    public DefaultPreference( Class<V> type ){
        if( type == null )
            throw new IllegalArgumentException( "type must not be null" );
        
        this.type = type;
    }
    
    /**
     * Creates a new preference.
     * @param label a shurt human readable label for this preference
     * @param type the type of value this preference uses
     */
    public DefaultPreference( String label, Class<V> type ){
        if( type == null )
            throw new IllegalArgumentException( "type must not be null" );
        
        this.type = type;
        setLabel( label );
    }
    
    public String getLabel() {
        return label;
    }
    
    /**
     * Sets a short human readable label for this preference. Note that
     * changes of the label are not propagated to any listener.
     * @param label the new label
     */
    public void setLabel( String label ) {
        this.label = label;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * Sets a description of this preference. 
     * @param description a human readable string, can be <code>null</code> and
     * can be formated in HTML
     */
    public void setDescription( String description ) {
        this.description = description;
    }
    
    public Class<V> getPreferenceClass() {
        return type;
    }
    
    public V getValue() {
        return value;
    }
    
    public void setValue( V value ) {
        if( this.value != value ){
            this.value = value;
            fireChanged();
        }
    }
}
