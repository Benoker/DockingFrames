package bibliothek.help.javadoc;

import bibliothek.help.model.Entry;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.Parameter;

public class EntryableMethodList extends AbstractEntryable{
    private ClassDoc doc;
    
    public EntryableMethodList( ClassDoc doc ){
        this.doc = doc;
        
        for( MethodDoc method : doc.methods() ){
            add( new EntryableMethod( method ) );
            
            print( method.returnType() );
            print( " " );
            italic( true );
            link( method.name(), "method", method.qualifiedName() + method.signature() );
            italic( false );
            print( "(" );
            Parameter[] args = method.parameters();
            for( int i = 0; i < args.length; i++ ){
                if( i > 0 )
                    print( ", " );
                print( args[i].type() );
            }
            println( ")" );
        }
    }
    
    public Entry toEntry() {
        return new Entry( "method-list", doc.qualifiedName(), content(), "class:" + doc.qualifiedName() );
    }
}
