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
package bibliothek.gui.dock.event;

import java.awt.Component;
import java.util.List;

import bibliothek.gui.DockController;
import bibliothek.gui.dock.control.ComponentHierarchyObserver;

/**
 * A listener of a {@link ComponentHierarchyObserver}, this listener
 * gets informed whenever some {@link Component}s enter or leave the realm
 * of a {@link DockController}.
 * @author Benjamin Sigg
 */
public interface ComponentHierarchyObserverListener {
    /**
     * Called when some {@link Component}s have entered the realm of
     * <code>controller</code>.
     * @param controller the source of the event
     * @param components an unmodifiable set of new {@link Component}s.
     */
    public void added( DockController controller, List<Component> components );
    
    /**
     * Called when some {@link Component}s have left the realm of
     * <code>controller</code>.
     * @param controller the source of the event
     * @param components an unmodifiable set of {@link Component}s
     */
    public void removed( DockController controller, List<Component> components );
}
