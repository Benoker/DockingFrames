/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2009 Benjamin Sigg
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
package bibliothek.extension.gui.dock.theme.eclipse.stack.tab;

import javax.swing.border.Border;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;

/**
 * An {@link InvisibleTabPane} can "show" some {@link InvisibleTab}s. This
 * panel is used at places where no tabs should be shown, but the framework
 * assumes that tabs could be shown. 
 * @author Benjamin Sigg
 */
public interface InvisibleTabPane {
	/**
	 * Gets the {@link Dockable} that is currently selected.
	 * @return the selected element, can be <code>null</code>s
	 */
	public Dockable getSelectedDockable();
	
	/**
	 * Gets the station for which this pane is used.
	 * @return the owner
	 */
	public DockStation getStation();
	
	/**
	 * Sets the border that should be painted around <code>dockable</code>.
	 * @param dockable some child of this pane
	 * @param border the new border, can be <code>null</code>
	 * @throws IllegalArgumentException if <code>dockable</code> is invalid
	 */
	public void setBorder( Dockable dockable, Border border );
}
