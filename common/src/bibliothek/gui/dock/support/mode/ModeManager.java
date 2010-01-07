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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import bibliothek.extension.gui.dock.util.Path;
import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.ActionGuard;
import bibliothek.gui.dock.action.DockActionSource;
import bibliothek.gui.dock.action.LocationHint;
import bibliothek.gui.dock.action.MultiDockActionSource;
import bibliothek.gui.dock.support.action.ModeTransitionSetting;
import bibliothek.gui.dock.util.DockUtilities;

/**
 * Associates {@link Dockable}s with one {@link Mode} out of a set
 * of modes. This manager remembers in which order the modes were applied
 * to a {@link Dockable}.
 * @param <H> the kind of properties that are to be stored in this manager
 * @param <M> the kind of {@link Mode}s used by this manager
 * @author Benjamin Sigg
 */
public abstract class ModeManager<H, M extends Mode<H>> {
	/** the ordered list of available modes */
	private List<ModeHandle> modes = new ArrayList<ModeHandle>();
	
	/** factories for creating {@link ModeSetting}s */
	private Map<Path, ModeSettingFactory<H>> factories = new HashMap<Path, ModeSettingFactory<H>>();
	
	/** lists for all known {@link Dockable}s their {@link DockableHandle} */
	private Map<Dockable, DockableHandle> dockables = new HashMap<Dockable, DockableHandle>();
	
	/** list all {@link DockableHandle}s ever created and not dismissed by this manager */
	private Map<String, DockableHandle> entries = new HashMap<String, DockableHandle>();
		
	/** all the listeners that are registered at this manager */
	private List<ModeManagerListener<? super H, ? super M>> listeners =
		new ArrayList<ModeManagerListener<? super H,? super M>>();
	
	/** whether a mode is currently applying itself */
	private boolean onTransition = false;
	
	/** the controller in whose realm this manager works */
	private DockController controller;
	
	private ActionGuard guard = new ActionGuard() {
		public boolean react( Dockable dockable ){
			return getHandle( dockable ) != null;
		}
		
		public DockActionSource getSource( Dockable dockable ){
			DockableHandle handle = getHandle( dockable );
			if( handle == null )
				return null;
			return handle.source;
		}
	};
	
	/**
	 * Creates a new manager.
	 * @param controller the controller in whose realm this manager will work
	 */
	public ModeManager( DockController controller ){
		controller.addActionGuard( guard );
		this.controller = controller;
	}
	
	/**
	 * Unregisters listeners which this manager added to the {@link DockController} and
	 * other components.
	 */
	public void destroy(){
		if( controller != null ){
			controller.removeActionGuard( guard );
			controller = null;
		}
	}
	
	/**
	 * Gets the controller in whose realm this manager works.
	 * @return the controller
	 */
	public DockController getController(){
		return controller;
	}
	
	/**
	 * Adds a listener to this manager, the listener will be informed about
	 * changes in this manager.
	 * @param listener the new listener, not <code>null</code>
	 */
	public void addModeManagerListener( ModeManagerListener<? super H, ? super M> listener ){
		if( listener == null )
			throw new IllegalArgumentException( "listener must not be null" );
		listeners.add( listener );
	}
	
	/**
	 * Removes <code>listener</code> from this manager.
	 * @param listener the listener to remove
	 */
	public void removeModeManagerListener( ModeManagerListener<? super H, ? super M> listener ){
		listeners.remove( listener );
	}
	
	/**
	 * Puts a new mode in this manager. If there is already a mode with the
	 * same id registered, then the old mode gets replaced by the new one.
	 * @param mode the new mode
	 */
	public void putMode( M mode ){
		if( mode == null )
			throw new IllegalArgumentException( "mode must not be null" );
		for( ModeHandle handle : modes ){
			if( handle.mode.getUniqueIdentifier().equals( mode.getUniqueIdentifier() )){
				fireRemoved( handle.mode );
				handle.mode = mode;
				fireAdded( mode );
				return;
			}
		}
		modes.add( new ModeHandle( mode ) );
		fireAdded( mode );
	}
	
	/**
	 * Adds a factory to this {@link ModeManager}. The factory will be used by the
	 * {@link ModeSettings} to read and write data of the mode with the same identifier
	 * as <code>factory</code> persistently.<br>
	 * <b>Note:</b> A {@link Mode} might also provide a {@link ModeSettingFactory}, if
	 * there is a collision of unique identifiers the factory of the mode is used. 
	 * @param factory the new factory
	 */
	public void putFactory( ModeSettingFactory<H> factory ){
		factories.put( factory.getModeId(), factory );
	}
	
	/**
	 * Removes <code>mode</code> from this manager. Note that history information
	 * about the mode remains.
	 * @param mode the mode to remove
	 */
	public void removeMode( M mode ){
		if( mode == null )
			throw new IllegalArgumentException( "mode must not be null" );
		for( ModeHandle handle : modes ){
			if( handle.mode.getUniqueIdentifier().equals( mode.getUniqueIdentifier() )){
				handle.mode = null;
				fireRemoved( handle.mode );
				modes.remove( handle );
				return;
			}
		}
	}
	
	/**
	 * Searches and returns the mode with given unique identifier <code>path</code>.
	 * @param path some unique identifier
	 * @return the mode with that identifier or <code>null</code>
	 */
	public M getMode( Path path ){
		ModeHandle handle = getAccess( path );
		return handle == null ? null : handle.mode;
	}
	
	private ModeHandle getAccess( Path path ){
		for( ModeHandle mode : modes ){
			if( mode.mode.getUniqueIdentifier().equals( path ))
				return mode;
		}
		return null;
	}
	
	/**
	 * Gets all the listeners that are currently registered in this manager.
	 * @return the list of registered listeners
	 */
	@SuppressWarnings("unchecked")
	protected ModeManagerListener<? super H, ? super M>[] listeners(){
		return listeners.toArray( new ModeManagerListener[ listeners.size() ] );
	}
	
	/**
	 * Calls {@link ModeManagerListener#dockableAdded(ModeManager, Dockable)}
	 * on all listeners that are currently registered
	 * @param dockable the new element
	 */
	protected void fireAdded( Dockable dockable ){
		for( ModeManagerListener<? super H, ? super M> listener : listeners() ){
			listener.dockableAdded( this, dockable );
		}
	}
	
	/**
	 * Calls {@link ModeManagerListener#dockableRemoved(ModeManager, Dockable)}
	 * on all listeners that are currently registered.
	 * @param dockable the removed element
	 */
	protected void fireRemoved( Dockable dockable ){
		for( ModeManagerListener<? super H, ? super M> listener : listeners() ){
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
		for( ModeManagerListener<? super H, ? super M> listener : listeners() ){
			listener.modeChanged( this, dockable, oldMode, newMode );
		}
	}

	/**
	 * Calls {@link ModeManagerListener#modeAdded(ModeManager, Mode)} on
	 * all listeners that are currently registered.
	 * @param mode the added mode
	 */
	protected void fireAdded( M mode ){
		for( ModeManagerListener<? super H, ? super M> listener : listeners() ){
			listener.modeAdded( this, mode );
		}
	}
	
	/**
	 * Calls {@link ModeManagerListener#modeRemoved(ModeManager, Mode)} on
	 * all listeners that are currently registered.
	 * @param mode the removed mode
	 */
	protected void fireRemoved( M mode ){
		for( ModeManagerListener<? super H, ? super M> listener : listeners() ){
			listener.modeRemoved( this, mode );
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
        
        DockableHandle entry = entries.get( key );
        if( entry != null && entry.dockable != null )
            throw new IllegalArgumentException( "There is already a dockable registered with the key: " + key );
        
        if( entry == null ){
            entry = new DockableHandle( dockable, key );
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
        
        DockableHandle entry = entries.get( key );
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
            entry = new DockableHandle( dockable, key );
            dockables.put( dockable, entry );
            entries.put( entry.id, entry );
            entry.putMode( access( getCurrentMode( dockable ) ) );
        }
        
        fireAdded( dockable );
        
        rebuild( dockable );
    }

    /**
     * Gets the unique identifier which is used for <code>dockable</code>.
     * @param dockable some element
     * @return the unique identifier or <code>null</code> if <code>dockable</code>
     * is not registered
     */
    public String getKey( Dockable dockable ){
    	DockableHandle handle = getHandle( dockable );
    	if( handle == null )
    		return null;
    	return handle.id;
    }
    
    /**
     * Returns a set containing all {@link Dockable}s that are currently
     * registered at this manager.
     * @return the set of dockables
     */
    public Set<Dockable> listDockables(){
    	return Collections.unmodifiableSet( dockables.keySet() );
    }
    
    /**
     * Runs an algorithm which affects the mode of some {@link Dockable}s.
     * @param runnable the algorithm, <code>null</code> will be ignored
     */
    public void run( AffectingRunnable runnable ){
    	if( runnable == null )
    		return;
    	
    	ChangeSet set = new ChangeSet();
    	runnable.run( set );
    	set.finish();
    }

    /**
     * Alters the mode of <code>dockable</code> to <code>mode</code>. This
     * method does nothing if the current mode of <code>dockable</code>
     * already is <code>mode</code>. This method does not alter the modes
     * of other dockables, notice however that the methods
     * {@link Mode#apply(Dockable, Object)} may trigger additional mode-changes.
     * @param dockable the element whose mode is going to be changed
     * @param mode the new mode
     * @throws IllegalArgumentException if <code>dockable</code> is <code>null</code>,
     * <code>mode</code> is <code>null</code> or <code>dockable</code> is not
     * registered.
     * @return <code>true</code> if <code>mode</code> was found, <code>false</code>
     * otherwise 
     */
    public boolean apply( Dockable dockable, Path mode ){
    	M resolved = getMode( mode );
    	if( resolved != null ){
    		apply( dockable, resolved );
    		return true;
    	}
    	return false;
    }
    
    /**
     * Alters the mode of <code>dockable</code> to <code>mode</code>. 
     * This method does not alter the modes of other dockables, notice however that the methods
     * {@link Mode#apply(Dockable, Object)} may trigger additional mode-changes.
     * @param dockable the element whose mode is going to be changed
     * @param mode the new mode
     * @throws IllegalArgumentException if <code>dockable</code> is <code>null</code>,
     * <code>mode</code> is <code>null</code> or <code>dockable</code> is not
     * registered. 
     */
    public void apply( Dockable dockable, M mode ){
    	ChangeSet set = new ChangeSet();
    	apply( dockable, mode, set );
    	set.finish();
    }
    
    /**
     * Alters the mode of <code>dockable</code> to <code>mode</code>. 
     * This method does not alter the modes of other dockables, notice however that the methods
     * {@link Mode#apply(Dockable, Object)} may trigger additional mode-changes.
     * @param dockable the element whose mode is going to be changed
     * @param mode the new mode
     * @param set to store all dockables whose mode might have been changed
     * @throws IllegalArgumentException if <code>dockable</code> is <code>null</code>,
     * <code>mode</code> is <code>null</code>, <code>set</code> is <code>null</code>,
     * or <code>dockable</code> is not registered.
     * @returns <code>true</code> if <code>mode</code> was found, 
     * <code>false</code> otherwise
     */
    public boolean apply( Dockable dockable, Path mode, AffectedSet set ){
    	M resolved = getMode( mode );
    	if( resolved != null ){
    		apply( dockable, resolved, set );
    		return true;
    	}
    	return false;
    }
    
    /**
     * Alters the mode of <code>dockable</code> to <code>mode</code>. This
     * method does nothing if the current mode of <code>dockable</code>
     * already is <code>mode</code>. This method does not alter the modes
     * of other dockables, notice however that the methods
     * {@link Mode#apply(Dockable, Object)} may trigger additional mode-changes.
     * @param dockable the element whose mode is going to be changed
     * @param mode the new mode
     * @param set to store all dockables whose mode might have been changed
     * @throws IllegalArgumentException if <code>dockable</code> is <code>null</code>,
     * <code>mode</code> is <code>null</code>, <code>set</code> is <code>null</code>,
     * or <code>dockable</code> is not registered. 
     */
    public void apply( Dockable dockable, M mode, AffectedSet set ){
    	if( dockable == null )
    		throw new IllegalArgumentException( "dockable is null" );
    	
    	if( mode == null )
    		throw new IllegalArgumentException( "mode is null" );
    	
    	if( set == null )
    		throw new IllegalArgumentException( "set is null" );
    	
    	DockableHandle entry = dockables.get( dockable );
    	if( entry == null )
    		throw new IllegalArgumentException( "dockable not registered" );
    	
    	M dockableMode = getCurrentMode( dockable );
    	if( dockableMode == mode )
    		return;
    	
    	H history = entry.properties.get( mode.getUniqueIdentifier() );
    	apply( dockable, mode, history, set );
    }
    
    /**
     * Alters the mode of <code>dockable</code> to be <code>mode</code>. 
     * This method does not alter the modes of other dockables, notice however
     * that the methods {@link Mode#apply(Dockable, Object)} may
     * trigger additional mode-changes.
     * @param dockable the element whose mode is changed
     * @param mode the new mode of <code>dockable</code>
     * @param history history information for {@link Mode#apply(Dockable, Object, AffectedSet)},
     * can be <code>null</code>
     * @param set to store elements that have changed
     * @throws IllegalArgumentException if either <code>dockable</code>, <code>mode</code>
     * or <code>set</code> is <code>null</code>
     * @returns <code>true</code> if <code>mode</code> was found, <code>false</code>
     * otherwise
     */
    public boolean apply( Dockable dockable, Path mode, H history, AffectedSet set ){
    	M resolved = getMode( mode );
    	if( resolved != null ){
    		apply( dockable, resolved, history, set );
    		return true;
    	}
    	return false;
    }
    
    /**
     * Alters the mode of <code>dockable</code> to be <code>mode</code>. 
     * This method does not alter the modes of other dockables, notice however
     * that the methods {@link Mode#apply(Dockable, Object)} may
     * trigger additional mode-changes.
     * @param dockable the element whose mode is changed
     * @param mode the new mode of <code>dockable</code>
     * @param history history information for {@link Mode#apply(Dockable, Object, AffectedSet)},
     * can be <code>null</code>
     * @param set to store elements that have changed
     * @throws IllegalArgumentException if either <code>dockable</code>, <code>mode</code>
     * or <code>set</code> is <code>null</code>
     */
    public void apply( Dockable dockable, M mode, H history, AffectedSet set ){
    	if( dockable == null )
    		throw new IllegalArgumentException( "dockable is null" );
    	
    	if( mode == null )
    		throw new IllegalArgumentException( "mode is null" );
    	
    	if( set == null )
    		throw new IllegalArgumentException( "set is null" );
    	
    	M dockableMode = getCurrentMode( dockable );
    	
    	if( dockableMode != null ){
    		store( dockable );
    	}
    	
    	set.add( dockable );
    	try{
    		onTransition = true;
    		mode.apply( dockable, history, set );
    	}
    	finally{
    		onTransition = false;
    	}
    }

    /**
     * Stores a property for <code>dockable</code> if in mode <code>mode</code>. This
     * method does not trigger any version of {@link #apply(Dockable, Mode) apply}.
     * @param mode the mode which is affected
     * @param dockable the dockables whose property is changed
     * @param property the new property, can be <code>null</code>
     */
    protected void setProperties( M mode, Dockable dockable, H property ){
    	DockableHandle entry = dockables.get( property );
    	if( entry != null ){
    		if( property == null )
    			entry.properties.remove( mode.getUniqueIdentifier() );
    		else
    			entry.properties.put( mode.getUniqueIdentifier(), property );
    	}
    }
    
    /**
     * Gets the properties which correspond to <code>dockable</code>
     * and <code>mode</code>.
     * @param mode the first part of the key
     * @param dockable the second part of the key
     * @return the properties or <code>null</code>
     */
    protected H getProperties( M mode, Dockable dockable ){
        DockableHandle entry = dockables.get( dockable );
        if( entry == null )
            return null;
        
        return entry.properties.get( mode.getUniqueIdentifier() );
    }
    
    /**
     * Tells whether a {@link Mode} is currently applying to a dockable.
     * @return <code>true</code> if a mode is currently working
     */
    public boolean isOnTransition(){
		return onTransition;
	}

    /**
     * Removes the properties that belong to <code>dockable</code>.
     * @param dockable the element to remove
     */
    public void remove( Dockable dockable ){
        DockableHandle entry = dockables.remove( dockable );
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
        DockableHandle entry = dockables.get( dockable );
        if( entry != null ){
            entry.dockable = null;
            fireRemoved( dockable );
        }
    }
    
	
    /**
     * Called while reading modes in {@link #setSetting(ModeTransitionSetting)}.
     * Subclasses might change the mode according to <code>newMode</code>.
     * @param id the identifier of <code>dockable</code>
     * @param oldMode the mode <code>dockable</code> is currently in
     * @param newMode the mode <code>dockable</code> is going to be
     * @param dockable the element that changes its mode, might be <code>null</code>
     */
	protected abstract void applyDuringRead( String key, Path old, Path current, Dockable dockable );

    /**
     * Tells whether an entry for a missing {@link Dockable} should be created.
     * This will result in a call to {@link #addEmpty(String)} during
     * {@link #setSetting(ModeTransitionSetting)}.
     * The default implementation returns always <code>false</code>.
     * @param key the key for which to create a new entry
     * @return <code>true</code> if an entry should be created
     */
	protected boolean createEntryDuringRead( String key ){
		return false;
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
        
        DockableHandle entry = entries.get( key );
        
        if( entry == null ){
            entry = new DockableHandle( null, key );
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
        
        DockableHandle entry = entries.get( name );
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
		
		for( ModeHandle mode : modes ){
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
	public M getCurrentMode( Dockable dockable ){
		for( ModeHandle mode : modes ){
			if( mode.mode.isCurrentMode( dockable )){
				return mode.mode;
			}
		}
		
		return null;
	}
	
	/**
	 * Reading the history this method tells which mode 
	 * <code>dockable</code> was in before the current mode. 
	 * @param dockable some element
	 * @return the previous mode or <code>null</code> if this
	 * information is not available
	 */
	public M getPreviousMode( Dockable dockable ){
		DockableHandle handle = getHandle( dockable );
		if( handle == null )
			return null;
		ModeHandle mode = handle.previousMode();
		if( mode == null )
			return null;
		return mode.mode;
	}
	
	/**
	 * Gets the history which modes <code>dockable</code>
	 * used in the past. The older entries are at the beginning
	 * of the list. The current mode may or may not be included 
	 * in the list.
	 * @param dockable the element whose history is asked
	 * @return the history or an empty list if no history is available
	 */
	public List<M> getModeHistory( Dockable dockable ){
		DockableHandle handle = getHandle( dockable );
		if( handle == null )
			return Collections.emptyList();
		
		List<M> result = new ArrayList<M>();
		for( Path path : handle.history ){
			M mode = getMode( path );
			if( mode != null ){
				result.add( mode );
			}
		}
		return result;
	}
	
	/**
	 * Gets the history which properties <code>dockable</code>
	 * used in the past. Entries of value <code>null</code> are ignored.
	 * The older entries are at the beginning of the list.
	 * @param dockable the element whose history is asked
	 * @return the history or an empty list if no history is available
	 */
	public List<H> getPropertyHistory( Dockable dockable ){
		DockableHandle handle = getHandle( dockable );
		if( handle == null )
			return Collections.emptyList();
		
		List<H> result = new ArrayList<H>();
		for( Path path : handle.history ){
			H history = handle.properties.get( path );
			if( history != null ){
				result.add( history );
			}
		}
		return result;
	}
	
    /**
     * Store the history information for <code>dockable</code> and all its children in respect
     * to their current {@link Mode}. Dockables that are not registered at this manager
     * are ignored.
     * @param dockable a root of a tree
     */
    public void store( Dockable dockable ){
        DockUtilities.visit( dockable, new DockUtilities.DockVisitor(){
            @Override
            public void handleDockable( Dockable check ) {
            	M mode = getCurrentMode( check );
            	if( mode != null )
                    store( mode, check );
            }
        });
    }

    /**
     * Stores the location of <code>dockable</code> under the key <code>mode</code>.
     * @param mode the mode <code>dockable</code> is currently in
     * @param dockable the element whose location will be stored
     */
    protected void store( M mode, Dockable dockable ){
    	DockableHandle handle = getHandle( dockable );
    	if( handle != null ){
    		handle.properties.put( mode.getUniqueIdentifier(), mode.current( dockable ) );
    	}
    }

	/**
	 * Gets the <code>ModeAccess</code> which represents <code>mode</code>.
	 * @param mode some mode or <code>null</code>
	 * @return its access or <code>null</code>
	 * @throws IllegalArgumentException if <code>mode</code> is unknown
	 */
	private ModeHandle access( M mode ){
		if( mode == null )
			return null;
		
		for( ModeHandle access : modes ){
			if( access.mode == mode ){
				return access;
			}
		}
		
		throw new IllegalArgumentException( "unknown mode: " + mode );
	}

	/**
	 * Returns an iteration of all modes that are stored in this manager.
	 * @return the iteration
	 */
	public Iterable<M> modes(){
		return new Iterable<M>(){
			public Iterator<M> iterator(){
				final Iterator<ModeHandle> handles = modes.iterator();
				return new Iterator<M>(){
					public boolean hasNext(){
						return handles.hasNext();
					}
					public M next(){
						return handles.next().mode;
					}
					public void remove(){
						throw new UnsupportedOperationException( "cannot remove modes this way" );
					}
				};
			}
		};
	}
	
	/**
	 * Rebuilds the actions sources for all {@link Dockable}s.
	 */
	protected void rebuildAll(){
		for( DockableHandle handle : dockables.values() ){
			handle.updateActionSource();
		}
	}
	
	/**
	 * Rebuilds the action sources of <code>dockable</code>.
	 * @param dockable the element whose actions are to be updated
	 */
	protected void rebuild( Dockable dockable ){
		DockableHandle entry = dockables.get( dockable );
		if( entry != null ){
			entry.updateActionSource();
		}
	}
	
	private DockableHandle getHandle( Dockable dockable ){
		return dockables.get( dockable );
	}
	
	/**
	 * Creates a new {@link ModeSetting} which is configured to transfer data from
	 * this {@link ModeManager} to persistent storage or the other way. The new setting
	 * contains all the {@link ModeSettingFactory}s which are currently known to this manager.
	 * @param <B> the intermediate format
	 * @param converter conversion tool from this managers meta-data format to the intermediate
	 * format.
	 * @return the new empty settings
	 */
	public <B> ModeSettings<H, B> createSettings( ModeSettingsConverter<H, B> converter ){
		ModeSettings<H, B> settings = new ModeSettings<H, B>( converter );
		for( ModeSettingFactory<H> factory : factories.values() ){
			settings.addFactory( factory );
		}
		for( ModeHandle mode : modes ){
			if( mode.mode != null ){
				ModeSettingFactory<H> factory = mode.mode.getSettingFactory();
				if( factory != null ){
					settings.addFactory( factory );
				}
			}
		}
		return settings;
	}
	
	/**
	 * Writes all the information stored in this {@link ModeManager} to
	 * <code>setting</code>.
	 * @param setting the settings to fill
	 */
	public void writeSettings( ModeSettings<H,?> setting ){
		// dockables
		for( DockableHandle handle : entries.values() ){
			setting.add( handle.id, handle.getCurrent(), handle.properties, handle.history );
		}
		
		// modes
		for( ModeHandle handle : modes ){
			if( handle.mode != null ){
				setting.add( handle.mode );
			}
		}
	}
	
	public void readSettings( ModeSettings<H, ?> settings ){
		// dockables
        for( int i = 0, n = settings.size(); i < n; i++ ){
            String key = settings.getId( i );
            DockableHandle entry = entries.get( key );
            
            if( entry == null ){
                if( createEntryDuringRead( key )){
                    addEmpty( key );
                    entry = entries.get( key );
                }
            }
            
            if( entry != null ){
                Path current = settings.getCurrent( i );
                Path old = null;
                if( entry.dockable != null ){
                	M oldMode = getCurrentMode( entry.dockable );
                	if( oldMode != null ){
                		old = oldMode.getUniqueIdentifier();
                	}
                }
                
                if( current == null )
                    current = old;
                
                entry.history.clear();
                for( Path next : settings.getHistory( i ))
                    entry.history.add( next );
                
                entry.properties = settings.getProperties( i );
                
                if( (old == null && current != null) || (old != null && !old.equals( current ))){
                    applyDuringRead( key, old, current, entry.dockable );
                }
            }
        }
		
		// modes
		for( ModeHandle handle : modes ){
			if( handle.mode != null ){
				ModeSetting<H> setting = settings.getSettings( handle.mode.getUniqueIdentifier() );
				if( setting != null ){
					handle.mode.readSetting( setting );
				}
			}
		}
	}
	
	/**
	 * A wrapper around a mode, giving access to its properties. The mode
	 * inside this wrapper can be replaced any time.
	 * @author Benjamin Sigg
	 */
	private class ModeHandle{
		private M mode;
		
		public ModeHandle( M mode ){
			this.mode = mode;
		}
	}
	
    /**
     * Describes all properties a {@link Dockable} has.
     * @author Benjamin Sigg
     */
    private class DockableHandle{
        /** the {@link Dockable} for which the properties are stored */
        public Dockable dockable;
        /** a unique id associated with {@link #dockable} */
        public String id;
        
        /** the set of actions available for {@link #dockable} */
        public MultiDockActionSource source;
        /** a map that stores some properties mapped to the different modes */
        public Map<Path, H> properties;

        /** The modes this entry already visited. No mode is more than once in this list. */
        private List<Path> history;
        
        /**
         * Creates a new entry
         * @param dockable the element whose properties are stores in this entry
         * @param id the unique if of this entry
         */
        public DockableHandle( Dockable dockable, String id ){
            this.dockable = dockable;
            this.id = id;
            source = new MultiDockActionSource( new LocationHint( LocationHint.ACTION_GUARD, LocationHint.RIGHT ) );
            properties = new HashMap<Path, H>();
            history = new LinkedList<Path>();
        }
        
        /**
         * Updates the action source of this manager.
         */
        public void updateActionSource(){
        	if( dockable != null ){
	        	source.removeAll();
	        	M mode = getCurrentMode( dockable );
	        	if( mode == null )
	        		mode = getDefaultMode( dockable );
	        	
	        	for( ModeHandle access : modes ){
	        		DockActionSource next = access.mode.getActionsFor( dockable, mode );
	        		if( next != null ){
	        			source.add( next );
	        		}
	        	}
        	}
        }
        
        /**
         * Stores <code>mode</code> in a stack that describes the history
         * through which this entry moved. If <code>mode</code> is already
         * in the stack, than it is moved to the top of the stack. 
         * @param mode the mode to store
         */
        public void putMode( ModeHandle mode ){
            ModeHandle oldMode = peekMode();
            if( oldMode != mode ){
	            history.remove( mode );
	            history.add( mode.mode.getUniqueIdentifier() );
	            rebuild( dockable );
	            fireModeChanged( dockable, oldMode == null ? null : oldMode.mode, mode == null ? null : mode.mode );
            }
        }
        
        /**
         * Gets the mode that was used previously to the current mode.
         * If the history gets empty, then {@link ModeManager#getDefaultMode(Dockable)}
         * is returned.
         * @return the mode in which this entry was before the current mode
         * was put onto the history
         */
        public ModeHandle previousMode(){
            if( history.size() < 2 )
                return access( getDefaultMode( dockable ) );
            else
                return getAccess( history.get( history.size()-2 ) );
        }
        
        /**
         * Gets the current mode of this entry.
         * @return the mode or <code>null</code>
         */
        public ModeHandle peekMode(){
            if( history.isEmpty() )
                return null;
            else
                return getAccess( history.get( history.size()-1 ) );
        }
        
        /**
         * Gets the id of the current mode (if any).
         * @return the id or <code>null</code>
         */
        public Path getCurrent(){
        	if( dockable == null )
        		return null;
        	
        	M mode = getCurrentMode( dockable );
        	if( mode == null )
        		return null;
        	
        	return mode.getUniqueIdentifier();
        }
    }
    
    /**
     * Default implementation of {@link AffectedSet}. Linked to the enclosing
     * {@link ModeManager}.
     * @author Benjamin Sigg
     */
    private class ChangeSet implements AffectedSet{
        /** the changed elements */
        private Set<Dockable> set = new HashSet<Dockable>();

        /**
         * Creates a new set
         */
        public ChangeSet(){
        	// nothing
        }
        
        public void add( Dockable dockable ){
        	if( dockable != null ){
	            set.add( dockable );
	            DockStation station = dockable.asDockStation();
	            if( station != null ){
	                for( int i = 0, n = station.getDockableCount(); i<n; i++ ){
	                    add( station.getDockable( i ));
	                }
	            }
        	}
        }
        
        /**
         * Performs the clean up operations that are required after some
         * <code>Dockable</code>s have changed their mode.<br>
         * for each element known to this set.
         */
        public void finish(){
            for( Dockable dockable : set ){
            	DockableHandle handle = getHandle( dockable );
            	if( handle != null ){
            		handle.putMode( access( getCurrentMode( dockable ) ) );
            	}
            }
        }
    }
}
