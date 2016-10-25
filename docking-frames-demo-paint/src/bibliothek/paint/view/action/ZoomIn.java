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
package bibliothek.paint.view.action;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.KeyStroke;

import bibliothek.gui.dock.common.action.CButton;
import bibliothek.paint.util.Resources;
import bibliothek.paint.view.Page;

/**
 * A button that enlarges a {@link Page} when clicked.
 * @author Benjamin Sigg
 *
 */
public class ZoomIn extends CButton {
	/** the page whose zoom-factor will be changed */
	private Page page;
	
	/**
	 * Creates a new button
	 * @param page the page whose zoom-factor will be changed
	 */
	public ZoomIn( Page page ){
		this.page = page;
		setText( "Zoom in" );
		setIcon( Resources.getIcon( "zoom.in" ) );
		setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_ADD, InputEvent.CTRL_MASK ) );
	}
	
	@Override
	protected void action(){
		double zoom = 2*page.getZoom();
		if( zoom <= 32.0 ){
			page.setZoom( zoom );
		}
	}
}
