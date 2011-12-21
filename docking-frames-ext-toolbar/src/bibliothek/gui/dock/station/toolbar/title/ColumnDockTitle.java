package bibliothek.gui.dock.station.toolbar.title;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.DockActionSource;
import bibliothek.gui.dock.themes.basic.action.buttons.ButtonPanel;
import bibliothek.gui.dock.title.AbstractMultiDockTitle;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.gui.dock.title.DockTitleVersion;

/**
 * This specialized {@link DockTitle} does not show a {@link DockActionSource}, instead it shows
 * several {@link DockActionSource}s, all derived from a single {@link ColumnDockActionSource}.
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
		ButtonPanel panel = new ButtonPanel( true );
		panel.set( getDockable(), item );
		panel.setController( getDockable().getController() );
		panel.setOrientation( getOrientation() );
		panel.setToolTipText( getToolTipText() );
		itemPanels.add( index, panel );
		add( panel );
		revalidate();
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
	protected void doTitleLayout(){
		
	}
	
	@Override
	public Dimension getPreferredSize(){
		
	}
}
