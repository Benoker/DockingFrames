package bibliothek.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * An iterator over many other iterators.
 * @author Benjamin Sigg
 */
public class MultiIterator<T> implements Iterator<T> {
    private Iterator<? extends T>[] iterators;
    private int current = 0;
    
    public MultiIterator( Iterator<? extends T>...iterators ){
        if( iterators == null )
            throw new IllegalArgumentException( "Null is not allowed" );
        this.iterators = iterators;
        
        step();
    }
    
    @SuppressWarnings( "unchecked" )
    public MultiIterator( Collection<? extends Iterator<T>> iterators ){
        if( iterators == null )
            throw new IllegalArgumentException( "Null is not allowed" );
        this.iterators = iterators.toArray( new Iterator[ iterators.size() ] );
        
        step();
    }
    
    public boolean hasNext() {
        return current < iterators.length && iterators[current].hasNext();
    }

    public T next() {
        if( current >= iterators.length )
            throw new NoSuchElementException();
        
        T next = iterators[current].next();
        step();
        return next;
    }
    
    private void step(){
        while( !iterators[current].hasNext() ){
            current++;
            
            if( current >= iterators.length )
                break;
        }
    }

    public void remove() {
        iterators[current].remove();
    }
}
