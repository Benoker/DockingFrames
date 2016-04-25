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
 * A container for three values of different priority.
 * @author Benjamin Sigg
 * @param <T> the kind of values in this container
 */
public class PriorityValue<T> {
    /** the default value */
    private T valueDefault;
    /** the value set by a {@link DockTheme} */
    private T valueTheme;
    /** the value set by the client */
    private T valueClient;
    
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
                break;
            case THEME:
                valueTheme = value;
                break;
            case CLIENT:
                valueClient = value;
                break;
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
        if( valueClient != null )
            return valueClient;
        
        if( valueTheme != null )
            return valueTheme;
        
        return valueDefault;
    }
}
