///*
// * Bibliothek - DockingFrames
// * Library built on Java/Swing, allows the user to "drag and drop"
// * panels containing any Swing-Component the developer likes to add.
// * 
// * Copyright (C) 2012 Benjamin Sigg
// * 
// * This library is free software; you can redistribute it and/or
// * modify it under the terms of the GNU Lesser General Public
// * License as published by the Free Software Foundation; either
// * version 2.1 of the License, or (at your option) any later version.
// *
// * This library is distributed in the hope that it will be useful,
// * but WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// * Lesser General Public License for more details.
// *
// * You should have received a copy of the GNU Lesser General Public
// * License along with this library; if not, write to the Free Software
// * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
// * 
// * Benjamin Sigg
// * benjamin_sigg@gmx.ch
// * CH - Switzerland
// */
//package bibliothek.gui.dock.station.split.layout;
//
//import bibliothek.gui.dock.SplitDockStation;
//import bibliothek.gui.dock.station.split.DefaultSplitLayoutManager;
//import bibliothek.gui.dock.station.split.Leaf;
//import bibliothek.gui.dock.station.split.Node;
//import bibliothek.gui.dock.station.split.Root;
//import bibliothek.gui.dock.station.split.SplitLayoutManager;
//import bibliothek.gui.dock.station.split.SplitNode;
//import bibliothek.util.ClientOnly;
//
///**
// * This {@link SplitLayoutManager} offers some inner classes to copy and easily modify the
// * tree of {@link SplitNode}s that make up the layout of a {@link SplitDockStation}.<br>
// * This class is intended to be used by clients, it is not used by the framework itself. It only adds
// * new ways to access data that already exists, it does not change any behavior.
// * @author Benjamin Sigg
// * @param <R> the type that is used to describe custom data for a {@link Root}
// * @param <N> the type that is used to describe custom data for a {@link Node}
// * @param <L> the type that is used to describe custom data for a {@link Leaf}
// */
//@ClientOnly
//public class CustomizeableSplitLayoutmanager<R, N, L> extends DefaultSplitLayoutManager {
//
//	public CustomizeableRoot toTree( SplitDockStation station ){
//		
//	}
//	
//	private CustomizeableRoot toRoot( Root root ){
//	}
//	
//	private CustomizeableNode toNode( Node node ){
//		
//	}
//	
//	private CustomizeableLeaf toLeaf( Leaf leaf ){
//		
//	}
//	
//	/**
//	 * A wrapper around a {@link SplitNode}, offers storage for subclasses of {@link CustomizeableSplitLayoutmanager}
//	 * @param <D> the type of the data that is stored in this node
//	 */
//	public abstract class CustomizeableSplitNode<D> {
//		private D data;
//		
//		/**
//		 * Gets the {@link SplitNode} which was used to create this node.
//		 * @return the original node
//		 */
//		public abstract SplitNode getOrigin();
//		
//		/**
//		 * Gets the custom data that was set by a client.
//		 * @return the custom data, can be <code>null</code>
//		 */
//		public D getData(){
//			return data;
//		}
//		
//		/**
//		 * Sets custom data, can be used by a client. It is up to the client to decide what kind of information should
//		 * be stored.
//		 * @param data the data to store, can be <code>null</code>
//		 */
//		public void setData( D data ){
//			this.data = data;
//		}
//	}
//
//	/**
//	 * A wrapper around a {@link Root}.
//	 */
//	public class CustomizeableRoot extends CustomizeableSplitNode<R> {
//		private Root root;
//		private CustomizeableSplitNode<?> child;
//		
//		private CustomizeableRoot( Root root, CustomizeableSplitNode<?> child ){
//			this.root = root;
//		}
//		
//		/**
//		 * Gets the only child of this root, either a {@link CustomizeableNode} or a {@link CustomizeableLeaf}.
//		 * @return the child, can be <code>null</code>
//		 */
//		public CustomizeableSplitNode<?> getChild(){
//			return child;
//		}
//		
//		@Override
//		public Root getOrigin(){
//			return root;
//		}
//	}
//
//	/**
//	 * A wrapper around a {@link Node}
//	 */
//	public class CustomizeableNode extends CustomizeableSplitNode<N> {
//		private Node node;
//		
//		private CustomizeableNode( Node node ){
//			this.node = node;
//		}
//		
//		@Override
//		public SplitNode getOrigin(){
//			return node;
//		}
//	}
//
//	/**
//	 * A wrapper around a {@link Leaf}
//	 */
//	public class CustomizeableLeaf extends CustomizeableSplitNode<L> {
//		private Leaf leaf;
//		
//		private CustomizeableLeaf( Leaf leaf ){
//			this.leaf = leaf;
//		}
//		
//		@Override
//		public SplitNode getOrigin(){
//			return leaf;
//		}
//	}
//}
