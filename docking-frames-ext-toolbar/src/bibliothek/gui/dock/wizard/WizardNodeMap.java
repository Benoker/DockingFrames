/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2012 Herve Guillaume, Benjamin Sigg
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
 * Herve Guillaume
 * rvguillaume@hotmail.com
 * FR - France
 *
 * Benjamin Sigg
 * benjamin_sigg@gmx.ch
 * CH - Switzerland
 */
package bibliothek.gui.dock.wizard;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.SplitDockStation.Orientation;
import bibliothek.gui.dock.station.DockableDisplayer;
import bibliothek.gui.dock.station.split.Leaf;
import bibliothek.gui.dock.station.split.Node;
import bibliothek.gui.dock.station.split.Placeholder;
import bibliothek.gui.dock.station.split.Root;
import bibliothek.gui.dock.station.split.SplitNode;
import bibliothek.gui.dock.station.split.SplitNodeVisitor;
import bibliothek.gui.dock.wizard.WizardSplitDockStation.Side;

/**
 * The node map tells the location of nodes and columns. It does not offer any logic to change these
 * properties.
 * @author Benjamin Sigg
 */
public abstract class WizardNodeMap {
	private Map<SplitNode, Column> columns;
	private WizardSplitDockStation station;
	
	/** Information about columns that needs to persist even when the stations layout changes */
	private PersistentColumn[] persistentColumns;
	
	/**
	 * Creates a new map using the current content of <code>station</code>
	 * @param station the station whose content is to be analyzed
	 * @param persistentColumns the current columns and their current size
	 */
	public WizardNodeMap( WizardSplitDockStation station, PersistentColumn[] persistentColumns ){
		this.station = station;
		this.persistentColumns = persistentColumns;
	}
	
	private void buildColumns(){
		columns = new HashMap<SplitNode, Column>();
		
		station.getRoot().visit( new SplitNodeVisitor(){
			@Override
			public void handleRoot( Root root ){
				// ignore
			}

			@Override
			public void handleNode( Node node ){
				if( isColumnRoot( node ) ) {
					columns.put( node, new Column( node ) );
				}
			}

			@Override
			public void handleLeaf( Leaf leaf ){
				if( isColumnRoot( leaf ) ) {
					columns.put( leaf, new Column( leaf ) );
				}
			}

			@Override
			public void handlePlaceholder( Placeholder placeholder ){
				// ignore
			}
		} );
		
		Column[] array = columns.values().toArray( new Column[ columns.size() ] );
		Arrays.sort( array, new Comparator<Column>(){
			@Override
			public int compare( Column a, Column b ){
				int sa = score( a );
				int sb = score( b );
				if( sa < sb ){
					return -1;
				}
				else if( sa > sb ){
					return 1;
				}
				return 0;
			}
			
			private int score( Column c ){
				int score = 0;
				SplitNode root = c.root;
				while( root != null ){
					SplitNode parent = root.getParent();
					if( parent != null && parent.getChildLocation( root ) > 0 ){
						score++;
					}
					root = parent;
				}
				return score;
			}
		});
		for( int i = 0; i < array.length; i++ ){
			array[i].index = i;
		}
	}
	
	/**
	 * Gets all the columns of this map.
	 * @return all the columns
	 */
	public Map<SplitNode, Column> getColumns(){
		if( columns == null ){
			buildColumns();
		}
		return columns;
	}
	
	/**
	 * Gets the number of columns.
	 * @return the number of columns
	 */
	public int getColumnCount(){
		return getColumns().size();
	}
	
	/**
	 * Gets the <code>index</code>'th column.
	 * @param index the index of the column
	 * @return the column
	 * @throws IndexOutOfBoundsException if <code>index</code> does not point to a column
	 */
	public Column getColumn( int index ){
		for( Column column : getColumns().values() ){
			if( column.index == index ){
				return column;
			}
		}
		throw new IndexOutOfBoundsException( "index: " + index );
	}
	
	/**
	 * Gets all the columns sorted by their {@link Column#getIndex() index}.
	 * @return the ordered columns
	 */
	public Column[] getSortedColumns(){
		Collection<Column> columns = getColumns().values();
		Column[] array = columns.toArray( new Column[ columns.size() ] );
		Arrays.sort( array, new Comparator<Column>(){
			@Override
			public int compare( Column o1, Column o2 ){
				return o1.getIndex() - o2.getIndex();
			}
		});
		return array;
	}

	/**
	 * Tells whether <code>node</code> is the root node of a {@link Column}.
	 * @param node the node to check
	 * @return whether <code>node</code> is the root of a {@link Column}
	 */
	public boolean isColumnRoot( SplitNode node ){
		if( node instanceof Root ) {
			return false;
		}
		else if( node instanceof Node ) {
			Node n = (Node)node;
			
			if( n.getOrientation() == side().getHeaderOrientation() ) {
				return false;
			}
			if( n.getLeft() == null || !n.getLeft().isVisible() ){
				return false;
			}
			if( n.getRight() == null || !n.getRight().isVisible() ){
				return false;
			}
			
			return isHeaderLevel( node );
			
		}
		else if( node instanceof Leaf ) {
			return isHeaderLevel( node, false );
		}
		return false;
	}
	

	/**
	 * Tells whether <code>node</code> is part of the header. The header includes all
	 * nodes whose orientation is orthogonal to the orientation of the layout.
	 * @param node the node to check
	 * @return whether <code>node</code> belongs to the header
	 */
	public boolean isHeaderLevel( SplitNode node ){
		return isHeaderLevel( node, true );
	}

	/**
	 * Tells whether <code>node</code> is part of the header. If <code>recursive</code> is
	 * <code>true</code>, then this node is considered to be part of the header if the parent
	 * node is part of the header (but the <code>recursive</code> attribute does not apply to the parent).
	 * @param node the node to check
	 * @param recursive whether to check the parent node as well
	 * @return whether <code>node</code> belongs to the header
	 */
	public boolean isHeaderLevel( SplitNode node, boolean recursive ){
		if( node instanceof Root ) {
			return true;
		}
		else if( node instanceof Node ) {
			Node n = (Node)node;
			if( n.getLeft() == null || n.getRight() == null ){
				return false;
			}
			else if( !n.getLeft().isVisible() || !n.getRight().isVisible() ){
				return isHeaderLevel( node.getParent(), recursive );
			}
			else if( n.getOrientation() == side().getHeaderOrientation() ) {
				return true;
			}
			else if( recursive ) {
				return isHeaderLevel( node.getParent(), false );
			}
			else {
				return false;
			}
		}
		else if( node.getParent() instanceof Root ) {
			return true;
		}
		else if( node instanceof Leaf ) {
			return isHeaderLevel( node.getParent(), false );
		}
		return false;
	}
	
	private Side side(){
		return station.getSide();
	}
	
	/**
	 * Searches the {@link Column} which is closest to the inside of the parent {@link Container}.
	 * @return the outer most column
	 */
	public Column getOutermostColumn(){
		return getHeadColumn( station.getRoot() );
	}
	
	/**
	 * Searches the {@link Column} which is nearest to the inside of the parent {@link Container},
	 * e.g. is {@link Side} is {@link Side#RIGHT}, then this method would return the left most
	 * {@link Column}.
	 * @param node the node in whose subtree the {@link Column} should be searched
	 * @return the outer most column or <code>null</code> if not found
	 */
	public Column getHeadColumn( SplitNode node ){
		while( node != null ){
			Column column = getColumns().get( node );
			if( column != null ){
				return column;
			}
			if( node instanceof Node ){
				if( ((Node) node).getLeft() == null || !((Node)node).getLeft().isVisible() ){
					node = ((Node) node).getRight();
				}
				else if( ((Node) node).getRight() == null || !((Node)node).getRight().isVisible() ){
					node = ((Node) node).getLeft();
				}
				else if( side() == Side.RIGHT || side() == Side.BOTTOM ){
					node = ((Node)node).getLeft();
				}
				else{
					node = ((Node)node).getRight();
				}
			}
			else if( node instanceof Root ){
				node = ((Root)node).getChild();
			}
			else{
				node = null;
			}
		}
		return null;
	}

	/**
	 * Follows the tree downwards using the {@link Node#getRight() right} path until a {@link Leaf}
	 * is found, the cell matching that leaf is returned.
	 * @param node the starting point of the search
	 * @return a cell or <code>null</code>
	 */
	public PersistentCell getHeadCell( SplitNode node ){
		while( node != null ){
			if( node instanceof Leaf ){
				Dockable dockable = ((Leaf)node).getDockable();
				for( Column column : getColumns().values() ){
					if( column.cells.get( node ) != null ){
						PersistentCell cell = column.getPersistentColumn().getCells().get( dockable );
						if( cell != null ){
							return cell;
						}
					}
				}
				node = null;
			}
			if( node instanceof Node ){
				node = ((Node)node).getRight();
			}
			else{
				node = null;
			}
		}
		return null;
	}
	
	/**
	 * Searches the column which contains <code>node</code>. If <code>node</code> is part of
	 * the header, then the result represents the column at the right side of the divider.
	 * @param node the node whose column index is searched
	 * @return the column, may be <code>null</code>
	 */
	public Column getColumn( SplitNode node ){
		Column column = getColumn( node, true );
		if( column != null ){
			return column;
		}
		if( node instanceof Root ){
			node = ((Root)node).getChild();
		}
		if( node instanceof Node ){
			SplitNode child = ((Node)node).getRight();
			while( child != null ){
				Column result = getColumns().get( child );
				if( result != null ){
					return result;
				}
				if( child instanceof Node ){
					child = ((Node)child).getLeft();
				}
				else{
					child = null;
				}
			}
		}
		return null;
	}
	
	/**
	 * Gets the {@link Column} which contains <code>node</code>.
	 * @param node the node whose column is searched
	 * @param upwards if <code>false</code>, then <code>node</code>
	 * has to be a {@link #isColumnRoot(SplitNode)}, otherwise 
	 * it can be a child of a column root as well.
	 * @return the column or <code>null</code>
	 */
	public Column getColumn( SplitNode node, boolean upwards ){
		if( upwards ){
			Column column = null;
			while( node != null && column == null ) {
				column = getColumns().get( node );
				node = node.getParent();
			}
			return column;
		}
		else{
			return getColumns().get( node );
		}
	}
	
	/**
	 * Gets the column which contains <code>dockable</code>.
	 * @param dockable the element to search
	 * @return the column containing <code>dockable</code>
	 */
	public Column getColumn( Dockable dockable ){
		for( Column column : getColumns().values() ){
			if( column.getLeafs().containsKey( dockable )){
				return column;
			}
		}
		return null;
	}
	
	/**
	 * Goes through all {@link Column}s all collects the last cell of these columns.
	 * @return the last cell of each {@link Column}
	 */
	public Leaf[] getLastLeafOfColumns(){
		List<Leaf> result = new ArrayList<Leaf>();
		for( Column column : getColumns().values() ){
			Leaf last = column.getLastLeafOfColumn();
			if( last != null ){
				result.add( last );
			}
		}
		return result.toArray( new Leaf[ result.size() ] );
	}
	
	/**
	 * Searches the {@link PersistentColumn} of the <code>index</code>'th {@link Column}.
	 * @param index the index of the column
	 * @return the persistent column or <code>null</code> if not found
	 */
	public PersistentColumn getPersistentColumn( int index ){
		for( PersistentColumn column : getPersistentColumns() ){
			if( column.getSource().index == index ){
				return column;
			}
		}
		return null;
	}
	
	public PersistentColumn[] getPersistentColumns(){
		List<PersistentColumn> result = new ArrayList<PersistentColumn>( getColumns().size() );
		for( Column column : getColumns().values() ){
			PersistentColumn next = column.toPersistentColumn();
			if( next != null ){
				result.add( next );
			}
		}
		
		if( persistentColumns == null ){
			persistentColumns = result.toArray( new PersistentColumn[ result.size() ] );
		}
		else {
			persistentColumns = adapt( persistentColumns, result.toArray( new PersistentColumn[ result.size() ] ) );
		}
		handlePersistentColumnsAdapted( persistentColumns );
		return persistentColumns;
	}
	
	/**
	 * Called if the current set of {@link PersistentColumn}s has been changed.
	 * @param persistentColumns the new set of persistent columns
	 */
	protected abstract void handlePersistentColumnsAdapted( PersistentColumn[] persistentColumns );

	/**
	 * Tries to re-map the size information from <code>oldColumns</code> to <code>newColumns</code>. The size
	 * of unmapped columns will be -1.
	 * @param oldColumns an old set of columns, may be modified
	 * @param newColumns the new set of columns, may be modified
	 * @return the re-mapped columns, may be one of the input arrays
	 */
	private PersistentColumn[] adapt( PersistentColumn[] oldColumns, PersistentColumn[] newColumns ){
		for( PersistentColumn column : newColumns ){
			/*
			 * There are three possible operations:
			 * merge -> size = max( sizes )
			 * split -> size = old size
			 * new   -> nop
			 */
			
			Set<PersistentColumn> sources = new HashSet<PersistentColumn>();
			contentLoop:for( Map.Entry<Dockable, PersistentCell> entry : column.getCells().entrySet() ){
				for( PersistentColumn source : oldColumns ){
					PersistentCell cell = source.getCells().get( entry.getKey() );
					if( cell != null ){
						sources.add( source );
						entry.getValue().setSize( cell.getSize() );
						continue contentLoop;
					}
				}
			}
			
			if( sources.size() == 1 ){
				PersistentColumn source = sources.iterator().next();
				if( source.getCells().keySet().containsAll( column.getCells().keySet() )){
					column.setSize( source.getSize() );
				}
				else{
					column.setSize( Math.max( source.getSize(), column.getPreferredSize() ));
				}
			}
			else if( sources.size() > 0 ){
				int max = 0;
				for( PersistentColumn source : sources ){
					max = Math.max( max, source.getSize() );
				}
				column.setSize( max );
			}
		}
		return newColumns;
	}
	
	/**
	 * A column is a set of {@link Cell}s.
	 * @author Benjamin Sigg
	 */
	public class Column{
		private SplitNode root;
		private Map<SplitNode, Cell> cells = new HashMap<SplitNode, Cell>();
		private List<Cell> leafCells = new ArrayList<WizardNodeMap.Cell>();
		private int index;
		
		private Column( SplitNode root ){
			this.root = root;
			root.visit( new SplitNodeVisitor(){
				@Override
				public void handleRoot( Root root ){
					cells.put( root, new Cell( root, Column.this ) );
				}

				@Override
				public void handlePlaceholder( Placeholder placeholder ){
					cells.put( placeholder, new Cell( placeholder, Column.this ) );
				}

				@Override
				public void handleNode( Node node ){
					cells.put( node, new Cell( node, Column.this ) );
				}

				@Override
				public void handleLeaf( Leaf leaf ){
					Cell cell = new Cell( leaf, Column.this );
					cells.put( leaf, cell );
					leafCells.add( cell );
				}
			});
			
			Cell[] array = leafCells.toArray( new Cell[ leafCells.size() ] );
			
			Arrays.sort( array, new Comparator<Cell>(){
				@Override
				public int compare( Cell a, Cell b ){
					int sa = score( a );
					int sb = score( b );
					if( sa < sb ){
						return -1;
					}
					else if( sa > sb ){
						return 1;
					}
					return 0;
				}
				
				private int score( Cell c ){
					SplitNode node = c.getNode();
					int score = 0;
					while( node != Column.this.root ){
						if( node.getParent().getChildLocation( node ) > 0 ){
							score++;
						}
						node = node.getParent();
					}
					return score;
				}
			});
			
			for( int i = 0; i < array.length; i++ ){
				array[i].index = i;
			}
		}

		/**
		 * Gets the root node of this column.
		 * @return the root node
		 */
		public SplitNode getRoot(){
			return root;
		}
		
		/**
		 * Gets the cells ordered by their index.
		 * @return the cells
		 */
		public Cell[] getSortedCells(){
			Cell[] array = cells.values().toArray( new Cell[ cells.size() ] );
			Arrays.sort( array, new Comparator<Cell>(){
				@Override
				public int compare( Cell o1, Cell o2 ){
					return o1.getIndex() - o2.getIndex();
				}
			});
			return array;
		}
		
		/**
		 * Converts this column into a new {@link PersistentColumn}.
		 * @return the new column, can be <code>null</code>
		 */
		public PersistentColumn toPersistentColumn(){
			int size;
			int preferred;
			Map<Dockable, PersistentCell> leafs = getLeafs();
			if( leafs.size() == 0 ){
				return null;
			}
			
			if( side().getHeaderOrientation() == Orientation.HORIZONTAL ){
				size = root.getSize().width;
				preferred = getPreferredSize().width;
			}
			else{
				size = root.getSize().height;
				preferred = getPreferredSize().height;
			}
			return new PersistentColumn( size, preferred, this, leafs );
		}
		
		/**
		 * Gets the index of this column, the left most column has index <code>0</code>.
		 * @return the index
		 */
		public int getIndex(){
			return index;
		}
		
		public PersistentColumn getPersistentColumn(){
			Map<Dockable, PersistentCell> leafs = getLeafs();
			
			for( PersistentColumn column : getPersistentColumns() ){
				if( column.getCells().keySet().equals( leafs.keySet() )){
					return column;
				}
			}
			
			return null;
		}
		
		private Map<Dockable, PersistentCell> getLeafs(){
			final Map<Dockable, PersistentCell> leafs = new HashMap<Dockable, PersistentCell>();
			root.visit( new SplitNodeVisitor(){
				@Override
				public void handleRoot( Root root ){
					// ignore	
				}
				
				@Override
				public void handlePlaceholder( Placeholder placeholder ){
					// ignore					
				}
				
				@Override
				public void handleNode( Node node ){
					// ignore
				}
				
				@Override
				public void handleLeaf( Leaf leaf ){
					Dimension preferredSize = getPreferredSize( leaf );
					if( preferredSize != null ){
						int size;
						int preferred;
						if( side().getHeaderOrientation() == Orientation.HORIZONTAL ){
							size = leaf.getSize().height;
							preferred = preferredSize.height;
						}
						else{
							size = leaf.getSize().width;
							preferred = preferredSize.width;
						}
						leafs.put( leaf.getDockable(), new PersistentCell( size, preferred ));
					}
				}
			} );
			return leafs;
		}
		
		public Cell getRightmostCell( SplitNode node ){
			while( node != null ){
				if( node instanceof Node ){
					node = ((Node)node).getRight();
				}
				else{
					return cells.get( node );
				}
			}
			return null;
		}

		public Cell getLeftmostCell( SplitNode node ){
			while( node != null ){
				if( node instanceof Node ){
					node = ((Node)node).getLeft();
				}
				else{
					return cells.get( node );
				}
			}
			return null;
		}
		
		public Leaf getLastLeafOfColumn(){
			SplitNode node = root;
			while( node != null ){
				if( node instanceof Root ){
					node = ((Root)node).getChild();
				}
				else if( node instanceof Node ){
					node = ((Node)node).getRight();
				}
				else if( node instanceof Leaf ){
					return (Leaf)node;
				}
				else {
					node = null;
				}
			}
			return null;
		}
		
		public Dimension getPreferredSize( SplitNode node ){
			Cell cell = cells.get( node );
			if( cell == null ){
				return null;
			}
			return cell.getPreferredSize();
		}
		
		public Dimension getMinimumSize( SplitNode node ){
			Cell cell = cells.get( node );
			if( cell == null ){
				return null;
			}
			return cell.getMinimumSize();
		}

		public Dimension getPreferredSize(){
			return getPreferredSize( root );
		}
		
		public Dimension getMinimumSize(){
			return getMinimumSize( root );
		}
		
		public Rectangle getBounds(){
			return root.getBounds();
		}
		
		public int getCellCount(){
			return leafCells.size();
		}

		public int getGaps( SplitNode node ){
			Cell cell = cells.get( node );
			if( cell == null ){
				return 0;
			}
			return cell.getGaps();
		}

		public int getGaps(){
			return getGaps( root );
		}
	}
	
	/**
	 * A cell is a single {@link SplitNode}, usually a {@link Leaf}, and a part of a {@link Column}.
	 * @author Benjamin Sigg
	 */
	public class Cell {
		private SplitNode node;
		private Column column;
		private Dimension preferredSize;
		private Dimension minimumSize;
		private int index;
		
		private Cell( SplitNode node, Column column ){
			this.node = node;
			this.column = column;
		}
		
		/**
		 * Gets the node which is represented by this {@link Cell}.
		 * @return the node of this cell
		 */
		public SplitNode getNode(){
			return node;
		}

		/**
		 * Gets the index of this cell.
		 * @return the index
		 */
		public int getIndex(){
			return index;
		}
		
		/**
		 * Gets the preferred size of this cell, does not include any gaps
		 * @return the preferred size ignoring gaps
		 */
		public Dimension getPreferredSize(){
			if( preferredSize == null ) {
				if( node instanceof Leaf ) {
					DockableDisplayer displayer = ((Leaf) node).getDisplayer();
					if( displayer != null ){
						preferredSize = displayer.getComponent().getPreferredSize();
					}
				}
				if( node instanceof Node ) {
					Dimension left = column.getPreferredSize( ((Node) node).getLeft() );
					Dimension right = column.getPreferredSize( ((Node) node).getRight() );
					if( left == null ) {
						preferredSize = right;
					}
					else if( right == null ) {
						preferredSize = left;
					}
					else if( left != null && right != null ) {
						if( ((Node) node).getOrientation() == Orientation.HORIZONTAL ) {
							preferredSize = new Dimension( left.width + right.width, Math.max( left.height, right.height ) );
						}
						else {
							preferredSize = new Dimension( Math.max( left.width, right.width ), left.height + right.height );
						}
					}
				}
				if( node instanceof Root ) {
					preferredSize = column.getPreferredSize( ((Root) node).getChild() );
				}
			}
			return preferredSize;
		}
		
		/**
		 * Gets the minimum size of this cell, does not include any gaps
		 * @return the minimum size ignoring gaps
		 */
		public Dimension getMinimumSize(){
			if( minimumSize == null ) {
				if( node instanceof Leaf ) {
					DockableDisplayer displayer = ((Leaf) node).getDisplayer();
					if( displayer != null ){
						minimumSize = displayer.getComponent().getMinimumSize();
					}
				}
				if( node instanceof Node ) {
					Dimension left = column.getMinimumSize( ((Node) node).getLeft() );
					Dimension right = column.getMinimumSize( ((Node) node).getRight() );
					if( left == null ) {
						minimumSize = right;
					}
					else if( right == null ) {
						minimumSize = left;
					}
					else if( left != null && right != null ) {
						if( ((Node) node).getOrientation() == Orientation.HORIZONTAL ) {
							minimumSize = new Dimension( left.width + right.width, Math.max( left.height, right.height ) );
						}
						else {
							minimumSize = new Dimension( Math.max( left.width, right.width ), left.height + right.height );
						}
					}
				}
				if( node instanceof Root ) {
					minimumSize = column.getMinimumSize( ((Root) node).getChild() );
				}
			}
			return minimumSize;
		}

		/**
		 * Gets the number of gaps between the leafs of this cell
		 * @return the number of gaps
		 */
		public int getGaps(){
			if( node instanceof Leaf ) {
				return 0;
			}
			if( node instanceof Node ) {
				int left = column.getGaps( ((Node) node).getLeft() );
				int right = column.getGaps( ((Node) node).getRight() );
				if( left == -1 ) {
					return right;
				}
				if( right == -1 ) {
					return left;
				}
				if( left == -1 && right == -1 ) {
					return -1;
				}
				return left + 1 + right;
			}
			else if( node instanceof Root ) {
				return column.getGaps( ((Root) node).getChild() );
			}
			else {
				return -1;
			}
		}
	}
}
