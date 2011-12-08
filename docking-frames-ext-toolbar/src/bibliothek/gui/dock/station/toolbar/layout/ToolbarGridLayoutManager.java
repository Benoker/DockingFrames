package bibliothek.gui.dock.station.toolbar.layout;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.LayoutManager2;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import bibliothek.gui.Dockable;
import bibliothek.gui.Orientation;
import bibliothek.gui.dock.station.support.PlaceholderListItem;

/**
 * This {@link LayoutManager2} orders a set of {@link Component}s in columns. To decide
 * which component belongs to which column, the contents of a {@link DockablePlaceholderToolbarGrid}
 * are used.
 * @author Benjamin Sigg
 */
public abstract class ToolbarGridLayoutManager<P extends PlaceholderListItem<Dockable>> implements LayoutManager2 {
	/** the list defining the layout of the components */
	private DockablePlaceholderToolbarGrid<P> grid;

	/** how to align the components */
	private Orientation orientation;

	private enum Size {
		MAXIMUM, MINIMUM, PREFERRED;

		public Dimension get( Component component ){
			switch( this ){
				case MAXIMUM:
					return component.getMaximumSize();
				case MINIMUM:
					return component.getMinimumSize();
				case PREFERRED:
					return component.getPreferredSize();
				default:
					throw new IllegalStateException();
			}
		}
	}

	/**
	 * Creates a new layout manager.
	 * @param orientation the orientation, must not be <code>null</code>
	 * @param grid the list of items to lay out, must not be <code>null</code>
	 */
	public ToolbarGridLayoutManager( Orientation orientation, DockablePlaceholderToolbarGrid<P> grid ){
		if( orientation == null ) {
			throw new IllegalArgumentException( "orientation must not be null" );
		}
		if( grid == null ) {
			throw new IllegalArgumentException( "grid must not be null" );
		}

		this.orientation = orientation;
		this.grid = grid;
	}

	/**
	 * Converts <code>item</code> into a {@link Component}, this {@link LayoutManager} will
	 * then set the location and size of the resulting component.
	 * @param item the item to convert
	 * @return the {@link Component} whose position and size will be set
	 */
	protected abstract Component toComponent( P item );

	/**
	 * Gets an array of columns, where each column is an array of {@link Component}s.
	 * @return all children sorted into the columns
	 */
	@SuppressWarnings("unchecked")
	protected Wrapper[][] layout(){
		Wrapper[][] components = new ToolbarGridLayoutManager.Wrapper[grid.getColumnCount()][];
		for( int i = 0; i < components.length; i++ ) {
			List<Wrapper> list = new ArrayList<Wrapper>();
			Iterator<P> iter = grid.getColumnContent( i );
			while( iter.hasNext() ) {
				list.add( new Wrapper( toComponent( iter.next() ) ) );
			}
			components[i] = list.toArray( new ToolbarGridLayoutManager.Wrapper[list.size()] );
		}
		return components;
	}

	@Override
	public void addLayoutComponent( String name, Component comp ){
		// nothing to do
	}

	@Override
	public void removeLayoutComponent( Component comp ){
		// nothing to do
	}

	@Override
	public void addLayoutComponent( Component comp, Object constraints ){
		// nothing to do
	}

	@Override
	public Dimension maximumLayoutSize( Container parent ){
		return layoutSize( parent, layout(), Size.MAXIMUM );
	}

	@Override
	public Dimension preferredLayoutSize( Container parent ){
		return layoutSize( parent, layout(), Size.PREFERRED );
	}

	@Override
	public Dimension minimumLayoutSize( Container parent ){
		return layoutSize( parent, layout(), Size.MINIMUM );
	}

	private Dimension layoutSize( Container parent, Wrapper[][] content, Size size ){
		int width = 0;
		int height = 0;

		if( orientation == Orientation.HORIZONTAL ) {
			for( Wrapper[] column : content ) {
				Dimension dim = layoutSize( column, size );
				width = Math.max( width, dim.width );
				height += dim.height;
			}
		}
		else {
			for( Wrapper[] column : content ) {
				Dimension dim = layoutSize( column, size );
				height = Math.max( height, dim.height );
				width += dim.width;
			}
		}

		Insets insets = parent.getInsets();
		Dimension result = new Dimension( width, height );
		if( insets != null ){
			result.width += insets.left + insets.right;
			result.height += insets.top + insets.bottom;
		}
		return result;
	}

	private Dimension layoutSize( Wrapper[] column, Size size ){
		int width = 0;
		int height = 0;

		if( orientation == Orientation.HORIZONTAL ) {
			for( Wrapper item : column ) {
				item.reset( size );
				Dimension dim = item.required;
				width += dim.width;
				height = Math.max( dim.height, height );
			}
		}
		else {
			for( Wrapper item : column ) {
				item.reset( size );
				Dimension dim = item.required;
				height += dim.height;
				width = Math.max( dim.width, width );
			}
		}

		return new Dimension( width, height );
	}

	@Override
	public void layoutContainer( Container parent ){
		Wrapper[][] components = layout();
		Dimension available = parent.getSize();
		Dimension preferred = layoutSize( parent, components, Size.PREFERRED );
		if( preferred.width <= available.width && preferred.height <= available.height ) {
			layout( parent, components, preferred, available, Size.PREFERRED );
		}
		else {
			layout( parent, components, layoutSize( parent, components, Size.MINIMUM ), available, Size.MINIMUM );
		}
	}

	/**
	 * Layouts <code>components</code> such that they fit into <code>available</code>.
	 * @param parent the {@link Container} whose layout is upated
	 * @param components the components to layout
	 * @param required the size required for the optimal layout
	 * @param available the size that is actually available
	 * @param size which {@link Dimension} to get for layouting the components
	 */
	protected void layout( Container parent, Wrapper[][] components, Dimension required, Dimension available, Size size ){
		if( components.length == 0 || available.width < 1 || available.height < 1 ) {
			return;
		}

		Dimension[] columns = new Dimension[components.length];
		for( int i = 0; i < columns.length; i++ ) {
			columns[i] = layoutSize( components[i], size );
		}

		Insets insets = parent.getInsets();
		
		if( orientation == Orientation.HORIZONTAL ) {
			if( required.height > available.height ) {
				double factor = available.height / (double) required.height;
				int sum = 0;
				for( int i = 0, n = columns.length - 1; i < n; i++ ) {
					columns[i].height = (int) (factor * columns[i].height);
					sum += columns[i].height;
				}
				columns[columns.length - 1].height = available.height - sum;
			}
			int y = 0;
			if( insets != null ){
				y = insets.top;
			}
			for( int i = 0; i < columns.length; i++ ) {
				layout( components[i], columns[i], available, y, size );
				y += columns[i].height;
			}
		}
		else {
			if( required.width > available.width ) {
				double factor = available.width / (double) required.width;
				int sum = 0;
				for( int i = 0, n = columns.length - 1; i < n; i++ ) {
					columns[i].width = (int) (factor * columns[i].width);
					sum += columns[i].width;
				}
				columns[columns.length - 1].width = available.width - sum;
			}

			int x = 0;
			if( insets != null ){
				x = insets.left;
			}
			for( int i = 0; i < columns.length; i++ ) {
				layout( components[i], columns[i], available, x, size );
				x += columns[i].width;
			}
		}
	}

	private void layout( Wrapper[] column, Dimension required, Dimension available, int startPoint, Size size ){
		if( orientation == Orientation.HORIZONTAL ) {
			if( required.width > available.width ) {
				double factor = available.width / (double) required.width;
				int sum = 0;
				for( int i = 0, n = column.length - 1; i < n; i++ ) {
					Dimension dim = column[i].required;
					dim.width = (int) (dim.width * factor);
					sum += dim.width;
				}
				column[column.length - 1].required.width = available.width - sum;
			}

			int x = 0;
			int y = startPoint;

			for( int i = 0; i < column.length; i++ ) {
				column[i].component.setBounds( x, y, column[i].required.width, required.height );
				x += column[i].required.width;
			}
		}
		else {
			if( required.height > available.height ) {
				double factor = available.height / (double) required.height;
				int sum = 0;
				for( int i = 0, n = column.length - 1; i < n; i++ ) {
					Dimension dim = column[i].required;
					dim.height = (int) (dim.height * factor);
					sum += dim.height;
				}
				column[column.length - 1].required.height = available.height - sum;
			}

			int x = startPoint;
			int y = 0;

			for( int i = 0; i < column.length; i++ ) {
				column[i].component.setBounds( x, y, required.width, column[i].required.height );
				y += column[i].required.height;
			}
		}
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
		// nothing to do
	}

	/**
	 * A wrapper around one {@link Component}, caches minimal, maximal or preferred size. 
	 * @author Benjamin Sigg
	 */
	protected class Wrapper {
		public Component component;
		public Dimension required;
		private Size size;

		public Wrapper( Component component ){
			this.component = component;
		}

		/**
		 * Resets the size constraints of this item.
		 * @param size the kind of size that should be used as constraint
		 */
		public void reset( Size size ){
			if( this.size != size ) {
				this.size = size;
				required = new Dimension( size.get( component ) );
			}
		}

	}
}
