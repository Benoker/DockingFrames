package bibliothek.notes.util;

import java.awt.Image;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * A set of properties used in the application "Notes".
 * @author Benjamin Sigg
 */
public class ResourceSet {
	/** The Icons which might be used as images for {@link bibliothek.notes.model.Note}s */
	public static final List<Icon> NOTE_ICONS;
	/** The Icons which are used in the application itself */
	public static final Map<String, Icon> APPLICATION_ICONS;
	
	static{
		NOTE_ICONS = readNoteIcons();
		APPLICATION_ICONS = readApplicationIcons();
	}
	
	/**
	 * Reads the set of icons which might be used as images for Notes.
	 * @return the set of icons
	 */
	private static List<Icon> readNoteIcons(){
		List<Icon> icons = new ArrayList<Icon>();
		
		try{
			Scanner list = new Scanner( openStream( "/data/bibliothek/notes/icons/choices/list.txt" ));
			
			while( list.hasNext() ){
				InputStream in = openStream( "/data/bibliothek/notes/icons/choices/" + list.next() );
				icons.add( new ImageIcon( ImageIO.read( in )));
				in.close();
			}
			
			list.close();
		}
		catch( IOException ex ){
			ex.printStackTrace();
		}
		
		return Collections.unmodifiableList( icons );
	}
	
	/**
	 * Reads the set of icons which are used in the application.
	 * @return the set of icons
	 */
	private static Map<String, Icon> readApplicationIcons(){
		Map<String, Icon> icons = new HashMap<String, Icon>();
		
		try{
			Properties properties = new Properties();
			InputStream in = openStream( "/data/bibliothek/notes/icons/icons.ini" );
			properties.load( in );
			in.close();
			
			for( Map.Entry<Object, Object> entry : properties.entrySet() ){
				in = openStream( "/data/bibliothek/notes/icons/" + entry.getValue() );
				icons.put( entry.getKey().toString(), new ImageIcon( ImageIO.read( in )) );
				in.close();
			}
		}
		catch( IOException ex ){
			ex.printStackTrace();
		}
		
		return Collections.unmodifiableMap( icons );
	}
	
	/**
	 * Opens a stream reading the resource <code>name</code>. 
	 * @param name a path to a file that can be found by the class-loader
	 * of this <code>ResourceSet</code>.
	 * @return the stream
	 * @throws IOException if <code>name</code> can't be read
	 */
	public static InputStream openStream( String name ) throws IOException{
		InputStream in = ResourceSet.class.getResourceAsStream( name );
		if( in == null )
			throw new FileNotFoundException( "Can't find " + name );
		return in;
	}
	
	/**
	 * Transforms an icon into an image.
	 * @param icon the icon to transform
	 * @return the image or <code>null</code> if the icon can't be transformed
	 */
	public static Image toImage( Icon icon ){
		if( icon instanceof ImageIcon )
			return ((ImageIcon)icon).getImage();
		
		return null;
	}
}
