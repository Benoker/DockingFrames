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

package bibliothek.gui.dock.station.split;

import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.event.MouseInputAdapter;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.event.DockAdapter;
import bibliothek.gui.dock.station.SplitDockStation;
import bibliothek.gui.dock.title.DockTitle;


/**
 * A listener which is added to a {@link SplitDockStation}. The listener
 * observes all children and adds to each {@link DockTitle} or {@link Dockable}
 * a {@link FullScreenListener}. The listener is removed when a {@link Dockable}
 * is removed from this station.
 * @author Benjamin Sigg
 *
 */ 
public class FullScreenClickableListener extends DockAdapter{
    /** The station which is observed by this listener */
    private SplitDockStation station;
    
    /** A map that tells which listener was added to which {@link Dockable} */
    private Map<Dockable, FullScreenListener> dockableListeners = 
        new HashMap<Dockable, FullScreenListener>();
    
    /** A map that tells which listener was added to which {@link DockTitle} */
    private Map<DockTitle, FullScreenListener> titleListeners =
        new HashMap<DockTitle, FullScreenListener>();
    
    /** A set that tells to which station this listener is added */
    private Set<DockStation> knownStations = new HashSet<DockStation>();
    
    /**
     * Constructs a new listener.
     * @param station the station which should be observed
     */
    public FullScreenClickableListener( SplitDockStation station ){
        this.station = station;
        collect( station.asDockable() );
    }
    
    /**
     * Gets the station which is observed by this listener.
     * @return the observed station
     */
    public SplitDockStation getStation() {
        return station;
    }
    
    /**
     * Adds to <code>dockable</code> (if {@link #shouldCollect(Dockable)} returns <code>true</code>)
     * and to all its children a listener.
     * @param dockable the {@link Dockable} which will be observed
     */
    private void collect( Dockable dockable ){
        if( shouldCollect( dockable ))
            add( dockable );
        
        DockStation station = dockable.asDockStation();
        if( station != null )
            collect( station );
    }
    
    /**
     * Adds to all children of <code>station</code> a listener.
     * @param station the station which will be observed
     */
    private void collect( DockStation station ){
        if( knownStations.add( station ))
            station.addDockStationListener( this );
        
        for( int i = 0, n = station.getDockableCount(); i<n; i++ )
            collect( station.getDockable( i ));
    }
    
    /**
     * Removes any listener that was added to <code>dockable</code>.
     * @param dockable the {@link Dockable} which will no longer be
     * observed
     */
    private void uncollect( Dockable dockable ){
        remove( dockable );
        
        DockStation station = dockable.asDockStation();
        if( station != null ){
            uncollect( station );
        }
    }
    
    /**
     * Removes all listener from <code>station</code> and its
     * children.
     * @param station the station which will no longer be
     * observed
     */
    private void uncollect( DockStation station ){
        station.removeDockStationListener( this );
        knownStations.remove( station );
        for( int i = 0, n = station.getDockableCount(); i<n; i++ )
            uncollect( station.getDockable( i ));
    }
    
    /**
     * Adds a listener to <code>dockable</code> which might change the
     * fullscreen-mode of <code>dockable</code> or one of its parents.
     * @param dockable the {@link Dockable} which will be observed
     */
    private void add( Dockable dockable ){
        if( dockableListeners.containsKey( dockable ))
            return;
        
        Dockable screened = unwrap( dockable );
        if( screened != null ){
            FullScreenListener listener = new FullScreenListener( screened );
            dockable.addMouseInputListener( listener );
            dockableListeners.put( dockable, listener );
        }
    }
    
    /**
     * Removes the listener which was added earlier to <code>dockable</code>.
     * @param dockable the {@link Dockable} which will no longer be observed
     */
    private void remove( Dockable dockable ){
        FullScreenListener listener = dockableListeners.remove( dockable );
        if( listener != null )
            dockable.removeMouseInputListener( listener );
    }
    
    @Override
    public void dockableAdded( DockStation station, Dockable dockable ) {
        collect( dockable );
    }
    
    @Override
    public void dockableRemoved( DockStation station, Dockable dockable ) {
        uncollect( dockable );
    }
    
    @Override
    public void titleBound( DockController controller, DockTitle title, Dockable dockable ) {
        if( shouldCollect( dockable, title )){
            Dockable screened = unwrap( dockable );
            if( screened != null ){
                FullScreenListener listener = new FullScreenListener( screened );
                title.addMouseInputListener( listener );
                titleListeners.put( title, listener );
            }
        }
    }
    
    @Override
    public void titleUnbound( DockController controller, DockTitle title, Dockable dockable ) {
        FullScreenListener listener = titleListeners.remove( title );
        if( listener != null )
            title.removeMouseInputListener( listener );
    }
    
    /**
     * Searches a parent of <code>dockable</code> which has the 
     * {@link #getStation() station} as its direct parent.
     * @param dockable the root of the search
     * @return <code>dockable</code>, a parent of <code>dockable</code>
     * or <code>null</code>
     */
    protected Dockable unwrap( Dockable dockable ){
        while( dockable.getDockParent() != station ){
            DockStation parent = dockable.getDockParent();
            if( parent == null )
                return null;
            
            dockable = parent.asDockable();
            if( dockable == null )
                return null;
        }
        return dockable;
    }
    
    /**
     * Determines whether <code>dockable</code> should be observed or not.
     * @param dockable the <code>dockable</code> which will be checked
     * @return <code>true</code> if <code>dockable</code> should have a listener,
     * <code>false</code> otherwise
     */
    protected boolean shouldCollect( Dockable dockable ){
        DockStation parent = dockable.getDockParent();
        if( parent == null )
            return false;
        
        if( parent instanceof SplitDockStation )
            return parent == station;
        
        dockable = parent.asDockable();
        if( dockable == null )
            return false;
        
        return dockable.getDockParent() == station;
    }
    
    /**
     * Determines whether <code>title</code> should be observed or not.
     * @param dockable the owner of <code>title</code>
     * @param title the title which will be checked
     * @return <code>true</code> if <code>title</code> has to be observed,
     * <code>false</code> otherwise
     */
    protected boolean shouldCollect( Dockable dockable, DockTitle title ){
        return shouldCollect( dockable );
    }
    
    /**
     * Deconstructor of this listener, removes all listeners from any known
     * observed object.
     */
    public void destroy(){
        for( Map.Entry<Dockable, FullScreenListener> listener : dockableListeners.entrySet() )
            listener.getKey().removeMouseInputListener( listener.getValue() );
        
        for( Map.Entry<DockTitle, FullScreenListener> listener : titleListeners.entrySet() )
            listener.getKey().removeMouseInputListener( listener.getValue() );
        
        for( DockStation station : knownStations )
            station.removeDockStationListener( this );
        
        dockableListeners.clear();
        titleListeners.clear();
        knownStations.clear();
    }
    
    /**
     * A listener which waits for a double-click-event to change the
     * fullscreen-mode of a {@link Dockable}.
     * @author Benjamin Sigg
     */
    protected class FullScreenListener extends MouseInputAdapter{
        /** The Dockable whose fullscreen-mode may be changed. */
        private Dockable dockable;
        
        /**
         * Constructs a new listener.
         * @param dockable the Dockable whose fullscreen-mode may be
         * changed by this listener
         */
        public FullScreenListener( Dockable dockable ){
            this.dockable = dockable;
        }
        
        @Override
        public void mousePressed( MouseEvent e ) {
            if( e.getClickCount() == 2 ){
                if( station.isFullScreen() ){
                    if( station.getFullScreen() == dockable )
                        station.setFullScreen( null );
                }
                else{
                    station.setFullScreen( dockable );
                }
            }
        }
    }
}