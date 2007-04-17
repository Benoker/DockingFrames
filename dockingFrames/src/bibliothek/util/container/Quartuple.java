/*
 * Bibliothek License
 * ==================
 * 
 * Except where otherwise noted, all of the documentation and software included
 * in the bibliothek package is copyrighted by Benjamin Sigg.
 * 
 * Copyright (C) 2001-2005 Benjamin Sigg. All rights reserved.
 * 
 * This software is provided "as-is," without any express or implied warranty.
 * In no event shall the author be held liable for any damages arising from the
 * use of this software.
 * 
 * Permission is granted to anyone to use this software for any purpose,
 * including commercial applications, and to alter and redistribute it,
 * provided that the following conditions are met:
 * 
 * 1. All redistributions of source code files must retain all copyright
 *    notices that are currently in place, and this list of conditions without
 *    modification.
 * 
 * 2. All redistributions in binary form must retain all occurrences of the
 *    above copyright notice and web site addresses that are currently in
 *    place (for example, in the About boxes).
 * 
 * 3. The origin of this software must not be misrepresented; you must not
 *    claim that you wrote the original software. If you use this software to
 *    distribute a product, an acknowledgment in the product documentation
 *    would be appreciated but is not required.
 * 
 * 4. Modified versions in source or binary form must be plainly marked as
 *    such, and must not be misrepresented as being the original software.
 * 
 * 
 * Benjamin sigg
 * benjamin_sigg@gmx.ch
 * 
 */
 
/*
 * Created on 07.12.2004
 */
package bibliothek.util.container;

/**
 * @author Benjamin Sigg
 */
public class Quartuple<A,B,C,D> extends Triple<A,B,C>{
	private D d;
	
	public Quartuple(){
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
	
    @Override
	public Quartuple<A, B, C, D> clone(){
		return new Quartuple<A, B, C, D>( getA(), getB(), getC(), d );
	}
	
    @Override
	public boolean equals( Object o ){
		if( o instanceof Quartuple ){
			Quartuple s = (Quartuple)o;
			return super.equals( o ) && ( 
				(s.d == null && d == null) ||
				s.d.equals( d ));
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
