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
package bibliothek.extension.gui.dock.preference.preferences;

import bibliothek.extension.gui.dock.preference.DefaultPreference;
import bibliothek.extension.gui.dock.preference.Preference;
import bibliothek.gui.dock.util.DockProperties;
import bibliothek.gui.dock.util.Priority;
import bibliothek.gui.dock.util.PropertyKey;
import bibliothek.gui.dock.util.TextManager;
import bibliothek.util.Path;

/**
 * A {@link Preference} that can read and write its value from a
 * {@link DockProperties}.
 * @author Benjamin Sigg
 *
 * @param <V> the kind of value this preference uses
 */
public class DockPropertyPreference<V> extends DefaultPreference<V> {
    private PropertyKey<V> key;
    private DockProperties properties;
    
    /**
     * Creates a new preference.
     * @param properties the properties from which this preference reads its values
     * and to which it writes its values
     * @param key the key of the value this preference reads from a {@link DockProperties}.
     * @param type the type of values used in this preference
     * @param path the unique path of this preference
     */
    public DockPropertyPreference( DockProperties properties, PropertyKey<V> key, Path type, Path path ){
        super( type, path );
        if( key == null )
            throw new IllegalArgumentException( "key must not be null" );
        
        if( properties == null )
            throw new IllegalArgumentException( "properties must not be null" );
        
        this.key = key;
        this.properties = properties;
    }
    
    /**
     * Creates a new preference.
     * @param properties the properties from which this preference reads its values
     * and to which it writes its values
     * @param key the key of the value this preference reads from a {@link DockProperties}.
     * @param label the text associated with this preference
     * @param type the type of values used in this preference
     * @param path the unique path of this preference
     */
    public DockPropertyPreference( DockProperties properties, PropertyKey<V> key, String label, Path type, Path path ){
        super( label, type, path );
        if( key == null )
            throw new IllegalArgumentException( "key must not be null" );
        
        if( properties == null )
            throw new IllegalArgumentException( "properties must not be null" );
        
        this.key = key;
        this.properties = properties;
    }
    
    /**
     * Creates a new preference.
     * @param prefix the prefix of the key used for the {@link TextManager}. The strings
     * ".label" and ".description" will be added to <code>prefix</code> in order
     * to generate two keys.
     * @param properties the properties from which this preference reads its values
     * and to which it writes its values
     * @param key the key of the value this preference reads from a {@link DockProperties}.
     * @param defaultValue the initial value of this preference
     * @param type the type of values used in this preference
     * @param path the unique path of this preference
     */
    public DockPropertyPreference( String prefix, DockProperties properties, PropertyKey<V> key, V defaultValue, Path type, Path path ){
        this( properties, key, type, path );
        setDefaultValue( defaultValue );
        
        setLabelId( prefix + ".label" );
        setDescriptionId( prefix + ".description" );
    }
    
    public void read(){
    	V value = properties.get( key, Priority.CLIENT );
    	if( value == null ){
    		setValue( getDefaultValue() );
    	}
    	else{
    		setValue( value );
    	}
    }
    
    public void write(){
        properties.setOrRemove( key, getValue(), Priority.CLIENT );
    }
}
