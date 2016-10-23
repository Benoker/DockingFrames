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

package bibliothek.gui.dock.station.toolbar.group;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.Orientation;
import bibliothek.gui.dock.ToolbarGroupDockStation;
import bibliothek.gui.dock.station.span.Span;
import bibliothek.gui.dock.station.span.SpanCallback;
import bibliothek.gui.dock.station.span.SpanFactory;
import bibliothek.gui.dock.station.span.SpanMode;
import bibliothek.gui.dock.station.span.SpanUsage;
import bibliothek.gui.dock.station.support.PlaceholderListItem;
import bibliothek.gui.dock.station.toolbar.layout.DockablePlaceholderToolbarGrid;
import bibliothek.gui.dock.themes.StationSpanFactoryValue;
import bibliothek.gui.dock.themes.ThemeManager;

/**
 * This class keeps track of the currently used {@link Span}s of a {@link ToolbarGroupDockStation}. To
 * be exact: the class offers a {@link Span} for each gap between columns or lines, and is able to update
 * the size of the spans when knowing the current drag and drop operation.
 * @author Benjamin Sigg
 */
public abstract class ToolbarGroupSpanStrategy<P extends PlaceholderListItem<Dockable>> {
	/** all columns and cells that are currently available */
	private DockablePlaceholderToolbarGrid<P> grid;
	
	/** the size of the gaps between columns */
	private Span[] columnSpans = new Span[]{};
	
	/** the size of the gaps between lines */
	private Span[][] lineSpans = new Span[][]{};
	
	/** the factory responsible for creating new {@link Span}s */
	private StationSpanFactoryValue factory;

	/** the station using this strategy */
	private ToolbarGroupDockStation station;
	
	/** tells the minimum size for each gap */
	private ToolbarGroupDividerStrategy dividers = ToolbarGroupDividerStrategy.NULL;
	
	private int currentColumn = -1;
	private int currentLine = -1;
	
	/**
	 * Creates a new strategy
	 * @param grid all the columns and theirs cells
	 * @param station the station that is using this strategy
	 */
	public ToolbarGroupSpanStrategy( DockablePlaceholderToolbarGrid<P> grid, ToolbarGroupDockStation station ){
		this.grid = grid;
		this.station = station;
		factory = new StationSpanFactoryValue( ThemeManager.SPAN_FACTORY + ".toolbar.group", station ){
			@Override
			protected void changed(){
				reset( true );
			}
		};
	}
	
	/**
	 * Sets the strategy for painting between the {@link Dockable}s. The strategy is required
	 * to find the minimum size of all gaps.
	 * @param dividers the new strategy, can be <code>null</code>
	 */
	public void setDividers( ToolbarGroupDividerStrategy dividers ){
		if( dividers == null ){
			dividers = ToolbarGroupDividerStrategy.NULL;
		}
		this.dividers = dividers;
		handleResized();
	}
	
	/**
	 * Called if the size of a {@link Span} changed and the station must be resized.
	 */
	protected abstract void handleResized();
	
	/**
	 * Sets the {@link DockController} in whose realm this strategy works, allows to access
	 * the current {@link SpanFactory}.
	 * @param controller the new controller, can be <code>null</code>
	 */
	public void setController( DockController controller ){
		factory.setController( controller );
	}
	
	/**
	 * Tests which columns and cells currently exist, and may create or delete {@link Span}
	 * if necessary.
	 * @param force if <code>true</code>, then the {@link Span}s get recreated even if the number of
	 * columns and lines did not change
	 */
	public void reset( boolean force ){
		if( force || isOutdated() ){
			columnSpans = new Span[ grid.getColumnCount()+1 ];
			for( int i = 0; i < columnSpans.length; i++ ){
				columnSpans[i] = createSpan( true );
			}
			lineSpans = new Span[ grid.getColumnCount() ][];
			for( int i = 0; i < lineSpans.length; i++ ){
				lineSpans[i] = new Span[ grid.getLineCount( i )+1 ];
				for( int j = 0; j < lineSpans[i].length; j++ ){
					lineSpans[i][j] = createSpan( false );
				}
			}
			currentColumn = -1;
			currentLine = -1;
		}
	}
	
	private boolean isOutdated(){
		if( columnSpans.length != grid.getColumnCount()+1 ){
			return true;
		}
		for( int i = 0; i < lineSpans.length; i++ ){
			if( lineSpans[i].length != grid.getLineCount( i ) + 1 ){
				return true;
			}
		}
		return false;
	}
	
	private Span createSpan( final boolean column ){
		return factory.create( new SpanCallback(){
			@Override
			public void resized(){
				handleResized();
			}
			
			@Override
			public boolean isVertical(){
				return (station.getOrientation() == Orientation.HORIZONTAL) ^ column;
			}
			
			@Override
			public boolean isHorizontal(){
				return (station.getOrientation() == Orientation.VERTICAL) ^ column;
			}
			
			@Override
			public SpanUsage getUsage(){
				return SpanUsage.INSERTING;
			}
			
			@Override
			public DockStation getStation(){
				return station;
			}
		});
	}
	
	/**
	 * Gets the size of the gap left of the column <code>index</code>. If there are <code>n</code>
	 * columns, <code>index=n</code> will return the size of the right most gap.
	 * @param index the index of a column
	 * @return the size of the gap
	 */
	public int getColumn( int index ){
		return Math.max( columnSpans[ index ].getSize(), dividers.getColumn( index ));
	}
	
	/**
	 * Gets the size of the gap top of the cell <code>index</code> of <code>column</code>. If there
	 * are <code>n</code> cells, then <code>index=n</code> will return the size of the bottom most gap.
	 * @param column the column in which to search
	 * @param index the index of the cell
	 * @return the size of the gap
	 */
	public int getLine( int column, int index ){
		return Math.max( lineSpans[ column ][ index ].getSize(), dividers.getLine( column, index ));
	}
	
	/**
	 * Mutates the {@link Span}s to present an insertion into column <code>column</code>.
	 * @param column the column where an item is inserted
	 */
	public void mutate( int column ){
		mutate( column, -1 );
	}
	
	/**
	 * Mutates the {@link Span}s to present an insertion into <code>column</code> at <code>line</code>.
	 * @param column the column where the insertion is happening 
	 * @param line the line into which an item is inserted
	 */
	public void mutate( int column, int line ){
		if( currentColumn != column || currentLine != line ){
			currentColumn = column;
			currentLine = line;
			
			if( line == -1 ){
				for( int i = 0; i < columnSpans.length; i++ ){
					if( i == column ){
						columnSpans[ i ].mutate( SpanMode.OPEN );
					}
					else{
						columnSpans[ i ].mutate( SpanMode.OFF );
					}
				}
				for( Span[] spans : lineSpans ){
					for( Span span : spans ){
						span.mutate( SpanMode.OFF );
					}
				}		
			}
			else{
				for( Span span : columnSpans ){
					span.mutate( SpanMode.OFF );
				}

				for( int i = 0; i < lineSpans.length; i++ ){
					for( int j = 0; j < lineSpans[i].length; j++ ){
						if( i == column && j == line ){
							lineSpans[i][j].mutate( SpanMode.OPEN );
						}
						else{
							lineSpans[i][j].mutate( SpanMode.OFF );
						}
					}
				}
			}
		}
	}
}
