/**
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

package bibliothek.gui.dock.action.actions;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.swing.Icon;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.ActionType;
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.action.SelectableDockAction;
import bibliothek.gui.dock.action.view.ActionViewConverter;
import bibliothek.gui.dock.action.view.ViewTarget;
import bibliothek.gui.dock.event.SelectableDockActionListener;

/**
 * A {@link DockAction} that has a state <code>selected</code>. That state
 * has always the same value for every {@link Dockable} using this action.
 * @author Benjamin Sigg
 */
public class SimpleSelectableAction extends SimpleDropDownItemAction implements SelectableDockAction{
	/** observers of this action */
	private List<SelectableDockActionListener> listeners = new ArrayList<SelectableDockActionListener>();
	/** whether this action is selected or not */
	private boolean selected = false;
	/** the Icon shown if this action is selected and enabled */
	private Icon selectedIcon;
	/** the Icon shown if this action is selected but not enabled */
	private Icon disabledSelectedIcon;
	/** how this action should be visualized */
	private ActionType<SelectableDockAction> type;
	
	/**
	 * A {@link SimpleSelectableAction} that is visualized as
	 * a {@link ActionType#CHECK}.
	 * @author Benjamin Sigg
	 */
	public static class Check extends SimpleSelectableAction{
		/**
		 * Creates the new action
		 */
		public Check(){
			super( ActionType.CHECK );
			setDropDownSelectable( false );
		}
	};

	/**
	 * A {@link SimpleSelectableAction} that is visualized as
	 * a {@link ActionType#RADIO}.
	 * @author Benjamin Sigg
	 */
	public static class Radio extends SimpleSelectableAction{
		/**
		 * Creates the new action
		 */
		public Radio(){
			super( ActionType.RADIO );
			setDropDownTriggerableSelected( false );
		}
	}
	
	/**
	 * Creates a new action.
	 * @param type how this action is to be visualized
	 */
	public SimpleSelectableAction( ActionType<SelectableDockAction> type ){
		if( type == null )
			throw new IllegalArgumentException( "Type must not be null" );
		
		this.type = type;
	}
	
	public <V> V createView( ViewTarget<V> target, ActionViewConverter converter, Dockable dockable ){
		return converter.createView( type, this, target, dockable );
	}
	
	public void addSelectableListener( SelectableDockActionListener listener ){
		listeners.add( listener );
	}

	public void removeSelectableListener( SelectableDockActionListener listener ){
		listeners.remove( listener );
	}

	/**
	 * Fires an event to all observers of type {@link SelectableDockActionListener}.
	 */
	protected void fireSelectedChanged(){
		Set<Dockable> dockables = getBindeds();
		for( SelectableDockActionListener listener : listeners.toArray( new SelectableDockActionListener[ listeners.size() ] ))
			listener.selectedChanged( this, dockables );
	}
	
	public boolean isSelected( Dockable dockable ){
		return selected;
	}
	
	public void setSelected( Dockable dockable, boolean selected ){
		setSelected( selected );
	}

    /**
     * Gets the selected-state of this action.
     * @return The current state
     * @see #setSelected(boolean)
     */
    public boolean isSelected() {
        return selected;
    }
	
    /**
     * Sets the state of this action. The action will notify all listeners
     * about the new state.
     * @param selected the new state
     */
	public void setSelected( boolean selected ){
		if( selected != this.selected ){
			this.selected = selected;
			fireSelectedChanged();
			fireActionIconChanged( getBindeds() );
			fireActionDisabledIconChanged( getBindeds() );
		}
	}
	
	@Override
	public Icon getIcon( Dockable dockable ){
		if( isSelected() )
			return firstNonNull( selectedIcon, super.getIcon( dockable ) );
		else
			return super.getIcon( dockable );
	}
	
	/**
     * Gets the first element of <code>icons</code> that is not <code>null</code>.
     * @param icons a list of icons
     * @return the first non-null icon
     */
    protected Icon firstNonNull( Icon...icons ){
        for( Icon icon : icons )
            if( icon != null )
                return icon;
        
        return null;
    }
	
	/**
     * Gets the icon that is shown when this action is selected.
     * @return The selected-icon, may be <code>null</code>
     * @see #setSelectedIcon(Icon)
     * @see #isSelected()
     */
    public Icon getSelectedIcon() {
        return selectedIcon;
    }
    
    /**
     * Sets the icon that will be shown, when this action is selected.
     * @param selectedIcon The icon, can be <code>null</code>
     * @see #setSelected(boolean)
     */
    public void setSelectedIcon( Icon selectedIcon ) {
        this.selectedIcon = selectedIcon;
        fireActionIconChanged( getBindeds() );
    }
    
    /**
     * Gets the icon that is shown, when this action is selected but
     * not enabled.
     * @return The icon, may be <code>null</code>
     * @see #setDisabledSelectedIcon(Icon)
     * @see #isEnabled()
     * @see #isSelected()
     */
    public Icon getDisabledSelectedIcon() {
        return disabledSelectedIcon;
    }
    
    @Override
    public Icon getDisabledIcon( Dockable dockable ){
    	if( selected )
    		return firstNonNull( disabledSelectedIcon, super.getDisabledIcon( dockable ) );
    	else
    		return super.getDisabledIcon( dockable );
    }
    
    /**
     * Sets the icon that will be shown when this action is selected
     * but not enabled.
     * @param disabledSelectedIcon The icon, <code>null</code> is allowed
     * @see #setSelected(boolean)
     * @see #setEnabled(boolean)
     */
    public void setDisabledSelectedIcon( Icon disabledSelectedIcon ) {
        this.disabledSelectedIcon = disabledSelectedIcon;
        fireActionDisabledIconChanged( getBindeds() );
    }
}
