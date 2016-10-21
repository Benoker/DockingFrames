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
package bibliothek.gui.dock.station.screen.magnet;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import bibliothek.gui.dock.ScreenDockStation;
import bibliothek.gui.dock.station.screen.BoundaryRestriction;
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

	/** information about the event */
	private MagnetRequest request;

	/** the node that represents the {@link ScreenDockWindow} of {@link #request} */
	private DefaultNode root;
	
	/** a list containing all nodes of this graph */
	private List<DefaultNode> nodes = new ArrayList<StickMagnetGraph.DefaultNode>();
	
	/** all edges of this graph */
	private List<DefaultEdge> edges = new ArrayList<StickMagnetGraph.DefaultEdge>();
	
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
	
	@Override
	public String toString(){
		if( root == null ){
			return getClass().getSimpleName() + "[root=null]";
		}
		else{
			final StringBuilder builder = new StringBuilder();
			builder.append( getClass().getSimpleName() );
			builder.append( "[\nroot: " );
			
			getRoot().visit( new Visitor(){
				private int depth = 0;
				
				public boolean beginVisit( Node node, boolean revisit ){
					builder.append( node.hashCode() ).append(" '").append( node.getWindow().getDockable().getTitleText() ).append( "' " );
					builder.append( node.getConstraints() ).append( "\n" );
					depth++;
					return !revisit;
				}
				
				public void endVisit( Edge edge ){
					depth--;
				}
				
				public void endVisit( Node node ){
					depth--;
				}
				
				public boolean beginVisit( Edge edge ){
					for( int i = 0; i < depth; i++ ){
						builder.append( "  " );
					}
					depth++;
					builder.append( "-> " ).append( edge.getSide().name().toLowerCase() );
					builder.append( " " );
					return true;
				}
			});
			
			builder.append( "]" );
			return builder.toString();
		}
	}
	
	/**
	 * Calls {@link Node#unmark()} on all nodes of this graph
	 */
	public void unmark(){
		for( DefaultNode node : nodes ){
			node.unmark();
		}
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
		queue.add( index );
		
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
					window.setWindowBounds( bounds );
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
	 * the same graph as <code>this</code> would be created. In general this method prefers
	 * to change only the position of {@link ScreenDockWindow}s, the method is however free
	 * to change the size as well if it looks like a good choice.
	 */
	public void moveAndResizeNeighbors(){
		Rectangle initial = request.getInitialBounds( request.getWindow() );
		Rectangle current = request.getResultBounds();
		
		StickMagnetGraphConstraint constraint = getRoot().getConstraints();
		
		constraint.set( Side.NORTH, current.y- initial.y, true, true );
		constraint.set( Side.WEST, current.x - initial.x, true, true );
		constraint.set( Side.EAST, (current.x + current.width) - (initial.x + initial.width), true, true );
		constraint.set( Side.SOUTH, (current.y + current.height) - (initial.y + initial.height), true, true );

		for( Side side : Side.values() ){
			hardPush( side );
		}
		validateHardConstraints();		
		buildConstraints();
		validateConstraints();		
		executeConstraints();
	}
	
	/**
	 * Gets all edges of this graph ordered by the distance of their {@link Edge#getTarget() target} node
	 * to the root node.
	 * @return the edges
	 */
	protected DefaultEdge[] getEdgesByDistance(){
		DefaultEdge[] edges = this.edges.toArray( new DefaultEdge[ this.edges.size() ] );
		Arrays.sort( edges, new Comparator<DefaultEdge>(){
			public int compare( DefaultEdge o1, DefaultEdge o2 ){
				return o2.getTarget().getRootDistance() - o1.getTarget().getRootDistance();
			}
		});
		return edges;
	}
	
	/**
	 * Makes an initial guess telling which node has to be resized by how much. This means that the
	 * {@link Node#getConstraints() constraints} of the nodes are set.
	 */
	protected void buildConstraints(){
		DefaultEdge[] edges = getEdgesByDistance();
		
		// fill in direct constraints
		for( DefaultEdge edge : edges ){
			Node target = edge.getTarget();
			StickMagnetGraphConstraint constraint = target.getConstraints();
			Side side = edge.getSide();
			StickMagnetGraphConstraint partner = edge.getSource().getConstraints();
			
			if( partner.isSet( side )){
				int delta = partner.get( side );
			
				constraint.set( side.opposite(), delta );
				if( partner.isDirect( side )){
					constraint.setDirect( side.opposite(), true );
				}
				
				if( !constraint.isDirect( side ) || !constraint.isSet( side )){
					constraint.set( side, delta );
				}
			}
		}
		
		// fill in unset constraints
		for( DefaultEdge edge : edges ){
			StickMagnetGraphConstraint source = edge.getSource().getConstraints();
			StickMagnetGraphConstraint target = edge.getTarget().getConstraints();
			Side side = edge.getSide();
			
			if( source.isSet( side ) && !target.isDirect( side.opposite() )){
				target.set( side.opposite(), source.get( side ));
			}
			else if( target.isSet( side.opposite() ) && !source.isDirect( side )){
				source.set( side, target.get( side.opposite() ) );
			}
			if( !source.isSet( side )){
				source.set( side, 0 );
			}
			if( !target.isSet( side.opposite() )){
				target.set( side.opposite(), 0 );
			}
		}
	}
	
	/**
	 * Starting at the root node, this method follows all {@link Edge}s going into direction <code>side</code>
	 * and sets the hard flag on <code>side</code> and on {@link Side#opposite()} on all {@link Node#getConstraints()}s it encounters.
	 * @param side the direction to travel
	 */
	protected void hardPush( final Side side ){
		getRoot().visit( new Visitor(){
			private Edge edge;
			
			public boolean beginVisit( Edge edge ){
				this.edge = edge;
				if( edge.getSide() == side ){
					return true;
				}
				if( edge.getSide() == side.opposite() ){
					return false;
				}
				int source = controller.getValue( edge.getSource().getWindow(), side, true );
				int target = controller.getValue( edge.getTarget().getWindow(), side, true );
				return source == target;
			}
		
			public void endVisit( Edge edge ){
				// nothing
			}
			public boolean beginVisit( Node node, boolean revisit ){
				if( revisit ){
					return false;
				}
				StickMagnetGraphConstraint constraint = node.getConstraints();
				
				if( edge != null ){
					if( edge.getSide() == side ){
						constraint.setHard( side, true );
						constraint.setHard( side.opposite(), true );
					}
					else{
						constraint.setHard( side, true );
					}
				}
				
				return true;
			}
			public void endVisit( Node node ){
				// nothing	
			}
		});
	}
	
	/**
	 * Ensures that if one side of an {@link Edge} is {@link StickMagnetGraphConstraint#isHard(MagnetRequest.Side) hard}, then
	 * the other side is hard as well.
	 */
	protected void validateHardConstraints(){
		getRoot().visit( new Visitor(){
			public boolean beginVisit( Edge edge ){
				if( edge.getSource().getConstraints().isHard( edge.getSide() )){
					edge.getTarget().getConstraints().setHard( edge.getSide().opposite(), true );
				}
				
				return true;
			}
			public void endVisit( Edge edge ){
				// nothing
			}
			public boolean beginVisit( Node node, boolean revisit ){
				return !revisit;
			}
			public void endVisit( Node node ){
				// nothing
			}
		});
	}
	
	/**
	 * Tries to ensure that the modifications described in {@link Node#getConstraints()} can be achieved. For example
	 * a constraint resulting in a negative width or height of a {@link ScreenDockWindow} can never be achieved.<br>
	 * The default implementation tries to smooth out resizes by distributing the changes to many windows. Note that
	 * truly invalid boundaries will be caught and processed by the {@link BoundaryRestriction}, which cannot be
	 * influenced by the {@link StickMagnetGraph}.<br>
	 * Implementations should also pay attention to {@link StickMagnetGraphConstraint#isHard(MagnetRequest.Side)} and not modify
	 * hard sides.<br>
	 * Note that while the root window has a {@link StickMagnetGraphConstraint}, that constraint is actually ignored. 
	 */
	protected void validateConstraints(){
		// the nodes that were resized and need further validation
		final List<Node> resizedVertically = new ArrayList<StickMagnetGraph.Node>();
		final List<Node> resizedHorizontally = new ArrayList<StickMagnetGraph.Node>();
		
		getRoot().visit( new Visitor(){
			public void endVisit( Edge edge ){
				// ignore
			}
			
			public void endVisit( Node node ){
				// ignore
			}
			
			public boolean beginVisit( Edge edge ){
				return true;
			}
			
			public boolean beginVisit( Node node, boolean revisit ){
				if( revisit ){
					return false;
				}
				StickMagnetGraphConstraint constraints = node.getConstraints();
				if( constraints.isSet( Side.SOUTH ) && constraints.isSet( Side.NORTH )){
					if( !constraints.isHard( Side.SOUTH ) || !constraints.isHard( Side.NORTH )){
						if( constraints.get( Side.SOUTH ) != constraints.get( Side.NORTH )){
							resizedVertically.add( node );
						}
					}
				}
				if( constraints.isSet( Side.EAST ) && constraints.isSet( Side.WEST )){
					if( !constraints.isHard( Side.EAST ) || !constraints.isHard( Side.WEST )){
						if( constraints.get( Side.EAST ) != constraints.get( Side.WEST )){
							resizedHorizontally.add( node );
						}
					}
				}
				return true;
			}
		});
		
		for( Node node : resizedHorizontally ){
			resizeRipple( node, Side.WEST );
		}
		for( Node node : resizedVertically ){
			resizeRipple( node, Side.NORTH );
		}
	}
	
	/**
	 * Starting from the resized node <code>node</code> this method distributes resizing over a chain of
	 * nodes. All the affected sides are set to {@link StickMagnetGraphConstraint#setHard(MagnetRequest.Side, boolean) hard} to
	 * prevent further modifications. Resizing stops at any {@link StickMagnetGraphConstraint#isHard(MagnetRequest.Side) hard} side.
	 * @param node the node that was resized
	 * @param topleft the direction into which the resize operation should ripple, the operation will also ripple in
	 * the opposite direction
	 */
	protected void resizeRipple( Node node, Side topleft ){
		int leftInitial = rippleBorder( node, topleft, true );
		unmark();
		
		int rightInitial = rippleBorder( node, topleft.opposite(), true );
		unmark();
		
		int leftAfter = rippleBorder( node, topleft, false );
		unmark();
		
		int rightAfter = rippleBorder( node, topleft.opposite(), false );
		unmark();
		
		if( leftInitial == rightInitial || leftAfter == rightAfter ){
			// should not happen often, actually should not happen ever
			return;
		}
		
		rippleSide( node, topleft, leftInitial, leftAfter, rightInitial, rightAfter );
		unmark();
		
		rippleSide( node, topleft.opposite(), leftInitial, leftAfter, rightInitial, rightAfter );
		unmark();
	}
	
	private int rippleBorder( Node start, Side direction, boolean initial ){
		start.mark();
		
		int result = controller.getValue( start.getWindow(), direction, true );
		if( !initial ){
			if( start.getConstraints().isSet( direction )){
				result += start.getConstraints().get( direction );
			}
		}
		
		if( start.getConstraints().isHard( direction )){
			return result;
		}
		
		if( direction == Side.NORTH || direction == Side.WEST ){
			for( Edge edge : start.getEdges() ){
				if( edge.getSource() == start && edge.getSide() == direction && !edge.getTarget().isMarked() ){
					result = Math.min( result, rippleBorder( edge.getTarget(), direction, initial ) );
				}
				else if( edge.getTarget() == start && edge.getSide() == direction.opposite() && !edge.getSource().isMarked() ){
					result = Math.min( result, rippleBorder( edge.getSource(), direction, initial ) );
				}
			}
		}
		else{
			for( Edge edge : start.getEdges() ){
				if( edge.getSource() == start && edge.getSide() == direction && !edge.getTarget().isMarked() ){
					result = Math.max( result, rippleBorder( edge.getTarget(), direction, initial ) );
				}
				else if( edge.getTarget() == start && edge.getSide() == direction.opposite()  && !edge.getSource().isMarked() ){
					result = Math.max( result, rippleBorder( edge.getSource(), direction, initial ) );
				}
			}
		}
		
		return result;
	}
	
	private void rippleSide( Node start, Side direction, int leftInitial, int leftAfter, int rightInitial, int rightAfter ){
		StickMagnetGraphConstraint constraint = start.getConstraints();
		start.mark();
		
		if( constraint.isHard( direction )){
			return;
		}
		for( Edge edge : start.getEdges() ){
			Node target = null;
			
			if( edge.getSource() == start && edge.getSide() == direction ){
				target = edge.getTarget();
			}
			else if( edge.getTarget() == start && edge.getSide() == direction.opposite() ){
				target = edge.getSource();
			}
			
			if( target != null && !target.isMarked() ){
				StickMagnetGraphConstraint next = target.getConstraints();
				if( next.isHard( direction.opposite() )){
					return;
				}
			}
		}
		
		int value = controller.getValue( start.getWindow(), direction, true );
		if( value <= leftInitial || value >= rightInitial ){
			return;
		}
		
		double point = (value - leftInitial) / (double)(rightInitial - leftInitial);
		int actual = (int)(leftAfter + point * (rightAfter - leftAfter) + 0.5);
		int delta = actual - value;

		constraint.set( direction, delta, true, true );
		
		for( Edge edge : start.getEdges() ){
			Node target = null;
			
			if( edge.getSource() == start && edge.getSide() == direction ){
				target = edge.getTarget();
			}
			else if( edge.getTarget() == start && edge.getSide() == direction.opposite() ){
				target = edge.getSource();
			}
			
			if( target != null && !target.isMarked() ){
				StickMagnetGraphConstraint next = target.getConstraints();

				next.set( direction.opposite(), delta, true, true );
				
				rippleSide( target, direction, leftInitial, leftAfter, rightInitial, rightAfter );
			}
		}
	}
	
	/**
	 * Reshapes all nodes except the root node according to {@link Node#getConstraints()}.
	 */
	protected void executeConstraints(){
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
					StickMagnetGraphConstraint constraint = node.getConstraints();
					if( constraint != null ){
						Rectangle initial = request.getInitialBounds( node.getWindow() );
						if( constraint.isSet( Side.NORTH )){
							initial.y += constraint.get( Side.NORTH );
							
							if( constraint.isSet( Side.SOUTH )){
								initial.height -= constraint.get( Side.NORTH );
								initial.height += constraint.get( Side.SOUTH );
							}
						}
						else if( constraint.isSet( Side.SOUTH ) ){
							initial.y += constraint.get( Side.SOUTH );
						}
						
						if( constraint.isSet( Side.WEST )){
							initial.x += constraint.get( Side.WEST );
							if( constraint.isSet( Side.EAST ) ){
								initial.width -= constraint.get( Side.WEST );
								initial.width += constraint.get( Side.EAST );
							}
						}
						else if( constraint.isSet( Side.EAST )){
							initial.x += constraint.get( Side.EAST );
						}
						

						
						
						node.getWindow().setWindowBounds( initial );
					}
				}
				node.getConstraints().reset();
				return true;
			}
			
			public void endVisit( Node node ){
				// ignore	
			}
		});
	}
	
	/**
	 * Tells whether the side <code>side</code> of <code>window</code> will be moved if the
	 * root window is moved at <code>side</code>. This means that there is a path from the root
	 * window at <code>side</code> to <code>window</code> at <code>side</code> going only in
	 * directions <code>side</code> and {@link Side#opposite()}.
	 * @param window the window to check
	 * @param side the side that might be affected 
	 * @return <code>true</code> if <code>side</code> is affected by the root window
	 */
	public boolean depends( final ScreenDockWindow window, final Side side ){
		return depends( getRoot(), window, side );
	}
	
	private boolean depends( Node node, ScreenDockWindow window, Side side ){
		if( node.isMarked() ){
			return false;
		}
		if( node.getWindow() == window ){
			return true;
		}
		node.mark();
		
		for( Edge edge : node.getEdges() ){
			if( edge.getSide() == side || edge.getSide() == side.opposite() ){
				if( edge.getSource() == node ){
					if( depends( edge.getTarget(), window, side )){
						node.unmark();
						return true;
					}
				}
				else{
					if( depends( edge.getSource(), window, side )){
						node.unmark();
						return true;
					}
				}
			}
		}
		
		node.unmark();
		return false;
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
		 * later case {@link #endVisit(StickMagnetGraph.Edge)} is called immediately
		 */
		public boolean beginVisit( Node node, boolean revisit );
		
		/**
		 * Called when <code>node</code> is popped from the stack.
		 * @param node the node that is no longer visited
		 */
		public void endVisit( Node node );
		
		/**
		 * Called when <code>edge</code> is added to the stack. The visitor always follows the edges from
		 * {@link Edge#getSource() source} to {@link Edge#getTarget() target}.
		 * @param edge the edge that is going to be visited
		 * @return <code>true</code> if the visitor should follow the edge, <code>false</code> if not.
		 * In the later case {@link #endVisit(StickMagnetGraph.Edge)} is called immediately
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
		
		/**
		 * Gets the constraints telling how this node has to be modified.
		 * @return the constraints, not <code>null</code>
		 */
		public StickMagnetGraphConstraint getConstraints();
		
		/**
		 * Marks this node with a flag.
		 */
		public void mark();
		
		/**
		 * Unmarks this node from the flag that was set by {@link #mark()}.
		 */
		public void unmark();
		
		/**
		 * Tells whether a flag was set by {@link #mark()}.
		 */
		public boolean isMarked();
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
			edges.add( this );
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
		
		private StickMagnetGraphConstraint constraints = new StickMagnetGraphConstraint();
		
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
			
			nodes.add( this );
		}
		
		public void visit( Visitor visitor ){
			try{
				doVisit( visitor );
			}
			finally{
				StickMagnetGraph.this.unmark();
			}
		}
		
		/**
		 * Gets the constraints telling how this node has to be modified.
		 * @return the constraints
		 */
		public StickMagnetGraphConstraint getConstraints(){
			return constraints;
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
						int distance = edge.getSource().getRootDistance() + 1;
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

		public void mark(){
			mark = true;
		}
		
		public boolean isMarked(){
			return mark;
		}
		
		public void unmark(){
			mark = false;
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
		 * an edge from either node to the other, then nothing happens.
		 * @param side the side at which <code>depending</code> lies
		 * @param depending the node to which a new edge may be created
		 */
		public void add( Side side, DefaultNode depending ){
			if( !depending.contains( this ) && !contains( depending )){
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
