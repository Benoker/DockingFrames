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


package bibliothek.gui.dock;

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
     * Tells whether to ignore the children of the station when saving or not. If the children
     * are ignored, no factories are needed for them.
     * @param station the station whose children might be ignored
     * @return <code>true</code> if the station is saved as having no children
     */
    public boolean ignoreChildren( DockStation station );
}
