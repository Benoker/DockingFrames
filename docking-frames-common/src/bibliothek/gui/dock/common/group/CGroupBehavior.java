/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2010 Benjamin Sigg
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
package bibliothek.gui.dock.common.group;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.common.intern.CDockable;
import bibliothek.gui.dock.common.mode.ExtendedMode;
import bibliothek.gui.dock.facile.mode.LocationMode;
import bibliothek.gui.dock.facile.mode.LocationModeManager;

/**
 * A {@link CGroupBehavior} allows to define groups of {@link CDockable}. Groups normally
 * act together when changing the {@link LocationMode}: e.g. if one {@link CDockable} is minimized, all the other 
 * {@link CDockable}s follow.
 * @author Benjamin Sigg
 */
public interface CGroupBehavior {
	/**
	 * A behavior that moves only one {@link Dockable} at a time.
	 */
	public static final CGroupBehavior TOPMOST = new TopMostGroupBehavior();
	
	/**
	 * A behavior that moves around entire stacks of {@link Dockable}s.
	 */
	public static final CGroupBehavior STACKED = new StackGroupBehavior();
	
	/**
	 * Calculates how the mode of <code>dockable</code> has to be changed such that it matches <code>target</code>. 
	 * Please note that some modules use directly {@link #getGroupElement(LocationModeManager, Dockable, ExtendedMode)} and do never call
	 * this method. 
	 * @param manager a manager which may be asked for additional information
	 * @param dockable the element that was clicked by the user
	 * @param target the extended mode intended for <code>dockable</code>
	 * @return the operation to execute, may be <code>null</code>
	 */
	public CGroupMovement prepare( LocationModeManager<? extends LocationMode> manager, Dockable dockable, ExtendedMode target );

    /**
     * Gets the element whose location or mode must be changed in order to apply
     * <code>mode</code> to <code>dockable</code>. Normally <code>dockable</code> itself
     * is returned, or a parent {@link DockStation} of <code>dockable</code>.
     * @param manager a manager which may be asked for additional information
     * @param dockable some element, not <code>null</code>
     * @param mode the target mode
     * @return the element that must be repositioned, might be <code>dockable</code>
     * itself, not <code>null</code>
     */
	public Dockable getGroupElement( LocationModeManager<? extends LocationMode> manager, Dockable dockable, ExtendedMode mode );
	
    /**
     * Gets the element which would replace <code>old</code> if <code>old</code> is currently
     * in <code>mode</code>, and <code>dockable</code> is or will not be in <code>mode</code>.<br>
     * @param manager a manager which may be asked for additional information
     * @param old some element
     * @param dockable some element, might be <code>old</code>
     * @param mode the mode in which <code>old</code> is
     * @return the element which would be maximized if <code>dockable</code> is
     * no longer in <code>mode</code>, can be <code>null</code>
     */
	public Dockable getReplaceElement( LocationModeManager<? extends LocationMode> manager, Dockable old, Dockable dockable, ExtendedMode mode );
	
	/**
	 * Tells whether the actions of <code>dockable</code> for mode <code>mode</code> should be
	 * shown on <code>station</code> too.
	 * @param manager a manager which may be asked for additional information
	 * @param station the parent of <code>dockable</code>
	 * @param dockable the element whose actions will be shown
	 * @param mode the mode for which the actions are requested
	 * @return <code>true</code> if the actions should be forwarded
	 */
	public boolean shouldForwardActions( LocationModeManager<? extends LocationMode> manager, DockStation station, Dockable dockable, ExtendedMode mode );
}
