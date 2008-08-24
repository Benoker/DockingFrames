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

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.DockUI;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.accept.DockAcceptance;
import bibliothek.gui.dock.action.DefaultDockActionSource;
import bibliothek.gui.dock.layout.DockableProperty;
import bibliothek.gui.dock.station.*;
import bibliothek.gui.dock.station.screen.*;
import bibliothek.gui.dock.station.support.CombinerWrapper;
import bibliothek.gui.dock.station.support.DisplayerFactoryWrapper;
import bibliothek.gui.dock.station.support.DockableVisibilityManager;
import bibliothek.gui.dock.station.support.StationPaintWrapper;
import bibliothek.gui.dock.title.ControllerTitleFactory;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.gui.dock.title.DockTitleVersion;
import bibliothek.gui.dock.util.*;

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
        new PropertyKey<BoundaryRestriction>( "ScreenDockStation.boundary_restriction", BoundaryRestriction.FREE, true );
    
    /** a key for a property telling how to create new windows */
    public static final PropertyKey<ScreenDockWindowFactory> WINDOW_FACTORY =
        new PropertyKey<ScreenDockWindowFactory>( "ScreenDockStation.window_factory", new DefaultScreenDockWindowFactory(), true );
    
    /** The visibility state of the windows */
    private boolean showing = false;
    
    /** A list of all windows that are used by this station */
    private List<ScreenDockWindow> dockables = new ArrayList<ScreenDockWindow>();
    
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
        version = null;
        super.setController( controller );
        displayers.setController( controller );
        
        if( controller != null ){
            version = controller.getDockTitleManager().getVersion( TITLE_ID, ControllerTitleFactory.INSTANCE );
        }
        
        restriction.setProperties( controller );
        windowFactory.setProperties( controller );
        
        for( ScreenDockWindow window : dockables ){
            window.setController( controller );
        }
    }
    
    public DefaultDockActionSource getDirectActionOffers( Dockable dockable ) {
        return null;
    }

    public DefaultDockActionSource getIndirectActionOffers( Dockable dockable ) {
        return null;
    }

    public int getDockableCount() {
        return dockables.size();
    }

    public Dockable getDockable( int index ) {
        return dockables.get( index ).getDockable();
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
        for( int i = 0, n = dockables.size(); i<n; i++ ){
            ScreenDockWindow window = dockables.get( i );
            if( window.getDockable() == dockable )
                return i;
        }
        
        return -1;
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
        
        dropInfo.x = x;
        dropInfo.y = y;
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
        
        for( ScreenDockWindow window : dockables ){
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

    public DockableProperty getDockableProperty( Dockable dockable ) {
        ScreenDockWindow window = getWindow( dockable );
        Rectangle bounds = window.getWindowBounds();
        return new ScreenDockProperty( bounds.x, bounds.y, bounds.width, bounds.height );
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
        
        return dockables.get( index );
    }
    
    /**
     * Get's the <code>index</code>'th window of this station. The number
     * of windows is identical to the {@link #getDockableCount() number of Dockables}.
     * @param index the index of the window
     * @return the window which shows the index'th Dockable.
     */
    public ScreenDockWindow getWindow( int index ){
        return dockables.get( index );
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
        
        dropInfo.draw = true;
        if( dropInfo.combine != null )
            dropInfo.combine.setPaintCombining( true );
    }

    public void forget() {
        if( dropInfo != null ){
            dropInfo.draw = false;
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
        DockUtilities.ensureTreeValidity( this, dockable );
        
        if( bounds == null )
            throw new IllegalArgumentException( "Bounds must not be null" );
        
        listeners.fireDockableAdding( dockable );
        
        ScreenDockWindow window = createWindow();
        register( window );
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
        double bestRatio = 0;
        
        int x = property.getX();
        int y = property.getY();
        int width = property.getWidth();
        int height = property.getHeight();
        
        double propertySize = width * height;
        
        for( ScreenDockWindow window : dockables ){
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
                if( old.accept( this, dockable ) && dockable.accept( this, old )){
                    combine( old, dockable );
                    done = true;
                }
            }
        }
        
        if( !done ){
            boolean accept = accept( dockable ) && dockable.accept( this );
            if( accept ){
                addDockable( dockable, new Rectangle( x, y, width, height ), boundsIncludeWindow );
                done = true;
            }
        }
        
        return done;
    }
    
    /**
     * Combines the <code>lower</code> and the <code>upper</code> {@link Dockable}
     * to one {@link Dockable}, and replaces the <code>lower</code> with
     * this new Dockable. There are no checks whether this station 
     * {{@link #accept(Dockable) accepts} the new child or the children
     * can be combined. The creation of the new {@link Dockable} is done
     * by the {@link #getCombiner() combiner}.
     * @param lower a {@link Dockable} which must be child of this station
     * @param upper a {@link Dockable} which may be child of this station
     */
    public void combine( Dockable lower, Dockable upper ){
        ScreenDockWindow window = getWindow( lower );
        if( window == null )
            throw new IllegalArgumentException( "lower is not child of this station" );
        
        removeDockable( upper );
        
        listeners.fireDockableRemoving( lower );
        window.setDockable( null );
        lower.setDockParent( null );
        listeners.fireDockableRemoved( lower );
        
        Dockable valid = combiner.combine( lower, upper, this );
        
        listeners.fireDockableAdding( valid );
        valid.setDockParent( this );
        window.setDockable( valid );
        listeners.fireDockableAdded( valid );
    }
    
    public boolean canReplace( Dockable old, Dockable next ) {
        return true;
    }

    public void replace( Dockable current, Dockable other ){
        ScreenDockWindow window = getWindow( current );
        
        listeners.fireDockableRemoving( current );
        window.setDockable( null );
        current.setDockParent( null );
        listeners.fireDockableRemoved( current );
        
        listeners.fireDockableAdding( other );
        other.setDockParent( this );
        window.setDockable( other );
        listeners.fireDockableAdded( other );
    }
    
    /**
     * Removes the <code>dockable</code> from this station.
     * @param dockable the {@link Dockable} to remove
     */
    public void removeDockable( Dockable dockable ){
        int index = indexOf( dockable );
        
        if( index >= 0 ){
            removeDockable( index );
        }
    }
    
    /**
     * Removes the <code>index</code>'th {@link Dockable} of this station.
     * @param index the index of the {@link Dockable} to remove
     */
    public void removeDockable( int index ){
        ScreenDockWindow window = dockables.get( index );
        Dockable dockable = window.getDockable();
        
        listeners.fireDockableRemoving( dockable );
        dockables.remove( index );
        
        window.setVisible( false );
        window.setDockable( null );
        deregister( window );
        
        dockable.setDockParent( null );
        listeners.fireDockableRemoved( dockable );
    }
    
    /**
     * Invoked after a new {@link ScreenDockWindow} has been created. This
     * method adds some listeners to the window. If the method is overridden,
     * it should be called from the subclass to ensure the correct function
     * of this station.
     * @param window the window which was newly created
     */
    protected void register( ScreenDockWindow window ){
        dockables.add( window );
        window.setController( getController() );
    }
    
    /**
     * Invoked when a {@link ScreenDockWindow} is no longer needed. This
     * method removes some listeners from the window. If overridden
     * by a subclass, the subclass should ensure that this implementation
     * is invoked too.
     * @param window the old window
     */
    protected void deregister( ScreenDockWindow window ){
        if( frontWindow == window )
            frontWindow = null;
        dockables.remove( window );
        window.setController( null );
        window.destroy();
    }
    
    /**
     * Creates a new window which is associated with this station.
     * @return the new window
     */
    protected ScreenDockWindow createWindow(){
        return windowFactory.getValue().createWindow( this );
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
            for( ScreenDockWindow window : dockables ){
                window.setVisible( showing );
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
     * Creates a {@link DockTitle} that will be used for <code>dockable</code>.
     * @param dockable the element for which a title is required
     * @return the new title or <code>null</code>
     */
    public DockTitle createDockTitle( Dockable dockable ){
        if( version == null )
            return null;
        
        return dockable.getDockTitle( version );
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
        for( ScreenDockWindow window : dockables )
            window.checkWindowBounds();
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
        public int x, y, titleX, titleY;
        /** Possible new parent */
        public ScreenDockWindow combine;
        /** <code>true</code> if some sort of selection should be painted */
        public boolean draw;
    }
    
}
