/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2008 Benjamin Sigg
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
 * An object that works together with a {@link DockFactory}, this object
 * decides of its own whether it wants to store information about a 
 * {@link DockElement} or not.
 * @author Benjamin Sigg
 * @param <L> the kind of object this factory uses as intermediate format
 */
public interface AdjacentDockFactory <L> extends DockConverter<DockElement, L>{
    /**
     * Tells whether this factory is interested in storing information for
     * <code>element</code>. This method must ensure that <code>element</code>
     * is an instance of of <code>D</code>.
     * @param element the element which might be stored by this factory
     * @return <code>true</code> if the factory wants to store <code>element</code>
     */
    public boolean interested( DockElement element );
    
}
