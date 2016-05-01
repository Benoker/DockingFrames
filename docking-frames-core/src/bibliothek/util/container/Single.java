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
 * An object containing one generic fields.
 * @author Benjamin Sigg
 * @param <A> type of the first field
 */
public class Single<A> implements Cloneable{
	private A a;
	
	public Single(){
        // nothing to do
	}
	public Single( A a ){
		this.a = a;
	}
	
	public void setA( A a ){
		this.a = a;
	}
	
	public A getA(){
		return a;
	}
	
    @SuppressWarnings("unchecked")
    @Override
	public Single<A> clone(){
        try{
            return (Single<A>)super.clone();
        }
        catch( CloneNotSupportedException ex ){
            throw new RuntimeException( ex );
        }
	}
	
	@Override
	public boolean equals( Object o ){
		if( o.getClass() == getClass() ){
			Single<?> s = (Single<?>)o; 
			return (s.a == null && a == null) || (s.a != null && s.a.equals( a ) );
		}
		return false;
	}
	
    @Override
	public int hashCode(){
		return a == null ? 0 : a.hashCode();
	}
	
    @Override
	public String toString(){
		return getClass().getName() + "[a=" + a + "]";
	}
}
