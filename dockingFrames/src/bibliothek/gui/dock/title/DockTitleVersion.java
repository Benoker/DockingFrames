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

package bibliothek.gui.dock.title;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.util.Priority;

/**
 * A DockTitleVersion is a hint which {@link DockTitleFactory} has to
 * be used to create one or more {@link DockTitle DockTitle}
 * for some {@link Dockable Dockables}.<br>
 * DockTitleVersions are created and registered by a {@link DockTitleManager}.<br>
 * Every version consists of three slots for factories, each with different
 * priority. If a new title is required, the factory with the highest priority
 * will be used.
 * @author Benjamin Sigg
 */
public class DockTitleVersion{
    /** the three slots for the factories */
    private DockTitleFactory[] factories = new DockTitleFactory[3];
    /** the name of this version */
    private String id;
    /** the controller for which the titles are created */
    private DockController controller;
    
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
    	
    	return controller.getTheme().getTitleFactory( controller );
    }
    
    /**
     * Stores <code>factory</code> at the slot <code>priority</code>.
     * @param factory the factory
     * @param priority the importance of the factory
     */
    public void setFactory( DockTitleFactory factory, Priority priority ){
    	factories[ map( priority ) ] = factory;
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
        return (obj instanceof DockTitleVersion) && ((DockTitleVersion)obj).id.equals( id );
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
     * Creates a title for <code>dockable</code>. The factory
     * with the highest priority is used for this job.
     * @param dockable the Dockable which needs a title
     * @return the title, might be <code>null</code>
     */
    public DockTitle createDockable( Dockable dockable ){
        DockTitleFactory factory = getFactory();
        if( factory == null )
            return null;
        return factory.createDockableTitle( dockable, this );
    }
    
    /**
     * Creates a title for <code>dock</code>. The factory
     * with the highest priority is used for this job.
     * @param <D> a class which is Dockable and DockStation
     * @param dock the dockable station
     * @return the title, might be <code>null</code>
     */
    public <D extends Dockable & DockStation> DockTitle createStation( D dock ){
        DockTitleFactory factory = getFactory();
        if( factory == null )
            return null;
        return factory.createStationTitle( dock, this );
    }
    
    /**
     * Gets the controller for which the titles are created.
     * @return the owner of this version
     */
    public DockController getController(){
        return controller;
    }
}