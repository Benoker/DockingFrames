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

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Represents a single {@link Class}.
 * @author Benjamin Sigg
 */
public class DocClass implements Iterable<DocProperty>{
	/** the package containing <code>this</code> */
	private DocPackage pack;
	
	/** the class represented by this */
	private Class<?> clazz;
	
	/** all the properties found in {@link #clazz} */
	private List<DocProperty> properties;
	
	/** all the paths found in {@link #clazz} */
	private Map<String, CssDocPath> paths;
	
	/**
	 * Creates a new representation of <code>clazz</code>
	 * @param pack the package containing <code>this</code>
	 * @param clazz the class to represent
	 */
	public DocClass( DocPackage pack, Class<?> clazz ){
		this.pack = pack;
		this.clazz = clazz;
	}
	
	/**
	 * Gets the root element of the documentation.
	 * @return the root element
	 */
	public DocRoot getRoot(){
		return pack.getRoot();
	}
	
	/**
	 * Gets the name of this class (not the qualified name).
	 * @return the name
	 */
	public String getName(){
		return clazz.getSimpleName();
	}
	
	@Override
	public Iterator<DocProperty> iterator(){
		if( properties == null ){
			initProperties();
		}
		return Collections.unmodifiableList( properties ).iterator();
	}
	
	private void initProperties(){
		properties = new ArrayList<DocProperty>();
		initProperties( clazz, new HashSet<Class<?>>() );
	}
	
	private void initProperties(Class<?> clazz, Set<Class<?>> visited){
		if( visited.add( clazz )){
			CssDocProperty property = clazz.getAnnotation( CssDocProperty.class );
			
			if( property != null ){
				properties.add( new DocProperty( this, property, clazz ) );
			}
			for( Constructor<?> constructor : clazz.getDeclaredConstructors()){
				property = constructor.getAnnotation( CssDocProperty.class );
				if( property != null ){
					properties.add( new DocProperty( this, property, clazz ) );
				}
			}
			
			for( Field field : clazz.getDeclaredFields() ){
				property = field.getAnnotation( CssDocProperty.class );
				if( property != null ){
					properties.add( new DocProperty( this, property, field.getType() ) );
				}
			}
			
			for( Method method : clazz.getDeclaredMethods() ){
				property = method.getAnnotation( CssDocProperty.class );
				if( property != null ){
					properties.add( new DocProperty( this, property, method.getReturnType() ) );
				}
			}
			
			Collections.sort( properties, new Comparator<DocProperty>(){
				private Collator collator = Collator.getInstance();
				
				@Override
				public int compare( DocProperty a, DocProperty b ){
					return collator.compare( a.getName(), b.getName() );
				}
			} );
			
			CssDocSeeAlso seeAlso = clazz.getAnnotation( CssDocSeeAlso.class );
			if( seeAlso != null ){
				for( Class<?> next : seeAlso.value() ){
					initProperties( next, visited );
				}
			}
		}
	}

	/**
	 * Searches for a {@link CssDocPath} whose {@link CssDocPath#id()} equals <code>refId</code>.
	 * @param refId the identifier of the path to search
	 * @return the path or <code>null</code> if not found
	 */
	public CssDocPath getPath( String refId ){
		if( paths == null ){
			initPaths();
		}
		return paths.get( refId );
	}
	
	private void initPaths(){
		paths = new HashMap<String, CssDocPath>();
		
		addPath( clazz.getAnnotation( CssDocPath.class ) );
		for( Constructor<?> constructor : clazz.getDeclaredConstructors() ){
			addPath( constructor.getAnnotation( CssDocPath.class ) );
		}
		for( Field field : clazz.getDeclaredFields() ){
			addPath( field.getAnnotation( CssDocPath.class ));
		}
		for( Method method : clazz.getDeclaredMethods() ){
			addPath( method.getAnnotation( CssDocPath.class ) );
		}
		for( DocProperty property : this ){
			addPath( property.getAnnotation().path() );
		}
	}
	
	private void addPath( CssDocPath path ){
		if( path != null ){
			String refId = path.id();
			if( !refId.isEmpty() ){
				if( paths.containsKey( refId )){
					throw new IllegalArgumentException( "refId: " + refId + " found twice in " + clazz.getName() );
				}
				paths.put( refId, path );
			}
		}
	}
}
