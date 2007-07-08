package bibliothek.help.javadoc;

import java.util.LinkedList;
import java.util.List;

import com.sun.javadoc.Type;

public abstract class AbstractEntryable implements Entryable{
    protected List<Entryable> entries = new LinkedList<Entryable>();
    private StringBuilder builder = new StringBuilder();
    
    protected void add( Entryable entryable ){
        entries.add( entryable );
    }
    
    public Entryable[] children() {
        return entries.toArray( new Entryable[ entries.size() ] );
    }
    
    protected String content(){
        return builder.toString();
    }
    
    public void mode( String mode, boolean begin ){
        builder.append( "[mode|" );
        builder.append( begin ? '+' : '-' );
        builder.append( mode );
        builder.append( "]" );
    }
    
    public void mode( String... content ){
        builder.append( '[' );
        for( int i = 0; i < content.length; i++ ){
            if( i > 0 )
                builder.append( '|' );
            
            print( content[i] );
        }
        builder.append( ']' );
    }
    
    public void bold( boolean enabled ){
        mode( "b", enabled );
    }
    
    public void italic( boolean enabled ){
        mode( "i", enabled );
    }
    
    public void print( Type type ){
        if( type.isPrimitive() )
            print( type.typeName() );
        else{
            link( type.typeName(), "class", type.qualifiedTypeName() );
        }
    }
    
    public void print( String text ){
        for( int i = 0, n = text.length(); i<n; i++ ){
            char c = text.charAt( i );
            if( c == '|' || c == '[' || c == ']' )
                builder.append( c );
            
            builder.append( c );
        }
    }
    
    public void println( String text ){
        print( text );
        println();
    }
    
    public void println(){
        print( "\n" );
    }
    
    public void link( String text, String type, String id ){
        builder.append( "[link|" );
        print( type );
        print( ":" );
        print( id );
        builder.append( '|' );
        print( text );
        builder.append( ']' );
    }
 
    public void linkln( String text, String type, String id ){
        link( text, type, id );
        println();
    }
}
