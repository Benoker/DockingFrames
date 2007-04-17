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


package bibliothek.gui.dock.station.support;

import bibliothek.gui.DockTheme;
import bibliothek.gui.DockUI;
import bibliothek.gui.dock.DockStation;
import bibliothek.gui.dock.Dockable;
import bibliothek.gui.dock.DockableDisplayer;
import bibliothek.gui.dock.station.DisplayerFactory;
import bibliothek.gui.dock.title.DockTitle;

/**
 * A wrapper for a {@link DisplayerFactory}. Every call to the Interface
 * is forwarded to a delegate. If no delegate is set, a default 
 * <code>DisplayerFactory</code> provided by the {@link DockTheme}
 * is used.
 * @author Benjamin Sigg
 */
public class DisplayerFactoryWrapper implements DisplayerFactory {
    private DisplayerFactory delegate;
    
    /**
     * Gets the delegate of this wrapper.
     * @return the delegate, may be <code>null</code>
     */
    public DisplayerFactory getDelegate() {
        return delegate;
    }
    
    /**
     * Sets the delegate of this wrapper.
     * @param delegate the delegate or <code>null</code>
     */
    public void setDelegate( DisplayerFactory delegate ) {
        if( delegate == this )
            throw new IllegalArgumentException( "Infinite recursion is not allowed" );
        
        this.delegate = delegate;
    }
    
    public DockableDisplayer create( DockStation station, Dockable dockable,
            DockTitle title ) {
        return DockUI.getDisplayerFactory( delegate, station ).create( station, dockable, title );
    }
}
