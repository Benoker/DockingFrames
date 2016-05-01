package bibliothek.help.javadoc;

import bibliothek.help.model.Entry;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.ConstructorDoc;
import com.sun.javadoc.Parameter;

/**
 * An {@link Entryable} which creates a list of all constructors
 * of some class.
 * @author Benjamin Sigg
 *
 */
@Content(type="constructor-list", encoding=Content.Encoding.DOCUMENT)
public class EntryableConstructorList extends AbstractEntryable {
    /** the class whose constructors are listed up */
    private ClassDoc doc;
    
    /**
     * Creates a new list of constructors
     * @param doc the class who owns the constructors
     */
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
