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
import bibliothek.gui.dock.action.ActionGuard;
import bibliothek.gui.dock.action.DefaultDockActionSource;
import bibliothek.gui.dock.action.DockActionSource;
import bibliothek.gui.dock.action.LocationHint;
import bibliothek.gui.dock.action.actions.SimpleButtonAction;
import bibliothek.gui.dock.event.DockControllerAdapter;
import bibliothek.gui.dock.event.IconManagerListener;
import bibliothek.gui.dock.station.FlapDockStation;
import bibliothek.gui.dock.util.DockUtilities;
import bibliothek.help.util.ResourceSet;
import bibliothek.util.container.Tuple;

public class Minimizer {
    private List<DockStation> areaMaximized = new ArrayList<DockStation>();
    private List<FlapDockStation> areaMinimized = new ArrayList<FlapDockStation>();
    
    private DockStation defaultStation;
    private Map<FlapDockStation, DockableProperty> defaultDrops = new HashMap<FlapDockStation, DockableProperty>();
    
    private Map<Dockable, Tuple<DockStation, DockableProperty>> locations =
        new HashMap<Dockable, Tuple<DockStation,DockableProperty>>();
    
    private DockController controller;
    
    public Minimizer( DockController controller ){
        this.controller = controller;
        
        controller.addActionGuard( new Minimize() );
        controller.addActionGuard( new Maximize() );
        
        controller.getRegister().addDockRegisterListener( new Listener() );
    }
    
    public void setDefaultStation( DockStation defaultStation ) {
        this.defaultStation = defaultStation;
    }
    
    public void addAreaMaximized( DockStation station ){
        areaMaximized.add( station );
    }
    
    public void addAreaMinimized( FlapDockStation station, DockableProperty defaultDrop ){
        areaMinimized.add( station );
        defaultDrops.put( station, defaultDrop );
    }
    
    public void maximize( Dockable dockable ){
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
                if( areaMaximized.contains( parent ))
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
    
    private class Maximize extends SimpleButtonAction implements ActionGuard {
        private DefaultDockActionSource source;
        
        public Maximize(){
            source = new DefaultDockActionSource( new LocationHint( LocationHint.ACTION_GUARD, LocationHint.RIGHT ), this );
            setText( "Maximize" );
            
            controller.getIcons().add( "split.normalize", new IconManagerListener(){
                public void iconChanged( String key, Icon icon ) {
                    setIcon( icon );
                }
            });
            
            setIcon( controller.getIcons().getIcon( "split.normalize" ) );
        }
        
        @Override
        public void action( Dockable dockable ) {
            maximize( dockable );
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
