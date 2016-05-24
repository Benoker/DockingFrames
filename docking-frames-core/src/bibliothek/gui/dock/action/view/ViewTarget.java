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

package bibliothek.gui.dock.action.view;

import javax.swing.JComponent;

import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.action.DropDownAction;
import bibliothek.gui.dock.themes.basic.action.BasicTitleViewItem;
import bibliothek.gui.dock.themes.basic.action.dropdown.DropDownViewItem;
import bibliothek.gui.dock.themes.basic.action.menu.MenuViewItem;
import bibliothek.gui.dock.title.DockTitle;

/**
 * Defines for which platform a {@link DockAction} is transformed to by
 * a {@link ActionViewConverter}. Clients may define their own ViewTargets, but
 * they must ensure, that the {@link ActionViewConverter} knows these new targets. 
 * @param <A> the resulting type of a transformation
 * @author Benjamin Sigg
 */
public class ViewTarget<A> {
	/**
	 * A target for a JMenu
	 */
	public static final ViewTarget<MenuViewItem<JComponent>> MENU = 
		new ViewTarget<MenuViewItem<JComponent>>( "target MENU" );
	
	/**
	 * A target for an item shown on a {@link DockTitle}
	 */
	public static final ViewTarget<BasicTitleViewItem<JComponent>> TITLE = 
		new ViewTarget<BasicTitleViewItem<JComponent>>( "target TITLE" );
	
	/**
	 * A target aiming to a {@link DropDownAction}
	 */
	public static final ViewTarget<DropDownViewItem> DROP_DOWN =
		new ViewTarget<DropDownViewItem>( "target DROP DOWN" );
	
	/**
	 * A unique id.
	 */
	private String id;
	
	/**
	 * Creates a new ViewTarget.
	 * @param id the unique id of this target
	 */
	public ViewTarget( String id ){
		if( id == null )
			throw new IllegalArgumentException( "id must not be null" );
		
		this.id = id;
	}
	
	@Override
	public String toString(){
		return id;
	}
	
	@Override
	public int hashCode(){
		return id.hashCode();
	}

	@Override
	public boolean equals( Object obj ){
		if (this == obj) {
			return true;
		}

		if (obj == null) {
			return false;
		}

		if( this.getClass() == obj.getClass()) {
			return ((ViewTarget<?>)obj).id.equals( id );
		}

		return false;

	}
}
