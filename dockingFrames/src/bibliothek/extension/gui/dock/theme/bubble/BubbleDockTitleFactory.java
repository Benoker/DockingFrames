package bibliothek.extension.gui.dock.theme.bubble;

import bibliothek.extension.gui.dock.theme.BubbleTheme;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.gui.dock.title.DockTitleFactory;
import bibliothek.gui.dock.title.DockTitleVersion;

public class BubbleDockTitleFactory implements DockTitleFactory {
    private BubbleTheme theme;
    
    public BubbleDockTitleFactory( BubbleTheme theme ){
        this.theme = theme;
    }

    public DockTitle createDockableTitle( Dockable dockable, DockTitleVersion version ) {
        return new BubbleDockTitle( theme, dockable, version );
    }

    public <D extends Dockable & DockStation> DockTitle createStationTitle( D dockable, DockTitleVersion version ) {
        return new BubbleDockTitle( theme, dockable, version );
    }

}
