package bibliothek.chess;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import bibliothek.chess.util.Utils;
import bibliothek.demonstration.Demonstration;
import bibliothek.demonstration.Monitor;

/**
 * This class is used to start up the application, and to register the
 * application as {@link Demonstration}.
 * @author Benjamin Sigg
 *
 */
public class Main implements Demonstration{
	/**
	 * Starts up this application
	 * @param args are ignored
	 */
	public static void main( String[] args ){
		Core core = new Core( null );
		core.startup();
	}

	/** a description of this application */
	private String description;

	/**
	 * Creates a new Demonstration
	 */
	public Main(){
	    try{
	        Reader in = new InputStreamReader( Main.class.getResourceAsStream( "/data/bibliothek/chess/description.txt" ));
	        StringBuilder builder = new StringBuilder();
	        int c;
	        while( (c = in.read()) != -1 ){
	            builder.append( (char)c );
	        }
	        in.close();
	        description = builder.toString();
	    }
	    catch( IOException ex ){
	        ex.printStackTrace();
	    }
	}
	
    public String getHTML() {
        return description;
    }

    public Icon getIcon() {
        return new ImageIcon( Utils.APPLICATION );
    }

    public BufferedImage getImage() {
        return Utils.IMAGE;
    }

    public String getName() {
        return "Chess";
    }

    public void show( Monitor monitor ) {
        monitor.startup();
        Core core = new Core( monitor );
        core.startup();
    }
}
