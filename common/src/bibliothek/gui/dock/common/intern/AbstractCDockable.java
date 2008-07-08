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

import java.awt.Dimension;
import java.util.HashMap;
import java.util.Map;

import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.CLocation;
import bibliothek.gui.dock.common.CStation;
import bibliothek.gui.dock.common.ColorMap;
import bibliothek.gui.dock.common.action.CAction;
import bibliothek.gui.dock.common.event.*;
import bibliothek.gui.dock.common.intern.action.CloseActionSource;
import bibliothek.gui.dock.common.layout.RequestDimension;
import bibliothek.gui.dock.title.DockTitle;


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
    private CStation workingArea;
    
    /** the control managing this dockable */
    private CControlAccess control;
    
    /** unique id of this {@link CDockable} */
    private String uniqueId;
    
    /** whether this element likes to have the same height all the time */
    private boolean resizeLockedVertically = false;
    
    /** whether this element likes to have the same width all the time */
    private boolean resizeLockedHorizontally = false;
    
    /** whether to remain visible when minimized and unfocused or not */
    private boolean minimizeHold = false;
    
    /** the preferred size when minimized */
    private Dimension minimizeSize = new Dimension( -1, -1 );
    
    /** the preferred size of this {@link CDockable} */
    private RequestDimension resizeRequest;
    
    /** the colors associated with this dockable */
    private ColorMap colors = new ColorMap( this );
    
    /** the actions that are shown by other modules */
    private Map<String, CAction> actions = new HashMap<String, CAction>();
    
    /** whether the {@link DockTitle} should not be created */
    private boolean titleShown = true;
    
    /** the listeners that were added to this dockable */
    protected CListenerCollection listenerCollection = new CListenerCollection();
    
    /** Source that contains the action that closes this dockable */
    private CloseActionSource close = new CloseActionSource( this );
    
    /** The default locations for the available {@link CDockable.ExtendedMode}s. */
    private Map<ExtendedMode, CLocation> defaultLocations = new HashMap<ExtendedMode, CLocation>( 4 );
    
    /**
     * Creates a new dockable
     * @param dockable the internal representation of this {@link CDockable},
     * can be <code>null</code> but then {@link #init(CommonDockable)} should
     * be called.
     */
    protected AbstractCDockable( CommonDockable dockable ){
        this.dockable = dockable;
    }
    
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
    
    public void removeCDockableStateListener( CDockableStateListener listener ){
        listenerCollection.removeCDockableStateListener( listener );
    }
    
    public void removeCDockablePropertyListener( CDockablePropertyListener listener ) {
        listenerCollection.removeCDockablePropertyListener( listener );
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
            throw new IllegalStateException( "This CDockable does not know its CControl. Call CControl.add(...) to connect this CDockable befor calling setVisible(...)." );
        
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
    
    /**
     * Tries to focus this dockable. There are no guarantees that this dockabe
     * really gets focused.
     */
    public void toFront(){
        if( isVisible() ){
            control.getOwner().intern().getController().setFocusedDockable( intern(), false );
        }
    }
    
    public void setLocation( CLocation location ){
        this.location = location;
        
        if( location != null ){
            if( control != null && isVisible() ){
                control.getStateManager().setLocation( dockable, location );
                location = null;
            }
        }
    }
    
    public CLocation getBaseLocation(){
        if( control != null && isVisible() ){
            return control.getStateManager().getLocation( dockable );
        }
        
        return location;
    }
    
    public void setExtendedMode( ExtendedMode extendedMode ){
        if( extendedMode == null )
            throw new NullPointerException( "extendedMode must not be null" );
    
        switch( extendedMode ){
            case EXTERNALIZED:
                if( !isExternalizable() )
                    return;
                
            case MAXIMIZED:
                if( !isMaximizable() )
                    return;
                
            case MINIMIZED:
                if( !isMinimizable() )
                    return;
        }
        
        CControlAccess control = control();
        if( control != null )
            control.getStateManager().setMode( dockable, extendedMode );
    }
    
    public ExtendedMode getExtendedMode(){
        CControlAccess control = control();
        if( control == null )
            return null;
        
        return control.getStateManager().getMode( dockable );
    }
    
    public void setWorkingArea( CStation area ) {
        this.workingArea = area;
    }
    
    public CStation getWorkingArea() {
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
     * This method will call {@link CControl#handleResizeRequests()} in order to
     * try to apply the requested size. However, there are no guarantees that
     * the requested size can be matched, or that the request gets handled at all.<br>
     * If this dockable is not registered at a {@link CControl}, then the request
     * will remain unprocessed until this dockable is registered, and someone calls
     * {@link CControl#handleResizeRequests()} on the new owner.
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
     * This method will call {@link CControl#handleResizeRequests()} in order to
     * try to apply the requested size. However, there are no guarantees that
     * the requested size can be matched, or that the request gets handled at all.<br>
     * If this dockable is not registered at a {@link CControl}, then the request
     * will remain unprocessed until this dockable is registered, and someone calls
     * {@link CControl#handleResizeRequests()} on the new owner.
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
    
    public void setMinimizedHold( boolean hold ) {
        if( this.minimizeHold != hold ){
            this.minimizeHold = hold;
            listenerCollection.getCDockablePropertyListener().minimizedHoldChanged( this );
        }
    }
    
    public boolean isMinimizedHold() {
        return minimizeHold;
    }
    
    public void setMinimizedSize( Dimension size ) {
        minimizeSize = new Dimension( size.width, size.height );
        listenerCollection.getCDockablePropertyListener().minimizeSizeChanged( this );
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
     * Gets the intern representation of this dockable.
     * @return the intern representation.
     */
    public CommonDockable intern(){
        return dockable;
    }
    
    /**
     * Sets the default location for mode <code>mode</code> for this dockable. Note
     * that this location does not override any existing setting. This method can
     * be called either before or after making this dockable visible. It is
     * the client's responsibility to ensure that <code>location</code> is valid
     * together with <code>mode</code>. It makes no sense to specify the default
     * location for {@link CDockable.ExtendedMode#MAXIMIZED}.
     * @param mode the mode for which to store the default location
     * @param location the default location or <code>null</code>
     */
    public void setDefaultLocation( ExtendedMode mode, CLocation location ){
        if( location == null )
            defaultLocations.remove( mode );
        else{
            defaultLocations.put( mode, location );
        
            if( control != null ){
                CStateManager state = control.getStateManager();
                if( state.getLocation( dockable, mode ) == null )
                    state.setLocation( dockable, mode, location );
            }
        }
    }
    
    /**
     * Gets an earlier set value of {@link #setDefaultLocation(bibliothek.gui.dock.common.intern.CDockable.ExtendedMode, CLocation)}.
     * @param mode the mode for which to search the default location
     * @return the location or <code>null</code>
     */
    public CLocation getDefaultLocation( ExtendedMode mode ){
        return defaultLocations.get( mode );
    }
    
    /**
     * Sets the {@link CControl} which is responsible for this dockable.
     * @param control the new control
     */
    public void setControl( CControlAccess control ){
        if( this.control != null ){
            this.control.getStateManager().remove( dockable );
            this.control.link( this, null );
        }
        
        this.control = control;
        
        if( control != null ){
            control.link( this, new CDockableAccess(){
                public void informVisibility( boolean visible ) {
                    listenerCollection.getCDockableStateListener().visibilityChanged( AbstractCDockable.this );
                }
                public void informMode( ExtendedMode mode ) {
                    CDockableStateListener forward = listenerCollection.getCDockableStateListener();
                    switch( mode ){
                        case EXTERNALIZED:
                            forward.externalized( AbstractCDockable.this );
                            break;
                        case MINIMIZED:
                            forward.minimized( AbstractCDockable.this );
                            break;
                        case MAXIMIZED:
                            forward.maximized( AbstractCDockable.this );
                            break;
                        case NORMALIZED:
                            forward.normalized( AbstractCDockable.this );
                            break;
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
                    uniqueId = id;
                    if( AbstractCDockable.this.control != null && id != null ){
                        CStateManager state = AbstractCDockable.this.control.getStateManager();
                        state.put( uniqueId, dockable );
                        
                        for( Map.Entry<ExtendedMode, CLocation> location : defaultLocations.entrySet() ){
                            if( state.getLocation( dockable, location.getKey() ) == null )
                                state.setLocation( dockable, location.getKey(), location.getValue() );
                        }
                    }
                }
                
                public String getUniqueId() {
                    return uniqueId;
                }
                
                public CLocation internalLocation(){
                    CLocation loc = location;
                    location = null;
                    return loc;
                }
            });
        }
        
        close.setControl( control );
    }
    
    /**
     * Exchanges an action of this dockable. The actions that are associated 
     * with this dockable through this method are not necessarily shown on the 
     * title. They are used by other modules to create effects that are known
     * only to them.
     * @param key the key of the action
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
    
    /**
     * Gets the control which is responsible for this dockable.
     * @return the control
     */
    public CControlAccess getControl(){
        return control;
    }
}
