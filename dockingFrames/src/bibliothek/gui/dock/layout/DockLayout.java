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
package bibliothek.gui.dock.layout;

import bibliothek.gui.dock.DockElement;
import bibliothek.gui.dock.DockFactory;

/**
 * A {@link DockLayout} describes the contents of one {@link DockElement}. It is
 * an intermediate format between a {@link DockElement} and the persistent representation
 * for example a xml-file. <code>DockLayout</code>s wrap around the data that is created and stored
 * by {@link DockFactory}s. 
 * @author Benjamin Sigg
 * @param <L> the kind of data stored for in this layout
 */
public class DockLayout<L>{
    private String factory;
    private L data;
    
    /**
     * Creates a new layout.
     * @param factory the factory that created the layout, might be <code>null</code>
     * @param data the data that is stored in this layout, might be <code>null</code>
     */
    public DockLayout( String factory, L data ){
        this.factory = factory;
        this.data = data;
    }
    
    /**
     * Sets the identifier of the factory which created this layout.
     * @param id the identifier of the factory
     */
    public void setFactoryID( String id ){
        factory = id;
    }
    
    /**
     * Gets the identifier of the factory which created this layout.
     * @return the identifier of the factory
     */
    public String getFactoryID(){
        return factory;
    }
    
    /**
     * Sets the data that is stored in the layout.
     * @param data the data, can be <code>null</code>
     */
    public void setData( L data ) {
        this.data = data;
    }
    
    /**
     * Gets the data that is stored in the layout.
     * @return the data, can be <code>null</code>
     */
    public L getData() {
        return data;
    }
}
