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

import java.awt.Component;

import bibliothek.gui.dock.ToolbarItem;
import bibliothek.gui.dock.common.CStation;
import bibliothek.gui.dock.common.SingleCDockable;
import bibliothek.gui.dock.common.action.CAction;
import bibliothek.gui.dock.common.intern.AbstractCDockable;
import bibliothek.gui.dock.toolbar.expand.ExpandedState;
import bibliothek.gui.dock.toolbar.intern.CommonToolbarItemDockable;

/**
 * A {@link CToolbarItem} is an item (e.g. a button) that is shown in a toolbar.<br>
 * In reality the {@link CToolbarItem} consists of several {@link Component}s, but only one of them is shown. Which
 * one depends on the {@link ExpandedState} which is set by the toolbar itself. In order to use this feature clients
 * have to call 
 * <code>CControl control = ...
 * control.putProperty( ExpandableToolbarItemStrategy.STRATEGY, new DefaultExpandableToolbarItemStrategy() );</code>
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

	/**
	 * Sets a component which should be shown when this dockable is in state {@link ExpandedState#SHRUNK}.
	 * @param item the item to show, can be <code>null</code>
	 */
	public void setItem( CAction item ){
		setItem( item, ExpandedState.SHRUNK );
	}
	
	/**
	 * Sets a component which should be shown when this dockable is in state {@link ExpandedState#SHRUNK}.
	 * @param item the item to show, can be <code>null</code>
	 */
	public void setItem( Component item ){
		setItem( item, ExpandedState.SHRUNK );
	}
	
	/**
	 * Sets a component which should be shown when this dockable is in state <code>state</code>.
	 * @param item the item to show, can be <code>null</code>
	 * @param state the state when to show <code>item</code>
	 */
	public void setItem( Component item, ExpandedState state ){
		intern().setComponent( item, state );
	}

	/**
	 * Sets a component which should be shown when this dockable is in state <code>state</code>.
	 * @param item the item to show, can be <code>null</code>
	 * @param state the state when to show <code>item</code>
	 */
	public void setItem( CAction item, ExpandedState state ){
		if( item == null ){
			setItem( (ToolbarItem)null, state );
		}
		else{
			intern().setAction( item.intern(), state );
		}
	}
	
	/**
	 * Sets a component which should be shown when this dockable is in state <code>state</code>.
	 * @param item the item to show, can be <code>null</code>
	 * @param state the state when to show <code>item</code>
	 */
	public void setItem( ToolbarItem item, ExpandedState state ){
		intern().setItem( item, state );
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
		return true;
	}

	@Override
	public boolean isStackable(){
		return true;
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
