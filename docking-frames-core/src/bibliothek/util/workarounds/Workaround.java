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
package bibliothek.util.workarounds;

import java.awt.Component;
import java.awt.Window;

import bibliothek.util.Workarounds;

/**
 * A {@link Workaround} provides code to workaround an issue that is only present in some versions of the
 * JRE or in some libraries.
 * @author Benjamin Sigg
 */
public interface Workaround {
	/**
	 * Called for any {@link Component} which is used as glass pane (as invisible panel).
	 * @param component the component that is invisible
	 */
	public void markAsGlassPane( Component component );
	
	/**
	 * Makes the window <code>window</code> transparent. See {@link Workarounds#makeTransparent(Window)} for a more
	 * detailed description.
	 * @param window the window that should be transparent
	 * @return whether the window is now transparent or not
	 */
	public boolean makeTransparent( Window window );
}
