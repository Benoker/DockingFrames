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
package bibliothek.gui.dock.station.split;

import java.awt.Rectangle;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.station.span.Span;
import bibliothek.gui.dock.station.span.SpanCallback;
import bibliothek.gui.dock.station.span.SpanMode;
import bibliothek.gui.dock.station.span.SpanUsage;
import bibliothek.gui.dock.station.split.PutInfo.Put;
import bibliothek.gui.dock.themes.StationSpanFactoryValue;
import bibliothek.gui.dock.themes.ThemeManager;

/**
 * This class is responsible for accessing and updating all {@link Span}s of a {@link SplitDockStation}.
 * @author Benjamin Sigg
 */
public class SplitSpanStrategy {
	private static final int LEFT = 0;
	private static final int RIGHT = 1;
	private static final int TOP = 2;
	private static final int BOTTOM = 3;
	
	private SplitDockStation station;
	
	private StationSpanFactoryValue factory;
	
	private PutInfo lastPut;
	
	/**
	 * Creates a new strategy
	 * @param station the owner of this strategy, not <code>null</code>
	 */
	public SplitSpanStrategy( SplitDockStation station ){
		this.station = station;
		factory = new StationSpanFactoryValue( ThemeManager.SPAN_FACTORY + ".split", station ){
			@Override
			protected void changed(){
				reset();
			}
		};
	}
	
	/**
	 * Mutates the {@link Span}s such that <code>info</code> shows up.
	 * @param info the current drop information or <code>null</code>
	 */
	public void setPut( PutInfo info ){
		if( info != null ){
			Put put = info.getPut();
			if( put != Put.LEFT && put != Put.RIGHT && put != Put.TOP && put != Put.BOTTOM ){
				info = null;
			}
		}
		
		if( info != null && info.willHaveNoEffect() ){
			return;
		}
		
		if( info != null && lastPut != null ){
			if( info.getPut() == lastPut.getPut() && info.getNode() == lastPut.getNode()){
				return;
			}
		}
		
		if( lastPut != null ){
			Span[] spans = getSpans( lastPut );
			if( spans != null ){
				spans[ putToPosition( lastPut.getPut() )].mutate( SpanMode.OFF );
			}
		}
		
		lastPut = info;
		
		if( info != null ){
			Put put = info.getPut();
			Span[] spans = getSpans( info );
			if( spans != null ){
				int position = putToPosition( put );
				double divider = info.getDivider();
				int size = 100;
				SplitNode node = info.getNode();
				if( node.getParent() == station.getRoot() ){
					node = node.getParent();
				}
				switch( put ){
					case LEFT:
						size = (int)(node.getSize().width * divider);
						break;
					case RIGHT:
						size = (int)(node.getSize().width * (1-divider));
						break;
					case TOP:
						size = (int)(node.getSize().height * divider);
						break;
					case BOTTOM:
						size = (int)(node.getSize().height * (1-divider));
						break;
				}
				
				spans[ position ].configureSize( SpanMode.OPEN, size );
				spans[ position ].mutate( SpanMode.OPEN );
			}
		}
	}
	
	/**
	 * Tells the index of the {@link Span} at side <code>put</code>.
	 * @param put one of the non-combining puts
	 * @return the index of the span
	 * @throws IllegalArgumentException if <code>put</code> does not describe one
	 * of the sides of a {@link SplitNode}
	 */
	public int putToPosition( Put put ){
		switch( put ){
			case LEFT:
				return LEFT;
			case RIGHT:
				return RIGHT;
			case TOP:
				return TOP;
			case BOTTOM:
				return BOTTOM;
			default:
				throw new IllegalArgumentException( "not a side: " + put );
		}
	}
	
	/**
	 * Gets the {@link Span}s that are used when <code>put</code> is active.
	 * @param put the drag and drop operation which may be active
	 * @return the {@link Span}s that would expand if <code>put</code> is active, can be <code>null</code>
	 */
	public Span[] getSpans( PutInfo put ){
		SplitNode node = put.getNode();
		if( node instanceof Leaf ){
			return ((Leaf)node).getSpans();
		}
		if( node instanceof Root ){
			return ((Root)node).getSpans();
		}
		else if( node.getParent() instanceof Root ){
			return ((Root)node.getParent()).getSpans();
		}
		return null;
	}
	
	/**
	 * Immediately resets all {@link Span}s to have a size of <code>0</code>.
	 */
	public void unsetPut(){
		if( lastPut != null ){
			Span[] spans = getSpans( lastPut );
			lastPut = null;
			if( spans != null ){
				for( int i = 0; i < 4; i++ ){
					spans[i].set( SpanMode.OFF );
				}
			}
		}
	}
	
	private void reset(){
		station.getRoot().visit( new SplitNodeVisitor(){
			public void handleRoot( Root root ){
				root.createSpans();
			}
			
			public void handlePlaceholder( Placeholder placeholder ){
				// ignore
			}
			
			public void handleNode( Node node ){
				// ignore
			}
			
			public void handleLeaf( Leaf leaf ){
				leaf.createSpans();
			}
		} );
	}
	
	/**
	 * Gets the factory which is responsible for creating new {@link Span}s.
	 * @return the factory
	 */
	public StationSpanFactoryValue getFactory(){
		return factory;
	}
	
	/**
	 * Sets the {@link DockController} which is used by the {@link SplitDockStation}.
	 * @param controller the controller in whose realm this strategy works
	 */
	public void setController( DockController controller ){
		factory.setController( controller );
	}
	
	/**
	 * Creates four {@link Span}s, one for each side of <code>leaf</code>.
	 * @param node the node which requires {@link Span}s
	 * @return the new set of {@link Span}s or <code>null</code>
	 */
	public Span[] createSpans( final SpanSplitNode node ){
		if( factory.get() == null ){
			return null;
		}
		Span[] result = new Span[4];
		for( int i = 0; i < 4; i++ ){
			result[i] = createSpan( i, node );
		}
		return result;
	}
	
	private Span createSpan( final int position, final SpanSplitNode node ){
		return factory.get().create( new SpanCallback(){
			public void resized(){
				node.onSpanResize();
			}
			
			public boolean isVertical(){
				return position == TOP || position == BOTTOM;
			}
			
			public boolean isHorizontal(){
				return position == LEFT || position == RIGHT;
			}
			
			public DockStation getStation(){
				return station;
			}
			
			public SpanUsage getUsage(){
				return SpanUsage.INSERTING;
			}
		} );
	}
	
	/**
	 * Creates a new {@link Rectangle} within <code>bounds</code>, using
	 * <code>spans</code> to create some insets.
	 * @param bounds the boundaries to shrink
	 * @param node the node to evaluate
	 * @return the smaller boundaries
	 */
	public Rectangle modifyBounds( Rectangle bounds, SpanSplitNode node ){
		Span[] spans = node.getSpans();
		
		if( spans == null ){
			return bounds;
		}
		
		Rectangle copy = new Rectangle( bounds );
		int left = spans[LEFT].getSize();
		int right = spans[RIGHT].getSize();
		int top = spans[TOP].getSize();
		int bottom = spans[BOTTOM].getSize();
		
		copy.x += left;
		copy.width -= left + right;
		copy.y += top;
		copy.height -= top + bottom;
		
		return copy;
	}
}
