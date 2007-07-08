package bibliothek.help.javadoc;

import com.sun.javadoc.ConstructorDoc;
import com.sun.javadoc.Parameter;
import com.sun.javadoc.Type;

import bibliothek.help.model.Entry;

public class EntryableConstructor extends AbstractEntryable {
    private ConstructorDoc doc;
    
    public EntryableConstructor( ConstructorDoc doc ){
        this.doc = doc;
        
        bold( true );
        println( "Containing: " );
        bold( false );
        linkln( doc.containingClass().qualifiedName(), "class", doc.containingClass().qualifiedName() );
        println();
        bold( true );
        println( "Name:" );
        bold( false );
        print( doc.modifiers() );
        print( " " );
        print( doc.name() );
        print( "(" );
        Parameter[] args = doc.parameters();
        for( int i = 0; i < args.length; i++ ){
            if( i > 0 )
                print( ", " );
            print( args[i].type() );
            print( " " );
            print( args[i].name() );
        }
        println( ")" );
        
        if( doc.thrownExceptionTypes().length > 0 ){
            println();
            bold( true );
            println( "Throws:" );
            bold( false );
            for( Type type : doc.thrownExceptionTypes()){
                print( type );
                println();
            }
        }
        
        if( doc.commentText() != null ){
            println();
            bold( true );
            println( "Comment:" );
            bold( false );
            println( doc.commentText() );
        }
    }

    public Entry toEntry() {
        return new Entry( "constructor", doc.qualifiedName() + doc.signature(), content(), 
                "class:" + doc.containingClass().qualifiedName() );
    }
}
