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
package bibliothek.gui.dock.common.event;

import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.intern.CDockable;

/**
 * This listener is used to prevent a {@link CDockable} from loosing or gaining
 * the focus. Events may happen such that the framework is unable to prevent the 
 * focus from changing, e.g. if a {@link CDockable} is closed.<br>
 * Please use this listener with care, the workflow of the user may be seriously
 * hindered if the focus cannot be changed.<br>
 * @author Benjamin Sigg
 * @see CControl#addVetoFocusListener(CVetoFocusListener)
 * @see CControl#removeVetoFocusListener(CVetoFocusListener)
 */
public interface CVetoFocusListener {
	/**
	 * Called before focus is transferred to <code>dockable</code>.
	 * @param dockable the dockable that gets the focus
	 * @return <code>true</code> if this listener approves the action,
	 * <code>false</code> to speak out a veto
	 */
	public boolean willGainFocus( CDockable dockable );
	
	/**
	 * Called before focus is transferred from <code>dockable</code>.
	 * @param dockable the dockable that looses the focus
	 * @return <code>true</code> if this listener approves the action,
	 * <code>false</code> to speak out a veto
	 */
	public boolean willLoseFocus( CDockable dockable );
}
