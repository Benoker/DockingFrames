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
package bibliothek.gui.dock.control;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.SwingUtilities;

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DockElementRepresentative;
import bibliothek.gui.dock.action.ActionPopup;
import bibliothek.gui.dock.action.ActionPopupSuppressor;
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.action.DockActionSource;
import bibliothek.gui.dock.action.popup.DefaultActionPopupMenuFactory;
import bibliothek.gui.dock.action.popup.ActionPopupMenuFactory;
import bibliothek.gui.dock.event.DockControllerRepresentativeListener;

/**
 * Manages the popup menus, adds a listeners to all {@link DockElementRepresentative}s to open popup menus
 * when the user makes a right click.
 * @author Benjamin Sigg
 */
public class PopupController implements DockControllerRepresentativeListener{
    /** tells which Dockable has which listener */
    private Map<DockElementRepresentative, ComponentObserver> listeners =
        new HashMap<DockElementRepresentative, ComponentObserver>();
    
    /** the controller for which this popup-controller works */
    private DockController controller;
    
    /** this factory creates the menus that are popping up */
    private ActionPopupMenuFactory factory = new DefaultActionPopupMenuFactory();
    
    /** tells which popups are to be shown */
    private ActionPopupSuppressor popupSuppressor = ActionPopupSuppressor.ALLOW_ALWAYS;
    
    /** if set, then popup menus may be opened during drag and drop operations */
    private boolean allowOnMove = false;
    
    /** if set, then empty menus can be opened */
    private boolean allowEmpty = false;
    
    /**
     * Creates a new popup-controller.
     * @param controller the controller for which this instance works
     */
    public PopupController( DockController controller ){
    	if( controller == null )
    		throw new IllegalArgumentException( "controller must not be null" );
    	
    	this.controller = controller;
    	controller.addRepresentativeListener( this );
    }
    
    public void representativeAdded( DockController controller, DockElementRepresentative representative ) {
        if( representative.getElement().asDockable() != null ){
            ComponentObserver observer = new ComponentObserver( representative );
            listeners.put( representative, observer );
            representative.addMouseInputListener( observer );
        }
    }
    
    public void representativeRemoved( DockController controller, DockElementRepresentative representative ) {
        if( representative.getElement().asDockable() != null ){
            ComponentObserver observer = listeners.remove( representative );
            if( observer != null ){
                representative.removeMouseInputListener( observer );
            }
        }
    }
    
    /**
     * Gets the {@link DockController} for which this {@link PopupController} works.
     * @return the owner of this controller
     */
    public DockController getController(){
		return controller;
	}
    
    /**
     * Sets the factory which creates new menus that pop up.
     * @param factory the factory creating menus, not <code>null</code>
     */
    public void setPopupMenuFactory( ActionPopupMenuFactory factory ){
    	if( factory == null ){
    		throw new IllegalArgumentException( "the factory must not be null" );
    	}
    	this.factory = factory;
    }
    
    /**
     * Gets the factory which is responsible for creating new menus.
     * @return the factory, never <code>null</code>
     */
    public ActionPopupMenuFactory getPopupMenuFactory(){
		return factory;
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
     * If set, then empty menus are still opened. The {@link ActionPopupSuppressor} or the
     * {@link ActionPopupMenuFactory} may however catch the empty menu and hide it.
     * @param allowEmpty if set to <code>false</code>, empty menus can never be shown
     */
    public void setAllowEmptyMenu( boolean allowEmpty ){
		this.allowEmpty = allowEmpty;
	}
    
    /**
     * Tells whether empty menus can be shown.
     * @return <code>true</code> if empty menus can be shown
     */
    public boolean isAllowEmptyMenu(){
		return allowEmpty;
	}
    
    /**
     * Sets whether menus can be opened during drag and drop operations. This property should
     * be remain <code>false</code> and only be set to <code>true</code> for very special occasions.
     * @param allowOnMove whether menus can be opened during drag and drop operations
     */
    public void setAllowOnMove( boolean allowOnMove ){
		this.allowOnMove = allowOnMove;
	}
    
    /**
     * Tells whether menus can be opened during drag and drop operations
     * @return <code>true</code> if menus can be opened all the time
     */
    public boolean isAllowOnMove(){
		return allowOnMove;
	}
    
    /**
     * A mouse listener opening a popup menu when necessary.
     * @author Benjamin Sigg
     */
    private static class ComponentObserver extends ActionPopup{
        /** the representation of the element for which this might open a popup */
    	private DockElementRepresentative representative;
        
    	/** whether currently the mouse gets clicked */
    	private boolean onMouseClick = false;
    	
    	/**
    	 * Creates a new observer
    	 * @param representative the element which represents the {@link Dockable}
    	 * of this observer
    	 */
    	public ComponentObserver( DockElementRepresentative representative ){
    		super( true );
    		this.representative = representative;
    	}
    	
    	@Override
    	public void mouseClicked( MouseEvent e ){
    	    if( isMenuOpen() )
    	        return;
    	    
    		if( isEnabled() && e.getClickCount() == 1 ){
    		    try{
    		        onMouseClick = true;
    		        popup( e.getComponent(), e.getX(), e.getY() );
    		    }
    		    finally{
    		        onMouseClick = false;
    		    }
    		}
    	}
    	
    	@Override
    	protected Point getPopupLocation( Component owner, Point location ) {
    	    location = new Point( location );
    	    location = SwingUtilities.convertPoint( owner, location, representative.getComponent() );
    	    location = representative.getPopupLocation( location, !onMouseClick );
    	    if( location == null )
    	        return null;
    	    else
    	        location = new Point( location );
    	    return SwingUtilities.convertPoint( representative.getComponent(), location, owner );
    	}
    	
        @Override
        protected Dockable getDockable() {
            return representative.getElement().asDockable();
        }

        @Override
        protected DockActionSource getActions() {
            return getDockable().getGlobalActionOffers();
        }
        
        @Override
        protected Object getSource(){
	        return representative;
        }

        @Override
        protected boolean isEnabled() {
            return true;
        }
    }
}
