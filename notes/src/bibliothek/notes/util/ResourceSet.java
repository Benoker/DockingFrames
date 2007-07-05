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

public class ResourceSet {
	public static final List<Icon> NOTE_ICONS;
	public static final Map<String, Icon> APPLICATION_ICONS;
	
	static{
		NOTE_ICONS = readNoteIcons();
		APPLICATION_ICONS = readApplicationIcons();
	}
	
	private static List<Icon> readNoteIcons(){
		List<Icon> icons = new ArrayList<Icon>();
		
		try{
			Scanner list = new Scanner( openStream( "/data/icons/choices/list.txt" ));
			
			while( list.hasNext() ){
				InputStream in = openStream( "/data/icons/choices/" + list.next() );
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
	
	private static Map<String, Icon> readApplicationIcons(){
		Map<String, Icon> icons = new HashMap<String, Icon>();
		
		try{
			Properties properties = new Properties();
			InputStream in = openStream( "/data/icons/icons.ini" );
			properties.load( in );
			in.close();
			
			for( Map.Entry<Object, Object> entry : properties.entrySet() ){
				in = openStream( "/data/icons/" + entry.getValue() );
				icons.put( entry.getKey().toString(), new ImageIcon( ImageIO.read( in )) );
				in.close();
			}
		}
		catch( IOException ex ){
			ex.printStackTrace();
		}
		
		return Collections.unmodifiableMap( icons );
	}
	
	public static InputStream openStream( String name ) throws IOException{
		InputStream in = ResourceSet.class.getResourceAsStream( name );
		if( in == null )
			throw new FileNotFoundException( "Can't find " + name );
		return in;
	}
	

	public static Image toImage( Icon icon ){
		if( icon instanceof ImageIcon )
			return ((ImageIcon)icon).getImage();
		
		return null;
	}
}
