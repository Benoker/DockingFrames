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


public class Main implements Demonstration{
	public static void main( String[] args ){
		Core core = new Core( null );
		core.startup();
	}

	private String description;
	
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
