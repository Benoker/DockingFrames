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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import bibliothek.gui.dock.common.CContentArea;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.CControlRegister;
import bibliothek.gui.dock.common.CStation;
import bibliothek.gui.dock.common.CStationContainer;
import bibliothek.gui.dock.common.CStationContainerListener;
import bibliothek.gui.dock.common.MultipleCDockable;
import bibliothek.gui.dock.common.MultipleCDockableFactory;
import bibliothek.gui.dock.common.SingleCDockable;
import bibliothek.gui.dock.common.SingleCDockableFactory;
import bibliothek.gui.dock.common.mode.ExtendedMode;
import bibliothek.gui.dock.frontend.FrontendEntry;

/**
 * Standard implementation of {@link CControlRegister}.
 * @author Benjamin Sigg
 */
public class DefaultCControlRegister implements MutableCControlRegister {
    /** the owner of this register */
    private CControl control;

    /** the center component of the main-frame */
    private CContentArea content;
    
    /** whether the default stations are currently added */
    private boolean settingDefaultStations = false;
    
    /** the whole list of station-containers known to this control, includes {@link #content} */
    private List<CStationContainer> containers = new ArrayList<CStationContainer>();

    /** A factory used to create missing {@link SingleCDockable}s */
    private CommonSingleDockableFactory backupFactory;

    /** the set of known factories */
    private Map<String, CommonMultipleDockableFactory> factories = 
        new HashMap<String, CommonMultipleDockableFactory>();

    /** list of all dockables registered  */
    private List<CDockable> dockables =
        new ArrayList<CDockable>();

    /** list of all {@link SingleCDockable}s */
    private List<SingleCDockable> singleDockables =
        new ArrayList<SingleCDockable>();

    /** the set of {@link MultipleCDockable}s */
    private List<MultipleCDockable> multiDockables = 
        new ArrayList<MultipleCDockable>();
    
    /** the stations known  */
    private List<CStation<?>> stations = new ArrayList<CStation<?>>();
    
    /** a listener added to all {@link CStationContainer}s, adds or removes {@link CStation}s
     * when necessary */
    private CStationContainerListener containerListener = new CStationContainerListener(){
		public void removed( CStationContainer source, CStation<?> station ){
			control.removeStation( station );
		}
		
		public void added( CStationContainer source, CStation<?> station ){
			try{
				settingDefaultStations = source == content;
				control.addStation( station, true );
			}
			finally{
				settingDefaultStations = false;
			}
		}
	};
    
    /**
     * Creates a new register
     * @param control the owner of this register
     */
    public DefaultCControlRegister( CControl control ){
        this.control = control;
        backupFactory = new CommonSingleDockableFactory( control );
    }
    
    public CControl getControl() {
        return control;
    }

    public List<CDockable> getDockables() {
        return Collections.unmodifiableList( dockables );
    }
    
    public List<MultipleCDockable> getMultipleDockables() {
        return Collections.unmodifiableList( multiDockables );
    }
    
    public List<SingleCDockable> getSingleDockables() {
        return Collections.unmodifiableList( singleDockables );
    }
    
    public List<CStation<?>> getStations() {
        return Collections.unmodifiableList( stations );
    }
    
    public List<CStationContainer> getStationContainers() {
        return Collections.unmodifiableList( containers );
    }
    
	public CStationContainer getContainer( CStation<?> child ){
		for( CStationContainer container : getStationContainers() ){
			for( int i = 0, n = container.getStationCount(); i<n; i++ ){
				if( container.getStation( i ) == child ){
					return container;
				}
			}
		}
		return null;
	}
    
    public void addStationContainer( CStationContainer container ){
        if( container == null )
            throw new NullPointerException( "container is null" );
        if( containers.contains( container ))
            throw new IllegalArgumentException( "container already registered" );
        
        for( CStationContainer child : containers ){
        	if( child.getUniqueId().equals( container.getUniqueId() )){
        		throw new IllegalArgumentException( "A container with unique id '" + container.getUniqueId() + "' is already registered" );
        	}
        }
        
        containers.add( container );
        
        try{
        	settingDefaultStations = container == content;
        	
	        for( int i = 0, n = container.getStationCount(); i<n; i++ ){
	        	control.addStation( container.getStation( i ), true );
	        }
        }
        finally{
        	settingDefaultStations = false;
        }
        container.addStationContainerListener( containerListener );
    }
    
    /**
     * Gets a list of keys for all {@link SingleCDockableFactory}s which
     * are currently registered at this control.
     * @return the list of keys
     */
    public Set<String> listSingleBackupFactories(){
        return backupFactory.getFactoryIds();
    }

    /**
     * Gets a list of identifiers of all {@link MultipleCDockableFactory}s
     * which are currently registered at this control.
     * @return the list of factories
     */
    public Set<String> listMultipleDockableFactories(){
        return factories.keySet();
    }
    
    /**
     * Gets a list of all {@link MultipleCDockable}s that are registered at this 
     * control and whose {@link MultipleCDockable#getFactory()} method returns
     * <code>factory</code>.
     * @param factory the factory to look out for
     * @return the list of dockables, never <code>null</code> but might be empty
     */
    public List<MultipleCDockable> listMultipleDockables( MultipleCDockableFactory<?, ?> factory ){
        List<MultipleCDockable> result = new ArrayList<MultipleCDockable>();
        for( MultipleCDockable dockable : multiDockables ){
            if( dockable.getFactory() == factory ){
                result.add( dockable );
            }
        }
        return result;
    }
    
    /**
     * Gets a list of all visible {@link CDockable}s in the given mode.
     * @param mode the mode which each <code>CDockable</code> must have
     * @return the list of <code>CDockable</code>s
     */
    public List<CDockable> listDockablesInMode( ExtendedMode mode ){
        List<CDockable> list = new ArrayList<CDockable>();
        for( CDockable check : dockables ){
            if( check.isVisible() && check.getExtendedMode() == mode ){
                list.add( check );
            }
        }
        return list;
    }
    
    /**
     * Gets a list of all identifiers of {@link SingleCDockable} for which
     * this control has location information within the current {@link CControl#load(String) setting}.
     * @return the list of ids, never <code>null</code>
     */
    public Set<String> listSingleDockables(){
        Set<String> result = new HashSet<String>();
        for( FrontendEntry entry : control.intern().listFrontendEntries() ){
            String id = entry.getKey();
            if( isSingleId( id )){
                result.add( singleToNormalId( id ));
            }
        }
        return result;
    }
    
    public String toSingleId( String id ){
        return "single " + id;
    }

    public boolean isSingleId( String id ){
        return id.startsWith( "single " );
    }
    
    public String singleToNormalId( String id ){
        return id.substring( 7 );
    }
    
    public String toMultiId( String id ){
        return "multi " + id;
    }
    
    public boolean isMultiId( String id ){
        return id.startsWith( "multi " );
    }
    
    public String multiToNormalId( String id ){
        return id.substring( 6 );
    }

    public void addMultipleDockable( MultipleCDockable dockable ) {
        dockables.add( dockable );
        multiDockables.add( dockable );
    }

    public void addSingleDockable( SingleCDockable dockable ) {
        dockables.add( dockable );
        singleDockables.add( dockable );
    }
    
    public SingleCDockable getSingleDockable( String id ){
	    for( SingleCDockable dockable : singleDockables ){
	    	if( dockable.getUniqueId().equals( id )){
	    		return dockable;
	    	}
	    }
	    return null;
    }

    public void addStation( CStation<?> station ) {
    	if( !settingDefaultStations ){
    		checkStationIdentifierUniqueness( station.getUniqueId(), station );
    	}
    	
        stations.add( station );
    }

    /**
     * Ensures the uniqueness of the identifier <code>uniqueId</code>. Throws
     * various exceptions if the id is not unique.
     * @param uniqueId the id that might be unique
     * @param adding the station that is about to be added
     */
    private void checkStationIdentifierUniqueness( String uniqueId, CStation<?> adding ){
        if( uniqueId == null )
            throw new NullPointerException( "uniqueId must not be null" );

        if( CContentArea.getCenterIdentifier( CControl.CONTENT_AREA_STATIONS_ID ).equals( uniqueId ) )
            throw new IllegalArgumentException( "The id " + uniqueId + " is reserved for special purposes" );
        if( CContentArea.getEastIdentifier( CControl.CONTENT_AREA_STATIONS_ID ).equals( uniqueId ) )
            throw new IllegalArgumentException( "The id " + uniqueId + " is reserved for special purposes" );
        if( CContentArea.getWestIdentifier( CControl.CONTENT_AREA_STATIONS_ID ).equals( uniqueId ) )
            throw new IllegalArgumentException( "The id " + uniqueId + " is reserved for special purposes" );
        if( CContentArea.getSouthIdentifier( CControl.CONTENT_AREA_STATIONS_ID ).equals( uniqueId ) )
            throw new IllegalArgumentException( "The id " + uniqueId + " is reserved for special purposes" );
        if( CContentArea.getNorthIdentifier( CControl.CONTENT_AREA_STATIONS_ID ).equals( uniqueId ) )
            throw new IllegalArgumentException( "The id " + uniqueId + " is reserved for special purposes" );

        for( CStation<?> station : stations ){
            if( station.getUniqueId().equals( uniqueId )){
            	if( station == adding ){
            		throw new IllegalArgumentException( "The station has already been registered" );
            	}
            	
                throw new IllegalArgumentException( "There exists already a station with id: " + uniqueId );    
            }
        }
    }

    public CommonSingleDockableFactory getBackupFactory() {
        return backupFactory;
    }

    public CommonMultipleDockableFactory getCommonMultipleDockableFactory( String id ) {
        return factories.get( id );
    }

    public CContentArea getDefaultContentArea(){
	    return content;
    }

    public void putCommonMultipleDockableFactory( String id, CommonMultipleDockableFactory factory ) {
        factories.put( id, factory );
    }

    public CommonMultipleDockableFactory removeCommonMultipleDockableFactory( String id ) {
        return factories.remove( id );
    }

    public boolean removeStationContainer( CStationContainer container ){
        if( containers.remove( container ) ){
        	container.removeStationContainerListener( containerListener );
        	
        	for( int i = 0, n = container.getStationCount(); i<n; i++ ){
        		control.removeStation( container.getStation( i ) );
        	}
        	return true;
        }
        return false;
    }

    public boolean removeMultipleDockable( MultipleCDockable dockable ) {
        if( dockables.remove( dockable ) ){
            multiDockables.remove( dockable );
            return true;
        }
        return false;
    }

    public boolean removeSingleDockable( SingleCDockable dockable ) {
        if( dockables.remove( dockable )){
            singleDockables.remove( dockable );
            return true;
        }
        return false;
    }

    public boolean removeStation( CStation<?> station ) {
        return stations.remove( station );
    }

    public void setDefaultContentArea( CContentArea container ){
	    this.content = container;	
    }

    public CDockable getDockable( int index ) {
        return dockables.get( index );
    }

    public int getDockableCount() {
        return dockables.size();
    }

    public Map<String, MultipleCDockableFactory<?, ?>> getFactories() {
        Map<String, MultipleCDockableFactory<?, ?>> result = new HashMap<String, MultipleCDockableFactory<?,?>>();
        for( Map.Entry<String, CommonMultipleDockableFactory> entry : factories.entrySet() ){
            result.put( entry.getKey(), entry.getValue().getFactory() );
        }
        return result;
    }
    
    public MultipleCDockableFactory<?, ?> getFactory( String id ) {
        CommonMultipleDockableFactory factory = factories.get( id );
        if( factory == null )
            return null;
        return factory.getFactory();
    }
}
