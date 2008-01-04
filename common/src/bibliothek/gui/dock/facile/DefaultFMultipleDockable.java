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
package bibliothek.gui.dock.facile;

import bibliothek.gui.dock.dockable.DefaultDockableFactory;
import bibliothek.gui.dock.facile.intern.DefaultFDockable;
import bibliothek.gui.dock.facile.intern.FControlAccess;

/**
 * A {@link FMultipleDockable} that contains a {@link #getContentPane() content-pane}
 * where the client might add or remove as many {@link java.awt.Component}s as
 * it wishes.
 * @author Benjamin Sigg
 */
public class DefaultFMultipleDockable extends DefaultFDockable implements FMultipleDockable{
    /** a factory needed to store or load this dockable */
    private FMultipleDockableFactory factory;
    
    /**
     * Creates a new dockable.
     * @param factory the factory which created or could create this
     * kind of dockable.
     */
    public DefaultFMultipleDockable( FMultipleDockableFactory factory ){
        if( factory == null )
            throw new NullPointerException( "factory must not be null" );
        this.factory = factory;

    }
    
    /**
     * Gets the factory that created this dockable.
     * @return the factory, not <code>null</code>
     */
    public FMultipleDockableFactory getFactory(){
        return factory;
    }
    
    @Override
    public void setControl( FControlAccess control ){
        super.setControl( control );
        if( control == null )
            intern().setFactoryID( DefaultDockableFactory.ID );
        else
            intern().setFactoryID( control.getFactoryId( factory ));
    }
}
