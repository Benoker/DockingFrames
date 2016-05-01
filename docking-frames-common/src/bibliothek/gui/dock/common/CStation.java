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
package bibliothek.gui.dock.common;

import bibliothek.gui.dock.common.event.ResizeRequestListener;
import bibliothek.gui.dock.common.intern.CControlAccess;
import bibliothek.gui.dock.common.intern.CDockable;
import bibliothek.gui.dock.common.intern.station.CommonDockStation;
import bibliothek.gui.dock.common.mode.CLocationModeManager;
import bibliothek.gui.dock.common.perspective.CPerspective;
import bibliothek.gui.dock.common.perspective.CStationPerspective;
import bibliothek.gui.dock.common.perspective.MissingPerspectiveStrategy;
import bibliothek.gui.dock.facile.mode.ModeArea;
import bibliothek.util.Path;

/**
 * A {@link CStation} is an element onto which {@link CDockable}s can be dropped.
 * Each station can, but does not have to be, a {@link CDockable} itself. Stations
 * are added to the {@link CControl} through {@link CControl#addStation(CStation, boolean)}.
 * A station is either a root-station (meaning that it has no parent) or 
 * {@link CDockable}. It is possible to use a {@link CDockable} as root-station.
 * @author Benjamin Sigg
 * @param <S> what kind of internal implementation this station uses
 */
public interface CStation<S extends CommonDockStation<?, ?>> {
    /**
     * Gets the internal representation of this {@link CStation}.
     * @return the internal representation
     */
    public S getStation();
    
    /**
     * If this station is a {@link CDockable} as well, then this method returns
     * the representation of this station as {@link CDockable}.
     * @return this as dockable or <code>null</code>
     */
    public CDockable asDockable();

    /**
     * Returns a new {@link CStationPerspective} that represents <code>this</code> station. This method
     * does not need to set any content of the new perspective. Basically this method just needs to tell
     * the framework what type of {@link CStationPerspective} is needed, not what its content is.
     * @return the new, empty perspective
     * @see #getTypeId()
     */
    public CStationPerspective createPerspective();
    
    /**
     * Tells whether this station is a special working area or not. It is not
     * possible drag a child from a working area if it is registered there, or
     * to drop a child onto a working area if it is not registered there.<br>
     * Working areas must either be registered as root-station (see {@link CControl#addStation(CStation, boolean)})
     * or must be a {@link SingleCDockable}.
     * @return <code>true</code> if this is a working area, <code>false</code>
     * otherwise
     */
    public boolean isWorkingArea();
    
    /**
     * Gets a unique and constant identifier for this station
     * @return the unique identifier
     */
    public String getUniqueId();
    
    /**
     * Gets a unique path denoting the type of this station. This path will be given
     * to {@link MissingPerspectiveStrategy#createStation(String, Path)} when loading a layout
     * as a {@link CPerspective}.
     * @return the type of this station. Paths starting with "dock." are reserved for the
     * framework. A value of <code>null</code> will just be ignored.
     */
    public Path getTypeId();
    
    /**
     * Gets a location which represents directly {@link #getStation()}. 
     * @return the location that will always represent the {@link #getStation() station}
     */
    public CLocation getStationLocation();
    
    /**
     * Creates a {@link CLocation} that can be used to drop a new {@link CDockable} on this station
     * or one of its children stations. This method returns the "optimal spot", where the definition
     * of "optimal spot" is up to the station itself. The default implementations however call
     * {@link CLocationModeManager#getDropLocation(CStation)}. 
     * @return the optimal spot for dropping a new {@link CDockable} or <code>null</code> if this
     * station is not able to calculate the optimal spot (e.g. because the station is not visible)
     * @see CDockable#setLocationsAside(CDockable)
     * @see CDockable#setLocationsAsideFocused()
     */
    public CLocation getDropLocation();
    
    /**
     * Called by {@link CControl} when this {@link CStation} is added or removed.
     * There are two actions which most stations might want to do:<br>
     * <ul>
     * <li>Call one of the <code>add</code> methods for {@link ModeArea}s accessible through the {@link CLocationModeManager}.
     * That will ensure that the station can be used as minimize/normalize/... area.</li>
     * <li>Add a {@link ResizeRequestListener} to {@link CControl} in order to be
     * informed when resize requests are to be handled.</li>
     * </ul>
     * @param access access to the internals of the {@link CControl} that uses
     * this station or <code>null</code> to remove all ties from a control
     */
    public void setControlAccess( CControlAccess access );
}
