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
package bibliothek.gui.dock.control.focus;

import java.awt.Component;

import bibliothek.gui.Dockable;

/**
 * A question that is asked to a {@link FocusStrategy}.
 * @author Benjamin Sigg
 */
public interface FocusStrategyRequest {
	/**
	 * Gets the item which is about to gain the focus
	 * @return the item that gains the focus, not <code>null</code>
	 */
	public Dockable getDockable();
	/**
	 * Gets the {@link Component} which was touched by the mouse.
	 * @return the clicked component, can be <code>null</code>
	 */
	public Component getMouseClicked();
	
	/**
	 * Tells whether <code>component</code> should be excluded from receiving the focus.
	 * @param component some {@link Component} which may receive the focus
	 * @return <code>true</code> if <code>component</code> should not gain the focus
	 */
	public boolean excluded( Component component );
}
