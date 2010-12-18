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
package bibliothek.gui.dock.control;

import java.awt.Component;

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DockElementRepresentative;
import bibliothek.gui.dock.event.DockableFocusListener;
import bibliothek.gui.dock.event.FocusVetoListener;
import bibliothek.gui.dock.event.FocusVetoListener.FocusVeto;


/**
 * The {@link FocusController} is responsible for transfering focus between {@link Dockable}s.
 * @author Benjamin Sigg
 */
public interface FocusController {
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
     * Adds a listener to this controller, the listener will be informed when
     * the focused {@link Dockable} changes.
     * @param listener the new listener
     */
    public void addDockableFocusListener( DockableFocusListener listener );

    /**
     * Removes a listener from this controller.
     * @param listener the listener to remove
     */
    public void removeDockableFocusListener( DockableFocusListener listener );
    
    /**
     * Sets the strategy which will be used to focus components.
     * @param strategy the new strategy, can be <code>null</code>
     */
    public void setStrategy( FocusStrategy strategy );
    
    /**
     * Gets the strategy that selects the {@link Component}s to focus.
     * @return the strategy, can be <code>null</code>
     */
    public FocusStrategy getStrategy();
    
    /**
     * Gets the {@link Dockable} which is currently focused.
     * @return the focused element or <code>null</code>
     */
    public Dockable getFocusedDockable();
    
    /**
     * Tells whether one of the methods which change the focus is currently
     * running, or not. If the result is <code>true</code>, noone should
     * change the focus.
     * @return <code>true</code> if the focus is currently changing
     */
    public boolean isOnFocusing();
    
    /**
     * Ensures that a title or a {@link Component} of the currently
     * {@link #getFocusedDockable() focused Dockable} really
     * has the focus.
     */
    public void ensureFocusSet();
    
    /**
     * Checks whether <code>source</code> can be used to select the next focused {@link Dockable}.
     * @param source the element which may be focused
     * @return whether the focus can be transfered, a value of <code>null</code> indicates that 
     * <code>source</code> does not represent a {@link Dockable}
     */
    public FocusVeto checkFocusedDockable( DockElementRepresentative source );
    
    /**
     * Sets the {@link Dockable} which should have the focus.
     * @param the item to focs, may be <code>null</code>
     * @param force <code>true</code> if this controller must ensure
     * that all properties are correct, <code>false</code> if some
     * optimations are allowed. Clients normally can set this argument
     * to <code>false</code>.
     * @param ensureFocusSet whether to ensure that the focus is set correctly
     * or not.
     * @return whether focus could be transfered, a value of <code>null</code> indicates that {@link #isOnFocusing()} returned
     * <code>true</code> and the call was ignored
     */
    public FocusVeto setFocusedDockable( DockElementRepresentative source, boolean force, boolean ensureFocusSet );
}
