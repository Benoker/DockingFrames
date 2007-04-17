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


package bibliothek.gui.dock.event;

import bibliothek.gui.DockFrontend;
import bibliothek.gui.dock.Dockable;

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
