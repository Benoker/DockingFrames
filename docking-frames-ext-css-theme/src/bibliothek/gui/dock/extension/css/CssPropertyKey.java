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

import java.util.Arrays;

/**
 * The name of a {@link CssProperty}. The key always describes the path from a {@link CssItem} to a {@link CssProperty},
 * each iteration over a {@link CssPropertyContainer} adds another element to the path. Since there is always a
 * {@link CssItem} as root, there is always at least one element in the path.
 * @author Benjamin Sigg
 */
public class CssPropertyKey {
	/** The separator to combine keys of {@link CssProperty}s to longer keys */
	private static final String SEPARATOR = "-";
	
	private String[] path;
	
	/**
	 * Parses a key that can be found in a css rule.
	 * @param name the key as textual representation
	 * @return the parsed key
	 */
	public static CssPropertyKey parse( String name ){
		return new CssPropertyKey( name.split( SEPARATOR ) );
	}
	
	/**
	 * Creates a new key.
	 * @param path the parts of the key, must contain at least one element
	 */
	public CssPropertyKey( String... path ){
		if( path.length == 0 ){
			throw new IllegalArgumentException( "require at least one element" );
		}
		this.path = path;
	}
	
	/**
	 * Creates a new key by appending <code>next</code> to this path.
	 * @param next the next element in this path
	 * @return the new key
	 */
	public CssPropertyKey append( String next ){
		String[] copy = new String[ path.length+1 ];
		System.arraycopy( path, 0, copy, 0, path.length );
		copy[ path.length ] = next;
		return new CssPropertyKey( copy );
	}
	
	/**
	 * Creates a new key by appending <code>next</code> to this path.
	 * @param next the next element in this path
	 * @return the new key
	 */
	public CssPropertyKey append( CssPropertyKey next ){
		String[] copy = new String[ path.length + next.path.length ];
		System.arraycopy( path, 0, copy, 0, path.length );
		System.arraycopy( next.path, 0, copy, path.length, next.path.length );
		return new CssPropertyKey( copy );
	}
	
	/**
	 * Converts this key into a {@link String} looking like the name of a property as it can
	 * be found in the css-rules.
	 * @return this key as {@link String}
	 */
	public String toPropertyName(){
		StringBuilder builder = new StringBuilder();
		for( int i = 0; i < path.length; i++ ){
			if( i > 0 ){
				builder.append( SEPARATOR );
			}
			builder.append( path[i] );
		}
		return builder.toString();
	}
	
	/**
	 * Gets the number of segments of this path.
	 * @return the number of segments
	 */
	public int length(){
		return path.length;
	}
	
	@Override
	public String toString(){
		return toPropertyName();
	}

	@Override
	public int hashCode(){
		return Arrays.hashCode( path );
	}

	@Override
	public boolean equals( Object obj ){
		if( this == obj )
			return true;
		if( obj == null )
			return false;
		if( getClass() != obj.getClass() )
			return false;
		CssPropertyKey other = (CssPropertyKey) obj;
		if( !Arrays.equals( path, other.path ) )
			return false;
		return true;
	}
}
