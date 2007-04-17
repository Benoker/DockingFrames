/**
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2007 Benjamin Sigg
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * Benjamin Sigg
 * benjamin_sigg@gmx.ch
 * 
 * Wunderklingerstr. 59
 * 8215 Hallau
 * CH - Switzerland
 */


package bibliothek.gui.dock.action;

import bibliothek.gui.dock.Dockable;

/**
 * This {@link ActionGuard} reacts only on {@link Dockable Dockables}
 * with a given type.
 * @author Benjamin Sigg
 * @param <T> the type of {@link Dockable Dockables} on which this
 * {@link ActionGuard} will react
 */
public abstract class TypedActionGuard<T extends Dockable> implements ActionGuard {
	/** the type recognized by this guard */
    private Class<T> type;
    
    /**
     * Constructs the TypedActionGuard and sets the type to react on.
     * @param type The type on which this guard will react
     */
    public TypedActionGuard( Class<T> type ){
        if( type == null )
            throw new IllegalArgumentException( "Type must not be null" );
        
        this.type = type;
    }
    
    public boolean react( Dockable dockable ) {
        return type.isInstance( dockable );
    }

    public DockActionSource getSource( Dockable dockable ) {
        @SuppressWarnings( "unchecked" )
        T t = (T)dockable;
        return getTypedSource( t );
    }

    /**
     * Gets the {@link DockActionSource} that will be returned by
     * {@link #getSource(Dockable) getSource}.
     * @param dockable The {@link Dockable} for which a source is required
     * @return The source
     */
    protected abstract DockActionSource getTypedSource( T dockable );
}
