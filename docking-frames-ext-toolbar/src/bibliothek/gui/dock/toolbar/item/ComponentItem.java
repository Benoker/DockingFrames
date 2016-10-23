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
package bibliothek.gui.dock.toolbar.item;

import java.awt.Component;

import javax.swing.AbstractButton;
import javax.swing.event.MouseInputListener;

import bibliothek.gui.DockController;
import bibliothek.gui.Orientation;
import bibliothek.gui.dock.ToolbarItem;
import bibliothek.gui.dock.ToolbarItemDockable;

/**
 * A {@link ComponentItem} is the most simple implementation of {@link ToolbarItem}, it
 * just wraps around a single {@link Component}.
 * @author Benjamin Sigg
 */
public class ComponentItem implements ToolbarItem{
	/** the content of this item */
	private Component component;
	
	/**
	 * Creates the new item.
	 * @param component the content of this item, not <code>null</code>
	 */
	public ComponentItem( Component component ){
		if( component == null ){
			throw new IllegalArgumentException( "component must not be null" );
		}
		
		if( component instanceof AbstractButton ) {
			((AbstractButton) component).setBorderPainted( false );
		}
		
		this.component = component;
	}

	@Override
	public Component getComponent(){
		return component;
	}

	@Override
	public void bind(){
		// ignore
	}

	@Override
	public void unbind(){
		// ignore
	}

	@Override
	public void setSelected( boolean selected ){
		// ignore
	}

	@Override
	public void setController( DockController controller ){
		// ignore
	}
	
	@Override
	public void setDockable( ToolbarItemDockable dockable ){
		// ignore	
	}

	@Override
	public void setOrientation( Orientation orientation ){
		// ignore	
	}
	
	@Override
	public void addMouseInputListener( MouseInputListener listener ){
		component.addMouseListener( listener );
		component.addMouseMotionListener( listener );
	}

	@Override
	public void removeMouseInputListener( MouseInputListener listener ){
		component.removeMouseListener( listener );
		component.removeMouseMotionListener( listener );
	}
}
