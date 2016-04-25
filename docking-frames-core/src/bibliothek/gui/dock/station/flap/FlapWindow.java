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

package bibliothek.gui.dock.station.flap;

import java.awt.Component;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.FlapDockStation;
import bibliothek.gui.dock.station.DockableDisplayer;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.gui.dock.title.DockTitleVersion;

/**
 * This window pops up if the user presses one of the buttons of a 
 * {@link FlapDockStation}. The window shows one {@link Dockable}. How exactly
 * to display the {@link Dockable} is up to the window.
 */
public interface FlapWindow {
	/**
	 * Shows or hides this window.
	 * @param flag whether to show this window
	 */
    public void setWindowVisible( boolean flag );
    
    /**
     * Tells whether this window is shown or hidden.
     * @return <code>true</code> if the window is shown
     */
    public boolean isWindowVisible();
    
    /**
     * Tells this window that is should redraw its entire content.
     */
    public void repaint();
    
    /**
     * Informs this window that it is no longer used by its owner
     * and will never be used again.
     */
    public void destroy();
    
    /**
     * Gets the current boundaries of this window in screen coordinates.
     * @return the boundaries
     */
    public Rectangle getWindowBounds();
    
    /**
     * Tells whether this window contains <code>point</code> which is a 
     * point of the screen.
     * @param point a point on the screen
     * @return <code>true</code> if this window contains <code>point</code>
     */
    public boolean containsScreenPoint(Point point);
    
    /**
     * Sets information where a {@link Dockable} will be dropped. This window
     * may draw some markings if the drop-information matches the location of
     * this windows {@link Dockable}.
     * @param dropInfo the information or <code>null</code>
     */
    public void setDropInfo( FlapDropInfo dropInfo );
    
    /**
     * Tells this window whether a drag and drop operation is currently removing its child.
     * @param removal whether the child of this window is removed
     */
    public void setRemoval( boolean removal );
    
    /**
     * Tells this window how to create a title for any {@link Dockable} that
     * may be shown on it.
     * @param title the title or <code>null</code>
     */
    public void setDockTitle( DockTitleVersion title );
    
    /**
     * Gets the title which is currently displayed
     * @return the title or <code>null</code>
     */
    public DockTitle getDockTitle();
    
    /**
     * Sets the {@link Dockable} which will be shown on this window.
     * @param dockable The <code>Dockable</code> or <code>null</code>
     */
    public void setDockable( Dockable dockable );
    
    /**
     * Gets the {@link Dockable} which is shown on this window.
     * @return The {@link Dockable} or <code>null</code>
     */
    public Dockable getDockable();
    
    /**
     * Gets the {@link DockableDisplayer} which manages {@link #getDockable() the dockable}.
     * @return the displayer or <code>null</code>
     */
    public DockableDisplayer getDisplayer();
    
    /**
     * Informs this {@link FlapWindow} about the {@link DockController} in whose realm
     * @param controller the controller or <code>null</code>
     */
    public void setController( DockController controller );
    
    /**
     * Makes a guess how big the insets around the current {@link Dockable}
     * of this window are.
     * @return a guess of the insets
     */
    public Insets getDockableInsets();    
    
    /**
     * Recalculates the size and the location of this window such that it
     * matches the size and location of its parent {@link FlapDockStation}.
     * @see FlapDockStation#getWindowSize(Dockable)
     * @see FlapDockStation#getWindowMinSize()
     * @see FlapDockStation#getExpansionBounds()
     */
    public void updateBounds();
    
    /**
     * Gets the root {@link Component} of this {@link FlapWindow}
     * @return the root component, may be <code>null</code> after {@link #destroy()} was called
     */
    public Component getComponent();
}
