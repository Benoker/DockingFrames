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
package bibliothek.gui.dock.common.perspective;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.CStation;
import bibliothek.util.FrameworkOnly;
import bibliothek.util.Path;

/**
 * A representation of a {@link CStation}.
 * @author Benjamin Sigg
 */
public interface CStationPerspective extends CElementPerspective{
	/**
	 * Gets the unique identifier of this station.
	 * @return the unique identifier
	 */
	public String getUniqueId();
	
	/**
	 * Gets the unique id denoting the type of this {@link CStation}, this should be the
	 * exact same result as {@link CStation#getTypeId()} will return.
	 * @return the type id, can be <code>null</code>
	 */
	public Path getTypeId();
	
	/**
	 * Informs this station by which perspective it is used. 
	 * @param perspective the perspective that uses this station or <code>null</code>
	 */
	@FrameworkOnly
	public void setPerspective( CPerspective perspective );
	
	/**
	 * Gets the perspective which presents this station.
	 * @return the owner of this station, can be <code>null</code>
	 */
	public CPerspective getPerspective();

	/**
	 * Tells whether this station will act as a {@link CStation#isWorkingArea() working area}.
	 * @return whether this station acts as working area
	 */
	public boolean isWorkingArea();
	

	/**
	 * Tells whether this is a root station or not.
	 * @return the root flag
	 * @see #setRoot(boolean)
	 */
	public boolean isRoot();
	
	/**
	 * Sets the root station flag. Setting flag is equivalent of setting the <code>root</code> parameter when calling
	 * {@link CControl#addStation(bibliothek.gui.dock.common.CStation, boolean)}. The location of a {@link Dockable} is
	 * always relative to its nearest root-station parent.<br>
	 * Please note that the root-flag set by calling {@link CControl#addStation(bibliothek.gui.dock.common.CStation, boolean)} will
	 * never be overridden by the flag set in the perspective.
	 * @param root
	 */
	public void setRoot( boolean root );
}
