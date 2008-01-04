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
package bibliothek.gui.dock.facile.intern;

import bibliothek.gui.dock.action.DockActionSource;
import bibliothek.gui.dock.facile.FControl;
import bibliothek.gui.dock.facile.FLocation;
import bibliothek.gui.dock.facile.FWorkingArea;
import bibliothek.gui.dock.facile.event.FDockableListener;

/**
 * A basic element representing some {@link java.awt.Component}.
 * @author Benjamin Sigg
 */
public interface FDockable {
	/**
	 * The mode tells how big a {@link FDockable} is.
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
	 * Adds a listener to this dockable, the listener will be informed of
	 * changes of this dockable.
	 * @param listener the new listener
	 */
	public void addFDockableListener( FDockableListener listener );
	
	/**
	 * Removes a listener from this dockable.
	 * @param listener the listener to remove
	 */
	public void removeFDockableListener( FDockableListener listener );
	
	/**
	 * Tells whether this dockable can be minimized by the user.
	 * @return <code>true</code> if this element can be minimized
	 */
	public abstract boolean isMinimizable();
	
	/**
	 * Tells whether this dockable can be maximized by the user.
	 * @return <code>true</code> if this element can be maximized
	 */
	public abstract boolean isMaximizable();
	
	/**
	 * Tells whether this dockable can be externalized by the user.
	 * @return <code>true</code> if this element can be externalized
	 */
	public abstract boolean isExternalizable();
	
	/**
	 * Tells whether this dockable can be combined with another
	 * dockable to create a stack.
	 * @return <code>true</code> if this element can be combined with
	 * another dockable, normally <code>true</code> should be the answer.
	 */
	public abstract boolean isStackable();
	
	/**
	 * Tells whether this dockable can be closed by the user.
	 * @return <code>true</code> if this element can be closed
	 */
	public abstract boolean isCloseable();
	
	/**
	 * Shows or hides this dockable. If this dockable is not visible and
	 * is made visible, then the framework tries to set its location at
	 * the last known position.
	 * @param visible the new visibility state
	 */
	public void setVisible( boolean visible );
	
	/**
	 * Tells whether this dockable is currently visible or not.
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
	public void setLocation( FLocation location );
	
	/**
	 * Gets the location of this dockable. If this dockable is visible, then
	 * a location will always be returned. Otherwise a location will only
	 * be returned if it just was set using {@link #setLocation(FLocation)}.
	 * @return the location or <code>null</code>
	 */
	public FLocation getLocation();
	
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
     * is not part of an {@link FControl}.
     */
    public ExtendedMode getExtendedMode();
    
    /**
     * Sets the parent of this dockable. This method can be called by the client
     * or indirectly through {@link #setLocation(FLocation)}.
     * @param area the new parent or <code>null</code>
     */
    public void setWorkingArea( FWorkingArea area );
    
    /**
     * Gets the parent of this dockable, this should be the same as
     * set by the last call of {@link #setWorkingArea(FWorkingArea)}.
     * @return the parent or <code>null</code>
     */
    public FWorkingArea getWorkingArea();
    
	/**
	 * Gets the intern representation of this dockable.
	 * @return the intern representation.
	 */
	public FacileDockable intern();
	
	/**
	 * Sets the {@link FControl} which is responsible for this dockable. Subclasses
	 * must call {@link FControlAccess#link(FDockable, FDockableAccess)} to grant
	 * the <code>FControl</code> access to the internal systems of this
	 * {@link FDockable}. <code>link</code> can also be used to revoke access.
	 * @param control the new control or <code>null</code>
	 */
	public void setControl( FControlAccess control );
	
	/**
	 * Gets the source that contains the close-action.
	 * @return the source
	 */
	public DockActionSource getClose();
	
	/**
	 * Gets the control which is responsible for this dockable.
	 * @return the control
	 */
	public FControlAccess getControl();
}
