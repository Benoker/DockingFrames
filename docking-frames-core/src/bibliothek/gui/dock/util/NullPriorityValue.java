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

import bibliothek.gui.DockTheme;

/**
 * This container holds three versions of the same value, a default, theme and
 * a client value. If asked for the value the value with the highest priority
 * is returned. Other than {@link PriorityValue} this container also supports
 * <code>null</code> as value.
 * @author Benjamin Sigg
 *
 * @param <T> the kind of value stored in this {@link NullPriorityValue}
 */
public class NullPriorityValue<T> {
	/** the default value */
    private T valueDefault;
    private boolean valueDefaultSet = false;
    
    /** the value set by a {@link DockTheme} */
    private T valueTheme;
    private boolean valueThemeSet = false;
    
    /** the value set by the client */
    private T valueClient;
    private boolean valueClientSet = false;
    
    /**
     * Sets the value for a given priority.
     * @param priority the priority of <code>value</code>
     * @param value the new value, can be <code>null</code>
     * @return <code>true</code> if the result of {@link #get()}
     * changes because of the call of this method
     */
    public boolean set( Priority priority, T value ){
        T old = get();
        
        switch( priority ){
            case DEFAULT:
                valueDefault = value;
                valueDefaultSet = true;
                break;
            case THEME:
                valueTheme = value;
                valueThemeSet = true;
                break;
            case CLIENT:
                valueClient = value;
                valueClientSet = true;
                break;
        }
        
        return old != get();
    }
    
    /**
     * Tells whether at least one version is set.
     * @return <code>true</code> if at least some value is set
     */
    public boolean isSomethingSet(){
    	return valueDefaultSet || valueThemeSet || valueClientSet;
    }
    
    /**
     * Tells whether the value for <code>priority</code> is set.
     * @param priority the priority to inquire
     * @return <code>true</code> if the value is set
     */
    public boolean isSet( Priority priority ){
    	switch( priority ){
    		case CLIENT: return valueClientSet;
    		case DEFAULT: return valueDefaultSet;
    		case THEME: return valueThemeSet;
    		default: throw new IllegalArgumentException( "unknown priority: " + priority );
    	}
    }
    
    /**
     * Removes the value for <code>priority</code>.
     * @param priority the priority to clean
     * @return <code>true</code> if the result of {@link #get()}
     * changes because of the call to this method
     */
    public boolean unset( Priority priority ){
    	T old = get();
    	switch( priority ){
    		case CLIENT:
    			valueClient = null;
    			valueClientSet = false;
    			break;
    		case DEFAULT:
    			valueDefault = null;
    			valueDefaultSet = false;
    			break;
    		case THEME:
    			valueTheme = null;
    			valueThemeSet = false;
    	}
    	
    	return old != get();
    }
    
    /**
     * Gets the value for a given priority.
     * @param priority the priority for which the value is requested 
     * @return the value that was {@link #set(Priority, Object)} for <code>priority</code>
     */
    public T get( Priority priority ){
        switch( priority ){
            case CLIENT:
                return valueClient;
            case DEFAULT:
                return valueDefault;
            case THEME:
                return valueTheme;
        }
        return null;
    }
    
    /**
     * Gets the current value with the highest priority.
     * @return the value or <code>null</code>
     */
    public T get(){
    	if( valueClientSet )
    		return valueClient;
    	
    	if( valueThemeSet )
    		return valueTheme;
        
    	if( valueDefaultSet )
    		return valueDefault;
        
    	return null;
    }
}
