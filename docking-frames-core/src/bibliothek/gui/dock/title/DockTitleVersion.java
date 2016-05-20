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

import java.util.ArrayList;
import java.util.List;

import bibliothek.gui.DockController;
import bibliothek.gui.DockTheme;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DockFactory;
import bibliothek.gui.dock.event.UIListener;
import bibliothek.gui.dock.util.Priority;
import bibliothek.gui.dock.util.extension.Extension;
import bibliothek.gui.dock.util.extension.ExtensionName;
import bibliothek.util.Path;

/**
 * A <code>DockTitleVersion</code> is a hint which {@link DockTitleFactory} has to
 * be used to create one or more {@link DockTitle DockTitle} for some {@link Dockable}.<br>
 * <code>DockTitleVersions</code> are created and registered by a {@link DockTitleManager}.<br>
 * Every version consists of three slots for factories, each with different
 * priority. If a new title is required, the factory with the highest priority
 * will be used.<br>
 * <code>DockTitleVersion</code> implements {@link DockTitleFactory} for convenience.
 * @author Benjamin Sigg
 */
public class DockTitleVersion implements DockTitleFactory{
	/** 
	 * Name of the {@link ExtensionName} that allows to load additional {@link DockFactory}s into this 
	 * {@link DockTitleVersion}. These factories will be asked to create a {@link DockTitle} before
	 * the real factory is asked.
	 */
	public static final Path DOCK_TITLE_VERSION_EXTENSION = new Path( "dock.DockTitleVersion" );
	
	/** Name of the only property of an {@link ExtensionName}, the property points to <code>this</code> */
	public static final String DOCK_TITLE_VERSION_EXTENSION_PARAMETER = "version";
	
    /** the three slots for the factories */
    private DockTitleFactory[] factories = new DockTitleFactory[3];
    /** additional high priority factories created by an {@link Extension} */
    private DockTitleFactory[] extensionFactories;
    /** the name of this version */
    private String id;
    /** the controller for which the titles are created */
    private DockController controller;
    
    /** the current requests on this version */
    private List<DockTitleRequest> requests = new ArrayList<DockTitleRequest>();
    
    /** whether the theme is currently changing, a version does automatically call {@link DockTitleRequest#request()} while the theme changes */
    private boolean onThemeChange = false;
    
    /**
     * Creates a new version.
     * @param controller the controller for which titles will be created
     * @param id the unique name of this version
     */
    public DockTitleVersion( DockController controller, String id ){
        if( controller == null )
            throw new NullPointerException( "Controller must not be null" );
        
        if( id == null )
            throw new IllegalArgumentException( "The ID must not be null" );
        
        this.controller = controller;
        this.id = id;
        controller.getThemeManager().addUIListener( new UIListener() {
			public void updateUI( DockController controller ){
				// ignore	
			}
			
			public void themeWillChange( DockController controller, DockTheme oldTheme, DockTheme newTheme ){
				onThemeChange = true;
			}
			
			public void themeChanged( DockController controller, DockTheme oldTheme, DockTheme newTheme ){
				onThemeChange = false;
			}
		});
        
        List<DockTitleFactory> list = controller.getExtensions().load( new ExtensionName<DockTitleFactory>( DOCK_TITLE_VERSION_EXTENSION, DockTitleFactory.class, DOCK_TITLE_VERSION_EXTENSION_PARAMETER, this ) );
        if( !list.isEmpty() ){
        	extensionFactories = list.toArray( new DockTitleFactory[ list.size() ] );
        }
    }
    
    /**
     * Adds <code>request</code> to this version. The <code>request</code> will
     * be installed on the current {@link DockTitleFactory} of this version.
     * This method should not be called by clients, clients should call {@link DockTitleRequest#install()}.
     * @param request the new request, not <code>null</code>
     */
    public void install( DockTitleRequest request ){
    	if( request == null )
    		throw new IllegalArgumentException( "request must not be null" );
    	requests.add( request );
    	DockTitleFactory factory = getFactory();
    	if( factory != null ){
    		factory.install( request );
    	}
    }
    
    /**
     * Removes <code>request</code> from this version. This method should not be
     * called by clients, clients should call {@link DockTitleRequest#uninstall()}.
     * @param request the request to remove
     */
    public void uninstall( DockTitleRequest request ){
    	requests.remove( request );
    	DockTitleFactory factory = getFactory();
    	if( factory != null ){
    		factory.uninstall( request );
    	}
    }
    
    /**
     * Calls {@link DockTitleFactory#request(DockTitleRequest)} for the current
     * factory. If there are any {@link #DOCK_TITLE_VERSION_EXTENSION extensions} installed, then
     * these extensions are questioned first.
     * @param request the request to answer
     */
    public void request( DockTitleRequest request ){
    	DockTitleFactory client = getFactory( Priority.CLIENT );
    	if( client != null ){
    		client.request( request );
    		return;
    	}
    	
    	if( extensionFactories != null ){
    		for( DockTitleFactory factory : extensionFactories ){
    			factory.request( request );
    			if( request.isAnswered() ){
    				return;
    			}
    		}
    	}
    	
    	DockTitleFactory factory = getFactory();
    	if( factory != null ){
    		factory.request( request );
    	}
    }
    
    /**
     * Calls {@link DockTitleRequest#request()} for all {@link DockTitleRequest}s
     * that are currently installed on this version.
     */
    public void request(){
    	for( DockTitleRequest request : requests ){
    		request.request();
    	}
    }
    
    /**
     * Gets the index of the slot in {@link #factories} which is used for
     * the factory with the given <code>priority</code>. 
     * @param priority the priority of the slot
     * @return the index of the slot
     */
    private int map( Priority priority ){
    	switch( priority ){
    		case CLIENT: return 0;
    		case THEME: return 1;
    		default: return 2;
    	}
    }
    
    /**
     * Gets the factory with the highest priority
     * @return the factory or <code>null</code> if there is no 
     * factory registered.
     */
    public DockTitleFactory getFactory(){
    	for( int i = 0; i < factories.length; i++ ){
    		if( factories[i] != null )
    			return factories[i];
    	}
    	
    	return null;
    }
    
    /**
     * Stores <code>factory</code> at the slot <code>priority</code>.
     * @param factory the factory
     * @param priority the importance of the factory
     */
    public void setFactory( DockTitleFactory factory, Priority priority ){
    	DockTitleFactory oldFactory = getFactory();
    	factories[ map( priority ) ] = factory;
    	DockTitleFactory newFactory = getFactory();
    	
    	if( oldFactory != newFactory ){
    		if( oldFactory != null ){
    			for( DockTitleRequest request : requests ){
    				oldFactory.uninstall( request );
    			}
    		}
    		if( newFactory != null ){
    			for( DockTitleRequest request : requests ){
    				newFactory.install( request );
    				if( !onThemeChange ){
    					request.request();
    				}
    			}
    		}
    	}
    }
    
    /**
     * Gets the factory with the given priority.
     * @param priority the priority
     * @return the factory or <code>null</code>
     */
    public DockTitleFactory getFactory( Priority priority ){
        return factories[ map( priority ) ];
    }
    
    /**
     * Tells whether there is a factory with the importance <code>priority</code>,
     * or not.
     * @param priority the searched priority
     * @return <code>true</code> if there is a factory
     */
    public boolean isSet( Priority priority ){
    	return factories[ map( priority ) ] != null;
    }
    
    @Override
    public int hashCode() {
        return id.hashCode();
    }
    
    @Override
    public boolean equals( Object obj ) {
		if (obj == this) {
			return true;
		}

		if (obj == null) {
			return false;
		}

		if (obj.getClass() == this.getClass()) {
			return (((DockTitleVersion)obj).id.equals( id ));
		}

		return false;
    }
    
    /**
     * Gets the name of this version. The name is set when this version
     * is created. It should be unique; it is unique if all versions are
     * created through the same {@link DockTitleManager}.
     * @return the name
     */
    public String getID(){
        return id;
    }
    
    /**
     * Gets the controller for which the titles are created.
     * @return the owner of this version
     */
    public DockController getController(){
        return controller;
    }
}