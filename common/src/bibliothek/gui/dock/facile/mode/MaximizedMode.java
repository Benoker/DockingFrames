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
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.swing.KeyStroke;

import bibliothek.extension.gui.dock.util.Path;
import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DockElement;
import bibliothek.gui.dock.StackDockStation;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.CMaximizeBehavior;
import bibliothek.gui.dock.common.action.predefined.CMaximizeAction;
import bibliothek.gui.dock.common.mode.ExtendedMode;
import bibliothek.gui.dock.event.DockRegisterAdapter;
import bibliothek.gui.dock.event.DoubleClickListener;
import bibliothek.gui.dock.event.KeyboardListener;
import bibliothek.gui.dock.facile.mode.action.MaximizedModeAction;
import bibliothek.gui.dock.facile.state.MaximizeArea;
import bibliothek.gui.dock.support.mode.AffectedSet;
import bibliothek.gui.dock.support.mode.AffectingRunnable;
import bibliothek.gui.dock.support.mode.ModeManager;
import bibliothek.gui.dock.support.mode.ModeManagerListener;
import bibliothek.gui.dock.support.mode.ModeSetting;
import bibliothek.gui.dock.support.mode.ModeSettingFactory;
import bibliothek.gui.dock.support.util.Resources;
import bibliothek.gui.dock.util.DockProperties;
import bibliothek.gui.dock.util.IconManager;
import bibliothek.gui.dock.util.PropertyValue;

/**
 * {@link Dockable}s are maximized if they take up the whole space a frame
 * or a screen offers.
 * @author Benjamin Sigg
 */
public class MaximizedMode<M extends MaximizedModeArea> extends AbstractLocationMode<M>{
	/** unique identifier for this mode */
	public static final Path IDENTIFIER = new Path( "dock.mode.maximized" );
	
    /** the key used for the {@link IconManager} to read the {@link javax.swing.Icon} for the "maximize"-action */
    public static final String ICON_IDENTIFIER = "location.maximize";

	/** when to maximize what */
	private CMaximizeBehavior maximizeBehavior = CMaximizeBehavior.STACKED;
	
	/** the mode in which some dockable with id=key was before maximizing */
	private HashMap<String, Path> lastMaximizedMode = new HashMap<String, Path>();
	
	/** the location some dockable had before maximizing */
	private HashMap<String, Location> lastMaximizedLocation = new HashMap<String, Location>();
	
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
	 * Creates a new mode
	 * @param control the control in whose realm this mode will work
	 */
	public MaximizedMode( CControl control ){
		setSelectModeAction( new CMaximizeAction( control ) );
	}
	
	/**
	 * Creates a new mode.
	 * @param controller the owner of this mode
	 */
	public MaximizedMode( DockController controller ){
		IconManager icons = controller.getIcons();
        icons.setIconDefault( "maximize", Resources.getIcon( "maximize" ) );
        
		setSelectModeAction( new MaximizedModeAction( controller, this ) );
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
	
	@Override
	public void add( M area ){
		super.add( area );
		area.connect( this );
	}
	
	@Override
	public M remove( String key ){
		M area = super.remove( key );
		if( area != null ){
			area.connect( null );
		}
		return area;
	}
	
	/**
	 * Sets the maximize behavior which determines what {@link Dockable} to 
	 * maximize when hitting the maximize-button.<br>
	 * Note: Changing the behavior if dockables are already shown can lead
	 * to an unspecified behavior.
	 * @param maximizeBehavior the behavior, not <code>null</code>
	 */
	public void setMaximizeBehavior( CMaximizeBehavior maximizeBehavior ){
		if( maximizeBehavior == null )
			throw new IllegalArgumentException( "maximizeBehavior must not be null" );
		this.maximizeBehavior = maximizeBehavior;
	}

	/**
	 * Gets the maximize behavior.
	 * @return the behavior, not <code>null</code>
	 * @see #setMaximizeBehavior(CMaximizeBehavior)
	 */
	public CMaximizeBehavior getMaximizeBehavior(){
		return maximizeBehavior;
	}
	
	public Path getUniqueIdentifier(){
		return IDENTIFIER;
	}

	public ExtendedMode getExtendedMode(){
		return ExtendedMode.MAXIMIZED;
	}
	
	public void runApply( Dockable dockable, Location history, AffectedSet set ){
		MaximizedModeArea area = null;
		if( history != null )
			area = get( history.getRoot() );
		if( area == null )
			area = getDefaultArea();
		
		area.prepareApply( dockable, set );
		maximize( area, dockable, set );
	}

	public Location current( Dockable dockable ){
		MaximizedModeArea area = get( dockable );
		if( area == null )
			return null;

		return new Location( getUniqueIdentifier(), area.getUniqueId(), null );
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

    /**
     * Ensures that <code>dockable</code> is maximized.
     * @param area the future parent of <code>dockable</code>, can be <code>null</code>
     * @param dockable the element that should be made maximized
     * @param set a set of <code>Dockable</code>s which will be filled by the
     * elements that change their mode because of this method
     */    
    public void maximize( MaximizedModeArea area, Dockable dockable, AffectedSet set ){
        Dockable maximizing = getMaximizingElement( dockable );
        if( maximizing != dockable )
            getManager().store( maximizing );

        if( area == null )
            area = getMaximizeArea( maximizing );

        if( area == null )
            area = getDefaultArea();

        String id = getManager().getKey( maximizing );
        LocationMode current = getManager().getCurrentMode( dockable );
        
        if( id == null && current != null ){
            lastMaximizedLocation.put( area.getUniqueId(), current.current( dockable ) );
            lastMaximizedMode.put( area.getUniqueId(), current.getUniqueIdentifier() );
        }
        else{
            lastMaximizedLocation.remove( area.getUniqueId() );
            lastMaximizedMode.remove( area.getUniqueId() );
        }

        area.setMaximized( maximizing, set );
        set.add( maximizing );
    }
	
    /**
     * Ensures that <code>dockable</code> is not maximized.
     * @param dockable the element that might be maximized currently
     * @param set a set of <code>Dockable</code>s which will be filled by the
     * elements that change their mode because of this method
     */
    public void unmaximize( Dockable dockable, AffectedSet set ){
        MaximizedModeArea area = getMaximizeArea( dockable );
        if( area != null && area.getMaximized() != null ){
            set.add( dockable );

            dockable = area.getMaximized();
            area.setMaximized( null, set );

            String key = area.getUniqueId();
            boolean done = false;
            LocationModeManager<?> manager = getManager();
            
            if( lastMaximizedLocation.get( key ) != null ){
            	done = true;
            	done = getManager().apply( 
            		dockable,
            		lastMaximizedMode.remove( key ),
            		lastMaximizedLocation.remove( key ),
            		set );
            }
            
            if( !done ){
            	LocationMode mode = manager.getPreviousMode( dockable );
            	if( mode == null || mode == this )
            		mode = manager.getMode( NormalMode.IDENTIFIER );
                
                manager.apply( dockable, mode.getUniqueIdentifier(), set );
            }
        }
    }
    
    /**
     * Ensures that either the {@link MaximizeArea} <code>station</code> or its
     * nearest parent does not show a maximized element.
     * @param station an area or a child of an area
     * @param affected elements whose mode changes will be added to this set
     */
    public void unmaximize( DockStation station, AffectedSet affected ){
        while( station != null ){
            MaximizedModeArea area = getMaximizeArea( station );
            if( area != null ){
                Dockable dockable = area.getMaximized();
                if( dockable != null ){
                    unmaximize( dockable, affected );
                    return;
                }
            }

            Dockable dockable = station.asDockable();
            if( dockable == null )
                return;

            station = dockable.getDockParent();
        }
    }
    
    /**
     * Ensures that <code>area</code> has no maximized child.
     * @param area some area
     * @param affected the element whose mode might change
     */
    public void unmaximize( MaximizedModeArea area, AffectedSet affected ){
    	Dockable dockable = area.getMaximized();
    	if( dockable != null ){
    		unmaximize( dockable, affected );
    		return;
    	}
    }

    public void ensureNotHidden( final Dockable dockable ){
    	getManager().run( new AffectingRunnable() {
			public void run( AffectedSet set ){
				Dockable mutableDockable = dockable;
				
				DockStation parent = mutableDockable.getDockParent();
		    	Dockable element = getMaximizingElement( mutableDockable );
		    	
				while( parent != null ){
		    		MaximizedModeArea area = getMaximizeArea( parent );
		    		if( area != null ){
		    			Dockable maximized = area.getMaximized();
		    			
		    			if( maximized != null && maximized != mutableDockable && maximized != element ){
		    				unmaximize( area.getMaximized(), set );
		    			}
		    		}

		    		mutableDockable = parent.asDockable();
		    		parent = mutableDockable == null ? null : mutableDockable.getDockParent();
		    	}	
			}
		});
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
     * Searches the one {@link MaximizeArea} whose station is
     * <code>station</code>.
     * @param station the station whose area is searched
     * @return the area or <code>null</code> if not found
     */
    protected MaximizedModeArea getMaximizeArea( DockStation station ){
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

    protected void applyStarting( LocationModeEvent event ){
    	Dockable dockable = event.getDockable();
    	
		final MaximizedModeArea maxiarea = getMaximizeArea( dockable );
		if( maxiarea == null )
			return;
		
		Dockable maximizedNow = maxiarea.getMaximized();
		if( maximizedNow == null )
			return;
		
		Dockable maximized = maximizedNow == null ? null : getMaximizingElement( maximizedNow, dockable );
		
		Runnable run = maxiarea.onApply( event, maximized );
		event.setClientObject( listener, run );
    }
    
    protected void applyDone( LocationModeEvent event ){
    	Runnable run = (Runnable)event.getClientObject( listener );
    	if( run != null ){
    		run.run();
    	}
    }
    
    public ModeSettingFactory<Location> getSettingFactory(){
    	return MaximizedModeSetting.FACTORY;
    }
    
    public void writeSetting( ModeSetting<Location> setting ){
	    // ignore	
    }
    
    public void readSetting( ModeSetting<Location> setting ){
    	// ignore
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
					manager.apply( dockable, mode.getUniqueIdentifier() );
					manager.ensureValidLocation( dockable );
					return true;
				}
			}
		}
		else{
			if( manager.isModeAvailable( dockable, getExtendedMode() )){
				manager.apply( dockable, getUniqueIdentifier() );
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
     * Calls to the {@link LocationMode#apply(Dockable, Location, AffectedSet) apply} method is forwarded
     * to the enclosing {@link MaximizedMode}.
     * @author Benjamin Sigg
     */
    private class Listener implements ModeManagerListener<Location, LocationMode>, LocationModeListener, DoubleClickListener{
    	/** controller to which this listener is attached */
    	private DockController controller;
    	
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
    		
    		if( controller != null ){
    			controller.getDoubleClickController().removeListener( this );
    			controller = null;
    		}
    		
    		if( newManager != null ){
    			controller = newManager.getController();
    			newManager.addModeManagerListener( this );
    			
    			for( LocationMode mode : newManager.modes() ){
    				modeAdded( newManager, mode );
    			}
    			
    			for( Dockable dockable : newManager.listDockables() ){
    				new KeyHook( dockable );
    			}
    		}
    		
    		if( controller != null ){
    			controller.getDoubleClickController().addListener( this );
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
    	
        public DockElement getTreeLocation() {
            return null;
        }
        public boolean process( Dockable dockable, MouseEvent event ) {
            if( event.isConsumed() )
                return false;
            
            if( switchMode( dockable ) ){
            	event.consume();
            	return true;
            }
            
            return false;
        }
    }
}
