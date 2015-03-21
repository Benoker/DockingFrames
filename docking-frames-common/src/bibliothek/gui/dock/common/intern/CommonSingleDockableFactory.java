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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DockFactory;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.CStation;
import bibliothek.gui.dock.common.SingleCDockable;
import bibliothek.gui.dock.common.SingleCDockableFactory;
import bibliothek.gui.dock.common.perspective.CPerspective;
import bibliothek.gui.dock.common.perspective.CommonElementPerspective;
import bibliothek.gui.dock.common.perspective.SingleCDockablePerspective;
import bibliothek.gui.dock.layout.LocationEstimationMap;
import bibliothek.gui.dock.perspective.PerspectiveDockable;
import bibliothek.gui.dock.station.support.PlaceholderStrategy;
import bibliothek.util.Filter;
import bibliothek.util.Version;
import bibliothek.util.filter.PresetFilter;
import bibliothek.util.xml.XElement;

/**
 * This factory is used to create new {@link SingleCDockable}s using various
 * {@link SingleCDockableFactory}s. This factory is accessed when a single
 * dockable is missing in the cache of its owning {@link CControl}.
 * @author Benjamin Sigg
 */
public class CommonSingleDockableFactory implements DockFactory<CommonDockable, CommonElementPerspective, CommonSingleDockableLayout>{
    public static final String BACKUP_FACTORY_ID = "ccontrol backup factory id";
    
    /** all the factories that are used */
    private List<Entry> factories = new ArrayList<Entry>();
    
    /** factories using one id only */
    private Map<String, Entry> singleIdFactories = new HashMap<String, Entry>();
    
    private CControl control;
    
    private CPerspective perspective;
    
    /**
     * Creates a new factory.
     * @param control the owner of the factory, the factory will add {@link SingleCDockable}
     * to this control
     */
    public CommonSingleDockableFactory( CControl control ){
        this.control = control;
    }
    
    /**
     * Creates a new factory.
     * @param control the owner of the factory, the factory will add {@link SingleCDockable}
     * to this control
     * @param perspective the perspective which is used to load perspective related content
     */
    public CommonSingleDockableFactory( CControl control, CPerspective perspective ){
    	this.control = control;
    	this.perspective = perspective;
    }
    
    /**
     * Registers a new factory that will load {@link SingleCDockable}s with
     * the unique id <code>id</code>.
     * @param id the unique id of the dockables which <code>factory</code> will create
     * @param factory the new factory
     */
    public void add( String id, SingleCDockableFactory factory ){
    	Entry old = singleIdFactories.remove( id );
    	if( old != null ){
    		factories.remove( old );
    	}
    	
    	Entry entry = new Entry( new PresetFilter<String>( id ), factory );
    	factories.add( entry );
    	singleIdFactories.put( id, entry );
    }
    
    /**
     * Registers a new factory that will load {@link SingleCDockable} with
     * unique identifiers that are included by <code>ids</code>.
     * @param ids the identifiers that are included
     * @param factory the new factory
     */
    public void add( Filter<String> ids, SingleCDockableFactory factory ){
    	factories.add( new Entry( ids, factory ));
    }
    
    /**
     * Removes a factory from this.
     * @param id the name of the factory to remove
     */
    public void remove( String id ){
        Entry entry = singleIdFactories.remove( id );
        if( entry != null ){
        	factories.remove( entry );
        }
    }
    
    /**
     * Removes any occurrence of <code>factory</code>.
     * @param factory the factory to remove
     */
    public void remove( SingleCDockableFactory factory ){
    	Iterator<Entry> entries = factories.iterator();
    	while( entries.hasNext() ){
    		Entry next = entries.next();
    		if( next.factory == factory ){
    			entries.remove();
    		}
    	}
    	
    	entries = singleIdFactories.values().iterator();
    	while( entries.hasNext() ){
    		Entry next = entries.next();
    		if( next.factory == factory ){
    			entries.remove();
    		}
    	}
    }
    
    /**
     * Searches the factory which handles <code>id</code>.
     * @param id the name of a factory
     * @return the factory or <code>null</code>
     */
    public SingleCDockableFactory getFactory( String id ){
    	Entry entry = singleIdFactories.get( id );
    	if( entry != null ){
    		return entry.factory;
    	}
    	
    	for( Entry factory : factories ){
    		if( factory.filter.includes( id )){
    			return factory.factory;
    		}
    	}
    	
    	return null;
    }
    
    public String getID() {
        return BACKUP_FACTORY_ID;
    }
    
    /**
     * Gets a list of keys for all factories known to this.
     * @return the list of keys
     */
    public String[] listFactories(){
        Set<String> keys = singleIdFactories.keySet();
        return keys.toArray( new String[ keys.size() ] );
    }
    
    /**
     * Gets the set of keys for all factories known to this.
     * @return the set of keys
     */
    public Set<String> getFactoryIds(){
        return Collections.unmodifiableSet( singleIdFactories.keySet() );
    }
    
    public void estimateLocations( CommonSingleDockableLayout layout, LocationEstimationMap children ){
    	// currently not supported
    }

    public CommonSingleDockableLayout getLayout( CommonDockable element, Map<Dockable, Integer> children ) {
        CDockable dockable = element.getDockable();
        if( dockable instanceof SingleCDockable ){
            SingleCDockable single = (SingleCDockable)dockable;
            CommonSingleDockableLayout layout = new CommonSingleDockableLayout();
            layout.setId( single.getUniqueId() );
            layout.setArea( single.getWorkingArea() == null ? null : single.getWorkingArea().getUniqueId() );
            return layout;
        }
        else
            throw new IllegalArgumentException( "A CommonSingleDockableFactory works only with Dockables of type SingleCDockable, but this is not a single dockable: " + element );
    }

    public CommonDockable layout( CommonSingleDockableLayout layout, Map<Integer, Dockable> children, PlaceholderStrategy placeholders ) {
        return layout( layout, placeholders );
    }

    public CommonDockable layout( CommonSingleDockableLayout layout, PlaceholderStrategy placeholders ) {
        SingleCDockableFactory backup = getFactory( layout.getId() );
        if( backup == null )
            return null;
        
        SingleCDockable dockable = backup.createBackup( layout.getId() );
        if( dockable == null )
            return null;
        
        String factoryId = dockable.intern().getFactoryID();
        if( !factoryId.equals( getID() )){
        	throw new IllegalArgumentException( "Wrong type of dockable for unique id '" + layout.getId() + "': The backup factory created a dockable which expects a factory with type-id '" + factoryId + 
        			"',  but the call was done from a factory with type-id '" + getID() + "'" );
        }
        
        control.addDockable( dockable );
        if( layout.isAreaSet()){
        	if( layout.getArea() != null ){
        		CStation<?> station = control.getStation( layout.getArea() );
        		if( station == null ){
        			DelayedWorkingAreaSetter setter = new DelayedWorkingAreaSetter( layout.getArea(), dockable, control );
        			setter.install();
        		}
        		else {
        			dockable.setWorkingArea( station );
        		}
        	}
        	else{
        		dockable.setWorkingArea( null );
        	}
        }
        return dockable.intern();
    }
    
    public CommonElementPerspective layoutPerspective( CommonSingleDockableLayout layout, Map<Integer, PerspectiveDockable> children ){
    	SingleCDockablePerspective dockable = new SingleCDockablePerspective( layout.getId() );
    	if( layout.isAreaSet() && layout.getArea() != null ){
    		dockable.setWorkingArea( perspective.getStation( layout.getArea() ));
    	}
    	
    	return dockable.intern();
    }
    
    public void layoutPerspective( CommonElementPerspective perspective, CommonSingleDockableLayout layout, Map<Integer, PerspectiveDockable> children ){
    	// can't do anything
    }
    
    public CommonSingleDockableLayout getPerspectiveLayout( CommonElementPerspective element, Map<PerspectiveDockable, Integer> children ){
	    SingleCDockablePerspective dockable = (SingleCDockablePerspective)element.getElement();
	    CommonSingleDockableLayout layout = new CommonSingleDockableLayout();
	    layout.setId( dockable.getUniqueId() );
	    layout.setArea( dockable.getWorkingArea() == null ? null : dockable.getWorkingArea().getUniqueId() );
	    return layout;
    }

    public CommonSingleDockableLayout read( DataInputStream in, PlaceholderStrategy placeholders ) throws IOException {
        Version version = Version.read( in );
        CommonSingleDockableLayout layout = new CommonSingleDockableLayout();
        
        if( version.equals( Version.VERSION_1_0_4 )){
        	layout.setId( in.readUTF() );
        }
        else if( version.equals( Version.VERSION_1_1_0 )){
        	layout.setId( in.readUTF() );
        	layout.setArea( in.readUTF() );
        }
        else if( version.equals( Version.VERSION_1_1_0a )){
        	layout.setId( in.readUTF() );
        	if( in.readBoolean() ){
        		layout.setArea( in.readUTF() );
        	}
        	else{
        		layout.setArea( null );
        	}
        }
        else{
            throw new IOException( "Data from the future - unknown version: " + version );
        }
        
        return layout;
    }

    public CommonSingleDockableLayout read( XElement element, PlaceholderStrategy placeholders ) {
        CommonSingleDockableLayout layout = new CommonSingleDockableLayout();
    	
    	layout.setId( element.getElement( "id" ).getString() );
    	
    	XElement xarea = element.getElement( "area" );
    	if( xarea != null ){
    		String area = xarea.getString();
    		if( "".equals( area )){
    			layout.setArea( null );
    		}
    		else{
    			layout.setArea( area );
    		}
    	}
        
    	return layout;
    }

    public void setLayout( CommonDockable element, CommonSingleDockableLayout layout, Map<Integer, Dockable> children, PlaceholderStrategy placeholders ) {
        // can't do anything
    }

    public void setLayout( CommonDockable element, CommonSingleDockableLayout layout, PlaceholderStrategy placeholders ) {
        // can't do anything
    }

    public void write( CommonSingleDockableLayout layout, DataOutputStream out ) throws IOException {
        Version.write( out, Version.VERSION_1_1_0a );
        out.writeUTF( layout.getId() );
        
        String area = layout.getArea();
        if( area == null ){
        	out.writeBoolean( false );
        }
        else{
        	out.writeBoolean( true );
        	out.writeUTF( area );
        }
    }

    public void write( CommonSingleDockableLayout layout, XElement element ) {
        element.addElement( "id" ).setString( layout.getId() );
        XElement xarea = element.addElement( "area" );
        if( layout.getArea() != null ){
        	xarea.setString( layout.getArea() );
        }
    }
    
    /**
     * One backup factory of a {@link CommonSingleDockableFactory}
     */
    private static class Entry{
    	/** the filter to apply before {@link #factory} can be used */
    	public final Filter<String> filter;
    	/** the factory represented by this entry */
    	public final SingleCDockableFactory factory;
    	
    	/**
    	 * Creates a new entry.
    	 * @param filter the filter to use
    	 * @param factory the new factory
    	 */
    	public Entry( Filter<String> filter, SingleCDockableFactory factory ){
    		if( filter == null ){
    			throw new IllegalArgumentException( "filter must not be null" );
    		}
    		if( factory == null ){
    			throw new IllegalArgumentException( "factory must not be null" );
    		}
    		
    		this.filter = filter;
    		this.factory = factory;
    	}
    }
}
