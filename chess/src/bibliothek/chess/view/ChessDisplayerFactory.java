package bibliothek.chess.view;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DockableDisplayer;
import bibliothek.gui.dock.station.DisplayerFactory;
import bibliothek.gui.dock.title.DockTitle;

public class ChessDisplayerFactory implements DisplayerFactory{
	public DockableDisplayer create( DockStation station, Dockable dockable, DockTitle title ){
		DockableDisplayer displayer = new DockableDisplayer();
		displayer.setTitle( title );
		displayer.setDockable( dockable );
		displayer.setOpaque( false );
		return displayer;
	}
}
