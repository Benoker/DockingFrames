package bibliothek.util;

import java.util.Iterator;

public abstract class TranslatingIterator<S,D> implements Iterator<D> {
	private Iterator<S> source;
	
	public TranslatingIterator( Iterator<S> source ){
		this.source = source;
	}
	
	public boolean hasNext() {
		return source.hasNext();
	}
	
	public D next() {
		return translate( source.next() );
	}
	
	public void remove() {
		source.remove();
	}
	
	protected abstract D translate( S value );
}
