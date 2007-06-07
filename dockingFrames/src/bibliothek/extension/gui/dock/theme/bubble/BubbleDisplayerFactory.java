package bibliothek.extension.gui.dock.theme.bubble;

import bibliothek.extension.gui.dock.theme.BubbleTheme;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DockableDisplayer;
import bibliothek.gui.dock.DockableDisplayer.Location;
import bibliothek.gui.dock.station.DisplayerFactory;
import bibliothek.gui.dock.title.DockTitle;

public class BubbleDisplayerFactory implements DisplayerFactory {
    private BubbleTheme theme;
    
    public BubbleDisplayerFactory( BubbleTheme theme ){
        this.theme = theme;
    }
    
    public DockableDisplayer create( DockStation station, Dockable dockable, DockTitle title ) {
        BubbleDisplayer displayer = new BubbleDisplayer( theme, dockable, title );
        displayer.setBorder( null );
        if( dockable.asDockStation() != null )
            displayer.setTitleLocation( Location.RIGHT );
        return displayer;
    }
    
}
