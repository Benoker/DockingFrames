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
package bibliothek.paint.util;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
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
	
	/** a snapshot of the application */
	private static BufferedImage screenshot;
	
	/** text describing the application */
	private static String text;
	
	/**
	 * Gets the icon which is stored under the name <code>key</code>.
	 * @param key the name of the icon
	 * @return the icon
	 */
	public static Icon getIcon( String key ){
		return ICONS.get( key );
	}
	
	/**
	 * Gets a screenshot of the application.
	 * @return the screenshot
	 */
	public static BufferedImage getScreenshot(){
		if( screenshot == null ){
			try {
				screenshot = ImageIO.read( Resources.class.getResource( "/data/bibliothek/paint/screenshot.png" ) );
			}
			catch( IOException e ) {
				e.printStackTrace();
			}
		}
		return screenshot;
	}
	
	/**
	 * Gets the text which describes this application.
	 * @return the description
	 */
	public static String getText(){
		if( text == null ){
			try{
				Reader reader = new InputStreamReader( Resources.class.getResourceAsStream( "/data/bibliothek/paint/text.txt" ) );
				StringBuilder builder = new StringBuilder();
				
				int read;
				while( (read = reader.read()) != -1 )
					builder.append( (char)read );
				
				reader.close();
				text = builder.toString();
			}
			catch( IOException ex ){
				ex.printStackTrace();
			}
		}
		return text;
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
