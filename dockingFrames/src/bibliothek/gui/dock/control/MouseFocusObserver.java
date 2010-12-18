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

package bibliothek.gui.dock.control;

import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.event.FocusVetoListener;
import bibliothek.util.FrameworkOnly;
import bibliothek.util.Todo;
import bibliothek.util.Todo.Compatibility;
import bibliothek.util.Todo.Priority;
import bibliothek.util.Todo.Version;

/**
 * A {@link MouseFocusObserver} tracks the movement and actions of the mouse and is responsible for
 * transfering the focus by calling {@link DockController#setFocusedDockable(Dockable, boolean, boolean)}
 * at an appropriate time.
 * @author Benjamin Sigg
 */
@Todo( compatibility=Compatibility.COMPATIBLE, priority=Priority.BUG, target=Version.VERSION_1_1_0,
		description="Dockables moving on ScreenDockStation: should keep focus" )
public interface MouseFocusObserver {
    /**
     * Gets the {@link DockController} whose {@link Dockable}s are tracked by this observer.
     * @return the controller
     */
    public DockController getController();
    
    /**
     * Adds a listener to this controller which can cancel a call to the {@link DockController}.
     * @param listener the new listener
     */
    public void addVetoListener( FocusVetoListener listener );
    
    /**
     * Removes a listener from this controller
     * @param listener the listener to remove
     */
    public void removeVetoListener( FocusVetoListener listener );
    
    /**
     * This method may be called at any time by any component that received 
     * the {@link MouseEvent} <code>event</code>.  This observer may transfer the
     * focus because of this call.<br>
     * If this application runs in a {@link DockController#isRestrictedEnvironment() restricted environment}
     * than any {@link DockStation} of this framework will call this method.
     * @param event the event to check
     */
    public void check( MouseEvent event );
    
    /**
     * This method may be called at any time by any component that received 
     * the {@link MouseWheelEvent} <code>event</code>.  This observer may transfer the
     * focus because of this call.<br>
     * If this application runs in a {@link DockController#isRestrictedEnvironment() restricted environment}
     * than any {@link DockStation} of this framework will call this method.
     * @param event the event to check
     */
    public void check( MouseWheelEvent event );
    
    /**
     * Stops this FocusController. This controller will remove all
     * its listeners and become ready for the garbage collector.<br>
     * This method should not be called by clients. 
     */
    @FrameworkOnly
    public void kill();
}
