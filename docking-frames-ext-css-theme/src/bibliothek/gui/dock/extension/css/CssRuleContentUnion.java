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
package bibliothek.gui.dock.extension.css;

import java.util.ArrayList;
import java.util.List;

/**
 * A {@link CssRuleContent} that collects properties from several other {@link CssRuleContent}s.
 * @author Benjamin Sigg
 */
public class CssRuleContentUnion implements CssRuleContent{
	/** the properties */
	private List<CssRuleContent> contents = new ArrayList<CssRuleContent>();

	/** this listener is added to {@link #contents} and forwards any received event */
	private Listener listener = new Listener();
	
	/** all the listeners that were ever added to this union */
	private List<CssRuleContentListener> listeners = new ArrayList<CssRuleContentListener>();
	
	@Override
	public <T> T getProperty( CssType<T> type, CssPropertyKey property ){
		for( CssRuleContent content : contents ){
			T result = content.getProperty( type, property );
			if( result != null ){
				return result;
			}
		}
		return null;
	}

	/**
	 * Adds <code>content</code> to the list of contents, <code>content</code> adds its properties
	 * to the set of properties.
	 * @param content the new properties
	 */
	public void add( CssRuleContent content ){
		insert( contents.size(), content );
	}
	
	/**
	 * Adds <code>content</code> to the list of contents, <code>content</code> adds its properties
	 * to the set of properties.
	 * @param index the position of <code>content</code>, as lower the number as higher the priority
	 * of <code>content</code>
	 * @param content the new properties
	 */
	public void insert( int index, CssRuleContent content ){
		contents.add( index, content );
		if( !listeners.isEmpty() ){
			content.addRuleContentListener( listener );
			listener.propertiesChanged( this );
		}
	}
	
	/**
	 * Gets the <code>index</code>'th item of this union.
	 * @param index the index of an item
	 * @return the item
	 */
	public CssRuleContent get( int index ){
		return contents.get( index );
	}
	
	/**
	 * Gets the number of items stored int his union.
	 * @return the number of items
	 */
	public int size(){
		return contents.size();
	}
	
	/**
	 * Removes the <code>index</code>'th {@link CssRuleContent} of this union.
	 * @param index the index of the element to remove
	 * @return the removed element
	 */
	public CssRuleContent remove( int index ){
		CssRuleContent content = contents.remove( index );
		if( !listeners.isEmpty() ){
			content.removeRuleContentListener( listener );
			listener.propertiesChanged( this );
		}
		return content;
	}
	
	/**
	 * Removes <code>content</code> from this union.
	 * @param content the item to remove
	 */
	public void remove( CssRuleContent content ){
		contents.remove( content );
		if( !listeners.isEmpty() ){
			content.removeRuleContentListener( listener );
			listener.propertiesChanged( this );
		}
	}

	@Override
	public void addRuleContentListener( CssRuleContentListener listener ){
		if( listener == null ){
			throw new IllegalArgumentException( "listener must not be null" );
		}
		if( listeners.isEmpty() ){
			for( CssRuleContent content : contents ){
				content.addRuleContentListener( this.listener );
			}
		}
		listeners.add( listener );
	}

	@Override
	public void removeRuleContentListener( CssRuleContentListener listener ){
		listeners.remove( listener );
		if( listeners.isEmpty() ){
			for( CssRuleContent content : contents ){
				content.removeRuleContentListener( this.listener );
			}
		}
	}
	
	private CssRuleContentListener[] listeners(){
		return listeners.toArray( new CssRuleContentListener[ listeners.size() ] );
	}
	
	/**
	 * Listener monitoring the {@link CssRuleContent}s of a {@link CssRuleContentUnion}.
	 * @author Benjamin Sigg
	 */
	private class Listener implements CssRuleContentListener{
		@Override
		public void propertiesChanged( CssRuleContent source ){
			for( CssRuleContentListener listener : listeners() ){
				listener.propertiesChanged( CssRuleContentUnion.this );
			}
		}
		
		@Override
		public void propertyChanged( CssRuleContent source, CssPropertyKey key ){
			for( CssRuleContentListener listener : listeners() ){
				listener.propertyChanged( CssRuleContentUnion.this, key );
			}
		}
	}
}
