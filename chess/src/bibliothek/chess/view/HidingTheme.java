package bibliothek.chess.view;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.themes.BasicTheme;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.gui.dock.title.DockTitleFactory;
import bibliothek.gui.dock.title.DockTitleVersion;
import bibliothek.gui.dock.title.MovingTitleGetter;

/**
 * A theme that hides all {@link DockTitle} when used together with a 
 * {@link ChessBoard}.
 * @author Benjamin Sigg
 */
public class HidingTheme extends BasicTheme {
	/**
	 * Creates a new theme
	 */
    public HidingTheme(){
        setMovingTitleGetter( new MovingTitleGetter(){
            public DockTitle get( DockController controller, DockTitle snatched ) {
                return null;
            }

            public DockTitle get( DockController controller, Dockable dockable ) {
                return null;
            }
        });   
    }

    @Override
    public void install( DockController controller ) {
        super.install( controller );
        controller.getDockTitleManager().registerTheme( "chess-board", new DockTitleFactory(){
            public DockTitle createDockableTitle( Dockable dockable, DockTitleVersion version ) {
                return null;
            }
            public <D extends Dockable & DockStation> DockTitle createStationTitle( D dockable, DockTitleVersion version ) {
                return null;
            }
        });
    }
    
    @Override
    public void uninstall( DockController controller ) {
        super.uninstall( controller );
        controller.getDockTitleManager().registerTheme( "chess-board", null );
    }
}
