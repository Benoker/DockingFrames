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
package bibliothek.gui.dock.control.focus;

import java.awt.Component;

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DockElementRepresentative;
import bibliothek.gui.dock.event.DockableFocusListener;
import bibliothek.gui.dock.event.FocusVetoListener;
import bibliothek.gui.dock.event.FocusVetoListener.FocusVeto;
import bibliothek.util.Todo;
import bibliothek.util.Todo.Compatibility;
import bibliothek.util.Todo.Version;


/**
 * The {@link FocusController} is responsible for transferring focus between {@link Dockable}s.
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
     * Temporarily disables this {@link FocusController}. Any call that would lead to a change
     * in the focus is silently ignored.
     */
    public void freezeFocus();
    
    /**
     * Re-enables this {@link FocusController} after it was temporarily disabled.
     */
    public void meltFocus();
    
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
     * running, or not. If the result is <code>true</code>, no-one should
     * change the focus.
     * @return <code>true</code> if the focus is currently changing
     */
    public boolean isOnFocusing();
    
    /**
     * Checks whether <code>source</code> can be used to select the next focused {@link Dockable}.
     * @param source the element which may be focused
     * @return whether the focus can be transferred, a value of <code>null</code> indicates that
     * <code>source</code> does not represent a {@link Dockable}
     */
    public FocusVeto checkFocusedDockable( DockElementRepresentative source );
    
    /**
     * Ensures that a title or a {@link Component} of the currently
     * {@link #getFocusedDockable() focused Dockable} really
     * has the focus.
     * @param dockableOnly if <code>true</code>, then only the {@link Dockable} itself
     * should be focused
     */
    public void ensureFocusSet( boolean dockableOnly );
    
    /**
     * Sets the {@link Dockable} which should have the focus.
     * @param source the item to focus, may be <code>null</code>
     * @param component the {@link Component} which triggered this call for example because the user clicked with the mouse on it. 
     * This method can assume that the focus will automatically be transferred to <code>component</code> by the Swing framework itself.
     * Can be <code>null</code>, in which case this method decides on its own which {@link Component} to focus. This method may or may
     * not do sanity checks concerning <code>component</code>. An invalid argument will silently be ignored and treated 
     * as if it would be <code>null</code>.
     * @param force <code>true</code> if this controller must ensure
     * that all properties are correct, <code>false</code> if some
     * optimizations are allowed. Clients normally can set this argument
     * to <code>false</code>.
     * @param ensureFocusSet if <code>true</code>, then this method should make sure that either <code>focusedDockable</code>
     * itself or one of its {@link DockElementRepresentative} is the focus owner 
     * @param ensureDockableFocused  if <code>true</code>, then this method should make sure that <code>focusedDockable</code>
     * is the focus owner. This parameter is stronger that <code>ensureFocusSet</code>
     * @return whether focus could be transferred, a value of <code>null</code> indicates that {@link #isOnFocusing()} returned
     * <code>true</code> and the call was ignored
     * @deprecated this method will be replaced by {@link #focus(FocusRequest)}
     */
    @Deprecated
    @Todo( compatibility=Compatibility.BREAK_MAJOR, description="remove this method", priority=Todo.Priority.ENHANCEMENT,
		target=Version.VERSION_1_1_3)
    public FocusVeto setFocusedDockable( DockElementRepresentative source, Component component, boolean force, boolean ensureFocusSet, boolean ensureDockableFocused );
    
    /**
     * Sets the {@link Dockable} which should have the focus.
     * @param request information about the {@link Dockable} that should receive the focus, must not be <code>null</code> 
     */
    public void focus( FocusRequest request );
    
    /**
     * After the currently executed {@link FocusRequest} is completed, or if there is currently no {@link FocusRequest} running,
     * <code>run</code> is executed. If this controller is {@link #freezeFocus() frozen}, then <code>run</code> is executed
     * Immediately.
     * @param run some code to execute once a new {@link Dockable} has been focused
     * @see #focus(FocusRequest)
     */
    public void onFocusRequestCompletion( Runnable run );
}
