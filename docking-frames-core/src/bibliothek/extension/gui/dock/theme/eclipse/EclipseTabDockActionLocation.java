/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2013 Benjamin Sigg
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
package bibliothek.extension.gui.dock.theme.eclipse;

/**
 * Can be used for an {@link EclipseTabDockAction} to fine tune the behavior.
 * @author Benjamin Sigg
 */
public enum EclipseTabDockActionLocation {
	/** the action is shown both on the tab and on the side */
	BOTH,
	/** the action is not shown at all (it will appear in the drop down menu) */
	HIDDEN,
	/** the action is shown on the side */
	SIDE,
	/** the action is shown on the tab*/
	TAB;
	
	/**
	 * Tells whether this location points to a tab.
	 * @return <code>true</code> if this location points to a tab
	 */
	public boolean isTab(){
		return this == TAB || this == BOTH;
	}
	
	/**
	 * Tells whether this location points to the side.
	 * @return <code>true</code> if this location points to the side
	 */
	public boolean isSide(){
		return this == SIDE || this == BOTH;
	}
}
