/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2011 Benjamin Sigg
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
package bibliothek.gui.dock.common.group;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.common.mode.ExtendedMode;

/**
 * This {@link CGroupMovement} is the most basic movement as it represents the movement of one
 * lonely {@link Dockable}.
 * @author Benjamin Sigg
 */
public class SingleGroupMovement implements CGroupMovement{
	private Dockable dockable;
	private ExtendedMode target;
	
	/**
	 * Creates a new movement object.
	 * @param dockable the element whose location will be changed
	 * @param target the new mode for <code>dockable</code>
	 */
	public SingleGroupMovement( Dockable dockable, ExtendedMode target ){
		this.dockable = dockable;
		this.target = target;
	}
	
	public void apply( CGroupBehaviorCallback callback ){
		callback.setMode( dockable, target );	
	}
	
	public boolean forceAccept( DockStation parent, Dockable child ){
		return true;
	}
}
