package bibliothek.help.control;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import bibliothek.help.model.Entry;

/**
 * The <code>URManager</code> is used by a {@link LinkManager} to store
 * the history of undo/redo-steps. Any client might use the methods of this 
 * <code>URManager</code> to <i>undo</i> or <i>redo</i> an action.
 * @author Benjamin Sigg
 */
public class URManager {
    /** the available steps */
    private LinkedList<Step> stack = new LinkedList<Step>();
    /**
     * The {@link Step} that is currently selected (that would be undone 
     * if {@link #undo()} is used) 
     */
    private int current = -1;
    /** whether this <code>URManager</code> is currently running an operation */
    private boolean onChange = false;    

    /** the list of listeners to inform whenever the {@link #stack()} and {@link #current selection} changes */
    private List<URListener> listeners = new ArrayList<URListener>();
    /** the list of elements whose contents must be considered when storing or applying a {@link Step} */
    private List<Undoable> undoables = new ArrayList<Undoable>();
    
    /**
     * Adds a new element whose content will be considered when storing
     * or applying a {@link Step}.
     * @param undoable the mutable element showing an {@link Entry}
     */
    public void register( Undoable undoable ){
    	undoables.add( undoable );
    }
    
    /**
     * Gets the index of the currently selected {@link Step}. That's the
     * index of the <code>Step</code> that would be undone when {@link #undo()}
     * is called.
     * @return the current step or -1
     */
    public int getCurrent(){
		return current;
	}
    
    /**
     * Gets the currently available <code>Step</code>s as an independent
     * array.
     * @return the stack of <code>Step</code>s
     */
    public Step[] stack(){
    	return stack.toArray( new Step[ stack.size() ] );
    }
    
    /**
     * Adds a listener to this manager, the listener will be informed whenever
     * the {@link #stack() stack} or the {@link #getCurrent() selection} changes.
     * @param listener the new observer
     */
    public void addListener( URListener listener ){
        listeners.add( listener );
    }
    
    /**
     * Removes a listener that was previously added from this manager.
     * @param listener the listener to remove
     */
    public void removeListener( URListener listener ){
        listeners.remove( listener );
    }
    
    /**
     * Informs all registered {@link URListener}s that the content
     * of this manager has changed.
     */
    protected void fire(){
        for( URListener listener : listeners )
            listener.changed( this );
    }
    
    /**
     * Informs this manager that the selected set of pages has
     * changed. This manager will add a new {@link Step} to the {@link #stack()},
     * and might delete some <code>Step</code>s when necessary.
     * <br>Clients should not call this method unless they created
     * the {@link URManager} for themselves.
     * @param entry the new selection
     */
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
    
    /**
     * Selects the content of the <code>index</code>'th {@link Step} to
     * show.
     * @param index the index of the newly selected <code>Step</code>
     */
    public void moveTo( int index ){
    	if( index != current ){
    		current = index;
    		stack.get( current ).apply();
    		fire();
    	}
    }
    
    /**
     * Tells whether the {@link #undo()}-action will have any effect or not. 
     * @return <code>true</code> if <i>undo</i> is possible
     */
    public boolean isUndoable(){
        return current > 0;
    }

    /**
     * Selects the {@link Step} that is one below the {@link #getCurrent() current}
     * <code>Step</code> in the {@link #stack()} and ensures that the
     * content of this new <code>Step</code> is shown. Does nothing if there
     * is no available <code>Step</code>.
     */
    public void undo(){
        if( isUndoable() ){
        	onChange = true;
            current--;
            stack.get( current ).apply();
            onChange = false;
           	fire();
        }
    }
    
    /**
     * Tells whether the {@link #redo()}-action will have and effect or not.
     * @return <code>true</code> if <i>redo</i> is possible
     */
    public boolean isRedoable(){
        return current+1 < stack.size();
    }
    
    /**
     * Selects the {@link Step} that is one above the {@link #getCurrent() current}
     * <code>Step</code> in the {@link #stack()} and ensures that the
     * content of this new <code>Step</code> is shown. Does nothing if there
     * is no available <code>Step</code>.
     */
    public void redo(){
        if( isRedoable() ){
            onChange = true;
            current++;
            stack.get( current ).apply();
            onChange = false;
            fire();
        }
    }
    
    /**
     * A <code>Step</code> stores for every known {@link Undoable} which
     * {@link Entry} it showed when the <code>Step</code> was created. These
     * <code>Entries</code> can be set again using {@link #apply()}.
     * @author Benjamin Sigg
     *
     */
    public class Step{
        /** a small description of this <code>Step</code> */
    	private String title;
    	/** a map telling for each {@link Undoable} which {@link Entry} it showed */
    	private Map<Undoable, Entry> selection;
    	
    	/**
    	 * Creates a new <code>Step</code>
    	 * @param entry the most important {@link Entry} of this <code>Step</code>,
    	 * used to get a {@link #getTitle() small description}
    	 */
    	public Step( Entry entry ){
    		title = entry.getTitle();
    		selection = new HashMap<Undoable, Entry>();
    		for( Undoable undoable : undoables )
    			selection.put( undoable, undoable.getCurrent() );
    	}
    	
    	/**
    	 * A small description of this <code>Step</code>.
    	 * @return the description
    	 */
    	public String getTitle(){
			return title;
		}
    	
    	/**
    	 * Ensures that every {@link Undoable} shows the same {@link Entry} as
    	 * it showed when this <code>Step</code> was created.
    	 */
    	public void apply(){
    		for( Map.Entry<Undoable, Entry> entry : selection.entrySet() )
    			entry.getKey().setCurrent( entry.getValue() );
    	}
    }
}
