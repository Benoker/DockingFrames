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

import bibliothek.gui.DockFrontend;
import bibliothek.gui.Dockable;

/**
 * A listener to a {@link DockFrontend}. The listener is informed about the
 * changes of the frontend.
 * @author Benjamin Sigg
 */
public interface DockFrontendListener {
    /**
     * Invoked if a {@link Dockable} was made invisible through the
     * methods of a frontend.
     * @param fronend the invoker
     * @param dockable the element which is no longer visible
     */
    public void hidden( DockFrontend fronend, Dockable dockable );
    
    /**
     * Invoked if a {@link Dockable} was made visible through the
     * methods of a frontend.
     * @param frontend the invoker
     * @param dockable the element which was made visible
     */
    public void showed( DockFrontend frontend, Dockable dockable );
    
    /**
     * Informs this listener that an additional <code>dockable</code> has
     * been added to the list of known {@link Dockable}s of <code>frontend</code>.
     * @param frontend the source of this call
     * @param dockable the new element
     */
    public void added( DockFrontend frontend, Dockable dockable );
    
    /**
     * Informs this listener that <code>dockable</code> has been removed
     * from the list of known {@link Dockable}s of <code>frontend</code>.
     * @param frontend the source of this call
     * @param dockable the element that has been removed
     */
    public void removed( DockFrontend frontend, Dockable dockable );
    
    /**
     * Called when the {@link DockFrontend#isHideable(Dockable) hideable}-state
     * of <code>dockable</code> changes.
     * @param frontend the source of the event
     * @param dockable the element whose state changed
     * @param hideable the new state
     */
    public void hideable( DockFrontend frontend, Dockable dockable, boolean hideable );
    
    /**
     * Invoked if a new setting was loaded.
     * @param frontend the invoker
     * @param name the name of the setting
     */
    public void loaded( DockFrontend frontend, String name );
    
    /**
     * Invoked if the current setting was saved with the name
     * <code>name</code>.
     * @param frontend the invoker
     * @param name the name of the setting
     */
    public void saved( DockFrontend frontend, String name );
    
    /**
     * Invoked if a setting was deleted.
     * @param frontend the invoker
     * @param name the name of the deleted setting
     */
    public void deleted( DockFrontend frontend, String name );
}
