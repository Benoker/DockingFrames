package bibliothek.gui.dock.themes;

import java.lang.reflect.Constructor;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ResourceBundle;

import bibliothek.gui.DockTheme;
import bibliothek.gui.DockUI;

/**
 * A factory using the {@link ThemeProperties} of a {@link DockTheme} to 
 * create instances of that <code>DockTheme</code>.
 * @author Benjamin Sigg
 */
public class ThemePropertyFactory implements ThemeFactory {
    /** Default constructor of the theme */
    private Constructor<? extends DockTheme> constructor;
    /** Information about the theme */
    private ThemeProperties properties;
    /** Bundle containing the text, may be <code>null</code> */
    private ResourceBundle bundle;
    /** Additional information, might be <code>null</code> */
    private DockUI ui;
    
    /**
     * Creates a new factory.
     * @param theme the class of a theme, must have the {@link ThemeProperties} annotation.
     */
    public ThemePropertyFactory( Class<? extends DockTheme> theme ){
        this( theme, null, null );
    }

    /**
     * Creates a new factory.
     * @param theme the class of a theme, must have the {@link ThemeProperties} annotation.
     * @param bundle the bundle to retrieve text, might be <code>null</code> if the
     * bundle of the {@link DockUI} should be used.
     */
    public ThemePropertyFactory( Class<? extends DockTheme> theme, ResourceBundle bundle ){
        this( theme, bundle, null );
    }

    /**
     * Creates a new factory.
     * @param theme the class of a theme, must have the {@link ThemeProperties} annotation.
     * @param ui the DockUI to retrieve more information, might be <code>null</code>
     */
    public ThemePropertyFactory( Class<? extends DockTheme> theme, DockUI ui ){
        this( theme, null, ui );
    }
    
    /**
     * Creates a new factory.
     * @param theme the class of a theme, must have the {@link ThemeProperties} annotation.
     * @param bundle the bundle to retrieve text, might be <code>null</code> if the
     * bundle of the {@link DockUI} should be used.
     * @param ui the DockUI to retrieve more information, might be <code>null</code>
     */
    public ThemePropertyFactory( Class<? extends DockTheme> theme, ResourceBundle bundle, DockUI ui ){
        if( theme == null )
            throw new IllegalArgumentException( "Theme must not be null" );
        
        properties = theme.getAnnotation( ThemeProperties.class );
        if( properties == null )
            throw new IllegalArgumentException( "Theme misses annotation ThemeProperties" );
        
        try {
            constructor = theme.getConstructor( new Class[0] );
        }
        catch( NoSuchMethodException e ){
            throw new IllegalArgumentException( "Missing default constructor", e );
        }
        
        this.bundle = bundle;
        this.ui = ui;
    }
    
    /**
     * Gets the bundle used to retrieve text for this factory.
     * @return the bundle or <code>null</code> if the bundle of
     * the {@link DockUI} is used.
     */
    public ResourceBundle getBundle() {
        return bundle;
    }
    
    /**
     * Gets the <code>DockUI</code> used with this factory.
     * @return the ui or <code>null</code> if the default-DockUI is used
     */
    public DockUI getUi() {
        return ui;
    }
    
    public DockTheme create() {
        try {
            return constructor.newInstance( new Object[0] );
        }
        catch( Exception e ){
            System.err.println( "Can't create theme due an unknown reason" );
            e.printStackTrace();
            return null;
        }
    }

    public String[] getAuthors() {
        return properties.authors();
    }
    
    protected String getString( String key ){
        if( bundle != null )
            return bundle.getString( key );
        if( ui != null )
            return ui.getString( key );
        return DockUI.getDefaultDockUI().getString( key );
    }

    public String getDescription() {
        return getString( properties.descriptionBundle() );
    }

    public String getName() {
        return getString( properties.nameBundle() );
    }

    public URI[] getWebpages() {
        try{
            String[] urls = properties.webpages();
            URI[] result = new URI[ urls.length ];
            for( int i = 0; i < result.length; i++ )
                result[i] = new URI( urls[i] );
        
            return result;
        }
        catch( URISyntaxException ex ){
            System.err.print( "Can't create urls due an unknown reason" );
            ex.printStackTrace();
            return null;
        }
    }
}
