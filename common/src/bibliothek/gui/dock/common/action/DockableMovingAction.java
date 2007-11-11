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
package bibliothek.gui.dock.common.action;

import java.awt.Component;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.SwingUtilities;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DockableProperty;
import bibliothek.gui.dock.action.ActionGuard;
import bibliothek.gui.dock.action.DefaultDockActionSource;
import bibliothek.gui.dock.action.DockActionSource;
import bibliothek.gui.dock.action.LocationHint;
import bibliothek.gui.dock.action.actions.SimpleButtonAction;
import bibliothek.gui.dock.event.DockControllerAdapter;
import bibliothek.gui.dock.event.IconManagerListener;
import bibliothek.gui.dock.station.FlapDockStation;
import bibliothek.gui.dock.util.DockUtilities;
import bibliothek.util.container.Tuple;


/**
 * The DockableMovingAction adds two new {@link DockAction actions} to the DockingFrames.
 * These actions can be used to move a {@link Dockable} from 
 * "base to special" or to move from "special to base".
 * "Moving to special" a <code>Dockable</code> means to store the location of the
 * <code>Dockable</code> and then move the <code>Dockable</code> into a
 * "special" {@link DockStation}.<br>
 * 
 * @TODO work in progress
 

 * Normalizing a <code>Dockable</code> means to move a minimized <code>Dockable</code>
 * from a <code>FlapDockStation</code> back to its original position.<br>
 * Clients can define which {@link DockStation}s are used for the "normal"
 * and the "minimized" <code>Dockable</code>s by registering them through
 * {@link #addAreaMinimized(FlapDockStation, DockableProperty) addAreaMinimized}
 * and {@link #addAreaNormalized(DockStation) addAreaNormalized}. 
 * @author Benjamin Sigg
 *
 */
public class DockableMovingAction {
    /** the areas which contain the normalized {@link Dockable}s */
    private List<DockStation> areaNormalized = new ArrayList<DockStation>();
    /** the areas which contain the minimized {@link Dockable}s */
    private List<DockStation> areaMinimized = new ArrayList<DockStation>();
    
    /** the station used to normalize a {@link Dockable} when its original position is unknown */
    private DockStation defaultStation;
    /** the preferred location where to add a minimized {@link Dockable} to a station from {@link #areaMinimized} */
    private Map<FlapDockStation, DockableProperty> defaultDrops = new HashMap<FlapDockStation, DockableProperty>();
    
    /** the preferred locations where a {@link Dockable} should be normalized again */
    private Map<Dockable, Tuple<DockStation, DockableProperty>> locations =
        new HashMap<Dockable, Tuple<DockStation,DockableProperty>>();
    
    /** the controller for which this {@link Minimizer} works */
    private DockController controller;
    
    /**
     * Creates a new <code>Minimizer</code>, adds all listeners and actions
     * to <code>controller</code>.
     * @param controller the controller to which the actions and listeners are
     * added.
     */
    public Minimizer( DockController controller ){    	
        this.controller = controller;
        
        controller.addActionGuard( new Minimize() );
        controller.addActionGuard( new Normalize() );
        
        controller.getRegister().addDockRegisterListener( new Listener() );
    }
    
    /**
     * Sets the station to which {@link Dockable}s are "normalized" when
     * their old location is not known or invalid.
     * @param defaultStation the backup
     */
    public void setDefaultStation( DockStation defaultStation ) {
        this.defaultStation = defaultStation;
    }
    
    /**
     * Stores a new station whose children will have the "minimize"-action.
     * @param station a station whose children can be minimized
     */
    public void addAreaNormalized( DockStation station ){
        areaNormalized.add( station );
    }
    
    /**
     * Stores a new station whose children are minimized {@link Dockable}s.
     * @param station the new station
     * @param defaultDrop the location where normally children will be inserted
     */
    public void addAreaMinimized( FlapDockStation station, DockableProperty defaultDrop ){
        areaMinimized.add( station );
        defaultDrops.put( station, defaultDrop );
    }
    
    /**
     * Ensures that <code>dockable</code> is no longer minimized.
     * @param dockable the <code>Dockable</code> that will be shown on
     * one of the {@link #addAreaNormalized(DockStation) normalized stations}.
     */
    public void normalize( Dockable dockable ){
        Tuple<DockStation, DockableProperty> location = locations.remove( dockable );
        if( location == null ){
            // add it somewhere...
            DockStation parent = stationOf( dockable );
            dockable.getDockParent().drag( dockable );
            if( parent != null )
                defaultStation.drop( dockable, defaultDrops.get( parent ) );
            else
                defaultStation.drop( dockable );
        }
        else{
            DockStation parent = stationOf( dockable );
            dockable.getDockParent().drag( dockable );
            boolean done = location.getA().drop( dockable, location.getB() );
            if( !done ){
                if( parent != null )
                    defaultStation.drop( dockable, defaultDrops.get( parent ) );
                else
                    defaultStation.drop( dockable );
            }
        }
    }
    
    /**
     * Searches the first parent of <code>dockable</code> that was 
     * registered through {@link #addAreaMinimized(FlapDockStation, DockableProperty)}.
     * @param dockable the element whose parent is searched
     * @return one of the stations for minimized {@link Dockable}s or <code>null</code>
     */
    private DockStation stationOf( Dockable dockable ){
        DockStation parent = dockable.getDockParent();
        while( parent != null ){
            if( areaMinimized.contains( parent ))
                return parent;
            
            Dockable parentDockable = parent.asDockable();
            if( parentDockable == null )
                return null;
            
            parent = parentDockable.getDockParent();
        }
        
        return null;
    }
    
    /**
     * Ensures that <code>dockable</code> is no longer "normalized". The
     * old location of <code>dockable</code> is stored, so it can be
     * {@link #normalize(Dockable) normalized} again.
     * @param dockable the element to minimize
     */
    public void minimize( Dockable dockable ){        
        Component component = dockable.getComponent();
        Point center = new Point( component.getWidth()/2, component.getHeight()/2);
        SwingUtilities.convertPointToScreen( center, component );
        
        FlapDockStation bestStation = null;
        double bestDistance = Double.MAX_VALUE;
        
        for( DockStation station : areaMinimized ){
            Component stationComponent = station.getComponent();
            Point stationCenter = new Point( stationComponent.getWidth()/2, stationComponent.getHeight()/2 );
            SwingUtilities.convertPointToScreen( stationCenter, stationComponent );
            
            double dist = Math.pow( center.x - stationCenter.x, 2 ) + Math.pow( center.y - stationCenter.y, 2 );
            if( dist < bestDistance ){
                bestDistance = dist;
                bestStation = station;
            }
        }
        
        if( bestStation != null ){
            DockStation root = DockUtilities.getRoot( dockable );
            DockableProperty location = DockUtilities.getPropertyChain( root, dockable );
            
            dockable.getDockParent().drag( dockable );
            bestStation.add( dockable );
            
            locations.put( dockable, new Tuple<DockStation, DockableProperty>( root, location ) );
        }
    }
    
    /**
     * A listener added to the {@link DockRegister}, this listener is responsible
     * to remove data about {@link Dockable}s that are no longer registered.
     * @author Benjamin Sigg
     *
     */
    private class Listener extends DockControllerAdapter{
        @Override
        public void dockableUnregistered( DockController controller, Dockable dockable ) {
        	if( !core.isOnThemeUpdate() )
        		locations.remove( dockable );
        }
    }
    
    /**
     * An action and action-guard that allows the user to minimize a {@link Dockable}.
     * The action is only added to children of the "normalized stations".
     * @author Benjamin Sigg
     *
     */
    private class Minimize extends SimpleButtonAction implements ActionGuard {
        /** the result of {@link #getSource(Dockable)} */
        private DefaultDockActionSource source;
       
        /**
         * Creates a new action and action-guard
         */
        public Minimize(){
            source = new DefaultDockActionSource( new LocationHint( LocationHint.ACTION_GUARD, LocationHint.RIGHT ), this );
            setText( "Minimize" );
            
            setIcon( ResourceSet.ICONS.get( "minimize" ) );
        }
        
        @Override
        public void action( Dockable dockable ) {
            minimize( dockable );
        }
        
        public boolean react( Dockable dockable ) {
            if( dockable.asDockStation() != null )
                return false;
            
            DockStation parent = dockable.getDockParent();
            
            while( parent != null ){
                if( areaNormalized.contains( parent ))
                    return true;
                
                Dockable parentDockable = parent.asDockable();
                if( parentDockable == null )
                    return false;
                
                parent = parentDockable.getDockParent();
            }
            return false;
        }
        
        public DockActionSource getSource( Dockable dockable ) {
            return source;
        }
    }
    
    /**
     * An action that allows the user to normalize a {@link Dockable}.
     * The action is only added to the children of the "minimized stations".
     * @author Benjamin Sigg
     *
     */
    private class Normalize extends SimpleButtonAction implements ActionGuard {
        /** the result of {@link #getSource(Dockable)} */
        private DefaultDockActionSource source;
        
        /**
         * Creates a new action
         */
        public Normalize(){
            source = new DefaultDockActionSource( new LocationHint( LocationHint.ACTION_GUARD, LocationHint.RIGHT ), this );
            setText( "Normalize" );
            
            controller.getIcons().add( "split.normalize", new IconManagerListener(){
                public void iconChanged( String key, Icon icon ) {
                    setIcon( icon );
                }
            });
            
            setIcon( controller.getIcons().getIcon( "split.normalize" ) );
        }
        
        @Override
        public void action( Dockable dockable ) {
            normalize( dockable );
        }
        
        public boolean react( Dockable dockable ) {
            if( dockable.asDockStation() != null )
                return false;
            
            DockStation parent = dockable.getDockParent();
            while( parent != null ){
                if( areaMinimized.contains( parent ))
                    return true;
                
                Dockable parentDockable = parent.asDockable();
                if( parentDockable == null )
                    return false;
                
                parent = parentDockable.getDockParent();
            }
            return false;
        }
        
        public DockActionSource getSource( Dockable dockable ) {
            return source;
        }
    }
}
