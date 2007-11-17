package bibliothek.gui.dock.common.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import bibliothek.gui.DockUI;
import bibliothek.gui.dock.facile.FControl;

/**
 * A set of resources available through the whole framework
 * @author Benjamin Sigg
 */
public class Resources {
    /** various text snippets */
    private static ResourceBundle bundle;
    
    /** the list of default-icons */
    private static Map<String, Icon> icons = new HashMap<String, Icon>();
    
    static{
        // read the localized text
        bundle = ResourceBundle.getBundle( 
                "data.bibliothek.gui.dock.locale.common", 
                Locale.getDefault(), FControl.class.getClassLoader() );
        
        // read the icons
        try{
            Properties properties = new Properties();
            InputStream in = Resources.class.getResourceAsStream( "/data/bibliothek/gui/dock/icons/icons.ini" );
            properties.load( in );
            in.close();
            
            ClassLoader loader = Resources.class.getClassLoader();
            for( Map.Entry<Object, Object> entry : properties.entrySet() ){
                ImageIcon icon = new ImageIcon( ImageIO.read( loader.getResource( "data/bibliothek/gui/dock/icons/" + entry.getValue()) ));
                icons.put( (String)entry.getKey(), icon );
            }
        }
        catch( IOException ex ){
            ex.printStackTrace();
        }
    }
    
    /**
     * Gets localized text snippets. 
     * @return the text
     */
    public static ResourceBundle getBundle() {
        return bundle;
    }
    
    /**
     * Searches an icon that was stored with the given key. The keys
     * can be found in the file <code>bibliothek/gui/dock/icons/icons.ini</code>.
     * @param key the name of an icon
     * @return the icon or <code>null</code>
     */
    public static Icon getIcon( String key ){
        return icons.get( key );
    }
}
