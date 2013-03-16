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
 * Represents a set of {@link Class classes} collected in groups of packages.
 * @author Benjamin Sigg
 */
public class DocRoot implements Iterable<DocPackage>{
	/** all the known packages */
	private Map<String, DocPackage> packages = new HashMap<String, DocPackage>();
	
	private Map<String, DocClass> cache = new HashMap<String, DocClass>();
	
	/**
	 * Adds <code>clazz</code> to the set of known classes, does not include inner classes.
	 * @param clazz the additional class to save
	 */
	public void add( Class<?> clazz ){
		String name = clazz.getPackage().getName();
		DocPackage pack = packages.get( name );
		if( pack == null ){
			pack = new DocPackage( this, name );
			packages.put( name, pack );
		}
		pack.add( clazz );
	}
	
	/**
	 * Searches (or creates) the {@link DocClass} for <code>clazz</code>.
	 * @param clazz the class to search
	 * @return the representation of <code>clazz</code>
	 */
	public DocClass get( Class<?> clazz ){
		String name = clazz.getName();
		DocClass result = cache.get( name );
		if( result == null ){
			String packageName = clazz.getPackage().getName();
			DocPackage pack = packages.get( packageName );
			if( pack != null ){
				result = pack.get( clazz );
			}
			else{
				pack = new DocPackage( this, packageName );
			}
			if( result == null ){
				result = new DocClass( pack, clazz );
			}
			cache.put( name, result );
		}
		return result;
	}
	
	@Override
	public Iterator<DocPackage> iterator(){
		List<DocPackage> result = new ArrayList<DocPackage>( packages.values() );
		Collections.sort( result, new Comparator<DocPackage>(){
			private Collator collator = Collator.getInstance();
			
			@Override
			public int compare( DocPackage a, DocPackage b ){
				return collator.compare( a.getName(), b.getName() );
			}
		} );
		return result.iterator();
	}

	/**
	 * Gets a (translated?) string for the key <code>id</code>.
	 * @param id the identifier
	 * @return the text or <code>null</code>
	 */
	public String getString( String id ){
		throw new UnsupportedOperationException( "not yet supported" );
	}
}
