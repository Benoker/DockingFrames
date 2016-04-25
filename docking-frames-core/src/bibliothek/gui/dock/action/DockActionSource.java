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

package bibliothek.gui.dock.action;

import bibliothek.gui.dock.event.DockActionSourceListener;

/**
 * A list of {@link DockAction DockActions} which may change its size
 * at any time.
 * @author Benjamin Sigg
 */
public interface DockActionSource extends Iterable<DockAction>{
	/**
	 * Gets a hint where to put this source in relation to other sources. Note
	 * that an {@link ActionOffer} can choose to ignore these hints, or interpret
	 * them in an unexpected way.
	 * @return the preferred location of this source
	 */
	public LocationHint getLocationHint();
	
    /**
     * Gets the number of {@link #getDockAction(int) DockActions} which are
     * provided by this source.
     * @return The number of {@link DockAction DockActions}
     */
    public int getDockActionCount();
    
    /**
     * Gets the index'th {@link DockAction} of this source.
     * @param index The index of the action
     * @return The DockAction
     */
    public DockAction getDockAction( int index );
        
    /**
     * Adds a listener to this source. The {@link DockActionSourceListener} should
     * be informed whenever some {@link DockAction DockActions} are added or
     * removed from this source.
     * @param listener The listener
     */
    public void addDockActionSourceListener( DockActionSourceListener listener );
    
    /**
     * Removes an earlier added listener.
     * @param listener The listener to remove
     * @see #addDockActionSourceListener(DockActionSourceListener)
     */
    public void removeDockActionSourceListener( DockActionSourceListener listener );
}
