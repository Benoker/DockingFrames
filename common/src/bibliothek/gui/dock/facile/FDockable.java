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
package bibliothek.gui.dock.facile;

import java.awt.Container;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;

import bibliothek.gui.dock.action.DefaultDockActionSource;
import bibliothek.gui.dock.action.DockActionSource;
import bibliothek.gui.dock.action.LocationHint;
import bibliothek.gui.dock.facile.action.FAction;
import bibliothek.gui.dock.facile.event.FDockableListener;
import bibliothek.gui.dock.facile.intern.FControlAccess;
import bibliothek.gui.dock.facile.intern.FDockableAccess;
import bibliothek.gui.dock.facile.intern.FacileDockable;
import bibliothek.gui.dock.facile.intern.action.FSeparator;

/**
 * A frame that shows a {@link java.awt.Component}, has a title, an icon
 * and can take various sizes and locations.
 * @author Benjamin Sigg
 */
public class FDockable {
	/**
	 * The mode tells how big a {@link FDockable} is.
	 * @author Benjamin Sigg
	 */
	public static enum ExtendedMode{
		/** the dockable is as small as possible */
		MINIMIZED,
		/** the dockable is as big as possible */
		MAXIMIZED,
		/** the dockable has the normal size */
		NORMALIZED,
		/** the dockable is floating in a dialog */
		EXTERNALIZED
	}
	
	/** whether this dockable can be minimized */
	private boolean minimizable;
	/** whether this dockable can be maximized */
	private boolean maximizable;
	/** whether this dockable can be put into a dialog */
	private boolean externalizable;
	/** whether this dockable can be closed by the user */
	private boolean closeable;
	
	/** a liste of listeners that were added to this dockable */
	private List<FDockableListener> listeners = new ArrayList<FDockableListener>();
	
	/** the graphical representation of this dockable */
	private FacileDockable dockable;
	
	/** the control managing this dockable */
	private FControlAccess control;
	
	/** unique id of this {@link FDockable} */
	private String uniqueId;
	
	/** Source that contains the action that closes this dockable */
	private DefaultDockActionSource close = new DefaultDockActionSource(
	        new LocationHint( LocationHint.ACTION_GUARD, LocationHint.RIGHT_OF_ALL ));
	
	/**
	 * Creates a new dockable
	 */
	public FDockable(){
		dockable = new FacileDockable( this );
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
	 * Sets the text that is shown as title.
	 * @param text the title
	 */
	public void setTitleText( String text ){
	    dockable.setTitleText( text );
	}
	
	/**
	 * Gets the text that is shown as title.
	 * @return the title
	 */
	public String getTitleText(){
	    return dockable.getTitleText();
	}
	
	/**
	 * Sets the icon that is shown in the title of this <code>FDockable</code>.
	 * @param icon the title-icon
	 */
	public void setTitleIcon( Icon icon ){
	    dockable.setTitleIcon( icon );
	}
	
	/**
	 * Gets the icon that is shown in the title.
	 * @return the title-icon, might be <code>null</code>
	 */
	public Icon getTitleIcon(){
	    return dockable.getTitleIcon();
	}
	
	/**
	 * Gets the container on which the client can pack its components.
	 * @return the panel showing the content
	 */
	public Container getContentPane(){
	    return dockable.getContentPane();
	}
	
	/**
	 * Adds an action to this dockable. The action will be shown in the
	 * popup-menu which belongs to this dockable, and also as button in some titles
	 * of this dockable.
	 * @param action the new action
	 */
	public void addAction( FAction action ){
	    dockable.getActions().add( action.intern() );
	}
	
	/**
	 * Adds a new action to this dockable.
	 * @param index the location of the action
	 * @param action the action
	 * @see #addAction(FAction)
	 */
	public void insertAction( int index, FAction action ){
	    dockable.getActions().add( index, action.intern() );
	}
	
	/**
	 * Adds a separator to the list of {@link FAction}s of this dockable.
	 */
	public void addSeparator(){
	    addAction( FSeparator.SEPARATOR );
	}
	
	/**
	 * Adds a separator to the list of {@link FAction}s of this dockable.
	 * @param index the location of the action
	 */
	public void insertSeparator( int index ){
	    insertAction( index, FSeparator.SEPARATOR );
	}
	
	/**
	 * Removes an action from this dockable
	 * @param index the location of the action
	 */
	public void removeAction( int index ){
	    dockable.getActions().remove( index );
	}
	
	/**
	 * Removes an action from this dockable.
	 * @param action the action to remove
	 */
	public void removeAction( FAction action ){
	    dockable.getActions().remove( action.intern() );
	}
	
	/**
	 * Tells whether this dockable can be minimized by the user.
	 * @return <code>true</code> if this element can be minimized
	 */
	public boolean isMinimizable(){
		return minimizable;
	}
	
	/**
	 * Sets whether the user can minimize this dockable.
	 * @param minimizable <code>true</code> if the user can minimize this element
	 */
	public void setMinimizable( boolean minimizable ){
	    if( this.minimizable != minimizable ){
    		this.minimizable = minimizable;
    		
    		for( FDockableListener listener : listeners() )
    		    listener.minimizableChanged( this );
    		
    		if( control != null ){
    		    control.getStateManager().rebuild( dockable );
    		    control.getStateManager().ensureValidMode( this );
    		}
	    }
	}
	
	/**
	 * Tells whether this dockable can be maximized by the user.
	 * @return <code>true</code> if this element can be maximized
	 */
	public boolean isMaximizable(){
		return maximizable;
	}
	
	/**
	 * Sets whether the user can maximize this dockable.
	 * @param maximizable <code>true</code> if the user can maximize this element
	 */
	public void setMaximizable( boolean maximizable ){
		if( this.maximizable != maximizable ){
    	    this.maximizable = maximizable;
    	    
    	    for( FDockableListener listener : listeners() )
    	        listener.maximizableChanged( this );
    	    
    		if( control != null ){
                control.getStateManager().rebuild( dockable );
                control.getStateManager().ensureValidMode( this );
    		}
		}
	}
	
	
	/**
	 * Tells whether this dockable can be externalized by the user.
	 * @return <code>true</code> if this element can be externalized
	 */
	public boolean isExternalizable(){
		return externalizable;
	}
	
	/**
	 * Sets whether the user can externalize this dockable.
	 * @param externalizable <code>true</code> if the user can externalize this element
	 */
	public void setExternalizable( boolean externalizable ){
	    if( this.externalizable != externalizable ){
    		this.externalizable = externalizable;
    		
    		for( FDockableListener listener : listeners() )
    		    listener.externalizableChanged( this );
    		
    		if( control != null ){
                control.getStateManager().rebuild( dockable );
                control.getStateManager().ensureValidMode( this );
    		}
	    }
	}
	
	
	/**
	 * Tells whether this dockable can be closed by the user.
	 * @return <code>true</code> if this element can be closed
	 */
	public boolean isCloseable(){
		return closeable;
	}
	
	/**
	 * Sets whether the user can close this dockable.
	 * @param closeable <code>true</code> if the user can close this element
	 */
	public void setCloseable( boolean closeable ){
	    if( this.closeable != closeable ){
	        this.closeable = closeable;		
		    updateClose();
		    for( FDockableListener listener : listeners() )
		        listener.closeableChanged( this );
	    }
	}
	
	/**
	 * Ensures that {@link #close} contains an action when necessary.
	 */
	private void updateClose(){
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
			if( visible )
				control.show( this );
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
	 * Sets how and where this dockable should be shown.
	 * @param extendedMode the size and location
	 */
	public void setExtendedMode( ExtendedMode extendedMode ){
		if( extendedMode == null )
			throw new NullPointerException( "extendedMode must not be null" );
		
		if( control != null )
		    control.getStateManager().setMode( dockable, extendedMode );
	}
	
	/**
	 * Gets the size and location of this dockable.
	 * @return the size and location or <code>null</code> if this dockable
	 * is not part of an {@link FControl}.
	 */
	public ExtendedMode getExtendedMode(){
		if( control == null )
		    return null;
		
		return control.getStateManager().getMode( dockable );
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
	void setControl( FControlAccess control ){
	    if( this.control != null ){
	        this.control.getStateManager().remove( dockable );
	        this.control.link( this, null );
	    }
	    
		this.control = control;
		
		if( control != null ){
		    control.link( this, new FDockableAccess(){
		        public void informVisibility( boolean visible ) {
		            for( FDockableListener listener : listeners() )
		                listener.visibilityChanged( FDockable.this );
		        }
		        public void informMode( ExtendedMode mode ) {
		            switch( mode ){
		                case EXTERNALIZED:
		                    for( FDockableListener listener : listeners() )
		                        listener.externalized( FDockable.this );
		                    break;
		                case MINIMIZED:
                            for( FDockableListener listener : listeners() )
                                listener.minimized( FDockable.this );
                            break;
		                case MAXIMIZED:
                            for( FDockableListener listener : listeners() )
                                listener.maximized( FDockable.this );
                            break;
		                case NORMALIZED:
                            for( FDockableListener listener : listeners() )
                                listener.normalized( FDockable.this );
                            break;
		            }
		        }
		        public void setUniqueId( String id ) {
		            uniqueId = id;
		            if( FDockable.this.control != null )
		                FDockable.this.control.getStateManager().add( uniqueId, dockable );
		        }
		        
		        public String getUniqueId() {
		            return uniqueId;
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
	DockActionSource getClose() {
        return close;
    }
	
	/**
	 * Gets the control which is responsible for this dockable.
	 * @return the control
	 */
	FControlAccess getControl(){
		return control;
	}
}
