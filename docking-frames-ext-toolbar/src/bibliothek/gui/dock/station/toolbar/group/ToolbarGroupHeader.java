/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2012 Herve Guillaume, Benjamin Sigg
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
 * Herve Guillaume
 * rvguillaume@hotmail.com
 * FR - France
 *
 * Benjamin Sigg
 * benjamin_sigg@gmx.ch
 * CH - Switzerland
 */
package bibliothek.gui.dock.station.toolbar.group;

import bibliothek.gui.Orientation;
import bibliothek.gui.dock.ToolbarGroupDockStation;

import java.awt.Component;

/**
 * Represents a single {@link Component} which can be shown at the top or
 * left end of a {@link ToolbarGroupDockStation}.<br>
 * {@link ToolbarGroupHeader}s are created by {@link ToolbarGroupHeaderFactory}s.
 * @author Benjamin Sigg
 */
public interface ToolbarGroupHeader {
	/**
	 * The actual component represented by this object. The result of
	 * this method must never change.
	 * @return the component, must not be <code>null</code> and must not change
	 */
	public Component getComponent();
	
	/**
	 * Called before the {@link #getComponent() component} is shown, and every time when
	 * the orientation of the {@link ToolbarGroupDockStation} changes.
	 * @param orientation the orientation of the station, which is orthogonal to how the component is shown, not <code>null</code>
	 */
	public void setOrientation( Orientation orientation );
	
	/**
	 * Called if this header is no longer required
	 */
	public void destroy();
}
