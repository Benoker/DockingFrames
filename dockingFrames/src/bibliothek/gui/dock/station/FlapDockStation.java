/**
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2007 Benjamin Sigg
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * Benjamin Sigg
 * benjamin_sigg@gmx.ch
 * 
 * Wunderklingerstr. 59
 * 8215 Hallau
 * CH - Switzerland
 */


package bibliothek.gui.dock.station;

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
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputAdapter;

import bibliothek.gui.DockController;
import bibliothek.gui.DockUI;
import bibliothek.gui.DockUtilities;
import bibliothek.gui.dock.DockAcceptance;
import bibliothek.gui.dock.DockStation;
import bibliothek.gui.dock.Dockable;
import bibliothek.gui.dock.DockableDisplayer;
import bibliothek.gui.dock.DockableProperty;
import bibliothek.gui.dock.FocusController;
import bibliothek.gui.dock.action.DefaultDockActionSource;
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.action.LocationHint;
import bibliothek.gui.dock.event.DockControllerAdapter;
import bibliothek.gui.dock.event.DockStationAdapter;
import bibliothek.gui.dock.event.DockTitleEvent;
import bibliothek.gui.dock.event.FlapDockListener;
import bibliothek.gui.dock.event.FocusVetoListener;
import bibliothek.gui.dock.station.flap.ButtonPane;
import bibliothek.gui.dock.station.flap.FlapDockHoldToggle;
import bibliothek.gui.dock.station.flap.FlapDockProperty;
import bibliothek.gui.dock.station.flap.FlapDockStationFactory;
import bibliothek.gui.dock.station.flap.FlapDropInfo;
import bibliothek.gui.dock.station.flap.FlapWindow;
import bibliothek.gui.dock.station.support.CombinerWrapper;
import bibliothek.gui.dock.station.support.DisplayerFactoryWrapper;
import bibliothek.gui.dock.station.support.DockableVisibilityManager;
import bibliothek.gui.dock.station.support.StationPaintWrapper;
import bibliothek.gui.dock.title.ButtonTitleFactory;
import bibliothek.gui.dock.title.ControllerTitleFactory;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.gui.dock.title.DockTitleVersion;

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
    
    /** The direction in which the popup-window is, in respect to this station */
    private Direction direction = Direction.SOUTH;
    /** 
     * This property tells this station whether the station can change the
     * {@link #direction} property automatically or not 
     */
    private boolean autoDirection = true;
    
    /** The popup-window */
    private FlapWindow window;
    /** The size of the popup-window */
    private int windowSize = 400;
    /** The size of the border, which can be grabbed by ther user, of the popup-window */
    private int windowBorder = 3;
    /** The minimal size of the popup-window */
    private int windowMinSize = 25;
    
    /** 
     * This variable is set when the front-dockable is removed, because
     * the {@link DockController} is removed. If the controller is added
     * again, then the frond-dockable can be restored with the value of
     * this variable.
     */
    private Dockable oldFrontDockable;
    
    /** A list of all {@link Dockable Dockables} registered on this station */
    private List<Dockable> dockables = new ArrayList<Dockable>();
    
    /** A map that tells for every {@link Dockable} which {@link DockTitle} is used for it */
    private Map<Dockable, DockTitle> buttonTitles = new HashMap<Dockable, DockTitle>();
    /** A map that tells for every {@link DockTitle} which listeners are used for it */
    private Map<DockTitle, ButtonListener> buttonListeners = new HashMap<DockTitle, ButtonListener>();
    
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
    
    /** A listener added to the {@link FocusController} */
    private ControllerListener controllerListener = new ControllerListener();
    
    /** 
     * The button-titles are organized in a way that does not need much
     * space if this property is <code>true</code>
     */
    private boolean smallButtons = true;
    
    /**
     * A map that tells for every {@link Dockable} whether it should remain
     * on the popup-window even if it has lost the focus, or if it should
     * not remain on the window.
     */
    private Map<Dockable, Boolean> hold = new HashMap<Dockable, Boolean>();
    /** 
     * An action that will be added to all children of this station. The
     * user can change the {@link #hold}-property with this action. 
     */
    private DockAction holdAction;
    
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
        visibility = new DockableVisibilityManager( listeners );
        buttonPane = new ButtonPane( this, buttonTitles );
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
     * Creates a {@link DockAction} that is added to all children
     * of this station. The action should change the <code>hold</code>
     * state of the associated {@link Dockable}, this can be done
     * through the method {@link #setHold(Dockable, boolean)}.
     * @return The action, or <code>null</code> if no action should
     * be added to the children
     */
    protected DockAction createHoldAction(){
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
                getController().removeDockControllerListener( controllerListener );
                getController().getFocusController().removeVetoListener( controllerListener );
                
                oldFrontDockable = getFrontDockable();
                setFrontDockable( null );
                
                // remove titles
                for( Dockable dockable : dockables ){
                    DockTitle title = buttonTitles.get( dockable );
                    if( title != null ){
                        unbind( dockable, title );
                    }
                }
                
                titleVersion = null;
                buttonVersion = null;
            }
    
            super.setController(controller);
            displayers.setController( controller );

            if( controller != null ){
                titleVersion = controller.getDockTitleManager().registerDefault( WINDOW_TITLE_ID, ControllerTitleFactory.INSTANCE );
                buttonVersion = controller.getDockTitleManager().registerDefault( BUTTON_TITLE_ID, ButtonTitleFactory.FACTORY );
                
                for( Dockable dockable : dockables ){
                    DockTitle title = dockable.getDockTitle( buttonVersion );
                    if( title != null )
                        bind( dockable, title );
                }
                
                if( window != null ){
                    Dockable dockable = window.getDockable();
                    if( dockable != null ){
                        DockTitle title = dockable.getDockTitle( titleVersion );
                        if( title != null )
                            dockable.bind( title );
                        window.setDockTitle( title );
                    }
                }
                
                controller.addDockControllerListener( controllerListener );
                controller.getFocusController().addVetoListener( controllerListener );
                
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
     * is set.
     * @param direction The direction of the popup-window 
     */
    public void setDirection( Direction direction ) {
        if( direction == null )
            throw new IllegalArgumentException();
             
        this.direction = direction;
        DockTitle.Orientation orientation = orientation( direction );
        
        
        for( DockTitle title : buttonTitles.values() )
            title.setOrientation( orientation );
        
        buttonPane.resetTitles();
        updateWindowBounds();
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
     * {@link #setDirection(bibliothek.gui.dock.station.FlapDockStation.Direction) direction}
     * itself, or if only the user can change the direction. 
     * @return <code>true</code> if the station chooses the direction itself
     * @see #setAutoDirection(boolean)
     */
    public boolean isAutoDirection() {
        return autoDirection;
    }
    
    /**
     * Tells this station whether it can choose the 
     * {@link #setDirection(bibliothek.gui.dock.station.FlapDockStation.Direction) direction}
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
     * {@link #setDirection(bibliothek.gui.dock.station.FlapDockStation.Direction) direction}
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
        
        if( oldFrontDockable == dockable )
            return;
        
        if( dockable == null ){
            if( window != null ){
                window.setDockable( null );
                window.setVisible( false );
            }
        }
        else{
            Window owner = SwingUtilities.getWindowAncestor( getComponent() );
            if( window == null || window.getOwner() != owner ){
                if( window != null )
                    window.setDockable( null );
                
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
                DockTitle[] titles = getDockTitles( oldFrontDockable );
                boolean active = getController().isFocused( oldFrontDockable );
                for( DockTitle title : titles )
                    changed( oldFrontDockable, title, active );
            }
        }
        
        visibility.fire();
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
        Boolean value = hold.get( dockable );
        if( value == null )
            return false;
        else
            return value;
    }
    
    /**
     * Tells whether the station should close the popup when the 
     * {@link Dockable} looses the focus, or if the popup should
     * remain open until the user closes the popup.
     * @param dockable the {@link Dockable} whose settings should change
     * @param hold <code>true</code> if the popup should remain open,
     * <code>false</code> if it should close
     */
    public void setHold( Dockable dockable, boolean hold ) {
        this.hold.put( dockable, hold );
        fireHoldChanged( dockable, hold );
        
        if( !hold && getController() != null && getFrontDockable() == dockable ){
            if( !getController().isFocused( dockable ))
                setFrontDockable( null );
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
     * @return the current size
     */
    public int getWindowSize(){
        return windowSize;
    }
    
    /**
     * Sets the size of the popup-window.
     * @param size the size, at least 0
     */
    public void setWindowSize( int size ){
        if( size < 0 )
            throw new IllegalArgumentException( "Size must at least be 0" );
        
        windowSize = size;
        updateWindowBounds();
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
    
    public DockTitle[] getDockTitles( Dockable dockable ) {
        DockTitle alpha = buttonTitles.get( dockable );
        DockTitle beta = getFrontDockable() == dockable ? window.getDockTitle() : null;
        
        if( alpha == null && beta == null )
            return new DockTitle[0];
        
        if( alpha == null )
            return new DockTitle[]{ beta };
        
        if( beta == null )
            return new DockTitle[]{ alpha };
        
        return new DockTitle[]{ alpha, beta };
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
        this.window = window;
        if( window != null )
            window.setDropInfo( dropInfo );
    }
    
    public boolean prepareDrop( int mouseX, int mouseY, int titleX, int titleY,
            Dockable dockable ) {
        
        Point mouse = new Point( mouseX, mouseY );
        SwingUtilities.convertPointFromScreen( mouse, buttonPane );
        
        boolean strong = buttonPane.titleContains( mouse.x, mouse.y );
        boolean combine = false;
        
        DockAcceptance acceptance = getController() == null ? null : getController().getAcceptance();
        
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
                    ( acceptance == null || 
                            acceptance.accept( this, child, dockable ));
            }
        }
        
        if( !strong && !combine ){
            DockStation parent = getDockParent();
            if( parent != null ){
                if( parent.isInOverrideZone( mouseX, mouseY, this, dockable ))
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
                (acceptance == null ||
                        acceptance.accept( this, child, dockable ));
        }
        
        if( combine && dockable == getFrontDockable() )
            return false;
        
        FlapDropInfo dropInfo = new FlapDropInfo( dockable );
        dropInfo.setCombine( combine );
        if( !combine ){
            dropInfo.setIndex( buttonPane.indexAt( mouse.x, mouse.y ) );
        }
        setDropInfo( dropInfo );
        return true;
    }

    public void drop(){
        if( dropInfo.isCombine() ){
            Dockable front = getFrontDockable();
            if( front != null ){
                combine( front, dropInfo.getDockable());
            }
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
        int index = property.getIndex();
        
        if( index >= getDockableCount() && this.accept( dockable ) && dockable.accept( this )){
            add( dockable );
            return true;
        }
        
        DockableProperty successor = property.getSuccessor();
        if( successor != null ){
            DockStation previous = getDockable( index ).asDockStation();
            if( previous != null ){
                if( previous.drop( dockable, successor ))
                    return true;
            }
        }
        
        if( dockable.accept( this ) && this.accept( dockable )){
            add( dockable, index );
            return true;
        }
        else
            return false;
    }

    public DockableProperty getDockableProperty( Dockable dockable ) {
        return new FlapDockProperty( indexOf( dockable ));
    }

    public boolean prepareMove( int mouseX, int mouseY, int titleX, int titleY,
            Dockable dockable ){
        
        return prepareDrop( mouseX, mouseY, titleX, titleY, dockable );
    }

    public void move() {
        int index = indexOf( dropInfo.getDockable() );
        dockables.remove( index );
        if( index < dropInfo.getIndex() )
            dropInfo.setIndex( dropInfo.getIndex()-1 );
        dockables.add( dropInfo.getIndex(), dropInfo.getDockable() );
        buttonPane.resetTitles();
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
        remove( dockable );
    }

    public String getFactoryID() {
        return FlapDockStationFactory.ID;
    }

    public Component getComponent() {
        return buttonPane;
    }
    
    public int getDockableCount() {
        return dockables.size();
    }

    public Dockable getDockable( int index ) {
        return dockables.get( index );
    }
    
    @Override
    public boolean isVisible( Dockable dockable ) {
        return isStationVisible() && (getFrontDockable() == dockable);
    }
    
    /**
     * Removes <code>dockable</code> from this station
     * @param dockable the child to remove
     */
    public void remove( Dockable dockable ){
        int index = indexOf( dockable );
        if( index >= 0 )
            remove( index );
    }
    
    /**
     * Removes the child with the given <code>index</code> from this station.
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
        dockables.remove( index );
        hold.remove( dockable );
        
        DockTitle title = buttonTitles.get( dockable );
        if( title != null )
            unbind( dockable, title );
        
        buttonPane.resetTitles();
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
        if( dockable.getDockParent() != null && dockable.getDockParent() != this )
            throw new IllegalArgumentException( "Dockable must not have another parent" );
        
        listeners.fireDockableAdding( dockable );
        dockables.add( index, dockable );
        dockable.setDockParent( this );
        if( buttonVersion != null ){
            DockTitle title = dockable.getDockTitle( buttonVersion );
            if( title != null )
                bind( dockable, title );
            buttonPane.resetTitles();
        }
        listeners.fireDockableAdded( dockable );
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
        int index = indexOf( child );
        if( index < 0 )
            throw new IllegalArgumentException( "Child must be a child of this station" );
        
        if( append.getDockParent() != null )
            append.getDockParent().drag( append );
        
        boolean hold = isHold( child );
        
        remove( index );
        int other = indexOf( append );
        if( other >= 0 ){
            remove( other );
            if( other < index )
                index--;
        }
        
        index = Math.min( index, getDockableCount());
        Dockable combination = combiner.combine( child, append, this );
        add( combination, index );
        setHold( combination, hold );
        return true;
    }
    
    public boolean canReplace( Dockable old, Dockable next ) {
        return true;
    }
    
    public void replace( Dockable child, Dockable append ){
        int index = indexOf( child );
        if( index < 0 )
            throw new IllegalArgumentException( "Child must be a child of this station" );
        
        boolean hold = isHold( child );
        boolean open = getFrontDockable() == child;
        
        remove( index );
        add( append, index );
        setHold( append, hold );
        
        if( open )
            setFrontDockable( append );
    }
    
    /**
     * Gets the location of <code>dockable</code> in the button-panel.
     * @param dockable the {@link Dockable} to search
     * @return the location or -1 if the child was not found
     */
    public int indexOf( Dockable dockable ){
        return dockables.indexOf( dockable );
    }
    
    /**
     * Binds the <code>title</code> to <code>dockable</code>. This method
     * is invoked only if the two arguments are not yet binded.
     * @param dockable the {@link Dockable}
     * @param title the {@link DockTitle}
     * @see Dockable#bind(DockTitle)
     */
    protected void bind( Dockable dockable, DockTitle title ){
        buttonTitles.put( dockable, title );
        
        ButtonListener listener = new ButtonListener( dockable );
        title.addMouseInputListener( listener );
        buttonListeners.put( title, listener );
        
        title.setOrientation( orientation( direction ) );
        
        if( title != null )
            dockable.bind( title );
    }
    
    /**
     * Unbinds the <code>title</code> from <code>dockable</code>. This
     * method is only called if the two arguments are binded.
     * @param dockable the {@link Dockable}
     * @param title the {@link DockTitle}
     * @see Dockable#unbind(DockTitle)
     */
    protected void unbind( Dockable dockable, DockTitle title ){
        ButtonListener listener = buttonListeners.remove( title );
        title.removeMouseInputListener( listener );
        
        buttonTitles.remove( dockable );
        if( title != null )
            dockable.unbind( title );
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
    
    private class ControllerListener extends DockControllerAdapter implements FocusVetoListener{
        public boolean vetoFocus( FocusController controller, Dockable dockable ) {
            return false;
        }
        
        public boolean vetoFocus( FocusController controller, DockTitle title ) {
            return buttonTitles.containsValue( title );
        }
        
        @Override
        public void dockableFocused( DockController controller, Dockable dockable ) {
            Dockable front = getFrontDockable();
            
            if( isStationVisible() ){
                if( front == null || (front != null && isHold( front )))
                    return;
                
                if( controller.isFocused( FlapDockStation.this ))
                    return;
                
                if( dockable == null || !DockUtilities.isAnchestor( FlapDockStation.this, dockable ) ){
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
        public void mousePressed( MouseEvent e ) {
            DockTitle title = buttonTitles.get( dockable );
            
            if( getFrontDockable() == dockable && title.isActive() ){
                getController().setFocusedDockable( FlapDockStation.this, true );
                setFrontDockable( null );
            }
            else
                getController().setFocusedDockable( dockable, true );
        }
        
    }
}
