package bibliothek.chess.view;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.dockable.DockableMovingImageFactory;
import bibliothek.gui.dock.dockable.MovingImage;
import bibliothek.gui.dock.themes.BasicTheme;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.gui.dock.title.DockTitleFactory;
import bibliothek.gui.dock.title.DockTitleVersion;

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
        setMovingImageFactory( call("new DockableMovingImageFactory(){\n  public MovingImage create(  DockController controller,  Dockable dockable){\n    return null;\n  }\n  public MovingImage create(  DockController controller,  DockTitle snatched){\n    return null;\n  }\n}\n"));
    }

    @Override
    public void install( DockController controller ) {
        super.install( controller );
        controller.getDockTitleManager().registerTheme( "chess-board", call("new DockTitleFactory(){\n  public DockTitle createDockableTitle(  Dockable dockable,  DockTitleVersion version){\n    return null;\n  }\n  public <D extends Dockable & DockStation>DockTitle createStationTitle(  D dockable,  DockTitleVersion version){\n    return null;\n  }\n}\n"));
    }
    
    @Override
    public void uninstall( DockController controller ) {
        super.uninstall( controller );
        controller.getDockTitleManager().registerTheme( "chess-board", null );
    }
}
