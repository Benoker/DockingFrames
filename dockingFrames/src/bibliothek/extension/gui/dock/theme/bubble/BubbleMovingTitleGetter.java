package bibliothek.extension.gui.dock.theme.bubble;

import bibliothek.extension.gui.dock.theme.BubbleTheme;
import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.gui.dock.title.MovingTitleGetter;

public class BubbleMovingTitleGetter implements MovingTitleGetter {
    private ReducedBubbleTitleFactory reduced;
    
    public BubbleMovingTitleGetter( BubbleTheme theme ){
        reduced = new ReducedBubbleTitleFactory( theme );
    }
    
    public DockTitle get( DockController controller, DockTitle snatched ) {
        return reduced.createDockableTitle( snatched.getDockable(), null );
    }

    public DockTitle get( DockController controller, Dockable dockable ) {
        return reduced.createDockableTitle( dockable, null );
    }
}
