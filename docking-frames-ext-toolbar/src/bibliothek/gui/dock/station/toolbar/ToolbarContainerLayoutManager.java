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

package bibliothek.gui.dock.station.toolbar;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager2;

import javax.swing.JComponent;

import bibliothek.gui.DockController;
import bibliothek.gui.Orientation;
import bibliothek.gui.Position;
import bibliothek.gui.dock.ToolbarContainerDockStation;
import bibliothek.gui.dock.station.span.Span;
import bibliothek.gui.dock.station.span.SpanFactory;
import bibliothek.gui.dock.station.support.ListSpanStrategy;
import bibliothek.gui.dock.themes.ThemeManager;

/**
 * The {@link LayoutManager2} used by a {@link ToolbarContainerDockStation}, this {@link LayoutManager2}
 * uses the current {@link SpanFactory} to add gaps between {@link Component}s if necessary.
 * @author Benjamin Sigg
 */
public class ToolbarContainerLayoutManager implements LayoutManager2{
	private JComponent parent;
	private ToolbarContainerDockStation station;
	private ListSpanStrategy spans;
	
	/**
	 * Creates a new layout manager
	 * @param parent the panel using this layout manager
	 * @param station the station showing <code>panel</code>
	 */
	public ToolbarContainerLayoutManager( JComponent parent, ToolbarContainerDockStation station ){
		this.parent = parent;
		this.station = station;
		spans = createSpans();
	}
	
	/**
	 * Sets the {@link DockController} in whose realm this layout manager is used.
	 * @param controller the controller, can be <code>null</code>
	 */
	public void setController( DockController controller ){
		spans.setController( controller );
	}
	
	/**
	 * Tells this layout manager which {@link Span}s have to mutate.
	 * @param info information about the item that is currently dropped
	 */
	public void setDrawing( ToolbarContainerDropInfo info ){
		if( info == null ){
			spans.untease();
		}
		else{
			int index = info.getIndex();
			Position position = info.getSideDockableBeneathMouse();
			if( position == Position.SOUTH || position == Position.EAST ){
				index++;
			}
			spans.tease( index );
			if( station.getOrientation() == Orientation.HORIZONTAL ){
				spans.size( index, info.getItem().getComponent().getWidth() );
			}
			else{
				spans.size( index, info.getItem().getComponent().getHeight() );
			}
		}
	}
	
	private ListSpanStrategy createSpans(){
		return new ListSpanStrategy( ThemeManager.SPAN_FACTORY + ".toolbar.container", station ){
			@Override
			protected void spanResized(){
				parent.revalidate();
			}
			
			@Override
			protected boolean isHorizontal(){
				return station.getOrientation() == Orientation.HORIZONTAL;
			}
			
			@Override
			protected int getNumberOfDockables(){
				return station.getDockableCount();
			}
		};
	}
	
	@Override
	public void addLayoutComponent( String name, Component comp ){
		spans.reset();
	}

	@Override
	public void removeLayoutComponent( Component comp ){
		spans.reset();
	}
	
	@Override
	public Dimension maximumLayoutSize( Container target ){
		return preferredLayoutSize( target );
	}

	@Override
	public Dimension preferredLayoutSize( Container parent ){
		if( station.getOrientation() == Orientation.VERTICAL ){
			int width = spans.getTeasing();
			int height = 0;
			for( int i = 0, n = parent.getComponentCount(); i<n; i++ ){
				Dimension size = parent.getComponent( i ).getPreferredSize();
				width = Math.max( size.width, width );
				height += size.height;
				height += spans.getGap( i );
			}
			height += spans.getGap( parent.getComponentCount() );
			return new Dimension( width, height );
		}
		else{
			int width = 0;
			int height = spans.getTeasing();
			for( int i = 0, n = parent.getComponentCount(); i<n; i++ ){
				Dimension size = parent.getComponent( i ).getPreferredSize();
				height = Math.max( size.height, height );
				width += size.width;
				width += spans.getGap( i );
			}
			width += spans.getGap( parent.getComponentCount() );
			return new Dimension( width, height );
		}
	}
	
	@Override
	public void layoutContainer( Container parent ){
		Dimension preferred = preferredLayoutSize( parent );
		int gaps = 0;
		for( int i = 0, n = parent.getComponentCount(); i <= n; i++ ){
			gaps += spans.getGap( i );
		}
		if( station.getOrientation() == Orientation.HORIZONTAL ){
			if( preferred.width > gaps ){
				float factor = parent.getWidth() / (float)(preferred.width - gaps);
				if( factor > 1 ){
					factor = 1;
				}
				else if( factor < 0 ){
					factor = 0;
				}
				int x = 0;
				int height = parent.getHeight();
				for( int i = 0, n = parent.getComponentCount(); i<n; i++ ){
					x += spans.getGap( i );
					Component child = parent.getComponent( i );
					Dimension size = child.getPreferredSize();
					int width = (int)(size.width * factor);
					child.setBounds( x, 0, width, Math.min( height, size.height ) );
					x += width;
				}
			}
		}
		else{
			if( preferred.height > gaps ){
				float factor = parent.getHeight() / (float)(preferred.height - gaps);
				if( factor > 1 ){
					factor = 1;
				}
				else if( factor < 0 ){
					factor = 0;
				}
				int y = 0;
				int width = parent.getWidth();
				for( int i = 0, n = parent.getComponentCount(); i<n; i++ ){
					y += spans.getGap( i );
					Component child = parent.getComponent( i );
					Dimension size = child.getPreferredSize();
					int height = (int)(size.height * factor);
					child.setBounds( 0, y, Math.min( width, size.width ), height );
					y += height;
				}
			}
		}
	}

	@Override
	public Dimension minimumLayoutSize( Container parent ){
		return preferredLayoutSize( parent );
	}

	@Override
	public void addLayoutComponent( Component comp, Object constraints ){
		spans.reset();
	}

	@Override
	public float getLayoutAlignmentX( Container target ){
		return 0.5f;
	}

	@Override
	public float getLayoutAlignmentY( Container target ){
		return 0.5f;
	}

	@Override
	public void invalidateLayout( Container target ){
		// ignore
	}
}
