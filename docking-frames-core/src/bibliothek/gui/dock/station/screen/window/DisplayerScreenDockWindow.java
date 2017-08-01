/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2008 Benjamin Sigg
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
package bibliothek.gui.dock.station.screen.window;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.ScreenDockStation;
import bibliothek.gui.dock.station.DockableDisplayer;
import bibliothek.gui.dock.station.DockableDisplayerListener;
import bibliothek.gui.dock.station.StationChildHandle;
import bibliothek.gui.dock.station.screen.ScreenDockFullscreenStrategy;
import bibliothek.gui.dock.station.screen.ScreenDockWindow;
import bibliothek.gui.dock.station.screen.ScreenDockWindowListener;
import bibliothek.gui.dock.themes.ThemeManager;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.gui.dock.util.BackgroundAlgorithm;
import bibliothek.gui.dock.util.BackgroundPaint;

/**
 * A window that uses a {@link DockableDisplayer} to show the {@link Dockable}.
 * @author Benjamin Sigg
 */
public abstract class DisplayerScreenDockWindow implements ScreenDockWindow {
    /** the owner of this station */
    private ScreenDockStation station;
    
    /** the dockable shown on this station */
    private StationChildHandle handle;
    
    /** all listeners known to this window */
    private List<ScreenDockWindowListener> listeners = new ArrayList<ScreenDockWindowListener>();
    
    /** a listener to the current {@link DockableDisplayer} */
    private DockableDisplayerListener displayerListener = new DockableDisplayerListener(){
    	public void discard( DockableDisplayer displayer ){
	    	discardDisplayer();	
    	}
    	public void moveableElementChanged( DockableDisplayer displayer ){
    		updateTitleMover();
    	}
    };
    
    /** the controller in whose realm this window works */
    private DockController controller;
    
    /** whether the {@link DockTitle} should be shown */
    private boolean showTitle = true;
    
    /** strategy for handling fullscreen mode */
    private ScreenDockFullscreenStrategy strategy;
    
    /** boundaries used in normal mode */
    private Rectangle normalBounds;
    
    /** the algorithm that paints the background */
    private Background background = new Background();
    
    /** a helper class moving the entire window if the title is dragged by the mouse */
    private WindowMover titleMover;
    
    /** the configuration that was used to create this window */
    private WindowConfiguration configuration;
    
    /** whether {@link #configuration} has been applied */
    private boolean configured = false;
    
    /**
     * Creates a new window
     * @param station the owner of this window, not <code>null</code>
     * @param configuration default configuration of this window, cannot be changed once the window is
     * created
     */
    public DisplayerScreenDockWindow( ScreenDockStation station, WindowConfiguration configuration ){
        if( station == null )
            throw new IllegalArgumentException( "station must not be null" );
        this.station = station;
        this.configuration = configuration;
    }
    
    /**
     * Called the first time when a {@link Dockable} is set.
     * @param configuration the configuration that is to be applied
     */
    protected void init( WindowConfiguration configuration ){
        if( configuration.isMoveOnTitleGrab() ){
        	titleMover = createTitleMover();
        	titleMover.setAllowDragAndDrop( configuration.isAllowDragAndDropOnTitle() );
        	titleMover.setResetOnDropable( configuration.isResetOnDropable() );
        }
    }
    
    /**
     * Tells whether this window can be moved by grabbing the title.
     * @return whether a {@link WindowMover} has been created
     */
    protected boolean isMoveOnTitleGrab(){
    	return titleMover != null;
    }
    
    /**
     * Creates a new {@link WindowMover} which is used to move this window if the {@link DockTitle}
     * is dragged by the mouse. This method is only called if {@link WindowConfiguration#isMoveOnTitleGrab()}
     * returns true.
     * @return the new mover, can be <code>null</code>
     */
    protected WindowMover createTitleMover(){
    	return new WindowMover( this );
    }
    
    public void addScreenDockWindowListener( ScreenDockWindowListener listener ){
    	listeners.add( listener );
    }
    
    public void removeScreenDockWindowListener( ScreenDockWindowListener listener ){
    	listeners.remove( listener );
    }
    
    /**
     * Gets a list of all listeners that are currently registered.
     * @return all listeners
     */
    protected ScreenDockWindowListener[] listeners(){
    	return listeners.toArray( new ScreenDockWindowListener[ listeners.size() ] );
    }
    
    /**
     * Informs all listeners that the fullscreen state changed
     */
    protected void fireFullscreenChanged(){
    	for( ScreenDockWindowListener listener : listeners() ){
    		listener.fullscreenStateChanged( this );
    	}
    }
    
    /**
     * Informs all listeners that the visibility state changed
     */
    protected void fireVisibilityChanged(){
    	for( ScreenDockWindowListener listener : listeners() ){
    		listener.visibilityChanged( this );
    	}
    }
    
    /**
     * Informs all listeners that the current size or position changed
     */
    protected void fireShapeChanged(){
    	for( ScreenDockWindowListener listener : listeners() ){
    		listener.shapeChanged( this );
    	}
    }
    
    /**
     * Informs all listeners that this window wants to be closed
     */
    protected void fireWindowClosing(){
    	for( ScreenDockWindowListener listener : listeners() ){
    		listener.windowClosing( this );
    	}
    }
    
    /**
     * Forces the subclass of this window to show <code>displayer</code>. Only
     * one displayer should be shown at any time. A new displayer replaces
     * an old one. 
     * @param displayer the displayer to show or <code>null</code> to remove
     * the current displayer
     */
    protected abstract void showDisplayer( DockableDisplayer displayer );
    
    /**
     * Gets the component on which {@link ScreenDockWindow#setWindowBounds(java.awt.Rectangle)}
     * is applied.
     * @return the base component
     */
    protected abstract Component getWindowComponent();
    
    /**
     * Sets the algorithm that paints the background of this window.
     * @param background the algorithm, may be <code>null</code>
     */
    protected abstract void setBackground( BackgroundAlgorithm background );
    
    /**
     * Sets whether the {@link DockTitle} should be shown or not.
     * @param showTitle <code>true</code> if the title should be visible,
     * <code>false</code> otherwise
     */
    public void setShowTitle( boolean showTitle ) {
        if( this.showTitle != showTitle ){
            this.showTitle = showTitle;
            
            if( handle != null ){
            	if( showTitle ){
            		handle.setTitleRequest( station.getTitleVersion() );
            	}
            	else{
            		handle.setTitleRequest( null );
            	}
            }
        }
    }
    
    /**
     * Tells whether the {@link DockTitle} is generally shown.
     * @return <code>true</code> if the title is shown
     */
    public boolean isShowTitle() {
        return showTitle;
    }
    
    public Dockable getDockable() {
    	if( handle == null )
    		return null;
    	return handle.getDockable();
    }

    public DockableDisplayer getDockableDisplayer(){
	    if( handle == null ){
	    	return null;
	    }
	    return handle.getDisplayer();
    }
    
    public void setDockable( Dockable dockable ) {
    	if( dockable != null && !configured ){
    		init( configuration );
    		configured = true;
    	}
    	
    	// remove old displayer
        if( handle != null ){
        	DockableDisplayer displayer = handle.getDisplayer();
        	displayer.removeDockableDisplayerListener( displayerListener );
            handle.destroy();
            updateTitleMover();
            handle = null;
        }
        
        // add new displayer
        DockableDisplayer displayer = null;
        
        if( dockable != null ){
        	handle = new StationChildHandle( station, station.getDisplayers(), dockable, showTitle ? station.getTitleVersion() : null ){
        		@Override
        		protected void updateTitle( DockTitle title ){
        			super.updateTitle( title );
        			updateTitleMover();
        		}
        	};
        	handle.updateDisplayer();
            displayer = handle.getDisplayer();
            displayer.addDockableDisplayerListener( displayerListener );
            updateTitleMover();
        }
        
        showDisplayer( displayer );
    }
    
    /**
     * Gets the configuration which was used to set up this window.
     * @return the configuration, should not be modified by clients or subclasses
     */
    public WindowConfiguration getConfiguration(){
		return configuration;
	}
    
    /**
     * If there is a {@link #titleMover}, then this method updates the element of the mover. It first
     * tries to set a {@link DockTitle}, if not available the method tries to find other elements like
     * a tab.
     */
    private void updateTitleMover(){
    	if( titleMover != null ){
    		if( handle != null && handle.getDisplayer() != null ){
    			titleMover.setElement( handle.getDisplayer().getMoveableElement() );
    		}
    		else{
    			titleMover.setElement( null );
    		}
    	}
    }
    
    /**
     * Replaces the current {@link DockableDisplayer} with a new instance.
     */
    protected void discardDisplayer(){
    	DockableDisplayer displayer = handle.getDisplayer();
    	displayer.removeDockableDisplayerListener( displayerListener );
    	handle.updateDisplayer();
    	displayer = handle.getDisplayer();
    	displayer.addDockableDisplayerListener( displayerListener );
    	showDisplayer( displayer );
    }
    
    public void setFullscreenStrategy( ScreenDockFullscreenStrategy strategy ) {
	    this.strategy = strategy;	
    }
    
    public boolean isFullscreen() {
    	if( strategy == null ){
    		if( isVisible() ){
    			throw new IllegalStateException( "no strategy available" );
    		}
    		else{
    			return false;
    		}
    	}
    	return strategy.isFullscreen( this );
    }
    
    public void setFullscreen( boolean fullscreen ) {
    	if( strategy == null ){
    		throw new IllegalStateException( "no strategy available" );
    	}
    	boolean state = isFullscreen();
    	if( state != fullscreen ){
    		strategy.setFullscreen( this, fullscreen );
    		fireFullscreenChanged();
    	}
    }
    
    public void setNormalBounds( Rectangle bounds ) {
	    this.normalBounds = bounds;	
    }
    
    public Rectangle getNormalBounds() {
	    return normalBounds;
    }
    
    public Dimension getMinimumWindowSize(){
    	return getWindowComponent().getMinimumSize();
    }

    public void setController( DockController controller ) {
        // remove old DockTitle
    	if( handle != null ){
    		if( this.controller != null ){
    			handle.setTitleRequest( null );
    		}
    	}
    	
    	background.setController( controller );
        this.controller = controller;
        
        // create new DockTitle
        if( handle != null ){
            if( this.controller != null && isShowTitle() ){
            	handle.setTitleRequest( station.getTitleVersion() );
            }
        }
    }
    
    public Point getTitleCenter(){
    	if( handle == null ){
    		return null;
    	}
    	
    	DockableDisplayer displayer = handle.getDisplayer();
    	if( displayer == null ){
    		return null;
    	}
    	
    	Point center = displayer.getTitleCenter();
    	if( center == null ){
    		return null;
    	}
    	
    	Component base = getWindowComponent();
        if( base == null )
            return null;
        
        return SwingUtilities.convertPoint( displayer.getComponent(), center, base );
    }
    
    public Point getOffsetDrop() {
    	if( handle == null )
    		return null;
    	
    	DockableDisplayer displayer = handle.getDisplayer();
    	
    	if( displayer == null )
            return null;
        
        Insets insets = getDockableInsets();
        
        return new Point( insets.left, insets.top );
    }
    
    public Point getOffsetMove() {
    	if( handle == null )
    		return null;
    	
    	DockableDisplayer displayer = handle.getDisplayer();
    	
        if( displayer == null )
            return null;
        
        DockTitle title = displayer.getTitle();
        if( title == null )
            return null;
        
        Component base = getWindowComponent();
        if( base == null )
            return null;
        
        Point zero = new Point( 0, 0 );
        zero = SwingUtilities.convertPoint( title.getComponent(), zero, base );
        return zero;
    }
    
    public boolean inTitleArea( int x, int y ){
    	if( handle == null )
    		return false;
    	
    	DockableDisplayer displayer = handle.getDisplayer();
    	
        if( displayer == null )
            return false;
        
        Point point = new Point( x, y );
        SwingUtilities.convertPointFromScreen( point, displayer.getComponent() );
        return displayer.titleContains( point.x, point.y );
    }
    
    public boolean inCombineArea( int x, int y ) {
    	return inTitleArea( x, y );
    }
    
    public boolean contains( int x, int y ){
	    Component component = getWindowComponent();
	    Point point = new Point( x, y );
	    SwingUtilities.convertPointFromScreen( point, component );
	    return component.contains( point );
    }
    
    /**
     * Gets the controller in whose realm this window is used.
     * @return the controller, can be <code>null</code>
     */
    public DockController getController() {
        return controller;
    }
    
    public ScreenDockStation getStation(){
        return station;
    }
    
    /**
     * The algorithm that paints the background of this window.
     * @author Benjamin Sigg
     */
    protected class Background extends BackgroundAlgorithm implements ScreenDockWindowBackgroundComponent{
    	public Background(){
    		super( ScreenDockWindowBackgroundComponent.KIND, ThemeManager.BACKGROUND_PAINT + ".station.screen" );
    	}
    	
    	@Override
    	public void set( BackgroundPaint value ){
    		super.set( value );
    		if( getPaint() == null ){
    			setBackground( null );
    		}
    		else{
    			setBackground( this );
    		}
    	}
    	
		public ScreenDockWindow getWindow(){
			return DisplayerScreenDockWindow.this;
		}

		public DockStation getStation(){
			return getWindow().getStation();
		}

		public Component getComponent(){
			return getWindowComponent();
		}
    }
}
