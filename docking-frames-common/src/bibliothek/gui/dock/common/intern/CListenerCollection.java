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
package bibliothek.gui.dock.common.intern;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import bibliothek.gui.dock.common.action.CAction;
import bibliothek.gui.dock.common.event.CDockableLocationEvent;
import bibliothek.gui.dock.common.event.CDockableLocationListener;
import bibliothek.gui.dock.common.event.CDockablePropertyListener;
import bibliothek.gui.dock.common.event.CDockableStateListener;
import bibliothek.gui.dock.common.event.CDoubleClickListener;
import bibliothek.gui.dock.common.event.CFocusListener;
import bibliothek.gui.dock.common.event.CKeyboardListener;
import bibliothek.gui.dock.common.event.CVetoClosingEvent;
import bibliothek.gui.dock.common.event.CVetoClosingListener;
import bibliothek.gui.dock.common.event.CVetoFocusListener;
import bibliothek.gui.dock.common.mode.ExtendedMode;

/**
 * A collection of the listeners which are normally added to a {@link CDockable}.
 * This class can be used by subclasses of {@link CDockable} to easily store
 * listeners and fire events.
 * @author Benjamin Sigg
 */
public class CListenerCollection {
    /** a list of state listeners that were added to this dockable */
    private List<CDockableStateListener> stateListeners = new ArrayList<CDockableStateListener>();
    
    private CDockableStateListener stateListener = new CDockableStateListener(){
    	public void extendedModeChanged( CDockable dockable, ExtendedMode mode ){
    	    for( CDockableStateListener listener : getCDockableStateListeners() )
                listener.extendedModeChanged( dockable, mode );
        }

        public void visibilityChanged( CDockable dockable ) {
            for( CDockableStateListener listener : getCDockableStateListeners() )
                listener.visibilityChanged( dockable );
        }
    };
    
    /** a list of property listeners that were added to this dockable */
    private List<CDockablePropertyListener> propertyListeners = new ArrayList<CDockablePropertyListener>();
    
    private CDockablePropertyListener propertyListener = new CDockablePropertyListener(){
        public void actionChanged( CDockable dockable, String key, CAction oldAction, CAction newAction ) {
            for( CDockablePropertyListener listener : getCDockablePropertyListeners() )
                listener.actionChanged( dockable, key, oldAction, newAction );
        }

        public void closeableChanged( CDockable dockable ) {
            for( CDockablePropertyListener listener : getCDockablePropertyListeners() )
                listener.closeableChanged( dockable );
        }

        public void externalizableChanged( CDockable dockable ) {
            for( CDockablePropertyListener listener : getCDockablePropertyListeners() )
                listener.externalizableChanged( dockable );
        }

        public void maximizableChanged( CDockable dockable ) {
            for( CDockablePropertyListener listener : getCDockablePropertyListeners() )
                listener.maximizableChanged( dockable );
        }

        public void minimizableChanged( CDockable dockable ) {
            for( CDockablePropertyListener listener : getCDockablePropertyListeners() )
                listener.minimizableChanged( dockable );
        }
        
        public void normalizeableChanged( CDockable dockable ){
        	for( CDockablePropertyListener listener : getCDockablePropertyListeners() )
                listener.normalizeableChanged( dockable );	
        }

        public void minimizeSizeChanged( CDockable dockable ) {
            for( CDockablePropertyListener listener : getCDockablePropertyListeners() )
                listener.minimizeSizeChanged( dockable );
        }

        public void stickyChanged( CDockable dockable ) {
            for( CDockablePropertyListener listener : getCDockablePropertyListeners() )
                listener.stickyChanged( dockable );
        }

        public void stickySwitchableChanged( CDockable dockable ){
        	for( CDockablePropertyListener listener : getCDockablePropertyListeners() )
                listener.stickySwitchableChanged( dockable );
        }
        
        public void resizeLockedChanged( CDockable dockable ) {
            for( CDockablePropertyListener listener : getCDockablePropertyListeners() )
                listener.resizeLockedChanged( dockable );
        }

        public void titleShownChanged( CDockable dockable ) {
            for( CDockablePropertyListener listener : getCDockablePropertyListeners() )
                listener.titleShownChanged( dockable );
        }
        
        public void singleTabShownChanged( CDockable dockable ){
	        for( CDockablePropertyListener listener : getCDockablePropertyListeners() )
	        	listener.singleTabShownChanged( dockable );
        }
        
        public void enabledChanged( CDockable dockable ){
        	for( CDockablePropertyListener listener : getCDockablePropertyListeners() )
	        	listener.enabledChanged( dockable );
        }
    };
    
    /** the list of focus listeners */
    private List<CFocusListener> focusListeners = new ArrayList<CFocusListener>();
    
    private CFocusListener focusListener = new CFocusListener(){
        public void focusGained( CDockable dockable ) {
            for( CFocusListener listener : getFocusListeners() )
                listener.focusGained( dockable );
        }

        public void focusLost( CDockable dockable ) {
            for( CFocusListener listener : getFocusListeners() )
                listener.focusLost( dockable );
        }
    };
    
    private List<CVetoFocusListener> vetoFocusListeners = new ArrayList<CVetoFocusListener>();
    
    private CVetoFocusListener vetoFocusListener = new CVetoFocusListener(){
    	public boolean willGainFocus( CDockable dockable ){
	    	for( CVetoFocusListener listener : getVetoFocusListeners() ){
	    		if( !listener.willGainFocus( dockable )){
	    			return false;
	    		}
	    	}
	    	return true;
    	}
    	
    	public boolean willLoseFocus( CDockable dockable ){
    		for( CVetoFocusListener listener : getVetoFocusListeners() ){
	    		if( !listener.willLoseFocus( dockable )){
	    			return false;
	    		}
	    	}
	    	return true;
    	}
    };
    
    private List<CKeyboardListener> keyboardListeners = new ArrayList<CKeyboardListener>();
    
    private CKeyboardListener keyboardListener = new CKeyboardListener(){
        public boolean keyPressed( CDockable source, KeyEvent event ) {
            for( CKeyboardListener listener : getKeyboardListeners() ){
                if( listener.keyPressed( source, event ))
                    return true;
            }
            return false;
        }

        public boolean keyReleased( CDockable source, KeyEvent event ) {
            for( CKeyboardListener listener : getKeyboardListeners() ){
                if( listener.keyReleased( source, event ))
                    return true;
            }
            return false;
        }

        public boolean keyTyped( CDockable source, KeyEvent event ) {
            for( CKeyboardListener listener : getKeyboardListeners() ){
                if( listener.keyTyped( source, event ))
                    return true;
            }
            return false;
        }
    };
    
    private List<CDoubleClickListener> doubleClickListeners = new ArrayList<CDoubleClickListener>();
    
    private CDoubleClickListener doubleClickListener = new CDoubleClickListener(){
        public boolean clicked( CDockable source, MouseEvent event ) {
            for( CDoubleClickListener listener : getDoubleClickListeners() ){
                if( listener.clicked( source, event ))
                    return true;
            }
            return false;
        }
    };
    
    private List<CVetoClosingListener> vetoClosingListeners = new ArrayList<CVetoClosingListener>();
    
    private CVetoClosingListener vetoClosingListener = new CVetoClosingListener() {
		public void closing( CVetoClosingEvent event ){
			for( CVetoClosingListener listener : getVetoClosingListeners() ){
				listener.closing( event );
			}
		}
		
		public void closed( CVetoClosingEvent event ){
			for( CVetoClosingListener listener : getVetoClosingListeners() ){
				listener.closed( event );
			}
		}
	};
	
	private List<CDockableLocationListener> locationListeners = new ArrayList<CDockableLocationListener>();
	
	private CDockableLocationListener locationListener = new CDockableLocationListener(){
		public void changed( CDockableLocationEvent event ){
			for( CDockableLocationListener listener : getCDockableLocationListeners() ){
				listener.changed( event );
			}
		}
	};
    
    /**
     * Stores an additional {@link CDockableStateListener} in this collection.
     * @param listener the additional listener
     */
    public void addCDockableStateListener( CDockableStateListener listener ){
        if( listener == null )
            throw new IllegalArgumentException( "listener must not be null" );
        stateListeners.add( listener );
    }
    
    /**
     * Removes a listener from this collection
     * @param listener the listener to remove
     */
    public void removeCDockableStateListener( CDockableStateListener listener ){
        stateListeners.remove( listener );
    }
    
    /**
     * Gets all currently registered {@link CDockableStateListener}s collected
     * in an array. Modifications of the array will not modify this collection.
     * @return the independent array of listeners
     */
    public CDockableStateListener[] getCDockableStateListeners(){
        return stateListeners.toArray( new CDockableStateListener[ stateListeners.size() ] );
    }
    
    /**
     * Gets a {@link CDockableLocationListener} which forwards all calls to its
     * methods to the listeners registered at this collection.
     * @return a forwarding listener
     */
    public CDockableLocationListener getCDockableLocationListener(){
        return locationListener;
    }
    
    /**
     * Tells whether at least one {@link CDockableStateListener} is currently
     * registered.
     * @return <code>true</code> if there is at least one listener
     */
    public boolean hasCDockableLocationListeners(){
    	return locationListeners.size() > 0;
    }
    
    /**
     * Stores an additional {@link CDockableLocationListener} in this collection.
     * @param listener the additional listener
     */
    public void addCDockableLocationListener( CDockableLocationListener listener ){
        if( listener == null )
            throw new IllegalArgumentException( "listener must not be null" );
        locationListeners.add( listener );
    }
    
    /**
     * Removes a listener from this collection
     * @param listener the listener to remove
     */
    public void removeCDockableLocationListener( CDockableLocationListener listener ){
        locationListeners.remove( listener );
    }
    
    /**
     * Gets all currently registered {@link CDockableLocationListener}s collected
     * in an array. Modifications of the array will not modify this collection.
     * @return the independent array of listeners
     */
    public CDockableLocationListener[] getCDockableLocationListeners(){
        return locationListeners.toArray( new CDockableLocationListener[ locationListeners.size() ] );
    }
    
    /**
     * Gets a {@link CDockableStateListener} which forwards all calls to its
     * methods to the listeners registered at this collection.
     * @return a forwarding listener
     */
    public CDockableStateListener getCDockableStateListener(){
        return stateListener;
    }
    
    /**
     * Stores an additional {@link CDockablePropertyListener} in this collection.
     * @param listener the additional listener
     */
    public void addCDockablePropertyListener( CDockablePropertyListener listener ){
        if( listener == null )
            throw new IllegalArgumentException( "listener must not be null" );
        propertyListeners.add( listener );
    }
    
    /**
     * Removes a listener from this collection.
     * @param listener the listener to remove
     */
    public void removeCDockablePropertyListener( CDockablePropertyListener listener ){
        propertyListeners.remove( listener );
    }
    
    /**
     * Gets all currently registered {@link CDockableStateListener}s collected
     * in an array. Modifications of the array will not modify this collection.
     * @return the independent array of listeners
     */
    public CDockablePropertyListener[] getCDockablePropertyListeners(){
        return propertyListeners.toArray( new CDockablePropertyListener[ propertyListeners.size() ] );
    }
    
    /**
     * Gets a {@link CDockablePropertyListener} which forwards all calls to its
     * methods to the listeners registered at this collection.
     * @return a forwarding listener
     */
    public CDockablePropertyListener getCDockablePropertyListener(){
        return propertyListener;
    }
    
    /**
     * Adds a {@link CFocusListener} to this collection.
     * @param listener the new listener
     */
    public void addFocusListener( CFocusListener listener ){
        if( listener == null )
            throw new IllegalArgumentException( "listener must not be null" );
        focusListeners.add( listener );
    }
    
    /**
     * Removes a listener from this collection.
     * @param listener the listener to remove
     */
    public void removeFocusListener( CFocusListener listener ){
        focusListeners.remove( listener );
    }
    
    /**
     * Gets all currently registered {@link CFocusListener}s collected in
     * an array. Modifications of the array will not modify this collection.
     * @return the independent array of listeners
     */
    public CFocusListener[] getFocusListeners(){
        return focusListeners.toArray( new CFocusListener[ focusListeners.size() ] ); 
    }
    
    /**
     * Gets a {@link CFocusListener} which forwards all calls to its 
     * methods to the listener registered at this collection.
     * @return a forwarding listener
     */
    public CFocusListener getFocusListener(){
        return focusListener;
    }
    
    /**
     * Adds a listener to this collection.
     * @param listener the additional listener
     */
    public void addVetoFocusListener( CVetoFocusListener listener ){
    	if( listener == null )
    		throw new IllegalArgumentException( "listener must not be null" );
    	
    	vetoFocusListeners.add( listener );
    }
    
    /**
     * Removes a listener from this collection
     * @param listener the listener to remove
     */
    public void removeVetoFocusListener( CVetoFocusListener listener ){
    	vetoFocusListeners.remove( listener );
    }
    
    /**
     * Gets all the {@link CVetoFocusListener} that are collected in
     * this collection.
     * @return an independent array of listeners
     */
    public CVetoFocusListener[] getVetoFocusListeners(){
    	return vetoFocusListeners.toArray( new CVetoFocusListener[ vetoFocusListeners.size() ] );
    }
    
    /**
     * Gets a {@link CVetoFocusListener} which forwards all calls to
     * its methods to the listeners registered in this collection.
     * @return a forwarding listener
     */
    public CVetoFocusListener getVetoFocusListener(){
		return vetoFocusListener;
	}
    
    /**
     * Stores an additional {@link CKeyboardListener} in this collection.
     * @param listener the new listener
     */
    public void addKeyboardListener( CKeyboardListener listener ){
        if( listener == null )
            throw new IllegalArgumentException( "listener must not be null" );
        keyboardListeners.add( listener );
    }
    
    /**
     * Removes a listener from this collection.
     * @param listener the listener to remove
     */
    public void removeKeyboardListener( CKeyboardListener listener ){
        keyboardListeners.remove( listener );
    }
    
    /**
     * Gets all currently registered {@link CKeyboardListener}s collected
     * in an array. Modifications of the array will not modify this collection.
     * @return the independent array of listeners
     */
    public CKeyboardListener[] getKeyboardListeners(){
        return keyboardListeners.toArray( new CKeyboardListener[ keyboardListeners.size() ]);
    }
    
    /**
     * Gets a {@link CKeyboardListener} which forwards all calls to its
     * methods to the listener registered at this collection. 
     * @return a forwarding listener
     */
    public CKeyboardListener getKeyboardListener(){
        return keyboardListener;
    }
    
    /**
     * Stores an additional {@link CDoubleClickListener} in this collection.
     * @param listener the additional listener
     */
    public void addDoubleClickListener( CDoubleClickListener listener ){
        if( listener == null )
            throw new IllegalArgumentException( "listener must not be null" );
        doubleClickListeners.add( listener );
    }
    
    /**
     * Removes a listener from this collection.
     * @param listener the listener to remove
     */
    public void removeDoubleClickListener( CDoubleClickListener listener ){
        doubleClickListeners.remove( listener );
    }
    
    /**
     * Gets all currently registered {@link CDoubleClickListener}s collected
     * in an array. Modifications of the array will not modify this collection.
     * @return the independent array of listeners
     */
    public CDoubleClickListener[] getDoubleClickListeners(){
        return doubleClickListeners.toArray( new CDoubleClickListener[ doubleClickListeners.size() ] );
    }
    
    /**
     * Gets a {@link CDoubleClickListener} which forwards all calls to its
     * methods to the listener registered at this collection.
     * @return a forwarding listener
     */
    public CDoubleClickListener getDoubleClickListener(){
        return doubleClickListener;
    }
    
    /**
     * Gets a {@link CVetoClosingListener} that forwards all events to the other
     * registered listeners.
     * @return the forwarding listener
     */
    public CVetoClosingListener getVetoClosingListener(){
		return vetoClosingListener;
	}
    
    /**
     * Stores an additional {@link CVetoClosingListener} in this collection.
     * @param listener the new listener
     */
    public void addVetoClosingListener( CVetoClosingListener listener ){
    	if( listener == null )
    		throw new IllegalArgumentException( "listener must not be null" );
    	vetoClosingListeners.add( listener );
    }
    
    /**
     * Removes <code>listener</code> from this collection.
     * @param listener the listener to remove
     */
    public void removeVetoClosingListener( CVetoClosingListener listener ){
    	vetoClosingListeners.remove( listener );
    }
    
    /**
     * Tells whether there is at least one {@link CVetoClosingListener} registered in
     * this collection.
     * @return whether there is at least one listener
     */
    public boolean hasVetoClosingListeners(){
    	return !vetoClosingListeners.isEmpty();
    }
    
    /**
     * Gets all currently registered {@link CVetoClosingListener}s collected
     * in an array. Modifications of the array will not modify this collection.
     * @return the independent array of listeners
     */
    public CVetoClosingListener[] getVetoClosingListeners(){
    	return vetoClosingListeners.toArray( new CVetoClosingListener[ vetoClosingListeners.size() ] );
    }
}
