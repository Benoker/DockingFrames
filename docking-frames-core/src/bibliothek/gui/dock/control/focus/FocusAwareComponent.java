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

import bibliothek.gui.Dockable;

/**
 * A {@link Component} implementing this interface tells the framework that
 * it is aware of the existence of the {@link MouseFocusObserver} and that this
 * {@link Component} prefers to request its focus independently. The framework
 * will call {@link #maybeRequestFocus()} instead of {@link Component#requestFocusInWindow()}
 * when it encounters this interface.  
 * @author Benjamin Sigg
 */
public interface FocusAwareComponent {
	/**
	 * Called by the {@link MouseFocusObserver} instead of 
	 * {@link Component#requestFocusInWindow()}.
	 */
	public void maybeRequestFocus();

	/**
	 * Informs this {@link FocusAwareComponent} that is should run <code>run</code> after
	 * it has requested the focus. <code>run</code> must only be executed once. It does not
	 * contain references to objects that need to be cleaned by the garbage collector, so this
	 * {@link FocusAwareComponent} can keep a reference of <code>run</code> for a long time.
	 * @param run this {@link Runnable} will make sure that the {@link Dockable} is selected
	 * that belongs to this {@link Component}, not <code>null</code>.
	 */
	public void invokeOnFocusRequest( Runnable run );
}
