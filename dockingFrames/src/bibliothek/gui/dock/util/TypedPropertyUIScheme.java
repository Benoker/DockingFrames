/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2010 Benjamin Sigg
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
package bibliothek.gui.dock.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import bibliothek.gui.dock.event.UIListener;
import bibliothek.util.Path;

/**
 * This {@link UIScheme} is intended to be used by a {@link TypedUIProperties}
 * to fill in gaps by reading selected keys of a {@link DockProperties}.
 * @author Benjamin Sigg
 */
public class TypedPropertyUIScheme implements UIScheme<Object, UIValue<Object>, UIBridge<Object, UIValue<Object>>>{
	/** the source of all the information to read */
	private DockProperties properties;
	
	/** all the links this scheme provides */
	private Map<String, Link<?, ?>> links = new HashMap<String, Link<?, ?>>();
	
	/** all the listeners to this scheme */
	private List<UISchemeListener<Object, UIValue<Object>, UIBridge<Object, UIValue<Object>>>> listeners =
		new ArrayList<UISchemeListener<Object,UIValue<Object>,UIBridge<Object,UIValue<Object>>>>();
	
	/** how often this scheme is used */
	private int bound = 0;
	
	/**
	 * Creates a new scheme
	 * @param properties the source of all the information to read, not <code>null</code>
	 */
	public TypedPropertyUIScheme( DockProperties properties ){
		if( properties == null ){
			throw new IllegalArgumentException( "properties must not be null" );
		}
		this.properties = properties;
	}
	
	/**
	 * Creates a link between the key <code>source</code> and the resource <code>destinationId</code>. If there is an
	 * older link to the same id, then the older link is removed first.
	 * @param <V> the type to read from a {@link DockProperties}
	 * @param <A> the type to write into a {@link TypedUIProperties}
	 * @param source the key for the resource
	 * @param destinationType the type of the resource
	 * @param destinationId the identifier of the resource
	 */
	@SuppressWarnings("unchecked")
	public <V, A extends V> void link( PropertyKey<A> source, TypedUIProperties.Type<V> destinationType, String destinationId ){
		String id = destinationType.getKey( destinationId );
		Link<?,?> old = links.get( id );
		if( old != null ){
			// just reuse, a shift of the generic type is no problem
			((Link)old).setKey( source );
		}
		else{
			Link<V, A> link = new Link<V, A>( source, destinationType, destinationId );
			links.put( id, link );
			if( bound > 0 ){
				link.setProperties( properties );
			}
		}
	}
	
	/**
	 * Disables the connection leading to the resource <code>destinationId</code>.
	 * @param <V> the <code>destinationType</code>
	 * @param destinationType the type of the resource
	 * @param destinationId the identifier of the resource that should no longer be managed by this scheme
	 */
	public <V> void unlink( TypedUIProperties.Type<V> destinationType, String destinationId ){
		String id = destinationType.getKey( destinationId );
		Link<?,?> old = links.remove( id );
		if( old != null ){
			old.setProperties( (DockProperties)null );
		}
	}
	
	public void addListener( UISchemeListener<Object, UIValue<Object>, UIBridge<Object, UIValue<Object>>> listener ){
		listeners.add( listener );	
	}

	public void removeListener( UISchemeListener<Object, UIValue<Object>, UIBridge<Object, UIValue<Object>>> listener ){
		listeners.remove( listener );
	}
	
	/**
	 * Forwards the event <code>event</code> to all registered {@link UIListener}s.
	 * @param event the event to forward
	 */
	@SuppressWarnings("unchecked")
	protected void fire( UISchemeEvent<Object, UIValue<Object>, UIBridge<Object,UIValue<Object>>> event ){
		UISchemeListener<Object, UIValue<Object>, UIBridge<Object,UIValue<Object>>>[] array = listeners.toArray( new UISchemeListener[ listeners.size() ] );
		
		for( UISchemeListener<Object, UIValue<Object>, UIBridge<Object,UIValue<Object>>> listener : array ){
			listener.changed( event );
		}
	}
	
	public UIBridge<Object, UIValue<Object>> getBridge( Path name, UIProperties<Object, UIValue<Object>, UIBridge<Object, UIValue<Object>>> properties ){
		return null;
	}

	public Object getResource( String name, UIProperties<Object, UIValue<Object>, UIBridge<Object, UIValue<Object>>> properties ){
		Link<?, ?> best = links.get( name );
		
		if( best == null ){
			int offset = 0;
			for( Map.Entry<String, Link<?, ?>> entry : links.entrySet() ){
				if( name.startsWith( entry.getKey() )){
					int length = entry.getKey().length();
					if( length > offset ){
						offset = length;
						best = entry.getValue();
					}
				}
			}
		}
		
		if( best == null ){
			return null;
		}
		else{
			return best.getValue();
		}
	}

	public void install( UIProperties<Object, UIValue<Object>, UIBridge<Object, UIValue<Object>>> properties ){
		if( bound == 0 ){
			for( Link<?,?> link : links.values() ){
				link.setProperties( (DockProperties)null );
			}
		}
		bound++;
	}

	public void uninstall( UIProperties<Object, UIValue<Object>, UIBridge<Object, UIValue<Object>>> properties ){
		bound--;
		if( bound == 0 ){
			for( Link<?,?> link : links.values() ){
				link.setProperties( this.properties );
			}
		}
	}
	
	private class Link<V, A extends V> extends PropertyValue<A>{
		private TypedUIProperties.Type<V> type;
		private String id;
		
		public Link( PropertyKey<A> key, TypedUIProperties.Type<V> type, String id ){
			super( key );
			this.type = type;
			this.id = id;
		}
		
		@Override
		protected void valueChanged( A oldValue, A newValue ){
			fire( new UISchemeEvent<Object, UIValue<Object>, UIBridge<Object, UIValue<Object>>>(){
				public Collection<Path> changedBridges( Set<Path> names ){
					return Collections.emptyList();
				}

				public Collection<String> changedResources( Set<String> names ){
					if( names == null ){
						return null;
					}
					
					String key = type.getKey( id );
					List<String> result = new ArrayList<String>();
					for( String name : names ){
						if( name.startsWith( key )){
							result.add( name );
						}
					}
					return result;
				}

				public UIScheme<Object, UIValue<Object>, UIBridge<Object, UIValue<Object>>> getScheme(){
					return TypedPropertyUIScheme.this;
				}
			});
		}
	}
}
