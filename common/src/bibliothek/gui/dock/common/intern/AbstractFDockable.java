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

import java.util.ArrayList;
import java.util.List;

import bibliothek.gui.dock.action.DefaultDockActionSource;
import bibliothek.gui.dock.action.DockActionSource;
import bibliothek.gui.dock.action.LocationHint;
import bibliothek.gui.dock.common.FControl;
import bibliothek.gui.dock.common.FLocation;
import bibliothek.gui.dock.common.FWorkingArea;
import bibliothek.gui.dock.common.action.FAction;
import bibliothek.gui.dock.common.event.FDockableListener;


/**
 * An abstract implementation of {@link FDockable}. Contains methods to 
 * work with listeners and with {@link FAction}s.
 * @author Benjamin Sigg
 */
public abstract class AbstractFDockable implements FDockable {
    
    /** the location of this dockable */
    private FLocation location = null;
    
    /** a liste of listeners that were added to this dockable */
    private List<FDockableListener> listeners = new ArrayList<FDockableListener>();
    
    /** the graphical representation of this dockable */
    private FacileDockable dockable;

    /** the preferred parent of this dockable */
    private FWorkingArea workingArea;
    
    /** the control managing this dockable */
    private FControlAccess control;
    
    /** unique id of this {@link FDockable} */
    private String uniqueId;
    
    /** Source that contains the action that closes this dockable */
    private DefaultDockActionSource close = new DefaultDockActionSource(
            new LocationHint( LocationHint.ACTION_GUARD, LocationHint.RIGHT_OF_ALL ));
    
    /**
     * Creates a new dockable
     * @param dockable the internal representation of this {@link FDockable},
     * can be <code>null</code> but then {@link #init(FacileDockable)} should
     * be called.
     */
    protected AbstractFDockable( FacileDockable dockable ){
        this.dockable = dockable;
    }
    
    /**
     * Initializes this FDockable.
     * @param dockable the representation of this <code>FDockable</code>, not <code>null</code>
     */
    protected void init( FacileDockable dockable ){
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
    protected FControlAccess control(){
        return control;
    }
    
    /**
     * Adds a listener to this dockable, the listener will be informed of
     * changes of this dockable.
     * @param listener the new listener
     */
    public void addFDockableListener( FDockableListener listener ){
        listeners.add( listener );
    }
    
    /**
     * Removes a listener from this dockable.
     * @param listener the listener to remove
     */
    public void removeFDockableListener( FDockableListener listener ){
        listeners.remove( listener );
    }
    
    /**
     * Gets the list of listeners.
     * @return the listeners
     */
    protected FDockableListener[] listeners(){
        return listeners.toArray( new FDockableListener[ listeners.size() ] );
    }
    
    /**
     * Tells whether this dockable can be closed by the user. Clients which
     * have a method like "setCloseable" can use {@link #updateClose()}
     * to ensure that an action is shown/hidden that allows the user to
     * close this <code>FDockable</code>.
     * @return <code>true</code> if this element can be closed
     */
    public abstract boolean isCloseable();
    
    
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
    
    /**
     * Shows or hides this dockable. If this dockable is not visible and
     * is made visible, then the framework tries to set its location at
     * the last known position.
     * @param visible the new visibility state
     */
    public void setVisible( boolean visible ){
        if( control != null ){
            if( visible ){
                control.show( this );
            }
            else
                control.hide( this );
        }
    }
    
    /**
     * Tells whether this dockable is currently visible or not.
     * @return <code>true</code> if this dockable can be accessed by the user
     * through a graphical user interface.
     */
    public boolean isVisible(){
        if( control == null )
            return false;
        else
            return control.isVisible( this );
    }
    
    /**
     * Sets the location of this dockable. If this dockable is visible, than
     * this method will take immediately effect. Otherwise the location will be
     * stored in a cache and read as soon as this dockable is made visible.<br>
     * Note that the location can only be seen as a hint, the framework tries
     * to fit the location as good as possible, but there are no guarantees.
     * @param location the new location, <code>null</code> is possible, but
     * will not move the dockable immediately
     */
    public void setLocation( FLocation location ){
        this.location = location;
        
        if( location != null ){
            if( control != null && isVisible() ){
                control.getStateManager().setLocation( dockable, location );
                location = null;
            }
        }
    }
    
    /**
     * Gets the location of this dockable. If this dockable is visible, then
     * a location will always be returned. Otherwise a location will only
     * be returned if it just was set using {@link #setLocation(FLocation)}.
     * @return the location or <code>null</code>
     */
    public FLocation getLocation(){
        if( control != null && isVisible() ){
            return control.getStateManager().getLocation( dockable );
        }
        
        return location;
    }
    
    /**
     * Sets how and where this dockable should be shown. Conflicts with
     * {@link #isExternalizable()}, {@link #isMaximizable()} and {@link #isMinimizable()}
     * will just be ignored.
     * @param extendedMode the size and location
     */
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
        
        FControlAccess control = control();
        if( control != null )
            control.getStateManager().setMode( dockable, extendedMode );
    }
    
    /**
     * Gets the size and location of this dockable.
     * @return the size and location or <code>null</code> if this dockable
     * is not part of an {@link FControl}.
     */
    public ExtendedMode getExtendedMode(){
        FControlAccess control = control();
        if( control == null )
            return null;
        
        return control.getStateManager().getMode( dockable );
    }
    
    public void setWorkingArea( FWorkingArea area ) {
        this.workingArea = area;
    }
    
    public FWorkingArea getWorkingArea() {
        return workingArea;
    }
    
    /**
     * Gets the intern representation of this dockable.
     * @return the intern representation.
     */
    public FacileDockable intern(){
        return dockable;
    }
    
    /**
     * Sets the {@link FControl} which is responsible for this dockable.
     * @param control the new control
     */
    public void setControl( FControlAccess control ){
        if( this.control != null ){
            this.control.getStateManager().remove( dockable );
            this.control.link( this, null );
        }
        
        this.control = control;
        
        if( control != null ){
            control.link( this, new FDockableAccess(){
                public void informVisibility( boolean visible ) {
                    for( FDockableListener listener : listeners() )
                        listener.visibilityChanged( AbstractFDockable.this );
                }
                public void informMode( ExtendedMode mode ) {
                    switch( mode ){
                        case EXTERNALIZED:
                            for( FDockableListener listener : listeners() )
                                listener.externalized( AbstractFDockable.this );
                            break;
                        case MINIMIZED:
                            for( FDockableListener listener : listeners() )
                                listener.minimized( AbstractFDockable.this );
                            break;
                        case MAXIMIZED:
                            for( FDockableListener listener : listeners() )
                                listener.maximized( AbstractFDockable.this );
                            break;
                        case NORMALIZED:
                            for( FDockableListener listener : listeners() )
                                listener.normalized( AbstractFDockable.this );
                            break;
                    }
                }
                public void setUniqueId( String id ) {
                    uniqueId = id;
                    if( AbstractFDockable.this.control != null && id != null ){
                        FStateManager state = AbstractFDockable.this.control.getStateManager();
                        state.put( uniqueId, dockable );
                    }
                }
                
                public String getUniqueId() {
                    return uniqueId;
                }
                
                public FLocation internalLocation(){
                    FLocation loc = location;
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
    public FControlAccess getControl(){
        return control;
    }
}
