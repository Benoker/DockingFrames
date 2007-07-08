package bibliothek.help.control;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import bibliothek.help.model.Entry;

public class URManager {
    private LinkedList<Step> stack = new LinkedList<Step>();
    private int current = -1;
    private boolean onChange = false;    

    private List<URListener> listeners = new ArrayList<URListener>();
    private List<Undoable> undoables = new ArrayList<Undoable>();
    
    public void register( Undoable undoable ){
    	undoables.add( undoable );
    }
    
    public int getCurrent(){
		return current;
	}
    
    public Step[] stack(){
    	return stack.toArray( new Step[ stack.size() ] );
    }
    
    public void addListener( URListener listener ){
        listeners.add( listener );
    }
    
    public void removeListener( URListener listener ){
        listeners.remove( listener );
    }
    
    protected void fire(){
        for( URListener listener : listeners )
            listener.changed( this );
    }
    
    public void selected( Entry entry ) {
        if( !onChange ){
            int remove = stack.size() - current;
            while( --remove > 0 )
                stack.removeLast();
            
            stack.addLast( new Step( entry ) );
            
            while( stack.size() > 25 )
            	stack.removeFirst();
            
            current = stack.size()-1;
            fire();
        }
    }
    
    public void moveTo( int index ){
    	if( index != current ){
    		current = index;
    		stack.get( current ).apply();
    		fire();
    	}
    }
    
    public boolean isUndoable(){
        return current > 0;
    }

    public void undo(){
        if( isUndoable() ){
        	onChange = true;
            current--;
            stack.get( current ).apply();
            onChange = false;
           	fire();
        }
    }
    
    public boolean isRedoable(){
        return current+1 < stack.size();
    }
    
    public void redo(){
        if( isRedoable() ){
            onChange = true;
            current++;
            stack.get( current ).apply();
            onChange = false;
            fire();
        }
    }
    
    public class Step{
    	private String title;
    	private Map<Undoable, Entry> selection;
    	
    	public Step( Entry entry ){
    		title = entry.getTitle();
    		selection = new HashMap<Undoable, Entry>();
    		for( Undoable undoable : undoables )
    			selection.put( undoable, undoable.getCurrent() );
    	}
    	
    	public String getTitle(){
			return title;
		}
    	
    	public void apply(){
    		for( Map.Entry<Undoable, Entry> entry : selection.entrySet() )
    			entry.getKey().setCurrent( entry.getValue() );
    	}
    }
}
