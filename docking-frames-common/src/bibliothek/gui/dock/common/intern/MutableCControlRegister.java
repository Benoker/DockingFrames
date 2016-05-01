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

import bibliothek.gui.dock.common.CContentArea;
import bibliothek.gui.dock.common.CControlRegister;
import bibliothek.gui.dock.common.CStation;
import bibliothek.gui.dock.common.CStationContainer;
import bibliothek.gui.dock.common.MultipleCDockable;
import bibliothek.gui.dock.common.SingleCDockable;

/**
 * A {@link CControlRegister} whose contents can be changed.
 * @author Benjamin Sigg
 */
public interface MutableCControlRegister extends CControlRegister {
    /**
     * Adds a new set of {@link CStation}s to this register.
     * @param container the new set of stations, not <code>null</code>
     * @throws IllegalArgumentException if <code>container</code> is already registered or
     * another container with the same unique id was found
     * @throws NullPointerException if <code>container</code> is <code>null</code>
     */
    public void addStationContainer( CStationContainer container );
    
    /**
     * Removes <code>container</code> from this registry. 
     * @param container the container to remove
     * @return <code>true</code> if <code>container</code> was known to this registry and
     * was removed, <code>false</code> otherwise
     */
    public boolean removeStationContainer( CStationContainer container );
    
    /**
     * Gets the default set of {@link CStation}s.
     * @return the container, can be <code>null</code>
     */
    public CContentArea getDefaultContentArea();
    
    /**
     * Sets the default set of {@link CStation}s. One of this {@link CStation}s will be used
     * to show new {@link CDockable}s if they do not have a location set.
     * @param container the new container
     */
    public void setDefaultContentArea( CContentArea container );
    
    /**
     * Adds <code>station</code> to this register.
     * @param station the new station
     */
    public void addStation( CStation<?> station );
    
    /**
     * Removes <code>station</code> from this register.
     * @param station the station to remove
     * @return <code>true</code> if <code>station</code> was removed, <code>false</code>
     * otherwise
     */
    public boolean removeStation( CStation<?> station );

    /**
     * Gets the backup factory for missing {@link SingleCDockable}s.
     * @return the factory, never <code>null</code>
     */
    public CommonSingleDockableFactory getBackupFactory();
    
    /**
     * Adds <code>dockable</code> to this register.
     * @param dockable the new element
     */
    public void addSingleDockable( SingleCDockable dockable );

    /**
     * Searches the one {@link SingleCDockable} whose unique id equals <code>id</code>.
     * @param id some id to search
     * @return a dockable with the same id
     */
    public SingleCDockable getSingleDockable( String id );
    
    /**
     * Adds <code>dockable</code> to this register.
     * @param dockable the new element
     */
    public void addMultipleDockable( MultipleCDockable dockable );
    
    /**
     * Adds <code>factory</code> to this register.
     * @param id the id for the factory
     * @param factory the new factory
     */
    public void putCommonMultipleDockableFactory( String id, CommonMultipleDockableFactory factory );
    
    /**
     * Gets the factory with identifier <code>id</code>.
     * @param id the id of the factory
     * @return the factory or <code>null</code>
     */
    public CommonMultipleDockableFactory getCommonMultipleDockableFactory( String id );
    
    /**
     * Removes the {@link CommonMultipleDockableFactory} with identifier <code>id</code>
     * rom this register.
     * @param id the identifier of the factory
     * @return the factory that was removed or <code>null</code>
     */
    public CommonMultipleDockableFactory removeCommonMultipleDockableFactory( String id );
    
    /**
     * Removes <code>dockable</code> from this register.
     * @param dockable the element to remove
     * @return <code>true</code> if <code>dockable</code> was removed,
     * <code>false</code> if not
     */
    public boolean removeSingleDockable( SingleCDockable dockable );
    
    /**
     * Removes <code>dockable</code> from this register.
     * @param dockable the element to remove
     * @return <code>true</code> if <code>dockable</code> was removed,
     * <code>false</code> if not
     */    
    public boolean removeMultipleDockable( MultipleCDockable dockable );
}
