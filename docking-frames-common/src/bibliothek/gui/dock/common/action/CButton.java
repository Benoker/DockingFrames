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
package bibliothek.gui.dock.common.action;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;

import bibliothek.gui.dock.common.action.core.CommonSimpleButtonAction;
import bibliothek.gui.dock.common.intern.action.CDropDownItem;

/**
 * A simple button, the user clicks onto the button and {@link #action()} is called.
 * @author Benjamin Sigg
 */
public class CButton extends CDropDownItem<CommonSimpleButtonAction> {
	/** all the registered {@link ActionListener} */
	private List<ActionListener> listeners = new ArrayList<ActionListener>();
	
    /**
     * Creates the new button
     */
    public CButton(){
        super( null );
        init( new CommonSimpleButtonAction( this ));
    }
    
    /**
     * Creates and initializes this button. 
     * @param action the action that represents this action, can be <code>null</code> but then 
     * subclasses have to call {@link #init(CommonSimpleButtonAction)}
     */
    protected CButton( CommonSimpleButtonAction action ){
    	super( null );
    	if( action != null ){
    		init( action );
    	}
    }
    
    /**
     * Creates a new button.
     * @param text the text of this button
     * @param icon the icon of this button
     */
    public CButton( String text, Icon icon ){
        this();
        setText( text );
        setIcon( icon );
    }
    
    @Override
    protected void init( CommonSimpleButtonAction action ){
    	super.init( action );
        action.addActionListener( new ActionListener(){
            public void actionPerformed( ActionEvent e ) {
                action();
            }
        });
    }
    
    /**
     * Adds <code>listener</code> to this button, <code>listener</code> will be called
     * whenever this button it triggered.
     * @param listener the new listener, not <code>null</code>
     */
    public void addActionListener( ActionListener listener ){
    	if( listener == null ){
    		throw new IllegalArgumentException( "listener must not be null" );
    	}
    	listeners.add( listener );
    }
    
    /**
     * Removes <code>listener</code> from this button.
     * @param listener the listener to remove
     */
    public void removeActionListener( ActionListener listener ){
    	listeners.remove( listener );
    }
    
    /**
     * Invoked when the user clicks onto this button. The default behavior
     * is to call {@link #fire()}.
     */
    protected void action(){
    	fire();
    }
    
    /**
     * Informs all {@link ActionListener}s that this button was clicked.
     */
    protected void fire(){
    	ActionEvent event = new ActionEvent( this, ActionEvent.ACTION_PERFORMED, null );
    	
    	for( ActionListener listener : listeners.toArray( new ActionListener[ listeners.size() ] )){
    		listener.actionPerformed( event );
    	}
    }
}
