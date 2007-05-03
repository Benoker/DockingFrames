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

import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputAdapter;
import javax.swing.event.MouseInputListener;

import bibliothek.gui.DockController;
import bibliothek.gui.DockTheme;
import bibliothek.gui.DockUI;
import bibliothek.gui.dock.*;
import bibliothek.gui.dock.action.DefaultDockActionSource;
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.action.DockActionSource;
import bibliothek.gui.dock.action.LocationHint;
import bibliothek.gui.dock.event.*;
import bibliothek.gui.dock.station.split.*;
import bibliothek.gui.dock.station.split.SplitDockTree.Key;
import bibliothek.gui.dock.station.support.*;
import bibliothek.gui.dock.title.ControllerTitleFactory;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.gui.dock.title.DockTitleFactory;
import bibliothek.gui.dock.title.DockTitleVersion;
import bibliothek.gui.dock.util.PropertyKey;
import bibliothek.gui.dock.util.PropertyValue;

/*
 * Notiz:
 * Der DividerListener setzt den Cursor beim Root. Die Demo zeigt, dass das
 * die einfachste Variante ist, den Cursor auch wirklich anzeigen zu lassen.
 * Leider ist das nicht eben gutes Design... und Fehleranf�llig.
 */

/**
 * This station shows all its children at once. The children are separated
 * by small gaps which can be moved by the user. It is possible to set
 * one child to {@link #setFullScreen(Dockable) fullscreen}, this child will
 * be shown above all other children. The user can double click on the title
 * of a child to change its fullscreen-mode.<br>
 * The station tries to register a {@link DockTitleFactory} with the
 * ID {@link #TITLE_ID}.
 * @author Benjamin Sigg
 */
public class SplitDockStation extends JPanel implements Dockable, DockStation {
    /** The ID under which this station tries to register a {@link DockTitleFactory} */
    public static final String TITLE_ID = "split";
    
    /** The parent of this station */
    private DockStation parent;
    
    /** Listener registered to the parent. When triggered it invokes other listeners */
    private VisibleListener visibleListener = new VisibleListener();
    
    /** The controller to which this station is registered */
    private DockController controller;
    
    /** The theme of this station */
    private DockTheme theme;
    
    /** Combiner to {@link #dropOver(Leaf, Dockable) combine} some Dockables */
    private CombinerWrapper combiner = new CombinerWrapper();
    
    /** The type of titles which are used for this station */
    private DockTitleVersion title;

    /** A list of {@link DockableListener} which will be invoked when something noticable happens */
    private List<DockableListener> dockableListeners = new ArrayList<DockableListener>();
    
    /** A list of {@link SplitDockListener} which will be invoked when something noticable happens */
    private List<SplitDockListener> splitListeners = new ArrayList<SplitDockListener>();
    
    /** The handler for events and listeners concerning the visibility of children */
    private DockableVisibilityManager visibility;
    
    /**
     * The list of all registered {@link DockStationListener DockStationListeners}. 
     * This list can be used to send events to all listeners.
     */
    protected DockStationListenerManager dockStationListeners = new DockStationListenerManager( this );
    
    /** Optional text for this station */
    private PropertyValue<String> titleText = new PropertyValue<String>( PropertyKey.DOCK_STATION_TITLE ){
    	@Override
    	protected void valueChanged( String oldValue, String newValue ){
    		if( oldValue == null )
    			oldValue = "";
    		if( newValue == null )
    			newValue = "";
    		
    		for( DockableListener listener : dockableListeners.toArray( new DockableListener[ dockableListeners.size() ] ))
                listener.titleTextChanged( SplitDockStation.this, oldValue, newValue );
    	}
    };
    
    /** Optional icon for this station */
    private PropertyValue<Icon> titleIcon = new PropertyValue<Icon>( PropertyKey.DOCK_STATION_ICON ){
    	@Override
    	protected void valueChanged( Icon oldValue, Icon newValue ){
    		for( DockableListener listener : dockableListeners.toArray( new DockableListener[ dockableListeners.size() ] ))
                listener.titleIconChanged( SplitDockStation.this, oldValue, newValue );
    	}
    };
    
    /** 
     * This listener decides on which children the user can click to set them
     * into fullscreen-mode
     */
    private FullScreenClickableListener fullScreenListener;
    
    /** Whether the user can double click on a child to expand it. Default is <code>true</code>. */
    private boolean expandOnDoubleclick = true;
    
    /** The list of {@link Dockable Dockables} which are shown on this station */
    private List<DockableDisplayer> dockables = new ArrayList<DockableDisplayer>();
    
    /** The {@link Dockable} which has the focus */
    private Dockable frontDockable;
    
    /** The {@link Dockable} which is currently in fullscreen-mode. This value might be <code>null</code> */
    private DockableDisplayer fullScreenDockable;
    
    /** An action that is added to all children. The action changes the fullscreen-mode of the child. Can be <code>null</code> */
    private DockAction fullScreenAction;
    
    /** Size of the gap between two children in pixel */
    private int dividerSize = 4;
    
    /** 
     * Relative size of the border where a {@link Dockable} will be placed aside 
     * another Dockable when dragging the new Dockable onto this station. Should
     * be between 0 and 0.25f.
     */
    private float sideSnapSize = 1 / 4f;
    
    /** 
     *  Size of the border outside this station where a {@link Dockable} will still
     *  be considered to be dropped onto this station. Measured in pixel.
     */
    private int borderSideSnapSize = 25;
    
    /** 
     * Whether the bounds of this station are slightly bigger than the station itself.
     * Used together with {@link #borderSideSnapSize} to grab Dockables "out of the sky".
     * The default is <code>true</code>. 
     */
    private boolean allowSideSnap = true;
        
    /** Access to the private and protected methods for some friends of this station */
    private SplitDockAccess access = new SplitDockAccess(){
        public void add( DockableDisplayer displayer ) {
            dockables.add( displayer );
            displayer.setStation( SplitDockStation.this );
            displayer.setController( getController() );
        }

        public DockableDisplayer getFullScreenDockable() {
            return fullScreenDockable;
        }

        public SplitDockStation getOwner() {
            return SplitDockStation.this;
        }

        public double validateDivider( double divider, Node node ) {
            return SplitDockStation.this.validateDivider( divider, node );
        }            
        
        public Leaf createLeaf( Dockable dockable ){
        	dockStationListeners.fireDockableAdding( dockable );
        	Leaf leaf = addToList( dockable );
        	dockStationListeners.fireDockableAdded( dockable );
        	return leaf;
        }
    };
    
    /** The root of the tree which determines the structure of this station */
    private Root root = new Root( access );
    
    /** Information about the {@link Dockable} which is currently draged onto this station. */
    private PutInfo putInfo;
    
    /** A {@link StationPaint} to draw some markings onto this station */
    private StationPaintWrapper paint = new StationPaintWrapper();
    
    /** A {@link DisplayerFactory} used to create {@link DockableDisplayer} for the children of this station */
    private DisplayerFactoryWrapper displayerFactory = new DisplayerFactoryWrapper();
    
    /** The set of displayers currently used by this station */
    private DisplayerCollection displayers;
    
    /**
     * A listener to the mouse. If triggered, the listener moves the dividers
     * between the children around.
     */
    private DividerListener dividerListener;
    
    /** If <code>true</code>, the components are resized while the split is dragged */
    private boolean continousDisplay = false;
    
    /**
     * Constructs a new {@link SplitDockStation}. 
     */
    public SplitDockStation(){
        displayers = new DisplayerCollection( this, displayerFactory );
        
        dividerListener = new DividerListener();
        fullScreenAction = createFullScreenAction();
        visibility = new DockableVisibilityManager( dockStationListeners );
        
        addMouseListener( dividerListener );
        addMouseMotionListener( dividerListener );
    }
    
    @Override
    public Dimension getMinimumSize() {
    	Insets insets = getInsets();
    	Dimension base = getRoot().getMinimumSize();
    	if( insets != null ){
    		base = new Dimension( base.width + insets.left + insets.right, 
    			base.height + insets.top + insets.bottom );
    	}
    	return base;
    }
    
    @Override
    public void doLayout() {
        root.updateBounds();
        
        Insets insets = getRoot().getInsets();
        
        if( fullScreenDockable != null ){
            fullScreenDockable.setBounds( insets.left, insets.top, 
                    getWidth() - insets.left - insets.right,
                    getHeight() - insets.bottom - insets.top );
        }
    }
    
    public DockTheme getTheme() {
    	return theme;
    }
    
    public void updateTheme() {
    	DockController controller = getController();
    	if( controller != null ){
    		DockTheme newTheme = controller.getTheme();
    		if( newTheme != theme ){
    			theme = newTheme;
    			try{
    				callDockUiUpdateTheme();
    			}
    			catch( IOException ex ){
    				throw new RuntimeException( ex );
    			}
    		}
    	}
    }
    
    /**
     * Calls the method {@link DockUI}.{@link DockUI#updateTheme(DockStation, DockFactory)}
     * with <code>this</code> as the first argument, and an appropriate factory
     * as the second argument.
     * @throws IOException if the DockUI throws an exception
     */
    protected void callDockUiUpdateTheme() throws IOException{
    	DockUI.updateTheme( this, new SplitDockStationFactory() );
    }
    
    /**
     * Creates an {@link DockAction action} which is added to all children
     * of this station. The action allows the user to expand a child to
     * fullscreen. The action is also added to subchildren, but the effect
     * does only affect direct children of this station.
     * @return the action or <code>null</code> if this feature should be
     * disabled, or the action is {@link #setFullScreenAction(DockAction) set later}
     */
    protected DockAction createFullScreenAction(){
        return new SplitFullScreenAction( this );
    }
    
    /**
     * Sets an {@link DockAction action} which allows to expand children. This
     * method can only be invoked if there is not already set an action. It is
     * a condition that {@link #createFullScreenAction()} returns <code>null</code>
     * @param fullScreenAction the new action
     * @throws IllegalStateException if there is already an action present
     */
    public void setFullScreenAction( DockAction fullScreenAction ) {
        if( this.fullScreenAction != null )
            throw new IllegalStateException( "The fullScreenAction can only be set once" );
        this.fullScreenAction = fullScreenAction;
    }
    
    /**
     * Sets whether a double click on a child or its title can expand the child 
     * to fullscreen or not.
     * @param expandOnDoubleclick <code>true</code> if the double click should 
     * have an effect, <code>false</code> if double clicks should be ignored.
     */
    public void setExpandOnDoubleclick( boolean expandOnDoubleclick ) {
        if( this.expandOnDoubleclick != expandOnDoubleclick ){
            if( this.expandOnDoubleclick ){
                if( fullScreenListener != null ){
                    fullScreenListener.destroy();
                    controller.removeDockControllerListener( fullScreenListener );
                    fullScreenAction = null;
                }
            }
            
            this.expandOnDoubleclick = expandOnDoubleclick;
            
            if( expandOnDoubleclick ){
                if( controller != null ){
                    fullScreenListener = new FullScreenClickableListener( this );
                    controller.addDockControllerListener( fullScreenListener );
                }
            }
        }
    }
    
    /**
     * Tells whether a child expands to fullscreen when double clicked or not.
     * @return <code>true</code> if a double click has an effect, <code>false</code>
     * otherwise
     * @see #setExpandOnDoubleclick(boolean)
     */
    public boolean isExpandOnDoubleclick() {
        return expandOnDoubleclick;
    }
    
    public void setDockParent( DockStation station ) {
        if( this.parent != null )
            this.parent.removeDockStationListener( visibleListener );
        
        parent = station;
        
        if( station != null )
            station.addDockStationListener( visibleListener );
    }

    public DockStation getDockParent() {
        return parent;
    }

    public void setController( DockController controller ) {
        if( this.controller != controller ){
            if( fullScreenListener != null ){
                fullScreenListener.destroy();
                if( this.controller != null )
                    this.controller.removeDockControllerListener( fullScreenListener );
                fullScreenListener = null;
            }
            
            for( DockableDisplayer displayer : dockables ){
                DockTitle title = displayer.getTitle();
                if( title != null ){
                    displayer.getDockable().unbind( title );
                    displayer.setTitle( null );
                }
            }
            
            this.controller = controller;
            getDisplayers().setController( controller );
            
            if( controller != null ){
                title = controller.getDockTitleManager().registerDefault( TITLE_ID, ControllerTitleFactory.INSTANCE );
                
                if( expandOnDoubleclick ){
                    fullScreenListener = new FullScreenClickableListener( this );
                    controller.addDockControllerListener( fullScreenListener );
                }
            }
            else
                title = null;
            
            for( DockableDisplayer displayer : dockables ){
                DockTitle title = displayer.getTitle();
                
                if( this.title != null ){
                    title = displayer.getDockable().getDockTitle( this.title );
                    displayer.setTitle( title );
                    if( title != null )
                        displayer.getDockable().bind( title );
                }
            }
        }
    }

    public DockController getController() {
        return controller;
    }

    public void addDockableListener( DockableListener listener ) {
        dockableListeners.add( listener );
    }

    public void removeDockableListener( DockableListener listener ) {
        dockableListeners.remove( listener );
    }
    
    public void addMouseInputListener( MouseInputListener listener ) {
        // ignore
    }
    
    public void removeMouseInputListener( MouseInputListener listener ) {
        // ignore
    }

    public boolean accept( DockStation station ) {
        return true;
    }

    public boolean accept( DockStation base, Dockable neighbour ) {
        return true;
    }
    
    public Component getComponent() {
        return this;
    }
    
    public String getTitleText() {
        String text = titleText.getValue();
        if( text == null )
        	return "";
        else
        	return text;
    }

    /**
     * Sets the text of the title of this dockable.
     * @param titleText the text displayed in the title
     */
    public void setTitleText( String titleText ) {
    	this.titleText.setValue( titleText );
    }
    
    public Icon getTitleIcon() {
        return titleIcon.getValue();
    }
    
    /**
     * Sets an icon that is shown in the {@link DockTitle titles} of this {@link Dockable}.
     * @param titleIcon the icon or <code>null</code>
     */
    public void setTitleIcon( Icon titleIcon ) {
    	this.titleIcon.setValue( titleIcon );
    }

    /**
     * Every child has an invisible border whose size is determined by <code>sideSnapSize</code>.
     * If another {@link Dockable} is dragged into that border, it is added as neighbor.
     * Otherwise it is merged with the present child.
     * @param sideSnapSize the relative size of the border, should be between
     * 0 and 0.5f
     * @throws IllegalArgumentException if the size is less than 0
     */
    public void setSideSnapSize( float sideSnapSize ) {
        if( sideSnapSize < 0 )
            throw new IllegalArgumentException( "sideSnapSize must not be less than 0" );
        
        this.sideSnapSize = sideSnapSize;
    }
    
    /**
     * Gets the relative size of the invisible border of all children.
     * @return the size
     * @see #setSideSnapSize(float)
     */
    public float getSideSnapSize() {
        return sideSnapSize;
    }
    
    /**
     * There is an invisible border around the station. If a {@link Dockable} is 
     * dragged inside this border, its considered to be on the station, but
     * will be dropped aside the station (like the whole station is a neighbor
     * of the Dockable).
     * @param borderSideSnapSize the size of the border in pixel
     * @throws IllegalArgumentException if the size is smaller than 0
     */
    public void setBorderSideSnapSize( int borderSideSnapSize ) {
        if( borderSideSnapSize < 0 )
            throw new IllegalArgumentException( "borderSideSnapeSize must not be less than 0" );
        
        this.borderSideSnapSize = borderSideSnapSize;
    }
    
    /**
     * Gets the size of the border around the station.
     * @return the size in pixel
     * @see #setBorderSideSnapSize(int)
     */
    public int getBorderSideSnapSize() {
        return borderSideSnapSize;
    }
    
    /**
     * Sets the size of the divider-gap between the children of this station.
     * @param dividerSize the size of the gap in pixel
     * @throws IllegalArgumentException if the size is less than 0.
     */
    public void setDividerSize( int dividerSize ) {
        if( dividerSize < 0 )
            throw new IllegalArgumentException( "dividerSize must not be less than 0" );
        
        this.dividerSize = dividerSize;
        doLayout();
    }
    
    /**
     * Gets the size of the divider-gap.
     * @return the size
     * @see #setDividerSize(int)
     */
    public int getDividerSize() {
        return dividerSize;
    }
    
    /**
     * Sets whether the dockables should be resized while the split
     * is dragged, or not.
     * @param continousDisplay <code>true</code> if the dockables should
     * be resized
     */
    public void setContinousDisplay( boolean continousDisplay ) {
        this.continousDisplay = continousDisplay;
    }
    
    /**
     * Tells whether the dockables are resized while the split is
     * dragged, or not.
     * @return <code>true</code> if the dockables are resized
     * @see #setContinousDisplay(boolean)
     */
    public boolean isContinousDisplay() {
        return continousDisplay;
    }
    
    /**
     * Sets whether {@link Dockable Dockables} which are dragged near
     * the station are captured and added to this station.
     * @param allowSideSnap <code>true</code> if the station can
     * snap Dockables which are near.
     * @see #setBorderSideSnapSize(int)
     */
    public void setAllowSideSnap( boolean allowSideSnap ) {
        this.allowSideSnap = allowSideSnap;
    }
    
    /**
     * Tells whether the station can grab Dockables which are dragged
     * near the station.
     * @return <code>true</code> if grabbing is allowed
     * @see #setAllowSideSnap(boolean)
     */
    public boolean isAllowSideSnap() {
        return allowSideSnap;
    }
    
    public DockTitle getDockTitle( DockTitleVersion version ) {
        return version.createStation( this );
    }
    
    public void changed( Dockable dockable, DockTitle title, boolean active ) {
        title.changed( new DockTitleEvent( this, dockable, active ));
    }

    public void bind( DockTitle title ) {
        for( DockableListener listener : dockableListeners.toArray( new DockableListener[ dockableListeners.size() ] ))
            listener.titleBinded( this, title );
    }

    public void unbind( DockTitle title ) {
        for( DockableListener listener : dockableListeners.toArray( new DockableListener[ dockableListeners.size() ] ))
            listener.titleUnbinded( this, title );
    }

    public DockActionSource getActionOffers() {
        return null;
    }

    public DockStation asDockStation() {
        return this;
    }

    public DefaultDockActionSource getDirectActionOffers( Dockable dockable ) {
    	if( fullScreenAction == null )
    		return null;
    	else{
	        DefaultDockActionSource source = new DefaultDockActionSource(new LocationHint( LocationHint.DIRECT_ACTION, LocationHint.VERY_RIGHT ));
	        source.add( fullScreenAction );
	        
	        return source;
    	}
    }
    
    public DockActionSource getIndirectActionOffers( Dockable dockable ) {
        if( fullScreenAction == null )
            return null;
        
        DockStation parent = dockable.getDockParent();
        if( parent == null )
            return null;
        
        if( parent instanceof SplitDockStation )
            return null;
        
        dockable = parent.asDockable();
        if( dockable == null )
            return null;
        
        parent = dockable.getDockParent();
        if( parent != this )
            return null;
        
        DefaultDockActionSource source = new DefaultDockActionSource( fullScreenAction );
        source.setHint( new LocationHint( LocationHint.INDIRECT_ACTION, LocationHint.VERY_RIGHT ));
        return source;
    }

    public void addDockStationListener( DockStationListener listener ) {
        dockStationListeners.addListener( listener );
    }

    public void removeDockStationListener( DockStationListener listener ) {
        dockStationListeners.removeListener( listener );
    }

    /**
     * Adds a listener to this station. The listener is informed some 
     * settings only available to a {@link SplitDockStation} are changed.
     * @param listener the new listener
     */
    public void addSplitDockStationListener( SplitDockListener listener ){
        splitListeners.add( listener );
    }
    
    /**
     * Removes an earlier added listener.
     * @param listener The listener to remove
     */
    public void removeSplitDockStationListener( SplitDockListener listener ){
        splitListeners.remove( listener );
    }
    
    public boolean isVisible( Dockable dockable ) {
        return isStationVisible() && (!isFullScreen() || dockable == getFullScreen());
    }

    public boolean isStationVisible() {
        if( parent != null )
            return parent.isStationVisible() && parent.isVisible( this );
        else
            return isDisplayable();
    }
    
    public int getDockableCount() {
        return dockables.size();
    }

    public Dockable getDockable( int index ) {
        return dockables.get( index ).getDockable();
    }
    
    public DockableProperty getDockableProperty( Dockable dockable ) {
        Leaf leaf = getRoot().getLeaf( dockable );
        return new SplitDockProperty( leaf.getX(), leaf.getY(),
                leaf.getWidth(), leaf.getHeight() );
    }
    
    public Dockable getFrontDockable() {
        if( isFullScreen() )
            return getFullScreen();
        
        if( frontDockable == null && dockables.size() > 0 )
            frontDockable = dockables.get( 0 ).getDockable();
        
        return frontDockable;
    }
    
    public void setFrontDockable( Dockable dockable ) {
        this.frontDockable = dockable;
        if( isFullScreen() && dockable != null )
            setFullScreen( dockable );
    }

    /**
     * Tells whether a {@link Dockable} is currently shown in fullscreen-mode
     * on this station. A <code>true</code> result implies that
     * {@link #getFullScreen()} returns not <code>null</code>.
     * @return <code>true</code> if a child is fullscreen. 
     */
    public boolean isFullScreen(){
        return fullScreenDockable != null;
    }

    /**
     * Gets the {@link Dockable} which is in fullscreen-mode and covers all
     * other children of this station.
     * @return the child or <code>null</code>
     * @see #setFullScreen(Dockable)
     * @see #isFullScreen()
     */
    public Dockable getFullScreen(){
        return fullScreenDockable == null ? null : fullScreenDockable.getDockable();
    }

    /**
     * Sets one of the children of this station as the one child which covers
     * all other children. This child is in "fullscreen"-mode.
     * @param dockable a child of this station or <code>null</code> if
     * all children should be visible.
     * @see #isFullScreen()
     */
    public void setFullScreen( Dockable dockable ){
        Dockable oldFullScreen = getFullScreen();
        
        if( dockable != null ){
            Leaf leaf = getRoot().getLeaf( dockable );
            if( leaf == null )
                throw new IllegalArgumentException( "Dockable not child of this station" );
            
            fullScreenDockable = leaf.getDisplayer();
           
            for( DockableDisplayer displayer : dockables ){
                displayer.setVisible( displayer == fullScreenDockable );
            }
        }
        else{
            fullScreenDockable = null;
            for( DockableDisplayer displayer : dockables ){
                displayer.setVisible( true );
            }
        }
        
        doLayout();
        fireFullScreenChanged( oldFullScreen, getFullScreen() );
        visibility.fire();
    }
    
    /**
     * Switches the child which is in fullscreen-mode. If there is no child,
     * nothing will happen. If there is only one child, it will be set to
     * fullscreen (if it is not already fullscreen).
     */
    public void setNextFullScreen(){
        if( dockables.size() > 0 ){
            if( fullScreenDockable == null )
                setFullScreen( getDockable(0) );
            else{
                int index = indexOfDockable( fullScreenDockable.getDockable() );
                index++;
                index %= getDockableCount();
                setFullScreen( getDockable( index ) );
            }
        }
    }
    
    public DockTitle[] getDockTitles( Dockable dockable ) {
        int index = indexOfDockable( dockable );
        
        if( index < 0 )
            return new DockTitle[0];
        else{
            DockTitle title = dockables.get( index ).getTitle();
            if( title == null )
                return new DockTitle[0];
            else
                return new DockTitle[]{ title };
        }
    }

    public boolean accept( Dockable child ) {
        return true;
    }

    public boolean prepareDrop( int x, int y, int titleX, int titleY, Dockable dockable ){
        if( isFullScreen() )
            return false;
        
        if( dockables.size() == 0 ){
            if( parent != null ){
                if( parent.isInOverrideZone( x, y, this, dockable ))
                    return false;
            }
            
            putInfo = new PutInfo( null, PutInfo.Put.CENTER );
            putInfo.setDockable( dockable );
            return true;
        }
        else{
            Point point = new Point( x, y );
            SwingUtilities.convertPointFromScreen( point, this );
            
            putInfo = root.getPut( point.x, point.y, dockable );
            
            if( putInfo == null && allowSideSnap ){
                putInfo = calculateSideSnap( point.x, point.y, null );
            }
            
            if( putInfo != null ){
                putInfo.setDockable( dockable );
                calculateDivider( putInfo, null );
            }
            
            if( parent != null && putInfo != null ){
                if( parent.isInOverrideZone( x, y, this, dockable )){
                    if( putInfo.getPut() == PutInfo.Put.CENTER ){
                        putInfo = null;
                        return false;
                    }
                    
                    return true;
                }
            }
            
            return putInfo != null;
        }
    }
    
    public void drop( Dockable dockable ) {
        dockStationListeners.fireDockableAdding( dockable );
        addDockable( dockable, false );
        dockStationListeners.fireDockableAdded( dockable );
    }

    public boolean drop( Dockable dockable, DockableProperty property ) {
        if( property instanceof SplitDockProperty ){
            return drop( dockable, (SplitDockProperty)property );
        }
        else
            return false;
    }
    
    /**
     * Tries to add <code>Dockable</code> such that the boundaries given
     * by <code>property</code> are full filled.
     * @param dockable a new child of this station
     * @param property the preferred location of the child
     * @return <code>true</code> if the child could be added, <code>false</code>
     * if no location could be found
     */
    public boolean drop( Dockable dockable, final SplitDockProperty property ){
        if( getDockableCount() == 0 ){
            drop( dockable );
            return true;
        }
        
        root.updateBounds();
        
        class DropInfo{
            public Leaf bestLeaf;
            public double bestLeafIntersection;
            
            public SplitNode bestNode;
            public double bestNodeIntersection = Double.POSITIVE_INFINITY;
            public PutInfo.Put bestNodePut;
        }
        
        final DropInfo info = new DropInfo();
        
        getRoot().visit( new SplitNodeVisitor(){
            public void handleLeaf( Leaf leaf ) {
                double intersection = leaf.intersection( property );
                if( intersection > info.bestLeafIntersection ){
                    info.bestLeafIntersection = intersection;
                    info.bestLeaf = leaf;
                }
                
                handleNeighbour( leaf );
            }
            
            public void handleNode( Node node ) {
                handleNeighbour( node );
            }
            
            public void handleRoot( Root root ) {
                // do nothing
            }
            
            private void handleNeighbour( SplitNode node ){
                double x = node.getX();
                double y = node.getY();
                double width = node.getWidth();
                double height = node.getHeight();
                
                double left   = Math.abs( x - property.getX() );
                double right  = Math.abs( x + width - property.getX() - property.getWidth() );
                double top    = Math.abs( y - property.getY() );
                double bottom = Math.abs( y + height - property.getY() - property.getHeight() );
                
                double value = left + right + top + bottom;
                value -= Math.max( Math.max( left, right ), Math.max( top, bottom ));
                
                double kx = property.getX() + property.getWidth()/2;
                double ky = property.getY() + property.getHeight()/2;
                
                PutInfo.Put put = node.relativeSidePut( kx, ky );
                
                double px, py;
                
                if( put == PutInfo.Put.TOP ){
                    px = x + 0.5*width;
                    py = y + 0.25*height;
                }
                else if( put == PutInfo.Put.BOTTOM ){
                    px = x + 0.5*width;
                    py = y + 0.75*height;
                }
                else if( put == PutInfo.Put.LEFT ){
                    px = x + 0.25*width;
                    py = y + 0.5*height;
                }
                else{
                    px = x + 0.5*width;
                    py = y + 0.75*height;
                }
                
                double distance = Math.pow( (kx-px)*(kx-px) + (ky-py)*(ky-py), 0.25 );
                
                value *= distance;
                
                if( value < info.bestNodeIntersection ){
                    info.bestNodeIntersection = value;
                    info.bestNode = node;
                    info.bestNodePut = put;
                }
            }
        });
        
        if( info.bestLeaf != null ){
            DockStation station = info.bestLeaf.getDockable().asDockStation();
            DockableProperty successor = property.getSuccessor();
            if( station != null && successor != null ){
                if( station.drop( dockable, successor )){
                    validate();
                    return true;
                }
            }
            
            if( info.bestLeafIntersection > 0.75 ){
                if( station != null && station.accept( dockable ) && dockable.accept( station )){
                    station.drop( dockable );
                    validate();
                    return true;
                }
                else{
                    boolean result = dropOver( info.bestLeaf, dockable );
                    validate();
                    return result;
                }
            }
        }
        
        if( info.bestNode != null ){
            if( !accept( dockable ) || !dockable.accept( this ))
                return false;
            
            double divider = 0.5;
            double min = Math.min( property.getWidth(), property.getHeight() );
            if( info.bestNodePut == PutInfo.Put.LEFT ){
                divider = min / info.bestNode.getWidth();
            }
            else if( info.bestNodePut == PutInfo.Put.RIGHT ){
                divider = 1 - min / info.bestNode.getWidth();
            }
            else if( info.bestNodePut == PutInfo.Put.TOP ){
                divider = min / info.bestNode.getHeight();
            }
            else if( info.bestNodePut == PutInfo.Put.BOTTOM ){
                divider = 1 - min / info.bestNode.getHeight();
            }
            
            divider = Math.max( 0, Math.min( 1, divider ));
            dropAside( info.bestNode, info.bestNodePut, dockable, divider, true );
            validate();
            return true;
        }
        
        repaint();
        return false;
    }
    
    public void drop(){
        drop( true );
    }
    
    /**
     * Drops the current {@link #putInfo}.
     * @param fire <code>true</code> if events should be fired,
     * <code>false</code> otherwise
     * @see #drop(PutInfo, boolean)
     */
    private void drop( boolean fire ) {
        drop( putInfo, fire );
    }
    
    /**
     * Adds the {@link Dockable} given by <code>putInfo</code> to this
     * station.
     * @param putInfo the location of the new child
     * @param fire <code>true</code> if events should be fired,
     * <code>false</code> if the process should be silent
     */
    private void drop( PutInfo putInfo, boolean fire ){
        if( putInfo.getNode() == null ){
            if( fire )
            	dockStationListeners.fireDockableAdding( putInfo.getDockable() );
            addDockable( putInfo.getDockable(), false );
            if( fire ){
            	dockStationListeners.fireDockableAdded( putInfo.getDockable() );
            }
        }
        else{
            boolean finish = false;
            
            if( putInfo.getPut() == PutInfo.Put.CENTER || putInfo.getPut() == PutInfo.Put.TITLE ){
                if( putInfo.getNode() instanceof Leaf &&
                        dropOver( (Leaf)putInfo.getNode(), putInfo.getDockable() ) )
                    finish = true;
                else
                    putInfo.setPut( PutInfo.Put.TOP );
            }
            
            if( !finish ){
                root.updateBounds();
                calculateDivider( putInfo, root.getLeaf( putInfo.getDockable() ));
                dropAside( putInfo.getNode(), putInfo.getPut(), putInfo.getDockable(), putInfo.getDivider(), fire );
            }
        }
        
        validate();
    }

    /**
     * Combines the {@link Dockable} of <code>leaf</code> and <code>dockable</code>
     * to a new child of this station. No checks whether the two elements accepts
     * each other nor if the station accepts the new child <code>dockable</code>
     * are performed.
     * @param leaf the leaf which will be combined with <code>dockable</code>
     * @param dockable a {@link Dockable} which is dropped over <code>leaf</code>
     * @return <code>true</code> if the operation was successful, <code>false</code>
     * otherwise
     */
    protected boolean dropOver( Leaf leaf, Dockable dockable ){
        Dockable old = leaf.getDockable();
        dockStationListeners.fireDockableRemoving( old );
        removeFromList( old );
        dockStationListeners.fireDockableRemoved( old );
        
        Dockable combination = DockUI.getCombiner( combiner, this ).combine( old, dockable, this );
        
        dockStationListeners.fireDockableAdding( combination );
        Leaf next = addToList( combination );
        
        SplitNode parent = leaf.getParent();
        if( parent == root )
            root.setChild( next );
        else{
            Node parentNode = (Node)parent;
            if( parentNode.getLeft() == leaf )
                parentNode.setLeft( next );
            else
                parentNode.setRight( next );
        }
        
        dockStationListeners.fireDockableAdded( combination );
        invalidate();
        return true;
    }
    
    /**
     * Adds <code>dockable</code> at the side <code>put</code> of 
     * <code>neighbor</code>. The divider is set to the value of <code>divider</code>,
     * and if <code>fire</code> is activated, some events are fired. There are
     * no checks whether <code>dockable</code> accepts this station or anything
     * else.
     * @param neighbor The node which will be the neighbor of <code>dockable</code>
     * @param put The side on which <code>dockable</code> should be added in 
     * respect to <code>neighbor</code>.
     * @param dockable the new child of this station
     * @param divider the divider-location, a value between 0 and 1
     * @param fire <code>true</code> if the method is allowed to fire events,
     * <code>false</code> otherwise
     */
    protected void dropAside( SplitNode neighbor, PutInfo.Put put, Dockable dockable, double divider, boolean fire ){
        if( fire )
        	dockStationListeners.fireDockableAdding( dockable );
        
        Leaf leaf = addToList( dockable );
        SplitNode parent = neighbor.getParent();
        
        // Node herstellen
        Node node = null;
        root.updateBounds();
        
        if( put == PutInfo.Put.TOP ){
            node = new Node( access, leaf, neighbor, Orientation.VERTICAL );
        }
        else if( put == PutInfo.Put.BOTTOM ){
            node = new Node( access, neighbor, leaf, Orientation.VERTICAL );
        }
        else if( put == PutInfo.Put.LEFT ){
            node = new Node( access, leaf, neighbor, Orientation.HORIZONTAL );
        }
        else{
            node = new Node( access, neighbor, leaf, Orientation.HORIZONTAL );
        }
        
        node.setDivider( divider );
        
        if( parent == root )
            root.setChild( node );
        else{
            Node n = (Node)parent;
            if( n.getLeft() == neighbor )
                n.setLeft( node );
            else
                n.setRight( node );
        }
        
        if( fire ){
        	dockStationListeners.fireDockableAdded( dockable );
        }
        invalidate();
    }
    
    public boolean prepareMove( int x, int y, int titleX, int titleY, Dockable dockable ) {
        if( isFullScreen() )
            return false;
        
        Point point = new Point( x, y );
        SwingUtilities.convertPointFromScreen( point, this );
        
        putInfo = root.getPut( point.x, point.y, dockable );
        Leaf leaf = root.getLeaf( dockable );
        
        if( putInfo == null && allowSideSnap ){
            putInfo = calculateSideSnap( point.x, point.y, leaf );
        }
        
        if( (putInfo != null) &&
            (putInfo.getNode() instanceof Leaf) &&
            (((Leaf)putInfo.getNode())).getDockable() == dockable){
                putInfo = null;
        }
        
        if( putInfo != null ){
            putInfo.setDockable( dockable );
            calculateDivider( putInfo, leaf );
        }
        
        if( parent != null && putInfo != null ){
            if( parent.isInOverrideZone( x, y, this, dockable )){
                if( putInfo.getPut() == PutInfo.Put.CENTER ){
                    putInfo = null;
                    return false;
                }
                return true;
            }
        }
        
        
        return putInfo != null;
    }

    public void move() {
        Leaf leaf = root.getLeaf( putInfo.getDockable() );
        if( leaf.getParent() == putInfo.getNode() ){
            if( putInfo.getNode() == root ){
                // no movement possible
                return;
            }
            else{
                Node node = (Node)putInfo.getNode();
                if( node.getLeft() == leaf )
                    putInfo.setNode( node.getRight() );
                else
                    putInfo.setNode( node.getLeft() );
            }
        }
        
	    
        removeDockable( indexOfDockable( putInfo.getDockable() ), false );
        putInfo.getDockable().setDockParent( null );
        drop( false );
    }

    /**
     * Calculates the value a divider should have if the {@link Dockable}
     * of <code>putInfo</code> is added alongside of <code>origin</code>. 
     * @param putInfo the new child of the station
     * @param origin a leaf of this station or <code>null</code>
     */
    protected void calculateDivider( PutInfo putInfo, Leaf origin ){
        SplitNode other = putInfo.getNode();
        
        Dimension oldSize = origin == null ? 
                putInfo.getDockable().getComponent().getSize() :
                origin.getSize();
                
        Dimension nodeSize = other.getSize();
        
        // create new node
        int size = Math.min( oldSize.width, oldSize.height );

        if( origin != null ){
            if( origin.getParent() instanceof Node ){
                Node originParent = (Node)origin.getParent();
                
                if( (putInfo.getPut() == PutInfo.Put.LEFT || putInfo.getPut() == PutInfo.Put.RIGHT) && 
                        originParent.getOrientation() == Orientation.HORIZONTAL ){
                    size = oldSize.width;
                }
                else if( (putInfo.getPut() == PutInfo.Put.TOP || putInfo.getPut() == PutInfo.Put.BOTTOM) && 
                        originParent.getOrientation() == Orientation.VERTICAL){
                    size = oldSize.height;
                }
            }
        }
        else{
            if( putInfo.getOldSize() != 0 ){
                size = putInfo.getOldSize();
            }
        }
        
        double divider = 0.5;

        if( putInfo.getPut() == PutInfo.Put.TOP ){
            if( size != 0 )
                divider = (size + dividerSize/2.0) / nodeSize.height;
            
            divider = validateDivider( divider, 
                    putInfo.getDockable().getComponent().getMinimumSize(),
                    other.getMinimumSize(), 
                    Orientation.VERTICAL, 
                    other.getWidth(), other.getHeight() );
        }
        else if( putInfo.getPut() == PutInfo.Put.BOTTOM ){
            if( size != 0 )
                divider = 1.0 - (size + dividerSize/2.0) / nodeSize.height;
            
            divider = validateDivider( divider, 
            		other.getMinimumSize(),
            		putInfo.getDockable().getComponent().getMinimumSize(),
                    Orientation.VERTICAL, 
                    other.getWidth(), other.getHeight() );
        }
        else if( putInfo.getPut() == PutInfo.Put.LEFT ){
            if( size != 0 )
                divider = (size + dividerSize/2.0) / nodeSize.width;
            
            divider = validateDivider( divider, 
            		putInfo.getDockable().getComponent().getMinimumSize(),
            		other.getMinimumSize(), 
                    Orientation.HORIZONTAL, 
                    other.getWidth(), other.getHeight() );
        }
        else if( putInfo.getPut() == PutInfo.Put.RIGHT ){
            if( size != 0 )
                divider = 1.0 - (size + dividerSize/2.0) / nodeSize.width;
            
            divider = validateDivider( divider, 
            		other.getMinimumSize(), 
            		putInfo.getDockable().getComponent().getMinimumSize(), 
                    Orientation.HORIZONTAL, 
                    other.getWidth(), other.getHeight() );
        }

        putInfo.setDivider( divider );
        putInfo.setOldSize( size );
    }
    
    /**
     * Calculates where to add a {@link Dockable} if the mouse is outside
     * this station.
     * @param x The x-coordinate of the mouse
     * @param y The y-coordinate of the mouse
     * @param leaf The leaf which was the old parent of the moved {@link Dockable} 
     * or <code>null</code>
     * @return The preferred location or <code>null</code>
     */
    protected PutInfo calculateSideSnap( int x, int y, Leaf leaf ){
        PutInfo info;
        
        if( SplitNode.above( 0, 0, getWidth(), getHeight(), x, y )){
            if( SplitNode.above( 0, getHeight(), getWidth(), 0, x, y )){
                // top
                info = new PutInfo( root.getChild(), PutInfo.Put.TOP );
            }
            else{
                // bottom
                info = new PutInfo( root.getChild(), PutInfo.Put.RIGHT );
            }
        }
        else{
            if( SplitNode.above( 0, getHeight(), getWidth(), 0, x, y )){
                // left
                info = new PutInfo( root.getChild(), PutInfo.Put.LEFT );
            }
            else{
                // right
                info = new PutInfo( root.getChild(), PutInfo.Put.BOTTOM );
            }            
        }
        
        if( leaf != null && root.getChild() instanceof Node){
            Node node = (Node)root.getChild();
            if( info.getPut() == PutInfo.Put.TOP && node.getOrientation() == Orientation.VERTICAL && node.getLeft() == leaf )
                return null;
            
            if( info.getPut() == PutInfo.Put.BOTTOM && node.getOrientation() == Orientation.VERTICAL && node.getLeft() == leaf )
                return null;
            
            if( info.getPut() == PutInfo.Put.LEFT && node.getOrientation() == Orientation.HORIZONTAL && node.getLeft() == leaf )
                return null;
            
            if( info.getPut() == PutInfo.Put.RIGHT && node.getOrientation() == Orientation.HORIZONTAL && node.getLeft() == leaf )
                return null;
        }
        
        return info;
    }
    
    /**
     * Removes all children from this station and then adds the contents
     * that are stored in <code>tree</code>.
     * @param tree the new set of children
     * @throws SplitDropTreeException If the tree is not acceptable.
     */
    public void dropTree( SplitDockTree tree ){
    	if( tree == null )
    		throw new IllegalArgumentException( "Tree must not be null" );
    	
    	setFullScreen( null );
    	removeAllDockables();
    	
    	Key rootKey = tree.getRoot();
    	if( rootKey != null ){
    		root.evolve( rootKey );
    		root.updateBounds();
    	}
    }
    
    /**
     * Gets the contents of this station as a {@link SplitDockTree}.
     * @return the tree
     */
    public SplitDockTree createTree(){
    	SplitDockTree tree = new SplitDockTree();
    	root.submit( tree );
    	return tree;
    }
    
    public void draw(){
        putInfo.setDraw( true );
        repaint();
    }

    public void forget() {
        putInfo = null;
        repaint();
    }

    public <D extends Dockable & DockStation> boolean isInOverrideZone( int x,
            int y, D invoker, Dockable drop ) {
        
        if( isFullScreen() )
            return false;
        
        if( getDockParent() != null && getDockParent().isInOverrideZone( x, y, invoker, drop ))
            return true;
        
        Point point = new Point( x, y );
        SwingUtilities.convertPointFromScreen( point, this );
        
        PutInfo info = root.getPut( point.x, point.y, drop );
        if( info == null )
            return false;
        
        return info.getPut() != PutInfo.Put.CENTER;
    }

    public boolean canDrag( Dockable dockable ) {
        return true;
    }

    public void drag( Dockable dockable ) {
        int index = indexOfDockable( dockable );
        if( index < 0 )
            throw new IllegalArgumentException( "This dockable cannot be dragged, its not child of the station." );
        
        dockStationListeners.fireDockableRemoving( dockable );
        removeDockable( index, false );
        dockStationListeners.fireDockableRemoved( dockable );
    }
    
    /**
     * Sends a message to all registered instances of {@link SplitDockListener},
     * that the {@link Dockable} in fullscreen-mode has changed.
     * @param oldDockable the old fullscreen-Dockable, can be <code>null</code>
     * @param newDockable the new fullscreen-Dockable, can be <code>null</code>
     */
    protected void fireFullScreenChanged( Dockable oldDockable, Dockable newDockable ){
        for( SplitDockListener listener : splitListeners.toArray( new SplitDockListener[ splitListeners.size() ] ))
            listener.fullScreenDockableChanged( this, oldDockable, newDockable );
    }
    
    public Rectangle getStationBounds() {
        Point location = new Point(0, 0);
        SwingUtilities.convertPointToScreen( location, this );
        if( allowSideSnap )
            return new Rectangle( location.x - borderSideSnapSize, location.y - borderSideSnapSize,
                    getWidth() + 2*borderSideSnapSize, getHeight() + 2*borderSideSnapSize );
        else
            return new Rectangle( location.x, location.y, getWidth(), getHeight() );
    }

    public boolean canCompare( DockStation station ) {
        return false;
    }

    public int compare( DockStation station ) {
        return 0;
    }

    public Dockable asDockable(){
        return this;
    }
    
    /**
     * Gets a {@link StationPaint} to paint markings on this station.
     * @return the paint
     */
    public StationPaintWrapper getPaint() {
        return paint;
    }
    
    /**
     * Gets a {@link DisplayerFactory} to create new {@link DockableDisplayer}
     * for this station.
     * @return the factory
     */
    public DisplayerFactoryWrapper getDisplayerFactory() {
        return displayerFactory;
    }
    
    /**
     * Gets the set of {@link DockableDisplayer displayers} that are currently
     * used by this station.
     * @return the set of displayers
     */
    public DisplayerCollection getDisplayers() {
        return displayers;
    }
    
    /**
     * Gets a {@link Combiner} to combine {@link Dockable Dockables} on
     * this station.
     * @return the combiner
     */
    public CombinerWrapper getCombiner() {
        return combiner;
    }
    
    @Override
    public void paint( Graphics g ) {
        super.paint(g);
        
        if( putInfo != null && putInfo.isDraw() ){
            StationPaint paint = getPaint();
            if( putInfo.getNode() == null ){
                Rectangle bounds = new Rectangle( 0, 0, getWidth(), getHeight() );
                paint.drawInsertion( g, this, bounds, bounds );
            }
            else{
                Rectangle bounds = putInfo.getNode().getBounds();
            
                if( putInfo.getPut() == PutInfo.Put.LEFT ){
                    bounds.width = (int)(bounds.width * putInfo.getDivider() + 0.5);
                }
                else if( putInfo.getPut() == PutInfo.Put.RIGHT ){
                    int width = bounds.width;
                    bounds.width = (int)(bounds.width * (1-putInfo.getDivider()) + 0.5);
                    bounds.x += width - bounds.width;
                }
                else if( putInfo.getPut() == PutInfo.Put.TOP ){
                    bounds.height = (int)(bounds.height * putInfo.getDivider() + 0.5);
                }
                else if( putInfo.getPut() == PutInfo.Put.BOTTOM ){
                    int height = bounds.height;
                    bounds.height = (int)(bounds.height * (1-putInfo.getDivider()) + 0.5);
                    bounds.y += height - bounds.height;
                }
                
                paint.drawInsertion( g, this, 
                        putInfo.getNode().getBounds(),
                        bounds );
            }
        }
        
        dividerListener.paint( g );
    }
    
    /**
     * Adds <code>dockable</code> to this station.
     * @param dockable A {@link Dockable} which must not be a child
     * of this station.
     */
    public void addDockable( Dockable dockable ){
        addDockable( dockable, true );
    }
    
    /**
     * Adds <code>dockable</code> to this station and fires events
     * only if <code>fire</code> is <code>true</code>.
     * @param dockable the new child of this station
     * @param fire <code>true</code> if the method should fire
     * some events.
     */
    private void addDockable( Dockable dockable, boolean fire ){
        if( fire )
        	dockStationListeners.fireDockableAdding( dockable );
        Leaf leaf = addToList( dockable );
        
        if( root.getChild() == null ){
            root.setChild( leaf );
        }
        else{
            SplitNode child = root.getChild();
            root.setChild( null );
            Node node = new Node( access, leaf, child );
            root.setChild( node );
        }
        
        if( fire ){
        	dockStationListeners.fireDockableAdded( dockable );
        }
        revalidate();
    }
    
    public boolean canReplace( Dockable old, Dockable next ) {
        return true;
    }
    
    public void replace( Dockable previous, Dockable next ){
        Leaf leaf = root.getLeaf( previous );
        if( leaf == null )
            throw new IllegalArgumentException( "Previous is not child of this station" );
        
        if( next.getDockParent() != null )
            throw new IllegalArgumentException( "Next must not be a child of another station" );
        
        boolean wasFullScreen = isFullScreen() && getFullScreen() == previous;
        
        // remove
        dockStationListeners.fireDockableRemoving( previous );
        removeFromList( previous );
        dockStationListeners.fireDockableRemoved( previous );
        
        // add
        dockStationListeners.fireDockableAdding( next );
        SplitNode parent = leaf.getParent();
        if( parent == root )
            root.setChild( addToList( next ));
        else{
            Node node = (Node)parent;
            if( node.getLeft() == leaf )
                node.setLeft( addToList( next ));
            else
                node.setRight( addToList( next ));
        }
        
        if( wasFullScreen )
            setFullScreen( next );
        
        dockStationListeners.fireDockableAdded( next );
        revalidate();
        repaint();
    }
    
    /**
     * Adds <code>dockable</code> to the list of children, generates a 
     * title and a {@link Leaf} for <code>dockable</code>, but does not
     * add the leaf to this station. The location of the leaf has be set by
     * the caller. 
     * @param dockable the new child of this station.
     * @return the {@link Leaf} for <code>dockable</code>
     */
    protected Leaf addToList( Dockable dockable ){
        if( dockable.getDockParent() != null && dockable.getDockParent() != this )
            throw new IllegalArgumentException( "Dockable must not have another parent" );
        
        DockTitle title = null;
        dockable.setDockParent( this );
        
        if( this.title != null ){
            title = dockable.getDockTitle( this.title );
            if( title != null )
                dockable.bind( title );
        }
        
        DockableDisplayer displayer = getDisplayers().fetch( dockable, title );
        add( displayer );
        displayer.setVisible( !isFullScreen() );
        dockables.add( displayer );
        
        return new Leaf( access, displayer );
    }
    
    /**
     * Gets the index of a child of this station.
     * @param dockable the child which is searched
     * @return the index or -1 if the child was not found
     */
    public int indexOfDockable( Dockable dockable ){
        for( int i = 0, n = dockables.size(); i<n; i++ )
            if( dockables.get( i ).getDockable() == dockable )
                return i;
        
        return -1;
    }
    
    /**
     * Removes all children from this station.
     */
    public void removeAllDockables(){
        for( int i = getDockableCount()-1; i >= 0; i-- )
            removeDockable( i );
    }
    
    /**
     * Removes <code>dockable</code> from this station. If 
     * <code>dockable</code> is not a child of this station, nothing happens.
     * @param dockable the child to remove
     */
    public void removeDockable( Dockable dockable ){
        int index = indexOfDockable( dockable );
        if( index >= 0 )
            removeDockable( index );
    }
    
    /**
     * Removes the child with the specified <code>index</code> from this 
     * station.
     * @param index the index of the child to remove
     */
    private void removeDockable( int index ){
        removeDockable( index, true );
    }
    
    /**
     * Removes the child with the specified <code>index</code> from this
     * station. Fires events if <code>fire</code> is <code>true</code>.
     * @param index the index of the child to remove
     * @param fire if <code>true</code>, the method will fire some events.
     * Otherwise the method will run silent.
     */
    private void removeDockable( int index, boolean fire ){
        Dockable dockable = getDockable( index );
        Leaf leaf = root.getLeaf( dockable );
        
        if( fire )
        	dockStationListeners.fireDockableRemoving( dockable );
        
        removeFromList( index );
        
        if( leaf != null ){
            removeLeaf( leaf );
        }
        if( fire )
        	dockStationListeners.fireDockableRemoved( dockable );
        
        revalidate();
        repaint();
    }
    
    /**
     * Removes <code>dockable</code> from the list of children of this station,
     * but does nothing more.
     * @param dockable the child to remove
     */
    protected void removeFromList( Dockable dockable ){
        int index = indexOfDockable( dockable );
        if( index >= 0 ){
        	dockStationListeners.fireDockableRemoving( dockable );
            removeFromList( index );
            dockStationListeners.fireDockableRemoved( dockable );
        }
    }
    
    /**
     * Removes the child at <code>index</code> from this station, does
     * not fire any events.
     * @param index the index of the child to remove
     */
    private void removeFromList( int index ){
        DockableDisplayer display = dockables.remove( index );
        Dockable dockable = display.getDockable();
        DockTitle title = display.getTitle();
        
        display.setVisible( true );
        remove( display );
        getDisplayers().release( display );
        
        if( display == fullScreenDockable ){
            setNextFullScreen();
            
            if( display == fullScreenDockable )
                setFullScreen( null );
        }
        
        if( title != null ){
            display.getDockable().unbind( title );
            display.setTitle( null );
        }
        
        if( display.getDockable() == frontDockable )
            setFrontDockable( null );
        
        dockable.setDockParent( null );
        //dockable.removeMouseInputListener( dockableFullScreenListeners.remove( dockable ));
    }
    
    /**
     * Removes the specified <code>leaf</code> from the tree of this station,
     * but does not remove the associated {@link DockableDisplayer}.
     * @param leaf the {@link Leaf} to remove
     */
    protected void removeLeaf( Leaf leaf ){
        SplitNode parent = leaf.getParent();
        if( parent == root ){
            root.setChild( null );
        }
        else{
            Node node = (Node)parent;
            SplitNode other = node.getLeft() == leaf ? node.getRight() : node.getLeft();
            
            parent = node.getParent();
            if( parent == root ){
                root.setChild( other );
            }
            else{
                Node next = (Node)parent;
                if( next.getLeft() == node )
                    next.setLeft( other );
                else
                    next.setRight( other );
            }
        }
    }
    
    /**
     * Tests whether the specified <code>divider</code>-value is legal or not.
     * @param divider the value of a divider on a {@link Node}
     * @param node the <code>Node</code> for which the test is performed
     * @return a legal value as near as possible to <code>divider</code>
     */
    private double validateDivider( double divider, Node node ){
        divider = Math.min( 1, Math.max( 0, divider ));
        
        return validateDivider( divider, node.getLeft().getMinimumSize(), 
        		node.getRight().getMinimumSize(), node.getOrientation(), node.getWidth(), node.getHeight() );
    }
        
    /**
     * Tests whether the specified <code>divider</code>-value is legal or not.
     * @param divider the value of a divider on a {@link Node}
     * @param minimumLeft the minimal number of pixels on the left or top
     * side of the divider
     * @param minimumRight the minimal number of pixels on the right or bottom
     * side of the divider
     * @param orientation the orientation of the divider
     * @param width the relative width of the base (in respect to the size of
     * this station)
     * @param height the relative height of the base (in respect to the size of
     * this station)
     * @return a legal value as near as possible to <code>divider</code>
     */
    protected double validateDivider( double divider,
    		Dimension minimumLeft, Dimension minimumRight,
            Orientation orientation, double width, double height ){
        
        double factor;
        double size;
        
        int left, right;
        
        if( orientation == Orientation.HORIZONTAL ){
            factor = getRoot().getWidthFactor();
            size = width;
            left = minimumLeft.width;
            right = minimumRight.width;
        }
        else{
            factor = getRoot().getHeightFactor();
            size = height;
            left = minimumLeft.height;
            right = minimumRight.height;
        }
        
        if( factor <= 0 || Double.isNaN( factor ))
            return divider;
        
        double leftNeed = left / factor;
        double rightNeed = right / factor;
        double dividerNeed = dividerSize / factor;
        
        if( leftNeed + rightNeed + dividerNeed >= size )
            divider = (leftNeed + dividerNeed / 2) / ( leftNeed + rightNeed + dividerNeed );
        else if( divider * size < leftNeed + dividerNeed / 2 )
            divider = (leftNeed + dividerNeed / 2) / size;
        else if( divider*size > size - rightNeed - dividerNeed / 2 )
            divider = (size - rightNeed - dividerNeed / 2) / size;
        
        return divider;
    }
        
    /**
     * Gets the {@link Root} of the tree which stores all locations and sizes
     * of the children of this station.
     * @return the root
     */
    protected Root getRoot(){
        return root;
    }
    
    public String getFactoryID() {
        return SplitDockStationFactory.ID;
    }
    
    /**
     * Writes the layout of this station into <code>out</code>.
     * @param children the children of this station. Each child must have
     * a unique id
     * @param out the stream in which to write
     * @throws IOException if the stream throws an exception
     * @see #read(Map, boolean, DataInputStream)
     */
    public void write( Map<Dockable, Integer> children, DataOutputStream out ) throws IOException{
        root.write( children, out );
        out.writeBoolean( isFullScreen() );
        if( isFullScreen() ){
            out.writeInt( children.get( getFullScreen() ) );
        }
    }
    
    /**
     * Reads an earlier saved layout of this station. The method assumes that
     * the station has no children, otherwise the behavior is not specified.
     * @param children a mapping of ids and potential children of this station
     * @param ignoreChildren <code>true</code> if the children should not be changed
     * @param in the source of information
     * @throws IOException if the stream throws an exception
     */
    public void read( 
            Map<Integer, Dockable> children,
            boolean ignoreChildren,
            DataInputStream in ) throws IOException{
      
        if( !ignoreChildren ){
            while( dockables.size() > 0 )
                drag( dockables.get( 0 ).getDockable() );
            
            root.read( children, in );
            
            for( DockableDisplayer displayer : dockables ){
                Dockable dockable = displayer.getDockable();
                dockStationListeners.fireDockableAdding( dockable );
                
            	if( this.title != null ){
                    DockTitle title = dockable.getDockTitle( this.title );
                    if( title != null )
                        dockable.bind( title );
                    displayer.setTitle( title );
                }
                
                displayer.getDockable().setDockParent( this );
                add( displayer );
                dockStationListeners.fireDockableAdded( displayer.getDockable() );
            }
            
            if( in.readBoolean() )
                setFullScreen( children.get( in.readInt() ) );
            
            invalidate();
        }
    }
            
    /**
     * Orientation how two {@link Dockable Dockables} are aligned.
     */
    public enum Orientation{
        /** One {@link Dockable} is at the left, the other at the right */
        HORIZONTAL,
        /** One {@link Dockable} is at the top, the other at the bottom */
        VERTICAL 
    };
    

    /**
     * This listener is added to the parent of this station, and ensures
     * that the visibility-state of the children of this station is always
     * correct.
     * @author Benjamin Sigg
     */
    private class VisibleListener extends DockStationAdapter{
        @Override
        public void dockableVisibiltySet( DockStation station, Dockable dockable, boolean visible ) {
            visibility.fire();
        }
    }
    
    /**
     * This listener is added directly to the {@link Component} of this 
     * {@link SplitDockStation}. This listener reacts when a divider is grabbed
     * by the mouse, and the listener will move the divider to a new position.
     * @author Benjamin Sigg
     */
    private class DividerListener extends MouseInputAdapter{
        /** the node of the currently selected divider */
        private Node current;
        
        /** the current location of the divider */
        private double divider;
        
        /** the current state of the mouse: pressed or not pressed */
        private boolean pressed = false;
        
        /** the current bounds of the divider */
        private Rectangle bounds = new Rectangle();
        
        /** 
         * A small modification of the position of the mouse. The modification
         * is the distance to the center of the divider.
         */
        private int deltaX;
        
        /** 
         * A small modification of the position of the mouse. The modification
         * is the distance to the center of the divider.
         */
        private int deltaY;
        
        @Override
        public void mousePressed( MouseEvent e ) {
            if( !pressed ){
                pressed = true;
                mouseMoved( e );
                if( current != null ){
                    divider = current.getDividerAt( e.getX() + deltaX, e.getY() + deltaY );
                    repaint( bounds.x, bounds.y, bounds.width, bounds.height );
                    bounds = current.getDividerBounds( divider, bounds );
                    repaint( bounds.x, bounds.y, bounds.width, bounds.height );
                }
            }
        }
        @Override
        public void mouseDragged( MouseEvent e ) {
            if( pressed && current != null ){
                divider = current.getDividerAt( e.getX() + deltaX, e.getY() + deltaY );
                divider = validateDivider( divider, current );
                repaint( bounds.x, bounds.y, bounds.width, bounds.height );
                bounds = current.getDividerBounds( divider, bounds );
                repaint( bounds.x, bounds.y, bounds.width, bounds.height );
                
                if( continousDisplay && current != null ){
                    current.setDivider( divider );
                    root.updateBounds();
                }
            }
        }
        
        @Override
        public void mouseReleased( MouseEvent e ) {
            if( pressed ){
                pressed = false;
                if( current != null ){
                    current.setDivider( divider );
                    repaint( bounds.x, bounds.y, bounds.width, bounds.height );
                    root.updateBounds();
                }
                mouseMoved( e );
            }
        }
        
        @Override
        public void mouseMoved( MouseEvent e ) {            
            current = root.getDividerNode( e.getX(), e.getY() );
            
            if( current == null )
                SplitDockStation.this.setCursor( null );
            else if( current.getOrientation() == Orientation.HORIZONTAL )
                SplitDockStation.this.setCursor( Cursor.getPredefinedCursor( Cursor.W_RESIZE_CURSOR ));
            else
                SplitDockStation.this.setCursor( Cursor.getPredefinedCursor( Cursor.N_RESIZE_CURSOR ));

            if( current != null ){
                bounds = current.getDividerBounds( current.getDivider(), bounds );
                deltaX = bounds.width / 2 + bounds.x - e.getX();
                deltaY = bounds.height / 2 + bounds.y - e.getY();
            }
        }
        
        @Override
        public void mouseExited( MouseEvent e ) {
            if( !pressed ){
                current = null;
                SplitDockStation.this.setCursor( null );
            }
        }
        
        /**
         * Paints a line at the current location of the divider.
         * @param g the Graphics used to paint
         */
        public void paint( Graphics g ){
            if( current != null && pressed ){
                getPaint().drawDivider( g, SplitDockStation.this, bounds );
            }
        }
    }
}
