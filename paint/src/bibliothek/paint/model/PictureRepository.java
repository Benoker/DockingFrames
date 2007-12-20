package bibliothek.paint.model;

import java.util.ArrayList;
import java.util.List;

/**
 * A modifiable set of {@link Picture}s.
 * @author Benjamin Sigg
 *
 */
public class PictureRepository {
	/** observers of this repository, will be informed whenever pictures are added or removed */
	private List<PictureRepositoryListener> listeners = new ArrayList<PictureRepositoryListener>();
	
	/** the pictures in this repository*/
	private List<Picture> pictures = new ArrayList<Picture>();
	
	/**
	 * Adds an observer to this repository. The observer will be informed whenever
	 * a picture is added or removed from this repository.
	 * @param listener the new observer
	 */
	public void addListener( PictureRepositoryListener listener ){
		listeners.add( listener );
	}
	
	/**
	 * Removes an observer from this repository.
	 * @param listener the observer to remove
	 */
	public void removeListener( PictureRepositoryListener listener ){
		listeners.remove( listener );
	}
	
	/**
	 * Adds a picture to the list of pictures.
	 * @param picture the new picture
	 */
	public void add( Picture picture ){
		pictures.add( picture );
		for( PictureRepositoryListener listener : listeners.toArray( new PictureRepositoryListener[ listeners.size() ] ) )
			listener.pictureAdded( picture );
	}
	
	/**
	 * Removes a picture from the list of pictures.
	 * @param picture the picture to remove
	 */
	public void remove( Picture picture ){
		if( pictures.remove( picture )){
			for( PictureRepositoryListener listener : listeners.toArray( new PictureRepositoryListener[ listeners.size() ] ) )
				listener.pictureRemoved( picture );	
		}
	}
	
	/**
	 * Gets the number of pictures which are stored in this repository.
	 * @return the number of pictures
	 */
	public int getPictureCount(){
		return pictures.size();
	}
	
	/**
	 * Gets the index'th picture of this repository.
	 * @param index the location of the picture
	 * @return the picture
	 */
	public Picture getPicture( int index ){
		return pictures.get( index );
	}
	
	/**
	 * Gets the first picture with the {@link Picture#getName() name} <code>name</code>.
	 * @param name the name of the picture
	 * @return a picture or <code>null</code>
	 */
	public Picture getPicture( String name ){
		for( Picture picture : pictures )
			if( picture.getName().equals( name ))
				return picture;
		
		return null;
	}
}
