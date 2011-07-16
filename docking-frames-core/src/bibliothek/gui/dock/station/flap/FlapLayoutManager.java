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
package bibliothek.gui.dock.station.flap;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.FlapDockStation;

/**
 * A <code>FlapLayoutManager</code> stores properties of {@link FlapDockStation}s
 * and can influence the behavior of a {@link FlapDockStation}.<br>
 * An implementation might be interested in these methods:
 * <ul>
 * <li>{@link FlapDockStation#updateHold(Dockable)} when the hold property of a <code>Dockable</code> needs to be changed</li>
 * <li>{@link FlapDockStation#updateWindowSize(Dockable)} when the size of a <code>Dockable</code> needs to be updated</li>
 * </ul>
 * @author Benjamin Sigg
 */
public interface FlapLayoutManager {
    /**
     * Called before this manager is used.
     * @param station the station that will use this manager
     */
    public void install( FlapDockStation station );
    
    /**
     * Called when this manager is no longer used.
     * @param station the station that will no longer use this manager
     */
    public void uninstall( FlapDockStation station );
    
    /**
     * Adds an observer to this manager, the observer is to be informed if an important property
     * changes.
     * @param listener the new observer, not <code>null</code>
     */
    public void addListener( FlapLayoutManagerListener listener );
    
    /**
     * Removes the observer <code>listener</code> from this manager.
     * @param listener the observer to remove
     */
    public void removeListener( FlapLayoutManagerListener listener );
    
    /**
     * Called when <code>dockable</code> is added to <code>station</code> and
     * <code>station</code> does not know whether <code>dockable</code> should
     * be hold open even when it is not focused.
     * @param station the caller
     * @param dockable the new element on <code>station</code>
     * @return the initial value of the hold property
     */
    public boolean isHold( FlapDockStation station, Dockable dockable );
    
    /**
     * Called when the user changes the hold property of <code>dockable</code>.
     * @param station the caller
     * @param dockable the child of <code>station</code> whose property
     * has changed
     * @param hold the new value
     */
    public void setHold( FlapDockStation station, Dockable dockable, boolean hold );
    
    /**
     * Tells whether the user is supposed to switch the {@link #setHold(FlapDockStation, Dockable, boolean) hold} property.
     * @param station the caller
     * @param dockable the child of <code>station</code> whose property is asked
     * @return <code>true</code> if the hold property can be changed, <code>false</code> if the user should not be
     * presented with a button to change the property
     */
    public boolean isHoldSwitchable( FlapDockStation station, Dockable dockable );
    
    /**
     * Called when <code>dockable</code> is about to open and <code>station</code>
     * has to find out which size <code>dockable</code> should have.
     * @param station the caller
     * @param dockable the child of <code>station</code> that is going to
     * be shown.
     * @return the size of <code>dockable</code>
     */
    public int getSize( FlapDockStation station, Dockable dockable );
    
    /**
     * Called when the user changes the size of <code>dockable</code>.
     * @param station the caller
     * @param dockable the element whose size has been changed
     * @param size the new size
     */
    public void setSize( FlapDockStation station, Dockable dockable, int size );
}
