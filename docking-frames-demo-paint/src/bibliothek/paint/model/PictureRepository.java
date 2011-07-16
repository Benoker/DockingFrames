/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2007 Benjamin Sigg
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 * Benjamin Sigg
 * benjamin_sigg@gmx.ch
 * CH - Switzerland
 */
package bibliothek.paint.model;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import bibliothek.util.xml.XElement;

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
	 * Writes the pictures of this repository into <code>out</code>.
	 * @param out the stream to write into
	 * @throws IOException if an I/O error occurs
	 */
	public void write( DataOutputStream out ) throws IOException{
	    out.writeInt( pictures.size() );
	    for( Picture picture : pictures )
	        picture.write( out );
	}
	
	/**
	 * Writes the pictures of this repository into <code>element</code>.
	 * @param element the element to write into
	 */
	public void writeXML( XElement element ){
	    for( Picture picture : pictures ){
	        picture.writeXML( element.addElement( "picture" ) );
	    }
	}
	
	/**
	 * Reads the pictures of this repository from <code>in</code>.
	 * @param in the stream to read from
	 * @throws IOException if an I/O error occurs
	 */
	public void read( DataInputStream in ) throws IOException{
	    while( !pictures.isEmpty() )
	        remove( pictures.get( pictures.size()-1 ) );
	    
	    for( int i = 0, n = in.readInt(); i<n; i++ ){
	        Picture picture = new Picture( null );
	        picture.read( in );
	        add( picture );
	    }
	}
	
	/**
	 * Reads the pictures of this repository from <code>element</code>.
	 * @param element the element to read from
	 */
	public void readXML( XElement element ){
	    while( !pictures.isEmpty() )
            remove( pictures.get( pictures.size()-1 ) );
        
        for( XElement xpicture : element.getElements( "picture" )){
            Picture picture = new Picture( null );
            picture.readXML( xpicture );
            add( picture );
        }
	}
	
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
