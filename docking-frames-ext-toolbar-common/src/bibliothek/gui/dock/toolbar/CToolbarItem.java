/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2012 Herve Guillaume, Benjamin Sigg
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
 * Herve Guillaume
 * rvguillaume@hotmail.com
 * FR - France
 *
 * Benjamin Sigg
 * benjamin_sigg@gmx.ch
 * CH - Switzerland
 */

package bibliothek.gui.dock.toolbar;

import bibliothek.gui.dock.common.CStation;
import bibliothek.gui.dock.common.SingleCDockable;
import bibliothek.gui.dock.common.intern.AbstractCDockable;
import bibliothek.gui.dock.common.intern.CommonDockable;
import bibliothek.gui.dock.toolbar.intern.CommonToolbarItemDockable;

/**
 * A {@link CToolbarItem} is an item (e.g. a button) that is shown in a toolbar.
 * @author Benjamin Sigg
 */
public class CToolbarItem extends AbstractCDockable implements SingleCDockable{
	private String id;
	
	/**
	 * Creates a new item.
	 * @param id the unique identifier of this item, not <code>null</code>
	 */
	public CToolbarItem( String id ){
		if( id == null ){
			throw new IllegalArgumentException( "id must not be null" );
		}
		this.id = id;
	}
	
	@Override
	public String getUniqueId(){
		return id;
	}
	
	@Override
	public boolean isMinimizable(){
		return false;
	}

	@Override
	public boolean isMaximizable(){
		return false;
	}

	@Override
	public boolean isExternalizable(){
		return false;
	}

	@Override
	public boolean isStackable(){
		return false;
	}

	@Override
	public boolean isCloseable(){
		return false;
	}

	@Override
	public CStation<?> asStation(){
		return null;
	}
	
	@Override
	public CommonToolbarItemDockable intern(){
		return (CommonToolbarItemDockable)super.intern();
	}

	@Override
	protected CommonToolbarItemDockable createCommonDockable(){
		return new CommonToolbarItemDockable( this );
	}
}
