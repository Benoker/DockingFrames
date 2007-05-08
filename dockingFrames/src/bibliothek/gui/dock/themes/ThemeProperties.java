package bibliothek.gui.dock.themes;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

import bibliothek.gui.DockUI;

/**
 * A small description of a DockTheme, used in
 * {@link DockUI} to create a factory for a theme.
 * @author Benjamin Sigg
 */
@Retention( RetentionPolicy.RUNTIME )
@Target({ ElementType.TYPE })
public @interface ThemeProperties {
    /**
     * The key for the name in the local bundle.
     * @return the name
     */
    public String nameBundle();
    
    /**
     * The key for the description in the local bundle.
     * @return the description
     */
    public String descriptionBundle();
    
    /**
     * The authors of the theme.
     * @return the authors
     */
    public String[] authors();
    
    /**
     * URLs for the webpage of this theme.
     * @return the webpages associated to this theme
     */
    public String[] webpages();
}
