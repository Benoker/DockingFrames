package bibliothek.gui.dock.station.toolbar;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.displayer.DisplayerRequest;
import bibliothek.gui.dock.station.DisplayerFactory;
import bibliothek.gui.dock.station.DockableDisplayer;
import bibliothek.gui.dock.themes.basic.BasicDockableDisplayer;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.gui.dock.util.Transparency;

/**
 * A simple implementation of a {@link DockableDisplayer} that can be used by
 * toolbar-{@link DockStation}s. This displayer is aware of the fact, that some
 * {@link DockStation}s have an orientation and may update its own orientation
 * automatically.
 * 
 * @author Benjamin Sigg
 */
public class ToolbarDockableDisplayer extends BasicDockableDisplayer {
	/**
	 * A factory creating new {@link ToolbarDockableDisplayer}s.
	 */
	public static final DisplayerFactory FACTORY = new DisplayerFactory(){
		@Override
		public void request( DisplayerRequest request ){
			ToolbarDockableDisplayer displayer = new ToolbarDockableDisplayer( request.getParent(), request.getTarget(), request.getTitle() );
			displayer.setDefaultBorderHint( false );
			displayer.setRespectBorderHint( true );
			request.answer( displayer );
		}
	};

	/**
	 * Creates a new displayer.
	 * 
	 * @param station
	 *            the owner of this displayer
	 * @param dockable
	 *            the element shown on this displayer, can be <code>null</code>
	 * @param title
	 *            the title shown on this displayer, can be <code>null</code>
	 */
	public ToolbarDockableDisplayer( DockStation station, Dockable dockable, DockTitle title ){
		super( station, dockable, title );
		setTransparency( Transparency.TRANSPARENT );
	}
}
