package bibliothek.help.javadoc;

import java.util.LinkedList;
import java.util.List;

import bibliothek.help.model.Entry;

import com.sun.javadoc.Type;

/**
 * An {@link Entryable} that contains methods to store text. The text is
 * encoded in a way that an {@link Entry} can decode it. The exact meaning
 * of the encoding must be specified by the client.
 * @author Benjamin Sigg
 *
 */
public abstract class AbstractEntryable implements Entryable{
    /** the children of this {@link Entryable} */
    protected List<Entryable> entries = new LinkedList<Entryable>();
    /** the text that will be the content of the {@link Entry} of this {@link Entryable} */
    private StringBuilder builder = new StringBuilder();
    
    /**
     * Adds a new child.
     * @param entryable the new child
     */
    protected void add( Entryable entryable ){
        entries.add( entryable );
    }
    
    public Entryable[] children() {
        return entries.toArray( new Entryable[ entries.size() ] );
    }
    
    /**
     * Gets the text for an {@link Entry}. The result of this method
     * is intended to be used for the {@link Entry#getContent() content}-property
     * of the <code>Entry</code>. The text is encoded in a way that an
     * <code>Entry</code> can read additional information. For example if
     * the text should be printed in italic.
     * @return the encoded text
     */
    protected String content(){
        return builder.toString();
    }
    
    /**
     * Inserts a tag in the text that is used to indicate the beginning or end
     * of some mode. The information is encoded into {@link #content() the content}.<br>
     * Example: a call <code>mode( "bold", true )</code> will add the tag
     * <code>[mode|+bold]</code>. On the other hand, <code>mode( "italic", false )</code>
     * will add <code>[mode|-italic]</code>.
     * @param mode the id of the mode
     * @param begin whether to start or end the mode
     */
    public void mode( String mode, boolean begin ){
        builder.append( "[mode|" );
        builder.append( begin ? '+' : '-' );
        builder.append( mode );
        builder.append( "]" );
    }
    
    /**
     * Inserts a tag that contains client specific meta information. The
     * tag is inserted into {@link #content() the content}.<br>
     * Example: a call <code>mode( "a", "b", "c" )</code> will lead to
     * a new tag <code>[a|b|c]</code>.<br>
     * Note: the characters '|', ']' and '[' should not be used in any argument.
     * @param content the information to insert into the tag
     */
    public void mode( String... content ){
        builder.append( '[' );
        for( int i = 0; i < content.length; i++ ){
            if( i > 0 )
                builder.append( '|' );
            
            print( content[i] );
        }
        builder.append( ']' );
    }
    
    /**
     * Starts or ends the "bold" mode. This method does nothing else
     * than invoking <code>mode( "b", enabled )</code>.
     * @param enabled whether the mode starts or ends
     * @see #mode(String, boolean)
     */
    public void bold( boolean enabled ){
        mode( "b", enabled );
    }

    /**
     * Starts or ends the "italic" mode. This method does nothing else
     * than invoking <code>mode( "i", enabled )</code>.
     * @param enabled whether the mode starts or ends
     * @see #mode(String, boolean)
     */
    public void italic( boolean enabled ){
        mode( "i", enabled );
    }
    
    /**
     * This either just prints the {@link Type#typeName()} of <code>type</code>
     * if <code>type</code> is a primitive, or adds a link to
     * {@link #content() the content} elsewhere. If a link is added, the 
     * type of the link is set to "class" and the target of the link is
     * the {@link Type#qualifiedTypeName()}.
     * @param type the type whose name should be printed
     * @see #print(String)
     * @see #link(String, String, String)
     */
    public void print( Type type ){
        if( type.isPrimitive() )
            print( type.typeName() );
        else{
            link( type.typeName(), "class", type.qualifiedTypeName() );
        }
    }
    
    /**
     * Adds some text to {@link #content() the content}. The characters
     * '[', '|' and ']' are written twice. This ensures that these characters
     * are not accidentally interpreted as meta-information.
     * @param text the text to add
     */
    public void print( String text ){
        for( int i = 0, n = text.length(); i<n; i++ ){
            char c = text.charAt( i );
            if( c == '|' || c == '[' || c == ']' )
                builder.append( c );
            
            builder.append( c );
        }
    }
    
    /**
     * Adds some text using {@link #print(String)}, and then inserts
     * an "newline" to {@link #content() the content}.
     * @param text the text to add
     */
    public void println( String text ){
        print( text );
        println();
    }
    
    /**
     * Adds a newline to {@link #content() the content}
     */
    public void println(){
        print( "\n" );
    }
    
    /**
     * Adds a link-tag to {@link #content() the content}. The new
     * tag has the form <code>[link|type:id|text]</code>.
     * @param text the text of the link, what the user will see
     * @param type the type of objects to which the link points
     * @param id the id of the object to which the link points
     */
    public void link( String text, String type, String id ){
        builder.append( "[link|" );
        print( type );
        print( ":" );
        print( id );
        builder.append( '|' );
        print( text );
        builder.append( ']' );
    }
 
    /**
     * Adds a link-tag and a newline to {@link #content() the content}.
     * @param text the text of the link, what the user will see
     * @param type the type of objects to which the link points
     * @param id the id of the object to which the link points
     * @see #link(String, String, String)
     * @see #println()
     */
    public void linkln( String text, String type, String id ){
        link( text, type, id );
        println();
    }
}
