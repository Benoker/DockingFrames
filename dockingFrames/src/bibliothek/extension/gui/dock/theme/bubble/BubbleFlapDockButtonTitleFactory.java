package bibliothek.extension.gui.dock.theme.bubble;

import bibliothek.extension.gui.dock.theme.BubbleTheme;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.gui.dock.title.DockTitleFactory;
import bibliothek.gui.dock.title.DockTitleVersion;

public class BubbleFlapDockButtonTitleFactory implements DockTitleFactory {
    private BubbleTheme theme;
    
    public BubbleFlapDockButtonTitleFactory( BubbleTheme theme ){
        this.theme = theme;
    }

    public DockTitle createDockableTitle( Dockable dockable, DockTitleVersion version ) {
        return new Title( theme, dockable, version );
    }

    public <D extends Dockable & DockStation> DockTitle createStationTitle( D dockable, DockTitleVersion version ) {
        return new Title( theme, dockable, version );
    }

    private static class Title extends BubbleDockTitle{
        public Title( BubbleTheme theme, Dockable dockable, DockTitleVersion origin ) {
            super( theme, dockable, origin, false );
        }
        
        @Override
        public void setOrientation( Orientation orientation ) {
            switch( orientation ){
                case SOUTH_SIDED:
                case NORTH_SIDED:
                case FREE_HORIZONTAL:
                    orientation = Orientation.FREE_HORIZONTAL;
                    break;
                case EAST_SIDED:
                case WEST_SIDED:
                case FREE_VERTICAL:
                    orientation = Orientation.FREE_VERTICAL;
                    break;
            }
            
            super.setOrientation( orientation );
        }
    }
}
