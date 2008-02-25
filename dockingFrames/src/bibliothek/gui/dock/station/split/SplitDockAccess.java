/**
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

package bibliothek.gui.dock.station.split;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.event.DockStationListener;
import bibliothek.gui.dock.station.DockableDisplayer;

/**
 * Provides access to some internal methods and attributes of a {@link SplitDockStation}. An access
 * is granted only to a few selected friend classes.
 * @author Benjamin Sigg
 */
public interface SplitDockAccess {
	/**
	 * Gets the station to which this object grants access.
	 * @return the owner
	 */
    public SplitDockStation getOwner();
        
    /**
     * Gets the {@link DockableDisplayer} which is currently in fullscreen-mode.
     * @return the displayer, may be <code>null</code>
     */
    public DockableDisplayer getFullScreenDockable();
    
    /**
     * Tests whether the given value of the divider is a legal value or not.
     * @param divider The value of the divider
     * @param node the node for which the divider is validated
     * @return a legal value, as near as possible to <code>divider</code>.
     */
    public double validateDivider( double divider, Node node );
    
    /**
     * Creates a {@link DockableDisplayer} for <code>dockable</code> and adds
     * the displayer to the station. Binds <code>dockable</code>.
     * @param dockable the new {@link Dockable}
     * @param fire whether to inform {@link DockStationListener}s about
     * the new element
     * @return the created displayer
     */
    public DockableDisplayer addDisplayer( Dockable dockable, boolean fire );
    
    /**
     * Removes a {@link DockableDisplayer} from the station. The {@link Dockable}
     * of <code>displayer</code> will be unbound.
     * @param displayer the element to remove
     * @param fire whether to inform {@link DockStationListener}s about
     * the change
     */
    public void removeDisplayer( DockableDisplayer displayer, boolean fire );
    
    /**
     * Tries to add <code>Dockable</code> such that the boundaries given
     * by <code>property</code> are full filled.
     * @param dockable a new child of this station
     * @param property the preferred location of the child
     * @param root the root of all possible parents where the child could be inserted
     * @return <code>true</code> if the child could be added, <code>false</code>
     * if no location could be found
     */
    public boolean drop( Dockable dockable, final SplitDockProperty property, SplitNode root );
    
    /**
     * Checks whether <code>info</code> is valid or not.
     * @param info the preferred drop location
     * @return <code>info</code> if it is valid, <code>null</code> otherwise
     */
    public PutInfo validatePutInfo( PutInfo info );
}
