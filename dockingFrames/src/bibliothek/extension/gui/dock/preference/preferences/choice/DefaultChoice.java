/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2008 Benjamin Sigg
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
package bibliothek.extension.gui.dock.preference.preferences.choice;

import java.util.ArrayList;
import java.util.List;


/**
 * A default implementation of {@link Choice}, provides text, id and objects
 * for every possible choice.
 * @author Benjamin Sigg
 */
public class DefaultChoice<V> implements Choice {
	private List<Entry> list = new ArrayList<Entry>();
	private boolean nullEntryAllowed = false;
	private String defaultChoice;
	
	/**
	 * Removes the index'th entry of this choice.
	 * @param index the index of the entry to remove
	 */
	public void remove( int index ){
		list.remove( index );
	}
	
	/**
	 * Adds an entry to this {@link Choice}.
	 * @param id the id of the new entry
	 * @param text the text of the new entry
	 * @param value the optional value
	 */
	public void add( String id, String text, V value ){
		if( id == null )
			throw new IllegalArgumentException( "id must not be null" );
		
		if( text == null )
			throw new IllegalArgumentException( "text must not be null" );
		
		list.add( new Entry( id, text, value ));
	}
	
	public String getId( int index ){
		return list.get( index ).id;
	}
	
	public String getText( int index ){
		return list.get( index ).text;
	}
	
	/**
	 * Gets the value associated with the <code>index</code>'th entry.
	 * @param index the index of the entry
	 * @return the value
	 */
	public V getValue( int index ){
		return list.get( index ).value;
	}
	
	/**
	 * Searches the entry with the identifier <code>id</code>.
	 * @param id some id, might be <code>null</code>
	 * @return the index or -1
	 */
	public int indexOfIdentifier( String id ){
		if( id == null ){
			for( int i = 0, n = list.size(); i<n; i++ ){
				if( list.get( i ).id == null )
					return i;
			}
		}
		else{
			for( int i = 0, n = list.size(); i<n; i++ ){
				if( list.get( i ).id.equals( id ))
					return i;
			}
		}
		
		return -1;
	}
	
	/**
	 * Searches the index of the entry that contains <code>value</code>. This
	 * method uses {@link #equals(Object, Object)} to compare two objects.
	 * @param value the value to search
	 * @return the index or -1
	 */
	public int indexOfValue( V value ){
		for( int i = 0, n = list.size(); i<n; i++ ){
			if( equals( list.get( i ).value, value ))
				return i;
		}
		
		return -1;
	}
	
	/**
	 * Checks the equality of <code>a</code> and <code>b</code>.
	 * @param a some value, might be <code>null</code>
	 * @param b some value, might be <code>null</code>
	 * @return <code>true</code> if <code>a</code> and <code>b</code> are equal
	 */
	protected boolean equals( V a, V b ){
		if( a == b )
			return true;
		
		if( a == null )
			return false;
		
		return a.equals( b );
	}
	
	/**
	 * Searches the identifier for an entry which contains <code>value</code>,
	 * this method uses {@link #equals(Object, Object)} to decide wheter two
	 * values are equal.
	 * @param value the value to search
	 * @return its identifier
	 */
	public String valueToIdentifier( V value ){
		int index = indexOfValue( value );
		if( index < 0 )
			return null;
		return getId( index );
	}
	
	/**
	 * Search the value for the entry width identifier <code>id</code>.
	 * @param id the id to search
	 * @return the value associated with <code>id</code>
	 */
	public V identifierToValue( String id ){
		int index = indexOfIdentifier( id );
		if( index < 0 )
			return null;
		return getValue( index );
	}
	
	public int size() {
		return list.size();
	}
	
	/**
	 * Sets whether the <code>null</code>-entry is allowed, the <code>null</code>-entry
	 * describes the non existing selection.
	 * @param nullEntryAllowed <code>true</code> if no selection is allowed
	 */
	public void setNullEntryAllowed(boolean nullEntryAllowed) {
		this.nullEntryAllowed = nullEntryAllowed;
	}
	
	public boolean isNullEntryAllowed() {
		return nullEntryAllowed;
	}
	
	/**
	 * Sets the default choice for this choice. 
	 * @param defaultChoice the default value
	 */
	public void setDefaultChoice(String defaultChoice) {
		this.defaultChoice = defaultChoice;
	}
	
	public String getDefaultChoice() {
		if( defaultChoice == null ){
			if( isNullEntryAllowed() )
				return null;
			
			if( list.size() > 0 )
				return list.get( 0 ).id;
			
			return null;
		}
		return defaultChoice;
	}
	
	private class Entry{
		public String id;
		public String text;
		public V value;
		
		public Entry( String id, String text, V value ){
			this.id = id;
			this.text = text;
			this.value = value;
		}
	}
}
