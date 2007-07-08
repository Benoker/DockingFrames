package bibliothek.help.javadoc;

import bibliothek.help.model.Entry;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.ConstructorDoc;
import com.sun.javadoc.Parameter;

public class EntryableConstructorList extends AbstractEntryable {
    private ClassDoc doc;
    public EntryableConstructorList( ClassDoc doc ){
        this.doc = doc;
        
        for( ConstructorDoc constructor : doc.constructors() ){
            add( new EntryableConstructor( constructor ) );

            italic( true );
            link( constructor.name(), "constructor", constructor.qualifiedName() + constructor.signature() );
            italic( false );
            print( "(" );
            Parameter[] args = constructor.parameters();
            for( int i = 0; i < args.length; i++ ){
                if( i > 0 )
                    print( ", " );
                print( args[i].type() );
            }
            println( ")" );
        }
    }
    
    public Entry toEntry() {
        return new Entry( "constructor-list", doc.qualifiedName(), "Constructors of " + doc.qualifiedName(), content(), 
                "class:" + doc.qualifiedName() );
    }

}
