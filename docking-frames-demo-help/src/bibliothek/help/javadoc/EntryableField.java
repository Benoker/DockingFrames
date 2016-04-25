package bibliothek.help.javadoc;

import com.sun.javadoc.FieldDoc;

import bibliothek.help.model.Entry;

/**
 * An {@link Entryable} that collects detailed information about a
 * field.
 * @author Benjamin Sigg
 *
 */
@Content(type="field", encoding=Content.Encoding.DOCUMENT)
public class EntryableField extends AbstractEntryable {
    /** the field */
    private FieldDoc doc;
    
    /**
     * Collects detailed information about the field <code>doc</code>.
     * @param doc the field whose content will be presented
     */
    public EntryableField( FieldDoc doc ){
        this.doc = doc;
        
        bold( true );
        println( "Containing class:" );
        bold( false );
        linkln( doc.containingClass().qualifiedName(), "class", doc.containingClass().qualifiedName()  );
        println();
        bold( true );
        println( "Name:" );
        bold( false );
        print( doc.modifiers() );
        print( " " );
        print( doc.type() );
        print( " " );
        println( doc.name() );
        
        if( doc.commentText() != null ){
            println();
            bold( true );
            println( "Comment:" );
            bold( false );
            println( doc.commentText() );
        }
    }
    
    public Entry toEntry() {
        return new Entry( "field", doc.qualifiedName(), "Field " + doc.qualifiedName(), content(),
                "class:"+doc.containingClass().qualifiedName() );
    }

}
