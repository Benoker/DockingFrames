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
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.SwingUtilities;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.DockUI;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.accept.DockAcceptance;
import bibliothek.gui.dock.action.DefaultDockActionSource;
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.action.DockActionSource;
import bibliothek.gui.dock.action.ListeningDockAction;
import bibliothek.gui.dock.action.LocationHint;
import bibliothek.gui.dock.event.DoubleClickListener;
import bibliothek.gui.dock.layout.DockableProperty;
import bibliothek.gui.dock.station.AbstractDockStation;
import bibliothek.gui.dock.station.Combiner;
import bibliothek.gui.dock.station.DisplayerCollection;
import bibliothek.gui.dock.station.DisplayerFactory;
import bibliothek.gui.dock.station.DockableDisplayer;
import bibliothek.gui.dock.station.StationPaint;
import bibliothek.gui.dock.station.screen.BoundaryRestriction;
import bibliothek.gui.dock.station.screen.DefaultScreenDockFullscreenStrategy;
import bibliothek.gui.dock.station.screen.DefaultScreenDockWindowFactory;
import bibliothek.gui.dock.station.screen.ScreenDockFullscreenStrategy;
import bibliothek.gui.dock.station.screen.ScreenDockProperty;
import bibliothek.gui.dock.station.screen.ScreenDockStationFactory;
import bibliothek.gui.dock.station.screen.ScreenDockStationListener;
import bibliothek.gui.dock.station.screen.ScreenDockWindow;
import bibliothek.gui.dock.station.screen.ScreenDockWindowFactory;
import bibliothek.gui.dock.station.screen.ScreenDockWindowHandle;
import bibliothek.gui.dock.station.screen.ScreenDockWindowListener;
import bibliothek.gui.dock.station.screen.ScreenFullscreenAction;
import bibliothek.gui.dock.station.support.CombinerWrapper;
import bibliothek.gui.dock.station.support.ConvertedPlaceholderListItem;
import bibliothek.gui.dock.station.support.DisplayerFactoryWrapper;
import bibliothek.gui.dock.station.support.DockableVisibilityManager;
import bibliothek.gui.dock.station.support.PlaceholderList;
import bibliothek.gui.dock.station.support.PlaceholderListItemAdapter;
import bibliothek.gui.dock.station.support.PlaceholderListItemConverter;
import bibliothek.gui.dock.station.support.PlaceholderMap;
import bibliothek.gui.dock.station.support.PlaceholderMetaMap;
import bibliothek.gui.dock.station.support.PlaceholderStrategy;
import bibliothek.gui.dock.station.support.StationPaintWrapper;
import bibliothek.gui.dock.station.support.PlaceholderList.Filter;
import bibliothek.gui.dock.station.support.PlaceholderList.Level;
import bibliothek.gui.dock.title.ControllerTitleFactory;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.gui.dock.title.DockTitleVersion;
import bibliothek.gui.dock.util.DirectWindowProvider;
import bibliothek.gui.dock.util.DockProperties;
import bibliothek.gui.dock.util.DockUtilities;
import bibliothek.gui.dock.util.PropertyKey;
import bibliothek.gui.dock.util.PropertyValue;
import bibliothek.gui.dock.util.WindowProvider;
import bibliothek.gui.dock.util.property.ConstantPropertyFactory;
import bibliothek.gui.dock.util.property.PropertyFactory;
import bibliothek.util.Path;

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
    
    /** a key for a property telling which boundaries a {@link ScreenDockWindow} can have */
    public static final PropertyKey<BoundaryRestriction> BOUNDARY_RESTRICTION = 
        new PropertyKey<BoundaryRestriction>( "ScreenDockStation.boundary_restriction",
        		new ConstantPropertyFactory<BoundaryRestriction>( BoundaryRestriction.FREE ), true );
    
    /** a key for a property telling how to create new windows */
    public static final PropertyKey<ScreenDockWindowFactory> WINDOW_FACTORY =
        new PropertyKey<ScreenDockWindowFactory>( "ScreenDockStation.window_factory", 
        		new ConstantPropertyFactory<ScreenDockWindowFactory>( new DefaultScreenDockWindowFactory() ), true );
    
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
    
    /** The visibility state of the windows */
    private boolean showing = false;
    
    /** A list of all windows that are used by this station */
    // private List<ScreenDockWindow> dockables = new ArrayList<ScreenDockWindow>();
    private PlaceholderList<ScreenDockWindowHandle> dockables = new PlaceholderList<ScreenDockWindowHandle>();
    
    /** All listeners that were added to this station */
    private List<ScreenDockStationListener> screenDockStationListeners = new ArrayList<ScreenDockStationListener>();
    
    /** The version of titles that are used */
    private DockTitleVersion version;
    
    /** Combiner to merge some {@link Dockable Dockables} */
    private CombinerWrapper combiner = new CombinerWrapper();
    
    /** Information about the current movement of a {@link Dockable} */
    private DropInfo dropInfo;
    
    /** The {@link Window} that is used as parent for the windows */
    private WindowProvider owner;
    
    /** The paint used to draw information on this station */
    private StationPaintWrapper stationPaint = new StationPaintWrapper();
    
    /** A factory to create new {@link DockableDisplayer}*/
    private DisplayerFactoryWrapper displayerFactory = new DisplayerFactoryWrapper();
    
    /** The set of {@link DockableDisplayer} used on this station */
    private DisplayerCollection displayers;
    
    /** The window which has currently the focus */
    private ScreenDockWindow frontWindow;
    
    /** A manager for the visibility of the children */
    private DockableVisibilityManager visibility;
    
    /** An action to enable or disable fullscreen mode of some window */
    private ListeningDockAction fullscreenAction;

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
            // ignore   
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
			if( dockable != ScreenDockStation.this ){
				DockStation parent = dockable.getDockParent();
				while( parent != null && parent != ScreenDockStation.this ){
					dockable = parent.asDockable();
					parent = dockable == null ? null : dockable.getDockParent();
				}
				if( parent == ScreenDockStation.this ){
					boolean state = isFullscreen( dockable );
					setFullscreen( dockable, !state );
					return true;
				}
			}
			return false;
		}
	};
    
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
        visibility = new DockableVisibilityManager( listeners );
        this.owner = owner;
        
        displayers = new DisplayerCollection( this, displayerFactory );
        fullscreenAction = createFullscreenAction();
        
        addScreenDockStationListener( new FullscreenListener() );
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

    public DefaultDockActionSource getDirectActionOffers( Dockable dockable ) {
        if( fullscreenAction == null )
            return null;
        else{
            DefaultDockActionSource source = new DefaultDockActionSource(new LocationHint( LocationHint.DIRECT_ACTION, LocationHint.VERY_RIGHT ));
            source.add( fullscreenAction );

            return source;
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

        DefaultDockActionSource source = new DefaultDockActionSource( fullscreenAction );
        source.setHint( new LocationHint( LocationHint.INDIRECT_ACTION, LocationHint.VERY_RIGHT ));
        return source;
    }
    
    /**
     * Gets the {@link DisplayerFactory} that is used by this station
     * to create an underground for its children.
     * @return the factory
     * @see DisplayerFactoryWrapper#setDelegate(DisplayerFactory)
     */
    public DisplayerFactoryWrapper getDisplayerFactory() {
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
     * @see CombinerWrapper#setDelegate(Combiner)
     */
    public CombinerWrapper getCombiner() {
        return combiner;
    }
    
    /**
     * Gets the {@link StationPaint} for this station. The paint is needed to
     * paint information on this station, when a {@link Dockable} is dragged
     * or moved.
     * @return the paint
     * @see StationPaintWrapper#setDelegate(StationPaint)
     */
    public StationPaintWrapper getPaint() {
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
        }
        
        restriction.setProperties( controller );
        windowFactory.setProperties( controller );
        fullscreenStrategy.setProperties( controller );
        placeholderStrategy.setProperties( controller );
        
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
    	
    	return dockables.toMap( new PlaceholderListItemAdapter<ScreenDockWindowHandle>() {
    		@Override
    		public ConvertedPlaceholderListItem convert( int index, ScreenDockWindowHandle dockable ) {
    			ConvertedPlaceholderListItem item = new ConvertedPlaceholderListItem();
    			Rectangle bounds = dockable.getBounds();
    			item.putInt( "id", children.get( dockable.asDockable() ) );
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
    	if( getDockableCount() > 0 ){
    		throw new IllegalStateException( "there are children on this station" );
    	}
    	try{
    		PlaceholderList<ScreenDockWindowHandle> next = new PlaceholderList<ScreenDockWindowHandle>( placeholders );
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
    	if( getDockableCount() > 0 ){
    		throw new IllegalStateException( "must not have any children" );
    	}
    	
    	PlaceholderList<ScreenDockWindowHandle> next = new PlaceholderList<ScreenDockWindowHandle>();
    	
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
    	
    	next.read( map, new PlaceholderListItemAdapter<ScreenDockWindowHandle>(){
			@Override
			public ScreenDockWindowHandle convert( ConvertedPlaceholderListItem item ){
				int id = item.getInt( "id" );
				Dockable dockable = children.get( id );
				if( dockable != null ){
					int x = item.getInt( "x" );
					int y = item.getInt( "y" );
					int width = item.getInt( "width" );
					int height = item.getInt( "height" );
					boolean fullscreen = item.getBoolean( "fullscreen" );
					
			        listeners.fireDockableAdding( dockable );
			        
			        ScreenDockWindow window = createWindow();
			        ScreenDockWindowHandle handle = new ScreenDockWindowHandle( dockable, window );
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
				dockable.asDockable().setDockParent( ScreenDockStation.this );
				for( ScreenDockStationListener listener : screenDockStationListeners() ){
		        	listener.windowRegistering( ScreenDockStation.this, dockable.asDockable(), dockable.getWindow() );
		        }
				listeners.fireDockableAdded( dockable.asDockable() );
			}
		});		
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

    public boolean prepareDrop( int x, int y, int titleX, int titleY, boolean checkOverrideZone, Dockable dockable ) {
        return prepare( x, y, titleX, titleY, dockable, true );
    }
    
    public boolean prepare( int x, int y, int titleX, int titleY, Dockable dockable, boolean drop ) {
        if( dropInfo == null )
            dropInfo = new DropInfo();
        
        ScreenDockWindow oldCombine = dropInfo.combine;
        
        dropInfo.titleX = titleX;
        dropInfo.titleY = titleY;
        dropInfo.dockable = dockable;
        dropInfo.combine = searchCombineDockable( x, y, dockable );
        
        if( dropInfo.combine != null && dropInfo.combine.getDockable() == dockable )
            dropInfo.combine = null;
        
        if( dropInfo.combine != oldCombine ){
            if( oldCombine != null )
                oldCombine.setPaintCombining( false );
            
            if( dropInfo.combine != null )
                dropInfo.combine.setPaintCombining( true );
        }
        
        checkDropInfo();
        return dropInfo != null;
    }

    
    /**
     * Ensures that the desired location where to insert the next child is valid
     * If not, then {@link #dropInfo} is set to <code>null</code>
     */
    private void checkDropInfo(){
        if( dropInfo != null ){
            if( dropInfo.combine != null ){
                if( !accept( dropInfo.dockable ) || 
                        !dropInfo.dockable.accept( this, dropInfo.combine.getDockable() ) ||
                        !dropInfo.combine.getDockable().accept( this, dropInfo.dockable ) ||
                        !getController().getAcceptance().accept( this, dropInfo.combine.getDockable(), dropInfo.dockable )){
                    dropInfo = null;
                }
            }
            else{
                if( !accept( dropInfo.dockable ) ||
                        !dropInfo.dockable.accept( this ) ||
                        !getController().getAcceptance().accept( this, dropInfo.dockable )){
                    dropInfo = null;
                }
            }
        }
    }

    
    /**
     * Searches a window on the coordinates x/y which can be used to create
     * a combination with <code>drop</code>.
     * @param x the x-coordinate on the screen
     * @param y die y-coordinate on the screen
     * @param drop the {@link Dockable} which might be combined with a window
     * @return the window which might become the parent of <code>drop</code>.
     */
    protected ScreenDockWindow searchCombineDockable( int x, int y, Dockable drop ){
        DockAcceptance acceptance = getController() == null ? null : getController().getAcceptance();
        
        for( ScreenDockWindowHandle handle : dockables.dockables() ){
        	ScreenDockWindow window = handle.getWindow();
        	
            if( window.inCombineArea( x, y )){
                Dockable child = window.getDockable();
                
                if( acceptance == null || acceptance.accept( this, child, drop )){
                    if( drop.accept( this, child ) && child.accept( this, drop )){
                        return window;
                    }
                }
            }
        }
        
        return null;
    }
    
    public void drop() {
        if( dropInfo.combine != null ){
            combine( dropInfo.combine.getDockable(), dropInfo.dockable );
        }
        else{
            Component component = dropInfo.dockable.getComponent();
            Rectangle bounds = new Rectangle( dropInfo.titleX, dropInfo.titleY, component.getWidth(), component.getHeight() );
            addDockable( dropInfo.dockable, bounds, false );
        }
    }

    public void drop( Dockable dockable ) {
        Window owner = getOwner();
        
        int x = 30;
        int y = 30;
        
        if( owner != null ){
            x += owner.getX();
            y += owner.getY();
        }
        
        Dimension preferred = dockable.getComponent().getPreferredSize();
        Rectangle rect = new Rectangle( x, y, Math.max( preferred.width, 100 ), Math.max( preferred.height, 100 ));
        addDockable( dockable, rect );
    }

    public DockableProperty getDockableProperty( Dockable dockable, Dockable target ) {
    	return getLocation( dockable, target );
    }
    
    /**
     * Gets the location of <code>dockable</code> and its current state.
     * @param dockable some child of this station
     * @param target the final element for which the location is needd
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
     * Tells this station what to do on a double click on a child. If set
     * to <code>true</code>, then the childs fullscreen mode gets changed.
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
    
    public boolean prepareMove( int x, int y, int titleX, int titleY, boolean checkOverrideZone, Dockable dockable ) {
        return prepare( x, y, titleX, titleY, dockable, false );
    }

    public void move() {
        if( dropInfo.combine != null ){
            combine( dropInfo.combine.getDockable(), dropInfo.dockable );
        }
        else{
            ScreenDockWindow window = getWindow( dropInfo.dockable );
            Point zero = window.getOffsetMove();
            if( zero == null )
                zero = new Point( 0, 0 );
            
            Rectangle bounds = window.getWindowBounds();
            bounds = new Rectangle( dropInfo.titleX - zero.x, dropInfo.titleY - zero.y, bounds.width, bounds.height );
            window.setWindowBounds( bounds );
        }
    }
    
    public void move( Dockable dockable, DockableProperty property ) {
        if( property instanceof ScreenDockProperty ){
            ScreenDockWindow window = getWindow( dockable );
            if( window == null )
                throw new IllegalArgumentException( "dockable not child of this station" );
            
            ScreenDockProperty bounds = (ScreenDockProperty)property;
            
            window.setWindowBounds( new Rectangle( bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight() ) );
        }
    }

    public void draw() {
        if( dropInfo == null )
            dropInfo = new DropInfo();
        
        if( dropInfo.combine != null )
            dropInfo.combine.setPaintCombining( true );
    }

    public void forget() {
        if( dropInfo != null ){
            if( dropInfo.combine != null )
                dropInfo.combine.setPaintCombining( false );
            dropInfo = null;
        }
    }

    public <D extends Dockable & DockStation> boolean isInOverrideZone( int x,
            int y, D invoker, Dockable drop ) {
        
        return searchCombineDockable( x, y, drop ) != null;
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
        DockUtilities.ensureTreeValidity( this, dockable );
        
        if( bounds == null )
            throw new IllegalArgumentException( "Bounds must not be null" );
        
        listeners.fireDockableAdding( dockable );
        
        ScreenDockWindow window = createWindow();
        register( dockable, placeholder, window );
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
        DockUtilities.ensureTreeValidity( this, dockable );
        ScreenDockWindow best = null;
        double bestRatio = 0.0;
        
        int x = property.getX();
        int y = property.getY();
        int width = property.getWidth();
        int height = property.getHeight();
        
        DockController controller = getController();
        DockAcceptance acceptance = controller == null ? null : controller.getAcceptance();
        
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
        			boundsIncludeWindow = true;
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
		                    bestRatio = max;
		                    best = window;
		                }
		            }
	        	}
	        }
        }
        
        boolean done = false;
        
        if( bestRatio > 0.75 ){
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
                    combine( old, dockable );
                    done = true;
                }
            }
        }
        
        if( !done ){
        	boolean accept = accept( dockable ) && dockable.accept( this ) && (acceptance == null || acceptance.accept( this, dockable ));
            if( accept ){
                addDockable( dockable, new Rectangle( x, y, width, height ), placeholder, boundsIncludeWindow );
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
    	int index = indexOf( lower );
    	if( index < 0 ){
    		throw new IllegalArgumentException( "lower is not child of this station" );
    	}
    	
        ScreenDockWindowHandle window = getWindowHandle( index );
        removeDockable( upper );
        
        listeners.fireDockableRemoving( lower );
        window.setDockable( null );
        lower.setDockParent( null );
        listeners.fireDockableRemoved( lower );
        
        int listIndex = dockables.levelToBase( index, Level.DOCKABLE );
        PlaceholderList<ScreenDockWindowHandle>.Item item = dockables.list().get( listIndex );
        PlaceholderMap map = item.getPlaceholderMap();
        item.setPlaceholderMap( null );
        
        Dockable valid = combiner.combine( lower, upper, this, map );
        
        listeners.fireDockableAdding( valid );
        window.setDockable( valid );
        valid.setDockParent( this );
        listeners.fireDockableAdded( valid );
    }
    
    public boolean canReplace( Dockable old, Dockable next ) {
        return true;
    }

    public void replace( DockStation old, Dockable next ){
	    replace( old.asDockable(), next, true );	
    }
    
    public void replace( Dockable current, Dockable other ){
    	replace( current, other, false );
    }
    
    public void replace( Dockable current, Dockable other, boolean station ){
    	int index = indexOf( current );
    	if( index < 0 ){
    		throw new IllegalArgumentException( "current not known to this station" );
    	}
    	
        ScreenDockWindowHandle window = getWindowHandle( index );
        
        if( station ){
	        int listIndex = dockables.levelToBase( index, Level.DOCKABLE );
	        PlaceholderList<ScreenDockWindowHandle>.Item item = dockables.list().get( listIndex );
	        item.setPlaceholderMap( current.asDockStation().getPlaceholders() );
        }
        
        listeners.fireDockableRemoving( current );
        window.setDockable( null );
        current.setDockParent( null );
        listeners.fireDockableRemoved( current );
        
        listeners.fireDockableAdding( other );
        window.setDockable( other );
        other.setDockParent( this );
        listeners.fireDockableAdded( other );
    }
    
    /**
     * Removes the <code>dockable</code> from this station.<br>
     * Note: clients may need to invoke {@link DockController#freezeLayout()}
     * and {@link DockController#meltLayout()} to ensure noone else adds or
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
     * and {@link DockController#meltLayout()} to ensure noone else adds or
     * removes <code>Dockable</code>s.
     * @param index the index of the {@link Dockable} to remove
     */
    public void removeDockable( int index ){
        ScreenDockWindowHandle handle = getWindowHandle( index );
        ScreenDockWindow window = handle.getWindow();
        Dockable dockable = window.getDockable();
        
        listeners.fireDockableRemoving( dockable );
        
        window.setVisible( false );
        handle.setDockable( null );
        deregister( dockable, window );
        
        dockable.setDockParent( null );
        listeners.fireDockableRemoved( dockable );
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
     * @return the newly created handle for <code>window</code>
     */
    protected ScreenDockWindowHandle register( Dockable dockable, Path placeholder, ScreenDockWindow window ){
    	ScreenDockWindowHandle handle = new ScreenDockWindowHandle( dockable, window );
    	
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
        
        dockables.remove( index );
        
        window.setController( null );
        window.setFullscreenStrategy( null );
        
        for( ScreenDockStationListener listener : screenDockStationListeners() ){
        	listener.windowDeregistering( this, dockable, window );
        }
        
        window.destroy();
    }
    
    /**
     * Creates a new window which is associated with this station.
     * @return the new window
     */
    protected ScreenDockWindow createWindow(){
        return getWindowFactory().createWindow( this );
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
     * Sets the factory that will be used to create new windows for this station,
     * already existing windows are not affected by this change.
     * @param factory the new factory, <code>null</code> to set the default
     * value
     */
    public void setWindowFactory( ScreenDockWindowFactory factory ){
        windowFactory.setValue( factory );
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

    @Override
    public boolean canCompare( DockStation station ) {
        return true;
    }
    
    @Override
    public int compare( DockStation station ) {
        return -1;
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
     * Information where a {@link Dockable} will be dropped. This class
     * is used only while a Dockable is dragged and this station has answered
     * as possible parent.
     */
    private static class DropInfo{
        /** The Dockable which is dragged */
        public Dockable dockable;
        /** Location of the mouse */
        public int titleX, titleY;
        /** Possible new parent */
        public ScreenDockWindow combine;
    }
    
    /**
     * A listener that adds itself to {@link ScreenDockWindow}s for monitoring their fullscreen state.
     * @author Benjamin Sigg
     */
    private class FullscreenListener implements ScreenDockStationListener, ScreenDockWindowListener{
		public void fullscreenChanged( ScreenDockStation station, Dockable dockable ) {
			// ignore
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
			// ignore
		}

		public void visibilityChanged( ScreenDockWindow window ) {
			// ignore
		}
    }
}
