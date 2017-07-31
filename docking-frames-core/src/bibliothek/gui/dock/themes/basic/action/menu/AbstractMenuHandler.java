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

package bibliothek.gui.dock.themes.basic.action.menu;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JMenuItem;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.ActionContentModifier;
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.action.StandardDockAction;
import bibliothek.gui.dock.event.StandardDockActionListener;
import bibliothek.gui.dock.util.DockUtilities;

/**
 * A handler that connects a {@link JMenuItem} with a {@link DockAction}.
 * @param <I> DropDownItemHandle used by this handler
 * @param <D> Action used by this handler
 * @author Benjamin Sigg
 */
public abstract class AbstractMenuHandler<I extends JMenuItem, D extends StandardDockAction> implements MenuViewItem<JComponent>{
    /** the visual representation of the action, may be <code>null</code> */
    protected I item;
    
    /** the Dockable for which actions are dispatched */
    protected Dockable dockable;
    
    /** the action shown by the item of this handler, may be <code>null</code> */
    protected D action;
    
    /** a listener to the action, changes text, icon, etc.. of the item */
    private Listener listener;
    
    /**
     * Creates a new handler with predefined item.
     * @param action the action to observe
     * @param dockable the dockable for which actions are dispatched
     * @param item the item whose values have to be updated, <code>null</code> is
     * only valid if <code>action</code> is <code>null</code> too.
     */
    public AbstractMenuHandler( D action, Dockable dockable, I item ){
        this.action = action;
        this.dockable = dockable;
        this.item = item;
    }
    
    /**
     * Gets the element for which actions are dispatched.
     * @return the element
     */
    public Dockable getDockable() {
        return dockable;
    }
    
    /**
     * Gets the action that is observed by this handler.
     * @return the action, may be <code>null</code>
     */
    public D getAction() {
        return action;
    }
    
    /**
     * Gets the item whose values are updated by this handler.
     * @return the item, may be <code>null</code>
     */
    public JMenuItem getItem(){
        return item;
    }
    
    /**
     * Connects this handler to its action.
     */
    public void bind(){
        if( action != null ){
            if( listener == null ){
                action.bind( dockable );
                
                listener = new Listener();
                action.addDockActionListener( listener );

                if( item != null ){
                	item.setEnabled( action.isEnabled( dockable ));
                    item.setIcon( getIcon( ActionContentModifier.NONE_HORIZONTAL ) );
                    updateDisabledIcon();
                    item.setText( action.getText( dockable ));
                    item.setToolTipText( action.getTooltipText( dockable ));
                }
            }
            else
                throw new IllegalStateException( "Handler is already bound" );
        }
    }
    
    private void updateDisabledIcon(){
    	Icon icon = getIcon( ActionContentModifier.DISABLED_HORIZONTAL, ActionContentModifier.NONE_HORIZONTAL, ActionContentModifier.NONE );
    	if( icon == null ){
    		icon = getIcon( ActionContentModifier.NONE_HORIZONTAL );
    		icon = DockUtilities.disabledIcon( getItem(), icon );
    	}
    	
    	item.setDisabledIcon( icon );
    }
    
    private Icon getIcon( ActionContentModifier modifier, ActionContentModifier... limits ){
    	List<ActionContentModifier> modifiers = new LinkedList<ActionContentModifier>();
    	modifiers.add( modifier );
    	
    	while( !modifiers.isEmpty() ){
    		modifier = modifiers.remove( 0 );
    		if( !isLimited( modifier, limits )){
	    		Icon icon = action.getIcon( dockable, modifier );
	    		if( icon != null ){
	    			return icon;
	    		}
	    		for( ActionContentModifier backup : modifier.getBackup() ){
	    			modifiers.add( backup );
	    		}
    		}
    	}
    	return null;
    }
    
    private boolean isLimited( ActionContentModifier modifier, ActionContentModifier... limits ){
    	for( ActionContentModifier limit : limits ){
    		if( limit.equals( modifier )){
    			return true;
    		}
    	}
    	return false;
    }
    
    /**
     * Disconnects this handler from its action
     */
    public void unbind(){
        if( action != null ){
            if( listener != null ){
                action.unbind( dockable );
                action.removeDockActionListener( listener );
                listener = null;
            }
            else
                throw new IllegalStateException( "Handler is already unbound" );
        }
    }
    
    /**
     * A listener to an action, changes the values of the item of the
     * enclosing handler.
     * @author Benjamin Sigg
     */
    private class Listener implements StandardDockActionListener{
        public void actionEnabledChanged( StandardDockAction action, Set<Dockable> dockables ) {
        	if( item != null ){
        		item.setEnabled( action.isEnabled( dockable ));
        	}
        }

        public void actionIconChanged( StandardDockAction action, ActionContentModifier modifier, Set<Dockable> dockables ){
        	if( item != null ){
	        	if( modifier == null || modifier == ActionContentModifier.NONE_HORIZONTAL ){
	        		item.setIcon( getIcon( ActionContentModifier.NONE_HORIZONTAL ) );
	        	}
	        	else if( modifier == null || modifier == ActionContentModifier.NONE ){
	        		item.setIcon( getIcon( ActionContentModifier.NONE_HORIZONTAL ) );
	        	}
	        	if( modifier == null || modifier == ActionContentModifier.DISABLED || modifier == ActionContentModifier.DISABLED_HORIZONTAL ){
	        		updateDisabledIcon();
	        	}
        	}
        }

        public void actionTextChanged( StandardDockAction action, Set<Dockable> dockables ) {
        	if( item != null ){
        		item.setText( action.getText( dockable ));
        	}
        }

        public void actionTooltipTextChanged( StandardDockAction action, Set<Dockable> dockables ) {
        	if( item != null ){
        		item.setToolTipText( action.getTooltipText( dockable ));
        	}
        }
        
        public void actionRepresentativeChanged( StandardDockAction action, Set<Dockable> dockables ){
	        // ignore	
        }
    }
}
