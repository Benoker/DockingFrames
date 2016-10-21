/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2010 Benjamin Sigg
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

import javax.swing.Icon;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.StackDockStation;
import bibliothek.gui.dock.station.stack.StackDockComponent;
import bibliothek.gui.dock.station.stack.TabContent;

/**
 * This {@link TabContentFilter} offers a set of predefined behavioral patterns.
 * @author Benjamin Sigg
 */
public class DefaultTabContentFilter extends AbstractTabContentFilter {
	/**
	 * Tells a {@link DefaultTabContentFilter} how it behaves.
	 */
	public static enum Behavior{
		/** paint icon and text */
		ALL,
		
		/** paint only the icon */
		ICON_ONLY,
		
		/** paint only the text */
		TEXT_ONLY,
		
		/** paint icon, or if missing paint text */
		ICON_OVER_TEXT,
		
		/** paint text, or if missing paint icon */
		TEXT_OVER_ICON;
	}
	
	/** how to paint a deselected item */
	private Behavior deselected;
	
	/** how to paint a selected item */
	private Behavior selected;
	
	/**
	 * Creates a new filter using the behavior {@link Behavior#ALL}.
	 */
	public DefaultTabContentFilter(){
		this( Behavior.ALL );
	}
	
	/**
	 * Creates a new filter.
	 * @param behavior the behavior applied to all elements
	 */
	public DefaultTabContentFilter( Behavior behavior ){
		this( behavior, behavior );
	}
	
	/**
	 * Creates a new filter.
	 * @param selected the behavior applied to selected elements
	 * @param deselected the behavior applied to unselected elements
	 */
	public DefaultTabContentFilter( Behavior selected, Behavior deselected ){
		setSelected( selected );
		setDeselected( deselected );
	}
	
	/**
	 * Sets the behavior that should be applied on a selected element.
	 * @param selected the new behavior, not <code>null</code>
	 */
	public void setSelected( Behavior selected ){
		if( selected == null ){
			throw new IllegalArgumentException( "selected must not be null" );
		}
		if( this.selected != selected ){
			this.selected = selected;
			fireChanged();
		}
	}
	
	/**
	 * Gets the behavior that is applied to selected elements.
	 * @return the behavior, not <code>null</code>
	 */
	public Behavior getSelected(){
		return selected;
	}
	
	/**
	 * Sets the behavior that should be applied on an unselected element.
	 * @param deselected the new behavior, not <code>null</code>
	 */
	public void setDeselected( Behavior deselected ){
		if( deselected == null ){
			throw new IllegalArgumentException( "deselected must not be null" );
		}
		if( this.deselected != deselected ){
			this.deselected = deselected;
			fireChanged();
		}
	}
	
	/**
	 * Gets the behavior that is applied to unselected elements.
	 * @return the behavior, not <code>null</code>
	 */
	public Behavior getDeselected(){
		return deselected;
	}
	
	@Override
	public TabContent filter( TabContent content, StackDockStation station, Dockable dockable ){
		boolean selected = station.getFrontDockable() == dockable;
		return filter( content, selected ? this.selected : this.deselected );
	}
	
	@Override
	public TabContent filter( TabContent content, StackDockComponent component, Dockable dockable ){
		int selection = component.getSelectedIndex();
		boolean selected = selection >= 0 && component.getDockableAt( selection ) == dockable;
		return filter( content, selected ? this.selected : this.deselected );
	}
	
	@Override
	protected void selected( StackDockStation station, Dockable dockable ){
		if( selected != deselected ){
			fireChanged( dockable );
		}
	}
	
	@Override
	protected void deselected( StackDockStation station, Dockable dockable ){
		if( selected != deselected ){
			fireChanged( dockable );
		}
	}
	
	@Override
	protected void selectionChanged( StackDockComponent component ){
		if( selected != deselected ){
			fireChanged( component );
		}
	}
	
	private TabContent filter( TabContent content, Behavior behavior ){
		Icon icon = content.getIcon();
		String title = content.getTitle();
		String tooltip = content.getTooltip();
		
		switch( behavior ){
			case ICON_ONLY:
				title = null;
				break;
			case ICON_OVER_TEXT:
				if( icon != null ){
					title = null;
				}
				break;
			case TEXT_ONLY:
				icon = null;
				break;
			case TEXT_OVER_ICON:
				if( title != null ){
					icon = null;
				}
				break;
		}
		
		return new TabContent( icon, title, tooltip );
	}
}
