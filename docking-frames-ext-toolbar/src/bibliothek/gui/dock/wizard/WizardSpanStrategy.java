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

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.SplitDockStation.Orientation;
import bibliothek.gui.dock.event.DockStationAdapter;
import bibliothek.gui.dock.station.span.Span;
import bibliothek.gui.dock.station.span.SpanCallback;
import bibliothek.gui.dock.station.span.SpanFactory;
import bibliothek.gui.dock.station.span.SpanMode;
import bibliothek.gui.dock.station.span.SpanUsage;
import bibliothek.gui.dock.station.split.Node;
import bibliothek.gui.dock.station.split.PutInfo;
import bibliothek.gui.dock.station.split.SplitNode;
import bibliothek.gui.dock.themes.StationSpanFactoryValue;
import bibliothek.gui.dock.themes.ThemeManager;
import bibliothek.gui.dock.wizard.WizardNodeMap.Cell;
import bibliothek.gui.dock.wizard.WizardNodeMap.Column;

/**
 * The {@link WizardSpanStrategy} keeps track of the required {@link Span}s for a {@link WizardSplitDockStation}
 * and adds or removes {@link Span}s when necessary.
 * @author Benjamin Sigg
 */
public class WizardSpanStrategy {
	private WizardSplitDockStation station;
	private Span[][] cellSpans = new Span[0][0];
	private Span[] columnSpans = new Span[0];
	
	private StationSpanFactoryValue factory;
	
	private int selectedColumn = -1;
	private int selectedCell = -1;
	
	public WizardSpanStrategy( WizardSplitDockStation station ){
		this.station = station;
		factory = new StationSpanFactoryValue( ThemeManager.SPAN_FACTORY + ".wizard", station ){
			@Override
			protected void changed(){
				reset();
			}
		};
		station.addDockStationListener( new DockStationAdapter(){
			@Override
			public void dockableAdded( DockStation station, Dockable dockable ){
				checkReset();
			}
			@Override
			public void dockableRemoved( DockStation station, Dockable dockable ){
				checkReset();
			}
			@Override
			public void dockablesRepositioned( DockStation station, Dockable[] dockables ){
				checkReset();
			}
		});
	}
	
	/**
	 * Updates the current {@link SpanFactory} if necessary.
	 * @param controller the realm in which this strategy should work
	 */
	public void setController( DockController controller ){
		factory.setController( controller );
	}
	
	private void checkReset(){
		WizardNodeMap map = station.getWizardSplitLayoutManager().getMap();
		Column[] columns = map.getSortedColumns();
		if( columns.length != columnSpans.length-1 ){
			reset();
		}
		else{
			for( int i = 0; i < columns.length; i++ ){
				if( cellSpans[i].length-1 != columns[i].getCellCount() ){
					reset();
					return;
				}
			}
		}
	}
	
	/**
	 * Deletes and recreates all spans.
	 */
	public void reset(){
		selectedColumn = -1;
		selectedCell = -1;
		
		WizardNodeMap map = station.getWizardSplitLayoutManager().getMap();
		Column[] columns = map.getSortedColumns();
		
		columnSpans = new Span[ columns.length+1 ];
		cellSpans = new Span[ columns.length ][];
		
		Callback columnCallback = new Callback( true );
		Callback cellCallback = new Callback( false );
		
		int gap = station.getDividerSize();
		
		for( int i = 0; i < columnSpans.length; i++ ){
			columnSpans[i] = factory.create( columnCallback );
		}
		for( int i = 1; i < columns.length; i++ ){
			columnSpans[i].configureSize( SpanMode.OFF, gap );
		}
		
		for( int i = 0; i < columns.length; i++ ){
			int cellCount = columns[i].getCellCount();
			cellSpans[i] = new Span[ cellCount+1 ];
			for( int j = 0; j < cellSpans[i].length; j++ ){
				cellSpans[i][j] = factory.create( cellCallback );
			}
			for( int j = 1; j < cellCount; j++ ){
				cellSpans[i][j].configureSize( SpanMode.OFF, gap );
			}
		}
	}
	
	/**
	 * Mutates the {@link Span}s such that <code>info</code> shows up.
	 * @param info the current drop information or <code>null</code>
	 */
	public void setPut( PutInfo info ){
		if( info == null || info.getCombinerTarget() != null ){
			setPut( -1, -1 );
		}
		else if( info.getNode() == null ){
			setPut( 0, -1 );
		}
		else{
			WizardNodeMap map = station.getWizardSplitLayoutManager().getMap();
			SplitNode node = info.getNode();
			node = traverseDown( node );
			Column column = map.getColumn( node );
			
			Dimension size = info.getDockable().getComponent().getPreferredSize();
			
			int width = size.width;
			int height = size.height;
			
			if( station.getSide().getHeaderOrientation() == Orientation.HORIZONTAL ){
				for( Span span : columnSpans ){
					span.configureSize( SpanMode.OPEN, width );
				}
				for( Span[] array : cellSpans ){
					for( Span span : array ){
						span.configureSize( SpanMode.OPEN, height );
					}
				}
				
				if( info.getPut() == PutInfo.Put.LEFT ){
					setPut( column.getIndex(), -1 );
				}
				else if( info.getPut() == PutInfo.Put.RIGHT ){
					setPut( column.getIndex()+1, -1 );
				}
				else if( info.getPut() == PutInfo.Put.TOP ){
					Cell cell = column.getLeftmostCell( node );
					setPut( column.getIndex(), cell.getIndex() );
				}
				else if( info.getPut() == PutInfo.Put.BOTTOM ){
					Cell cell = column.getRightmostCell( node );
					setPut( column.getIndex(), cell.getIndex()+1 );
				}
			}
			else{
				for( Span span : columnSpans ){
					span.configureSize( SpanMode.OPEN, height );
				}
				for( Span[] array : cellSpans ){
					for( Span span : array ){
						span.configureSize( SpanMode.OPEN, width );
					}
				}
				
				if( info.getPut() == PutInfo.Put.LEFT ){
					Cell cell = column.getLeftmostCell( node );
					setPut( column.getIndex(), cell.getIndex() );
				}
				else if( info.getPut() == PutInfo.Put.RIGHT ){
					Cell cell = column.getRightmostCell( node );
					setPut( column.getIndex(), cell.getIndex()+1 );
				}
				else if( info.getPut() == PutInfo.Put.TOP ){
					setPut( column.getIndex(), -1 );			
				}
				else if( info.getPut() == PutInfo.Put.BOTTOM ){
					setPut( column.getIndex()+1, -1 );
				}	
			}
			
		}
	}
	
	private SplitNode traverseDown( SplitNode node ){
		while( node instanceof Node ){
			Node n = (Node)node;
			
			boolean left = n.getLeft().isVisible();
			boolean right = n.getRight().isVisible();
			
			if( !left && right ){
				node = n.getRight();
			}
			else if( left && !right ){
				node = n.getLeft();
			}
			else{
				break;
			}
		}
		return node;
	}
	
	private void setPut( int column, int cell ){
		selectedColumn = column;
		selectedCell = cell;
		
		for( int i = 0; i < columnSpans.length; i++ ){
			if( i == column && cell == -1 ){
				columnSpans[i].mutate( SpanMode.OPEN );
			}
			else{
				columnSpans[i].mutate( SpanMode.OFF );
			}
		}
		for( int i = 0; i < cellSpans.length; i++ ){
			for( int j = 0; j < cellSpans[i].length; j++ ){
				if( i == column && j == cell ){
					cellSpans[i][j].mutate( SpanMode.OPEN );
				}
				else{
					cellSpans[i][j].mutate( SpanMode.OFF );
				}
			}
		}
	}
	
	/**
	 * Immediately resets all {@link Span}s to have a size of <code>0</code>.
	 */
	public void unsetPut(){
		selectedColumn = -1;
		selectedCell = -1;
		
		for( Span span : columnSpans ){
			span.set( SpanMode.OFF );
		}
		for( Span[] array : cellSpans ){
			for( Span span : array ){
				span.set( SpanMode.OFF );
			}
		}
	}
	
	/**
	 * Gets the size of the currently selected {@link Span} according to
	 * {@link #setPut(PutInfo)}. If there is no {@link Span} selected, then
	 * this method returns the standard size of a gap.
	 * @return the size of the currently selected gap
	 */
	public int getGap(){
		if( selectedColumn == -1 ){
			return station.getDividerSize();
		}
		if( selectedCell == -1 ){
			if( selectedColumn >= columnSpans.length ){
				return station.getDividerSize();
			}
			else{
				return getSize( columnSpans[selectedColumn] );
			}
		}
		if( selectedColumn >= cellSpans.length ){
			return station.getDividerSize();
		}
		Span[] array = cellSpans[selectedColumn];
		if( selectedCell >= array.length ){
			return station.getDividerSize();
		}
		return getSize( array[selectedCell] );
	}
	
	/**
	 * Gets the size of the gap left of <code>column</code>.
	 * @param column the column whose gap to its predecessor is requested
	 * @return the size of the gap
	 */
	public int getGap( int column ){
		if( column >= columnSpans.length ){
			return 0;
		}
		return getSize( columnSpans[column] );
	}
	
	/**
	 * Gets the size of the gap between <code>cell</code> and its predecessor. 
	 * @param column the column in which to search
	 * @param cell the cell to search
	 * @return the gap before <code>cell</code>
	 */
	public int getGap( int column, int cell ){
		if( column >= cellSpans.length ){
			return 0;
		}
		if( cell >= cellSpans[column].length ){
			return 0;
		}
		return getSize( cellSpans[column][cell] );
	}
	
	/**
	 * Gets the current size of <code>span</code>. May be overridden by subclasses to influence the
	 * size of a span.
	 * @param span the size of <code>span</code>
	 * @return the size of the span
	 */
	protected int getSize( Span span ){
		return span.getSize();
	}
	
	/**
	 * Gets the size of the gap that is currently to be used by <code>node</code>
	 * @param node the node whose inner gap is requested
	 * @param map detailed information about the layout of this station
	 * @return the size of the inner gap
	 */
	public int getGap( Node node, WizardNodeMap map ){
		Column column = map.getColumn( node );
		if( column == null ){
			return 0;
		}
		
		SplitNode root = column.getRoot().getParent();
		while( root != null ){
			if( root == node ){
				return getGap( column.getIndex() );
			}
			root = root.getParent();
		}
		
		Cell cell = column.getLeftmostCell( node.getRight() );
		if( cell == null ){
			return getGap( column.getIndex() );
		}
		else{
			return getGap( column.getIndex(), cell.getIndex() );
		}
	}
	
	/**
	 * Callback for a set of {@link Span}s used by a {@link WizardSpanStrategy}.
	 * @author Benjamin Sigg
	 */
	private class Callback implements SpanCallback{
		private boolean column;
		
		public Callback( boolean column ){
			this.column = column;
		}

		@Override
		public DockStation getStation(){
			return station;
		}

		@Override
		public boolean isHorizontal(){
			if( column ){
				return station.getSide().getHeaderOrientation() == Orientation.HORIZONTAL;
			}
			else{
				return station.getSide().getColumnOrientation() == Orientation.HORIZONTAL;
			}
		}

		@Override
		public boolean isVertical(){
			return !isHorizontal();
		}

		@Override
		public void resized(){
			station.revalidateOutside();
		}

		@Override
		public SpanUsage getUsage(){
			return SpanUsage.INSERTING;
		}
	}
}
