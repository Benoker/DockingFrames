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
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.HierarchyBoundsListener;
import java.awt.event.HierarchyEvent;
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
import bibliothek.gui.dock.action.DefaultDockActionSource;
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.action.ListeningDockAction;
import bibliothek.gui.dock.action.LocationHint;
import bibliothek.gui.dock.control.MouseFocusObserver;
import bibliothek.gui.dock.event.DockStationAdapter;
import bibliothek.gui.dock.event.DockTitleEvent;
import bibliothek.gui.dock.event.DockableAdapter;
import bibliothek.gui.dock.event.DockableFocusEvent;
import bibliothek.gui.dock.event.DockableFocusListener;
import bibliothek.gui.dock.event.FlapDockListener;
import bibliothek.gui.dock.event.FocusVetoListener;
import bibliothek.gui.dock.layout.DockableProperty;
import bibliothek.gui.dock.station.AbstractDockableStation;
import bibliothek.gui.dock.station.Combiner;
import bibliothek.gui.dock.station.DisplayerCollection;
import bibliothek.gui.dock.station.DisplayerFactory;
import bibliothek.gui.dock.station.DockableDisplayer;
import bibliothek.gui.dock.station.StationPaint;
import bibliothek.gui.dock.station.flap.ButtonPane;
import bibliothek.gui.dock.station.flap.DefaultFlapLayoutManager;
import bibliothek.gui.dock.station.flap.FlapDockHoldToggle;
import bibliothek.gui.dock.station.flap.FlapDockProperty;
import bibliothek.gui.dock.station.flap.FlapDockStationFactory;
import bibliothek.gui.dock.station.flap.FlapDropInfo;
import bibliothek.gui.dock.station.flap.FlapLayoutManager;
import bibliothek.gui.dock.station.flap.FlapWindow;
import bibliothek.gui.dock.station.support.CombinerWrapper;
import bibliothek.gui.dock.station.support.ConvertedPlaceholderListItem;
import bibliothek.gui.dock.station.support.DisplayerFactoryWrapper;
import bibliothek.gui.dock.station.support.DockableVisibilityManager;
import bibliothek.gui.dock.station.support.PlaceholderList;
import bibliothek.gui.dock.station.support.PlaceholderListItem;
import bibliothek.gui.dock.station.support.PlaceholderListItemAdapter;
import bibliothek.gui.dock.station.support.PlaceholderListItemConverter;
import bibliothek.gui.dock.station.support.PlaceholderMap;
import bibliothek.gui.dock.station.support.PlaceholderStrategy;
import bibliothek.gui.dock.station.support.StationPaintWrapper;
import bibliothek.gui.dock.station.support.PlaceholderList.Level;
import bibliothek.gui.dock.themes.basic.BasicButtonTitleFactory;
import bibliothek.gui.dock.title.ControllerTitleFactory;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.gui.dock.title.DockTitleRequest;
import bibliothek.gui.dock.title.DockTitleVersion;
import bibliothek.gui.dock.util.DockProperties;
import bibliothek.gui.dock.util.DockUtilities;
import bibliothek.gui.dock.util.PropertyKey;
import bibliothek.gui.dock.util.PropertyValue;
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
     * What kind of information should be displayed on the buttons. 
     */
    public static enum ButtonContent{
        THEME_DEPENDENT,
        ICON_ONLY, TEXT_ONLY, ICON_AND_TEXT_ONLY, ICON_THEN_TEXT_ONLY, TEXT_THEN_ICON_ONLY,
        ICON_ACTIONS, TEXT_ACTIONS, ICON_AND_TEXT_ACTIONS, ICON_THEN_TEXT_ACTIONS, TEXT_THEN_ICON_ACTIONS;
        

        /**
         * Tells whether actions should be shown on the button of a {@link FlapDockStation}
         * or not.
         * @param theme what the theme would do
         * @return <code>true</code> if the actions should be shown
         */
        public boolean showActions( boolean theme ){
            switch( this ){
                case ICON_AND_TEXT_ONLY:
                case ICON_ONLY:
                case ICON_THEN_TEXT_ONLY:
                case TEXT_ONLY:
                case TEXT_THEN_ICON_ONLY:
                    return false;
                case ICON_ACTIONS:
                case ICON_AND_TEXT_ACTIONS:
                case ICON_THEN_TEXT_ACTIONS:
                case TEXT_ACTIONS:
                case TEXT_THEN_ICON_ACTIONS:
                    return true;
            }
            return theme;
        }
        
        /**
         * Tells whether an icon should be shown.
         * @param text whether text is present or not.
         * @param theme what the theme would decide.
         * @return <code>true</code> if icons should be shown
         */
        public boolean showIcon( boolean text, boolean theme ){
            switch( this ){
                case ICON_ACTIONS:
                case ICON_AND_TEXT_ACTIONS:
                case ICON_AND_TEXT_ONLY:
                case ICON_ONLY:
                case ICON_THEN_TEXT_ACTIONS:
                case ICON_THEN_TEXT_ONLY:
                    return true;
                case TEXT_ACTIONS:
                case TEXT_ONLY:
                    return false;
                case TEXT_THEN_ICON_ACTIONS:
                case TEXT_THEN_ICON_ONLY:
                    return !text;
            }
            
            return theme;
        }
        
        /**
         * Tells whether text should be shown.
         * @param icon whether an icon is present or not
         * @param theme what the theme would decide.
         * @return <code>true</code> if text should be shown
         */
        public boolean showText( boolean icon, boolean theme ){
            switch( this ){
                case TEXT_ACTIONS:
                case TEXT_ONLY:
                case TEXT_THEN_ICON_ACTIONS:
                case TEXT_THEN_ICON_ONLY:
                case ICON_AND_TEXT_ACTIONS:
                case ICON_AND_TEXT_ONLY:
                    return true;
                case ICON_ACTIONS:
                case ICON_ONLY:
                    return false;
                case ICON_THEN_TEXT_ACTIONS:
                case ICON_THEN_TEXT_ONLY:
                    return !icon;
            }
            
            return theme;
        }
    };

    /**
     * Key for all {@link DockTheme}s, tells the theme what content on the buttons
     * should be visible. Note that some themes might ignore that setting.
     */
    public static final PropertyKey<ButtonContent> BUTTON_CONTENT = new PropertyKey<ButtonContent>(
            "flap dock station button content", new ConstantPropertyFactory<ButtonContent>( ButtonContent.THEME_DEPENDENT ), true );
    
    /**
     * Key for the minimum size of all {@link FlapDockStation}s.
     */
    public static final PropertyKey<Dimension> MINIMUM_SIZE = new PropertyKey<Dimension>( "flap dock station empty size",
    		new ConstantPropertyFactory<Dimension>( new Dimension( 10, 10 ) ), true );
    
    /**
     * The layoutManager which is responsible to layout this station
     */
    private PropertyValue<FlapLayoutManager> layoutManager = new PropertyValue<FlapLayoutManager>( LAYOUT_MANAGER ){
        @Override
        protected void valueChanged( FlapLayoutManager oldValue, FlapLayoutManager newValue ) {
            if( oldValue != null )
                oldValue.uninstall( FlapDockStation.this );
            
            if( newValue != null )
                newValue.install( FlapDockStation.this );
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
    
    /** The direction in which the popup-window is, in respect to this station */
    private Direction direction = Direction.SOUTH;
    /** 
     * This property tells this station whether the station can change the
     * {@link #direction} property automatically or not 
     */
    private boolean autoDirection = true;
    
    /** The popup-window */
    private FlapWindow window;
    /** The size of the border, which can be grabbed by ther user, of the popup-window */
    private int windowBorder = 3;
    /** The minimal size of the popup-window */
    private int windowMinSize = 25;
    /** The initial size of windows, can be overridden by the layout manager */
    private int defaultWindowSize = 400;
    
    /** 
     * This variable is set when the front-dockable is removed, because
     * the {@link DockController} is removed. If the controller is added
     * again, then the frond-dockable can be restored with the value of
     * this variable.
     */
    private Dockable oldFrontDockable;
    
    /** A list of all {@link Dockable Dockables} registered on this station */
    private PlaceholderList<DockableHandle> handles = new PlaceholderList<DockableHandle>();
    /** a listener for all {@link Dockable}s of this station */
    private Listener dockableListener = new Listener();
    
    /** The component on which all "buttons" are shown (the titles created with the id {@link #BUTTON_TITLE_ID}) */
    private ButtonPane buttonPane;
    
    /** This version is obtained by using {@link #BUTTON_TITLE_ID} */
    private DockTitleVersion buttonVersion;
    /** This version is obtained by using {@link #WINDOW_TITLE_ID} */
    private DockTitleVersion titleVersion;
    
    /** The {@link StationPaint} used to paint on this station */
    private StationPaintWrapper paint = new StationPaintWrapper();
    /** The {@link Combiner} user to combine {@link Dockable Dockables}*/
    private CombinerWrapper combiner = new CombinerWrapper();
    /** The {@link DisplayerFactory} used to create displayers*/
    private DisplayerFactoryWrapper displayerFactory = new DisplayerFactoryWrapper();
    /** Collection used to handle the {@link DockableDisplayer} */
    private DisplayerCollection displayers;
    
    /** 
     * Temporary information needed when a {@link Dockable} is moved
     * over this station.
     */
    private FlapDropInfo dropInfo;
    
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
    /** A list of listeners that were added to this station */
    private List<FlapDockListener> flapDockListeners = new ArrayList<FlapDockListener>();
    
    /** Manager for the visibility of the children of this station */
    private DockableVisibilityManager visibility;
    
    
    
    /**
     * Defaultconstructor of a {@link FlapDockStation}
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
    	if( init )
    		init();
    }
    
    /**
     * Initializes the fields of this station, hast to be called exactly once
     */
    protected void init(){
        visibility = new DockableVisibilityManager( listeners );
        buttonPane = createButtonPane();
        setDirection( Direction.SOUTH );
        
        displayers = new DisplayerCollection( this, displayerFactory );
        
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
                getController().getFocusObserver().removeVetoListener( controllerListener );
                
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
                controller.getFocusObserver().addVetoListener( controllerListener );
                
                if( isStationVisible() )
                    setFrontDockable( oldFrontDockable );
            }
            
            buttonPane.resetTitles();
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
     * may be overridden sone, if the property {@link #isAutoDirection() autoDirection} 
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
    public DisplayerFactoryWrapper getDisplayerFactory() {
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
    public CombinerWrapper getCombiner() {
        return combiner;
    }
    
    /**
     * Gets the {@link StationPaint} to paint on this station.
     * @return The paint
     */
    public StationPaintWrapper getPaint() {
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
        
        if( dockable == null ){
            if( window != null ){
                window.setDockable( null );
            }
        }
        else {
            Window owner = SwingUtilities.getWindowAncestor( getComponent() );
            if( window == null || window.getOwner() != owner ){
                if( window != null ){
                    window.setDockable( null );
                }
                
                FlapWindow window = createFlapWindow( owner, buttonPane );
                if( window != null )
                    setFlapWindow( window );
            }
            
            if( window != null && owner != null ){
                window.setDockable( dockable );
                if( owner.isVisible() )
                    window.setVisible( true );
            
                updateWindowBounds();
            }
        }
        
        if( oldFrontDockable != null ){
            if( getController() != null ){
                DockTitle[] titles = oldFrontDockable.listBoundTitles();
                boolean active = getController().isFocused( oldFrontDockable );
                for( DockTitle title : titles )
                    changed( oldFrontDockable, title, active );
            }
        }
        
        if( window != null ){
        	if( window.getDockable() == null )
        		window.setVisible( false );
        	else
        		window.repaint();
        }
        
        visibility.fire();
        listeners.fireDockableSelected( oldFrontDockable, dockable );
    }
    
    /**
     * Creates a window for this station.
     * @param owner the owner of the window
     * @param buttonPane the panel needed to calculate the size of the window
     * @return the window or <code>null</code> if no window could be created
     */
    protected FlapWindow createFlapWindow( Window owner, ButtonPane buttonPane ){
        if( owner instanceof Dialog )
            return new FlapWindow( this, buttonPane, (Dialog)owner );
        else if( owner instanceof Frame )
            return new FlapWindow( this, buttonPane, (Frame)owner );
        return null;
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
    public DefaultDockActionSource getDirectActionOffers( Dockable dockable ) {
    	if( holdAction == null )
    		return null;
    	else{
    		DefaultDockActionSource source = new DefaultDockActionSource(new LocationHint( LocationHint.DIRECT_ACTION, LocationHint.LITTLE_LEFT ));
            source.add( holdAction );
            return source;
    	}
    }
    
    @Override
    public void changed( Dockable dockable, DockTitle title, boolean active ) {
        DockTitleEvent event = new DockTitleEvent( this, dockable, active );
        event.setPreferred( dockable == getFrontDockable() );
        title.changed( event );
    }
    
    @Override
    public Rectangle getStationBounds() {
        Point point = new Point( 0, 0 );
        SwingUtilities.convertPointToScreen( point, getComponent() );
        Rectangle result = new Rectangle( point.x, point.y, getComponent().getWidth(), getComponent().getHeight() );
        
        if( window != null && window.isVisible() ){
            result = SwingUtilities.computeUnion( window.getX(), window.getY(), window.getWidth(), window.getHeight(), result );
        }
        
        return result;
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
    	if( this.window != null )
    		this.window.dispose();
    	
        this.window = window;
        if( window != null )
            window.setDropInfo( dropInfo );
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
    
    public PlaceholderMap getPlaceholders(){
	    return handles.toMap();
    }
    
    public void setPlaceholders( PlaceholderMap placeholders ){
    	if( getDockableCount() > 0 ){
    		throw new IllegalStateException( "only allowed if there are not children present" );
    	}
    	
    	try{
    		PlaceholderList<DockableHandle> next = new PlaceholderList<DockableHandle>( placeholders );
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
    	
    	return handles.toMap( new PlaceholderListItemAdapter<DockableHandle>() {
    		@Override
    		public ConvertedPlaceholderListItem convert( int index, DockableHandle dockable ){
	    		ConvertedPlaceholderListItem item = new ConvertedPlaceholderListItem();
	    		item.putInt( "id", children.get( dockable.getDockable() ) );
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
    	if( getDockableCount() > 0 ){
    		throw new IllegalStateException( "must not have any children" );
    	}
    	
		PlaceholderList<DockableHandle> next = new PlaceholderList<DockableHandle>( map, new PlaceholderListItemAdapter<DockableHandle>(){
			@Override
			public DockableHandle convert( ConvertedPlaceholderListItem item ){
				int id = item.getInt( "id" );
				Dockable dockable = children.get( id );
				if( dockable != null ){
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
				dockable.getDockable().setDockParent( FlapDockStation.this );
				listeners.fireDockableAdded( dockable.getDockable() );
			}
		});
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
		buttonPane.resetTitles();
    }

    public boolean prepareDrop( int mouseX, int mouseY, int titleX, int titleY, boolean checkOverrideZone, Dockable dockable ) {
    	if( SwingUtilities.isDescendingFrom( getComponent(), dockable.getComponent() )){
    		setDropInfo( null );
    		return false;
    	}
        
        Point mouse = new Point( mouseX, mouseY );
        SwingUtilities.convertPointFromScreen( mouse, buttonPane );
        
        boolean strong = buttonPane.titleContains( mouse.x, mouse.y );
        boolean combine = false;
        
        DockAcceptance acceptance = getController().getAcceptance();
        
        if( !strong && window != null && window.isVisible() ){
            DockTitle title = window.getDockTitle();
            if( title != null ){
                Component c = title.getComponent();
                Point point = new Point( mouseX, mouseY );
                SwingUtilities.convertPointFromScreen( point, c );
                // test if combination is allowed
                Dockable child = window.getDockable();
                
                combine = c.contains( point ) &&
                    dockable.accept( this, child ) &&
                    child.accept( this, dockable ) &&
                    acceptance.accept( this, child, dockable );
            }
        }
        
        if( !strong && !combine ){
            DockStation parent = getDockParent();
            if( parent != null ){
                if( checkOverrideZone && parent.isInOverrideZone( mouseX, mouseY, this, dockable ))
                    return false;
            }
        }
        
        if( window != null && window.isVisible() && !combine ){
            Point point = new Point( mouseX, mouseY );
            SwingUtilities.convertPointFromScreen( point, window );
            Dockable child = window.getDockable();
            combine = window.contains( point ) &&
                dockable.accept( this, child) &&
                child.accept( this, dockable ) &&
                acceptance.accept( this, child, dockable );
        }
        
        if( combine && dockable == getFrontDockable() )
            return false;
        
        FlapDropInfo dropInfo = null;
        if( combine ){
            dropInfo = new FlapDropInfo( dockable );
        	dropInfo.setCombine( getFrontDockable() );
        }
        else{
            if( dockable.accept( this ) &&
                accept( dockable ) &&
                acceptance.accept( this, dockable )){
                
                dropInfo = new FlapDropInfo( dockable );
                dropInfo.setIndex( buttonPane.indexAt( mouse.x, mouse.y ) );
            }
        }
        
        setDropInfo( dropInfo );
        return dropInfo != null;
        
    }

    public void drop(){
        if( dropInfo.getCombine() != null ){
        	combine( dropInfo.getCombine(), dropInfo.getDockable());
        }
        else{
            add( dropInfo.getDockable(), dropInfo.getIndex() );
        }
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
    public boolean drop( Dockable dockable, FlapDockProperty property ) {
        DockUtilities.ensureTreeValidity( this, dockable );
        boolean result = false;
        
        Path placeholder = property.getPlaceholder();
        DockableProperty successor = property.getSuccessor();
        int index = property.getIndex();
        boolean acceptable = acceptable( dockable );
        
    	if( placeholder != null && successor != null ){
    		DockableHandle current = handles.getDockableAt( placeholder );
    		if( current != null ){
    			DockStation station = current.getDockable().asDockStation();
    			if( station != null ){
    				if( station.drop( dockable, successor )){
    					result = true;
    					handles.removeAll( placeholder );
    				}
    			}
    		}
    	}
    	else if( placeholder != null ){
    		index = handles.getDockableIndex( placeholder );
    		if( index == -1 ){
    			index = property.getIndex();
    		}
    		else{
    			if( acceptable ){
	    			listeners.fireDockableAdding( dockable );
	    			DockableHandle handle = link( dockable );
	    			handles.put( placeholder, handle );
	    			dockable.setDockParent( this );
	    			listeners.fireDockableAdded( dockable );
	    			result = true;
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

    public boolean prepareMove( int mouseX, int mouseY, int titleX, int titleY,
            boolean checkOverrideZone, Dockable dockable ){
        
        return prepareDrop( mouseX, mouseY, titleX, titleY, checkOverrideZone, dockable );
    }

    public void move() {
    	if( dropInfo.getCombine() != null ){
            remove( dropInfo.getDockable() );
            combine( dropInfo.getCombine(), dropInfo.getDockable());
        }
    	else{
	    	int index = indexOf( dropInfo.getDockable() );
	    	if( index < dropInfo.getIndex() ){
	    		dropInfo.setIndex( dropInfo.getIndex()-1 );
	    	}
	    	handles.dockables().move( index, dropInfo.getIndex() );
	    	buttonPane.resetTitles();
    	}
    }
    
    public void move( Dockable dockable, DockableProperty property ) {
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
            }
        }
    }

    public void draw() {
        if( dropInfo != null )
            dropInfo.setDraw( true );
        buttonPane.repaint();
        if( window != null )
            window.repaint();
    }

    public void forget() {
        setDropInfo( null );
        buttonPane.repaint();
    }

    public <D extends Dockable & DockStation> boolean isInOverrideZone( int x,
            int y, D invoker, Dockable drop ) {
        
        Point mouse = new Point( x, y );
        SwingUtilities.convertPointFromScreen( mouse, buttonPane );
        if( buttonPane.contains( mouse ) && accept( drop ) && drop.accept( this ))
            return true;
        
        DockStation parent = getDockParent();
        if( parent != null )
            return parent.isInOverrideZone( x, y, invoker, drop );

        return false;
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
        return isStationVisible() && (getFrontDockable() == dockable);
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
     * and {@link DockController#meltLayout()} to ensure noone else adds or
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
     * and {@link DockController#meltLayout()} to ensure noone else adds or
     * removes <code>Dockable</code>s.
     * @param index the index of the child that will be removed
     */
    public void remove( int index ){
        Dockable dockable = getDockable( index );
        if( getFrontDockable() == dockable )
            setFrontDockable( null );
        
        if( oldFrontDockable == dockable )
            oldFrontDockable = null;
        
        listeners.fireDockableRemoving( dockable );
        dockable.setDockParent( null );
        DockableHandle handle = handles.dockables().get( index );
        handles.remove( index );
        handle.setTitle( null );
        dockable.removeDockableListener( dockableListener );
        listeners.fireDockableRemoved( dockable );
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
        DockUtilities.ensureTreeValidity( this, dockable );
        
        listeners.fireDockableAdding( dockable );
        DockableHandle handle = link( dockable );
        handles.dockables().add( index, handle );
        dockable.setDockParent( this );
        listeners.fireDockableAdded( dockable );
    }
    
    private DockableHandle link( Dockable dockable ){
    	DockableHandle handle = new DockableHandle( dockable );
        handle.setTitle( buttonVersion );
        dockable.addDockableListener( dockableListener );
        return handle;
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
    	DockController controller = getController();
    	try{
    		if( controller != null )
    			controller.freezeLayout();
    	
	        int index = indexOf( child );
	        if( index < 0 )
	            throw new IllegalArgumentException( "Child must be a child of this station" );
	        
	        if( append.getDockParent() != null )
	            append.getDockParent().drag( append );
	        
	        boolean hold = isHold( child );
	        
	        int listIndex = handles.levelToBase( index, Level.DOCKABLE );
	        PlaceholderList<DockableHandle>.Item oldItem = handles.list().get( listIndex );
	        
	        remove( index );
	        int other = indexOf( append );
	        if( other >= 0 ){
	            remove( other );
	            if( other < index )
	                index--;
	        }
	        
	        index = Math.min( index, getDockableCount());
	        Dockable combination = combiner.combine( child, append, this, oldItem.getPlaceholderMap() );
	        add( combination, index );
	        
	        PlaceholderList<DockableHandle>.Item newItem = handles.list().get( listIndex );
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
    		PlaceholderList<DockableHandle>.Item oldItem = handles.list().get( listIndex );
    		remove( index );
    		add( append, index );
    		PlaceholderList<DockableHandle>.Item newItem = handles.list().get( listIndex );
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
    
    /**
     * This listener is added to the direct parent of the enclosing
     * {@link FlapDockListener}. The listener fires events if the visibility
     * changes, and the listener can remove the popup-window if the station
     * looses its visibility. 
     * @author Benjamin Sigg
     */
    private class VisibleListener extends DockStationAdapter{
        /** The last known state. Used to react only if real changes happen */
        private boolean visible = false;
        
        @Override
        public void dockableVisibiltySet( DockStation station, Dockable dockable, boolean visible ) {
            if( visible != this.visible ){
                if( dockable == FlapDockStation.this ){
                    this.visible = visible;
                    
                    if( visible ){
                        if( oldFrontDockable != null )
                            setFrontDockable( oldFrontDockable );
                    }
                    else{
                        oldFrontDockable = getFrontDockable();
                        setFrontDockable( null );
                        if( !isHold( oldFrontDockable ))
                            oldFrontDockable = null;
                    }
                    
                    visibility.fire();
                }
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
     * Handles title and listeners that are associated with a {@link Dockable}.
     * @author Benjamin Sigg
     */
    private class DockableHandle implements PlaceholderListItem{
    	/** the element that is handled by this handler */
    	private Dockable dockable;
    	/** the title used */
    	private DockTitleRequest title;
    	/** the listener that gets added to the title of this handle */
    	private ButtonListener buttonListener;
    	
    	public DockableHandle( Dockable dockable ){
    		this.dockable = dockable;
    		buttonListener = new ButtonListener( dockable );
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
        public FocusVeto vetoFocus( MouseFocusObserver controller, Dockable dockable ) {
            return FocusVeto.NONE;
        }
        
        public FocusVeto vetoFocus( MouseFocusObserver controller, DockTitle title ) {
        	for( DockableHandle handle : handles.dockables() ){
        		if( handle.getTitle() == title ){
        			return FocusVeto.VETO_NO_CONSUME;
        		}
        	}
        	
            return FocusVeto.NONE;
        }
        
        public void dockableFocused( DockableFocusEvent event ) {
            Dockable front = getFrontDockable();
            
            if( isStationVisible() ){
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
        		
        		if( e.getButton() == MouseEvent.BUTTON1 && (e.getModifiersEx() & MASK ) == 0 ){
        			int index = indexOf( dockable );
        			if( index < 0 )
        				return;
        			
        			DockableHandle handle = handles.dockables().get( index );
        			DockTitle title = handle.getTitle();
	        		
		            if( getFrontDockable() == dockable && title.isActive() ){
		                getController().setFocusedDockable( FlapDockStation.this, true );
		                setFrontDockable( null );
		            }
		            else
		                getController().setFocusedDockable( dockable, true );
        		}
        	}
        }
    }
}
