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
import java.util.Collection;
import java.util.List;

import bibliothek.gui.DockController;
import bibliothek.gui.dock.util.TextManager;
import bibliothek.gui.dock.util.extension.ExtensionName;


/**
 * A default implementation of {@link Choice}, provides text, id and objects
 * for every possible choice.
 * @author Benjamin Sigg
 * @param <V> the kind of values this choice manages
 */
public class DefaultChoice<V> implements Choice {
	private List<Entry<V>> list = new ArrayList<Entry<V>>();
	private boolean nullEntryAllowed = false;
	private String defaultChoice;
	
	/** all the listeners that were added to this choice */
	private List<ChoiceListener> listeners = new ArrayList<ChoiceListener>();
	
	private DockController controller;
	
	/**
	 * Creates a new choice, adding additional entries if there are any 
	 * extensions.
	 * @param controller the realm in which this choice is used, can be <code>null</code>
	 */
	@SuppressWarnings("unchecked")
	public DefaultChoice( DockController controller ){
		if( controller != null ){
			ExtensionName<ChoiceExtension> name = new ExtensionName<ChoiceExtension>( 
					ChoiceExtension.CHOICE_EXTENSION, ChoiceExtension.class, ChoiceExtension.CHOICE_PARAMETER, this );
			Collection<ChoiceExtension> choices = controller.getExtensions().load( name );
			for( ChoiceExtension item : choices ){
				ChoiceExtension<V> choice = (ChoiceExtension<V>)item;
				
				for( int i = 0, n = choice.size(); i<n; i++ ){
					String text = choice.getText( i );
					boolean textIsKey = choice.isTextKey( i );
					String id = choice.getId( i );
					V value = choice.getChoice( i );
					add( id, text, textIsKey, value );
				}
				
				if( defaultChoice == null ){
					defaultChoice = choice.getDefaultChoice();
				}
			}
		}
	}
	
	public void addChoiceListener( ChoiceListener listener ){
		listeners.add( listener );	
	}
	
	public void removeChoiceListener( ChoiceListener listener ){
		listeners.remove( listener );
	}
	
	private ChoiceListener[] listeners(){
		return listeners.toArray( new ChoiceListener[ listeners.size() ] );
	}
	
	/**
	 * Calls {@link ChoiceListener#inserted(Choice, int, int)} on all listeners
	 * that are currently known.
	 * @param start the index of the first entry
	 * @param end the index of the last entry
	 */
	protected void fireInserted( int start, int end ){
		for( ChoiceListener listener : listeners() ){
			listener.inserted( this, start, end );
		}
	}

	/**
	 * Calls {@link ChoiceListener#removed(Choice, int, int)} on all listeners
	 * that are currently known.
	 * @param start the index of the first entry
	 * @param end the index of the last entry
	 */
	protected void fireRemoved( int start, int end ){ 
		for( ChoiceListener listener : listeners() ){
			listener.removed( this, start, end );
		}		
	}
	
	/**
	 * Calls {@link ChoiceListener#updated(Choice, int, int)} on all listeners
	 * that are currently known.
	 * @param start the index of the first entry
	 * @param end the index of the last entry
	 */
	protected void fireUpdated( int start, int end ){
		for( ChoiceListener listener : listeners() ){
			listener.updated( this, start, end );
		}
	}
	
	public void setController( DockController controller ){
		this.controller = controller;
		for( Entry<V> entry : list ){
			entry.setController( controller );
		}
	}
	
	/**
	 * Removes the index'th entry of this choice.
	 * @param index the index of the entry to remove
	 */
	public void remove( int index ){
		Entry<V> entry = list.remove( index );
		entry.setController( null );
		fireRemoved( index, index );
	}

	/**
	 * Like {@link #add(String, String, boolean, Object)} with <code>codeIsKey</code> set to <code>false</code>
	 * @param id the id of the new entry
	 * @param text the text of the new entry
	 * @param value the optional value
	 * @return a direct link to the data that was added
	 */
	public Entry<V> add( String id, String text, V value ){
		return add( id, text, false, value );
	}
	
	/**
	 * Like {@link #add(String, String, boolean, Object)} with <code>codeIsKey</code> set to <code>true</code>
	 * @param id the id of the new entry
	 * @param text the text of the new entry
	 * @param value the optional value
	 * @return a direct link to the data that was added
	 */
	public Entry<V> addLinked( String id, String text, V value ){
		return add( id, text, true, value );
	}
	
	/**
	 * Adds an entry to this {@link Choice}.
	 * @param id the id of the new entry
	 * @param text the text of the new entry
	 * @param textIsKey if <code>true</code>, then <code>text</code> is interpreted as a key for a {@link TextManager},
	 * otherwise it is just text 
	 * @param value the optional value
	 * @return a direct link to the data that was added
	 */
	public Entry<V> add( String id, String text, boolean textIsKey, V value ){
		if( id == null )
			throw new IllegalArgumentException( "id must not be null" );
		
		if( text == null )
			throw new IllegalArgumentException( "text must not be null" );
		
		int index = list.size();
		Entry<V> entry;
		if( textIsKey){ 
			entry = new IdentifiedEntry( id, text, value );
		}
		else{
			entry = new BaseEntry( id, text, value );
		}
		entry.setController( controller );
		list.add( entry );
		fireInserted( index, index );
		
		return entry;
	}
	
	public String getId( int index ){
		return list.get( index ).getEntryId();
	}
	
	public String getText( int index ){
		return list.get( index ).getEntryText();
	}
	
	/**
	 * Gets the value associated with the <code>index</code>'th entry.
	 * @param index the index of the entry
	 * @return the value
	 */
	public V getValue( int index ){
		return list.get( index ).getEntryValue();
	}
	
	/**
	 * Gets all the data that is stored at <code>index</code>.
	 * @param index the index of some entry
	 * @return the entry at that location
	 */
	public Entry<V> getEntry( int index ){
		return list.get( index );
	}
	
	/**
	 * Searches the entry with the identifier <code>id</code>.
	 * @param id some id, might be <code>null</code>
	 * @return the index or -1
	 */
	public int indexOfIdentifier( String id ){
		if( id == null ){
			for( int i = 0, n = list.size(); i<n; i++ ){
				if( list.get( i ).getEntryId() == null )
					return i;
			}
		}
		else{
			for( int i = 0, n = list.size(); i<n; i++ ){
				if( id.equals( list.get( i ).getEntryId() ) )
					return i;
			}
		}
		
		return -1;
	}
	
	/**
	 * Searches the index of the entry that contains <code>value</code>. This
	 * method uses {@link #equals(Object, Object)} to compare two objects.
	 * @param value the value to search
	 * @return the index or -1 if <code>value</code> can't be found or
	 * is <code>null</code>
	 */
	public int indexOfValue( V value ){
		if( value == null )
			return -1;
		
		for( int i = 0, n = list.size(); i<n; i++ ){
			if( equals( list.get( i ).getEntryValue(), value ))
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
	 * this method uses {@link #equals(Object, Object)} to decide whether two
	 * values are equal.
	 * @param value the value to search
	 * @return its identifier, <code>null</code> if <code>value</code> is <code>null</code>
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
				return list.get( 0 ).getEntryId();
			
			return null;
		}
		return defaultChoice;
	}
	
	public interface Entry<V>{
		public String getEntryId();
		public V getEntryValue();
		public String getEntryText();
		public void setEntryText( String text );
		public void setController( DockController controller );
	}
	
	private class BaseEntry implements Entry<V>{
		private String id;
		private V value;
		private String text;
		
		public BaseEntry( String id, String text, V value ){
			this.id = id;
			this.text = text;
			this.value = value;
		}
		
		public String getEntryId(){
			return id;
		}
		
		public V getEntryValue(){
			return value;
		}
		
		public String getEntryText(){
			return text;
		}
		
		public void setEntryText( String text ){
			this.text = text;
			int index = indexOfIdentifier( id );
			if( index >= 0 ){
				fireUpdated( index, index );
			}
		}
		
		public void setController( DockController controller ){
			// ignore
		}
	}
	
	private class IdentifiedEntry extends ChoiceEntryText implements Entry<V>{
		public String id;
		public V value;
		
		public IdentifiedEntry( String id, String text, V value ){
			super( text, DefaultChoice.this );
			this.id = id;
			this.value = value;
		}
		
		@Override
		protected void changed( String oldValue, String newValue ){
			int index = indexOfIdentifier( id );
			if( index >= 0 ){
				fireUpdated( index, index );
			}
		}
		
		public String getEntryId(){
			return id;
		}
		
		public V getEntryValue(){
			return value;
		}
		
		public void setEntryText( String text ){
			setValue( text );
		}
		
		public String getEntryText(){
			return value();
		}
	}
}
