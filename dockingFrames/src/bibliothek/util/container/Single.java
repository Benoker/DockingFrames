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
public class Single<A>{
	private A a;
	
	public Single(){
		
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
	
    @Override
	public Single<A> clone(){
		return new Single<A>( a );
	}
	
    @Override
	public boolean equals( Object o ){
		if( o instanceof Single ){
			Single s = (Single)o;
			return (s.a == null && a == null) ||
				s.a.equals( a );
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
