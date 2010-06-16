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
     * Call forwarded from {@link DockStation#prepareDrop(int, int, int, int, boolean, Dockable) DockStation.prepareDrop}.
     * This method calculates where to drop a new {@link Dockable}.
     * @param station the calling station
     * @param x the x-coordinate of the mouse on the screen
     * @param y the y-coordinate of the mouse on the screen
     * @param titleX the location of the title or <code>x</code> if no title is grabbed
     * @param titleY the location of the title or <code>y</code> if no title is grabbed
     * @param checkOverrideZone whether to respect the override zone of the parent
     * @param dockable the element that might be dropped
     * @return where to drop <code>dockable</code> or <code>null</code> if the element should not be dropped
     */
    public PutInfo prepareDrop( SplitDockStation station, int x, int y, int titleX, int titleY, boolean checkOverrideZone, Dockable dockable );

    /**
     * Call forwarded from {@link DockStation#prepareMove(int, int, int, int, boolean, Dockable) DockStation.prepareMove}.
     * This method calculates where to move a {@link Dockable} of <code>station</code>.
     * @param station the calling station
     * @param x the x-coordinate of the mouse on the screen
     * @param y the y-coordinate of the mouse on the screen
     * @param titleX the location of the title or <code>x</code> if no title is grabbed
     * @param titleY the location of the title or <code>y</code> if no title is grabbed
     * @param checkOverrideZone whether to respect the override zone of the parent
     * @param dockable the element that might be dropped
     * @return where to drop <code>dockable</code> or <code>null</code> if the element should not be dropped
     */
    public PutInfo prepareMove( SplitDockStation station, int x, int y, int titleX, int titleY, boolean checkOverrideZone, Dockable dockable );
    
    /**
     * Calculates the value a divider should have if the {@link Dockable}
     * of <code>putInfo</code> is added alongside of <code>origin</code>. The
     * result has to be stored directly in <code>putInfo</code>.
     * @param station the station for which the calculation has to be done
     * @param putInfo the new child of the station
     * @param origin a leaf of this station or <code>null</code>
     */
    public void calculateDivider( SplitDockStation station, PutInfo putInfo, Leaf origin );
    
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
