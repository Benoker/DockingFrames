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
package bibliothek.gui.dock.station.split;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.station.DockableDisplayer;
import bibliothek.gui.dock.station.StationDropItem;
import bibliothek.gui.dock.station.support.CombinerSource;
import bibliothek.gui.dock.station.support.CombinerTarget;

/**
 * A manager used to implement the behavior of a {@link SplitDockStation}. The
 * {@link SplitLayoutManager} is responsible to define the actions that have
 * to be taken on drop and move-events. It is also responsible to validate
 * any movement of the dividers.
 * 
 * @author Benjamin Sigg
 */
public interface SplitLayoutManager {
    /**
     * Called by a {@link SplitDockStation} when this manager will be used
     * by the station.
     * @param station the invoking station
     */
    public void install( SplitDockStation station );
    
    /**
     * Called by a {@link SplitDockStation} which used this manager and no
     * longer does.
     * @param station the invoking station
     */
    public void uninstall( SplitDockStation station );
    
    /**
     * Called before the station changes its fullscreen-Dockable.
     * @param station the invoking station
     * @param dockable the element which the station wants to make fullscreen,
     * can be <code>null</code>
     * @return the element which the station will made fullscreen, must either
     * be a child of <code>station</code> or <code>null</code>.
     */
    public Dockable willMakeFullscreen( SplitDockStation station, Dockable dockable );

    /**
     * Call forwarded from {@link DockStation#prepareDrop(StationDropItem) DockStation.prepareDrop}.
     * This method calculates where to drop a new {@link Dockable}.<br>
     * This {@link SplitLayoutManager} may or may not set the {@link CombinerTarget} and {@link CombinerSource} of
     * the created {@link PutInfo}. If there is no combiner information but the mouse is over the center or the title
     * of a selected {@link Leaf}, then the {@link SplitDockStation} will create the missing information automatically.
     * If there is already information set, then the station will silently assume that this manager did set the information
     * and that the information is correct.
     * @param station the calling station
     * @param dockable the element that might be dropped
     * @return where to drop <code>dockable</code> or <code>null</code> if the element should not be dropped
     */
    public PutInfo prepareDrop( SplitDockStation station, StationDropItem dockable );

    /**
     * Call forwarded from {@link DockStation#prepareDrop(bibliothek.gui.dock.station.StationDropItem) DockStation.prepareDrop} if
     * the operation is a move operation (if the <code>dockable</code> is already a child of this station).
     * This method calculates where to move a {@link Dockable} of <code>station</code>.
     * @param station the calling station
     * @param dockable the element that might be dropped
     * @return where to drop <code>dockable</code> or <code>null</code> if the element should not be dropped
     * @see #prepareDrop(SplitDockStation, StationDropItem)
     */
    public PutInfo prepareMove( SplitDockStation station, StationDropItem dockable );
    
    /**
     * Calculates the value a divider should have if the {@link Dockable}
     * of <code>putInfo</code> is added alongside of <code>origin</code>. The
     * result has to be stored directly in <code>putInfo</code>.
     * @param station the station for which the calculation has to be done
     * @param putInfo the new child of the station
     * @param origin a leaf of this station or <code>null</code>
     * @param item detailed information about the drag and drop operation that is going on
     */
    public void calculateDivider( SplitDockStation station, PutInfo putInfo, Leaf origin, StationDropItem item );
    
    /**
     * Tests whether the specified <code>divider</code>-value is legal or not.
     * @param station the station for which the divider is calculated
     * @param divider the value of a divider on a {@link Node}
     * @param node the <code>Node</code> for which the test is performed
     * @return a legal value as near as possible to <code>divider</code>
     */
    public double validateDivider( SplitDockStation station, double divider, Node node );
    
    /**
     * Checks whether <code>info</code> is valid or not.
     * @param station the station for which <code>info</code> will be used 
     * @param info the preferred drop location
     * @return a valid {@link PutInfo}, <code>info</code> or <code>null</code>.
     */
    public PutInfo validatePutInfo( SplitDockStation station, PutInfo info );
    
    /**
     * Called when the bounds of all {@link DockableDisplayer} of a station have
     * to be updated. Subclasses normally can just call
     * {@link Root#updateBounds(double, double, double, double, double, double, boolean)} 
     * with <code>width</code> and <code>height</code> set to 1.0.
     * @param root the root of a tree of {@link Dockable}s
     * @param x the left bound
     * @param y the top bound
     * @param factorW a factor with which all x-coordinates have to be multiplied in
     * order to get coordinates in pixels. 0 if the basic station has no size.
     * @param factorH a factor with which all y-coordinates have to be multiplied in
     * order to get coordinates in pixels. 0 if the basic station has no size.
     */
    public void updateBounds( Root root, double x, double y, double factorW, double factorH );
}
