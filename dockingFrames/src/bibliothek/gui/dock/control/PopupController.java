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
import bibliothek.gui.dock.action.DockActionSource;
import bibliothek.gui.dock.event.DockControllerRepresentativeListener;

/**
 * Adds listeners to all {@link DockElementRepresentative}s.
 * Opens a popup-menu when the user triggers the popup-action.
 * @author Benjamin Sigg
 */
public class PopupController implements DockControllerRepresentativeListener{
    /** tells which Dockable has which listener */
    private Map<DockElementRepresentative, ComponentObserver> listeners =
        new HashMap<DockElementRepresentative, ComponentObserver>();
    
    /** the controller for which this popup-controller works */
    private DockController controller;
    
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
        protected DockActionSource getSource() {
            return getDockable().getGlobalActionOffers();
        }

        @Override
        protected boolean isEnabled() {
            return true;
        }
    }
}
