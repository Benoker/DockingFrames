package bibliothek.util;

import java.util.Iterator;

public class ArrayIterator<E> implements Iterator<E>{
	private E[] array;
	private int index;
	
	public ArrayIterator( E[] array ){
		this.array = array;
	}

	public boolean hasNext() {
		return array != null && index < array.length;
	}
	
	public E next() {
		if( !hasNext() )
			throw new IllegalStateException( "no next available" );
		
		return array[index++];
	}
	
	public void remove() {
		throw new UnsupportedOperationException();
	}
}
