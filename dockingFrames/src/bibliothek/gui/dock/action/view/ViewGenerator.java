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

package bibliothek.gui.dock.action.view;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.DockAction;


/**
 * A single entry for a {@link ActionViewConverter}. A generator
 * can convert one or more types of {@link DockAction} into one or more types
 * of {@link ViewTarget}.
 * @author Benjamin Sigg
 * 
 * @param <D> The type of DockAction converted by this generator
 * @param <A> The type of view created by this generator
 */
public interface ViewGenerator<D extends DockAction, A> {
	/**
	 * Converts <code>action</code> into a view. The result of this method
	 * can be <code>null</code> if no view should be shown for the given action.
	 * @param converter the converter that invoked this method
	 * @param action the action to convert
	 * @param dockable the Dockable for which the action will be used
	 * @return the view of the action or <code>null</code> if nothing should
	 * be displayed
	 */
	public A create( ActionViewConverter converter, D action, Dockable dockable );
}
