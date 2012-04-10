package bibliothek.gui.dock.station.toolbar;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager2;

import javax.swing.JComponent;

import bibliothek.gui.Orientation;
import bibliothek.gui.dock.ToolbarContainerDockStation;
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
	
	public ToolbarContainerLayoutManager( JComponent parent, ToolbarContainerDockStation station ){
		this.parent = parent;
		this.station = station;
		spans = createSpans();
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
		// ignore
	}

	@Override
	public void removeLayoutComponent( Component comp ){
		// ignore
	}
	
	@Override
	public Dimension maximumLayoutSize( Container target ){
		return preferredLayoutSize( target );
	}

	@Override
	public Dimension preferredLayoutSize( Container parent ){
		if( station.getOrientation() == Orientation.HORIZONTAL ){
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
		
	}

	@Override
	public Dimension minimumLayoutSize( Container parent ){
		return preferredLayoutSize( parent );
	}

	@Override
	public void addLayoutComponent( Component comp, Object constraints ){
		// ignore
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
