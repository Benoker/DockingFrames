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

package bibliothek.gui.dock.station.flap.button;

import bibliothek.gui.dock.FlapDockStation;
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.title.DockTitle;

/**
 * The {@link ButtonContentFilter} tells {@link DockTitle}s (and other components) that paint their content 
 * on the basis of a {@link ButtonContent} which actions to paint.
 * @see FlapDockStation#BUTTON_CONTENT_FILTER
 * @author Benjamin Sigg
 */
public interface ButtonContentFilter {
	/**
	 * Tells whether <code>action</code> is an important {@link DockAction} and should if the client told the
	 * component to filter actions.
	 * @param action the action to filter
	 * @return <code>true</code> if <code>action</code> should be visible
	 */
	public boolean isButtonAction( DockAction action );
}
