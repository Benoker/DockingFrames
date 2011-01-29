/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2011 Benjamin Sigg
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
package bibliothek.gui.dock.facile.menu;

import bibliothek.gui.dock.support.menu.MenuPiece;
import bibliothek.gui.dock.util.UIValue;
import bibliothek.gui.dock.util.text.TextValue;
import bibliothek.util.Path;

/**
 * Represents a text that is used by a {@link MenuPiece}
 * @author Benjamin Sigg
 */
public abstract class MenuPieceText extends TextValue{
	/** what kind of {@link UIValue} this is */
	public static final Path KIND_MENU = KIND_TEXT.append( "menu" );
	
	/** the menu using the text */
	private MenuPiece menu;
	
	/**
	 * Creates a new text.
	 * @param id the unique identifier of this text
	 * @param menu the menu that is using this text
	 */
	public MenuPieceText( String id, MenuPiece menu ){
		super( id, KIND_MENU );
		this.menu = menu;
	}
	
	/**
	 * Gets the menu which is using this text.
	 * @return the menu
	 */
	public MenuPiece getMenu(){
		return menu;
	}
}
