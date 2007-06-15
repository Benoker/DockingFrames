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
            
            DockTitle[] titles = dockable.listBindedTitles();
            for( DockTitle title : titles ){
            	listener.titleBinded( dockable, title );
            }
        }
    }
    
    public void dockableUnregistered( DockController controller, Dockable dockable ) {
        DockableObserver listener = listeners.remove( dockable );
        if( listener != null ){
            dockable.removeMouseInputListener( listener );
            dockable.removeDockableListener( listener );
            
            DockTitle[] titles = dockable.listBindedTitles();
            for( DockTitle title : titles ){
            	listener.titleUnbinded( dockable, title );
            }
        }
    }
    
	public void dockStationRegistered( DockController controller, DockStation station ){
		// TODO Auto-generated method stub
		
	}

	public void dockStationRegistering( DockController controller, DockStation station ){
		// TODO Auto-generated method stub
		
	}

	public void dockStationUnregistered( DockController controller, DockStation station ){
		// TODO Auto-generated method stub
		
	}

	public void dockableRegistering( DockController controller, Dockable dockable ){
		// TODO Auto-generated method stub
		
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
        

		public void titleBinded( Dockable dockable, DockTitle title ){
			if( !listeners.containsKey( title )){
				ComponentObserver listener = new ComponentObserver( dockable, title );
				title.addMouseInputListener( listener );
				listeners.put( title, listener );
			}
		}
		
		public void titleUnbinded( Dockable dockable, DockTitle title ){
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
    			SwingUtilities.convertPoint( e.getComponent(), click, title.getComponent() );
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
            return controller.listOffers( dockable );
        }

        @Override
        protected boolean isEnabled() {
            return true;
        }
    }
}
