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
package bibliothek.gui.dock.station.stack;

import javax.swing.Icon;

import bibliothek.gui.dock.StackDockStation;

/**
 * All the content of a single tab on a {@link StackDockStation}.
 * @author Benjamin Sigg
 */
public class TabContent {
	private Icon icon;
	private String title;
	private String tooltip;
	
	/**
	 * Creates a new set of data.
	 * @param icon the icon of this tab
	 * @param title the text of this tab
	 * @param tooltip the tooltip of this tab
	 */
	public TabContent( Icon icon, String title, String tooltip ){
		this.icon = icon;
		this.title = title;
		this.tooltip = tooltip;
	}
	
	/**
	 * Gets the icon of this tab.
	 * @return the icon, can be <code>null</code>
	 */
	public Icon getIcon(){
		return icon;
	}

	/**
	 * Gets the text of this tab.
	 * @return the text, can be <code>null</code>
	 */
	public String getTitle(){
		return title;
	}
	
	/**
	 * Gets the tooltip of this tab
	 * @return the tooltip, can be <code>null</code>
	 */
	public String getTooltip(){
		return tooltip;
	}
}
