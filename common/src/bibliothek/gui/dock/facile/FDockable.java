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
package bibliothek.gui.dock.facile;

import bibliothek.gui.dock.facile.intern.FControlAccess;
import bibliothek.gui.dock.facile.intern.FacileDockable;

/**
 * A frame that shows a {@link java.awt.Component}, has a title, an icon
 * and can take various sizes and locations.
 * @author Benjamin Sigg
 */
public class FDockable {
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
	
	/** whether this dockable can be minimized */
	private boolean minimizable;
	/** whether this dockable can be maximized */
	private boolean maximizable;
	/** whether this dockable can be put into a dialog */
	private boolean externalizable;
	/** whether this dockable can be closed by the user */
	private boolean closeable;
	
	/** the size and location of this dockable */
	private ExtendedMode extendedMode = ExtendedMode.NORMALIZED;
	
	/** the graphical representation of this dockable */
	private FacileDockable dockable;
	
	/** the control managing this dockable */
	private FControlAccess control;
	
	/**
	 * Creates a new dockable
	 */
	public FDockable(){
		dockable = new FacileDockable( this );
	}
	
	/**
	 * Tells whether this dockable can be minimized by the user.
	 * @return <code>true</code> if this element can be minimized
	 */
	public boolean isMinimizable(){
		return minimizable;
	}
	
	/**
	 * Sets whether the user can minimize this dockable.
	 * @param minimizable <code>true</code> if the user can minimize this element
	 */
	public void setMinimizable( boolean minimizable ){
		this.minimizable = minimizable;
	}
	
	/**
	 * Tells whether this dockable can be maximized by the user.
	 * @return <code>true</code> if this element can be maximized
	 */
	public boolean isMaximizable(){
		return maximizable;
	}
	
	/**
	 * Sets whether the user can maximize this dockable.
	 * @param maximizable <code>true</code> if the user can maximize this element
	 */
	public void setMaximizable( boolean maximizable ){
		this.maximizable = maximizable;
	}
	
	
	/**
	 * Tells whether this dockable can be externalized by the user.
	 * @return <code>true</code> if this element can be externalized
	 */
	public boolean isExternalizable(){
		return externalizable;
	}
	
	/**
	 * Sets whether the user can externalize this dockable.
	 * @param externalizable <code>true</code> if the user can externalize this element
	 */
	public void setExternalizable( boolean externalizable ){
		this.externalizable = externalizable;
	}
	
	
	/**
	 * Tells whether this dockable can be closed by the user.
	 * @return <code>true</code> if this element can be closed
	 */
	public boolean isCloseable(){
		return closeable;
	}
	
	/**
	 * Sets whether the user can close this dockable.
	 * @param closeable <code>true</code> if the user can close this element
	 */
	public void setCloseable( boolean closeable ){
		this.closeable = closeable;
	}
	
	/**
	 * Shows or hides this dockable. If this dockable is not visible and
	 * is made visible, then the framework tries to set its location at
	 * the last known position.
	 * @param visible the new visibility state
	 */
	public void setVisible( boolean visible ){
		if( control != null ){
			if( visible )
				control.show( this );
			else
				control.hide( this );
		}
	}
	
	/**
	 * Tells whether this dockable is currently visible or not.
	 * @return <code>true</code> if this dockable can be accessed by the user
	 * through a graphical user interface.
	 */
	public boolean isVisible(){
		if( control == null )
			return false;
		else
			return control.isVisible( this );
	}
	
	/**
	 * Sets how and where this dockable should be shown.
	 * @param extendedMode the size and location
	 */
	public void setExtendedMode( ExtendedMode extendedMode ){
		if( extendedMode == null )
			throw new NullPointerException( "extendedMode must not be null" );
		
		this.extendedMode = extendedMode;
	}
	
	/**
	 * Gets the size and location of this dockable.
	 * @return the size and location
	 */
	public ExtendedMode getExtendedMode(){
		return extendedMode;
	}

	/**
	 * Gets the intern representation of this dockable.
	 * @return the intern representation.
	 */
	public FacileDockable getDockable(){
		return dockable;
	}
	
	/**
	 * Sets the {@link FControl} which is responsible for this dockable.
	 * @param control the new control
	 */
	void setControl( FControlAccess control ){
		this.control = control;
	}
	
	/**
	 * Gets the control which is responsible for this dockable.
	 * @return the control
	 */
	FControlAccess getControl(){
		return control;
	}
}
