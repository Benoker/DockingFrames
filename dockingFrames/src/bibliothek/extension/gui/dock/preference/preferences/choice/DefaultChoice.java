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
public class DefaultChoice implements Choice {
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
	 */
	public void add( String id, String text ){
		if( id == null )
			throw new IllegalArgumentException( "id must not be null" );
		
		if( text == null )
			throw new IllegalArgumentException( "text must not be null" );
		
		list.add( new Entry( id, text ));
	}
	
	public String getId( int index ){
		return list.get( index ).id;
	}
	
	public String getText( int index ){
		return list.get( index ).text;
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
		
		public Entry( String id, String text ){
			this.id = id;
			this.text = text;
		}
	}
}
