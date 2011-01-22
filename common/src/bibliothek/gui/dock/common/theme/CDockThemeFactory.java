package bibliothek.gui.dock.common.theme;

import bibliothek.gui.DockController;
import bibliothek.gui.DockTheme;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.themes.ThemeFactory;
import bibliothek.gui.dock.themes.ThemeMeta;
import bibliothek.gui.dock.themes.ThemePropertyFactory;

/**
 * A factory that envelops another factory in order to build a 
 * CX-theme instead of a X-theme.
 * @author Benjamin Sigg
 *
 * @param <D> the kind of theme that gets wrapped up
 */
public  abstract class CDockThemeFactory<D extends DockTheme> implements ThemeFactory{
    private ThemePropertyFactory<D> delegate;
    private CControl control;
    
    /**
     * Creates a new factory.
     * @param delegate the factory that should be used as delegate to create
     * the initial {@link DockTheme}.
     * @param control the control for which this factory will work
     */
    public CDockThemeFactory( ThemePropertyFactory<D> delegate, CControl control ){
        this.delegate = delegate;
        this.control = control;
    }
    
    /**
     * Gets the control for which this factory works.
     * @return the control
     */
    public CControl getControl() {
        return control;
    }
    
    public DockTheme create( DockController controller ) {
    	if( control.getController() != controller ){
    		throw new IllegalArgumentException( "the supplied controller does not match the CControl" );
    	}
        return create( control );
    }
    
    /**
     * Creates a new theme.
     * @param control the control in whose realm the theme will be used
     * @return the new theme
     */
    public abstract DockTheme create( CControl control );
    
    public ThemeMeta createMeta( DockController controller ){
	    ThemeMeta meta = delegate.createMeta( controller );
	    meta.setFactory( this );
	    return meta;
    }
}