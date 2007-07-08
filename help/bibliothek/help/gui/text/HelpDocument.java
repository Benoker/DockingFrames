package bibliothek.help.gui.text;

import java.awt.Color;
import java.util.Set;

import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;

public class HelpDocument extends DefaultStyledDocument{
    public HelpDocument(){
        Style root = addStyle( "root", null );
        
        addStyle( "text", root );
        Style link = addStyle( "link", root );
        
        StyleConstants.setForeground( link, Color.BLUE );
        StyleConstants.setUnderline( link, true );
    }
    
    public void appendText( String text, Set<String> modes ){
        try {
            insertString( getLength(), text, getOrCreate( "text", modes ) );
        }
        catch( BadLocationException e ) {
            e.printStackTrace();
        }
    }
    
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
