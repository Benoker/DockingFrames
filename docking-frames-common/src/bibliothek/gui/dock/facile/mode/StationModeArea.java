/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2009 Benjamin Sigg
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
package bibliothek.gui.dock.facile.mode;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.accept.DockAcceptance;
import bibliothek.gui.dock.layout.DockableProperty;
import bibliothek.gui.dock.support.mode.AffectedSet;
import bibliothek.gui.dock.support.mode.Mode;

/**
 * A representation of a {@link DockStation} which can be accessed 
 * through {@link Mode}s.
 * @author Benjamin Sigg
 */
public interface StationModeArea extends ModeArea{	
	/**
	 * Gets the location of <code>dockable</code> which is a child
	 * of this station.
	 * @param child the child
	 * @return the location, may be <code>null</code>
	 */
	public DockableProperty getLocation( Dockable child );
	
	/**
	 * Sets the location of <code>dockable</code> to <code>location</code>
	 * and tries to ensure that <code>dockable</code> is a child of this station.<br>
	 * This method may completely fail to change the location of <code>dockable</code>, for example because
	 * a {@link DockAcceptance} does not allow the dockable to be moved. In such cases <code>false</code> is 
	 * returned. 
	 * @param dockable the new or old child
	 * @param location the new location, may be <code>null</code>
	 * @param set this method has to store all {@link Dockable}s which might have changed their
	 * mode in the set.
	 * @return <code>true</code> if <code>dockable</code> is now a child of this {@link StationModeArea}, <code>false</code> if not
	 */
	public boolean setLocation( Dockable dockable, DockableProperty location, AffectedSet set );
}
