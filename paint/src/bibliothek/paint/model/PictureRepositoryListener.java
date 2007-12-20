package bibliothek.paint.model;

/**
 * An observer of a {@link PictureRepository}. Gets informed when a 
 * {@link Picture} is added or removed from the repository.
 * @author Benjamin Sigg
 *
 */
public interface PictureRepositoryListener {
	/**
	 * Invoked when a picture was added to the observed repository.
	 * @param picture the new picture
	 */
	public void pictureAdded( Picture picture );
	
	/**
	 * Invoked when a picture was removed from the observed repository.
	 * @param picture the removed picture
	 */
	public void pictureRemoved( Picture picture );
}
