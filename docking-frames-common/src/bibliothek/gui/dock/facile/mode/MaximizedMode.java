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

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.KeyStroke;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DockElement;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.action.predefined.CMaximizeAction;
import bibliothek.gui.dock.common.mode.CLocationModeManager;
import bibliothek.gui.dock.common.mode.ExtendedMode;
import bibliothek.gui.dock.event.DockRegisterAdapter;
import bibliothek.gui.dock.event.KeyboardListener;
import bibliothek.gui.dock.facile.mode.action.MaximizedModeAction;
import bibliothek.gui.dock.layout.DockableProperty;
import bibliothek.gui.dock.support.mode.AffectedSet;
import bibliothek.gui.dock.support.mode.AffectingRunnable;
import bibliothek.gui.dock.support.mode.Mode;
import bibliothek.gui.dock.support.mode.ModeManager;
import bibliothek.gui.dock.support.mode.ModeManagerListener;
import bibliothek.gui.dock.support.mode.ModeSetting;
import bibliothek.gui.dock.support.mode.ModeSettingFactory;
import bibliothek.gui.dock.util.DockProperties;
import bibliothek.gui.dock.util.DockUtilities;
import bibliothek.gui.dock.util.IconManager;
import bibliothek.gui.dock.util.PropertyValue;
import bibliothek.util.Path;

/**
 * {@link Dockable}s are maximized if they take up the whole space a frame
 * or a screen offers.
 * @author Benjamin Sigg
 * @param <M> the kind of areas this mode handles
 */
public class MaximizedMode<M extends MaximizedModeArea> extends AbstractLocationMode<M>{
	/** unique identifier for this mode */
	public static final Path IDENTIFIER = new Path( "dock.mode.maximized" );

	/** the key used for the {@link IconManager} to read the {@link javax.swing.Icon} for the "maximize"-action */
	public static final String ICON_IDENTIFIER = CLocationModeManager.ICON_MANAGER_KEY_MAXIMIZE;

	/** the mode in which some dockable with id=key was before maximizing */
	private Map<String, Path> lastMaximizedMode = new HashMap<String, Path>();

	/** the location some dockable had before maximizing */
	private Map<String, Location> lastMaximizedLocation = new HashMap<String, Location>();

	/** the listener responsible for detecting apply-events on other modes */
	private Listener listener = new Listener();

	/** all the {@link KeyHook}s that are currently used */
	private List<KeyHook> hooks = new LinkedList<KeyHook>();

	/**
	 * {@link KeyStroke} used to go into, or go out from the maximized state.
	 */
	private PropertyValue<KeyStroke> keyStrokeMaximizeChange = new PropertyValue<KeyStroke>( CControl.KEY_MAXIMIZE_CHANGE ){
		@Override
		protected void valueChanged( KeyStroke oldValue, KeyStroke newValue ) {
			// ignore
		}
	};

	/**
	 * Empty default constructor. Subclasses should call 
	 * {@link #setActionProvider(LocationModeActionProvider)} to complete
	 * initialization of this mode.
	 */
	protected MaximizedMode(){
		// nothing
	}

	/**
	 * Creates a new mode
	 * @param control the control in whose realm this mode will work
	 */
	public MaximizedMode( CControl control ){
		setActionProvider( new DefaultLocationModeActionProvider( new CMaximizeAction( control ) ) );
	}

	/**
	 * Creates a new mode.
	 * @param controller the owner of this mode
	 */
	public MaximizedMode( DockController controller ){
		setActionProvider( new DefaultLocationModeActionProvider( new MaximizedModeAction( controller, this ) ) );
	}

	@Override
	public void setManager( LocationModeManager<?> manager ){
		for( KeyHook hook : hooks ){
			hook.destroy( false );
		}
		hooks.clear();

		LocationModeManager<?> old = getManager();
		listener.replaceManager( old, manager );

		if( manager == null )
			keyStrokeMaximizeChange.setProperties( (DockProperties)null );
		else
			keyStrokeMaximizeChange.setProperties( manager.getController() );

		super.setManager( manager );
	}

	public Path getUniqueIdentifier(){
		return IDENTIFIER;
	}

	public ExtendedMode getExtendedMode(){
		return ExtendedMode.MAXIMIZED;
	}

	public boolean runApply( Dockable dockable, Location history, AffectedSet set ){
		MaximizedModeArea area = getMaximizeArea( dockable, history );
		
		if( area == null )
			area = getDefaultArea();
		
		area.prepareApply( dockable, history, set );
		return maximize( area, dockable, history, set );
	}

	public Location current( Dockable dockable ){
		MaximizedModeArea area = get( dockable );
		if( area == null )
			return null;
		
		DockableProperty location = area.getLocation( dockable );
		return new Location( getUniqueIdentifier(), area.getUniqueId(), location, false );
	}

	public boolean isCurrentMode( Dockable dockable ){
		for( MaximizedModeArea area : this ){
			if( area.isChild( dockable ) )
				return true;
		}
		return false;
	}

	public boolean isDefaultMode( Dockable dockable ){
		return false;
	}
	
	
	@Override
	public boolean isRepresenting( DockStation station ){
		if( super.isRepresenting( station )){
			return true;
		}
		
		Dockable dockable = station.asDockable();
		if( dockable == null ){
			return false;
		}
		
		for( MaximizedModeArea area : this ){
			if( area.isChild( dockable ) ){
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Assuming <code>dockable</code> is a maximized element, tells which
	 * mode would be the preferred mode for unmaximization.
	 * @param dockable some child
	 * @return the preferred unmaximized mode, can be <code>null</code>
	 */
	public LocationMode getUnmaximizedMode( Dockable dockable ){
		while( dockable != null ){
			for( MaximizedModeArea area : this ){
				if( area.isChild( dockable ) ){
					return area.getUnmaximizedMode();
				}
			}
			DockStation parent = dockable.getDockParent();
			dockable = parent == null ? null : parent.asDockable();
		}
		return null;
	}

	/**
	 * Ensures that <code>dockable</code> is maximized.
	 * @param area the future parent of <code>dockable</code>, can be <code>null</code>
	 * @param dockable the element that should be made maximized
	 * @param set a set of <code>Dockable</code>s which will be filled by the
	 * elements that change their mode because of this method
	 */
	public void maximize( MaximizedModeArea area, Dockable dockable, AffectedSet set ){
		maximize( area, dockable, null, set );
	}

	/**
	 * Ensures that <code>dockable</code> is maximized.
	 * @param area the future parent of <code>dockable</code>, can be <code>null</code>
	 * @param dockable the element that should be made maximized
	 * @param history the expected location of <code>dockable</code> after this method has finished, can be <code>null</code>.
	 * No guarantees are given that the final location matches <code>history</code>.
	 * @param set a set of <code>Dockable</code>s which will be filled by the
	 * elements that change their mode because of this method
	 * @return whether the operation was a success
	 */
	public boolean maximize( MaximizedModeArea area, Dockable dockable, Location history, AffectedSet set ){
		Dockable maximizing = getMaximizingElement( dockable );
		if( maximizing != dockable )
			getManager().store( maximizing );

		if( area == null )
			area = getMaximizeArea( maximizing );

		if( area == null )
			area = getDefaultArea();

		String id = getManager().getKey( maximizing );
		LocationMode current = getManager().getCurrentMode( maximizing );

		if( id == null && current == null ){
			throw new IllegalStateException( "an unidentified dockable without location has been found, all dockables except true root-station must have a location, true root-stations can never be used in this method." );
		}
		
		if( id == null && current != null ){
			lastMaximizedLocation.put( area.getUniqueId(), current.current( maximizing ) );
			lastMaximizedMode.put( area.getUniqueId(), current.getUniqueIdentifier() );
		}
		else{
			getManager().store( dockable );
		}

		List<Dockable> oldMaximized = getMaximized( area );
		area.setMaximized( maximizing, true, history, set );
		
		if( !(id == null && current == null )){
			for( Dockable newMaximized : area.getMaximized() ){
				if( newMaximized != maximizing ){
					if( DockUtilities.isAncestor( newMaximized, maximizing )){
						// the maximizing element was put on a DockStation
						if( !oldMaximized.contains( newMaximized )){
							// and the DockStation was created by this action
							for( Dockable replaced : oldMaximized ){
								if( DockUtilities.isAncestor( newMaximized, replaced )){
									storeLastMaximizedLocation( area, replaced );
									break;
								}
							}
						}
					}
				}
			}
		}
		
		set.add( maximizing );
		return true;
	}
	
	private List<Dockable> getMaximized( MaximizedModeArea area ){
		Dockable[] children = area.getMaximized();
		if( children == null ){
			return Collections.emptyList();
		}
		else{
			return Arrays.asList( children );
		}
	}
	
	private void storeLastMaximizedLocation( MaximizedModeArea area, Dockable dockable ){
		LocationMode previousMode = getManager().getPreviousMode( dockable );
		if( previousMode != null ){
			Location previousLocation = getManager().getHistory( dockable, previousMode.getUniqueIdentifier() );
			if( previousLocation != null ){
				lastMaximizedLocation.put( area.getUniqueId(), previousLocation );
				lastMaximizedMode.put( area.getUniqueId(), previousMode.getUniqueIdentifier() );
			}
		}
	}

	/**
	 * Ensures that <code>dockable</code> is not maximized. Does nothing if the parent
	 * {@link MaximizedModeArea} of <code>dockable</code> has not maximized <code>dockable</code>
	 * or if the {@link LocationModeManager} does not know <code>dockable</code>.
	 * @param dockable the element that might be maximized currently
	 * @param set a set of <code>Dockable</code>s which will be filled by the
	 * elements that change their mode because of this method
	 */
	public void unmaximize( Dockable dockable, AffectedSet set ){
		final MaximizedModeArea area = getMaximizeArea( dockable );
		if( area != null && area.getMaximized() != null ){
			Dockable[] maximized = area.getMaximized();
			if( maximized != null ){
				for( Dockable check : maximized ){
					if( DockUtilities.isAncestor( check, dockable )){
						set.add( dockable );
						dockable = check;
						final Dockable element = dockable;
	
						final LocationModeManager<?> manager = getManager();
						manager.runTransaction( new AffectingRunnable() {
							public void run( AffectedSet set ){
								area.setMaximized( element, false, null, set );
	
								String key = area.getUniqueId();
								boolean done = false;
	
								// try to apply the last mode
								if( lastMaximizedLocation.get( key ) != null ){
									done = getManager().apply( 
											element,
											lastMaximizedMode.remove( key ),
											lastMaximizedLocation.remove( key ),
											set );
								}
								
								if( !done ){
									applyOldLocation( element, set );
								}		
							}
						}, true );
	
						manager.store( dockable );
						return;
					}
				}
			}
		}
	}

	/**
	 * Recursively searches through the tree of {@link DockElement}s and applies old locations
	 * on those {@link Dockable}s which are known to have a location. Branches are not visited
	 * if the a parent element has been found with an old location.
	 * @param element the root of the tree to search through
	 * @param set a set to be filled with all the {@link Dockable}s whose location changed.
	 */
	private void applyOldLocation( Dockable element, AffectedSet set ){
		LocationModeManager<?> manager = getManager();
		if( manager.isRegistered( element ) ){
			LocationMode mode = manager.getPreviousMode( element );
			if( mode == null || mode == MaximizedMode.this )
				mode = manager.getMode( NormalMode.IDENTIFIER );
			
			manager.apply( element, mode.getUniqueIdentifier(), set, true );
		}
		else if( element.asDockStation() != null ){
			DockStation station = element.asDockStation();
			Dockable[] children = new Dockable[ station.getDockableCount() ];
			for( int i = 0; i < children.length; i++ ){
				children[i] = station.getDockable( i );
			}
			for( Dockable child : children ){
				applyOldLocation( child, set );
			}
		}
	}
	
	/**
	 * Searches the {@link MaximizedModeArea} which either represents
	 * <code>station</code> or its nearest parent.
	 * @param station some station
	 * @return the nearest area or <code>null</code>
	 */
	public MaximizedModeArea getNextMaximizeArea( DockStation station ){
		while( station != null ){
			MaximizedModeArea area = getMaximizeArea( station );
			if( area != null ){
				return area;
				
			}

			Dockable dockable = station.asDockable();
			if( dockable == null )
				return null;

			station = dockable.getDockParent();
		}	
		return null;
	}
	
	/**
	 * Ensures that either the {@link MaximizedModeArea} <code>station</code> or its
	 * nearest parent does not show a maximized element.
	 * @param station an area or a child of an area
	 * @param affected elements whose mode changes will be added to this set
	 */
	public void unmaximize( DockStation station, AffectedSet affected ){
		MaximizedModeArea area = getNextMaximizeArea( station );
		if( area != null ){
			Dockable[] dockables = area.getMaximized();
			if( dockables != null ){
				for( Dockable dockable : dockables ){
					unmaximize( dockable, affected );
				}
			}
		}
	}

	/**
	 * Ensures that <code>area</code> has no maximized child.
	 * @param area some area
	 * @param affected the element whose mode might change
	 */
	public void unmaximize( MaximizedModeArea area, AffectedSet affected ){
		Dockable[] dockables = area.getMaximized();
		if( dockables != null ){
			for( Dockable dockable : dockables ){
				unmaximize( dockable, affected );
			}
		}
	}

	public void ensureNotHidden( final Dockable dockable ){
		getManager().runTransaction( new AffectingRunnable() {
			public void run( AffectedSet set ){
				Dockable mutableDockable = dockable;

				DockStation parent = mutableDockable.getDockParent();
				Dockable element = getMaximizingElement( mutableDockable );

				while( parent != null ){
					MaximizedModeArea area = getMaximizeArea( parent );
					if( area != null ){
						Dockable[] maximized = area.getMaximized();
						if( maximized != null ){
							for( Dockable check : maximized ){
								if( maximized != null && check != mutableDockable && check != element ){
									unmaximize( check, set );
								}	
							}
						}
					}

					mutableDockable = parent.asDockable();
					parent = mutableDockable == null ? null : mutableDockable.getDockParent();
				}	
			}
		});
	}
	
	/**
	 * Gets the area to which <code>dockable</code> should be maximized. This can be 
	 * {@link #getMaximizeArea(Dockable)}, or some other station.
	 * @param dockable the element that is maximized
	 * @param history the history of the last place where <code>dockable</code> was maximized, might be <code>null</code>
	 * @return the preferred area to maximize <code>dockable</code>
	 */
	public MaximizedModeArea getMaximizeArea( Dockable dockable, Location history ){
		return getMaximizeArea( dockable );
	}

	/**
	 * Searches the first {@link MaximizedModeArea} which is a parent
	 * of <code>dockable</code>. This method will never return
	 * <code>dockable</code> itself.
	 * @param dockable the element whose maximize area is searched
	 * @return the area or <code>null</code>
	 */
	public MaximizedModeArea getMaximizeArea( Dockable dockable ){
		DockStation parent = dockable.getDockParent();
		while( parent != null ){
			MaximizedModeArea area = getMaximizeArea( parent );
			if( area != null )
				return area;

			dockable = parent.asDockable();
			if( dockable == null ){
				parent = null;
			}
			else{
				parent = dockable.getDockParent();
			}
		}
		return null;
	}

	/**
	 * Searches the one {@link MaximizedModeArea} whose station is
	 * <code>station</code>.
	 * @param station the station whose area is searched
	 * @return the area or <code>null</code> if not found
	 */
	public MaximizedModeArea getMaximizeArea( DockStation station ){
		for( MaximizedModeArea area : this ){
			if( area.isRepresenting( station ) ){
				return area;
			}
		}
		return null;
	}

	/**
	 * Gets the element which must be maximized when the user requests that
	 * <code>dockable</code> is maximized.
	 * @param dockable some element, not <code>null</code>
	 * @return the element that must be maximized, might be <code>dockable</code>
	 * itself, not <code>null</code>
	 */
	public Dockable getMaximizingElement( Dockable dockable ){
		return getManager().getGroupBehavior().getGroupElement( getManager(), dockable, getExtendedMode() );
	}

	/**
	 * Gets the element which would be maximized if <code>old</code> is currently
	 * maximized, and <code>dockable</code> is or will not be maximized.
	 * @param old some element
	 * @param dockable some element, might be <code>old</code>
	 * @return the element which would be maximized if <code>dockable</code> is
	 * no longer maximized, can be <code>null</code>
	 */
	public Dockable getMaximizingElement( Dockable old, Dockable dockable ){
		return getManager().getGroupBehavior().getReplaceElement( getManager(), old, dockable, getExtendedMode() );
	}

	protected void applyStarting( LocationModeEvent event ){
		List<Runnable> runs = new ArrayList<Runnable>();

		for( MaximizedModeArea area : this ){
			Runnable run = area.onApply( event );
			if( run != null ){
				runs.add( run );
			}
		}

		Dockable dockable = event.getDockable();

		final MaximizedModeArea maxiarea = getMaximizeArea( dockable );
		if( maxiarea == null )
			return;

		Dockable[] maximizedNow = maxiarea.getMaximized();
		if( maximizedNow == null )
			return;

		Dockable maximized = null;
		
		for( int i = 0; i < maximizedNow.length; i++ ){
			if( DockUtilities.isAncestor( maximizedNow[i], dockable  )){
				maximized = getMaximizingElement( maximizedNow[i], dockable );
				break;
			}
		}
		
		Runnable run = maxiarea.onApply( event, maximized );
		if( run != null ){
			runs.add( run );
		}
		if( !runs.isEmpty() ){
			event.setClientObject( listener, runs );	
		}
	}

	@SuppressWarnings("unchecked")
	protected void applyDone( LocationModeEvent event ){
		List<Runnable> runs = (List<Runnable>)event.getClientObject( listener );
		if( runs != null ){
			for( Runnable run : runs ){
				run.run();
			}
		}
	}

	public ModeSettingFactory<Location> getSettingFactory(){
		return MaximizedModeSetting.FACTORY;
	}

	public void writeSetting( ModeSetting<Location> setting ){
		if( setting instanceof MaximizedModeSetting ){
			MaximizedModeSetting modeSetting = (MaximizedModeSetting)setting;
			modeSetting.setLastMaximizedLocation( lastMaximizedLocation );
			modeSetting.setLastMaximizedMode( lastMaximizedMode );
		}
	}

	public void readSetting( ModeSetting<Location> setting ){
		if( setting instanceof MaximizedModeSetting ){
			MaximizedModeSetting modeSetting = (MaximizedModeSetting)setting;
			lastMaximizedLocation = new HashMap<String, Location>( modeSetting.getLastMaximizedLocation() );
			lastMaximizedMode = new HashMap<String, Path>( modeSetting.getLastMaximizedMode() );
		}
	}


	/**
	 * Invoked whenever a key is pressed, released or typed.
	 * @param dockable the element to which the event belongs
	 * @param event the event
	 * @return <code>true</code> if the event has been processed, <code>false</code>
	 * if the event was not used up.
	 */
	protected boolean process( Dockable dockable, KeyEvent event ){
		KeyStroke stroke = KeyStroke.getKeyStrokeForEvent( event );
		if( stroke.equals( keyStrokeMaximizeChange.getValue() )){
			return switchMode( dockable );
		}

		return false;
	}

	/**
	 * Tries to switch the current mode of <code>dockable</code> to or from
	 * the maximized mode.
	 * @param dockable the element whose mode is to be changed
	 * @return whether the operation was successful
	 */
	public boolean switchMode( Dockable dockable ){
		LocationModeManager<?> manager = getManager();
		LocationMode current = manager.getCurrentMode( dockable );
		if( current == this ){
			LocationMode mode = manager.getPreviousMode( dockable );
			if( mode != null ){
				if( manager.isModeAvailable( dockable, mode.getExtendedMode() )){
					manager.setMode( dockable, mode.getExtendedMode() );
					manager.ensureValidLocation( dockable );
					return true;
				}
			}
		}
		else{
			if( manager.isModeAvailable( dockable, getExtendedMode() )){
				manager.setMode( dockable, getExtendedMode() );
				manager.ensureValidLocation( dockable );
				return true;
			}
		}
		return false;
	}

	
	/**
	 * A hook recording key-events for a specific {@link Dockable}. The hook will remove
	 * itself from the {@link Dockable} automatically once the element is removed from
	 * the controller.
	 * @author Benjamin Sigg
	 */
	private class KeyHook extends DockRegisterAdapter implements KeyboardListener{
		/** the Dockable which is observed by this hook */
		private Dockable dockable;

		/** the controller on which this hook has registered its listeners */
		private DockController controller;

		/**
		 * Creates a new hook
		 * @param dockable the element which will be observed until it is removed
		 * from the {@link DockController}.
		 */
		public KeyHook( Dockable dockable ){
			this.dockable = dockable;
			controller = getController();
			controller.getKeyboardController().addListener( this );
			controller.getRegister().addDockRegisterListener( this );
			hooks.add( this );
		}

		@Override
		public void dockableUnregistered( DockController controller, Dockable dockable ) {
			if( this.dockable == dockable ){
				destroy( true );
			}
		}

		/**
		 * Removes this hook from the controller
		 * @param complete whether to remove <code>this</code> from {@link MaximizedMode#hooks}
		 */
		public void destroy( boolean complete ){
			controller.getKeyboardController().removeListener( this );
			controller.getRegister().removeDockRegisterListener( this );
			if( complete ){
				hooks.remove( this );
			}
		}

		public DockElement getTreeLocation() {
			return dockable;
		}

		public boolean keyPressed( DockElement element, KeyEvent event ) {
			return process( dockable, event );
		}

		public boolean keyReleased( DockElement element, KeyEvent event ) {
			return process( dockable, event );
		}

		public boolean keyTyped( DockElement element, KeyEvent event ) {
			return process( dockable, event );
		}
	}

	/**
	 * A listener that adds itself to all {@link LocationMode}s a {@link LocationModeManager} has.
	 * Calls to the {@link Mode#apply(Dockable, Object, AffectedSet) apply} method is forwarded
	 * to the enclosing {@link MaximizedMode}.
	 * @author Benjamin Sigg
	 */
	private class Listener implements ModeManagerListener<Location, LocationMode>, LocationModeListener {
		/**
		 * Removes this listener from <code>oldManager</code> and adds this to <code>newManager</code>.
		 * @param oldManager the old manager, can be <code>null</code>
		 * @param newManager the new manager, can be <code>null</code>
		 */
		public void replaceManager( LocationModeManager<?> oldManager, LocationModeManager<?> newManager ){
			if( oldManager != null ){
				oldManager.removeModeManagerListener( this );

				for( LocationMode mode : oldManager.modes() ){
					modeRemoved( oldManager, mode );
				}
			}

			if( newManager != null ){
				newManager.addModeManagerListener( this );

				for( LocationMode mode : newManager.modes() ){
					modeAdded( newManager, mode );
				}

				for( Dockable dockable : newManager.listDockables() ){
					new KeyHook( dockable );
				}
			}
		}

		public void dockableAdded( ModeManager<? extends Location, ? extends LocationMode> manager, Dockable dockable ){
			new KeyHook( dockable );
		}

		public void dockableRemoved( ModeManager<? extends Location, ? extends LocationMode> manager, Dockable dockable ){
			// ignore
		}

		public void modeAdded( ModeManager<? extends Location, ? extends LocationMode> manager, LocationMode mode ){
			if( mode != MaximizedMode.this ){
				mode.addLocationModeListener( this );
			}
		}

		public void modeChanged( ModeManager<? extends Location, ? extends LocationMode> manager, Dockable dockable, LocationMode oldMode, LocationMode newMode ){
			// ignore
		}

		public void modeRemoved( ModeManager<? extends Location, ? extends LocationMode> manager, LocationMode mode ){
			mode.removeLocationModeListener( this );
		}

		public void applyDone( LocationModeEvent event ){
			MaximizedMode.this.applyDone( event );
		}

		public void applyStarting( LocationModeEvent event ){
			MaximizedMode.this.applyStarting( event );
		}
	}
}
