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
import bibliothek.gui.dock.common.event.CDoubleClickListener;
import bibliothek.gui.dock.common.event.CFocusListener;
import bibliothek.gui.dock.common.event.CKeyboardListener;
import bibliothek.gui.dock.common.mode.ExtendedMode;

/**
 * An interface giving access to the hidden properties of a {@link CDockable}. This
 * interface should only be implemented by inner classes of a {@link CDockable}. Instances
 * should only be used for calling {@link CControlAccess#link(CDockable, CDockableAccess)}.
 * @author Benjamin Sigg
 */
public interface CDockableAccess {
    /**
     * Called after the visibility of the {@link CDockable} has changed.
     * @param visible the new state
     */
    public void informVisibility( boolean visible );
    
    /**
     * Called after the mode of the {@link CDockable} may have changed.
     * @param mode the new mode
     */
    public void informMode( ExtendedMode mode );
    
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
     * Gets the user set location of this dockable. Sets the location to <code>null</code>.
     * @param reset if <code>true</code>, then the location is reset to <code>null</code>
     * @return the location
     */
    public CLocation internalLocation( boolean reset );
    
    /**
     * A focus listener which will be informed whenever the owner of this
     * {@link CDockableAccess} experiences a change in the focus. 
     * @return the listener
     */
    public CFocusListener getFocusListener();
    
    /**
     * A keyboard listener which will be informed about key events that happen
     * on the owner of this access.
     * @return the listener
     */
    public CKeyboardListener getKeyboardListener();
    
    /**
     * A double click listener which will be informed about double click events
     * that happen on the owner of this access.
     * @return the listener
     */
    public CDoubleClickListener getDoubleClickListener();
}
