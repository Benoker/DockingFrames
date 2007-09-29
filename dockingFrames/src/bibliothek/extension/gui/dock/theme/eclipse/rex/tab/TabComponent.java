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
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.border.Border;

public interface TabComponent {
	public Component getComponent();
	
	public void setSelected( boolean selected );
	
	public void setFocused( boolean focused );
	
	public void setIndex( int index );
	
	public void setPaintIconWhenInactive( boolean paint );
	
	/**
	 * Called when a property of the tab has been changed and this 
	 * component has to reevaluate its content.
	 */
	public void update();

	public void addMouseListener( MouseListener listener );
	
	public void addMouseMotionListener( MouseMotionListener listener );
	
	public void removeMouseListener( MouseListener listener );
	
	public void removeMouseMotionListener( MouseMotionListener listener );

	public Border getContentBorder();
}
