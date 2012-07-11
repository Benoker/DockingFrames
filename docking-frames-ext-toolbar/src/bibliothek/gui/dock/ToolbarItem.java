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
package bibliothek.gui.dock;

import java.awt.Component;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseListener;

import javax.swing.event.MouseInputListener;

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.Orientation;

/**
 * A {@link ToolbarItem} is an item that is shown as part of a toolbar. {@link ToolbarItem}s must
 * be wrapped into a {@link ToolbarItemDockable} to be shown.
 * @author Benjamin Sigg
 */
public interface ToolbarItem {
	/**
	 * Gets the {@link Component} which is wrapped by this item. The result of this method must not be
	 * <code>null</code> after {@link #bind()} has been called, it may not change until {@link #unbind()} 
	 * was called.
	 * @return the wrapped {@link Component}, can be <code>null</code> unless {@link #bind()} was called
	 */
	public Component getComponent();
	
	/**
	 * Informs this item that it is now in use, the result of {@link #getComponent()} must not be <code>null</code>
	 * and must not change after this method has been called.
	 */
	public void bind();
	
	/**
	 * Informs this item that it is no longer in use, the result of {@link #getComponent()} can be <code>null</code>
	 * or change after this method has been called.
	 */
	public void unbind();
	
	/**
	 * Tells this item the orientation of the toolbar
	 * @param orientation the orientation of the toolbar
	 */
	public void setOrientation( Orientation orientation );
	
	/**
	 * Informs this item whether it is actually shown or not.
	 * @param selected <code>true</code> if the item is shown
	 */
	public void setSelected( boolean selected );
	
	/**
	 * Informs this item about the {@link DockController} in whose realm it is used. This method will always
	 * be called before {@link #bind()}, or after {@link #unbind()}, is executed. 
	 * @param controller the controller in whose realm this item works, can be <code>null</code>
	 */
	public void setController( DockController controller );
	
	/**
	 * Informs this item about the {@link Dockable} that is using it.
	 * @param dockable the owner of this item
	 */
	public void setDockable( ToolbarItemDockable dockable );
	
	/**
	 * Urges this item to add <code>listener</code> to its {@link #getComponent() component}. This method
	 * must only be called if {@link #bind()} was executed.
	 * @param listener the {@link MouseListener} and {@link MouseMotionListener} that should be added
	 */
	public void addMouseInputListener( MouseInputListener listener );
	
	/**
	 * Urges this item to remove <code>listener</code> from its {@link #getComponent() component}. This method
	 * must only be called if {@link #bind()} was executed.
	 * @param listener the {@link MouseListener} and {@link MouseMotionListener} that should be removed
	 */
	public void removeMouseInputListener( MouseInputListener listener );
}
