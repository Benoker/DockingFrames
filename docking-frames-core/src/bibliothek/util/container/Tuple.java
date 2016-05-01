/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2007 Benjamin Sigg
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

/*
 * Created on 07.12.2004
 */
package bibliothek.util.container;

/**
 * An object containing two generic fields.
 * @author Benjamin Sigg
 * @param <A> type of the first field
 * @param <B> type of the second field
 */
public class Tuple<A, B> extends Single<A>{
	public static <A, B> Tuple<A, B> of( A a, B b ){
		return new Tuple<A,B>( a, b );
	}
	
	private B b;
	
	public Tuple(){
		// nothing to do
	}
	public Tuple( A a, B b ){
		super( a );
		this.b = b;
	}
	
	public void setB( B b ){
		this.b = b;
	}
	
	public B getB(){
		return b;
	}
	
    @SuppressWarnings("unchecked")
    @Override
	public Tuple<A, B> clone(){
        return (Tuple<A,B>)super.clone();
	}
	
	@Override
	public boolean equals( Object o ){
    	if( o.getClass() == getClass() ){
			Tuple<?, ?> s = (Tuple<?, ?>)o;
			return super.equals( o ) && ( (s.b == null && b == null) || (s.b != null && s.b.equals( b )));
		}
		return false;
	}
	
    @Override
	public int hashCode(){
		return super.hashCode() ^ (b == null ? 0 : b.hashCode());
	}
	
    @Override
	public String toString(){
		return getClass().getName() + "[a=" + getA() + ", b=" + b + "]";
	}
}
