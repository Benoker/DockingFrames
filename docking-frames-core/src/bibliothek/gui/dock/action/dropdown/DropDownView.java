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

package bibliothek.gui.dock.action.dropdown;

import javax.swing.Icon;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.ActionContentModifier;

/**
 * A connection between an drop-down-item and a view. Clients should use
 * instances of this interface as if they access a button.
 * @author Benjamin Sigg
 */
public interface DropDownView {
	/**
	 * Sets the text of the button.
	 * @param text the text
	 */
	public void setText( String text );
	
	/**
	 * Sets the tooltip of the button.
	 * @param tooltip the tooltip
	 */
	public void setTooltip( String tooltip );
		
	/**
	 * Sets the icon of the button.
	 * @param modifier the context in which the icon is used, not <code>null</code>
	 * @param icon the icon
	 */
	public void setIcon( ActionContentModifier modifier, Icon icon );
	
	/**
	 * Gets the {@link ActionContentModifier}s for which {@link #setIcon(ActionContentModifier, Icon)} was called
	 * with a value other than <code>null</code>.
	 * @return the icons that were set
	 */
	public ActionContentModifier[] getIconContexts();
	
	/**
	 * Clears all {@link Icon}s, any field pointing to an {@link Icon} is set to <code>null</code>
	 */
	public void clearIcons();
	
	/**
	 * Sets the enabled-state of the button.
	 * @param enabled the state
	 */
	public void setEnabled( boolean enabled );
		
	/**
	 * Sets the selected-state of the button.
	 * @param selected the state
	 */
	public void setSelected( boolean selected );
	
	/**
	 * Sets the {@link Dockable} which is represented by the view.
	 * @param dockable the represented element, can be <code>null</code>
	 */
	public void setDockableRepresentation( Dockable dockable );
}
