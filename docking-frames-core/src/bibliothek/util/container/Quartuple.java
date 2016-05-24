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
 * An object containing four generic fields.
 * @author Benjamin Sigg
 * @param <A> type of the first field
 * @param <B> type of the second field
 * @param <C> type of the third field
 * @param <D> type of the fourth field
 */
public class Quartuple<A,B,C,D> extends Triple<A,B,C>{
	private D d;
	
	public Quartuple(){
        // nothing to do
	}
	
	public Quartuple( A a, B b, C c, D d ){
		super( a, b, c );
		this.d = d;
	}

	public void setD( D d ){
		this.d = d;
	}
	
	public D getD(){
		return d;
	}
	
    @SuppressWarnings("unchecked")
    @Override
	public Quartuple<A, B, C, D> clone(){
		return (Quartuple<A, B, C, D>)super.clone();
	}
	
    @SuppressWarnings("unchecked")
	@Override
	public boolean equals( Object o ){
		if (this == o) {
			return true;
		}

		if (o == null) {
			return false;
		}

		if (this.getClass() == o.getClass()) {
			Quartuple s = (Quartuple)o;
			return super.equals( o ) && ( (s.d == null && d == null) || (s.d != null && s.d.equals( d )));
		}

		return false;
	}
	
    @Override
	public int hashCode(){
		return super.hashCode() ^ (d == null ? 0 : d.hashCode());
	}
	
    @Override
	public String toString(){
		return getClass().getName() + "[a=" + getA() + ", b=" + getB() + 
			", c=" + getC() + ", d=" + d + "]";
	}
}
