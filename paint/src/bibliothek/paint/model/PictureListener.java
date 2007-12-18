package bibliothek.paint.model;

/**
 * An observer of a {@link Picture}.
 * @author Benjamin Sigg
 */
public interface PictureListener {
    /**
     * Called when the observed picture has changed.
     */
    public void pictureChanged();
}
