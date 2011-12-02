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
package bibliothek.gui.dock.station.flap;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.FlapDockStation;

/**
 * A {@link FlapWindowFactory} creates instances of {@link FlapWindow}.
 * @author Benjamin Sigg
 */
public interface FlapWindowFactory {
	/**
	 * Creates a new window for <code>station</code>.
	 * @param station a known client of this factory
	 * @param buttonPane the panel which actually shows the buttons of
	 * <code>station</code>, may be needed to calculate the location and
	 * size of the window.
	 * @return the new window or <code>null</code> if this factory is unable to create
	 * a valid window for <code>station</code>
	 */
	public FlapWindow create(FlapDockStation station, ButtonPane buttonPane);
	
	/**
	 * Tells whether <code>window</code> can still be used by <code>station</code>.
	 * @param window a window create by this factory, was not yet {@link FlapWindow#destroy() destroied}
	 * @param station the owner of <code>window</code>
	 * @return <code>true</code> if <code>station</code> can show another {@link Dockable} on
	 * <code>window</code>
	 */
	public boolean isValid(FlapWindow window, FlapDockStation station);
	
	/**
	 * Informs this factory that it will be used by <code>station</code>.
	 * @param station a new client of this factory
	 */
	public void install(FlapDockStation station);
	
	/**
	 * Informs this factory that it will no longer be used by <code>station</code>.
	 * @param station a withdrawing client
	 */
	public void uninstall(FlapDockStation station);
}
