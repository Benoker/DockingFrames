/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2012 Hervé Guillaume, Benjamin Sigg
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
 * Hervé Guillaume
 * rvguillaume@hotmail.com
 * FR - France
 *
 * Benjamin Sigg
 * benjamin_sigg@gmx.ch
 * CH - Switzerland
 */

package bibliothek.gui.dock.station.toolbar;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.LayoutManager2;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.Orientation;
import bibliothek.gui.dock.ToolbarDockStation;
import bibliothek.gui.dock.station.span.Span;
import bibliothek.gui.dock.station.span.SpanCallback;
import bibliothek.gui.dock.station.span.SpanMode;
import bibliothek.gui.dock.station.span.SpanUsage;
import bibliothek.gui.dock.themes.StationSpanFactoryValue;
import bibliothek.gui.dock.themes.ThemeManager;

/**
 * {@link LayoutManager} used by the {@link ToolbarDockStation}.
 * @author Benjamin Sigg
 */
public abstract class SpanToolbarLayoutManager implements LayoutManager2{
	private ToolbarDockStation station;
	private Container parent;
	private Span[] spans = new Span[]{};
	private StationSpanFactoryValue factory;
	private int size;
	private int index = -1;
	
	public SpanToolbarLayoutManager( ToolbarDockStation station, Container parent ){
		this.parent = parent;
		this.station = station;
		factory = new StationSpanFactoryValue( ThemeManager.SPAN_FACTORY + ".toolbar", station ){
			@Override
			protected void changed(){
				reset();	
			}
		};
	}
	
	public void setController( DockController controller ){
		factory.setController( controller );
	}
	
	/**
	 * Calculates where to insert an item assuming the mouse is at position <code>x/y</code>.
	 * @param x the x coordinate of the mouse
	 * @param y the y coordinate of the mouse
	 * @return the preferred position to insert a new {@link Dockable}
	 */
	public int getInsertionIndex( int x, int y ){
		if( station.getDockableCount() == 0 ){
			return 0;
		}
		int[] positions = new int[parent.getComponentCount()];
		int location;
		if( station.getOrientation() == Orientation.HORIZONTAL ){
			location = x;
			for( int i = 0; i < positions.length; i++ ){
				Component child = parent.getComponent( i );
				positions[i] = child.getX() + child.getWidth()/2;
			}
		}
		else{
			location = y;
			for( int i = 0; i < positions.length; i++ ){
				Component child = parent.getComponent( i );
				positions[i] = child.getY() + child.getHeight()/2;
			}
		}
		
		for( int i = 0; i < positions.length; i++ ){
			if( location <= positions[i] ){
				return i;
			}
		}
		return positions.length;
	}
	
	public void setSpanSize( Dockable moved ){
		Dimension dim = moved.getComponent().getPreferredSize();
		int size;
		if( station.getOrientation() == Orientation.HORIZONTAL ){
			size = dim.width;
		}
		else{
			size = dim.height;
		}
		if( this.size != size ){
			this.size = size;
			for( Span span : spans ){
				span.configureSize( SpanMode.OPEN, size );
			}
		}
	}
	
	public void setExpandedSpan( int index, boolean mutate ){
		if( this.index != index ){
			this.index = index;
			if( mutate ){
				for( int i = 0; i < spans.length; i++ ){
					if( i == index ){
						spans[i].mutate( SpanMode.OPEN );
					}
					else{
						spans[i].mutate( SpanMode.OFF );
					}
				}
			}
			else{
				for( int i = 0; i < spans.length; i++ ){
					if( i == index ){
						spans[i].set( SpanMode.OPEN );
					}
					else{
						spans[i].set( SpanMode.OFF );
					}
				}
			}
		}
	}
	
	protected abstract void revalidate();
	
	private void reset(){
		index = -1;
		int count = station.getDockableCount();
		if( spans.length != count ){
			SpanCallback callback = new SpanCallback(){
				@Override
				public DockStation getStation(){
					return station;
				}

				@Override
				public boolean isHorizontal(){
					return station.getOrientation() == Orientation.HORIZONTAL;
				}

				@Override
				public boolean isVertical(){
					return station.getOrientation() == Orientation.VERTICAL;
				}

				@Override
				public void resized(){
					revalidate();
				}

				@Override
				public SpanUsage getUsage(){
					return SpanUsage.INSERTING;
				}
				
			};
			spans = new Span[ count ];
			for( int i = 0; i < count; i++ ){
				spans[i] = factory.create( callback );
				spans[i].configureSize( SpanMode.OPEN, size );
			}
		}
	}
	
	@Override
	public void addLayoutComponent( String name, Component comp ){
		reset();
	}

	@Override
	public void removeLayoutComponent( Component comp ){
		reset();
	}

	@Override
	public Dimension preferredLayoutSize( Container parent ){
		if( station.getOrientation() == Orientation.HORIZONTAL ){
			int width = 0;
			int height = 0;
			
			for( int i = 0, n = parent.getComponentCount(); i<n; i++ ){
				Component child = parent.getComponent( i );
				Dimension size = child.getPreferredSize();
				width += size.width;
				height = Math.max( height, size.height );
			}
			
			for( Span span : spans ){
				width += span.getSize();
			}
			
			return new Dimension( width, height );
		}
		else{
			int width = 0;
			int height = 0;
			
			for( int i = 0, n = parent.getComponentCount(); i<n; i++ ){
				Component child = parent.getComponent( i );
				Dimension size = child.getPreferredSize();
				height += size.height;
				width = Math.max( width, size.width );
			}
			
			for( Span span : spans ){
				height += span.getSize();
			}
			
			return new Dimension( width, height );
		}
	}

	@Override
	public Dimension minimumLayoutSize( Container parent ){
		return preferredLayoutSize( parent );
	}

	@Override
	public void layoutContainer( Container parent ){
		if( station.getOrientation() == Orientation.HORIZONTAL ){
			int x = 0;
			int height = parent.getHeight();
			
			for( int i = 0, n = parent.getComponentCount(); i<n; i++ ){
				int span = 0;
				if( i < spans.length ){
					span = spans[i].getSize();
				}
				x += span;
				Component comp = parent.getComponent( i );
				Dimension size = comp.getPreferredSize();
				comp.setBounds( x, 0, size.width, height );
				x += size.width;
			}
		}
		else{
			int y = 0;
			int width = parent.getWidth();

			for( int i = 0, n = parent.getComponentCount(); i<n; i++ ){
				int span = 0;
				if( i < spans.length ){
					span = spans[i].getSize();
				}
				y += span;
				Component comp = parent.getComponent( i );
				Dimension size = comp.getPreferredSize();
				comp.setBounds( 0, y, width, size.height );
				y += size.height;
			}
		}
	}

	@Override
	public void addLayoutComponent( Component comp, Object constraints ){
		reset();
	}

	@Override
	public Dimension maximumLayoutSize( Container target ){
		return preferredLayoutSize( target );
	}

	@Override
	public float getLayoutAlignmentX( Container target ){
		return 0;
	}

	@Override
	public float getLayoutAlignmentY( Container target ){
		return 0;
	}

	@Override
	public void invalidateLayout( Container target ){
		// ihnotr
	}
}
