/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2011 Benjamin Sigg
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
package bibliothek.gui.dock.station.screen;

import java.util.ArrayList;
import java.util.List;

import bibliothek.gui.dock.ScreenDockStation;
import bibliothek.gui.dock.station.screen.AttractorStrategy.Attraction;
import bibliothek.gui.dock.station.screen.MagnetRequest.Side;

/**
 * The {@link StickMagnetGraph} is a helper class for a {@link MagnetStrategy}. The {@link StickMagnetGraph}
 * is a directed graph without cycles telling which moved {@link ScreenDockWindow} affects which other
 * {@link ScreenDockWindow}, the graph is created depending on the  
 * {@link AttractorStrategy#stick(bibliothek.gui.dock.ScreenDockStation, bibliothek.gui.Dockable, bibliothek.gui.Dockable) stickiness}
 * of two windows.
 * @author Benjamin Sigg
 */
public class StickMagnetGraph {
	/** information about all the {@link ScreenDockWindow}s */
	private MagnetController controller;

	/** information abou the event */
	private MagnetRequest request;

	/** the node that represents the {@link ScreenDockWindow} of {@link #request} */
	private Node root;
	
	public StickMagnetGraph( MagnetController controller, MagnetRequest request ){
		this.controller = controller;
		this.request = request;
	}
	
	/**
	 * Gets the root node of the graph. The root node has no ingoing edges and its
	 * {@link Node#getWindow() window} is the same object as {@link MagnetRequest#getWindow()}.
	 * The root node is created the first time this method is called, it is then cached.
	 * @return the root node, never <code>null</code>
	 */
	public Node getRoot(){
		if( root == null ){
			ScreenDockWindow[] windows = controller.getWindows();
			DefaultNode[] nodes = new DefaultNode[ windows.length ];
			int index = 0;
			ScreenDockWindow window = request.getWindow();
			
			while( windows[ index ] != window ){
				index++;
			}
			
			expand( index, nodes, windows );
			root = nodes[ index ];
		}
		return root;
	}

	/**
	 * Gets information about all known {@link ScreenDockWindow}s.
	 * @return the information, not <code>null</code>
	 */
	public MagnetController getController(){
		return controller;
	}

	/**
	 * Gets information about the moved {@link ScreenDockWindow}.
	 * @return the information, not <code>null</code>
	 */
	public MagnetRequest getRequest(){
		return request;
	}

	/**
	 * Recursively builds the entire stickiness graph. 
	 * @param index the node whose neighbors have to be found by this method
	 * @param nodes an array containing all nodes that may be created. An entry of <code>null</code>
	 * at index <code>a</code> indicates that the {@link ScreenDockWindow} in <code>windows</code> at
	 * index <code>a</code> is not yet part of the graph
	 * @param windows all the windows of the {@link ScreenDockStation}
	 */
	protected void expand( int index, DefaultNode[] nodes, ScreenDockWindow[] windows ){
		if( nodes[index] == null ) {
			nodes[index] = new DefaultNode( index, windows[index] );
		}

		for( int i = 0, n = nodes.length; i < n; i++ ) {
			if( i != index ) {
				Side relation = relation( windows[index], windows[i] );
				if( relation != null ) {
					if( nodes[i] == null ){
						nodes[i] = new DefaultNode( i, windows[i] );
					}
					
					nodes[index].add( relation, nodes[i] );
				}
			}
		}
		
		for( DefaultEdge edge : nodes[ index ].getEdges() ){
			if( edge.getTarget() != nodes[ index ]){
				expand( edge.getTarget().getIndex(), nodes, windows );
			}
		}
	}

	/**
	 * Gets the relation of <code>moved</code> to <code>fixed</code>. The relation is <code>null</code>
	 * if the two windows to not stick together.
	 * @param moved the window that was moved
	 * @param fixed the window that was not moved
	 * @return if <code>fixed</code> depends on <code>moved</code>: the side at which <code>fixed</code>
	 * stays, <code>null</code> if <code>fixed</code> does not depend on <code>moved</code>
	 */
	protected Side relation( ScreenDockWindow moved, ScreenDockWindow fixed ){
		Attraction attraction = getController().getStickiness( moved.getDockable(), fixed.getDockable() );
		if( attraction == Attraction.ATTRACTED || attraction == Attraction.STRONGLY_ATTRACTED ) {
			MagnetController controller = getController();

			if( controller.intersectHorizontally( moved, fixed ) ) {
				if( controller.distance( moved, Side.EAST, fixed, Side.WEST ) == 1 ) {
					return Side.EAST;
				}
				if( controller.distance( moved, Side.WEST, fixed, Side.EAST ) == 1 ) {
					return Side.WEST;
				}
			}

			if( controller.intersectVertically( moved, fixed ) ) {
				if( controller.distance( moved, Side.NORTH, fixed, Side.SOUTH ) == 1 ) {
					return Side.NORTH;
				}
				if( controller.distance( moved, Side.SOUTH, fixed, Side.NORTH ) == 1 ) {
					return Side.SOUTH;
				}
			}

		}
		return null;
	}

	/**
	 * Represents one node of the graph.
	 * @author Benjamin Sigg
	 */
	public interface Node{
		/**
		 * Gest the window which is described by this node.
		 * @return the window
		 */
		public ScreenDockWindow getWindow();
		
		/**
		 * Tells whether <code>window</code> is a neighbor of this node and
		 * depends on this node, and if so tells on which side of this node
		 * <code>window</code> lies.
		 * @param window a window that might be a neighbor of this node
		 * @return the side at which <code>window</code> lies or <code>null</code>
		 */
		public Side getNeighbor( ScreenDockWindow window );
		
		/**
		 * Gets a list of all edges that either start or end at this node.
		 * @return the list of edges, may be empty
		 */
		public Edge[] getEdges();
	}
	
	/**
	 * Represents an edge between two {@link Node}s of a graph.
	 * @author Benjamin Sigg	 
	 */
	public interface Edge{
		/**
		 * Gets the starting node of this edge.
		 * @return the start
		 */
		public Node getSource();
		
		/**
		 * Gets the ending node of this edge.
		 * @return the end
		 */
		public Node getTarget();
		
		/**
		 * Tells at which side of the {@link #getSource() starting node} this
		 * edge leaves the node.
		 * @return the side
		 */
		public Side getSide();
	}
	
	/**
	 * Default implementation of {@link Edge}.
	 * @author Benjamin Sigg
	 */
	protected class DefaultEdge implements Edge{
		private DefaultNode source;
		private DefaultNode target;
		private Side side;
		
		public DefaultEdge( DefaultNode source, DefaultNode target, Side side ){
			this.source = source;
			this.target = target;
			this.side = side;
		}
		
		public DefaultNode getSource(){
			return source;
		}
		
		public DefaultNode getTarget(){
			return target;
		}
		
		public Side getSide(){
			return side;
		}
	}
	
	/**
	 * The default implementation of {@link Node}
	 * @author Benjamin Sigg
	 */
	protected class DefaultNode implements Node{
		private ScreenDockWindow window;
		private int index;
		
		private List<DefaultEdge> edges;

		/**
		 * Creates a new node.
		 * @param index the location of this node in the array of all nodes
		 * @param window the window represented by this node
		 */
		public DefaultNode( int index, ScreenDockWindow window ){
			this.index = index;
			this.window = window;
		}
		
		public ScreenDockWindow getWindow(){
			return window;
		}
		
		/**
		 * Gets the location of this node in the array of all nodes.
		 * @return the location
		 */
		public int getIndex(){
			return index;
		}
		
		/**
		 * Creates a new edge between <code>this</code> and <code>depending</code>. If there is already
		 * an edge from <code>depending</code> to <code>this</code>, then nothing happens.
		 * @param side the side at which <code>depending</code> lies
		 * @param depending the node to which a new edge may be created
		 */
		public void add( Side side, DefaultNode depending ){
			if( !depending.contains( this )){
				DefaultEdge edge = new DefaultEdge( this, depending, side );
				add( edge );
				depending.add( edge );
			}
		}

		public void add( DefaultEdge edge ){
			if( edges == null ){
				edges = new ArrayList<DefaultEdge>();
			}
			edges.add( edge );
		}
		
		/**
		 * Checks whether there is an edge from <code>this</code> node to <code>node</code>.
		 * @param node the node to search
		 * @return <code>true</code> if there is a directed edge
		 */
		public boolean contains( DefaultNode node ){
			if( edges != null ){
				for( Edge edge : edges ){
					if( edge.getTarget() == node ){
						return true;
					}
				}
			}
			return false;
		}
		
		public Side getNeighbor( ScreenDockWindow window ){
			if( edges != null && getWindow() != window ){
				for( Edge edge : edges ){
					if( edge.getTarget().getWindow() == window ){
						return edge.getSide();
					}
				}
			}
			return null;
		}
		
		public DefaultEdge[] getEdges(){
			if( edges == null ){
				return new DefaultEdge[]{};
			}
			else{
				return edges.toArray( new DefaultEdge[ edges.size() ] );
			}
		}
	}
}
