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
package bibliothek.gui.dock.common.intern;

import bibliothek.gui.dock.common.CLocation;

/**
 * An interface giving access to the internal methods of an {@link CDockable}. Only
 * {@link CDockable}s should create instances of this interface.
 * @author Benjamin Sigg
 *
 */
public interface CDockableAccess {
    /**
     * Called after the visibility of the {@link CDockable} has changed.
     * @param visible the new state
     */
    public void informVisibility( boolean visible );
    
    /**
     * Called after the mode of the {@link CDockable} has changed.
     * @param mode the new mode
     */
    public void informMode( CDockable.ExtendedMode mode );
    
    /**
     * Tells which unique id the owning {@link CDockable} has.
     * @param id the unique id
     */
    public void setUniqueId( String id );
    
    /**
     * Gets the unique id of this dockable.
     * @return the unique id
     */
    public String getUniqueId();
    
    /**
     * Gets the user set location of this dockable. Sets the location
     * to <code>null</code>.
     * @return the location
     */
    public CLocation internalLocation();
}
