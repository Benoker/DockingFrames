/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Com
import java.util.LinkedList;

import java.util.LinkedList;

import java.util.LinkedList;

import java.util.Map;

import java.util.Map;
ponent the developer likes to add.
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
package bibliothek.gui.dock.station.screen.magnet;

import java.awt.Insets;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import bibliothek.gui.dock.ScreenDockStation;
import bibliothek.gui.dock.station.screen.ScreenDockWindow;
import bibliothek.gui.dock.station.screen.magnet.AttractorStrategy.Attraction;
import bibliothek.gui.dock.station.screen.magnet.MagnetRequest.Side;

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
	 * Builds the entire stickiness graph using a breath first search algorithm. 
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

		LinkedList<Integer> queue = new LinkedList<Integer>();
		queue.push( index );
		
		while( !queue.isEmpty() ){
			index = queue.poll();
			
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
					queue.offer( edge.getTarget().getIndex() );
				}
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

			if( controller.intersectHorizontally( moved, fixed, true ) ) {
				if( controller.distance( moved, Side.EAST, fixed, Side.WEST, true ) == 1 ) {
					return Side.EAST;
				}
				if( controller.distance( moved, Side.WEST, fixed, Side.EAST, true ) == 1 ) {
					return Side.WEST;
				}
			}

			if( controller.intersectVertically( moved, fixed, true ) ) {
				if( controller.distance( moved, Side.NORTH, fixed, Side.SOUTH, true ) == 1 ) {
					return Side.NORTH;
				}
				if( controller.distance( moved, Side.SOUTH, fixed, Side.NORTH, true ) == 1 ) {
					return Side.SOUTH;
				}
			}

		}
		return null;
	}
	
	/**
	 * Compares the initial location of the root {@link ScreenDockWindow} with its
	 * current location and moves all neighbors by the same amount. This method does
	 * not change the size of any window.
	 */
	public void moveNeighbors(){
		Rectangle initial = request.getInitialBounds( request.getWindow() );
		Rectangle current = request.getResultBounds();
		
		final int dx = current.x - initial.x;
		final int dy = current.y - initial.y;
		
		getRoot().visit( new Visitor(){
			public boolean beginVisit( Edge edge ){
				return true;
			}
			public void endVisit( Edge edge ){
				// nothing	
			}
			
			public boolean beginVisit( Node node, boolean revisit ){
				if( revisit ){
					return false;
				}
				
				ScreenDockWindow window = node.getWindow();
				
				if( window != request.getWindow() ){
					Rectangle bounds = request.getInitialBounds( window );
					bounds.x += dx;
					bounds.y += dy;
					window.setWindowBounds( bounds, true );
				}
				
				return true;
			}
			
			public void endVisit( Node node ){
				// nothing
			}
		});
	}
	
	/**
	 * Compares the initial location and side of the root {@link ScreenDockWindow} with
	 * its current shape and reshapes all neighbors such that with the resulting boundaries
	 * the same graph as <code>this</code> would be created. In general this method preferres
	 * to change only the position of {@link ScreenDockWindow}s, the method is however free
	 * to change the size as well if it looks like a good choice.
	 */
	public void moveAndResizeNeighbors(){
		// a value of Integer.MIN_VALUE is an unset constraint
		Map<Node, Insets> constraints = new HashMap<Node, Insets>();
		
		Rectangle initial = request.getInitialBounds( request.getWindow() );
		Rectangle current = request.getResultBounds();
		
		int deltaWest = current.x - initial.x;
		int deltaNorth = current.y - initial.y;
		int deltaEast = (current.x + current.width) - (initial.x + initial.width);
		int deltaSouth = (current.y + current.height) - (initial.y + initial.height);
		
		constraints.put( getRoot(), new Insets( deltaNorth, deltaWest, deltaSouth, deltaEast ) );
		
		buildConstraints( constraints );
		validateConstraints( constraints );
		executeConstraints( constraints );
	}
	
	/**
	 * Makes an initial guess telling which node has to be resized by how much. Each node is associated with
	 * an {@link Insets} object, the value of each field of that {@link Insets} object is the delta in pixels that
	 * the border of the {@link ScreenDockWindow} has to be moved. A value of {@link Integer#MIN_VALUE} or of
	 * <code>null</code> indicates that no constraint has been found.
	 * @param constraints the map of constraints that has to be filled, the root node is already set
	 */
	protected void buildConstraints( final Map<Node, Insets> constraints ){
		getRoot().visit( new Visitor(){
			public boolean beginVisit( Edge edge ){
				Node target = edge.getTarget();
				Insets insets = constraints.get( target );
				if( insets == null ){
					insets = new Insets( Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE );
					constraints.put( target, insets );
				}
				Side side = edge.getSide();
				int delta = get( constraints.get( edge.getSource() ), side );
				put( insets, side.opposite(), delta );
				if( get( insets, side ) == Integer.MIN_VALUE ){
					put( insets, side, delta );
				}
				return true;
			}
			
			public void endVisit( Edge edge ){
				// nothing
			}
			
			public boolean beginVisit( Node node, boolean revisit ){
				return true;
			}
			
			public void endVisit( Node node ){
				
			}
		});
	}
	
	/**
	 * Tries to ensure that the modifications described in <code>constraints</code> can be achieved. For example
	 * a constraint resulting in a negative width or height of a {@link ScreenDockWindow} can never be achieved.
	 * @param constraints tells for each {@link Node} how its borders are to be moved, an entry of <code>null</code> or
	 * a value of {@link Integer#MIN_VALUE} indicate that no constraint is set
	 */
	protected void validateConstraints( final Map<Node, Insets> constraints ){
		
	}
	
	/**
	 * Reshapes all nodes except the root node according to <code>constraints</code>.
	 * @param constraints tells for each {@link Node} how its borders are to be moved, an entry of <code>null</code> or
	 * a value of {@link Integer#MIN_VALUE} indicate that no constraint is set
	 */
	protected void executeConstraints( final Map<Node, Insets> constraints ){
		getRoot().visit( new Visitor(){
			public boolean beginVisit( Edge edge ){
				return true;
			}
			public void endVisit( Edge edge ){
				// nothing
			}
			
			public boolean beginVisit( Node node, boolean revisit ){
				if( revisit ){
					return false;
				}
				if( node != getRoot() ){
					Insets insets = constraints.get( node );
					if( insets != null ){
						Rectangle initial = request.getInitialBounds( node.getWindow() );
						if( insets.top != Integer.MIN_VALUE ){
							initial.y += insets.top;
							initial.height -= insets.top;
						}
						if( insets.bottom != Integer.MIN_VALUE ){
							initial.height += insets.bottom;
						}
						else if( insets.top != Integer.MIN_VALUE ){
							initial.height += insets.top;
						}
						
						if( insets.left != Integer.MIN_VALUE ){
							initial.x += insets.left;
							initial.width -= insets.left;
						}
						if( insets.right != Integer.MIN_VALUE ){
							initial.width += insets.right;
						}
						else if( insets.left != Integer.MIN_VALUE ){
							initial.width += insets.left;
						}
						
						node.getWindow().setWindowBounds( initial, true );
					}
				}
				return true;
			}
			
			public void endVisit( Node node ){
				// ignore	
			}
		});
	}
	
	private int get( Insets insets, Side side ){
		switch( side ){
			case EAST: return insets.right;
			case NORTH: return insets.top;
			case SOUTH: return insets.bottom;
			case WEST: return insets.left;
			default: throw new IllegalArgumentException( "unknown side: " + side );
		}
	}
	
	private void put( Insets insets, Side side, int value ){
		switch( side ){
			case EAST:
				insets.right = value;
				break;
			case WEST:
				insets.left = value;
				break;
			case NORTH:
				insets.top = value;
				break;
			case SOUTH:
				insets.bottom = value;
				break;
		}
	}
	
	/**
	 * A {@link Visitor} can be used to visit all the nodes of the graph. 
	 * @author Benjamin Sigg
	 */
	public interface Visitor{
		/**
		 * Called when <code>node</code> is added to the stack.
		 * @param node the node that is visited
		 * @param revisit whether this node has already been visited 
		 * @return <code>true</code> if the node should be visited, <code>false</code> if not. In the
		 * later case {@link #endVisit(Edge)} is called immediatelly
		 */
		public boolean beginVisit( Node node, boolean revisit );
		
		/**
		 * Called when <code>node</code> is popped from the stack.
		 * @param node the node that is no longer visited
		 */
		public void endVisit( Node node );
		
		/**
		 * Called when <code>edge</code> is added to the stack.
		 * @param edge the edge that is going to be visited
		 * @return <code>true</code> if the visitor should follow the edge, <code>false</code> if not.
		 * In the later case {@link #endVisit(Edge)} is called immediatelly
		 */
		public boolean beginVisit( Edge edge );
		
		/**
		 * Called when <code>edge</code> is popped from the stack.
		 * @param edge the edge that is no longer visited
		 */
		public void endVisit( Edge edge );
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
		
		/**
		 * Visits this node and all its children.
		 * @param visitor the visitor used to traverse the node
		 */
		public void visit( Visitor visitor );
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
		
		private boolean mark = false;
		
		/**
		 * Minimum distance to the root node, 0 for the root node. -1 indicates that the distance
		 * has not yet been calculated
		 */
		private int rootDistance = -1;

		/**
		 * Creates a new node.
		 * @param index the location of this node in the array of all nodes
		 * @param window the window represented by this node
		 */
		public DefaultNode( int index, ScreenDockWindow window ){
			this.index = index;
			this.window = window;
		}
		
		public void visit( Visitor visitor ){
			try{
				doVisit( visitor );
			}
			finally{
				unmark();
			}
		}
		
		/**
		 * Gets the distance of this node to the root node.
		 * @return the distance, 0 for the root node itself
		 */
		public int getRootDistance(){
			if( rootDistance == -1 ){
				int min = -1;
				for( DefaultEdge edge : edges ){
					if( edge.getTarget() == this ){
						int distance = edge.getSource().getRootDistance();
						if( min == -1 ){
							min = distance;
						}
						else{
							min = Math.min( distance, min );
						}
					}
				}
				if( min == -1 ){
					min = 0;
				}
				rootDistance = min;
			}
			
			return rootDistance;
		}
		
		/**
		 * Visits this node and all its children, sets the {@link #mark} flag.
		 * @param visitor the visitor that is called
		 */
		public void doVisit( Visitor visitor ){
			if( visitor.beginVisit( this, mark )){
				mark = true;
				if( edges != null ){
					for( DefaultEdge edge : edges ){
						if( edge.getSource() == this ){
							if( visitor.beginVisit( edge )){
								edge.getTarget().doVisit( visitor );
								visitor.endVisit( edge );
							}
						}
					}
				}
			}
			visitor.endVisit( this );
		}
		
		/**
		 * Removes the {@link #mark} flag from this and from all children.
		 */
		public void unmark(){
			if( mark ){
				mark = false;
				
				if( edges != null ){
					for( DefaultEdge edge : edges ){
						edge.getTarget().unmark();
						edge.getSource().unmark();
					}
				}
			}
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
			rootDistance = -1;
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
