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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.ScreenDockStation;
import bibliothek.gui.dock.station.DockableDisplayer;
import bibliothek.gui.dock.station.StationChildHandle;
import bibliothek.gui.dock.station.screen.magnet.MagnetController;
import bibliothek.gui.dock.station.support.CombinerTarget;
import bibliothek.gui.dock.title.DockTitle;

/**
 * A {@link ScreenDockWindow} is used by a {@link ScreenDockStation} to show
 * a {@link Dockable} on the screen. Subclasses are free to show the {@link Dockable}
 * in any way they like, however subclasses are encouraged to use a 
 * {@link StationChildHandle} to manage displayers and title.<br>
 * New implementations of {@link ScreenDockWindow} may require the implementation of a {@link ScreenDockFullscreenStrategy}
 * as well.<br>
 * {@link ScreenDockWindow}s offering users a way to modify the size or location should call
 * {@link ScreenDockStation#getMagnetController()} to gain access to the {@link MagnetController}. This controller
 * can be used to calculate magnetic attraction between two {@link ScreenDockWindow}s.
 * @author Benjamin Sigg
 */
public interface ScreenDockWindow {
	/**
	 * Adds a listener to this window, the listener has to be informed about changes of this window.
	 * @param listener the new listener
	 */
	public void addScreenDockWindowListener( ScreenDockWindowListener listener );
	
	/**
	 * Removes a listener from this window.
	 * @param listener the listener to remove
	 */
	public void removeScreenDockWindowListener( ScreenDockWindowListener listener );
	
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
     * Gets the station which owns this window.
     * @return the owner, not <code>null</code>
     */
    public ScreenDockStation getStation();
    
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
     * Gets the {@link DockableDisplayer} which manages {@link #getDockable() the dockable}.
     * @return the displayer or <code>null</code>
     */
    public DockableDisplayer getDockableDisplayer();
    
    /**
     * Gets the center of the title of this window. In general the center of
     * the title should remain visible all the time.
     * @return the center, can be <code>null</code>
     */
    public Point getTitleCenter();
    
    /**
     * Called when this window should become the focus owner and be shown
     * at the most prominent location.
     */
    public void toFront();
    
    /**
     * Tells this window what strategy to use for handling fullscreen mode.
     * @param strategy the strategy
     */
    public void setFullscreenStrategy( ScreenDockFullscreenStrategy strategy );
    
    /**
     * Changes the mode of this window to fullscreen or to normal. This method
     * should call {@link ScreenDockFullscreenStrategy#setFullscreen(ScreenDockWindow, boolean)}, 
     * subclasses may execute additional code.
     * @param fullscreen the new state
     */
    public void setFullscreen( boolean fullscreen );
    
    /**
     * Tells whether this window is in fullscreen mode or not.  This method should
     * call {@link ScreenDockFullscreenStrategy#isFullscreen(ScreenDockWindow)}, subclasses
     * may execute additional checks.
     * @return <code>true</code> if fullscreen mode is active
     */
    public boolean isFullscreen();
    
    /**
     * Changes the visibility state of this window.
     * @param visible the new state
     */
    public void setVisible( boolean visible );
    
    /**
     * Tells whether this window is visible or not.
     * @return the visibility state
     */
    public boolean isVisible();
    
    /**
     * Tells this window that it should try not to steal the focus if possible.
     * @param prevent whether to attempt to prevent focus stealing
     */
    public void setPreventFocusStealing( boolean prevent );
    
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
     * @param target if <code>null</code> then nothing should be painted, otherwise
     * the method {@link CombinerTarget#paint(java.awt.Graphics, java.awt.Component, bibliothek.gui.dock.station.StationPaint, Rectangle, Rectangle)}
     * should be called
     */
    public void setPaintCombining( CombinerTarget target );
    
    /**
     * Informs this window that a drag and drop operation is in progress, and that the child of
     * this window may be removed in the near future.
     * @param removal whether the operation is in progress
     */
    public void setPaintRemoval( boolean removal );
    
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
     * Sets the boundaries this window should use if not in fullscreen mode. This boundaries
     * need to be stored but must not be applied. This property is intended to be used by
     * a {@link ScreenDockFullscreenStrategy} and is usually set to <code>null</code> if this
     * window is not in fullscreen mode.
     * @param bounds the normal bounds, can be <code>null</code>
     */
    public void setNormalBounds( Rectangle bounds );
    
    /**
     * Gets the boundaries this window should use if not in fullscreen mode.
     * @return the boundaries, can be <code>null</code>
     */
    public Rectangle getNormalBounds();
    
    /**
     * Gets the minimum size this window should have. Usually these boundaries
     * are used by a {@link BoundaryRestriction}, but there are no guarantees that
     * the window is not made smaller than this size.
     * @return the minimum size
     */
    public Dimension getMinimumWindowSize();
    
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
     * Gets the root {@link Component} of this window.
     * @return the root component
     */
    public Component getComponent();
    
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
    
    /**
     * Checks whether at <code>x/y</code> there is a title.
     * @param x an x coordinate in the screen
     * @param y an y coordinate in the screen
     * @return <code>true</code> if there is a title, <code>false</code>
     * otherwise
     */
    public boolean inTitleArea( int x, int y );
    
    /**
     * Tells whether the point <code>x/y</code> is over this window or not.
     * @param x an x coordinate in the screen
     * @param y an y coordinate in the screen
     * @return <code>true</code> if <code>this</code> window is under
     * <code>x/y</code>, <code>false</code> otherwise
     */
    public boolean contains( int x, int y );
}
