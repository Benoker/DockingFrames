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

import bibliothek.gui.DockStation;
import bibliothek.gui.dock.DockElement;
import bibliothek.gui.dock.perspective.PerspectiveElement;
import bibliothek.gui.dock.perspective.PerspectiveStation;

/**
 * A DockSituationIgnore is used in a {@link DockSituation} to decide, which
 * elements (stations and dockables) should be written into a stream. Elements
 * can be filtered by this Ignore, and will not reappear if the situation is
 * read again.
 * @author Benjamin Sigg
 *
 */
public interface DockSituationIgnore {
    /**
     * Tells whether to ignore this element when saving. If an element is ignored, no 
     * factory is needed for it.
     * @param element the element which might not be saved
     * @return <code>true</code> if the element should not be saved
     */
    public boolean ignoreElement( DockElement element );
    
    /**
     * Tells whether to ignore this element when saving. If an element is ignored, no 
     * factory is needed for it.
     * @param element the element which might not be saved
     * @return <code>true</code> if the element should not be saved
     */
    public boolean ignoreElement( PerspectiveElement element );
    
    /**
     * Tells whether to ignore the children of the station when saving or not. If the children
     * are ignored, no factories are needed for them.
     * @param station the station whose children might be ignored
     * @return <code>true</code> if the station is saved as having no children
     */
    public boolean ignoreChildren( DockStation station );
    
    /**
     * Tells whether to ignore the children of the station when saving or not. If the children
     * are ignored, no factories are needed for them.
     * @param station the station whose children might be ignored
     * @return <code>true</code> if the station is saved as having no children
     */
    public boolean ignoreChildren( PerspectiveStation station );
}
