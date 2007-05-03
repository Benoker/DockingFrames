/**
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
	 * @param icon the icon
	 */
	public void setIcon( Icon icon );
	
	/**
	 * Sets the disabled icon of the button.
	 * @param icon the disabled icon
	 */
	public void setDisabledIcon( Icon icon );	
	
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
}
