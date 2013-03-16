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
package bibliothek.gui.dock.extension.css.doc;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents one {@link CssDocKey}.
 * @author Benjamin Sigg
 */
public class DocKey {
	/**
	 * Generates all the {@link DocKey}s that are defined by <code>keys</code>.
	 * @param root the root of the documentation
	 * @param keys the keys to convert into a {@link DocKey}
	 * @return all the keys represented by <code>key</code>
	 */
	public static DocKey[] of( DocRoot root, CssDocKey... keys ){
		List<DocKey> result = new ArrayList<DocKey>();
		for( CssDocKey key : keys ){
			for( DocKey item : of( root, key )){
				result.add( item );
			}
		}
		return result.toArray( new DocKey[ result.size() ] );
	}
	
	/**
	 * Generates all the {@link DocKey}s that are defined by <code>key</code>.
	 * @param root the root of the documentation
	 * @param key the key to convert into a {@link DocKey}
	 * @return all the keys represented by <code>key</code>
	 */
	public static DocKey[] of( DocRoot root, CssDocKey key ){
		if( key.reference() == Object.class ){
			if( key.key().isEmpty() ){
				return new DocKey[]{};
			}
			else{
				return new DocKey[]{ new DocKey( root, key ) };
			}
		}
		else{
			CssDocKeys replacements = key.reference().getAnnotation( CssDocKeys.class );
			CssDocKey replacement = key.reference().getAnnotation( CssDocKey.class );
			
			List<DocKey> results = new ArrayList<DocKey>();
			if( replacement != null ){
				for( DocKey next : of( root, replacement )){
					results.add( next );
				}
			}
			if( replacements != null ){
				for( CssDocKey sub : replacements.value() ){
					for( DocKey next : of( root, sub )){
						results.add( next );
					}
				}
			}
			
			return results.toArray( new DocKey[ results.size() ] );
		}
	}
	
	/**
	 * Generates the one {@link DocKey}s that is defined by <code>key</code>.
	 * @param root the root of the documentation
	 * @param key the key to convert into a {@link DocKey}
	 * @return the key represented by <code>key</code>, or <code>null</code>
	 */
	public static DocKey only( DocRoot root, CssDocKey key ){
		DocKey[] keys = of( root, key );
		if( keys.length > 1 ){
			throw new IllegalArgumentException( "key does not represent exactly one key" );
		}
		if( keys.length == 0 ){
			return null;
		}
		return keys[0];
	}
	
	/** the root of the documentation */
	private DocRoot root;
	
	/** the key that is represented by <code>this</code> */
	private CssDocKey key;
	
	private DocKey( DocRoot root, CssDocKey key ){
		this.root = root;
		this.key = key;
		
		if( key.key().isEmpty() ){
			throw new IllegalArgumentException( "found a key with empty name" );
		}
	}
	
	/**
	 * Gets the name of the key.
	 * @return the name of the key, never <code>null</code>
	 */
	public String getKey(){
		return key.key();
	}
	
	/**
	 * Gets the documentation of this key.
	 * @return the description
	 */
	public DocText getDescription(){
		return new DocText( root, key.description() );
	}
}
