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
package bibliothek.gui.dock.station.stack.tab;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Rectangle;

import bibliothek.gui.dock.station.stack.tab.layouting.TabPlacement;

/**
 * A {@link TabPaneComponent} that really represents a {@link Component}.
 * @author Benjamin Sigg
 */
public abstract class AbstractTabPaneComponent implements TabPaneComponent{
	/** the owner of this component */
	private TabPane parent;
	
	/** how to paint this component */
	private TabPlacement orientation = TabPlacement.TOP_OF_DOCKABLE;
	
	/**
	 * Creates a new object.
	 * @param parent the owner of this object, not <code>null</code>
	 */
	public AbstractTabPaneComponent( TabPane parent ){
		if( parent == null )
			throw new IllegalArgumentException( "parent must not be null" );
		this.parent = parent;
	}
	
	/**
	 * Gets the {@link Component} which is wrapped into this
	 * {@link AbstractTabPaneComponent}. This method is not called from the 
	 * constructor.
	 * @return the wrapped {@link Component}, not <code>null</code>
	 */
	public abstract Component getComponent();
	
	public Rectangle getBounds(){
		return getComponent().getBounds();
	}

	public Dimension getMaximumSize(){
		return getComponent().getMaximumSize();
	}

	public Dimension getMinimumSize(){
		return getComponent().getMinimumSize();
	}

	public Dimension getPreferredSize(){
		return getComponent().getPreferredSize();
	}

	public TabPane getTabParent(){
		return parent;
	}

	public void setBounds( Rectangle bounds ){
		getComponent().setBounds( bounds );
	}
	
	public Insets getOverlap( TabPaneComponent other ){
		return new Insets( 0, 0, 0, 0 );
	}
	
	public void setOrientation( TabPlacement orientation ){
		if( orientation == null )
			throw new IllegalArgumentException( "orientation must not be null" );
		
		this.orientation = orientation;
	}
	
	/**
	 * Gets the orientation of this component.
	 * @return the orientation, never <code>null</code>
	 * @see #setOrientation(TabPlacement)
	 */
	public TabPlacement getOrientation(){
		return orientation;
	}
}
