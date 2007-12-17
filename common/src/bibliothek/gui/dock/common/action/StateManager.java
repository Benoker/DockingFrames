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

import java.awt.Point;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.*;

import javax.swing.Icon;
import javax.swing.SwingUtilities;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DockableProperty;
import bibliothek.gui.dock.IconManager;
import bibliothek.gui.dock.PropertyTransformer;
import bibliothek.gui.dock.action.ActionGuard;
import bibliothek.gui.dock.action.actions.SimpleButtonAction;
import bibliothek.gui.dock.event.*;
import bibliothek.gui.dock.station.FlapDockStation;
import bibliothek.gui.dock.station.ScreenDockStation;
import bibliothek.gui.dock.station.SplitDockStation;
import bibliothek.gui.dock.station.screen.ScreenDockProperty;
import bibliothek.gui.dock.station.split.SplitDockTree;
import bibliothek.gui.dock.support.action.ModeTransitionManager;
import bibliothek.gui.dock.support.util.GenericStreamTransformation;
import bibliothek.gui.dock.support.util.Resources;
import bibliothek.gui.dock.util.DockUtilities;

/**
 * A manager that can minimize/normalize/maximize and externalize a
 * {@link Dockable}.<br>
 * <ul>
 *  <li>minimized: the element is child of a {@link FlapDockStation}</li>
 *  <li>maximized: the element is child of a {@link SplitDockStation}, and
 *  in fullscreen-mode</li>
 *  <li>externalized: the element is child of {@link ScreenDockStation}</li>
 *  <li>normalized: everything else</li>
 * </ul>
 * @author Benjamin Sigg
 */
public class StateManager extends ModeTransitionManager<StateManager.Location> {
	/** the key used for the {@link IconManager} to read the {@link javax.swing.Icon} for the "minimize"-action */
	public static final String ICON_MANAGER_KEY_MINIMIZE = "state manager minimize";
	/** the key used for the {@link IconManager} to read the {@link javax.swing.Icon} for the "maximize"-action */
	public static final String ICON_MANAGER_KEY_MAXIMIZE = "state manager maximize";
	/** the key used for the {@link IconManager} to read the {@link javax.swing.Icon} for the "normalize"-action */
	public static final String ICON_MANAGER_KEY_NORMALIZE = "state manager normalize";
	/** the key used for the {@link IconManager} to read the {@link javax.swing.Icon} for the "externalize"-action */
	public static final String ICON_MANAGER_KEY_EXTERNALIZE = "state manager externalize";
	
    /** the areas used to show a maximized element */
    private Set<SplitDockStation> normal = new HashSet<SplitDockStation>();
    
    /** the default station for normalized elements */
    private SplitDockStation defaultNormal;
    
    /** the areas used to show a minimized element */
    private Set<FlapDockStation> mini = new HashSet<FlapDockStation>();
    
    /** the default station for minimized elements */
    private FlapDockStation defaultMini;
    
    /** the station for maximized elements */
    private SplitDockStation maxi;
    
    /** the areas used to show a externalized element */
    private Set<ScreenDockStation> external = new HashSet<ScreenDockStation>();
    
    /** the default station for externalized elements */
    private ScreenDockStation defaultExternal;
    
    /** all stations known to this manager */
    private Map<String, DockStation> stations = new HashMap<String, DockStation>();
    
    /** key for the minimized mode */
    public static final String MINIMIZED = "mini";
    
    /** key for the maximized mode */
    public static final String MAXIMIZED = "maxi";
    
    /** key for the normalized mode */
    public static final String NORMALIZED = "normal";
    
    /** key for the externalized mode */
    public static final String EXTERNALIZED = "extern";
    
    /** listener to some elements known to this manager, ensures that {@link Dockable}s are associated with the correct mode */
    private Listener listener = new Listener();
    
    /** the controller of this realm */
    private DockController controller;
    
    /**
     * Creates a new manager. Adds listeners to the <code>controller</code>
     * and adds <code>this</code> as {@link ActionGuard} to the <code>controller</code>.
     * @param controller the controller which is observed by this manager.
     */
    public StateManager( DockController controller ){
        super( MINIMIZED, NORMALIZED, MAXIMIZED, EXTERNALIZED );
        this.controller = controller;
        
        controller.addActionGuard( this );
        controller.getRelocator().addDockRelocatorListener( listener );
        controller.getRegister().addDockRegisterListener( listener );
        
        ResourceBundle bundle = Resources.getBundle();
        
        getIngoingAction( MINIMIZED ).setText( bundle.getString( "minimize.in" ) );
        getIngoingAction( MINIMIZED ).setTooltipText( bundle.getString( "minimize.in.tooltip" ) );
        
        getIngoingAction( NORMALIZED ).setText( bundle.getString( "normalize.in" ) );
        getIngoingAction( NORMALIZED ).setTooltipText( bundle.getString( "normalize.in.tooltip" ) );
        
        getIngoingAction( MAXIMIZED ).setText( bundle.getString( "maximize.in" ) );
        getIngoingAction( MAXIMIZED ).setTooltipText( bundle.getString( "maximize.in.tooltip" ) );
        
        getIngoingAction( EXTERNALIZED ).setText( bundle.getString( "externalize.in" ) );
        getIngoingAction( EXTERNALIZED ).setTooltipText( bundle.getString( "externalize.in.tooltip" ) );
        
        IconManager icons = controller.getIcons();
        icons.setIconDefault( ICON_MANAGER_KEY_MAXIMIZE, Resources.getIcon( "maximize" ) );
        icons.setIconDefault( ICON_MANAGER_KEY_MINIMIZE, Resources.getIcon( "minimize" ) );
        icons.setIconDefault( ICON_MANAGER_KEY_NORMALIZE, Resources.getIcon( "normalize" ) );
        icons.setIconDefault( ICON_MANAGER_KEY_EXTERNALIZE, Resources.getIcon( "externalize" ) );
        
        wire( icons, ICON_MANAGER_KEY_MAXIMIZE, getIngoingAction( MAXIMIZED ));
        wire( icons, ICON_MANAGER_KEY_MINIMIZE, getIngoingAction( MINIMIZED ));
        wire( icons, ICON_MANAGER_KEY_NORMALIZE, getIngoingAction( NORMALIZED ));
        wire( icons, ICON_MANAGER_KEY_EXTERNALIZE, getIngoingAction( EXTERNALIZED ));
    }
    
    /**
     * Adds a listener to <code>icons</code> which will change the icon of
     * <code>action</code> whenever the icon <code>key</code> is changed.
     * @param icons the set of icons
     * @param key the name of the icon to observe
     * @param action the action whose icon will be changed
     */
    private void wire( IconManager icons, String key, final SimpleButtonAction action ){
    	icons.add( key, new IconManagerListener(){
    		public void iconChanged( String key, Icon icon ){
    			action.setIcon( icon );
    		}
    	});
    	action.setIcon( icons.getIcon( key ) );
    }
    
    /**
     * Adds a station to which a {@link Dockable} can be <i>normalized</i>
     * or <i>maximized</i>. If this is the first call to this method, then
     * <code>station</code> becomes the default station for this kind or
     * operation.
     * @param name the name of the station
     * @param station the new station.
     */
    public void add( String name, SplitDockStation station ){
        if( name == null )
            throw new NullPointerException( "name must not be null" );
        
        if( stations.containsKey( name ))
            throw new IllegalArgumentException( "There is already a station registered with that name" );
        
        if( station == null )
            throw new NullPointerException( "station must not be null" );
        
        stations.put( name, station );
        normal.add( station );
        if( defaultNormal == null )
            defaultNormal = station;
        
        if( maxi == null )
        	maxi = station;
        
        station.addSplitDockStationListener( listener );
    }
    
    /**
     * Adds a station to which a {@link Dockable} can be <i>minimized</i>.
     * If this is the first call to this method, then
     * <code>station</code> becomes the default station for this kind or
     * operation.
     * @param name the name of the station
     * @param station the new station.
     */
    public void add( String name, FlapDockStation station ){
        if( name == null )
            throw new NullPointerException( "name must not be null" );
        
        if( stations.containsKey( name ))
            throw new IllegalArgumentException( "There is already a station registered with that name" );
        
        if( station == null )
            throw new NullPointerException( "station must not be null" );
        
        stations.put( name, station );
        mini.add( station );
        if( defaultMini == null )
            defaultMini = station;
    }
    
    /**
     * Adds a station to which a {@link Dockable} can be <i>externalized</i>.
     * If this is the first call to this method, then
     * <code>station</code> becomes the default station for this kind or
     * operation.
     * @param name the name of the station
     * @param station the new station.
     */
    public void add( String name, ScreenDockStation station ){
        if( name == null )
            throw new NullPointerException( "name must not be null" );
        
        if( stations.containsKey( name ))
            throw new IllegalArgumentException( "There is already a station registered with that name" );
        
        if( station == null )
            throw new NullPointerException( "station must not be null" );
        
        stations.put( name, station );
        external.add( station );
        if( defaultExternal == null )
            defaultExternal = station;
    }
    
    @Override
    protected String[] availableModes( String current, Dockable dockable ) {
        if( MINIMIZED.equals( current ))
            return new String[]{ NORMALIZED, MAXIMIZED, EXTERNALIZED };
        
        if( NORMALIZED.equals( current ))
            return new String[]{ MINIMIZED, MAXIMIZED, EXTERNALIZED };
        
        if( MAXIMIZED.equals( current ))
            return new String[]{ NORMALIZED, MINIMIZED, EXTERNALIZED };
        
        if( EXTERNALIZED.equals( current ))
            return new String[]{ NORMALIZED, MAXIMIZED, MINIMIZED };
        
        return new String[]{ MINIMIZED, NORMALIZED, MAXIMIZED, EXTERNALIZED };
    }

    @Override
    protected String currentMode( Dockable dockable ) {
        if( normal.contains( dockable ) || mini.contains( dockable ) || external.contains( dockable ))
            return NORMALIZED;
        
        DockStation parent = dockable.getDockParent();
        while( parent != null ){
            if( mini.contains( parent ))
                return MINIMIZED;
            
            if( external.contains( parent ))
                return EXTERNALIZED;
            
            if( maxi == parent ){
            	if( maxi.getFullScreen() == dockable )
            		return MAXIMIZED;
            	else
            		return NORMALIZED;
            }
            
            if( normal.contains( parent )){
            	return NORMALIZED;
            }
            
            dockable = parent.asDockable();
            parent = dockable == null ? null : dockable.getDockParent();
        }
        
        return NORMALIZED;
    }

    @Override
    protected String getDefaultMode( Dockable dockable ) {
        return NORMALIZED;
    }

    @Override
    protected void transition( String oldMode, String newMode, Dockable dockable ) {
        store( oldMode, dockable );
        if( MAXIMIZED.equals( newMode )){
        	maximize( dockable );
        }
        else{
        	changeTo( dockable, newMode, true );
        	unmaximize();
        }
        
        if( !MINIMIZED.equals( newMode ))
        	controller.setFocusedDockable( dockable, true );	
    }
    
    @Override
    protected void transitionDuringRead( String oldMode, String newMode, Dockable dockable ) {
        // ignore
    }
    
    /**
     * Ensures that <code>dockable</code> is the full-screen element of
     * the maximized-station.
     * @param dockable the element that should be made fullscreen
     */
    private void maximize( Dockable dockable ){
    	unmaximize();
    	
    	if( dockable.getDockParent() == maxi )
    		maxi.setFullScreen( dockable );
    	else{
    	    dockable.getDockParent().drag( dockable );
    	    SplitDockTree tree = maxi.createTree();
    		if( tree.getRoot() == null )
    			tree.root( dockable );
    		else{
    			tree.root( tree.horizontal( tree.put( dockable ), tree.unroot() ) );
    		}
    		maxi.dropTree( tree );
    		maxi.setFullScreen( dockable );
    	}
    }
    
    /**
     * Ensures that no {@link Dockable} is maximized
     */
    private void unmaximize(){
    	Dockable dockable = maxi.getFullScreen();
    	if( dockable != null ){
    		String mode = previousMode( dockable );
    		if( mode == null )
    			mode = getDefaultMode( dockable );
    		
    		if( MAXIMIZED.equals( mode ))
    			mode = NORMALIZED;

    		changeTo( dockable, mode, false );
    		putMode( dockable, mode );
    		maxi.setFullScreen( null );
    	}
    }
    
    /**
     * Changes the size and location of <code>dockable</code> such that its
     * new mode is <code>mode</code>
     * @param dockable the element whose mode will be changed
     * @param mode the new mode, one of {@link #MINIMIZED}, {@link #NORMALIZED}
     * or {@link #EXTERNALIZED}
     * @param unmaximize whether this method should check that the maxi-station
     * does not show a maximized {@link Dockable}.
     * @throws IllegalArgumentException if <code>mode</code> is {@link #MAXIMIZED}
     */
    private void changeTo( Dockable dockable, String mode, boolean unmaximize ){
    	if( MAXIMIZED.equals( mode ))
    		throw new IllegalArgumentException( "This method can't handle mode MAXIMIZED" );
    	
        Location location = getProperties( mode, dockable );
        
        boolean done = false;
        if( location != null ){
            DockStation station = stations.get( location.root );
            if( station != null ){
            	if( station == dockable.getDockParent() ){
            	    boolean needDrop = true;
            	    
            	    if( unmaximize && station == maxi ){
            	        if( location.location.getSuccessor() == null ){
                	        String[] history = history( dockable );
                	        if( history != null && history.length >= 2 ){
                	            String last = history[ history.length-1 ];
                	            String secondLast = history[ history.length-2 ];
                	            if( NORMALIZED.equals( secondLast ) && MAXIMIZED.equals( last ))
                	                needDrop = false;
                	        }
            	        }
            	        
                        maxi.setFullScreen( null );
                        
                        if( needDrop )
                            maxi.drag( dockable );
                        else
                            done = true;
            		}
            		
            	    if( needDrop )
            	        done = station.drop( dockable, location.location );
            	}
            	else{
	            	if( unmaximize && station == maxi )
	            		unmaximize();
	            	
	                done = station.drop( dockable, location.location );
            	}
            }
        }
        
        if( !done ){
            // put onto default location
            if( MINIMIZED.equals( mode ))
                checkedDrop( defaultMini, dockable, null );
            else if( EXTERNALIZED.equals( mode )){
                if( defaultExternal != dockable.getDockParent() ){

                    Point corner = new Point();
                    SwingUtilities.convertPointToScreen( corner, dockable.getComponent() );
                    ScreenDockProperty property = new ScreenDockProperty( 
                            corner.x, corner.y, dockable.getComponent().getWidth(), dockable.getComponent().getHeight() );
                    
                    boolean externDone = defaultExternal.drop( dockable, property, false );
                    
                    if( !externDone )
                        defaultExternal.drop( dockable );
                }
            }
            else{ // normal
            	if( unmaximize && defaultNormal == maxi )
            		unmaximize();
            	
                checkedDrop( defaultNormal, dockable, null );
            }
        }
    }
    
    /**
     * Drops <code>dockable</code> onto <code>station</code>, but only
     * if <code>station</code> is not the parent of <code>dockable</code>.
     * @param station the new parent of <code>dockable</code>
     * @param dockable the new child of <code>station</code>
     * @param location the preferred location, can be <code>null</code>
     */
    private void checkedDrop( DockStation station, Dockable dockable, DockableProperty location ){
        if( station != dockable.getDockParent() ){
            boolean done = false;
            
            if( location != null )
                done = station.drop( dockable, location );
            
            if( !done )
                station.drop( dockable );
        }
    }

    /**
     * Stores the location of <code>dockable</code> under the key <code>mode</code>.
     * @param mode the mode <code>dockable</code> is currently in
     * @param dockable the element whose location will be stored
     */
    private void store( String mode, Dockable dockable ){
        String root = null;
        DockStation rootStation = null;
        
        for( Map.Entry<String, DockStation> station : stations.entrySet() ){
            if( DockUtilities.isAnchestor( station.getValue(), dockable )){
                root = station.getKey();
                rootStation = station.getValue();
                break;
            }
        }
        
        if( root == null ){
            setProperties( mode, dockable, null );
        }
        else{
            Location location = new Location();
            location.root = root;
            location.location = DockUtilities.getPropertyChain( rootStation, dockable );
            setProperties( mode, dockable, location );
        }
    }
    
    /**
     * Describes the location of a {@link Dockable}.
     * @author Benjamin Sigg
     */
    public static class Location{
        /** the name of the root station */
        private String root;
        /** the exact location */
        private DockableProperty location;
        
        /**
         * Gets the name of the root-station.
         * @return the name
         */
        public String getRoot() {
            return root;
        }
        
        /**
         * Gets the exact location.
         * @return the location
         */
        public DockableProperty getLocation() {
            return location;
        }
    }
    
    /**
     * A transformer to read or write {@link Location}s.
     * @author Benjamin Sigg
     *
     */
    public static class LocationStreamTransformer implements GenericStreamTransformation<Location>{
        /** transformer to read or write single {@link DockableProperty}s */
        private PropertyTransformer transformer = new PropertyTransformer();

        public void write( DataOutputStream out, Location element ) throws IOException {
            out.writeUTF( element.root );
            transformer.write( element.location, out );
        }
        public Location read( DataInputStream in ) throws IOException {
            Location location = new Location();
            location.root = in.readUTF();
            location.location = transformer.read( in );
            return location;
        }
    }
    
    /**
     * A listener informing the enclosing manager when a {@link Dockable} changes
     * its mode.
     * @author Benjamin Sigg
     */
    private class Listener implements SplitDockListener, DockRelocatorListener, DockRegisterListener, DockHierarchyListener{
        public void fullScreenDockableChanged( SplitDockStation station, Dockable oldFullScreen, Dockable newFullScreen ) {
            if( oldFullScreen != null )
                validate( oldFullScreen );
            
            if( newFullScreen != null )
                validate( newFullScreen );
        }

        public void dockableDrag( DockController controller, Dockable dockable, DockStation station ) {
            store( currentMode( dockable ), dockable );
        }

        public void dockablePut( DockController controller, Dockable dockable, DockStation station ) {
            unmaximize();
        }

		public void dockStationRegistered( DockController controller, DockStation station ){
			// ignore
		}

		public void dockStationRegistering( DockController controller, DockStation station ){
			// ignore
		}

		public void dockStationUnregistered( DockController controller, DockStation station ){
			// ignore
		}

		public void dockableRegistered( DockController controller, Dockable dockable ){
			dockable.addDockHierarchyListener( this );
		}

		public void dockableRegistering( DockController controller, Dockable dockable ){
			// ignore
		}

		public void dockableUnregistered( DockController controller, Dockable dockable ){
		    dockable.removeDockHierarchyListener( this );
		}
		
		public void controllerChanged( DockHierarchyEvent event ) {
		    // ignore
		}
		
		public void hierarchyChanged( DockHierarchyEvent event ) {
		    rebuild( event.getDockable() );
		}
    }
}
