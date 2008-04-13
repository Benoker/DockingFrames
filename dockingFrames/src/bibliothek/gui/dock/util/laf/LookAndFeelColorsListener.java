package bibliothek.gui.dock.util.laf;

/**
 * A listener listening to a {@link LookAndFeelColors}. Gets informed
 * when a color changes.
 * @author Benjamin Sigg
 */
public interface LookAndFeelColorsListener {
    /**
     * Called when a single color changed.
     * @param key the key of the color that changed, one of the keys
     * specified in {@link LookAndFeelColors}.
     */
    public void colorChanged( String key );
    
    /**
     * Called when an unspecified number of colors (maybe zero) changed.
     */
    public void colorsChanged();
}
