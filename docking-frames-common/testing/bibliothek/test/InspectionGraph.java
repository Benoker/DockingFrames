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
package bibliothek.test;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import bibliothek.test.inspect.NullInspectable;
import bibliothek.test.inspect.ObjectInspectable;

/**
 * A graph built by the contents of an {@link Inspect}.
 * @author Benjamin Sigg
 */
public class InspectionGraph {
	private int id = 0;
	
	private Map<Class<?>, Adapter<?, Inspectable>> inspectableAdapters = new HashMap<Class<?>, Adapter<?, Inspectable>>();
	private Map<Class<?>, Adapter<?, String>> stringAdapters = new HashMap<Class<?>, Adapter<?,String>>();
	
	private Map<Object, Inspectable> inspectables = new IdentityHashMap<Object, Inspectable>();
	private Map<Inspectable, Inspect> inspects = new IdentityHashMap<Inspectable, Inspect>();
	private Map<Inspect, InspectionNode> nodes = new IdentityHashMap<Inspect, InspectionNode>();
	
	public InspectionGraph(){
		putInspectableAdapter( Inspectable.class, new Adapter<Inspectable, Inspectable>() {
			public Inspectable adapt( Inspectable value ){
				return value;
			}
		});
		
		putInspectableAdapter( Object.class, ObjectInspectable.class );
		
		putStringAdapter( Object.class, new Adapter<Object, String>() {
			public String adapt( Object value ){
				return String.valueOf( value );
			}
		});
	}
	
	public void updateAll(){
		InspectionNode[] update = nodes.values().toArray( new InspectionNode[ nodes.size() ] );
		
		for( InspectionNode node : update ){
			node.update();
		}
	}
	
	public void retainAll( Set<InspectionNode> usedNodes ){
		Iterator<InspectionNode> nodeIterator = nodes.values().iterator();
		while( nodeIterator.hasNext() ){
			if( !usedNodes.contains( nodeIterator.next() )){
				nodeIterator.remove();
			}
		}
		
		Iterator<Inspect> iterator = inspects.values().iterator();
		while( iterator.hasNext() ){
			if( !nodes.containsKey( iterator.next() )){
				iterator.remove();
			}
		}
		
		Iterator<Inspectable> inspectableIterator = inspectables.values().iterator();
		while( inspectableIterator.hasNext() ){
			if( !inspects.containsKey( inspectableIterator.next() )){
				inspectableIterator.remove();
			}
		}
	}
	
	public <C> void putInspectableAdapter( Class<C> clazz, Adapter<C, Inspectable> adapter ){
		inspectableAdapters.put( clazz, adapter );
	}
	
	public <C> void putInspectableAdapter( Class<C> source, Class<? extends Inspectable> destination ){
		putInspectableAdapter( source, new ReflectionAdapter<C,Inspectable>( source, destination ) );
	}
	
	public <C> void putStringAdapter( Class<C> clazz, Adapter<C, String> adapter ){
		stringAdapters.put( clazz, adapter );
	}
	
	private <X> X get( Class<?> clazz, Map<Class<?>, X> adapters ){
		while( clazz != null ){
			X adapter = adapters.get( clazz );
			if( adapter != null ){
				return adapter;
			}
			
			for( Class<?> interfaze : clazz.getInterfaces() ){
				adapter = adapters.get( interfaze );
				if( adapter != null ){
					return adapter;
				}	
			}
			
			clazz = clazz.getSuperclass();
		}
		
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public Inspectable getInspectable( Object object ){
		if( object == null ){
			return NullInspectable.INSTANCE;
		}
		
		Inspectable result = inspectables.get( object );
		if( result == null ){
			Adapter<?, Inspectable> adapter = get( object.getClass(), inspectableAdapters );
			result = ((Adapter<Object, Inspectable>)adapter).adapt( object );
			inspectables.put( object, result );
		}
		return result;
	}
	
	public Inspect getInspect( Inspectable inspectable ){
		Inspect inspect = inspects.get( inspectable );
		if( inspect == null ){
			inspect = inspectable.inspect( this );
			inspects.put( inspectable, inspect );
		}
		return inspect;
	}
	
	public InspectionNode getNode( Inspect inspect ){
		InspectionNode node = nodes.get( inspect );
		if( node == null ){
			node = new InspectionNode( this, inspect, id++ );
			nodes.put( inspect, node );
			node.update();
		}
		return node;
	}
	
	public InspectionNode getNode( Object value ){
		return getNode( getInspect( getInspectable( value ) ) );
	}
	
	@SuppressWarnings("unchecked")
	public String toString( Object object ){
		if( object == null ){
			return "null";
		}
		Adapter<?, String> adapter = get( object.getClass(), stringAdapters );
		return ((Adapter<Object, String>)adapter).adapt( object );
	}
}
