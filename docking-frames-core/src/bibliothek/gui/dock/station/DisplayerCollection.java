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

package bibliothek.gui.dock.station;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.themes.DefaultDisplayerFactoryValue;
import bibliothek.gui.dock.title.DockTitle;

/**
 * A set of {@link DockableDisplayer}s. Clients may
 * {@link #fetch(Dockable, DockTitle) fetch} a new displayer at any time. They
 * should {@link #release(DockableDisplayer) release} a displayer which
 * is no longer used. The collection ensures that various properties of the
 * displayers are set in the proper order.
 * @author Benjamin Sigg
 *
 */
public class DisplayerCollection implements Iterable<DockableDisplayer>{
    /** the station for which displayers are created */
    private DockStation station;
    
    /** the current controller, all displayer should know this controller */
    private DockController controller;
    
    /** a factory used to create new displayers */
    private DisplayerFactory factory;
    
    /** the set of displayers that are fetched but not released */
    private List<DockableDisplayer> displayers = new ArrayList<DockableDisplayer>();
    
    /** list of listeners added to each {@link DockableDisplayer} known to this collection */
    private List<DockableDisplayerListener> listeners = new ArrayList<DockableDisplayerListener>();
    
    /**
     * Creates a new collection
     * @param station the station for which {@link DockableDisplayer} will be created
     * @param factory the factory that is initially used to create displayers
     */
    public DisplayerCollection( DockStation station, DisplayerFactory factory ){
        if( station == null )
            throw new IllegalArgumentException( "Station must not be null" );
        
        if( factory == null )
            throw new IllegalArgumentException( "Factory must not be null" );
        
        this.station = station;
        this.factory = factory;
    }
    
    /**
     * Creates a new collection
     * @param station the station for which {@link DockableDisplayer}s will be created
     * @param factory the factory that is used create displayers
     */
    public DisplayerCollection( DockStation station, final DefaultDisplayerFactoryValue factory ){
    	this( station, new DisplayerFactory(){
			public DockableDisplayer create( DockStation station, Dockable dockable, DockTitle title ){
				return factory.create( dockable, title );
			}
		});
    }
    
    /**
     * Adds <code>listener</code> to all {@link DockableDisplayer}s that are
     * in this collection.
     * @param listener a new listener, not <code>null</code>
     */
    public void addDockableDisplayerListener( DockableDisplayerListener listener ){
    	listeners.add( listener );
    	
    	for( DockableDisplayer displayer : this ){
    		displayer.addDockableDisplayerListener( listener );
    	}
    }
    
    /**
     * Removes <code>listener</code> from all {@link DockableDisplayer}s
     * that are in this collection.
     * @param listener the listener to remove
     */
    public void removeDockableDisplayerListener( DockableDisplayerListener listener ){
    	listeners.remove( listener );
    	
    	for( DockableDisplayer displayer : this ){
    		displayer.removeDockableDisplayerListener( listener );
    	}
    }
    
    public Iterator<DockableDisplayer> iterator() {
        return displayers.iterator();
    }
    
    /**
     * Creates a new {@link DockableDisplayer} using the {@link #setFactory(DisplayerFactory) factory}
     * of this collection. This method also sets the {@link DockableDisplayer#setTitle(DockTitle) title},
     * {@link DockableDisplayer#setStation(DockStation) station}
     * and the {@link DockableDisplayer#setController(DockController) controller} property of
     * the displayer.<br>
     * If the displayer is no longer needed, then it should be {@link #release(DockableDisplayer) released}
     * @param dockable the Dockable which will be shown on the displayer.
     * @param title the title which will be shown on the displayer, might be <code>null</code>
     * @return the new displayer
     */
    public DockableDisplayer fetch( Dockable dockable, DockTitle title ){
        DockableDisplayer displayer = factory.create( station, dockable, title );
        displayer.setDockable( dockable );
        displayer.setTitle( title );
        displayer.setStation( station );
        displayer.setController( controller );
        displayers.add( displayer );
        
        for( DockableDisplayerListener listener : listeners )
        	displayer.addDockableDisplayerListener( listener );
        
        return displayer;
    }
    
    /**
     * Releases a displayer that was created by this collection.
     * @param displayer the displayer to release
     */
    public void release( DockableDisplayer displayer ){
    	for( DockableDisplayerListener listener : listeners )
    		displayer.removeDockableDisplayerListener( listener );
    	
        displayers.remove( displayer );
        displayer.setTitle( null );
        displayer.setDockable( null );
        displayer.setStation( null );
        displayer.setController( null );
    }
    
    /**
     * Sets the factory that will create new {@link DockableDisplayer} when
     * needed.
     * @param factory the new factory, not <code>null</code>
     */
    public void setFactory( DisplayerFactory factory ){
        if( factory == null )
            throw new IllegalArgumentException( "Factory must not be null" );
        
        this.factory = factory;
    }
    
    /**
     * Sets the current {@link DockController}, that controller will be made
     * known to all {@link DockableDisplayer} created by this collection.
     * @param controller the new controller, can be <code>null</code>
     */
    public void setController( DockController controller ){
        if( this.controller != controller ){
            this.controller = controller;
            for( DockableDisplayer displayer : displayers )
                displayer.setController( controller );
        }
    }
}
