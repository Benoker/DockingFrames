package bibliothek.help.control;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import bibliothek.help.model.Entry;

public class URManager implements Linking{
    private LinkedList<Entry> stack = new LinkedList<Entry>();
    private int current = -1;
    private boolean onChange = false;    
    private LinkManager links;
    
    private List<URListener> listeners = new ArrayList<URListener>();
    
    public URManager( LinkManager links ){
        this.links = links;
        links.add( this );
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
    
    public void selected( List<Entry> list ) {
        if( !onChange ){
            int remove = stack.size() - current;
            while( --remove > 0 )
                stack.removeLast();
            
            stack.addLast( list.get( 0 ) );
            current = stack.size()-1;
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
            Entry entry = stack.get( current );
            links.select( entry.getType() + ":" + entry.getId() );
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
            Entry entry = stack.get( current );
            links.select( entry.getType() + ":" + entry.getId() );
            onChange = false;
            fire();
        }
    }
}
