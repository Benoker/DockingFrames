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
package bibliothek.gui.dock.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import bibliothek.gui.DockController;
import bibliothek.util.Path;

/**
 * A map containing some string-values pairs and so called
 * bridges to modify these values when reading them out. 
 * @author Benjamin Sigg
 * @param <V> The kind of values this map contains
 * @param <U> The kind of observers used to read values from this map
 * @param <B> The kind of bridges used to transfer values <code>V</code> to observers <code>U</code>
 */
public class UIProperties<V, U extends UIValue<V>, B extends UIBridge<V, U>> {
    /** the map of providers known to this manager */
    private Map<Path, UIPriorityValue<B>> bridges = new HashMap<Path, UIPriorityValue<B>>();
    
    /** how often some bridges are observed */
    private Map<Path, Integer> bridgesAccess = new HashMap<Path, Integer>();
    
    /** the map of resources that have been set */
    private Map<String, UIPriorityValue<V>> resources = new HashMap<String, UIPriorityValue<V>>();
    
    /** how often some resources are observed */
    private Map<String, Integer> resourcesAccess = new HashMap<String, Integer>();
    
    /** all the backup schemes for missing values (resources and bridges) */
    private PriorityValue<UIScheme<V, U, B>> schemes = new PriorityValue<UIScheme<V,U,B>>();
    
    /** all the listeners to the {@link #schemes} */
    private PriorityValue<UISchemeListener<V, U, B>> schemeListeners = new PriorityValue<UISchemeListener<V,U,B>>();
    
    /** a list of all observers */
    private List<Observer> observers = new LinkedList<Observer>();
    
    /** whether to stall updates or not */
    private int updateLock = 0;
    
    /** the owner of this properties map */
    private DockController controller;
    
    /**
     * Creates a new map.
     * @param controller the owner of this map
     */
    public UIProperties( DockController controller ){
    	this.controller = controller;
    }
    
    /**
     * Gets the controller in whose realm this map is used.
     * @return the controller, not <code>null</code>
     */
    public DockController getController(){
		return controller;
	}
    
    /**
     * Tells this manager to stall all updates. No {@link UIValue} will
     * be informed when a color or provider changes.
     */
    public void lockUpdate(){
        updateLock++;
    }
    
    /**
     * Tells this manager no longer to stall updates. This triggers a full
     * update on all {@link UIValue}s.
     */
    public void unlockUpdate(){
        updateLock--;
        if( updateLock == 0 ){
            for( Observer observer : observers )
                observer.resetAll();
        }
    }
    
    /**
     * Gets the {@link UIScheme} that is used to fill up missing values in
     * the level <code>priority</code>.
     * @param priority some priority
     * @return the scheme of that level or <code>null</code>
     * @see #setScheme(Priority, UIScheme)
     */
    public UIScheme<V, U, B> getScheme( Priority priority ){
    	return schemes.get( priority );
    }
    
    /**
     * Sets or removes an {@link UIScheme} for the level <code>priority</code> of this
     * {@link UIProperties}. The scheme will be used to fill missing values of this properties. Since
     * a "missing resource" cannot be removed, any attempt to delete a resource created by a scheme
     * must fail. 
     * @param priority the level which will be provided with new values from <code>scheme</code>.
     * @param scheme the new scheme or <code>null</code>
     */
    public void setScheme( final Priority priority, UIScheme<V, U, B> scheme ){
    	UIScheme<V, U, B> old = schemes.get( priority );
    	schemes.set( priority, scheme );
    	
    	if( old != scheme ){
    		if( old != null ){
    			old.removeListener( schemeListeners.get( priority ) );
    			
	    		int count = 0;
	    		for( Priority p : Priority.values() ){
	    			if( schemes.get( p ) == old ){
	    				count++;
	    			}
	    		}
	    		if( count == 0 ){
	    			old.uninstall( this );
	    		}
    		}
    		
    		if( scheme != null ){
	    		int count = 0;
	    		for( Priority p : Priority.values() ){
	    			if( schemes.get( p ) == scheme ){
	    				count++;
	    			}
	    		}
	    		if( count == 1 ){
	    			scheme.install( this );
	    		}
	    		
	    		if( schemeListeners.get( priority ) == null ){	    		
	    			schemeListeners.set( priority, new UISchemeListener<V, U, B>(){
	    				public void changed( UISchemeEvent<V, U, B> event ){
		    				schemeUpdate( priority, event );	
	    				}
					});
	    		}
	    		
	    		scheme.addListener( schemeListeners.get( priority ) );
    		}
    		
    		fullSchemeUpdate( priority );
    	}
    }
    
    private void fullSchemeUpdate( Priority priority ){
    	schemeUpdate( priority, new UISchemeEvent<V,U,B>(){
			public Collection<Path> changedBridges( Set<Path> names ){
				return null;
			}
			public Collection<String> changedResources( Set<String> names ){
				return null;
			}
			public UIScheme<V,U,B> getScheme(){
				return null;
			}
		});
    }
    
    private void schemeUpdate( Priority priority, UISchemeEvent<V, U, B> event ){
    	try{
    		lockUpdate();
    		
    		// collect changes
    		Set<String> usedResources = getAllUsedResources();
    		Collection<String> changedResources = event.changedResources( usedResources );
    		if( changedResources == null ){
    			changedResources = usedResources;
    		}
    		
    		Set<Path> usedBridges = getAllUsedBridges();
    		Collection<Path> changedBridges = event.changedBridges( usedBridges );
    		if( changedBridges == null ){
    			changedBridges = usedBridges;
    		}
    		
    		UIScheme<V, U, B> scheme = schemes.get( priority );
    		
    		// resources
    		for( String name : changedResources ){
    			UIPriorityValue<V> value = resources.get( name );
    			V replacement = null;
    			if( scheme != null ){
    				replacement = scheme.getResource( name, this );
    			}
    			
    			if( value == null ){
    				if( replacement != null ){
    					value = new UIPriorityValue<V>();
    					value.set( priority, replacement, scheme );
    					if( !isRemoveable( name, value )){
    						resources.put( name, value );
    					}
    				}
    			}
    			else{
    				if( value.getScheme( priority ) == null ){
    					if( value.get( priority ) == null ){
    						value.set( priority, replacement, scheme );
    					}
    				}
    				else{
    					value.set( priority, replacement, scheme );
    				}
    				if( isRemoveable( name, value )){
    					resources.remove( name );
    				}
    			}
    		}
    		
    		// bridges
    		for( Path name : changedBridges ){
    			UIPriorityValue<B> value = bridges.get( name );
    			B replacement = null;
    			if( scheme != null ){
    				replacement = scheme.getBridge( name, this );
    			}
    			
    			if( value == null ){
    				if( replacement != null ){
    					value = new UIPriorityValue<B>();
    					value.set( priority, replacement, scheme );
    					if( !isRemoveable( name, value )){
    						bridges.put( name, value );
    					}
    				}
    			}
    			else{
    				if( value.getScheme( priority ) == null ){
    					if( value.get( priority ) == null ){
    						value.set( priority, replacement, scheme );
    					}
    				}
    				else{
    					value.set( priority, replacement, scheme );
    				}
    				if( isRemoveable( name, value )){
    					bridges.remove( name );
    				}
    			}
    		}
    	}
    	finally{
    		unlockUpdate();
    	}
    }
    
    private Set<String> getAllUsedResources(){
    	Set<String> result = new HashSet<String>();
    	for( Observer observer : observers ){
    		result.add( observer.id );
    	}
    	return result;
    }
    
    private Set<Path> getAllUsedBridges(){
    	Set<Path> result = new HashSet<Path>();
    	for( Observer observer : observers ){
    		result.add( observer.path );
    	}
    	return result;
    }
    
    /**
     * Adds a new bridge between this {@link UIProperties} and a set of
     * {@link UIValue}s that have a certain type.
     * @param priority the importance of the new provider
     * @param path the path for which this bridge should be used.
     * @param bridge the new bridge
     */
    public void publish( Priority priority, Path path, B bridge ){
        if( priority == null )
            throw new IllegalArgumentException( "priority must not be null" );
        if( path == null )
            throw new IllegalArgumentException( "path must not be null" );
        if( bridge == null )
            throw new IllegalArgumentException( "bridge must not be null" );
        
        UIPriorityValue<B> value = bridges.get( path );
        if( value == null ){
            value = createBridge( path );
            bridges.put( path, value );
        }
        
        if( value.set( priority, bridge, null )){
            if( updateLock == 0 ){
                for( Observer check : observers ){
                    check.resetBridge();
                }
            }
        }
    }
    

    /**
     * Removes the bridge that handles the {@link UIValue}s of kind <code>path</code>. Please note
     * that bridges created by the current {@link UIScheme} cannot be removed. Also note that the removed bridge
     * may be replaced by a bridge created by the current {@link UIScheme}.
     * @param priority the importance of the bridge 
     * @param path the path of the bridge
     */
    public void unpublish( Priority priority, Path path ){
        UIPriorityValue<B> value = bridges.get( path );
        if( value != null && value.getScheme( priority ) == null ){
        	UIScheme<V,U,B> scheme = schemes.get( priority );
        	B bridge = null;
        	
        	if( scheme != null ){
        		bridge = scheme.getBridge( path, this );
        	}
        	
            boolean change = value.set( priority, bridge, scheme );
            if( isRemoveable( path, value ) ){
                bridges.remove( path );
            }
            
            if( change && updateLock == 0 ){
                for( Observer check : observers ){
                    check.resetBridge();
                }
            }   
        }
    }
    
    /**
     * Searches for all occurrences of <code>bridge</code> and removes them. Please note
     * that bridges created by the current {@link UIScheme} cannot be removed. Also note that the removed bridge
     * may be replaced by a bridge created by the current {@link UIScheme}.
     * All {@link UIValue}s that used <code>bridge</code> are redistributed.
     * @param priority the importance of the bridge 
     * @param bridge the bridge to remove
     */
    public void unpublish( Priority priority, B bridge ){
        Iterator<Map.Entry<Path, UIPriorityValue<B>>> iterator = bridges.entrySet().iterator();
        boolean change = false;
        
        UIScheme<V, U, B> scheme = schemes.get( priority );
        
        while( iterator.hasNext() ){
        	Map.Entry<Path, UIPriorityValue<B>> entry = iterator.next();
        	UIPriorityValue<B> next = entry.getValue();
        	
            if( next.get( priority ) == bridge ){
            	if( next.getScheme( priority ) == null ){
            		B replacement = null;
            		if( scheme != null ){
            			replacement = scheme.getBridge( entry.getKey(), this );
            		}
            		
	                change = next.set( priority, replacement, scheme ) || change;
	                if( isRemoveable( entry.getKey(), next ) ){
	                    iterator.remove();
	                }
            	}
            }
        }
        
        if( change && updateLock == 0 ){
            for( Observer check : observers ){
                check.resetBridge();
            }
        }
    }
    
    /**
     * Gets the bridge which is stored on level <code>priority</code> for {@link UIValue}s
     * of kind <code>path</code>.
     * @param priority the level in which to search
     * @param path the kind of the {@link UIValue}s
     * @return either <code>null</code>, a bridge that has been {@link #publish(Priority, Path, UIBridge) published}
     * or a bridge that was created by an {@link UIScheme}
     */
    public B getBridge( Priority priority, Path path ){
    	UIPriorityValue<B> bridge = bridges.get( path );
    	if( bridge == null ){
    		bridge = createBridge( path );
    		if( !isRemoveable( path, bridge )){
    			bridges.put( path, bridge );
    		}
    	}

    	if( bridge != null ){
    		UIPriorityValue.Value<B> value = bridge.get( priority );
    		if( value != null ){
    			return value.getValue();
    		}
    	}
    	return null;
    }

    /**
     * Tells whether <code>bridge</code> is stored in this map.
     * @param bridge some object to search
     * @return <code>true</code> if <code>bridge</code> was found anywhere
     */
    public boolean isStored( B bridge ){
    	for( UIPriorityValue<B> value : bridges.values() ){
    		for( Priority priority : Priority.values() ){
    			if( value.getValue( priority ) == bridge ){
    				return true;
    			}
    		}
    	}
    	return false;
    }
    
    /**
     * Tells whether the bridge with id <code>path</code> is observed by at least one {@link UIValue}.
     * @param path the name of some {@link UIBridge}
     * @return if <code>path</code> is observed
     */
    public boolean isObserved( Path path ){
    	return bridgesAccess.containsKey( path );
    }
    
    private boolean isRemoveable( Path path, UIPriorityValue<B> value ){
    	if( value.getValue() == null ){
    		return true;
    	}
    	if( value.isAllScheme() && !isObserved( path )){
    		return true;
    	}
    	return false;
    }
    
    private void checkRemove( Path path ){
    	UIPriorityValue<B> value = bridges.get( path );
    	if( value != null ){
	    	if( isRemoveable( path, value )){
	    		bridges.remove( path );
	    	}
    	}
    }
    
    /**
     * Installs a new {@link UIValue}. The value will be informed about
     * any change in the resource <code>id</code>.
     * @param id the id of the resource that <code>value</code> will monitor
     * @param path the kind of the value
     * @param value the new observer
     */
    public void add( String id, Path path, U value ){
        if( path == null )
            throw new IllegalArgumentException( "path must not be null" );
        if( id == null )
            throw new IllegalArgumentException( "id must not be null" );
        if( value == null )
            throw new IllegalArgumentException( "value must not be null" );
        
        Observer combination = new Observer( id, path, value );
        observers.add( combination );
        combination.resetAll();
    }
    
    /**
     * Uninstalls an observer of a resource
     * @param value the observer to remove
     */
    public void remove( U value ){
        ListIterator<Observer> list = observers.listIterator();
        while( list.hasNext() ){
            Observer next = list.next();
            if( next.getValue() == value ){
                list.remove();
                next.destroy();
                return;
            }
        }
    }
    
    /**
     * Tells whether the value with id <code>id</code> is observed by at least one {@link UIValue}.
     * @param id the name of some value
     * @return if <code>id</code> is observed
     */
    public boolean isObserved( String id ){
    	return resourcesAccess.containsKey( id );
    }
    
    private boolean isRemoveable( String id, UIPriorityValue<V> value ){
    	if( value.getValue() == null ){
    		return true;
    	}
    	if( value.isAllScheme() && !isObserved( id )){
    		return true;
    	}
    	return false;
    }
    
    private void checkRemove( String id ){
    	UIPriorityValue<V> value = resources.get( id );
    	if( value != null ){
	    	if( isRemoveable( id, value )){
	    		resources.remove( id );
	    	}
    	}
    }
    
    /**
     * Searches a bridge that can be used for <code>path</code>.
     * @param path the kind of bridge that is searched. First a bridge for
     * <code>path</code> will be searched, then for the parent of <code>path</code>,
     * and so on...
     * @return the bridge or <code>null</code>
     */
    protected B getBridgeFor( Path path ){
        while( path != null ){
            UIPriorityValue<B> bridge = bridges.get( path );
            if( bridge == null ){
            	bridge = createBridge( path );
            	if( !isRemoveable( path, bridge )){
            		bridges.put( path, bridge );
            	}
            }
            
            if( bridge != null ){
                B result = bridge.getValue();
                if( result != null )
                    return result;
            }
            
            path = path.getParent();
        }
        
        return null;
    }
    
    /**
     * Sets a new resource and informs all {@link UIValue} that are observing <code>id</code> about the change.
     * Please note that values created by an {@link UIScheme} cannot be removed, and that a removed value may
     * be replaced by a value of an {@link UIScheme}.
     * @param priority the importance of this value
     * @param id the name of the value
     * @param resource the new resource, can be <code>null</code>
     */
    public void put( Priority priority, String id, V resource ){
        UIPriorityValue<V> value = resources.get( id );
        if( value == null && resource != null ){
            value = createResource( id );
            resources.put( id, value );
        }
        
        if( value != null ){
        	UIScheme<V, U, B> scheme = null;
        	if( resource == null ){
        		scheme = schemes.get( priority );
        		if( scheme != null ){
        			resource = scheme.getResource( id, this );
        		}
        	}
        	
	        if( value.set( priority, resource, scheme ) ){
	            if( updateLock == 0 ){
	                for( Observer observer : observers ){
	                    if( observer.id.equals( id )){
	                        observer.update( resource );
	                    }
	                }
	            }
	        }
	        
	        if( isRemoveable( id, value ) ){
	            resources.remove( id );
	        }
        }
    }
    
    /**
     * Gets a resource.
     * @param id the id of the resource
     * @return the resource or <code>null</code>
     * @see #put(Priority, String, Object)
     */
    public V get( String id ){
        UIPriorityValue<V> value = resources.get( id );
        if( value == null ){
        	value = createResource( id );
        	if( !isRemoveable( id, value )){
        		resources.put( id, value );
        	}
        }
        
        return value == null ? null : value.getValue();
    }
    
    /**
     * Call {@link UIValue#set(Object)} with the matching value that is stored in this
     * map for <code>id</code>.
     * @param id the unique identifier of the value to read
     * @param kind the kind of value <code>key</code> is
     * @param key the destination of the value
     */
    public void get( String id, Path kind, U key ){
    	V base = get( id );
    	B bridge = getBridgeFor( kind );
    	if( bridge != null ){
    		bridge.set( id, base, key );
    	}
    	else{
    		key.set( base );
    	}
    }
    
    /**
     * Removes all values that stored under the given priority. Values created by an {@link UIScheme} are
     * not affected by this call.
     * @param priority the priority whose elements should be removed
     */
    public void clear( Priority priority ){
    	UIScheme<V, U, B> scheme = schemes.get( priority );
    	
    	Iterator<Map.Entry<String, UIPriorityValue<V>>> resources = this.resources.entrySet().iterator();
    	while( resources.hasNext() ){
    		Map.Entry<String, UIPriorityValue<V>> entry = resources.next();
    		UIPriorityValue<V> value = entry.getValue();
    		
    		if( value.getScheme( priority ) == null ){
    			V replacement = null;
    			if( scheme != null ){
    				replacement = scheme.getResource( entry.getKey(), this );
    			}
    			value.set( priority, replacement, scheme );
        		if( isRemoveable( entry.getKey(), value )){
        			resources.remove();
        		}
    		}
    	}
    	
    	Iterator<Map.Entry<Path, UIPriorityValue<B>>> bridges = this.bridges.entrySet().iterator();
        while( bridges.hasNext() ){
        	Map.Entry<Path, UIPriorityValue<B>> entry = bridges.next();
    		UIPriorityValue<B> value = entry.getValue();
    		
    		if( value.getScheme( priority ) == null ){
    			B replacement = null;
    			if( scheme != null ){
    				replacement = scheme.getBridge( entry.getKey(), this );
    			}
    			value.set( priority, replacement, scheme );
        		if( isRemoveable( entry.getKey(), value )){
        			bridges.remove();
        		}
    		}
        }
        
        if( updateLock == 0 ){
            for( Observer observer : observers ){
                observer.resetAll();
            }
        }
    }
    
    /**
     * Sets up a new {@link PriorityValue} for the bridge with name <code>path</code>, the 
     * {@link PriorityValue} is pre-filled with the values of all the schemes known to this properties.
     * @param path the path of some bridge
     * @return the new filled value
     */
    private UIPriorityValue<B> createBridge( Path path ){
    	UIPriorityValue<B> result = new UIPriorityValue<B>();
    	
    	for( Priority priority : Priority.values() ){
    		UIScheme<V, U, B> scheme = schemes.get( priority );
    		if( scheme != null ){
    			B value = scheme.getBridge( path, this );
    			if( value != null ){
    				result.set( priority, value, scheme );
    			}
    		}
    	}
    	
    	return result;
    }
    
    /**
     * Creates a new {@link PriorityValue} that is set up with the resources of the current {@link UIScheme}s.
     * @param name the name of the value
     * @return the new value
     */
    private UIPriorityValue<V> createResource( String name ){
    	UIPriorityValue<V> result = new UIPriorityValue<V>();
    	
    	for( Priority priority : Priority.values() ){
    		UIScheme<V, U, B> scheme = schemes.get( priority );
    		if( scheme != null ){
    			V value = scheme.getResource( name, this );
    			if( value != null ){
    				result.set( priority, value, scheme );
    			}
    		}
    	}
    	
    	return result;
    }
    
    /**
     * Represents the combination of a resource, an {@link UIValue} and its 
     * {@link UIBridge}.
     * @author Benjamin Sigg
     */
    private class Observer{
        /** the id of the observed resource */
        private String id;
        /** the kind of value this observers bridge handles */
        private Path path;
        /** the observer which gets informed about changed resources */
        private U value;
        /** a bridge for modified resources */
        private B bridge;
        
        /**
         * Creates a new observer
         * @param id the id of the observed resource
         * @param path the type of observer that is wrapped by this <code>Observer</code>
         * @param value the listener for changed resources
         */
        public Observer( String id, Path path, U value ){
            this.id = id;
            this.path = path;
            this.value = value;
            
            Integer count = bridgesAccess.get( path );
            if( count == null ){
            	count = 1;
            }
            else{
            	count = count+1;
            }
            bridgesAccess.put( path, count );
            
            count = resourcesAccess.get( id );
            if( count == null ){
            	count = 1;
            }
            else{
            	count = count+1;
            }
            resourcesAccess.put( id, count );
        }
        
        /**
         * Tells this observer to release resources.
         */
        public void destroy(){
        	setBridge( null, false );
        	
        	Integer count = bridgesAccess.get( path );
            if( count == 1 ){
            	bridgesAccess.remove( path );
            	checkRemove( path );
            }
            else{
            	bridgesAccess.put( path, count-1 );
            }
            
            count = resourcesAccess.get( id );
            if( count == 1 ){
            	resourcesAccess.remove( id );
            	checkRemove( id );
            }
            else{
            	resourcesAccess.put( id, count-1 );
            }
        }
        
        /**
         * Gets the listener for changed resources.
         * @return the listener
         */
        public U getValue() {
            return value;
        }
        
        /**
         * Updates resource and bridge of this <code>Observer</code>.
         */
        public void resetAll(){
            B bridge = getBridgeFor( path );
            if( bridge == null )
                update( get( id ));
            else
                setBridge( bridge, true );
        }
        
        /**
         * Ensures that the correct {@link UIBridge} is used.
         */
        public void resetBridge(){
            setBridge( getBridgeFor( path ), false );
        }
        
        /**
         * Sets the {@link UIBridge} of this <code>Observer</code>.
         * @param bridge the new bridge, can be <code>null</code>
         * @param force if <code>true</code>, than an update of the resources will
         * be done anyway. Otherwise an update will only be done if a new
         * bridge is set.
         */
        public void setBridge( B bridge, boolean force ) {
            if( this.bridge != bridge ){
                if( this.bridge != null )
                    this.bridge.remove( id, value );
                
                this.bridge = bridge;
                
                if( this.bridge != null ){
                    this.bridge.add( id, value );
                }
                
                update( get( id ));
            }
            else if( force ){
                update( get( id ));
            }
        }
        
        /**
         * Called when a resource has been exchanged and the callback of this
         * <code>Observer</code> has to be informed.
         * @param value the new value of the resource, can be <code>null</code>
         */
        public void update( V value ){
            if( bridge == null )
                this.value.set( value );
            else
                bridge.set( id, value, this.value );
        }
    }

}
