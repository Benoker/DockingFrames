package bibliothek.help.control;

import java.util.*;

import bibliothek.help.model.Entry;
import bibliothek.help.model.HelpModel;

public class LinkManager {
    private List<Linking> views = new ArrayList<Linking>();
    private HelpModel model;
    private URManager ur;
    
    public void setModel( HelpModel model ) {
        this.model = model;
        ur = new URManager();
    }
    
    public URManager getUR(){
		return ur;
	}
    
    public void add( Linking view ){
        views.add( view );
    }
    
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
