package bibliothek.help.view.dock;

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
import bibliothek.gui.dock.action.*;
import bibliothek.gui.dock.action.actions.SimpleButtonAction;
import bibliothek.gui.dock.event.DockControllerAdapter;
import bibliothek.gui.dock.event.IconManagerListener;
import bibliothek.gui.dock.station.FlapDockStation;
import bibliothek.gui.dock.util.DockUtilities;
import bibliothek.help.Core;
import bibliothek.help.util.ResourceSet;
import bibliothek.util.container.Tuple;

/**
 * The minimizer adds two new {@link DockAction actions} to the DockingFrames.
 * These actions can be used to <i>minimize</i> and <i>normalize</i> any
 * {@link Dockable}.<br>
 * Minimizing a <code>Dockable</code> means to store the location of the
 * <code>Dockable</code> and then move the <code>Dockable</code> into a
 * {@link FlapDockStation} that sits at the edge of the applications main frame.<br>
 * Normalizing a <code>Dockable</code> means to move a minimized <code>Dockable</code>
 * from a <code>FlapDockStation</code> back to its original position.<br>
 * Clients can define which {@link DockStation}s are used for the "normal"
 * and the "minimized" <code>Dockable</code>s by registering them through
 * {@link #addAreaMinimized(FlapDockStation, DockableProperty) addAreaMinimized}
 * and {@link #addAreaNormalized(DockStation) addAreaNormalized}. 
 * @author Benjamin Sigg
 *
 */
public class Minimizer {
    /** the areas which contain the normalized {@link Dockable}s */
    private List<DockStation> areaNormalized = new ArrayList<DockStation>();
    /** the areas which contain the minimized {@link Dockable}s */
    private List<FlapDockStation> areaMinimized = new ArrayList<FlapDockStation>();
    
    /** the station used to normalize a {@link Dockable} when its original position is unknown */
    private DockStation defaultStation;
    /** the preferred location where to add a minimized {@link Dockable} to a station from {@link #areaMinimized} */
    private Map<FlapDockStation, DockableProperty> defaultDrops = new HashMap<FlapDockStation, DockableProperty>();
    
    /** the preferred locations where a {@link Dockable} should be normalized again */
    private Map<Dockable, Tuple<DockStation, DockableProperty>> locations =
        new HashMap<Dockable, Tuple<DockStation,DockableProperty>>();
    
    /** the core of this application */
    private Core core;
    /** the controller for which this {@link Minimizer} works */
    private DockController controller;
    
    /**
     * Creates a new <code>Minimizer</code>, adds all listeners and actions
     * to <code>controller</code>.
     * @param core the center of this application
     * @param controller the controller to which the actions and listeners are
     * added.
     */
    public Minimizer( Core core, DockController controller ){
    	this.core = core;
        this.controller = controller;
        
        controller.addActionGuard( new Minimize() );
        controller.addActionGuard( new Normalize() );
        
        controller.getRegister().addDockRegisterListener( new Listener() );
    }
    
    public void setDefaultStation( DockStation defaultStation ) {
        this.defaultStation = defaultStation;
    }
    
    public void addAreaNormalized( DockStation station ){
        areaNormalized.add( station );
    }
    
    public void addAreaMinimized( FlapDockStation station, DockableProperty defaultDrop ){
        areaMinimized.add( station );
        defaultDrops.put( station, defaultDrop );
    }
    
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
    
    public void minimize( Dockable dockable ){        
        Component component = dockable.getComponent();
        Point center = new Point( component.getWidth()/2, component.getHeight()/2);
        SwingUtilities.convertPointToScreen( center, component );
        
        FlapDockStation bestStation = null;
        double bestDistance = Double.MAX_VALUE;
        
        for( FlapDockStation station : areaMinimized ){
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
    
    private class Listener extends DockControllerAdapter{
        @Override
        public void dockableUnregistered( DockController controller, Dockable dockable ) {
        	if( !core.isOnThemeUpdate() )
        		locations.remove( dockable );
        }
    }
    
    private class Minimize extends SimpleButtonAction implements ActionGuard {
        private DefaultDockActionSource source;
        
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
    
    private class Normalize extends SimpleButtonAction implements ActionGuard {
        private DefaultDockActionSource source;
        
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
