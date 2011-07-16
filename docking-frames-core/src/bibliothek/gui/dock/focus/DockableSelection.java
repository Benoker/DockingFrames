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
package bibliothek.gui.dock.focus;

import java.awt.Component;

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;

/**
 * A {@link DockableSelection} is a {@link Component} which shows some or all
 * {@link Dockable}s of a {@link DockController}. The user can select one of
 * the {@link Dockable} either by using the mouse or the keyboard.
 * @author Benjamin Sigg
 */
public interface DockableSelection {
    /**
     * Checks whether <code>controller</code> has at least two different
     * {@link Dockable}s which would be shown on this {@link DockableSelection}.
     * @param controller the controller which might not have any children at all
     * @return <code>true</code> if opening this {@link DockableSelection} gives
     * the user a choice, <code>false</code> if this selection would not show
     * anything
     */
    public boolean hasChoices( DockController controller );
    
    /**
     * Gets the component which represents the selection. The result of this
     * method should always be the same.
     * @return the representation of this selection
     */
    public Component getComponent();
    
    /**
     * Called when a new selection is about to happen.
     * @param controller the controller for which the selection will happen
     */
    public void open( DockController controller );
    
    /**
     * Called when the current selection is no longer needed either because
     * a selection was done or was canceled.
     */
    public void close();
    
    /**
     * Adds a listener that gets informed when the state of this selection changes.
     * @param listener the new listener
     */
    public void addDockableSelectionListener( DockableSelectionListener listener );
    
    /**
     * Removes a listener that gets informed when the state of this selection changes.
     * @param listener the new listener
     */
    public void removeDockableSelectionListener( DockableSelectionListener listener );
}
