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
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.SwingUtilities;
import javax.swing.Timer;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.DockUI;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.accept.DockAcceptance;
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.action.DockActionSource;
import bibliothek.gui.dock.action.ListeningDockAction;
import bibliothek.gui.dock.action.LocationHint;
import bibliothek.gui.dock.component.DefaultDockStationComponentRootHandler;
import bibliothek.gui.dock.component.DockComponentRootHandler;
import bibliothek.gui.dock.displayer.DisplayerCombinerTarget;
import bibliothek.gui.dock.event.DoubleClickListener;
import bibliothek.gui.dock.layout.DockableProperty;
import bibliothek.gui.dock.layout.location.AsideRequest;
import bibliothek.gui.dock.station.AbstractDockStation;
import bibliothek.gui.dock.station.Combiner;
import bibliothek.gui.dock.station.DisplayerCollection;
import bibliothek.gui.dock.station.DisplayerFactory;
import bibliothek.gui.dock.station.DockableDisplayer;
import bibliothek.gui.dock.station.PlaceholderMapping;
import bibliothek.gui.dock.station.StationDragOperation;
import bibliothek.gui.dock.station.StationDropItem;
import bibliothek.gui.dock.station.StationDropOperation;
import bibliothek.gui.dock.station.StationPaint;
import bibliothek.gui.dock.station.layer.DockStationDropLayer;
import bibliothek.gui.dock.station.screen.BoundaryRestriction;
import bibliothek.gui.dock.station.screen.DefaultScreenDockFullscreenStrategy;
import bibliothek.gui.dock.station.screen.FullscreenActionSource;
import bibliothek.gui.dock.station.screen.ScreenDockFullscreenFilter;
import bibliothek.gui.dock.station.screen.ScreenDockFullscreenStrategy;
import bibliothek.gui.dock.station.screen.ScreenDockProperty;
import bibliothek.gui.dock.station.screen.ScreenDockStationExtension;
import bibliothek.gui.dock.station.screen.ScreenDockStationFactory;
import bibliothek.gui.dock.station.screen.ScreenDockStationListener;
import bibliothek.gui.dock.station.screen.ScreenDockWindow;
import bibliothek.gui.dock.station.screen.ScreenDockWindowConfiguration;
import bibliothek.gui.dock.station.screen.ScreenDockWindowFactory;
import bibliothek.gui.dock.station.screen.ScreenDockWindowListener;
import bibliothek.gui.dock.station.screen.ScreenDropSizeStrategy;
import bibliothek.gui.dock.station.screen.ScreenFullscreenAction;
import bibliothek.gui.dock.station.screen.layer.ScreenLayer;
import bibliothek.gui.dock.station.screen.layer.ScreenWindowLayer;
import bibliothek.gui.dock.station.screen.magnet.AttractorStrategy;
import bibliothek.gui.dock.station.screen.magnet.DefaultMagnetStrategy;
import bibliothek.gui.dock.station.screen.magnet.MagnetController;
import bibliothek.gui.dock.station.screen.magnet.MagnetStrategy;
import bibliothek.gui.dock.station.screen.magnet.MultiAttractorStrategy;
import bibliothek.gui.dock.station.screen.window.DefaultScreenDockWindowConfiguration;
import bibliothek.gui.dock.station.screen.window.DefaultScreenDockWindowFactory;
import bibliothek.gui.dock.station.screen.window.ScreenDockWindowClosingStrategy;
import bibliothek.gui.dock.station.screen.window.ScreenDockWindowHandle;
import bibliothek.gui.dock.station.screen.window.WindowConfiguration;
import bibliothek.gui.dock.station.support.CombinerSource;
import bibliothek.gui.dock.station.support.CombinerSourceWrapper;
import bibliothek.gui.dock.station.support.CombinerTarget;
import bibliothek.gui.dock.station.support.ConvertedPlaceholderListItem;
import bibliothek.gui.dock.station.support.DockablePlaceholderList;
import bibliothek.gui.dock.station.support.DockableShowingManager;
import bibliothek.gui.dock.station.support.Enforcement;
import bibliothek.gui.dock.station.support.PlaceholderList.Filter;
import bibliothek.gui.dock.station.support.PlaceholderList.Level;
import bibliothek.gui.dock.station.support.PlaceholderListItemAdapter;
import bibliothek.gui.dock.station.support.PlaceholderListItemConverter;
import bibliothek.gui.dock.station.support.PlaceholderListMapping;
import bibliothek.gui.dock.station.support.PlaceholderMap;
import bibliothek.gui.dock.station.support.PlaceholderMetaMap;
import bibliothek.gui.dock.station.support.PlaceholderStrategy;
import bibliothek.gui.dock.themes.DefaultDisplayerFactoryValue;
import bibliothek.gui.dock.themes.DefaultStationPaintValue;
import bibliothek.gui.dock.themes.StationCombinerValue;
import bibliothek.gui.dock.themes.StationThemeItemValue;
import bibliothek.gui.dock.themes.ThemeManager;
import bibliothek.gui.dock.title.ControllerTitleFactory;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.gui.dock.title.DockTitleVersion;
import bibliothek.gui.dock.util.DirectWindowProvider;
import bibliothek.gui.dock.util.DockProperties;
import bibliothek.gui.dock.util.DockUtilities;
import bibliothek.gui.dock.util.PropertyKey;
import bibliothek.gui.dock.util.PropertyValue;
import bibliothek.gui.dock.util.WindowProvider;
import bibliothek.gui.dock.util.WindowProviderListener;
import bibliothek.gui.dock.util.extension.Extension;
import bibliothek.gui.dock.util.extension.ExtensionName;
import bibliothek.gui.dock.util.property.ConstantPropertyFactory;
import bibliothek.gui.dock.util.property.DynamicPropertyFactory;
import bibliothek.gui.dock.util.property.PropertyFactory;
import bibliothek.util.Path;
import bibliothek.util.Todo;
import bibliothek.util.Todo.Compatibility;
import bibliothek.util.Todo.Priority;
import bibliothek.util.Todo.Version;

/**
 * A {@link DockStation} which is the whole screen. Every child of this
 * station is a window. These windows can be moved and resized by the user.<br>
 * This station tries to register a {@link DockTitleVersion} with 
 * the key {@link #TITLE_ID}.
 * 
 * @author Benjamin Sigg
 */
public class ScreenDockStation extends AbstractDockStation {
    /** The key for the {@link DockTitleVersion} of this station */
    public static final String TITLE_ID = "screen dock";
    
    /** This id is forwarded to {@link Extension}s which load additional {@link DisplayerFactory}s */
    public static final String DISPLAYER_ID = "screen";
    
    /** Path of an {@link ExtensionName} for creating additional {@link AttractorStrategy} */
    public static final Path ATTRACTOR_STRATEGY_EXTENSION = new Path( "dock.AttractorStrategy" );
    
    /** Path of an {@link ExtensionName} for creating {@link ScreenDockStationExtension}s */
    public static final Path STATION_EXTENSION = new Path( "dock.ScreenDockStation" );
    
    /** Name of a parameter of an {@link ExtensionName} pointing to <code>this</code>. */
    public static final String EXTENSION_PARAM = "station";
    
    /** a key for a property telling which boundaries a {@link ScreenDockWindow} can have */
    public static final PropertyKey<BoundaryRestriction> BOUNDARY_RESTRICTION = 
        new PropertyKey<BoundaryRestriction>( "ScreenDockStation.boundary_restriction",
        		new ConstantPropertyFactory<BoundaryRestriction>( BoundaryRestriction.MEDIUM ), true );
    
    /** a key for a property telling how to create new windows */
    public static final PropertyKey<ScreenDockWindowFactory> WINDOW_FACTORY =
        new PropertyKey<ScreenDockWindowFactory>( "ScreenDockStation.window_factory", 
        		new ConstantPropertyFactory<ScreenDockWindowFactory>( new DefaultScreenDockWindowFactory() ), true );
    
    /** strategy for closing {@link ScreenDockWindow}s, default is <code>null</code> */
    public static final PropertyKey<ScreenDockWindowClosingStrategy> WINDOW_CLOSING_STRATEGY =
    		new PropertyKey<ScreenDockWindowClosingStrategy>( "ScreenDockStation.window_closing" );
    
    /** 
     * A key for a property telling how to configure new windows. Replacing the configuration always leads to closing
     * and recreating all windows. 
     */
    public static final PropertyKey<ScreenDockWindowConfiguration> WINDOW_CONFIGURATION = 
    		new PropertyKey<ScreenDockWindowConfiguration>( "ScreenDockStation.window_configuration",
    			new PropertyFactory<ScreenDockWindowConfiguration>(){
    				public ScreenDockWindowConfiguration getDefault( PropertyKey<ScreenDockWindowConfiguration> key, DockProperties properties ){
    					return new DefaultScreenDockWindowConfiguration( properties.getController() );
    				}
    					
    				public ScreenDockWindowConfiguration getDefault( PropertyKey<ScreenDockWindowConfiguration> key ){
    					return new DefaultScreenDockWindowConfiguration( null );	
    				}
    			}, true);
    
    /** a key for a property telling how to handle fullscreen mode */
    public static final PropertyKey<ScreenDockFullscreenStrategy> FULL_SCREEN_STRATEGY =
    	new PropertyKey<ScreenDockFullscreenStrategy>( "ScreenDockStation.full_screen_strategy",
    			new PropertyFactory<ScreenDockFullscreenStrategy>() {
					public ScreenDockFullscreenStrategy getDefault( PropertyKey<ScreenDockFullscreenStrategy> key, DockProperties properties ) {
						return new DefaultScreenDockFullscreenStrategy();
					}
					public ScreenDockFullscreenStrategy getDefault( PropertyKey<ScreenDockFullscreenStrategy> key ){
						return new DefaultScreenDockFullscreenStrategy();
					}
    			}, true );
    
    /** global setting to change the effect happening on a double click */
    public static final PropertyKey<Boolean> EXPAND_ON_DOUBLE_CLICK =
    	new PropertyKey<Boolean>( "ScreenDockStation.double_click_fullscreen", new ConstantPropertyFactory<Boolean>( true ), true );
    
    /** time in milliseconds a {@link ScreenDockWindow} is prevented from stealing the focus after the {@link #getOwner() owner} of this station changed. A value
     * of <code>null</code> disables the focus stealing prevention. */
    public static final PropertyKey<Integer> PREVENT_FOCUS_STEALING_DELAY = 
    		new PropertyKey<Integer>( "ScreenDockStation.prevent_focus_stealing_delay", new ConstantPropertyFactory<Integer>( 500 ), false );
    
    /** the {@link MagnetStrategy} decides how two {@link ScreenDockWindow}s attract each other */
    public static final PropertyKey<MagnetStrategy> MAGNET_STRATEGY = 
    		new PropertyKey<MagnetStrategy>( "ScreenDockStation.magnet_strategy", new ConstantPropertyFactory<MagnetStrategy>( new DefaultMagnetStrategy() ){
    			public MagnetStrategy getDefault( PropertyKey<MagnetStrategy> key ){
    				return null;
    			};
    		}, true );
    
    /** the {@link AttractorStrategy} that tells whether two {@link Dockable}s attract each other */
    public static final PropertyKey<AttractorStrategy> ATTRACTOR_STRATEGY = 
    		new PropertyKey<AttractorStrategy>( "ScreenDockStation.attractor_strategy", new DynamicPropertyFactory<AttractorStrategy>(){
    			public AttractorStrategy getDefault( PropertyKey<AttractorStrategy> key, DockProperties properties ){
    				ExtensionName<AttractorStrategy> name = new ExtensionName<AttractorStrategy>( ATTRACTOR_STRATEGY_EXTENSION, AttractorStrategy.class, null );
    				List<AttractorStrategy> extensions = properties.getController().getExtensions().load( name );
    				
    				MultiAttractorStrategy strategy = new MultiAttractorStrategy();
    				for( AttractorStrategy extension : extensions ){
    					strategy.add( extension );
    				}
    				
    				return strategy;
    			}
    		}, true );
    

    /** key for the {@link ScreenDropSizeStrategy} that is used when dropping a {@link Dockable} onto this station */
    public static final PropertyKey<ScreenDropSizeStrategy> DROP_SIZE_STRATEGY = 
    		new PropertyKey<ScreenDropSizeStrategy>( "ScreendockStation.drop_size_strategy", new ConstantPropertyFactory<ScreenDropSizeStrategy>( ScreenDropSizeStrategy.CURRENT_SIZE ), true  );
    
    /** The visibility state of the windows */
    private boolean showing = false;
    
    /** A list of all windows that are used by this station */
    private DockablePlaceholderList<ScreenDockWindowHandle> dockables = new DockablePlaceholderList<ScreenDockWindowHandle>();
    
    /** All listeners that were added to this station */
    private List<ScreenDockStationListener> screenDockStationListeners = new ArrayList<ScreenDockStationListener>();
    
    /** The version of titles that are used */
    private DockTitleVersion version;
    
    /** Extensions to this station, these extensions are loaded with {@link #STATION_EXTENSION} */
    private ScreenDockStationExtension[] extensions;
    
    /** Combiner to merge some {@link Dockable Dockables} */
    private StationCombinerValue combiner;
    
    /** Information about the current movement of a {@link Dockable} */
    private DropInfo dropInfo;
    
    /** Information about the current removal of a {@link Dockable} */
    private StationDragOperation dragInfo;
    
    /** The {@link Window} that is used as parent for the windows */
    private WindowProvider owner;
    
    /** The paint used to draw information on this station */
    private DefaultStationPaintValue stationPaint;
    
    /** A factory to create new {@link DockableDisplayer}*/
    private DefaultDisplayerFactoryValue displayerFactory;
    
    /** The set of {@link DockableDisplayer} used on this station */
    private DisplayerCollection displayers;
    
    /** The window which has currently the focus */
    private ScreenDockWindow frontWindow;
    
    /** A manager for the visibility of the children */
    private DockableShowingManager visibility;
    
    /** An action to enable or disable fullscreen mode of some window */
    private ListeningDockAction fullscreenAction;

    /** tells how much two windows must overlap in order for them to be merged */
    private double dropOverRatio = 0.75;
    
    /** controls attraction between {@link ScreenDockWindow}s */
    private MagnetController magnet;
    
    /** the restrictions of the boundaries of this window*/
    private PropertyValue<BoundaryRestriction> restriction =
        new PropertyValue<BoundaryRestriction>( ScreenDockStation.BOUNDARY_RESTRICTION ){
            @Override
            protected void valueChanged( BoundaryRestriction oldValue, BoundaryRestriction newValue ) {
                checkWindowBoundaries();
            }
    };
    
    /** a factory used to create new windows for this station */
    private PropertyValue<ScreenDockWindowFactory> windowFactory =
        new PropertyValue<ScreenDockWindowFactory>( ScreenDockStation.WINDOW_FACTORY ){
	        @Override
	        protected void valueChanged( ScreenDockWindowFactory oldValue, ScreenDockWindowFactory newValue ) {
	        	updateWindows( true );   
	        }
    };
    
    /** a strategy for telling {@link #windowFactory} how to create new windows */
    private PropertyValue<ScreenDockWindowConfiguration> windowConfiguration =
    	new PropertyValue<ScreenDockWindowConfiguration>( ScreenDockStation.WINDOW_CONFIGURATION ){
    		@Override
    		protected void valueChanged( ScreenDockWindowConfiguration oldValue, ScreenDockWindowConfiguration newValue ){
	    		updateWindows( true );
    		}
    };
    
    /** the current fullscreen strategy */
    private PropertyValue<ScreenDockFullscreenStrategy> fullscreenStrategy = 
    	new PropertyValue<ScreenDockFullscreenStrategy>( ScreenDockStation.FULL_SCREEN_STRATEGY ) {
			@Override
			protected void valueChanged( ScreenDockFullscreenStrategy oldValue, ScreenDockFullscreenStrategy newValue ) {
				List<ScreenDockWindow> fullscreenWindows = new ArrayList<ScreenDockWindow>();
				for( ScreenDockWindowHandle handle : dockables.dockables() ){
					ScreenDockWindow window = handle.getWindow();
					if( window.isFullscreen() ){
						fullscreenWindows.add( window );
						window.setFullscreen( false );
					}
				}
				
				if( oldValue != null ){
					oldValue.uninstall( ScreenDockStation.this );
				}
				if( newValue != null ){
					newValue.install( ScreenDockStation.this );
				}
				
				for( ScreenDockWindowHandle window : dockables.dockables() ){
					window.getWindow().setFullscreenStrategy( newValue );
				}
				
				for( ScreenDockWindow window : fullscreenWindows ){
					window.setFullscreen( true );
				}
			}
		};
		
	/** whether the children of this station expand on double click to fullscreen */
	private PropertyValue<Boolean> expandOnDoubleClick =
		new PropertyValue<Boolean>( EXPAND_ON_DOUBLE_CLICK ){
			@Override
			protected void valueChanged( Boolean oldValue, Boolean newValue ){
				if( oldValue.booleanValue() != newValue.booleanValue() ){	
					DockController controller = getController();
					if( controller != null ){
						if( newValue ){
							controller.getDoubleClickController().addListener( doubleClickListener );
						}
						else{
							controller.getDoubleClickController().removeListener( doubleClickListener );
						}
					}
				}
			}
		};
		
	/** current {@link PlaceholderStrategy} */
	private PropertyValue<PlaceholderStrategy> placeholderStrategy = new PropertyValue<PlaceholderStrategy>(PlaceholderStrategy.PLACEHOLDER_STRATEGY) {
		@Override
		protected void valueChanged( PlaceholderStrategy oldValue, PlaceholderStrategy newValue ){
			dockables.setStrategy( newValue );
		}
	}; 
		
	/** monitors the children of this station and reacts on double clicks by changing their fullscreen state */
	private DoubleClickListener doubleClickListener = new DoubleClickListener() {
		public DockElement getTreeLocation(){
			return ScreenDockStation.this;
		}
		
		public boolean process( Dockable dockable, MouseEvent event ){
			DockStation parent = dockable.getDockParent();
			while( parent != null && parent != ScreenDockStation.this ){
				dockable = parent.asDockable();
				parent = dockable == null ? null : dockable.getDockParent();
			}
			if( parent == ScreenDockStation.this ){
				for( ScreenDockFullscreenFilter filter : filters ){
					if( !filter.isFullscreenEnabled( dockable )){
						return false;
					}
				}
				
				boolean state = isFullscreen( dockable );
				setFullscreen( dockable, !state );
				return true;
			}
			
			return false;
		}
	};
	
	/** this strategy tells how to drop a {@link Dockable} onto this station */
	private PropertyValue<ScreenDropSizeStrategy> dropSizeStrategy = new PropertyValue<ScreenDropSizeStrategy>( DROP_SIZE_STRATEGY ){
		@Override
		protected void valueChanged( ScreenDropSizeStrategy oldValue, ScreenDropSizeStrategy newValue ){
			if( oldValue != null ){
				oldValue.uninstall( ScreenDockStation.this );
			}
			if( newValue != null ){
				newValue.install( ScreenDockStation.this );
			}
		}
	};
    
	/** a list of filters that can disable fullscreen mode for some windows */
	private List<ScreenDockFullscreenFilter> filters = new ArrayList<ScreenDockFullscreenFilter>();
	
	/** all the {@link FullscreenActionSource}s that are currently used */
	private List<FullscreenActionSource> filterSources = new LinkedList<FullscreenActionSource>();
	
    /**
     * Constructs a new <code>ScreenDockStation</code>.
     * @param owner the window which will be used as parent for the 
     * windows of this station, must not be <code>null</code>
     */
    public ScreenDockStation( Window owner ){
        if( owner == null )
            throw new IllegalArgumentException( "Owner must not be null" );
        
        init( new DirectWindowProvider( owner ));
    }
    
    /**
     * Constructs a new <code>ScreenDockStation</code>.
     * @param owner the window which will be used as parent for
     * the windows of this station, must not be <code>null</code>
     */
    public ScreenDockStation( WindowProvider owner ){
    	if( owner == null )
            throw new IllegalArgumentException( "Owner must not be null" );
    	
    	init( owner );
    }
    
    private void init( WindowProvider owner ){
        visibility = new DockableShowingManager( listeners );
        this.owner = owner;
        
        displayerFactory = new DefaultDisplayerFactoryValue( ThemeManager.DISPLAYER_FACTORY + ".screen", this );
        combiner = new StationCombinerValue( ThemeManager.COMBINER + ".screen", this );
        
        displayers = new DisplayerCollection( this, displayerFactory, DISPLAYER_ID );
        fullscreenAction = createFullscreenAction();
        
        stationPaint = new DefaultStationPaintValue( ThemeManager.STATION_PAINT + ".screen", this );
        magnet = new MagnetController( this );
        
        addScreenDockStationListener( new ScreenWindowListener() );
        
        owner.addWindowProviderListener( new WindowProviderListener(){
        	public void visibilityChanged( WindowProvider provider, boolean showing ){
        		// ignore
        	}
        	public void windowChanged (WindowProvider provider, Window window ){
        		updateWindows();
        	}
        });
    }
    
    protected DockComponentRootHandler createRootHandler() {
    	return new DefaultDockStationComponentRootHandler( this, displayers );
    }
    
    /**
     * Creates an {@link DockAction action} which is added to all children
     * of this station. The action allows the user to expand a child to
     * fullscreen. The action is also added to subchildren, but the effect
     * does only affect direct children of this station.
     * @return the action or <code>null</code> if this feature should be
     * disabled, or the action is {@link #setFullscreenAction(ListeningDockAction) set later}
     */
    protected ListeningDockAction createFullscreenAction(){
    	return new ScreenFullscreenAction( this );
    }
    
    /**
     * Adds <code>listener</code> to this station.
     * @param listener the new listener
     */
    public void addScreenDockStationListener( ScreenDockStationListener listener ){
    	screenDockStationListeners.add( listener );
    }
    
    /**
     * Removes <code>listener</code> from this station.
     * @param listener the listener to remove
     */
    public void removeScreenDockStationListener( ScreenDockStationListener listener ){
    	screenDockStationListeners.remove( listener );
    }
    
    /**
     * Gets all the {@link ScreenDockStationListener}s that were added to this station.
     * @return all the listeners
     */
    protected ScreenDockStationListener[] screenDockStationListeners(){
    	return screenDockStationListeners.toArray( new ScreenDockStationListener[ screenDockStationListeners.size() ] );
    }
    
    /**
     * Sets an {@link DockAction action} which allows to expand children. This
     * method can only be invoked if there is not already set an action. It is
     * a condition that {@link #createFullscreenAction()} returns <code>null</code>
     * @param fullScreenAction the new action
     * @throws IllegalStateException if there is already an action present
     */
    public void setFullscreenAction( ListeningDockAction fullScreenAction ) {
        if( this.fullscreenAction != null )
            throw new IllegalStateException( "The fullScreenAction can only be set once" );
        this.fullscreenAction = fullScreenAction;
    }

    public DockActionSource getDirectActionOffers( Dockable dockable ) {
        if( fullscreenAction == null )
            return null;
        else{
            return createFullscreenSource( dockable, new LocationHint( LocationHint.DIRECT_ACTION, LocationHint.VERY_RIGHT ));
        }
    }

    public DockActionSource getIndirectActionOffers( Dockable dockable ) {
        if( fullscreenAction == null )
            return null;

        DockStation parent = dockable.getDockParent();
        if( parent == null )
            return null;

        if( parent instanceof ScreenDockStation )
            return null;

        dockable = parent.asDockable();
        if( dockable == null )
            return null;

        parent = dockable.getDockParent();
        if( parent != this )
            return null;

        return createFullscreenSource( dockable, new LocationHint( LocationHint.INDIRECT_ACTION, LocationHint.VERY_RIGHT ));
    }
    
    private DockActionSource createFullscreenSource( final Dockable dockable, LocationHint hint ){
    	return new FullscreenActionSource( fullscreenAction, hint ){
    		private boolean listening;
    		
    		protected boolean isFullscreenEnabled(){
    			for( ScreenDockFullscreenFilter filter : filters ){
    				if( !filter.isFullscreenEnabled( dockable )){
    					return false;
    				}
    			}
    			return true;
    		}
    		
    		protected void listen( boolean listening ){
    			if( this.listening != listening ){
    				this.listening = listening;
	    			if( listening ){
	    				filterSources.add( this );
	    			}
	    			else{
	    				filterSources.remove( this );
	    			}
    			}
    		}
    	};
    }
    
    /**
     * Gets the {@link DisplayerFactory} that is used by this station
     * to create an underground for its children.
     * @return the factory
     * @see StationThemeItemValue#setDelegate(Object)
     */
    public DefaultDisplayerFactoryValue getDisplayerFactory() {
        return displayerFactory;
    }
    
    /**
     * Gets the current set of {@link DockableDisplayer displayers} used
     * on this station.
     * @return the set of displayers
     */
    public DisplayerCollection getDisplayers() {
        return displayers;
    }
    
    /**
     * Gets the {@link Combiner} that is used to merge two {@link Dockable Dockables}
     * on this station.
     * @return the combiner
     * @see StationThemeItemValue#setDelegate(Object)
     */
    public StationCombinerValue getCombiner() {
        return combiner;
    }
    
    /**
     * Gets the {@link StationPaint} for this station. The paint is needed to
     * paint information on this station, when a {@link Dockable} is dragged
     * or moved.
     * @return the paint
     * @see StationThemeItemValue#setDelegate(Object)
     */
    public DefaultStationPaintValue getPaint() {
        return stationPaint;
    }
    
    @Override
    protected void callDockUiUpdateTheme() throws IOException {
    	DockUI.updateTheme( this, new ScreenDockStationFactory( owner ) );
    }
    
    @Override
    public void setController( DockController controller ) {
    	DockController old = getController();
    	if( old != null ){
    		if( expandOnDoubleClick.getValue() ){
    			old.getDoubleClickController().removeListener( doubleClickListener );
    		}
    		dockables.unbind();
    	}
    	
        version = null;
        super.setController( controller );
        displayers.setController( controller );
        
        if( controller != null ){
            version = controller.getDockTitleManager().getVersion( TITLE_ID, ControllerTitleFactory.INSTANCE );
            if( expandOnDoubleClick.getValue() ){
            	controller.getDoubleClickController().addListener( doubleClickListener );
            }
            dockables.bind();
            
            List<ScreenDockStationExtension> list = controller.getExtensions().load( new ExtensionName<ScreenDockStationExtension>( STATION_EXTENSION, ScreenDockStationExtension.class,  EXTENSION_PARAM, this ) );
            extensions = list.toArray( new ScreenDockStationExtension[ list.size() ] );
        }
        else{
        	extensions = null;
        }
        
        stationPaint.setController( controller );
        combiner.setController( controller );
        displayerFactory.setController( controller );
        
        restriction.setProperties( controller );
        windowFactory.setProperties( controller );
        windowConfiguration.setProperties( controller );
        fullscreenStrategy.setProperties( controller );
        placeholderStrategy.setProperties( controller );
        magnet.setController( controller );
        dropSizeStrategy.setProperties( controller );
        
        if( fullscreenAction != null ){
        	fullscreenAction.setController( controller );
        }
        
        for( ScreenDockWindowHandle window : dockables.dockables() ){
            window.getWindow().setController( controller );
        }
    }
    
    public int getDockableCount() {
        return dockables.dockables().size();
    }

    public Dockable getDockable( int index ) {
        return dockables.dockables().get( index ).asDockable();
    }
    
    /**
     * Gets the index of a {@link Dockable} that is shown on this
     * station. A call to {@link #getDockable(int)} with the result of this
     * method would return <code>dockable</code>, if <code>dockable</code>
     * is on this station.
     * @param dockable the item to search
     * @return the index of the item or -1 if not found
     */
    public int indexOf( Dockable dockable ){
    	Filter<ScreenDockWindowHandle> handles = dockables.dockables();
    	
    	for( int i = 0, n = handles.size(); i<n; i++ ){
            ScreenDockWindowHandle window = handles.get( i );
            if( window.asDockable() == dockable )
                return i;
        }
        
        return -1;
    }
    
    public PlaceholderMapping getPlaceholderMapping() {
    	return new PlaceholderListMapping( this, dockables ){
    		public DockableProperty getLocationAt( Path placeholder ) {
    			DockablePlaceholderList<ScreenDockWindowHandle>.Item item = dockables.getItem( placeholder );
    			if( item == null ){
    				return null;
    			}
    			
    			ScreenDockWindowHandle handle = item.getDockable();
    			if( handle != null ){
    				Dockable dockable = handle.asDockable();
    				ScreenDockProperty property = getLocation( dockable, dockable );
    				property.setPlaceholder( placeholder );
    				return property;
    			}
    			else if( item.contains( "x", "y", "width", "height" )){    				
    				int x = item.getInt( "x" );
    				int y = item.getInt( "y" );
    				int width = item.getInt( "width" );
    				int height = item.getInt( "height" );
    				
    				return new ScreenDockProperty( x, y, width, height, placeholder );
    			}
    			else{
    				return null;
    			}
    		}
    	};
    }
    
    public PlaceholderMap getPlaceholders(){
    	return dockables.toMap();
    }
    
    /**
     * Gets the placeholders of this station using a {@link PlaceholderListItemConverter} to
     * encode the children of this station. To be exact, the converter puts the following
     * parameters for each {@link Dockable} into the map:
     * <ul>
     * 	<li>id: the integer from <code>children</code></li>
     * 	<li>x, y, width, height: the location of the child if not in fullscreen mode</li>
     *  <li>fullscreen: whether the child is in fullscreen mode</li>
     *  <li>placeholder: the placeholder of the element, might not be written</li>
     * </ul> 
     * @param children a unique identifier for each child of this station
     * @return the map 
     */
    public PlaceholderMap getPlaceholders( final Map<Dockable, Integer> children ){
    	final PlaceholderStrategy strategy = getPlaceholderStrategy();
    	
    	return dockables.toMap( new PlaceholderListItemAdapter<Dockable, ScreenDockWindowHandle>() {
    		@Override
    		public ConvertedPlaceholderListItem convert( int index, ScreenDockWindowHandle dockable ) {
    			Integer id = children.get( dockable.asDockable() );
    			if( id == null ){
    				return null;
    			}
    			
    			saveLocation( index );
    			
    			ConvertedPlaceholderListItem item = new ConvertedPlaceholderListItem();
    			Rectangle bounds = dockable.getBounds();
    			item.putInt( "id", id );
    			item.putInt( "x", bounds.x );
    			item.putInt( "y", bounds.y );
    			item.putInt( "width", bounds.width );
    			item.putInt( "height", bounds.height );
    			item.putBoolean( "fullscreen", dockable.getWindow().isFullscreen() );
	    		
	    		if( strategy != null ){
	    			Path placeholder = strategy.getPlaceholderFor( dockable.asDockable() );
	    			if( placeholder != null ){
	    				item.putString( "placeholder", placeholder.toString() );
	    				item.setPlaceholder( placeholder );
	    			}
	    		}
    			return item;
    		}
		});
    }
    
    public void setPlaceholders( PlaceholderMap placeholders ){
    	DockUtilities.checkLayoutLocked();
    	if( getDockableCount() > 0 ){
    		throw new IllegalStateException( "there are children on this station" );
    	}
    	try{
    		DockablePlaceholderList<ScreenDockWindowHandle> next = new DockablePlaceholderList<ScreenDockWindowHandle>( placeholders );
    		if( getController() != null ){
    			dockables.setStrategy( null );
    			dockables.unbind();
    			dockables = next;
    			dockables.bind();
    			dockables.setStrategy( getPlaceholderStrategy() );
    		}
    		else{
    			dockables = next;
    		}
    	}
    	catch( IllegalArgumentException ex ){
    		// ignore
    	}
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
    		}
    		
	    	DockablePlaceholderList<ScreenDockWindowHandle> next = new DockablePlaceholderList<ScreenDockWindowHandle>();
	    	
			if( controller != null ){
				dockables.setStrategy( null );
				dockables.unbind();
				dockables = next;
				dockables.bind();
				dockables.setStrategy( getPlaceholderStrategy() );
			}
			else{
				dockables = next;
			}
	    	
	    	next.read( map, new PlaceholderListItemAdapter<Dockable, ScreenDockWindowHandle>(){
	    		private DockHierarchyLock.Token token;
	    		
				@Override
				public ScreenDockWindowHandle convert( ConvertedPlaceholderListItem item ){
					int id = item.getInt( "id" );
					Dockable dockable = children.get( id );
					if( dockable != null ){
						DockUtilities.ensureTreeValidity( ScreenDockStation.this, dockable );
						token = DockHierarchyLock.acquireLinking( ScreenDockStation.this, dockable );
						
						int x = item.getInt( "x" );
						int y = item.getInt( "y" );
						int width = item.getInt( "width" );
						int height = item.getInt( "height" );
						boolean fullscreen = item.getBoolean( "fullscreen" );
						
				        listeners.fireDockableAdding( dockable );
				        
				        WindowConfiguration configuration = getConfiguration( dockable );
				        ScreenDockWindow window = createWindow( configuration );
				        ScreenDockWindowHandle handle = new ScreenDockWindowHandle( dockable, window, configuration );
				        window.setController( getController() );
				        window.setFullscreenStrategy( getFullscreenStrategy() );
				        window.setDockable( dockable );
				        window.setWindowBounds( new Rectangle( x, y, width, height ) );
				        window.setVisible( isShowing() );
				        window.validate();
				        window.setFullscreen( fullscreen );
				        
				        return handle;
					}
					return null;
				}
				
				@Override
				public void added( ScreenDockWindowHandle dockable ){
					try{
						dockable.asDockable().setDockParent( ScreenDockStation.this );
						for( ScreenDockStationListener listener : screenDockStationListeners() ){
				        	listener.windowRegistering( ScreenDockStation.this, dockable.asDockable(), dockable.getWindow() );
				        }
						listeners.fireDockableAdded( dockable.asDockable() );
					}
					finally{
						token.release();
					}
				}
			});
    	}
    	finally{
    		if( controller != null ){
    			controller.meltLayout();
    		}
    	}
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

    public Dockable getFrontDockable() {
        if( frontWindow == null )
            return null;
        else
            return frontWindow.getDockable();
    }

    public void setFrontDockable( Dockable dockable ) {
        Dockable oldSelected = getFrontDockable();
        frontWindow = getWindow( dockable );

        if( frontWindow != null ){
            frontWindow.toFront();
        }
        
        Dockable newSelected = getFrontDockable();
        if( oldSelected != newSelected )
            listeners.fireDockableSelected( oldSelected, newSelected );
    }

    public DockStationDropLayer[] getLayers() {
    	DockStationDropLayer[] result = new DockStationDropLayer[ getDockableCount()+1 ];
    	result[0] = new ScreenLayer( this );
    	for( int i = 1; i < result.length; i++ ){
    		result[i] = new ScreenWindowLayer( this, getWindow( i-1 ));
    	}
    	return result;
    }
    
    public StationDropOperation prepareDrop( StationDropItem item ){
        return prepare( item, item.getDockable().getDockParent() != this );
    }
    
    public StationDragOperation prepareDrag( Dockable dockable ){
    	final ScreenDockWindow window = getWindow( dockable );
    	if( dragInfo != null ){
			dragInfo.canceled();
		}
    	if( window != null ){
    		window.setPaintRemoval( true );
    		dragInfo = new StationDragOperation(){
				public void succeeded(){
					window.setPaintRemoval( false );
					dragInfo = null;
				}
				
				public void canceled(){
					window.setPaintRemoval( false );
					dragInfo = null;
				}
			};
    	}
    	return dragInfo;
    }
    
    public StationDropOperation prepare( StationDropItem item, boolean drop ) {
    	DropInfo dropInfo = new DropInfo();

        dropInfo.x = item.getMouseX();
        dropInfo.y = item.getMouseY();
        dropInfo.titleX = item.getTitleX();
        dropInfo.titleY = item.getTitleY();
        dropInfo.dockable = item.getDockable();
        dropInfo.move = !drop;
        
        Enforcement force = Enforcement.HARD;
        dropInfo.combine = searchCombineDockable( dropInfo.x, dropInfo.y, dropInfo.dockable, true );
        if( dropInfo.combine == null ){
        	force = Enforcement.EXPECTED;
        	dropInfo.combine = searchCombineDockable( dropInfo.x, dropInfo.y, dropInfo.dockable, false );
        }
        
        if( dropInfo.combine != null && dropInfo.combine.getDockable() == dropInfo.dockable )
            dropInfo.combine = null;
        
        if( dropInfo.combine != null ){
        	dropInfo.combiner = combiner.prepare( dropInfo, force );
        	if( dropInfo.combiner == null ){
        		dropInfo.combine = null;
        	}
        }
        
        if( !checkDropInfo( dropInfo ) ){
        	dropInfo = null;
        }
        return dropInfo;
    }
    
    /**
     * Ensures that the desired location where to insert the next child is valid.
     * @param dropInfo information about the element to drop
     * @return <code>true</code> if <code>dropInfo</code> is valid, <code>false</code> otherwise
     */
    private boolean checkDropInfo( DropInfo dropInfo ){
        if( dropInfo.combine != null ){
            if( !accept( dropInfo.dockable ) || 
                    !dropInfo.dockable.accept( this, dropInfo.combine.getDockable() ) ||
                    !dropInfo.combine.getDockable().accept( this, dropInfo.dockable ) ||
                    !getController().getAcceptance().accept( this, dropInfo.combine.getDockable(), dropInfo.dockable )){
                return false;
            }
        }
        else{
            if( !accept( dropInfo.dockable ) ||
                    !dropInfo.dockable.accept( this ) ||
                    !getController().getAcceptance().accept( this, dropInfo.dockable )){
                return false;
            }
        }
        return true;
    }

    
    
    /**
     * Searches a window on the coordinates x/y which can be used to create
     * a combination with <code>drop</code>.
     * @param x the x-coordinate on the screen
     * @param y die y-coordinate on the screen
     * @param drop the {@link Dockable} which might be combined with a window
     * @param combineArea whether the point <code>x/y</code> must be over the
     * {@link ScreenDockWindow#inCombineArea(int, int) combine area} or just
     * over the window.
     * @return the window which might become the parent of <code>drop</code>.
     */
    protected ScreenDockWindow searchCombineDockable( int x, int y, Dockable drop, boolean combineArea ){
        for( ScreenDockWindowHandle handle : dockables.dockables() ){
        	ScreenDockWindow window = handle.getWindow();
        	
        	boolean candidate;
        	if( combineArea ){
        		candidate = window.inCombineArea( x, y );
        	}
        	else{
        		candidate = window.contains( x, y );
        	}
        	
            if( candidate ){
                Dockable child = window.getDockable();
                
                if( DockUtilities.acceptable( this, child, drop ) ){
                	return window;
                }
            }
        }
        
        return null;
    }
    
    public void drop( Dockable dockable ) {
        Window owner = getOwner();
        
        int x = 30;
        int y = 30;
        
        if( owner != null ){
            x += owner.getX();
            y += owner.getY();
        }
        
        Dimension preferred = dropSizeStrategy.getValue().getAddSize( this, dockable );
        int width = Math.max( preferred.width, 100 );
        int height = Math.max( preferred.height, 100 );
        
        if( !drop( dockable, new ScreenDockProperty( x, y, width, height ) ) ){
        	addDockable( dockable, new Rectangle( x, y, width, height ) );
        }
    }

    public DockableProperty getDockableProperty( Dockable dockable, Dockable target ) {
    	return getLocation( dockable, target );
    }
    
    /**
     * Gets the location of <code>dockable</code> and its current state.
     * @param dockable some child of this station
     * @param target the final element for which the location is needed
     * @return the location, not <code>null</code>
     */
    public ScreenDockProperty getLocation( Dockable dockable, Dockable target ){
    	int index = indexOf( dockable );
    	ScreenDockWindow window = getWindow( index );
        if( window == null ){
        	throw new IllegalArgumentException( "dockable not child of this station" );
        }
        
        Rectangle bounds = null;
        boolean fullscreen = window.isFullscreen();
        
        if( fullscreen ){
        	bounds = window.getNormalBounds();
        }
        if( bounds == null ){
    		bounds = window.getWindowBounds();
    	}
        
    	PlaceholderStrategy strategy = getPlaceholderStrategy();
    	Path placeholder = null;
    	if( strategy != null ){
    		placeholder = strategy.getPlaceholderFor( target == null ? dockable : target );
    		if( placeholder != null ){
    			dockables.dockables().addPlaceholder( index, placeholder );
    		}
    	}
        
        return new ScreenDockProperty( bounds.x, bounds.y, bounds.width, bounds.height, placeholder, fullscreen );
    }
    
    public void aside( AsideRequest request ){
	    DockableProperty location = request.getLocation();
	    if( location instanceof ScreenDockProperty ){
	    	ScreenDockProperty screenLocation = (ScreenDockProperty)location;
	    	DockablePlaceholderList<ScreenDockWindowHandle>.Item item = getItem( screenLocation );

	    	if( item != null ){
	    		delegate().combine( item, getCombiner(), request );
	    	}
	    	
	    	ScreenDockProperty copy = screenLocation.copy();
	    	copy.setSuccessor( null );
	    	copy.setPlaceholder( request.getPlaceholder() );
	    	request.answer( copy );
	    }
    }
    
    private DockablePlaceholderList<ScreenDockWindowHandle>.Item getItem( ScreenDockProperty property ){
    	Path oldPlaceholder = property.getPlaceholder();
    	if( oldPlaceholder != null ){
    		DockablePlaceholderList<ScreenDockWindowHandle>.Item item = dockables.getItem( oldPlaceholder );
    		if( item != null ){
    			return item;
    		}
    	}
    	ScreenDockStationExtension.DropArguments args = new ScreenDockStationExtension.DropArguments();
		args.setProperty( property );
		args.setBoundsIncludeWindow( true );
		windowAt( args );
		ScreenDockWindow window = args.getWindow();
		if( window != null ){
			return dockables.getItem( window.getDockable() );
		}
		return null;
    }
    
    /**
     * Searches the {@link ScreenDockWindow} which displays the <code>dockable</code>.
     * @param dockable the {@link Dockable} to search
     * @return the window or <code>null</code>
     */
    public ScreenDockWindow getWindow( Dockable dockable ){
        int index = indexOf( dockable );
        if( index < 0 )
            return null;
        
        return getWindow( index );
    }
    
    /**
     * Gets the <code>index</code>'th window of this station. The number
     * of windows is identical to the {@link #getDockableCount() number of Dockables}.
     * @param index the index of the window
     * @return the window which shows the index'th Dockable.
     */
    public ScreenDockWindow getWindow( int index ){
        return getWindowHandle( index ).getWindow();
    }
    
    /**
     * Gets the <code>index</code>'th window of this station. 
     * @param index the index of the window
     * @return the handle for <code>index</code>
     */
    private ScreenDockWindowHandle getWindowHandle( int index ){
    	return dockables.dockables().get( index );
    }

    /**
     * Gets a list of all children of this station that are currently in fullscreen mode.
     * @return a list of children, not <code>null</code>
     */
	public Dockable[] getFullscreenChildren() {
		List<Dockable> result = new ArrayList<Dockable>();
		for( ScreenDockWindowHandle handle : dockables.dockables() ){
			ScreenDockWindow window = handle.getWindow();
			if( window.isFullscreen() ){
				result.add( window.getDockable() );
			}
		}
		return result.toArray( new Dockable[ result.size() ] );
	}
    
    /**
     * Tells whether <code>dockable</code> is currently shown in fullscreen mode.
     * @param dockable the element to check
     * @return the mode
     * @throws IllegalArgumentException if <code>dockable</code> is not known
     */
    public boolean isFullscreen( Dockable dockable ){
    	ScreenDockWindow window = getWindow( dockable );
    	if( window == null ){
    		throw new IllegalArgumentException( "dockable is not known to this station" );
    	}
    	return window.isFullscreen();
    }
    
    /**
     * Changes the fullscreen mode of <code>dockable</code>.
     * @param dockable the element whose mode is to be changed
     * @param fullscreen the new mode
     * @throws IllegalArgumentException if <code>dockable</code> is not known to this station
     */
    public void setFullscreen( Dockable dockable, boolean fullscreen ){
    	ScreenDockWindow window = getWindow( dockable );
    	if( window == null ){
    		throw new IllegalArgumentException( "dockable is not known to this station" );
    	}
    	window.setFullscreen( fullscreen );
    }
    
    /**
     * Adds the new filter <code>filter</code> to this station. The filter can deny {@link Dockable}s the
     * possibility of being in fullscreen mode.
     * @param filter the new filter, not <code>null</code>
     */
    public void addFullscreenFilter( ScreenDockFullscreenFilter filter ){
    	filters.add( filter );
    	filterChanged();
    }
    
    /**
     * Removes <code>filter</code> from this station.
     * @param filter the filter to remove
     * @see #addFullscreenFilter(ScreenDockFullscreenFilter)
     */
    public void removeFullscreenFilter( ScreenDockFullscreenFilter filter ){
    	filters.remove( filter );
    	filterChanged();
    }
    
    private void filterChanged(){
    	for( FullscreenActionSource source : filterSources ){
    		source.update();
    	}
    }
    
    /**
     * Tells this station what to do on a double click on a child. If set
     * to <code>true</code>, then the child's fullscreen mode gets changed.
     * @param expand whether to react on double clicks
     */
    public void setExpandOnDoubleClick( boolean expand ){
    	expandOnDoubleClick.setValue( expand );
    }
    
    /**
     * Resets the expand-on-double-click property to its default value.
     * @see #setExpandOnDoubleClick(boolean)
     */
    public void clearExpandOnDoubleClick(){
    	expandOnDoubleClick.setValue( null );
    }
    
    /**
     * Tells whether children change their fullscreen mode if
     * the user double clicks on them.
     * @return the state
     */
    public boolean isExpandOnDoubleClick(){
    	return expandOnDoubleClick.getValue();
    }
    
    public void move( Dockable dockable, DockableProperty property ) {
    	DockUtilities.checkLayoutLocked();
        if( property instanceof ScreenDockProperty ){
            ScreenDockWindow window = getWindow( dockable );
            if( window == null )
                throw new IllegalArgumentException( "dockable not child of this station" );
            
            ScreenDockProperty bounds = (ScreenDockProperty)property;
            
            window.setWindowBounds( new Rectangle( bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight() ) );
        }
    }

    public boolean canDrag( Dockable dockable ) {
        return true;
    }

    public void drag( Dockable dockable ) {
        if( dockable.getDockParent() != this )
            throw new IllegalArgumentException( "The dockable can't be dragged, it is not child of this station" );
        
        removeDockable( dockable );
    }

    /**
     * Adds a {@link Dockable} on a newly created {@link ScreenDockWindow} to
     * the station. If the station {@link #isShowing() is visible}, the window
     * will be made visible too.
     * @param dockable the {@link Dockable} to show
     * @param bounds the bounds that the window will have
     */
    public void addDockable( Dockable dockable, Rectangle bounds ){
        addDockable( dockable, bounds, true );
    }

    /**
     * Adds a {@link Dockable} on a newly created {@link ScreenDockWindow} to
     * the station. If the station {@link #isShowing() is visible}, the window
     * will be made visible too.
     * @param dockable the {@link Dockable} to show
     * @param bounds the bounds that the window will have
     * @param boundsIncludeWindow if <code>true</code>, the bounds describe the size
     * of the resulting window. Otherwise the size of the window will be a bit larger
     * such that the title can be shown in the new space
     */
    public void addDockable( Dockable dockable, Rectangle bounds, boolean boundsIncludeWindow ){
    	addDockable( dockable, bounds, null, boundsIncludeWindow );
    }
    
    /**
     * Adds a {@link Dockable} on a newly created {@link ScreenDockWindow} to
     * the station. If the station {@link #isShowing() is visible}, the window
     * will be made visible too.
     * @param dockable the {@link Dockable} to show
     * @param bounds the bounds that the window will have
     * @param placeholder the name of <code>dockable</code>, used to associate a group of other dockables
     * to <code>dockable</code>. Can be <code>null</code>.
     * @param boundsIncludeWindow if <code>true</code>, the bounds describe the size
     * of the resulting window. Otherwise the size of the window will be a bit larger
     * such that the title can be shown in the new space
     * @throws IllegalStateException if there is already a window associated with the group of <code>placeholder</code>
     */
    protected void addDockable( Dockable dockable, Rectangle bounds, Path placeholder, boolean boundsIncludeWindow ){
    	DockUtilities.checkLayoutLocked();
        DockUtilities.ensureTreeValidity( this, dockable );
        DockHierarchyLock.Token token = DockHierarchyLock.acquireLinking( this, dockable );
        try{
	        if( bounds == null )
	            throw new IllegalArgumentException( "Bounds must not be null" );
	        
	        listeners.fireDockableAdding( dockable );
	        
	        WindowConfiguration configuration = getConfiguration( dockable );
	        ScreenDockWindow window = createWindow(configuration);
	        register( dockable, placeholder, window, configuration );
	        window.setDockable( dockable );
	        
	        bounds = new Rectangle( bounds );
	        if( !boundsIncludeWindow ){
	            window.validate();
	            Insets estimate = window.getDockableInsets();
	            if( estimate != null ){
	                bounds.x -= estimate.left;
	                bounds.y -= estimate.top;
	                bounds.width += estimate.left + estimate.right;
	                bounds.height += estimate.top + estimate.bottom;
	            }
	        }
	        
	        window.setWindowBounds( bounds );
	        window.validate();
	        
	        if( !boundsIncludeWindow ){
	            window.validate();
	            Point offset = window.getOffsetDrop();
	            if( offset != null ){
	                Rectangle windowBounds = window.getWindowBounds();
	                windowBounds = new Rectangle( windowBounds.x + offset.x, windowBounds.y + offset.y, windowBounds.width, windowBounds.height );
	                window.setWindowBounds( windowBounds );
	            }
	        }
	        
	        if( isShowing() )
	            window.setVisible( true );
	        
	        dockable.setDockParent( this );
	        listeners.fireDockableAdded( dockable );
        }
        finally{
        	token.release();
        }
    }
    
    public boolean drop( Dockable dockable, DockableProperty property ){
        if( property instanceof ScreenDockProperty )
            return drop( dockable, (ScreenDockProperty)property );
        else
            return false;
    }
    
    /**
     * Tries to add the <code>dockable</code> to this station, and uses
     * the <code>property</code> to determine its location. If the preferred
     * location overlaps an existing window, then the {@link Dockable} may be
     * added to a child-station of this station.
     * @param dockable the new {@link Dockable}
     * @param property the preferred location of the dockable
     * @return <code>true</code> if the dockable could be added, <code>false</code>
     * otherwise.
     */
    public boolean drop( Dockable dockable, ScreenDockProperty property ){
        return drop( dockable, property, true );
    }
    
    /**
     * Tries to add the <code>dockable</code> to this station, and uses
     * the <code>property</code> to determine its location. If the preferred
     * location overlaps an existing window, then the {@link Dockable} may be
     * added to a child-station of this station.
     * @param dockable the new {@link Dockable}
     * @param property the preferred location of the dockable
     * @param boundsIncludeWindow if <code>true</code>, the bounds describe the size
     * of the resulting window. Otherwise the size of the window will be a bit larger
     * such that the title can be shown in the new space
     * @return <code>true</code> if the dockable could be added, <code>false</code>
     * otherwise.
     */
    public boolean drop( Dockable dockable, ScreenDockProperty property, boolean boundsIncludeWindow ){
    	ScreenDockStationExtension.DropArguments args = new ScreenDockStationExtension.DropArguments();
		args.setDockable( dockable );
		args.setProperty( property );
		args.setBoundsIncludeWindow( boundsIncludeWindow );
		
		windowAt( args );
		
    	if( extensions != null ){
    		DockController controller = getController();
    		if( controller != null ){
    			controller.freezeLayout();
    		}
    		
    		try{
	    		for( ScreenDockStationExtension extension : extensions ){
	    			extension.drop( this, args );
	    		}
	    		boolean result = executeDrop( args );
	    		for( ScreenDockStationExtension extension : extensions ){
	    			extension.dropped( this, args, result );
	    		}
	    		return result;
    		}
    		finally{
    			if( controller != null ){
    				controller.meltLayout();
    			}
    		}
    	}
    	else{
    		return executeDrop( args );
    	}
    }
    
    private void windowAt( ScreenDockStationExtension.DropArguments args ){
    	ScreenDockWindow best = null;
        double bestRatio = 0.0;
        
        ScreenDockProperty property = args.getProperty();
        
        int x = property.getX();
        int y = property.getY();
        int width = property.getWidth();
        int height = property.getHeight();
        
        Path placeholder = property.getPlaceholder();
        if( placeholder != null ){
        	ScreenDockWindowHandle handle = dockables.getDockableAt( placeholder );
        	if( handle != null ){
        		bestRatio = 1.0;
        		best = handle.getWindow();
        	}
        	else{
        		PlaceholderMetaMap meta = dockables.getMetaMap( placeholder );
        		if( meta != null ){
        			if( meta.contains( "x" ) ){
        				x = meta.getInt( "x" );
        			}
        			if( meta.contains( "y" ) ){
        				y = meta.getInt( "y" );
        			}
        			if( meta.contains( "width" ) ){
        				width = meta.getInt( "width" );
        			}
        			if( meta.contains( "height" ) ){
        				height = meta.getInt( "height" );
        			}
        			ScreenDockProperty replacement = new ScreenDockProperty( x, y, width, height, placeholder, property.isFullscreen() );
        			replacement.setSuccessor( property.getSuccessor() );
        			args.setProperty( replacement );
        			args.setBoundsIncludeWindow( true );
        		}
        		else{
        			placeholder = null;
        		}
        	}
        }
        
        if( bestRatio == 0.0 ){
	        double propertySize = width * height;
	        for( ScreenDockWindowHandle handle : dockables.dockables() ){
	        	ScreenDockWindow window = handle.getWindow();
	        	if( !window.isFullscreen() ){
		            Rectangle bounds = window.getWindowBounds();
		            double windowSize = bounds.width * bounds.height;
		            bounds = SwingUtilities.computeIntersection( x, y, width, height, bounds );
		            
		            if( !(bounds.width == 0 || bounds.height == 0) ){
		                double size = bounds.width * bounds.height;
		                double max = Math.max( propertySize, windowSize );
		                double ratio = size / max;
		                
		                if( ratio > bestRatio ){
		                    bestRatio = ratio;
		                    best = window;
		                }
		            }
	        	}
	        }
        }
        
        if( bestRatio >= dropOverRatio ){
        	args.setWindow( best );
        }
    }
    
    private boolean executeDrop( ScreenDockStationExtension.DropArguments args ){
    	DockUtilities.checkLayoutLocked();
        DockUtilities.ensureTreeValidity( this, args.getDockable() );
        
        
        DockController controller = getController();
        DockAcceptance acceptance = controller == null ? null : controller.getAcceptance();
        
        ScreenDockWindow best = args.getWindow();
        
        boolean done = false;
        Dockable dockable = args.getDockable();
        ScreenDockProperty property = args.getProperty();
        
        if( best != null && best.getDockable() != null ){
            DockableProperty successor = property.getSuccessor();
            Dockable dock = best.getDockable();
            if( successor != null ){
                DockStation station = dock.asDockStation();
                if( station != null )
                    done = station.drop( dockable, successor );
            }
            
            if( !done ){
                Dockable old = best.getDockable();
                if( old.accept( this, dockable ) && dockable.accept( this, old ) && (acceptance == null || acceptance.accept( this, old, dockable ))){
                    combine( old, dockable, property.getSuccessor() );
                    done = true;
                }
            }
        }
        
        if( !done ){
        	boolean accept = accept( dockable ) && dockable.accept( this ) && (acceptance == null || acceptance.accept( this, dockable ));
            if( accept ){
                addDockable( dockable, new Rectangle( property.getX(), property.getY(), property.getWidth(), property.getHeight() ), property.getPlaceholder(), args.isBoundsIncludeWindow() );
                done = true;
            }
        }
        
        if( done && property.isFullscreen() ){
        	DockStation parent = dockable.getDockParent();
        	while( parent != null && parent != this ){
        		dockable = parent.asDockable();
        		parent = dockable == null ? null : dockable.getDockParent();
        	}
        	
        	if( dockable != null ){
        		setFullscreen( dockable, true );
        	}
        }
        
        return done;
    }
    
    /**
     * Drops <code>dockable</code> at the same coordinates as <code>location</code>, a
     * direct child of this station.
     * @param dockable a new dockable
     * @param location a known dockable
     * @return whether the operation completed
     */
    public boolean drop( Dockable dockable, Dockable location ){
        boolean accept = accept( dockable ) && dockable.accept( this );
        if( !accept ){
        	return false;
        }
        
        ScreenDockWindow window = getWindow( location );
        if( window == null ){
        	throw new IllegalArgumentException( "location is now known to this station" );
        }
        
        Rectangle bounds = null;
        if( window.isFullscreen() ){
        	bounds = window.getNormalBounds();
        }
        if( bounds == null ){
        	bounds = window.getWindowBounds();
        }
        	
        addDockable( dockable, bounds, true );
        return true;
    }
    
    /**
     * Combines the <code>lower</code> and the <code>upper</code> {@link Dockable}
     * to one {@link Dockable}, and replaces the <code>lower</code> with
     * this new Dockable. There are no checks whether this station 
     * {@link #accept(Dockable) accepts} the new child or the children
     * can be combined. The creation of the new {@link Dockable} is done
     * by the {@link #getCombiner() combiner}.
     * @param lower a {@link Dockable} which must be child of this station
     * @param upper a {@link Dockable} which may be child of this station
     */
    public void combine( Dockable lower, Dockable upper ){
    	combine( lower, upper, null );
    }
    
    /**
     * Combines the <code>lower</code> and the <code>upper</code> {@link Dockable}
     * to one {@link Dockable}, and replaces the <code>lower</code> with
     * this new Dockable. There are no checks whether this station 
     * {@link #accept(Dockable) accepts} the new child or the children
     * can be combined. The creation of the new {@link Dockable} is done
     * by the {@link #getCombiner() combiner}.
     * @param lower a {@link Dockable} which must be child of this station
     * @param upper a {@link Dockable} which may be child of this station
     * @param property location information associated with <code>upper</code>, may be <code>null</code>
     */
    public void combine( Dockable lower, Dockable upper, DockableProperty property ){
    	DropInfo info = new DropInfo();
    	
    	info.dockable = upper;
    	info.combine = getWindow( lower );
    	if( info.combine == null ){
    		throw new IllegalArgumentException( "lower is not a child of this station" );
    	}
    	
    	Component component = lower.getComponent();
    	Point middle = new Point( component.getWidth() / 2, component.getHeight() / 2 );
    	SwingUtilities.convertPointToScreen( middle, component );
    	
    	info.x = middle.x;
    	info.y = middle.y;
    	info.titleX = info.x;
    	info.titleY = info.y;
    	
    	info.combiner = combiner.prepare( info, Enforcement.HARD );
    	
    	combine( info, info.combiner, property );
    }

    /**
     * Uses the current {@link Combiner} to combine the {@link Dockable}s described
     * in <code>source</code>.
     * @param source the source {@link Dockable}s to combine
     * @param target the target created by the {@link Combiner}
     * @param property location information associated with the new {@link Dockable}, can be <code>null</code>
     */
    private void combine( CombinerSource source, CombinerTarget target, DockableProperty property ){
    	DockUtilities.checkLayoutLocked();
    	Dockable lower = source.getOld();
    	Dockable upper = source.getNew();
    	
    	int index = indexOf( lower );
    	if( index < 0 ){
    		throw new IllegalArgumentException( "old is not child of this station" );
    	}
    	
        ScreenDockWindowHandle window = getWindowHandle( index );
        removeDockable( upper );
        
        index = indexOf( lower );
        
        final Dockable old = window.getWindow().getDockable();
        int listIndex = dockables.levelToBase( index, Level.DOCKABLE );
        DockablePlaceholderList<ScreenDockWindowHandle>.Item item = dockables.list().get( listIndex );
        final PlaceholderMap map = item.getPlaceholderMap();
        
        DockHierarchyLock.Token token = DockHierarchyLock.acquireUnlinking( this, lower );
        try{
	        listeners.fireDockableRemoving( lower );
	        item.setPlaceholderMap( null );
	        window.setDockable( null );
	        lower.setDockParent( null );
        }
        finally{
        	token.release();
        }
        listeners.fireDockableRemoved( lower );
        
        Dockable valid = combiner.combine( new CombinerSourceWrapper( source ){
        	@Override
        	public PlaceholderMap getPlaceholders(){
	        	return map;
        	}
        	@Override
        	public Dockable getOld(){
	        	return old;
        	}
        }, target );
        
        if( property != null ){
        	DockStation combined = valid.asDockStation();
        	if( combined != null && upper.getDockParent() == combined ){
        		combined.move( upper, property );
        	}
        }
        
        token = DockHierarchyLock.acquireLinking( this, valid );
        try{
	        listeners.fireDockableAdding( valid );
	        window.setDockable( valid );
	        valid.setDockParent( this );
	        listeners.fireDockableAdded( valid );
        }
        finally{
        	token.release();
        }
    }    
    
    public boolean canReplace( Dockable old, Dockable next ) {
    	if( extensions != null ){
    		for( ScreenDockStationExtension extension : extensions ){
    			if( !extension.canReplace( this, old, next )){
    				return false;
    			}
    		}
    	}
        return true;
    }

    public void replace( DockStation old, Dockable next ){
	    replace( old.asDockable(), next, true );	
    }
    
    public void replace( Dockable current, Dockable other ){
    	replace( current, other, false );
    }
    
    public void replace( Dockable current, Dockable other, boolean station ){
    	DockUtilities.checkLayoutLocked();
    	int index = indexOf( current );
    	if( index < 0 ){
    		throw new IllegalArgumentException( "current not known to this station" );
    	}
    	
    	DockUtilities.ensureTreeValidity( this, other );
    	
        ScreenDockWindowHandle window = getWindowHandle( index );
        
        if( station ){
	        int listIndex = dockables.levelToBase( index, Level.DOCKABLE );
	        DockablePlaceholderList<ScreenDockWindowHandle>.Item item = dockables.list().get( listIndex );
	        item.setPlaceholderMap( current.asDockStation().getPlaceholders() );
        }
        
        DockHierarchyLock.Token token = DockHierarchyLock.acquireUnlinking( this, current );
        try{
	        listeners.fireDockableRemoving( current );
	        window.setDockable( null );
	        current.setDockParent( null );
	        listeners.fireDockableRemoved( current );
        }
        finally{
        	token.release();
        }
        
        token = DockHierarchyLock.acquireLinking( this, other );
        try{
	        listeners.fireDockableAdding( other );
	        window.setDockable( other );
	        other.setDockParent( this );
	        listeners.fireDockableAdded( other );
        }
        finally{
        	token.release();
        }
    }
    
    /**
     * Removes the <code>dockable</code> from this station.<br>
     * Note: clients may need to invoke {@link DockController#freezeLayout()}
     * and {@link DockController#meltLayout()} to ensure no-one else adds or
     * removes <code>Dockable</code>s.
     * @param dockable the {@link Dockable} to remove
     */
    public void removeDockable( Dockable dockable ){
        int index = indexOf( dockable );
        
        if( index >= 0 ){
            removeDockable( index );
        }
    }
    
    /**
     * Removes the <code>index</code>'th {@link Dockable} of this station.<br>
     * Note: clients may need to invoke {@link DockController#freezeLayout()}
     * and {@link DockController#meltLayout()} to ensure no-one else adds or
     * removes <code>Dockable</code>s.
     * @param index the index of the {@link Dockable} to remove
     */
    public void removeDockable( int index ){
    	DockUtilities.checkLayoutLocked();
        ScreenDockWindowHandle handle = getWindowHandle( index );
        ScreenDockWindow window = handle.getWindow();
        Dockable dockable = window.getDockable();
        
        DockHierarchyLock.Token token = DockHierarchyLock.acquireUnlinking( this, dockable );
        try{
	        listeners.fireDockableRemoving( dockable );
	        
	        window.setVisible( false );
	        deregister( dockable, window );
	        handle.setDockable( null );
	        
	        dockable.setDockParent( null );
	        listeners.fireDockableRemoved( dockable );
        }
        finally{
        	token.release();
        }
    }
    
    /**
     * Invoked after a new {@link ScreenDockWindow} has been created. This
     * method adds some listeners to the window. If the method is overridden,
     * it should be called from the subclass to ensure the correct function
     * of this station.
     * @param dockable the element for which <code>window</code> will be used
     * @param placeholder the name of <code>dockable</code>, used to place the new
     * {@link ScreenDockWindowHandle} at its correct position. Can be <code>null</code>.
     * @param window the window which was newly created
     * @param configuration the configuration that was used to create <code>window</code>
     * @return the newly created handle for <code>window</code>
     */
    protected ScreenDockWindowHandle register( Dockable dockable, Path placeholder, ScreenDockWindow window, WindowConfiguration configuration ){
    	ScreenDockWindowHandle handle = new ScreenDockWindowHandle( dockable, window, configuration );
    	
    	if( placeholder != null ){
    		if( dockables.getDockableAt( placeholder ) != null ){
    			throw new IllegalStateException( "there is already a window in the group " + placeholder + ", add the element directly to that window or do not use a placeholder" );
    		}
    		if( dockables.put( placeholder, handle ) == -1 ){
    			dockables.dockables().add( handle );	
    		}
    	}
    	else{
    		dockables.dockables().add( handle );
    	}
    	
        window.setController( getController() );
        window.setFullscreenStrategy( getFullscreenStrategy() );
        
        getRootHandler().addRoot( window.getComponent() );
        
        for( ScreenDockStationListener listener : screenDockStationListeners() ){
        	listener.windowRegistering( this, dockable, window );
        }
        
        return handle;
    }
    
    /**
     * Invoked when a {@link ScreenDockWindow} is no longer needed. This
     * method removes some listeners from the window. If overridden
     * by a subclass, the subclass should ensure that this implementation
     * is invoked too.
     * @param dockable the element for which <code>window</code> was used
     * @param window the old window
     */
    protected void deregister( Dockable dockable, ScreenDockWindow window ){
        if( frontWindow == window )
            frontWindow = null;
        
        int index = indexOf( window.getDockable() );
        saveLocation( index );
        
        dockables.remove( index );
        
        getRootHandler().removeRoot( window.getComponent() );
        window.setDockable( null );
        window.setPaintCombining( null );
        window.setController( null );
        window.setFullscreenStrategy( null );
        
        for( ScreenDockStationListener listener : screenDockStationListeners() ){
        	listener.windowDeregistering( this, dockable, window );
        }
        
        window.destroy();
    }
    
    private void saveLocation( int index ){
    	ScreenDockWindow window = dockables.dockables().get( index ).getWindow();
    	
    	PlaceholderMetaMap map = dockables.dockables().getMetaMap( index );
        Rectangle bounds = null;
        if( window.isFullscreen() ){
        	bounds = window.getNormalBounds();
        }
        if( bounds == null ){
        	bounds = window.getWindowBounds();
        }
        map.putInt( "x", bounds.x );
        map.putInt( "y", bounds.y );
        map.putInt( "width", bounds.width );
        map.putInt( "height", bounds.height );
    }
    
    /**
     * Gets the {@link WindowConfiguration} which should be used to create a new {@link ScreenDockWindow}
     * for <code>dockable</code>.
     * @param dockable the element that is going to be shown
     * @return its configuration, not <code>null</code>
     */
    protected WindowConfiguration getConfiguration( Dockable dockable ){
    	WindowConfiguration result =  windowConfiguration.getValue().getConfiguration( this, dockable );
    	if( result == null ){
    		result = new WindowConfiguration();
    	}
    	return result;
    }
    
    /**
     * Creates a new window which is associated with this station.
     * @param configuration the configuration that should be used to set up the new window
     * @return the new window
     */
    protected ScreenDockWindow createWindow( WindowConfiguration configuration ){
    	return getWindowFactory().createWindow( this, configuration );
    }
    
    /**
     * Called if {@link #getOwner()} changed. This method replaces existing {@link ScreenDockWindow}
     * by new windows created by {@link ScreenDockWindowFactory#updateWindow(ScreenDockWindow, WindowConfiguration, ScreenDockStation)}.
     */
    protected void updateWindows(){
    	updateWindows( false );
    }
    
    /**
     * Update all windows either by calling {@link ScreenDockWindowFactory#updateWindow(ScreenDockWindow, WindowConfiguration, ScreenDockStation)}
     * or by calling {@link ScreenDockWindowFactory#createWindow(ScreenDockStation, WindowConfiguration)}.
     * @param force if <code>true</code>, then {@link ScreenDockWindowFactory#createWindow(ScreenDockStation, WindowConfiguration) createWindow}
     * is used and all windows are replaced, if <code>false</code> the factory is allowed to do optimizations.
     */
    protected void updateWindows( boolean force ){
    	ScreenDockWindowFactory factory = getWindowFactory();
    	
    	Integer delay = PREVENT_FOCUS_STEALING_DELAY.getDefault( null );
    	DockController controller = getController();
    	if( controller != null ){
    		delay = controller.getProperties().get( PREVENT_FOCUS_STEALING_DELAY ); 
    	}
    	
    	for( ScreenDockWindowHandle handle : dockables.dockables() ){
    		final ScreenDockWindow oldWindow = handle.getWindow();
    		final ScreenDockWindow newWindow;
    		final WindowConfiguration configuration;
    		
    		if( force ){
    			configuration = getConfiguration( oldWindow.getDockable() );
    			newWindow = createWindow( configuration );
    		}
    		else{
    			configuration = handle.getConfiguration();
    			newWindow = factory.updateWindow( oldWindow, configuration, this );
    		}
    		
    		if( newWindow != null && newWindow != oldWindow ){
    			Dockable dockable = oldWindow.getDockable();
    			Rectangle bounds = oldWindow.getNormalBounds();
    			if( bounds == null ){
    				bounds = oldWindow.getWindowBounds();
    			}
    			boolean fullscreen = oldWindow.isFullscreen();
    			boolean visible = oldWindow.isVisible();
    			
    			oldWindow.setDockable( null );
    			oldWindow.setPaintCombining( null );
    			oldWindow.setController( null );
    	        oldWindow.setFullscreenStrategy( null );
    	        
    	        for( ScreenDockStationListener listener : screenDockStationListeners() ){
    	        	listener.windowDeregistering( this, dockable, oldWindow );
    	        }
    	        
    			oldWindow.destroy();
    			handle.setWindow( newWindow, configuration );
    			
    	        newWindow.setController( getController() );
    	        newWindow.setFullscreenStrategy( getFullscreenStrategy() );
    	        newWindow.setWindowBounds( bounds );
    	        newWindow.setFullscreen( fullscreen );
    	        
    	        for( ScreenDockStationListener listener : screenDockStationListeners() ){
    	        	listener.windowRegistering( this, dockable, newWindow );
    	        }
    	        
    	        if( visible && isShowing() ){
    	        	if( delay == null || delay.intValue() <= 0 ){
    	        		newWindow.setVisible( true );
    	        	}
    	        	else{
	    	        	newWindow.setPreventFocusStealing( true );
	    	        	newWindow.setVisible( true );
	    	        	Timer timer = new Timer( delay, new ActionListener(){
	    	        		public void actionPerformed( ActionEvent e ){
	    	        			newWindow.setPreventFocusStealing( false );	
	    	        		}
	    	        	});
	    	        	timer.setRepeats( false );
	    	        	timer.start();
    	        	}
    	        }
    		}
    	}
    }
    
    /**
     * Gets the owner of this station. The owner is forwarded to some
     * windows as their owner. So the windows will always remain in the
     * foreground.
     * @return the current owner
     * @see #getProvider()
     */
    public Window getOwner(){
        return owner.searchWindow();
    }
    
    /**
     * Gets the provider which delivers window owners for the windows of this
     * station.
     * @return the provider for windows
     */
    public WindowProvider getProvider(){
        return owner;
    }
    
    /**
     * Gets the factory that is currently used to create new windows for this station.
     * @return the factory, not <code>null</code>
     */
    public ScreenDockWindowFactory getWindowFactory(){
        return windowFactory.getValue();
    }
    
    /**
     * Gets the property which represents the window factory.
     * @return the property
     */
    protected PropertyValue<ScreenDockWindowFactory> getWindowFactoryProperty(){
        return windowFactory;
    }
    

    /**
     * Sets the factory that will be used to create new windows for this station, Calling this
     * method will result in closing all existing windows and creating new windows.
     * @param factory the new factory, <code>null</code> to set the default
     * value
     */
    public void setWindowFactory( ScreenDockWindowFactory factory ){
        windowFactory.setValue( factory );
    }
    
    /**
     * Gets the configuration which is currently used to create new windows.
     * @return the configuration, not <code>null</code>
     */
    public ScreenDockWindowConfiguration getWindowConfiguration(){
    	return windowConfiguration.getValue();
    }
    
    /**
     * Gets the property which represents the window configuration.
     * @return the property, not <code>null</code>
     */
    protected PropertyValue<ScreenDockWindowConfiguration> getWindowConfigurationProperty(){
		return windowConfiguration;
	}
    
    /**
     * Sets the configuration which should be used to create new windows. Calling this method
     * results in closing all existing windows and creating new windows.
     * @param configuration the new configuration or <code>null</code> to use the default configuration
     */
    public void setWindowConfiguration( ScreenDockWindowConfiguration configuration ){
    	windowConfiguration.setValue( configuration );
    }
    
    /**
     * Gets the current fullscreen strategy.
     * @return the strategy, not <code>null</code>
     */
    public ScreenDockFullscreenStrategy getFullscreenStrategy(){
    	return fullscreenStrategy.getValue();
    }
    
    /**
     * Sets the strategy used to handle fullscreen mode.
     * @param strategy the new strategy, <code>null</code> will reapply the default strategy
     */
    public void setFullscreenStrategy( ScreenDockFullscreenStrategy strategy ){
    	fullscreenStrategy.setValue( strategy );
    }
    
    /**
     * Tells whether this station shows its children or not.
     * @return <code>true</code> if the windows are visible, <code>false</code>
     * otherwise
     * @see #setShowing(boolean)
     */
    public boolean isShowing() {
        return showing;
    }
    
    /**
     * Sets the visibility of all windows of this station.
     * @param showing <code>true</code> if all windows should be visible,
     * <code>false</code> otherwise.
     */
    public void setShowing( boolean showing ){
        if( this.showing != showing ){
            this.showing = showing;
            for( ScreenDockWindowHandle window : dockables.dockables() ){
                window.getWindow().setVisible( showing );
            }
            visibility.fire();
        }
    }
    
    /**
     * Tells whether this station shows its children. This method just calls
     * {@link #isShowing()}.
     * @return <code>true</code> if the windows are visible, <code>false</code>
     * if not.
     * @see #isShowing()
     */
    public boolean isStationShowing(){
	    return isShowing();
    }
    
    @Deprecated
    @Todo( compatibility=Compatibility.BREAK_MAJOR, priority=Priority.ENHANCEMENT, target=Version.VERSION_1_1_3, description="remove this method" )
    public boolean isStationVisible(){
    	return isShowing();
    }
    
    public boolean isChildShowing( Dockable dockable ){
    	return isVisible( dockable );
    }
    
    @Deprecated
    @Todo( compatibility=Compatibility.BREAK_MAJOR, priority=Priority.ENHANCEMENT, target=Version.VERSION_1_1_3, description="remove this method" )
    public boolean isVisible( Dockable dockable ){
	    return isStationVisible();
    }
        
    public Rectangle getStationBounds() {
        return null;
    }

    public Dockable asDockable() {
        return null;
    }

    public DockStation asDockStation() {
        return this;
    }

    public String getFactoryID() {
        return ScreenDockStationFactory.ID;
    }
    
    /**
     * Gets the {@link DockTitleVersion} used by this station to create
     * new {@link DockTitle}s.
     * @return the version, can be <code>null</code>
     */
    public DockTitleVersion getTitleVersion(){
        return version;
    }
    
    /**
     * Gets the currently used {@link BoundaryRestriction}.
     * @return the restriction
     */
    public BoundaryRestriction getBoundaryRestriction(){
        return restriction.getValue();
    }
    
    /**
     * Changes the boundary restriction used to check the boundaries of
     * the windows of this station.
     * @param restriction the new restriction or <code>null</code> to reset
     * the default value
     */
    public void setBoundaryRestriction( BoundaryRestriction restriction ){
        this.restriction.setValue( restriction );
    }
    
    /**
     * Checks the boundaries of all windows of this station
     */
    public void checkWindowBoundaries(){
        for( ScreenDockWindowHandle window : dockables.dockables() )
            window.getWindow().checkWindowBounds();
    }
    
    /**
     * Gets the {@link MagnetController} of this station. The {@link MagnetController} controls the
     * attraction between {@link ScreenDockWindow}s.
     * @return the controller, never <code>null</code>
     */
    public MagnetController getMagnetController(){
    	return magnet;
    }
    
    /**
     * Tells the current overlapping two windows must have in order to be merged.
     * @return the overlapping, a number between 0 and 1
     * @see #setDropOverRatio(double)
     */
    public double getDropOverRatio(){
		return dropOverRatio;
	}
    
    /**
     * Sets how much two windows must overlap in order to be merged. This property is only used when
     * {@link #drop(Dockable, ScreenDockProperty, boolean) dropping} a {@link Dockable}. A value of 0 means that
     * the windows don't have to overlap, a value of 1 indicates a perfect match. The default value is 0.75.
     * @param dropOverRatio the new ratio, a value between 0 and 1 inclusive
     */
    public void setDropOverRatio( double dropOverRatio ){
    	if( dropOverRatio < 0 || dropOverRatio > 1 ){
    		throw new IllegalArgumentException( "dropOverRatio must be between 0 and 1" );
    	}
		this.dropOverRatio = dropOverRatio;
	}
    
    private ScreenDockWindowClosingStrategy getWindowClosingStrategy(){
    	DockController controller = getController();
    	if( controller == null ){
    		return null;
    	}
    	return controller.getProperties().get( WINDOW_CLOSING_STRATEGY );
    }
    
    /**
     * Information where a {@link Dockable} will be dropped. This class
     * is used only while a Dockable is dragged and this station has answered
     * as possible parent.
     */
    private class DropInfo implements CombinerSource, StationDropOperation{
        /** The Dockable which is dragged */
        public Dockable dockable;
        /** Location of the mouse */
        public int x, y;
        /** Location of the title */
        public int titleX, titleY;
        /** Possible new parent */
        public ScreenDockWindow combine;
        
        /** Information about how to combine {@link #combine} with {@link #dockable} */
        public CombinerTarget combiner;
        
        /** whether this is a move operation or not */
        public boolean move;

		public Point getMousePosition(){
			Point point = new Point( x, y );
			SwingUtilities.convertPointFromScreen( point, combine.getDockable().getComponent() );
			return point;
		}
		
		public void draw(){
			dropInfo = this;
		        
	        if( combine != null ){
	            combine.setPaintCombining( dropInfo.combiner );
	        }
		}
		
		public void destroy( StationDropOperation next ){
		    if( combine != null ){
                combine.setPaintCombining( null );
            }
			
			if( dropInfo == this ){	
				dropInfo = null;
			}
		}
		
		public DockStation getTarget(){
			return ScreenDockStation.this;
		}
		
		public Dockable getItem(){
			return dockable;
		}
		
		public CombinerTarget getCombination(){
			return combiner;
		}
		
		public DisplayerCombinerTarget getDisplayerCombination(){
			CombinerTarget target = getCombination();
			if( target == null ){
				return null;
			}
			return target.getDisplayerCombination();
		}
		
		public Dimension getSize(){
			return combine.getDockable().getComponent().getSize();
		}

		public boolean isMouseOverTitle(){
			return combine.inTitleArea( x, y );
		}
		
		public Dockable getNew(){
			return dockable;
		}

		public Dockable getOld(){
			return combine.getDockable();
		}

		public DockableDisplayer getOldDisplayer(){
			return combine.getDockableDisplayer();
		}
		
		public DockStation getParent(){
			return ScreenDockStation.this;
		}

		public PlaceholderMap getPlaceholders(){
			for( DockablePlaceholderList<ScreenDockWindowHandle>.Item item : dockables.list() ){
				ScreenDockWindowHandle handle = item.getDockable();
				if( handle != null && handle.getWindow() == combine ){
					return item.getPlaceholderMap();
				}
			}
			return null;
		}
		
		public boolean isMove(){
			return move;
		}
		
		public void execute(){
			if( isMove() ){
				move();
			}
			else{
				drop();
			}
		}
		
	    private void move() {
	    	DockUtilities.checkLayoutLocked();
	        if( combine != null ){
	            combine( dropInfo, combiner, null );
	        }
	        else{
	            ScreenDockWindow window = getWindow( dockable );
	            Point zero = window.getOffsetMove();
	            if( zero == null )
	                zero = new Point( 0, 0 );
	            
	            Rectangle bounds = window.getWindowBounds();
	            bounds = new Rectangle( titleX - zero.x, titleY - zero.y, bounds.width, bounds.height );
	            window.setWindowBounds( bounds );
	        }
	    }

	    private void drop() {
	        if( combine != null ){
	            combine( dropInfo, combiner, null );
	        }
	        else{
	            Dimension size = dropSizeStrategy.getValue().getDropSize( ScreenDockStation.this, dockable );
	            ScreenDockProperty property = new ScreenDockProperty( titleX, titleY, size.width, size.height );
	            ScreenDockStation.this.drop( dockable, property, false );
	        }
	    }
    }
    
    /**
     * A listener that adds itself to {@link ScreenDockWindow}s for monitoring their fullscreen state,
     * their position, and calling the {@link ScreenDockWindowClosingStrategy} when necessary.
     * @author Benjamin Sigg
     */
    private class ScreenWindowListener implements ScreenDockStationListener, ScreenDockWindowListener{
		public void fullscreenChanged( ScreenDockStation station, Dockable dockable ) {
			listeners.fireDockablesRepositioned( dockable );
		}

		public void windowDeregistering( ScreenDockStation station, Dockable dockable, ScreenDockWindow window ) {
			window.removeScreenDockWindowListener( this );
		}

		public void windowRegistering( ScreenDockStation station, Dockable dockable, ScreenDockWindow window ) {
			window.addScreenDockWindowListener( this );
		}

		public void fullscreenStateChanged( ScreenDockWindow window ) {
			Dockable dockable = window.getDockable();
			
			if( dockable != null ){
				for( ScreenDockStationListener listener : screenDockStationListeners() ){
					listener.fullscreenChanged( ScreenDockStation.this, dockable );
				}
			}
		}

		public void shapeChanged( ScreenDockWindow window ) {
			Dockable dockable = window.getDockable();
			if( dockable != null ){
				listeners.fireDockablesRepositioned( dockable );
			}
		}

		public void visibilityChanged( ScreenDockWindow window ) {
			// ignore
		}
		
		public void windowClosing( ScreenDockWindow window ){
			ScreenDockWindowClosingStrategy strategy = getWindowClosingStrategy();
			if( strategy != null ){
				strategy.closing( window );
			}
		}
    }
}
