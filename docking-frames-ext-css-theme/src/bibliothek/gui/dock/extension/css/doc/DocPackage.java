/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2013 Benjamin Sigg
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
package bibliothek.gui.dock.extension.css.doc;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Represents a single package of classes (does not include sub packages).
 * @author Benjamin Sigg
 */
public class DocPackage implements Iterable<DocClass>{
	/** the root of the application */
	private DocRoot root;
	
	/** the fully qualified name of this package */
	private String name;

	/** all the classes in this package */
	private Map<String, DocClass> classes = new HashMap<String, DocClass>();
	
	/**
	 * Creates a new package.
	 * @param root the root element of the documentation
	 * @param name the fully qualified name of this package
	 */
	public DocPackage( DocRoot root, String name ){
		this.root = root;
		this.name = name;
	}
	
	/**
	 * Gets the root element of the documentation.
	 * @return the root element
	 */
	public DocRoot getRoot(){
		return root;
	}

	/**
	 * Gets the fully qualified name of this package.
	 * @return the name
	 */
	public String getName(){
		return name;
	}
	
	/**
	 * Adds <code>clazz</code> to the set of known {@link Class classes}. Does nothing if
	 * <code>clazz</code> is already known.
	 * @param clazz the new class
	 */
	public void add( Class<?> clazz ){
		String name = clazz.getSimpleName();
		if( !classes.containsKey( name )){
			DocClass doc = new DocClass( this, clazz );
			classes.put( name, doc );
		}
	}
	
	/**
	 * Searches for the {@link DocClass} for <code>clazz</code>
	 * @param clazz the class to search for
	 * @return the representation of <code>clazz</code> or <code>null</code> if not found
	 */
	public DocClass get( Class<?> clazz ){
		return classes.get( clazz.getSimpleName() );
	}
	
	@Override
	public Iterator<DocClass> iterator(){
		List<DocClass> copy = new ArrayList<DocClass>( classes.values() );
		Collections.sort( copy, new Comparator<DocClass>(){
			private Collator collator = Collator.getInstance();
			
			@Override
			public int compare( DocClass a, DocClass b ){
				return collator.compare( a.getName(), b.getName() );
			}
		} );
		return copy.iterator();
	}
}
