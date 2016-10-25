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

import java.awt.Dimension;
import java.awt.Insets;

import javax.swing.JComponent;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.SplitDockStation.Orientation;
import bibliothek.gui.dock.station.split.Divideable;
import bibliothek.gui.dock.station.split.Leaf;
import bibliothek.gui.dock.station.split.Node;
import bibliothek.gui.dock.station.split.Root;
import bibliothek.gui.dock.station.split.SplitNode;
import bibliothek.gui.dock.wizard.WizardNodeMap.Cell;
import bibliothek.gui.dock.wizard.WizardNodeMap.Column;
import bibliothek.gui.dock.wizard.WizardSplitDockStation.Side;

/**
 * This class offers an interface to the tree of a {@link SplitDockStation} that handles as if the tree
 * would build a table.
 * @author Benjamin Sigg
 */
public class WizardColumnModel {
	private WizardSplitDockStation station;
	private double factorW;
	private double factorH;
	
	/** Information about columns that needs to persist even when the stations layout changes */
	private PersistentColumn[] persistentColumns;
	
	public WizardColumnModel( WizardSplitDockStation station ){
		this( station, -1, -1 );
	}

	public WizardColumnModel( WizardSplitDockStation station, double factorW, double factorH ){
		this.station = station;
		this.factorH = factorH;
		this.factorW = factorW;
	}
	
	public void setFactors( double factorW, double factorH ){
		this.factorW = factorW;
		this.factorH = factorH;
	}
	
	private Side side(){
		return station.getSide();
	}

	/**
	 * Gets the size of the gap between the column <code>column</code> and
	 * <code>column-1</code> (the left side of <code>column</code>).
	 * @param column the column whose gap on the left side is requested
	 * @return the size of the gap
	 */
	private int gap( int column ){
		return station.getWizardSpanStrategy().getGap( column );
	}
	
	/**
	 * Gets the size of the gap between the cell <code>cell</code> and <code>cell-1</code> 
	 * (the top side of <code>cell</code>).
	 * @param column the column in which the gap is requested
	 * @param cell the cell whose gap on the upper side is requested
	 * @return the size of the gap
	 */
	private int gap( int column, int cell ){
		return station.getWizardSpanStrategy().getGap( column, cell );
	}
	
	/**
	 * Gets the size of the gap that is currently to be used by <code>node</code>
	 * @param node the node whose inner gap is requested
	 * @param map detailed information about the layout of this station
	 * @return the size of the inner gap
	 */
	private int gap( Node node, WizardNodeMap map ){
		return station.getWizardSpanStrategy().getGap( node, map );
	}
	
	private int gap(){
		return station.getDividerSize();
	}

	public Leaf[] getLastLeafOfColumns(){
		return getMap().getLastLeafOfColumns();
	}
	
	public PersistentColumn[] getPersistentColumns(){
		return getMap().getPersistentColumns();
	}
	
	public boolean isHeaderLevel( SplitNode node ){
		return getMap().isHeaderLevel( node );
	}

	public boolean isHeaderLevel( SplitNode node, boolean recursive ){
		return getMap().isHeaderLevel( node, recursive );
	}
	
	/**
	 * Gets a map containing the current columns and cells. This method may decide
	 * at any time to create a new map. Callers may use the map to ask as many queries as they
	 * want, they should however never use more than one map at the same time.
	 * @return the current map of cells and columns
	 */
	protected WizardNodeMap getMap(){
		return new WizardNodeMap( station, persistentColumns ){
			@Override
			protected void handlePersistentColumnsAdapted( PersistentColumn[] persistentColumns ){
				WizardColumnModel.this.persistentColumns = persistentColumns;	
			}
		};
	}
	
	/**
	 * Gets the current preferred size of the entire {@link WizardSplitDockStation}
	 * @return the current preferred size
	 */
	public Dimension getPreferredSize(){
		PersistentColumn[] columns = getMap().getPersistentColumns();
		
		int size = 0;
		int cellMax = 20;
		for( int c = 0; c < columns.length; c++ ){
			PersistentColumn column = columns[c];
			size += column.getSize();
			size += gap( c );
			
			int cellSize = 0;
			int count = 0;
			for( PersistentCell cell : column.getCells().values() ){
				cellSize += cell.getSize();
				cellSize += gap( c, count );
				count++;
			}
			cellSize += gap( c, count );
			cellMax = Math.max( cellMax, cellSize );
		}
		
		size += gap( columns.length );
		if( station.getDockableCount() > 0 ){
			size += station.getSideGap();
		}
		
		Dimension result;
		
		if( side().getHeaderOrientation() == Orientation.HORIZONTAL ){
			result = new Dimension( size, cellMax );
		}
		else{
			result = new Dimension( cellMax, size );
		}
		
		Insets insets = station.getContentPane().getInsets();
		if( insets != null ){
			result.width += insets.left + insets.right;
			result.height += insets.top + insets.bottom;
		}
		
		return result;
	}
	
	/**
	 * Visits all {@link PersistentColumn}s and {@link PersistentCell}s and updates them according
	 * to the values delivered to this method. If the current layout does not match the arguments, then
	 * some cells will simply be ignored. 
	 * @param columnsAndCells the children of the station, sorted into columns and cells. The actual layout on the
	 * station does not have to match this array, the other arguments of the method however must. 
	 * @param cellSizes the size of each cell, this array must have the same dimensions as <code>columnsAndCells</code>
	 * @param columnSizes the size of each column, this array must have the same dimensions as <code>columnsAndCells</code>
	 */
	public void setPersistentColumns( Dockable[][] columnsAndCells, int[][] cellSizes, int[] columnSizes ){
		WizardNodeMap map = getMap();
		PersistentColumn[] persistentColumns = map.getPersistentColumns();
		
		for( int i = 0; i < columnsAndCells.length; i++ ){
			loop:for( int j = 0; j < columnsAndCells[i].length; j++ ){
				Dockable key = columnsAndCells[i][j];
				if( key != null ){
					for( PersistentColumn column : persistentColumns ){
						PersistentCell cell = column.getCells().get( key );
						if( cell != null ){
							cell.setSize( cellSizes[i][j] );
							column.setSize( columnSizes[i] );
							
							continue loop;
						}
					}
				}
			}
		}
		
		applyPersistentSizes( map, true );
	}
	
	/**
	 * Called if the user changed the position of a divider.
	 * @param node the node whose divider changed
	 * @param divider the new divider
	 */
	public void setDivider( Divideable divideable, double divider ){
		WizardNodeMap map = getMap();
		
		if( divideable instanceof Node ){
			Node node = (Node)divideable;
			Column column;
			if( side() == Side.RIGHT || side() == Side.BOTTOM ){
				column = map.getHeadColumn( node.getRight() );
			}
			else{
				column = map.getHeadColumn( node.getLeft() );
			}
			if( column != null ){
				setDivider( map, column, node.getDivider(), divider, node.getSize() );
			}
			else{
				PersistentCell cell = map.getHeadCell( node.getLeft() );
				if( cell != null ){
					double dividerDelta = divider - node.getDivider();
					int deltaPixel;
					if( side().getHeaderOrientation() == Orientation.HORIZONTAL ){
						deltaPixel = (int)(dividerDelta * node.getSize().height);
					}
					else{
						deltaPixel = (int)(dividerDelta * node.getSize().width);
					}
					cell.setSize( cell.getSize() + deltaPixel );
					applyPersistentSizes( map, true );
				}
				else{
					node.setDivider( divider );
				}
			}
		}
		else if( divideable instanceof ColumnDividier ){
			Column column = map.getHeadColumn( station.getRoot() );
			if( column != null ){
				setDivider( map, column, divideable.getDivider(), divider, station.getSize() );
			}
		}
		else if( divideable instanceof CellDivider ){
			PersistentCell cell = map.getHeadCell( ((CellDivider)divideable).getLeaf() );
			if( cell != null ){
				double dividierDelta = divider - divideable.getDivider();
				int deltaPixel = (int)(dividierDelta * cell.getSize());
				cell.setSize( cell.getSize() + deltaPixel );
				applyPersistentSizes( map, true );
			}
		}
	}
	
	private void setDivider( WizardNodeMap map, Column column, double oldDividier, double newDividier, Dimension size ){
		PersistentColumn persistent = column.getPersistentColumn();
		double dividerDelta;
		if( side() == Side.RIGHT || side() == Side.BOTTOM ){
			dividerDelta = oldDividier - newDividier;
		}
		else{
			dividerDelta = newDividier - oldDividier;
		}
		int deltaPixel;
		if( side().getHeaderOrientation() == Orientation.HORIZONTAL ){
			deltaPixel = (int)(dividerDelta * size.width);
		}
		else{
			deltaPixel = (int)(dividerDelta * size.height);
		}
		persistent.setSize( persistent.getSize() + deltaPixel );
		applyPersistentSizes( map, true );
	}
	
	/**
	 * Updates the size of each cell and column such that they met their preferred size.
	 */
	public void resetToPreferredSizes(){
		WizardNodeMap map = getMap();
		for( PersistentColumn column : map.getPersistentColumns() ){
			column.setSize( column.getPreferredSize() );
			for( PersistentCell cell : column.getCells().values() ){
				cell.setSize( cell.getPreferredSize() );
			}
		}
		applyPersistentSizes( map, true );
	}
	
	/**
	 * Updates the size of the <code>index</code>'th column such that it has its preferred size.
	 * @param index the index of the column to update
	 */
	public void resetToPreferredSize( int index ){
		PersistentColumn column = getMap().getPersistentColumn( index );
		column.setSize( column.getPreferredSize() );
	}
	
	/**
	 * Updates the dividers of all {@link Node}s such that the actual size of the columns and cells results. 
	 * @param map information about the layout of the station
	 * @param revalidate if <code>true</code>, a call to {@link JComponent#revalidate()} is made
	 * @return the number of pixels required to show all columns
	 */
	protected int applyPersistentSizes( WizardNodeMap map, boolean revalidate ){
		int result = applyPersistentSizes( station.getRoot(), map );
		if( revalidate ){
			station.revalidateOutside();
		}
		return result;
	}
	
	/**
	 * Updates the dividers of the head of the columns such that the actual size of the columns results. 
	 * @param node a head node
	 * @param map information about the layout of the station
	 * @return the number of pixels required for <code>node</code>
	 */
	private int applyPersistentSizes( SplitNode node, WizardNodeMap map ){
		Column column = map.getColumn( node, false );
		if( column != null ){
			applyPersistentSizes( column.getRoot(), column.getPersistentColumn(), map );
			PersistentColumn persistent = column.getPersistentColumn();
			if( persistent == null ){
				return 0;
			}
			return persistent.getSize();
		}
		
		if( node instanceof Root ){
			return applyPersistentSizes( ((Root)node).getChild(), map );
		}
		else if( node instanceof Node ){
			int left = applyPersistentSizes( ((Node)node).getLeft(), map );
			int right = applyPersistentSizes( ((Node)node).getRight(), map );
			int gap = gap( (Node)node, map );
			
			((Node)node).setDivider( (left + gap/2) / (double)(left + right + gap));
			return left + gap + right;
		}
		else{
			return 0;
		}
	}
	
	/**
	 * Updates the dividers of an node inside of <code>column</code> such that the actual size of the cells results.
	 * @param node a node inside <code>column</code>
	 * @param column detailed information about the current column
	 * @param map detailed information about the layout
	 * @return the number of pixels required for <code>node</code>
	 */
	private int applyPersistentSizes( SplitNode node, PersistentColumn column, WizardNodeMap map ){
		if( node instanceof Root ){
			return applyPersistentSizes( ((Root)node).getChild(), column, map );
		}
		else if( node instanceof Node ){
			Node n = (Node)node;
			
			int left = applyPersistentSizes( n.getLeft(), column, map );
			int right = applyPersistentSizes( n.getRight(), column, map );
			
			if( n.getLeft() == null || !n.getLeft().isVisible() ){
				return right;
			}
			if( n.getRight() == null || !n.getRight().isVisible() ){
				return left;
			}
			
			int gap = gap((Node)node, map );
			((Node)node).setDivider( (left + gap/2) / (double)(left + right + gap));
			return left + gap + right;
		}
		else if( node instanceof Leaf ){
			PersistentCell cell = column.getCells().get( ((Leaf)node).getDockable() );
			if( cell != null ){
				return cell.getSize();
			}
		}
		return 0;
	}

	/**
	 * Updates the boundaries of all {@link SplitNode}s.
	 * @param x the top left corner
	 * @param y the top left corner
	 */
	public void updateBounds( double x, double y ){
		double w = 1.0;
		double h = 1.0;
		int gap0 = gap( 0 );
		WizardNodeMap map = getMap();
		int columns = map.getColumns().size();
		
		if( side().getHeaderOrientation() == Orientation.HORIZONTAL ){
			x += gap0 / factorW;
			w -= gap0 / factorW;
			if( columns > 0 ){
				w -= gap( columns ) / factorW;
			}
		}
		else{
			y += gap0 / factorH;
			h -= gap0 / factorH;
			if( columns > 0 ){
				h -= gap( columns ) / factorH;
			}
		}
		
		int sideGap = station.getSideGap();
		switch( station.getSide() ){
			case RIGHT:
				x += sideGap / factorW;
			case LEFT:
				w -= sideGap / factorW;
				break;
			case BOTTOM:
				y += sideGap / factorH;
			case TOP:
				h -= sideGap / factorH;
				break;
		}
		
		Root root = station.getRoot();
		root.updateBounds( x, y, w, h, factorW, factorH, false );
		int pixels = applyPersistentSizes( map, false );
		
		if( station.getSide().getHeaderOrientation() == Orientation.HORIZONTAL ){
			w = pixels / factorW;
		}
		else{
			h = pixels / factorH;
		}
		
		updateBounds( station.getRoot(), x, y, w, h, map );
	}
	
	/**
	 * Updates the boundaries of <code>node</code> and all its children. This method forwards the call
	 * to either {@link #updateBounds(SplitNode, double, double, double, double)} or 
	 * {@link #updateBounds(double, double, double, double, Column)} depending on the existence of a 
	 * {@link Column} for <code>node</code> in <code>map</code>.
	 * @param node the node whose boundaries are to be updated
	 * @param x the minimum x coordinate
	 * @param y the minimum y coordinate
	 * @param width the maximum width
	 * @param height the maximum height
	 * @param map more information about the current layout.
	 */
	protected void updateBounds( SplitNode node, double x, double y, double width, double height, WizardNodeMap map ){
		if( node != null && node.isVisible() ) {
			Column column = map.getColumn( node, false );
			if( column != null ) {
				updateBounds( x, y, width, height, column, map );
			}
			else {
				updateBoundsRecursive( node, x, y, width, height, map );
			}
		}
	}
	
	/**
	 * Update the boundaries of the column <code>column</code> and all its children.
	 * @param x the minimum x coordinate
	 * @param y the minimum y coordinate
	 * @param width the maximum width
	 * @param height the maximum height
	 * @param column the column whose boundaries are to be updated
	 * @param map information about the current layout
	 */
	protected void updateBounds( double x, double y, double width, double height, Column column, WizardNodeMap map ){
		int requested = 0;
		int count = 0;
		int gaps = 0;
		
		for( PersistentCell cell : column.getPersistentColumn().getCells().values()){
			requested += cell.getSize();
			gaps += gap( column.getIndex(), count );
			count++;
		}
		gaps += gap( column.getIndex(), count );
		int gap0 = gap( column.getIndex(), 0 );
		int cellCount = column.getCellCount();
		
		if( side().getHeaderOrientation() == Orientation.HORIZONTAL ) {
			double available = height * factorH - gaps;
			available = Math.max( available, 0 );
			if( requested < available ) {
				height = requested / factorH + gaps / factorH;
			}
			y += gap0 / factorH;
			height -= gap0 / factorH;
			if( cellCount > 0 ){
				height -= gap( column.getIndex(), cellCount ) / factorH;
			}
		}
		else {
			double available = width * factorW - gaps;
			available = Math.max( available, 0 );
			if( requested < available ) {
				width = requested / factorW + gaps / factorW;
			}
			x += gap0 / factorW;
			width -= gap0 / factorW;
			if( cellCount > 0 ){
				width -= gap( column.getIndex(), cellCount ) / factorW;
			}
		}
		updateBoundsRecursive( column.getRoot(), x, y, width, height, map );
	}
	
	/**
	 * Updates the boundaries of <code>node</code> and all its children. This method recursively visits all
	 * children of <code>node</code> and forwards the call to {@link #updateBounds(SplitNode, double, double, double, double, WizardNodeMap)}
	 * if a {@link Root} or a {@link Node} is found.
	 * @param node the node whose boundaries are to be update
	 * @param x the minimum x coordinate
	 * @param y the minimum y coordinate
	 * @param width the maximum width
	 * @param height the maximum height
	 * @param map information about the current layout
	 */
	protected void updateBoundsRecursive( SplitNode node, double x, double y, double width, double height, WizardNodeMap map ){
		if( node != null && node.isVisible() ) {
			if( node instanceof Root ) {
				updateBounds( ((Root) node).getChild(), x, y, width, height, map );
			}
			else if( node instanceof Node ) {
				Node n = (Node) node;
				if( n.getLeft() != null && n.getLeft().isVisible() && n.getRight() != null && n.getRight().isVisible() ) {
					if( n.getOrientation() == Orientation.HORIZONTAL ) {
						double dividerWidth = factorW > 0 ? Math.max( 0, gap( n, map ) / factorW ) : 0.0;
						double dividerLocation = width * n.getDivider();

						updateBounds( n.getLeft(), x, y, dividerLocation - dividerWidth / 2, height, map );
						updateBounds( n.getRight(), x + dividerLocation + dividerWidth / 2, y, width - dividerLocation - dividerWidth / 2, height, map );
					}
					else {
						double dividerHeight = factorH > 0 ? Math.max( 0, gap( n, map ) / factorH ) : 0.0;
						double dividerLocation = height * n.getDivider();

						updateBounds( n.getLeft(), x, y, width, dividerLocation - dividerHeight / 2, map );
						updateBounds( n.getRight(), x, y + dividerLocation + dividerHeight / 2, width, height - dividerLocation - dividerHeight / 2, map );
					}
				}
				else {
					updateBounds( n.getLeft(), x, y, width, height, map );
					updateBounds( n.getRight(), x, y, width, height, map );
				}
			}
			node.setBounds( x, y, width, height, factorW, factorH, true );
		}
	}


	/**
	 * Calculates the valid value of <code>divider</code> for <code>node</code>.
	 * @param divider the location of the divider
	 * @param node the node whose divider is changed
	 * @return the valid divider
	 */
	public double validateDivider( double divider, Node node ){
		return validateDivider( divider, node, getMap() );
	}
	
	/**
	 * Calculates the valid value of <code>divider</code> for <code>leaf</code>.
	 * @param divider the location of the divider
	 * @param node the node whose divider is changed
	 * @return the valid divider
	 */
	public double validateDivider( double divider, Leaf leaf ){
		return validateDivider( divider, leaf, getMap() );
	}

	/**
	 * Calculates the valid value of <code>divider</code> for the outermost column
	 * @param divider the location of the divider
	 * @param node the node whose divider is changed
	 * @return the valid divider
	 */
	public double validateColumnDivider( double divider ){
		return validateColumnDivider( divider, getMap() );
	}
	
	/**
	 * Validates <code>divider</code>, makes sure it is within acceptable boundaries.
	 * @param divider the divider to validate
	 * @param node the node which owns the dividier
	 * @param map information about the current layout
	 * @return the validated divider
	 */
	private double validateDivider( double divider, Node node, WizardNodeMap map ){
		Column column = map.getColumn( node, true );
		if( column == null ) {
			return validateHeadNode( divider, node, map );
		}
		else {
			return validateDivider( column, divider, node, map );
		}
	}
	
	
	private double validateDivider( double divider, Leaf leaf, WizardNodeMap map ){
		Column column = map.getColumn( leaf, true );
		if( column != null ) {
			return validateDivider( column, divider, leaf, map );
		}
		return divider;
	}

	private double validateColumnDivider( double divider, WizardNodeMap map ){
		Column outer = map.getOutermostColumn();
		if( outer == null ){
			return divider;
		}
		int min = 0;
		int gap = gap();
		
		int available;
		if( side().getHeaderOrientation() == Orientation.HORIZONTAL ){
			for( Column column : map.getColumns().values() ){
				min += column.getRoot().getSize().width + gap;
			}
			min -= outer.getRoot().getSize().width + gap;
			min += outer.getMinimumSize().width;
			available = station.getWidth() - gap;
		}
		else{
			for( Column column : map.getColumns().values() ){
				min += column.getRoot().getSize().height + gap;
			}
			
			min -= outer.getRoot().getSize().height + gap;
			min += outer.getMinimumSize().height;
			available = station.getHeight() - gap;
		}
		
		if( side() == Side.RIGHT || side() == Side.BOTTOM ){
			double maxDividier = 1.0 - (min + gap()/2) / (double)(available + gap());
			return Math.min( maxDividier, divider );
		}
		else{
			double minDividier = (min + gap()/2) / (double)(available + gap());
			return Math.max( minDividier, divider );
		}
	}

	private double validateHeadNode( double divider, Node node, WizardNodeMap map ){
		if( side() == Side.RIGHT || side() == Side.BOTTOM ){
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
		
		Column head;
		
		if( side() == Side.RIGHT || side() == Side.BOTTOM ){
			head = map.getHeadColumn( node.getRight() );
		}
		else{
			head = map.getHeadColumn( node.getLeft() );
		}
		if( head == null ){
			return divider;
		}
		
		int min;
		int available;
		if( side().getHeaderOrientation() == Orientation.HORIZONTAL ){
			min = head.getMinimumSize().width + gap();
			available = node.getSize().width;
		}
		else{
			min = head.getMinimumSize().height + gap();
			available = node.getSize().height;
		}
		
		if( side() == Side.RIGHT || side() == Side.BOTTOM ){
			double maxDividier = 1.0 - (min + gap()/2) / (double)(available + gap());
			return Math.min( maxDividier, divider );
		}
		else{
			double minDividier = (min + gap()/2) / (double)(available + gap());
			return Math.max( minDividier, divider );	
		}
	}
	
	public double validateDivider( Column column, double divider, Node node, WizardNodeMap map ){
		if( divider > node.getDivider() ){
			return divider;
		}
		
		Cell head = column.getRightmostCell( node.getLeft() );
		if( head == null ){
			return divider;
		}
		
		int min;
		int available;
		if( side().getHeaderOrientation() == Orientation.HORIZONTAL ){
			min = node.getLeft().getSize().height - head.getNode().getSize().height + head.getMinimumSize().height;
			available = node.getSize().height;
		}
		else{
			min = node.getLeft().getSize().width - head.getNode().getSize().width + head.getMinimumSize().width;
			available = node.getSize().width;
		}
		
		double minDividier = (min + gap()/2) / (double)(available + gap());
		return Math.max( minDividier, divider );
	}
	
	public double validateDivider( Column column, double divider, Leaf leaf, WizardNodeMap map ){
		Cell head = column.getRightmostCell( leaf );
		if( head == null ){
			return divider;
		}
		
		int min;
		int available;
		if( side().getHeaderOrientation() == Orientation.HORIZONTAL ){
			min = head.getMinimumSize().height + gap();
			available = leaf.getSize().height;
		}
		else{
			min = head.getMinimumSize().width + gap();
			available = leaf.getSize().width;
		}
		
		double minDividier = (min + gap()/2) / (double)(available + gap());
		return Math.max( minDividier, divider );
	}
}
