/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2012 Benjamin Sigg
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
package bibliothek.gui.dock.station.support;

import java.util.ArrayList;
import java.util.List;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.FlapDockStation;
import bibliothek.gui.dock.station.span.Span;
import bibliothek.gui.dock.station.span.SpanCallback;
import bibliothek.gui.dock.station.span.SpanMode;
import bibliothek.gui.dock.station.span.SpanUsage;
import bibliothek.gui.dock.themes.StationSpanFactoryValue;

/**
 * Manages the {@link Span}s of a {@link DockStation} that orders its {@link Dockable}s like a list.
 * @author Benjamin Sigg
 */
public abstract class ListSpanStrategy {
	/** {@link Span}s shown between the buttons */
	private List<Span> spans = new ArrayList<Span>();
	
	/** {@link Span} used to open the station if it does not have children */
	private Span teaser;
	
	/** how the buttons are currently layed out */
	private boolean horizontal = true;
	
	/** the station using this strategy */
	private DockStation station;
	
	private StationSpanFactoryValue factory;
	
	/**
	 * Creates a new strategy.
	 * @param spanFactoryId the unique identifier used to create a {@link StationSpanFactoryValue}
	 * @param station the station that is using this strategy
	 */
	public ListSpanStrategy( String spanFactoryId, DockStation station ){
		this.station = station;
		factory = new StationSpanFactoryValue( spanFactoryId, station ){
			@Override
			protected void changed(){
				teaser = null;
				spans.clear();
				reset();
			}
		};
	}
	
	/**
	 * Called when the {@link DockController} of the {@link FlapDockStation} changes.
	 * @param controller the new controller, can be <code>null</code>
	 */
	public void setController( DockController controller ){
		factory.setController( controller );
	}

	/**
	 * Tells whether the orientation of the underlying {@link DockStation} currently is horizontal.
	 * A station is horizontal if the {@link Dockable}s are ordered in a list going from the left side
	 * of the station to the right side.
	 * @return whether the station is horizontal or vertical
	 */
	protected abstract boolean isHorizontal();
	
	/**
	 * Gets the number of {@link Dockable}s that are actually shown on the station.
	 * @return the number of {@link Dockable}s
	 */
	protected abstract int getNumberOfDockables();
	
	/**
	 * Called if at least one {@link Span} changed its size
	 */
	protected abstract void spanResized();
	
	/**
	 * To be called if the number of children or the orientation changed.
	 */
	public void reset(){
		boolean orientation = isHorizontal();
		
		if( orientation != horizontal ){
			horizontal = orientation;
			spans.clear();
			teaser = null;
		}
		
		if( teaser == null ){
			teaser = createSpan( !horizontal, SpanUsage.HIDING );
		}
		
		int requested = getNumberOfDockables()+1;
		int actual = spans.size();
		while( requested > actual ){
			spans.add( createSpan( horizontal, SpanUsage.INSERTING ));
			actual++;
		}
		while( requested < actual ){
			spans.remove( --actual );
		}
		
		teaser.set( SpanMode.OFF );
		for( Span button : spans ){
			button.set( SpanMode.OFF );
		}
	}
	
	private Span createSpan( final boolean horizontal, final SpanUsage usage ){
		return factory.create( new SpanCallback(){
			public void resized(){
				spanResized();
			}
			
			public boolean isVertical(){
				return !isHorizontal();
			}
			
			public boolean isHorizontal(){
				return horizontal;
			}
			
			public DockStation getStation(){
				return station;
			}
			
			public SpanUsage getUsage(){
				return usage;
			}
		} );
	}
	
	/**
	 * Opens some {@link Span}s such that a {@link Dockable} could be inserted
	 * at <code>index</code>.
	 * @param index the index of the new {@link Dockable}
	 */
	public void tease( int index ){
		if( teaser != null ){
			teaser.mutate( SpanMode.TEASING );
		}
		for( int i = 0, n = spans.size(); i<n; i++ ){
			if( i == index ){
				spans.get( i ).mutate( SpanMode.OPEN );
			}
			else{
				spans.get( i ).mutate( SpanMode.OFF );
			}
		}
	}
	
	/**
	 * Configures the size of the <code>index</code>'th {@link Span}. Nothing happens if there
	 * is no span at <code>index</code>.
	 * @param index the index of the span
	 * @param size the new size
	 */
	public void size( int index, int size ){
		if( index >= 0 && index < spans.size() ){
			spans.get( index ).configureSize( SpanMode.OPEN, size );
		}
	}
	
	/**
	 * Makes all {@link Span}s invisible.
	 */
	public void untease(){
		if( teaser != null ){
			teaser.mutate( SpanMode.OFF );
		}
		for( Span button : spans ){
			button.mutate( SpanMode.OFF );
		}
	}
	
	/**
	 * Gets the current minimum size of the station.
	 * @return the current minimum size
	 */
	public int getTeasing(){
		if( teaser == null ){
			return 0;
		}
		return teaser.getSize();
	}
	
	/**
	 * Gets the size of the gap between button <code>index-1</code> and
	 * button <code>index</code>.
	 * @param index the index of the gap, where 0 is the most left gap
	 * @return the size of the gap
	 */
	public int getGap( int index ){
		if( index >= spans.size() ){
			return 0;
		}
		return spans.get( index ).getSize();
	}

}
