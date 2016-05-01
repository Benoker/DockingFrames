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
package bibliothek.gui.dock.facile.mode;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.common.mode.ExtendedMode;
import bibliothek.gui.dock.facile.mode.status.ExtendedModeEnablement;

/**
 * Used by a {@link LocationModeManager} to change the {@link ExtendedMode} when
 * an element gets double-clicked.
 * @author Benjamin Sigg
 * @see LocationModeManager#setDoubleClickStrategy(DoubleClickLocationStrategy)
 */
public interface DoubleClickLocationStrategy {
	/**
	 * The default implementation of a {@link DoubleClickLocationStrategy} switches between
	 * {@link ExtendedMode#NORMALIZED} and {@link ExtendedMode#MAXIMIZED}.
	 */
	public static DoubleClickLocationStrategy DEFAULT = new DoubleClickLocationStrategy() {
		public ExtendedMode handleDoubleClick( Dockable dockable, ExtendedMode current, ExtendedModeEnablement enablement ){
			if( current == ExtendedMode.MAXIMIZED ){
				return ExtendedMode.NORMALIZED;
			}
			else{
				if( enablement.isAvailable( dockable, ExtendedMode.MAXIMIZED ).isAvailable()){
					return ExtendedMode.MAXIMIZED;
				}
				return null;
			}
		}
	};
	
	/**
	 * Called if the user double-clicked on <code>dockable</code>.
	 * @param dockable the clicked element
	 * @param current the current mode of <code>dockable</code>, might be <code>null</code>
	 * @param enablement tells which modes are available for <code>dockable</code> and which not
	 * @return the mode that <code>dockable</code> should be assigned, <code>null</code> indicates
	 * that nothing should happen
	 */
	public ExtendedMode handleDoubleClick( Dockable dockable, ExtendedMode current, ExtendedModeEnablement enablement );
}
