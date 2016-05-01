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
import java.io.IOException;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.DockTheme;
import bibliothek.gui.DockUI;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DockFactory;
import bibliothek.gui.dock.component.DockComponentConfiguration;
import bibliothek.gui.dock.component.DockComponentRootHandler;
import bibliothek.gui.dock.displayer.DisplayerRequest;
import bibliothek.gui.dock.event.DockStationListener;
import bibliothek.gui.dock.station.support.DockStationListenerManager;
import bibliothek.gui.dock.title.ActivityDockTitleEvent;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.gui.dock.title.DockTitleRequest;

/**
 * An abstract implementation of {@link DockStation}. This station
 * has the ability to send events to registered 
 * {@link DockStationListener DockStationListeners}.
 * @author Benjamin Sigg
 *
 */
public abstract class AbstractDockStation implements DockStation {
	/** The owner of this station */
    private DockController controller;
    
	/**
	 * The list of {@link DockStationListener DockStationListeners} which
	 * can be used to send events to all listeners.
	 */
	protected DockStationListenerManager listeners = new DockStationListenerManager( this );
	
	/** The theme of this station */
	private DockTheme theme;
	
	/** keeps track of all the components of this station */
	private DockComponentRootHandler rootHandler;
	
	/**
	 * Gets the {@link DockComponentRootHandler} which is responsible for keeping track of all the
	 * {@link Component}s of this station.
	 * @return the root handler, not <code>null</code>
	 */
	protected DockComponentRootHandler getRootHandler(){
		if( rootHandler == null ){
			rootHandler = createRootHandler();
		}
		return rootHandler;
	}
	
	/**
	 * Creates a new {@link DockComponentRootHandler} which will be responsible for keeping track of
	 * all the {@link Component}s of this station.
	 * @return the new handler
	 */
	protected abstract DockComponentRootHandler createRootHandler();
	
    public void setController( DockController controller ) {
        this.controller = controller;
        getRootHandler().setController( controller );
    }

    public DockController getController() {
        return controller;
    }
    
    public DockComponentConfiguration getComponentConfiguration() {
    	return getRootHandler().getConfiguration();
    }
    
    public void setComponentConfiguration( DockComponentConfiguration configuration ) {
	    getRootHandler().setConfiguration( configuration );
    }
    
    public DockTheme getTheme() {
    	return theme;
    }
    
    public void updateTheme() {
    	DockController controller = getController();
    	if( controller != null ){
    		DockTheme newTheme = controller.getTheme();
    		if( newTheme != theme ){
    			theme = newTheme;
    			try{
    				callDockUiUpdateTheme();
    			}
    			catch( IOException ex ){
    				throw new RuntimeException( ex );
    			}
    		}
    	}
    }
    
    /**
     * Calls the method {@link DockUI}.{@link DockUI#updateTheme(DockStation, DockFactory)}
     * with <code>this</code> as the first argument, and an appropriate factory
     * as the second argument.
     * @throws IOException if the DockUI throws an exception
     */
    protected abstract void callDockUiUpdateTheme() throws IOException;

    public void addDockStationListener( DockStationListener listener ) {
        listeners.addListener( listener );
    }

    public void removeDockStationListener( DockStationListener listener ) {
        listeners.removeListener( listener );
    }
    
    public boolean isVisible( Dockable dockable ) {
        return true;
    }

    public boolean accept( Dockable child ) {
        return true;
    }

    public boolean canCompare( DockStation station ) {
        return false;
    }

    public int compare( DockStation station ) {
        return 0;
    }
    
    public void changed( Dockable dockable, DockTitle title, boolean active ) {
        title.changed( new ActivityDockTitleEvent( this, dockable, active ));
    }
    
    public void requestChildDockTitle( DockTitleRequest request ){
	    // ignore	
    }
    
    public void requestChildDisplayer( DisplayerRequest request ){
    	// ignore
    }

    /**
     * Creates a set of algorithms which may depend on this {@link DockStation}, and which perform
     * various tasks that are handled the same way in many implementations of {@link DockStation}.
     * @return the generic set of algorithms
     */
    protected DockStationDelegate delegate(){
    	return new DockStationDelegate();
    }
}
