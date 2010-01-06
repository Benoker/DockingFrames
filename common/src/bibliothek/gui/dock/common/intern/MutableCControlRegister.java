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

import java.util.List;

import bibliothek.gui.dock.common.CContentArea;
import bibliothek.gui.dock.common.CControlRegister;
import bibliothek.gui.dock.common.CStation;
import bibliothek.gui.dock.common.MultipleCDockable;
import bibliothek.gui.dock.common.SingleCDockable;

/**
 * A {@link CControlRegister} whose contents can be changed.
 * @author Benjamin Sigg
 */
public interface MutableCControlRegister extends CControlRegister {
    /**
     * Gets an unmodifiable list of all {@link CContentArea}s known 
     * to this register. The list might or might not be updated when the contents
     * of this register change.
     * @return the unmodifiable list
     */
    public List<CContentArea> getContentAreas();
    
    /**
     * Adds a new content area to this register.
     * @param area the new area
     * @throws IllegalArgumentException if <code>area</code> is already known
     * to this register
     * @throws NullPointerException if <code>area</code> is <code>null</code>
     */
    public void addContentArea( CContentArea area );
    
    /**
     * Removes <code>area</code> from this register
     * @param area the area to remove
     * @return <code>true</code> if <code>area</code> was known to this register
     * before, <code>false</code> if <code>area</code> could not be removed
     * because it was not in the list
     */
    public boolean removeContentArea( CContentArea area );
    
    /**
     * Gets the default content area.
     * @return the area, can be <code>null</code>
     */
    public CContentArea getDefaultContentArea();
    
    /**
     * Sets the default content area.
     * @param area the new area
     */
    public void setDefaultContentArea( CContentArea area );
    
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

    /**
     * Transforms an identifier to an identifier for a {@link SingleCDockable}.<br>
     * Note that this method must never create an identifier that passes 
     * {@link #isMultiId(String)}.
     * @param id some identifier
     * @return an identifier marked as being for a {@link SingleCDockable}
     */
    public String toSingleId( String id );
    
    /**
     * Checks whether <code>id</code> could be created by {@link #toSingleId(String)}.
     * @param id the id to check
     * @return <code>true</code> if there is an input for {@link #toSingleId(String)}
     * that would result in <code>id</code>
     */
    public boolean isSingleId( String id );
    
    /**
     * Undoes the changes of {@link #toSingleId(String)}. It must be <code>true</code>
     * that <code>singleToNormalId( toSingleId( id )) = id</code>. The behavior
     * of this method is unspecified if {@link #isSingleId(String)} returns
     * <code>false</code> for <code>id</code>.
     * @param id some id create by {@link #toSingleId(String)}.
     * @return the original id
     */
    public String singleToNormalId( String id );
    
    /**
     * Transforms an identifier to an identifier for a {@link MultipleCDockable}.<br>
     * Note that this method must never create an identifier that passes 
     * {@link #isSingleId(String)}.
     * @param id some identifier
     * @return an identifier marked as being for a {@link MultipleCDockable}
     */
    public String toMultiId( String id );
    
    /**
     * Checks whether <code>id</code> could be created by {@link #toMultiId(String)}.
     * @param id the id to check
     * @return <code>true</code> if there is an input for {@link #toMultiId(String)}
     * that would result in <code>id</code>
     */
    public boolean isMultiId( String id );
    
    /**
     * Undoes the changes of {@link #toMultiId(String)}. It must be <code>true</code>
     * that <code>multiToNormalId( toMultiId( id )) = id</code>. The behavior
     * of this method is unspecified if {@link #isMultiId(String)} returns
     * <code>false</code> for <code>id</code>.
     * @param id some id create by {@link #toMultiId(String)}.
     * @return the original id
     */
    public String multiToNormalId( String id );
}
