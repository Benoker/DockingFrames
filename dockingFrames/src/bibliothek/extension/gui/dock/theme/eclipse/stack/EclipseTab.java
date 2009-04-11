/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2009 Benjamin Sigg
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
package bibliothek.extension.gui.dock.theme.eclipse.stack;

import java.awt.Component;
import java.awt.Insets;
import java.awt.Point;

import javax.swing.Icon;
import javax.swing.event.MouseInputListener;

import bibliothek.extension.gui.dock.theme.eclipse.rex.tab.TabComponent;
import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DockElement;
import bibliothek.gui.dock.station.stack.CombinedTab;
import bibliothek.gui.dock.station.stack.tab.AbstractTab;

/**
 * A wrapper around a {@link TabComponent} allowing to use the {@link TabComponent}
 * also as {@link CombinedTab}.
 * @author Benjamin Sigg
 */
public class EclipseTab extends AbstractTab implements CombinedTab{
	/** painting code for this tab */
	private TabComponent component;
	

	/**
	 * Creates a new tab.
	 * @param parent the owner of this tab.
	 * @param dockable the element associated with this tab.
	 * @param delegate the real {@link TabComponent}
	 */
	public EclipseTab( EclipseTabPane parent, Dockable dockable, TabComponent delegate ){
		super( parent, dockable );
		this.component = delegate;
	}
	
	@Override
	public Component getComponent(){
		return component.getComponent();
	}

	public void setIcon( Icon icon ){
		// ignore	
	}

	public void setText( String text ){
		// ignore
	}

	public void setTooltip( String tooltip ){
		// ignore
	}

	public void addMouseInputListener( MouseInputListener listener ){
		component.addMouseInputListener( listener );
	}

	public DockElement getElement(){
		return getDockable();
	}

	public Point getPopupLocation( Point click, boolean popupTrigger ){
		if( popupTrigger ){
			return click;
		}
		else{
			return null;
		}
	}

	public boolean isUsedAsTitle(){
		return true;
	}

	public void removeMouseInputListener( MouseInputListener listener ){
		component.removeMouseInputListener( listener );
	}

	/**
	 * Adds observers to parent and {@link DockController} to keep the 
	 * user interface up to date.
	 */
	public void bind(){
		component.bind();
		super.bind();
	}
	
	/**
	 * Removes observers from parent and {@link DockController}.
	 */
	public void unbind(){
		component.unbind();
		super.unbind();
	}

	public Insets getOverlap(){
		return component.getOverlap();
	}

	public void setPaintIconWhenInactive( boolean paint ){
		component.setPaintIconWhenInactive( paint );
	}

	@Override
	protected void informFocusChanged( boolean focused ){
		component.setFocused( focused );
	}

	@Override
	protected void informSelectionChanged( boolean selected ){
		component.setSelected( selected );
	}
}
