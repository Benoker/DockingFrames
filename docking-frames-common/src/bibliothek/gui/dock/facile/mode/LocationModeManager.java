/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2009 Benjamin Sigg
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
package bibliothek.gui.dock.facile.mode;

import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DockElement;
import bibliothek.gui.dock.action.DockActionSource;
import bibliothek.gui.dock.action.MultiDockActionSource;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.group.CGroupBehavior;
import bibliothek.gui.dock.common.group.CGroupBehaviorCallback;
import bibliothek.gui.dock.common.group.CGroupMovement;
import bibliothek.gui.dock.common.group.StackGroupBehavior;
import bibliothek.gui.dock.common.mode.ExtendedMode;
import bibliothek.gui.dock.control.DockRegister;
import bibliothek.gui.dock.control.focus.DefaultFocusRequest;
import bibliothek.gui.dock.control.relocator.DockRelocatorEvent;
import bibliothek.gui.dock.control.relocator.VetoableDockRelocatorAdapter;
import bibliothek.gui.dock.event.DockHierarchyEvent;
import bibliothek.gui.dock.event.DockHierarchyListener;
import bibliothek.gui.dock.event.DockRegisterAdapter;
import bibliothek.gui.dock.event.DoubleClickListener;
import bibliothek.gui.dock.facile.mode.status.DefaultExtendedModeEnablement;
import bibliothek.gui.dock.facile.mode.status.ExtendedModeEnablement;
import bibliothek.gui.dock.facile.mode.status.ExtendedModeEnablementFactory;
import bibliothek.gui.dock.facile.mode.status.ExtendedModeEnablementListener;
import bibliothek.gui.dock.layout.location.AsideRequest;
import bibliothek.gui.dock.layout.location.AsideRequestFactory;
import bibliothek.gui.dock.support.mode.AffectedSet;
import bibliothek.gui.dock.support.mode.AffectingRunnable;
import bibliothek.gui.dock.support.mode.ModeManager;
import bibliothek.gui.dock.support.mode.ModeManagerListener;
import bibliothek.gui.dock.util.DockProperties;
import bibliothek.gui.dock.util.IconManager;
import bibliothek.gui.dock.util.PropertyKey;
import bibliothek.gui.dock.util.PropertyValue;
import bibliothek.gui.dock.util.property.ConstantPropertyFactory;
import bibliothek.util.Path;

/**
 * {@link ModeManager} for the location of a {@link Dockable}. This manager is able to
 * work together with {@link CControl} or together with {@link DockController}. Clients
 * using it together with a {@link DockController} need to set icons for the 
 * modes manually, they can use the {@link IconManager} and the keys provided in
 * each mode (e.g. {@link NormalMode#ICON_IDENTIFIER}).
 * @author Benjamin Sigg
 * @param <M> the kind of mode this manager handles
 */
public class LocationModeManager<M extends LocationMode> extends ModeManager<Location, M>{
    /**
	 * {@link PropertyKey} for the {@link ExtendedModeEnablement} that should be used
	 * by a {@link LocationModeManager} to activate and deactivate the modes.
	 */
	public static final PropertyKey<ExtendedModeEnablementFactory> MODE_ENABLEMENT = 
		new PropertyKey<ExtendedModeEnablementFactory>( "locationmodemanager.mode_enablement", 
				new ConstantPropertyFactory<ExtendedModeEnablementFactory>( DefaultExtendedModeEnablement.FACTORY ), true  );
	
	/**
	 * {@link PropertyKey} for the {@link DoubleClickLocationStrategy} that should be used
	 * to change the {@link ExtendedMode} of an element which has been double-clicked.
	 */
	public static final PropertyKey<DoubleClickLocationStrategy> DOUBLE_CLICK_STRATEGY =
		new PropertyKey<DoubleClickLocationStrategy>( "locationmodemanager.double_click_strategy",
				new ConstantPropertyFactory<DoubleClickLocationStrategy>( DoubleClickLocationStrategy.DEFAULT ), true );
	
	/** a set of listeners that will be automatically added or removed from a {@link LocationMode} */
	private Map<Path, List<LocationModeListener>> listeners = new HashMap<Path, List<LocationModeListener>>();
	
	/** registers new dockables */
	private RegisterListener registerListener = new RegisterListener();
	
	/** registers when dockables change their position */
	private HierarchyListener hierarchyListener = new HierarchyListener();
	
	/** registers dragged and dropped dockables */
	private RelocatorListener relocatorListener = new RelocatorListener();
	
	/** how to group dockables */
	private CGroupBehavior behavior = new StackGroupBehavior();
	
	/** the action that is currently executing */
	private List<CGroupMovement> currentAction = new ArrayList<CGroupMovement>();
	
	/** the list of {@link Dockable}s for which {@link #refresh(Dockable, boolean)} has to be called */
	private LinkedHashSet<Dockable> pendingRefreshs = new LinkedHashSet<Dockable>();
	
	/** the current {@link ExtendedModeEnablementFactory} */
	private PropertyValue<ExtendedModeEnablementFactory> extendedModeFactory = new PropertyValue<ExtendedModeEnablementFactory>( MODE_ENABLEMENT ) {
		@Override
		protected void valueChanged( ExtendedModeEnablementFactory oldValue, ExtendedModeEnablementFactory newValue ){
			updateEnablement();
		}
	};
	
	/** the current {@link DoubleClickLocationStrategy} */
	private PropertyValue<DoubleClickLocationStrategy> doubleClickStrategy = new PropertyValue<DoubleClickLocationStrategy>( DOUBLE_CLICK_STRATEGY ) {
		@Override
		protected void valueChanged( DoubleClickLocationStrategy oldValue, DoubleClickLocationStrategy newValue ){
			// ignore
		}
	};
	
	/** a listener added to the current {@link #enablement} */
	private ExtendedModeEnablementListener enablementListener = new ExtendedModeEnablementListener() {
		public void availabilityChanged( Dockable dockable, ExtendedMode mode, boolean available ){
			refresh( dockable, true );
		}
	};
	
	/** detects double-click events and changes the mode of the clicked element */
	private DoubleClickListener doubleClickListener = new DoubleClickListener() {
		public DockElement getTreeLocation(){
			return null;
		}
		
		public boolean process( Dockable dockable, MouseEvent event ){
			if( event.isConsumed() )
				return false;
			
			dockable = getDoubleClickTarget( dockable );
			if( dockable != null ){
				M current = getCurrentMode( dockable );
				ExtendedMode next = getDoubleClickStrategy().handleDoubleClick( dockable, current == null ? null : current.getExtendedMode(), enablement );
				if( next != null && isModeAvailable( dockable, next )){
					setMode( dockable, next );
					ensureValidLocation( dockable );
					return true;
				}
			}
			return false;
		}
	};
	
	/** tells which modes are available for which element */
	private ExtendedModeEnablement enablement;

	/** 
	 * if > 0 then the layout-mode is active. In this mode this manager does not react on some
	 * events to not intervene layouting
	 */
	private int layoutMode = 0;
	
	/**
	 * Creates a new manager.
	 * @param controller the controller in whose realm this manager will work
	 */
	public LocationModeManager( DockController controller ){
		super( controller );
		registerListener.connect( controller );
		controller.getRelocator().addVetoableDockRelocatorListener( relocatorListener );
		
		updateEnablement();
		extendedModeFactory.setProperties( controller );
		
		addModeManagerListener( new LocationModeListenerAdapter() );
		
		controller.getDoubleClickController().addListener( doubleClickListener );
	}
	
	public void destroy(){
		registerListener.connect( null );
		DockController controller = getController();
		controller.getRelocator().removeVetoableDockRelocatorListener( relocatorListener );
		controller.getDoubleClickController().removeListener( doubleClickListener );
		
		for( LocationMode mode : this.modes() ){
			mode.setController( null );
		}
		
		super.destroy();
		extendedModeFactory.setProperties( (DockProperties)null );
	}
	
	/**
	 * Updates the current {@link ExtendedModeEnablement} using the factory
	 * provided by {@link #MODE_ENABLEMENT}.
	 */
	protected void updateEnablement(){
		if( enablement != null ){
			enablement.removeListener( enablementListener );
			enablement.destroy();
			enablement = null;
		}
		if( getController() != null ){
			enablement = extendedModeFactory.getValue().create( this );
			enablement.addListener( enablementListener );
		}
		rebuildAll();
	}
	
	/**
	 * Sets the group behavior. The group behavior is applied if {@link #setMode(Dockable, ExtendedMode)} is called and
	 * modifies the call such that another {@link Dockable} receives the event. Any call directly to any of
	 * the <code>apply</code> methods will not be modified by the group behavior.
	 * @param behavior the new behavior, not <code>null</code>
	 */
	public void setGroupBehavior( CGroupBehavior behavior ){
		if( behavior == null ){
			throw new IllegalArgumentException( "the group behavior must not be null" );
		}
		this.behavior = behavior;
	}
	
	/**
	 * Gets the current group behavior.
	 * @return the current behavior, not <code>null</code>
	 * @see #setGroupBehavior(CGroupBehavior)
	 */
	public CGroupBehavior getGroupBehavior(){
		return behavior;
	}
	
	/**
	 * Sets the current mode of <code>dockable</code>.
	 * @param dockable the dockable whose mode is to be set
	 * @param extendedMode the mode
	 * @throws IllegalArgumentException if <code>extendedMode</code> is unknown
	 */
	public void setMode( final Dockable dockable, final ExtendedMode extendedMode ){
		M mode = getMode( extendedMode.getModeIdentifier() );
		if( mode == null ){
			throw new IllegalArgumentException( "No mode '" + extendedMode.getModeIdentifier() + "' available" );
		}
	
		runTransaction( new Runnable(){
			public void run(){
				CGroupMovement action = behavior.prepare( LocationModeManager.this, dockable, extendedMode );
				if( action == null ){
					return;
				}
				
				apply( dockable, extendedMode, action );		
			}
		});
	}
	
	/**
	 * Gets the action that is currently carried out.
	 * @return the current action, can be <code>null</code>
	 */
	public CGroupMovement getCurrentAction(){
		if( currentAction.isEmpty() ){
			return null;
		}
		return currentAction.get( currentAction.size()-1 );
	}
	
	/**
	 * Executes <code>action</code> in a transaction assuming that the result of this action will lead to
	 * <code>dockable</code> having the new mode <code>extendedMode</code>.
	 * @param dockable the primary {@link Dockable}, this item may very well be the new focus owner
	 * @param extendedMode the expected mode <code>dockable</code> will have after <code>action</code> completed
	 * @param action the action to execute
	 */
	public void apply( final Dockable dockable, final ExtendedMode extendedMode, final CGroupMovement action ){
		runTransaction( new AffectingRunnable(){
			public void run( final AffectedSet set ){
				try{
					getController().getFocusController().freezeFocus();
					currentAction.add( action );
					action.apply( new CGroupBehaviorCallback(){
						public void setMode( Dockable element, ExtendedMode mode ){
							apply( element, mode.getModeIdentifier(), false );
						}
						
						public void setLocation( Dockable element, Location location ){
							apply( element, location.getMode(), location, set );
						}
						
						public LocationModeManager<? extends LocationMode> getManager(){
							return LocationModeManager.this;
						}
						
						public Location getLocation( Dockable dockable ){
							M mode = getCurrentMode( dockable );
							if( mode == null ){
								return null;
							}
							return mode.current( dockable );
						}
					});
				}
				finally{
					currentAction.remove( action );
					getController().getFocusController().meltFocus();
				}
				
				LocationMode mode = getMode( extendedMode.getModeIdentifier() );
				if( mode != null ){
					if( mode.shouldAutoFocus() ){
						getController().setFocusedDockable( new DefaultFocusRequest( dockable, null, true, true, false ));
					}
					else{
						getController().setFocusedDockable( new DefaultFocusRequest( null, null, true ));
					}
				}	
			}
		});
	}
	
	/**
	 * Gets the current mode of <code>dockable</code>.
	 * @param dockable the element whose mode is searched
	 * @return the mode or <code>null</code> if not found
	 */
	public ExtendedMode getMode( Dockable dockable ){
		LocationMode mode = getCurrentMode( dockable );
		if( mode == null )
			return null;
		return mode.getExtendedMode();
	}
	
	/**
	 * Checks all {@link LocationMode}s of this manager and returns all
	 * {@link DockStation}s that were registered with the given id. The same
	 * station or the same id might be used for different modes.
	 * @param id the id of some station
	 * @return each mode-area pair where the area is not <code>null</code>, can be empty
	 */
	public Map<ExtendedMode, DockStation> getRepresentations( String id ){
		if( id == null )
			throw new IllegalArgumentException( "id must not be null" );
		Map<ExtendedMode, DockStation> result = new HashMap<ExtendedMode, DockStation>();
		for( LocationMode mode : modes() ){
			DockStation station = mode.getRepresentation( id );
			if( station != null ){
				result.put( mode.getExtendedMode(), station );
			}
		}
		return result;
	}
	
	/**
	 * Ignores the call, the position of {@link Dockable}s is set elsewhere.
	 */
	@Override
	protected void applyDuringRead( String key, Path old, Path current, Dockable dockable ){
		// ignore
	}
	
	@Override
	public void apply( Dockable dockable, M mode, Location history, AffectedSet set ) {
		super.apply( dockable, mode, history, set );
		if( history != null ){
			history.resetApplicationDefined();
		}
	}
	
	/**
	 * Using the current {@link ExtendedModeEnablement} this method tells whether
	 * mode <code>mode</code> can be applied to <code>dockable</code>.
	 * @param dockable some element, not <code>null</code>
	 * @param mode some mode, not <code>null</code>
	 * @return the result of {@link ExtendedModeEnablement#isAvailable(Dockable, ExtendedMode)}
	 */
	public boolean isModeAvailable( Dockable dockable, ExtendedMode mode ){
		if( enablement == null )
			return false;
		
		return enablement.isAvailable( dockable, mode ).isAvailable();
	}
	
	/**
	 * Using the current {@link ExtendedModeEnablement} this method tells whether
	 * mode <code>mode</code> is hidden from the user when looking at <code>dockable</code>. A hidden
	 * mode 
	 * @param dockable some element, not <code>null</code>
	 * @param mode some mode, not <code>null</code>
	 * @return the result of {@link ExtendedModeEnablement#isAvailable(Dockable, ExtendedMode)}
	 */
	public boolean isModeHidden( Dockable dockable, ExtendedMode mode ){
		if( enablement == null )
			return false;
		
		return enablement.isHidden( dockable, mode ).isHidden();
	}
	
	/**
	 * Adds a listener to the mode with unique identifier <code>identifier</code>. If the
	 * mode is exchanged then this listener is automatically removed and may be re-added
	 * to the new mode.
	 * @param identifier the identifier of some mode (not necessarily registered yet).
	 * @param listener the new listener, not <code>null</code>
	 */
	public void addListener( Path identifier, LocationModeListener listener ){
		if( listener == null )
			throw new IllegalArgumentException( "listener must not be null" );
		
		List<LocationModeListener> list = listeners.get( identifier );
		if( list == null ){
			list = new ArrayList<LocationModeListener>();
			listeners.put( identifier, list );
		}
		list.add( listener );
		
		LocationMode mode = getMode( identifier );
		if( mode != null ){
			mode.addLocationModeListener( listener );
		}
	}
	
	/**
	 * Removes a listener from the mode <code>identifier</code>.
	 * @param identifier the name of a mode
	 * @param listener the listener to remove
	 */
	public void removeListener( Path identifier, LocationModeListener listener ){
		List<LocationModeListener> list = listeners.get( identifier );
		if( list == null )
			return;
		
		list.remove( listener );
		if( list.isEmpty() )
			listeners.remove( identifier );
		
		LocationMode mode = getMode( identifier );
		if( mode != null ){
			mode.removeLocationModeListener( listener );
		}
	}
	
	@Override
	public M getCurrentMode( Dockable dockable ){
		while( dockable != null ){
			for( M mode : modes() ){
				if( mode.isCurrentMode( dockable ))
					return mode;
			}
			DockStation station = dockable.getDockParent();
			dockable = station == null ? null : station.asDockable();
		}
		
		return null;
	}
	
	/**
	 * Gets the current strategy for handing double-clicks.
	 * @return the strategy, never <code>null</code>
	 * @see #setDoubleClickStrategy(DoubleClickLocationStrategy)
	 */
	public DoubleClickLocationStrategy getDoubleClickStrategy(){
		return doubleClickStrategy.getValue();
	}
	
	/**
	 * Sets the current strategy for handling double-clicks on {@link Dockable}s. This
	 * strategy will be asked what mode to assign to an element that has been double-clicked.
	 * Results that are not allowed by the current {@link ExtendedModeEnablement} are ignored.
	 * @param strategy the new strategy, can be <code>null</code> to set the default strategy
	 */
	public void setDoubleClickStrategy( DoubleClickLocationStrategy strategy ){
		doubleClickStrategy.setValue( strategy );
	}
	
	/**
	 * Tells whether this mode is currently in layouting mode. Some
	 * methods of this manager do not react while in layouting mode.
	 * @return <code>true</code> if layouting mode is active
	 */
	public boolean isLayouting(){
		return layoutMode > 0; 
	}
	
	/**
	 * Activates the {@link #isLayouting() layout mode} while <code>run</code>
	 * is running.
	 * @param run some code to execute
	 */
	public void runLayoutTransaction( Runnable run ){
		try{
			layoutMode++;
			runTransaction( run, true );
		}
		finally{
			layoutMode--;
		}
	}
	
    /**
     * Ensures that <code>dockable</code> is not hidden behind another 
     * {@link Dockable}. That does not mean that <code>dockable</code> becomes
     * visible, just that it is easier reachable without the need to change
     * modes of any <code>Dockable</code>s.<br>
     * This method returns immediately if in {@link #isLayouting() layouting mode}
     * @param dockable the element which should not be hidden
     */
    public void ensureNotHidden( final Dockable dockable ){
    	if( isLayouting() )
    		return;
    	
    	runTransaction( new Runnable() {
			public void run(){
		    	for( LocationMode mode : modes() ){
		    		mode.ensureNotHidden( dockable );
		    	}	
			}
		});
    }
    
    /**
     * Empty method evaluating the correct location of a {@link Dockable}. To be
     * overridden by subclasses to handle elements which have additional restrictions.
     * @param dockable the element to check
     */
    public void ensureValidLocation( Dockable dockable ){
    	// nothing
    }
    
    /**
     * Iterates through all the {@link LocationMode}s for which <code>aside</code> has stored locations,
     * and sets <code>dockable</code> as neighbor. This method does not change the actual location of <code>dockable</code>,
     * rather a call to <code>apply</code> would be necessary to update the location.<br> 
     * It is the responsibility of the caller to ensure that <code>dockable</code> can actually be placed at any
     * location where <code>aside</code> ever was.
     * @param dockable the item whose location should change
     * @param aside the item whose neighbor is set
     */
    public void setLocationAside( Dockable dockable, Dockable aside ){
    	M current = getCurrentMode( aside );
    	List<M> history = getModeHistory( aside );
    	
    	for( M mode : history ){
    		Location location;
    		if( mode == current ){
    			location = mode.current( aside );
    		}
    		else{
    			location = getHistory( aside, mode.getUniqueIdentifier() );
    		}
    		if( location != null ){
    			AsideRequestFactory factory = getController().getProperties().get( AsideRequest.REQUEST_FACTORY );
    			AsideRequest request = factory.createAsideRequest( location.getLocation(), dockable );
    			
    			Location result = mode.aside( request, location );
    			if( result != null ){
    				addToModeHistory( dockable, mode, result );
    			}
    			
    		}
    	}
    }
    
    @Override
    public DockActionSource getSharedActions( DockStation station ){
    	Dockable selected = station.getFrontDockable();
    	if( selected == null ){
    		return null;
    	}
    	
    	LocationMode mode = getCurrentMode( selected );
    	if( mode == null ){
    		return null;
    	}
    	
    	MultiDockActionSource result = new MultiDockActionSource();
    	
    	for( LocationMode other : modes() ){
    		if( behavior.shouldForwardActions( this, station, selected, other.getExtendedMode() ) ){
    			DockActionSource source = other.getActionsFor( selected, mode );
    			if( source != null ){
    				result.add( source );
    			}
    		}
    	}
    	
    	return result;
    }
	
	/**
	 * Adds and removes listeners from {@link LocationMode}s according to the map
	 * {@link LocationModeManager#listeners}.
	 * @author Benjamin Sigg
	 */
	private class LocationModeListenerAdapter implements ModeManagerListener<Location, LocationMode>{
		public void modeAdded(	ModeManager<? extends Location, ? extends LocationMode> manager, LocationMode mode ){
			mode.setManager( LocationModeManager.this );
			mode.setController( getController() );
			
			List<LocationModeListener> list = listeners.get( mode.getUniqueIdentifier() );
			if( list != null ){
				for( LocationModeListener listener : list ){
					mode.addLocationModeListener( listener );
				}
			}
		}
		
		public void modeRemoved( ModeManager<? extends Location, ? extends LocationMode> manager, LocationMode mode ){
			mode.setManager( null );
			mode.setController( null );
			
			List<LocationModeListener> list = listeners.get( mode.getUniqueIdentifier() );
			if( list != null ){
				for( LocationModeListener listener : list ){
					mode.removeLocationModeListener( listener );
				}
			}
		}
		
		public void dockableAdded( ModeManager<? extends Location, ? extends LocationMode> manager, Dockable dockable ){
			// ignore
		}

		public void dockableRemoved( ModeManager<? extends Location, ? extends LocationMode> manager, Dockable dockable ){
			// ignore
		}

		public void modeChanged( ModeManager<? extends Location, ? extends LocationMode> manager, Dockable dockable, LocationMode oldMode, LocationMode newMode ){
			// ignore	
		}		
	}

	/**
	 * If the {@link DockRegister} is currently stalled, then a call to {@link LocationModeManager#refresh(Dockable, boolean)}
	 * is scheduled, otherwise the call is performed directly.
	 * @param dockable the dockable which should be forwarded to {@link LocationModeManager#refresh(Dockable, boolean)}
	 */
	public void delayedRefresh( Dockable dockable ){
		if( getController().getRegister().isStalled() ){
			pendingRefreshs.add( dockable );
		}
		else{
			refresh( dockable, true );
		}
	}
	
	/**
	 * This listener registers when {@link Dockable}s enter and leave and adds or
	 * removes a {@link DockHierarchyListener}. 
	 * @author Benjamin Sigg
	 */
	private class RegisterListener extends DockRegisterAdapter{
		private DockController controller;
		
		public void connect( DockController controller ){
			if( this.controller != null ){
				DockRegister register = this.controller.getRegister();
				register.removeDockRegisterListener( this );
				for( Dockable dockable : register.listDockables() ){
					dockable.removeDockHierarchyListener( hierarchyListener );
					rebuild( dockable );
				}
			}
			this.controller = controller;
			if( controller != null ){
				DockRegister register = controller.getRegister();
				register.addDockRegisterListener( this );
				for( Dockable dockable : register.listDockables() ){
					dockable.addDockHierarchyListener( hierarchyListener );
				}
			}
		}
		
		@Override
		public void dockableRegistered( DockController controller, Dockable dockable ){
			dockable.addDockHierarchyListener( hierarchyListener );
			rebuild( dockable );
		}
		
		@Override
		public void dockableUnregistered( DockController controller, Dockable dockable ){
			dockable.removeDockHierarchyListener( hierarchyListener );
		}
		
		@Override
		public void registerUnstalled( DockController controller ){
			while( pendingRefreshs.size() > 0 && !controller.getRegister().isStalled() ){
				Iterator<Dockable> iter = pendingRefreshs.iterator();
				Dockable next = iter.next();
				iter.remove();
				refresh( next, true );
			}
		}
	}
	
	/**
	 * Reacts on dockables that are changing their position by calling
	 * {@link LocationModeManager#refresh(Dockable, boolean)}.
	 * @author Benjamin Sigg
	 */
	private class HierarchyListener implements DockHierarchyListener{
		public void controllerChanged( DockHierarchyEvent event ){
			// ignore
		}

		public void hierarchyChanged( DockHierarchyEvent event ){
            if( !isOnTransaction() ){
            	delayedRefresh( event.getDockable() );
            }	
		}		
	}
	
	/**
	 * Detects the drag-operation and calls {@link LocationModeManager#store(Dockable)}.
	 * @author Benjamin Sigg
	 */
	private class RelocatorListener extends VetoableDockRelocatorAdapter{
		@Override
		public void dragging( DockRelocatorEvent event ){
			store( event.getDockable() );
			for( Dockable dockable : event.getImplicitDockables() ){
				store( dockable );
			}
        }
	}
}
