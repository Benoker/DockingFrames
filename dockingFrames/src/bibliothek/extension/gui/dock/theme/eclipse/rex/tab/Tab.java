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
package bibliothek.extension.gui.dock.theme.eclipse.rex.tab;

import java.awt.Component;
import javax.swing.Icon;

/**
 * @author Janni Kovacs
 */
public class Tab {
	private String title;
	private Icon icon;
	private Component comp;
	private Component tabComponent;
	private TabComponent painter;
	
	public Tab(String title, Component comp) {
		this(title, null, comp, null );
	}

	public Tab(String title, Icon icon, Component comp) {
		this(title, icon, comp, null);
	}

	public Tab(String title, Icon icon, Component comp, Component tabComponent ) {
		this.title = title;
		this.comp = comp;
		this.tabComponent = tabComponent;
		this.icon = icon;
	}
	
	public void setPainter( TabComponent painter ){
		this.painter = painter;
	}
	
	public String getTitle() {
		return title;
	}

	public Icon getIcon() {
		return icon;
	}

	public Component getTabComponent(){
		return tabComponent;
	}
	
	public Component getComponent() {
		return comp;
	}

	public void setTitle(String newTitle) {
		this.title = newTitle;
		if( painter != null )
			painter.update();
	}

	public void setIcon(Icon newIcon) {
		this.icon = newIcon;
		if( painter != null )
			painter.update();
	}
}
