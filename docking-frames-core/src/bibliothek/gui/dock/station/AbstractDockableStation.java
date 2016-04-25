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

import java.io.IOException;

import javax.swing.Icon;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.DockTheme;
import bibliothek.gui.DockUI;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DockFactory;
import bibliothek.gui.dock.accept.DockAcceptance;
import bibliothek.gui.dock.action.DockActionSource;
import bibliothek.gui.dock.displayer.DisplayerRequest;
import bibliothek.gui.dock.dockable.AbstractDockable;
import bibliothek.gui.dock.event.DockStationListener;
import bibliothek.gui.dock.station.layer.DefaultDropLayer;
import bibliothek.gui.dock.station.layer.DockStationDropLayer;
import bibliothek.gui.dock.station.support.DockStationListenerManager;
import bibliothek.gui.dock.title.ActivityDockTitleEvent;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.gui.dock.title.DockTitleRequest;
import bibliothek.gui.dock.util.DockUtilities;
import bibliothek.gui.dock.util.PropertyKey;
import bibliothek.gui.dock.util.icon.DockIcon;
import bibliothek.util.Todo;
import bibliothek.util.Todo.Compatibility;
import bibliothek.util.Todo.Priority;
import bibliothek.util.Todo.Version;

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
		super( PropertyKey.DOCK_STATION_TITLE, PropertyKey.DOCK_STATION_TOOLTIP );
	}
	
	@Override
	protected DockIcon createTitleIcon(){
		return new DockStationIcon( "dockStation.default", this ){
			protected void changed( Icon oldValue, Icon newValue ){
				fireTitleIconChanged( oldValue, newValue );
			}
		};
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

    @Deprecated
    @Todo( compatibility=Compatibility.BREAK_MAJOR, priority=Priority.ENHANCEMENT, target=Version.VERSION_1_1_3, description="remove this method" )
    public boolean isVisible( Dockable dockable ) {
        return isStationShowing();
    }
    
    public boolean isChildShowing( Dockable dockable ){
    	return isVisible( dockable );
    }

    public boolean isStationShowing(){
	    return isStationVisible();
    }
    
    @Deprecated
    @Todo( compatibility=Compatibility.BREAK_MAJOR, priority=Priority.ENHANCEMENT, target=Version.VERSION_1_1_3, description="remove this method" )    
    public boolean isStationVisible() {
    	boolean visible = isDockableShowing();
    	if( visible ){
    		return true;
    	}
    	if( getController() != null ){
    		return getComponent().isShowing();
    	}
    	return false;
    }

    public boolean accept( Dockable child ) {
        return true;
    }
    
    /**
     * Tells whether this station accepts <code>child</code> as new child. This is a shortcut
     * asking {@link #accept(Dockable)}, {@link Dockable#accept(DockStation)} and
     * {@link DockAcceptance#accept(DockStation, Dockable)} in one go.
     * @param child the child to add
     * @return <code>true</code> if acceptable
     */
    public boolean acceptable( Dockable child ){
    	return DockUtilities.acceptable( this, child );
    }
    
    /**
     * Tells whether this station accepts <code>child</code> as combination with <code>old</code>.
     * @param old some child of this station
     * @param next a new child
     * @return <code>true</code> if <code>old</code> and <code>next</code> can be combined
     */
    public boolean acceptable( Dockable old, Dockable next ){
    	return DockUtilities.acceptable( this, old, next );
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

    public DockStationDropLayer[] getLayers(){
    	return new DockStationDropLayer[]{
    			new DefaultDropLayer( this )
    	};
    }
    
    /**
     * Invokes {@link DockStationListenerManager#fireDockablesRepositioned(Dockable...)} for
     * all children starting at index <code>fromIndex</code>.
     * @param fromIndex the index of the first moved child
     */
    protected void fireDockablesRepositioned( int fromIndex ){
    	fireDockablesRepositioned( fromIndex, getDockableCount()-1 );
    }
    
    /**
     * Invokes {@link DockStationListenerManager#fireDockablesRepositioned(Dockable...)} for
     * all children starting at index <code>fromIndex</code> to index <code>toIndex</code>.
     * @param fromIndex the index of the first moved child
     * @param toIndex the index of the last moved child
     */
    protected void fireDockablesRepositioned( int fromIndex, int toIndex ){
        int count = toIndex - fromIndex + 1;
        if( count > 0 ){
        	Dockable[] moved = new Dockable[count];
        	for( int i = 0; i < count; i++ ){
        		moved[i] = getDockable( i+fromIndex );
        	}
        	listeners.fireDockablesRepositioned( moved );
        }
    }
    
    /**
     * Creates a generic set of algorithms.
     * @return the algorithms, not <code>null</code>
     */
    protected DockStationDelegate delegate(){
    	return new DockStationDelegate();
    }
}
