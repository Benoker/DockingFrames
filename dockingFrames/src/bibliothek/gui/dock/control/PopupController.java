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

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.SwingUtilities;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.ActionPopup;
import bibliothek.gui.dock.action.DockActionSource;
import bibliothek.gui.dock.event.DockRegisterListener;
import bibliothek.gui.dock.event.DockableListener;
import bibliothek.gui.dock.title.DockTitle;

/**
 * Adds listeners to all {@link Dockable Dockables} and {@link DockTitle DockTitles}.
 * Opens a popup-menu when the user triggers the popup-action.
 * @author Benjamin Sigg
 */
public class PopupController implements DockRegisterListener{
    /** tells which Dockable has which listener */
    private Map<Dockable, DockableObserver> listeners =
        new HashMap<Dockable, DockableObserver>();
    
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
    }
    
    public void dockableRegistered( DockController controller, Dockable dockable ) {
        if( !listeners.containsKey( dockable )){
            DockableObserver listener = new DockableObserver( dockable );
            dockable.addMouseInputListener( listener );
            dockable.addDockableListener( listener );
            listeners.put( dockable, listener );
            
            DockTitle[] titles = dockable.listBoundTitles();
            for( DockTitle title : titles ){
            	listener.titleBound( dockable, title );
            }
        }
    }
    
    public void dockableUnregistered( DockController controller, Dockable dockable ) {
        DockableObserver listener = listeners.remove( dockable );
        if( listener != null ){
            dockable.removeMouseInputListener( listener );
            dockable.removeDockableListener( listener );
            
            DockTitle[] titles = dockable.listBoundTitles();
            for( DockTitle title : titles ){
            	listener.titleUnbound( dockable, title );
            }
        }
    }
    
	public void dockStationRegistered( DockController controller, DockStation station ){
	    // ignore
	}

	public void dockStationRegistering( DockController controller, DockStation station ){
		// ignore
	}

	public void dockStationUnregistered( DockController controller, DockStation station ){
		// ignore
	}

	public void dockableRegistering( DockController controller, Dockable dockable ){
		// ignore
	}

	/**
     * A listener to a Dockable, lets the user
     * drag and drop a Dockable.
     * @author Benjamin Sigg
     */
    private class DockableObserver extends ComponentObserver implements DockableListener{
        private Map<DockTitle, ComponentObserver> listeners = new HashMap<DockTitle, ComponentObserver>();
        
        /**
         * Constructs a new listener
         * @param dockable the Dockable to observe
         */
        public DockableObserver( Dockable dockable ){
        	super( dockable, null );
        }
        

		public void titleBound( Dockable dockable, DockTitle title ){
			if( !listeners.containsKey( title )){
				ComponentObserver listener = new ComponentObserver( dockable, title );
				title.addMouseInputListener( listener );
				listeners.put( title, listener );
			}
		}
		
		public void titleUnbound( Dockable dockable, DockTitle title ){
			ComponentObserver listener = listeners.remove( title );
			if( listener != null ){
				title.removeMouseInputListener( listener );
			}
		}

		public void titleIconChanged( Dockable dockable, Icon oldIcon, Icon newIcon ){
			// ignore
		}

		public void titleTextChanged( Dockable dockable, String oldTitle, String newTitle ){
			// ignore
		}
    }
    
    /**
     * A mouse listener opening a popup menu when necessary.
     * @author Benjamin Sigg
     */
    private class ComponentObserver extends ActionPopup{
    	/** the dockable for which a listener might be opened */
    	protected Dockable dockable;
    	/** the observed title, can be <code>null</code> */
    	private DockTitle title;
    	
    	/**
    	 * Creates a new observer
    	 * @param dockable the element for which a popup might be opened
    	 * @param title the title which might be observed, can be <code>null</code>
    	 */
    	public ComponentObserver( Dockable dockable, DockTitle title ){
    		super( true );
    		this.dockable = dockable;
    		this.title = title;
    	}
    	
    	@Override
    	public void mouseClicked( MouseEvent e ){
    		if( title != null && isEnabled() ){
    			Point click = e.getPoint();
    			click = SwingUtilities.convertPoint( e.getComponent(), click, title.getComponent() );
    			Point popup = title.getPopupLocation( click );
    			if( popup != null ){
    				popup( title.getComponent(), popup.x, popup.y );
    			}
    		}
    	}
    	
        @Override
        protected Dockable getDockable() {
            return dockable;
        }

        @Override
        protected DockActionSource getSource() {
        	return dockable.getGlobalActionOffers();
        }

        @Override
        protected boolean isEnabled() {
            return true;
        }
    }
}
