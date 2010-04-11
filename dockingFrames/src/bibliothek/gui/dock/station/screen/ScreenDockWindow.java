/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2008 Benjamin Sigg
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
package bibliothek.gui.dock.station.screen;

import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.ScreenDockStation;
import bibliothek.gui.dock.station.DockableDisplayer;
import bibliothek.gui.dock.station.StationChildHandle;
import bibliothek.gui.dock.title.DockTitle;

/**
 * A {@link ScreenDockWindow} is used by a {@link ScreenDockStation} to show
 * a {@link Dockable} on the screen. Subclasses are free to show the {@link Dockable}
 * in any way they like, however subclasses are encouraged to use a 
 * {@link StationChildHandle} to manage displayers and title.
 * @author Benjamin Sigg
 */
public interface ScreenDockWindow {
    /**
     * Sets the controller in whose realm this window will be used. This
     * method will be called after the controller of the owning {@link ScreenDockStation}
     * was set, so {@link ScreenDockStation#getController()} will always
     * return the same value as <code>controller</code>. This also implies
     * that any method of the station called from this method already uses the
     * new controller.
     * @param controller the new controller, can be <code>null</code>
     */
    public void setController( DockController controller );
    
    /**
     * Sets the {@link Dockable} which should be shown on this window.
     * @param dockable the new element, can be <code>null</code> to remove
     * an old <code>Dockable</code>
     */
    public void setDockable( Dockable dockable );
    
    /**
     * Gets the {@link Dockable} which is currently shown in this window.
     * @return the current element, can be <code>null</code>
     * @see #setDockable(Dockable)
     */
    public Dockable getDockable();
    
    /**
     * Called when this window should become the focus owner and be shown
     * at the most prominent location.
     */
    public void toFront();
    
//    /**
//     * Changes the mode of this window to fullscreen or to normal.
//     * @param fullscreen the new state
//     */
//    public void setFullscreen( boolean fullscreen );
//    
//    /**
//     * Tells whether this window is in fullscreen mode or not.
//     * @return <code>true</code> if fullscreen mode is active
//     */
//    public boolean isFullscreen();
    
    /**
     * Changes the visibility state of this window.
     * @param visible the new state
     */
    public void setVisible( boolean visible );
    
    /**
     * Informs this window that it is no longer used by the station
     * and will never be used again.
     */
    public void destroy();
    
    /**
     * Sets whether this window should paint some additional markings which
     * indicate that a {@link Dockable} is about to be dropped onto it.<br>
     * Subclasses should use {@link ScreenDockStation#getPaint()} to get
     * an algorithm that paints.
     * @param paint <code>true</code> if something should be painted,
     * <code>false</code> otherwise
     */
    public void setPaintCombining( boolean paint );
    
    /**
     * Gets the boundaries of the window.
     * @return the boundaries
     */
    public Rectangle getWindowBounds();
    
    /**
     * Sets the bounds the window is supposed to have. This method should
     * use {@link ScreenDockStation#getBoundaryRestriction()} to check the validity
     * of the new bounds.
     * @param bounds the new location and size
     */
    public void setWindowBounds( Rectangle bounds );
    
    /**
     * Ensures the correctness of the boundaries of this window. This method
     * should use {@link ScreenDockStation#getBoundaryRestriction()} to do so. 
     */
    public void checkWindowBounds();
    
    /**
     * Forces this window to update the boundaries of its children.
     */
    public void validate();
    
    /**
     * Gets the distances between the edges of the window and the edges of
     * the {@link Dockable}. This is only an estimate and does not have
     * to be correct. Implementations using {@link DockableDisplayer} should
     * call {@link DockableDisplayer#getDockableInsets()} as well.
     * @return the insets, not <code>null</code>
     */
    public Insets getDockableInsets();
    
    /**
     * Gets an offset that will be subtracted from the location when
     * moving the window around. The offset should be equal to the point
     * 0/0 on the {@link DockTitle} of the {@link Dockable} shown in this
     * window. The value <code>null</code> can be returned to indicate
     * that such an offset is not available. 
     * @return the offset or <code>null</code>
     */
    public Point getOffsetMove();
    
    /**
     * Gets an offset that will be added to the location when
     * dropping a window.<br>
     * A value of <code>null</code> indicates that no such offset is
     * available.
     * @return the offset or <code>null</code>
     */
    public Point getOffsetDrop();
    
    /**
     * Checks what would happen if a {@link Dockable} is dropped at point
     * <code>x/y</code>. 
     * @param x an x coordinate in the screen
     * @param y an y coordinate in the screen
     * @return <code>true</code> if dropping a <code>Dockable</code> at
     * <code>x/y</code> should lead to a combination of the dropped element
     * and the element in this window, <code>false</code> otherwise
     */
    public boolean inCombineArea( int x, int y );
}
