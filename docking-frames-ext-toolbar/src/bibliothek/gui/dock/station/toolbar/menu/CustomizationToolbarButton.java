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
package bibliothek.gui.dock.station.toolbar.menu;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.JToggleButton;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;

/**
 * The {@link CustomizationToolbarButton} is a button that allows to add one {@link Dockable} to a 
 * {@link DockStation}. This button also offers an indication telling whether the {@link Dockable} is
 * already shown on another station. 
 * @author Benjamin Sigg
 */
public abstract class CustomizationToolbarButton implements CustomizationMenuContent{
	private Icon icon;
	private String description;
	
	private JToggleButton button;
	
	private CustomizationMenuCallback callback;
	
	@Override
	public void setController( DockController controller ){
		// not required for now
	}
	
	@Override
	public Component getView(){
		return button;
	}
	
	@Override
	public void bind( CustomizationMenuCallback callback ){
		this.callback = callback;
		button = new JToggleButton();
		button.setIcon( icon );
		button.setToolTipText( description );
		
		if( hasDockable() ){
			Dockable item = getDockable();
			button.setSelected( item.getDockParent() != null );
			if( item.getDockParent() != null && !item.getDockParent().canDrag( item )){
				button.setEnabled( false );
			}
		}
		
		button.addActionListener( new ActionListener(){
			@Override
			public void actionPerformed( ActionEvent e ){
				Dockable item = getDockable();
				DockStation parent = item.getDockParent();
				if( parent != null ){
					parent.drag( item );
				}
				
				if( button.isSelected() ){
					CustomizationToolbarButton.this.callback.append( item );
				}
			}
		});
	}
	
	@Override
	public void unbind(){
		button = null;
	}
	
	/**
	 * Sets the icon which should be shown on the button.
	 * @param icon the new icon, can be <code>null</code>
	 */
	public void setIcon( Icon icon ){
		this.icon = icon;
		if( button != null ){
			button.setIcon( icon );
		}
	}
	
	/**
	 * Sets a text which describes the meaning of the button.
	 * @param description the description, can be <code>null</code>
	 */
	public void setDescription( String description ){
		this.description = description;
		if( button != null ){
			button.setToolTipText( description );
		}
	}
	
	/**
	 * Tells whether the {@link Dockable} of this button, accessible by calling {@link #getDockable()}, is
	 * already present. If the item is not yet present, then it cannot be visible or selected at this time.
	 * @return whether the {@link Dockable} already exists
	 */
	protected abstract boolean hasDockable();
	
	/**
	 * Gets the element that is put onto a toolbar. This method may create the {@link Dockable} lazily
	 * in the very moment it is used the first time.
	 * @return the item to show on the toolbar
	 */
	protected abstract Dockable getDockable();
}
