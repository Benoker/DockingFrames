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
package bibliothek.gui.dock.util.laf;

import java.util.ArrayList;
import java.util.List;

/**
 * An abstract implementation of {@link LookAndFeelColors}, handles all the
 * tasks that are related to the management of the {@link LookAndFeelColorsListener}.
 * @author Benjamin Sigg
 */
public abstract class AbstractLookAndFeelColors implements LookAndFeelColors{
    protected List<LookAndFeelColorsListener> listeners = new ArrayList<LookAndFeelColorsListener>();
    
    public void addListener( LookAndFeelColorsListener listener ) {
        if( listener == null )
            throw new IllegalArgumentException( "listener must not be null" );
        listeners.add( listener );
    }
    
    public void removeListener( LookAndFeelColorsListener listener ) {
        listeners.remove( listener );
    }
    
    /**
     * Fires an event that the color <code>key</code> has changed.
     * @param key the key of the changed color
     */
    protected void fireColorChanged( String key ){
        for( LookAndFeelColorsListener listener : listeners.toArray( new LookAndFeelColorsListener[ listeners.size() ] ))
            listener.colorChanged( key );
    }
    
    /**
     * Fires an event that some colors changed
     */
    protected void fireColorsChanged(){
        for( LookAndFeelColorsListener listener : listeners.toArray( new LookAndFeelColorsListener[ listeners.size() ] ))
            listener.colorsChanged();
    }
}
