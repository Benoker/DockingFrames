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
package bibliothek.gui.dock.common.theme;

import bibliothek.gui.dock.themes.ThemeFactory;

/**
 * A listener to a {@link ThemeMap}, gets informed about changes of the map.
 * @author Benjamin Sigg
 */
public interface ThemeMapListener {

    /**
     * Called when an entry was changed (includes adding or removing).
     * @param map the source of the event
     * @param index the location of the changed entry
     * @param key the key of the changed entry
     * @param oldFactory the old value of the entry, <code>null</code> if the entry was added
     * @param newFactory the new value of the entry, <code>null</code> if the entry was removed
     */
    public void changed( ThemeMap map, int index, String key, ThemeFactory oldFactory, ThemeFactory newFactory );
    
    /**
     * Called when the selected factory has been changed.
     * @param map the source of the event
     * @param oldKey the name of the old factory, can be <code>null</code>
     * @param newKey the name of the new factory, can be <code>null</code>
     */
    public void selectionChanged( ThemeMap map, String oldKey, String newKey );
}
