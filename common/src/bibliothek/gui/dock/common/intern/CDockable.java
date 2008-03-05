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

import java.awt.Dimension;

import bibliothek.gui.dock.action.DockActionSource;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.ColorMap;
import bibliothek.gui.dock.common.CLocation;
import bibliothek.gui.dock.common.CWorkingArea;
import bibliothek.gui.dock.common.event.CDockablePropertyListener;
import bibliothek.gui.dock.common.event.CDockableStateListener;

/**
 * A basic element representing some {@link java.awt.Component}.
 * @author Benjamin Sigg
 */
public interface CDockable {
	/**
	 * The mode tells how big a {@link CDockable} is.
	 * @author Benjamin Sigg
	 */
	public static enum ExtendedMode{
		/** the dockable is as small as possible */
		MINIMIZED,
		/** the dockable is as big as possible */
		MAXIMIZED,
		/** the dockable has the normal size */
		NORMALIZED,
		/** the dockable is floating in a dialog */
		EXTERNALIZED
	}
	
	/**
	 * Adds a state listener to this dockable, the listener will be informed of
	 * changes of this dockable.
	 * @param listener the new listener
	 */
	public void addCDockableStateListener( CDockableStateListener listener );
	
    /**
     * Adds a property listener to this dockable, the listener will be informed of
     * changes of this dockable.
     * @param listener the new listener
     */
	public void addCDockablePropertyListener( CDockablePropertyListener listener );
	
	/**
	 * Removes a state listener from this dockable.
	 * @param listener the listener to remove
	 */
	public void removeCDockableStateListener( CDockableStateListener listener );

	/**
     * Removes a property listener from this dockable.
     * @param listener the listener to remove
     */
	public void removeCDockablePropertyListener( CDockablePropertyListener listener );
	
	/**
	 * Tells whether this dockable can be minimized by the user.
	 * @return <code>true</code> if this element can be minimized
	 */
	public boolean isMinimizable();
	
	/**
	 * Tells whether this dockable can be maximized by the user.
	 * @return <code>true</code> if this element can be maximized
	 */
	public boolean isMaximizable();
	
	/**
	 * Tells whether this dockable can be externalized by the user.
	 * @return <code>true</code> if this element can be externalized
	 */
	public boolean isExternalizable();
	
	/**
	 * Tells whether this dockable can be combined with another
	 * dockable to create a stack.
	 * @return <code>true</code> if this element can be combined with
	 * another dockable, normally <code>true</code> should be the answer.
	 */
	public boolean isStackable();
	
	/**
	 * Tells whether this dockable can be closed by the user.
	 * @return <code>true</code> if this element can be closed
	 */
	public boolean isCloseable();
	
	/**
	 * Tells whether the size of this dockable should remain the same when
	 * its parent changes the size. This has only effect if the parent can
	 * choose the size of its children. A lock is no guarantee for staying
	 * with the same size, the user still can resize this dockable.
	 * @return <code>true</code> if the size of this dockable should remain
	 * the same during resize events of the parent.
	 */
	public boolean isResizeLocked();
	
	/**
	 * Gets the preferred size of this {@link CDockable}. The preferred size
	 * will be used to resize this <code>CDockable</code> when 
	 * {@link CControl#handleResizeRequests()} is called. There are no guarantees
	 * that the request can be granted, or will be handled at all.<br>
	 * Calling this method should delete the request, so calling this method
	 * twice should have the effect, that the second time <code>null</code> is
	 * returned.
	 * @return the next requested size or <code>null</code>
	 */
	public Dimension getAndClearResizeRequest();
	
	/**
	 * Shows or hides this dockable. If this dockable is not visible and
	 * is made visible, then the framework tries to set its location at
	 * the last known position.<br>
	 * Subclasses should call {@link CControlAccess#show(CDockable)} or
	 * {@link CControlAccess#hide(CDockable)}.
	 * @param visible the new visibility state
	 * @see #isVisible()
	 * @throws IllegalStateException if this dockable can't be made visible
	 */
	public void setVisible( boolean visible );
	
	/**
	 * Tells whether this dockable is currently visible or not. Visibility
	 * means that this dockable is in the tree structure of DockingFrames. Being
	 * in the structure does not imply being visible on the screen. If some
	 * <code>JFrame</code> is not shown, or some <code>DockStation</code> not
	 * properly added to a parent component, then a visible dockable can
	 * be invisible for the user.<br>
	 * Subclasses should return the result of {@link CControlAccess#isVisible(CDockable)}.
	 * @return <code>true</code> if this dockable can be accessed by the user
	 * through a graphical user interface.
	 */
	public boolean isVisible();
	
	/**
	 * Sets the location of this dockable. If this dockable is visible, than
	 * this method will take immediately effect. Otherwise the location will be
	 * stored in a cache and read as soon as this dockable is made visible.<br>
	 * Note that the location can only be seen as a hint, the framework tries
	 * to fit the location as good as possible, but there are no guarantees.
	 * @param location the new location, <code>null</code> is possible, but
	 * will not move the dockable immediately
	 */
	public void setLocation( CLocation location );
	
	/**
	 * Gets the location of this dockable. If this dockable is visible, then
	 * a location will always be returned. Otherwise a location will only
	 * be returned if it just was set using {@link #setLocation(CLocation)}.
	 * @return the location or <code>null</code>
	 */
	public CLocation getLocation();
	
    /**
     * Sets how and where this dockable should be shown. Conflicts with
     * {@link #isExternalizable()}, {@link #isMaximizable()} and {@link #isMinimizable()}
     * will just be ignored.
     * @param extendedMode the size and location
     */
    public void setExtendedMode( ExtendedMode extendedMode );
	
    /**
     * Gets the size and location of this dockable.
     * @return the size and location or <code>null</code> if this dockable
     * is not part of an {@link CControl}.
     */
    public ExtendedMode getExtendedMode();
    
    /**
     * Sets the parent of this dockable. This method can be called by the client
     * or indirectly through {@link #setLocation(CLocation)}.
     * @param area the new parent or <code>null</code>
     */
    public void setWorkingArea( CWorkingArea area );
    
    /**
     * Gets the parent of this dockable, this should be the same as
     * set by the last call of {@link #setWorkingArea(CWorkingArea)}.
     * @return the parent or <code>null</code>
     */
    public CWorkingArea getWorkingArea();
    
    /**
     * Sets the size of this dockable when this dockable is minimized and
     * on a popup window.
     * @param size the size
     */
    public void setMinimizedSize( Dimension size );
    
    /**
     * Gets the size which is used when this dockable is minimzed and
     * on a popup window. If a value below 0 is set, then the default size
     * is used.
     * @return the size
     */
    public Dimension getMinimizedSize();
    
    /**
     * Sets whether this dockable should remain visible when minimized
     * and without focus.
     * @param hold whether to remain visible
     */
    public void setMinimizedHold( boolean hold );
    
    /**
     * Tells whether this dockable remains visible when minimized and 
     * without focus.
     * @return <code>true</code> if this remains visible, <code>false</code>
     * otherwise 
     */
    public boolean isMinimizedHold();
    
	/**
	 * Gets the intern representation of this dockable.
	 * @return the intern representation.
	 */
	public CommonDockable intern();
	
	/**
	 * Sets the {@link CControl} which is responsible for this dockable. Subclasses
	 * must call {@link CControlAccess#link(CDockable, CDockableAccess)} to grant
	 * the <code>CControl</code> access to the internal systems of this
	 * {@link CDockable}. <code>link</code> can also be used to revoke access.
	 * @param control the new control or <code>null</code>
	 */
	public void setControl( CControlAccess control );
	
	/**
	 * Gets the source that contains the close-action.
	 * @return the source
	 */
	public DockActionSource getClose();
	
	/**
	 * Gets a mutable map of colors. Clients can put colors into this map, and
	 * the colors will be presented on the screen by various effects.
	 * @return the map, this has always to be the same object
	 */
	public ColorMap getColors();
	
	/**
	 * Gets the control which is responsible for this dockable. Clients
	 * should not use this method unless they know exactly what they are doing.
	 * @return the control
	 */
	public CControlAccess getControl();
}
