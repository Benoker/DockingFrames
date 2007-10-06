package bibliothek.help;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import javax.imageio.ImageIO;
import javax.swing.Icon;

import bibliothek.demonstration.Demonstration;
import bibliothek.demonstration.Monitor;
import bibliothek.help.util.ResourceSet;

/**
 * A class that can start this application in a restricted environment
 * (that means the {@link SecurityManager} restricts this application like
 * an applet).<br>
 * Instances of this class can also be used as entries in the demonstration-framework.
 * @author Benjamin Sigg
 *
 */
public class Webstart implements Demonstration{
    /**
     * Starts the application.
     * @param args are ignored
     */
	public static void main( String[] args ){
		new Core( true, null ).startup();
	}
	
	public String getHTML(){
		try{
			Reader reader = new InputStreamReader( ResourceSet.openStream( "/data/bibliothek/help/description.txt" ) );
			StringBuilder builder = new StringBuilder();
			
			int read;
			while( (read = reader.read()) != -1 )
				builder.append( (char)read );
			
			reader.close();
			return builder.toString();
		}
		catch( IOException ex ){
			ex.printStackTrace();
			return "";
		}
	}

	public Icon getIcon(){
		return ResourceSet.ICONS.get( "application" );
	}

	public BufferedImage getImage(){
		try{
			InputStream in = ResourceSet.openStream( "/data/bibliothek/help/image.png" );
			BufferedImage image = ImageIO.read( in );
			in.close();
			return image;
		}
		catch( IOException ex ){
			ex.printStackTrace();
			return null;
		}
	}

	public String getName(){
		return "Help";
	}

	public void show( Monitor monitor ){
		monitor.startup();
		Core core = new Core( true, monitor );
		core.startup();
	}
}
