package bibliothek.gui.dock.station.toolbar.title;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.action.DockActionSource;
import bibliothek.gui.dock.themes.basic.action.BasicTitleViewItem;
import bibliothek.gui.dock.themes.basic.action.buttons.ButtonPanel;
import bibliothek.gui.dock.title.AbstractMultiDockTitle;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.gui.dock.title.DockTitleVersion;

/**
 * This specialized {@link DockTitle} does not show a {@link DockActionSource}, a text or even
 * an {@link Icon}. Instead it shows several {@link DockActionSource}s, all derived from a single {@link ColumnDockActionSource}. 
 * @author Benjamin Sigg
 */
public abstract class ColumnDockTitle extends AbstractMultiDockTitle {
	private List<ButtonPanel> itemPanels = new ArrayList<ButtonPanel>();
	private ColumnDockActionSource source;

	/**
	 * Creates a new title.
	 * @param dockable the element for which this title is used
	 * @param origin a description telling how this title was created
	 */
	public ColumnDockTitle( Dockable dockable, DockTitleVersion origin ){
		init( dockable, origin );
	}

	/**
	 * This listener is added to the current {@link #source} and adds or removes {@link #itemPanels}
	 * when necessary.
	 */
	private ColumnDockActionSourceListener listener = new ColumnDockActionSourceListener(){
		@Override
		public void reshaped( ColumnDockActionSource source ){
			revalidate();
		}

		@Override
		public void removed( ColumnDockActionSource source, DockActionSource item, int index ){
			if( isBound() ) {
				ButtonPanel panel = itemPanels.remove( index );
				panel.set( null );
				panel.setController( null );
				remove( panel );
				revalidate();
			}
		}

		@Override
		public void inserted( ColumnDockActionSource source, DockActionSource item, int index ){
			if( isBound() ) {
				createPanel( item, index );
			}
		}
	};

	private void createPanel( DockActionSource item, int index ){
		ButtonPanel panel = new ButtonPanel( true ){
			@Override
			protected BasicTitleViewItem<JComponent> createItemFor( DockAction action, Dockable dockable ){
				return ColumnDockTitle.this.createItemFor( action, dockable );
			}
		};
		panel.set( getDockable(), item );
		panel.setController( getDockable().getController() );
		panel.setOrientation( getOrientation() );
		panel.setToolTipText( getToolTipText() );
		itemPanels.add( index, panel );
		add( panel );
		revalidate();
	}

	@Override
	public void setOrientation( Orientation orientation ){
		if( getOrientation() != orientation ) {
			super.setOrientation( orientation );
			for( ButtonPanel panel : itemPanels ) {
				panel.setOrientation( orientation );
			}
			revalidate();
		}
	}

	/**
	 * Gets the {@link ColumnDockActionSource} that should be used for finding the actions
	 * of <code>dockable</code>.
	 * @param dockable the element that is represented by this title.
	 * @return the source for <code>dockable</code> or <code>null</code>
	 */
	protected abstract ColumnDockActionSource getSourceFor( Dockable dockable );

	@Override
	public void bind(){
		if( !isBound() ) {
			source = getSourceFor( getDockable() );
			if( source != null ) {
				for( int i = 0, n = source.getSourceCount(); i < n; i++ ) {
					createPanel( source.getSource( i ), i );
				}
				source.addListener( listener );
			}
		}
		super.bind();
	}

	@Override
	public void unbind(){
		super.unbind();
		if( !isBound() ) {
			if( source != null ) {
				source.removeListener( listener );
				for( ButtonPanel panel : itemPanels ) {
					panel.set( null );
					panel.setController( null );
					remove( panel );
				}
				itemPanels.clear();
				revalidate();
				source = null;
			}
		}
	}

	@Override
	protected void updateIcon(){
		// ignore
	}

	@Override
	protected void updateText(){
		// ignore
	}

	private int getOffset( int sourceIndex ){
		int offset = source.getSourceOffset( sourceIndex );
		Point point = new Point( offset, offset );
		point = SwingUtilities.convertPoint( getDockable().getComponent(), point, this );

		if( source.getOrientation() == bibliothek.gui.Orientation.VERTICAL ) {
			return point.x;
		}
		else {
			return point.y;
		}
	}

	@Override
	protected void doTitleLayout(){
		if( source == null ) {
			return;
		}

		Insets insets = titleInsets();
		int x = insets.left;
		int y = insets.top;
		int width = getWidth() - insets.left - insets.right;
		int height = getHeight() - insets.top - insets.bottom;

		boolean horizontal = getOrientation().isHorizontal();

		for( int i = 0, n = source.getSourceCount(); i < n; i++ ) {
			int start = getOffset( i );
			int length = source.getSourceLength( i );
			ButtonPanel items = itemPanels.get( i );

			Dimension[] preferred = items.getPreferredSizes();

			if( horizontal ) {
				int size = 0;
				int delta = 0;
				for( int j = preferred.length - 1; j >= 0; j-- ) {
					if( preferred[j].width <= length ) {
						size = j;
						delta = length - preferred[j].width;
						break;
					}
				}
				items.setVisibleActions( size );
				items.setBounds( start + delta, y, length - delta, height );
			}
			else {
				int size = 0;
				int delta = 0;
				for( int j = preferred.length - 1; j >= 0; j-- ) {
					if( preferred[j].height <= length ) {
						size = j;
						delta = length - preferred[j].height;
						break;
					}
				}
				items.setVisibleActions( size );
				items.setBounds( x, start + delta, width, length - delta );
			}
		}
	}

	@Override
	public Dimension getPreferredSize(){
		int w = 0;
		int h = 0;

		if( source != null ) {
			if( getOrientation().isHorizontal() ) {
				for( int i = 0, n = source.getSourceCount(); i < n; i++ ) {
					w = Math.max( w, getOffset( i ) + source.getSourceLength( i ) );
					h = Math.max( h, itemPanels.get( i ).getPreferredSize().height );
				}
			}
			else {
				for( int i = 0, n = source.getSourceCount(); i < n; i++ ) {
					w = Math.max( w, itemPanels.get( i ).getPreferredSize().width );
					h = Math.max( h, getOffset( i ) + source.getSourceLength( i ) );
				}
			}
		}

		w = Math.max( w, 5 );
		h = Math.max( h, 5 );

		Insets insets = titleInsets();

		w += insets.left + insets.right;
		h += insets.top + insets.bottom;

		return new Dimension( w, h );
	}
}
