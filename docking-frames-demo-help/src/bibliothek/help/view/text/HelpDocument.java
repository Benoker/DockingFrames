package bibliothek.help.view.text;

import java.awt.Color;
import java.util.Set;

import javax.swing.text.*;

/**
 * A {@link StyledDocument} that has to default-styles for normal text
 * and for embedded links. The style of the links contains an attribute
 * with key {@link HelpLinker#LINK} and the target of the link as value.<br>
 * The methods {@link #appendLink(String, String, Set)} and {@link #appendText(String, Set)}
 * both take a set of modes which can change the appearance of the text. The
 * modes are:<br>
 * <ul>
 *  <li><b>b</b>: bold</li>
 *  <li><b>i</b>: italic</li>
 * </ul>
 * @author Benjamin Sigg
 *
 */
public class HelpDocument extends DefaultStyledDocument{
    /**
     * Creates a new document.
     */
    public HelpDocument(){
        Style root = addStyle( "root", null );
        
        addStyle( "text", root );
        Style link = addStyle( "link", root );
        
        StyleConstants.setForeground( link, Color.BLUE );
        StyleConstants.setUnderline( link, true );
    }
    
    /**
     * Adds some text at the end of the document.
     * @param text the text
     * @param modes the appearance of the text
     */
    public void appendText( String text, Set<String> modes ){
        try {
            insertString( getLength(), text, getOrCreate( "text", modes ) );
        }
        catch( BadLocationException e ) {
            e.printStackTrace();
        }
    }
    
    /**
     * Adds some link at the end of the document.
     * @param text the text to display
     * @param link the target of the link
     * @param modes the appearance of <code>text</code>
     */
    public void appendLink( String text, String link, Set<String> modes ){
        try {
            Style info = addStyle( null, getOrCreate( "link", modes ) );
            info.addAttribute( HelpLinker.LINK, link );
            insertString( getLength(), text, info );
        }
        catch( BadLocationException e ) {
            e.printStackTrace();
        }        
    }
    
    /**
     * Gets a style that represents the given modes.
     * @param prefix the type of style, the original document uses
     * only "text" and "link".
     * @param modes the appearance
     * @return the created or cached style
     */
    private Style getOrCreate( String prefix, Set<String> modes ){
        String id = id( prefix, modes );
        Style style = getStyle( id );
        if( style == null ){
            style = addStyle( id, getStyle( prefix ) );
            
            StyleConstants.setBold( style, modes.contains( "b" ));
            StyleConstants.setItalic(  style, modes.contains( "i" ));
        }
        return style;
    }
    
    /**
     * Creates an id that is unique for the given prefix
     * and set of modes.
     * @param prefix the prefix of the id
     * @param modes the active modes
     * @return the unique id
     */
    private String id( String prefix, Set<String> modes ){
        StringBuilder result = new StringBuilder();
        result.append( prefix );
        result.append( " " );
        
        if( modes.contains( "b" ))
            result.append( "b" );
        
        if( modes.contains( "i" ))
            result.append( "i" );
        
        return result.toString();
    }
}
