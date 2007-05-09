/**
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

package bibliothek.gui;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

import javax.swing.FocusManager;
import javax.swing.JComponent;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputAdapter;
import javax.swing.event.MouseInputListener;

import bibliothek.gui.dock.*;
import bibliothek.gui.dock.action.*;
import bibliothek.gui.dock.action.views.ActionViewConverter;
import bibliothek.gui.dock.event.DockAdapter;
import bibliothek.gui.dock.event.DockControllerListener;
import bibliothek.gui.dock.event.DockTitleEvent;
import bibliothek.gui.dock.themes.DefaultTheme;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.gui.dock.title.DockTitleVersion;
import bibliothek.gui.dock.title.MovingTitleGetter;
import bibliothek.gui.dock.util.DockProperties;

/**
 * A controller is needed to drag and drop {@link Dockable dockables} from
 * one {@link DockStation} to another station.<br>
 * In order to use a station, it must be {@link #add(DockStation) added}
 * to a controller. Stations which are children of other stations will be 
 * added automatically. Dockables can only be dragged and dropped from
 * stations with the same controller.<br>
 * Note: if a controller is no longer in use, the method {@link #kill()} should
 * be called to free some resources.
 * @author Benjamin Sigg
 */
public class DockController {
	/** the known stations */
    private List<DockStation> stations = new ArrayList<DockStation>();
    /** the known dockables */
    private List<Dockable> dockables = new ArrayList<Dockable>();

    /** the Dockable which has currently the focus, can be <code>null</code> */
    private Dockable focusedDockable = null;
    
    /** an observer of the stations */
    private StationListener stationListener = new StationListener();
    /** observer of this controller */
    private List<DockControllerListener> listeners = new ArrayList<DockControllerListener>();
    
    /** <code>true</code> as long as the user drags a title or a Dockable */
    private boolean onMove = false;
    /** <code>true</code> while a drag and drop-operation is performed */
    private boolean onPut = false;
    /** how many pixels the mouse must be moved until a title is dragged */
    private int dragDistance = 10;
    /** Whether a drag event can only be initialiced by dragging a title or not */
    private boolean dragOnlyTitel = false;
    /** the current destination of a dragged dockable */
    private DockStation dragStation;
    /** a window painting a title onto the screen */
    private TitleWindow movingTitleWindow;
    /** the point where the mouse was pressed on the currently dragged title */
    private Point pressPoint;
    
    /** <code>true</code> while the controller actively changes the focus */
    private boolean onFocusing = false;
    /** a special controller listening to AWT-events and changing the focused dockable */
    private FocusController focusController;
    
    /** an observer of the {@link DockTitle} */
    private TitleListener titleListener = new TitleListener();
    /** mapping tells which titles are currently active */
    private Map<DockTitle, Dockable> activeTitles = new HashMap<DockTitle, Dockable>();
    /** a source for {@link DockTitle} */
    private DockTitleManager dockTitles = new DockTitleManager( this );
   
    /** A list of sources for a {@link DockActionSource} */
    private List<ActionOffer> actionOffers = new ArrayList<ActionOffer>();
    /** A  list of sources for {@link DockActionSource DockActionSources} */
    private List<ActionGuard> guards = new ArrayList<ActionGuard>();
    /** The default source for a {@link DockActionSource} */
    private ActionOffer defaultActionOffer;
    /** A converter used to transform {@link DockAction actions} into views */
    private ActionViewConverter actionViewConverter;
    
    /** behavior which dockable can be dropped over which station */
    private DockAcceptance acceptance;
    /** tells which popups are to be shown */
    private ActionPopupSuppressor popupSuppressor = ActionPopupSuppressor.ALLOW_ALWAYS;
   
    /** whether stations with none or one child will be removed */
    private boolean singleParentRemove = false;
    /** remover of stations with none or one child */
    private SingleParentRemover remover;
    
    /** a theme describing the look of the stations */
    private DockTheme theme;
    /** a set of properties */
    private DockProperties properties = new DockProperties();
    
    /**
     * Creates a new controller. 
     */
    public DockController(){
    	addDockControllerListener( titleListener );
        addDockControllerListener( new MouseDockableListener() );
        addDockControllerListener( stationListener );

        defaultActionOffer = createDefaultActionOffer();
        focusController = createFocusController();
        actionViewConverter = createActionViewConverter();
        
        setTheme( new DefaultTheme() );
    }
    
    /**
     * Removes listeners and frees resources. This method should be called
     * if this controller is no longer needed.
     */
    public void kill(){
	    focusController.kill();
        
        List<DockStation> stations = new ArrayList<DockStation>( this.stations );
        for( DockStation station : stations )
            remove( station );
    }
    
    /**
     * Creates the focus-controller of this controller.
     * @return the controller, not <code>null</code>
     */
    protected FocusController createFocusController(){
        return new DefaultFocusController( this );
    }
    
    /**
     * Gets the current focus-controller
     * @return the controller
     */
    public FocusController getFocusController() {
        return focusController;
    }
    
    /**
     * Creates the converter that will transform actions into views.
     * @return the new converter.
     */
    protected ActionViewConverter createActionViewConverter(){
    	return new ActionViewConverter();
    }

    /**
     * Gets the current {@link ActionViewConverter}.
     * @return the converter
     */
    public ActionViewConverter getActionViewConverter(){
    	return actionViewConverter;
    }
    
    /**
     * Creates the default action offer. This {@link ActionOffer} will
     * be used if no other offer was interested in a Dockable.
     * @return the offer, must not be <code>null</code>
     */
    protected ActionOffer createDefaultActionOffer(){
        return new DefaultActionOffer();
    }
    
    /**
     * Tells whether stations with only one child are removed or not.
     * @return <code>true</code> if stations with one or less
     * children are removed automatically
     * @see #setSingleParentRemove(boolean)
     */
    public boolean isSingleParentRemove(){
        return singleParentRemove;
    }
    
    /**
     * Sets whether stations with one or none child are removed automatically
     * or not. This property has a great effect on some stations, clients shouldn't
     * change the value once the first station is {@link #add(DockStation) added}.
     * @param remove <code>true</code> if stations with one or less
     * children are removed
     * @see #createSingleParentRemover()
     */
    public void setSingleParentRemove( boolean remove ){
        if( singleParentRemove != remove ){
            if( remove ){
                if( remover == null )
                    remover = createSingleParentRemover();
                
                remover.install( this );
            }
            else{
                remover.uninstall( this );
            }
            singleParentRemove = remove;
        }
    }
    
    /**
     * Creates a {@link SingleParentRemover} that will be used to remove
     * some stations from this controller.
     * @return The remover
     * @see #setSingleParentRemove(boolean)
     */
    protected SingleParentRemover createSingleParentRemover(){
    	return new SingleParentRemover();
    }
            
    /**
     * Gets the behavior that tells which stations can have which children.
     * @return the behavior, may be <code>null</code>
     * @see #setAcceptance(DockAcceptance)
     */
    public DockAcceptance getAcceptance() {
        return acceptance;
    }
    
    /**
     * Sets the behavior that decides which station can have which children. 
     * The <code>acceptance</code> does not override the
     * <code>accept</code>-methods of {@link Dockable#accept(DockStation) Dockable}
     * and {@link DockStation#accept(Dockable) DockStation}.
     * @param acceptance the behavior or <code>null</code>
     */
    public void setAcceptance( DockAcceptance acceptance ) {
        this.acceptance = acceptance;
    }

    /**
     * Gets the guard which decides, which popups should be allowed.
     * @return the guard
     * @see #setPopupSuppressor(ActionPopupSuppressor)
     */
    public ActionPopupSuppressor getPopupSuppressor() {
        return popupSuppressor;
    }
    
    /**
     * Sets the guard which decides, which popups with {@link DockAction DockActions}
     * are allowed to show up, and which popups will be suppressed.
     * @param popupSuppressor the guard
     */
    public void setPopupSuppressor( ActionPopupSuppressor popupSuppressor ) {
        if( popupSuppressor == null )
            throw new IllegalArgumentException( "suppressor must not be null" );
        this.popupSuppressor = popupSuppressor;
    }
    
    /**
     * Tells whether dockables can only be dragged through their title or not.
     * @return <code>true</code> if a Dockable must be dragged through their
     * titles, <code>false</code> if every part of the dockable can be
     * catched by the mouse.
     * @see #setDragOnlyTitel(boolean)
     */
    public boolean isDragOnlyTitel(){
		return dragOnlyTitel;
	}
    
    /**
     * Tells whether dockables can only be dragged through their title or not. 
     * @param dragOnlyTitel <code>true</code> if a Dockable must be dragged through their
     * titles, <code>false</code> if every part of the dockable can be
     * catched by the mouse.
     */
    public void setDragOnlyTitel( boolean dragOnlyTitel ){
		this.dragOnlyTitel = dragOnlyTitel;
	}
    
    /**
     * Gets the factory for a {@link DockActionSource} which is used
     * if no other offer was {@link ActionOffer#interested(Dockable) interested}
     * in a {@link Dockable}. 
     * @return the default offer
     */
    public ActionOffer getDefaultActionOffer() {
        return defaultActionOffer;
    }
    
    /**
     * Sets the factory for a {@link DockActionSource} which is used
     * if no other offer was {@link ActionOffer#interested(Dockable) interested}
     * in a {@link Dockable}. 
     * @param defaultActionOffer the offer, not <code>null</code>
     */
    public void setDefaultActionOffer( ActionOffer defaultActionOffer ) {
        if( defaultActionOffer == null )
            throw new IllegalArgumentException();
                
        this.defaultActionOffer = defaultActionOffer;
    }
    
    /**
     * Adds a factory for a {@link DockActionSource}. The factory will
     * create a source if it is the first offer which is
     * {@link ActionOffer#interested(Dockable) interested} in a {@link Dockable}.
     * @param offer the algorithm
     */
    public void addActionOffer( ActionOffer offer ){
        if( offer == null )
            throw new IllegalArgumentException();
        actionOffers.add( offer );
    }
    
    /**
     * Removes an earlier added offer.
     * @param offer the factory to remove
     */
    public void removeActionOffer( ActionOffer offer ){
        actionOffers.remove( offer );
    }
    
    /**
     * Sets the theme of this controller. This method ensures that all
     * registered stations know also the new theme.
     * @param theme the new theme
     */
    public void setTheme(DockTheme theme) {
    	if( theme == null )
    		throw new IllegalArgumentException( "Theme must not be null" );
    	
    	if( this.theme != theme ){
    		Dockable focused = getFocusedDockable();
    		
    		if( this.theme != null )
    			this.theme.uninstall( this );
    		
    		this.theme = theme;
    		theme.install( this );
    		
    		// update only those station which are registered to this controller
    		List<DockStation> currentStations = new ArrayList<DockStation>( stations );
    		for( DockStation station : currentStations ){
    			if( station.getController() == this ){
    				station.updateTheme();
    			}
    		}
    		
    		setFocusedDockable( focused, true );
    	}
	}
    
    /**
     * Gets the current theme of this controller.
     * @return the theme
     */
    public DockTheme getTheme() {
		return theme;
	}
    
    /**
     * A set of properties that can be used at any place.
     * @return the set of properties
     */
    public DockProperties getProperties(){
		return properties;
	}
    
    /**
     * Tells whether the user has currently grabbed a dockable and moves
     * the dockable around.
     * @return <code>true</code> if a Dockable is currently dragged
     */
    public boolean isOnMove(){
        return onMove;
    }
    
    /**
     * Tells whether this controller currently puts a Dockable. A Dockable
     * is put as soon as the user releases the mouse.
     * @return <code>true</code> if a Dockable is moved
     */
    public boolean isOnPut() {
        return onPut;
    }
    
    /**
     * Searches a station which can become the parent of <code>dockable</code> 
     * if the mouse is released at <code>mouseX/mouseY</code>.
     * @param mouseX x-coordinate of the mouse on the screen
     * @param mouseY y-coordinate of the mouse on the screen
     * @param titleX x-coordinate of the dragged title or mouseX
     * @param titleY y-coordinate of the dragged title or mouseY
     * @param dockable a Dockable which is dragged
     * @return the new parent of <code>dockable</code> or <code>null</code>
     */
    protected DockStation preparePut( int mouseX, int mouseY, int titleX, int titleY, Dockable dockable ){
        List<DockStation> list = listStationsOrdered( mouseX, mouseY, dockable );
        
        for( DockStation station : list ){   
            if( dockable.getDockParent() == station ){
                // just a move
                if( station.prepareMove( mouseX, mouseY, titleX, titleY, dockable ) ){
                    return station;
                }
            }
            else{
                // perhaps a drop
                if( acceptance == null || acceptance.accept( station, dockable )){
                    if( station.accept( dockable ) && dockable.accept( station ) ){
                        if( station.prepareDrop( mouseX, mouseY, titleX, titleY, dockable )){
                            return station;
                        }
                    }
                }
            }
        }
        
        return null;
    }
    
    /**
     * Executes a drag and drop event. <code>dockable</code> is removed
     * from its parent (if the parent is not <code>station</code>) and
     * dropped to <code>station</code>. The new location of
     * <code>dockable</code> has to be precomputed by <code>station</code>.
     * @param dockable a {@link Dockable} which is moved
     * @param station the new parent of <code>dockable</code>
     */
    protected void executePut( Dockable dockable, DockStation station ){
        onPut = true;
        try{
            if( station == null )
                throw new IllegalStateException( "There is no station to put the dockable." );
            
            DockStation parent = dockable.getDockParent();
            if( parent != station){
                fireDockableDrag( dockable, station );
                parent.drag( dockable );
                station.drop();
                updateChildrenTitle( dockable );
                fireDockablePut( dockable, station );
            }
            else{
                fireDockableDrag( dockable, parent );
                parent.move();
                fireDockablePut( dockable, parent );
            }
        }
        finally{
            onPut = false;
        }
    }
    
    /**
     * Rebinds all titles of the children of <code>dockable</code>. This action
     * ensures that the titles have the correct {@link DockActionSource}. This
     * method must only be called if the normal register/unregister mechanism is
     * disabled. This case happens only if a Dockable is dragged.
     * @param dockable a DockStation whose children will be updated
     */
    protected void updateChildrenTitle( Dockable dockable ){
    	DockStation station = dockable.asDockStation();
    	if( station != null ){
    		for( int i = 0, n = station.getDockableCount(); i<n; i++ ){
    			DockUtilities.visit( station.getDockable(i), new DockUtilities.DockVisitor(){
    				@Override
    				public void handleDockable(Dockable dockable) {
    					DockStation parent = dockable.getDockParent();
    					DockTitle[] titles = parent.getDockTitles( dockable );
    					if( titles != null ){
    						for( DockTitle title : titles ){
    							dockable.unbind( title );
    							dockable.bind( title );
    						}
    					}
    				}
    			});
    		}
    	}
    }
    
    /**
     * Makes a list of all stations which are visible and contain the point
     * <code>x/y</code>. The stations are ordered by their visibility.
     * @param x x-coordinate on the screen
     * @param y y-coordinate on the screen
     * @param moved a Dockable which is dragged. If this is a 
     * station, then no child of the station will be in the resulting list.
     * @return a list of stations
     */
    protected List<DockStation> listStationsOrdered( int x, int y, Dockable moved ){
        List<DockStation> result = new LinkedList<DockStation>();
        DockStation movedStation = moved.asDockStation();
                
        for( DockStation station : stations ){   
            if( movedStation == null || (!DockUtilities.isAnchestor( movedStation, station ) && movedStation != station )){
                if( station.isStationVisible() && station.getStationBounds().contains( x, y )){
                    int index = 0;
                    
                    // insertion sort
                    for( DockStation resultStation : result ){
                        int compare = compare( resultStation, station );
                        if( compare < 0 )
                            break;
                        else
                            index++;
                    }
                    
                    result.add( index, station );
                }
            }
        }        
        return result;
    }    
    
    /**
     * Tries to decide which station is over the other stations.
     * @param a the first station
     * @param b the second station
     * @return a number less/equal/greater than zero if
     * a is less/equal/more visible than b. 
     */
    protected int compare( DockStation a, DockStation b ){
        if( DockUtilities.isAnchestor( a, b ))
            return -1;
        
        if( DockUtilities.isAnchestor( b, a ))
            return 1;
        
        if( a.canCompare( b ))
            return a.compare( b );
        
        if( b.canCompare( a ))
            return -b.compare( a );
        
        Dockable dockA = a.asDockable();
        Dockable dockB = b.asDockable();
        
        if( dockA != null && dockB != null ){
            Component compA = dockA.getComponent();
            Component compB = dockB.getComponent();
            
            Window windowA = SwingUtilities.getWindowAncestor( compA );
            Window windowB = SwingUtilities.getWindowAncestor( compB );
            
            if( windowA != null && windowB != null ){
                if( isParent( windowA, windowB ))
                    return -1;
                
                if( isParent( windowB, windowA ))
                    return 1;
            }
        }
        return 0;
    }
        
    /**
     * Tells whether <code>parent</code> is really a parent of <code>child</code>
     * or not.
     * @param parent a window which may be an anchestor  of <code>child</code>
     * @param child a window which may be child of <code>parent</code>
     * @return <code>true</code> if <code>parent</code> is an
     * anchestor of <code>child</code>
     */
    private boolean isParent( Window parent, Window child ){
        Window temp = child.getOwner();
        while( temp != null ){
            if( temp == parent )
                return true;
            
            temp = temp.getOwner();
        }
        
        return false;
    }
    
    /**
     * Adds a station to this controller. The controller allows the user to
     * drag and drop children from and to <code>station</code>. If
     * the children of <code>station</code> are stations itself, then
     * they will be added automatically
     * @param station the new station
     */
    public void add( DockStation station ){
    	if( station == null )
            throw new NullPointerException( "Station must not be null" );
    	
        if( !stations.contains( station )){
            DockController other = station.getController();
            if( other != null && other != this ){
                other.remove( station );
            }
            
            DockUtilities.visit( station, new DockUtilities.DockVisitor(){
                @Override
                public void handleDockable( Dockable dockable ) {
                    register( dockable );
                }
                @Override
                public void handleDockStation( DockStation station ) {
                    register( station );
                }
            });
        }
    }
    
    /**
     * Removes a station which was managed by this controller.
     * @param station the station to remove
     */
    public void remove( DockStation station ){
        if( stations.contains( station )){
            Dockable dock = station.asDockable();
            if( dock != null ){
                DockStation parent = dock.getDockParent();
                if( parent != null )
                    parent.drag( dock );
            }
            
            DockUtilities.visit( station, new DockUtilities.DockVisitor(){
                @Override
                public void handleDockable( Dockable dockable ) {
                    unregister( dockable );
                }
                @Override
                public void handleDockStation( DockStation station ) {
                    unregister( station );
                }
            });
        }
    }
    
    /**
     * Gets a list of stations which have no parent and are therefore
     * the roots of the dock-trees.
     * @return the roots
     */
    public DockStation[] listRoots(){
        List<DockStation> list = new LinkedList<DockStation>();
        for( DockStation station : stations ){
            Dockable dockable = station.asDockable();
            if( dockable == null || dockable.getDockParent() == null )
                list.add( station );
        }
        
        return list.toArray( new DockStation[ list.size() ] );
    }
    
    /**
     * Registers <code>dockable</code>, the controller will know the titles
     * of <code>dockable</code> to allow drag and drop operations.<br>
     * Clients and subclasses should not call this method.
     * @param dockable a new Dockable
     */
    protected void register( Dockable dockable ){
        if( !dockables.contains( dockable )){
            fireDockableRegistering( dockable );
            
            dockables.add( dockable );
            dockable.addDockableListener( titleListener );
            dockable.setController( this );
            
            fireDockableRegistered( dockable );
        }
    }
    
    /**
     * Unregisters <code>dockable</code>, the controller will no longer 
     * support drag and drop for <code>dockable</code>.<br>
     * Clients and subclasses should not call this method.
     * @param dockable the element to remove
     */
    protected void unregister( Dockable dockable ){
        if( dockables.remove( dockable ) ){
            dockable.setController( null );
            dockable.removeDockableListener( titleListener );
            
            fireDockableUnregistered( dockable );
            
            if( focusedDockable == dockable )
                setFocusedDockable( null, false );
        }
    }
    
    /**
     * Registers <code>station</code>, this controller will support
     * drag and drop for <code>station</code>.<br>
     * Clients and subclasses should not call this method.
     * @param station the station to add
     */
    protected void register( DockStation station ){
        if( !stations.contains( station )){
        	fireDockStationRegistering( station );
            
            stations.add( station );
            station.addDockStationListener( stationListener );
            station.setController( this );
            station.updateTheme();
            
            fireDockStationRegistered( station );
        }
    }
    
    /**
     * Unregisters <code>station</code>, this controller will no longer
     * support drag and drop operations for <code>station</code>.<br>
     * Clients and subclasses should not call this method.
     * @param station the station to remove
     */
    protected void unregister( DockStation station ){
        if( stations.remove( station ) ){
            station.setController( null );
            station.removeDockStationListener( stationListener );
            
            fireDockStationUnregistered( station );
        }
    }

    
    /**
     * Tells whether one of the methods which change the focus is currently
     * running, or not. If the result is <code>true</code>, none should
     * change the focus.
     * @return <code>true</code> if the focus is currently changing
     */
    public boolean isOnFocusing() {
        return onFocusing;
    }
    
    /**
     * Sets the focused {@link Dockable}. If <code>focusedDockable</code>
     * is a station and one of its children has the focus, then nothing will
     * happen.
     * @param focusedDockable the element which should have the focus
     * @see #isOnFocusing()
     */
    public void setAtLeastFocusedDockable( Dockable focusedDockable ) {
        if( this.focusedDockable == null )
            setFocusedDockable( focusedDockable, false );
        
        if( !DockUtilities.isAnchestor( focusedDockable, this.focusedDockable ))
            setFocusedDockable( focusedDockable, false );
    }
    
    /**
     * Sets the {@link Dockable} which should have the focus.
     * @param focusedDockable the element with the focus or <code>null</code>
     * @param force <code>true</code> if this controller must ensure
     * that all properties are correct, <code>false</code> if some
     * optimations are allowed. Clients normally can set this argument
     * to <code>false</code>.
     */
    public void setFocusedDockable( Dockable focusedDockable, boolean force ) {
        setFocusedDockable( focusedDockable, force, true );
    }

    /**
     * Sets the {@link Dockable} which should have the focus.
     * @param focusedDockable the element with the focus or <code>null</code>
     * @param force <code>true</code> if this controller must ensure
     * that all properties are correct, <code>false</code> if some
     * optimations are allowed. Clients normally can set this argument
     * to <code>false</code>.
     * @param ensureFocusSet whether to ensure that the focus is set correctly
     * or not.
     */
    public void setFocusedDockable( Dockable focusedDockable, boolean force, boolean ensureFocusSet ) {
    	// ignore more than one call
    	if( onFocusing )
    		return;
    	
    	try{
	        onFocusing = true;
	        
	        if( force || this.focusedDockable != focusedDockable ){
	            this.focusedDockable = focusedDockable;
	            
	            for( Map.Entry<DockTitle, Dockable> title : activeTitles.entrySet() ){
	                DockStation parent = title.getValue().getDockParent();
	                if( parent != null )
	                    parent.changed( title.getValue(), title.getKey(), false );
	                else
	                    title.getKey().changed( new DockTitleEvent( title.getValue(), false ));
	            }
	            
	            activeTitles.clear();
	            Dockable dockable = focusedDockable;
	            
	            while( dockable != null ){
	                DockStation station = dockable.getDockParent();
	                if( station != null ){
	                    DockTitle[] titles = station.getDockTitles( dockable );
	                    
	                    for( DockTitle title : titles ){
	                        station.changed( dockable, title, true );
	                        activeTitles.put( title, dockable );
	                    }
	                    
	                    dockable = station.asDockable();
	                }
	                else
	                    dockable = null;
	            }
	            
	            if( ensureFocusSet )
	                ensureFocusSet();
	            
	            firedockableFocused( focusedDockable );
	        }
    	}
    	finally{
    		onFocusing = false;
    	}
    }
    
    /**
     * Tells whether <code>dockable</code> or one of its children
     * has currently the focus.
     * @param dockable the element which may have the focus
     * @return <code>true</code> if <code>dockable</code> or
     * one of its children is focused
     */
    public boolean isFocused( Dockable dockable ){
        Dockable temp = focusedDockable;
        while( temp != null ){
            if( temp == dockable )
                return true;
            
            DockStation station = temp.getDockParent();
            temp = station == null ? null : station.asDockable();
        }
        return false;
    }
    
    /**
     * Ensures that a title or a {@link Component} of the currently
     * {@link #getFocusedDockable() focused Dockable} really
     * has the focus.
     */
    public void ensureFocusSet(){
        if( focusedDockable != null ){
            Stack<Dockable> front = new Stack<Dockable>();            
            
            Dockable temp = focusedDockable;
            
            while( temp != null ){
                DockStation parent = temp.getDockParent();
                if( parent != null )
                    front.push( temp );
                
                temp = parent == null ? null : parent.asDockable();
            }
            
            while( !front.isEmpty() ){
                Dockable element = front.pop();
                element.getDockParent().setFrontDockable( element );
            }
        
            DockTitle[] titles = getBindedTitlesOf( focusedDockable );
            Component focused = FocusManager.getCurrentManager().getFocusOwner();
            if( focused != null ){
                if( SwingUtilities.isDescendingFrom( focused, focusedDockable.getComponent() ) )
                    return;
                
                for( DockTitle title : titles )
                    if( SwingUtilities.isDescendingFrom( focused, title.getComponent() ))
                        return;
            }
            
            Component component = focusedDockable.getComponent();
            if( component.isFocusable() ){
                component.requestFocus();
            }
            else{
                KeyboardFocusManager.getCurrentKeyboardFocusManager().focusNextComponent( component );
            }
        }
    }
    
    /**
     * Gets the {@link Dockable} which is currently focused.
     * @return the focused element or <code>null</code>
     */
    public Dockable getFocusedDockable() {
        return focusedDockable;
    }
    
    /**
     * Gets a list of titles which are binded to <code>dockable</code>. Note
     * that this method only returns the titles managed by this controller.
     * @param dockable the owner of some titles
     * @return a list of titles
     */
    public DockTitle[] getBindedTitlesOf( Dockable dockable ){
        if( dockable.getDockParent() == null )
            return new DockTitle[0];
        else
            return dockable.getDockParent().getDockTitles( dockable );
    }
    
    /**
     * Gets the manager of all titles on this controller
     * @return the manager
     */
    public DockTitleManager getDockTitleManager() {
		return dockTitles;
	}
    
    /**
     * Gest the number of stations registered at this controller.
     * @return the number of stations
     * @see #add(DockStation)
     */
    public int getStationCount(){
        return stations.size();
    }
    
    /**
     * Gets the station at the specified position.
     * @param index the location
     * @return the station
     */
    public DockStation getStation( int index ){
        return stations.get( index );
    }
    
    /**
     * Adds <code>guard</code> to this controller. The new 
     * {@link ActionGuard} has no influence on 
     * {@link DockActionSource DockActionSources} which are already
     * created.
     * @param guard the new guard
     */
    public void addActionGuard( ActionGuard guard ){
        if( guard == null )
            throw new IllegalArgumentException( "guard must not be null" );
        
        guards.add( guard );
    }
    
    /**
     * Removes <code>guard</code> from this controller.
     * @param guard the element to remove
     */
    public void removeActionGuard( ActionGuard guard ){
        guards.remove( guard );
    }
    
    /**
     * Creates a list of {@link DockAction DockActions} which can 
     * affect {@link Dockable}.
     * @param dockable a Dockable whose actions are demanded
     * @return a list of actions
     */
    public DockActionSource listOffers( Dockable dockable ){
        List<DockActionSource> guards = new ArrayList<DockActionSource>();
        List<DockActionSource> parents = new ArrayList<DockActionSource>();
        
        DockStation station = dockable.getDockParent();
        
        while( station != null ){
            parents.add( station.getIndirectActionOffers( dockable ) );
            
            Dockable transform = station.asDockable();
            if( transform != null )
                station = transform.getDockParent();
            else
                station = null;
        }
        
        for( ActionGuard guard : this.guards ){
            if( guard.react( dockable ))
                guards.add( guard.getSource( dockable ) );
        }        
        
        ActionOffer offer = null;
        
        for( ActionOffer temp : actionOffers )
            if( temp.interested( dockable )){
                offer = temp;
                break;
            }
        
        if( offer == null )
            offer = defaultActionOffer;

        DockActionSource parentSource = null;
        
        if( dockable.getDockParent() != null )
        	parentSource = dockable.getDockParent().getDirectActionOffers( dockable );
        
        return offer.getSource( dockable, dockable.getActionOffers(), guards.toArray( new DockActionSource[guards.size()] ),
        		parentSource, parents.toArray( new DockActionSource[ parents.size() ] ));
    }
        
    /**
     * Gets a window which shows a title of <code>dockable</code>. The
     * title on the window will be binded to <code>dockable</code>.
     * @param dockable the Dockable for which a title should be shown
     * @param title a title which is grabbed by the mouse, can be <code>null</code>
     * @return a window or <code>null</code>
     */
    private TitleWindow getTitleWindow( Dockable dockable, DockTitle title ){
        MovingTitleGetter movingTitleGetter = getTheme().getMovingTitleGetter( this );
        
        if( title == null )
            title = movingTitleGetter.get( this, dockable );
        else
            title = movingTitleGetter.get( this, title );
        
        if( title == null )
            return null;
        
    	Window parent = SwingUtilities.getWindowAncestor( dockable.getComponent() );
        TitleWindow window = new TitleWindow( parent, title );
        window.pack();
        return window;
    }
    
    /**
     * Adds an observer to this controller.
     * @param listener the observer
     */
    public void addDockControllerListener( DockControllerListener listener ){
        listeners.add( listener );
    }
    
    /**
     * Removes an observer from this controller.
     * @param listener the observer to remove
     */
    public void removeDockControllerListener( DockControllerListener listener ){
        listeners.remove( listener );
    }
    
    /**
     * Lists all {@link DockControllerListener} of this station. The list is
     * independent from the original list.
     * @return the list of listeners
     */
    protected DockControllerListener[] listDockControllerListener(){
        return listeners.toArray( new DockControllerListener[ listeners.size() ]);
    }

    /**
     * Informs all listeners that a {@link Dockable} will be registered.
     * @param dockable the Dockable which will be registered
     */
    protected void fireDockableRegistering( Dockable dockable ){
        for( DockControllerListener listener : listDockControllerListener() )
            listener.dockableRegistering( this, dockable );
    }
    
    /**
     * Informs all listeners that a {@link Dockable} has been registered.
     * @param dockable the registered Dockable
     */
    protected void fireDockableRegistered( Dockable dockable ){
        for( DockControllerListener listener : listDockControllerListener() )
            listener.dockableRegistered( this, dockable );
    }

    /**
     * Informs all listeners that a {@link Dockable} has been
     * unregistered.
     * @param dockable the unregistered Dockable
     */
    protected void fireDockableUnregistered( Dockable dockable ){
        for( DockControllerListener listener : listDockControllerListener() )
            listener.dockableUnregistered( this, dockable );
    }

    /**
     * Informs all listeners that <code>station</code> will be registered.
     * @param station the new station
     */
    protected void fireDockStationRegistering( DockStation station ){
        for( DockControllerListener listener : listDockControllerListener() )
            listener.dockStationRegistering( this, station );
    }
    
    /**
     * Informs all listeners that <code>station</code> has been registered.
     * @param station the new station
     */
    protected void fireDockStationRegistered( DockStation station ){
        for( DockControllerListener listener : listDockControllerListener() )
            listener.dockStationRegistered( this, station );
    }
    
    /**
     * Informs all listeners that <code>station</code> has been unregistered.
     * @param station the unregistered station
     */
    protected void fireDockStationUnregistered( DockStation station ){
        for( DockControllerListener listener : listDockControllerListener() )
            listener.dockStationUnregistered( this, station );
    }
    
    /**
     * Informs all listeners that <code>title</code> has been binded
     * to <code>dockable</code>.
     * @param title the binded title
     * @param dockable the owner of <code>title</code>
     */
    protected void fireTitleBinded( DockTitle title, Dockable dockable ){
        for( DockControllerListener listener : listDockControllerListener() )
            listener.titleBinded( this, title, dockable );
    }
    
    /**
     * Informs all listeners that <code>title</code> is no longer binded
     * to <code>dockable</code>.
     * @param title the unbinded title
     * @param dockable the former owner of <code>title</code>
     */
    protected void fireTitleUnbinded( DockTitle title, Dockable dockable ){
        for( DockControllerListener listener : listDockControllerListener() )
            listener.titleUnbinded( this, title, dockable );
    }
    
    /**
     * Informs all listeners that <code>dockable</code> will be dragged.
     * @param dockable the dragged Dockable
     * @param station the parent of <code>dockable</code>
     */
    protected void fireDockableDrag( Dockable dockable, DockStation station ){
        for( DockControllerListener listener : listDockControllerListener() )
            listener.dockableDrag( this, dockable, station );
    }
    
    /**
     * Informs all listeners that <code>dockable</code> was dropped on
     * <code>station</code>.
     * @param dockable the dropped Dockable
     * @param station the new owner of <code>dockable</code>
     */
    protected void fireDockablePut( Dockable dockable, DockStation station ){
        for( DockControllerListener listener : listDockControllerListener() )
            listener.dockablePut( this, dockable, station );
    }
    
    /**
     * Informs all listeners that <code>dockable</code> has gained
     * the focus.
     * @param dockable the owner of the focus, may be <code>null</code>
     */
    protected void firedockableFocused( Dockable dockable ){
        for( DockControllerListener listener : listDockControllerListener() )
            listener.dockableFocused( this, dockable );
    }
    
    /**
     * Invoked by the listeners of a title to start a drag and drop operation.
     * @param e the initializing event
     * @param title the grabbed title, can be <code>null</code> if
     * <code>dockable</code> is not <code>null</code>
     * @param dockable the grabbed Dockable, can be <code>null</code>
     * if <code>title</code> is not <code>null</code>
     */
    protected void dragMousePressed( MouseEvent e, DockTitle title, Dockable dockable ) {
        if( dockable == null )
            dockable = title.getDockable();
        
        if( dockable.getDockParent() == null )
            return;
        
        if( pressPoint == null && e.getButton() == MouseEvent.BUTTON1){
            pressPoint = e.getPoint();
        }
        else if( pressPoint != null ){
            titleDragCancel();
        }
        e.consume();
    }
    
    /**
     * Invoked while the user drags a title or Dockable.
     * @param e the initializing event
     * @param title the grabbed title, can be <code>null</code> if
     * <code>dockable</code> is not <code>null</code>
     * @param dockable the grabbed Dockable, can be <code>null</code>
     * if <code>title</code> is not <code>null</code>
     */
    protected void dragMouseDragged( MouseEvent e, DockTitle title, Dockable dockable ) {
        if( pressPoint == null )
            return;
        
        if( dockable == null )
            dockable = title.getDockable();
        
        if( dockable.getDockParent() == null )
            return;
        
        e.consume();
        Point mouse = e.getPoint();
        SwingUtilities.convertPointToScreen( mouse, e.getComponent() );
        
        if( !onMove ){
            // not yet free
            if( !dockable.getDockParent().canDrag( dockable ))
                return;
            
            int distance = Math.abs( e.getX() - pressPoint.x ) + Math.abs( e.getY() - pressPoint.y );
            if( distance >= dragDistance ){
                
                movingTitleWindow = getTitleWindow( dockable, title );
                if( movingTitleWindow != null ){
                    updateTitleWindowPosition( mouse );
                    movingTitleWindow.setVisible( true );
                }
                
                onMove = true;
            }
        }
        else{
            if( movingTitleWindow != null )
                updateTitleWindowPosition( mouse );
            
            DockStation next = preparePut( 
                    mouse.x, mouse.y,
                    mouse.x - pressPoint.x, mouse.y - pressPoint.y,
                    dockable );
            
            if( next != null ){
                next.draw();
            }
            
            if( next != dragStation ){
                if( dragStation != null ){
                    dragStation.forget();
                }
                dragStation = next;
            }
        }
    }
    
    /**
     * Updates the location of the {@link #movingTitleWindow} according
     * to the current location of the mouse.
     * @param mouse the location of the mouse
     */
    private void updateTitleWindowPosition( Point mouse ){
        int width = movingTitleWindow.getWidth();
        int height = movingTitleWindow.getHeight();
        
        int delta = Math.min( width, height ) + 1;
        
        int px = Math.min( pressPoint.x, width );
        int py = Math.min( pressPoint.y, height );
        
        movingTitleWindow.setLocation( mouse.x - px + delta, mouse.y - py + delta );
    }
    
    /**
     * Invoked while the user drags a title or Dockable and releases a mouse
     * button.
     * @param e the initializing event
     * @param title the grabbed title, can be <code>null</code> if
     * <code>dockable</code> is not <code>null</code>
     * @param dockable the grabbed Dockable, can be <code>null</code>
     * if <code>title</code> is not <code>null</code> 
     */
    protected void dragMouseReleased( MouseEvent e, DockTitle title, Dockable dockable ) {
        if( !onMove ){
            titleDragCancel();
            return;
        }
        
        if( dockable == null )
            dockable = title.getDockable();
        
        if( dockable.getDockParent() == null )
            return;
        
        e.consume();
        
        if( dockable == null )
            dockable = title.getDockable();
        
        if( dragStation != null ){
            Point mouse = e.getPoint();
            SwingUtilities.convertPointToScreen( mouse, e.getComponent() );
            
            executePut( dockable, dragStation );
            dragStation.forget();
            dragStation = null;
        }
        
        if( movingTitleWindow != null )
            movingTitleWindow.close();
        movingTitleWindow = null;
        pressPoint = null;
        onMove = false;
    }
    
    /**
     * Cancels a drag and drop operation.
     */
    private void titleDragCancel(){
        if( dragStation != null ){
            dragStation.forget();
            dragStation = null;
        }
        
        if( movingTitleWindow != null )
            movingTitleWindow.dispose();
        movingTitleWindow = null;
        pressPoint = null;
        onMove = false;
    }
    
    /**
     * Observers this controller and registers listeners to all new titles.
     */
    private class TitleListener extends DockAdapter{
        /** a map telling which listener was added to which title */
        private Map<DockTitle, MouseTitleListener> listeners =
            new HashMap<DockTitle, MouseTitleListener>();
        
        /**
         * Constructs a new listener
         */
        public TitleListener(){
        	// do nothing
        }
        
        /**
         * Tells whether title is binded to its {@link Dockable} or not.
         * @param title the title whose state is searched
         * @return the state
         */
        public boolean isBinded( DockTitle title ){
            return listeners.containsKey( title );
        }
        
        @Override
        public void titleBinded( Dockable dockable, DockTitle title ) {
            if( !listeners.containsKey( title )){
                MouseTitleListener listener = new MouseTitleListener( title );
                listeners.put( title, listener );
            
                title.addMouseInputListener( listener );
            }
            
            title.bind();
            fireTitleBinded( title, dockable );
            
            DockStation station = dockable.getDockParent();
            boolean focused = false;
            Dockable temp = focusedDockable;
            while( !focused && temp != null ){
                focused = temp == dockable;
                DockStation parent = temp.getDockParent();
                temp = parent == null ? null : parent.asDockable();
            }
            
            if( station == null )
                title.changed( new DockTitleEvent( dockable, focused ));
            else
                station.changed( dockable, title, focused );
            
            if( focused )
                activeTitles.put( title, dockable );
        }
        
        @Override
        public void titleUnbinded( DockController controller, DockTitle title, Dockable dockable ) {
            activeTitles.remove( title );
            DockStation parent = dockable.getDockParent();
            if( parent != null )
                parent.changed( dockable, title, false );
            else
                title.changed( new DockTitleEvent( dockable, false ));
        }
        
        @Override
        public void titleUnbinded( Dockable dockable, DockTitle title ) {
            MouseTitleListener listener = listeners.remove( title );
            if( listener != null ){
                title.removeMouseInputListener( listener );
            }
            
            title.unbind();
            fireTitleUnbinded( title, dockable );
        }

        
        @Override
        public void dockableRegistered( DockController controller, Dockable dockable ) {
            DockStation parent = dockable.getDockParent();
            if( parent != null ){
                DockTitle[] titles = parent.getDockTitles( dockable );
                for( DockTitle title : titles ){
                    if( !listeners.containsKey( title )){
                        MouseTitleListener listener = new MouseTitleListener( title );
                        listeners.put( title, listener );
                    
                        title.addMouseInputListener( listener );
                        title.bind();
                        fireTitleBinded( title, dockable );
                    }
                }
            }
        }

        @Override
        public void dockableUnregistered( DockController controller, Dockable dockable ) {
            // The station itself should unbind the titles, however, ensure
            // that there are no listeners remaining
            DockStation parent = dockable.getDockParent();
            if( parent != null ){
                DockTitle[] titles = parent.getDockTitles( dockable );
                for( DockTitle title : titles ){
                    if( listeners.containsKey( title )){
                        MouseInputListener listener = listeners.remove( title );
                        title.removeMouseInputListener( listener );
                        
                        title.unbind();
                        fireTitleUnbinded( title, dockable );
                    }
                }
            }
        }
        
        /**
         * A {@link MouseListener} which is added to a {@link DockTitle}. This
         * listener informs a controller as soon as the mouse grabs the
         * title.
         * @author Benjamin Sigg
         */
        private class MouseTitleListener extends MouseInputAdapter{
            /** the observed title */
            private DockTitle title;
            
            /**
             * Creates a new listener
             * @param title the title to observe
             */
            public MouseTitleListener( DockTitle title ){
                this.title = title;
            }
            
            @Override
            public void mousePressed( MouseEvent e ){
                if( e.isConsumed() )
                    return;
                dragMousePressed( e, title, null );
            }
            @Override
            public void mouseReleased( MouseEvent e ) {
                if( e.isConsumed() )
                    return;
                dragMouseReleased( e, title, null );
            }
            @Override
            public void mouseDragged( MouseEvent e ) {
                if( e.isConsumed() )
                    return;
                dragMouseDragged( e, title, null );
            }
        }
    }
    
    /**
     * A window which shows a single {@link DockTitle}.
     * @author Benjamin Sigg
     */
    private class TitleWindow extends JWindow{
        /** the title to display */
        private DockTitle title;
        /** whether the title was already binded when this window was constructed */
        private boolean binded;
        
        /**
         * Constructs a new window
         * @param parent the parent of the window
         * @param title the title to show, may be binded
         */
        public TitleWindow( Window parent, DockTitle title ){
            super( parent );
            
            Container content = getContentPane();
            content.setLayout( new GridLayout( 1, 1 ));
            setFocusableWindowState( false );
            
            try{
                setAlwaysOnTop( true );
            }
            catch( SecurityException ex ){
                // ignore
            }
            
            binded = titleListener.isBinded( title );

            if( binded && title.getOrigin() != null ){
                DockTitleVersion origin = title.getOrigin();
                DockTitle replacement = title.getDockable().getDockTitle( origin );
                if( replacement != null ){
                    replacement.setOrientation( title.getOrientation() );
                    title = replacement;
                    binded = false;
                }
            }
            
            if( !binded ){
                title.getDockable().bind( title );
                title.changed( new DockTitleEvent( title.getDockable(), true ));
                content.add( title.getComponent() );
            }
            else{
                /* TODO find a way to use the preferred size */
                Component c = title.getComponent();
                final Dimension size = c.getSize();
                final Image image = new BufferedImage( size.width, size.height, BufferedImage.TYPE_INT_ARGB );
                Graphics graphics = image.getGraphics();
                c.paint( graphics );
                graphics.dispose();
                
                JComponent ground = new JComponent(){
                    @Override
                    public void paint( Graphics g ){
                        g.drawImage( image, 0, 0, this );
                        /*Component c = TitleWindow.this.title.getComponent();
                        Dimension size = c.getSize();
                        c.setSize( getWidth(), getHeight() );
                        c.validate();
                        c.paint( g );
                        c.setSize( size );
                        c.validate();*/
                    }
                    
                    @Override
                    public Dimension getPreferredSize() {
                        return size;
                        //return TitleWindow.this.title.getComponent().getPreferredSize();
                    }
                };
                
                content.add( ground );
            }
            
            this.title = title;
        }
        
        /**
         * Gets the title which is painted on this window
         * @return the title
         */
        public DockTitle getTitle() {
            return title;
        }
        
        /**
         * Closes this window and ensures that the title has the same
         * binding-state as it had at the time when this window was
         * constructed. 
         */
        public void close(){
            dispose();
            
            if( !binded ){
                Dockable dockable = title.getDockable();
                dockable.unbind(title);
            }
        }
    }
    
    /**
     * A listener to this controller. Adds a {@link MouseListener} to all
     * {@link Dockable Dockables}. This second listener allows a popup-menu
     * and connects the Dockables to the drag and drop mechanism.
     */
    private class MouseDockableListener extends DockAdapter{
        /** tells which Dockable has which listener */
        private Map<Dockable, SingleMouseDockableListener> listeners =
            new HashMap<Dockable, SingleMouseDockableListener>();
        
        @Override
        public void dockableRegistered( DockController controller, Dockable dockable ) {
            if( !listeners.containsKey( dockable )){
                SingleMouseDockableListener listener = new SingleMouseDockableListener( dockable );
                dockable.addMouseInputListener( listener );
                listeners.put( dockable, listener );
            }
        }
        
        @Override
        public void dockableUnregistered( DockController controller, Dockable dockable ) {
            SingleMouseDockableListener listener = listeners.remove( dockable );
            if( listener != null ){
                dockable.removeMouseInputListener( listener );
            }
        }
        
        /**
         * A listener to a Dockable, shows a popup menu or lets the user
         * drag and drop a Dockable.
         * @author Benjamin Sigg
         */
        private class SingleMouseDockableListener extends ActionPopup{
            /** the observed element */
            private Dockable dockable;
            
            /**
             * Constructs a new listener
             * @param dockable the Dockable to observe
             */
            public SingleMouseDockableListener( Dockable dockable ){
                super( true );
                this.dockable = dockable;
            }
            
            @Override
            protected Dockable getDockable() {
                return dockable;
            }

            @Override
            protected DockActionSource getSource() {
                return listOffers( dockable );
            }

            @Override
            protected boolean isEnabled() {
                return true;
            }
            
            @Override
            public void mousePressed( MouseEvent e ) {
                if( !onMove )
                    super.mousePressed(e);
                
                if( !e.isConsumed() ){
                	if( !dragOnlyTitel )
                		dragMousePressed( e, null, dockable );
                }
            }
            @Override
            public void mouseDragged( MouseEvent e ) {
                if( !e.isConsumed() ){
                	if( !dragOnlyTitel )
                		dragMouseDragged( e, null, dockable );
                }
            }
            @Override
            public void mouseReleased( MouseEvent e ) {
                if( !onMove )    
                    super.mouseReleased(e);
                
                if( !e.isConsumed() ){
                	if( !dragOnlyTitel )
                		dragMouseReleased( e, null, dockable );
                }
            }
        }
    }
    
    /**
     * A listener of this controller. Ensures that stations and
     * dockables are known to the controller even if the tree of elements
     * is changed.
     * @author Benjamin Sigg
     */
    private class StationListener extends DockAdapter{
        /** a set of Dockables which were removed during a drag and drop operation */
        private Set<Dockable> removedOnPut = new HashSet<Dockable>();
        /** a set of Dockable which were added during a drag and drop operation */
        private Set<Dockable> addedOnPut = new HashSet<Dockable>();
        
        @Override
        public void dockablePut( DockController controller, Dockable dockable, DockStation station ) {
            for( Dockable d : removedOnPut )
                removeDockable( d );
            
            for( Dockable d : addedOnPut )
                addDockable( d );
            
            removedOnPut.clear();
            addedOnPut.clear();
        }
        
        @Override
        public void dockableAdding( DockStation station, Dockable dockable ) {
            dockable.setDockParent( station );
            
            if( isOnPut() ){
                DockUtilities.visit( dockable, new DockUtilities.DockVisitor(){
                    @Override
                    public void handleDockable( Dockable dockable ) {
                        addedOnPut.add( dockable );
                        removedOnPut.remove( dockable );
                    }
                });
            }
            else{
                addDockable( dockable );
            }
        }
        
        /**
         * Adds a Dockable either as station or as pure Dockable to this
         * controller.
         * @param dockable the Dockable to register
         */
        private void addDockable( Dockable dockable ){
            DockStation asStation = dockable.asDockStation();
            
            if( asStation != null )
                add( asStation );
            else
                register( dockable );
        }

        @Override
        public void dockableAdded( DockStation station, Dockable dockable ){
            if( !isOnPut() ){
                if( dockable == focusedDockable || focusedDockable == null )
                    if( station.isVisible( dockable ))
                        setFocusedDockable( dockable, true );
            }
        }
        
        @Override
        public void dockableRemoving( DockStation station, Dockable dockable ) {
            if( isOnPut() ){
                DockUtilities.visit( dockable, new DockUtilities.DockVisitor(){
                    @Override
                    public void handleDockable( Dockable dockable ) {
                        addedOnPut.remove( dockable );
                        removedOnPut.add( dockable );
                    }
                });
            }
        }
        
        @Override
        public void dockableRemoved( DockStation station, Dockable dockable ) {
            dockable.setDockParent( null );
            
            if( !isOnPut() ){
                removeDockable( dockable );
            }
        }
        
        /**
         * Removes a Dockable either as station or as pure Dockable from
         * this controller.
         * @param dockable the Dockable to unregister
         */
        private void removeDockable( Dockable dockable ){
            DockStation asStation = dockable.asDockStation();
            
            if( asStation != null )
                remove( asStation );
            else
                unregister( dockable );
        }
        
        @Override
        public void dockableVisibiltySet( DockStation station, Dockable dockable, boolean visible ){
            if( !onFocusing && !visible && isFocused( dockable ) ){
            	DockStation parent = dockable.getDockParent();
            	while( parent != null ){
            		dockable = parent.asDockable();
            		if( dockable != null ){
            			parent = dockable.getDockParent();
            			if( parent != null ){
            				if( parent.isVisible( dockable )){
            					setFocusedDockable( dockable, false );
            					return;
            				}
            			}
            		}
            		else
            			break;
            	}
            	
                setFocusedDockable( null, false );
            }
        }
    }
}
