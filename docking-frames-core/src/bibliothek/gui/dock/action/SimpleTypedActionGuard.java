/**
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

package bibliothek.gui.dock.action;

import bibliothek.gui.Dockable;

/**
 * An {@link ActionGuard} that {@link #react(Dockable) reacts} if the
 * {@link Dockable} has the exact type that was set through the
 * {@link #SimpleTypedActionGuard(Class, DockActionSource) constructor}.
 * @author Benjamin Sigg
 * @param <T> the type a {@link Dockable} must have such that this
 * {@link ActionGuard} reacts
 */
public class SimpleTypedActionGuard<T extends Dockable> extends TypedActionGuard<T> {
    private DockActionSource source;
    
    /**
     * Sets the type and the source of this guard
     * @param type The type for which this SimpleTypedActionGuard will react.
     * The method {@link #react(Dockable)} will return <code>true</code> only
     * if the {@link Dockable} has this type
     * @param source The source that will be returned by {@link #getSource(Dockable)}
     */
    public SimpleTypedActionGuard( Class<T> type, DockActionSource source ){
        super( type );
        setSource( source );
    }
    
    /**
     * Gets the {@link DockActionSource} that will be added to all
     * {@link Dockable Dockables} with the correct type.
     * @return The source
     */
    public DockActionSource getSource(){
        return source;
    }

    /**
     * Sets the source of this ActionGuard. The source will be returned
     * by {@link #getSource(Dockable)} and so it will be added to all
     * Dockables on which this {@link ActionGuard} {@link #react(Dockable) reacts}.
     * @param source The source, not <code>null</code>
     */
    public void setSource( DockActionSource source ) {
        if( source == null )
            throw new IllegalArgumentException( "source must not be null" );
        
        this.source = source;
    }
    
    @Override
    protected DockActionSource getTypedSource( T dockable ) {
        return source;
    }
}
