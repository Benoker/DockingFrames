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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.Icon;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.ActionContentModifier;
import bibliothek.gui.dock.action.ActionType;
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.action.SelectableDockAction;
import bibliothek.gui.dock.action.view.ActionViewConverter;
import bibliothek.gui.dock.action.view.ViewTarget;
import bibliothek.gui.dock.disable.DisablingStrategy;
import bibliothek.gui.dock.event.SelectableDockActionListener;

/**
 * A {@link DockAction} that has a state <code>selected</code>. That state
 * has always the same value for every {@link Dockable} using this action.
 * @author Benjamin Sigg
 */
public abstract class SimpleSelectableAction extends SimpleDropDownItemAction implements SharingSelectableDockAction, SelectableDockAction{
	/** observers of this action */
	private List<SelectableDockActionListener> listeners = new ArrayList<SelectableDockActionListener>();
	/** whether this action is selected or not */
	private boolean selected = false;
	
	/** icons to be used if this action is selected */
	private Map<ActionContentModifier, Icon> selectIcons = new HashMap<ActionContentModifier, Icon>();

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
			this( true );
		}
		
		/**
		 * Creates a new action
		 * @param monitorDisabling whether the current {@link DisablingStrategy} should be monitored
		 */
		public Check( boolean monitorDisabling ){
			super( ActionType.CHECK, monitorDisabling );
			setDropDownSelectable( false );
		}
		
		public boolean trigger( Dockable dockable ) {
		    if( !isEnabled( dockable ))
                return false;
            
            setSelected( dockable, !isSelected( dockable ) );
            return true;
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
			this( true );
		}
		
		/**
		 * Creates a new action
		 * @param monitorDisabling whether the current {@link DisablingStrategy} should be monitored
		 */
		public Radio( boolean monitorDisabling ){
			super( ActionType.RADIO, monitorDisabling );
			setDropDownTriggerableSelected( false );
		}
		
		public boolean trigger( Dockable dockable ) {
		    if( !isEnabled( dockable ) || isSelected( dockable ))
                return false;
            
            setSelected( dockable, true );
            return true;
		}
	}
	
	/**
	 * Creates a new action.
	 * @param type how this action is to be visualized
	 * @param monitorDisabling whether the current {@link DisablingStrategy} should be monitored
	 */
	public SimpleSelectableAction( ActionType<SelectableDockAction> type, boolean monitorDisabling ){
		super( monitorDisabling );
		
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
		Set<Dockable> dockables = getBoundDockables();
		for( SelectableDockActionListener listener : listeners.toArray( new SelectableDockActionListener[ listeners.size() ] ))
			listener.selectedChanged( this, dockables );
	}
	
	public boolean isSelected( Dockable dockable ){
		return selected;
	}
	
	public void setSelected( Dockable dockable, boolean selected ){
		setSelected( selected );
	}

    public boolean isSelected() {
        return selected;
    }
	
	public void setSelected( boolean selected ){
		if( selected != this.selected ){
			this.selected = selected;
			fireSelectedChanged();
			fireActionIconChanged( null, getBoundDockables() );
		}
	}
	
	@Override
	public Icon getIcon( Dockable dockable, ActionContentModifier modifier ){
		if( isSelected() )
			return firstNonNull( selectIcons.get( modifier ), super.getIcon( dockable, modifier ) );
		else
			return super.getIcon( dockable, modifier );
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
	
    @Override
    public ActionContentModifier[] getIconContexts( Dockable dockable ){
    	Set<ActionContentModifier> result = new HashSet<ActionContentModifier>();
    	for( ActionContentModifier modifier : super.getIconContexts( dockable )){
    		result.add( modifier );
    	}
    	result.addAll( selectIcons.keySet() );
    	return result.toArray( new ActionContentModifier[ result.size() ] );
    }
    
    /**
     * Sets the icon which is shown if this action is selected
     * @param icon the icon to show, or <code>null</code>
     */
    public void setSelectedIcon( Icon icon ){
    	setSelectedIcon( ActionContentModifier.NONE, icon );
    }
    
    /**
     * Gets the icon which is shown if this action is selected
     * @return the icon or <code>null</code>
     */
    public Icon getSelectedIcon(){
    	return getSelectedIcon( ActionContentModifier.NONE );
    }
    
    /**
     * Sets the icon which is shown if this action is selected but disabled
     * @param icon the icon to show, or <code>null</code>
     */
    public void setDisabledSelectedIcon( Icon icon ){
    	setSelectedIcon( ActionContentModifier.DISABLED, icon );
    }
    
    /**
     * Gets the icon which is shown if this action is selected but disabled
     * @return the icon or <code>null</code>
     */
    public Icon getDisabledSelectedIcon(){
    	return getSelectedIcon( ActionContentModifier.DISABLED );
    }
    
    public Icon getSelectedIcon( ActionContentModifier modifier ){
    	return selectIcons.get( modifier );
    }
    
    public void setSelectedIcon( ActionContentModifier modifier, Icon selectedIcon ){
    	if( selectedIcon == null ){
    		selectIcons.remove( modifier );
    	}
    	else{
    		selectIcons.put( modifier, selectedIcon );
    	}
    	fireActionIconChanged( modifier, getBoundDockables() );
    }    
}
