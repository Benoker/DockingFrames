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
import java.util.List;

import bibliothek.gui.dock.action.DefaultDockActionSource;
import bibliothek.gui.dock.action.DockActionSource;
import bibliothek.gui.dock.action.LocationHint;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.CLocation;
import bibliothek.gui.dock.common.CWorkingArea;
import bibliothek.gui.dock.common.action.CAction;
import bibliothek.gui.dock.common.event.CDockablePropertyListener;
import bibliothek.gui.dock.common.event.CDockableStateListener;


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
    private CWorkingArea workingArea;
    
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
    
    /** Source that contains the action that closes this dockable */
    private DefaultDockActionSource close = new DefaultDockActionSource(
            new LocationHint( LocationHint.ACTION_GUARD, LocationHint.RIGHT_OF_ALL ));
    
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
    
    /**
     * Ensures that {@link #close} contains an action when necessary.
     */
    protected void updateClose(){
        boolean closeable = isCloseable();
        
        if( control == null || !closeable )
            close.removeAll();
        else if( control != null && closeable && close.getDockActionCount() == 0 )
            close.add( control.createCloseAction( this ) );
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
    
    public CLocation getLocation(){
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
    
    public void setWorkingArea( CWorkingArea area ) {
        this.workingArea = area;
    }
    
    public CWorkingArea getWorkingArea() {
        return workingArea;
    }
    
    public boolean isResizeLocked() {
        return resizeLocked;
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
        
        close.removeAll();
        updateClose();
    }
    
    /**
     * Gets the source that contains the close-action.
     * @return the source
     */
    public DockActionSource getClose() {
        return close;
    }
    
    /**
     * Gets the control which is responsible for this dockable.
     * @return the control
     */
    public CControlAccess getControl(){
        return control;
    }
}
