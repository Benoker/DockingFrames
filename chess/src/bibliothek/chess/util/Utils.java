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

public class Utils {
    public static final BufferedImage APPLICATION;
    public static final BufferedImage IMAGE;
    
    static{
        APPLICATION = image( "/data/bibliothek/chess/icons/Chess_klt16.png" );
        IMAGE = image( "/data/bibliothek/chess/image.png" );
    }
    
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
    
    private static Map<String, Icon> chessIcons = new HashMap<String, Icon>();
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
