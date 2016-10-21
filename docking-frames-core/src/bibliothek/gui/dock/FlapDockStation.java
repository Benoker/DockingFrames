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

package bibliothek.gui.dock;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.HierarchyBoundsListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputAdapter;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.DockTheme;
import bibliothek.gui.DockUI;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.accept.DockAcceptance;
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.action.DockActionSource;
import bibliothek.gui.dock.action.ListeningDockAction;
import bibliothek.gui.dock.component.DockComponentRootHandler;
import bibliothek.gui.dock.control.focus.DefaultFocusRequest;
import bibliothek.gui.dock.control.focus.FocusController;
import bibliothek.gui.dock.control.focus.MouseFocusObserver;
import bibliothek.gui.dock.disable.DisablingStrategy;
import bibliothek.gui.dock.disable.DisablingStrategyListener;
import bibliothek.gui.dock.displayer.DisplayerCombinerTarget;
import bibliothek.gui.dock.event.DockStationAdapter;
import bibliothek.gui.dock.event.DockableAdapter;
import bibliothek.gui.dock.event.DockableFocusEvent;
import bibliothek.gui.dock.event.DockableFocusListener;
import bibliothek.gui.dock.event.FlapDockListener;
import bibliothek.gui.dock.event.FocusVetoListener;
import bibliothek.gui.dock.layout.DockableProperty;
import bibliothek.gui.dock.layout.location.AsideRequest;
import bibliothek.gui.dock.station.AbstractDockableStation;
import bibliothek.gui.dock.station.Combiner;
import bibliothek.gui.dock.station.DisplayerCollection;
import bibliothek.gui.dock.station.DisplayerFactory;
import bibliothek.gui.dock.station.DockableDisplayer;
import bibliothek.gui.dock.station.PlaceholderMapping;
import bibliothek.gui.dock.station.StationBackgroundComponent;
import bibliothek.gui.dock.station.StationDragOperation;
import bibliothek.gui.dock.station.StationDropItem;
import bibliothek.gui.dock.station.StationDropOperation;
import bibliothek.gui.dock.station.StationPaint;
import bibliothek.gui.dock.station.flap.ButtonPane;
import bibliothek.gui.dock.station.flap.DefaultFlapLayoutManager;
import bibliothek.gui.dock.station.flap.DefaultFlapWindowFactory;
import bibliothek.gui.dock.station.flap.FlapDockHoldToggle;
import bibliothek.gui.dock.station.flap.FlapDockProperty;
import bibliothek.gui.dock.station.flap.FlapDockStationFactory;
import bibliothek.gui.dock.station.flap.FlapDockStationSource;
import bibliothek.gui.dock.station.flap.FlapDropInfo;
import bibliothek.gui.dock.station.flap.FlapLayoutManager;
import bibliothek.gui.dock.station.flap.FlapLayoutManagerListener;
import bibliothek.gui.dock.station.flap.FlapWindow;
import bibliothek.gui.dock.station.flap.FlapWindowFactory;
import bibliothek.gui.dock.station.flap.button.ButtonContent;
import bibliothek.gui.dock.station.flap.button.ButtonContentFilter;
import bibliothek.gui.dock.station.flap.button.DefaultButtonContentFilter;
import bibliothek.gui.dock.station.flap.layer.FlapOverrideDropLayer;
import bibliothek.gui.dock.station.flap.layer.FlapSideDropLayer;
import bibliothek.gui.dock.station.flap.layer.WindowDropLayer;
import bibliothek.gui.dock.station.layer.DefaultDropLayer;
import bibliothek.gui.dock.station.layer.DockStationDropLayer;
import bibliothek.gui.dock.station.support.CombinerSource;
import bibliothek.gui.dock.station.support.CombinerSourceWrapper;
import bibliothek.gui.dock.station.support.CombinerTarget;
import bibliothek.gui.dock.station.support.ConvertedPlaceholderListItem;
import bibliothek.gui.dock.station.support.DockablePlaceholderList;
import bibliothek.gui.dock.station.support.DockableShowingManager;
import bibliothek.gui.dock.station.support.Enforcement;
import bibliothek.gui.dock.station.support.PlaceholderList;
import bibliothek.gui.dock.station.support.PlaceholderList.Level;
import bibliothek.gui.dock.station.support.PlaceholderListItem;
import bibliothek.gui.dock.station.support.PlaceholderListItemAdapter;
import bibliothek.gui.dock.station.support.PlaceholderListItemConverter;
import bibliothek.gui.dock.station.support.PlaceholderListMapping;
import bibliothek.gui.dock.station.support.PlaceholderMap;
import bibliothek.gui.dock.station.support.PlaceholderStrategy;
import bibliothek.gui.dock.themes.DefaultDisplayerFactoryValue;
import bibliothek.gui.dock.themes.DefaultStationPaintValue;
import bibliothek.gui.dock.themes.StationCombinerValue;
import bibliothek.gui.dock.themes.ThemeManager;
import bibliothek.gui.dock.themes.basic.BasicButtonTitleFactory;
import bibliothek.gui.dock.title.ActivityDockTitleEvent;
import bibliothek.gui.dock.title.ControllerTitleFactory;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.gui.dock.title.DockTitleRequest;
import bibliothek.gui.dock.title.DockTitleVersion;
import bibliothek.gui.dock.util.BackgroundAlgorithm;
import bibliothek.gui.dock.util.DockProperties;
import bibliothek.gui.dock.util.DockUtilities;
import bibliothek.gui.dock.util.PropertyKey;
import bibliothek.gui.dock.util.PropertyValue;
import bibliothek.gui.dock.util.extension.Extension;
import bibliothek.gui.dock.util.property.ConstantPropertyFactory;
import bibliothek.gui.dock.util.property.DynamicPropertyFactory;
import bibliothek.util.Path;

/**
 * This {@link DockStation} shows only a title for each of it's children.<br>
 * If the user clicks on one of the titles, a window will popup. The {@link Dockable}
 * which owns the clicked title is shown in this window.
 * @author Benjamin Sigg
 */
public class FlapDockStation extends AbstractDockableStation {
    /** 
     * The direction in which the window with the <code>Dockable</code> will popup,
     * in respect to the location of this station.
     */
    public static enum Direction{ NORTH, WEST, SOUTH, EAST };
    
    /**
     * This id is used to get a {@link DockTitleVersion} from the
     * {@link DockController} which owns this station. The titles that are
     * created for this version are used on the popup-window.
     */
    public static final String WINDOW_TITLE_ID = "flap window";
    
    /**
     * This id is used to get a {@link DockTitleVersion} from the
     * {@link DockController} which owns this station. The titles that are
     * created for this version are used as buttons on this station.
     */
    public static final String BUTTON_TITLE_ID = "flap button";
    
    /**
     * This id is forwarded to {@link Extension}s which load additional {@link DisplayerFactory}s.
     */
    public static final String DISPLAYER_ID = "flap";
    
    /**
     * Key for the {@link FlapLayoutManager} that is used by all {@link FlapDockStation}s.
     */
    public static final PropertyKey<FlapLayoutManager> LAYOUT_MANAGER = new PropertyKey<FlapLayoutManager>(
            "flap dock station layout manager", 
            	new DynamicPropertyFactory<FlapLayoutManager>(){
            		public FlapLayoutManager getDefault(
            				PropertyKey<FlapLayoutManager> key,
            				DockProperties properties ){
            			return new DefaultFlapLayoutManager();
            		}
            	}, true );
    
    /**
     * Key for all {@link DockTheme}s, tells the theme what content on the buttons
     * should be visible. Note that some themes might ignore that setting. Changing this property will call
     * {@link #recreateTitles()}, meaning all {@link DockTitle}s are removed and recreated.
     */
    public static final PropertyKey<ButtonContent> BUTTON_CONTENT = new PropertyKey<ButtonContent>(
            "flap dock station button content", new ConstantPropertyFactory<ButtonContent>( ButtonContent.THEME_DEPENDENT ), true );
    
    /**
     * Key for all elements that depend from {@link #BUTTON_CONTENT}, adds additional information to the {@link ButtonContent}.
     */
    public static final PropertyKey<ButtonContentFilter> BUTTON_CONTENT_FILTER = new PropertyKey<ButtonContentFilter>( 
    		"flap dock station button content connector", new ConstantPropertyFactory<ButtonContentFilter>( new DefaultButtonContentFilter() ), true );
    
    /**
     * Key for the minimum size of all {@link FlapDockStation}s.
     */
    public static final PropertyKey<Dimension> MINIMUM_SIZE = new PropertyKey<Dimension>( "flap dock station empty size",
    		new ConstantPropertyFactory<Dimension>( new Dimension( 0, 0 ) ), true );
    
    /**
     * Key for a factory that creates the windows of this station.
     */
    public static final PropertyKey<FlapWindowFactory> WINDOW_FACTORY = new PropertyKey<FlapWindowFactory>("flap dock station window factory",
    		new ConstantPropertyFactory<FlapWindowFactory>( new DefaultFlapWindowFactory() ), true );
    

    /**
     * A listener that is added to the current {@link #layoutManager}
     */
    private FlapLayoutManagerListener layoutManagerListener = new FlapLayoutManagerListener(){
		public void holdSwitchableChanged( FlapLayoutManager manager, FlapDockStation station, Dockable dockable ){
			if( station == null || station == FlapDockStation.this ){
				updateIsHoldSwitchable( dockable );
			}
		}
	};
    
    /**
     * The layoutManager which is responsible to layout this station
     */
    private PropertyValue<FlapLayoutManager> layoutManager = new PropertyValue<FlapLayoutManager>( LAYOUT_MANAGER ){
        @Override
        protected void valueChanged( FlapLayoutManager oldValue, FlapLayoutManager newValue ) {
            if( oldValue != null ){
            	oldValue.removeListener( layoutManagerListener );
                oldValue.uninstall( FlapDockStation.this );
            }
            
            if( newValue != null ){
            	newValue.addListener( layoutManagerListener );
                newValue.install( FlapDockStation.this );
            }
        }
    };
    
    /** current {@link PlaceholderStrategy} */
    private PropertyValue<PlaceholderStrategy> placeholderStrategy = new PropertyValue<PlaceholderStrategy>(PlaceholderStrategy.PLACEHOLDER_STRATEGY) {
		@Override
		protected void valueChanged( PlaceholderStrategy oldValue, PlaceholderStrategy newValue ){
			handles.setStrategy( newValue );
		}
	}; 

    
    /**
     * How to layout the buttons on this station
     */
    private PropertyValue<ButtonContent> buttonContent = new PropertyValue<ButtonContent>( BUTTON_CONTENT ){
    	@Override
    	protected void valueChanged( ButtonContent oldValue, ButtonContent newValue ){
    		if( oldValue != newValue ){
    			recreateTitles();
    		}
    	}
    };
    
    /** the minimum size this station has */
    private PropertyValue<Dimension> minimumSize = new PropertyValue<Dimension>( MINIMUM_SIZE ) {
    	protected void valueChanged( Dimension oldValue, Dimension newValue ){
    		buttonPane.revalidate();
    	}
	};
	
	/** the factory creating {@link FlapWindow}s for this station */
	private PropertyValue<FlapWindowFactory> windowFactory = new PropertyValue<FlapWindowFactory>( WINDOW_FACTORY ){
		protected void valueChanged( FlapWindowFactory oldValue, FlapWindowFactory newValue ){
			if( oldValue != null ){
				oldValue.uninstall( FlapDockStation.this );
			}
			
			if( newValue != null ){
				newValue.install( FlapDockStation.this );
			}

			updateWindow( getFrontDockable(), true );
		}
	};
	
	/** Access to the current {@link DisablingStrategy} */
	private PropertyValue<DisablingStrategy> disablingStrategy = new PropertyValue<DisablingStrategy>( DisablingStrategy.STRATEGY ){
		@Override
		protected void valueChanged( DisablingStrategy oldValue, DisablingStrategy newValue ){
			if( oldValue != null ){	
				oldValue.removeDisablingStrategyListener( disablingStrategyListener );
			}
			if( newValue != null ){
				newValue.addDisablingStrategyListener( disablingStrategyListener );
				if( newValue.isDisabled( FlapDockStation.this )){
					setFrontDockable( null );
				}
			}
		}
	};
	
	/** observes the {@link #disablingStrategy} and closes the front dockable if necessary */
	private DisablingStrategyListener disablingStrategyListener = new DisablingStrategyListener(){
		public void changed( DockElement item ){
			if( item == FlapDockStation.this ){
				if( disablingStrategy.getValue().isDisabled( item )){
					setFrontDockable( null );
				}
			}
		}
	};
    
    /** The direction in which the popup-window is, in respect to this station */
    private Direction direction = Direction.SOUTH;
    /** 
     * This property tells this station whether the station can change the
     * {@link #direction} property automatically or not 
     */
    private boolean autoDirection = true;
    
    /** The popup-window */
    private FlapWindow window;
    /** The size of the border, which can be grabbed by the user, of the popup-window */
    private int windowBorder = 3;
    /** The minimal size of the popup-window */
    private int windowMinSize = 25;
    /** The initial size of windows, can be overridden by the layout manager */
    private int defaultWindowSize = 400;
    
    /** 
     * This variable is set when the front-dockable is removed, because
     * the {@link DockController} is removed. If the controller is added
     * again, then the front-dockable can be restored with the value of
     * this variable.
     */
    private Dockable oldFrontDockable;
    
    /** A list of all {@link Dockable Dockables} registered on this station */
    private DockablePlaceholderList<DockableHandle> handles = new DockablePlaceholderList<DockableHandle>();
    /** a listener for all {@link Dockable}s of this station */
    private Listener dockableListener = new Listener();
    
    /** The component on which all "buttons" are shown (the titles created with the id {@link #BUTTON_TITLE_ID}) */
    private ButtonPane buttonPane;
    
    /** This version is obtained by using {@link #BUTTON_TITLE_ID} */
    private DockTitleVersion buttonVersion;
    /** This version is obtained by using {@link #WINDOW_TITLE_ID} */
    private DockTitleVersion titleVersion;
    
    /** The {@link StationPaint} used to paint on this station */
    private DefaultStationPaintValue paint;
    /** The {@link Combiner} user to combine {@link Dockable Dockables}*/
    private StationCombinerValue combiner;
    /** The {@link DisplayerFactory} used to create displayers*/
    private DefaultDisplayerFactoryValue displayerFactory;
    /** Collection used to handle the {@link DockableDisplayer} */
    private DisplayerCollection displayers;
    
    /** 
     * Temporary information needed when a {@link Dockable} is moved
     * over this station.
     */
    private FlapDropInfo dropInfo;
    
    /** Information about a dockable that is removed from this station */
    private StationDragOperation dragInfo;
    
    /** A listener added to the {@link MouseFocusObserver} */
    private ControllerListener controllerListener = new ControllerListener();
    
    /** 
     * The button-titles are organized in a way that does not need much
     * space if this property is <code>true</code>
     */
    private boolean smallButtons = true;
    
    /** 
     * An action that will be added to all children of this station.
     */
    private ListeningDockAction holdAction;
    
    /** A listener that is added to the parent of this dockable station. */
    private VisibleListener visibleListener = new VisibleListener();
    /** the last checked state of {@link #isDockableVisible()} */
    private boolean lastShowing = false;
    /** A list of listeners that were added to this station */
    private List<FlapDockListener> flapDockListeners = new ArrayList<FlapDockListener>();
    
    /** Manager for the visibility of the children of this station */
    private DockableShowingManager showingManager;
    
    /** the background algorithm of this component */
    private Background background = new Background();
    
    /** tells how far the {@link FlapSideDropLayer} stretches */
    private int borderSideSnapSize = 15;
    
    /**
     * Default constructor of a {@link FlapDockStation}
     */
    public FlapDockStation(){
    	init();
    }
    
    /**
     * Creates a new {@link FlapDockStation}.
     * @param init <code>true</code> if the fields of this station should
     * be initialized, <code>false</code> otherwise. If <code>false</code>, then
     * {@link #init()} must be called by a subclass.
     */
    protected FlapDockStation( boolean init ){
    	if( init ){
    		init();
    	}
    }
    
    /**
     * Initializes the fields of this station, hast to be called exactly once
     */
    protected void init(){
        showingManager = new DockableShowingManager( listeners );
        buttonPane = createButtonPane();
        buttonPane.setBackground( background );
        buttonPane.setController( getController() );
        
        setDirection( Direction.SOUTH );
        
        displayerFactory = new DefaultDisplayerFactoryValue( ThemeManager.DISPLAYER_FACTORY + ".flap", this );
        displayers = new DisplayerCollection( this, displayerFactory, DISPLAYER_ID );
        paint = new DefaultStationPaintValue( ThemeManager.STATION_PAINT + ".flap", this );
        combiner = new StationCombinerValue( ThemeManager.COMBINER + ".flap", this );
        
        buttonPane.addComponentListener( new ComponentAdapter(){
            @Override
            public void componentResized( ComponentEvent e ) {
                if( autoDirection )
                    selfSetDirection();
                else
                    updateWindowBounds();
            }
        });
        
        buttonPane.addHierarchyBoundsListener( new HierarchyBoundsListener(){
            public void ancestorMoved( HierarchyEvent e ) {
                if( autoDirection )
                    selfSetDirection();
                else
                    updateWindowBounds();
            }
            public void ancestorResized( HierarchyEvent e ) {
                if( autoDirection )
                    selfSetDirection();
                else
                    updateWindowBounds();
            }
        });
        
		buttonPane.addHierarchyListener( new HierarchyListener(){
			public void hierarchyChanged( HierarchyEvent e ){
				if( (e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0 ){
					if( getDockParent() == null ){
						getDockableStateListeners().checkShowing();
					}
					checkShowing();
				}
			}
		});
        
        holdAction = createHoldAction();
    }
    
    /**
     * Creates the panel which will show buttons for the children of this station.
     * @return the new panel
     */
    protected ButtonPane createButtonPane(){
    	return new ButtonPane( this );
    }
    
    /**
     * Creates a {@link DockAction} that is added to all children
     * of this station. The action should change the <code>hold</code>
     * state of the associated {@link Dockable}, this can be done
     * through the method {@link #setHold(Dockable, boolean)}.
     * @return The action, or <code>null</code> if no action should
     * be added to the children
     */
    protected ListeningDockAction createHoldAction(){
        return new FlapDockHoldToggle( this );
    }
    
    protected DockComponentRootHandler createRootHandler() {
    	return new DockComponentRootHandler( this ){
			protected TraverseResult shouldTraverse( Component component ) {
				if( buttonPane.getBasePane() == component ){
					return TraverseResult.EXCLUDE_CHILDREN;
				}
				if( displayers.isDisplayerComponent( component )){
					return TraverseResult.EXCLUDE;
				}
				
				return TraverseResult.INCLUDE_CHILDREN;
			}
		};
    }
    
    @Override
    public void setDockParent( DockStation station ) {
        if( getDockParent() != null ){
            getDockParent().removeDockStationListener( visibleListener );
        }
        
        super.setDockParent(station);
        
        if( station != null ){
            station.addDockStationListener( visibleListener );
        }
    }
    
    @Override
    public void setController( DockController controller ) {
        if( getController() != controller ){
            boolean remove = getController() != null;
            
            if( remove ){
            	handles.unbind();
                getController().removeDockableFocusListener( controllerListener );
                getController().getFocusController().removeVetoListener( controllerListener );
                
                oldFrontDockable = getFrontDockable();
                setFrontDockable( null );
                
                for( DockableHandle dockable : handles.dockables() ){
                	if( dockable != null ){
                		dockable.setTitle( null );
                	}
                }
                
                if( window != null ){
                	window.setDockTitle( null );
                }
                titleVersion = null;
                buttonVersion = null;
            }

            super.setController(controller);
            placeholderStrategy.setProperties( controller );
            displayers.setController( controller );
            paint.setController( controller );
            displayerFactory.setController( controller );
            combiner.setController( controller );
            background.setController( controller );
            if( window != null ){
            	window.setController( controller );
            }
            disablingStrategy.setProperties( controller );
            buttonPane.setController( controller );
            FlapLayoutManager oldLayoutManager = layoutManager.getValue();
            layoutManager.setProperties( controller );
            FlapLayoutManager newLayoutManager = layoutManager.getValue();
            
            if( oldLayoutManager == newLayoutManager ){
                if( controller == null ){
                	if( oldLayoutManager != null ){
                		oldLayoutManager.uninstall( this );
                	}
                }
                else{
                	if( newLayoutManager != null ){
                		newLayoutManager.install( this );
                	}
                }
            }
            
            buttonContent.setProperties( controller );
            minimumSize.setProperties( controller );
            
            if( holdAction != null )
                holdAction.setController( controller );

            if( controller != null ){
            	handles.bind();
            	
                titleVersion = controller.getDockTitleManager().getVersion( WINDOW_TITLE_ID, ControllerTitleFactory.INSTANCE );
                buttonVersion = controller.getDockTitleManager().getVersion( BUTTON_TITLE_ID, BasicButtonTitleFactory.FACTORY );
                
                for( DockableHandle dockable : handles.dockables() ){
                	if( dockable != null ){
                		dockable.setTitle( buttonVersion );
                	}
                }
                
                if( window != null ){
                	window.setDockTitle( titleVersion );
                }
                
                controller.addDockableFocusListener( controllerListener );
                controller.getFocusController().addVetoListener( controllerListener );
                
                if( isStationShowing() )
                    setFrontDockable( oldFrontDockable );
            }
            
            windowFactory.setProperties( controller );
            buttonPane.setProperties( controller );
            buttonPane.resetTitles();
            
            showingManager.fire();
        }
    }
    
    @Override
    protected void callDockUiUpdateTheme() throws IOException {
    	DockUI.updateTheme( this, new FlapDockStationFactory());
    }
    
    /**
     * Gets the direction in which the popup-window is currently opened.
     * @return The direction
     */
    public Direction getDirection() {
        return direction;
    }
    
    /**
     * Sets the direction in which the popup-window points. The direction
     * may be overridden, if the property {@link #isAutoDirection() autoDirection} 
     * is set to <code>true</code>.
     * @param direction The direction of the popup-window 
     */
    public void setDirection( Direction direction ) {
        if( direction == null )
            throw new IllegalArgumentException();
             
        this.direction = direction;
        DockTitle.Orientation orientation = orientation( direction );
        
        for( DockableHandle dockable : handles.dockables() ){
        	DockTitle title = dockable.getTitle();
        	if( title != null ){
        		title.setOrientation( orientation );
        	}
        }
        
        buttonPane.resetTitles();
        updateWindowBounds();
        buttonPane.revalidate();
    }
    
    /**
     * Determines the orientation of the {@link DockTitle DockTitles} on this
     * station.
     * @param direction the direction in which the flap opens
     * @return the orientation of the titles
     */
    protected DockTitle.Orientation orientation( Direction direction ){
        switch( direction ){
            case NORTH:
                return DockTitle.Orientation.SOUTH_SIDED;
            case SOUTH:
                return DockTitle.Orientation.NORTH_SIDED;
            case EAST:
                return DockTitle.Orientation.WEST_SIDED;
            case WEST:
                return DockTitle.Orientation.EAST_SIDED;
        }
        
        return null;
    }
    
    /**
     * Recalculates the size and the location of the popup-window, if
     * there is a window.
     */
    protected void updateWindowBounds(){
        if( window != null )
            window.updateBounds();
    }
    
    /**
     * Gets the minimum size this station should have.
     * @return the minimum size, never <code>null</code>
     */
    public Dimension getMinimumSize(){
    	return minimumSize.getValue();
    }
    
    /**
     * Sets the minimum size this station should have. A value of <code>null</code> 
     * is valid and will let this station use the property {@link #MINIMUM_SIZE}.
     * @param size the new minimum size or <code>null</code>
     */
    public void setMinimumSize( Dimension size ){
    	minimumSize.setValue( size );
    }
    
    /**
     * Gets the factory to create new {@link DockableDisplayer}.
     * @return the factory
     */
    public DefaultDisplayerFactoryValue getDisplayerFactory() {
        return displayerFactory;
    }
    
    /**
     * Gets the set of displayers currently used on this station.
     * @return the set of displayers
     */
    public DisplayerCollection getDisplayers() {
        return displayers;
    }
    
    /**
     * Gets the {@link Combiner} to merge {@link Dockable Dockables}
     * @return the combiner
     */
    public StationCombinerValue getCombiner() {
        return combiner;
    }
    
    /**
     * Gets the {@link StationPaint} to paint on this station.
     * @return The paint
     */
    public DefaultStationPaintValue getPaint() {
        return paint;
    }
    
    /**
     * Gets the rectangle to which a flap-window will be attached. The default
     * is a rectangle that lies exactly over this component. The coordinates
     * of the result are relative to the component of this station.
     * @return the free area near a window
     */
    public Rectangle getExpansionBounds(){
        Component component = getComponent();
        return new Rectangle( 0, 0, component.getWidth(), component.getHeight() );
    }
    
    /**
     * Tells whether this station can change the
     * {@link #setDirection(bibliothek.gui.dock.FlapDockStation.Direction) direction}
     * itself, or if only the user can change the direction. 
     * @return <code>true</code> if the station chooses the direction itself
     * @see #setAutoDirection(boolean)
     */
    public boolean isAutoDirection() {
        return autoDirection;
    }
    
    /**
     * Tells this station whether it can choose the 
     * {@link #setDirection(bibliothek.gui.dock.FlapDockStation.Direction) direction}
     * of the popup-window itself, or if the direction remains always the
     * same.
     * @param autoDirection <code>true</code> if the station can choose the
     * direction itself, <code>false</code> otherwise
     */
    public void setAutoDirection( boolean autoDirection ) {
        this.autoDirection = autoDirection;
        if( autoDirection )
            selfSetDirection();
    }
    
    /**
     * Calculates the best 
     * {@link #setDirection(bibliothek.gui.dock.FlapDockStation.Direction) direction}
     * for the popup-window of this station.
     */
    public void selfSetDirection(){
        Component c = getComponent();
        Point center = new Point( c.getWidth()/2, c.getHeight()/2 );
        SwingUtilities.convertPointToScreen( center, c );
        Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
        
        Direction direction;
        
        if( c.getWidth() > c.getHeight() ){
            if( center.y < size.height/2 ){
                direction = Direction.SOUTH;
            }
            else{
                direction = Direction.NORTH;
            }
        }
        else{
            if( center.x < size.width/2 ){
                direction = Direction.EAST;
            }
            else{
                direction = Direction.WEST;
            }
        }
        
        if( direction != this.direction )
            setDirection( direction );
        else
            updateWindowBounds();
    }
    
    public Dockable getFrontDockable() {
        if( window == null ) //|| !window.isVisible() )
            return null;
        else
            return window.getDockable();
    }

    public void setFrontDockable( Dockable dockable ) {
        Dockable oldFrontDockable = getFrontDockable();

        if( oldFrontDockable == dockable ){
            return;
        }
        
        updateWindow( dockable, false );
        
        if( getController() != null ){
        	if( oldFrontDockable != null ){
                DockTitle[] titles = oldFrontDockable.listBoundTitles();
                boolean active = getController().isFocused( oldFrontDockable );
                for( DockTitle title : titles )
                    changed( oldFrontDockable, title, active );
            }
        	
        }
        
        if( window != null ){
        	if( window.getDockable() == null )
        		window.setWindowVisible( false );
        	else
        		window.repaint();
        }
        
        if( getController() != null ){
        	if( dockable != null ){
        		DockTitle[] titles = dockable.listBoundTitles();
                boolean active = getController().isFocused( dockable );
                for( DockTitle title : titles )
                    changed( dockable, title, active );
            }
        }
        
        showingManager.fire();
        listeners.fireDockableSelected( oldFrontDockable, dockable );
    }
    
    /**
     * Makes sure that <code>dockable</code> is shown on the current {@link FlapWindow}. May replace
     * the current window if necessary. 
     * @param dockable the item to show, can be <code>null</code>
     * @param forceReplace whether the window should be replaced anyway
     */
    private void updateWindow( Dockable dockable, boolean forceReplace ){
    	if( dockable == null ){
    		if( window != null ){
    			window.setDockable( null );
    			if( forceReplace ){
    				setFlapWindow( null );
    			}
    		}
    	}
    	else{
	    	Window owner = SwingUtilities.getWindowAncestor( getComponent() );
	        if( window == null || forceReplace || !windowFactory.getValue().isValid(window, this) ){
	            if( window != null ){
	                window.setDockable( null );
	            }
	            
	            FlapWindow window = createFlapWindow( buttonPane );
	            if( window != null )
	                setFlapWindow( window );
	        }
	        
	        if( window != null && owner != null ){
	            window.setDockable( dockable );
	            if( owner.isVisible() )
	                window.setWindowVisible( true );
	        
	            updateWindowBounds();
	        }
    	}
    }
    
    /**
     * Creates a window for this station.
     * @param buttonPane the panel needed to calculate the size of the window
     * @return the window or <code>null</code> if no window could be created
     */
    protected FlapWindow createFlapWindow( ButtonPane buttonPane ){
    	FlapWindow window = windowFactory.getValue().create(this, buttonPane);
    	if( window != null ){
    		window.setDockTitle( titleVersion );
    	}
    	return window;
    }
    
    /**
     * Tells the <code>hold</code>=property of <code>dockable</code>.
     * @param dockable the {@link Dockable} whose property is asked
     * @return the current state
     * @see #setHold(Dockable, boolean)
     */
    public boolean isHold( Dockable dockable ) {
    	FlapLayoutManager manager = layoutManager.getValue();
    	if( manager == null )
    		return false;
    	
        return manager.isHold( this, dockable );
    }
    
    /**
     * Tells whether the station should close the popup when the 
     * {@link Dockable} looses the focus, or if the popup should
     * remain open until the user closes the popup. The value is forwarded
     * to the {@link FlapLayoutManager layout manager} of this station, the
     * layout manager can then decide if and how it would like to react. 
     * @param dockable the {@link Dockable} whose settings should change
     * @param hold <code>true</code> if the popup should remain open,
     * <code>false</code> if it should close
     */
    public void setHold( Dockable dockable, boolean hold ) {
    	FlapLayoutManager manager = layoutManager.getValue();
    	if( manager != null ){
    		boolean old = manager.isHold( this, dockable );
    		manager.setHold( this, dockable, hold );
    		hold = manager.isHold( this, dockable );
    		if( old != hold )
    			updateHold( dockable );
    	}
    }
    
    /**
     * Updates the hold property of <code>dockable</code>.
     * The new value is provided by the {@link FlapLayoutManager layout manager}.
     * @param dockable the element whose property is updated
     */
    public void updateHold( Dockable dockable ){
    	FlapLayoutManager manager = layoutManager.getValue();
    	if( manager != null ){
    		boolean hold = manager.isHold( this, dockable );
    		fireHoldChanged( dockable, hold );

    		if( !hold && getController() != null && getFrontDockable() == dockable ){
    			if( !getController().isFocused( dockable ))
    				setFrontDockable( null );
    		}
    	}
    }
    
    /**
     * How the buttons are organized.
     * @return <code>true</code> if the buttons are layout in a way that
     * needs not much space.
     * @see #setSmallButtons(boolean)
     */
    public boolean isSmallButtons() {
        return smallButtons;
    }
    
    /**
     * Sets how the buttons are layout. If <code>true</code>, then the buttons
     * have their preferred size. If <code>false</code> the buttons take
     * all available space of this station.
     * @param smallButtons <code>true</code> if the buttons should be small
     */
    public void setSmallButtons( boolean smallButtons ) {
        this.smallButtons = smallButtons;
    }
    
    /**
     * Gets the {@link DockTitleVersion} that is used to create titles
     * for the popup-window.
     * @return the version of titles for the popup, can be <code>null</code>
     */
    public DockTitleVersion getTitleVersion() {
        return titleVersion;
    }
    
    /**
     * Gets the {@link DockTitleVersion} that is used to create titles
     * for the button-panel.
     * @return the version of titles for buttons, can be <code>null</code>
     */
    public DockTitleVersion getButtonVersion() {
        return buttonVersion;
    }
    
    /**
     * Gets the size of the border of the popup-window, where the user
     * can change the size of the window itself.
     * @return the popup-size
     * @see #setWindowBorder(int)
     */
    public int getWindowBorder() {
        return windowBorder;
    }
    
    /**
     * Sets the size of the draggable area on the popup-window, that is used
     * to change the size of the window.
     * @param windowBorder the border, at least 0
     */
    public void setWindowBorder( int windowBorder ) {
        if( windowBorder < 0 )
            throw new IllegalArgumentException( "Border must not be less than 0" );
        
        this.windowBorder = windowBorder;
        updateWindowBounds();
    }
    
    /**
     * Gets the minimal size the popup-window can have.
     * @return the minimal size
     * @see #setWindowMinSize(int)
     */
    public int getWindowMinSize() {
        return windowMinSize;
    }
    
    /**
     * Sets the minimal size which the popup-window can have.
     * @param windowMinSize the minimal size
     */
    public void setWindowMinSize( int windowMinSize ) {
        if( windowMinSize < 0 )
            throw new IllegalArgumentException( "Min size must not be smaller than 0" );
        this.windowMinSize = windowMinSize;
        updateWindowBounds();
    }
    
    /**
     * Gets the current size of the popup-window
     * @param dockable the element for which the size should be returned
     * @return the current size
     */
    public int getWindowSize( Dockable dockable ){
    	FlapLayoutManager manager = layoutManager.getValue();
    	if( manager == null )
    		return 0;
    	
        return manager.getSize( this, dockable );
    }
    
    /**
     * Sets the size of the popup-window for <code>dockable</code>. The
     * value will be forwarded to the {@link FlapLayoutManager layout manager}
     * of this station, the layout manager can decide if and how the new size
     * is to be stored.
     * @param dockable the element for which the size should be set
     * @param size the size, at least 0
     */
    public void setWindowSize( Dockable dockable, int size ){
        if( size < 0 )
            throw new IllegalArgumentException( "Size must at least be 0" );
        
        FlapLayoutManager manager = layoutManager.getValue();
        if( manager != null ){
        	manager.setSize( this, dockable, size );
        	updateWindowSize( dockable );
        }
    }
    
    /**
     * Updates the size of the window if <code>dockable</code> is currently
     * shown. The new size is provided by the {@link FlapLayoutManager layout manager}.
     * @param dockable the element whose size should be updated
     */
    public void updateWindowSize( Dockable dockable ){
        if( getFrontDockable() == dockable ){
            updateWindowBounds();
        }
    }
    
    /**
     * Sets the default size a window should have. This property might be
     * overridden by the {@link FlapLayoutManager layout manager}.
     * @param defaultWindowSize the default size of windows
     */
    public void setDefaultWindowSize( int defaultWindowSize ) {
        this.defaultWindowSize = defaultWindowSize;
    }
    
    /**
     * Gets the default size of a new window.
     * @return the default size
     */
    public int getDefaultWindowSize() {
        return defaultWindowSize;
    }
    
    /**
     * Sets the layout manager which should be used by this station. The
     * manager can be changed on a global level using {@link #LAYOUT_MANAGER}.
     * @param manager the manager or <code>null</code> when a default
     * manager should be used
     */
    public void setFlapLayoutManager( FlapLayoutManager manager ){
        layoutManager.setValue( manager );
    }
    
    /**
     * Gets the layout manager which was explicitly set by {@link #setFlapLayoutManager(FlapLayoutManager)}.
     * @return the manager or <code>null</code>
     */
    public FlapLayoutManager getFlapLayoutManager(){
        return layoutManager.getOwnValue();
    }
    
    /**
     * Gets the currently used {@link FlapLayoutManager}.
     * @return the current manager
     */
    public FlapLayoutManager getCurrentFlapLayoutManager(){
    	return layoutManager.getValue();
    }
    
    /**
     * Gets the {@link PlaceholderStrategy} that is currently in use.
     * @return the current strategy, may be <code>null</code>
     */
    public PlaceholderStrategy getPlaceholderStrategy(){
    	return placeholderStrategy.getValue();
    }
    
    /**
     * Sets the {@link PlaceholderStrategy} to use, <code>null</code> will set
     * the default strategy.
     * @param strategy the new strategy, can be <code>null</code>
     */
    public void setPlaceholderStrategy( PlaceholderStrategy strategy ){
    	placeholderStrategy.setValue( strategy );
    }
    
    /**
     * Adds a listener to this station. The listener will be invoked when
     * some properties of this station change.
     * @param listener the new listener
     */
    public void addFlapDockStationListener( FlapDockListener listener ){
        flapDockListeners.add( listener );
    }
    
    /**
     * Removes an earlier added listener from this station.
     * @param listener the listener to remove
     */
    public void removeFlapDockStationListener( FlapDockListener listener ){
        flapDockListeners.remove( listener );
    }
    
    /**
     * Informs all registered {@link FlapDockListener FlapDockListeners}
     * that the hold-property of a {@link Dockable} has been changed.
     * @param dockable the <code>Dockable</code> whose property is changed
     * @param value the new value of the property
     */
    protected void fireHoldChanged( Dockable dockable, boolean value ){
        for( FlapDockListener listener : flapDockListeners.toArray( new FlapDockListener[ flapDockListeners.size() ] ))
            listener.holdChanged( this, dockable, value );
    }
    
    @Override
    public DockActionSource getDirectActionOffers( Dockable dockable ) {
    	int index = indexOf( dockable );
    	if( index < 0 )
    		return null;
    	
    	DockableHandle handle = handles.dockables().get( index );
    	return handle.getActions();
    }
    
    private void updateIsHoldSwitchable( Dockable dockable ){
    	if( dockable == null ){
    		for( DockableHandle handle : handles.dockables() ){
    			handle.getActions().updateHoldSwitchable();
    		}
    	}
    	else{
    		int index = indexOf( dockable );
        	if( index >= 0 ){
	        	DockableHandle handle = handles.dockables().get( index );
	        	handle.getActions().updateHoldSwitchable();
        	}
    	}
    }
    
    @Override
    public void changed( Dockable dockable, DockTitle title, boolean active ) {
    	ActivityDockTitleEvent event = new ActivityDockTitleEvent( this, dockable, active );
        event.setPreferred( dockable == getFrontDockable() );
        title.changed( event );
    }
    
    /**
     * Sets the current drop-information. The information is forwarded
     * to the popup-window and the button-panel (if they exist).
     * @param info the new information, or <code>null</code>
     */
    private void setDropInfo( FlapDropInfo info ){
        this.dropInfo = info;
        if( window != null )
            window.setDropInfo( info );
        
        if( buttonPane != null )
            buttonPane.setDropInfo( info );
    }
    
    /**
     * Sets the popup-window that will be used in the future. The popup-window
     * can be replaced by another window if the root window of the tree in which
     * this {@link Component} is changes.
     * @param window the new window, can be <code>null</code>
     */
    private void setFlapWindow( FlapWindow window ){
    	if( this.window != null ){
    		getRootHandler().removeRoot( window.getComponent() );
    		this.window.setController( null );
    		this.window.destroy();
    	}
    	
        this.window = window;
        if( window != null ){
        	window.setController( getController() );
            window.setDropInfo( dropInfo );
            getRootHandler().removeRoot( window.getComponent() );
        }
    }
    
    /**
     * Checks whether the currently used {@link FlapWindow} equals
     * <code>window</code>.
     * @param window a window
     * @return <code>true</code> if <code>window</code> is currently used
     * by this station
     */
    public boolean isFlapWindow( FlapWindow window ){
    	return this.window == window;
    }
    
    /**
     * Gets the window which is currently used by this station. The window
     * may or may not be shown currently. Callers should not modify the window.
     * @return the current window, might be <code>null</code>
     */
    public FlapWindow getFlapWindow(){
    	return window;
    }
    
    public PlaceholderMap getPlaceholders(){
	    return handles.toMap();
    }
    
    public PlaceholderMapping getPlaceholderMapping() {
    	return new PlaceholderListMapping( this, handles ){
    		public DockableProperty getLocationAt( Path placeholder ) {
    			int index = handles.getDockableIndex( placeholder );
    			return new FlapDockProperty( index, false, -1, placeholder );
    		}
    	};
    }
    
    public void setPlaceholders( PlaceholderMap placeholders ){
    	if( getDockableCount() > 0 ){
    		throw new IllegalStateException( "only allowed if there are not children present" );
    	}
    	
    	try{
    		DockablePlaceholderList<DockableHandle> next = new DockablePlaceholderList<DockableHandle>( placeholders );
    		if( getController() != null ){
    			handles.setStrategy( null );
    			handles.unbind();
    			handles = next;
    			handles.bind();
    			handles.setStrategy( getPlaceholderStrategy() );
    		}
    		else{
    			handles = next;
    		}
    	}
    	catch( IllegalArgumentException ex ){
    		// silent
    	}
    }
    

    /**
     * Gets the placeholders of this station using a {@link PlaceholderListItemConverter} to
     * encode the children of this station. To be exact, the converter puts the following
     * parameters for each {@link Dockable} into the map:
     * <ul>
     * 	<li>id: the integer from <code>children</code></li>
     * 	<li>index: the location of the element in the dockables-list</li>
     * 	<li>hold: the return value of {@link #isHold(Dockable)}</li>
     *  <li>size: the return value of {@link #getWindowSize(Dockable)}</li>
     *  <li>placeholder: the placeholder of the element, might not be written</li>
     * </ul> 
     * @param children a unique identifier for each child of this station
     * @return the map 
     */
    public PlaceholderMap getPlaceholders( final Map<Dockable, Integer> children ){
    	final PlaceholderStrategy strategy = getPlaceholderStrategy();
    	
    	return handles.toMap( new PlaceholderListItemAdapter<Dockable, DockableHandle>() {
    		@Override
    		public ConvertedPlaceholderListItem convert( int index, DockableHandle dockable ){
    			Integer id = children.get( dockable.getDockable() );
    			if( id == null ){
    				return null;
    			}
    			
    			ConvertedPlaceholderListItem item = new ConvertedPlaceholderListItem();
	    		item.putInt( "id", id );
	    		item.putInt( "index", index );
	    		item.putBoolean( "hold", isHold( dockable.getDockable() ));
	    		item.putInt( "size", getWindowSize( dockable.getDockable() ) );
	    		
	    		if( strategy != null ){
	    			Path placeholder = strategy.getPlaceholderFor( dockable.getDockable() );
	    			if( placeholder != null ){
	    				item.putString( "placeholder", placeholder.toString() );
	    				item.setPlaceholder( placeholder );
	    			}
	    		}
	    		
	    		return item;
    		}
		});
    }
    
    /**
     * Sets a new layout on this station, this method assumes that <code>map</code> was created
     * using {@link #getPlaceholders(Map)}.
     * @param map the map to read
     * @param children the new children of this stations
     * @throws IllegalStateException if there are children left on this station
     */
    public void setPlaceholders( PlaceholderMap map, final Map<Integer, Dockable> children ){
    	DockUtilities.checkLayoutLocked();
    	if( getDockableCount() > 0 ){
    		throw new IllegalStateException( "must not have any children" );
    	}
    	
    	DockController controller = getController();
    	try{
    		if( controller != null ){
    			controller.freezeLayout();
    			handles.setStrategy( null );
				handles.unbind();
    		}
    		
	    	handles = new DockablePlaceholderList<DockableHandle>();
	    	handles.read( map, new PlaceholderListItemAdapter<Dockable, DockableHandle>(){
	    		private DockHierarchyLock.Token token;
	    		
				@Override
				public DockableHandle convert( ConvertedPlaceholderListItem item ){
					int id = item.getInt( "id" );
					Dockable dockable = children.get( id );
					if( dockable != null ){
						DockUtilities.ensureTreeValidity( FlapDockStation.this, dockable );
						token = DockHierarchyLock.acquireLinking( FlapDockStation.this, dockable );
						
						boolean hold = item.getBoolean( "hold" );
						int size = item.getInt( "size" );
						
				        listeners.fireDockableAdding( dockable );
				        DockableHandle handle = link( dockable );;    
				        
				        setHold( dockable, hold );
				        setWindowSize( dockable, size );
				        
				        return handle;
					}
					return null;
				}
				
				@Override
				public void added( DockableHandle dockable ){
					try{
						dockable.getDockable().setDockParent( FlapDockStation.this );
						listeners.fireDockableAdded( dockable.getDockable() );
					}
					finally{
						token.releaseNoCheck();
					}
				}
			});
			if( getController() != null ){
				handles.bind();
				handles.setStrategy( getPlaceholderStrategy() );
			}
    	}
    	finally{
    		if( controller != null ){
    			controller.meltLayout();
    		}
    	}
		buttonPane.resetTitles();
    }

    public DockStationDropLayer[] getLayers(){
    	// do not support drag and drop if the component is invisible 
    	Component component = getComponent();
    	if( component.getWidth() <= 0 || component.getHeight() <= 0 ){
    		return new DockStationDropLayer[]{};
    	}
    	
    	if( getDockableCount() == 0 ){
	    	return new DockStationDropLayer[]{
	    			new DefaultDropLayer( this ),
	    			new FlapOverrideDropLayer( this ),
	    			new WindowDropLayer( this ),
	    			new FlapSideDropLayer( this )
	    	};
    	}
    	else{
    		return new DockStationDropLayer[]{
	    			new DefaultDropLayer( this ),
	    			new FlapOverrideDropLayer( this ),
	    			new WindowDropLayer( this )
	    	};
    	}
    }
    
    /**
     * Sets the size of the outside layer. If the mouse is outside this station, but within
     * <code>borderSideSnapSize</code>, then this station may still be the target of a drag and
     * drop operation.
     * @param borderSideSnapSize the size in pixels
     */
    public void setBorderSideSnapSize( int borderSideSnapSize ){
		this.borderSideSnapSize = borderSideSnapSize;
	}
    
    /**
     * Tells how far the layer outside the station stretches.
     * @return the size of the outside layer
     * @see #setBorderSideSnapSize(int)
     */
    public int getBorderSideSnapSize(){
		return borderSideSnapSize;
	}
    
    public StationDragOperation prepareDrag( Dockable dockable ){
    	if( dragInfo != null ){
    		dragInfo.canceled();
    	}
    	if( window != null && window.getDockable() == dockable ){
    		window.setRemoval( true );
    		dragInfo = new StationDragOperation(){
				public void succeeded(){
					window.setRemoval( false );
					dragInfo = null;
				}
				
				public void canceled(){
					window.setRemoval( false );
					dragInfo = null;
				}
			};
    	}
    	return dragInfo;
    }
    
    public StationDropOperation prepareDrop( StationDropItem item ){
    	int mouseX = item.getMouseX();
    	int mouseY = item.getMouseY();
    	Dockable dockable = item.getDockable();
    	
    	boolean move = dockable.getDockParent() == this;
    	
    	if( SwingUtilities.isDescendingFrom( getComponent(), dockable.getComponent() )){
    		return null;
    	}
        
        Point mouse = new Point( mouseX, mouseY );
        SwingUtilities.convertPointFromScreen( mouse, buttonPane );
        FlapDropInfo dropInfo = null;
        
        DockAcceptance acceptance = getController().getAcceptance();
        
        // if mouse over window title: force combination
        if( window != null && window.isWindowVisible() ){
            DockTitle title = window.getDockTitle();
            
            if( title != null ){
                Component c = title.getComponent();
                Point point = new Point( mouseX, mouseY );
                SwingUtilities.convertPointFromScreen( point, c );
                Dockable child = window.getDockable();
                boolean combine = c.contains( point ) && acceptable( child, dockable );
                
                if( combine ){
                	dropInfo = prepareCombine( dockable, window, new Point( mouseX, mouseY ), combine, Enforcement.HARD );
                }
            }
        }
        
        // maybe a parent station wants to catch the event
        
        // if mouse over window: force combination
        if( window != null && window.isWindowVisible() && dropInfo == null ){
            Point point = new Point( mouseX, mouseY );
            Dockable child = window.getDockable();
            boolean combine = window.containsScreenPoint(point) && acceptable( child, dockable );
            
            if( combine ){
            	dropInfo = prepareCombine( dockable, window, point, false, Enforcement.HARD );
            }
        }
        
        if( dropInfo != null && dockable == getFrontDockable() )
            return null;
        
        if( dropInfo == null ){
            if( dockable.accept( this ) &&
                accept( dockable ) &&
                acceptance.accept( this, dockable )){
                
                dropInfo = new FlapDropInfo( this, dockable ){
					public Point getMousePosition(){
						return null;
					}

					public Dockable getOld(){
						return null;
					}

					public DockableDisplayer getOldDisplayer(){
						return null;
					}
					
					public PlaceholderMap getPlaceholders(){
						return null;
					}

					public Dimension getSize(){
						return null;
					}

					public boolean isMouseOverTitle(){
						return false;
					}                	
                };
                dropInfo.setIndex( buttonPane.indexAt( mouse.x, mouse.y ) );
            }
        }
        
        if( dropInfo == null ){
        	return null;
        }
        
        return new FlapDropOperation( dropInfo, move );
        
    }
    
    /**
     * Prepares a combination of <code>dockable</code> and <code>window</code>.
     * @param dockable the element that is going to be dropped
     * @param window the visible window under the mouse
     * @param mouseOnScreen the location of the mouse on the screen
     * @param mouseOverTitle whether the mouse is currently over a title
     * @param force whether a combination must happen or not
     * @return the combination, <code>null</code> if a combination is not desired for the given arguments
     */
    private FlapDropInfo prepareCombine( Dockable dockable, FlapWindow window, final Point mouseOnScreen, final boolean mouseOverTitle, Enforcement force ){
    	final Dockable child = window.getDockable();
    	final DockableDisplayer displayer = window.getDisplayer();
    	
    	FlapDropInfo info = new FlapDropInfo( this, dockable ){
			public boolean isMouseOverTitle(){
				return mouseOverTitle;
			}
			
			public Dimension getSize(){
				return child.getComponent().getSize();
			}
			
			public PlaceholderMap getPlaceholders(){
				for( DockablePlaceholderList<DockableHandle>.Item item : handles.list() ){
					DockableHandle handle = item.getDockable();
					if( handle != null && handle.getDockable() == child ){
						return item.getPlaceholderMap();
					}
				}
				return null;
			}
			
			public Point getMousePosition(){
				Point mouse = new Point( mouseOnScreen );
				SwingUtilities.convertPointFromScreen( mouse, getOld().getComponent() );
				return mouse;
			}
			
			public Dockable getOld(){
				return child;
			}
			
			public DockableDisplayer getOldDisplayer(){
				return displayer;
			}
		};
		
		CombinerTarget target = combiner.prepare( info, force );
		if( target == null ){
			return null;
		}
		info.setCombineTarget( target );
		return info;
    }

    public void drop( Dockable dockable ) {
        add( dockable );
    }

    public boolean drop( Dockable dockable, DockableProperty property ) {
        if( property instanceof FlapDockProperty )
            return drop( dockable, (FlapDockProperty)property );
        
        return false;
    }
    
    /**
     * Adds the {@link Dockable} <code>dockable</code> to this station or
     * to a child of this station, according to the contents of 
     * <code>property</code>.
     * @param dockable the new child
     * @param property the location of the new child
     * @return <code>true</code> if the new child could be added,
     * <code>false</code> if the child has been rejected
     */
    public boolean drop( final Dockable dockable, FlapDockProperty property ) {
    	DockUtilities.checkLayoutLocked();
    	boolean result = false;
        
        final Path placeholder = property.getPlaceholder();
        DockableProperty successor = property.getSuccessor();
        int index = property.getIndex();
        boolean acceptable = acceptable( dockable );
        
    	if( placeholder != null && successor != null ){
    		DockableHandle current = handles.getDockableAt( placeholder );
    		if( current != null ){
    			final Dockable oldDockable = current.getDockable();
    			DockStation station = oldDockable.asDockStation();
    			if( station != null ){
    				if( station.drop( dockable, successor )){
    					result = true;
    					handles.removeAll( placeholder );
    				}
    			}
    			else{
    				result = combine( current.getDockable(), dockable, successor );
    			}
    		}
    	}
    	
    	if( placeholder != null && !result ){
    		int listIndex = handles.getListIndex( placeholder );
    		if( listIndex >= 0 ){
    			add( dockable, property.getIndex(), listIndex );
    			setHold( dockable, property.isHolding() );
    			int size = property.getSize();
    			if( size >= getWindowMinSize() )
    				setWindowSize( dockable, size );
    			result = true;
    		}
    		else{
	    		index = handles.getDockableIndex( placeholder );
	    		if( index == -1 ){
	    			index = property.getIndex();
	    		}
    		}
    	}
    	
        if( !result && index >= getDockableCount() && acceptable ){
            add( dockable );
            setHold( dockable, property.isHolding() );
            int size = property.getSize();
            if( size >= getWindowMinSize() )
            	setWindowSize( dockable, size );
            result = true;
        }
        
        if( !result && successor != null ){
            DockStation previous = getDockable( index ).asDockStation();
            if( previous != null ){
                if( previous.drop( dockable, successor )){
                    result = true;
                }
            }
            else{
            	result = combine( getDockable( index ), dockable, successor );
            }
        }
        
        if( !result && acceptable ){
            add( dockable, index );
            setHold( dockable, property.isHolding() );
            int size = property.getSize();
            if( size >= getWindowMinSize() )
            	setWindowSize( dockable, size );
            result = true;
        }
        
        return result;
    }
    
    public DockableProperty getDockableProperty( Dockable dockable, Dockable target ) {
    	int index = indexOf( dockable );
    	boolean holding = isHold( dockable );
    	int size = getWindowSize( dockable );
    	
    	PlaceholderStrategy strategy = getPlaceholderStrategy();
    	Path placeholder = null;
    	if( strategy != null ){
    		placeholder = strategy.getPlaceholderFor( target == null ? dockable : target );
    		if( placeholder != null ){
    			handles.dockables().addPlaceholder( index, placeholder );
    		}
    	}
    	
        return new FlapDockProperty( index, holding, size, placeholder );
    }

    public void aside( AsideRequest request ){
	    DockableProperty location = request.getLocation();
	    if( location instanceof FlapDockProperty ){
	    	FlapDockProperty flapLocation = (FlapDockProperty)location;
	    	DockablePlaceholderList<DockableHandle>.Item item = getItem( flapLocation );
	    	
	    	if( item != null ){
	    		delegate().combine( item, getCombiner(), request );
	    	}
	    	
	    	FlapDockProperty copy = flapLocation.copy();
	    	copy.setSuccessor( null );
	    	copy.setPlaceholder( request.getPlaceholder() );
	    	request.answer( copy );
	    }
    }
    
    private DockablePlaceholderList<DockableHandle>.Item getItem( FlapDockProperty property ){
    	Path oldPlaceholder = property.getPlaceholder();

    	if( oldPlaceholder != null ){
    		DockablePlaceholderList<DockableHandle>.Item item = handles.getItem( oldPlaceholder );
    		if( item != null ){
    			return item;
    		}
    	}
    	
    	if( property.getIndex() >= 0 && property.getIndex() < handles.dockables().size() ){
    		int index = handles.levelToBase( property.getIndex(), Level.DOCKABLE );
    		return handles.list().get( index );
    	}
    	else{
    		return null;
    	}
    }
    
    public void move( Dockable dockable, DockableProperty property ) {
    	DockUtilities.checkLayoutLocked();
        if( property instanceof FlapDockProperty ){
            int index = indexOf( dockable );
            if( index < 0 )
                throw new IllegalArgumentException( "dockable is not child of this station" );
            
            int destination = ((FlapDockProperty)property).getIndex();
            destination = Math.min( destination, handles.dockables().size()-1 );
            destination = Math.max( 0, destination );
            
            if( destination != index ){
            	handles.dockables().move( index, destination );
                buttonPane.resetTitles();
                fireDockablesRepositioned( Math.min( index, destination ), Math.max( index, destination ) );
            }
        }
    }
    
    /**
     * Tells whether the point <code>x/y</code> is over the buttons of this station.
     * @param x the x-coordinate on the screen
     * @param y the y-coordinate on the screen
     * @return <code>true</code> if the point <code>x/y</code> is over the buttons
     */
    public boolean isOverButtons( int x, int y ){
    	Point mouse = new Point( x, y );
        SwingUtilities.convertPointFromScreen( mouse, buttonPane );
        return buttonPane.contains( mouse );
    }

    public boolean canDrag( Dockable dockable ) {
        return true;
    }

    public void drag( Dockable dockable ) {
        if( dockable.getDockParent() != this )
            throw new IllegalArgumentException( "The dockable can't be dragged, it is not child of this station" );
        
        remove( dockable );
    }

    public String getFactoryID() {
        return FlapDockStationFactory.ID;
    }

    public Component getComponent() {
        return buttonPane;
    }
    
    public int getDockableCount() {
        return handles.dockables().size();
    }

    public Dockable getDockable( int index ) {
        return handles.dockables().get( index ).getDockable();
    }
    
    /**
     * Gets the title which is used as button for the <code>index</code>'th dockable.
     * Clients should not modify the result of this method.
     * @param index the index of a {@link Dockable}
     * @return the title or <code>null</code>
     */
    public DockTitle getButton( int index ){
    	return handles.dockables().get( index ).getTitle();
    }
    
    @Override
    public boolean isVisible( Dockable dockable ) {
        return isStationShowing() && (getFrontDockable() == dockable);
    }
    
    /**
     * Deletes all titles of the button pane and then recreates them.
     */
    protected void recreateTitles(){
    	for( DockableHandle handle : handles.dockables() ){
    		handle.setTitle( buttonVersion );
    	}
    }
    
    /**
     * Removes <code>dockable</code> from this station.<br>
     * Note: clients may need to invoke {@link DockController#freezeLayout()}
     * and {@link DockController#meltLayout()} to ensure no-one else adds or
     * removes <code>Dockable</code>s.
     * @param dockable the child to remove
     */
    public void remove( Dockable dockable ){
        int index = indexOf( dockable );
        if( index >= 0 )
            remove( index );
    }
    
    /**
     * Removes the child with the given <code>index</code> from this station.<br>
     * Note: clients may need to invoke {@link DockController#freezeLayout()}
     * and {@link DockController#meltLayout()} to ensure no-one else adds or
     * removes <code>Dockable</code>s.
     * @param index the index of the child that will be removed
     */
    public void remove( int index ){
    	DockUtilities.checkLayoutLocked();
        Dockable dockable = getDockable( index );
        if( getFrontDockable() == dockable )
            setFrontDockable( null );
        
        if( oldFrontDockable == dockable )
            oldFrontDockable = null;
        
        DockHierarchyLock.Token token = DockHierarchyLock.acquireUnlinking( this, dockable );
        try{
	        listeners.fireDockableRemoving( dockable );
	        dockable.setDockParent( null );
	        DockableHandle handle = handles.dockables().get( index );
	        handles.remove( index );
	        handle.setTitle( null );
	        dockable.removeDockableListener( dockableListener );
	        // race condition, only required if not called from the EDT
	        buttonPane.resetTitles();
	        listeners.fireDockableRemoved( dockable );
        }
        finally{
        	token.release();
        }
        
        fireDockablesRepositioned( index );
    }
    
    /**
     * Adds <code>dockable</code> as new child to this station. The child
     * is added at the end of all children.
     * @param dockable the new child
     */
    public void add( Dockable dockable ){
        add( dockable, getDockableCount() );
    }
    
    /**
     * Inserts <code>dockable</code> as new child in the list of 
     * children.
     * @param dockable the new child
     * @param index the location in the button-panel of the child
     */
    public void add( Dockable dockable, int index ){
    	add( dockable, index, -1 );
    }
    
    private void add( Dockable dockable, int index, int listIndex ){
    	DockUtilities.checkLayoutLocked();
        DockUtilities.ensureTreeValidity( this, dockable );
        
        DockHierarchyLock.Token token = DockHierarchyLock.acquireLinking( this, dockable );
        try{
	        listeners.fireDockableAdding( dockable );
	        DockableHandle handle = link( dockable );
	        if( listIndex == -1 || handles.list().get( listIndex ).getDockable() != null ){
	        	handles.dockables().add( index, handle );
	        }
	        else if( handles.list().get( listIndex ).getDockable() == null ){
	        	handles.list().get( listIndex ).setDockable( handle );
	        }
	        dockable.setDockParent( this );
	        buttonPane.resetTitles(); // race condition, only required if not called from the EDT
        
	        listeners.fireDockableAdded( dockable );
        
        	fireDockablesRepositioned( index+1 );
        	
        	if( getController().isFocused( dockable )){
        		setFrontDockable( dockable );
        	}
        }
        finally{
        	token.release();
        }
    }
    
    private DockableHandle link( Dockable dockable ){
    	DockableHandle handle = createHandle( dockable );
        handle.setTitle( buttonVersion );
        dockable.addDockableListener( dockableListener );
        return handle;
    }
    
    /**
     * Creates a new wrapper around <code>dockable</code>, the wrapper is used as internal representation
     * of <code>dockable</code>.
     * @param dockable the element for which a new wrapper is created
     * @return the new wrapper, must not be <code>null</code>
     */
    protected DockableHandle createHandle( Dockable dockable ){
    	return new DockableHandle( dockable );
    }
    
    /**
     * Gets the wrapper of <code>dockable</code>.
     * @param dockable a child of this station
     * @return the wrapper or <code>null</code> if not yet created or if
     * <code>dockable</code> is not a child of this station
     */
    protected DockableHandle getHandle( Dockable dockable ){
		int index = indexOf( dockable );
		if( index < 0 ){
			return null;
		}
		return handles.dockables().get( index );
	}
    
    /**
     * Creates a combination out of <code>child</code>, which must be a
     * child of this station, and <code>append</code> which must not be
     * a child of this station. 
     * @param child a child of this station
     * @param append a {@link Dockable} that is not a child of this station
     * @return <code>true</code> if the combination was successful,
     * <code>false</code> otherwise (the <code>child</code> will remain
     * on this station)
     */
    public boolean combine( Dockable child, Dockable append ){
    	return combine( child, append, null );
    }

    /**
     * Creates a combination out of <code>child</code>, which must be a
     * child of this station, and <code>append</code> which must not be
     * a child of this station. 
     * @param child a child of this station
     * @param append a {@link Dockable} that is not a child of this station
     * @param property location information associated with <code>append</code>
     * @return <code>true</code> if the combination was successful,
     * <code>false</code> otherwise (the <code>child</code> will remain
     * on this station)
     */
    public boolean combine( final Dockable child, Dockable append, DockableProperty property ){
    	DockUtilities.checkLayoutLocked();
    	int index = indexOf( child );
        if( index < 0 )
            throw new IllegalArgumentException( "Child must be a child of this station" );
        
        int listIndex = handles.levelToBase( index, Level.DOCKABLE );
        DockablePlaceholderList<DockableHandle>.Item oldItem = handles.list().get( listIndex );
        final PlaceholderMap placeholders = oldItem.getPlaceholderMap();
        
    	FlapDropInfo info = new FlapDropInfo( this, append ){
			public boolean isMouseOverTitle(){
				return true;
			}
			
			public Dimension getSize(){
				return null;
			}
			
			public PlaceholderMap getPlaceholders(){
				return placeholders;
			}
			
			public Dockable getOld(){
				return child;
			}
			
			public DockableDisplayer getOldDisplayer(){
				return null;
			}
			
			public Point getMousePosition(){
				return null;
			}
		};
		
		CombinerTarget target = combiner.prepare( info, Enforcement.HARD );
		return combine( info, target, property );
    }
    
    private boolean combine( CombinerSource source, CombinerTarget target, DockableProperty property ){
    	DockUtilities.checkLayoutLocked();
    	
    	DockController controller = getController();
    	Dockable child = source.getOld();
    	Dockable append = source.getNew();
    	
    	DockUtilities.ensureTreeValidity( this, append );
    	
    	try{
    		if( controller != null )
    			controller.freezeLayout();
    	
	        int index = indexOf( child );
	        if( index < 0 )
	            throw new IllegalArgumentException( "old dockable must be a child of this station" );
	        
	        if( append.getDockParent() != null )
	            append.getDockParent().drag( append );
	        
	        boolean hold = isHold( child );
	        
	        int listIndex = handles.levelToBase( index, Level.DOCKABLE );
	        DockablePlaceholderList<DockableHandle>.Item oldItem = handles.list().get( listIndex );
	        final PlaceholderMap placeholders = oldItem.getPlaceholderMap();
	        oldItem.setPlaceholderMap( null );
	        
	        remove( index );
	        int other = indexOf( append );
	        if( other >= 0 ){
	            remove( other );
	            if( other < index )
	                index--;
	        }
	        
	        index = Math.min( index, getDockableCount());
	        
	        Dockable combination = combiner.combine( new CombinerSourceWrapper( source ){
	        	@Override
	        	public PlaceholderMap getPlaceholders(){
		        	return placeholders;
	        	}
	        }, target );
	        
	        if( property != null ){
	        	DockStation combined = combination.asDockStation();
	        	if( combined != null && append.getDockParent() == combined ){
	        		combined.move( append, property );
	        	}
	        }
	        
	        add( combination, index );
	        
	        listIndex = handles.levelToBase( index, Level.DOCKABLE );
	        DockablePlaceholderList<DockableHandle>.Item newItem = handles.list().get( listIndex );
	        newItem.setPlaceholderSet( newItem.getPlaceholderSet() );
	        
	        setHold( combination, hold );
	        return true;
    	}
    	finally{
    		if( controller != null )
    			controller.meltLayout();
    	}
    }
    
    public boolean canReplace( Dockable old, Dockable next ) {
        return true;
    }
    
    public void replace( DockStation old, Dockable next ){
	    replace( old.asDockable(), next, true );
    }
    
    public void replace( Dockable child, Dockable append ){
    	replace( child, append, false );
    }
    
    private void replace( Dockable child, Dockable append, boolean station ){
    	DockUtilities.checkLayoutLocked();
    	DockController controller = getController();
    	try{
    		if( controller != null )
    			controller.freezeLayout();
    		
    		int index = indexOf( child );
    		if( index < 0 )
    			throw new IllegalArgumentException( "Child must be a child of this station" );

    		boolean hold = isHold( child );
    		boolean open = getFrontDockable() == child;
    		
    		int listIndex = handles.levelToBase( index, Level.DOCKABLE );
    		DockablePlaceholderList<DockableHandle>.Item oldItem = handles.list().get( listIndex );
    		remove( index );
    		handles.list().remove( oldItem );
    		add( append, index );
    		DockablePlaceholderList<DockableHandle>.Item newItem = handles.list().get( listIndex );
    		if( station ){
    			newItem.setPlaceholderMap( child.asDockStation().getPlaceholders() );
    		}
    		else{
    			newItem.setPlaceholderMap( oldItem.getPlaceholderMap() );
    		}
    		newItem.setPlaceholderSet( oldItem.getPlaceholderSet() );
    		
    		setHold( append, hold );

    		if( open )
    			setFrontDockable( append );
    	}
    	finally{
    		if( controller != null )
    			controller.meltLayout();
    	}
    }
    
    /**
     * Gets the location of <code>dockable</code> in the button-panel.
     * @param dockable the {@link Dockable} to search
     * @return the location or -1 if the child was not found
     */
    public int indexOf( Dockable dockable ){
    	PlaceholderList.Filter<DockableHandle> list = handles.dockables();
    	
    	int index = 0;
    	for( DockableHandle handle : list ){
    		if( handle.getDockable() == dockable ){
    			return index;
    		}
    		index++;
    	}
    	
    	return -1;
    }

    private void checkShowing(){
    	boolean showing = isDockableShowing();
    	if( showing != lastShowing ){
    		lastShowing = showing;
            
            if( showing ){
                if( oldFrontDockable != null )
                    setFrontDockable( oldFrontDockable );
            }
            else{
                oldFrontDockable = getFrontDockable();
                setFrontDockable( null );
                if( !isHold( oldFrontDockable ))
                    oldFrontDockable = null;
            }
            
            showingManager.fire();
    	}
    }
    
    /**
     * This listener is added to the direct parent of the enclosing
     * {@link FlapDockStation}. The listener fires events if the visibility
     * changes, and the listener can remove the popup-window if the station
     * looses its visibility. 
     * @author Benjamin Sigg
     */
    private class VisibleListener extends DockStationAdapter{
        @Override
        public void dockableShowingChanged( DockStation station, Dockable dockable, boolean visible ) {
        	if( dockable == FlapDockStation.this ){
        		checkShowing();
            }
        }
    }
    
    /**
     * Listener added to the {@link Dockable}s of the enclosing
     * {@link FlapDockStation}, reacts on changes of the {@link DockTitle}.
     * @author Benjamin Sigg
     */
    private class Listener extends DockableAdapter{
        @Override
        public void titleExchanged( Dockable dockable, DockTitle title ) {
        	int index = indexOf( dockable );
        	if( index < 0 )
        		return;
        	
        	DockableHandle handle = handles.dockables().get( index );
        	if( handle.getTitle() == title ){
        		handle.setTitle( buttonVersion );
            }
        }
    }
    
    /**
     * Custom implementation of {@link StationDropOperation}.
     * @author Benjamin Sigg
     */
    protected class FlapDropOperation implements StationDropOperation{
    	private FlapDropInfo dropInfo;
    	private boolean move;
    	
    	/**
    	 * Creates a new operation.
    	 * @param dropInfo the location information of the dropped {@link Dockable}
    	 * @param move whether this is a move operation
    	 */
    	public FlapDropOperation( FlapDropInfo dropInfo, boolean move ){
    		if( dropInfo == null ){
    			throw new IllegalArgumentException( "dropInfo must not be null" );
    		}
    		
    		this.dropInfo = dropInfo;
    		this.move = move;
    	}
    	
    	public boolean isMove(){
	    	return move;
    	}
    	
    	public void draw(){
	    	setDropInfo( dropInfo );	
    	}
    	
    	public void destroy( StationDropOperation next ){
    		if( FlapDockStation.this.dropInfo == dropInfo ){
    			if( next == null || !(next instanceof FlapDropOperation) || next.getTarget() != getTarget() ){
    				setDropInfo( null );
    			}
    		}
    	}
    	
    	public Dockable getItem(){
    		return dropInfo.getDockable();
    	}
    	
    	public DockStation getTarget(){
    		return FlapDockStation.this;
    	}
    	
    	public CombinerTarget getCombination(){
	    	return dropInfo.getCombineTarget();
    	}
    	
    	public DisplayerCombinerTarget getDisplayerCombination(){
    		CombinerTarget target = getCombination();
    		if( target == null ){
    			return null;
    		}
    		return target.getDisplayerCombination();
    	}
    	
    	public void execute(){
	    	if( isMove() ){
	    		move();
	    	}
	    	else{
	    		drop();
	    	}
    	}
    	
        public void move() {
        	if( dropInfo.getCombineTarget() != null ){
                remove( dropInfo.getDockable() );
                combine( dropInfo, dropInfo.getCombineTarget(), null );
            }
        	else{
    	    	int index = indexOf( dropInfo.getDockable() );
    	    	if( index < dropInfo.getIndex() ){
    	    		dropInfo.setIndex( dropInfo.getIndex()-1 );
    	    	}
    	    	handles.dockables().move( index, dropInfo.getIndex() );
    	    	buttonPane.resetTitles();
    	    	fireDockablesRepositioned( Math.min( index, dropInfo.getIndex() ), Math.max( index, dropInfo.getIndex() ) );
        	}
        }

        public void drop(){
        	if( dropInfo.getCombineTarget() != null ){
                combine( dropInfo, dropInfo.getCombineTarget(), null );
            }
            else{
                add( dropInfo.getDockable(), dropInfo.getIndex() );
            }
        }
    }
    
    /**
     * Handles title, listeners and actions that are associated with a {@link Dockable}.
     * @author Benjamin Sigg
     */
    protected class DockableHandle implements PlaceholderListItem<Dockable>{
    	/** the element that is handled by this handler */
    	private Dockable dockable;
    	/** the title used */
    	private DockTitleRequest title;
    	/** the listener that gets added to the title of this handle */
    	private ButtonListener buttonListener;
    	/** the actions added by this station to {@link #dockable} */
    	private FlapDockStationSource actions;
    	
    	/**
    	 * Creates a new wrapper around <code>dockable</code>
    	 * @param dockable the dockable to wrap
    	 */
    	public DockableHandle( Dockable dockable ){
    		this( dockable, false );
    	}
    	
    	/**
    	 * Creates a new wrapper around <code>dockable</code>
    	 * @param dockable the dockable to wrap
    	 * @param forceActionSourceCreation whether {@link #getActions()} must always return a value other
    	 * than <code>null</code>
    	 */
    	public DockableHandle( Dockable dockable, boolean forceActionSourceCreation ){
    		this.dockable = dockable;
    		buttonListener = new ButtonListener( dockable );
    		if( holdAction != null || forceActionSourceCreation ){
    			actions = new FlapDockStationSource( FlapDockStation.this, dockable, holdAction );
    			actions.updateHoldSwitchable();
    		}
    	}
    	
    	/**
    	 * Gets the {@link DockActionSource} that should be shown on the {@link Dockable}.
    	 * @return the action source, can be <code>null</code>
    	 */
    	public FlapDockStationSource getActions(){
			return actions;
		}
    	
    	/**
    	 * Sets the action of {@link #getActions()} back to the action that was created
    	 * by {@link FlapDockStation#createHoldAction()}.
    	 */
    	public void resetHoldAction(){
    		if( actions != null ){
    			actions.setHoldAction( holdAction );
    		}
    	}
    	
    	public Dockable getDockable(){
			return dockable;
		}
    	
    	public Dockable asDockable(){
	    	return getDockable();
    	}
    	
    	public DockTitle getTitle(){
    		if( title == null )
    			return null;
    		return title.getAnswer();
    	}
    	
    	public void setTitle( DockTitleVersion version ){
    		if( title != null ){
    			DockTitle answer = title.getAnswer();
    			if( answer != null ){
    				answer.removeMouseInputListener( buttonListener );
    				dockable.unbind( answer );
    				buttonPane.resetTitles();
    			}
    			title.uninstall();
    			title = null;
    		}
    		
    		if( version != null ){
    			title = new DockTitleRequest( FlapDockStation.this, dockable, version ) {
					@Override
					protected void answer( DockTitle previous, DockTitle title ){
						if( previous != null ){
							previous.removeMouseInputListener( buttonListener );
							dockable.unbind( previous );
						}
						if( title != null ){
							title.addMouseInputListener( buttonListener );
							title.setOrientation( orientation( direction ) );
							dockable.bind( title );
						}
						buttonPane.resetTitles();
					}
				};
				title.install();
				title.request();
    		}
    	}
    }
    
    private class ControllerListener implements FocusVetoListener, DockableFocusListener{
    	public FocusVeto vetoFocus( FocusController controller, Dockable dockable ){
            return FocusVeto.NONE;
        }
    	
    	public FocusVeto vetoFocus( FocusController controller, DockTitle title ){
    		for( DockableHandle handle : handles.dockables() ){
        		if( handle.getTitle() == title ){
        			return FocusVeto.VETO_NO_CONSUME;
        		}
        	}
        	
            return FocusVeto.NONE;
        }
        
        public void dockableFocused( DockableFocusEvent event ) {
            Dockable front = getFrontDockable();
            
            if( isStationShowing() ){
                if( front == null || (front != null && isHold( front )))
                    return;
                
                DockController controller = event.getController();
                Dockable dockable = event.getNewFocusOwner();
                
                if( controller.isFocused( FlapDockStation.this ))
                    return;
                
                if( dockable == null || !DockUtilities.isAncestor( FlapDockStation.this, dockable ) ){
                	setFrontDockable( null );
                }
            }
        }
    }
    
    /**
     * Listens to the buttons. If one button is pressed, the popup-window
     * will be made visible.
     */
    private class ButtonListener extends MouseInputAdapter{
        /**
         * The <code>Dockable</code> whose button is observed by this
         * listener.
         */
        private Dockable dockable;
        
        /**
         * Constructs a new listener.
         * @param dockable the owner of the observed button
         */
        public ButtonListener( Dockable dockable ){
            this.dockable = dockable;
        }
        
        @Override
        public void mouseReleased( MouseEvent e ){
        	if( dockable.getDockParent() == FlapDockStation.this ){
        		final int MASK = InputEvent.BUTTON1_DOWN_MASK | InputEvent.BUTTON2_DOWN_MASK | InputEvent.BUTTON3_DOWN_MASK;
        		DisablingStrategy strategy = disablingStrategy.getValue();
        		boolean enabled = strategy == null || (!strategy.isDisabled( dockable ) && !strategy.isDisabled( FlapDockStation.this ));
        		
        		if( enabled && e.getButton() == MouseEvent.BUTTON1 && (e.getModifiersEx() & MASK ) == 0 ){
        			int index = indexOf( dockable );
        			if( index < 0 )
        				return;
        			
        			DockableHandle handle = handles.dockables().get( index );
        			DockTitle title = handle.getTitle();
	        		
		            if( getFrontDockable() == dockable && title.isActive() ){
		                getController().setFocusedDockable( new DefaultFocusRequest( FlapDockStation.this, null, true ));
		                setFrontDockable( null );
		            }
		            else
		                getController().setFocusedDockable( new DefaultFocusRequest( dockable, null, true ));
        		}
        	}
        }
    }
    
    /**
     * The background algorithm of this {@link FlapDockStation}.
     * @author Benjamin Sigg
     */
    private class Background extends BackgroundAlgorithm implements StationBackgroundComponent{
    	/**
    	 * Creates a new algorithm
    	 */
    	public Background(){
    		super( StationBackgroundComponent.KIND, ThemeManager.BACKGROUND_PAINT + ".station.flap" );
    	}

		public DockStation getStation(){
			return FlapDockStation.this;
		}

		public Component getComponent(){
			return FlapDockStation.this.getComponent();
		}
    }
}
