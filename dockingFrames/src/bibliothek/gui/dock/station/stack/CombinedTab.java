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

package bibliothek.gui.dock.station.stack;

import javax.swing.Icon;
import javax.swing.JComponent;

/**
 * A tab of a {@link CombinedStackDockComponent}. Every tab represents one
 * Component which can be selected.
 * @author Benjamin Sigg
 */
public interface CombinedTab {
	/**
	 * Gets a component which represents this tab.
	 * @return the graphical representation of this tab
	 */
	public JComponent getComponent();
	
	/**
	 * Sets the text of this tab.
	 * @param text the text to display
	 */
	public void setText( String text );
	
	/**
	 * Sets the image of this tab.
	 * @param icon an icon that should be shown, can be <code>null</code>
	 */
	public void setIcon( Icon icon );
	
	/**
	 * Sets the location of this tab. Every tab has another location, and
	 * the location can be used to find the component for which this tab
	 * is shown.
	 * @param index the index of this tab
	 */
	public void setIndex( int index );
}
