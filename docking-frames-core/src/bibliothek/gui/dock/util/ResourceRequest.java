/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2011 Benjamin Sigg
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 * Benjamin Sigg
 * benjamin_sigg@gmx.ch
 * CH - Switzerland
 */
package bibliothek.gui.dock.util;


/**
 * A {@link ResourceRequest} describes an algorithm for finding or creating a resource by calling
 * a set of resource providers or factories in a specific order.
 * @author Benjamin Sigg
 * @param <T> the kind of resource this {@link ResourceRequest} provides
 */
public abstract class ResourceRequest<T> {
	/** the current answer to this request */
	private T answer;
	/** whether this request has been answered */
	private boolean answered = false;
	
	/** whether {@link #request()} has been called */
	private boolean requesting = false;
	
	/**
	 * Called when the requested resource changed.
	 * @param previousResource the old resource object, may be <code>null</code>
	 * @param newResource the new resource object, may be <code>null</code>
	 */
	protected abstract void answer( T previousResource, T newResource );
	
	/**
	 * Asks for a new resource and may trigger {@link #answer(Object, Object)}. This method
	 * is made protected, subclasses can expose it, or write another method requiring additional
	 * parameters, that calls this method.
	 */
	protected void request(){
		answered = false;
		T old = answer;
				
		try{
			requesting = true;
			executeRequestList();
		}
		finally{
			requesting = false;
		}

		if( old != answer ){
			answer( old, answer );
		}
	}
	
	/**
	 * Tells whether {@link #answer(Object)} was called since the last {@link #request()}.
	 * @return <code>true</code> if there is an answer
	 */
	public boolean isAnswered(){
		return answered;
	}
	
	/**
	 * Asks all sources for a new resource, needs to stop as soon
	 * as one source called {@link #answer(Object)} (this can be queried
	 * with {@link #isAnswered()}).
	 */
	protected abstract void executeRequestList();
		
	/**
	 * Asks this request to simulate a call to {@link #request()} which is
	 * answered with <code>null</code>
	 */
	public void requestNull(){
		if( answer != null ){
			T old = answer;
			answer = null;
			answer( old, answer );
		}
	}
	
	/**
	 * Informs this request that <code>resource</code> should be user. This method
	 * can be called more than once to use different resources. Subclasses may put strict demands on
	 * what objects are valid resource objects. This method is not intended to be called in a generic way, callers
	 * must be aware of the restrictions a subclass requires.
	 * An answer must fulfill some rules:
	 * 
	 * @param resource the new resource or <code>null</code>
	 * @throws IllegalArgumentException if the resource does not met the specifications a subclass requires
	 * @throws IllegalStateException if {@link #request()} is not currently executing
	 */
	public void answer( T resource ){
		if( !requesting ){
			throw new IllegalStateException( "not requesting a title" );
		}
		
		validate( resource );
		answered = true;
		answer = resource;
	}
	
	/**
	 * Called by {@link #answer(Object)}, this method ensure that <code>resource</code> is a valid answer. The method
	 * throws an {@link IllegalArgumentException} if not.
	 * @param resource the resource to check
	 * @throws IllegalArgumentException if <code>resource</code> is not a valid resource
	 */
	protected abstract void validate( T resource );
	
	/**
	 * Gets the last answer made to this request.
	 * @return the last answer, may be <code>null</code>
	 */
	public T getAnswer(){
		return answer;
	}
}
