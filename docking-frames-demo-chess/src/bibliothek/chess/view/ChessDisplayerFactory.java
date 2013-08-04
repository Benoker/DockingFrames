package bibliothek.chess.view;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.displayer.DisplayerRequest;
import bibliothek.gui.dock.station.DisplayerFactory;
import bibliothek.gui.dock.station.DockableDisplayer;
import bibliothek.gui.dock.themes.basic.BasicDockableDisplayer;
import bibliothek.gui.dock.title.DockTitle;

/**
 * A {@link DisplayerFactory} used on a {@link ChessBoard} to create
 * transparent {@link DockableDisplayer DockableDisplayers}.
 * @author Benjamin Sigg
 */
public class ChessDisplayerFactory implements DisplayerFactory{
	public void request( DisplayerRequest request ){
		Dockable dockable = request.getTarget();
    	DockStation station = request.getParent();
    	DockTitle title = request.getTitle();
    	
		BasicDockableDisplayer displayer = new BasicDockableDisplayer( station );
		displayer.setTitle( title );
		displayer.setDockable( dockable );
		displayer.setOpaque( false );
		
		displayer.setRespectBorderHint(false);
		displayer.setDefaultBorderHint(false);
		
		request.answer( displayer );
	}
}
