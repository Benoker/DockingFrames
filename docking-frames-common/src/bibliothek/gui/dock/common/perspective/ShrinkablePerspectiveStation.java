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

import bibliothek.gui.dock.perspective.PerspectiveDockable;
import bibliothek.gui.dock.perspective.PerspectiveStation;

/**
 * This kind of {@link PerspectiveStation} can find out whether it is really
 * needed and can remove itself if not.
 * @author Benjamin Sigg
 */
public interface ShrinkablePerspectiveStation extends PerspectiveStation{
	
	/**
	 * Checks the number of children this perspective has. If the number of children is 0 or 1, then
	 * this perspective replaces itself by its child in the of perspectives. 
	 * @return the replacement of <code>this</code>, which is either <code>this</code>, the only
	 * child of <code>this</code>, or <code>null</code>
	 */
	public PerspectiveDockable shrink();
}
