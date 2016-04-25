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

package bibliothek.gui.dock.station;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.displayer.DisplayerRequest;
import bibliothek.gui.dock.themes.DefaultDisplayerFactoryValue;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.gui.dock.util.extension.Extension;

/**
 * A set of {@link DockableDisplayer}s. Clients may
 * {@link #fetch(Dockable, DockTitle) fetch} a new displayer at any time. They
 * should {@link #release(DockableDisplayer) release} a displayer which
 * is no longer used. The collection ensures that various properties of the
 * displayers are set in the proper order.
 * @author Benjamin Sigg
 */
public class DisplayerCollection implements Iterable<DockableDisplayer>{
    /** the station for which displayers are created */
    private DockStation station;
    
    /** the current controller, all displayer should know this controller */
    private DockController controller;
    
    /** a factory used to create new displayers */
    private DisplayerFactory factory;
    
    /** the set of displayers that are fetched but not released */
    private List<Handle> displayers = new ArrayList<Handle>();
    
    /** list of listeners added to each {@link DockableDisplayer} known to this collection */
    private List<DockableDisplayerListener> listeners = new ArrayList<DockableDisplayerListener>();
    
    private String displayerId;
    
    /**
     * Creates a new collection
     * @param station the station for which {@link DockableDisplayer} will be created
     * @param factory the factory that is initially used to create displayers
     * @param displayerId an identifier depending on <code>station</code>, this identifier is forwarded to
     * {@link Extension}s allowing them an easy solution to filter uninteresting requests
     */
    public DisplayerCollection( DockStation station, DisplayerFactory factory, String displayerId ){
        if( station == null )
            throw new IllegalArgumentException( "Station must not be null" );
        
        if( factory == null )
            throw new IllegalArgumentException( "Factory must not be null" );
        
        if( displayerId == null ){
        	throw new IllegalArgumentException( "displayerId must not be null" );
        }
        
        this.station = station;
        this.factory = factory;
        this.displayerId = displayerId;
    }
    
    /**
     * Creates a new collection
     * @param station the station for which {@link DockableDisplayer}s will be created
     * @param factory the factory that is used create displayers
     * @param displayerId an identifier depending on <code>station</code>, this identifier is forwarded to
     * {@link Extension}s allowing them an easy solution to filter uninteresting requests
     */
    public DisplayerCollection( DockStation station, final DefaultDisplayerFactoryValue factory, String displayerId ){
    	this( station, new DisplayerFactory(){
    		public void request( DisplayerRequest request ){
    			factory.request( request );
			}
		}, displayerId );
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
    
    /**
     * Tells whether <code>component</code> is the root component of any {@link DockableDisplayer}.
     * @param component the component to search
     * @return <code>true</code> if at least one {@link DockableDisplayer#getComponent()} returns <code>component</code>
     */
    public boolean isDisplayerComponent( Component component ){
    	for( DockableDisplayer displayer : this ){
    		if( displayer.getComponent() == component ){
    			return true;
    		}
    	}
    	return false;
    }
    
    public Iterator<DockableDisplayer> iterator() {
    	return new Iterator<DockableDisplayer>(){
    		private Iterator<Handle> handles = displayers.iterator();
    		
			public boolean hasNext(){
				return handles.hasNext();
			}

			public DockableDisplayer next(){
				return handles.next().getAnswer();
			}

			public void remove(){
				handles.remove();	
			}
    	};
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
    public DockableDisplayer fetch(  Dockable dockable, DockTitle title ){
    	Handle handle = new Handle( dockable );
    	handle.setController( controller );
    	handle.request( title );
    	DockableDisplayer displayer = handle.getAnswer();
    	
        displayer.setDockable( dockable );
        displayer.setTitle( title );
        displayer.setStation( station );
        displayers.add( handle );
        
        for( DockableDisplayerListener listener : listeners )
        	displayer.addDockableDisplayerListener( listener );
        
        return displayer;
    }
    
    /**
     * Releases a displayer that was created by this collection.
     * @param displayer the displayer to release
     */
    public void release( DockableDisplayer displayer ){
    	for( DockableDisplayerListener listener : listeners ){
    		displayer.removeDockableDisplayerListener( listener );
    	}
    	
    	Iterator<DockableDisplayer> iter = iterator();
    	while( iter.hasNext() ){
    		if( iter.next() == displayer ){
    			iter.remove();
    		}
    	}

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
            for( Handle handle : displayers ){
            	handle.setController( controller );
            }
        }
    }
    
    /**
     * A {@link Handle} handles the {@link DockableDisplayer} of one {@link Dockable}
     * @author Benjamin Sigg
     */
    private class Handle extends DisplayerRequest {
		public Handle( Dockable target ){
			super( station, target, new DisplayerFactory(){
				public void request( DisplayerRequest request ){
					DisplayerCollection.this.factory.request( request );
				}
			}, displayerId );
		}
		
		@Override
		public void setController( DockController controller ){
			super.setController( controller );
			DockableDisplayer displayer = getAnswer();
			if( displayer != null ){
				displayer.setController( null );
			}
		}

		@Override
		protected void answer( DockableDisplayer previousResource, DockableDisplayer newResource ){
			if( previousResource != null ){
				previousResource.setController( null );
			}
			if( newResource != null ){
				newResource.setController( getController() );
			}
		}
    }
}
