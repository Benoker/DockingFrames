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
 * A button that shrinks a {@link Page} when clicked.
 * @author Benjamin Sigg
 *
 */
public class ZoomOut extends CButton {
	/** the page whose zoom-factor will be changed */
	private Page page;
	
	/**
	 * Creates a new button
	 * @param page the page whose zoom-factor will be changed
	 */
	public ZoomOut( Page page ){
		this.page = page;
		setText( "Zoom out" );
		setIcon( Resources.getIcon( "zoom.out" ) );
		setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_SUBTRACT, InputEvent.CTRL_MASK ) );
		
	}
	
	@Override
	protected void action(){
		double zoom = 0.5*page.getZoom();
		if( zoom >= 1.0 / 32.0 ){
			page.setZoom( zoom );
		}
	}
}