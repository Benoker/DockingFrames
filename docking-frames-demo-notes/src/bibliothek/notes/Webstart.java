package bibliothek.notes;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import javax.imageio.ImageIO;
import javax.swing.Icon;

import bibliothek.demonstration.Demonstration;
import bibliothek.demonstration.Monitor;
import bibliothek.notes.util.ResourceSet;

/**
 * The startup-class used if this application should be treated
 * as a restricted, sub-application of the demonstration-framework.
 * @author Benjamin Sigg
 */
public class Webstart implements Demonstration{
    /**
     * Entrypoint
     * @param args are ignored
     */
	public static void main( String[] args ){
		Core core = new Core( true, null );
		core.startup();
	}

	public String getHTML(){
		try{
			Reader reader = new InputStreamReader( ResourceSet.openStream( "/data/bibliothek/notes/description.txt" ) );
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
		return ResourceSet.APPLICATION_ICONS.get( "application" );
	}

	public BufferedImage getImage(){
		try{
			InputStream in = ResourceSet.openStream( "/data/bibliothek/notes/image.png" );
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
		return "Notes";
	}

	public void show( Monitor monitor ){
		monitor.startup();
		Core core = new Core( true, monitor );
		core.startup();
	}
}
