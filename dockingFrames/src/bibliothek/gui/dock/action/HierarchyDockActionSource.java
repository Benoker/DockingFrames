package bibliothek.gui.dock.action;

import java.util.Iterator;

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.event.DockActionSourceListener;
import bibliothek.gui.dock.event.DockHierarchyEvent;
import bibliothek.gui.dock.event.DockHierarchyListener;

/**
 * A {@link DockActionSource} which observes the hierarchy of a {@link bibliothek.gui.Dockable}
 * and changes its content using {@link bibliothek.gui.DockController#listOffers(bibliothek.gui.Dockable)}.<br>
 * Clients using this source must call {@link #bind()} to connect the source with its {@link Dockable},
 * and {@link #unbind()} to free resources.
 * @author Benjamin Sigg
 */
public class HierarchyDockActionSource extends AbstractDockActionSource {
	/** The observed Dockable */
	private Dockable dockable;
	
	/** The number of times this source was bound */
	private int bound = 0;
	
	/** A listener to the hierarchy of {@link #dockable} and the source fetched from the controller */
	private Listener listener = new Listener();
	
	/** the source from which currently actions are fetched, can be <code>null</code> */
	private DockActionSource source;
	
	/**
	 * Creates a new source.
	 * @param dockable the Dockable to observe
	 */
	public HierarchyDockActionSource( Dockable dockable ){
		this.dockable = dockable;
		update();
	}
	
	/**
	 * Ensures that this source observes its Dockable.
	 */
	public void bind(){
		if( bound == 0 ){
			dockable.addDockHierarchyListener( listener );
			update();
		}
		bound++;
	}
	
	/**
	 * Ensures that this source frees resources.
	 */
	public void unbind(){
		bound--;
		if( bound == 0 ){
			dockable.removeDockHierarchyListener( listener );
		}
	}
	
	/**
	 * Updates the list of actions known to this source.
	 */
	public void update(){
		int oldSize = getDockActionCount();
		if( source != null ){
			source.removeDockActionSourceListener( listener );
			source = null;
		}
		
		if( oldSize > 0 )
			fireRemoved( 0, oldSize-1 );
		
		DockController controller = dockable.getController();
		if( controller != null )
			source = controller.listOffers( dockable );
		
		if( source != null && !listeners.isEmpty() ){
			source.addDockActionSourceListener( listener );
		}
		
		int newSize = getDockActionCount();
		if( newSize > 0 ){
			fireAdded( 0, newSize-1 );
		}
	}
	
	@Override
	public void addDockActionSourceListener( DockActionSourceListener listener ){
		if( listeners.isEmpty() && source != null )
			source.addDockActionSourceListener( this.listener );
		super.addDockActionSourceListener( listener );
	}
	
	@Override
	public void removeDockActionSourceListener( DockActionSourceListener listener ){
		super.removeDockActionSourceListener( listener );
		if( listeners.isEmpty() && source != null )
			source.removeDockActionSourceListener( this.listener );
	}
	
	public DockAction getDockAction( int index ){
		if( source == null )
			throw new IllegalArgumentException( "index out of bounds" );
		else
			return source.getDockAction( index );
	}

	public int getDockActionCount(){
		if( source == null )
			return 0;
		else
			return source.getDockActionCount();
	}

	public LocationHint getLocationHint(){
		if( source == null )
			return LocationHint.UNKNOWN;
		else
			return source.getLocationHint();
	}

	public Iterator<DockAction> iterator(){
		if( source == null ){
			return new Iterator<DockAction>(){
				public boolean hasNext(){
					return false;
				}

				public DockAction next(){
					return null;
				}

				public void remove(){
					// ignore
				}
			};
		}
		else{
			return source.iterator();
		}
	}

	/**
	 * A listener used to observe the Dockable and the source of the enclosing
	 * class.
	 * @author Benjamin Sigg
	 */
	private class Listener implements DockHierarchyListener, DockActionSourceListener{
		public void controllerChanged( DockHierarchyEvent event ){
			update();
		}
		public void hierarchyChanged( DockHierarchyEvent event ){
			update();
		}
		
		public void actionsAdded( DockActionSource source, int firstIndex, int lastIndex ){
			fireAdded( firstIndex, lastIndex );
		}
		
		public void actionsRemoved( DockActionSource source, int firstIndex, int lastIndex ){
			fireRemoved( firstIndex, lastIndex );
		}
	}
}
