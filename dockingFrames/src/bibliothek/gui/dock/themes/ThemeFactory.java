package bibliothek.gui.dock.themes;

import java.net.URL;

import bibliothek.gui.DockTheme;

/**
 * A factory creating new themes.
 * @author Benjamin Sigg
 */
public interface ThemeFactory {
    /**
     * Creates a new theme.
     * @return the new theme
     */
    public DockTheme create();
    
    /**
     * Gets a human readable description of the theme.
     * @return the description, might be <code>null</code>
     */
    public String getDescription();
    
    /**
     * Gets the name of the theme.
     * @return the name, might be <code>null</code>
     */
    public String getName();
    
    /**
     * Gets a list of strings, containing the names of the authors.
     * @return the authors, might be <code>null</code>
     */
    public String[] getAuthors();
    
    /**
     * Gets a set of links to any webpage the authors might want to
     * show the user.
     * @return the pages, might be <code>null</code>
     */
    public URL[] getWebpages();
}
