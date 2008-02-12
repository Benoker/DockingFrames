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
 * for example a xml-file. <code>DockLayout</code>s are created and stored
 * by {@link DockFactory}s.<br> 
 * A <code>DockLayout</code> should not have any references to <code>DockElement</code>s. 
 * @author Benjamin Sigg
 */
public interface DockLayout{
    /**
     * Sets the identifier of the factory which created this layout.
     * @param id the identifier of the factory
     */
    public void setFactoryID( String id );
    
    /**
     * Gets the identifier of the factory which created this layout.
     * @return the identifier of the factory
     */
    public String getFactoryID();
}
