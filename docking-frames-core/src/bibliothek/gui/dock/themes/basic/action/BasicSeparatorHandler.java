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
package bibliothek.gui.dock.themes.basic.action;

import java.awt.Color;
import java.awt.event.ActionListener;

import javax.swing.JComponent;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.action.actions.SeparatorAction;
import bibliothek.gui.dock.themes.basic.action.menu.MenuViewItem;
import bibliothek.gui.dock.title.DockTitle.Orientation;

/**
 * A handler that shows a {@link JSeparator} for a {@link SeparatorAction}.
 * @author Benjamin Sigg
 */
public class BasicSeparatorHandler implements BasicTitleViewItem<JComponent>, MenuViewItem<JComponent> {
	/** the action for which the separator is shown */
	private SeparatorAction action;
	
	/** the separator */
	private JSeparator separator;
	
	/**
	 * Creates a new handler
	 * @param separator the separator to show
	 * @param action the action for which the action is shown
	 */
	public BasicSeparatorHandler( JSeparator separator, SeparatorAction action ){
		this.action = action;
		this.separator = separator;
		separator.setOrientation( SwingConstants.HORIZONTAL );
	}
	
	public void bind(){
		// ignore
	}
	
	public void addActionListener( ActionListener listener ){
		// ignore
	}
	
	public void removeActionListener( ActionListener listener ){
		// ignore
	}
	
	public void setOrientation( Orientation orientation ){
		if( orientation.isHorizontal() )
			separator.setOrientation( SwingConstants.VERTICAL );
        else
            separator.setOrientation( SwingConstants.HORIZONTAL );
	}

	public DockAction getAction(){
		return action;
	}

	public JComponent getItem(){
		return separator;
	}

	public void setBackground( Color background ) {
	    // ignore
	}
	
	public void setForeground( Color foreground ) {
	    // ignore
	}
	
	public void unbind(){
		// ignore
	}
}
