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

import java.util.ArrayList;

import java.util.List;

/**
 * A node in the {@link InspectionGraph}, represents one {@link Inspect}.
 * @author Benjamin Sigg
 */
public class InspectionNode {
	private InspectionNode[] children;
	
	private Inspect inspect;
	private int inspectId;
	private InspectionGraph graph;
	
	private List<InspectionNodeListener> listeners = new ArrayList<InspectionNodeListener>();
	
	public InspectionNode( InspectionGraph graph, Inspect inspect, int inspectId ){
		this.graph = graph;
		this.inspect = inspect;
		this.inspectId = inspectId;
	}
	
	public Inspect getInspect(){
		return inspect;
	}
	
	public void addListener( InspectionNodeListener listener ){
		listeners.add( listener );
	}
	
	public void removeListener( InspectionNodeListener listener ){
		listeners.remove( listener );
	}
	
	protected InspectionNodeListener[] listeners(){
		return listeners.toArray( new InspectionNodeListener[ listeners.size() ] );
	}
	
	public InspectionNode[] getChildren(){
		if( children == null ){
			Object[] next = inspect.getChildren();
			children = new InspectionNode[ next.length ];
			for( int i = 0; i < next.length; i++ ){
				children[i] = graph.getNode( next[i] );
			}
		}
		return children;
	}
	
	public void update(){
		Inspect[] current = null;
		if( children != null ){
			current = new Inspect[children.length];
		
			for( int i = 0; i < current.length; i++ ){
				current[i] = children[i].inspect;
			}
		}
		
		if( inspect.update() ){
			if( children != null ){
				InspectionNode[] old = children;
				children = nodes();
				for( InspectionNodeListener listener : listeners() ){
					listener.updated( old, children );
				}
			}
			else{
				for( InspectionNodeListener listener : listeners() ){
					listener.updated();
				}
			}
		}
	}
	
	private InspectionNode[] nodes(){
		Object[] next = inspect.getChildren();
		List<Inspect> inspects = new ArrayList<Inspect>();
		if( next != null ){
			for( Object inspectable : next ){
				if( inspectable != null ){
					inspects.add( graph.getInspect( graph.getInspectable( inspectable ) ) );
				}
			}
		}
		
		InspectionNode[] nodes = new InspectionNode[ inspects.size() ];
		for( int i = 0; i < nodes.length; i++ ){
			nodes[i] = graph.getNode( inspects.get( i ) );
		}
		
		return nodes;
	}
	
	@Override
	public String toString(){
		return inspect.getName() + " (" + inspectId + "): " + graph.toString( inspect.getValue() );
	}
}
