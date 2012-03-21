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

package bibliothek.gui.dock.action.actions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.Icon;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.ActionType;
import bibliothek.gui.dock.action.SelectableDockAction;
import bibliothek.gui.dock.action.view.ActionViewConverter;
import bibliothek.gui.dock.action.view.ViewTarget;
import bibliothek.gui.dock.event.SelectableDockActionListener;

/**
 * An action that can change between selected and not selected. 
 * @author Benjamin Sigg
 *
 * @param <K> the type of key to distinguish between groups of Dockables
 */
public abstract class GroupedSelectableDockAction<K> extends GroupedDropDownItemAction<K, SimpleSelectableAction> implements SelectableDockAction{
	/**
	 * An action intended to use as type {@link ActionType#CHECK}
	 * @author Benjamin Sigg
	 * @param <K> the type of key used to distinguish between groups
	 */
	public static abstract class Check<K> extends GroupedSelectableDockAction<K>{
		/**
		 * Creates a new action.
		 * @param generator the generator to create new keys for unknown 
		 * Dockables
		 */
		public Check( GroupKeyGenerator<? extends K> generator ){
			super( generator, ActionType.CHECK );
		}
		
		@Override
		protected SimpleSelectableAction createGroup( SelectableDockActionListener listener ){
			SimpleSelectableAction action = new SimpleSelectableAction.Check( false );
			action.addSelectableListener( listener );
			return action;
		}
	};

	/**
	 * An action intended to use as type {@link ActionType#RADIO}
	 * @author Benjamin Sigg
	 * @param <K> the type of key used to distinguish between groups
	 */
	public static abstract class Radio<K> extends GroupedSelectableDockAction<K>{
		/**
		 * Creates a new action.
		 * @param generator the generator to create new keys for unknown 
		 * Dockables
		 */
		public Radio( GroupKeyGenerator<? extends K> generator ){
			super( generator, ActionType.RADIO );
		}
		
		@Override
        protected SimpleSelectableAction createGroup( SelectableDockActionListener listener ){
			SimpleSelectableAction action = new SimpleSelectableAction.Radio( false );
			action.addSelectableListener( listener );
			return action;
		}
	};
	
	/** Listeners added to this action */
	private List<SelectableDockActionListener> listeners = new ArrayList<SelectableDockActionListener>();
	
	/** the type of this action */
	private ActionType<SelectableDockAction> type;
	
	/** A listener added to all children of this action */
	private SelectableDockActionListener listener = new SelectableDockActionListener(){
		public void selectedChanged( SelectableDockAction action, Set<Dockable> dockables ){
			fireSelectedChanged( dockables );
		}
	};

	/**
	 * Creates a new action.
	 * @param generator a generator to create keys for Dockables which are not
	 * yet in a group.
	 * @param type the type of this action
	 */
	public GroupedSelectableDockAction( GroupKeyGenerator<? extends K> generator, ActionType<SelectableDockAction> type ){
		super( generator );
		
		if( type == null )
			throw new IllegalArgumentException( "Type must not be null" );
		
		this.type = type;
	}
	
	@Override
	public void setGroup( K key, Dockable dockable ){
		super.setGroup( key, dockable );
		fireSelectedChanged( dockable );
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
	
	@Override
	protected SimpleSelectableAction createGroup( K key ){
		return createGroup( listener );
	}
	
	/**
	 * Creates a new group and adds a listener to the group.
	 * @param listener the listener to add
	 * @return the new group
	 */
	protected abstract SimpleSelectableAction createGroup( SelectableDockActionListener listener );
	
	/**
	 * Fires a change-event on all known listeners.
	 * @param dockable the Dockable whose state has changed
	 */
	protected void fireSelectedChanged( Dockable dockable ){
		Set<Dockable> set = new HashSet<Dockable>();
		set.add( dockable );
		fireSelectedChanged( set );
	}
	
	/**
	 * Fires a change-event on all known listeners.
	 * @param dockables the Dockables whose state has been changed
	 */
	protected void fireSelectedChanged( Set<Dockable> dockables ){
		for( SelectableDockActionListener listener : listeners.toArray( new SelectableDockActionListener[ listeners.size() ] ))
			listener.selectedChanged( this, dockables );
	}
	
	public boolean isSelected( Dockable dockable ){
		return getGroup( dockable ).isSelected( dockable );
	}
	
	public void setSelected( Dockable dockable, boolean selected ){
		getGroup( dockable ).setSelected( dockable, selected );
	}
	
	/**
     * Sets the selected-state of the group <code>key</code>.
     * If the group does not exist, it will be created.
     * @param key The name of the group
     * @param selected The new state of the group
     */
    public void setSelected( K key, boolean selected ){
        ensureGroup( key ).setSelected( selected );
    }
    
    /**
     * Gets the selected-state property of the group <code>key</code>.
     * @param key The name of the group
     * @return The state
     * @throws IllegalArgumentException If the group does not exist
     * @see #setSelected(Object, boolean)
     */
    public boolean isSelected( Object key ){
        SimpleSelectableAction action = getGroup( key );
        if( action == null )
            throw new IllegalArgumentException( "There is no such group" );
        return action.isSelected();
    }
    
    /**
     * Sets the <code>icon</code> that will be shown when the group
     * named <code>key</code> is selected. If the group does not
     * exist, it will be created.
     * @param key The name of the group
     * @param icon The selected-icon, may be <code>null</code>
     */
    public void setSelectedIcon( K key, Icon icon ){
        ensureGroup( key ).setSelectedIcon( icon );
    }
    
    /**
     * Gets the icon that is shown when the group named <code>key</code>
     * is in the selected-state.
     * @param key The name of the group
     * @return The selected-icon, may be <code>null</code>
     * @throws IllegalArgumentException if the group does not exist
     * @see #setDisabledIcon(Object, Icon)
     */
    public Icon getSelectedIcon( Object key ){
    	SimpleSelectableAction action = getGroup( key );
        if( action == null )
            throw new IllegalArgumentException( "There is no such group" );
        return action.getSelectedIcon();
    }    
    
    /**
     * Sets the <code>icon</code> that will be shown when the group
     * <code>key</code> is disabled and selected. If the group
     * does not exist, it will be created.
     * @param key The name of the group
     * @param icon The icon to display, when the group is 
     * selected and disabled, may be <code>null</code>
     */
    public void setDisabledSelectedIcon( K key, Icon icon ){
        ensureGroup( key ).setDisabledSelectedIcon( icon );
    }
    
    /**
     * Gets the icon that is shown when the group <code>key</code>
     * is selected and disabled.
     * @param key The name of the group
     * @return The disabled-selected-icon, may be <code>null</code>
     * @throws IllegalArgumentException if the group does not exist
     * @see #setDisabledSelectedIcon(Object, Icon)
     */
    public Icon getDisabledSelectedIcon( Object key ){
    	SimpleSelectableAction action = getGroup( key );
        if( action == null )
            throw new IllegalArgumentException( "There is no such group" );
        return action.getDisabledSelectedIcon();
    }
}
