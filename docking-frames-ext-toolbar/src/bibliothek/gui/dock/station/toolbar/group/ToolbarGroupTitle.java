package bibliothek.gui.dock.station.toolbar.group;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JComponent;

import bibliothek.gui.Dockable;
import bibliothek.gui.ToolbarExtension;
import bibliothek.gui.dock.ToolbarGroupDockStation;
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.station.toolbar.title.ColumnDockActionSource;
import bibliothek.gui.dock.station.toolbar.title.ColumnDockTitle;
import bibliothek.gui.dock.themes.basic.action.BasicTitleViewItem;
import bibliothek.gui.dock.title.DockTitleFactory;
import bibliothek.gui.dock.title.DockTitleRequest;
import bibliothek.gui.dock.title.DockTitleVersion;

/**
 * A specialized title that should only be used for {@link ToolbarGroupDockStation}s.
 * @author Benjamin Sigg
 */
public class ToolbarGroupTitle extends ColumnDockTitle {
	/**
	 * A factory creating new {@link ToolbarGroupTitle}s.
	 */
	public static final DockTitleFactory FACTORY = new DockTitleFactory(){
		@Override
		public void uninstall( DockTitleRequest request ){
			// ignore
		}

		@Override
		public void request( DockTitleRequest request ){
			request.answer( new ToolbarGroupTitle( request.getTarget(), request.getVersion() ) );
		}

		@Override
		public void install( DockTitleRequest request ){
			// ignore
		}
	};

	protected BasicTitleViewItem<JComponent> createItemFor( DockAction action, Dockable dockable ){
		return dockable.getController().getActionViewConverter().createView( action, ToolbarExtension.TOOLBAR_TITLE, dockable );
	}

	/**
	 * Creates a new title.
	 * @param dockable the element, preferrably a {@link ToolbarGroupDockStation}, which is represented
	 * by this title.
	 * @param origin how to create this title
	 */
	public ToolbarGroupTitle( Dockable dockable, DockTitleVersion origin ){
		super( dockable, origin );
	}

	@Override
	protected ColumnDockActionSource getSourceFor( Dockable dockable ){
		if( dockable instanceof ToolbarGroupDockStation ) {
			return ((ToolbarGroupDockStation) dockable).getExpandActionSource();
		}
		return null;
	}

	@Override
	public void setActive( boolean active ){
		super.setActive( active );
		repaint();
	}

	@Override
	public void paintBackground( Graphics g, JComponent component ){
		super.paintComponents( g );
		final Graphics2D g2D = (Graphics2D) g;
		g2D.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
		g2D.setColor( color );
		if( getOrientation().isHorizontal() ) {
			final RoundRectangle2D rectangleRounded = new RoundRectangle2D.Double( 0, 0, getWidth(), getHeight() * 2, 8, 8 );
			g2D.fill( rectangleRounded );
		}
		else {
			final RoundRectangle2D rectangleRounded = new RoundRectangle2D.Double( 0, 0, getWidth() * 2, getHeight(), 8, 8 );
			g2D.fill( rectangleRounded );
		}
	}
}
