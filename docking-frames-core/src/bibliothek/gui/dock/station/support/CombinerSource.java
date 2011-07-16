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
package bibliothek.gui.dock.station.support;

import java.awt.Dimension;
import java.awt.Point;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.station.Combiner;
import bibliothek.gui.dock.station.DockableDisplayer;

/**
 * A set of information intended for a {@link Combiner}. This set of data allows the {@link Combiner} to merge the two
 * {@link Dockable}s {@link #getOld() old} and {@link #getNew() new}.<br>
 * Some clients of {@link Combiner} extend this interface to provide more information to the {@link Combiner}.
 * @author Benjamin Sigg
 */
public interface CombinerSource {
	/**
	 * Gets the old {@link Dockable}, the one {@link Dockable} which is already a child
	 * of {@link #getParent() the future parent}.
	 * @return the old {@link Dockable}, never <code>null</code>
	 */
	public Dockable getOld();
	
	/**
	 * Gets the {@link DockableDisplayer} which manages the old {@link Dockable}.
	 * @return the displayer or <code>null</code>
	 */
	public DockableDisplayer getOldDisplayer();
	
	/**
	 * Gets the new {@link Dockable}, the one {@link Dockable} which is currently dragged around
	 * by the user and which is about to be dropped over {@link #getOld() old}. The parent of
	 * this {@link Dockable} may or may not be {@link #getParent() the old ones parent}
	 * @return the new {@link Dockable}, never <code>null</code>
	 */
	public Dockable getNew();
	
	/**
	 * Gets the station which will be the new parent station of the combined {@link Dockable}. 
	 * @return the parent station, never <code>null</code>
	 */
	public DockStation getParent();
	
	/**
	 * Gets the estimated size of the combined {@link Dockable}. In most cases the size will be equal to the
	 * current size of the {@link #getOld() old Dockable}.<br>
	 * If the size is unknown, then <code>null</code> is returned. A value of <code>null</code> also means
	 * that the mouse is currently not over the old dockable. 
	 * @return the estimated size, can be <code>null</code>
	 */
	public Dimension getSize();
	
	/**
	 * Gets the position of the mouse. A value of <code>0/0</code> indicates that the mouse is at 
	 * the top left edge of the {@link #getOld() old Dockable}, a value equal to {@link #getSize() the estimated size}
	 * means that the mouse is at the lower right edge of the old dockable. The mouse may be outside the 
	 * of these boundaries.<br>
	 * A value of <code>null</code> indicates that the mouse position should not be used for deciding
	 * of how to combine the two {@link Dockable}s. This can happen for example if the mouse hovers directly
	 * over the title of the old {@link Dockable}. 
	 * @return the position of the mouse, may be <code>null</code>
	 */
	public Point getMousePosition();
	
	/**
	 * Gets a map of placeholders which are to be used for creating the combined {@link Dockable}. These placeholders
	 * have been created by a {@link DockStation} that was removed because its children count dropped to one.
	 * @return existing placeholders, may be <code>null</code>
	 */
	public PlaceholderMap getPlaceholders();
	
	/**
	 * Tells whether the mouse currently is over the title of the {@link #getOld() old Dockable}.
	 * @return <code>true</code> if the mouse hovers over the title, <code>false</code> otherwise
	 */
	public boolean isMouseOverTitle();
}
