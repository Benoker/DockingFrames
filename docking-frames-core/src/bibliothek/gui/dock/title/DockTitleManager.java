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

package bibliothek.gui.dock.title;

import java.util.Hashtable;
import java.util.Map;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.DockTheme;
import bibliothek.gui.dock.util.Priority;

/**
 * The manager of the {@link DockTitleFactory DockTitleFactories}. Every 
 * {@link DockStation} will try to register some factories here. The factories
 * can be overridden by a client or a {@link DockTheme}.
 * @author Benjamin Sigg
 */
public class DockTitleManager {
	/** unique identifier of the {@link DockTitleVersion} which represents the current {@link DockTheme}s default factory */
	public static final String THEME_FACTORY_ID = "theme";
	
    /** A map of all versions registered at this manager */
	private Map<String, DockTitleVersion> titleVersions = new Hashtable<String, DockTitleVersion>();
    /** The controller for which the factories are stored */
    private DockController controller;
    
    /**
     * Creates a new manager
     * @param controller the controller for which the titles are used
     */
    public DockTitleManager( DockController controller ){
    	if( controller == null )
    		throw new IllegalArgumentException( "Controller must not be null" );
    	this.controller = controller;
    	getVersion( THEME_FACTORY_ID, NullTitleFactory.INSTANCE );
    }
    
    /**
     * Tests whether there is a handle registered at <code>id</code> or not.
     * @param id the id
     * @return <code>true</code> if there is a handle, <code>false</code>
     * otherwise
     */
    public boolean existsTitleVersion( String id ){
        return titleVersions.containsKey( id );
    }
    
    /**
     * Gets the handle with the key <code>id</code>.
     * @param id the key
     * @return the handle or <code>null</code> if no handle is
     * registered
     */
    public DockTitleVersion getVersion( String id ){
        return titleVersions.get( id );
    }
    
    /**
     * Gets the handle with the key <code>id</code>. If the key is unknown, then
     * a new handle is created, using <code>factory</code> as default factory.
     * @param id the key of the handle
     * @param factory the default factory used when a new handle has to be created, can be <code>null</code>
     * @return the handle
     */
    public DockTitleVersion getVersion( String id, DockTitleFactory factory ){
        DockTitleVersion version = titleVersions.get( id );
        if( version == null ){
            version = new DockTitleVersion( controller, id );
            titleVersions.put( id, version );
        }
       
        if( version.getFactory( Priority.DEFAULT ) == null ){
        	version.setFactory( factory, Priority.DEFAULT );
        }

        return version;
    }
    
    /**
     * Registers a factory with client-priority
     * @param id the key of the factory
     * @param factory the factory
     * @return a handle of the factories of this id
     */
    public DockTitleVersion registerClient( String id, DockTitleFactory factory ){
    	return register( id, factory, Priority.CLIENT );
    }

    /**
     * Registers a factory with theme-priority
     * @param id the key of the factory
     * @param factory the factory
     * @return a handle of the factories of this id
     */
    public DockTitleVersion registerTheme( String id, DockTitleFactory factory ){
    	return register( id, factory, Priority.THEME );
    }
    
    /**
     * Registers a factory with default-priority
     * @param id the key of the factory
     * @param factory the factory
     * @return a handle of the factories of this id
     */
    public DockTitleVersion registerDefault( String id, DockTitleFactory factory ){
    	return register( id, factory, Priority.DEFAULT );
    }
    
    /**
     * Registers a factory for the given key
     * @param id the key of the factory
     * @param factory the factory
     * @param priority the priority of this registration
     * @return the handle to the factory or a factory with higher priority
     */
    public DockTitleVersion register( String id, DockTitleFactory factory, Priority priority ){
        DockTitleVersion version = titleVersions.get( id );
        if( version == null ){
            version = new DockTitleVersion( controller, id );
            titleVersions.put( id, version );
        }
        
        version.setFactory( factory, priority );
        
        return version;    
    }
    
    /**
     * Removes all factories that were added by a theme
     */
    public void clearThemeFactories(){
    	for( DockTitleVersion version : titleVersions.values() )
    		version.setFactory( null, Priority.THEME );
    }
}
