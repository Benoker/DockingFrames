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
 * An object containing three generic fields.
 * @author Benjamin Sigg
 * @param <A> type of the first field
 * @param <B> type of the second field
 * @param <C> type of the third field
 */
public class Triple<A, B, C> extends Tuple<A, B>{
	private C c;
	
	public Triple(){
        // nothing to do
	}
	
	public Triple( A a, B b, C c ){
		super( a, b );
		this.c = c;
	}
	
	public void setC( C c ){
		this.c = c;
	}
	
	public C getC(){
		return c;
	}
	
    @SuppressWarnings("unchecked")
    @Override
	public Triple<A, B, C> clone(){
		return (Triple<A, B, C>)super.clone();
	}
	
    @SuppressWarnings("unchecked")
	@Override
	public boolean equals( Object o ){
		if (o == this) {
			return true;
		}

		if (o == null) {
			return false;
		}

		if (o.getClass() == this.getClass()){
			Triple s = (Triple)o;
			return super.equals( o ) && ( (s.c == null && c == null) || (s.c != null && s.c.equals( c )));
		}

		return false;
	}
	
    @Override
	public int hashCode(){
		return super.hashCode() ^ (c == null ? 0 : c.hashCode());
	}
	
    @Override
    public String toString(){
		return getClass().getName() + "[a=" + getA() + ", b=" + getB() + ", c=" + c + "]";
	}
}
