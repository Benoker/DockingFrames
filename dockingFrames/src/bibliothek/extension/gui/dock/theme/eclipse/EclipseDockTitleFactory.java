package bibliothek.extension.gui.dock.theme.eclipse;

import bibliothek.extension.gui.dock.theme.EclipseTheme;
import bibliothek.extension.gui.dock.theme.eclipse.EclipseThemeConnector.TitleBar;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.gui.dock.title.DockTitleFactory;
import bibliothek.gui.dock.title.DockTitleVersion;

/**
 * A {@link DockTitleFactory} that calls another factory but only
 * if the method {@link EclipseThemeConnector#getTitleBarKind(Dockable)} returns
 * {@link TitleBar#BASIC} for the {@link Dockable} whose title should be
 * created.
 * @author Benjamin Sigg
 *
 */
public class EclipseDockTitleFactory implements DockTitleFactory{
    /** the theme for which titles are created */
    private EclipseTheme theme;
    /** the factory that really does create the titles */
    private DockTitleFactory factory;
    
    /**
     * Creates a new factory
     * @param theme the theme whose {@link EclipseThemeConnector} is used
     * to determine whether to create a title for a {@link Dockable} or not.
     * @param factory the factory which creates title when necessary
     */
    public EclipseDockTitleFactory( EclipseTheme theme, DockTitleFactory factory ){
        if( theme == null )
            throw new IllegalArgumentException( "theme must not be null" );
        
        if( factory == null )
            throw new IllegalArgumentException( "factory must not be null" );
        
        this.theme = theme;
        this.factory = factory;
    }
    
    public DockTitle createDockableTitle( Dockable dockable, DockTitleVersion version ) {
        TitleBar bar = theme.getThemeConnector( version.getController() ).getTitleBarKind( dockable );
        if( bar == TitleBar.BASIC || bar == TitleBar.BASIC_BORDERED )
            return factory.createDockableTitle( dockable, version );
        
        return null;
    }
    
    public <D extends Dockable & DockStation> DockTitle createStationTitle( D dockable, DockTitleVersion version ) {
        TitleBar bar = theme.getThemeConnector( version.getController() ).getTitleBarKind( dockable );
        if( bar == TitleBar.BASIC || bar == TitleBar.BASIC_BORDERED )
            return factory.createStationTitle( dockable, version );
        
        return null;        
    }
}
