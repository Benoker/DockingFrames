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

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.station.DockableDisplayer;

/**
 * A manager used to change the behavior of a {@link SplitDockStation}.
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
     * Called when the bounds of all {@link DockableDisplayer} of a station have
     * to be updated. Subclasses normally can just call {@link Root#updateBounds(double, double, double, double, double, double)}.
     * @param root the root of a tree of {@link Dockable}s
     * @param x the left bound
     * @param y the top bound
     * @param width the width of the area into which all displayers should be put
     * @param height the height of the area into which all displayers should be put
     * @param factorW a factory with which all x-coordinates have to be multiplied in
     * order to get coordinates in pixels.
     * @param factorH a factory with which all y-coordinates have to be multiplied in
     * order to get coordinates in pixels.
     */
    public void updateBounds( Root root, double x, double y, double width, double height, double factorW, double factorH );
}
