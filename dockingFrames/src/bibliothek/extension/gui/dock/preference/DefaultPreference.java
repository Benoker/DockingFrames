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

import bibliothek.util.Path;

/**
 * A generic {@link Preference} that can hold any value. This preference does not implement 
 * the {@link #read()} or {@link #write()} method. If a {@link #setDefaultValue(Object) default-value}
 * is set, then this preference activates the operation {@link PreferenceOperation#DEFAULT}, otherwise
 * it shows no operations.<br>
 * This preference is set to be an {@link #isNatural() artificial} preference, subclasses may call
 * {@link #setNatural(boolean)} to change the behavior.
 * @author Benjamin Sigg
 *
 * @param <V> the kind of value this preference holds
 */
public abstract class DefaultPreference<V> extends AbstractPreference<V>{
    private V value;
    private Object valueInfo;
    private Path type;
    
    private String label;
    private String description;

    private V defaultValue;
    private Path path;
    
    private boolean natural = false;
    
    /**
     * Creates a new preference.
     * @param type the type of value this preference uses
     * @param path a unique path for this preference, all paths starting with
     * "dock" are reserved for this framework
     */
    public DefaultPreference( Path type, Path path ){
        if( type == null )
            throw new IllegalArgumentException( "type must not be null" );
        
        if( path == null )
            throw new IllegalArgumentException( "path must not be null" );
        
        if( path.getSegmentCount() == 0 )
            throw new IllegalArgumentException( "the root path is not a valid path for a preference" );          
        
        this.type = type;
        this.path = path;
    }
    
    /**
     * Creates a new preference.
     * @param label a short human readable label for this preference
     * @param type the type of value this preference uses
     * @param path a unique path for this preference, all paths starting with
     * "dock" are reserved for this framework
     */
    public DefaultPreference( String label, Path type, Path path ){
        this( type, path );
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
    
    public Path getTypePath() {
        return type;
    }
    
    /**
     * Sets information about this preferences value. For example for
     * an integer that could be the upper and the lower bounds. The exact
     * meaning and type of this object depends on {@link #getTypePath()}.
     * @param valueInfo the new information or <code>null</code>
     */
    public void setValueInfo(Object valueInfo) {
		this.valueInfo = valueInfo;
	}
    
    public Object getValueInfo() {
    	return valueInfo;
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

    /**
     * Sets the default value of this preference
     * @param defaultValue the new default value
     */
    public void setDefaultValue( V defaultValue ) {
        this.defaultValue = defaultValue;
        fireChanged();
    }
    
    /**
     * Gets the default value of this preference
     * @return the default value, might be <code>null</code>
     */
    public V getDefaultValue() {
        return defaultValue;
    }

    public Path getPath() {
        return path;
    }
    
    /**
     * Sets whether this preference is natural or artificial.
     * @param natural <code>true</code> if natural, <code>false</code>
     * if artificial
     * @see #isNatural()
     * @see PreferenceModel#isNatural(int)
     */
    public void setNatural( boolean natural ) {
        this.natural = natural;
    }
    
    public boolean isNatural() {
        return natural;
    }
    
    @Override
    public PreferenceOperation[] getOperations() {
        if( defaultValue == null )
            return null;
        else
            return new PreferenceOperation[]{ PreferenceOperation.DEFAULT };
    }
    
    @Override
    public boolean isEnabled( PreferenceOperation operation ) {
        if( operation == PreferenceOperation.DEFAULT ){
            if( defaultValue == null )
                return false;
            
            return !defaultValue.equals( getValue() );
        }
        return false;
    }
    
    @Override
    public void doOperation( PreferenceOperation operation ) {
        if( operation == PreferenceOperation.DEFAULT ){
            if( defaultValue != null ){
                setValue( defaultValue );
            }
        }
        else{
            super.doOperation( operation );
        }
    }
}
