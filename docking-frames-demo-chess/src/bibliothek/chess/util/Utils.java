package bibliothek.chess.util;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import bibliothek.chess.model.Player;

/**
 * A class containing methods to load icons. Most of them are used to
 * display the figures.
 * @author Benjamin Sigg
 *
 */
public class Utils {
	/** The icon of the application */
    public static final BufferedImage APPLICATION;
    /** A screenshot of the application */
    public static final BufferedImage IMAGE;
    
    static{
        APPLICATION = image( "/data/bibliothek/chess/icons/Chess_klt16.png" );
        IMAGE = image( "/data/bibliothek/chess/image.png" );
    }
    
    /**
     * Loads an image directly through the classloader
     * @param path the path to the image
     * @return the image or <code>null</code> if an error occurs
     */
    private static BufferedImage image( String path ){
        BufferedImage image = null;
        try{
            InputStream in = Utils.class.getResourceAsStream( path );
            image = ImageIO.read( in );
            in.close();
        }
        catch( IOException ex ){
            ex.printStackTrace();
        }
        return image;
    }
    
    /**
     * A map containing all icons which are used to display figures
     */
    private static Map<String, Icon> chessIcons = new HashMap<String, Icon>();
    
    /**
     * Gets an icon to display a figure.
     * @param name the name of the icon
     * @param player the player who owns the figure
     * @param size the size of the image
     * @return the icon
     */
    public static Icon getChessIcon( String name, Player player, int size ){
        String key = "/data/bibliothek/chess/icons/Chess_" + name + (player == Player.BLACK ? "d" : "l" ) + "t" + size + ".png";
        Icon icon = chessIcons.get( key );
        if( icon == null ){
            icon = new ImageIcon( image( key ));
            chessIcons.put( key, icon );
        }
        return icon;
    }
}