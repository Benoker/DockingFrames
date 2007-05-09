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

package bibliothek.gui.dock.action.views.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Set;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;

import bibliothek.gui.dock.Dockable;
import bibliothek.gui.dock.action.ActionType;
import bibliothek.gui.dock.action.SelectableDockAction;
import bibliothek.gui.dock.event.SelectableDockActionListener;

/**
 * A handler which ensures that the selected-state of a {@link JMenuItem} and
 * a {@link SelectableDockAction} are always the same.
 * @param <M> the type of menu-item handled by this handler
 * @author Benjamin Sigg
 *
 */
public abstract class SelectableMenuHandler<M extends JMenuItem> extends AbstractMenuHandler<M, SelectableDockAction> {
	/**
	 * A handler designed to handle actions of type {@link ActionType#CHECK}.
	 * @author Benjamin Sigg
	 */
	public static class Check extends SelectableMenuHandler<JCheckBoxMenuItem>{
		/**
		 * Creates a new handler.
		 * @param action the action to handle
		 * @param dockable the owner of the action
		 */
		public Check( SelectableDockAction action, Dockable dockable ){
			super( action, dockable, new JCheckBoxMenuItem() );
		}
		
		@Override
		protected boolean allowChange( boolean newValue ){
			return true;
		}
	};
	
	/**
	 * A handler designed to handle actions of type {@link ActionType#RADIO}.
	 * @author Benjamin Sigg
	 */
	public static class Radio extends SelectableMenuHandler<JRadioButtonMenuItem>{
		/**
		 * Creates a new handler.
		 * @param action the action to handle
		 * @param dockable the owner of the action
		 */
		public Radio( SelectableDockAction action, Dockable dockable ){
			super( action, dockable, new JRadioButtonMenuItem() );
		}
		
		@Override
		protected boolean allowChange( boolean newValue ){
			return newValue;
		}
	};
	
	/** a listener intended to ensure the selection-state on the view and in the action are the same */
	private Listener listener = new Listener();
	
	/**
     * Creates a new handler
     * @param action the action to observe
     * @param dockable the dockable for which actions are dispatched
     * @param item the item to manage
     */
    public SelectableMenuHandler( final SelectableDockAction action, final Dockable dockable, M item ) {
    	super( action, dockable, item );
    }
    
    public void addActionListener( ActionListener listener ){
    	item.addActionListener( listener );
    }
    
    public void removeActionListener( ActionListener listener ){
    	item.removeActionListener( listener );
    }
    
    @Override
    public void bind(){
    	super.bind();
    	item.setSelected( getAction().isSelected( getDockable() ) );
    	action.addSelectableListener( listener );
    	item.addActionListener( listener );
    }
    
    @Override
    public void unbind(){
    	super.unbind();
    	action.removeSelectableListener( listener );
    	item.removeActionListener( listener );
    }
    
    /**
     * Tells whether the new value <code>newValue</code> can be set through
     * the view or not.
     * @param newValue the new value for the element
     * @return <code>true</code> if the value is accepted
     */
    protected abstract boolean allowChange( boolean newValue );
    
    /**
     * A listener added 
     * @author Benjamin Sigg
     */
    private class Listener implements ActionListener, SelectableDockActionListener{
    	public void actionPerformed( ActionEvent e ){
    		boolean old = action.isSelected( dockable );
    		boolean current = item.isSelected();
    		if( old != current ){
    			if( allowChange( current ))
    				action.setSelected( dockable, current );
    			else
    				item.setSelected( old );
    		}
    	}
    	public void selectedChanged( SelectableDockAction action, Set<Dockable> dockables ){
    		if( dockables.contains( dockable )){
    			boolean old = item.isSelected(); 
    			boolean selected = action.isSelected( dockable );
    			if( old != selected )
    				item.setSelected( selected );
    		}
    	}
    }
}
