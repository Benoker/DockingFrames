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
package bibliothek.gui.dock.station.stack.tab.layouting;

import java.awt.Dimension;
import java.awt.Rectangle;

import bibliothek.gui.dock.station.stack.tab.TabPaneComponent;

/**
 * This {@link LayoutBlock} handles exactly one {@link TabPaneComponent}.
 * @author Benjamin Sigg
 */
public class ComponentLayoutBlock<C extends TabPaneComponent> implements LayoutBlock{
	/** the component managed by this block */
	private C component;
	
	/**
	 * Sets the component which should be managed by this block.
	 * @param component the new component, may be <code>null</code>
	 */
	public void setComponent( C component ){
		this.component = component;
	}
	
	/**
	 * Gets the component which is managed by this block.
	 * @return the component, may be <code>null</code>
	 */
	public C getComponent(){
		return component;
	}
	
	public Dimension getMinimumSize(){
		if( component == null )
			return null;
		return component.getMinimumSize();
	}

	public Dimension getPreferredSize(){
		if( component == null )
			return null;
		return component.getPreferredSize();
	}

	public boolean isVisible(){
		return component != null;
	}

	public void setBounds( int x, int y, int width, int height ){
		if( component != null ){
			component.setBounds( new Rectangle( x, y, width, height ));
		}
	}
}
