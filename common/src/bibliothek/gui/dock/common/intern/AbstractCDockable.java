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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bibliothek.gui.dock.common.*;
import bibliothek.gui.dock.common.action.CAction;
import bibliothek.gui.dock.common.event.CDockablePropertyListener;
import bibliothek.gui.dock.common.event.CDockableStateListener;
import bibliothek.gui.dock.common.intern.action.CloseActionSource;
import bibliothek.gui.dock.title.DockTitle;


/**
 * An abstract implementation of {@link CDockable}. Contains methods to 
 * work with listeners and with {@link CAction}s.
 * @author Benjamin Sigg
 */
public abstract class AbstractCDockable implements CDockable {
    
    /** the location of this dockable */
    private CLocation location = null;
    
    /** a list of state listeners that were added to this dockable */
    private List<CDockableStateListener> stateListeners = new ArrayList<CDockableStateListener>();
    
    /** a list of property listeners that were added to this dockable */
    private List<CDockablePropertyListener> propertyListeners = new ArrayList<CDockablePropertyListener>();
    
    /** the graphical representation of this dockable */
    private CommonDockable dockable;

    /** the preferred parent of this dockable */
    private CStation workingArea;
    
    /** the control managing this dockable */
    private CControlAccess control;
    
    /** unique id of this {@link CDockable} */
    private String uniqueId;
    
    /** whether this element likes to have the same size all the time */
    private boolean resizeLocked = false;
    
    /** whether to remain visible when minimized and unfocused or not */
    private boolean minimizeHold = false;
    
    /** the preferred size when minimized */
    private Dimension minimizeSize = new Dimension( -1, -1 );
    
    /** the preferred size of this {@link CDockable} */
    private Dimension resizeRequest;
    
    /** the colors associated with this dockable */
    private ColorMap colors = new ColorMap( this );
    
    /** the actions that are shown by other modules */
    private Map<String, CAction> actions = new HashMap<String, CAction>();
    
    /** whether the {@link DockTitle} should not be created */
    private boolean titleShown = true;
    
    /** Source that contains the action that closes this dockable */
    private CloseActionSource close = new CloseActionSource( this );
    
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
        stateListeners.add( listener );
    }
    
    public void addCDockablePropertyListener( CDockablePropertyListener listener ) {
        propertyListeners.add( listener );
    }
    
    public void removeCDockableStateListener( CDockableStateListener listener ){
        stateListeners.remove( listener );
    }
    
    public void removeCDockablePropertyListener( CDockablePropertyListener listener ) {
        propertyListeners.remove( listener );
    }
    
    /**
     * Gets the list of state listeners.
     * @return the stateListeners
     */
    protected CDockableStateListener[] stateListeners(){
        return stateListeners.toArray( new CDockableStateListener[ stateListeners.size() ] );
    }
    
    /**
     * Gets the list of property listeners.
     * @return the stateListeners
     */
    protected CDockablePropertyListener[] propertyListeners(){
        return propertyListeners.toArray( new CDockablePropertyListener[ propertyListeners.size() ] );
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
    
    public boolean isResizeLocked() {
        return resizeLocked;
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
    public void setResizeRequest( Dimension size, boolean process ){
        resizeRequest = size == null ? null : new Dimension( size );
        
        if( process && control != null ){
            control.getOwner().handleResizeRequests();
        }
    }
    
    public Dimension getAndClearResizeRequest() {
        Dimension result = resizeRequest;
        resizeRequest = null;
        return result;
    }
    
    /**
     * Sets whether this dockable likes to remain with the same size all the time.
     * @param resizeLocked <code>true</code> if the size of this dockable should
     * be kept as long as possible.
     */
    public void setResizeLocked( boolean resizeLocked ) {
        if( this.resizeLocked != resizeLocked ){
            this.resizeLocked = resizeLocked;
            
            for( CDockablePropertyListener listener : propertyListeners() )
                listener.resizeLockedChanged( this );
        }
    }
    
    public void setMinimizedHold( boolean hold ) {
        if( this.minimizeHold != hold ){
            this.minimizeHold = hold;
            for( CDockablePropertyListener listener : propertyListeners() )
                listener.minimizedHoldChanged( this );
        }
    }
    
    public boolean isMinimizedHold() {
        return minimizeHold;
    }
    
    public void setMinimizedSize( Dimension size ) {
        minimizeSize = new Dimension( size.width, size.height );
        for( CDockablePropertyListener listener : propertyListeners() )
            listener.minimizeSizeChanged( this );
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
            
            for( CDockablePropertyListener listener : propertyListeners() )
                listener.titleShownChanged( this );
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
                    for( CDockableStateListener listener : stateListeners() )
                        listener.visibilityChanged( AbstractCDockable.this );
                }
                public void informMode( ExtendedMode mode ) {
                    switch( mode ){
                        case EXTERNALIZED:
                            for( CDockableStateListener listener : stateListeners() )
                                listener.externalized( AbstractCDockable.this );
                            break;
                        case MINIMIZED:
                            for( CDockableStateListener listener : stateListeners() )
                                listener.minimized( AbstractCDockable.this );
                            break;
                        case MAXIMIZED:
                            for( CDockableStateListener listener : stateListeners() )
                                listener.maximized( AbstractCDockable.this );
                            break;
                        case NORMALIZED:
                            for( CDockableStateListener listener : stateListeners() )
                                listener.normalized( AbstractCDockable.this );
                            break;
                    }
                }
                public void setUniqueId( String id ) {
                    uniqueId = id;
                    if( AbstractCDockable.this.control != null && id != null ){
                        CStateManager state = AbstractCDockable.this.control.getStateManager();
                        state.put( uniqueId, dockable );
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
            for( CDockablePropertyListener listener : propertyListeners())
                listener.actionChanged( this, key, old, action );
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
