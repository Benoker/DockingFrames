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
package bibliothek.gui.dock.control.focus;

import java.awt.Component;

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;

/**
 * A {@link FocusStrategy} is used by a {@link DockController} to decide which {@link Component} to focus if
 * a {@link Dockable} should receive the focus.
 * @author Benjamin Sigg
 */
public interface FocusStrategy {
	/**
	 * Given a {@link Dockable} this method tells which {@link Component} should be focused.
	 * @param dockable some dockable which is about to get the focus
	 * @param mouseClicked the component which was touched by the mouse and which would get the focus normally. Can be <code>null</code>.
	 * @return the component to focus or <code>null</code> if this method cannot decide (in which case
	 * a default component will be chosen). If the <code>Component</code> is not focusable, then the next {@link Component} that
	 * follows in the iteration of focusable <code>Component</code>s will be focused. If the result is
	 * <code>mouseClicked</code> then no focus will be transfered - even if <code>mouseClicked</code> is not focusable 
	 */
	public Component getFocusComponent( Dockable dockable, Component mouseClicked );
	
	/**
	 * Informs this strategy that it will be used from now on.
	 */
	public void bind();
	
	/**
	 * Informs this strategy that it is no longer used from now on.
	 */
	public void unbind();
}
