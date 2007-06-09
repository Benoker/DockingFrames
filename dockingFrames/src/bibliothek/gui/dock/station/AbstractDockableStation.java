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

import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.IOException;

import javax.swing.SwingUtilities;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.DockTheme;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.AbstractDockable;
import bibliothek.gui.dock.action.DockActionSource;
import bibliothek.gui.dock.event.DockStationListener;
import bibliothek.gui.dock.event.DockTitleEvent;
import bibliothek.gui.dock.station.support.DockStationListenerManager;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.gui.dock.title.DockTitleVersion;
import bibliothek.gui.dock.util.PropertyKey;

/**
 * An abstract combination between {@link DockStation} and {@link Dockable}. This
 * station has no functionality except the ability to store and call
 * {@link DockStationListener DockStationListeners}.
 * @author Benjamin Sigg
 *
 */
public abstract class AbstractDockableStation extends AbstractDockable implements DockStation {
	/**
	 * The list of {@link DockStationListener DockStationListeners} which
	 * can be used to send events to all listeners.
	 */
	protected DockStationListenerManager listeners = new DockStationListenerManager( this );
	
	/** The theme of this station */
	private DockTheme theme;
    
	/**
	 * Constructs a new station, but does nothing more
	 */
	public AbstractDockableStation(){
		super( PropertyKey.DOCK_STATION_ICON, PropertyKey.DOCK_STATION_TITLE );
	}
	
	/**
	 * Constructs a new station and sets the theme.
	 * @param theme the theme, may be <code>null</code>
	 */
	public AbstractDockableStation( DockTheme theme ){
		this();
		this.theme = theme;
	}
	
    public DockStation asDockStation() {
        return this;
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
    
    /**
     * Overridden, returns now a {@link DockTitleVersion} created by
     * {@link DockTitleVersion#createStation}.
     * @return the {@link DockTitleVersion} for this dockable
     */
    @Override
    public DockTitle getDockTitle( DockTitleVersion version ) {
        return version.createStation( this );
    }
    
    public DockActionSource getDirectActionOffers( Dockable dockable ) {
        return null;
    }
    
    public DockActionSource getIndirectActionOffers( Dockable dockable ) {
        return null;
    }

    public void addDockStationListener( DockStationListener listener ) {
        listeners.addListener( listener );
    }

    public void removeDockStationListener( DockStationListener listener ) {
        listeners.removeListener( listener );
    }

    public boolean isVisible( Dockable dockable ) {
        return isStationVisible();
    }

    public boolean isStationVisible() {
        Dockable dockable = asDockable();
        if( dockable == null )
            return true;
        
        DockStation parent = dockable.getDockParent();
        
        if( parent == null )
            return true;
        else
            return parent.isVisible( dockable );
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
        title.changed( new DockTitleEvent( this, dockable, active ));
    }
    
    public Rectangle getStationBounds() {
        Component component = getComponent();
        Point location = new Point( 0, 0 );
        SwingUtilities.convertPointToScreen( location, component );
        return new Rectangle( location.x, location.y, component.getWidth(), component.getHeight() );
    }
}
