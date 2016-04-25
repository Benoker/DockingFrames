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
package bibliothek.gui.dock.facile.lookandfeel;

import java.awt.Component;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import bibliothek.gui.DockFrontend;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.support.lookandfeel.ComponentCollector;

/**
 * A {@link ComponentCollector} which collects all {@link Component}s accessible
 * through the known {@link Dockable}s of a {@link DockFrontend}.
 * @author Benjamin Sigg
 */
public class DockableCollector implements ComponentCollector {
    /** the source of Components */
    private DockFrontend frontend;
    
    /**
     * Creates a new collector
     * @param frontend the source of all {@link Component}s
     */
    public DockableCollector( DockFrontend frontend ){
        if( frontend == null )
            throw new NullPointerException( "frontend must not be null" );
        this.frontend = frontend;
    }
    
    public Collection<Component> listComponents() {
        Set<Component> set = new HashSet<Component>();
        for( Dockable dockable : frontend.listDockables() )
            set.add( dockable.getComponent() );
        
        for( Dockable dockable : frontend.getController().getRegister().listDockables() )
            set.add( dockable.getComponent() );
        
        return set;
    }
}
