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
package bibliothek.gui.dock.facile.action;

import java.awt.Point;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javax.swing.Icon;
import javax.swing.SwingUtilities;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.FlapDockStation;
import bibliothek.gui.dock.ScreenDockStation;
import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.StackDockStation;
import bibliothek.gui.dock.action.ActionGuard;
import bibliothek.gui.dock.action.actions.SimpleButtonAction;
import bibliothek.gui.dock.event.DockHierarchyEvent;
import bibliothek.gui.dock.event.DockHierarchyListener;
import bibliothek.gui.dock.event.DockRegisterListener;
import bibliothek.gui.dock.event.DockRelocatorListener;
import bibliothek.gui.dock.event.IconManagerListener;
import bibliothek.gui.dock.event.SplitDockListener;
import bibliothek.gui.dock.layout.DockableProperty;
import bibliothek.gui.dock.layout.PropertyTransformer;
import bibliothek.gui.dock.station.screen.ScreenDockProperty;
import bibliothek.gui.dock.station.split.SplitDockTree;
import bibliothek.gui.dock.support.action.ModeTransitionManager;
import bibliothek.gui.dock.support.util.GenericStreamTransformation;
import bibliothek.gui.dock.support.util.Resources;
import bibliothek.gui.dock.util.DockUtilities;
import bibliothek.gui.dock.util.IconManager;

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
     * The location of the last element which was maximized, can be <code>null</code>.
     * Only used when an unknown element is maximized.
     */
    private Location lastMaximizedLocation = null;
    /** 
     * The mode of the last element which was maximized, can be <code>null</code>.
     * Only used when an unknown element is maximized.
     */
    private String lastMaximizedMode = null;
    
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
        getIngoingAction( MINIMIZED ).setTooltip( bundle.getString( "minimize.in.tooltip" ) );
        
        getIngoingAction( NORMALIZED ).setText( bundle.getString( "normalize.in" ) );
        getIngoingAction( NORMALIZED ).setTooltip( bundle.getString( "normalize.in.tooltip" ) );
        
        getIngoingAction( MAXIMIZED ).setText( bundle.getString( "maximize.in" ) );
        getIngoingAction( MAXIMIZED ).setTooltip( bundle.getString( "maximize.in.tooltip" ) );
        
        getIngoingAction( EXTERNALIZED ).setText( bundle.getString( "externalize.in" ) );
        getIngoingAction( EXTERNALIZED ).setTooltip( bundle.getString( "externalize.in.tooltip" ) );
        
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
    
    @Override
    public void write( GenericStreamTransformation<? super Location> mode, DataOutputStream out ) throws IOException {
        if( lastMaximizedLocation == null ){
            out.writeBoolean( false );
        }
        else{
            out.writeBoolean( true );
            mode.write( out, lastMaximizedLocation );
            out.writeUTF( lastMaximizedMode );
        }
        
        super.write( mode, out );
    }
    
    @Override
    public void read( GenericStreamTransformation<? extends Location> mode, DataInputStream in ) throws IOException {
        if( in.readBoolean() ){
            lastMaximizedLocation = mode.read( in );
            lastMaximizedMode = in.readUTF();
        }
        else{
            lastMaximizedLocation = null;
            lastMaximizedMode = null;
        }
        
        super.read( mode, in );
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
    
    /**
     * Removes the {@link DockStation} <code>name</code> from this manager. If
     * the station is a default-station, then this property is set to <code>null</code>.
     * @param name the name of the station to remove
     */
    public void remove( String name ){
    	if( name == null )
    		throw new NullPointerException( "name must not be null" );
    	
    	DockStation station = stations.remove( name );
    	normal.remove( name );
    	mini.remove( name );
    	external.remove( name );
    	
    	if( station == defaultExternal )
    		defaultExternal = null;
    	
    	if( station == defaultMini )
    		defaultMini = null;
    	
    	if( station == defaultNormal )
    		defaultNormal = null;
    	
    	if( station == maxi )
    		maxi = null;
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
    
    /**
     * Guesses the mode a child of <code>station</code> would have if it is
     * dropped on <code>station</code>.
     * @param station some station
     * @return the mode or <code>null</code>. The mode is one of 
     * {@link #NORMALIZED}, {@link #MINIMIZED} or {@link #EXTERNALIZED}.
     * The mode {@link #MAXIMIZED} will not be considered.
     */
    protected String childsMode( DockStation station ){
        while( station != null ){
            if( normal.contains( station ))
                return NORMALIZED;
            if( mini.contains( station ))
                return MINIMIZED;
            if( external.contains( station ))
                return EXTERNALIZED;
            
            Dockable dockable = station.asDockable();
            station = dockable == null ? null : dockable.getDockParent();
        }
        return null;
    }

    @Override
    protected String getDefaultMode( Dockable dockable ) {
        return NORMALIZED;
    }

    @Override
    protected void transition( String oldMode, String newMode, Dockable dockable ) {
        try{
            controller.getRegister().setStalled( true );

            if( oldMode != null ){
            	store( dockable );
            }
            
            change( oldMode, newMode, dockable );
            
            putMode( dockable );
            
            if( !MINIMIZED.equals( newMode ))
            	controller.setFocusedDockable( dockable, true );
        }
        finally{
            controller.getRegister().setStalled( false );
            controller.getSingleParentRemover().testAll( controller );
        }
    }
    
    @Override
    protected void transitionDuringRead( String oldMode, String newMode, Dockable dockable ) {
        // ignore
    }
    
    /**
     * Gets the element which must be maximized when the user requests that
     * <code>dockable</code> is maximized.
     * @param dockable some element, not <code>null</code>
     * @return the element that must be maximized, might be <code>dockable</code>
     * itself, not <code>null</code>
     */
    protected Dockable getMaximizingElement( Dockable dockable ){
        DockStation station = dockable.getDockParent();
        if( station == null )
            return dockable;
        
        if( !(station instanceof StackDockStation ))
            return dockable;
        
        return station.asDockable();
    }
    
    /**
     * Gets the element which would be maximized if <code>old</code> is currently
     * maximized, and <code>dockable</code> is or will not be maximized.
     * @param old some element
     * @param dockable some element, might be <code>old</code>
     * @return the element which would be maximized if <code>dockable</code> is
     * no longer maximized, can be <code>null</code>
     */
    protected Dockable getMaximizingElement( Dockable old, Dockable dockable ){
        if( old == dockable )
            return null;
        
        if( old instanceof DockStation ){
            DockStation station = (DockStation)old;
            if( station.getDockableCount() == 2 ){
                if( station.getDockable( 0 ) == dockable )
                    return station.getDockable( 1 );
                if( station.getDockable( 1 ) == dockable )
                    return station.getDockable( 0 );
            }
            if( station.getDockableCount() < 2  )
                return null;
        }
        
        return old;
    }

    /**
     * Changes the mode of <code>dockable</code>. Modes are
     * {@link #MINIMIZED}, {@link #NORMALIZED}, {@link #MAXIMIZED} and
     * {@link #EXTERNALIZED}.
     * @param current the current mode, can be <code>null</code>
     * @param destination the mode <code>dockable</code> would like to have
     * @param dockable the element whose mode is to change
     */
    private void change( String current, String destination, Dockable dockable ){
        change( current, destination, dockable, getProperties( destination, dockable ));
    }
    
    /**
     * Changes the mode of <code>dockable</code>. Modes are
     * {@link #MINIMIZED}, {@link #NORMALIZED}, {@link #MAXIMIZED} and
     * {@link #EXTERNALIZED}.
     * @param current the current mode, can be <code>null</code>
     * @param destination the mode <code>dockable</code> would like to have
     * @param dockable the element whose mode is to change
     * @param location the new location, can be <code>null</code>. The location
     * should match the <code>destination</code>, otherwise the <code>dockable</code>
     * might be placed at the wrong location.
     */
    private void change( String current, String destination, Dockable dockable, Location location ){
        if( MAXIMIZED.equals( destination ))
            maximize( current, dockable );
        if( MINIMIZED.equals( destination ))
            minimize( dockable, location );
        if( NORMALIZED.equals( destination ))
            normalize( dockable, location );
        if( EXTERNALIZED.equals( destination ))
            externalize( dockable, location );
    }
    
    /**
     * Ensures that <code>dockable</code> is maximized.
     * @param current the mode <code>dockable</code> is currently in,
     * can be <code>null</code> to indicate that <code>dockable</code>
     * is newly added.
     * @param dockable the element that should be made maximized
     */
    private void maximize( String current, Dockable dockable ){
    	unmaximize();
    	
    	if( current != null && !NORMALIZED.equals( current )){
    		change( current, NORMALIZED, dockable );
    		current = NORMALIZED;
    	}
    	
    	Dockable maximizing = getMaximizingElement( dockable );
    	if( maximizing != dockable )
    		store( maximizing );
    	
    	if( getName( maximizing ) == null ){
    	    lastMaximizedLocation = currentLocation( current, dockable );
    	    lastMaximizedMode = current;
    	}
    	else{
    	    lastMaximizedLocation = null;
    	    lastMaximizedMode = null;
    	}
    	
    	if( maximizing.getDockParent() == maxi )
    		maxi.setFullScreen( maximizing );
    	else{
    	    maximizing.getDockParent().drag( maximizing );
    	    SplitDockTree tree = maxi.createTree();
    		if( tree.getRoot() == null )
    			tree.root( maximizing );
    		else{
    			tree.root( tree.horizontal( tree.put( maximizing ), tree.unroot() ) );
    		}
    		maxi.dropTree( tree, false );
    		maxi.setFullScreen( maximizing );
    	}
    	
    	if( maximizing != dockable )
    		putMode( maximizing );
    }
    
    /**
     * Ensures that no {@link Dockable} is maximized
     */
    private void unmaximize(){
    	Dockable dockable = maxi.getFullScreen();
    	if( dockable != null ){
            maxi.setFullScreen( null );
    	    
    	    if( lastMaximizedLocation != null ){
    	        change( MAXIMIZED, lastMaximizedMode, dockable, lastMaximizedLocation );
    	        lastMaximizedLocation = null;
    	        lastMaximizedMode = null;
    	    }
    	    else{
        	    String mode = previousMode( dockable );
        		if( mode == null )
        			mode = getDefaultMode( dockable );
        		
        		if( MAXIMIZED.equals( mode ))
        			mode = NORMALIZED;
    
        		change( MAXIMIZED, mode, dockable );
    	    }
    	    
    	    putMode( dockable );
    	    
    	    controller.getSingleParentRemover().testAll( controller );
    	}
    }
    
    /**
     * Makes sure that <code>dockable</code> is normalized, where
     * <code>location</code> describes the new position of <code>dockable</code>.
     * @param dockable the element to normalize, can already be in 
     * normalized-state.
     * @param location a location describing the new position of <code>dockable</code>,
     * the behavior is unspecified if <code>location</code> does not describe
     * a normalized position. Can be <code>null</code>.
     */
    private void normalize( Dockable dockable, Location location ){
        boolean done = false;
        if( location != null ){
            if( location.location.getSuccessor() == null ){
                String[] history = history( dockable );
                if( history != null && history.length >= 2 ){
                    String last = history[ history.length-1 ];
                    String secondLast = history[ history.length-2 ];
                    if( NORMALIZED.equals( secondLast ) && MAXIMIZED.equals( last )){
                        if( stations.get( location.root ) == maxi ){
                            unmaximize();
                            done = true;
                        }
                    }
                }
            }
        }
        
        if( !done ){
            Dockable maximized = maxi.getFullScreen();
            boolean child = false;
            if( maximized != null ){
                child = DockUtilities.isAncestor( maximized, dockable );
                if( child )
                    maximized = null;
            }
            
            unmaximize();
            
            if( !child && !drop( location, dockable )){
                checkedDrop( defaultNormal, dockable, null );
            
                if( maximized != null && maximized.getDockParent() != null ){
                    if( !DockUtilities.isAncestor( maxi, dockable )){
                        maximize( currentMode( maximized ), maximized );
                    }
                }
            }
        }
    }
    
    /**
     * Makes sure that <code>dockable</code> is minimized, where
     * <code>location</code> describes the new position of <code>dockable</code>.
     * @param dockable the element to minimize, can already be in 
     * minimized-state.
     * @param location a location describing the new position of <code>dockable</code>,
     * the behavior is unspecified if <code>location</code> does not describe
     * a minimized position. Can be <code>null</code>.
     */
    private void minimize( Dockable dockable, Location location ){
        Dockable maximized = null;
        if( MAXIMIZED.equals( currentMode( dockable ) ) ){
            maximized = maxi.getFullScreen();
            if( maximized != null )
                maximized = getMaximizingElement( maximized, dockable );
            
            unmaximize();
        }
        
        if( !drop( location, dockable )){
            checkedDrop( defaultMini, dockable, null );
        }
        
        if( maximized != null && maximized.getDockParent() != null )
            maximize( currentMode( maximized ), maximized );
    }
    
    /**
     * Makes sure that <code>dockable</code> is externalized, where
     * <code>location</code> describes the new position of <code>dockable</code>.
     * @param dockable the element to externalized, can already be in 
     * externalize-state.
     * @param location a location describing the new position of <code>dockable</code>,
     * the behavior is unspecified if <code>location</code> does not describe
     * an externalized position. Can be <code>null</code>.
     */
    private void externalize( Dockable dockable, Location location ){
        Dockable maximized = null;
        if( MAXIMIZED.equals( currentMode( dockable ) ) ){
            maximized = maxi.getFullScreen();
            if( maximized != null )
                maximized = getMaximizingElement( maximized, dockable );
            
            unmaximize();
        }
        
        if( !drop( location, dockable )){
            if( dockable.getDockParent() != defaultExternal ){
                Point corner = new Point();
                SwingUtilities.convertPointToScreen( corner, dockable.getComponent() );
                ScreenDockProperty property = new ScreenDockProperty( 
                        corner.x, corner.y, dockable.getComponent().getWidth(), dockable.getComponent().getHeight() );
                
                boolean externDone = defaultExternal.drop( dockable, property, false );
                
                if( !externDone )
                    defaultExternal.drop( dockable );
            }
        }
        
        if( maximized != null && maximized.getDockParent() != null )
            maximize( currentMode( maximized ), maximized );
    }
    
    /**
     * Searches the station matching <code>location</code> and drops
     * <code>dockable</code> there.
     * @param location the new location, can be <code>null</code>
     * @param dockable the element to drop
     * @return <code>true</code> if the operation was successful, 
     * <code>false</code> if not.
     */
    private boolean drop( Location location, Dockable dockable ){
        if( location == null )
            return false;
        
        DockStation station = stations.get( location.root );
        if( station == null )
            return false;
        
        return station.drop( dockable, location.getLocation() );
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
     * Stores for each {@link Dockable} in the tree with the root <code>dockable</code>
     * the location associated to their current mode.
     * @param dockable a root of a tree
     */
    protected void store( Dockable dockable ){
        DockUtilities.visit( dockable, new DockUtilities.DockVisitor(){
            @Override
            public void handleDockable( Dockable check ) {
                String current = currentMode( check );
                if( current != null )
                    store( current, check );
            }
        });
    }
    
    /**
     * Stores the location of <code>dockable</code> under the key <code>mode</code>.
     * @param mode the mode <code>dockable</code> is currently in
     * @param dockable the element whose location will be stored
     */
    protected void store( String mode, Dockable dockable ){
        // there is not much to store for "maximized"...
        if( !MAXIMIZED.equals( mode )){
            setProperties( mode, dockable, currentLocation( mode, dockable ) );
        }
    }
    
    /**
     * Stores for each {@link Dockable} of the tree with the root 
     * <code>dockable</code> the current mode.
     * @param dockable the element whose mode should be stored
     */
    protected void putMode( Dockable dockable ){
        DockUtilities.visit( dockable, new DockUtilities.DockVisitor(){
            @Override
            public void handleDockable( Dockable check ) {
                String current = currentMode( check );
                if( current != null )
                    putMode( check, current );
            }
        });
    }
    
    /**
     * Creates the {@link Location} describing the location of <code>dockable</code>.
     * @param mode the current mode
     * @param dockable the element whose {@link Location} is created
     * @return the new {@link Location} or <code>null</code>
     */
    protected Location currentLocation( String mode, Dockable dockable ){
        String root = null;
        DockStation rootStation = null;
        
        for( Map.Entry<String, DockStation> station : stations.entrySet() ){
            if( DockUtilities.isAncestor( station.getValue(), dockable )){
                root = station.getKey();
                rootStation = station.getValue();
                break;
            }
        }
        
        
        if( root == null || rootStation == dockable ){
            return null;
        }
        else{
            DockableProperty location = DockUtilities.getPropertyChain( rootStation, dockable );
            return new Location( root, location );
        }
    }
    
    /**
     * Gets the name of the root of <code>dockable</code>.
     * @param dockable the element whose station is searched
     * @return the name of the root
     */
    protected String getRootName( Dockable dockable ){
    	for( Map.Entry<String, DockStation> station : stations.entrySet() ){
            if( DockUtilities.isAncestor( station.getValue(), dockable )){
                return station.getKey();
            }
        }
        
    	return null;
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
         * Creates a new location.
         * @param root the name of the root station
         * @param location the location relatively to the root station
         */
        public Location( String root, DockableProperty location ){
        	this.root = root;
        	this.location = location;
        }
        
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
            String root = in.readUTF();
            DockableProperty location = transformer.read( in );
            return new Location( root, location );
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
