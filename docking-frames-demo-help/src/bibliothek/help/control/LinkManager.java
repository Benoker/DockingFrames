package bibliothek.help.control;

import java.util.*;

import bibliothek.help.model.Entry;
import bibliothek.help.model.HelpModel;

/**
 * The <code>LinkManager</code> is used to selected the set of pages which
 * should be shown. Whenever the user clicks onto a link, the 
 * method {@link #select(String)} has to be called.<br>
 * Clients can implement {@link Linking} if they want to be informed when
 * the selected pages change. This manager also supports undo/redo through
 * an {@link URManager}.
 * @author Benjamin Sigg
 */
public class LinkManager {
    /** the observer of this manager */
    private List<Linking> views = new ArrayList<Linking>();
    /** set of available pages */
    private HelpModel model;
    /** set of available undo/redo-steps, modified by this manager */
    private URManager ur;
    
    /**
     * Sets the set of available pages, restarts the {@link URManager}.
     * @param model the pages
     */
    public void setModel( HelpModel model ) {
        this.model = model;
        ur = new URManager();
    }
    
    /**
     * Gets the manager for undo/redo operations. The {@link URManager} is
     * automatically modified by this <code>LinkManager</code>.
     * @return the undo/redo-manager
     */
    public URManager getUR(){
		return ur;
	}
    
    /**
     * Adds an observer to this manager.
     * @param view the new observer
     */
    public void add( Linking view ){
        views.add( view );
    }
    
    /**
     * Selects a new set of pages. This method will search an
     * {@link Entry} in the {@link HelpModel} with the name <code>link</code>.
     * Then the {@link Entry#getDetails() further details} - property of the
     * selected <code>Entry</code> will be read and used to search additional
     * pages that should be shown. This reading goes on as recursion until
     * no new <code>Entries</code> can be found. The resulting list of
     * <code>Entries</code> is forwarded to all registered {@link Linking}s. 
     * @param link the newly selected link
     */
    public void select( String link ){
        Set<String> selections = new HashSet<String>();
        LinkedList<String> queue = new LinkedList<String>();
        List<Entry> list = new LinkedList<Entry>();
        
        Entry selection = model.get( link ) ;
        if( selection != null ){
        	queue.add( link );
        
	        while( !queue.isEmpty() ){
	            String next = queue.removeFirst();
	            if( selections.add( next )){
	                Entry entry = model.get( next );
	                if( entry != null ){
	                    list.add( entry );
	                    
	                    for( String details : entry.getDetails() )
	                        queue.add( details );
	                }
	            }
	        }
	        
	        for( Linking view : views )
	            view.selected( list );
	        
	        ur.selected( selection );
        }
    }
}
