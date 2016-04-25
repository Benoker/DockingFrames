package bibliothek.extension.gui.dock.theme.eclipse;

import bibliothek.extension.gui.dock.theme.EclipseTheme;
import bibliothek.extension.gui.dock.theme.eclipse.EclipseThemeConnector.TitleBar;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.title.DockTitleFactory;
import bibliothek.gui.dock.title.DockTitleRequest;

/**
 * A {@link DockTitleFactory} that calls another factory but only
 * if the method {@link EclipseThemeConnector#getTitleBarKind(bibliothek.gui.DockStation, Dockable)} returns
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
    
    public void install( DockTitleRequest request ){
	    factory.install( request );	
    }
    
    public void uninstall( DockTitleRequest request ){
	    factory.uninstall( request );	
    }
    
    public void request( DockTitleRequest request ){
        TitleBar bar = theme.getThemeConnector( request.getVersion().getController() ).getTitleBarKind( request.getParent(), request.getTarget() );
        if( bar == TitleBar.BASIC || bar == TitleBar.BASIC_BORDERED ){
        	factory.request( request );
        }
        else{
        	request.answer( null );
        }
    }
}
