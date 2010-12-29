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
package bibliothek.gui.dock.control.focus;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.event.DockableFocusEvent;
import bibliothek.gui.dock.event.DockableFocusListener;
import bibliothek.gui.dock.event.FocusVetoListener;
import bibliothek.gui.dock.event.FocusVetoListener.FocusVeto;
import bibliothek.gui.dock.title.DockTitle;

/**
 * Abstract implementation of {@link FocusController} offering methods to 
 * handle the listeners.
 * @author Benjamin Sigg
 */
public abstract class AbstractFocusController implements FocusController{
	/** the owner of this focus controller */
	private DockController controller;
	
	/** strategy that decides which {@link Component} to focus */
	private FocusStrategy strategy;
	
    /** Listeners observing the focused {@link Dockable} */
    private List<DockableFocusListener> dockableFocusListeners = new ArrayList<DockableFocusListener>();

    /** A list of listeners which can cancel a call to the controller */
    private List<FocusVetoListener> vetos = new ArrayList<FocusVetoListener>();
    
    /** how often this focus controller was frozen */
    private int frozen = 0;
    
    /**
     * Creates a new focus controller
     * @param controller the owner of this controller, not <code>null</code>
     */
    public AbstractFocusController( DockController controller ){
    	this.controller = controller;
    }
    
    public void setStrategy( FocusStrategy strategy ){
    	if( this.strategy != strategy ){
    		if( this.strategy != null ){
    			this.strategy.unbind();
    		}
    		
    		this.strategy = strategy;
    		
    		if( strategy != null ){
    			strategy.bind();
    		}
    	}
    }
    
    public FocusStrategy getStrategy(){
    	return strategy;
    }
    
    public void freezeFocus(){
	    frozen++;	
    }
    
    public void meltFocus(){
	    frozen--;	
    }
    
    /**
     * Tells whether this {@link FocusController} is temporarily frozen and should
     * not process any events.
     * @return <code>true</code> if disabled
     */
    protected boolean isFrozen(){
    	return frozen > 0;
    }
    
    /**
     * Adds a listener to this controller which can cancel a call to
     * the {@link DockController}.
     * @param listener the new listener
     */
    public void addVetoListener( FocusVetoListener listener ){
        vetos.add( listener );
    }
    
    /**
     * Removes a listener from this controller
     * @param listener the listener to remove
     */
    public void removeVetoListener( FocusVetoListener listener ){
        vetos.remove( listener );
    }
    
    /**
     * Asks all {@link FocusVetoListener} through their method
     * {@link FocusVetoListener#vetoFocus(FocusController, DockTitle)}
     * whether they want cancel a call to the {@link DockController}.
     * @param title the title which is about to be focused
     * @return the first veto
     */
    protected FocusVeto fireVetoTitle( DockTitle title ){
        for( FocusVetoListener listener : vetos.toArray( new FocusVetoListener[ vetos.size() ] )){
        	FocusVeto veto = listener.vetoFocus( this, title );
        	if( veto != FocusVeto.NONE )
        		return veto;
        }
        
        return FocusVeto.NONE;
    }
    
    /**
     * Asks all {@link FocusVetoListener} through their method
     * {@link FocusVetoListener#vetoFocus(FocusController, Dockable)}
     * whether they want cancel a call to the {@link DockController}.
     * @param dockable the Dockable which is about to be focused
     * @return the first veto
     */    
    protected FocusVeto fireVetoDockable( Dockable dockable ){
    	for( FocusVetoListener listener : vetos.toArray( new FocusVetoListener[ vetos.size() ] )){
        	FocusVeto veto = listener.vetoFocus( this, dockable );
        	if( veto != FocusVeto.NONE )
        		return veto;
        }
        
        return FocusVeto.NONE;
    }

    public DockController getController(){
	    return controller;
    }
    
    /**
     * Adds a listener to this controller, the listener will be informed when
     * the focused {@link Dockable} changes.
     * @param listener the new listener
     */
    public void addDockableFocusListener( DockableFocusListener listener ){
        if( listener == null )
            throw new NullPointerException( "listener must not be null" );
        dockableFocusListeners.add( listener );
    }
    
    /**
     * Removes a listener from this controller.
     * @param listener the listener to remove
     */
    public void removeDockableFocusListener( DockableFocusListener listener ){
        if( listener == null )
            throw new NullPointerException( "listener must not be null" );
        dockableFocusListeners.remove( listener );
    }
    

    /**
     * Gets an array of currently registered {@link DockableFocusListener}s.
     * @return the modifiable array
     */
    protected DockableFocusListener[] dockableFocusListeners(){
        return dockableFocusListeners.toArray( new DockableFocusListener[ dockableFocusListeners.size() ] );
    }
    
    /**
     * Informs all listeners that <code>dockable</code> has gained
     * the focus.
     * @param oldFocused the old owner of the focus, may be <code>null</code>
     * @param newFocused the owner of the focus, may be <code>null</code>
     */
    protected void fireDockableFocused( Dockable oldFocused, Dockable newFocused ){
        DockableFocusEvent event = new DockableFocusEvent( controller, oldFocused, newFocused );
        
        for( DockableFocusListener listener : dockableFocusListeners() )
            listener.dockableFocused( event );
    }
}
