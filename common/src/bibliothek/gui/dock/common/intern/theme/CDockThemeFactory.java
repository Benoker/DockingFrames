package bibliothek.gui.dock.common.intern.theme;

import java.net.URI;

import bibliothek.gui.DockTheme;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.themes.ThemeFactory;
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
    
    public DockTheme create() {
        return create( control );
    }
    
    /**
     * Creates a new theme.
     * @param control the control in whose realm the theme will be used
     * @return the new theme
     */
    public abstract DockTheme create( CControl control );
    
    public String[] getAuthors() {
        return delegate.getAuthors();
    }

    public String getDescription() {
        return delegate.getDescription();
    }

    public String getName() {
        return delegate.getName();
    }

    public URI[] getWebpages() {
        return delegate.getWebpages();
    }
}