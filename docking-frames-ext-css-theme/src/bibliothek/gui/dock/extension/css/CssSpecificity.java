/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2012 Benjamin Sigg
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
package bibliothek.gui.dock.extension.css;

/**
 * Tells how important a {@link CssSelector} is.
 * @author Benjamin Sigg
 */
public class CssSpecificity implements Comparable<CssSpecificity>{
	private int style;
	private int numberOfIds;
	private int numberOfAttributes;
	private int numberOfElements;
	
	/**
	 * 
	 * @param style 1 if the declaration is from is a 'style' attribute rather than a rule with a selector, 0 otherwise (= a) 
	 * (In HTML, values of an element's "style" attribute are style sheet rules. These rules have no selectors, so a=1, b=0, c=0, and d=0.)
	 * @param numberOfIds the number of ID attributes in the selector (= b)
	 * @param numberOfAttributes the number of other attributes and pseudo-classes in the selector (= c)
	 * @param numberOfElements the number of element names and pseudo-elements in the selector (= d)
	 */
	public CssSpecificity( int style, int numberOfIds, int numberOfAttributes, int numberOfElements ){ 
		this.style = style;
		this.numberOfIds = numberOfIds;
		this.numberOfAttributes = numberOfAttributes;
		this.numberOfElements = numberOfElements;
	}
	
	@Override
	public int compareTo( CssSpecificity o ){
		if( o.style > style ){
			return 1;
		}
		else if( o.style < style ){
			return -1;
		}
		
		if( o.numberOfIds > numberOfIds ){
			return 1;
		}
		else if( o.numberOfIds < numberOfIds ){
			return -1;
		}
		
		if( o.numberOfAttributes > numberOfAttributes ){
			return 1;
		}
		else if( o.numberOfAttributes < numberOfAttributes ){
			return -1;
		}
		
		if( o.numberOfElements > numberOfElements ){
			return 1;
		}
		else if( o.numberOfElements < numberOfAttributes ){
			return -1;
		}
		
		return 0;
	}
}
