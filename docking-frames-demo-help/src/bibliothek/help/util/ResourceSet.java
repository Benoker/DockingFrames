package bibliothek.help.util;

import java.awt.Image;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * This class provides static methods used to read resources like
 * icons.
 * @author Benjamin Sigg
 *
 */
public class ResourceSet {
    /**
     * A map containing all {@link Icon}s that are needed by this application.
     * The mapping of key and image is read from the file
     * "/data/bibliothek/help/icons/icons.ini".<br>
     * The map is not modifiable. 
     */
	public static final Map<String, Icon> ICONS;
	
	static{
		ICONS = readApplicationIcons();
	}
	
	/** 
	 * Loads all {@link Icon}s that are needed by this application.
	 * @return the images
	 */
	private static Map<String, Icon> readApplicationIcons(){
		Map<String, Icon> icons = new HashMap<String, Icon>();
		
		try{
			Properties properties = new Properties();
			InputStream in = openStream( "/data/bibliothek/help/icons/icons.ini" );
			properties.load( in );
			in.close();
			
			for( Map.Entry<Object, Object> entry : properties.entrySet() ){
				in = openStream( "/data/bibliothek/help/icons/" + entry.getValue() );
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
	 * Gets a stream that reads a file from the same location as the class-files
	 * for this application are stored.
	 * @param name the path of the file
	 * @return the stream that reads <code>name</code>
	 * @throws IOException if the path is not valid
	 */
	public static InputStream openStream( String name ) throws IOException{
		InputStream in = ResourceSet.class.getResourceAsStream( name );
		if( in == null )
			throw new FileNotFoundException( "Can't find " + name );
		return in;
	}
	
	/**
	 * Converts an {@link Icon} in an image of the same size.
	 * @param icon the icon to convert
	 * @return the image or <code>null</code> if the conversion couldn't
	 * be performed
	 */
	public static Image toImage( Icon icon ){
		if( icon instanceof ImageIcon )
			return ((ImageIcon)icon).getImage();
		
		return null;
	}
}
