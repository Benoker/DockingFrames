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

import java.util.HashSet;
import java.util.Set;

import javax.swing.Icon;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.ActionContentModifier;
import bibliothek.gui.dock.action.DropDownAction;
import bibliothek.gui.dock.themes.basic.action.dropdown.DropDownViewItem;

/**
 * An implementation of {@link DropDownFilter}. This filter uses the contents
 * of the selected element whenever possible. The tooltip either consists of
 * the drop-down-action if the selected action is not triggerable, or of
 * the action itself if it is triggerable.
 * @author Benjamin Sigg
 */
public class DefaultDropDownFilter extends AbstractDropDownFilter {
	/**
	 * A factory for this type of filter
	 */
	public static final DropDownFilterFactory FACTORY = new DropDownFilterFactory(){
		public DropDownFilter createView( DropDownAction action, Dockable dockable, DropDownView view ){
			return new DefaultDropDownFilter( action, dockable, view );
		}
	};
	
	/**
	 * Creates a new filter
	 * @param action the action to filter
	 * @param dockable the owner of <code>action</code>
	 * @param view the view where this filter will write into
	 */
	public DefaultDropDownFilter( DropDownAction action, Dockable dockable, DropDownView view ){
		super( action, dockable, view );
	}

	@Override
    public void update( DropDownViewItem selection ){
		updateEnabled( selection );
		updateSelected( selection );
		updateIcon( selection );
		updateText( selection );
		updateTooltip( selection );
		updateRepresentative( selection );
	}
	
	/**
	 * Updates the enabled-state of the {@link #getView() view}.
	 * @param selection the selected item, ignored by the default implementation
	 */
	protected void updateEnabled( DropDownViewItem selection ){
		getView().setEnabled( enabled );
	}
	
	/**
	 * Updates the {@link Dockable} which is represented by {@link #getView() the view}.
	 * @param selection the selected item, ignored by the default implementation
	 */
	protected void updateRepresentative( DropDownViewItem selection ){
		getView().setDockableRepresentation( representative );
	}
	
	/**
	 * Updates the selected-state of the {@link #getView() view}.
	 * @param selection the selected item, ignored by the default implementation
	 */
	protected void updateSelected( DropDownViewItem selection ){
		getView().setSelected( selected );
	}

	public ActionContentModifier[] getIconContexts(){
		return getView().getIconContexts();
	}
	
	/**
	 * Updates the icon of the {@link #getView() view}.
	 * @param selection the selected item, ignored by the default implementation
	 */
	protected void updateIcon( DropDownViewItem selection ){
		Set<ActionContentModifier> modifiers = new HashSet<ActionContentModifier>();
		modifiers.addAll( icons.keySet() );
		for( ActionContentModifier modifier : getView().getIconContexts() ){
			modifiers.add( modifier );
		}
		for( ActionContentModifier modifier : getAction().getIconContexts( getDockable() )){
			modifiers.add( modifier );
		}
		
		for( ActionContentModifier modifier : modifiers ){
			Icon icon = icons.get( modifier );
			if( icon == null ){
				getView().setIcon( modifier, getAction().getIcon( getDockable(), modifier ) );
			}
			else{
				getView().setIcon( modifier, icon );
			}
		}
	}
	
	/**
	 * Updates the text of the {@link #getView() view}.
	 * @param selection the selected item, ignored by the default implementation
	 */
	protected void updateText( DropDownViewItem selection ){
		if( text == null )
			getView().setText( getAction().getText( getDockable() ) );
		else
			getView().setText( text );
	}
	
	/**
	 * Updates the tooltip of the {@link #getView() view}.
	 * @param selection the selected item, ignored by the default implementation
	 */
	protected void updateTooltip( DropDownViewItem selection ){
		if( selection == null || !selection.isTriggerable( true ) ){
			String tooltip = getAction().getTooltipText( getDockable() );
			if( tooltip == null )
				tooltip = super.tooltip;
			if( tooltip == null )
				tooltip = getAction().getText( getDockable() );
			if( tooltip == null )
				tooltip = super.text;
			getView().setTooltip( tooltip );
		}
		else{
			String tooltip = super.tooltip;
			if( tooltip == null )
				tooltip = getAction().getTooltipText( getDockable() );
			if( tooltip == null )
				tooltip = super.text;
			if( tooltip == null )
				tooltip = getAction().getText( getDockable() );
			getView().setTooltip( tooltip );
		}
	}
}
