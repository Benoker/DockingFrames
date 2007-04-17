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
public class Triple<A, B, C> extends Tuple<A, B>{
	private C c;
	
	public Triple(){
		
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
	
    @Override
	public Triple<A, B, C> clone(){
		return new Triple<A, B, C>( getA(), getB(), getC() );
	}
	
    @Override
	public boolean equals( Object o ){
		if( o instanceof Triple ){
			Triple s = (Triple)o;
			return super.equals( o ) && ( 
				(s.c == null && c == null) ||
				s.c.equals( c ));
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
