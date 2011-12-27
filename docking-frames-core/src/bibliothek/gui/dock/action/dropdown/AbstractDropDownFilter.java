/*
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

package bibliothek.gui.dock.action.dropdown;

import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.ActionContentModifier;
import bibliothek.gui.dock.action.DropDownAction;

/**
 * A {@link DropDownFilter} which stores all properties. The properties can
 * be read by subclasses.
 * @author Benjamin Sigg
 *
 */
public abstract class AbstractDropDownFilter extends DropDownFilter {
	/** the icons provided by the selected element */
	protected Map<ActionContentModifier, Icon> icons = new HashMap<ActionContentModifier, Icon>();
	
	/** whether the selected element is enabled */
	protected boolean enabled;
	
	/** whether the selected element is selected */
	protected boolean selected;
	
	/** the text of the selected element */
	protected String text;
	
	/** the tooltip of the selected element */
	protected String tooltip;
	
	/** the {@link Dockable} which is represented by this view */
	protected Dockable representative;

	/**
	 * Creates a new filter.
	 * @param action the action to filter
	 * @param dockable the owner of <code>action</code>.
	 * @param view the view in which this action will write its properties
	 */
	public AbstractDropDownFilter( DropDownAction action, Dockable dockable, DropDownView view ){
		super( dockable, action, view );
	}
	
	public void setIcon( ActionContentModifier modifier, Icon icon ){
		if( icon == null ){	
			icons.remove( modifier );
		}
		else{
			icons.put( modifier, icon );
		}
	}
	
	public void clearIcons(){
		icons.clear();	
	}
	
	/**
	 * Gets the icon which was stored using {@link #setIcon(ActionContentModifier, Icon)}.
	 * @param modifier the context in which to use the icon
	 * @return the icon or <code>null</code>
	 */
	protected Icon getIcon( ActionContentModifier modifier ){
		return icons.get( modifier );
	}
	
	public void setEnabled( boolean enabled ){
		this.enabled = enabled;
	}
	
	public void setSelected( boolean selected ){
		this.selected = selected;
	}
	
	public void setText( String text ){
		this.text = text;
	}

	public void setTooltip( String tooltip ){
		this.tooltip = tooltip;
	}
	
	public void setDockableRepresentation( Dockable dockable ){
		this.representative = dockable;	
	}
}
