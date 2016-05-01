package bibliothek.help.javadoc;

import bibliothek.help.model.Entry;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.Type;

/**
 * Reads the documentation of a class.
 * @author Benjamin Sigg
 */
@Content(type="class", encoding=Content.Encoding.DOCUMENT)
public class EntryableClass extends AbstractEntryable {
    /** the documentation of the class */
    private ClassDoc doc;
    
    /**
     * Creates a new <code>Entryable</code>
     * @param doc the documentation to transform
     */
    public EntryableClass( ClassDoc doc ){
        this.doc = doc;
        bold( true );
        println( "Package: ");
        bold( false );
        linkln( doc.containingPackage().name(), "class-list", doc.containingPackage().name() );
        println();
        bold( true );
        println( "Name: " );
        bold( false );
        println( doc.name() );
        println();
        bold( true );
        println( "Content:");
        bold( false );
        linkln( "Fields", "field-list", doc.qualifiedName() );
        linkln( "Constructors", "constructor-list", doc.qualifiedName() );
        linkln( "Methods", "method-list", doc.qualifiedName() );
        add( new EntryableFieldList( doc ));
        add( new EntryableConstructorList( doc ));
        add( new EntryableMethodList( doc ));
       
        println();
        bold( true );
        println( "Inherits:" );
        bold( false );
        if( doc.superclassType() != null ){
            print( doc.superclassType() );
            println();
        }
        for( Type type : doc.interfaceTypes() ){
            print( type );
            println();
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
        return new Entry( "class", doc.qualifiedName(), "Class " + doc.qualifiedName(), content(),
                "constructor-list:" + doc.qualifiedName(),
                "field-list:" + doc.qualifiedName(),
                "method-list:" + doc.qualifiedName(),
                "package-list:" + doc.containingPackage().name(),
                "hierarchy-class:" + doc.qualifiedName() );
    }

}
