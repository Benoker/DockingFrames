/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2009 Benjamin Sigg
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
package bibliothek.gui.dock.support.mode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.DockActionSource;
import bibliothek.gui.dock.action.LocationHint;
import bibliothek.gui.dock.action.MultiDockActionSource;

/**
 * Associates {@link Dockable}s with one {@link Mode} out of a set
 * of modes. This manager remembers in which order the modes were applied
 * to a {@link Dockable}.
 * @param <A> the kind of properties that are to be stored in this manager
 * @param <M> the kinf of {@link Mode}s used by this manager
 * @author Benjamin Sigg
 */
public class ModeManager<A, M extends Mode> {
	/** the ordered list of available modes */
	private List<ModeAccess> modes = new ArrayList<ModeAccess>();
	
	/** lists for all known {@link Dockable}s their {@link Entry} */
	private Map<Dockable, Entry> dockables = new HashMap<Dockable, Entry>();
	
	/** list all {@link Entry}s ever created and not dismissed by this manager */
	private Map<String, Entry> entries = new HashMap<String, Entry>();
		
	/** all the listeners that are registered at this manager */
	private List<ModeManagerListener<? super A, ? super M>> listeners =
		new ArrayList<ModeManagerListener<? super A,? super M>>();
	
	/**
	 * Adds a listener to this manager, the listener will be informed about
	 * changes in this manager.
	 * @param listener the new listener, not <code>null</code>
	 */
	public void addModeManagerListener( ModeManagerListener<? super A, ? super M> listener ){
		if( listener == null )
			throw new IllegalArgumentException( "listener must not be null" );
		listeners.add( listener );
	}
	
	/**
	 * Removes <code>listener</code> from this manager.
	 * @param listener the listener to remove
	 */
	public void removeModeManagerListener( ModeManagerListener<? super A, ? super M> listener ){
		listeners.remove( listener );
	}
	
	/**
	 * Gets all the listeners that are currently registered in this manager.
	 * @return the list of registered listeners
	 */
	@SuppressWarnings("unchecked")
	protected ModeManagerListener<? super A, ? super M>[] listeners(){
		return listeners.toArray( new ModeManagerListener[ listeners.size() ] );
	}
	
	/**
	 * Calls {@link ModeManagerListener#dockableAdded(ModeManager, Dockable)}
	 * on all listeners that are currently registered
	 * @param dockable the new element
	 */
	protected void fireAdded( Dockable dockable ){
		for( ModeManagerListener<? super A, ? super M> listener : listeners() ){
			listener.dockableAdded( this, dockable );
		}
	}
	
	/**
	 * Calls {@link ModeManagerListener#dockableRemoved(ModeManager, Dockable)}
	 * on all listeners that are currently registered.
	 * @param dockable the removed element
	 */
	protected void fireRemoved( Dockable dockable ){
		for( ModeManagerListener<? super A, ? super M> listener : listeners() ){
			listener.dockableRemoved( this, dockable );
		}
	}
	
	/**
	 * Calls {@link ModeManagerListener#modeChanged(ModeManager, Dockable, Mode, Mode)}
	 * on all listeners that are currently registered.
	 * @param dockable the element whose mode changed
	 * @param oldMode its old mode
	 * @param newMode its new mode
	 */
	protected void fireModeChanged( Dockable dockable, M oldMode, M newMode ){
		for( ModeManagerListener<? super A, ? super M> listener : listeners() ){
			listener.modeChanged( this, dockable, oldMode, newMode );
		}
	}
	
	/**
	 * Registers a new {@link Dockable} at this manager. If there is already
	 * mode-information for <code>key</code> present, then <code>dockable</code>
	 * inherits this information.
	 * @param key the unique key of <code>dockable</code>
	 * @param dockable the new element
	 * @throws NullPointerException if either <code>key</code> or <code>dockable</code>
	 * is <code>null</code>
	 * @throws IllegalArgumentException if there is already a dockable registered
	 * with <code>key</code>
	 */
	public void add( String key, Dockable dockable ){
        if( key == null )
            throw new NullPointerException( "key must not be null" );
        
        if( dockable == null )
            throw new NullPointerException( "dockable must not be null" );
        
        Entry entry = entries.get( key );
        if( entry != null && entry.dockable != null )
            throw new IllegalArgumentException( "There is already a dockable registered with the key: " + key );
        
        if( entry == null ){
            entry = new Entry( dockable, key );
            entries.put( entry.id, entry );
        }
        else{
            entry.dockable = dockable;
        }
        
        dockables.put( dockable, entry );
        entry.putMode( access( getCurrentMode( dockable ) ) );
        
        fireAdded( dockable );
        
        rebuild( dockable );
	}
	
	/**
	 * Registers a new {@link Dockable} at this maanger. This method works
	 * like {@link #add(String, Dockable)} but does not throw an exception
	 * if another {@link Dockable} is already registered with <code>key</code>.
	 * Instead the other <code>Dockable</code> is unregistered and <code>docakble</code>
	 * inherits its mode-information.
	 * @param key the unique identifier of <code>dockable</code>
	 * @param dockable some new element
	 * @throws NullPointerException if either <code>key</code> or <code>dockable</code>
	 * is <code>null</code>
	 */
    public void put( String key, Dockable dockable ){
        if( key == null )
            throw new NullPointerException( "key must not be null" );
        
        if( dockable == null )
            throw new NullPointerException( "dockable must not be null" );
        
        Entry entry = entries.get( key );
        if( entry != null ){
            if( entry.dockable != null ){
                dockables.remove( entry.dockable );
                fireRemoved( entry.dockable );
            }
            entry.dockable = dockable;
            dockables.put( dockable, entry );
        }
        else{
            // was not inserted
            entry = new Entry( dockable, key );
            dockables.put( dockable, entry );
            entries.put( entry.id, entry );
            entry.putMode( access( getCurrentMode( dockable ) ) );
        }
        
        fireAdded( dockable );
        
        rebuild( dockable );
    }

    /**
     * Alters the mode of <code>dockable</code> to <code>mode</code>. This
     * method does nothing if the current mode of <code>dockable</code>
     * already is <code>mode</code>. Notice that other dockables might
     * change their mode because of this method call.
     * @param dockable the element whose mode is going to be changed
     * @param mode the new mode
     * @throws IllegalArgumentException if <code>dockable</code> is <code>null</code>,
     * <code>mode</code> is <code>null</code> or <code>dockable</code> is not
     * registered. 
     */
    public void alter( Dockable dockable, Mode mode ){
    	if( dockable == null )
    		throw new IllegalArgumentException( "dockable is null" );
    	
    	if( mode == null )
    		throw new IllegalArgumentException( "mode is null" );
    	
    	Entry entry = dockables.get( dockable );
    	if( entry == null )
    		throw new IllegalArgumentException( "dockable not registered" );
    	
    	Mode dockableMode = getCurrentMode( dockable );
    	if( dockableMode == null )
    		dockableMode = getDefaultMode( dockable );
    	
    	// apply new mode
    	NeutralHistories histories = new NeutralHistories();
    	for( Dockable old : dockables.keySet() ){
    		Mode oldMode = getCurrentMode( old );
    		if( oldMode == null )
    			oldMode = getDefaultMode( old );
    		if( oldMode != null ){
    			histories.add( old, oldMode );
    		}
    	}
    	
    	histories.advance();
    	histories.remove( dockable );
    	mode.apply( dockable, dockableMode );
    	histories.add( dockable, mode );
    	histories.restore();
    	
    	for( Entry other : dockables.values() ){
    		Mode otherMode = getCurrentMode( other.dockable );
    		if( otherMode == null )
    			otherMode = getDefaultMode( other.dockable );
    		
    		entry.putMode( access( otherMode ) );
    	}
    }

    /**
     * Removes the properties that belong to <code>dockable</code>.
     * @param dockable the element to remove
     */
    public void remove( Dockable dockable ){
        Entry entry = dockables.remove( dockable );
        if( entry != null ){
            entries.remove( entry.id );
            fireRemoved( dockable );
        }
    }
    
    /**
     * Removes <code>dockable</code> itself, put the properties of
     * <code>dockable</code> remain in the system.
     * @param dockable the element to reduce
     */
    public void reduceToEmpty( Dockable dockable ){
        Entry entry = dockables.get( dockable );
        if( entry != null ){
            entry.dockable = null;
            fireRemoved( dockable );
        }
    }
    
    /**
     * Adds an empty entry to this manager. The empty entry can be used to store
     * information for a {@link Dockable} that has not yet been created. It is
     * helpful if the client intends to load first its properties and create
     * only those {@link Dockable}s which are visible.<br>
     * If there is already an entry for <code>name</code>, then this method
     * does do nothing.
     * @param key the name of the empty entry
     * @throws NullPointerException if <code>key</code> is <code>null</code>
     */
    public void addEmpty( String key ){
        if( key == null )
            throw new NullPointerException( "name must not be null" );
        
        Entry entry = entries.get( key );
        
        if( entry == null ){
            entry = new Entry( null, key );
            entries.put( key, entry );
        }
    }
    
    /**
     * Removes the entry for <code>name</code> but only if the entry is not
     * associated with any {@link Dockable}.
     * @param name the name of the entry which might be empty
     * @throws NullPointerException if <code>key</code> is <code>null</code>
     */
    public void removeEmpty( String name ){
        if( name == null )
            throw new NullPointerException( "name must not be null" );
        
        Entry entry = entries.get( name );
        if( entry.dockable == null )
            entries.remove( name );
    }
	
    
    
	/**
	 * Gets the default mode of <code>dockable</code>, the mode
	 * dockable is in if nothing else is specified. This method checks
	 * {@link Mode#isDefaultMode(Dockable)} and returns the first
	 * {@link Mode} where the answer was <code>true</code>.
	 * @param dockable some dockable, not <code>null</code>
	 * @return its default mode, must be registered at this {@link ModeManager}
	 * and not be <code>null</code>
	 */
	protected M getDefaultMode( Dockable dockable ){
		if( modes.isEmpty() )
			throw new IllegalStateException( "no modes available" );
		
		for( ModeAccess mode : modes ){
			if( mode.mode.isDefaultMode( dockable )){
				return mode.mode;
			}
		}
		
		throw new IllegalStateException( "no mode is the default mode for '" + dockable.getTitleText() + "'" );
	}
	
	/**
	 * Tries to find the mode <code>dockable</code> is currently in. This method
	 * calls {@link Mode#isCurrentMode(Dockable)} and returns the first
	 * {@link Mode} where the answer was <code>true</code>.
	 * @param dockable some dockable, not <code>null</code>
	 * @return the current mode or <code>null</code> if not found
	 */
	protected M getCurrentMode( Dockable dockable ){
		for( ModeAccess mode : modes ){
			if( mode.mode.isCurrentMode( dockable )){
				return mode.mode;
			}
		}
		
		return null;
	}
	
	/**
	 * A wrapper around a mode, giving access to its properties. The mode
	 * inside this wrapper can be replaced any time.
	 * @author Benjamin Sigg
	 */
	private class ModeAccess{
		private M mode;
	}

	/**
	 * Gets the <code>ModeAccess</code> which represents <code>mode</code>.
	 * @param mode some mode or <code>null</code>
	 * @return its access or <code>null</code>
	 * @throws IllegalArgumentException if <code>mode</code> is unknown
	 */
	private ModeAccess access( Mode mode ){
		if( mode == null )
			return null;
		
		for( ModeAccess access : modes ){
			if( access.mode == mode ){
				return access;
			}
		}
		
		throw new IllegalArgumentException( "unknown mode: " + mode );
	}
	
	/**
	 * Rebuilds the action sources of <code>dockable</code>.
	 * @param dockable the element whose actions are to be updated
	 */
	protected void rebuild( Dockable dockable ){
		Entry entry = dockables.get( dockable );
		if( entry != null ){
			entry.updateActionSource();
		}
	}
	
    /**
     * Describes all properties a {@link Dockable} has.
     * @author Benjamin Sigg
     */
    private class Entry{
        /** the {@link Dockable} for which the properties are stored */
        public Dockable dockable;
        /** a unique id associated with {@link #dockable} */
        public String id;
        
        /** the set of actions available for {@link #dockable} */
        public MultiDockActionSource source;
        /** a map that stores some properties mapped to the different modes */
        public Map<String, A> properties;

        /** The modes this entry already visited. No mode is more than once in this list. */
        private List<ModeAccess> history;
        
        /**
         * Creates a new entry
         * @param dockable the element whose properties are stores in this entry
         * @param id the unique if of this entry
         */
        public Entry( Dockable dockable, String id ){
            this.dockable = dockable;
            this.id = id;
            source = new MultiDockActionSource( new LocationHint( LocationHint.ACTION_GUARD, LocationHint.RIGHT ) );
            properties = new HashMap<String, A>();
            history = new LinkedList<ModeAccess>();
        }
        
        /**
         * Updates the action source of this manager.
         */
        public void updateActionSource(){
        	source.removeAll();
        	Mode mode = getCurrentMode( dockable );
        	if( mode == null )
        		mode = getDefaultMode( dockable );
        	
        	for( ModeAccess access : modes ){
        		DockActionSource next = access.mode.getActionsFor( dockable, mode );
        		if( next != null ){
        			source.add( next );
        		}
        	}
        }
        
        /**
         * Stores <code>mode</code> in a stack that describes the history
         * through which this entry moved. If <code>mode</code> is already
         * in the stack, than it is moved to the top of the stack. 
         * @param mode the mode to store
         */
        public void putMode( ModeAccess mode ){
            ModeAccess oldMode = peekMode();
            if( oldMode != mode ){
	            history.remove( mode );
	            history.add( mode );
	            rebuild( dockable );
	            fireModeChanged( dockable, oldMode.mode, mode.mode );
            }
        }
        
        /**
         * Gets the mode that was used previously to the current mode.
         * If the history gets empty, then {@link ModeManager#getDefaultMode(Dockable)}
         * is returned.
         * @return the mode in which this entry was before the current mode
         * was put onto the history
         */
        public ModeAccess previousMode(){
            if( history.size() < 2 )
                return access( getDefaultMode( dockable ) );
            else
                return history.get( history.size()-2 );
        }
        
        /**
         * Gets the current mode of this entry.
         * @return the mode or <code>null</code>
         */
        public ModeAccess peekMode(){
            if( history.isEmpty() )
                return null;
            else
                return history.get( history.size()-1 );
        }
    }
}
