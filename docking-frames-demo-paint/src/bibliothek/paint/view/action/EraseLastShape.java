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

import bibliothek.gui.dock.common.action.CButton;
import bibliothek.paint.model.Picture;
import bibliothek.paint.util.Resources;
import bibliothek.paint.view.Page;

/**
 * Removes the last drawn shape from a picture.
 * @author Benjamin Sigg
 *
 */
public class EraseLastShape extends CButton{
	/** the page this actions belongs to */
	private Page page;
	
	/**
	 * Creates a new action.
	 * @param page the page this action belongs to
	 */
	public EraseLastShape( Page page ){
		this.page = page;
		
		setText( "Undo" );
		setTooltip( "Erases the newest shape of the picture" );
		setIcon( Resources.getIcon( "shape.remove" ) );
	}
	
	@Override
	protected void action(){
		Picture picture = page.getPicture();
		if( picture != null )
			picture.removeLast();
	}
}
