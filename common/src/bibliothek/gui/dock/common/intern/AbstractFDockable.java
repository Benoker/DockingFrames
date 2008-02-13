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
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.CLocation;
import bibliothek.gui.dock.common.CWorkingArea;
import bibliothek.gui.dock.common.action.CAction;
import bibliothek.gui.dock.common.event.CDockableListener;


/**
 * An abstract implementation of {@link CDockable}. Contains methods to 
 * work with listeners and with {@link CAction}s.
 * @author Benjamin Sigg
 */
public abstract class AbstractFDockable implements CDockable {
    
    /** the location of this dockable */
    private CLocation location = null;
    
    /** a liste of listeners that were added to this dockable */
    private List<CDockableListener> listeners = new ArrayList<CDockableListener>();
    
    /** the graphical representation of this dockable */
    private CommonDockable dockable;

    /** the preferred parent of this dockable */
    private CWorkingArea workingArea;
    
    /** the control managing this dockable */
    private CControlAccess control;
    
    /** unique id of this {@link CDockable} */
    private String uniqueId;
    
    /** Source that contains the action that closes this dockable */
    private DefaultDockActionSource close = new DefaultDockActionSource(
            new LocationHint( LocationHint.ACTION_GUARD, LocationHint.RIGHT_OF_ALL ));
    
    /**
     * Creates a new dockable
     * @param dockable the internal representation of this {@link CDockable},
     * can be <code>null</code> but then {@link #init(CommonDockable)} should
     * be called.
     */
    protected AbstractFDockable( CommonDockable dockable ){
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
    
    /**
     * Adds a listener to this dockable, the listener will be informed of
     * changes of this dockable.
     * @param listener the new listener
     */
    public void addFDockableListener( CDockableListener listener ){
        listeners.add( listener );
    }
    
    /**
     * Removes a listener from this dockable.
     * @param listener the listener to remove
     */
    public void removeFDockableListener( CDockableListener listener ){
        listeners.remove( listener );
    }
    
    /**
     * Gets the list of listeners.
     * @return the listeners
     */
    protected CDockableListener[] listeners(){
        return listeners.toArray( new CDockableListener[ listeners.size() ] );
    }
    
    /**
     * Tells whether this dockable can be closed by the user. Clients which
     * have a method like "setCloseable" can use {@link #updateClose()}
     * to ensure that an action is shown/hidden that allows the user to
     * close this <code>CDockable</code>.
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
        if( control == null )
            throw new IllegalStateException( "This CDockable does not know its CControl. Call CControl.add(...) to connect this CDockable befor calling setVisible(...)." );
        
        if( visible ){
            control.show( this );
        }
        else{
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
    public void setLocation( CLocation location ){
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
     * be returned if it just was set using {@link #setLocation(CLocation)}.
     * @return the location or <code>null</code>
     */
    public CLocation getLocation(){
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
        
        CControlAccess control = control();
        if( control != null )
            control.getStateManager().setMode( dockable, extendedMode );
    }
    
    /**
     * Gets the size and location of this dockable.
     * @return the size and location or <code>null</code> if this dockable
     * is not part of an {@link CControl}.
     */
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
                    for( CDockableListener listener : listeners() )
                        listener.visibilityChanged( AbstractFDockable.this );
                }
                public void informMode( ExtendedMode mode ) {
                    switch( mode ){
                        case EXTERNALIZED:
                            for( CDockableListener listener : listeners() )
                                listener.externalized( AbstractFDockable.this );
                            break;
                        case MINIMIZED:
                            for( CDockableListener listener : listeners() )
                                listener.minimized( AbstractFDockable.this );
                            break;
                        case MAXIMIZED:
                            for( CDockableListener listener : listeners() )
                                listener.maximized( AbstractFDockable.this );
                            break;
                        case NORMALIZED:
                            for( CDockableListener listener : listeners() )
                                listener.normalized( AbstractFDockable.this );
                            break;
                    }
                }
                public void setUniqueId( String id ) {
                    uniqueId = id;
                    if( AbstractFDockable.this.control != null && id != null ){
                        CStateManager state = AbstractFDockable.this.control.getStateManager();
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
