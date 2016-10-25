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
package bibliothek.gui.dock.common.intern;

import java.awt.Component;
import java.awt.Dimension;
import java.util.HashMap;
import java.util.Map;

import javax.swing.SwingUtilities;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.CFocusHistory;
import bibliothek.gui.dock.common.CGrid;
import bibliothek.gui.dock.common.CLocation;
import bibliothek.gui.dock.common.CStation;
import bibliothek.gui.dock.common.ColorMap;
import bibliothek.gui.dock.common.EnableableItem;
import bibliothek.gui.dock.common.FontMap;
import bibliothek.gui.dock.common.action.CAction;
import bibliothek.gui.dock.common.event.CDockableLocationListener;
import bibliothek.gui.dock.common.event.CDockablePropertyListener;
import bibliothek.gui.dock.common.event.CDockableStateListener;
import bibliothek.gui.dock.common.event.CDoubleClickListener;
import bibliothek.gui.dock.common.event.CFocusListener;
import bibliothek.gui.dock.common.event.CKeyboardListener;
import bibliothek.gui.dock.common.event.CVetoClosingEvent;
import bibliothek.gui.dock.common.event.CVetoClosingListener;
import bibliothek.gui.dock.common.grouping.DockableGrouping;
import bibliothek.gui.dock.common.intern.action.CloseActionSource;
import bibliothek.gui.dock.common.intern.station.CommonDockStation;
import bibliothek.gui.dock.common.layout.RequestDimension;
import bibliothek.gui.dock.common.location.CExtendedModeLocation;
import bibliothek.gui.dock.common.mode.CLocationMode;
import bibliothek.gui.dock.common.mode.CLocationModeManager;
import bibliothek.gui.dock.common.mode.ExtendedMode;
import bibliothek.gui.dock.control.focus.DefaultFocusRequest;
import bibliothek.gui.dock.control.focus.FocusRequest;
import bibliothek.gui.dock.disable.DisablingStrategy;
import bibliothek.gui.dock.event.VetoableDockFrontendEvent;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.util.Filter;
import bibliothek.util.FrameworkOnly;
import bibliothek.util.Todo;
import bibliothek.util.Todo.Compatibility;
import bibliothek.util.Todo.Priority;
import bibliothek.util.Todo.Version;


/**
 * An abstract implementation of {@link CDockable}. Contains methods to 
 * work with listeners and with {@link CAction}s.
 * @author Benjamin Sigg
 */
public abstract class AbstractCDockable implements CDockable {
    /** the location of this dockable */
    private CLocation location = null;
    
    /** the graphical representation of this dockable */
    private CommonDockable dockable;

    /** the preferred parent of this dockable */
    private CStation<?> workingArea;
    
    /** the control managing this dockable */
    private CControlAccess control;
    
    /** unique id of this {@link CDockable} */
    private String uniqueId;
    
    /** whether this element likes to have the same height all the time */
    private boolean resizeLockedVertically = false;
    
    /** whether this element likes to have the same width all the time */
    private boolean resizeLockedHorizontally = false;
    
    /** whether to remain visible when minimized and unfocused or not */
    private boolean sticky = false;
    
    /** whether {@link #sticky} can be switched by the user */
    private boolean stickySwitchable = true;
    
    /** the preferred size when minimized */
    private Dimension minimizeSize = new Dimension( -1, -1 );
    
    /** the preferred size of this {@link CDockable} */
    private RequestDimension resizeRequest;
    
    /** the colors associated with this dockable */
    private ColorMap colors = new ColorMap( this );
    
    /** the fonts associated with this dockable */
    private FontMap fonts = new FontMap( this );
    
    /** the actions that are shown by other modules */
    private Map<String, CAction> actions = new HashMap<String, CAction>();
    
    /** whether the {@link DockTitle} should not be created */
    private boolean titleShown = true;
    
    /** whether a single tab is shown */
    private boolean singleTabShown = false;
    
    /** the listeners that were added to this dockable */
    protected CListenerCollection listenerCollection = new CListenerCollection();
    
    /** handles the {@link CDockableLocationListener}s */
    private CDockableLocationListenerManager locationListenerManager;
    
    /** support class to fire {@link CVetoClosingEvent}s */
    private ControlVetoClosingListener vetoClosingListenerConverter;
    
    /** Source that contains the action that closes this dockable */
    private CloseActionSource close = new CloseActionSource( this );
    
    /** The default locations for the available {@link ExtendedMode}s. */
    private Map<ExtendedMode, CLocation> defaultLocations = new HashMap<ExtendedMode, CLocation>( 4 );
    
    /** The component which should be focused */
    private Component focusComponent;
    
    /** All the items that are enabled */
    private int enabled = EnableableItem.ALL.getFlag();
    
    /** Tells how this {@link CDockable} tries to automatically group itself with other dockables */
    private DockableGrouping grouping;
    
    /**
     * Creates a new dockable
     */
    protected AbstractCDockable(){
    	// nothing
    }
    
    @Override
    public String toString(){
    	return getClass().getSimpleName() + "[unique id=" + uniqueId + "]";
    }
    
    /**
     * Creates the {@link CommonDockable} that is associated with this dockable, called the first
     * time the {@link CommonDockable} is required for an operation.
     * @return the new dockable
     */
    protected abstract CommonDockable createCommonDockable();
    
    /**
     * Initializes this CDockable.
     * @param dockable the representation of this <code>CDockable</code>, not <code>null</code>
     */
    protected void init( CommonDockable dockable ){
        if( this.dockable != null )
            throw new IllegalStateException( "dockable already set" );
        if( dockable == null )
            throw new NullPointerException( "dockable is null" );
        
        this.dockable = dockable;
    }
    
    /**
     * Gets the action source which might show a single action that closes
     * this dockable.
     * @return the close source
     */
    protected CloseActionSource getClose(){
        return close;
    }
    
    /**
     * Gets access to the controller.
     * @return access or <code>null</code>
     */
    protected CControlAccess control(){
        return control;
    }
    
    public void addCDockableStateListener( CDockableStateListener listener ){
        listenerCollection.addCDockableStateListener( listener );
    }
    
    public void addCDockablePropertyListener( CDockablePropertyListener listener ) {
        listenerCollection.addCDockablePropertyListener( listener );
    }
    
    public void addCDockableLocationListener( CDockableLocationListener listener ){
    	if( locationListenerManager == null ){
    		locationListenerManager = new CDockableLocationListenerManager( this );
    	}
    	boolean has = listenerCollection.hasCDockableLocationListeners();
	    listenerCollection.addCDockableLocationListener( listener );	
	    if( !has ){
	    	locationListenerManager.setListener( listenerCollection.getCDockableLocationListener() );
	    }
    }
    
    public void removeCDockableStateListener( CDockableStateListener listener ){
        listenerCollection.removeCDockableStateListener( listener );
        if( locationListenerManager != null && !listenerCollection.hasCDockableLocationListeners() ){
        	locationListenerManager.setListener( null );
        }
    }
    
    public void removeCDockablePropertyListener( CDockablePropertyListener listener ) {
        listenerCollection.removeCDockablePropertyListener( listener );
    }
    
    public void removeCDockableLocationListener( CDockableLocationListener listener ){
    	listenerCollection.removeCDockableLocationListener( listener );
    }
    
    public void addFocusListener( CFocusListener listener ){
        listenerCollection.addFocusListener( listener );
    }
    
    public void removeFocusListener( CFocusListener listener ){
        listenerCollection.removeFocusListener( listener );
    }
    
    public void addKeyboardListener( CKeyboardListener listener ){
        listenerCollection.addKeyboardListener( listener );
    }
    
    public void removeKeyboardListener( CKeyboardListener listener ){
        listenerCollection.removeKeyboardListener( listener );
    }
    
    public void addDoubleClickListener( CDoubleClickListener listener ){
        listenerCollection.addDoubleClickListener( listener );
    }
    
    public void removeDoubleClickListener( CDoubleClickListener listener ){
        listenerCollection.removeDoubleClickListener( listener );
    }
    
    public void addVetoClosingListener( CVetoClosingListener listener ){
    	boolean empty = !listenerCollection.hasVetoClosingListeners();
	    listenerCollection.addVetoClosingListener( listener );
	    if( empty && control != null ){
	    	control.getOwner().intern().addVetoableListener( getVetoClosingListenerConverter() );
	    }
    }
    
    public void removeVetoClosingListener( CVetoClosingListener listener ){
	    listenerCollection.removeVetoClosingListener( listener );
	    if( !listenerCollection.hasVetoClosingListeners() ){
	    	if( control != null && vetoClosingListenerConverter != null ){
	    		control.getOwner().intern().removeVetoableListener( vetoClosingListenerConverter );
	    		vetoClosingListenerConverter = null;
	    	}
	    }
    }
    
    private ControlVetoClosingListener getVetoClosingListenerConverter(){
    	if( vetoClosingListenerConverter == null ){
    		vetoClosingListenerConverter = new ControlVetoClosingListener( control.getOwner(), listenerCollection.getVetoClosingListener() ){
    			@Override
    			protected CDockable[] getCDockables( VetoableDockFrontendEvent event ){
    				for( Dockable dockable : event ){
    					if( dockable == intern() ){
    						return new CDockable[]{ AbstractCDockable.this };
    					}
    				}
    				return null;
    			}
    		};
    	}
		return vetoClosingListenerConverter;
	}
    
    /**
     * Gets the list of state listeners.
     * @return the stateListeners
     * @deprecated subclasses should use {@link CListenerCollection#getCDockableStateListener()}
     * of {@link #listenerCollection} if they want to fire an event
     */
    @Deprecated
    protected CDockableStateListener[] stateListeners(){
        return listenerCollection.getCDockableStateListeners();
    }
    
    /**
     * Gets the list of property listeners.
     * @return the stateListeners
     * @deprecated subclasses should use {@link CListenerCollection#getCDockablePropertyListener()}
     * of {@link #listenerCollection} if they want to fire an event
     */
    @Deprecated
    protected CDockablePropertyListener[] propertyListeners(){
        return listenerCollection.getCDockablePropertyListeners();
    }
    
    public void setVisible( boolean visible ){
        if( control == null )
            throw new IllegalStateException( "This CDockable does not know its CControl. Call CControl.addDockable(...) to connect this CDockable befor calling setVisible(...)." );
        
        if( visible ){
            control.show( this );
        }
        else{
            control.hide( this );
        }
    }
    
    public boolean isVisible(){
        if( control == null )
            return false;
        else
            return control.isVisible( this );
    }
    
    public boolean hasParent(){
	    if( control == null ){
	    	return false;
	    }
	    else{
	    	return control.hasParent( this );
	    }
    }
    
    @Deprecated
    @Todo( compatibility=Compatibility.BREAK_MAJOR, priority=Priority.ENHANCEMENT, target=Version.VERSION_1_1_3, description="remove this method" )
    public boolean isDockableVisible(){
    	return intern().isDockableShowing();
    }
    
    public boolean isShowing(){
    	return isDockableVisible();
    }
    
    /**
     * Tries to focus this dockable. In order to gain focus this dockable must at least be visible, additional
     * restrictions exist, like gaining focus takes some time during which no other dockable must ask for the
     * focus. This method is best used to highlight existing dockables, but not while building 
     * a new layout (look at methods like {@link CGrid#select(double, double, double, double, CDockable) CGrid.select} 
     * to select a dockable in a stack while building a layout).<br>
     * There is no guarantee of success, this methods fails silently if the focus cannot be gained.
     */
    public void toFront(){
        if( isVisible() ){
        	FocusRequest request = new DefaultFocusRequest( intern(), null, false, true, false, true );
            control.getOwner().intern().getController().setFocusedDockable( request );
        }
    }
    
    /**
     * Tries to focus this dockable, and then ensures that the {@link Component} <code>focus</code> actually gets the focus.<br>
     * The behavior of this method is not defined for the case where <code>focus</code> is not a child of <code>this</code>.<br>
     * There is no guarantee of success, this methods fails silently if the focus cannot be gained.
     * @param focus a child of this dockable, not <code>null</code>
     * @see #toFront()
     */
    public void toFront( Component focus ){
        if( isVisible() ){
        	FocusRequest request = new DefaultFocusRequest( intern(), focus, true, true, true, true );
            control.getOwner().intern().getController().setFocusedDockable( request );
        }
    }
    
    public void setLocation( CLocation location ){
        this.location = location;
        
        if( location != null ){
            if( control != null && control.hasParent( this ) ){
            	control.getLocationManager().setLocation( intern(), location );
                this.location = null;
            }
        }
    }
    
    public void setLocationsAside( CDockable dockable ){
	    if( dockable == null ){
	    	throw new IllegalArgumentException( "dockable must not be null" );
	    }
	    if( dockable == this ){
	    	throw new IllegalArgumentException( "dockable must not be the same object as this" );
	    }
	    if( dockable.getControl() == null ){
	    	throw new IllegalArgumentException( "dockable is not registered at a CControl" );
	    }
	    if( dockable.getControl() != getControl() ){
	    	throw new IllegalStateException( "dockable is registered at another CControl" ); 
	    }
	    if( dockable.getWorkingArea() != getWorkingArea() ){
	    	throw new IllegalArgumentException( "dockable has another working-area as this" );
	    }
	    CLocationModeManager locationManager = getControl().getLocationManager();
	    locationManager.setLocationAside( intern(), dockable.intern() );
	    
	    CLocationMode mode = locationManager.getCurrentMode( dockable.intern() );
	    if( mode != null ){
    		setLocation( new CExtendedModeLocation( mode.getExtendedMode() ) );
	    }
    }
    
    public boolean setLocationsAside( Filter<CDockable> filter ){
    	if( getControl() == null ){
    		throw new IllegalStateException( "this dockable must be registered at a CControl" );
    	}
    	CFocusHistory history = getControl().getFocusHistory();
    	CDockable dockable = history.getFirst( filter );
    	if( dockable == null ){
    		return false;
    	}
    	setLocationsAside( dockable );
    	return true;
    }
    
    public boolean setLocationsAsideFocused(){
    	boolean result = setLocationsAside( new Filter<CDockable>(){
    		public boolean includes( CDockable item ){
    			return item != AbstractCDockable.this && item.getWorkingArea() == workingArea && item.isVisible();
    		}
		});
    	if( !result ){
    		result = setLocationsAside( new Filter<CDockable>(){
        		public boolean includes( CDockable item ){
        			return item != AbstractCDockable.this && item.getWorkingArea() == workingArea;
        		}
    		});
    	}
    	return result;
    }
    
    public CLocation getBaseLocation(){
        if( control != null && isVisible() ){
            return control.getLocationManager().getLocation( intern() );
        }
        
        return location;
    }
    
    public CLocation getAutoBaseLocation( boolean noBackwardsTransformation ){
        if( control == null || control.hasParent( this ) ){
	    	return null;
	    }
	    
	    return control.getAutoBaseLocation( this, noBackwardsTransformation );
    }
    
    public void setExtendedMode( ExtendedMode extendedMode ){
        if( extendedMode == null )
            throw new NullPointerException( "extendedMode must not be null" );
    
        if( extendedMode == ExtendedMode.EXTERNALIZED ){
        	if( !isExternalizable() )
        		return;
        }
        if( extendedMode == ExtendedMode.MINIMIZED ){
        	if( !isMinimizable() )
        		return;
        }
        if( extendedMode == ExtendedMode.MAXIMIZED ){
        	if( !isMaximizable() )
        		return;
        }
        if( extendedMode == ExtendedMode.NORMALIZED ){
        	if( !isNormalizeable() ){
        		return;
        	}
        }
        
        CControlAccess control = control();
        if( control != null )
            control.getLocationManager().setMode( intern(), extendedMode );
    }
    
    public ExtendedMode getExtendedMode(){
        CControlAccess control = control();
        if( control == null )
            return null;
        
        return control.getLocationManager().getMode( intern() );
    }
    
    /**
     * Sets an algorithm that defines how this dockable attempts to automatically group itself with
     * other dockables.
     * @param grouping the grouping behavior, can be <code>null</code> in which case this dockable
     * does not attempt to group itself. The default value of this property is <code>null</code>.
     */
    public void setGrouping( DockableGrouping grouping ) {
		this.grouping = grouping;
	}
    
    public DockableGrouping getGrouping() {
    	return grouping;
    }
    
    public void setWorkingArea( CStation<?> area ) {
        this.workingArea = area;
    }
    
    public CStation<?> getWorkingArea() {
        return workingArea;
    }
    
    /**
     * Tells whether width and height are locked.
     * @return <code>true</code> if width and height are locked
     */
    public boolean isResizeLocked() {
        return resizeLockedVertically && resizeLockedHorizontally;
    }
    
    public boolean isResizeLockedVertically() {
        return resizeLockedVertically;
    }
    
    public boolean isResizeLockedHorizontally() {
        return resizeLockedHorizontally;
    }
    
    /**
     * Tells this {@link CDockable} which size it should have. The size will
     * be stored until it is read by {@link #getAndClearResizeRequest()}.<br>
     * If <code>process</code> is <code>true</code>, then this method will call 
     * {@link CControl#handleResizeRequests()} in order to try to apply the requested size. 
     * However, there are no guarantees that the requested size can be matched, or that 
     * the request gets handled at all.<br> If this <code>CDockable</code> is not registered at a 
     * {@link CControl}, then the request will remain unprocessed until this <code>CDockable</code>
     * is registered, and someone calls {@link CControl#handleResizeRequests()} on the new owner.
     * @param size the new preferred size, can be <code>null</code> to cancel an
     * earlier request
     * @param process whether to process all pending requests of all {@link CDockable}
     * registered at the {@link CControl} which is the owner of <code>this</code>.
     * Clients can set this parameter to <code>false</code> and call
     * {@link CControl#handleResizeRequests()} manually to process all pending
     * requests.
     * @see #setResizeRequest(RequestDimension, boolean)
     */
    public void setResizeRequest( Dimension size, boolean process ){
        resizeRequest = size == null ? null : new RequestDimension( size );
        
        if( process && control != null ){
            control.getOwner().handleResizeRequests();
        }
    }

    /**
     * Tells this {@link CDockable} which size it should have. The size will
     * be stored until it is read by {@link #getAndClearResizeRequest()}.<br>
     * If <code>process</code> is <code>true</code>, then this method will call 
     * {@link CControl#handleResizeRequests()} in order to try to apply the requested size. 
     * However, there are no guarantees that the requested size can be matched, or that the 
     * request gets handled at all.<br> If this <code>CDockable</code> is not registered at
     * a {@link CControl}, then the request will remain unprocessed until this <code>CDockable</code>
     * is registered, and someone calls {@link CControl#handleResizeRequests()} on the new owner.
     * @param size the new preferred size, can be <code>null</code> to cancel an
     * earlier request
     * @param process whether to process all pending requests of all {@link CDockable}
     * registered at the {@link CControl} which is the owner of <code>this</code>.
     * Clients can set this parameter to <code>false</code> and call
     * {@link CControl#handleResizeRequests()} manually to process all pending
     * requests.
     */
    public void setResizeRequest( RequestDimension size, boolean process ){
        resizeRequest = size == null ? null : new RequestDimension( size );
        
        if( process && control != null ){
            control.getOwner().handleResizeRequests();
        }
    }
    
    public RequestDimension getAndClearResizeRequest() {
        RequestDimension result = resizeRequest;
        resizeRequest = null;
        return result;
    }
    
    /**
     * Sets whether this dockable likes to remain with the same size all the time.
     * @param resizeLocked <code>true</code> if the size of this dockable should
     * be kept as long as possible
     * @see #setResizeLockedHorizontally(boolean)
     * @see #setResizeLockedVertically(boolean)
     */
    public void setResizeLocked( boolean resizeLocked ) {
        if( isResizeLocked() != resizeLocked ){
            this.resizeLockedHorizontally = resizeLocked;
            this.resizeLockedVertically = resizeLocked;
            listenerCollection.getCDockablePropertyListener().resizeLockedChanged( this );
        }
    }
    
    /**
     * Sets whether this dockable likes to remain with the same width all
     * the time.
     * @param resizeLockedHorizontally <code>true</code> if the width of
     * this dockable should be kept as long as possible
     */
    public void setResizeLockedHorizontally( boolean resizeLockedHorizontally ) {
        if( this.resizeLockedHorizontally != resizeLockedHorizontally ){
            this.resizeLockedHorizontally = resizeLockedHorizontally;
            listenerCollection.getCDockablePropertyListener().resizeLockedChanged( this );
        }
    }
    
    /**
     * Sets whether this dockable likes to remain with the same height
     * all the time.
     * @param resizeLockedVertically <code>true</code> if the height
     * of this dockable should be kept as long as possible
     */
    public void setResizeLockedVertically( boolean resizeLockedVertically ) {
        if( this.resizeLockedVertically != resizeLockedVertically ){
            this.resizeLockedVertically = resizeLockedVertically;
            listenerCollection.getCDockablePropertyListener().resizeLockedChanged( this );
        }
    }
    
    /**
     * Enables or disables a part of this dockable. Some effects are visible immediately, others
     * will need some time to show up. Usually disabling a part means that said part is shown in
     * some gray colors and won't react to any user input (e.g. to the mouse).<br>
     * Developers which need more accuracy in disabling items, should have a look at the
     * {@link DisablingStrategy}.
     * @param item what part of this {@link CDockable} should be enabled or disabled 
     * @param enabled whether the part should be enabled
     */
    public void setEnabled( EnableableItem item, boolean enabled ){
    	int flag = this.enabled;
		if( enabled ){
			this.enabled = EnableableItem.add( this.enabled, item );
		}
		else{
			this.enabled = EnableableItem.remove( this.enabled, item );
		}
		if( flag != this.enabled ){
			listenerCollection.getCDockablePropertyListener().enabledChanged( this );
		}
	}
    
    public boolean isEnabled( EnableableItem item ){
    	return EnableableItem.isEnabled( enabled, item );
    }
    
    public void setSticky( boolean sticky ){
        if( this.sticky != sticky ){
            this.sticky = sticky;
            listenerCollection.getCDockablePropertyListener().stickyChanged( this );
        }
    }
    
    public boolean isSticky(){
	    return sticky;
    }
    
    public void setStickySwitchable( boolean switchable ){
    	if( this.stickySwitchable != switchable ){
    		this.stickySwitchable = switchable;
    		listenerCollection.getCDockablePropertyListener().stickySwitchableChanged( this );
    	}
	}
       
    public boolean isStickySwitchable(){
    	return stickySwitchable;
    }
    
    public void setMinimizedSize( Dimension size ) {
        minimizeSize = new Dimension( size.width, size.height );
        listenerCollection.getCDockablePropertyListener().minimizeSizeChanged( this );
    }
    
    /**
     * Always <code>true</code>, clients should not override this method unless they know exactly what they are doing.
     */
    public boolean isNormalizeable(){
    	return true;
    }
    
    public Dimension getMinimizedSize() {
        return new Dimension( minimizeSize.width, minimizeSize.height );
    }
    
    /**
     * Tells this {@link CDockable} whether to show or to hide its titles.
     * @param shown <code>true</code> if titles should be shown, <code>false</code>
     * if they should be hidden.
     */
    public void setTitleShown( boolean shown ){
        if( this.titleShown != shown ){
            this.titleShown = shown;
            listenerCollection.getCDockablePropertyListener().titleShownChanged( this );
        }
    }
    
    public boolean isTitleShown() {
        return titleShown;
    }
    
    /**
     * Tells this {@link CDockable} whether to show a single tab or not.
     * @param singleTabShown <code>true</code> if a single tab should be shown,
     * <code>false</code> otherwise
     * @see #isSingleTabShown()
     */
    public void setSingleTabShown( boolean singleTabShown ){
    	if( this.singleTabShown != singleTabShown ){
    		this.singleTabShown = singleTabShown;
    		listenerCollection.getCDockablePropertyListener().singleTabShownChanged( this );
    	}
	}
    
    public boolean isSingleTabShown(){
	    return singleTabShown;
    }
    
    /**
     * Gets the intern representation of this dockable.
     * @return the intern representation.
     */
    public CommonDockable intern(){
    	if( dockable == null ){
    		init( createCommonDockable() );
    	}
        return dockable;
    }

    /**
     * Sets the default location for mode <code>mode</code> for this dockable. Note
     * that this location does not override any existing setting. This method can
     * be called either before or after making this dockable visible. It is
     * the client's responsibility to ensure that <code>location</code> is valid
     * together with <code>mode</code>.
     * @param mode the mode for which to store the default location
     * @param location the default location or <code>null</code>
     */
    public void setDefaultLocation( ExtendedMode mode, CLocation location ){
    	if( mode == null ){
    		throw new IllegalArgumentException( "mode must not be null" );
    	}
    	
        if( location == null ){
            defaultLocations.remove( mode );
        }
        else{
        	ExtendedMode locationMode = location.findMode();
        	if( locationMode == null ){
        		throw new IllegalArgumentException( "location does not carry enough information to find its mode" );
        	}
        	if( !mode.getModeIdentifier().equals( locationMode.getModeIdentifier() )){
        		throw new IllegalArgumentException( "mode of location and \'mode\' do not have the same identifier" );
        	}
        	
            defaultLocations.put( mode, location );
        
            if( control != null ){
                CLocationModeManager state = control.getLocationManager();
                if( state.getLocation( intern(), mode ) == null )
                    state.setLocation( intern(), mode, location );
            }
        }
    }
    
    /**
     * Gets an earlier set value of {@link #setDefaultLocation(ExtendedMode, CLocation)}.
     * @param mode the mode for which to search the default location
     * @return the location or <code>null</code>
     */
    public CLocation getDefaultLocation( ExtendedMode mode ){
        return defaultLocations.get( mode );
    }
    
    /**
     * Gets the unique identifier that has been assigned to this {@link CDockable} by the {@link CControl}. Every
     * dockable has a unique identifier, but it may not be the same identifier as set by this client.
     * @return the unique identifier, it is set once this dockable is added to a {@link CControl}
     */
    @FrameworkOnly
    protected String getDockableUniqueId(){
    	return uniqueId;
    }
    
    public CControlAccess getControlAccess(){
	    return control;
    }
    
    public void setControlAccess( CControlAccess control ){
    	if( this.control == control )
    		return;
    	
        if( this.control != null ){
        	if( this.control.shouldStore( this ) == null ){
        		this.control.getLocationManager().remove( intern() );
        	}
        	else{
        		this.control.getLocationManager().reduceToEmpty( intern() );
        	}
        	
            this.control.link( this, null );
            if( vetoClosingListenerConverter != null ){
            	this.control.getOwner().intern().removeVetoableListener( vetoClosingListenerConverter );
            	vetoClosingListenerConverter = null;
            }
        }
        
        this.control = control;
        
        if( control != null ){
        	if( uniqueId != null ){
        		control.getLocationManager().add( uniqueId, intern() );
        	}
        	
        	if( listenerCollection.hasVetoClosingListeners() ){
        		control.getOwner().intern().addVetoableListener( getVetoClosingListenerConverter() );
        	}
        	
            control.link( this, new CDockableAccess(){
            	private ExtendedMode currentMode;
            	
                public void informVisibility( boolean visible ) {
                    listenerCollection.getCDockableStateListener().visibilityChanged( AbstractCDockable.this );
                }
                public void informMode( ExtendedMode mode ) {
                	if( currentMode != mode ){
                		currentMode = mode;
	                	CDockableStateListener forward = listenerCollection.getCDockableStateListener();
	                	forward.extendedModeChanged( AbstractCDockable.this, mode );
                	}
                }
                public CFocusListener getFocusListener() {
                    return listenerCollection.getFocusListener();
                }
                public CKeyboardListener getKeyboardListener() {
                    return listenerCollection.getKeyboardListener();
                }
                public CDoubleClickListener getDoubleClickListener() {
                    return listenerCollection.getDoubleClickListener();
                }
                public void setUniqueId( String id ) {
                	if( (id != null && !id.equals( uniqueId )) || (id == null && uniqueId != null) ){
	                	if( AbstractCDockable.this.control != null && uniqueId != null ){
	                		CLocationModeManager manager = AbstractCDockable.this.control.getLocationManager();
	                		manager.remove( intern() );
	                	}
	                	
	                    uniqueId = id;
	                    
	                    if( AbstractCDockable.this.control != null && id != null ){
	                        CLocationModeManager manager = AbstractCDockable.this.control.getLocationManager();
	                        manager.put( uniqueId, intern() );
	                        
	                        for( Map.Entry<ExtendedMode, CLocation> location : defaultLocations.entrySet() ){
	                            if( manager.getLocation( intern(), location.getKey() ) == null )
	                                manager.setLocation( intern(), location.getKey(), location.getValue() );
	                        }
	                    }
                	}
                }
                
                public String getUniqueId() {
                    return uniqueId;
                }
                
                public CLocation internalLocation( boolean reset ){
                	if( reset ){
	                    CLocation loc = location;
	                    location = null;
	                    return loc;
                	}
                	else{
                		return location;
                	}
                }
            });
        }
        
        close.setControl( control );
    }
    
    /**
     * Exchanges an action of this {@link CDockable}. The actions that are associated 
     * with this <code>CDockable</code> through this method are not necessarily shown on the 
     * title. They are used by other modules to create effects that are known
     * only to them.
     * @param key the key of the action, one of the <code>ACTION_KEY_xzy</code>-constants
     * defined in {@link CDockable}
     * @param action the new action, can be <code>null</code> which might force
     * back a default action (that depends on the module that uses <code>key</code>)
     */
    public void putAction( String key, CAction action ){
        CAction old = actions.put( key, action );
        if( old != action ){
            listenerCollection.getCDockablePropertyListener().actionChanged( this, key, old, action );
        }
    }
    
    public CAction getAction( String key ) {
        return actions.get( key );
    }
    
    public ColorMap getColors() {
        return colors;
    }
    
    public FontMap getFonts() {
        return fonts;
    }
    
    public Component getFocusComponent(){
	    return focusComponent;
    }
    
    /**
     * Sets the {@link Component} which should receive the focus when this <code>CDockable</code> is focused. Please note
     * that the focus will be transferred to this component every time the dockable lost the focus and gained the focus again. The
     * default behavior of re-focusing the last focus owner should be sufficient for most applications.
     * @param component the component to focus, can be <code>null</code>, should be a child of this <code>CDockable</code>
     */
    public void setFocusComponent( Component component ){
		this.focusComponent = component;
	}
    
    public CStation<?> getParentStation(){
	    DockStation parent = intern().getDockParent();
	    while( parent != null ){
	    	if( parent instanceof CommonDockStation<?,?> ){
	    		return ((CommonDockStation<?, ?>)parent).getStation();
	    	}
	    	Dockable item = parent.asDockable();
	    	if( item == null ){
	    		return null;
	    	}
	    	parent = item.getDockParent();
	    }
	    return null;
    }
    
    public CControl getControl(){
        if( control == null ){
        	return null;
        }
        return control.getOwner();
    }
}
