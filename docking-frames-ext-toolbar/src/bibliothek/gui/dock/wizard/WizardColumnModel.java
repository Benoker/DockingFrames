package bibliothek.gui.dock.wizard;

import java.awt.Dimension;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.SplitDockStation.Orientation;
import bibliothek.gui.dock.station.split.Divideable;
import bibliothek.gui.dock.station.split.Leaf;
import bibliothek.gui.dock.station.split.Node;
import bibliothek.gui.dock.station.split.Placeholder;
import bibliothek.gui.dock.station.split.Root;
import bibliothek.gui.dock.station.split.SplitNode;
import bibliothek.gui.dock.station.split.SplitNodeVisitor;
import bibliothek.gui.dock.wizard.WizardSplitDockStation.Side;

/**
 * This class offers an interface to the tree of a {@link SplitDockStation} that handles as if the tree
 * would build a table.
 * @author Benjamin Sigg
 */
public class WizardColumnModel {
	private WizardSplitDockStation.Side side;
	private SplitDockStation station;
	private double factorW;
	private double factorH;
	
	/** Information about columns that needs to persist even when the stations layout changes */
	private PersistentColumn[] persistentColumns;
	
	public WizardColumnModel( SplitDockStation station, WizardSplitDockStation.Side side ){
		this( station, side, -1, -1 );
	}

	public WizardColumnModel( SplitDockStation station, WizardSplitDockStation.Side side, double factorW, double factorH ){
		this.station = station;
		this.side = side;
		this.factorH = factorH;
		this.factorW = factorW;
	}
	
	public void setFactors( double factorW, double factorH ){
		this.factorW = factorW;
		this.factorH = factorH;
	}

	/**
	 * Updates the boundaries of all {@link SplitNode}s.
	 * @param x the top left corner
	 * @param y the top left corner
	 */
	public void updateBounds( double x, double y ){
		Root root = station.getRoot();
		root.updateBounds( x, y, 1.0, 1.0, factorW, factorH, false );
		new Table().updateBounds( x, y );
	}

	/**
	 * Calculates the valid value of <code>divider</code> for <code>node</code>.
	 * @param divider the location of the divider, value between 0 and 1
	 * @param node the node whose divider is changed
	 * @return the valid divider
	 */
	public double validateDivider( double divider, Node node ){
		Table table = new Table();
		return table.validateDivider( divider, node );
	}

	private int gap(){
		return station.getDividerSize();
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

	public boolean isHeaderLevel( SplitNode node, boolean recursive ){
		if( node instanceof Root ) {
			return true;
		}
		else if( node instanceof Node ) {
			if( ((Node) node).getOrientation() == side.getHeaderOrientation() ) {
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

	private boolean isColumnRoot( SplitNode node ){
		if( node instanceof Root ) {
			return false;
		}
		else if( node instanceof Node ) {
			if( ((Node) node).getOrientation() == side.getHeaderOrientation() ) {
				return false;
			}
			else {
				return isHeaderLevel( node );
			}
		}
		else if( node instanceof Leaf ) {
			return isHeaderLevel( node, false );
		}
		return false;
	}
	
	public Dimension getPreferredSize(){
		Table table = new Table();
		PersistentColumn[] columns = table.getPersistentColumns();
		
		int size = 0;
		for( PersistentColumn column : columns ){
			size += column.size;
		}
		size += (columns.length-1) * gap();
		
		size = Math.max( size, 5 );
		
		Dimension result;
		
		if( side.getHeaderOrientation() == Orientation.HORIZONTAL ){
			result = new Dimension( size, 10 );
		}
		else{
			result = new Dimension( 10, size );
		}
		
		Insets insets = station.getInsets();
		if( insets != null ){
			result.width += insets.left + insets.right;
			result.height += insets.top + insets.bottom;
		}
		return result;
	}
	
	/**
	 * Called if the user changed the position of a dividier.
	 * @param node the node whose dividier changed
	 * @param divider the new dividier
	 */
	public void setDivider( Divideable divideable, double divider ){
		if( divideable instanceof Node ){
			Node node = (Node)divideable;
			Table table = new Table();
			Column column;
			if( side == Side.RIGHT || side == Side.BOTTOM ){
				column = table.getHeadColumn( node.getRight() );
			}
			else{
				column = table.getHeadColumn( node.getLeft() );
			}
			if( column != null ){
				setDivider( table, column, node.getDivider(), divider, node.getSize() );
			}
			else{
				PersistentCell cell = table.getHeadCell( node.getLeft() );
				if( cell != null ){
					double dividerDelta = divider - node.getDivider();
					int deltaPixel;
					if( side.getHeaderOrientation() == Orientation.HORIZONTAL ){
						deltaPixel = (int)(dividerDelta * node.getSize().height);
					}
					else{
						deltaPixel = (int)(dividerDelta * node.getSize().width);
					}
					cell.size += deltaPixel;
					table.applyPersistentSizes();
				}
				else{
					node.setDivider( divider );
				}
			}
		}
		else if( divideable instanceof ColumnDividier ){
			Table table = new Table();
			Column column = table.getHeadColumn( station.getRoot() );
			if( column != null ){
				setDivider( table, column, divideable.getDivider(), divider, station.getSize() );
			}
		}
	}
	
	private void setDivider( Table table, Column column, double oldDividier, double newDividier, Dimension size ){
		PersistentColumn persistent = column.getPersistentColumn();
		double dividerDelta = oldDividier - newDividier;
		int deltaPixel;
		if( side.getHeaderOrientation() == Orientation.HORIZONTAL ){
			deltaPixel = (int)(dividerDelta * size.width);
		}
		else{
			deltaPixel = (int)(dividerDelta * size.height);
		}
		persistent.size += deltaPixel;
		table.applyPersistentSizes();
	}
	
	/**
	 * Tries to remap the size information from <code>oldColumns</code> to <code>newColumns</code>. The size
	 * of unmapped columns will be -1.
	 * @param oldColumns an old set of columns, may be modified
	 * @param newColumns the new set of columns, may be modified
	 * @return the remaped columns, may be one of the input arrays
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
			contentLoop:for( Map.Entry<Dockable, PersistentCell> entry : column.cells.entrySet() ){
				for( PersistentColumn source : oldColumns ){
					PersistentCell cell = source.cells.get( entry.getKey() );
					if( cell != null ){
						sources.add( source );
						entry.getValue().size = cell.size;
						continue contentLoop;
					}
				}
			}
			
			if( sources.size() == 1 ){
				PersistentColumn source = sources.iterator().next();
				if( source.cells.keySet().equals( column.cells.keySet() )){
					column.size = source.size;
				}
				else{
					column.size = Math.max( column.size, column.preferred );
				}
			}
			else if( sources.size() > 0 ){
				int max = 0;
				for( PersistentColumn source : sources ){
					max = Math.max( max, source.size );
				}
				column.size = max;
			}
		}
		return newColumns;
	}
	
	private class PersistentColumn{
		private int size;
		private int preferred;
		private Map<Dockable, PersistentCell> cells;
		
		public PersistentColumn( int size, int preferred, Map<Dockable, PersistentCell> cells ){
			this.size = size;
			this.preferred = preferred;
			if( size <= 0 ){
				this.size = preferred;
			}
			this.cells = cells;
		}
	}
	
	private class PersistentCell{
		private int size;
		private int preferred;
		
		public PersistentCell( int size, int preferred ){
			this.size = size;
			this.preferred = preferred;
			if( size <= 0 ){
				this.size = preferred;
			}
		}
	}

	private class Base {
		protected void updateBounds( SplitNode node, double x, double y, double width, double height ){
			if( node != null && node.isVisible() ) {
				if( node instanceof Root ) {
					updateBounds( ((Root) node).getChild(), x, y, width, height );
				}
				else if( node instanceof Node ) {
					Node n = (Node) node;
					if( n.getLeft().isVisible() && n.getRight().isVisible() ) {
						if( n.getOrientation() == Orientation.HORIZONTAL ) {
							double dividerWidth = factorW > 0 ? Math.max( 0, gap() / factorW ) : 0.0;
							double dividerLocation = width * n.getDivider();

							updateBounds( n.getLeft(), x, y, dividerLocation - dividerWidth / 2, height );
							updateBounds( n.getRight(), x + dividerLocation + dividerWidth / 2, y, width - dividerLocation - dividerWidth / 2, height );
						}
						else {
							double dividerHeight = factorH > 0 ? Math.max( 0, gap() / factorH ) : 0.0;
							double dividerLocation = height * n.getDivider();

							updateBounds( n.getLeft(), x, y, width, dividerLocation - dividerHeight / 2 );
							updateBounds( n.getRight(), x, y + dividerLocation + dividerHeight / 2, width, height - dividerLocation - dividerHeight / 2 );
						}
					}
					else {
						updateBounds( n.getLeft(), x, y, width, height );
						updateBounds( n.getRight(), x, y, width, height );
					}
				}
				node.setBounds( x, y, width, height, factorW, factorH, true );
			}
		}
	}

	private class Table extends Base {
		private Map<SplitNode, Column> columns = new HashMap<SplitNode, Column>();

		public Table(){
			station.getRoot().visit( new SplitNodeVisitor(){
				@Override
				public void handleRoot( Root root ){
					// ignore
				}

				@Override
				public void handleNode( Node node ){
					if( isColumnRoot( node ) ) {
						columns.put( node, new Column( Table.this, node ) );
					}
				}

				@Override
				public void handleLeaf( Leaf leaf ){
					if( isColumnRoot( leaf ) ) {
						columns.put( leaf, new Column( Table.this, leaf ) );
					}
				}

				@Override
				public void handlePlaceholder( Placeholder placeholder ){
					// ignore
				}
			} );
		}

		public PersistentColumn[] getPersistentColumns(){
			PersistentColumn[] result = new PersistentColumn[ columns.size() ];
			int index = 0;
			for( Column column : columns.values() ){
				result[ index++ ] = column.toPersistentColumn();
			}
			if( persistentColumns == null ){
				persistentColumns = result;
			}
			else {
				persistentColumns = adapt( persistentColumns, result );
			}
			return persistentColumns;
		}
		
		public void updateBounds( double x, double y ){
			applyPersistentSizes();
			updateBounds( station.getRoot(), x, y, 1.0, 1.0 );
		}

		@Override
		protected void updateBounds( SplitNode node, double x, double y, double width, double height ){
			if( node != null && node.isVisible() ) {
				Column column = columns.get( node );
				if( column != null ) {
					column.updateBounds( x, y, width, height );
				}
				else {
					super.updateBounds( node, x, y, width, height );
				}
			}
		}

		public double validateDivider( double divider, Node node ){
			Column column = getColumn( node );
			if( column == null ) {
				return validateHeadNode( divider, node );
			}
			else {
				return column.validateDivider( divider, node );
			}
		}

		private double validateHeadNode( double divider, Node node ){
			if( side == Side.RIGHT || side == Side.BOTTOM ){
				if( divider < node.getDivider() ){
					// it's always possible to go far to the left/top
					return divider;
				}
			}
			else{
				if( divider > node.getDivider() ){
					// it's always possible to go far to the right/bottom
					return divider;
				}
			}
			
			Column[] left = getColumns( node.getLeft() );
			Column[] right = getColumns( node.getRight() );

			int needLeft = 0;
			int needRight = 0;

			int available;
			
			if( side.getHeaderOrientation() == Orientation.HORIZONTAL ) {
				for( Column c : left ) {
					needLeft += c.getMinimumSize().width;
				}
				for( Column c : right ) {
					needRight += c.getMinimumSize().width;
				}
				available = node.getSize().width;
			}
			else {
				for( Column c : left ) {
					needLeft += c.getMinimumSize().height;
				}
				for( Column c : right ) {
					needRight += c.getMinimumSize().height;
				}
				available = node.getSize().height;
			}

			needLeft += (left.length - 1) * gap();
			needRight += (right.length - 1) * gap();

			int required = needLeft + gap() + needRight;
			if( available <= required ){
				return (needLeft + gap() / 2) / (double)required;
			}
			else{
				double minDivider = (needLeft + gap() / 2) / (double)available;
				double maxDivider = (available - needRight - gap()/2 ) / (double)available;
				
				return Math.min( maxDivider, Math.max( minDivider, divider ) );
			}
		}
		
		public void applyPersistentSizes(){
			applyPersistentSizes( station.getRoot() );
		}
		
		private int applyPersistentSizes( SplitNode node ){
			Column column = columns.get( node );
			if( column != null ){
				column.applyPersistentSizes();
				return column.getPersistentColumn().size;
			}
			
			if( node instanceof Root ){
				return applyPersistentSizes( ((Root)node).getChild() );
			}
			else if( node instanceof Node ){
				int left = applyPersistentSizes( ((Node)node).getLeft() );
				int right = applyPersistentSizes( ((Node)node).getRight() );
				int gap = gap();
				
				((Node)node).setDivider( (left + gap/2) / (double)(left + right + gap));
				return left + gap + right;
			}
			else{
				return 0;
			}
		}
		
		public Column getHeadColumn( SplitNode node ){
			while( node != null ){
				Column column = columns.get( node );
				if( column != null ){
					return column;
				}
				if( node instanceof Node ){
					if( side == Side.RIGHT || side == Side.BOTTOM ){
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

		public PersistentCell getHeadCell( SplitNode node ){
			while( node != null ){
				if( node instanceof Leaf ){
					Dockable dockable = ((Leaf)node).getDockable();
					for( Column column : columns.values() ){
						if( column.cells.get( node ) != null ){
							PersistentCell cell = column.getPersistentColumn().cells.get( dockable );
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
		
		private Column getColumn( SplitNode node ){
			Column column = null;
			while( node != null && column == null ) {
				column = columns.get( node );
				node = node.getParent();
			}
			return column;
		}

		private Column[] getColumns( SplitNode node ){
			List<Column> result = new ArrayList<Column>();
			searchColumns( node, result );
			return result.toArray( new Column[result.size()] );
		}

		private void searchColumns( SplitNode node, List<Column> result ){
			if( node != null ) {
				Column column = columns.get( node );
				if( column != null ) {
					result.add( column );
				}
				else {
					for( int i = 0, n = node.getMaxChildrenCount(); i < n; i++ ) {
						searchColumns( node.getChild( i ), result );
					}
				}
			}
		}
	}

	private class Column extends Base {
		private SplitNode root;
		private Table table;
		private Map<SplitNode, Cell> cells = new HashMap<SplitNode, Cell>();

		public Column( Table table, SplitNode root ){
			this.table = table;
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
					cells.put( leaf, new Cell( leaf, Column.this ) );
				}
			} );
		}

		public PersistentColumn toPersistentColumn(){
			int size;
			int preferred;
			if( side.getHeaderOrientation() == Orientation.HORIZONTAL ){
				size = root.getSize().width;
				preferred = getPreferredSize().width;
			}
			else{
				size = root.getSize().height;
				preferred = getPreferredSize().height;
			}
			return new PersistentColumn( size, preferred, getLeafs() );
		}
		
		public PersistentColumn getPersistentColumn(){
			Map<Dockable, PersistentCell> leafs = getLeafs();
			
			for( PersistentColumn column : table.getPersistentColumns() ){
				if( column.cells.keySet().equals( leafs.keySet() )){
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
					int size;
					int preferred;
					
					if( side.getHeaderOrientation() == Orientation.HORIZONTAL ){
						size = leaf.getSize().height;
						preferred = getPreferredSize( leaf ).height;
					}
					else{
						size = leaf.getSize().width;
						preferred = getPreferredSize( leaf ).width;
					}
					leafs.put( leaf.getDockable(), new PersistentCell( size, preferred ));
				}
			} );
			return leafs;
		}
		
		public void applyPersistentSizes(){
			applyPersistentSizes( root, getPersistentColumn() );
		}
		
		private int applyPersistentSizes( SplitNode node, PersistentColumn column ){
			if( node instanceof Root ){
				return applyPersistentSizes( ((Root)node).getChild(), column );
			}
			else if( node instanceof Node ){
				int left = applyPersistentSizes( ((Node)node).getLeft(), column );
				int right = applyPersistentSizes( ((Node)node).getRight(), column );
				int gap = gap();
				
				((Node)node).setDivider( (left + gap/2) / (double)(left + right + gap));
				return left + gap + right;
			}
			else if( node instanceof Leaf ){
				PersistentCell cell = column.cells.get( ((Leaf)node).getDockable() );
				if( cell != null ){
					return cell.size;
				}
			}
			return 0;
		}
		
		public void updateBounds( double x, double y, double width, double height ){
			int gaps = getGaps();
			int requested = 0;
			
			for( PersistentCell cell : getPersistentColumn().cells.values()){
				requested += cell.size;
			}
			
			if( side.getHeaderOrientation() == Orientation.HORIZONTAL ) {
				double available = height * factorH - gaps * gap();
				available = Math.max( available, 0 );
				if( requested < available ) {
					height = requested / factorH + gaps * gap() / factorH;
				}
			}
			else {
				double available = width * factorW - gaps * gap();
				available = Math.max( available, 0 );
				if( requested < available ) {
					width = requested / factorW + gaps * gap() / factorW;
				}
			}
			updateBounds( root, x, y, width, height );
		}

		public double validateDivider( double divider, Node node ){
			if( divider > node.getDivider() ){
				return divider;
			}
			
			Cell[] left = getCells( node.getLeft() );
			Cell[] right = getCells( node.getRight() );
			
			int needLeft = 0;
			int needRight = 0;
			int available;
			
			if( side.getHeaderOrientation() == Orientation.HORIZONTAL ){
				for( Cell cell : left ){
					needLeft += cell.getMinimumSize().height;
				}
				for( Cell cell : right ){
					needRight += cell.getMinimumSize().height;
				}
				available = node.getSize().height ;
			}
			else{
				for( Cell cell : left ){
					needLeft += cell.getMinimumSize().width;
				}
				for( Cell cell : right ){
					needRight += cell.getMinimumSize().width;
				}
				available = node.getSize().width;
			}
			
			int gap = gap();
			needLeft += (left.length-1) * gap;
			needRight += (right.length-1) * gap;
			
			double minDivider = (needLeft + gap/2) / (double)(available);
			return Math.max( minDivider, divider );
		}

		public Dimension getPreferredSize( SplitNode node ){
			return cells.get( node ).getPreferredSize();
		}
		
		public Dimension getMinimumSize( SplitNode node ){
			return cells.get( node ).getMinimumSize();
		}

		public Dimension getPreferredSize(){
			return getPreferredSize( root );
		}
		
		public Dimension getMinimumSize(){
			return getMinimumSize( root );
		}

		public int getGaps( SplitNode node ){
			return cells.get( node ).getGaps();
		}

		public int getGaps(){
			return getGaps( root );
		}

		public Cell[] getCells( SplitNode node ){
			List<Cell> result = new ArrayList<Cell>();
			searchCells( node, result );
			return result.toArray( new Cell[result.size()] );
		}

		private void searchCells( SplitNode node, List<Cell> result ){
			if( node != null ){
				Cell cell = cells.get( node );
				if( cell != null ){
					result.add( cell );
				}
				else{
					for( int i = 0, n = node.getMaxChildrenCount(); i<n; i++ ){
						searchCells( node.getChild( i ), result );
					}
				}
			}
		}
	}

	private class Cell {
		private SplitNode node;
		private Column column;
		private Dimension preferredSize;
		private Dimension minimumSize;
		
		public Cell( SplitNode node, Column column ){
			this.node = node;
			this.column = column;
		}

		/**
		 * Gets the preferred size of this cell, does not include any gaps
		 * @return the preferred size ignoring gaps
		 */
		public Dimension getPreferredSize(){
			if( preferredSize == null ) {
				if( node instanceof Leaf ) {
					preferredSize = ((Leaf) node).getDisplayer().getComponent().getPreferredSize();
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
					minimumSize = ((Leaf) node).getDisplayer().getComponent().getMinimumSize();
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