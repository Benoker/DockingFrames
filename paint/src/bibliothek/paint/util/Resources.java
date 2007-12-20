package bibliothek.paint.util;

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
 * Some resources which are used globally in this application.
 * @author Benjamin Sigg
 *
 */
public class Resources {
	/** the icons used in this application */
	private static final Map<String, Icon> ICONS = readApplicationIcons();
	
	/**
	 * Gets the icon which is stored under the name <code>key</code>.
	 * @param key the name of the icon
	 * @return the icon
	 */
	public static Icon getIcon( String key ){
		return ICONS.get( key );
	}
	
	/**
	 * Reads the set of icons which are used in the application.
	 * @return the set of icons
	 */
	private static Map<String, Icon> readApplicationIcons(){
		Map<String, Icon> icons = new HashMap<String, Icon>();
		
		try{
			Properties properties = new Properties();
			InputStream in = openStream( "/data/bibliothek/paint/icons/icons.ini" );
			properties.load( in );
			in.close();
			
			for( Map.Entry<Object, Object> entry : properties.entrySet() ){
				in = openStream( "/data/bibliothek/paint/icons/" + entry.getValue() );
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
		InputStream in = Resources.class.getResourceAsStream( name );
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
